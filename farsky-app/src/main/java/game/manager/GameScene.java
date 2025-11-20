package game.manager;
import game.seafloorBase.Octree;

import game.Main;
import game.chunks.ChunkManager;
import game.cinematic.Cinematic;
import game.collision.CollisionBox;
import game.collision.CollisionDetector;
import game.enemy.EnemyGenerator;
import game.enemy.EnemyManager;
import game.environment.DepthAtmosphere;
import game.environment.EnvironmentManager;
import game.environment.ParticleBurst;
import game.environment.SkyDome;
import game.gui.InteractionHint;
import game.gui.dialog.DialogManager;
import game.inventory.Item;
import game.map.MapRenderer;
import game.outsideObj.OutsideObj;
import game.player.Avatar;
import game.player.WorldChest;
import game.player.droid.Droid;
import game.saving.SaveManager;
import game.seafloorBase.SeafloorBase;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.submarine.Submarine;
import game.util.Coord;
import game.util.Point;
import game.util.State;
import java.util.ArrayList;
import java.util.Hashtable;
import org.lwjgl.opengl.GL11;

public final class GameScene {
   public static Point gravity = new Point(0.0F, -1.0F, 0.0F);
   private static Point waterGravity = new Point(0.0F, -1.0F, 0.0F);
   private static Point insideGravity = new Point(0.0F, -4.0F, 0.0F);
   public static Avatar avatar;
   public static ArrayList<SeafloorBase> seafloorBases;
   public static ArrayList<Submarine> submarines;
   public static ArrayList<OutsideObj> outsideObjects;
   public static ArrayList<Droid> droids;
   private static float baseSpawnProgress = 1.0F;
   private static float submarineSpawnProgress = 1.0F;
   private static float outsideObjSpawnProgress = 1.0F;
   private static ArrayList<CollisionBox> worldCollisionBoxes = new ArrayList<>();
   public static DialogManager dialogManager;
   public static EnemyManager enemyManager;
   public static EnemyGenerator enemyGenerator;
   public static WorldChest worldChest = null;
   private static float collisionUpdateTimer = 0.0F;
   private static InGameState inGameState = null;
   private static InGameState prevInGameState = null;
   public static boolean nearSurface = false;
   public static GameMode gameMode = GameMode.ADVENTURE;
   public static boolean causticEnabled = true;
   public static Stats stats;
   public static Hashtable<String, Integer> debugTimings = new Hashtable<>();
   private static float debugInterval = 0.0F;

   public static void clear() {
      avatar = null;
      stats = null;
      seafloorBases = new ArrayList<>();
      outsideObjects = new ArrayList<>();
      submarines = new ArrayList<>();
      droids = new ArrayList<>();
      DialogManager.clear();
      dialogManager = null;
      enemyGenerator = null;
      inGameState = null;
      worldChest = null;
   }

   public static void registerAvatar(Avatar newAvatar) {
      avatar = newAvatar;
   }

   public static void registerSeafloorBase(SeafloorBase base) {
      base.rebuildDisplayLists();
      seafloorBases.add(base);
   }

   public static void registerOutsideObject(OutsideObj obj) {
      outsideObjects.add(obj);
   }

   public static void registerSubmarine(Submarine sub) {
      submarines.add(sub);
   }

   public static void registerDialogManager(DialogManager manager) {
      dialogManager = manager;
   }

   public static void registerDroid(Droid droid) {
      droids.add(droid);
   }

   public static void registerStats(Stats newStats) {
      stats = newStats;
   }

   public static void setInitialState(InGameState state) {
      inGameState = state;
   }

