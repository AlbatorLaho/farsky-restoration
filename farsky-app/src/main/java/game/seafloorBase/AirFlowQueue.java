package game.seafloorBase;

import game.seafloorBase.util.BlockType;
import java.util.ArrayList;

final class AirFlowQueue {
   @SuppressWarnings("unchecked")
   private ArrayList<Block>[] levelQueues = (ArrayList<Block>[]) new ArrayList[8];

   AirFlowQueue() {
      for (int i = 0; i < this.levelQueues.length; i++) {
         this.levelQueues[i] = new ArrayList<>();
      }
   }

   public final void enqueueBlock(Block[][][] blocks, int x, int level, int z) {
      this.levelQueues[level].add(blocks[x][level][z]);
   }

   public final void distributeAir(float airAmount) {
      airAmount *= 0.01F;

      for (int level = 0; level < this.levelQueues.length; level++) {
         if (this.levelQueues[level].size() > 0) {
            float maxWaterLevel = this.levelQueues[level].get(0).getElement(BlockType.WATER_LEVEL).getParam();
            if (this.levelQueues[level].size() > 1) {
               maxWaterLevel = Math.max(maxWaterLevel, this.levelQueues[level].get(1).getElement(BlockType.WATER_LEVEL).getParam());
            }

            if (maxWaterLevel < 1.0F) {
               float newLevel = Math.min(maxWaterLevel + airAmount, 1.0F);
               airAmount -= newLevel - maxWaterLevel;

               for (int i = 0; i < this.levelQueues[level].size(); i++) {
                  this.levelQueues[level].get(i).getElement(BlockType.WATER_LEVEL).setParam(newLevel);
               }
            }
         }

         if (airAmount == 0.0F) {
            break;
         }
      }
   }
}
