package game.outsideObj;

import game.chunks.ChunkManager;
import game.collision.AABB;
import game.inventory.ItemType;
import game.manager.GameScene;
import game.manager.Frustum;
import game.util.Point;
import game.util.State;
import java.io.Serializable;

public abstract class OutsideObj implements Serializable {
   private static final long serialVersionUID = -2153515751347163602L;
   protected Point pos;
   protected AABB aabb;
   protected ItemType type;
   protected transient boolean nearPlayer = false;

   public final void renderModel() {
      if (GameScene.avatar != null && GameScene.avatar.getCameraPos().distanceTo(this.pos) < ChunkManager.viewDistance &&Frustum.isVisible(this.aabb)) {
         this.drawModel();
      }
   }

   public final void renderEffects() {
      if (GameScene.avatar != null && GameScene.avatar.getCameraPos().distanceTo(this.pos) < ChunkManager.viewDistance &&Frustum.isVisible(this.aabb)) {
         this.drawEffects();
      }
   }

   public final void update(float delta) {
      this.nearPlayer = this.aabb.isInPlayerSight(new Point(), 40.0F);
      this.tick(delta);
   }

   public abstract void tick(float delta);

   public abstract void drawModel();

   public abstract void drawEffects();

   public abstract State resolveCollision(State from, State to);

   public final Point getPosition() {
      return this.pos;
   }

   public final AABB getAABB() {
      return this.aabb;
   }

   public final boolean isNearPlayer() {
      return this.nearPlayer;
   }

   public abstract boolean canPickUp();

   public final ItemType getItemType() {
      return this.type;
   }
}
