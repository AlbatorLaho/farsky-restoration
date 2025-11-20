package game.util;

public final class IntCoord {
   public int x;
   public int y;

   public IntCoord(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public final float distanceTo(IntCoord other) {
      return (float)Math.sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y));
   }
}
