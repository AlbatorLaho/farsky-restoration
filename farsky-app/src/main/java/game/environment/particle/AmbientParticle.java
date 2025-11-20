package game.environment.particle;

import game.util.Point;

public final class AmbientParticle extends Particle {
   public AmbientParticle() {
      super(new Point(), 1.0F);
      this.resetPosition(new Point());
   }

   public final void resetPosition(Point center) {
      float angle = (float)(Math.random() * Math.PI * 2.0);
      Point offset = new Point(-Math.sin(angle), Math.random() * 2.0 - 1.0, -Math.cos(angle));
      this.position = center.plus(offset.scaled(50.0F + (float)Math.random() * 350.0F));
   }
}
