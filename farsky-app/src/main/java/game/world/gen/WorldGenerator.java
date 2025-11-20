package game.world.gen;
import game.enemy.EnemyGenerator;

import game.manager.GameScene;
import game.sounds.ChunkLayer;
import game.util.Coord;
import game.util.PerlinNoise;
import game.world.World;
import game.world.structure.ZoneId;
import java.util.Random;

public final class WorldGenerator {
   private static World world;
   private static int ZONE_COUNT = 3;
   private static Coord startPos;
   private static Random rng;

   public static void generate(float dayTime, float nightTime, EnemyGenerator.SpawningLevel spawning) {
      rng = new Random(SeedInput.getSeed());
      switch (GameScene.gameMode) {
         case ADVENTURE:
         case SURVIVOR:
            generateAdventureWorld(130, 130);
            break;
         case SANDBOX:
            generateSandboxWorld(200, 200);
		default:
			break;
      }

      world.setRandLevelGenCliff(rng.nextFloat());
      world.setRandGenDunes(rng.nextFloat());
      world.setRandGenRock(rng.nextFloat());
      world.setRandGenSource(rng.nextFloat());
      world.setRandLandscapeAlga(rng.nextFloat());
      world.setRandLandscapeGiantAlga(rng.nextFloat());
      world.setDayTime(dayTime);
      world.setNightTime(nightTime);
      world.setSpawning(spawning);
   }

   public static World getWorld() {
      return world;
   }

   private static void generateAdventureWorld(int width, int height) {
      do {
         world = new World(130, 130);
         float initialRadius = world.getWidth() / 2 * 0.75F;
         float radius = initialRadius;

         for (int zone = ZONE_COUNT - 1; zone >= 0; zone--) {
            placeZoneDisk(new Coord(world.getWidth() / 2, world.getHeight() / 2), radius, zone);

            for (int s = 0; s < 3; s++) {
               float angle = (float)(rng.nextFloat() * Math.PI * 2.0);
               placeZoneDisk(new Coord(world.getWidth() / 2, world.getHeight() / 2).plus(new Coord(radius * Math.cos(angle), radius * Math.sin(angle))), 7.0F, zone);
            }

            if (zone < ZONE_COUNT - 1) {
               for (int s = 0; s < 3; s++) {
                  float angle = (float)(rng.nextFloat() * Math.PI * 2.0);
                  placeZoneDisk(new Coord(world.getWidth() / 2, world.getHeight() / 2).plus(new Coord(radius * Math.cos(angle), radius * Math.sin(angle))), 7.0F, zone + 1);
               }
            }

            radius -= initialRadius * 0.3F;
         }

         addHeightVariation();
      } while (!findStartPosition());

      world = TerrainSmoother.smooth(world);
      world = ChunkLayer.generate(world, startPos, ZONE_COUNT, rng);
      world = TerrainSmoother.smooth(world);
      world.setAvatarStartPosition(new Coord(startPos.x * 128.0F, startPos.y * 128.0F));
   }

   private static void generateSandboxWorld(int width, int height) {
      do {
         world = new World(200, 200);
         float noiseSeed1 = rng.nextFloat() * 1000.0F;
         float noiseSeed2 = rng.nextFloat() * 1000.0F;

         for (int x = 0; x < world.getWidth(); x++) {
            for (int z = 0; z < world.getHeight(); z++) {
               PerlinNoise.seed = noiseSeed1;
               float noiseVal = PerlinNoise.noise(x / 7.0F, z / 7.0F, 2);
               byte stageVal = 0;
               if (noiseVal >= 0.15F) {
                  stageVal = 0;
               }

               if (Math.abs(noiseVal) < 0.15F) {
                  stageVal = 1;
               }

               if (noiseVal <= -0.15F) {
                  stageVal = 2;
               }

               world.setHeightAt(x, z, -1000.0F + stageVal * -1000.0F);
               world.setStageAt(x, z, (int)stageVal);
               if (stageVal == 0) {
                  PerlinNoise.seed = noiseSeed2;
                  if (PerlinNoise.noise(x / 2.0F, z / 2.0F, 1) > 0.0F) {
                     world.setZoneIdAt(x, z, ZoneId.REEF);
                  } else {
                     world.setZoneIdAt(x, z, ZoneId.KELP_FOREST);
                  }
               } else if (stageVal == 1) {
                  PerlinNoise.seed = noiseSeed2;
                  if (PerlinNoise.noise(x / 2.0F, z / 2.0F, 1) > 0.0F) {
                     world.setZoneIdAt(x, z, ZoneId.MIDWATER_A);
                  } else {
                     world.setZoneIdAt(x, z, ZoneId.MIDWATER_B);
                  }
               } else if (stageVal == 2) {
                  world.setZoneIdAt(x, z, ZoneId.ABYSS);
               }
            }
         }

         Coord center = new Coord(100, 100);
         float circleRadius = Math.min(80.0F, 80.0F);
         float noiseSeed3 = rng.nextFloat() * 1000.0F;

         for (int x = 0; x < world.getWidth(); x++) {
            for (int z = 0; z < world.getHeight(); z++) {
               Coord normalized = new Coord((float)x, (float)z);
               normalized.normalize();
               PerlinNoise.seed = noiseSeed3;
               float edgeNoise = PerlinNoise.noise(normalized.x * circleRadius, normalized.y * circleRadius, 2) * circleRadius * 0.35F;
               if (new Coord(x, z).distanceTo(center) > circleRadius + edgeNoise) {
                  world.setZoneIdAt(x, z, ZoneId.OPEN_OCEAN);
                  world.setHeightAt(x, z, -4000.0F);
                  world.setStageAt(x, z, -1);
               }
            }
         }

         addHeightVariation();
      } while (!findStartPosition());

      world = TerrainSmoother.smooth(world);
      world = ChunkLayer.generate(world, startPos, ZONE_COUNT, rng);
      world = TerrainSmoother.smooth(world);
      world.setAvatarStartPosition(new Coord(startPos.x * 128.0F, startPos.y * 128.0F));
   }

