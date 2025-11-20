package game.util;

import java.util.ArrayList;

import game.render.QuadVbo;
import game.render.Vertex;

public final class UnitQuad {
   private static QuadVbo quad = null;

   public static void render() {
      if (quad == null) {
         ArrayList<Vertex> vertices = new ArrayList<>();
         vertices.add(new Vertex(new Coord(-0.5F, -0.5F), new Coord(0, 1)));
         vertices.add(new Vertex(new Coord(0.5F, -0.5F), new Coord(1, 1)));
         vertices.add(new Vertex(new Coord(0.5F, 0.5F), new Coord(1, 0)));
         vertices.add(new Vertex(new Coord(-0.5F, 0.5F), new Coord(0, 0)));
         quad = new QuadVbo(vertices);
         vertices.clear();
      }

      quad.render();
   }
}
