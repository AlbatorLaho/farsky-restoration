package game.sounds;

import game.Main;
import game.chunks.InteractiveElmt;
import game.chunks.chunkElements.ChunkElement;
import game.util.IntCoord;
import game.chunks.chunkElements.AbyssElement;
import game.chunks.chunkElements.AbyssalAlga;
import game.chunks.chunkElements.Alga;
import game.chunks.chunkElements.Chest;
import game.chunks.chunkElements.GiantAlga;
import game.chunks.chunkElements.GiantAlgaLight;
import game.chunks.chunkElements.DarkSeaGrass;
import game.chunks.chunkElements.Vent;
import game.chunks.chunkElements.JellyPlant;
import game.chunks.chunkElements.TreasureChest;
import game.chunks.chunkElements.Coral;
import game.chunks.chunkElements.RockRing;
import game.chunks.chunkElements.Rock;
import game.chunks.chunkElements.OreDeposit;
import game.chunks.chunkElements.Kelp;
import game.chunks.chunkElements.SeaBush;
import game.chunks.chunkElements.SeaGrass;
import game.chunks.chunkElements.SeaSponge;
import game.chunks.chunkElements.Seaweed;
import game.chunks.chunkElements.LargeSeaweed;
import game.chunks.chunkElements.Shipwreck;
import game.chunks.chunkElements.SubmarinePart;
import game.chunks.chunkElements.TerrainOverlay;
import game.cinematic.Cinematic;
import game.collision.AABB;
import game.collision.CollisionDetector;
import game.environment.DepthAtmosphere;
import game.environment.EnvironmentManager;
import game.environment.pickup.ItemPickup;
import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.types.Inventory;
import game.manager.GameMode;
import game.manager.Loading;
import game.manager.TextureManager;
import game.render.QuadVbo;
import game.render.Vertex;
import game.manager.GameScene;
import game.manager.GameTime;
import game.submarine.SubmarinePiece;
import game.util.Coord;
import game.util.FontRenderer;
import game.util.Point;
import game.util.Segment;
import game.util.State;
import game.world.World;
import game.world.structure.GamePlayElmt;
import game.world.structure.GamePlayType;
import game.world.structure.InteractionUsed;
import game.world.structure.TerrainSample;
import game.world.structure.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import org.lwjgl.opengl.GL11;

public class ChunkLayer {
   private static SoundSource musicSource;
   private static SoundSource backgroundSource;
   private static SoundSource underwaterSource;
   private static float musicCooldown;
   private static ArrayList<Integer> shallowPlaylist;
   private static ArrayList<Integer> nightPlaylist;
   private static ArrayList<Integer> deepPlaylist;
   private ArrayList<ChunkElement> algas = new ArrayList<>();
   private ArrayList<ChunkElement> giantAlgas = new ArrayList<>();
   private ArrayList<ChunkElement> giantAlgaLights = new ArrayList<>();
   private ArrayList<ChunkElement> jellyPlants = new ArrayList<>();
   private ArrayList<ChunkElement> seaGrasses = new ArrayList<>();
   private ArrayList<ChunkElement> chests = new ArrayList<>();
   private ArrayList<ChunkElement> kelps = new ArrayList<>();
   private ArrayList<ChunkElement> seaBushes = new ArrayList<>();
   private ArrayList<ChunkElement> rocks = new ArrayList<>();
   private ArrayList<ChunkElement> seaSponges = new ArrayList<>();
   private ArrayList<ChunkElement> seaweeds = new ArrayList<>();
   private ArrayList<ChunkElement> corals = new ArrayList<>();
   private ArrayList<ChunkElement> rockRings = new ArrayList<>();
   private ArrayList<ChunkElement> terrainOverlays = new ArrayList<>();
   private ArrayList<ChunkElement> oreDeposits = new ArrayList<>();
   private ArrayList<ChunkElement> shipwrecks = new ArrayList<>();
   private ArrayList<ChunkElement> submarineParts = new ArrayList<>();
   private ArrayList<ChunkElement> abyssElements = new ArrayList<>();
   private ArrayList<ChunkElement> vents = new ArrayList<>();
   private ArrayList<ChunkElement> abyssalAlgas = new ArrayList<>();
   private ArrayList<ChunkElement> darkSeaGrasses = new ArrayList<>();
   private ArrayList<ChunkElement> largeSeaweeds = new ArrayList<>();
   private ArrayList<ChunkElement> treasureChests = new ArrayList<>();
   private QuadVbo seaGrassVbo;
   private QuadVbo darkSeaGrassVbo;
   private QuadVbo kelpVbo;
   private QuadVbo seaBushVbo;
   private static int[] chestCounts;
   private static int[] coralOverlayCounts;
   private static int[] grassOverlayCounts;
   private static int[] goldDepositCounts;
   private static int[] crystalDepositCounts;
   private static int[] silverDepositCounts;
   private static int[] shipwreckCounts;
   private static int[] submarinePartCounts;
   private static int[] abandonedBaseCounts;
   private static int[] droidCounts;
   private static int[] treasureChestCounts;

   public static void init() {
      musicSource = new SoundSource(SoundManager.musicAwakening);
      musicCooldown = 60.0F;
      shallowPlaylist = new ArrayList<>();
      ArrayList<Integer> temp = new ArrayList<>();
      temp.add(SoundManager.musicFarsky);
      temp.add(SoundManager.musicWithTheSeaAsACage);

      for (int i = temp.size() - 1; i >= 0; i--) {
         shallowPlaylist.add(temp.remove((int)(temp.size() * Math.random())));
      }

      shallowPlaylist.add(SoundManager.musicAwakening);
      nightPlaylist = new ArrayList<>();
      nightPlaylist.add(SoundManager.musicDarkCrushingDark);
      nightPlaylist.add(SoundManager.musicLurker);
      deepPlaylist = new ArrayList<>();
      deepPlaylist.add(SoundManager.musicTimeDoesntFlow);
      deepPlaylist.add(SoundManager.musicIntoColdDarkness);
      backgroundSource = new SoundSource(SoundManager.ambientBackground);
      backgroundSource.setSoundType(SoundType.AMBIENT);
      backgroundSource.setMaxVolume(0.3F);
      underwaterSource = new SoundSource(SoundManager.ambientUnderwater);
      underwaterSource.setSoundType(SoundType.AMBIENT);
      underwaterSource.setLooping(true);
   }

