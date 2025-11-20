package game.manager;
import game.enemy.EnemyGenerator;

import game.Main;
import game.chunks.ChunkManager;
import game.cinematic.Cinematic;
import game.util.Point;
import game.world.World;
import game.world.WorldManager;

public final class Loading {
   static enum LoadingState {
      INITIALIZING,
      LOADING,
      POPULATING,
      COMPLETE;
   }

   public static WorldManager worldManager;
   private static Thread worldThread;
   private static LoadingState loadingState;
   private static boolean isNewWorld = false;

   public static void loadMenu() {
      GameScene.destroy();
      loadingState = LoadingState.INITIALIZING;
      stopWorldThread();
      ChunkManager.disposeAll();
      startWorldThread(null, false);
   }

   public static void loadGame(World world) {
      if (Main.isVerbose) {
         System.out.println("Load Game");
      }

      loadingState = LoadingState.INITIALIZING;
      stopWorldThread();
      ChunkManager.disposeAll();
      startWorldThread(world, false);
      isNewWorld = false;
   }

   public static void newWorld(float dayTime, float nightTime, EnemyGenerator.SpawningLevel spawning) {
      if (Main.isVerbose) {
         System.out.println("New World");
      }

      WorldManager.dayTime = dayTime;
      WorldManager.nightTime = nightTime;
      WorldManager.spawning = spawning;
      loadingState = LoadingState.INITIALIZING;
      stopWorldThread();
      ChunkManager.disposeAll();
      startWorldThread(null, true);
      isNewWorld = true;
   }

   public static void reloadGame() {
      loadingState = LoadingState.INITIALIZING;
      if (worldManager != null) {
         worldManager.requestReset();

         while (!worldManager.isResetComplete()) {
            try {
               Thread.sleep(500L);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }

      ChunkManager.disposeAll();
      worldManager.confirmReset();
      Main.gameState = GameState.LOADING_GAME;
      isNewWorld = false;
   }

   public static void update(float delta) {
      if (worldManager.isReady() && (Main.getGameState() != GameState.LOADING_GAME || worldManager.getWorld() != null)) {
         if (Main.getGameState() == GameState.LOADING_MENU) {
            Camera.setPosition(RenderManager.menuCameraPos, -135.0F, -20.0F);
         } else if (Main.getGameState() == GameState.LOADING_GAME) {
            if (GameScene.avatar != null) {
               Camera.setPosition(GameScene.avatar.getCameraPos(), 0.0F, 0.0F);
            } else {
               Camera.setPosition(new Point(worldManager.getAvatarStartPosition().x, -9999.0F, worldManager.getAvatarStartPosition().y), 0.0F, 0.0F);
            }
         }

         ChunkManager.update(delta);
         switch (loadingState) {
            case INITIALIZING:
               if (ChunkManager.pendingChunks.size() != 0) {
                  loadingState = LoadingState.LOADING;
                  return;
               }
               break;
            case LOADING:
               if (ChunkManager.pendingChunks.size() == 0) {
                  loadingState = LoadingState.POPULATING;
                  return;
               }
               break;
            case POPULATING:
               if (ChunkManager.queuedChunks.size() == 0) {
                  loadingState = LoadingState.COMPLETE;
                  return;
               }
               break;
            case COMPLETE:
               if (Main.getGameState() == GameState.LOADING_GAME) {
                  if (isNewWorld && GameScene.gameMode.hasCinematic()) {
                     Cinematic.init(Cinematic.CinematicState.INTRO);
                     Main.gameState = GameState.CINEMATIC_INTRO;
                  } else {
                     Main.gameState = GameState.PLAYING;
                  }
               } else if (Main.getGameState() == GameState.LOADING_MENU) {
                  Main.gameState = GameState.MAIN_MENU;
               }

               if (isNewWorld) {
                  GameTime.setTime(0.0F, 0.0F);
               }

               GameTime.setDayCycle(worldManager.getDayTime(), worldManager.getNightTime());
               GameScene.initScene(worldManager.getAvatarStartPosition(), worldManager.getStartBasePosition(), worldManager.getAbandonedBasePositions(), worldManager.getSpawning(), worldManager.getDroidPositions());
         }
      }
   }

   public static void renderLoadingText() {
      switch (Main.getGameState()) {
         case LOADING_GAME:
            if (isNewWorld) {
               AssetLoader.renderLoadingScreen("Generating new world...");
               return;
            }

            AssetLoader.renderLoadingScreen("Loading game...");
            return;
         default:
            AssetLoader.renderLoadingScreen("Loading main menu...");
      }
   }

   private static void startWorldThread(World world, boolean isNew) {
      worldManager = new WorldManager(world, isNew);
      worldThread = new Thread(worldManager);
      if (Main.isVerbose) {
         System.out.println("WorldManager Thread -> Start");
      }

      worldThread.start();
   }

   public static void stopWorldThread() {
      if (worldManager != null) {
         if (Main.isVerbose) {
            System.out.println("WorldManager Thread -> Stop");
         }

         worldManager.stop();

         try {
            worldThread.join();
            return;
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   public static World getPendingWorld() {
      return worldManager.getWorld();
   }
}
