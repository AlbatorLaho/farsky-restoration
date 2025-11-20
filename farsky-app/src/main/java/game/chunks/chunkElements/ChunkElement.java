package game.chunks.chunkElements;

import game.collision.AABB;
import game.inventory.ItemType;
import game.render.Vertex;
import game.util.Point;
import game.util.State;
import game.world.structure.TerrainSample;

import java.util.ArrayList;

public abstract class ChunkElement {
   protected Point position;

   public void update(float deltaTime) {
   }

   public void render() {
   }

   public State resolveCollision(State state, State result) {
      return null;
   }

   public void renderTransparent() {
   }

   public ArrayList<Vertex> buildVertices(TerrainSample[][] terrain) {
      return null;
   }

   public AABB getBoundingBox() {
      return null;
   }

   public AABB getLocalBoundingBox() {
      return null;
   }

   public ItemType harvest(boolean consume) {
      return null;
   }

   public void onUnload() {
   }

   public final Point getPosition() {
      return this.position;
   }
}
