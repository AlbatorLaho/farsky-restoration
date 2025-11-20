package game.render;

import game.util.Coord;
import game.util.Point;
import java.util.ArrayList;

public final class AnimatedMesh {
   private ArrayList<Vertex>[] frames;
   private ArrayList<Integer> indices;
   private Vbo vbo;
   private ArrayList<Vertex> blendBuffer = new ArrayList<>();
   private ArrayList<Vertex> fromVertices = new ArrayList<>();

   @SuppressWarnings("unchecked")
   public AnimatedMesh(int frameCount) {
      this.frames = (ArrayList<Vertex>[]) new ArrayList[frameCount];
      this.vbo = new Vbo();
   }

   @SuppressWarnings("unchecked")
   public final void loadFrame(int frameIndex, ArrayList<Point> positions, ArrayList<Coord> texCoords, ArrayList<Point> normals, ArrayList<int[][]> faces) {
      if (frameIndex == 0) {
         this.indices = new ArrayList<>();
      }

      this.frames[frameIndex] = new ArrayList<>();

      for (int f = 0; f < faces.size(); f++) {
         for (int v = 0; v < 3; v++) {
            int posIdx = faces.get(f)[v][0] - 1;
            int texIdx = faces.get(f)[v][1] - 1;
            int normIdx = faces.get(f)[v][2] - 1;
            int vertexListIdx = 0;
            this.frames[frameIndex].add(new Vertex(positions.get(posIdx), texCoords.get(texIdx), normals.get(normIdx)));
            vertexListIdx = this.frames[frameIndex].size() - 1;
            if (frameIndex == 0) {
               this.indices.add(vertexListIdx);
            }
         }
      }

      if (frameIndex == 0) {
         this.fromVertices = (ArrayList<Vertex>)this.frames[0].clone();
      }
   }

   public final void blendToFrame(int frameIndex, float t) {
      this.blendBuffer.clear();

      for (int i = 0; i < this.fromVertices.size(); i++) {
         ArrayList<Vertex> blendList = this.blendBuffer;
         Vertex fromVertex = this.fromVertices.get(i);
         Vertex toVertex = this.frames[frameIndex].get(i);
         Vertex blended = new Vertex();
         blended.position.x = fromVertex.position.x * (1.0F - t) + toVertex.position.x * t;
         blended.position.y = fromVertex.position.y * (1.0F - t) + toVertex.position.y * t;
         blended.position.z = fromVertex.position.z * (1.0F - t) + toVertex.position.z * t;
         blended.texCoord.x = fromVertex.texCoord.x * (1.0F - t) + toVertex.texCoord.x * t;
         blended.texCoord.y = fromVertex.texCoord.y * (1.0F - t) + toVertex.texCoord.y * t;
         blended.normal.x = fromVertex.normal.x * (1.0F - t) + toVertex.normal.x * t;
         blended.normal.y = fromVertex.normal.y * (1.0F - t) + toVertex.normal.y * t;
         blended.normal.z = fromVertex.normal.z * (1.0F - t) + toVertex.normal.z * t;
         blendList.add(blended);
      }

      if (this.blendBuffer.size() > 0) {
         this.vbo.updatePositions(this.blendBuffer);
      }
   }

   @SuppressWarnings("unchecked")
   public final void commitBlend() {
      this.fromVertices = (ArrayList<Vertex>)this.blendBuffer.clone();
   }

   public final void upload() {
      this.vbo.build(this.frames[0], this.indices, false, false, true);
   }

   public final void render() {
      this.vbo.render();
   }

   public final void freeVbo() {
      this.vbo.dispose();
   }
}
