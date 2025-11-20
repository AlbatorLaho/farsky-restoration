package game.chunks.chunkElements;

import game.manager.TextureManager;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Point;

import org.lwjgl.opengl.GL11;

public final class AbyssElement extends ChunkElement {
   private static Vbo vbo;
   private Point normalAngles;

   public AbyssElement(Point pos, Point normal) {
      this.position = new Point();
      this.position.set(pos);
      this.position.addY(-1.0F);
      this.normalAngles = normal.plus(0.0F, 1.0F, 0.0F);
      this.normalAngles.normalize();
   }

   public static void loadAssets() {
      vbo = ModelLoader.loadMesh("abyssElmtAlpha");
   }

   public static void beginBatch() {
      Shaders.setUniform("alphaLightPercent", 1.0);
      Shaders.setUniform("alphaLightcolor", new Point(0.3F, 0.6F, 1.0F));
      Shaders.setUniform("wave", true);
      Shaders.setUniform("factor", 1.0);
      Shaders.setUniform("offset", 0.0);
      Shaders.setUniform("height", 2.5);
      Shaders.setUniform("amplitude", 3.0);
   }

   public static void endBatch() {
      Shaders.setUniform("alphaLightPercent", 0.0);
      Shaders.setUniform("wave", false);
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      this.normalAngles.applyGLRotation();
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(10.0F, 10.0F, 10.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.alphaTexture);
      vbo.render();
      GL11.glPopMatrix();
   }
}
