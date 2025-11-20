package game.enemy.kraken;

import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class KrakenBody {
   private static Vbo mesh;
   private static int texture;

   public KrakenBody() {
   }

   public static void loadResources() {
      texture = ModelLoader.loadTexture("kraken");
      mesh = ModelLoader.loadMesh("kraken");
   }

   public static void render() {
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 1.0F, 0.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 0.0F, 0.0F));
      Shaders.setUniform("wave", new Point(1.0F, 0.0F, 0.0F));
      Shaders.setUniform("height", 6.0);
      Shaders.setUniform("factor", 5.0);
      GL11.glPushMatrix();
      GL11.glScalef(7.0F, 7.0F, 7.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
      mesh.render();
      GL11.glPopMatrix();
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("axisSign", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("wave", new Point(0.0F, 0.0F, 0.0F));
   }
}
