package game.enemy;

import game.render.ModelLoader;
import game.render.Vbo;

public final class RockWallResources {
   @SuppressWarnings("unused")
   private static int texture;
   @SuppressWarnings("unused")
   private static Vbo rockWallMesh;
   @SuppressWarnings("unused")
   private static Vbo rockMesh;

   public static void loadResources() {
      rockWallMesh = ModelLoader.loadMesh("rockWall");
      texture = ModelLoader.loadTexture("rockWall");
      rockMesh = ModelLoader.loadMesh("rock0");
   }
}
