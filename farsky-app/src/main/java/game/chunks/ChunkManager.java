package game.chunks;

import game.Main;
import game.collision.CollisionBox;
import game.environment.DepthAtmosphere;
import game.environment.Resource;
import game.inventory.ItemType;
import game.manager.Camera;
import game.manager.Loading;
import game.manager.TextureManager;
import game.util.Coord;
import game.util.Plan;
import game.util.Point;
import game.util.State;
import game.util.Segment;
import game.util.Vec4;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public final class ChunkManager {
   public static ArrayList<Chunk> activeChunks = new ArrayList<>();
   public static ArrayList<Chunk> pendingChunks = new ArrayList<>();
   public static ArrayList<Chunk> queuedChunks = new ArrayList<>();
   public static float viewDistance = DepthAtmosphere.getMaxFogDistance() + 50.0F;
   private static long lastUpdateTime = 0L;
   public static int pendingCount = 0;
   public static int activeCount = 0;

   public static void disposeAll() {
      for (int i = 0; i < queuedChunks.size(); i++) {
         activeChunks.get(i).dispose();
      }

      for (int i = 0; i < pendingChunks.size(); i++) {
         activeChunks.get(i).dispose();
      }

      for (int i = 0; i < activeChunks.size(); i++) {
         activeChunks.get(i).dispose();
      }

      activeChunks.clear();
      pendingChunks.clear();
      queuedChunks.clear();
   }

   public static void update(float deltaTime) {
      if (Main.isVerbose) {
         pendingCount = pendingChunks.size();
         activeCount = activeChunks.size();
      }

      if (System.currentTimeMillis() - lastUpdateTime > 500L) {
         viewDistance = DepthAtmosphere.getFogDistance() + 128.0F;
         ArrayList<Chunk> newChunks = Loading.worldManager.outgoingQueue.drain();
         queuedChunks.addAll(newChunks);

         for (int i = 0; i < newChunks.size(); i++) {
            int chunkX = newChunks.get(i).x;
            int chunkZ = newChunks.get(i).z;

            for (int j = 0; j < pendingChunks.size(); j++) {
               if (pendingChunks.get(j).x == chunkX && pendingChunks.get(j).z == chunkZ) {
                  pendingChunks.remove(j);
                  break;
               }
            }
         }

         for (int i = 0; i < activeChunks.size(); i++) {
            if (new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(activeChunks.get(i).x, activeChunks.get(i).z)) >= viewDistance
               && new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(activeChunks.get(i).x + 128, activeChunks.get(i).z)) >= viewDistance
               && new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(activeChunks.get(i).x + 128, activeChunks.get(i).z + 128)) >= viewDistance
               && new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(activeChunks.get(i).x, activeChunks.get(i).z + 128)) >= viewDistance) {
               activeChunks.get(i).dispose();
               activeChunks.remove(i);
            }
         }

         ArrayList<Float> distances = new ArrayList<>();
         int count = activeChunks.size();

         for (int i = 0; i < count; i++) {
            distances.add(new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(activeChunks.get(i).x + 64.0F, activeChunks.get(i).z + 64.0F)));
         }

         for (int pass = 0; pass < 6; pass++) {
            for (int j = 0; j < count - 1; j++) {
               if (distances.get(j) > distances.get(j + 1)) {
                  Chunk tmp = activeChunks.get(j);
                  activeChunks.set(j, activeChunks.get(j + 1));
                  activeChunks.set(j + 1, tmp);
                  float tmpDist = distances.get(j);
                  distances.set(j, distances.get(j + 1));
                  distances.set(j + 1, tmpDist);
               }
            }
         }

         requestChunks();
         lastUpdateTime = System.currentTimeMillis();
      }

      if (queuedChunks.size() > 0) {
         queuedChunks.get(0).build();
         activeChunks.add(queuedChunks.get(0));
         queuedChunks.remove(0);
      }

      for (int i = 0; i < activeChunks.size(); i++) {
         if (isChunkVisible(activeChunks.get(i))) {
            activeChunks.get(i).update(deltaTime);
         }
      }
   }

   public static void renderTerrain() {
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL12.GL_TEXTURE_3D, TextureManager.terrainTextures);
      GL13.glActiveTexture(GL13.GL_TEXTURE1);
      GL11.glBindTexture(GL12.GL_TEXTURE_3D, TextureManager.terrainNormals);
      GL13.glActiveTexture(GL13.GL_TEXTURE2);
      GL11.glBindTexture(GL12.GL_TEXTURE_3D, TextureManager.causticTextures);

      for (int i = 0; i < activeChunks.size(); i++) {
         if (isChunkVisible(activeChunks.get(i))) {
            activeChunks.get(i).renderTerrain();
         }
      }

      GL11.glDisable(GL12.GL_TEXTURE_3D);
   }

   public static void renderElements() {
      for (int i = 0; i < activeChunks.size(); i++) {
         if (isChunkVisible(activeChunks.get(i))) {
            activeChunks.get(i).renderElements();
         }
      }
   }

   public static void renderTransparent() {
      for (int i = activeChunks.size() - 1; i >= 0; i--) {
         if (isChunkVisible(activeChunks.get(i))) {
            activeChunks.get(i).renderTransparent();
         }
      }
   }

   public static State resolveCollision(State current, State result) {
      for (int i = 0; i < activeChunks.size(); i++) {
         result = activeChunks.get(i).resolveCollision(current, result);
      }

      return result;
   }

   public static void traceSegment(Segment segment) {
      for (int i = 0; i < activeChunks.size(); i++) {
         if (new Coord(activeChunks.get(i).x + 64, activeChunks.get(i).z + 64).distanceTo(segment.start.toCoord()) < 256.0F) {
            activeChunks.get(i).traceSegment(segment);
         }
      }
   }

   public static ItemType pickItem(Segment segment, boolean consume) {
      ItemType result = null;

      for (int i = 0; i < activeChunks.size(); i++) {
         if (new Coord(activeChunks.get(i).x + 64, activeChunks.get(i).z + 64).distanceTo(segment.start.toCoord()) < 256.0F) {
            result = activeChunks.get(i).pickItem(segment, consume);
            if (result != null) {
               return result;
            }
         }
      }

      return result;
   }

   public static ArrayList<CollisionBox> getCollisionBoxes() {
      ArrayList<CollisionBox> boxes = new ArrayList<>();

      for (int i = 0; i < activeChunks.size(); i++) {
         CollisionBox box = activeChunks.get(i).getCollisionBox();
         if (box != null) {
            boxes.add(box);
         }
      }

      return boxes;
   }

   private static void requestChunks() {
      if (Loading.worldManager.isReady()) {
         ArrayList<Chunk> chunksToLoad = new ArrayList<>();

         for (int chunkCol = (int)(Camera.getPosition().x + 64.0F - viewDistance) / 128 - 1; chunkCol < (int)(Camera.getPosition().x + 64.0F + viewDistance) / 128 + 1; chunkCol++) {
            for (int chunkRow = (int)(Camera.getPosition().z + 64.0F - viewDistance) / 128 - 1; chunkRow < (int)(Camera.getPosition().z + 64.0F + viewDistance) / 128 + 1; chunkRow++) {
               if (chunkCol >= 0 && chunkRow >= 0) {
                  int worldX = chunkCol << 7;
                  int worldZ = chunkRow << 7;
                  if (!isChunkLoaded(worldX, worldZ)
                     && (
                        new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(worldX, worldZ)) < viewDistance
                           || new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(worldX + 128, worldZ)) < viewDistance
                           || new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(worldX + 128, worldZ + 128)) < viewDistance
                           || new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(worldX, worldZ + 128)) < viewDistance
                     )) {
                     chunksToLoad.add(new Chunk(worldX, worldZ));
                  }
               }
            }
         }

         Loading.worldManager.incomingQueue.enqueue(chunksToLoad);
         pendingChunks.addAll(chunksToLoad);
      }
   }

   private static boolean isChunkLoaded(int worldX, int worldZ) {
      for (int i = 0; i < activeChunks.size(); i++) {
         if (activeChunks.get(i).x == worldX && activeChunks.get(i).z == worldZ) {
            return true;
         }
      }

      for (int i = 0; i < pendingChunks.size(); i++) {
         if (pendingChunks.get(i).x == worldX && pendingChunks.get(i).z == worldZ) {
            return true;
         }
      }

      for (int i = 0; i < queuedChunks.size(); i++) {
         if (queuedChunks.get(i).x == worldX && queuedChunks.get(i).z == worldZ) {
            return true;
         }
      }

      return false;
   }

   private static boolean isChunkVisible(Chunk chunk) {
      return new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(chunk.x, chunk.z)) < DepthAtmosphere.getFogDistance()
         || new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(chunk.x + 128, chunk.z)) < DepthAtmosphere.getFogDistance()
         || new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(chunk.x + 128, chunk.z + 128)) < DepthAtmosphere.getFogDistance()
         || new Coord(Camera.getPosition().x, Camera.getPosition().z).distanceTo(new Coord(chunk.x, chunk.z + 128)) < DepthAtmosphere.getFogDistance();
   }

   public static float getHeight(float x, float z) {
      return getHeight((int)x, (int)z);
   }

   public static float getHeight(int x, int z) {
      for (int i = 0; i < activeChunks.size(); i++) {
         if (activeChunks.get(i).x <= x && activeChunks.get(i).x + 128 > x && activeChunks.get(i).z <= z && activeChunks.get(i).z + 128 > z) {
            return activeChunks.get(i).getHeight(x % 128, z % 128);
         }
      }

      return Loading.worldManager.getTerrainHeightAt((float)x, (float)z);
   }

   public static Vec4 getTerrainColor(int x, int z) {
      for (int i = 0; i < activeChunks.size(); i++) {
         if (activeChunks.get(i).x <= x && activeChunks.get(i).x + 128 > x && activeChunks.get(i).z <= z && activeChunks.get(i).z + 128 > z) {
            return activeChunks.get(i).getTerrainColor(x % 128, z % 128);
         }
      }

      return new Vec4();
   }

   public static void rebuildLayerAt(int x, int z) {
      for (int i = 0; i < activeChunks.size(); i++) {
         if (activeChunks.get(i).x <= x && activeChunks.get(i).x + 128 > x && activeChunks.get(i).z <= z && activeChunks.get(i).z + 128 > z) {
            activeChunks.get(i).rebuildLayer();
         }
      }
   }

   public static Chunk.RockProperty getRockProperty(int x, int z) {
      for (int i = 0; i < activeChunks.size(); i++) {
         if (activeChunks.get(i).x <= x && activeChunks.get(i).x + 128 > x && activeChunks.get(i).z <= z && activeChunks.get(i).z + 128 > z) {
            return activeChunks.get(i).getRockProperty(x % 128, z % 128);
         }
      }

      return Chunk.RockProperty.PLAIN;
   }

   public static Point getTerrainNormal(int x, int z) {
      for (int i = 0; i < activeChunks.size(); i++) {
         if (activeChunks.get(i).x <= x && activeChunks.get(i).x + 128 > x && activeChunks.get(i).z <= z && activeChunks.get(i).z + 128 > z) {
            return activeChunks.get(i).getTerrainNormal(x % 128, z % 128);
         }
      }

      return Loading.worldManager.getTerrainNormalAt((float)x, (float)z);
   }

   public static ArrayList<Resource> getMineResources(int x, int z, int layer, Chunk.RockProperty rockProperty) {
      ArrayList<Resource> resources = new ArrayList<>();
      if (layer == 0) {
         resources.add(new Resource(ItemType.SAND, 90));
         resources.add(new Resource(ItemType.ROCK, 10));
      }

      if (layer == 1) {
         int orePercent = Math.min((int)(Loading.worldManager.getResourceCountAt(x, z) / 250.0F * 100.0F * 3.0F), 100);
         switch (rockProperty) {
            case SHALLOW:
               resources.add(new Resource(ItemType.IRON, orePercent));
               resources.add(new Resource(ItemType.ROCK, 100 - orePercent));
               break;
            case MIDWATER:
               resources.add(new Resource(ItemType.COPPER, orePercent));
               resources.add(new Resource(ItemType.ROCK, 100 - orePercent));
               break;
            case DEEP:
               resources.add(new Resource(ItemType.MANGANESE, orePercent));
               resources.add(new Resource(ItemType.ROCK, 100 - orePercent));
               break;
            default:
               resources.add(new Resource(ItemType.ROCK, 100));
         }
      }

      if (layer == 2) {
         resources.add(new Resource(ItemType.SAND, 50));
         resources.add(new Resource(ItemType.FERTILIZER, 40));
         resources.add(new Resource(ItemType.ROCK, 10));
      }

      if (layer == 3) {
         resources.add(new Resource(ItemType.SAND, 50));
         resources.add(new Resource(ItemType.FERTILIZER, 40));
         resources.add(new Resource(ItemType.ROCK, 10));
      }

      return resources;
   }

   public static void onOreMined(float x, float z, ItemType itemType) {
      if (itemType == ItemType.IRON || itemType == ItemType.COPPER || itemType == ItemType.MANGANESE) {
         Loading.worldManager.decrementResourceAt(x, z);
         int ix = (int)x;
         int iz = (int)z;

         for (int i = 0; i < activeChunks.size(); i++) {
            if (activeChunks.get(i).x <= ix && activeChunks.get(i).x + 128 > ix && activeChunks.get(i).z <= iz && activeChunks.get(i).z + 128 > iz) {
               activeChunks.get(i).decrementOre();
            }
         }
      }
   }

   public static float getHeightSmooth(float x, float z) {
      Point p0 = new Point((float)((int)(x / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP), getHeight((int)x, (int)z), (float)((int)(z / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP));
      Point p1 = new Point((float)((int)(x / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP + Chunk.TERRAIN_STEP), getHeight((int)x + Chunk.TERRAIN_STEP, (int)z), (float)((int)(z / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP));
      Point p2 = new Point((float)((int)(x / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP), getHeight((int)x, (int)z + Chunk.TERRAIN_STEP), (float)((int)(z / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP + Chunk.TERRAIN_STEP));
      Point normal = p1.minus(p0).cross(p2.minus(p0));
      normal.normalize();
      return new Plan(normal, p0).getYAt(x, z);
   }

   public static Point computeNormal(float x, float z) {
      Point p0 = new Point((float)((int)(x / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP), getHeight((int)x, (int)z), (float)((int)(z / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP));
      Point p1 = new Point((float)((int)(x / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP + Chunk.TERRAIN_STEP), getHeight((int)x + Chunk.TERRAIN_STEP, (int)z), (float)((int)(z / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP));
      Point normal = new Point((float)((int)(x / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP), getHeight((int)x, (int)z + Chunk.TERRAIN_STEP), (float)((int)(z / Chunk.TERRAIN_STEP) * Chunk.TERRAIN_STEP + Chunk.TERRAIN_STEP))
            .minus(p0)
            .cross(p1.minus(p0));
      normal.normalize();
      return normal;
   }
}
