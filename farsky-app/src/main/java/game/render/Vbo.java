package game.render;

import game.chunks.Chunk;
import game.util.Coord;
import game.util.Point;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.Color;

public class Vbo {
   private int vertexBufferId = -1;
   private int indexBufferId = -1;
   private int indexCount = 0;
   private int floatsPerVertex = 0;
   private FloatBuffer vertexBuffer = null;
   private boolean ready = false;
   private boolean hasNormals = false;
   private boolean hasColors = false;
   private boolean hasTexCoords = false;
   private boolean drawTriangles = true;

   public final void buildFromGrid(Point[][] heightGrid, Point[][] normalGrid, Color[][] colorGrid, Coord[][] texCoordGrid, int startX, int startZ, int width, int height, boolean dynamic) {
      this.hasNormals = normalGrid != null;
      this.hasColors = colorGrid != null;
      this.hasTexCoords = texCoordGrid != null;
      this.drawTriangles = false;
      byte stride = 3;
      if (this.hasNormals) {
         stride = 9;
      }

      if (this.hasColors) {
         stride <<= 2;
      }

      if (this.hasTexCoords) {
         stride <<= 1;
      }

      FloatBuffer vertBuf = BufferUtils.createFloatBuffer(width * height * stride);
      IntBuffer idxBuf = BufferUtils.createIntBuffer((width - 1) * (height - 1) << 2);
      this.indexCount = (width - 1) * (height - 1) << 2;

      for (int x = 0; x < width; x++) {
         for (int z = 0; z < height; z++) {
            vertBuf.put(new float[]{heightGrid[x + startX][z].x, heightGrid[x + startX][z + startZ].y, heightGrid[x + startX][z + startZ].z});
            if (this.hasTexCoords) {
               vertBuf.put(new float[]{((Coord)null).x, ((Coord)null).y});
            }

            if (this.hasNormals) {
               vertBuf.put(new float[]{((Point)null).x, ((Point)null).y, ((Point)null).z});
            }

            if (this.hasColors) {
               vertBuf.put(
                  new float[]{
                     colorGrid[x + startX][z + startZ].getRed() / 255.0F,
                     colorGrid[x + startX][z + startZ].getBlue() / 255.0F,
                     colorGrid[x + startX][z + startZ].getGreen() / 255.0F,
                     colorGrid[x + startX][z + startZ].getAlpha() / 255.0F
                  }
               );
            }
         }
      }

      for (int z = 0; z < height - 1; z++) {
         for (int x = 0; x < width - 1; x++) {
            idxBuf.put(x * height + z);
            idxBuf.put(x * height + z + 1);
            idxBuf.put(x * height + z + 1);
            idxBuf.put((x + 1) * height + z + 1);
         }
      }

      vertBuf.flip();
      idxBuf.flip();
      this.vertexBufferId = genBuffer();
      uploadFloatBuffer(this.vertexBufferId, vertBuf, dynamic);
      this.indexBufferId = genBuffer();
      uploadIntBuffer(this.indexBufferId, idxBuf, dynamic);
      this.ready = true;
   }

   public final void build(ArrayList<Vertex> vertices, ArrayList<Integer> indices, boolean hasNormals, boolean dynamic) {
      this.drawTriangles = false;
      this.build(vertices, indices, false, hasNormals, dynamic);
   }

   public final void build(ArrayList<Vertex> vertices, ArrayList<Integer> indices, boolean hasNormals, boolean hasColors, boolean dynamic) {
      this.hasTexCoords = true;
      this.hasColors = hasColors;
      this.hasNormals = hasNormals;
      int floatCount = vertices.size() * 3;
      this.floatsPerVertex = 3;
      floatCount <<= 1;
      this.floatsPerVertex += 2;
      if (this.hasNormals) {
         floatCount *= 3;
         this.floatsPerVertex += 3;
      }

      if (this.hasColors) {
         floatCount <<= 2;
         this.floatsPerVertex += 4;
      }

      FloatBuffer vertBuf = BufferUtils.createFloatBuffer(floatCount);
      IntBuffer idxBuf = BufferUtils.createIntBuffer(indices.size());
      this.indexCount = indices.size();

      for (int i = 0; i < vertices.size(); i++) {
         vertBuf.put(new float[]{vertices.get(i).position.x, vertices.get(i).position.y, vertices.get(i).position.z});
         vertBuf.put(new float[]{vertices.get(i).texCoord.x, vertices.get(i).texCoord.y});
         if (this.hasNormals) {
            vertBuf.put(new float[]{vertices.get(i).normal.x, vertices.get(i).normal.y, vertices.get(i).normal.z});
         }

         if (this.hasColors) {
            vertBuf.put(new float[]{vertices.get(i).color.r, vertices.get(i).color.g, vertices.get(i).color.b, vertices.get(i).color.alpha});
         }
      }

      for (int j = 0; j < indices.size(); j++) {
         idxBuf.put(indices.get(j));
      }

      vertBuf.flip();
      idxBuf.flip();
      if (dynamic) {
         this.vertexBuffer = vertBuf;
      }

      this.vertexBufferId = genBuffer();
      uploadFloatBuffer(this.vertexBufferId, vertBuf, dynamic);
      this.indexBufferId = genBuffer();
      uploadIntBuffer(this.indexBufferId, idxBuf, dynamic);
      this.ready = true;
   }