   public static void initScene(Coord spawnPos, Coord basePos, ArrayList<Coord> extraBases, EnemyGenerator.SpawningLevel spawningLevel, ArrayList<Coord> droidPositions) {
      if (Main.getGameState() == GameState.LOADING_GAME) {
         if (avatar == null) {
            avatar = new Avatar();
            avatar.setPos(new Point(spawnPos.x, -99999.0F, spawnPos.y));
            avatar.setLastSafeSpot(new Point(spawnPos.x, -99999.0F, spawnPos.y));
         }

         if (seafloorBases == null) {
            seafloorBases = new ArrayList<>();
            if (basePos != null) {
               seafloorBases.add(new SeafloorBase(new Point(basePos.x, ChunkManager.getHeight(basePos.x, basePos.y), basePos.y), Octree.BaseType.STARTER));
            }

            if (extraBases != null) {
               for (int i = 0; i < extraBases.size(); i++) {
                  seafloorBases.add(
                     new SeafloorBase(
                        new Point(extraBases.get(i).x, ChunkManager.getHeight(extraBases.get(i).x, extraBases.get(i).y), extraBases.get(i).y),
                        Octree.BaseType.EXTRA
                     )
                  );
               }
            }
         }

         if (outsideObjects == null) {
            outsideObjects = new ArrayList<>();
         }

         if (submarines == null) {
            submarines = new ArrayList<>();
         }

         if (enemyManager == null) {
            enemyManager = new EnemyManager();
         }

         enemyGenerator = new EnemyGenerator(spawningLevel);
         if (droids == null) {
            droids = new ArrayList<>();
            if (droidPositions != null) {
               for (int i = 0; i < droidPositions.size(); i++) {
                  droids.add(
                     new Droid(
                        new Point(droidPositions.get(i).x, ChunkManager.getHeight(droidPositions.get(i).x, droidPositions.get(i).y), droidPositions.get(i).y)
                     )
                  );
               }
            }
         }

         if (stats == null) {
            stats = new Stats();
         }

         MapRenderer.buildMap();
         if (dialogManager == null) {
            dialogManager = new DialogManager();
         }

         if (inGameState == null) {
            inGameState = InGameState.INITIALIZING;
         }
      }

      gravity = waterGravity;
      EnvironmentManager.init();
      SoundManager.stopAll();
   }

   public static void destroy() {
      if (avatar != null) {
         avatar.dispose();
         avatar = null;
      }

      if (seafloorBases != null) {
         seafloorBases.clear();
         seafloorBases = null;
      }

      if (outsideObjects != null) {
         outsideObjects = null;
      }

      if (submarines != null) {
         submarines.clear();
         submarines = null;
      }

      droids = null;
      dialogManager = null;
      enemyManager = null;
      enemyGenerator = null;
      inGameState = null;
      worldChest = null;
      stats = null;
   }

   public static void tick(float dt) {
      long startNano = 0L;
      if (Main.isDebug) {
         if (debugInterval <= 0.0F) {
            debugInterval++;
         }

         if ((debugInterval -= dt) <= 0.0F) {
            debugTimings = new Hashtable<>();
         }
      }

      ChunkManager.update(dt);
      if (avatar != null && avatar.isInside()) {
         gravity = insideGravity;
      } else {
         gravity = waterGravity;
      }

      if (Loading.worldManager.isReady()) {
         if (Main.getGameState() != GameState.MAIN_MENU) {
            if (seafloorBases != null) {
               if (Main.isDebug && debugInterval <= 0.0F) {
                  startNano = System.nanoTime();
               }

               avatar.setInSeafloorBase(false);

               for (int i = 0; i < seafloorBases.size(); i++) {
                  seafloorBases.get(i).update(dt);
               }

               if (Main.isDebug && debugInterval <= 0.0F) {
                  debugTimings.put("Tick Base", (int)((System.nanoTime() - startNano) / 1000L));
               }
            }

            if (outsideObjects != null) {
               for (int i = 0; i < outsideObjects.size(); i++) {
                  outsideObjects.get(i).update(dt);
               }
            }

            if (submarines != null && submarineSpawnProgress >= 1.0F) {
               for (int i = 0; i < submarines.size(); i++) {
                  submarines.get(i).update(dt);
               }
            }

            if (avatar != null) {
               if (Main.isDebug && debugInterval <= 0.0F) {
                  startNano = System.nanoTime();
               }

               avatar.update(dt);
               if (Main.isDebug && debugInterval <= 0.0F) {
                  debugTimings.put("Tick Avatar", (int)((System.nanoTime() - startNano) / 1000L));
               }
            }

            if (worldChest != null) {
               worldChest.update(dt);
            }

            if (droids != null) {
               for (int i = 0; i < droids.size(); i++) {
                  droids.get(i).update(dt);
               }
            }

            if (Main.isDebug && debugInterval <= 0.0F) {
               startNano = System.nanoTime();
            }

            updateWorldCollision(dt);
            if (Main.isDebug && debugInterval <= 0.0F) {
               debugTimings.put("Tick updateWorldAABB", (int)((System.nanoTime() - startNano) / 1000L));
            }

            if (Main.isDebug && debugInterval <= 0.0F) {
               startNano = System.nanoTime();
            }

            if (enemyManager != null) {
               enemyManager.tick(dt);
            }

            if (Main.isDebug && debugInterval <= 0.0F) {
               debugTimings.put("Tick enemies", (int)((System.nanoTime() - startNano) / 1000L));
            }

            if (dialogManager != null) {
               dialogManager.update(dt);
            }

            InteractionHint.update(dt);
            if (avatar != null && avatar.isAboveWater() && !RenderManager.freeCam && Main.getGameState() == GameState.PLAYING && gameMode.hasCinematic()) {
               Cinematic.init(Cinematic.CinematicState.ENDING);
               Main.gameState = GameState.CINEMATIC_INGAME;
               stats.updateTimePlayed();
            }
         }

         EnvironmentManager.update(dt);
         InGameState nextState = inGameState;
         if (inGameState != null) {
            switch (inGameState) {
               case INITIALIZING:
                  if (avatar.isInside() && avatar.isOnGround()) {
                     nextState = InGameState.ACTIVE;
                  }
               case ACTIVE:
               default:
                  break;
               case BOSS_KILLED:
                  if (!enemyManager.bossPresent) {
                     nextState = InGameState.ACTIVE;
                  }
            }
         }

         if (prevInGameState != inGameState && Main.isVerbose) {
            System.out.println(inGameState);
         }

         prevInGameState = inGameState;
         inGameState = nextState;
      }

      if (submarineSpawnProgress < 1.0F) {
         submarineSpawnProgress += dt;
      } else {
         submarineSpawnProgress = 1.0F;
      }

      if (baseSpawnProgress < 1.0F) {
         baseSpawnProgress += dt;
      } else {
         baseSpawnProgress = 1.0F;
      }

      if (outsideObjSpawnProgress < 1.0F) {
         outsideObjSpawnProgress += dt;
      } else {
         outsideObjSpawnProgress = 1.0F;
      }

      nearSurface = avatar != null && Camera.getPosition().y > -500.0F;
   }

