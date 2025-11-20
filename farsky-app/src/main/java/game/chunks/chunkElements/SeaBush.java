package game.chunks.chunkElements;

import game.manager.TextureManager;
import game.render.Vertex;
import game.shader.Shaders;
import game.util.Color;
import game.util.Coord;
import game.util.Point;
import game.world.structure.TerrainSample;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class SeaBush extends ChunkElement {
   private float angle = 0.0F;
   private float scale = 1.0F;

   public SeaBush(Point pos) {
      this.position = new Point(0.0F, 0.0F, 0.0F);
      this.position.set(pos);
      this.angle = (float)(Math.random() * Math.PI * 2.0);
      this.scale = 6.0F + (float)Math.random() * 6.0F;
   }

   @Override
   public final ArrayList<Vertex> buildVertices(TerrainSample[][] terrain) {
      ArrayList<Vertex> vertices = new ArrayList<>();

      for (float leafAngle = 0.0F; leafAngle < Math.PI * 0.9; leafAngle = (float)(leafAngle + (Math.PI / 3))) {
         vertices.add(
            new Vertex(
               new Point(
                  this.position.x + -0.5F * (float)Math.cos(this.angle + leafAngle) * this.scale,
                  this.position.y + 0.5F * this.scale,
                  this.position.z + 0.5F * (float)Math.sin(this.angle + leafAngle) * this.scale
               ),
               new Coord(0.0F, 0.1F),
               new Color(1.0F, 1.0F, 1.0F, 1.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(
                  this.position.x + 0.5F * (float)Math.cos(this.angle + leafAngle) * this.scale,
                  this.position.y + 0.5F * this.scale,
                  this.position.z + -0.5F * (float)Math.sin(this.angle + leafAngle) * this.scale
               ),
               new Coord(1.0F, 0.1F),
               new Color(1.0F, 1.0F, 1.0F, 1.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(this.position.x + 0.5F * (float)Math.cos(this.angle + leafAngle) * this.scale, this.position.y, this.position.z + -0.5F * (float)Math.sin(this.angle + leafAngle) * this.scale),
               new Coord(1.0F, 1.0F),
               new Color(1.0F, 1.0F, 1.0F, 0.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(this.position.x + -0.5F * (float)Math.cos(this.angle + leafAngle) * this.scale, this.position.y, this.position.z + 0.5F * (float)Math.sin(this.angle + leafAngle) * this.scale),
               new Coord(0.0F, 1.0F),
               new Color(1.0F, 1.0F, 1.0F, 0.0F + this.angle / 1000.0F)
            )
         );
      }

      return vertices;
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.seabedBeta);
      Shaders.setUniform("wave", true);
      Shaders.setUniform("useAlphaAsHeight", true);
      Shaders.setUniform("height", 2.0);
      Shaders.setUniform("factor", 1.5);
      Shaders.setUniform("amplitude", 5.0);
      Shaders.setUniform("discardTransparency", false);
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glDepthMask(false);
   }

   public static void endBatch() {
      Shaders.setUniform("wave", false);
      Shaders.setUniform("useAlphaAsHeight", false);
      Shaders.setUniform("discardTransparency", false);
      GL11.glEnable(GL11.GL_CULL_FACE);
      GL11.glDepthMask(true);
   }
}
