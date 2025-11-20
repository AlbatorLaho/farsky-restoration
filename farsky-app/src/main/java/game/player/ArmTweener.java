package game.player;

import game.util.Point;

public final class ArmTweener {
   private Point fromPosition;
   private Point fromRotation;
   private Point targetPosition;
   private Point targetRotation;
   private float progress = 0.0F;
   private float speed = 1.0F;

   public ArmTweener() {
      this.fromPosition = new Point();
      this.fromRotation = new Point();
      this.targetPosition = new Point();
      this.targetRotation = new Point();
   }

   public final void startTween(float speed) {
      this.fromPosition = this.getPosition();
      this.fromRotation = this.getRotation();
      this.progress = 0.0F;
      this.speed = speed;
   }

   public final void update(float delta) {
      if (this.progress < 1.0F) {
         this.progress = this.progress + delta * this.speed;
      } else {
         this.progress = 1.0F;
      }
   }

   public final boolean isComplete() {
      return this.progress == 1.0F;
   }

   public final void setTargetPosition(Point target) {
      this.targetPosition = target;
   }

   public final void setTargetRotation(Point target) {
      this.targetRotation = target;
   }

   public final Point getPosition() {
      return this.fromPosition.scaled(1.0F - this.progress).plus(this.targetPosition.scaled(this.progress));
   }

   public final Point getRotation() {
      return this.fromRotation.scaled(1.0F - this.progress).plus(this.targetRotation.scaled(this.progress));
   }
}