   public static void render() {
      long startNano = 0L;
      if (Main.isDebug && debugInterval <= 0.0F) {
         startNano = System.nanoTime();
      }

      if (Shaders.testShaderEnabled) {
         Shaders.worldFloorTestShader.bind();
      } else {
         Shaders.worldFloorShader.bind();
      }

      RenderManager.setLight();
      Shaders.setUniform("time", GameTime.elapsedMillis);
      Shaders.setUniform("alphaAbyssColor", new Point(0.1F, 0.4F, 0.8F));
      RenderManager.enableCubemap();
      if (avatar != null) {
         Shaders.setUniform("lightLimit", 100.0);
      } else {
         Shaders.setUniform("lightLimit", 120.0);
      }

      Shaders.setUniform("visibleLimit", DepthAtmosphere.getFogDistance());
      Shaders.setUniform("glowColor", SkyDome.skyColor);
      Shaders.setUniform("useCaustic", causticEnabled);
      Shaders.setUniform("causticX", GameTime.elapsedMillis / 29550.0F);
      Shaders.setUniform("causticY", GameTime.elapsedMillis / 35900.0F);
      Shaders.setUniform("causticZoomX", 0.25);
      Shaders.setUniform("causticZoomY", 0.25);
      Shaders.setUniform("causticAlpha", DepthAtmosphere.getCausticIntensity() * (GameTime.getLightLevel() * 1.5F - 0.5F) * 0.1F);
      ChunkManager.renderTerrain();
      if (Main.isDebug && debugInterval <= 0.0F) {
         debugTimings.put("Chunk Floor", (int)((System.nanoTime() - startNano) / 1000L));
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         startNano = System.nanoTime();
      }

      if (Shaders.testShaderEnabled) {
         Shaders.worldTestShader.bind();
      } else {
         Shaders.worldShader.bind();
      }

      RenderManager.setLight();
      RenderManager.disableCubemap();
      Shaders.setUniform("time", GameTime.elapsedMillis);
      if (avatar != null) {
         Shaders.setUniform("lightLimit", 100.0);
      } else {
         Shaders.setUniform("lightLimit", 120.0);
      }

      Shaders.setUniform("visibleLimit", DepthAtmosphere.getFogDistance());
      Shaders.setUniform("glowColor", SkyDome.skyColor);
      if (avatar != null) {
         Point lightPos = avatar.getCameraPos().plus(0.0F, 500.0F, 0.0F);
         Shaders.setUniform("topLightPos", Camera.toViewSpace(lightPos));
      } else {
         Shaders.setUniform("topLightPos", new Point(0.0F, 1000.0F, 0.0F));
      }

      Shaders.setUniform("topLight", false);
      Shaders.setUniform("selected", false);
      Shaders.setUniform("selectedFactor", 2.0 + (Math.cos(GameTime.elapsedMillis / 250.0F) * 0.5 + 0.5) * 0.5);
      ChunkManager.renderElements();
      if (worldChest != null) {
         worldChest.render();
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         debugTimings.put("Chunk Elements", (int)((System.nanoTime() - startNano) / 1000L));
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         startNano = System.nanoTime();
      }

      EnvironmentManager.renderOpaque();
      if (Main.isDebug && debugInterval <= 0.0F) {
         debugTimings.put("Environment", (int)((System.nanoTime() - startNano) / 1000L));
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         startNano = System.nanoTime();
      }

      if (droids != null) {
         for (int i = 0; i < droids.size(); i++) {
            droids.get(i).render(i);
         }
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         debugTimings.put("Droids", (int)((System.nanoTime() - startNano) / 1000L));
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         startNano = System.nanoTime();
      }

      Shaders.enemyShader.bind();
      RenderManager.setLight();
      Shaders.setUniform("time", GameTime.elapsedMillis);
      Shaders.setUniform("alphaLightPercent", 0.0);
      Shaders.setUniform("emissive", false);
      if (avatar != null) {
         Shaders.setUniform("lightLimit", 100.0);
      } else {
         Shaders.setUniform("lightLimit", 120.0);
      }

      Shaders.setUniform("topLight", true);
      Shaders.setUniform("topLightPos", new Point(0.0F, 1000.0F, 0.0F));
      Shaders.setUniform("visibleLimit", DepthAtmosphere.getFogDistance());
      Shaders.setUniform("glowColor", SkyDome.skyColor);
      EnvironmentManager.renderSeaLife();
      if (Main.isDebug && debugInterval <= 0.0F) {
         debugTimings.put("Life", (int)((System.nanoTime() - startNano) / 1000L));
      }

      if (Main.getGameState() == GameState.PLAYING || Main.getGameState() == GameState.CINEMATIC_INGAME) {
         if (Main.isDebug && debugInterval <= 0.0F) {
            startNano = System.nanoTime();
         }

         if (enemyManager != null) {
            enemyManager.draw();
            enemyManager.drawExtra();
         }

         if (Main.isDebug && debugInterval <= 0.0F) {
            debugTimings.put("Enemies", (int)((System.nanoTime() - startNano) / 1000L));
         }
      }

      if (!nearSurface) {
         if (Main.isDebug && debugInterval <= 0.0F) {
            startNano = System.nanoTime();
         }

         if (Shaders.testShaderEnabled) {
            Shaders.worldTestShader.bind();
         } else {
            Shaders.worldShader.bind();
         }

         RenderManager.setLight();
         ChunkManager.renderTransparent();
         if (Main.isDebug && debugInterval <= 0.0F) {
            debugTimings.put("Chunk Transparency", (int)((System.nanoTime() - startNano) / 1000L));
         }
      }

      Shaders.insideShader.bind();
      RenderManager.setLight();
      Shaders.setUniform("alphaLight", false);
      Shaders.setUniform("emissive", false);
      Shaders.setUniform("inside", false);
      Shaders.setUniform("discardTransparency", false);
      Shaders.setUniform("selected", false);
      Shaders.setUniform("lightLimit", 100.0);
      Shaders.setUniform("visibleLimit", DepthAtmosphere.getFogDistance());
      Shaders.setUniform("glowColor", SkyDome.skyColor);
      Shaders.setUniform("time", GameTime.elapsedMillis);
      Shaders.setUniform("selectedFactor", 2.0 + (Math.cos(GameTime.elapsedMillis / 250.0F) * 0.5 + 0.5) * 0.5);
      if (!nearSurface) {
         if (Main.isDebug && debugInterval <= 0.0F) {
            startNano = System.nanoTime();
         }

         if (outsideObjects != null) {
            for (int i = 0; i < outsideObjects.size(); i++) {
               if (i == outsideObjects.size() - 1 && outsideObjSpawnProgress < 1.0F) {
                  GL11.glTranslatef(0.0F, -50.0F * (1.0F - outsideObjSpawnProgress), 0.0F);
               }

               outsideObjects.get(i).renderModel();
               if (i == outsideObjects.size() - 1 && outsideObjSpawnProgress < 1.0F) {
                  GL11.glTranslatef(0.0F, 50.0F * (1.0F - outsideObjSpawnProgress), 0.0F);
               }
            }
         }

         if (Main.isDebug && debugInterval <= 0.0F) {
            debugTimings.put("Outside Objects", (int)((System.nanoTime() - startNano) / 1000L));
         }
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         startNano = System.nanoTime();
      }

      if (submarines != null) {
         for (int i = 0; i < submarines.size(); i++) {
            if (i == submarines.size() - 1 && submarineSpawnProgress < 1.0F) {
               GL11.glTranslatef(0.0F, -100.0F * (1.0F - submarineSpawnProgress), 0.0F);
            }

            submarines.get(i).renderExterior();
            if (i == submarines.size() - 1 && submarineSpawnProgress < 1.0F) {
               GL11.glTranslatef(0.0F, 100.0F * (1.0F - submarineSpawnProgress), 0.0F);
            }
         }
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         debugTimings.put("Submarine", (int)((System.nanoTime() - startNano) / 1000L));
      }

      if (!nearSurface) {
         if (Main.isDebug && debugInterval <= 0.0F) {
            startNano = System.nanoTime();
         }

         if (seafloorBases != null) {
            for (int i = 0; i < seafloorBases.size(); i++) {
               if (i == seafloorBases.size() - 1 && baseSpawnProgress < 1.0F) {
                  GL11.glTranslatef(0.0F, -100.0F * (1.0F - baseSpawnProgress), 0.0F);
               }

               seafloorBases.get(i).renderOpaque();
               if (i == seafloorBases.size() - 1 && baseSpawnProgress < 1.0F) {
                  GL11.glTranslatef(0.0F, 100.0F * (1.0F - baseSpawnProgress), 0.0F);
               }
            }
         }

         if (Main.isDebug && debugInterval <= 0.0F) {
            debugTimings.put("Base", (int)((System.nanoTime() - startNano) / 1000L));
         }
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         startNano = System.nanoTime();
      }

      if (Camera.getPosition().y < 0.0F || Main.getGameState() == GameState.MAIN_MENU) {
         if (Shaders.testShaderEnabled) {
            Shaders.worldTestShader.bind();
         } else {
            Shaders.worldShader.bind();
         }

         RenderManager.setLight();
         EnvironmentManager.renderTransparent();
      }

      if (nearSurface) {
         Shaders.surfaceShader.bind();
         RenderManager.setLight();
         Shaders.setUniform("emissive", false);
         Shaders.setUniform("lightLimit", 100.0);
         Shaders.setUniform("visibleLimit", DepthAtmosphere.getFogDistance());
         Shaders.setUniform("glowColor", SkyDome.skyColor);
         Shaders.setUniform("time", GameTime.elapsedMillis);
         Shaders.setUniform("selectedFactor", 2.0 + (Math.cos(GameTime.elapsedMillis / 250.0F) * 0.5 + 0.5) * 0.5);
         EnvironmentManager.renderWater();
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         debugTimings.put("Environment Transparency", (int)((System.nanoTime() - startNano) / 1000L));
      }

      Shaders.insideShader.bind();
      if (Main.isDebug && debugInterval <= 0.0F) {
         startNano = System.nanoTime();
      }

      if (submarines != null) {
         for (int i = 0; i < submarines.size(); i++) {
            if (i == submarines.size() - 1 && submarineSpawnProgress < 1.0F) {
               GL11.glTranslatef(0.0F, -100.0F * (1.0F - submarineSpawnProgress), 0.0F);
            }

            submarines.get(i).renderCanopy();
            if (i == submarines.size() - 1 && submarineSpawnProgress < 1.0F) {
               GL11.glTranslatef(0.0F, 100.0F * (1.0F - submarineSpawnProgress), 0.0F);
            }
         }
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         debugTimings.put("Submarine Transparent", (int)((System.nanoTime() - startNano) / 1000L));
      }

      if (!nearSurface) {
         if (Main.isDebug && debugInterval <= 0.0F) {
            startNano = System.nanoTime();
         }

         if (outsideObjects != null) {
            for (int i = 0; i < outsideObjects.size(); i++) {
               if (i == outsideObjects.size() - 1 && outsideObjSpawnProgress < 1.0F) {
                  GL11.glTranslatef(0.0F, -50.0F * (1.0F - outsideObjSpawnProgress), 0.0F);
               }

               outsideObjects.get(i).renderEffects();
               if (i == outsideObjects.size() - 1 && outsideObjSpawnProgress < 1.0F) {
                  GL11.glTranslatef(0.0F, 50.0F * (1.0F - outsideObjSpawnProgress), 0.0F);
               }
            }
         }

         if (Main.isDebug && debugInterval <= 0.0F) {
            debugTimings.put("Outside Objects Transparent", (int)((System.nanoTime() - startNano) / 1000L));
         }
      }

      if (!nearSurface) {
         if (Main.isDebug && debugInterval <= 0.0F) {
            startNano = System.nanoTime();
         }

         if (seafloorBases != null) {
            for (int i = 0; i < seafloorBases.size(); i++) {
               if (i == seafloorBases.size() - 1 && baseSpawnProgress < 1.0F) {
                  GL11.glTranslatef(0.0F, -100.0F * (1.0F - baseSpawnProgress), 0.0F);
               }

               seafloorBases.get(i).renderAlpha();
               if (i == seafloorBases.size() - 1 && baseSpawnProgress < 1.0F) {
                  GL11.glTranslatef(0.0F, 100.0F * (1.0F - baseSpawnProgress), 0.0F);
               }
            }
         }

         if (Main.isDebug && debugInterval <= 0.0F) {
            debugTimings.put("Base Transparent", (int)((System.nanoTime() - startNano) / 1000L));
         }
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         startNano = System.nanoTime();
      }

      if (Main.getGameState() == GameState.PLAYING && avatar != null && !RenderManager.freeCam) {
         Shaders.handsShader.bind();
         RenderManager.disableCubemap();
         Shaders.setUniform("percent", 1.0);
         Shaders.setUniform("color", avatar.getColor());
         avatar.render();
      }

      if (Main.isDebug && debugInterval <= 0.0F) {
         debugTimings.put("Avatar Hands", (int)((System.nanoTime() - startNano) / 1000L));
      }

      if (Main.getGameState() == GameState.PLAYING || Main.getGameState() == GameState.CINEMATIC_INGAME) {
         if (Main.isDebug && debugInterval <= 0.0F) {
            startNano = System.nanoTime();
         }

         if (Shaders.testShaderEnabled) {
            Shaders.worldTestShader.bind();
         } else {
            Shaders.worldShader.bind();
         }

         RenderManager.setLight();
         if (avatar != null) {
            avatar.renderExtra();
         }

         if (Main.isDebug && debugInterval <= 0.0F) {
            debugTimings.put("Avatar", (int)((System.nanoTime() - startNano) / 1000L));
         }
      }
   }

