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

public final class SeaGrass extends ChunkElement {
   private float angle = 0.0F;
   private static float leafSize = 3.0F;

   public SeaGrass(Point pos) {
      this.position = new Point(0.0F, 0.0F, 0.0F);
      this.position.set(pos);
      this.angle = (float)(Math.random() * Math.PI * 2.0);
   }

   @Override
   public final ArrayList<Vertex> buildVertices(TerrainSample[][] terrain) {
      ArrayList<Vertex> vertices = new ArrayList<>();
      vertices.add(
            new Vertex(
               new Point(this.position.x - leafSize * 2.0F * (float)Math.cos(this.angle), this.position.y - 1.5F + leafSize * 4.0F, this.position.z + leafSize * 2.0F * (float)Math.sin(this.angle)),
               new Coord(0, 0),
               new Color(1.0F, 1.0F, 1.0F, 1.0F + this.angle / 1000.0F)
            )
         );
      vertices.add(
         new Vertex(
            new Point(this.position.x + leafSize * 2.0F * (float)Math.cos(this.angle), this.position.y - 1.5F + leafSize * 4.0F, this.position.z - leafSize * 2.0F * (float)Math.sin(this.angle)),
            new Coord(1, 0),
            new Color(1.0F, 1.0F, 1.0F, 1.0F + this.angle / 1000.0F)
         )
      );
      vertices.add(
         new Vertex(
            new Point(this.position.x + leafSize * 2.0F * (float)Math.cos(this.angle), this.position.y - 1.5F, this.position.z - leafSize * 2.0F * (float)Math.sin(this.angle)),
            new Coord(1, 1),
            new Color(1.0F, 1.0F, 1.0F, 0.0F + this.angle / 1000.0F)
         )
      );
      vertices.add(
         new Vertex(
            new Point(this.position.x - leafSize * 2.0F * (float)Math.cos(this.angle), this.position.y - 1.5F, this.position.z + leafSize * 2.0F * (float)Math.sin(this.angle)),
            new Coord(0, 1),
            new Color(1.0F, 1.0F, 1.0F, 0.0F + this.angle / 1000.0F)
         )
      );
      return vertices;
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.grass);
      Shaders.setUniform("wave", true);
      Shaders.setUniform("useAlphaAsHeight", true);
      Shaders.setUniform("height", 3.0);
      Shaders.setUniform("factor", 2.0);
      Shaders.setUniform("amplitude", 20.0);
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
