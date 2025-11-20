package game.manager;

import game.Main;
import game.cinematic.Cinematic;
import game.environment.DepthAtmosphere;
import game.environment.SkyDome;
import game.gui.GuiRenderer;
import game.inventory.InventoryHud;
import game.map.MapRenderer;
import game.shader.BlackBordersEffect;
import game.shader.BloomEffect;
import game.shader.Shaders;
import game.util.Point;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.GLU;

public final class RenderManager {
   public static float aspectRatio;
   public static float fov = 70.0F;
   private static float nearClip = 2.0F;
   private static float farClip = 2000.0F;
   public static boolean freeCam = false;
   public static boolean hideHud = false;
   public static Point menuCameraPos = new Point(350.0F, 0.0F, 250.0F);

   public static void initGL() {
      GL11.glClearDepth(1.0);
      GL11.glDepthFunc(GL11.GL_LEQUAL);
      GL11.glEnable(GL11.GL_NORMALIZE);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glDisable(GL11.GL_FOG);
      GL11.glDisable(GL11.GL_DITHER);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL11.glDisable(GL11.GL_STENCIL_TEST);
      GL11.glEnable(GL11.GL_CULL_FACE);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      if (Shaders.checkGLError() && !Main.isRelease) {
         System.out.println("Render init error, current program: " + Shaders.activeProgramId);
      }
   }

   public static void update(float delta) {
      if (Main.getGameState() == GameState.PLAYING && !freeCam && GameScene.avatar != null) {
         Camera.setFromAvatar(GameScene.avatar);
      }

      if (Main.getGameState() == GameState.MAIN_MENU) {
         Camera.setPosition(
            menuCameraPos.plus(new Point((float)Math.cos(GameTime.elapsedMillis / 11000.0F), (float)Math.sin(GameTime.elapsedMillis / 13000.0F), (float)Math.sin(GameTime.elapsedMillis / 15000.0F)).scaled(13.0F)),
            -120.0F,
            (float)(Math.sin(GameTime.elapsedMillis / 8650.0F) * 0.5 + 0.5) * 20.0F
         );
      }

      Camera.update(delta);
      Camera.applyTransform();
      DepthAtmosphere.update(Camera.getPosition().y);
      aspectRatio = (float)Display.getWidth() / Display.getHeight();
   }

   public static void render() {
      Shaders.checkGLError();
      switch (Main.getGameState()) {
         case PLAYING:
            if (Shaders.testShaderEnabled) {
               GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
            } else {
               GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            }

            setPerspective();
            renderWorld();
            Shaders.unbind();
            setOrtho();
            BloomEffect.apply(DepthAtmosphere.getMinBloom());
            if (!freeCam) {
               BlackBordersEffect.render();
            }

            if (!freeCam && !hideHud) {
               GuiRenderer.render();
               if (GameScene.avatar != null && !GameScene.avatar.isDead()) {
                  MapRenderer.renderMinimapBackground();
                  Shaders.mapShader.bind();
                  GL11.glMatrixMode(GL11.GL_PROJECTION);
                  GL11.glLoadIdentity();
                  GLU.gluPerspective(fov, 1.0F, nearClip, farClip);
                  GL11.glMatrixMode(GL11.GL_MODELVIEW);
                  GL11.glLoadIdentity();
                  GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);
                  GL11.glEnable(GL11.GL_DEPTH_TEST);
                  GL11.glDisable(GL11.GL_LIGHTING);
                  GL11.glEnable(GL11.GL_TEXTURE_2D);
                  GL11.glDisable(GL12.GL_TEXTURE_3D);
                  GL11.glDisable(GL11.GL_CULL_FACE);
                  if (Shaders.checkGLError() && Main.isVerbose) {
                     System.out.println("set3D() error, current program: " + Shaders.activeProgramId);
                  }

                  MapRenderer.renderMinimap();
               }
            }
            break;
         case CINEMATIC_INTRO:
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            Shaders.unbind();
            setOrtho();
            Cinematic.applyBlurEffect(true);
            setPerspective();
            Cinematic.renderCrashScene();
            Shaders.unbind();
            setOrtho();
            BloomEffect.apply(DepthAtmosphere.getMinBloom());
            BlackBordersEffect.render();
            Cinematic.render();
            GuiRenderer.render();
            break;
         case CINEMATIC_INGAME:
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            setPerspective();
            renderWorld();
            Shaders.unbind();
            setOrtho();
            BloomEffect.apply(DepthAtmosphere.getMinBloom());
            BlackBordersEffect.render();
            Cinematic.render();
            GuiRenderer.render();
            break;
         case MAP:
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            Shaders.unbind();
            setOrtho();
            GuiRenderer.render();
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            Shaders.mapShader.bind();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GLU.gluPerspective(fov, aspectRatio, nearClip, farClip);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL12.GL_TEXTURE_3D);
            GL11.glDisable(GL11.GL_CULL_FACE);
            if (Shaders.checkGLError() && Main.isVerbose) {
               System.out.println("set3D() error, current program: " + Shaders.activeProgramId);
            }