   public static State resolveCollision(State from, State to) {
      return resolveCollision(from, to, null);
   }

   public static State resolveCollision(State from, State to, Submarine excludedSub) {
      to = enemyManager.resolveCollision(from, to);

      for (int i = 0; i < submarines.size(); i++) {
         if (submarines.get(i) != excludedSub) {
            to = submarines.get(i).resolveInteriorCollision(from, to);
         }
      }

      for (int i = 0; i < outsideObjects.size(); i++) {
         to = outsideObjects.get(i).resolveCollision(from, to);
      }

      for (int i = 0; i < seafloorBases.size(); i++) {
         to = seafloorBases.get(i).resolveCollision(from, to);
      }

      return to;
   }

   private static void updateWorldCollision(float dt) {
      if (collisionUpdateTimer < 1.0F) {
         collisionUpdateTimer += dt;
      } else {
         collisionUpdateTimer--;
         worldCollisionBoxes = new ArrayList<>();
         if (seafloorBases != null) {
            for (int i = 0; i < seafloorBases.size(); i++) {
               if (seafloorBases.get(i).getPos().distanceTo(Camera.getPosition()) < ChunkManager.viewDistance) {
                  worldCollisionBoxes.add(new CollisionBox(seafloorBases.get(i).getBoundingBox(), seafloorBases.get(i).getPos(), new Point()));
               }
            }
         }

         if (outsideObjects != null) {
            for (int i = 0; i < outsideObjects.size(); i++) {
               if (outsideObjects.get(i).getPosition().distanceTo(Camera.getPosition()) < ChunkManager.viewDistance) {
                  worldCollisionBoxes.add(new CollisionBox(outsideObjects.get(i).getAABB(), outsideObjects.get(i).getPosition(), new Point()));
               }
            }
         }

         if (submarines != null) {
            for (int i = 0; i < submarines.size(); i++) {
               if (submarines.get(i).getPosition().distanceTo(Camera.getPosition()) < ChunkManager.viewDistance) {
                  worldCollisionBoxes.add(new CollisionBox(Submarine.getBoundingBox(), submarines.get(i).getPosition(), submarines.get(i).getRotation()));
               }
            }
         }

         worldCollisionBoxes.addAll(ChunkManager.getCollisionBoxes());
      }
   }

