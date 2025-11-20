package game.util;

public class Coord extends Point {
   private static final long serialVersionUID = 5320092647303704284L;

   public Coord() {
      super(0.0F, 0.0F, 0.0F);
   }

   public Coord(int x, int y) {
      super((float)x, (float)y, 0.0F);
   }

   public Coord(float x, float y) {
      super(x, y, 0.0F);
   }

   public Coord(double x, double y) {
      super(x, y, 0.0);
   }

   public final Coord copy() {
      return new Coord(this.x, this.y);
   }

   public final Coord plus(Coord other) {
      return new Coord(this.x + other.x, this.y + other.y);
   }

   public final Coord minus(Coord other) {
      return new Coord(this.x - other.x, this.y - other.y);
   }

   public final Coord toPixels(float scale) {
      return new Coord(this.x * 100.0F, this.y * 100.0F);
   }

   public final float angle() {
      Coord normalized = new Coord(this.x, this.y);
      normalized.normalize();
      float angle = (float)Math.acos(normalized.x);
      if (normalized.y < 0.0F) {
         angle = (float) (Math.PI * 2) - angle;
      }

      return angle;
   }

   public final boolean equals(Coord other) {
      return this.x == other.x && this.y == other.y;
   }
}
