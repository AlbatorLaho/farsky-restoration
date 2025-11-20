package game.seafloorBase;

import game.manager.GameTime;
import game.render.ModelLoader;
import game.shader.Shaders;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class BaseModels {
   public static int wall;
   public static int roof;
   public static int glassWall;
   public static int floor;
   public static int top;
   public static int hatch;
   public static int ladder;
   public static int outsidePillar;
   public static int workshopTable;
   public static int equipmentTable;
   public static int weaponTable;
   public static int buildingTable;
   public static int furnitureTable;
   public static int plantPot;
   public static int chestSmall;
   public static int table;
   public static int door;
   public static int cooker;
   public static int stool;
   public static int glass;
   private static int whiteTexture;
   public static int water;
   public static int textureCount = 22;

   public static void loadTextures() {
      wall = ModelLoader.loadTexture("seafloorBase", "wall");
      roof = ModelLoader.loadTexture("seafloorBase", "roof");
      glassWall = ModelLoader.loadTexture("seafloorBase", "glassWall");
      floor = ModelLoader.loadTexture("seafloorBase", "floor");
      top = ModelLoader.loadTexture("seafloorBase", "top");
      hatch = ModelLoader.loadTexture("seafloorBase", "hatch");
      ladder = ModelLoader.loadTexture("seafloorBase", "ladder");
      outsidePillar = ModelLoader.loadTexture("seafloorBase", "outsidePillar");
      workshopTable = ModelLoader.loadTexture("seafloorBase", "workshopTable");
      equipmentTable = ModelLoader.loadTexture("seafloorBase", "equipmentTable");
      weaponTable = ModelLoader.loadTexture("seafloorBase", "weaponTable");
      buildingTable = ModelLoader.loadTexture("seafloorBase", "buildingTable");
      furnitureTable = ModelLoader.loadTexture("seafloorBase", "furnitureTable");
      plantPot = ModelLoader.loadTexture("seafloorBase", "plantPot");
      chestSmall = ModelLoader.loadTexture("seafloorBase", "chestSmall");
      table = ModelLoader.loadTexture("seafloorBase", "table");
      door = ModelLoader.loadTexture("seafloorBase", "door");
      cooker = ModelLoader.loadTexture("seafloorBase", "cooker");
      stool = ModelLoader.loadTexture("seafloorBase", "stool");
      glass = ModelLoader.loadTexture("seafloorBase", "glass");
      whiteTexture = ModelLoader.loadTexture("seafloorBase", "white");
      water = ModelLoader.loadTexture("seafloorBase", "water");
   }

   public static int getTextureId(int index) {
      if (index == 0) {
         return wall;
      } else if (index == 1) {
         return roof;
      } else if (index == 2) {
         return glassWall;
      } else if (index == 3) {
         return floor;
      } else if (index == 4) {
         return top;
      } else if (index == 5) {
         return hatch;
      } else if (index == 6) {
         return ladder;
      } else if (index == 7) {
         return outsidePillar;
      } else if (index == 8) {
         return workshopTable;
      } else if (index == 9) {
         return equipmentTable;
      } else if (index == 10) {
         return weaponTable;
      } else if (index == 11) {
         return buildingTable;
      } else if (index == 12) {
         return furnitureTable;
      } else if (index == 13) {
         return plantPot;
      } else if (index == 14) {
         return chestSmall;
      } else if (index == 15) {
         return table;
      } else if (index == 16) {
         return door;
      } else if (index == 17) {
         return cooker;
      } else if (index == 18) {
         return stool;
      } else if (index == 19) {
         return glass;
      } else if (index == 20) {
         return whiteTexture;
      } else {
         return index == 21 ? water : 0;
      }
   }

   public static boolean isAlphaTexture(int index) {
      return getTextureId(index) == glass || getTextureId(index) == whiteTexture || getTextureId(index) == water;
   }

   public static void bindTexture(int textureIdx, boolean depthMask, float lightMult) {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId(textureIdx));
      Shaders.setUniform("emissive", false);
      if (getTextureId(textureIdx) == wall) {
         GL11.glDepthMask(true);
         GL11.glEnable(GL11.GL_CULL_FACE);
         float wallPulse = ((float)Math.cos(GameTime.elapsedMillis / 300.0F) * 0.5F + 0.5F) * 0.15F + 0.95F;
         Shaders.setUniform("lightColor", new Point(wallPulse * 0.5F, wallPulse * 0.5F, wallPulse * 0.3F).scaled(lightMult * 4.0F));
         Shaders.setUniform("water", false);
         Shaders.setUniform("alphaLight", true);
      } else if (getTextureId(textureIdx) == plantPot) {
         GL11.glDepthMask(true);
         GL11.glEnable(GL11.GL_CULL_FACE);
         float plantPulse = ((float)Math.cos(GameTime.elapsedMillis / 1500.0F) * 0.5F + 0.5F) * 0.3F + 0.4F;
         Shaders.setUniform("lightColor", new Point(plantPulse, plantPulse * 0.9F, plantPulse * 0.75F));
         Shaders.setUniform("water", false);
         Shaders.setUniform("alphaLight", true);
      } else if (getTextureId(textureIdx) == cooker) {
         GL11.glDepthMask(true);
         GL11.glEnable(GL11.GL_CULL_FACE);
         Shaders.setUniform("water", false);
         Shaders.setUniform("alphaLight", true);
      } else if (getTextureId(textureIdx) == whiteTexture || getTextureId(textureIdx) == glass) {
         GL11.glDisable(GL11.GL_CULL_FACE);
         if (!depthMask) {
            GL11.glDepthMask(false);
         }

         Shaders.setUniform("water", false);
         Shaders.setUniform("alphaLight", false);
      } else if (getTextureId(textureIdx) == water) {
         GL11.glDisable(GL11.GL_CULL_FACE);
         Shaders.setUniform("water", true);
         Shaders.setUniform("alphaLight", false);
      } else {
         GL11.glDepthMask(true);
         GL11.glEnable(GL11.GL_CULL_FACE);
         Shaders.setUniform("water", false);
         Shaders.setUniform("alphaLight", false);
      }
   }

   public static void resetShaderState() {
      Shaders.setUniform("alphaLight", false);
      Shaders.setUniform("water", false);
      GL11.glDepthMask(true);
      GL11.glEnable(GL11.GL_CULL_FACE);
   }
}