   public static ArrayList<CollisionBox> getCollisionBoxes() {
      return worldCollisionBoxes;
   }

   public static CollisionBox getCollisionBoxAt(Point point) {
      for (int i = 0; i < worldCollisionBoxes.size(); i++) {
         if (CollisionDetector.containsPoint(point, worldCollisionBoxes.get(i).getBounds(), worldCollisionBoxes.get(i).getPosition(), worldCollisionBoxes.get(i).getRotation())) {
            return worldCollisionBoxes.get(i);
         }
      }

      return null;
   }

   public static void spawnSeafloorBase(Point pos) {
      for (int i = 0; i < seafloorBases.size(); i++) {
         if (pos.x >= seafloorBases.get(i).getPos().x + seafloorBases.get(i).getBoundingBox().min.x - 50.0F
            && pos.x <= seafloorBases.get(i).getPos().x + seafloorBases.get(i).getBoundingBox().max.x + 50.0F
            && pos.z >= seafloorBases.get(i).getPos().z + seafloorBases.get(i).getBoundingBox().min.z - 50.0F
            && pos.z <= seafloorBases.get(i).getPos().z + seafloorBases.get(i).getBoundingBox().max.z + 50.0F) {
            return;
         }
      }

      seafloorBases.add(new SeafloorBase(pos.plus(0.0F, 0.0F, 0.0F), Octree.BaseType.EMPTY));
      avatar.getInventory().consumeCurrentItem();
      baseSpawnProgress = 0.0F;
      Camera.addShake(1.0F);
      EnvironmentManager.addParticleBurst(new ParticleBurst(pos, new Point(0.0F, 1.0F, 0.0F), 5.0F, 20));

      for (int i = 0; i < 15; i++) {
         EnvironmentManager.addParticleBurst(
            new ParticleBurst(
               pos.plus((float)Math.cos(((Math.PI * 2F) / 15F) * i) * 50.0F, -20.0F, (float)Math.sin(((Math.PI * 2F) / 15F) * i) * 50.0F),
               new Point((Math.random() - 0.5) * 0.5, 1.0, (Math.random() - 0.5) * 0.5),
               30.0F + (float)Math.random() * 20.0F,
               5
            )
         );
      }

      SoundManager.playSound(SoundManager.sfxRisingFromFloor, pos, 0.5F);
   }

