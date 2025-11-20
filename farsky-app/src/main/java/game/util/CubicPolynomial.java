package game.util;

public final class CubicPolynomial {
   public float c0 = 0.0F;
   public float c1 = 0.0F;
   public float c2 = 0.0F;
   public float c3 = 0.0F;

   public final float evaluate(float t) {
      return this.c0 + this.c1 * t + this.c2 * t * t + this.c3 * t * t * t;
   }
}
