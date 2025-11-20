package game.render;

import game.Main;
import game.exceptions.TextureWidthException;
import game.manager.TextureManager;
import game.util.Coord;
import game.util.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.newdawn.slick.util.ResourceLoader;

public final class ModelLoader {
   private static ArrayList<String> textureNames = new ArrayList<>();
   private static ArrayList<Integer> textureIds = new ArrayList<>();
   private static ArrayList<Point> positions;
   private static ArrayList<Coord> texCoords;
   private static ArrayList<Point> normals;
   private static ArrayList<int[][]> faces;

   private static boolean isTextureCached(String name) {
      for (int i = 0; i < textureNames.size(); i++) {
         if (textureNames.get(i).equals(name)) {
            return true;
         }
      }

      return false;
   }

   private static int getCachedTextureId(String name) {
      for (int i = 0; i < textureNames.size(); i++) {
         if (textureNames.get(i).equals(name)) {
            return textureIds.get(i);
         }
      }

      return 0;
   }

   public static Vbo loadMesh(String name) {
      return loadMesh(name, name);
   }

   public static Vbo loadMesh(String dirName, String fileName) {
      Vbo vbo = new Vbo();
      positions = new ArrayList<>();
      texCoords = new ArrayList<>();
      normals = new ArrayList<>();
      faces = new ArrayList<>();
      loadObjFile("obj/" + dirName + "/" + fileName + ".obj");
      ArrayList<Integer> indices = new ArrayList<>();
      ArrayList<Vertex> vertices = new ArrayList<>();

      for (int f = 0; f < faces.size(); f++) {
         for (int v = 0; v < 3; v++) {
            int posIdx = faces.get(f)[v][0] - 1;
            int texIdx = faces.get(f)[v][1] - 1;
            int normIdx = faces.get(f)[v][2] - 1;
            int existingIdx = -1;
            Vertex vertex = new Vertex(positions.get(posIdx), new Coord(1.0F - texCoords.get(texIdx).x, 1.0F - texCoords.get(texIdx).y), normals.get(normIdx));

            for (int j = 0; j < vertices.size(); j++) {
               Vertex existing = vertices.get(j);
               if (vertex.position.equals(existing.position) && vertex.texCoord.x == existing.texCoord.x && vertex.texCoord.y == existing.texCoord.y && vertex.normal.equals(existing.normal)) {
                  existingIdx = j;
                  break;
               }
            }

            if (existingIdx == -1) {
               vertices.add(vertex);
               existingIdx = vertices.size() - 1;
            }

            indices.add(existingIdx);
         }
      }

      vbo.build(vertices, indices, true, false, false);
      return vbo;
   }

   public static AnimatedMesh loadAnimatedMesh(String name, int frameCount) {
      AnimatedMesh mesh = new AnimatedMesh(frameCount);

      for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
         positions = new ArrayList<>();
         texCoords = new ArrayList<>();
         normals = new ArrayList<>();
         faces = new ArrayList<>();
         if (frameIndex < 10) {
            loadObjFile("obj/" + name + "/" + name + "_00000" + frameIndex + ".obj");
         } else {
            loadObjFile("obj/" + name + "/" + name + "_0000" + frameIndex + ".obj");
         }

         mesh.loadFrame(frameIndex, positions, texCoords, normals, faces);
      }

      mesh.upload();
      return mesh;
   }

   private static void loadObjFile(String path) {
      InputStream stream = ResourceLoader.getResourceAsStream(path);
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

      try {
         String line;
         while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s");
            if (parts[0].matches("v")) {
               positions.add(new Point(Float.valueOf(parts[1]), Float.valueOf(parts[2]), Float.valueOf(parts[3])));
            }

            if (parts[0].matches("vt")) {
               texCoords.add(new Coord(Float.valueOf(parts[1]), Float.valueOf(parts[2])));
            }

            if (parts[0].matches("vn")) {
               normals.add(new Point(Float.valueOf(parts[1]), Float.valueOf(parts[2]), Float.valueOf(parts[3])));
            }

            if (parts[0].matches("f")) {
               int[][] faceIndices = new int[3][3];

               for (int v = 0; v < 3; v++) {
                  String[] components = parts[v + 1].split("/");
                  faceIndices[v][0] = Integer.parseInt(components[0]);
                  faceIndices[v][1] = Integer.parseInt(components[1]);
                  faceIndices[v][2] = Integer.parseInt(components[2]);
               }

               faces.add(faceIndices);
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static int loadTexture(String name) {
      boolean mipmap = true;
      boolean flip = false;
      return loadOrCacheTexture(name, name, flip, mipmap);
   }

   public static int loadTexture(String dirName, String fileName) {
      return loadOrCacheTexture(dirName, fileName, false, true);
   }

   public static int loadTexture(String name, boolean flip, boolean mipmap) {
      return loadOrCacheTexture(name, name, flip, mipmap);
   }

   private static int loadOrCacheTexture(String dirName, String fileName, boolean flip, boolean mipmap) {
      if (isTextureCached(dirName + "T" + fileName)) {
         return getCachedTextureId(dirName + "T" + fileName);
      } else {
         String texturePath = "obj/" + dirName + "/T" + fileName + ".png";

         int textureId;
         try {
            textureId = TextureManager.loadTexture(texturePath, true, false, flip, mipmap);
         } catch (TextureWidthException e) {
            textureId = 0;
         }

         dirName = dirName + "T" + fileName;
         textureNames.add(dirName);
         textureIds.add(textureId);
         return textureId;
      }
   }
}