   public final void updatePositions(ArrayList<Vertex> vertices) {
      this.ready = false;

      for (int i = 0; i < vertices.size(); i++) {
         this.vertexBuffer.position(i * this.floatsPerVertex);
         this.vertexBuffer.put(new float[]{vertices.get(i).position.x, vertices.get(i).position.y, vertices.get(i).position.z});
      }

      this.vertexBuffer.flip();
      uploadFloatBuffer(this.vertexBufferId, this.vertexBuffer, true);
      this.ready = true;
   }

   public final void buildFromChunk(Chunk chunk) {
      FloatBuffer vertBuf = BufferUtils.createFloatBuffer((Chunk.SIZE + 1) * (Chunk.SIZE + 1) * 3 * 3 << 2 << 1);
      IntBuffer idxBuf = BufferUtils.createIntBuffer(Chunk.SIZE * Chunk.SIZE * 6);
      this.indexCount = Chunk.SIZE * Chunk.SIZE * 6;
      this.hasNormals = true;
      this.hasColors = true;
      this.hasTexCoords = true;
      this.drawTriangles = true;

      for (int x = 0; x <= Chunk.SIZE; x++) {
         for (int z = 0; z <= Chunk.SIZE; z++) {
            int wx = x * Chunk.TERRAIN_STEP;
            int wz = z * Chunk.TERRAIN_STEP;
            Point normal = chunk.getTerrainNormal(wx, wz);
            vertBuf.put(new float[]{wx, chunk.getHeight(wx, wz), wz});
            vertBuf.put(new float[]{wx / 32.0F, wz / 32.0F});
            vertBuf.put(new float[]{normal.x, normal.y, normal.z});
            vertBuf.put(new float[]{chunk.getTerrainColor(wx, wz).x, chunk.getTerrainColor(wx, wz).y, chunk.getTerrainColor(wx, wz).z, chunk.getTerrainColor(wx, wz).w});
         }
      }

      for (int x = 0; x < Chunk.SIZE; x++) {
         for (int z = 0; z < Chunk.SIZE; z++) {
            idxBuf.put(x * (Chunk.SIZE + 1) + z);
            idxBuf.put(x * (Chunk.SIZE + 1) + z + 1);
            idxBuf.put((x + 1) * (Chunk.SIZE + 1) + z + 1);
            idxBuf.put((x + 1) * (Chunk.SIZE + 1) + z + 1);
            idxBuf.put((x + 1) * (Chunk.SIZE + 1) + z);
            idxBuf.put(x * (Chunk.SIZE + 1) + z);
         }
      }

      vertBuf.flip();
      idxBuf.flip();
      this.vertexBufferId = genBuffer();
      uploadFloatBuffer(this.vertexBufferId, vertBuf, false);
      this.indexBufferId = genBuffer();
      uploadIntBuffer(this.indexBufferId, idxBuf, false);
      this.ready = true;
   }

   public final void render() {
      if (this.ready) {
         int stride = 3;
         if (this.hasTexCoords) {
            stride += 2;
         }

         if (this.hasNormals) {
            stride += 3;
         }

         if (this.hasColors) {
            stride += 4;
         }

         stride <<= 2;
         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vertexBufferId);
         GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
         GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
         if (this.hasTexCoords) {
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
         }

         if (this.hasNormals) {
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
         }

         if (this.hasColors) {
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
         }

         GL11.glVertexPointer(3, 5126, stride, 0L);
         byte offset = 12;
         if (this.hasTexCoords) {
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, stride, 12L);
            offset = 20;
         }

         if (this.hasNormals) {
            GL11.glNormalPointer(GL11.GL_FLOAT, stride, offset);
            offset += 12;
         }

         if (this.hasColors) {
            GL11.glColorPointer(4, GL11.GL_FLOAT, stride, offset);
         }

         if (this.drawTriangles) {
            GL11.glDrawElements(4, this.indexCount, GL11.GL_UNSIGNED_INT, 0L);
         } else {
            GL11.glDrawElements(1, this.indexCount, GL11.GL_UNSIGNED_INT, 0L);
         }

         if (this.hasColors) {
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
         }

         if (this.hasNormals) {
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
         }

         if (this.hasTexCoords) {
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
         }

         GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
      }
   }

   public final void dispose() {
      if (this.vertexBufferId != -1) {
         GL15.glDeleteBuffers(this.vertexBufferId);
      }

      if (this.indexBufferId != -1) {
         GL15.glDeleteBuffers(this.indexBufferId);
      }

      this.vertexBufferId = -1;
      this.indexBufferId = -1;
      this.ready = false;
   }

   public final boolean isReady() {
      return this.ready;
   }

   private static int genBuffer() {
      IntBuffer buf = BufferUtils.createIntBuffer(1);
      GL15.glGenBuffers(buf);
      return buf.get(0);
   }

   private static void uploadFloatBuffer(int bufferId, FloatBuffer data, boolean dynamic) {
      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
      if (!dynamic) {
         GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
      } else {
         GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW);
      }
   }

   private static void uploadIntBuffer(int bufferId, IntBuffer data, boolean dynamic) {
      GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, bufferId);
      if (!dynamic) {
         GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
      }

      GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, GL15.GL_DYNAMIC_DRAW);
   }
}
