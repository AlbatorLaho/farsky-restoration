package game.seafloorBase;

import game.collision.AABB;
import game.seafloorBase.util.BlockType;
import game.seafloorBase.util.Dir;
import game.seafloorBase.util.Material;
import game.seafloorBase.util.Neighbor;
import game.util.Point;
import game.util.State;
import java.io.Serializable;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public class Block implements Serializable {
   private static final long serialVersionUID = 1105951845076145794L;
   private boolean built = false;
   private ArrayList<Element> elmts;
   private Point offset;
   public transient boolean visited = false;
   private transient ArrayList<Element> pendingWalls;

   public Block(Point pos) {
      this.elmts = new ArrayList<>();
      pos.x *= 10.0F;
      pos.y *= 35.0F;
      pos.z *= 10.0F;
      this.offset = pos.copy();
   }

   public final void build() {
      this.elmts.clear();
      if ((int)this.offset.x / 10.0F > -16.0F
         && (int)this.offset.x / 10.0F < 15.0F
         && (int)this.offset.z / 10.0F > -16.0F
         && (int)this.offset.z / 10.0F < 15.0F) {
         if (!this.hasElement(BlockType.FLOOR)) {
            this.elmts.add(new Element(BlockType.FLOOR));
         }

         this.built = true;
      }
   }

   public final void demolish() {
      this.elmts.clear();
      this.built = false;
   }

   public final void clearElements() {
      if (this.built) {
         this.elmts.clear();
      }
   }

   public final boolean addElement(BlockType type, Block[] neighbors, boolean dryRun) {
      return this.addElement(type, Dir.NORTH, neighbors, false);
   }

   public final boolean addElement(BlockType type, Dir dir, Block[] neighbors, boolean dryRun) {
      Element element = null;
      if (this.hasElement(type)) {
         return false;
      } else {
         switch (type) {
            case SEABED:
               element = new Element(type);
               break;
            case LADDER:
               if (this.hasElement(BlockType.FLOOR)) {
                  if (this.built && neighbors[dir.neighborIndex()].canHaveWall()) {
                     element = new Element(type, dir);
                  } else if (this.built && neighbors[Neighbor.NORTH.getIndex()].canHaveWall()) {
                     element = new Element(type, Dir.NORTH);
                  } else if (this.built && neighbors[Neighbor.EAST.getIndex()].canHaveWall()) {
                     element = new Element(type, Dir.EAST);
                  } else if (this.built && neighbors[Neighbor.SOUTH.getIndex()].canHaveWall()) {
                     element = new Element(type, Dir.SOUTH);
                  } else if (this.built && neighbors[Neighbor.WEST.getIndex()].canHaveWall()) {
                     element = new Element(type, Dir.WEST);
                  }
               }
               break;
            default:
               if (this.hasElement(BlockType.FLOOR)) {
                  int i = 0;

                  boolean hasBlockSized;
                  while (true) {
                     if (i >= this.elmts.size()) {
                        hasBlockSized = false;
                        break;
                     }

                     if (this.elmts.get(i).getBlockType().isBlockSized()) {
                        hasBlockSized = true;
                        break;
                     }

                     i++;
                  }

                  if (!hasBlockSized) {
                     element = new Element(type, dir);
                  }
               }
         }

         if (element != null && !dryRun) {
            this.elmts.add(element);
         }

         return element != null;
      }
   }

   public final boolean applyMaterial(Material mat, boolean dryRun) {
      Element target = null;

      for (int i = this.elmts.size() - 1; i >= 0; i--) {
         if (mat == Material.GLASS && this.elmts.get(i).getBlockType() == BlockType.WALL && this.elmts.get(i).getMaterial() != Material.GLASS) {
            target = this.elmts.get(i);
         }
      }

      if (target != null && !dryRun) {
         target.setMaterial(mat);
      }

      return target != null;
   }

   public final void renderElementsWithTexture(int textureId, boolean insideView, boolean showWater) {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).getTextureId(insideView) == textureId && (this.elmts.get(i).getBlockType() != BlockType.WATER_LEVEL || showWater || !this.hasWall())) {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.offset.x, this.offset.y, this.offset.z);
            this.elmts.get(i).renderStatic(insideView);
            GL11.glPopMatrix();
         }
      }
   }

   public final boolean hasElementWithTexture(int textureId, boolean insideView) {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).getTextureId(insideView) == textureId) {
            return true;
         }
      }

      return false;
   }

   public final void renderAnimated() {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).isAnimated()) {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.offset.x, this.offset.y, this.offset.z);
            this.elmts.get(i).renderAnimated();
            GL11.glPopMatrix();
         }
      }
   }

   public final void renderLeakEffects() {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).hasWaterLeak()) {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.offset.x, this.offset.y, this.offset.z);
            this.elmts.get(i).renderLeakEffect();
            GL11.glPopMatrix();
         }
      }
   }

   public final void prepareUpdate() {
      this.pendingWalls = new ArrayList<>();
      if (this.hasElement(BlockType.SEABED)) {
         this.demolish();
      }
   }

   public final void updateFromNeighbors(Block[] neighbors) {
      if (!this.built) {
         ArrayList<Element> newWalls = new ArrayList<>();
         if (neighbors[Neighbor.EAST.getIndex()].built && !neighbors[Neighbor.SOUTH.getIndex()].built && !neighbors[Neighbor.NORTH.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL, Dir.EAST));
         }

         if (neighbors[Neighbor.WEST.getIndex()].built && !neighbors[Neighbor.SOUTH.getIndex()].built && !neighbors[Neighbor.NORTH.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL, Dir.WEST));
         }

         if (neighbors[Neighbor.SOUTH.getIndex()].built && !neighbors[Neighbor.WEST.getIndex()].built && !neighbors[Neighbor.EAST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL, Dir.SOUTH));
         }

         if (neighbors[Neighbor.NORTH.getIndex()].built && !neighbors[Neighbor.WEST.getIndex()].built && !neighbors[Neighbor.EAST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL, Dir.NORTH));
         }

         if (neighbors[Neighbor.NORTHEAST.getIndex()].built && !neighbors[Neighbor.NORTH.getIndex()].built && !neighbors[Neighbor.EAST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_DIAGONAL, Dir.EAST));
         }

         if (neighbors[Neighbor.SOUTHEAST.getIndex()].built && !neighbors[Neighbor.SOUTH.getIndex()].built && !neighbors[Neighbor.EAST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_DIAGONAL, Dir.SOUTH));
         }

         if (neighbors[Neighbor.SOUTHWEST.getIndex()].built && !neighbors[Neighbor.SOUTH.getIndex()].built && !neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_DIAGONAL, Dir.WEST));
         }

         if (neighbors[Neighbor.NORTHWEST.getIndex()].built && !neighbors[Neighbor.NORTH.getIndex()].built && !neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_DIAGONAL, Dir.NORTH));
         }

         if (neighbors[Neighbor.NORTH.getIndex()].built && neighbors[Neighbor.EAST.getIndex()].built && !neighbors[Neighbor.SOUTH.getIndex()].built && !neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_CORNER, Dir.EAST));
         }

         if (!neighbors[Neighbor.NORTH.getIndex()].built && neighbors[Neighbor.EAST.getIndex()].built && neighbors[Neighbor.SOUTH.getIndex()].built && !neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_CORNER, Dir.SOUTH));
         }

         if (!neighbors[Neighbor.NORTH.getIndex()].built && !neighbors[Neighbor.EAST.getIndex()].built && neighbors[Neighbor.SOUTH.getIndex()].built && neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_CORNER, Dir.WEST));
         }

         if (neighbors[Neighbor.NORTH.getIndex()].built && !neighbors[Neighbor.EAST.getIndex()].built && !neighbors[Neighbor.SOUTH.getIndex()].built && neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_CORNER, Dir.NORTH));
         }

         if (!neighbors[Neighbor.NORTH.getIndex()].built && neighbors[Neighbor.EAST.getIndex()].built && neighbors[Neighbor.SOUTH.getIndex()].built && neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_T, Dir.SOUTH));
         }

         if (neighbors[Neighbor.NORTH.getIndex()].built && !neighbors[Neighbor.EAST.getIndex()].built && neighbors[Neighbor.SOUTH.getIndex()].built && neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_T, Dir.WEST));
         }

         if (neighbors[Neighbor.NORTH.getIndex()].built && neighbors[Neighbor.EAST.getIndex()].built && !neighbors[Neighbor.SOUTH.getIndex()].built && neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_T, Dir.NORTH));
         }

         if (neighbors[Neighbor.NORTH.getIndex()].built && neighbors[Neighbor.EAST.getIndex()].built && neighbors[Neighbor.SOUTH.getIndex()].built && !neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_T, Dir.EAST));
         }

         if (neighbors[Neighbor.NORTH.getIndex()].built && neighbors[Neighbor.EAST.getIndex()].built && neighbors[Neighbor.SOUTH.getIndex()].built && neighbors[Neighbor.WEST.getIndex()].built) {
            newWalls.add(new Element(BlockType.WALL_CROSS));
         }

         this.pendingWalls.addAll(newWalls);
      }

      for (int i = this.elmts.size() - 1; i >= 0; i--) {
         Element existing = this.elmts.get(i);
         if (this.hasWall()
            && (existing.getBlockType() == BlockType.WALL || existing.getBlockType() == BlockType.WALL_DIAGONAL || existing.getBlockType() == BlockType.WALL_CORNER || existing.getBlockType() == BlockType.WALL_T)) {
            Element existing2 = this.elmts.get(i);
            if (existing2 == null ? true : !this.isPendingWall(existing2.getBlockType(), existing2.getDir())) {
               this.demolish();
               break;
            }
         }
      }

      for (int j = 0; j < this.pendingWalls.size(); j++) {
         Element pending = this.pendingWalls.get(j);
         if (pending == null ? true : !this.hasElement(pending.getBlockType(), pending.getDir())) {
            this.elmts.add(this.pendingWalls.get(j));
         }
      }

      this.pendingWalls.clear();
      this.removeAllOfType(BlockType.SOFFIT);
      this.removeAllOfType(BlockType.ROOF);
      if (!this.built) {
         if (neighbors[Neighbor.UP.getIndex()].built && neighbors[Neighbor.UP.getIndex()].hasElement(BlockType.FLOOR) || neighbors[Neighbor.UP.getIndex()].hasElement(BlockType.WALL_CROSS)) {
            this.elmts.add(new Element(BlockType.SOFFIT));
         }

         if (neighbors[Neighbor.DOWN.getIndex()].built || neighbors[Neighbor.DOWN.getIndex()].hasElement(BlockType.WALL_CROSS)) {
            this.elmts.add(new Element(BlockType.ROOF));
         }
      }

      if (this.built && (!neighbors[Neighbor.UP.getIndex()].built || neighbors[Neighbor.UP.getIndex()].hasElement(BlockType.FLOOR))) {
         if (!this.hasElement(BlockType.CEILING)) {
            this.elmts.add(new Element(BlockType.CEILING));
         }
      } else {
         this.removeAllOfType(BlockType.CEILING);
      }

      if (this.getElement(BlockType.WATER_LEVEL) == null) {
         this.elmts.add(new Element(BlockType.WATER_LEVEL));
      }

      if (!this.built && !this.hasElement(BlockType.WALL_CROSS) && !this.hasWall()) {
         this.removeAllOfType(BlockType.WATER_LEVEL);
      }

      if (this.hasElement(BlockType.WATER_LEVEL)) {
         float waterLevel = 0.0F;
         if (neighbors[Neighbor.SOUTH.getIndex()].hasElement(BlockType.WATER_LEVEL)) {
            waterLevel = Math.max(neighbors[Neighbor.SOUTH.getIndex()].getElement(BlockType.WATER_LEVEL).getParam(), 0.0F);
         }

         if (neighbors[Neighbor.WEST.getIndex()].hasElement(BlockType.WATER_LEVEL)) {
            waterLevel = Math.max(neighbors[Neighbor.WEST.getIndex()].getElement(BlockType.WATER_LEVEL).getParam(), waterLevel);
         }

         if (neighbors[Neighbor.NORTH.getIndex()].hasElement(BlockType.WATER_LEVEL)) {
            waterLevel = Math.max(neighbors[Neighbor.NORTH.getIndex()].getElement(BlockType.WATER_LEVEL).getParam(), waterLevel);
         }

         if (neighbors[Neighbor.EAST.getIndex()].hasElement(BlockType.WATER_LEVEL)) {
            waterLevel = Math.max(neighbors[Neighbor.EAST.getIndex()].getElement(BlockType.WATER_LEVEL).getParam(), waterLevel);
         }

         if (!this.hasElement(BlockType.WATER_LEVEL)) {
            this.elmts.add(new Element(BlockType.WATER_LEVEL));
         }

         this.getElement(BlockType.WATER_LEVEL).setParam(waterLevel);
      }

      if (this.hasElement(BlockType.WATER_LEVEL)) {
         if (!neighbors[Neighbor.DOWN.getIndex()].built) {
            this.getElement(BlockType.WATER_LEVEL).setDrawWaterLevel0(true);
         } else {
            this.getElement(BlockType.WATER_LEVEL).setDrawWaterLevel0(false);
         }
      }

      this.removeAllOfType(BlockType.HOLE);
      if (this.hasElement(BlockType.FLOOR)) {
         if (!neighbors[Neighbor.NORTH.getIndex()].hasElement(BlockType.FLOOR)) {
            this.elmts.add(new Element(BlockType.HOLE, Dir.NORTH));
         }

         if (!neighbors[Neighbor.EAST.getIndex()].hasElement(BlockType.FLOOR)) {
            this.elmts.add(new Element(BlockType.HOLE, Dir.EAST));
         }

         if (!neighbors[Neighbor.SOUTH.getIndex()].hasElement(BlockType.FLOOR)) {
            this.elmts.add(new Element(BlockType.HOLE, Dir.SOUTH));
         }

         if (!neighbors[Neighbor.WEST.getIndex()].hasElement(BlockType.FLOOR)) {
            this.elmts.add(new Element(BlockType.HOLE, Dir.WEST));
         }
      }

      if (this.hasElement(BlockType.LADDER)) {
         if (!this.built || neighbors[Neighbor.NORTH.getIndex()].hasElement(BlockType.FLOOR)) {
            this.removeElementAt(BlockType.LADDER, Dir.NORTH);
         }

         if (!this.built || neighbors[Neighbor.EAST.getIndex()].hasElement(BlockType.FLOOR)) {
            this.removeElementAt(BlockType.LADDER, Dir.EAST);
         }

         if (!this.built || neighbors[Neighbor.SOUTH.getIndex()].hasElement(BlockType.FLOOR)) {
            this.removeElementAt(BlockType.LADDER, Dir.SOUTH);
         }

         if (!this.built || neighbors[Neighbor.WEST.getIndex()].hasElement(BlockType.FLOOR)) {
            this.removeElementAt(BlockType.LADDER, Dir.WEST);
         }
      }
   }

   private boolean isPendingWall(BlockType type, Dir dir) {
      for (int i = 0; i < this.pendingWalls.size(); i++) {
         if (this.pendingWalls.get(i).getBlockType() == type && this.pendingWalls.get(i).getDir() == dir) {
            return true;
         }
      }

      return false;
   }

   public final boolean hasElement(BlockType type) {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).getBlockType() == type) {
            return true;
         }
      }

      return false;
   }

   public final boolean hasInteractiveElement() {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).getBlockType().isInteractive()) {
            return true;
         }
      }

      return false;
   }

   public final boolean hasElement(BlockType type, Dir dir) {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).getBlockType() == type && this.elmts.get(i).getDir() == dir) {
            return true;
         }
      }

      return false;
   }

   private void removeAllOfType(BlockType type) {
      for (int i = this.elmts.size() - 1; i >= 0; i--) {
         if (this.elmts.get(i).getBlockType() == type) {
            this.elmts.remove(i);
         }
      }
   }

   public final void removeElement(Element elmt) {
      this.removeElementAt(elmt.getBlockType(), elmt.getDir());
   }

   private void removeElementAt(BlockType type, Dir dir) {
      for (int i = this.elmts.size() - 1; i >= 0; i--) {
         if (this.elmts.get(i).getBlockType() == type && this.elmts.get(i).getDir() == dir) {
            this.elmts.remove(i);
         }
      }
   }

   public final void enableWaterLeak() {
      for (int i = 0; i < this.elmts.size(); i++) {
         this.elmts.get(i).enableWaterLeak();
      }
   }

   public final boolean hasWaterLeak() {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).hasWaterLeak()) {
            return true;
         }
      }

      return false;
   }

   public final void disableWaterLeak() {
      for (int i = 0; i < this.elmts.size(); i++) {
         this.elmts.get(i).disableWaterLeak();
      }
   }

   public final State resolveCollision(State prevState, State state, boolean ceiling) {
      for (int i = 0; i < this.elmts.size(); i++) {
         AABB box = this.elmts.get(i).getBoundingBox();
         if ((ceiling && this.elmts.get(i).getBlockType().isHung() || !ceiling && !this.elmts.get(i).getBlockType().isHung())
            && box != null) {
            box.translate(this.offset);
            state = box.resolveCollision(prevState, state, ceiling);
         }
      }

      return state;
   }

   public final AABB getWaterLevelBox(BlockType type) {
      Element elmt = this.getElement(type);
      AABB box = elmt != null ? elmt.getWaterLevelBox() : null;
      if (elmt != null && box != null) {
         box.translate(this.offset);
         return box;
      } else {
         return null;
      }
   }

   public final boolean hasAnimatedElement() {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).isAnimated()) {
            return true;
         }
      }

      return false;
   }

   public final Element getElement(BlockType type) {
      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).getBlockType() == type) {
            return this.elmts.get(i);
         }
      }

      return null;
   }

   public final ArrayList<Element> getElementsAt(Point pos) {
      ArrayList<Element> result = new ArrayList<>();

      for (int i = 0; i < this.elmts.size(); i++) {
         if (this.elmts.get(i).trackPosition(pos)) {
            result.add(this.elmts.get(i));
         }
      }

      return result;
   }

   public final ArrayList<Element> getElements() {
      return this.elmts;
   }

   public final boolean isRoom() {
      return this.built && !this.hasElement(BlockType.WALL_CROSS);
   }

   public final boolean isBuilt() {
      return this.built;
   }

   public final Point getOffset() {
      return this.offset;
   }

   public final boolean hasWall() {
      return this.hasElement(BlockType.WALL) || this.hasElement(BlockType.WALL_DIAGONAL) || this.hasElement(BlockType.WALL_CORNER) || this.hasElement(BlockType.WALL_T);
   }

   private boolean canHaveWall() {
      return !this.hasWall() && !this.hasElement(BlockType.WALL_CROSS) && !this.hasElement(BlockType.FLOOR);
   }

   public final void resetElements() {
      for (int i = 0; i < this.elmts.size(); i++) {
         this.elmts.get(i).reset();
      }
   }
}
