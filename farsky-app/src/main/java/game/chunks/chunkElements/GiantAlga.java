package game.chunks.chunkElements;

import game.collision.AABB;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Point;

import org.lwjgl.opengl.GL11;

public final class GiantAlga extends ChunkElement {
   private float yRotation = 0.0F;
   private float scale = 1.0F;
   private float brightness = 1.0F;
   private static Vbo vbo;
   private static int textureId;

   public GiantAlga(Point pos) {
      this.position = new Point(0.0F, 0.0F, 0.0F);
      this.position.set(pos);
      this.yRotation = (float)(Math.random() * 180.0);
      this.scale = 15.0F + (float)(Math.random() * 20.0);
      this.brightness = (int)((float)(Math.random() + 0.5) * 10.0F) / 10.0F;
   }

   public static void loadAssets() {
      vbo = ModelLoader.loadMesh("giantAlga");
      textureId = ModelLoader.loadTexture("giantAlga");
   }

   @Override
   public final AABB getBoundingBox() {
      return new AABB(this.position, 4.0F * this.scale, 20.0F * this.scale, 4.0F * this.scale);
   }

   @Override
   public final AABB getLocalBoundingBox() {
      return new AABB(new Point(), this.scale, 20.0F * this.scale, this.scale);
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
      Shaders.setUniform("wave", true);
      Shaders.setUniform("factor", 1.0);
      Shaders.setUniform("offset", 0.0);
      Shaders.setUniform("height", 10.0);
      Shaders.setUniform("amplitude", 2.0);
   }

   public static void endBatch() {
      Shaders.setUniform("wave", false);
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glColor4f(this.brightness, this.brightness, this.brightness, 1.0F);
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.scale * 0.8F, this.scale, this.scale * 0.8F);
      GL11.glRotatef(this.yRotation, 0.0F, 1.0F, 0.0F);
      vbo.render();
      GL11.glPopMatrix();
   }
}
