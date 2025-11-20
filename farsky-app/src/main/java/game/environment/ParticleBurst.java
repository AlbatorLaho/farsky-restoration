package game.environment;

import game.environment.particle.FadingParticle;
import game.manager.TextureManager;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class ParticleBurst {
   private static float PARTICLE_SIZE = 40.0F;
   private FadingParticle[] particles;
   private float elapsedTime = 0.0F;

   public ParticleBurst(Point position, Point direction, float speed, int count) {
      this.particles = new FadingParticle[count];
      direction.normalize();

      for (int i = 0; i < count; i++) {
         Point dir = direction.copy();
         dir.rotateX((float)Math.random() * 45.0F - 22.5F);
         dir.rotateY((float)Math.random() * 45.0F - 22.5F);
         dir.rotateZ((float)Math.random() * 45.0F - 22.5F);
         this.particles[i] = new FadingParticle(position, dir, speed, 1.0F + (float)Math.random());
      }
   }

   public final void update(float delta) {
      this.elapsedTime += delta;

      for (int i = 0; i < this.particles.length; i++) {
         this.particles[i].update(delta);
      }
   }

   public final void render() {
      GL11.glDepthMask(false);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.sandParticle);

      for (int i = 0; i < this.particles.length; i++) {
         this.particles[i].render(PARTICLE_SIZE);
      }

      FadingParticle.resetRenderState();
   }

   public final boolean isDone() {
      return this.elapsedTime >= 3.0F;
   }
}
