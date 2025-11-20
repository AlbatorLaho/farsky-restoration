package game.world;
import game.enemy.EnemyGenerator;
import game.environment.DepthAtmosphere;
import game.sounds.ChunkLayer;
import game.Main;
import game.chunks.Chunk;
import game.chunks.InteractiveElmt;
import game.chunks.chunkElements.AbyssElement;
import game.chunks.chunkElements.AbyssalAlga;
import game.chunks.chunkElements.Alga;
import game.chunks.chunkElements.Chest;
import game.chunks.chunkElements.Seaweed;
import game.chunks.chunkElements.TerrainOverlay;
import game.chunks.chunkElements.TreasureChest;
import game.chunks.chunkElements.GiantAlgaLight;
import game.chunks.chunkElements.JellyPlant;
import game.chunks.chunkElements.SeaGrass;
import game.chunks.chunkElements.DarkSeaGrass;
import game.chunks.chunkElements.GiantAlga;
import game.chunks.chunkElements.Vent;
import game.chunks.chunkElements.Coral;
import game.chunks.chunkElements.Rock;
import game.chunks.chunkElements.OreDeposit;
import game.chunks.chunkElements.Kelp;
import game.chunks.chunkElements.SeaBush;
import game.chunks.chunkElements.SeaSponge;
import game.chunks.chunkElements.LargeSeaweed;
import game.chunks.chunkElements.Shipwreck;
import game.chunks.chunkElements.SubmarinePart;
import game.util.Coord;
import game.util.Point;
import game.util.PerlinNoise;
import game.util.Vec4;
import game.util.SeededRandom;
import game.world.structure.GamePlayElmt;
import game.world.structure.InteractionUsed;
import game.world.structure.TerrainSample;
import game.world.structure.ZoneId;
import java.util.ArrayList;

public final class WorldManager implements Runnable {
   static enum WorldGeneration {
      GENERATE,
      LOAD,
      DEMO;
   }

   private boolean stopped = false;
   private boolean resetRequested = false;
   private boolean resetComplete = false;
   private World loadedWorld;
   private WorldHandler worldHandler;
   private WorldGeneration generationMode;
   private boolean ready = false;
   public WorldDataQueue incomingQueue = new WorldDataQueue();
   public WorldDataQueue outgoingQueue = new WorldDataQueue();
   private ArrayList<Chunk> pendingChunks = new ArrayList<>();
   private TerrainSample[][] rawSamples;
   private TerrainSample[][] finalSamples;
   private ChunkLayer chunkLayer;
   private Chunk.RockProperty rockProperty;
   private int chunkWorldX;
   private int chunkWorldZ;
   private float noiseValue;
   private static Vec4 ZONE_COLOR_SAND = new Vec4(1.0F, 0.0F, 0.0F, 0.0F);
   private static Vec4 ZONE_COLOR_ROCK = new Vec4(0.0F, 1.0F, 0.0F, 0.0F);
   private static Vec4 ZONE_COLOR_MID = new Vec4(0.0F, 0.0F, 1.0F, 0.0F);
   private static Vec4 ZONE_COLOR_DEEP = new Vec4(0.0F, 0.0F, 0.0F, 1.0F);
   public static float dayTime;
   public static float nightTime;
   public static EnemyGenerator.SpawningLevel spawning;
   private int chunkIdx = 0;

   public WorldManager(World world, boolean generateNew) {
      this.worldHandler = new WorldHandler();
      this.loadedWorld = world;
      if (world != null) {
         this.generationMode = WorldGeneration.LOAD;
      } else if (generateNew) {
         this.generationMode = WorldGeneration.GENERATE;
      } else {
         this.generationMode = WorldGeneration.DEMO;
      }
   }