   public static void spawnSubmarine(Point pos, boolean consumeItem) {
      for (int i = 0; i < seafloorBases.size(); i++) {
         if (pos.x >= seafloorBases.get(i).getPos().x + seafloorBases.get(i).getBoundingBox().min.x - 50.0F
            && pos.x <= seafloorBases.get(i).getPos().x + seafloorBases.get(i).getBoundingBox().max.x + 50.0F
            && pos.z >= seafloorBases.get(i).getPos().z + seafloorBases.get(i).getBoundingBox().min.z - 50.0F
            && pos.z <= seafloorBases.get(i).getPos().z + seafloorBases.get(i).getBoundingBox().max.z + 50.0F) {
            return;
         }
      }

      submarines.add(new Submarine(pos.plus(0.0F, 50.0F, 0.0F)));
      if (consumeItem) {
         avatar.getInventory().consumeCurrentItem();
      }

      submarineSpawnProgress = 0.0F;
      Camera.addShake(1.0F);
      EnvironmentManager.addParticleBurst(new ParticleBurst(pos, new Point(0.0F, 1.0F, 0.0F), 5.0F, 20));

      for (int i = 0; i < 15; i++) {
         EnvironmentManager.addParticleBurst(
            new ParticleBurst(
               pos.plus((float)Math.cos(((Math.PI * 2F) / 15F) * i) * 50.0F, -20.0F, (float)Math.sin(((Math.PI * 2F) / 15F) * i) * 50.0F),
               new Point((Math.random() - 0.5) * 0.5, 1.0, (Math.random() - 0.5) * 0.5),
               30.0F + (float)Math.random() * 20.0F,
               5
            )
         );
      }

      SoundManager.playSound(SoundManager.sfxRisingFromFloor, pos, 0.5F);
   }

