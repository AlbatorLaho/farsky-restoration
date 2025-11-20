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

public final class Kelp extends ChunkElement {
   private float angle = 0.0F;
   private float scale = 1.0F;

   public Kelp(Point pos) {
      this.position = new Point(0.0F, 0.0F, 0.0F);
      this.position.set(pos);
      this.angle = (float)(Math.random() * Math.PI * 2.0);
      this.scale = 3.0F + (float)Math.random() * 3.0F;
   }

   @Override
   public final ArrayList<Vertex> buildVertices(TerrainSample[][] terrain) {
      ArrayList<Vertex> vertices = new ArrayList<>();

      for (float segment = 0.0F; segment < 4.0F; segment++) {
         vertices.add(
            new Vertex(
               new Point(
                  this.position.x + -0.5F * (float)Math.cos(this.angle) * this.scale, this.position.y + (segment + 1.0F) * this.scale, this.position.z + 0.5F * (float)Math.sin(this.angle) * this.scale
               ),
               new Coord(0.0F, 1.0F - (segment + 1.0F) / 4.0F),
               new Color(1.0F, 1.0F, 1.0F, (segment + 1.0F) / 4.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(
                  this.position.x + 0.5F * (float)Math.cos(this.angle) * this.scale, this.position.y + (segment + 1.0F) * this.scale, this.position.z + -0.5F * (float)Math.sin(this.angle) * this.scale
               ),
               new Coord(1.0F, 1.0F - (segment + 1.0F) / 4.0F),
               new Color(1.0F, 1.0F, 1.0F, (segment + 1.0F) / 4.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(this.position.x + 0.5F * (float)Math.cos(this.angle) * this.scale, this.position.y + segment * this.scale, this.position.z + -0.5F * (float)Math.sin(this.angle) * this.scale),
               new Coord(1.0F, 1.0F - segment / 4.0F),
               new Color(1.0F, 1.0F, 1.0F, segment / 4.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(this.position.x + -0.5F * (float)Math.cos(this.angle) * this.scale, this.position.y + segment * this.scale, this.position.z + 0.5F * (float)Math.sin(this.angle) * this.scale),
               new Coord(0.0F, 1.0F - segment / 4.0F),
               new Color(1.0F, 1.0F, 1.0F, segment / 4.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(
                  this.position.x + -0.5F * (float)Math.cos(this.angle + (Math.PI / 2)) * this.scale,
                  this.position.y + (segment + 1.0F) * this.scale,
                  this.position.z + 0.5F * (float)Math.sin(this.angle + (Math.PI / 2)) * this.scale
               ),
               new Coord(0.0F, 1.0F - (segment + 1.0F) / 4.0F),
               new Color(1.0F, 1.0F, 1.0F, (segment + 1.0F) / 4.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(
                  this.position.x + 0.5F * (float)Math.cos(this.angle + (Math.PI / 2)) * this.scale,
                  this.position.y + (segment + 1.0F) * this.scale,
                  this.position.z + -0.5F * (float)Math.sin(this.angle + (Math.PI / 2)) * this.scale
               ),
               new Coord(1.0F, 1.0F - (segment + 1.0F) / 4.0F),
               new Color(1.0F, 1.0F, 1.0F, (segment + 1.0F) / 4.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(
                  this.position.x + 0.5F * (float)Math.cos(this.angle + (Math.PI / 2)) * this.scale,
                  this.position.y + segment * this.scale,
                  this.position.z + -0.5F * (float)Math.sin(this.angle + (Math.PI / 2)) * this.scale
               ),
               new Coord(1.0F, 1.0F - segment / 4.0F),
               new Color(1.0F, 1.0F, 1.0F, segment / 4.0F + this.angle / 1000.0F)
            )
         );
         vertices.add(
            new Vertex(
               new Point(
                  this.position.x + -0.5F * (float)Math.cos(this.angle + (Math.PI / 2)) * this.scale,
                  this.position.y + segment * this.scale,
                  this.position.z + 0.5F * (float)Math.sin(this.angle + (Math.PI / 2)) * this.scale
               ),
               new Coord(0.0F, 1.0F - segment / 4.0F),
               new Color(1.0F, 1.0F, 1.0F, segment / 4.0F + this.angle / 1000.0F)
            )
         );
      }

      return vertices;
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.seabedAlpha);
      Shaders.setUniform("wave", true);
      Shaders.setUniform("useAlphaAsHeight", true);
      Shaders.setUniform("height", 3.5);
      Shaders.setUniform("factor", 1.5);
      Shaders.setUniform("amplitude", 50.0);
      Shaders.setUniform("discardTransparency", true);
      GL11.glDisable(GL11.GL_CULL_FACE);
   }

   public static void endBatch() {
      Shaders.setUniform("wave", false);
      Shaders.setUniform("useAlphaAsHeight", false);
      Shaders.setUniform("discardTransparency", false);
      GL11.glEnable(GL11.GL_CULL_FACE);
   }
}
