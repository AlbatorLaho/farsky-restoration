package game.enemy.kraken;

import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class KrakenTentacle {
   private static Vbo mesh;
   private static int texture;
   private Point rotation = new Point();
   private Point offset;

   public static void loadResources() {
      texture = ModelLoader.loadTexture("kraken", "tentacle");
      mesh = ModelLoader.loadMesh("kraken", "tentacle");
   }

   public KrakenTentacle(Kraken kraken, float angle, float tilt) {
      this.rotation.y = -angle;
      this.rotation.x = 0.0F;
      this.offset = new Point(Math.cos(Math.toRadians(angle)), 0.0, Math.sin(Math.toRadians(angle))).scaled(5.0F);
   }

   public static void setupRender() {
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 1.0F, 0.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
      Shaders.setUniform("wave", new Point(0.0F, 0.0F, 1.0F));
      Shaders.setUniform("height", 8.0);
      Shaders.setUniform("factor", 10.0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
   }

   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(this.offset.x, this.offset.y, this.offset.z);
      GL11.glRotatef(90.0F + this.rotation.y, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-this.rotation.x, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(-25.0F, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(5.0F, 5.0F, 5.0F);
      mesh.render();
      GL11.glPopMatrix();
   }

   public final void moveToAngle(float targetAngle, float deltaTime, float speed) {
      deltaTime = Math.signum(targetAngle - this.rotation.x) * deltaTime * speed;
      if (Math.abs(deltaTime) >= Math.abs(targetAngle - this.rotation.x)) {
         this.rotation.x = targetAngle;
      } else {
         this.rotation.x += deltaTime;
      }
   }
}