   public static void spawnOutsideObject(OutsideObj obj) {
      for (int i = 0; i < outsideObjects.size(); i++) {
         if (outsideObjects.get(i).getPosition().x == obj.getPosition().x && outsideObjects.get(i).getPosition().z == obj.getPosition().z) {
            return;
         }
      }

      outsideObjects.add(obj);
      avatar.getInventory().consumeCurrentItem();
      outsideObjSpawnProgress = 0.0F;
      Camera.addShake(0.5F);

      for (int i = 0; i < 7; i++) {
         EnvironmentManager.addParticleBurst(
            new ParticleBurst(
               obj.getPosition().plus((float)Math.cos((Math.PI * 2.0 / 7.0) * i) * 5.0F, -20.0F, (float)Math.sin((Math.PI * 2.0 / 7.0) * i) * 5.0F),
               new Point((Math.random() - 0.5) * 0.5, 1.0, (Math.random() - 0.5) * 0.5),
               30.0F + (float)Math.random() * 20.0F,
               2
            )
         );
      }

      SoundManager.playSound(SoundManager.sfxRisingFromFloor, obj.getPosition(), 0.8F, 0.5F);
   }

   public static void spawnDroid(Droid droid) {
      droids.add(droid);
      avatar.getInventory().consumeCurrentItem();
      outsideObjSpawnProgress = 0.0F;
      Camera.addShake(0.5F);

      for (int i = 0; i < 7; i++) {
         EnvironmentManager.addParticleBurst(
            new ParticleBurst(
               droid.getPosition().plus((float)Math.cos((Math.PI * 2.0 / 7.0) * i) * 5.0F, -20.0F, (float)Math.sin((Math.PI * 2.0 / 7.0) * i) * 5.0F),
               new Point((Math.random() - 0.5) * 0.5, 1.0, (Math.random() - 0.5) * 0.5),
               30.0F + (float)Math.random() * 20.0F,
               2
            )
         );
      }

      SoundManager.playSound(SoundManager.sfxRisingFromFloor, droid.getPosition(), 0.8F, 0.5F);
   }

