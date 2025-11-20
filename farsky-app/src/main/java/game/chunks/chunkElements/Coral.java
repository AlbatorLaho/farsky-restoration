package game.chunks.chunkElements;

import game.manager.TextureManager;
import game.render.QuadVbo;
import game.render.Vertex;
import game.shader.Shaders;
import game.util.Coord;
import game.util.Point;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class Coral extends ChunkElement {
   private float size = 1.0F;
   private float scaleModifier = 1.0F;
   private Point normalAngles;
   private static QuadVbo quadVbo;

   public Coral(Point pos, Point surfaceNormal) {
      this.position = pos.plus(0.0F, -0.5F, 0.0F);
      this.size = 4.0F + (float)(Math.random() * 3.0);
      this.normalAngles = surfaceNormal.toAngles();
   }

   public static void loadAssets() {
      ArrayList<Vertex> vertices = new ArrayList<>();

      for (int i = 0; i < 3; i++) {
         vertices.add(
            new Vertex(
               new Point(-0.5F * (float)Math.cos((Math.PI * 2.0 / 3.0) * i), 1.0F, -0.5F * (float)Math.sin((Math.PI * 2.0 / 3.0) * i)),
               new Coord(0.0F, 0.01F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(-0.5F * (float)Math.cos((Math.PI * 2.0 / 3.0) * i), 0.0F, -0.5F * (float)Math.sin((Math.PI * 2.0 / 3.0) * i)),
               new Coord(0.0F, 1.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(0.5F * (float)Math.cos((Math.PI * 2.0 / 3.0) * i), 0.0F, 0.5F * (float)Math.sin((Math.PI * 2.0 / 3.0) * i)), new Coord(1, 1)
            )
         );
         vertices.add(
            new Vertex(
               new Point(0.5F * (float)Math.cos((Math.PI * 2.0 / 3.0) * i), 1.0F, 0.5F * (float)Math.sin((Math.PI * 2.0 / 3.0) * i)),
               new Coord(1.0F, 0.01F)
            )
         );
      }

      quadVbo = new QuadVbo(vertices);
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.size * this.scaleModifier, this.size * this.scaleModifier, this.size * this.scaleModifier);
      GL11.glRotatef(this.normalAngles.y, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(this.normalAngles.x + 90.0F, 1.0F, 0.0F, 0.0F);
      quadVbo.render();
      GL11.glPopMatrix();
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.moss);
      GL11.glDisable(GL11.GL_CULL_FACE);
      Shaders.setUniform("discardTransparency", true);
      Shaders.setUniform("wave", true);
      Shaders.setUniform("height", 4.0);
      Shaders.setUniform("factor", 1.5);
      Shaders.setUniform("amplitude", 8.0);
   }

   public static void endBatch() {
      Shaders.setUniform("discardTransparency", false);
      Shaders.setUniform("wave", false);
      GL11.glEnable(GL11.GL_CULL_FACE);
   }
}