   private static boolean findStartPosition() {
      Coord center = new Coord(world.getWidth() / 2, world.getHeight() / 2);
      int searchRadius = Math.min(world.getWidth() / 2, world.getHeight() / 2);

      for (int r = 0; r < searchRadius; r++) {
         for (int dx = -r; dx < r; dx++) {
            for (int dz = -r; dz < r; dz++) {
               if ((dx == -r || dx == r || dz == -r || dz == r)
                  && ChunkLayer.isAreaFree(world, (int)center.x + dx, (int)center.y + dz, 3, 0)
                  && ChunkLayer.isFlatEnough(world, (int)center.x + dx, (int)center.y + dz, 3, 5.0F)) {
                  startPos = new Coord((int)center.x + dx, (int)center.y + dz);
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static void placeZoneDisk(Coord origin, float radius, int stage) {
      float noiseSeed1 = rng.nextFloat() * 1000.0F;
      float noiseSeed2 = rng.nextFloat() * 1000.0F;

      for (int x = 0; x < world.getWidth(); x++) {
         for (int z = 0; z < world.getHeight(); z++) {
            Coord normalized = new Coord((float)x, (float)z);
            normalized.normalize();
            PerlinNoise.seed = noiseSeed1;
            float edgeNoise = PerlinNoise.noise(normalized.x * radius, normalized.y * radius, 2) * radius * 0.35F;
            if (new Coord(x, z).distanceTo(origin) <= radius + edgeNoise) {
               world.setHeightAt(x, z, -1000.0F + stage * -1000.0F);
               world.setStageAt(x, z, stage);
               switch (stage) {
                  case 0:
                     PerlinNoise.seed = noiseSeed2;
                     if (PerlinNoise.noise(x / 2.0F, z / 2.0F, 1) > 0.1F) {
                        world.setZoneIdAt(x, z, ZoneId.REEF);
                     } else {
                        world.setZoneIdAt(x, z, ZoneId.KELP_FOREST);
                     }
                     break;
                  case 1:
                     PerlinNoise.seed = noiseSeed2;
                     if (PerlinNoise.noise(x / 2.0F, z / 2.0F, 1) > 0.0F) {
                        world.setZoneIdAt(x, z, ZoneId.MIDWATER_A);
                     } else {
                        world.setZoneIdAt(x, z, ZoneId.MIDWATER_B);
                     }
                     break;
                  case 2:
                     world.setZoneIdAt(x, z, ZoneId.ABYSS);
               }
            }
         }
      }
   }

   private static void addHeightVariation() {
      float noiseSeed = rng.nextFloat() * 100.0F;

      for (int x = 0; x < world.getWidth(); x++) {
         for (int z = 0; z < world.getHeight(); z++) {
            if (world.getZoneIdAt(x, z) != ZoneId.ABYSS) {
               PerlinNoise.seed = noiseSeed;
               float heightVariation;
               if (world.getZoneIdAt(x, z) != ZoneId.MIDWATER_A && world.getZoneIdAt(x, z) != ZoneId.MIDWATER_B) {
                  heightVariation = PerlinNoise.noise(x / 5.0F, z / 5.0F, 1) * 4.0F;
               } else {
                  heightVariation = PerlinNoise.noise(x / 15.0F, z / 15.0F, 1) * 4.0F;
               }

               if (heightVariation > 1.0F) {
                  heightVariation = 1.0F;
               }

               if (heightVariation < -1.0F) {
                  heightVariation = -1.0F;
               }

               heightVariation = (float)Math.floor((heightVariation * 0.5F + 0.5F) * 3.0F) / 3.0F;
               if (world.getStageAt(x, z) >= 0) {
                  world.setHeightAt(x, z, world.getHeightAt(x, z) + heightVariation * -1000.0F * 0.5F);
               }
            }
         }
      }
   }
}
