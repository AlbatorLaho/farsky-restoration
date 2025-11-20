package game.util;

public final class Vec4 {
   public float x;
   public float y;
   public float z;
   public float w;

   public Vec4() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
      this.w = 0.0F;
   }

   public Vec4(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public final Vec4 add(Vec4 other) {
      return new Vec4(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w);
   }

   public final Vec4 scale(float factor) {
      return new Vec4(this.x * factor, this.y * factor, this.z * factor, this.w * factor);
   }

   public final int dominantAxis() {
      if (this.x >= this.y && this.x >= this.z && this.x >= this.w) {
         return 0;
      } else if (this.y >= this.x && this.y >= this.z && this.y >= this.w) {
         return 1;
      } else if (this.z >= this.x && this.z >= this.y && this.z >= this.w) {
         return 2;
      } else {
         return this.w >= this.x && this.w >= this.y && this.w >= this.z ? 3 : -1;
      }
   }

   public final boolean isAxisAligned() {
      if (this.y == 0.0F && this.z == 0.0F && this.w == 0.0F) {
         return true;
      } else if (this.x == 0.0F && this.z == 0.0F && this.w == 0.0F) {
         return true;
      } else {
         return this.x == 0.0F && this.y == 0.0F && this.w == 0.0F ? true : this.x == 0.0F && this.y == 0.0F && this.z == 0.0F;
      }
   }
}
