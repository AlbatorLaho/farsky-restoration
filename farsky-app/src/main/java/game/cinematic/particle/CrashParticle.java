package game.cinematic.particle;

import game.environment.particle.Particle;
import game.manager.Camera;
import game.util.Point;

public final class CrashParticle extends Particle {
   private Point direction;
   private float scale;

   public CrashParticle(Point direction, float scale) {
      super(new Point(), 1.0F);
      this.direction = direction;
      this.scale = scale;
      this.resetPosition(new Point());
   }

   private void resetPosition(Point center) {
      Point offset = new Point((Math.random() * 2.0 - 1.0) * 150.0, (Math.random() * 2.0 - 1.0) * 150.0, (Math.random() * 2.0 - 1.0) * 150.0);
      this.position = center.plus(offset);
   }

   public final void update(float deltaTime) {
      this.position.add(this.direction.scaled(deltaTime * this.scale));
      float dist = this.position.distanceTo(Camera.getPosition());
      if (dist > 300.0F) {
         this.resetPosition(Camera.getPosition());
      } else if (dist > 150.0F) {
         Point dir = Camera.getPosition().minus(this.position);
         dir.normalize();
         this.position = Camera.getPosition().plus(dir.scaled(150.0F - dist % 150.0F));
      }

      if (dist / 150.0F < 0.8F) {
         this.alpha = 1.0F;
      } else {
         this.alpha = -(dist / 150.0F) / 0.2F + 5.0F;
      }
   }
}
