package game.world.gen;

import game.world.World;
import game.world.structure.ZoneId;

public final class TerrainSmoother {
   public static World smooth(World world) {
      for (int pass = 0; pass < 20; pass++) {
         for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int z = 1; z < world.getHeight() - 1; z++) {
               if (world.getZoneIdAt(x, z) == ZoneId.OPEN_OCEAN
                  && (
                     Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x - 1, z)) > 400.0F
                        || Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x + 1, z)) > 400.0F
                        || Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x, z - 1)) > 400.0F
                        || Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x, z + 1)) > 400.0F
                  )) {
                  world.setHeightAt(x, z, (world.getHeightAt(x - 1, z) + world.getHeightAt(x + 1, z) + world.getHeightAt(x, z - 1) + world.getHeightAt(x, z + 1)) / 4.0F);
               }
            }
         }
      }

      for (int pass = 0; pass < 5; pass++) {
         for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int z = 1; z < world.getHeight() - 1; z++) {
               if (world.getZoneIdAt(x, z) != ZoneId.OPEN_OCEAN
                  && (
                     Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x - 1, z)) > 400.0F
                        || Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x + 1, z)) > 400.0F
                        || Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x, z - 1)) > 400.0F
                        || Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x, z + 1)) > 400.0F
                  )) {
                  world.setZoneIdAt(x, z, ZoneId.OPEN_OCEAN);
                  world.setHeightAt(x, z, (world.getHeightAt(x - 1, z) + world.getHeightAt(x + 1, z) + world.getHeightAt(x, z - 1) + world.getHeightAt(x, z + 1)) / 4.0F);
                  world.setStageAt(x, z, -1);
               }
            }
         }
      }

      for (int x = 1; x < world.getWidth() - 1; x++) {
         for (int z = 1; z < world.getHeight() - 1; z++) {
            if (world.getZoneIdAt(x, z) != ZoneId.OPEN_OCEAN
               && (
                  Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x - 1, z)) > 200.0F
                     || Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x + 1, z)) > 200.0F
                     || Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x, z - 1)) > 200.0F
                     || Math.abs(world.getHeightAt(x, z) - world.getHeightAt(x, z + 1)) > 200.0F
               )) {
               world.setZoneIdAt(x, z, ZoneId.OPEN_OCEAN);
            }
         }
      }

      return world;
   }
}