   public static void pickUpNearbyObjects() {
      for (int i = outsideObjects.size() - 1; i >= 0; i--) {
         if (outsideObjects.get(i).isNearPlayer()) {
            if (outsideObjects.get(i).canPickUp()) {
               if (avatar != null && avatar.pickupItem(new Item(outsideObjects.get(i).getItemType()))) {
                  outsideObjects.remove(i);
               }
            } else {
               InteractionHint.addTimedHint("Must be empty", 1.0F);
            }
         }
      }
   }

   public static Submarine getActiveSubmarine() {
      for (int i = 0; i < submarines.size(); i++) {
         if (submarines.get(i).isMoving()) {
            return submarines.get(i);
         }
      }

      return null;
   }

   public static ArrayList<SeafloorBase> getSeafloorBases() {
      return seafloorBases;
   }

   public static ArrayList<OutsideObj> getOutsideObjects() {
      return outsideObjects;
   }

   public static ArrayList<Submarine> getSubmarines() {
      return submarines;
   }

   public static ArrayList<Droid> getDroids() {
      return droids;
   }

   public static void save() {
      SaveManager.saveGame();
   }

   public static void markGameComplete() {
      if (gameMode == GameMode.ADVENTURE) {
         gameMode = GameMode.ADVENTURE_DONE;
      }

      if (gameMode == GameMode.SURVIVOR) {
         gameMode = GameMode.SURVIVOR_DONE;
      }

      SaveManager.saveGame();
   }

   public static void onPlayerDeath() {
      stats.recordDeath();
      if (enemyManager != null) {
         enemyManager.removeAll();
      }
   }

   public static void onBossKilled() {
      if (inGameState == InGameState.ACTIVE) {
         inGameState = InGameState.BOSS_KILLED;
      }
   }

   public static InGameState getInGameState() {
      return inGameState;
   }
}
