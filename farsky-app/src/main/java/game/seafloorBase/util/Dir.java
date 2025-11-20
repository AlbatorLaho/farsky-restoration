package game.seafloorBase.util;

import game.util.Point;

public enum Dir {
   NORTH,
   EAST,
   SOUTH,
   WEST;

   public final int neighborIndex() {
      if (this == EAST) {
         return Neighbor.EAST.getIndex();
      } else if (this == SOUTH) {
         return Neighbor.SOUTH.getIndex();
      } else {
         return this == WEST ? Neighbor.WEST.getIndex() : Neighbor.NORTH.getIndex();
      }
   }

   public final Point offset() {
      if (this == EAST) {
         return Neighbor.EAST.getOffset();
      } else if (this == SOUTH) {
         return Neighbor.SOUTH.getOffset();
      } else {
         return this == WEST ? Neighbor.WEST.getOffset() : Neighbor.NORTH.getOffset();
      }
   }

   public final Dir opposite() {
      if (this == EAST) {
         return WEST;
      } else if (this == SOUTH) {
         return NORTH;
      } else {
         return this == WEST ? EAST : SOUTH;
      }
   }
}