   public static void update(float delta) {
      switch (Main.getGameState()) {
         case STARTUP:
         case RELOADING:
         case LOADING_GAME:
         case LOADING_MENU:
            underwaterSource.stop();
            backgroundSource.stop();
            musicSource.stop();
            break;
         case MAIN_MENU:
            updateUnderwaterAmbient();
            if (!backgroundSource.isPlaying()) {
               backgroundSource.play();
               backgroundSource.setVolume(1.0F);
            }
            break;
         case PLAYING:
            if (Main.hasStateChanged()) {
               underwaterSource.resume();
               backgroundSource.startFadeIn(true);
               backgroundSource.resume();
               musicSource.startFadeIn(true);
               musicSource.resume();
            }

            if (GameScene.avatar != null && GameScene.avatar.isInside()) {
               underwaterSource.stop();
            } else {
               updateUnderwaterAmbient();
            }

            if (musicSource != null && musicSource.isPlaying()) {
               backgroundSource.stop();
            } else if (!backgroundSource.isPlaying()) {
               backgroundSource.play();
            }
         case CINEMATIC_INGAME:
            break;
         default:
            underwaterSource.pause();
            backgroundSource.startFadeOut(true);
            musicSource.startFadeOut(true);
      }

      switch (Main.getGameState()) {
         case PLAYING:
            if (GameScene.getInGameState() != null) {
               switch (GameScene.getInGameState()) {
                  case INITIALIZING:
                  default:
                     break;
                  case ACTIVE:
                     if (musicSource != null && musicSource.getBufferId() == SoundManager.musicKraken) {
                        if (musicSource.isPlaying()) {
                           musicSource.startFadeOut(true);
                        } else {
                           musicCooldown = 0.0F;
                        }
                     }

                     if (!musicSource.isPlaying() && (musicCooldown -= delta) <= 0.0F) {
                        if (GameTime.isNight()) {
                           int trackId = nightPlaylist.remove(0);
                           playMusic(trackId, 1.0F);
                           nightPlaylist.add(trackId);
                        } else if (DepthAtmosphere.getDepthZone() > 1) {
                           int trackId = deepPlaylist.remove(0);
                           playMusic(trackId, 1.0F);
                           deepPlaylist.add(trackId);
                        } else if (GameTime.isDusk()) {
                           playMusic(SoundManager.musicUnderwaterSunrise, 1.0F);
                        } else {
                           int trackId = shallowPlaylist.remove(0);
                           playMusic(trackId, 1.0F);
                           shallowPlaylist.add(trackId);
                        }

                        musicCooldown = 420.0F + 240.0F * (float)Math.random();
                     }

                     if (DepthAtmosphere.getDepthZone() > 1 && musicSource.getBufferId() != SoundManager.musicTimeDoesntFlow && musicSource.getBufferId() != SoundManager.musicIntoColdDarkness) {
                        musicSource.startFadeOut(true);
                        musicCooldown = 0.0F;
                     }
                     break;
                  case BOSS_KILLED:
                     playMusic(SoundManager.musicKraken, 1.6F);
               }
            }
            break;
         case CINEMATIC_INGAME:
            if (Cinematic.isShowingCredits()) {
               if (musicSource.isPlaying()) {
                  musicSource.stop();
               }

               if (!backgroundSource.isPlaying()) {
                  backgroundSource.play();
                  backgroundSource.setVolume(1.0F);
               }
            } else if (!musicSource.isPlaying()) {
               playMusic(SoundManager.musicAwakening, 1.0F);
            }
		default:
			break;
      }

      backgroundSource.update(delta);
      musicSource.update(delta);
   }

   public static void stopAll() {
      backgroundSource.applyVolume();
      underwaterSource.applyVolume();
      musicSource.applyVolume();
   }

   private static void updateUnderwaterAmbient() {
      if (!underwaterSource.isPlaying()) {
         underwaterSource.play();
         underwaterSource.setVolume(0.03F);
      }

      if (underwaterSource.isPlaying()) {
         underwaterSource.setPitch(DepthAtmosphere.getSunBrightness());
      }
   }

   private static void playMusic(int bufferId, float volumeMult) {
      if (musicSource == null || !musicSource.isPlaying() || musicSource.getBufferId() != bufferId) {
         if (musicSource != null) {
            musicSource.stop();
         }

         musicSource = new SoundSource(bufferId);
         musicSource.setSoundType(SoundType.MUSIC);
         musicSource.setMaxVolume(0.15F * volumeMult);
         musicSource.applyVolume();
         musicSource.play();
         if (backgroundSource != null) {
            backgroundSource.startFadeOut(true);
         }
      }
   }

   public void addElement(ChunkElement element) {
      if (element instanceof Alga) {
         this.algas.add(element);
      } else if (element instanceof GiantAlga) {
         this.giantAlgas.add(element);
      } else if (element instanceof SeaGrass) {
         this.seaGrasses.add(element);
      } else if (element instanceof Chest) {
         this.chests.add(element);
      } else if (element instanceof Kelp) {
         this.kelps.add(element);
      } else if (element instanceof SeaBush) {
         this.seaBushes.add(element);
      } else if (element instanceof Rock) {
         this.rocks.add(element);
      } else if (element instanceof SeaSponge) {
         this.seaSponges.add(element);
      } else if (element instanceof Seaweed) {
         this.seaweeds.add(element);
      } else if (element instanceof Coral) {
         this.corals.add(element);
      } else if (element instanceof RockRing) {
         this.rockRings.add(element);
      } else if (element instanceof TerrainOverlay) {
         this.terrainOverlays.add(element);
      } else if (element instanceof OreDeposit) {
         this.oreDeposits.add(element);
      } else if (element instanceof Shipwreck) {
         this.shipwrecks.add(element);
      } else if (element instanceof SubmarinePart) {
         this.submarineParts.add(element);
      } else if (element instanceof GiantAlgaLight) {
         this.giantAlgaLights.add(element);
      } else if (element instanceof JellyPlant) {
         this.jellyPlants.add(element);
      } else if (element instanceof AbyssElement) {
         this.abyssElements.add(element);
      } else if (element instanceof Vent) {
         this.vents.add(element);
      } else if (element instanceof AbyssalAlga) {
         this.abyssalAlgas.add(element);
      } else if (element instanceof DarkSeaGrass) {
         this.darkSeaGrasses.add(element);
      } else if (element instanceof LargeSeaweed) {
         this.largeSeaweeds.add(element);
      } else {
         if (element instanceof TreasureChest) {
            this.treasureChests.add(element);
         }
      }
   }

