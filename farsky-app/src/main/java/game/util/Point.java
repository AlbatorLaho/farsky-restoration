package game.util;

import java.io.Serializable;
import org.lwjgl.opengl.GL11;

public class Point implements Serializable {
   private static final long serialVersionUID = 6443898131086514296L;
   public float x;
   public float y;
   public float z;

   public Point() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
   }

   public Point(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Point(double x, double y, double z) {
      this.x = (float)x;
      this.y = (float)y;
      this.z = (float)z;
   }

   public final void rotateX(float degrees) {
      float rad = degrees * (float) Math.PI / 180.0F;
      float newY = (float)(this.y * Math.cos(rad) - this.z * Math.sin(rad));
      rad = (float)(this.y * Math.sin(rad) + this.z * Math.cos(rad));
      this.y = newY;
      this.z = rad;
   }

   public final void rotateY(float degrees) {
      float rad = degrees * (float) Math.PI / 180.0F;
      float newX = (float)(this.x * Math.cos(rad) + this.z * Math.sin(rad));
      rad = (float)(-this.x * Math.sin(rad) + this.z * Math.cos(rad));
      this.x = newX;
      this.z = rad;
   }

   public final void rotateZ(float degrees) {
      this.rotateZRad(degrees * (float) Math.PI / 180.0F);
   }

   public final void rotateZRad(float rad) {
      float newX = (float)(this.x * Math.cos(rad) - this.y * Math.sin(rad));
      rad = (float)(this.x * Math.sin(rad) + this.y * Math.cos(rad));
      this.x = newX;
      this.y = rad;
   }

   public final Point cross(Point other) {
      return new Point(
         this.y * other.z - this.z * other.y,
         this.z * other.x - this.x * other.z,
         this.x * other.y - this.y * other.x
      );
   }

   public final void add(Point other) {
      this.x = this.x + other.x;
      this.y = this.y + other.y;
      this.z = this.z + other.z;
   }

   public final void add(float x, float y, float z) {
      this.add(new Point(x, y, z));
   }

   public final void subtract(Point other) {
      this.x = this.x - other.x;
      this.y = this.y - other.y;
      this.z = this.z - other.z;
   }

   public final Point plus(Point other) {
      return new Point(this.x + other.x, this.y + other.y, this.z + other.z);
   }

   public final Point plus(float x, float y, float z) {
      return this.plus(new Point(x, y, z));
   }

   public final Point minus(Point other) {
      return new Point(this.x - other.x, this.y - other.y, this.z - other.z);
   }

   public final Point minus(float x, float y, float z) {
      return new Point(this.x - x, this.y - y, this.z - z);
   }

   public final void addX(float delta) {
      this.x += delta;
   }

   public final void addY(float delta) {
      this.y += delta;
   }

   public final void toChunkSpace(float slotSize) {
      this.x /= slotSize;
      this.y /= slotSize;
      this.z /= slotSize;
   }

   public final void scale(float factor) {
      this.x *= factor;
      this.y *= factor;
      this.z *= factor;
   }

   public final Point scaled(float factor) {
      return new Point(this.x * factor, this.y * factor, this.z * factor);
   }

   public final void set(Point other) {
      this.x = other.x;
      this.y = other.y;
      this.z = other.z;
   }

   public Point copy() {
      return new Point(this.x, this.y, this.z);
   }

   public final float length() {
      return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public final void normalize() {
      float len = this.length();
      if (len != 0.0F) {
         this.x /= len;
         this.y /= len;
         this.z /= len;
      }
   }

   public final float dot(Point other) {
      return this.x * other.x + this.y * other.y + this.z * other.z;
   }

   public final float distanceTo(Point other) {
      return other.minus(this).length();
   }

   public final float magnitude() {
      return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public final boolean equals(Point other) {
      return this.x == other.x && this.y == other.y && this.z == other.z;
   }

   public final void applyGLRotation() {
      Point angles = this.toAngles();
      GL11.glRotatef(angles.y, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(angles.x, 1.0F, 0.0F, 0.0F);
   }

   public final Point toAngles() {
      return new Point(Math.toDegrees(new Coord(new Coord(this.x, this.z).length(), -this.y).angle()), Math.toDegrees(new Coord(this.z, this.x).angle()), 0.0);
   }

   public final Coord toCoord() {
      return new Coord(this.x, this.z);
   }

   @Override
   public String toString() {
      return "\tX:" + this.x + "\tY:" + this.y + "\tZ:" + this.z;
   }
}
