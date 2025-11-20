package game.chunks.chunkElements;

import game.chunks.Chunk;
import game.inventory.ItemType;
import game.manager.TextureManager;
import game.render.QuadVbo;
import game.render.Vbo;
import game.render.Vertex;
import game.util.Coord;
import game.util.Point;
import game.world.structure.GamePlayType;
import game.world.structure.TerrainSample;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class TerrainOverlay extends ChunkElement {
   private Vbo vbo;
   private GamePlayType overlayType;
   private int oreCount;

   public TerrainOverlay(GamePlayType overlayType, int oreCount) {
      this.overlayType = overlayType;
      this.oreCount = oreCount;
   }

   @Override
   public final void render() {
      if (this.overlayType == GamePlayType.CORAL_OVERLAY) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.dirt);
      }

      if (this.overlayType == GamePlayType.GRASS_OVERLAY) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.coal);
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, this.oreCount / 250.0F * 3.0F);
      this.vbo.render();
   }

   @Override
   public final ArrayList<Vertex> buildVertices(TerrainSample[][] terrain) {
      ArrayList<Vertex> vertices = new ArrayList<>();

      for (byte xi = 0; xi < Chunk.SIZE - 1; xi += 4) {
         for (byte zi = 0; zi < Chunk.SIZE - 1; zi += 4) {
            int x0 = xi * Chunk.TERRAIN_STEP;
            int x1 = xi * Chunk.TERRAIN_STEP + (Chunk.TERRAIN_STEP << 2);
            int z0 = zi * Chunk.TERRAIN_STEP;
            int z1 = zi * Chunk.TERRAIN_STEP + (Chunk.TERRAIN_STEP << 2);
            vertices.add(new Vertex(new Point((float)x0, terrain[xi][zi].height + 0.5F, (float)z0), new Coord((float)xi / Chunk.SIZE, (float)zi / Chunk.SIZE)));
            vertices.add(
               new Vertex(new Point((float)x0, terrain[xi][zi + 4].height + 0.5F, (float)z1), new Coord((float)xi / Chunk.SIZE, (float)(zi + 4) / Chunk.SIZE))
            );
            vertices.add(
               new Vertex(
                  new Point((float)x1, terrain[xi + 4][zi + 4].height + 0.5F, (float)z1), new Coord((float)(xi + 4) / Chunk.SIZE, (float)(zi + 4) / Chunk.SIZE)
               )
            );
            vertices.add(
               new Vertex(new Point((float)x1, terrain[xi + 4][zi].height + 0.5F, (float)z0), new Coord((float)(xi + 4) / Chunk.SIZE, (float)zi / Chunk.SIZE))
            );
         }
      }

      this.vbo = new QuadVbo(vertices);
      return null;
   }

   @Override
   public final void onUnload() {
      this.vbo.dispose();
   }

   @Override
   public final ItemType harvest(boolean consume) {
      this.oreCount = Math.max(0, this.oreCount - 1);
      return null;
   }
}