            MapRenderer.renderFullMap();
            break;
         case INVENTORY:
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            Shaders.unbind();
            setOrtho();
            GuiRenderer.render();
            Shaders.guiEffectShader.bind();
            InventoryHud.render();
            break;
         case MAIN_MENU:
            if (Shaders.testShaderEnabled) {
               GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
            } else {
               GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            }

            setPerspective();
            renderWorld();
            Shaders.unbind();
            setOrtho();
            BloomEffect.apply(0.2F);
            GuiRenderer.render();
            break;
         case PAUSED:
            Shaders.unbind();
            setOrtho();
            GuiRenderer.render();
            break;
         case RELOADING:
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
            Shaders.unbind();
            setOrtho();
            TextureManager.renderLoadingProgress();
            break;
         case STARTUP:
         case LOADING_GAME:
         case LOADING_MENU:
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
            Shaders.unbind();
            setOrtho();
            GuiRenderer.render();
      }

      Shaders.unbind();
      setOrtho();
      GuiRenderer.renderDebugOverlay();
   }

   private static void setPerspective() {
      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();
      GLU.gluPerspective(fov, aspectRatio, nearClip, farClip);
      GL11.glMatrixMode(GL11.GL_MODELVIEW);
      GL11.glLoadIdentity();
      GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glEnable(GL11.GL_CULL_FACE);
      GL11.glCullFace(GL11.GL_BACK);
   }

   public static void setLight() {
      float lightScale = 1.0F;
      if (GameScene.avatar != null) {
         lightScale = GameScene.avatar.getScale() * GameTime.getLightLevel();
      }

      Shaders.setUniform("light_ambient", new Point(DepthAtmosphere.getSunColor().x * lightScale, DepthAtmosphere.getSunColor().y * lightScale, DepthAtmosphere.getSunColor().z * lightScale));
      Shaders.setUniform("light_diffuse", new Point(0.5F, 0.7F, 0.8F));
      Shaders.setUniform("light_specular", new Point(0.1F, 0.15F, 0.2F));
      if (Shaders.checkGLError() && !Main.isRelease) {
         System.out.println("setLight() error, current program: " + Shaders.activeProgramId);
      }
   }

   public static void enableCubemap() {
      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glEnable(GL12.GL_TEXTURE_3D);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
   }

   public static void disableCubemap() {
      GL11.glDisable(GL12.GL_TEXTURE_3D);
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
   }

   public static void setOrtho() {
      GL11.glMatrixMode(GL11.GL_MODELVIEW);
      GL11.glLoadIdentity();
      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();
      GLU.gluOrtho2D(0.0F, Display.getWidth(), Display.getHeight(), 0.0F);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glDisable(GL11.GL_LIGHTING);
      disableCubemap();
   }

   private static void renderWorld() {
      GL11.glPushMatrix();
      Camera.applyMatrix();
      GL11.glTranslatef(-Camera.getWorldOffset().x, 0.0F, -Camera.getWorldOffset().z);
      SkyDome.render();
      GameScene.render();
      GL11.glPopMatrix();
   }
}