   public void render() {
      if (this.algas.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Alga.beginBatch();

         for (int i = 0; i < this.algas.size(); i++) {
            (this.algas.get(i)).render();
         }

         Alga.endBatch();
      }

      if (this.giantAlgaLights.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GiantAlgaLight.beginBatch();

         for (int i = 0; i < this.giantAlgaLights.size(); i++) {
            (this.giantAlgaLights.get(i)).render();
         }

         GiantAlgaLight.endBatch();
      }

      if (this.jellyPlants.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         JellyPlant.beginBatch();

         for (int i = 0; i < this.jellyPlants.size(); i++) {
            (this.jellyPlants.get(i)).render();
         }

         JellyPlant.endBatch();
      }

      if (this.giantAlgas.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GiantAlga.beginBatch();

         for (int i = 0; i < this.giantAlgas.size(); i++) {
            (this.giantAlgas.get(i)).render();
         }

         GiantAlga.endBatch();
      }

      if (this.largeSeaweeds.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         LargeSeaweed.beginBatch();

         for (int i = 0; i < this.largeSeaweeds.size(); i++) {
            (this.largeSeaweeds.get(i)).render();
         }

         LargeSeaweed.endBatch();
      }

      if (this.seaGrasses.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         SeaGrass.beginBatch();
         this.seaGrassVbo.render();
         SeaGrass.endBatch();
      }

      if (this.darkSeaGrasses.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         DarkSeaGrass.beginBatch();
         this.darkSeaGrassVbo.render();
         DarkSeaGrass.endBatch();
      }

      if (this.chests.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

         for (int i = 0; i < this.chests.size(); i++) {
            (this.chests.get(i)).render();
         }
      }

      if (this.treasureChests.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

         for (int i = 0; i < this.treasureChests.size(); i++) {
            (this.treasureChests.get(i)).render();
         }
      }

      if (this.kelps.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Kelp.beginBatch();
         this.kelpVbo.render();
         Kelp.endBatch();
      }

      if (this.rocks.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Rock.beginBatch();

         for (int i = 0; i < this.rocks.size(); i++) {
            (this.rocks.get(i)).render();
         }
      }

      if (this.seaSponges.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         SeaSponge.beginBatch();

         for (int i = 0; i < this.seaSponges.size(); i++) {
            (this.seaSponges.get(i)).render();
         }
      }

      if (this.corals.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Coral.beginBatch();

         for (int i = 0; i < this.corals.size(); i++) {
            (this.corals.get(i)).render();
         }

         Coral.endBatch();
      }

      if (this.rockRings.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         RockRing.beginBatch();

         for (int i = 0; i < this.rockRings.size(); i++) {
            (this.rockRings.get(i)).render();
         }
      }

      if (this.terrainOverlays.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         (this.terrainOverlays.get(0)).render();
      }

      if (this.oreDeposits.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

         for (int i = 0; i < this.oreDeposits.size(); i++) {
            (this.oreDeposits.get(i)).render();
         }

         OreDeposit.endBatch();
      }

      if (this.shipwrecks.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Shipwreck.beginBatch();

         for (int i = 0; i < this.shipwrecks.size(); i++) {
            (this.shipwrecks.get(i)).render();
         }
      }

      if (this.submarineParts.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

         for (int i = 0; i < this.submarineParts.size(); i++) {
            (this.submarineParts.get(i)).render();
         }
      }

      if (this.abyssElements.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbyssElement.beginBatch();

         for (int i = 0; i < this.abyssElements.size(); i++) {
            (this.abyssElements.get(i)).render();
         }

         AbyssElement.endBatch();
      }

      if (this.vents.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Vent.beginBatch();

         for (int i = 0; i < this.vents.size(); i++) {
            (this.vents.get(i)).render();
         }
      }

      if (this.abyssalAlgas.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbyssalAlga.beginBatch();

         for (int i = 0; i < this.abyssalAlgas.size(); i++) {
            (this.abyssalAlgas.get(i)).render();
         }

         AbyssalAlga.endBatch();
      }
   }

   public void renderTransparent() {
      if (this.seaweeds.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Seaweed.beginBatch();

         for (int i = 0; i < this.seaweeds.size(); i++) {
            (this.seaweeds.get(i)).render();
         }

         Seaweed.endBatch();
      }

      if (this.seaBushes.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         SeaBush.beginBatch();
         this.seaBushVbo.render();
         SeaBush.endBatch();
      }

      if (this.submarineParts.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

         for (int i = 0; i < this.submarineParts.size(); i++) {
            (this.submarineParts.get(i)).renderTransparent();
         }
      }
   }

   public void update(float delta, Coord chunkPos) {
      if (GameScene.avatar != null && chunkPos.distanceTo(GameScene.avatar.getPos2D()) < 256.0F) {
         for (int i = 0; i < this.chests.size(); i++) {
            (this.chests.get(i)).update(delta);
         }

         for (int i = 0; i < this.treasureChests.size(); i++) {
            (this.treasureChests.get(i)).update(delta);
         }

         for (int i = 0; i < this.seaweeds.size(); i++) {
            (this.seaweeds.get(i)).update(delta);
         }
      }

      for (int i = 0; i < this.rockRings.size(); i++) {
         (this.rockRings.get(i)).update(delta);
      }

      for (int i = 0; i < this.submarineParts.size(); i++) {
         (this.submarineParts.get(i)).update(delta);
      }

      for (int i = 0; i < this.vents.size(); i++) {
         (this.vents.get(i)).update(delta);
      }

      for (int i = 0; i < this.oreDeposits.size(); i++) {
         (this.oreDeposits.get(i)).update(delta);
      }
   }

   public State resolveCollision(State movement, State state) {
      for (int i = 0; i < this.oreDeposits.size(); i++) {
         state = (this.oreDeposits.get(i)).getBoundingBox().resolveCollision(movement, state);
      }

      for (int i = 0; i < this.shipwrecks.size(); i++) {
         state = (this.shipwrecks.get(i)).resolveCollision(movement, state);
      }

      for (int i = 0; i < this.vents.size(); i++) {
         state = (this.vents.get(i)).getBoundingBox().resolveCollision(movement, state);
      }

      return state;
   }

   public void buildVbos(TerrainSample[][] terrain) {
      ArrayList<Vertex> vertices = new ArrayList<>();

      for (int i = 0; i < this.seaGrasses.size(); i++) {
         vertices.addAll((this.seaGrasses.get(i)).buildVertices(null));
      }

      this.seaGrassVbo = new QuadVbo(vertices, true);
      ArrayList<Vertex> vertices2 = new ArrayList<>();

      for (int i = 0; i < this.darkSeaGrasses.size(); i++) {
         vertices2.addAll((this.darkSeaGrasses.get(i)).buildVertices(null));
      }

      this.darkSeaGrassVbo = new QuadVbo(vertices2, true);
      vertices = new ArrayList<>();

      for (int i = 0; i < this.kelps.size(); i++) {
         vertices.addAll((this.kelps.get(i)).buildVertices(null));
      }

      this.kelpVbo = new QuadVbo(vertices, true);
      vertices2 = new ArrayList<>();

      for (int i = 0; i < this.seaBushes.size(); i++) {
         vertices2.addAll((this.seaBushes.get(i)).buildVertices(null));
      }

      this.seaBushVbo = new QuadVbo(vertices2, true);

      for (int i = 0; i < this.terrainOverlays.size(); i++) {
         (this.terrainOverlays.get(i)).buildVertices(terrain);
      }
   }

