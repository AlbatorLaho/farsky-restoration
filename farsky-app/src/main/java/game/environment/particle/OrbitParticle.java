package game.environment.particle;

import game.manager.Camera;
import game.util.Point;

public final class OrbitParticle extends Particle {
   private float orbitSpeedX;
   private float orbitSpeedY;
   private float angleX = (float)(Math.random() * Math.PI);
   private float angleY = (float)(Math.random() * Math.PI);
   private float orbitRadius = 10.0F;
   private Point center;

   public OrbitParticle() {
      super(new Point(), 1.0F);
      this.orbitSpeedX = (float)(Math.random() * 100.0);
      this.orbitSpeedY = (float)(Math.random() * 100.0);
      this.resetCenter(new Point((Math.random() * 2.0 - 1.0) * 150.0, (Math.random() * 2.0 - 1.0) * 150.0, (Math.random() * 2.0 - 1.0) * 150.0));
      this.position = this.center.copy();
   }

   private void resetCenter(Point newCenter) {
      Point offset = new Point((Math.random() * 2.0 - 1.0) * 150.0, (Math.random() * 2.0 - 1.0) * 150.0, (Math.random() * 2.0 - 1.0) * 150.0);
      this.center = newCenter.plus(offset);
   }

   public final void update(float delta) {
      this.angleX = this.angleX + 50.0F / (130.0F + this.orbitSpeedX) * delta;
      this.angleY = this.angleY + 50.0F / (180.0F + this.orbitSpeedY) * delta;
      this.angleX = (float)(this.angleX % (Math.PI * 2));
      this.angleY = (float)(this.angleY % (Math.PI * 2));
      delta = this.center.distanceTo(Camera.getPosition());
      if (delta > 300.0F) {
         this.resetCenter(Camera.getPosition());
      } else if (delta > 150.0F) {
         Point dir = Camera.getPosition().minus(this.center);
         dir.normalize();
         this.center = Camera.getPosition().plus(dir.scaled(150.0F - delta % 150.0F));
      }

      this.position = this.center.plus(new Point(this.orbitRadius * Math.cos(this.angleX) * Math.sin(this.angleY), this.orbitRadius * Math.cos(this.angleY), this.orbitRadius * Math.sin(this.angleX) * Math.sin(this.angleY)));
      if (delta / 150.0F < 0.8F) {
         this.alpha = 1.0F;
      } else {
         this.alpha = -(delta / 150.0F) / 0.2F + 5.0F;
      }
   }
}
