package game.environment.particle;

import game.manager.Camera;
import game.shader.Shaders;
import game.util.Point;
import game.util.UnitQuad;
import org.lwjgl.opengl.GL11;

public class Particle {
   public Point position;
   public float alpha;

   public Particle(Point position, float alpha) {
      this.position = position;
      this.alpha = alpha;
   }

   public final void render(float size) {
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, this.alpha);
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      Camera.applyYawPitch();
      GL11.glScalef(size, size, 0.0F);
      UnitQuad.render();
      GL11.glPopMatrix();
   }

   public static void beginRender() {
      Shaders.setUniform("emissive", true);
      GL11.glDepthMask(false);
   }

   public static void endRender() {
      Shaders.setUniform("emissive", false);
      GL11.glDepthMask(true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
