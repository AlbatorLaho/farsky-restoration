package game.render;

import java.util.ArrayList;

public final class SimpleVbo extends Vbo {
   public SimpleVbo(ArrayList<Vertex> vertices, boolean dynamic) {
      ArrayList<Integer> indices = new ArrayList<>();

      for (int i = 0; i < vertices.size(); i++) {
         indices.add(i);
      }

      this.build(vertices, indices, true, false);
   }
}
