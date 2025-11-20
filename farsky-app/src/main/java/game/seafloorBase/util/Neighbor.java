package game.seafloorBase.util;

import game.util.Point;

public enum Neighbor {
   UP(0, new Point(0.0F, 1.0F, 0.0F)),
   DOWN(1, new Point(0.0F, -1.0F, 0.0F)),
   WEST(2, new Point(-1.0F, 0.0F, 0.0F)),
   EAST(3, new Point(1.0F, 0.0F, 0.0F)),
   NORTH(4, new Point(0.0F, 0.0F, -1.0F)),
   SOUTH(5, new Point(0.0F, 0.0F, 1.0F)),
   NORTHEAST(6, new Point(1.0F, 0.0F, -1.0F)),
   NORTHWEST(7, new Point(-1.0F, 0.0F, -1.0F)),
   SOUTHEAST(8, new Point(1.0F, 0.0F, 1.0F)),
   SOUTHWEST(9, new Point(-1.0F, 0.0F, 1.0F));

   int index = 0;
   Point offset = new Point();

   private Neighbor(int index, Point offset) {
      this.index = index;
      this.offset = offset;
   }

   public final int getIndex() {
      return this.index;
   }

   public final Point getOffset() {
      return this.offset;
   }
}
