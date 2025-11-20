package game.gui.util;

public final class Rect {
   int x;
   int y;
   int width;
   int height;

   public Rect(float x, float y, float width, float height) {
      this((int)x, (int)y, (int)width, (int)height);
   }

   private Rect(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }
}
