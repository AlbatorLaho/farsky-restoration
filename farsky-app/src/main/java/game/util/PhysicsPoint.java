package game.util;

import game.manager.GameScene;

public final class PhysicsPoint {
   private Point pos;
   private Point vel;
   private Point angularPos;
   private Point angularVel;
   private float gravityScale;
   private float scale;

   public PhysicsPoint(Point pos, Point vel, Point angularPos, Point angularVel, float gravityScale, float scale) {
      this.pos = pos;
      this.vel = vel;
      this.angularPos = angularPos;
      this.angularVel = angularVel;
      this.gravityScale = gravityScale;
      this.scale = scale;
   }

   public final void update(float delta) {
      this.vel.add(GameScene.gravity.scaled(this.gravityScale * delta));
      this.pos.add(this.vel.scaled(delta));
      this.angularPos.add(this.angularVel.scaled(delta));
   }

   public final Point getPosition() {
      return this.pos;
   }

   public final Point getVelocity() {
      return this.vel;
   }

   public final Point getAngularPosition() {
      return this.angularPos;
   }

   public final float getScale() {
      return this.scale;
   }
}
