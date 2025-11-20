package game.render;

import game.util.Color;
import game.util.Coord;
import game.util.Point;

public final class Vertex {
   public Point position;
   public Coord texCoord;
   public Point normal;
   public Color color;

   public Vertex() {
      this.position = new Point();
      this.texCoord = new Coord();
      this.normal = new Point();
      this.color = new Color();
   }

   public Vertex(Point position, Coord texCoord) {
      this(position, texCoord, new Point(), new Color());
   }

   public Vertex(Point position, Coord texCoord, Color color) {
      this(position, texCoord, new Point(), color);
   }

   public Vertex(Point position, Coord texCoord, Point normal) {
      this(position, texCoord, normal, new Color());
   }

   private Vertex(Point position, Coord texCoord, Point normal, Color color) {
      this.position = position;
      this.texCoord = texCoord;
      this.normal = normal;
      this.color = color;
   }

   @Override
   public final String toString() {
      return "Vertex: " + this.position + ", Texture: " + this.texCoord.x + "/" + this.texCoord.y + ", Normal: " + this.normal;
   }
}