   public void unload() {
      for (int i = 0; i < this.algas.size(); i++) {
         (this.algas.get(i)).onUnload();
      }

      for (int i = 0; i < this.giantAlgas.size(); i++) {
         (this.giantAlgas.get(i)).onUnload();
      }

      for (int i = 0; i < this.seaGrasses.size(); i++) {
         (this.seaGrasses.get(i)).onUnload();
      }

      for (int i = 0; i < this.kelps.size(); i++) {
         (this.kelps.get(i)).onUnload();
      }

      for (int i = 0; i < this.seaBushes.size(); i++) {
         (this.seaBushes.get(i)).onUnload();
      }

      for (int i = 0; i < this.rocks.size(); i++) {
         (this.rocks.get(i)).onUnload();
      }

      for (int i = 0; i < this.seaSponges.size(); i++) {
         (this.seaSponges.get(i)).onUnload();
      }

      for (int i = 0; i < this.chests.size(); i++) {
         (this.chests.get(i)).onUnload();
      }

      for (int i = 0; i < this.treasureChests.size(); i++) {
         (this.treasureChests.get(i)).onUnload();
      }

      for (int i = 0; i < this.rockRings.size(); i++) {
         (this.rockRings.get(i)).onUnload();
      }

      for (int i = 0; i < this.terrainOverlays.size(); i++) {
         (this.terrainOverlays.get(i)).onUnload();
      }

      this.seaGrassVbo.dispose();
      this.darkSeaGrassVbo.dispose();
      this.kelpVbo.dispose();
      this.seaBushVbo.dispose();
   }

   public AABB getBoundingBox() {
      AABB bounds = null;

      for (int i = 0; i < this.algas.size(); i++) {
         if (bounds == null) {
            bounds = (this.algas.get(i)).getBoundingBox();
         } else {
            bounds = bounds.union((this.algas.get(i)).getBoundingBox());
         }
      }

      for (int i = 0; i < this.giantAlgas.size(); i++) {
         if (bounds == null) {
            bounds = (this.giantAlgas.get(i)).getBoundingBox();
         } else {
            bounds = bounds.union((this.giantAlgas.get(i)).getBoundingBox());
         }
      }

      for (int i = 0; i < this.largeSeaweeds.size(); i++) {
         if (bounds == null) {
            bounds = (this.largeSeaweeds.get(i)).getBoundingBox();
         } else {
            bounds = bounds.union((this.largeSeaweeds.get(i)).getBoundingBox());
         }
      }

      for (int i = 0; i < this.giantAlgaLights.size(); i++) {
         if (bounds == null) {
            bounds = (this.giantAlgaLights.get(i)).getBoundingBox();
         } else {
            bounds = bounds.union((this.giantAlgaLights.get(i)).getBoundingBox());
         }
      }

      for (int i = 0; i < this.jellyPlants.size(); i++) {
         if (bounds == null) {
            bounds = (this.jellyPlants.get(i)).getBoundingBox();
         } else {
            bounds = bounds.union((this.jellyPlants.get(i)).getBoundingBox());
         }
      }

      for (int i = 0; i < this.shipwrecks.size(); i++) {
         if (bounds == null) {
            bounds = (this.shipwrecks.get(i)).getBoundingBox();
         } else {
            bounds = bounds.union((this.shipwrecks.get(i)).getBoundingBox());
         }
      }

      return bounds;
   }

   public AABB getStructureBoundingBox() {
      if (this.shipwrecks.size() > 0) {
         return (this.shipwrecks.get(0)).getBoundingBox();
      } else {
         return this.oreDeposits.size() > 0 ? (this.oreDeposits.get(0)).getBoundingBox() : null;
      }
   }

