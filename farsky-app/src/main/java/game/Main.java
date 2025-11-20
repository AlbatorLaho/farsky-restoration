package game;

import game.shader.Shaders;
import game.sounds.SoundManager;
import game.cinematic.Cinematic;
import game.gui.GuiRenderer;
import game.gui.InteractionHint;
import game.input.InputManager;
import game.inventory.InventoryHud;
import game.manager.Achievements;
import game.manager.GameState;
import game.manager.Loading;
import game.manager.RenderManager;
import game.manager.AssetLoader;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.map.MapRenderer;
import game.manager.GameTime;
import game.util.FontRenderer;
import game.util.IconLoader;
import game.util.DisplayModes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class Main {
   public static boolean isVerbose = true;
   public static boolean isDebug = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
   public static final boolean isRelease = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") <= 0;
   public static boolean exitRequested = false;
   public static String VERSION = "FarSky v1.0";
   public static String dataPath = "";
   private static String logPath = "";
   public static Achievements achievements = new Achievements();
   private static long lastFrameNanos;
   public static int targetFps = 50;
   private static int fixedUpdatesPerSecond = 50;
   private static float maxFixedStep = 2.0F / fixedUpdatesPerSecond;
   private static int frameCount = 0;
   public static int fps = 0;
   public static int syncSleepMs = 0;
   public static GameState gameState = GameState.STARTUP;
   private static GameState frameState = GameState.STARTUP;
   private static GameState prevFrameState = GameState.STARTUP;

   public static void main(String[] args) {
      boolean windowMode = isDebug;
      String[] argsCopy = args;
      int argCount = args.length;

      for (int i = 0; i < argCount; i++) {
         String arg = argsCopy[i];
         String[] parts = arg.split(":", 2);
         if (parts.length > 0) {
            if (parts[0].equals("-windowMode")) {
               windowMode = true;
            }

            if (parts[0].equals("-path") && parts.length > 1) {
               dataPath = parts[1];
            }

            if (parts[0].equals("-logPath") && parts.length > 1) {
               logPath = parts[1];
            }
         }
      }

      try {
         Display.setTitle("FarSky");
         Display.setIcon(IconLoader.loadIcons("textures/gui/icon.png"));
         DisplayModes.init();
         if (windowMode) {
            DisplayModes.exitFullscreen();
         } else {
            DisplayModes.enterFullscreen();
         }

         Display.create();
         Display.update();
      } catch (LWJGLException e) {
         e.printStackTrace();
         System.exit(0);
      }

      if (!isDebug) {
         try {
            File logFile = new File(logPath + "farsky.log");
            FileOutputStream logStream = new FileOutputStream(logFile);
            PrintStream printStream = new PrintStream(logStream);
            System.setOut(printStream);
            System.setErr(printStream);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }
      }

      System.out.println(VERSION);
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Date date = new Date();
      System.out.println(dateFormat.format(date));
      System.out.println("******** HARDWARE INFO ************");
      System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
      System.out.println("OS name: " + System.getProperty("os.name") + " version " + System.getProperty("os.version"));
      System.out.println("Java version: " + System.getProperty("java.version"));
      System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors() + " cores");
      System.out.println("OS Architecture: " + System.getProperty("os.arch"));
      System.out.println("***********************************");
      run();
   }

   private static void run() {
      TextureManager.initMenuTextures();
      FontRenderer.init();
      RenderManager.initGL();
      if (!GLContext.getCapabilities().OpenGL20) {
         RenderManager.setOrtho();
         GuiRenderer.renderOpenGLError("2.0");
         Display.update();

         try {
            Thread.sleep(6000L);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }

         shutdown();
      } else {
         long startLoadNanos = System.nanoTime();
         RenderManager.render();
         Display.update();
         InputManager.init();
         RenderManager.render();
         Display.update();
         GameTime.init();
         RenderManager.render();
         Display.update();
         Shaders.loadAll();
         RenderManager.render();
         Display.update();

         try {
            Thread.sleep(Math.max(0L, 1000L - (System.nanoTime() - startLoadNanos) / 1000000L));
         } catch (InterruptedException e) {
            e.printStackTrace();
         }

         GuiRenderer.showSplash = false;

         while (AssetLoader.loadNextAsset()) {
            RenderManager.render();
            Display.update();
         }

         Loading.loadMenu();
         RenderManager.render();
         Display.update();
         long accumNanos = 0L;
         System.nanoTime();
         long syncSleepNanos = 0L;
         lastFrameNanos = System.nanoTime();

         while (!Display.isCloseRequested() && !exitRequested) {
            long nowNanos = System.nanoTime();
            long frameNanos = nowNanos - lastFrameNanos;
            lastFrameNanos = nowNanos;
            float rawDt = (float)frameNanos / 1000000000F;
            float dt = Math.min(rawDt, maxFixedStep);
            frameState = gameState;

            while (dt > 0.0F) {
               InteractionHint.clearAll();
               if (frameState != GameState.LOADING_GAME && frameState != GameState.LOADING_MENU) {
                  InputManager.update(dt);
               }

               GameTime.update(dt);
               SoundManager.update(dt);
               if (hasStateChanged() && prevFrameState == GameState.INVENTORY) {
                  InventoryHud.deselectStorage();
               }

               switch (frameState) {
                  case STARTUP:
                  case RELOADING:
                     SoundManager.stopAll();
                     TextureManager.loadNextTexture();
                     break;
                  case LOADING_MENU:
                     SoundManager.stopAll();
                     if (hasStateChanged()) {
                        Loading.loadMenu();
                     }

                     Loading.update(dt);
                     break;
                  case LOADING_GAME:
                     SoundManager.stopAll();
                     Loading.update(dt);
                     break;
                  case PLAYING:
                     GuiRenderer.update(dt);
                     GameScene.tick(dt);
                     MapRenderer.update(dt);
                     RenderManager.update(dt);
                     break;
                  case MAP:
                     GuiRenderer.update(dt);
                     MapRenderer.update(dt);
                     RenderManager.update(dt);
                     break;
                  case INVENTORY:
                     GuiRenderer.update(dt);
                     InventoryHud.update(dt);
                     RenderManager.update(dt);
                     break;
                  case MAIN_MENU:
                     GameScene.tick(dt);
                     GuiRenderer.update(dt);
                     RenderManager.update(dt);
                     break;
                  case PAUSED:
                     GuiRenderer.update(dt);
                     RenderManager.update(dt);
                     break;
                  case CINEMATIC_INTRO:
                     GuiRenderer.update(dt);
                     Cinematic.update(dt);
                     RenderManager.update(dt);
                     break;
                  case CINEMATIC_INGAME:
                     GuiRenderer.update(dt);
                     Cinematic.update(dt);
                     GameScene.tick(dt);
                     RenderManager.update(dt);
               }

               prevFrameState = frameState;
               dt = Math.min(rawDt -= maxFixedStep, maxFixedStep);
               if (frameState == GameState.RELOADING || frameState == GameState.LOADING_GAME || frameState == GameState.LOADING_MENU) {
                  dt = 0.0F;
                  lastFrameNanos = System.nanoTime();
               }
            }

            RenderManager.render();
            Display.update();
            frameCount++;
            accumNanos += frameNanos;
            long beforeSyncNanos = System.nanoTime();
            Display.sync(targetFps);
            syncSleepNanos += System.nanoTime() - beforeSyncNanos;
            if (accumNanos >= 1000000000L) {
               fps = frameCount;
               syncSleepMs = (int)(syncSleepNanos / 1000000L);
               syncSleepNanos = 0L;
               frameCount = 0;
               accumNanos -= 1000000000L;
            }
         }

         shutdown();
      }
   }

   private static void shutdown() {
      Loading.stopWorldThread();
      SoundManager.destroy();
      TextureManager.deleteAll();
      Display.destroy();
   }

   public static boolean hasStateChanged() {
      return prevFrameState != frameState;
   }

   public static GameState getGameState() {
      return frameState;
   }
}
