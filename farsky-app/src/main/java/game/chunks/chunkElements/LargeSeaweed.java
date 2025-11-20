package game.chunks.chunkElements;

import game.collision.AABB;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Point;

import org.lwjgl.opengl.GL11;

public final class LargeSeaweed extends ChunkElement {
   private float yRotation = 0.0F;
   private float scale = 1.0F;
   private float brightness = 1.0F;
   private static Vbo highVbo;
   private static Vbo largeVbo;
   private static int textureId;
   private boolean tall;
   private boolean orange;

   public LargeSeaweed(Point pos, boolean tall, boolean orange) {
      this.position = new Point(0.0F, 0.0F, 0.0F);
      this.position.set(pos);
      this.tall = tall;
      this.orange = orange;
      this.yRotation = (float)(Math.random() * 180.0);
      this.scale = 15.0F + (float)(Math.random() * 20.0);
      this.brightness = (float)(Math.random() * 0.4F + 0.6F);
   }

   public static void loadAssets() {
      highVbo = ModelLoader.loadMesh("seaweed", "highSeaweed");
      largeVbo = ModelLoader.loadMesh("seaweed", "largeSeaweed");
      textureId = ModelLoader.loadTexture("seaweed");
   }

   @Override
   public final AABB getBoundingBox() {
      return this.tall ? new AABB(this.position, 4.0F * this.scale, 20.0F * this.scale, 4.0F * this.scale) : new AABB(this.position, 4.0F * this.scale, 10.0F * this.scale, 4.0F * this.scale);
   }

   @Override
   public final AABB getLocalBoundingBox() {
      return this.tall ? new AABB(new Point(), this.scale, 20.0F * this.scale, this.scale) : new AABB(new Point(), this.scale, 10.0F * this.scale, this.scale);
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
      Shaders.setUniform("wave", true);
      Shaders.setUniform("factor", 1.0);
      Shaders.setUniform("offset", 0.0);
      Shaders.setUniform("amplitude", 2.0);
      Shaders.setUniform("discardTransparency", true);
      GL11.glDisable(GL11.GL_CULL_FACE);
   }

   public static void endBatch() {
      Shaders.setUniform("wave", false);
      Shaders.setUniform("discardTransparency", false);
      GL11.glEnable(GL11.GL_CULL_FACE);
   }

   @Override
   public final void render() {
      if (this.tall) {
         Shaders.setUniform("height", 13.0);
      } else {
         Shaders.setUniform("height", 9.0);
      }

      GL11.glPushMatrix();
      if (!this.orange) {
         GL11.glColor4f(0.18F * this.brightness, 0.7F * this.brightness, 0.28F * this.brightness, 1.0F);
      } else {
         GL11.glColor4f(0.95F * this.brightness, 0.5F * this.brightness, 0.1F * this.brightness, 1.0F);
      }

      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.scale * 0.8F, this.scale, this.scale * 0.8F);
      GL11.glRotatef(this.yRotation, 0.0F, 1.0F, 0.0F);
      if (this.tall) {
         highVbo.render();
      } else {
         largeVbo.render();
      }

      GL11.glPopMatrix();
   }
}
