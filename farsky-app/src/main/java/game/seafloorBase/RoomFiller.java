package game.seafloorBase;

import game.manager.GameScene;
import game.seafloorBase.util.BlockType;
import game.seafloorBase.util.Dir;
import game.util.Point;
import java.util.ArrayList;

public final class RoomFiller {
   private ArrayList<AirFlowQueue> airQueues = new ArrayList<>();
   private Point center;
   private float deferredAir;

   public RoomFiller(Point center) {
      this.center = center;
      this.deferredAir = 0.0F;
   }

   public final void registerRoom(Block[][][] blocks, Point pos) {
      int x = (int)pos.x;
      int y = (int)pos.y;
      int z = (int)pos.z;
      this.airQueues.add(new AirFlowQueue());

      for (int bx = 0; bx < 32; bx++) {
         for (int by = 0; by < 8; by++) {
            for (int bz = 0; bz < 32; bz++) {
               blocks[bx][by][bz].visited = false;
            }
         }
      }

      if (!blocks[x][y][z].hasWall()) {
         this.floodFillFrom(blocks, x, y, z);
      } else {
         if (blocks[x][y][z].hasElement(BlockType.WALL, Dir.EAST)) {
            this.floodFillFrom(blocks, x + 1, y, z);
         }

         if (blocks[x][y][z].hasElement(BlockType.WALL, Dir.WEST)) {
            this.floodFillFrom(blocks, x - 1, y, z);
         }

         if (blocks[x][y][z].hasElement(BlockType.WALL, Dir.SOUTH)) {
            this.floodFillFrom(blocks, x, y, z + 1);
         }

         if (blocks[x][y][z].hasElement(BlockType.WALL, Dir.NORTH)) {
            this.floodFillFrom(blocks, x, y, z - 1);
         }
      }
   }

   private void floodFillFrom(Block[][][] blocks, int x, int y, int z) {
      while (!blocks[x][y][z].visited && blocks[x][y][z].hasElement(BlockType.WATER_LEVEL)) {
         this.airQueues.get(this.airQueues.size() - 1).enqueueBlock(blocks, x, y, z);
         blocks[x][y][z].visited = true;
         if (blocks[x][y][z].hasElement(BlockType.WATER_LEVEL)
            && y > 0
            && blocks[x][y][z].isBuilt()
            && !blocks[x][y][z].hasElement(BlockType.FLOOR)
            && blocks[x][y - 1][z].isBuilt()) {
            this.floodFillFrom(blocks, x, y - 1, z);
         }

         if (!blocks[x][y][z].hasWall()) {
            if (x > 0) {
               this.floodFillFrom(blocks, x - 1, y, z);
            }

            if (x < 31) {
               this.floodFillFrom(blocks, x + 1, y, z);
            }

            if (z > 0) {
               this.floodFillFrom(blocks, x, y, z - 1);
            }

            if (z < 31) {
               this.floodFillFrom(blocks, x, y, z + 1);
            }

            if (x > 0 && z > 0 && blocks[x - 1][y][z - 1].hasElement(BlockType.WALL_DIAGONAL)) {
               this.floodFillFrom(blocks, x - 1, y, z - 1);
            }

            if (x < 31 && z > 0 && blocks[x + 1][y][z - 1].hasElement(BlockType.WALL_DIAGONAL)) {
               this.floodFillFrom(blocks, x + 1, y, z - 1);
            }

            if (x > 0 && z < 31 && blocks[x - 1][y][z + 1].hasElement(BlockType.WALL_DIAGONAL)) {
               this.floodFillFrom(blocks, x - 1, y, z + 1);
            }

            if (x < 31 && z < 31 && blocks[x + 1][y][z + 1].hasElement(BlockType.WALL_DIAGONAL)) {
               int nextX = x + 1;
               z++;
               x = nextX;
               continue;
            }
         }

         return;
      }
   }

   public final void update(float delta, Block[][][] blocks) {
      if (!(this.center.distanceTo(GameScene.avatar.getCameraPos()) < 600.0F)) {
         this.deferredAir += delta;
      } else {
         if (this.deferredAir != 0.0F) {
            delta += this.deferredAir;
            this.deferredAir = 0.0F;
         }

         for (int i = 0; i < this.airQueues.size(); i++) {
            this.airQueues.get(i).distributeAir(delta);
         }

         float drainRate = 0.005F * delta;
         delta = 0.0F;

         for (int level = 6; level >= 0; level--) {
            boolean foundWater = false;

            for (int bx = 0; bx < 32; bx++) {
               for (int bz = 0; bz < 32; bz++) {
                  if (blocks[bx][level][bz] != null && blocks[bx][level][bz].hasElement(BlockType.WATER_LEVEL)) {
                     if (!foundWater && blocks[bx][level][bz].getElement(BlockType.WATER_LEVEL).getParam() > 0.0F) {
                        foundWater = true;
                        delta = Math.min(blocks[bx][level][bz].getElement(BlockType.WATER_LEVEL).getParam(), drainRate);
                        drainRate -= delta;
                     }

                     blocks[bx][level][bz].getElement(BlockType.WATER_LEVEL).setDrawWaterLevel1(true);
                     if (blocks[bx][level + 1][bz] != null
                        && blocks[bx][level + 1][bz].hasElement(BlockType.WATER_LEVEL)
                        && blocks[bx][level + 1][bz].getElement(BlockType.WATER_LEVEL).getParam() > 0.0F) {
                        blocks[bx][level][bz].getElement(BlockType.WATER_LEVEL).setDrawWaterLevel1(false);
                     } else if (delta > 0.0F) {
                        blocks[bx][level][bz].getElement(BlockType.WATER_LEVEL).setParam(Math.max(blocks[bx][level][bz].getElement(BlockType.WATER_LEVEL).getParam() - delta, 0.0F));
                     }

                     if (blocks[bx][level][bz].getElement(BlockType.WATER_LEVEL).getParam() > 0.16F) {
                        blocks[bx][level][bz].resetElements();
                     }
                  }
               }
            }
         }
      }
   }
}
