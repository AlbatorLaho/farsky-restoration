package game.chunks.chunkElements;

import game.manager.TextureManager;
import game.render.ModelLoader;
import game.render.Vbo;
import game.util.Point;

import org.lwjgl.opengl.GL11;

public final class Rock extends ChunkElement {
   private float yRotation = 0.0F;
   private float scale = 1.0F;
   private float brightness = 1.0F;
   private static Vbo vbo;

   public Rock(Point pos) {
      this.position = pos.copy();
      this.yRotation = (float)(Math.random() * 180.0);
      this.scale = 0.8F + (float)(Math.random() * 1.2F);
      this.brightness = (float)(Math.random() / 4.0 + 0.25);
   }

   public static void loadAssets() {
      vbo = ModelLoader.loadMesh("rock1");
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.pebble);
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glColor4f(this.brightness, this.brightness, this.brightness, 1.0F);
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.scale, this.scale, this.scale);
      GL11.glRotatef(this.yRotation, 0.0F, 1.0F, 0.0F);
      vbo.render();
      GL11.glPopMatrix();
   }
}
