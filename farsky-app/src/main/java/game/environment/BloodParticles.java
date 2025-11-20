package game.environment;

import game.environment.particle.BloodParticle;
import game.environment.particle.FadingParticle;
import game.manager.TextureManager;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class BloodParticles {
   public static enum BloodType {
      RED,
      BLUE;
   }

   private static float LIFETIME = 8.0F;
   private BloodParticle[] particles;
   private float elapsedTime = 0.0F;
   private Point position;

   public BloodParticles(Point position, int count) {
      this(position, 7, BloodType.RED);
   }

   public BloodParticles(Point position, int count, BloodType bloodType) {
      this.position = position.copy();
      this.particles = new BloodParticle[count];

      for (int i = 0; i < count; i++) {
         this.particles[i] = new BloodParticle(position.plus(new Point(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).scaled(4.0F)), 0.5F, bloodType);
      }
   }

   public final void update(float delta) {
      this.elapsedTime += delta;

      for (int i = 0; i < this.particles.length; i++) {
         BloodParticle particle = this.particles[i];
         float alpha = 0.5F - this.elapsedTime / LIFETIME * 0.5F;
         particle.alpha = alpha;
      }
   }

   public final void render() {
      GL11.glDepthMask(false);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.blood);

      for (int i = 0; i < this.particles.length; i++) {
         this.particles[i].render();
      }

      FadingParticle.resetRenderState();
   }

   public final boolean isDone() {
      return this.elapsedTime >= LIFETIME;
   }

   public final Point getPosition() {
      return this.position;
   }
}
