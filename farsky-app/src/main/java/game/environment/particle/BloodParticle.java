package game.environment.particle;
import game.environment.BloodParticles;
import game.manager.Camera;
import game.manager.GameTime;
import game.shader.Shaders;
import game.util.Point;
import game.util.UnitQuad;
import org.lwjgl.opengl.GL11;

public final class BloodParticle extends Particle {
   private float size;
   private float rotation;
   private BloodParticles.BloodType bloodType;

   public BloodParticle(Point position, float scale, BloodParticles.BloodType bloodType) {
      super(position, 0.5F);
      this.bloodType = bloodType;
      this.rotation = (float)(Math.random() * 360.0);
      if (bloodType == BloodParticles.BloodType.RED) {
         this.size = 6.0F + (float)Math.random() * 10.0F;
      } else {
         this.size = 3.0F + (float)Math.random() * 5.0F;
      }
   }

   public final void render() {
      Shaders.setUniform("emissive", true);
      GL11.glPushMatrix();
      if (this.bloodType == BloodParticles.BloodType.RED) {
         GL11.glColor4f(0.9F, 0.0F, 0.0F, this.alpha);
      } else {
         GL11.glColor4f(0.5F, 0.8F, 1.0F, this.alpha);
      }

      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      Camera.applyYawPitch();
      GL11.glRotatef(this.rotation, 0.0F, 0.0F, 1.0F);
      GL11.glScalef(1.0F + (float)Math.cos(GameTime.elapsedMillis / 500.0F) / 4.0F, 1.0F + (float)Math.cos(GameTime.elapsedMillis / 750.0F) / 4.0F, 1.0F);
      GL11.glScalef(this.size, this.size, 0.0F);
      UnitQuad.render();
      GL11.glPopMatrix();
      Shaders.setUniform("emissive", false);
   }
}
