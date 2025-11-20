package game.render;

import java.util.ArrayList;

public final class QuadVbo extends Vbo {
   public QuadVbo(ArrayList<Vertex> vertices) {
      this(vertices, false);
   }

   public QuadVbo(ArrayList<Vertex> vertices, boolean dynamic) {
      ArrayList<Integer> indices = new ArrayList<>();

      for (int q = 0; q < vertices.size() / 4; q++) {
         indices.add(q << 2);
         indices.add((q << 2) + 1);
         indices.add((q << 2) + 2);
         indices.add((q << 2) + 2);
         indices.add((q << 2) + 3);
         indices.add(q << 2);
      }

      this.build(vertices, indices, false, dynamic, false);
   }
}
