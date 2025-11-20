package game.collision;

import game.manager.GameScene;
import game.util.Point;

public class AABB extends BoundingBox {
   private static final long serialVersionUID = 3266043807752899897L;
   public Point min = new Point();
   public Point max = new Point();

   public AABB() {
      super(
         new Point(-1.0F, 1.0F, -1.0F),
         new Point(1.0F, 1.0F, -1.0F),
         new Point(1.0F, 1.0F, 1.0F),
         new Point(-1.0F, 1.0F, 1.0F),
         new Point(-1.0F, -1.0F, -1.0F),
         new Point(1.0F, -1.0F, -1.0F),
         new Point(1.0F, -1.0F, 1.0F),
         new Point(-1.0F, -1.0F, 1.0F)
      );
   }

   public AABB(Point position, float width, float height, float depth) {
      super(
         new Point(-width / 2.0F, height / 2.0F, -depth / 2.0F),
         new Point(width / 2.0F, height / 2.0F, -depth / 2.0F),
         new Point(width / 2.0F, height / 2.0F, depth / 2.0F),
         new Point(-width / 2.0F, height / 2.0F, depth / 2.0F),
         new Point(-width / 2.0F, -height / 2.0F, -depth / 2.0F),
         new Point(width / 2.0F, -height / 2.0F, -depth / 2.0F),
         new Point(width / 2.0F, -height / 2.0F, depth / 2.0F),
         new Point(-width / 2.0F, -height / 2.0F, depth / 2.0F)
      );
      this.min = new Point(-width / 2.0F, -height / 2.0F, -depth / 2.0F);
      this.max = new Point(width / 2.0F, height / 2.0F, depth / 2.0F);
      this.translate(position);
   }

   public AABB(Point position, Point min, Point max) {
      super(
         new Point(min.x, max.y, min.z),
         new Point(max.x, max.y, min.z),
         new Point(max.x, max.y, max.z),
         new Point(min.x, max.y, max.z),
         new Point(min.x, min.y, min.z),
         new Point(max.x, min.y, min.z),
         new Point(max.x, min.y, max.z),
         new Point(min.x, min.y, max.z)
      );
      this.min = min;
      this.max = max;
      this.translate(position);
   }

   public final AABB union(AABB other) {
      if (other == null) {
         return this;
      } else {
         Point newMin = new Point();
         Point newMax = new Point();
         newMin.x = Math.min(this.min.x, other.min.x);
         newMin.y = Math.min(this.min.y, other.min.y);
         newMin.z = Math.min(this.min.z, other.min.z);
         newMax.x = Math.max(this.max.x, other.max.x);
         newMax.y = Math.max(this.max.y, other.max.y);
         newMax.z = Math.max(this.max.z, other.max.z);
         Point center = this.getCenter();
         float width = newMax.x - newMin.x;
         float height = newMax.y - newMin.y;
         float depth = newMax.z - newMin.z;
         return new AABB(center, width, height, depth);
      }
   }

   public final Point getCenter() {
      return new Point((this.min.x + this.max.x) / 2.0F, (this.min.y + this.max.y) / 2.0F, (this.min.z + this.max.z) / 2.0F);
   }

   @Override
   public final void translate(Point offset) {
      super.translate(offset);
      this.min.add(offset);
      this.max.add(offset);
   }

   @Override
   public final void copyFrom(AABB source) {
      super.copyFrom(source);
      this.min.set(source.min);
      this.max.set(source.max);
   }

   public final float getHeight() {
      return this.max.y - this.min.y;
   }

   public final boolean isInPlayerSight(Point worldPos, float maxDist) {
      return GameScene.avatar != null && CollisionDetector.rayIntersectsAABB(GameScene.avatar.getCameraPos().minus(worldPos), GameScene.avatar.getLookDir(), this) && CollisionDetector.getHitDistance() <= maxDist && this.getCenter().plus(worldPos).minus(GameScene.avatar.getCameraPos()).dot(GameScene.avatar.getLookDir()) > 0.0F;
   }
}
