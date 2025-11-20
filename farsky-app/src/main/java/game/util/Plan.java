package game.util;

import java.io.Serializable;

public class Plan implements Serializable {
   private static final long serialVersionUID = -412409752022900772L;
   private float nx;
   private float ny;
   private float nz;
   private float dist;

   public Plan() {
      this.nx = 0.0F;
      this.ny = 0.0F;
      this.nz = 0.0F;
      this.dist = 0.0F;
   }

   public Plan(Point normal, Point point) {
      normal.normalize();
      this.nx = normal.x;
      this.ny = normal.y;
      this.nz = normal.z;
      this.dist = -(this.nx * point.x + this.ny * point.y + this.nz * point.z);
   }

   public final void copyFrom(Plan other) {
      this.nx = other.nx;
      this.ny = other.ny;
      this.nz = other.nz;
      this.dist = other.dist;
   }

   public final int sideOf(Point point) {
      float dot = this.nx * point.x + this.ny * point.y + this.nz * point.z + this.dist;
      if (dot < 0.0F) {
         return -1;
      } else {
         return dot == 0.0F ? 0 : 1;
      }
   }

   public final Point getNormal() {
      return new Point(this.nx, this.ny, this.nz);
   }

   public final float getYAt(float x, float z) {
      return -(this.nx * x + this.nz * z + this.dist) / this.ny;
   }

   public final Point intersect(Point origin, Point dir) {
      Point result = new Point();
      float t = this.nx * dir.x + this.ny * dir.y + this.nz * dir.z;
      if (t == 0.0F) {
         return null;
      } else {
         t = -(this.nx * origin.x + this.ny * origin.y + this.nz * origin.z + this.dist) / t;
         result.x = dir.x * t + origin.x;
         result.y = dir.y * t + origin.y;
         result.z = dir.z * t + origin.z;
         return result;
      }
   }

   @Override
   public String toString() {
      return "P: ax+by+cz+d=0\na:" + this.nx + " b:" + this.ny + " c:" + this.nz + " d:" + this.dist;
   }
}
