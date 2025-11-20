package game.chunks;

import game.render.Vbo;
import game.shader.Shaders;
import game.sounds.ChunkLayer;
import game.collision.AABB;
import game.collision.CollisionBox;
import game.inventory.ItemType;
import game.manager.Frustum;
import game.util.Coord;
import game.util.Point;
import game.util.Segment;
import game.util.State;
import game.util.Vec4;
import game.world.structure.TerrainSample;

import org.lwjgl.opengl.GL11;

public final class Chunk {
   public static enum RockProperty {
      PLAIN,
      SHALLOW,
      MIDWATER,
      DEEP;
   }

   public static int SIZE = 64;
   public static int TERRAIN_STEP = 128 / SIZE;
   public static boolean debugBounds = false;
   public boolean debugHighlight = false;
   public int x;
   public int z;
   private TerrainSample[][] terrain = new TerrainSample[SIZE + 1][SIZE + 1];
   private AABB bounds;
   private AABB extendedBounds;
   private Vbo vbo = new Vbo();
   private int oreCount;
   private float oreRatio;
   private ChunkLayer soundLayer = new ChunkLayer();
   private RockProperty rockProperty = RockProperty.PLAIN;
   private boolean visible = false;

   public Chunk(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public final void init(TerrainSample[][] terrain, ChunkLayer soundLayer, RockProperty rockProperty, int oreCount) {
      this.terrain = terrain;
      this.soundLayer = soundLayer;
      this.rockProperty = rockProperty;
      this.oreCount = oreCount;
      this.oreRatio = oreCount / 250;
      float minHeight = terrain[0][0].height;
      float maxHeight = terrain[0][0].height;

      for (int xi = 0; xi <= SIZE; xi++) {
         for (int zi = 0; zi <= SIZE; zi++) {
            if (minHeight > terrain[xi][zi].height) {
               minHeight = terrain[xi][zi].height;
            }

            if (maxHeight < terrain[xi][zi].height) {
               maxHeight = terrain[xi][zi].height;
            }
         }
      }

      if (maxHeight - minHeight < 10.0F) {
         minHeight = maxHeight - 10.0F;
      }

      this.bounds = new AABB(new Point(this.x + 64.0F, (maxHeight + minHeight) / 2.0F, this.z + 64.0F), 128.0F, maxHeight - minHeight, 128.0F);
      this.extendedBounds = soundLayer.getBoundingBox();
      if (this.extendedBounds != null) {
         this.extendedBounds.translate(new Point((float)this.x, 0.0F, (float)this.z));
      }

      this.extendedBounds = this.bounds.union(this.extendedBounds);
   }

   public final void build() {
      this.soundLayer.buildVbos(this.terrain);
      this.vbo.buildFromChunk(this);
   }

   public final void dispose() {
      this.soundLayer.unload();
      this.vbo.dispose();
   }

   public final void update(float deltaTime) {
      this.oreRatio = this.oreCount / 250.0F * 3.0F;
      this.soundLayer.update(deltaTime, new Coord(this.x, this.z));
   }

   public final void renderTerrain() {
      this.visible = this.vbo.isReady() && Frustum.isVisible(this.extendedBounds);
      if (this.visible) {
         Shaders.setUniform("alphaColor", new Point());
         switch (this.rockProperty) {
            case SHALLOW:
               Shaders.setUniform("alphaColor", new Point(0.9F, 0.9F, 1.0F).scaled(this.oreRatio));
               break;
            case MIDWATER:
               Shaders.setUniform("alphaColor", new Point(1.0F, 0.6F, 0.3F).scaled(this.oreRatio));
               break;
            case DEEP:
               Shaders.setUniform("alphaColor", new Point(0.2F, 0.7F, 1.0F).scaled(this.oreRatio));
			default:
				break;
         }

         GL11.glTranslatef(this.x, 0.0F, this.z);
         this.vbo.render();
         GL11.glTranslatef(-this.x, 0.0F, -this.z);
         if (debugBounds) {
            if (this.debugHighlight) {
               GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.0F);
            } else {
               GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.0F);
            }

            this.bounds.debugDraw(false);
         }
      }
   }

   public final void renderElements() {
      if (this.visible) {
         GL11.glTranslatef(this.x, 0.0F, this.z);
         this.soundLayer.render();
         GL11.glTranslatef(-this.x, 0.0F, -this.z);
      }
   }

   public final void renderTransparent() {
      if (this.visible) {
         GL11.glTranslatef(this.x, 0.0F, this.z);
         this.soundLayer.renderTransparent();
         GL11.glTranslatef(-this.x, 0.0F, -this.z);
      }
   }

   public final State resolveCollision(State current, State result) {
      current.pos = current.pos.minus(this.x, 0.0F, this.z);
      result.pos = result.pos.minus(this.x, 0.0F, this.z);
      State resolved = this.soundLayer.resolveCollision(current, result);
      resolved.pos.add(this.x, 0.0F, this.z);
      current.pos.add(this.x, 0.0F, this.z);
      return resolved;
   }

   public final void traceSegment(Segment segment) {
      this.soundLayer.harvestPlants(this.x, this.z, segment);
   }

   public final ItemType pickItem(Segment segment, boolean consume) {
      return this.soundLayer.harvestOre(this.x, this.z, segment, consume);
   }

   public final CollisionBox getCollisionBox() {
      AABB aabb = this.soundLayer.getStructureBoundingBox();
      return aabb != null ? new CollisionBox(aabb, new Point((float)this.x, 0.0F, (float)this.z), new Point()) : null;
   }

   public final Vec4 getTerrainColor(int localX, int localZ) {
      return localX >= 0 && localX <= 128 && localZ >= 0 && localZ <= 128 ? this.terrain[localX / TERRAIN_STEP][localZ / TERRAIN_STEP].color : ChunkManager.getTerrainColor(localX + this.x, localZ + this.z);
   }

   public final float getHeight(int localX, int localZ) {
      return localX >= 0 && localX <= 128 && localZ >= 0 && localZ <= 128 ? this.terrain[localX / TERRAIN_STEP][localZ / TERRAIN_STEP].height : ChunkManager.getHeight(localX + this.x, localZ + this.z);
   }

   public final Point getTerrainNormal(int localX, int localZ) {
      return localX >= 0 && localX <= 128 && localZ >= 0 && localZ <= 128 ? this.terrain[localX / TERRAIN_STEP][localZ / TERRAIN_STEP].normal : ChunkManager.getTerrainNormal(localX + this.x, localZ + this.z);
   }

   public final RockProperty getRockProperty(int localX, int localZ) {
      return localX >= 0 && localX <= 128 && localZ >= 0 && localZ <= 128 ? this.rockProperty : ChunkManager.getRockProperty(localX + this.x, localZ + this.z);
   }

   public final AABB getBounds() {
      AABB copy = new AABB();
      copy.copyFrom(this.bounds);
      return copy;
   }

   public final void decrementOre() {
      this.oreCount--;
   }

   public final void rebuildLayer() {
      this.soundLayer.harvestTerrainOverlay();
   }
}
