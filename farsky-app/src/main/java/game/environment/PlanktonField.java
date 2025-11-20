package game.environment;

import game.environment.particle.AmbientParticle;
import game.environment.particle.Particle;
import game.manager.Camera;
import game.manager.GameTime;
import game.manager.TextureManager;
import game.util.Point;
import game.util.UnitQuad;
import org.lwjgl.opengl.GL11;

public final class PlanktonField {
   private AmbientParticle[] particles = new AmbientParticle[250];

   public PlanktonField() {
      for (int i = 0; i < 250; i++) {
         this.particles[i] = new AmbientParticle();
      }
   }

   public final void update(float delta) {
      for (int i = 0; i < Math.min(EnvironmentManager.particleCount, 250); i++) {
         AmbientParticle particle = this.particles[i];
         float dist = particle.position.distanceTo(Camera.getPosition());
         if (dist > 800.0F) {
            particle.resetPosition(Camera.getPosition());
         } else if (dist > 400.0F) {
            Point dir = Camera.getPosition().minus(particle.position);
            dir.normalize();
            particle.position = Camera.getPosition().plus(dir.scaled(400.0F));
         }

         dist = particle.position.toCoord().distanceTo(Camera.getPosition().toCoord());
         if (dist - 50.0F < 140.0F) {
            particle.alpha = 0.5F * (dist - 50.0F) / 140.0F;
         } else if (400.0F - dist < 140.0F) {
            particle.alpha = 0.5F * (400.0F - dist) / 140.0F;
         } else {
            particle.alpha = 0.5F;
         }
      }
   }

   public final void render() {
      Point sunColor = DepthAtmosphere.getSunColor().plus(0.5F, 0.5F, 0.5F);
      Particle.beginRender();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.glowParticle);

      for (int i = 0; i < Math.min(EnvironmentManager.particleCount, 250); i++) {
         AmbientParticle particle = this.particles[i];
         float size = 60.0F + (float)Math.cos(GameTime.elapsedMillis / 1000.0F + i) * 60.0F * 0.08F;
         float rotationOffset = particle.position.x + particle.position.y + particle.position.z;
         GL11.glPushMatrix();
         GL11.glColor4f(sunColor.x, sunColor.y, sunColor.z, particle.alpha * GameTime.getLightLevel());
         GL11.glTranslatef(particle.position.x, particle.position.y, particle.position.z);
         Camera.lookAt(particle.position);
         GL11.glRotatef(rotationOffset, 0.0F, 0.0F, 1.0F);
         GL11.glScalef(size + rotationOffset % 20.0F, size + rotationOffset % 20.0F, 0.0F);
         UnitQuad.render();
         GL11.glPopMatrix();
      }

      Particle.endRender();
   }
}
