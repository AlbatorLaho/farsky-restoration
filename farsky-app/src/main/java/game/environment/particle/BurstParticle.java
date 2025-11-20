package game.environment.particle;

import game.util.Point;

public final class BurstParticle extends Particle {
   private Point velocity;
   private float speed;
   private float lifetime;
   private float maxLifetime;

   public BurstParticle(Point position, Point velocity, float speed, float lifetime) {
      super(position, 1.0F);
      this.velocity = velocity;
      this.speed = speed;
      this.lifetime = lifetime;
      this.maxLifetime = lifetime;
      this.alpha = 0.5F;
   }

   public final void update(float delta) {
      this.lifetime -= delta;
      this.alpha = this.lifetime / this.maxLifetime * 0.8F;
      this.position = this.position.plus(this.velocity.scaled(delta * this.speed));
   }

   public final void render() {
      this.render(5.0F + (this.maxLifetime - this.lifetime) * 5.0F);
   }

   public final boolean isDone() {
      return this.lifetime <= 0.0F;
   }
}
