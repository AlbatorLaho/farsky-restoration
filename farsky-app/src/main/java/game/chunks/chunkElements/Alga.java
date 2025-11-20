package game.chunks.chunkElements;

import game.collision.AABB;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Point;

import org.lwjgl.opengl.GL11;

public final class Alga extends ChunkElement {
   public static enum AlgaType {
      NORMAL,
      GLOWING;
   }

   private float yRotation = 0.0F;
   private float scale = 1.0F;
   private static Vbo vbo;
   private static boolean textureSet = false;
   private static int normalTextureId;
   private static int glowTextureId;
   private float brightness;
   private AlgaType algaType;

   public Alga(Point pos) {
      this(pos, AlgaType.NORMAL);
   }

   public Alga(Point pos, AlgaType type) {
      this.position = pos.plus(0.0F, -5.0F, 0.0F);
      this.algaType = type;
      this.yRotation = (float)(Math.random() * 180.0);
      this.scale = 3.0F + (float)(Math.random() * 5.0);
      this.brightness = (int)((float)(Math.random() + 0.5) * 10.0F) / 10.0F;
   }

   public static void loadAssets() {
      vbo = ModelLoader.loadMesh("algaSource");
      normalTextureId = ModelLoader.loadTexture("algaSource");
      glowTextureId = ModelLoader.loadTexture("algaSource", "algaSourceGlowing");
   }

   @Override
   public final AABB getBoundingBox() {
      return new AABB(this.position, 2.0F * this.scale, 18.0F * this.scale, 2.0F * this.scale);
   }

   public static void beginBatch() {
      textureSet = false;
      Shaders.setUniform("wave", true);
      Shaders.setUniform("factor", 1.0);
      Shaders.setUniform("offset", 0.0);
      Shaders.setUniform("height", 10.0);
      Shaders.setUniform("amplitude", 2.0);
      Shaders.setUniform("alphaLightPercent", 1.0);
      Shaders.setUniform("alphaLightcolor", new Point(1.0F, 1.0F, 1.0F));
   }

   public static void endBatch() {
      Shaders.setUniform("wave", false);
      Shaders.setUniform("alphaLightPercent", 0.0);
   }

   @Override
   public final void render() {
      if (!textureSet) {
         if (this.algaType == AlgaType.NORMAL) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTextureId);
         } else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, glowTextureId);
         }

         textureSet = true;
      }

      GL11.glPushMatrix();
      GL11.glColor4f(this.brightness, this.brightness, this.brightness, 1.0F);
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.scale * 0.8F, this.scale, this.scale * 0.8F);
      GL11.glRotatef(this.yRotation, 0.0F, 1.0F, 0.0F);
      vbo.render();
      GL11.glPopMatrix();
   }

   @Override
   public final AABB getLocalBoundingBox() {
      return new AABB(new Point(), 2.0F * this.scale, 18.0F * this.scale, 2.0F * this.scale);
   }
}
