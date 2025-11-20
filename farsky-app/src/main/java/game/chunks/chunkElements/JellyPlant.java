package game.chunks.chunkElements;

import game.collision.AABB;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Point;

import org.lwjgl.opengl.GL11;

public final class JellyPlant extends ChunkElement {
   private float yRotation = 0.0F;
   private float scale = 1.0F;
   private float brightness = 1.0F;
   private static Vbo stemVbo;
   private static Vbo topVbo;
   private static int textureId;

   public JellyPlant(Point pos) {
      this.position = new Point(0.0F, 0.0F, 0.0F);
      this.position.set(pos);
      this.yRotation = (float)(Math.random() * 180.0);
      this.scale = 25.0F + (float)(Math.random() * 20.0);
      this.brightness = (int)((float)(Math.random() + 0.5) * 10.0F) / 10.0F;
   }

   public static void loadAssets() {
      stemVbo = ModelLoader.loadMesh("jellyPlant");
      topVbo = ModelLoader.loadMesh("jellyPlant", "jellyPlantTop");
      textureId = ModelLoader.loadTexture("jellyPlant");
   }

   @Override
   public final AABB getBoundingBox() {
      return new AABB(this.position, 4.0F * this.scale, 10.0F * this.scale, 4.0F * this.scale);
   }

   @Override
   public final AABB getLocalBoundingBox() {
      return new AABB(new Point(), this.scale, 10.0F * this.scale, this.scale);
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
      Shaders.setUniform("wave", true);
      Shaders.setUniform("factor", 1.0);
      Shaders.setUniform("offset", 0.0);
      Shaders.setUniform("height", 7.0);
      Shaders.setUniform("amplitude", 2.0);
      Shaders.setUniform("alphaLightPercent", 1.0);
      Shaders.setUniform("alphaLightcolor", new Point(1.0F, 1.0F, 0.5F));
   }

   public static void endBatch() {
      Shaders.setUniform("wave", false);
      Shaders.setUniform("alphaLightPercent", 0.0);
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glColor4f(this.brightness, this.brightness, this.brightness, 1.0F);
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.scale, this.scale, this.scale);
      GL11.glRotatef(this.yRotation, 0.0F, 1.0F, 0.0F);
      stemVbo.render();
      topVbo.render();
      GL11.glPopMatrix();
   }
}
