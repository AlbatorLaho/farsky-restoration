package game.environment.particle;

import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class FadingParticle extends Particle {
   private Point direction;
   private float speed;
   private float lifetime;

   public FadingParticle(Point position, Point direction, float speed, float lifetime) {
      super(position, 1.0F);
      this.direction = direction;
      this.speed = speed;
      this.lifetime = lifetime;
   }

   public final void update(float delta) {
      this.alpha = this.alpha - delta / this.lifetime;
      this.position = this.position.plus(this.direction.scaled(delta * this.speed));
   }

   public static void resetRenderState() {
      GL11.glDepthMask(true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