   @Override
   public final void run() {
      if (Main.isVerbose) {
         System.out.println("WorldManager Thread -> Run");
      }

      this.stopped = false;
      switch (this.generationMode) {
         case DEMO:
            this.worldHandler.createTestWorld();
            break;
         case LOAD:
            this.worldHandler.loadWorld(this.loadedWorld);
            break;
         case GENERATE:
            this.worldHandler.generateWorld(dayTime, nightTime, spawning);
      }

      for (this.ready = true; !this.stopped; this.resetComplete = false) {
         this.pendingChunks = this.incomingQueue.drain();

         for (this.chunkIdx = 0; this.chunkIdx < this.pendingChunks.size(); this.chunkIdx++) {
            this.chunkWorldX = this.pendingChunks.get(this.chunkIdx).x;
            this.chunkWorldZ = this.pendingChunks.get(this.chunkIdx).z;
            this.rawSamples = new TerrainSample[Chunk.SIZE + 3][Chunk.SIZE + 3];

            for (int lx = -1; lx < Chunk.SIZE + 2; lx++) {
               for (int lz = -1; lz < Chunk.SIZE + 2; lz++) {
                  this.rawSamples[lx + 1][lz + 1] = this.sampleTerrain(this.toWorldX(lx), this.toWorldZ(lz));
               }
            }

            for (int lx = -1; lx < Chunk.SIZE + 2; lx++) {
               for (int lz = -1; lz < Chunk.SIZE + 2; lz++) {
                  this.rawSamples[lx + 1][lz + 1].accumulate(this.sampleZoneColor(this.toWorldX(lx), this.toWorldZ(lz)));
               }
            }

            for (int lx = -1; lx < Chunk.SIZE + 2; lx++) {
               for (int lz = -1; lz < Chunk.SIZE + 2; lz++) {
                  int si = lx + 1;
                  int sj = lz + 1;
                  if (this.getStageAt(this.toWorldX(lx), this.toWorldZ(lz)) == -1 && this.rawSamples[si][sj].color.dominantAxis() == 1) {
                     float blend = 1.0F - Math.abs(Math.abs(this.rawSamples[si][sj].height % 150.0F) - 75.0F) / 75.0F;
                     blend = Math.min(1.0F, blend * 1.5F);
                     this.rawSamples[si][sj].height = this.rawSamples[si][sj].height * (1.0F - blend) + ((int)(this.rawSamples[si][sj].height / 150.0F - 1.0F) * 150.0F + 75.0F) * blend;
                     if (blend > 0.2F) {
                        this.rawSamples[si][sj].color = ZONE_COLOR_SAND;
                     }
                  }
               }
            }

            this.finalSamples = new TerrainSample[Chunk.SIZE + 1][Chunk.SIZE + 1];

            for (int lx = 0; lx < Chunk.SIZE + 1; lx++) {
               for (int lz = 0; lz < Chunk.SIZE + 1; lz++) {
                  this.finalSamples[lx][lz] = new TerrainSample();
               }
            }

            for (int lx = 1; lx <= Chunk.SIZE + 1; lx++) {
               for (int lz = 1; lz <= Chunk.SIZE + 1; lz++) {
                  this.finalSamples[lx - 1][lz - 1].normal = a(
                     lx,
                     lz,
                     this.rawSamples[lx][lz].height,
                     this.rawSamples[lx + 1][lz].height,
                     this.rawSamples[lx][lz + 1].height,
                     this.rawSamples[lx - 1][lz].height,
                     this.rawSamples[lx][lz - 1].height
                  );
               }
            }

            float terrainStep = 1.0F * Chunk.TERRAIN_STEP;

            for (int lx = -1; lx < Chunk.SIZE + 2; lx++) {
               for (int lz = -1; lz < Chunk.SIZE + 2; lz++) {
                  float maxDiff = 0.0F;
                  int si = lx + 1;
                  int sj = lz + 1;
                  if (lx >= 0) {
                     maxDiff = Math.max(0.0F, Math.abs(this.rawSamples[si][sj].height - this.rawSamples[si - 1][sj].height));
                  }

                  if (lx < Chunk.SIZE + 1) {
                     maxDiff = Math.max(maxDiff, Math.abs(this.rawSamples[si][sj].height - this.rawSamples[si + 1][sj].height));
                  }

                  if (lz >= 0) {
                     maxDiff = Math.max(maxDiff, Math.abs(this.rawSamples[si][sj].height - this.rawSamples[si][sj - 1].height));
                  }

                  if (lz < Chunk.SIZE + 1) {
                     maxDiff = Math.max(maxDiff, Math.abs(this.rawSamples[si][sj].height - this.rawSamples[si][sj + 1].height));
                  }

                  if (lx >= 0 && lz >= 0) {
                     maxDiff = Math.max(maxDiff, Math.abs(this.rawSamples[si][sj].height - this.rawSamples[si - 1][sj - 1].height));
                  }

                  if (lx < Chunk.SIZE + 1 && lz >= 0) {
                     maxDiff = Math.max(maxDiff, Math.abs(this.rawSamples[si][sj].height - this.rawSamples[si + 1][sj - 1].height));
                  }

                  if (lx >= 0 && lz < Chunk.SIZE + 1) {
                     maxDiff = Math.max(maxDiff, Math.abs(this.rawSamples[si][sj].height - this.rawSamples[si - 1][sj + 1].height));
                  }

                  if (lx < Chunk.SIZE + 1 && lz < Chunk.SIZE + 1) {
                     maxDiff = Math.max(maxDiff, Math.abs(this.rawSamples[si][sj].height - this.rawSamples[si + 1][sj + 1].height));
                  }

                  if (maxDiff > terrainStep) {
                     float rockBlend = Math.min(1.0F, maxDiff - terrainStep);
                     this.rawSamples[si][sj].color = this.rawSamples[si][sj].color.scale(1.0F - rockBlend).add(ZONE_COLOR_ROCK.scale(rockBlend));
                  }
               }
            }

            TerrainSample[][] smoothedSamples = new TerrainSample[Chunk.SIZE - 1 + 2][Chunk.SIZE - 1 + 2];

            for (int lx = 0; lx < Chunk.SIZE + 1; lx++) {
               for (int lz = 0; lz < Chunk.SIZE + 1; lz++) {
                  smoothedSamples[lx][lz] = new TerrainSample();
               }
            }

            for (int lx = 0; lx < Chunk.SIZE + 1; lx++) {
               for (int lz = 0; lz < Chunk.SIZE + 1; lz++) {
                  float hSum = this.rawSamples[lx + 1][lz + 1].height;
                  Vec4 cSum = this.rawSamples[lx + 1][lz + 1].color;
                  hSum += this.rawSamples[lx][lz + 1].height;
                  cSum = cSum.add(this.rawSamples[lx][lz + 1].color);
                  hSum += this.rawSamples[lx][lz].height;
                  cSum = cSum.add(this.rawSamples[lx][lz].color);
                  hSum += this.rawSamples[lx + 1][lz].height;
                  cSum = cSum.add(this.rawSamples[lx + 1][lz].color);
                  hSum += this.rawSamples[lx + 2][lz].height;
                  cSum = cSum.add(this.rawSamples[lx + 2][lz].color);
                  hSum += this.rawSamples[lx + 2][lz + 1].height;
                  cSum = cSum.add(this.rawSamples[lx + 2][lz + 1].color);
                  hSum += this.rawSamples[lx + 2][lz + 2].height;
                  cSum = cSum.add(this.rawSamples[lx + 2][lz + 2].color);
                  hSum += this.rawSamples[lx + 1][lz + 2].height;
                  cSum = cSum.add(this.rawSamples[lx + 1][lz + 2].color);
                  hSum += this.rawSamples[lx][lz + 2].height;
                  cSum = cSum.add(this.rawSamples[lx][lz + 2].color);
                  smoothedSamples[lx - 1 + 1][lz - 1 + 1].height = hSum / 9.0F;
                  TerrainSample sample = smoothedSamples[lx - 1 + 1][lz - 1 + 1];
                  hSum = 9.0F;
                  sample.color = new Vec4(cSum.x / 9.0F, cSum.y / 9.0F, cSum.z / 9.0F, cSum.w / 9.0F);
               }
            }

            for (int lx = 0; lx < Chunk.SIZE + 1; lx++) {
               for (int lz = 0; lz < Chunk.SIZE + 1; lz++) {
                  this.rawSamples[lx + 1][lz + 1].height = smoothedSamples[lx - 1 + 1][lz - 1 + 1].height;
                  this.rawSamples[lx + 1][lz + 1].color = smoothedSamples[lx - 1 + 1][lz - 1 + 1].color;
               }
            }

            for (int lx = 0; lx <= Chunk.SIZE; lx++) {
               for (int lz = 0; lz <= Chunk.SIZE; lz++) {
                  this.finalSamples[lx][lz].height = this.rawSamples[lx + 1][lz + 1].height;
                  this.finalSamples[lx][lz].color = this.rawSamples[lx + 1][lz + 1].color;
                  this.finalSamples[lx][lz].zoneId = this.rawSamples[lx + 1][lz + 1].zoneId;
               }
            }

            this.buildChunkLayer();
            this.pendingChunks.get(this.chunkIdx).init(this.finalSamples, this.chunkLayer, this.rockProperty, this.getResourceCountAt(this.chunkWorldX, this.chunkWorldZ));
         }

         this.outgoingQueue.enqueue(this.pendingChunks);
         this.pendingChunks.clear();

         do {
            try {
               if (this.resetRequested) {
                  this.resetQueues();
                  this.resetComplete = true;
               }

               Thread.sleep(500L);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         } while (this.resetRequested);
      }

      this.resetQueues();
   }

   public final boolean isReady() {
      return this.ready;
   }

   public final Coord getAvatarStartPosition() {
      return this.worldHandler.getAvatarStartPosition();
   }

   public final Coord getStartBasePosition() {
      return this.worldHandler.getStartBasePosition();
   }

   public final ArrayList<Coord> getAbandonedBasePositions() {
      return this.worldHandler.getAbandonedBasePositions();
   }

   public final ArrayList<Coord> getDroidPositions() {
      return this.worldHandler.getDroidPositions();
   }

   public final int getWorldWidth() {
      return this.worldHandler.getWorld().getWidth();
   }

   public final int getWorldHeight() {
      return this.worldHandler.getWorld().getHeight();
   }

   public final boolean isVisibleAt(int x, int z) {
      return this.worldHandler.getWorld().isVisibleAt(x, z);
   }

   public final World getWorld() {
      return this.worldHandler.getWorld();
   }

   public final float getDayTime() {
      return this.worldHandler.getDayTime();
   }

   public final float getNightTime() {
      return this.worldHandler.getNightTime();
   }

   public final EnemyGenerator.SpawningLevel getSpawning() {
      return this.worldHandler.getSpawning();
   }

   private void buildChunkLayer() {
      this.chunkLayer = new ChunkLayer();
      GamePlayElmt elmt = this.getGamePlayElmtAt(this.chunkWorldX / 128, this.chunkWorldZ / 128);
      Point center = new Point(64.0F, this.finalSamples[Chunk.SIZE / 2][Chunk.SIZE / 2].height, 64.0F);

      for (int lx = 0; lx < 128; lx++) {
         for (int lz = 0; lz < 128; lz++) {
            Coord worldCoord = new Coord(this.chunkWorldX + lx, this.chunkWorldZ + lz);
            int tx = lx / Chunk.TERRAIN_STEP;
            int tz = lz / Chunk.TERRAIN_STEP;
            switch (this.finalSamples[tx][tz].zoneId) {
               case KELP_FOREST:
                  if (this.finalSamples[tx][tz].color.dominantAxis() != 1) {
                     if (Math.random() < 5.0E-4F) {
                        this.chunkLayer.addElement(new Kelp(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     if (Math.random() < 5.0E-4F) {
                        this.chunkLayer.addElement(new SeaBush(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     if (Math.random() < 3.0E-4F) {
                        this.chunkLayer.addElement(new Rock(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     PerlinNoise.seed = this.worldHandler.getRandLandscapeAlga();
                     if (PerlinNoise.noise((lx + this.chunkWorldX) / 700.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 700.0F * Chunk.TERRAIN_STEP, 1) > 0.0F) {
                        SeededRandom.seed = this.worldHandler.getRandLandscapeGiantAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9998F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.SEAWEED))) {
                           this.chunkLayer.addElement(new GiantAlga(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz)));
                        }
                     } else {
                        SeededRandom.seed = this.worldHandler.getRandLandscapeGiantAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9998F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.SEAWEED))) {
                           this.chunkLayer.addElement(new LargeSeaweed(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz), true, false));
                        }
                     }
                  }
                  break;
               case REEF:
                  if (this.finalSamples[tx][tz].color.dominantAxis() != 1) {
                     if (Math.random() < 5.0E-4F) {
                        this.chunkLayer.addElement(new Kelp(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     if (Math.random() < 5.0E-4F) {
                        this.chunkLayer.addElement(new SeaBush(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     if (Math.random() < 3.0E-4F) {
                        this.chunkLayer.addElement(new Rock(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     PerlinNoise.seed = this.worldHandler.getRandLandscapeAlga();
                     float noiseReef = PerlinNoise.noise((lx + this.chunkWorldX) / 100.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 100.0F * Chunk.TERRAIN_STEP, 1);
                     if (noiseReef > 0.45F) {
                        float reefWeight = Math.min(noiseReef * 5.0F, 1.0F);
                        SeededRandom.seed = this.worldHandler.getRandLandscapeAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) * reefWeight > 0.99F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.ALGA))) {
                           this.chunkLayer.addElement(new Alga(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                        }
                     }

                     SeededRandom.seed = this.worldHandler.getRandLandscapeGiantAlga();
                     if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.99998F
                        && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.SEAWEED))) {
                        this.chunkLayer.addElement(new GiantAlga(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz)));
                     }
                  } else {
                     if (Math.random() < 5.0E-4F) {
                        int spongeCount = (int)(4.0 + Math.random() * 6.0);

                        for (int s = 0; s < spongeCount; s++) {
                           this.chunkLayer.addElement(new SeaSponge(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                        }
                     }

                     if (Math.random() < 0.005F) {
                        ChunkLayer layer = this.chunkLayer;
                        Point pos = new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz);
                        layer.addElement(new Coral(pos, this.finalSamples[tx][tz].normal));
                     }

                     if (Math.random() < 0.005F) {
                        this.chunkLayer
                           .addElement(
                              new Seaweed(
                                 new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz),
                                 new Point((float)(lx + this.chunkWorldX), this.finalSamples[tx][tz].height, (float)(lz + this.chunkWorldZ)),
                                 this.finalSamples[tx][tz].normal
                              )
                           );
                     }
                  }
                  break;
               case MIDWATER_A:
                  if (this.finalSamples[tx][tz].color.dominantAxis() != 1) {
                     if (this.finalSamples[tx][tz].color.isAxisAligned() && Math.random() < 0.03F) {
                        this.chunkLayer.addElement(new SeaGrass(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     PerlinNoise.seed = this.worldHandler.getRandLandscapeAlga();
                     float noiseMwa = PerlinNoise.noise((lx + this.chunkWorldX) / 100.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 100.0F * Chunk.TERRAIN_STEP, 1);
                     if (noiseMwa > 0.25F) {
                        float mwaWeight = Math.min(noiseMwa * 5.0F, 1.0F);
                        SeededRandom.seed = this.worldHandler.getRandLandscapeAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) * mwaWeight > 0.99F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.ALGA))) {
                           this.chunkLayer.addElement(new Alga(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                        }
                     }

                     PerlinNoise.seed = this.worldHandler.getRandLevelGenCliff();
                     if (PerlinNoise.noise((lx + this.chunkWorldX) / 700.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 700.0F * Chunk.TERRAIN_STEP, 1) > -0.15F) {
                        SeededRandom.seed = this.worldHandler.getRandLandscapeGiantAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9998F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.ALGA_LIGHT))) {
                           this.chunkLayer.addElement(new GiantAlgaLight(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz)));
                        }

                        SeededRandom.seed = this.worldHandler.getRandGenRock();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9999F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.SEAWEED))) {
                           this.chunkLayer.addElement(new LargeSeaweed(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz), false, false));
                        }
                     }
                  }
                  break;
               case MIDWATER_C:
                  if (this.finalSamples[tx][tz].color.dominantAxis() != 1) {
                     if (this.finalSamples[tx][tz].color.isAxisAligned() && Math.random() < 0.03F) {
                        this.chunkLayer.addElement(new SeaGrass(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     PerlinNoise.seed = this.worldHandler.getRandLandscapeAlga();
                     float noiseMwc = PerlinNoise.noise((lx + this.chunkWorldX) / 100.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 100.0F * Chunk.TERRAIN_STEP, 1);
                     if (noiseMwc > 0.25F) {
                        float mwcWeight = Math.min(noiseMwc * 5.0F, 1.0F);
                        SeededRandom.seed = this.worldHandler.getRandLandscapeAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) * mwcWeight > 0.99F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.ALGA))) {
                           this.chunkLayer.addElement(new Alga(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                        }
                     }
                  }
                  break;
               case MIDWATER_B:
                  if (this.finalSamples[tx][tz].color.dominantAxis() != 1) {
                     if (this.finalSamples[tx][tz].color.isAxisAligned() && Math.random() < 0.03F) {
                        this.chunkLayer.addElement(new SeaGrass(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     PerlinNoise.seed = this.worldHandler.getRandLevelGenCliff();
                     if (PerlinNoise.noise((lx + this.chunkWorldX) / 700.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 700.0F * Chunk.TERRAIN_STEP, 1) > 0.0F) {
                        SeededRandom.seed = this.worldHandler.getRandLandscapeGiantAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9997F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.JELLY_PLANT))) {
                           this.chunkLayer.addElement(new JellyPlant(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz)));
                        }

                        SeededRandom.seed = this.worldHandler.getRandLandscapeAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9999F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.SEAWEED))) {
                           this.chunkLayer.addElement(new LargeSeaweed(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz), false, false));
                        }
                     } else {
                        SeededRandom.seed = this.worldHandler.getRandGenRock();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9998F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.SEAWEED))) {
                           this.chunkLayer.addElement(new LargeSeaweed(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz), false, false));
                        }
                     }
                  }
                  break;
               case SHALLOW:
               case OPEN_OCEAN:
                  if (Math.random() < 5.0E-4F) {
                     this.chunkLayer.addElement(new Kelp(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                  }

                  if (Math.random() < 5.0E-4F) {
                     this.chunkLayer.addElement(new SeaBush(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                  }

                  if (Math.random() < 3.0E-4F) {
                     this.chunkLayer.addElement(new Rock(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                  }

                  if (this.finalSamples[tx][tz].color.dominantAxis() != 1) {
                     PerlinNoise.seed = this.worldHandler.getRandLandscapeAlga();
                     float noiseShallow = PerlinNoise.noise((lx + this.chunkWorldX) / 100.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 100.0F * Chunk.TERRAIN_STEP, 1);
                     if (noiseShallow > 0.45F) {
                        float shallowWeight = Math.min(noiseShallow * 5.0F, 1.0F);
                        SeededRandom.seed = this.worldHandler.getRandLandscapeAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) * shallowWeight > 0.99F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.ALGA))) {
                           this.chunkLayer.addElement(new Alga(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                        }
                     }
                  }
                  break;
               case ABYSS:
                  if (this.finalSamples[tx][tz].color.dominantAxis() == 3) {
                     if (Math.random() < 4.0E-5F) {
                        this.chunkLayer.addElement(new Vent(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz), this.chunkWorldX, this.chunkWorldZ));
                     }

                     if (Math.random() < 8.0E-4F) {
                        this.chunkLayer.addElement(new Rock(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     PerlinNoise.seed = this.worldHandler.getRandLandscapeAlga();
                     float noiseAbyss = PerlinNoise.noise((lx + this.chunkWorldX) / 100.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 100.0F * Chunk.TERRAIN_STEP, 1);
                     if (noiseAbyss > 0.4F) {
                        float abyssWeight = Math.min(noiseAbyss * 5.0F, 1.0F);
                        SeededRandom.seed = this.worldHandler.getRandLandscapeAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) * abyssWeight > 0.99F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.ALGA))) {
                           this.chunkLayer.addElement(new Alga(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz), Alga.AlgaType.GLOWING));
                        }
                     }

                     if (this.finalSamples[tx][tz].color.isAxisAligned() && Math.random() < 0.03F) {
                        this.chunkLayer.addElement(new DarkSeaGrass(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     PerlinNoise.seed = this.worldHandler.getRandLevelGenCliff();
                     if (PerlinNoise.noise((lx + this.chunkWorldX) / 700.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 700.0F * Chunk.TERRAIN_STEP, 1) > 0.0F) {
                        SeededRandom.seed = this.worldHandler.getRandLandscapeGiantAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9998F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.SEAWEED))) {
                           this.chunkLayer.addElement(new AbyssalAlga(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz)));
                        }

                        SeededRandom.seed = this.worldHandler.getRandGenDunes();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9999F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.SEAWEED))) {
                           this.chunkLayer.addElement(new LargeSeaweed(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz), false, true));
                        }
                     } else {
                        SeededRandom.seed = this.worldHandler.getRandGenDunes();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) > 0.9998F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.SEAWEED))) {
                           this.chunkLayer.addElement(new LargeSeaweed(new Point(lx, this.finalSamples[tx][tz].height + Math.random(), lz), false, true));
                        }
                     }
                  }
               case DEEP_ABYSS:
                  if (this.finalSamples[tx][tz].color.dominantAxis() == 3) {
                     if (Math.random() < 4.0E-5F) {
                        this.chunkLayer.addElement(new Vent(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz), this.chunkWorldX, this.chunkWorldZ));
                     }

                     if (Math.random() < 8.0E-4F) {
                        this.chunkLayer.addElement(new Rock(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }

                     PerlinNoise.seed = this.worldHandler.getRandLandscapeAlga();
                     float noiseDeep = PerlinNoise.noise((lx + this.chunkWorldX) / 100.0F * Chunk.TERRAIN_STEP, (lz + this.chunkWorldZ) / 100.0F * Chunk.TERRAIN_STEP, 1);
                     if (noiseDeep > 0.4F) {
                        float deepWeight = Math.min(noiseDeep * 5.0F, 1.0F);
                        SeededRandom.seed = this.worldHandler.getRandLandscapeAlga();
                        if (SeededRandom.nextFloat((float)(lx + this.chunkWorldX) * Chunk.TERRAIN_STEP, (float)(lz + this.chunkWorldZ) * Chunk.TERRAIN_STEP) * deepWeight > 0.99F
                           && !this.worldHandler.hasInteractionUsed(new InteractionUsed(worldCoord, InteractiveElmt.ALGA))) {
                           this.chunkLayer.addElement(new Alga(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz), Alga.AlgaType.GLOWING));
                        }
                     }

                     if (this.finalSamples[tx][tz].color.isAxisAligned() && Math.random() < 0.03F) {
                        this.chunkLayer.addElement(new DarkSeaGrass(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz)));
                     }
                  }

                  if (this.finalSamples[tx][tz].color.dominantAxis() == 0 && Math.random() < 0.01F) {
                     this.chunkLayer.addElement(new AbyssElement(new Point((float)lx, this.finalSamples[tx][tz].height, (float)lz), this.finalSamples[tx][tz].normal));
                  }
            }
         }
      }

      switch (elmt.getType()) {
         case NONE:
         default:
            break;
         case CHEST:
            this.chunkLayer.addElement(new Chest(elmt.getInventory(), center, center.plus(this.chunkWorldX, 0.0F, this.chunkWorldZ), this.finalSamples[Chunk.SIZE / 2][Chunk.SIZE / 2].normal, this.chunkWorldX / 128, this.chunkWorldZ / 128));
            break;
         case CORAL_OVERLAY:
         case GRASS_OVERLAY:
            this.chunkLayer.addElement(new TerrainOverlay(elmt.getType(), elmt.getCount()));
            break;
         case GOLD_DEPOSIT:
         case CRYSTAL_DEPOSIT:
         case SILVER_DEPOSIT:
            this.chunkLayer.addElement(new OreDeposit(center, center.plus(this.chunkWorldX, 0.0F, this.chunkWorldZ), elmt.getType(), elmt.getCount(), this.chunkWorldX / 128, this.chunkWorldZ / 128, elmt.getInventory()));
            break;
         case SHIPWRECK:
            this.chunkLayer.addElement(new Shipwreck(center));
            break;
         case SUBMARINE_PART:
            this.chunkLayer.addElement(new SubmarinePart(elmt.getSubmarinePiece(), center, center.plus(this.chunkWorldX, 0.0F, this.chunkWorldZ), this.finalSamples[Chunk.SIZE / 2][Chunk.SIZE / 2].normal, this.chunkWorldX / 128, this.chunkWorldZ / 128));
            break;
         case TREASURE_CHEST:
            this.chunkLayer
               .addElement(new TreasureChest(elmt.getInventory(), center, center.plus(this.chunkWorldX, 0.0F, this.chunkWorldZ), this.finalSamples[Chunk.SIZE / 2][Chunk.SIZE / 2].normal, this.chunkWorldX / 128, this.chunkWorldZ / 128));
      }

      this.rockProperty = Chunk.RockProperty.PLAIN;
      if (this.finalSamples[Chunk.SIZE / 2][Chunk.SIZE / 2].zoneId != ZoneId.OPEN_OCEAN) {
         if (DepthAtmosphere.getZone(this.finalSamples[Chunk.SIZE / 2][Chunk.SIZE / 2].height) == 0) {
            SeededRandom.seed = this.worldHandler.getRandLandscapeGiantAlga();
            if (SeededRandom.nextFloat(this.chunkWorldX / 30.0F, this.chunkWorldZ / 30.0F) > 0.4F) {
               this.rockProperty = Chunk.RockProperty.SHALLOW;
               return;
            }
         } else if (DepthAtmosphere.getZone(this.finalSamples[Chunk.SIZE / 2][Chunk.SIZE / 2].height) == 1) {
            SeededRandom.seed = this.worldHandler.getRandLandscapeAlga();
            if (SeededRandom.nextFloat(this.chunkWorldX / 30.0F, this.chunkWorldZ / 30.0F) > 0.4F) {
               this.rockProperty = Chunk.RockProperty.MIDWATER;
               return;
            }
         } else if (DepthAtmosphere.getZone(this.finalSamples[Chunk.SIZE / 2][Chunk.SIZE / 2].height) == 2) {
            SeededRandom.seed = this.worldHandler.getRandGenSource();
            if (SeededRandom.nextFloat(this.chunkWorldX / 30.0F, this.chunkWorldZ / 30.0F) > 0.4F) {
               this.rockProperty = Chunk.RockProperty.DEEP;
            }
         }
      }
   }

   public final float getTerrainHeightAt(float worldX, float worldZ) {
      TerrainSample sample = this.sampleTerrain(worldX, worldZ);
      sample.accumulate(this.sampleZoneColor(worldX, worldZ));
      return sample.height;
   }

   public final Point getTerrainNormalAt(float worldX, float worldZ) {
      int ix = (int)worldX;
      int iz = (int)worldZ;
      return a(
         ix,
         iz,
         this.getTerrainHeightAt((float)ix, (float)iz),
         this.getTerrainHeightAt((float)(ix + 1), (float)iz),
         this.getTerrainHeightAt((float)ix, (float)(iz + 1)),
         this.getTerrainHeightAt((float)(ix - 1), (float)iz),
         this.getTerrainHeightAt((float)ix, (float)(iz - 1))
      );
   }

   public final int getStageAt(float worldX, float worldZ) {
      return this.worldHandler.getWorld().getStageAt((int)worldX / 128, (int)worldZ / 128);
   }

   public final int getResourceCountAt(float worldX, float worldZ) {
      return this.worldHandler.getWorld().getResourceCountAt((int)worldX / 128, (int)worldZ / 128);
   }

   public final GamePlayElmt getGamePlayElmtAt(float worldX, float worldZ) {
      return this.worldHandler.getWorld().getGamePlayElmtAt((int)worldX / 128, (int)worldZ / 128);
   }

   public final GamePlayElmt getGamePlayElmtAt(int x, int z) {
      return this.worldHandler.getWorld().getGamePlayElmtAt(x, z);
   }

   public final void setGamePlayElmtAt(GamePlayElmt elmt, int x, int z) {
      this.worldHandler.setGamePlayElmtAt(elmt, x, z);
   }

   public final void setGamePlayElmtAt(GamePlayElmt elmt, float worldX, float worldZ) {
      this.worldHandler.setGamePlayElmtAt(elmt, (int)worldX / 128, (int)worldZ / 128);
   }

   public final void decrementResourceAt(float worldX, float worldZ) {
      this.worldHandler.decrementResourceAt((int)worldX / 128, (int)worldZ / 128);
   }

   public final void addInteractionUsed(InteractionUsed interaction) {
      this.worldHandler.addInteractionUsed(interaction);
   }

   public final void setVisibleAt(int x, int z, boolean visible) {
      this.worldHandler.setVisibleAt(x, z, true);
   }

   private synchronized TerrainSample sampleTerrain(float x, float z) {
      TerrainSample sample = new TerrainSample();
      sample.height = this.worldHandler.interpolateHeight(x, z);
      return sample;
   }

   private synchronized TerrainSample sampleZoneColor(float x, float z) {
      TerrainSample result = new TerrainSample();
      float dominantBlend = 0.0F;
      float activeZones = 0.0F;
      float blendOcean = this.worldHandler.interpolateZoneBlend(x, z, ZoneId.OPEN_OCEAN);
      float blendReef = this.worldHandler.interpolateZoneBlend(x, z, ZoneId.REEF);
      float blendKelp = this.worldHandler.interpolateZoneBlend(x, z, ZoneId.KELP_FOREST);
      float blendMidA = this.worldHandler.interpolateZoneBlend(x, z, ZoneId.MIDWATER_A);
      float blendShallow = this.worldHandler.interpolateZoneBlend(x, z, ZoneId.SHALLOW);
      float blendMidB = this.worldHandler.interpolateZoneBlend(x, z, ZoneId.MIDWATER_B);
      float blendAbyss = this.worldHandler.interpolateZoneBlend(x, z, ZoneId.ABYSS);
      float blendMidC = this.worldHandler.interpolateZoneBlend(x, z, ZoneId.MIDWATER_C);
      float blendDeep = this.worldHandler.interpolateZoneBlend(x, z, ZoneId.DEEP_ABYSS);
      if (blendOcean > 0.0F) {
         activeZones = 1.0F;
      }

      if (blendReef > 0.0F) {
         activeZones++;
      }

      if (blendKelp > 0.0F) {
         activeZones++;
      }

      if (blendMidA > 0.0F) {
         activeZones++;
      }

      if (blendShallow > 0.0F) {
         activeZones++;
      }

      if (blendMidB > 0.0F) {
         activeZones++;
      }

      if (blendAbyss > 0.0F) {
         activeZones++;
      }

      if (blendMidC > 0.0F) {
         activeZones++;
      }

      if (blendDeep > 0.0F) {
         activeZones++;
      }

      if (blendOcean > 0.0F) {
         TerrainSample oceanSample = new TerrainSample();
         PerlinNoise.seed = this.worldHandler.getRandGenRock();
         this.noiseValue = (PerlinNoise.noise(x / 120.0F, z / 120.0F, 4) - 0.2F) * 450.0F;
         if (this.noiseValue > 0.0F) {
            if (this.noiseValue < 225.0F) {
               oceanSample.height = this.noiseValue;
            } else {
               oceanSample.height = 225.0F + (this.noiseValue - 225.0F) / 20.0F;
            }
         } else {
            oceanSample.height = this.noiseValue / 5.0F;
         }

         oceanSample.color = ZONE_COLOR_ROCK;
         result.height = result.height + oceanSample.height * blendOcean;
         result.color = result.color.add(oceanSample.color.scale(clampZoneWeight(blendOcean, 1.0F / activeZones)));
         if (blendOcean > 0.0F) {
            result.zoneId = ZoneId.OPEN_OCEAN;
            dominantBlend = blendOcean;
         }
      }

      if (blendReef > 0.0F) {
         TerrainSample reefSample = new TerrainSample();
         PerlinNoise.seed = this.worldHandler.getRandGenDunes();
         reefSample.height = PerlinNoise.noise(x / 300.0F, z / 300.0F, 4) * 100.0F;
         reefSample.color = ZONE_COLOR_SAND;
         PerlinNoise.seed = this.worldHandler.getRandGenRock();
         this.noiseValue = (PerlinNoise.noise(x / 60.0F, z / 60.0F, 3) + 0.0F) * 200.0F;
         if (this.noiseValue > 0.0F) {
            if (this.noiseValue < 40.0F) {
               reefSample.height = reefSample.height + this.noiseValue;
            } else {
               reefSample.height = reefSample.height + (40.0F + (this.noiseValue - 40.0F) / 20.0F);
            }

            reefSample.color = ZONE_COLOR_ROCK;
         }

         result.height = result.height + reefSample.height * blendReef;
         result.color = result.color.add(reefSample.color.scale(clampZoneWeight(blendReef, 1.0F / activeZones)));
         if (blendReef > dominantBlend) {
            result.zoneId = ZoneId.REEF;
            dominantBlend = blendReef;
         }
      }

      if (blendKelp > 0.0F) {
         TerrainSample kelpSample = new TerrainSample();
         PerlinNoise.seed = this.worldHandler.getRandGenDunes();
         kelpSample.height = PerlinNoise.noise(x / 300.0F, z / 300.0F, 4) * 100.0F;
         kelpSample.color = ZONE_COLOR_SAND;
         PerlinNoise.seed = this.worldHandler.getRandGenRock();
         this.noiseValue = (PerlinNoise.noise(x / 120.0F, z / 120.0F, 4) - 0.3F) * 450.0F;
         if (this.noiseValue > 0.0F) {
            if (this.noiseValue < 225.0F) {
               kelpSample.height = kelpSample.height + this.noiseValue;
            } else {
               kelpSample.height = kelpSample.height + (225.0F + (this.noiseValue - 225.0F) / 20.0F);
            }

            kelpSample.color = ZONE_COLOR_ROCK;
         }

         result.height = result.height + kelpSample.height * blendKelp;
         result.color = result.color.add(kelpSample.color.scale(clampZoneWeight(blendKelp, 1.0F / activeZones)));
         if (blendKelp > dominantBlend) {
            result.zoneId = ZoneId.KELP_FOREST;
            dominantBlend = blendKelp;
         }
      }

      if (blendShallow > 0.0F) {
         TerrainSample shallowSample = new TerrainSample();
         PerlinNoise.seed = this.worldHandler.getRandGenDunes();
         shallowSample.height = PerlinNoise.noise(x / 300.0F, z / 300.0F, 4) * 100.0F;
         shallowSample.color = ZONE_COLOR_SAND;
         result.height = result.height + shallowSample.height * blendShallow;
         result.color = result.color.add(shallowSample.color.scale(clampZoneWeight(blendShallow, 1.0F / activeZones)));
         if (blendShallow > dominantBlend) {
            result.zoneId = ZoneId.SHALLOW;
            dominantBlend = blendShallow;
         }
      }

      if (blendMidA > 0.0F) {
         TerrainSample midASample = new TerrainSample();
         PerlinNoise.seed = this.worldHandler.getRandGenDunes();
         midASample.height = PerlinNoise.noise(x / 200.0F, z / 200.0F, 1) * 250.0F;
         midASample.color = ZONE_COLOR_MID;
         PerlinNoise.seed = this.worldHandler.getRandGenRock();
         this.noiseValue = (PerlinNoise.noise(x / 120.0F, z / 120.0F, 4) - 0.4F) * 450.0F;
         if (this.noiseValue > 0.0F) {
            if (this.noiseValue < 225.0F) {
               midASample.height = midASample.height + this.noiseValue;
            } else {
               midASample.height = midASample.height + (225.0F + (this.noiseValue - 225.0F) / 20.0F);
            }

            midASample.color = ZONE_COLOR_ROCK;
         }

         result.height = result.height + midASample.height * blendMidA;
         result.color = result.color.add(midASample.color.scale(clampZoneWeight(blendMidA, 1.0F / activeZones)));
         if (blendMidA > dominantBlend) {
            result.zoneId = ZoneId.MIDWATER_A;
            dominantBlend = blendMidA;
         }
      }

      if (blendMidB > 0.0F) {
         TerrainSample midBSample = new TerrainSample();
         PerlinNoise.seed = this.worldHandler.getRandGenDunes();
         midBSample.height = PerlinNoise.noise(x / 130.0F, z / 130.0F, 1) * 250.0F;
         midBSample.color = ZONE_COLOR_MID;
         PerlinNoise.seed = this.worldHandler.getRandGenRock();
         this.noiseValue = (PerlinNoise.noise(x / 120.0F, z / 120.0F, 4) - 0.4F) * 450.0F;
         if (this.noiseValue > 0.0F) {
            if (this.noiseValue < 225.0F) {
               midBSample.height = midBSample.height + this.noiseValue;
            } else {
               midBSample.height = midBSample.height + (225.0F + (this.noiseValue - 225.0F) / 20.0F);
            }

            midBSample.color = ZONE_COLOR_ROCK;
         }

         result.height = result.height + midBSample.height * blendMidB;
         result.color = result.color.add(midBSample.color.scale(clampZoneWeight(blendMidB, 1.0F / activeZones)));
         if (blendMidB > dominantBlend) {
            result.zoneId = ZoneId.MIDWATER_B;
            dominantBlend = blendMidB;
         }
      }

      if (blendAbyss > 0.0F) {
         TerrainSample abyssSample = new TerrainSample();
         PerlinNoise.seed = this.worldHandler.getRandGenRock();
         this.noiseValue = PerlinNoise.noise(x / 130.0F, z / 130.0F, 2);
         abyssSample.height = this.noiseValue * 100.0F;
         abyssSample.color = ZONE_COLOR_DEEP;
         if (Math.abs(this.noiseValue) < 0.1F) {
            abyssSample.height = abyssSample.height - 100.0F * Math.min(Math.max((1.0F - Math.abs(this.noiseValue) / 0.1F) * 4.0F, 0.0F), 1.0F) / 2.0F;
            abyssSample.color = ZONE_COLOR_SAND;
         }

         PerlinNoise.seed = this.worldHandler.getRandGenDunes();
         this.noiseValue = PerlinNoise.noise(x / 30.0F, z / 30.0F, 2) - 0.3F;
         if (this.noiseValue > 0.0F) {
            abyssSample.height = abyssSample.height + this.noiseValue * 450.0F / 2.0F;
            abyssSample.color = ZONE_COLOR_ROCK;
         }

         result.height = result.height + abyssSample.height * blendAbyss;
         result.color = result.color.add(abyssSample.color.scale(clampZoneWeight(blendAbyss, 1.0F / activeZones)));
         if (blendAbyss > dominantBlend) {
            result.zoneId = ZoneId.ABYSS;
            dominantBlend = blendAbyss;
         }
      }

      if (blendMidC > 0.0F) {
         TerrainSample midCSample = new TerrainSample();
         PerlinNoise.seed = this.worldHandler.getRandGenDunes();
         midCSample.height = PerlinNoise.noise(x / 300.0F, z / 300.0F, 4) * 100.0F;
         midCSample.color = ZONE_COLOR_MID;
         result.height = result.height + midCSample.height * blendMidC;
         result.color = result.color.add(midCSample.color.scale(clampZoneWeight(blendMidC, 1.0F / activeZones)));
         if (blendMidC > dominantBlend) {
            result.zoneId = ZoneId.MIDWATER_C;
            dominantBlend = blendMidC;
         }
      }

      if (blendDeep > 0.0F) {
         TerrainSample deepSample = new TerrainSample();
         PerlinNoise.seed = this.worldHandler.getRandGenDunes();
         deepSample.height = PerlinNoise.noise(x / 300.0F, z / 300.0F, 4) * 100.0F;
         deepSample.color = ZONE_COLOR_DEEP;
         result.height = result.height + deepSample.height * blendDeep;
         result.color = result.color.add(deepSample.color.scale(clampZoneWeight(blendDeep, 1.0F / activeZones)));
         if (blendDeep > dominantBlend) {
            result.zoneId = ZoneId.DEEP_ABYSS;
         }
      }

      return result;
   }

   private static float clampZoneWeight(float blend, float weight) {
      blend = weight + (blend - weight) * 4.0F;
      if (blend < 0.0F) {
         blend = 0.0F;
      }

      if (blend > 1.0F) {
         blend = 1.0F;
      }

      return blend;
   }

   private float toWorldX(int localX) {
      return this.chunkWorldX + localX * Chunk.TERRAIN_STEP;
   }

   private float toWorldZ(int localZ) {
      return this.chunkWorldZ + localZ * Chunk.TERRAIN_STEP;
   }

   private static Point a(int x, int z, float h, float hRight, float hFwd, float hLeft, float hBack) {
      Point center = new Point((float)x, h, (float)z);
      Point toRight = new Point((float)(x + 1), hRight, (float)z).minus(center);
      Point toFwd = new Point((float)x, hFwd, (float)(z + 1)).minus(center);
      Point toLeft = new Point((float)(x - 1), hLeft, (float)z).minus(center);
      Point toBack = new Point((float)x, hBack, (float)(z - 1)).minus(center);
      Point normal = toFwd.cross(toRight);
      normal.add(toLeft.cross(toFwd));
      normal.add(toBack.cross(toLeft));
      normal.add(toRight.cross(toBack));
      normal.normalize();
      return normal;
   }

   public final void stop() {
      this.stopped = true;
   }

   public final void requestReset() {
      this.resetRequested = true;
   }

   public final boolean isResetComplete() {
      return this.resetComplete;
   }

   public final void confirmReset() {
      this.resetRequested = false;
   }

   private void resetQueues() {
      this.incomingQueue = new WorldDataQueue();
      this.outgoingQueue = new WorldDataQueue();
      if (Main.isVerbose) {
         System.out.println("WorldManager: Empty Queue");
      }
   }
}
