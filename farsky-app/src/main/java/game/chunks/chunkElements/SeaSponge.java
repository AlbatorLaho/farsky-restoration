package game.chunks.chunkElements;

import game.render.ModelLoader;
import game.render.Vbo;
import game.util.Point;

import org.lwjgl.opengl.GL11;

public final class SeaSponge extends ChunkElement {
   private float yRotation = 0.0F;
   private float tiltAngle = 0.0F;
   private float width = 1.0F;
   private float spongeHeight = 1.0F;
   private Point color = new Point();
   private static Vbo vbo;
   private static int textureId;

   public SeaSponge(Point pos) {
      this.position = new Point();
      this.position.set(pos.plus(0.0F, -1.0F, 0.0F));
      this.yRotation = (float)(Math.random() * 360.0);
      this.tiltAngle = (float)(Math.random() * 20.0);
      this.width = 3.0F + (float)(Math.random() * 15.0);
      this.spongeHeight = 4.0F + (float)(Math.random() * 6.0);
      this.color = new Point(1.0F, 0.1F, 0.0F);
      this.color.scale(1.0F - (float)Math.random() * 0.4F);
   }

   public static void loadAssets() {
      vbo = ModelLoader.loadMesh("seaSponge");
      textureId = ModelLoader.loadTexture("seaSponge");
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glColor4f(this.color.x, this.color.y, this.color.z, 1.0F);
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.width, this.spongeHeight, this.width);
      GL11.glRotatef(this.yRotation, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(this.tiltAngle, 1.0F, 0.0F, 0.0F);
      vbo.render();
      GL11.glPopMatrix();
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
   }
}
