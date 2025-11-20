package game.chunks.chunkElements;

import game.collision.AABB;
import game.render.ModelLoader;
import game.render.Vbo;
import game.util.Point;
import game.util.State;

import org.lwjgl.opengl.GL11;

public final class Shipwreck extends ChunkElement {
   private float scale = 1.0F;
   private static Vbo vbo;
   private static int textureId;

   public Shipwreck(Point pos) {
      this.position = pos.plus(0.0F, -15.0F, 0.0F);
      this.scale = 10.0F;
   }

   public static void loadAssets() {
      vbo = ModelLoader.loadMesh("shipwreck");
      textureId = ModelLoader.loadTexture("shipwreck");
   }

   @Override
   public final AABB getBoundingBox() {
      return new AABB(this.position, 11.0F * this.scale, 9.5F * this.scale, 28.0F * this.scale);
   }

   @Override
   public final AABB getLocalBoundingBox() {
      return new AABB(new Point(), this.scale, 20.0F * this.scale, this.scale);
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.scale, this.scale, this.scale);
      vbo.render();
      GL11.glPopMatrix();
   }

   @Override
   public final State resolveCollision(State state, State result) {
      result = new AABB(this.position.plus(-2.7F * this.scale, 0.0F, 0.5F * this.scale), 3.7F * this.scale, 9.0F * this.scale, 28.0F * this.scale).resolveCollision(state, result);
      result = new AABB(this.position.plus(0.0F, 0.0F, 6.4F * this.scale), 3.5F * this.scale, 7.5F * this.scale, 9.5F * this.scale).resolveCollision(state, result);
      result = new AABB(this.position.plus(0.0F, 0.0F, 9.1F * this.scale), 8.0F * this.scale, 7.5F * this.scale, 4.2F * this.scale).resolveCollision(state, result);
      result = new AABB(this.position.plus(2.0F * this.scale, 0.0F, 4.4F * this.scale), 9.0F * this.scale, 2.5F * this.scale, 3.5F * this.scale).resolveCollision(state, result);
      result = new AABB(this.position.plus(-1.0F * this.scale, 4.2F * this.scale, -0.5F * this.scale), 2.5F * this.scale, 0.3F * this.scale, 24.0F * this.scale).resolveCollision(state, result);
      result = new AABB(this.position.plus(-1.0F * this.scale, -4.2F * this.scale, -0.5F * this.scale), 2.5F * this.scale, 0.3F * this.scale, 24.0F * this.scale).resolveCollision(state, result);
      result = new AABB(this.position.plus(-5.0F * this.scale, 0.0F, 1.5F * this.scale), 2.0F * this.scale, 2.5F * this.scale, 20.0F * this.scale).resolveCollision(state, result);
      result = new AABB(this.position.plus(-1.0F * this.scale, 3.5F * this.scale, 0.7F * this.scale), 2.6F * this.scale, 0.6F * this.scale, 4.0F * this.scale).resolveCollision(state, result);
      return new AABB(this.position.plus(-1.0F * this.scale, -3.5F * this.scale, 0.7F * this.scale), 2.6F * this.scale, 0.6F * this.scale, 4.0F * this.scale).resolveCollision(state, result);
   }
}