   public void harvestPlants(int chunkX, int chunkZ, Segment segment) {
      Segment localSegment = segment.copy();
      localSegment.translate(new Point((float)(-chunkX), 0.0F, (float)(-chunkZ)));

      for (int i = this.giantAlgas.size() - 1; i >= 0; i--) {
         if (CollisionDetector.segmentIntersects(localSegment, (this.giantAlgas.get(i)).getLocalBoundingBox(), (this.giantAlgas.get(i)).getPosition(), new Point())) {
            for (int j = 0; j < 5; j++) {
               if (Math.random() < 0.5) {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.giantAlgas.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.FERTILIZER));
               } else {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.giantAlgas.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.SEAWEED));
               }
            }

            Loading.worldManager.addInteractionUsed(new InteractionUsed((this.giantAlgas.get(i)).getPosition().toCoord().plus(new Coord(chunkX, chunkZ)), InteractiveElmt.SEAWEED));
            this.giantAlgas.remove(i);
         }
      }

      for (int i = this.largeSeaweeds.size() - 1; i >= 0; i--) {
         if (CollisionDetector.segmentIntersects(localSegment, (this.largeSeaweeds.get(i)).getLocalBoundingBox(), (this.largeSeaweeds.get(i)).getPosition(), new Point())) {
            for (int j = 0; j < 5; j++) {
               if (Math.random() < 0.5) {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.largeSeaweeds.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.FERTILIZER));
               } else {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.largeSeaweeds.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.SEAWEED));
               }
            }

            Loading.worldManager.addInteractionUsed(new InteractionUsed((this.largeSeaweeds.get(i)).getPosition().toCoord().plus(new Coord(chunkX, chunkZ)), InteractiveElmt.SEAWEED));
            this.largeSeaweeds.remove(i);
         }
      }

      for (int i = this.giantAlgaLights.size() - 1; i >= 0; i--) {
         if (CollisionDetector.segmentIntersects(localSegment, (this.giantAlgaLights.get(i)).getLocalBoundingBox(), (this.giantAlgaLights.get(i)).getPosition(), new Point())) {
            for (int j = 0; j < 3; j++) {
               float r = (float)Math.random();
               if (r < 0.3F) {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.giantAlgaLights.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.FERTILIZER));
               } else if (r < 0.6F) {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.giantAlgaLights.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.SEAWEED));
               } else {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.giantAlgaLights.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.ENERGY_SPHERE));
               }
            }

            Loading.worldManager.addInteractionUsed(new InteractionUsed((this.giantAlgaLights.get(i)).getPosition().toCoord().plus(new Coord(chunkX, chunkZ)), InteractiveElmt.ALGA_LIGHT));
            this.giantAlgaLights.remove(i);
         }
      }

      for (int i = this.jellyPlants.size() - 1; i >= 0; i--) {
         if (CollisionDetector.segmentIntersects(localSegment, (this.jellyPlants.get(i)).getLocalBoundingBox(), (this.jellyPlants.get(i)).getPosition(), new Point())) {
            for (int j = 0; j < 5; j++) {
               if (Math.random() < 0.5) {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.jellyPlants.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.FERTILIZER));
               } else {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.jellyPlants.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.SEAWEED));
               }
            }

            EnvironmentManager.addItemPickup(new ItemPickup((this.jellyPlants.get(i)).getPosition().plus(chunkX, 75.0F, chunkZ), ItemType.ENERGY_SPHERE));
            Loading.worldManager.addInteractionUsed(new InteractionUsed((this.jellyPlants.get(i)).getPosition().toCoord().plus(new Coord(chunkX, chunkZ)), InteractiveElmt.ALGA_LIGHT));
            this.jellyPlants.remove(i);
         }
      }

      for (int i = this.algas.size() - 1; i >= 0; i--) {
         if (CollisionDetector.segmentIntersects(localSegment, (this.algas.get(i)).getLocalBoundingBox(), (this.algas.get(i)).getPosition(), new Point())) {
            if (Math.random() < 0.5) {
               EnvironmentManager.addItemPickup(new ItemPickup((this.algas.get(i)).getPosition().plus(chunkX, 20.0F, chunkZ), ItemType.FERTILIZER));
            } else {
               EnvironmentManager.addItemPickup(new ItemPickup((this.algas.get(i)).getPosition().plus(chunkX, 20.0F, chunkZ), ItemType.SEAWEED));
            }

            Loading.worldManager.addInteractionUsed(new InteractionUsed((this.algas.get(i)).getPosition().toCoord().plus(new Coord(chunkX, chunkZ)), InteractiveElmt.ALGA));
            this.algas.remove(i);
         }
      }

      for (int i = this.abyssalAlgas.size() - 1; i >= 0; i--) {
         if (CollisionDetector.segmentIntersects(localSegment, (this.abyssalAlgas.get(i)).getLocalBoundingBox(), (this.abyssalAlgas.get(i)).getPosition(), new Point())) {
            for (int j = 0; j < 5; j++) {
               float r = (float)Math.random();
               if (r < 0.3F) {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.abyssalAlgas.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.FERTILIZER));
               } else if (r < 0.6F) {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.abyssalAlgas.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.SEAWEED));
               } else {
                  EnvironmentManager.addItemPickup(new ItemPickup((this.abyssalAlgas.get(i)).getPosition().plus(chunkX, j * 15.0F, chunkZ), ItemType.ENERGY_SPHERE));
               }
            }

            Loading.worldManager.addInteractionUsed(new InteractionUsed((this.abyssalAlgas.get(i)).getPosition().toCoord().plus(new Coord(chunkX, chunkZ)), InteractiveElmt.ALGA_LIGHT));
            this.abyssalAlgas.remove(i);
         }
      }
   }

   public ItemType harvestOre(int chunkX, int chunkZ, Segment segment, boolean consume) {
      ItemType result = null;
      Segment localSegment = segment.copy();
      localSegment.translate(new Point((float)(-chunkX), 0.0F, (float)(-chunkZ)));

      for (int i = this.oreDeposits.size() - 1; i >= 0; i--) {
         if (CollisionDetector.segmentIntersects(localSegment, (this.oreDeposits.get(i)).getLocalBoundingBox(), (this.oreDeposits.get(i)).getPosition(), new Point())) {
            result = (this.oreDeposits.get(i)).harvest(consume);
         }
      }

      return result;
   }

   public void harvestTerrainOverlay() {
      if (this.terrainOverlays.size() > 0) {
         (this.terrainOverlays.get(0)).harvest(true);
      }
   }

   public static void drawQuad(int x, int y, float width, float height) {
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(x - width / 2.0F, y - height / 2.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(x - width / 2.0F, y + height / 2.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(x + width / 2.0F, y + height / 2.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(x + width / 2.0F, y - height / 2.0F);
      GL11.glEnd();
   }

   public static void drawLabel(String text, float scale, int padding, int offset, float alpha) {
      drawLabel(text, scale, padding, 100, alpha, new Point(1.0F, 1.0F, 1.0F));
   }

   public static void drawLabel(String text, float scale, int padding, int offset, float alpha, Point color) {
      int textWidth = FontRenderer.getTextWidth(text, scale);
      int charHeight = FontRenderer.getCharHeight(scale) - 4;
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3i(offset, 0, 0);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3i(offset, charHeight, 0);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3i(textWidth + 2 * padding - offset, charHeight, 0);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3i(textWidth + 2 * padding - offset, 0, 0);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glBegin(GL11.GL_LINES);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3i(offset, 0, 0);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3i(textWidth + 2 * padding - offset, 0, 0);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3i(offset, charHeight, 0);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3i(textWidth + 2 * padding - offset, charHeight, 0);
      GL11.glEnd();
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3i(0, 0, 0);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3i(0, charHeight, 0);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3i(offset, charHeight, 0);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3i(offset, 0, 0);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3i(textWidth + 2 * padding, 0, 0);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3i(textWidth + 2 * padding, charHeight, 0);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3i(textWidth + 2 * padding - offset, charHeight, 0);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3i(textWidth + 2 * padding - offset, 0, 0);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glBegin(GL11.GL_LINES);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3i(0, 0, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3i(offset + 1, 0, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3i(0, charHeight, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3i(offset + 1, charHeight, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3i(textWidth + 2 * padding, 0, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3i(textWidth + 2 * padding - offset - 1, 0, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3i(textWidth + 2 * padding, charHeight, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3i(textWidth + 2 * padding - offset - 1, charHeight, 0);
      GL11.glEnd();
      GL11.glColor4f(color.x, color.y, color.z, alpha);
      FontRenderer.draw(padding, (int)(-8.0F * scale), text, scale);
   }

   public static float getLabelWidth(String text, float scale, int padding) {
      return FontRenderer.getTextWidth(text, scale) + 2 * padding;
   }

   public static World generate(World world, Coord origin, int zoneCount, Random rng) {
      chestCounts = new int[zoneCount];
      coralOverlayCounts = new int[zoneCount];
      grassOverlayCounts = new int[zoneCount];
      goldDepositCounts = new int[zoneCount];
      crystalDepositCounts = new int[zoneCount];
      silverDepositCounts = new int[zoneCount];
      shipwreckCounts = new int[zoneCount];
      submarinePartCounts = new int[zoneCount];
      abandonedBaseCounts = new int[zoneCount];
      droidCounts = new int[zoneCount];
      treasureChestCounts = new int[zoneCount];
      int remainingParts = SubmarinePiece.values().length;

      for (int z = 0; z < zoneCount; z++) {
         if (GameScene.gameMode != GameMode.ADVENTURE && GameScene.gameMode != GameMode.SURVIVOR) {
            chestCounts[z] = 11;
            coralOverlayCounts[z] = 11;
            grassOverlayCounts[z] = 11;
            goldDepositCounts[z] = 14;
            if (z > 0) {
               crystalDepositCounts[z] = 6;
            } else {
               crystalDepositCounts[z] = 0;
            }

            if (z > 0) {
               silverDepositCounts[z] = 6;
            } else {
               silverDepositCounts[z] = 0;
            }

            shipwreckCounts[z] = 10;
            submarinePartCounts[z] = 0;
            abandonedBaseCounts[z] = 2;
            droidCounts[z] = 2;
            treasureChestCounts[z] = 50;
         } else {
            chestCounts[z] = 3 + (z << 1);
            coralOverlayCounts[z] = 4 + (z << 1);
            grassOverlayCounts[z] = 4 + (z << 1);
            goldDepositCounts[z] = 4 + z * 3;
            if (z > 0) {
               crystalDepositCounts[z] = z << 1;
            } else {
               crystalDepositCounts[z] = 0;
            }

            if (z > 0) {
               silverDepositCounts[z] = z << 1;
            } else {
               silverDepositCounts[z] = 0;
            }

            shipwreckCounts[z] = 1 + (z << 1);
            if (z < zoneCount - 1) {
               submarinePartCounts[z] = SubmarinePiece.values().length / zoneCount;
               remainingParts -= submarinePartCounts[z];
            } else {
               submarinePartCounts[z] = remainingParts;
            }

            if (z > 0) {
               abandonedBaseCounts[z] = 1;
            } else {
               abandonedBaseCounts[z] = 0;
            }

            droidCounts[z] = 1;
            treasureChestCounts[z] = 15 + z * 15;
         }
      }

      setZoneInRadius(world, (int)origin.x, (int)origin.y, 2.0F, ZoneId.SHALLOW);
      ArrayList<IntCoord> workList = getCandidatePositions(world, 0);
      float minDist = 999999.0F;
      float maxDist = 0.0F;
      int closestIdx = 0;
      int farthestIdx = 0;

      for (int i = 0; i < workList.size(); i++) {
         float dist = new Coord((workList.get(i)).x, (workList.get(i)).y).distanceTo(origin);
         if (dist > 6.0F && dist < minDist) {
            closestIdx = i;
            minDist = dist;
         }

         if (dist > 20.0F || dist > maxDist) {
            farthestIdx = i;
            maxDist = dist;
         }
      }

      Coord baseCoord;
      if (GameScene.gameMode != GameMode.ADVENTURE && GameScene.gameMode != GameMode.SURVIVOR) {
         world.setGamePlayElmtAt((workList.get(closestIdx)).x, (workList.get(closestIdx)).y, new GamePlayElmt(GamePlayType.GOLD_DEPOSIT, 4 + (int)(rng.nextFloat() * 2.0F)));
         baseCoord = new Coord((workList.get(closestIdx)).x, (workList.get(closestIdx)).y);
      } else {
         world.setStartBasePosition(new Coord((workList.get(closestIdx)).x << 7, (workList.get(closestIdx)).y << 7));
         setZoneInRadius(world, (workList.get(closestIdx)).x, (workList.get(closestIdx)).y, 2.0F, ZoneId.SHALLOW);
         baseCoord = new Coord((workList.get(closestIdx)).x, (workList.get(closestIdx)).y);
      }

      Coord abandonedCoord = null;
      if (GameScene.gameMode == GameMode.ADVENTURE || GameScene.gameMode == GameMode.SURVIVOR) {
         abandonedCoord = new Coord((workList.get(farthestIdx)).x, (workList.get(farthestIdx)).y);
         world.addAbandonedBasePosition(new Coord(abandonedCoord.x * 128.0F, abandonedCoord.y * 128.0F));
         setZoneForStage(world, (workList.get(farthestIdx)).x, (workList.get(farthestIdx)).y, 3);
      }

      ArrayList<SubmarinePiece> submarinePieceList = new ArrayList<>();
      if (GameScene.gameMode == GameMode.ADVENTURE || GameScene.gameMode == GameMode.SURVIVOR) {
         for (SubmarinePiece piece : SubmarinePiece.values()) {
            submarinePieceList.add(piece);
         }
      }

      @SuppressWarnings("unchecked")
	  ArrayList<IntCoord>[] zoneCandidates = (ArrayList<IntCoord>[]) new ArrayList[zoneCount];

      for (int zi = 0; zi < zoneCount; zi++) {
         zoneCandidates[zi] = getCandidatePositions(world, zi);

         for (int ci = zoneCandidates[zi].size() - 1; ci >= 0; ci--) {
            if (zi == 0) {
               if ((zoneCandidates[zi].get(ci)).distanceTo(new IntCoord((int)origin.x, (int)origin.y)) < 3.0F) {
                  zoneCandidates[zi].remove(ci);
               }

               if ((zoneCandidates[zi].get(ci)).distanceTo(new IntCoord((int)baseCoord.x, (int)baseCoord.y)) < 3.0F) {
                  zoneCandidates[zi].remove(ci);
               }
            }

            if (abandonedCoord != null && (zoneCandidates[zi].get(ci)).distanceTo(new IntCoord((int)abandonedCoord.x, (int)abandonedCoord.y)) < 3.0F) {
               zoneCandidates[zi].remove(ci);
            }
         }

         ArrayList<IntCoord> placedPositions = new ArrayList<>();

         while (
            (
                  treasureChestCounts[zi] > 0
                     || droidCounts[zi] > 0
                     || abandonedBaseCounts[zi] > 0
                     || chestCounts[zi] > 0
                     || coralOverlayCounts[zi] > 0
                     || goldDepositCounts[zi] > 0
                     || crystalDepositCounts[zi] > 0
                     || shipwreckCounts[zi] > 0
                     || grassOverlayCounts[zi] > 0
                     || silverDepositCounts[zi] > 0
                     || submarinePartCounts[zi] > 0
               )
               && zoneCandidates[zi].size() > 0
         ) {
            int bestIdx = 0;
            float bestDist = 0.0F;

            for (int trial = 0; trial < 40; trial++) {
               int candidateIdx = (int)(zoneCandidates[zi].size() * rng.nextFloat());
               float minPlacedDist = 9999.0F;

               for (int pi = 0; pi < placedPositions.size(); pi++) {
                  float placedDist = (placedPositions.get(pi)).distanceTo(zoneCandidates[zi].get(candidateIdx));
                  if (placedDist < minPlacedDist) {
                     minPlacedDist = placedDist;
                  }
               }

               if (minPlacedDist > 4.0F) {
                  bestIdx = candidateIdx;
                  break;
               }

               if (minPlacedDist > bestDist) {
                  bestDist = minPlacedDist;
                  bestIdx = candidateIdx;
               }
            }

            IntCoord pos = zoneCandidates[zi].remove(bestIdx);
            if (submarinePartCounts[zi] > 0) {
               submarinePartCounts[zi]--;
               if (submarinePieceList.size() > 0) {
                  world.setGamePlayElmtAt(pos.x, pos.y, new GamePlayElmt(GamePlayType.SUBMARINE_PART, submarinePieceList.remove((int)(rng.nextFloat() * submarinePieceList.size()))));
               }
            } else if (abandonedBaseCounts[zi] > 0) {
               abandonedBaseCounts[zi]--;
               world.addAbandonedBasePosition(new Coord(pos.x << 7, pos.y << 7));
               setZoneForStage(world, pos.x, pos.y, 3);
            } else if (droidCounts[zi] > 0) {
               droidCounts[zi]--;
               world.addDroidPosition(new Coord(pos.x << 7, pos.y << 7));
            } else if (goldDepositCounts[zi] > 0) {
               goldDepositCounts[zi]--;
               world.setGamePlayElmtAt(pos.x, pos.y, new GamePlayElmt(GamePlayType.GOLD_DEPOSIT, 4 + (int)(rng.nextFloat() * 2.0F)));
            } else if (crystalDepositCounts[zi] > 0) {
               crystalDepositCounts[zi]--;
               Inventory crystalInv = new Inventory("Kraken", 1, 1);
               if (world.getStageAt(pos.x, pos.y) == 1) {
                  crystalInv.addItem(new Item(ItemType.CRYSTAL, 3));
               }

               if (world.getStageAt(pos.x, pos.y) == 2) {
                  crystalInv.addItem(new Item(ItemType.CRYSTAL, 4));
               }

               world.setGamePlayElmtAt(pos.x, pos.y, new GamePlayElmt(GamePlayType.CRYSTAL_DEPOSIT, 2 + (int)rng.nextFloat(), crystalInv));
            } else if (silverDepositCounts[zi] > 0) {
               silverDepositCounts[zi]--;
               world.setGamePlayElmtAt(pos.x, pos.y, new GamePlayElmt(GamePlayType.SILVER_DEPOSIT, 2 + (int)rng.nextFloat()));
            } else if (chestCounts[zi] > 0) {
               chestCounts[zi]--;
               world.setGamePlayElmtAt(pos.x, pos.y, new GamePlayElmt(GamePlayType.CHEST, createChestInventory(rng)));
            } else if (coralOverlayCounts[zi] > 0) {
               coralOverlayCounts[zi]--;
               world.setGamePlayElmtAt(pos.x, pos.y, new GamePlayElmt(GamePlayType.CORAL_OVERLAY, 250));
               setZoneForStage(world, pos.x, pos.y, 2);
            } else if (grassOverlayCounts[zi] > 0) {
               grassOverlayCounts[zi]--;
               world.setGamePlayElmtAt(pos.x, pos.y, new GamePlayElmt(GamePlayType.GRASS_OVERLAY, 250));
               setZoneForStage(world, pos.x, pos.y, 2);
            } else if (shipwreckCounts[zi] > 0) {
               shipwreckCounts[zi]--;
               world.setGamePlayElmtAt(pos.x, pos.y, new GamePlayElmt(GamePlayType.SHIPWRECK, 2 + (int)rng.nextFloat()));
               setZoneInRadius(world, pos.x, pos.y, 2.0F, null);
               if (rng.nextFloat() < 0.5F && isFlatEnough(world, pos.x + 1, pos.y, 1, 0.0F)) {
                  world.setGamePlayElmtAt(pos.x + 1, pos.y, new GamePlayElmt(GamePlayType.CHEST, createChestInventory(rng)));
               }

               if (rng.nextFloat() < 0.5F && isFlatEnough(world, pos.x - 1, pos.y, 1, 0.0F)) {
                  world.setGamePlayElmtAt(pos.x - 1, pos.y, new GamePlayElmt(GamePlayType.CHEST, createChestInventory(rng)));
               }

               if (rng.nextFloat() < 0.5F && isFlatEnough(world, pos.x + 1, pos.y - 1, 1, 0.0F)) {
                  world.setGamePlayElmtAt(pos.x + 1, pos.y - 1, new GamePlayElmt(GamePlayType.CHEST, createChestInventory(rng)));
               }

               if (rng.nextFloat() < 0.5F && isFlatEnough(world, pos.x + 1, pos.y + 1, 1, 0.0F)) {
                  world.setGamePlayElmtAt(pos.x + 1, pos.y + 1, new GamePlayElmt(GamePlayType.CHEST, createChestInventory(rng)));
               }

               if (rng.nextFloat() < 0.5F && isFlatEnough(world, pos.x - 1, pos.y - 1, 1, 0.0F)) {
                  world.setGamePlayElmtAt(pos.x - 1, pos.y - 1, new GamePlayElmt(GamePlayType.CHEST, createChestInventory(rng)));
               }

               if (rng.nextFloat() < 0.5F && isFlatEnough(world, pos.x - 1, pos.y + 1, 1, 0.0F)) {
                  world.setGamePlayElmtAt(pos.x - 1, pos.y + 1, new GamePlayElmt(GamePlayType.CHEST, createChestInventory(rng)));
               }
            } else if (treasureChestCounts[zi] > 0) {
               treasureChestCounts[zi]--;
               Inventory treasureInv = new Inventory("Money Chest", 1, 1);
               short baseGold = 80;
               if (world.getStageAt(pos.x, pos.y) == 1) {
                  baseGold = 120;
               }

               if (world.getStageAt(pos.x, pos.y) == 2) {
                  baseGold = 150;
               }

               int goldAmount = (int)(baseGold + Math.random() * 20.0);
               if (Math.random() < 0.2F) {
                  goldAmount <<= 1;
               }

               treasureInv.addItem(new Item(ItemType.GOLD, goldAmount));
               world.setGamePlayElmtAt(pos.x, pos.y, new GamePlayElmt(GamePlayType.TREASURE_CHEST, treasureInv));
            }

            placedPositions.add(pos);
         }
      }

      return world;
   }

   private static void setZoneForStage(World world, int x, int z, int radius) {
      if (world.getStageAt(x, z) == 1) {
         setZoneInRadius(world, x, z, radius, ZoneId.MIDWATER_C);
      } else if (world.getStageAt(x, z) == 2) {
         setZoneInRadius(world, x, z, radius, ZoneId.DEEP_ABYSS);
      } else {
         setZoneInRadius(world, x, z, radius, ZoneId.SHALLOW);
      }
   }

   private static Inventory createChestInventory(Random rng) {
      Inventory inv = new Inventory("Chest", 4, 2);
      if (rng.nextFloat() < 0.2F) {
         inv.addItem(new Item(ItemType.POTATO, (int)(1.0F + rng.nextFloat() * 2.0F)));
      }

      if (rng.nextFloat() < 0.2F) {
         inv.addItem(new Item(ItemType.CARROT_SEED, (int)(1.0F + rng.nextFloat() * 2.0F)));
      }

      if (rng.nextFloat() < 0.2F) {
         inv.addItem(new Item(ItemType.GREEN_BEAN, (int)(1.0F + rng.nextFloat() * 2.0F)));
      }

      if (rng.nextFloat() < 0.1F) {
         inv.addItem(new Item(ItemType.IRON_SPEAR, (int)(5.0F + rng.nextFloat() * 10.0F)));
      }

      if (rng.nextFloat() < 0.1F) {
         inv.addItem(new Item(ItemType.IRON_STUN_SPEAR, (int)(5.0F + rng.nextFloat() * 10.0F)));
      }

      if (rng.nextFloat() < 0.1F) {
         inv.addItem(new Item(ItemType.COPPER_SPEAR, (int)(5.0F + rng.nextFloat() * 10.0F)));
      }

      if (rng.nextFloat() < 0.1F) {
         inv.addItem(new Item(ItemType.COPPER_STUN_SPEAR, (int)(5.0F + rng.nextFloat() * 10.0F)));
      }

      if (rng.nextFloat() < 0.4F) {
         inv.addItem(new Item(ItemType.PLANT_POT, (int)(1.0F + rng.nextFloat() * 2.0F)));
      }

      if (rng.nextFloat() < 0.7F) {
         inv.addItem(new Item(ItemType.GLASS, (int)(5.0F + rng.nextFloat() * 10.0F)));
      }

      if (rng.nextFloat() < 0.5F) {
         inv.addItem(new Item(ItemType.GLASS_WALL, (int)(5.0F + rng.nextFloat() * 10.0F)));
      }

      if (rng.nextFloat() < 0.6F) {
         inv.addItem(new Item(ItemType.FLOOR, (int)(5.0F + rng.nextFloat() * 10.0F)));
      }

      if (rng.nextFloat() < 0.4F) {
         inv.addItem(new Item(ItemType.IRON, (int)(10.0F + rng.nextFloat() * 20.0F)));
      }

      if (rng.nextFloat() < 0.3F) {
         inv.addItem(new Item(ItemType.ENERGY_SPHERE, (int)(5.0F + rng.nextFloat() * 10.0F)));
      }

      if (rng.nextFloat() < 0.2F) {
         inv.addItem(new Item(ItemType.COPPER, (int)(3.0F + rng.nextFloat() * 5.0F)));
      }

      if (rng.nextFloat() < 0.1F) {
         inv.addItem(new Item(ItemType.GOLD, (int)(1.0F + rng.nextFloat() * 2.0F)));
      }

      if (rng.nextFloat() < 0.1F) {
         inv.addItem(new Item(ItemType.EXTRACTOR));
      }

      if (rng.nextFloat() < 0.1F) {
         inv.addItem(new Item(ItemType.MANGANESE, (int)(1.0F + rng.nextFloat() * 2.0F)));
      }

      if (inv.isEmpty()) {
         inv.addItem(new Item(ItemType.POTATO, (int)(1.0F + rng.nextFloat() * 2.0F)));
      }

      return inv;
   }

   private static ArrayList<IntCoord> getCandidatePositions(World world, int stage) {
      ArrayList<IntCoord> candidates = new ArrayList<>();

      for (int x = 1; x < world.getWidth() - 1; x++) {
         for (int z = 1; z < world.getHeight() - 1; z++) {
            if (allCellsMatchStage(world, x, z, 2, stage) && isFlatEnough(world, x, z, 2, 0.0F)) {
               candidates.add(new IntCoord(x, z));
            }
         }
      }

      return candidates;
   }

   private static World setZoneInRadius(World world, int cx, int cz, float radius, ZoneId zone) {
      int r = (int)Math.floor(radius);

      for (int wx = cx - r; wx <= cx + r; wx++) {
         for (int wz = cz - r; wz <= cz + r; wz++) {
            wx = clampCoord(wx, world.getWidth());
            wz = clampCoord(wz, world.getHeight());
            if (new Coord(cx, cz).distanceTo(new Coord(wx, wz)) <= radius && zone != null) {
               world.setZoneIdAt(wx, wz, zone);
            }
         }
      }

      return world;
   }

   private static boolean allCellsMatchStage(World world, int cx, int cz, int checkRadius, int stage) {
      for (int wx = cx - 2; wx <= cx + 2; wx++) {
         for (int wz = cz - 2; wz <= cz + 2; wz++) {
            int clampedX = clampCoord(wx, world.getWidth());
            int clampedZ = clampCoord(wz, world.getHeight());
            if (new Coord(cx, cz).distanceTo(new Coord(clampedX, clampedZ)) <= 2.0F && world.getStageAt(clampedX, clampedZ) != stage) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean isFlatEnough(World world, int cx, int cz, int radius, float tolerance) {
      float centerHeight = world.getHeightAt(cx, cz);

      for (int wx = cx - radius; wx <= cx + radius; wx++) {
         for (int wz = cz - radius; wz <= cz + radius; wz++) {
            wx = clampCoord(wx, world.getWidth());
            wz = clampCoord(wz, world.getHeight());
            if (new Coord(cx, cz).distanceTo(new Coord(wx, wz)) <= radius && Math.abs(world.getHeightAt(wx, wz) - centerHeight) > tolerance) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean isAreaFree(World world, int cx, int cz, int radius, int stage) {
      if (cx >= 3 && cx <= world.getWidth() - 3 && cz >= 3 && cz <= world.getHeight() - 3) {
         for (int wx = cx - 3; wx <= cx + 3; wx++) {
            for (int wz = cz - 3; wz <= cz + 3; wz++) {
               int clampedX = clampCoord(wx, world.getWidth());
               int clampedZ = clampCoord(wz, world.getHeight());
               if (new Coord(cx, cz).distanceTo(new Coord(clampedX, clampedZ)) <= 3.0F && world.getStageAt(clampedX, clampedZ) != 0) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static int clampCoord(int coord, int size) {
      if (coord < 0) {
         coord = 0;
      }

      if (coord >= size) {
         coord = size - 1;
      }

      return coord;
   }
}
