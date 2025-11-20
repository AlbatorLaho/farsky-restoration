package game.util;

public final class Color {
   public float r = 1.0F;
   public float g = 1.0F;
   public float b = 1.0F;
   public float alpha;

   public Color() {
      this.alpha = 1.0F;
   }

   public Color(float r, float g, float b, float alpha) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.alpha = alpha;
   }
}
