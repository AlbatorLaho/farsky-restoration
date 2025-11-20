package game.environment;

import org.lwjgl.opengl.GL11;

import game.environment.particle.OrbitParticle;
import game.environment.particle.Particle;
import game.manager.TextureManager;

public final class BubbleField {
   private OrbitParticle[] particles = new OrbitParticle[200];

   public BubbleField() {
      for (int i = 0; i < 200; i++) {
         this.particles[i] = new OrbitParticle();
      }
   }

   public final void update(float delta) {
      for (int i = 0; i < Math.min(EnvironmentManager.particleCount, 200); i++) {
         this.particles[i].update(delta);
      }
   }

   public final void render() {
      Particle.beginRender();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.particle);

      for (int i = 0; i < Math.min(EnvironmentManager.particleCount, 200); i++) {
         this.particles[i].render(1.25F);
      }

      Particle.endRender();
   }
}
