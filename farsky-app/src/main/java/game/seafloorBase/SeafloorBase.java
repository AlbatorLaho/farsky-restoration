package game.seafloorBase;

import game.chunks.ChunkManager;
import game.collision.AABB;
import game.collision.CollisionDetector;
import game.manager.TextureManager;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.manager.Frustum;
import game.manager.GameScene;
import game.util.Point;
import game.util.State;
import java.io.Serializable;
import org.lwjgl.opengl.GL11;

public class SeafloorBase implements Serializable {
   private static final long serialVersionUID = 7564246278970646170L;
   private Octree octree;
   private Point pos;
   private transient float buildFlashIntensity = 1.0F;
   private transient boolean visible = false;

   public SeafloorBase(Point pos, Octree.BaseType baseType) {
      this.octree = new Octree(pos);
      this.octree.initialize(baseType);
      this.pos = pos.copy();
      this.pos.y = ChunkManager.getHeight(pos.x, pos.z) + 17.5F;
   }

   public final void update(float delta) {
      if (GameScene.avatar != null) {
         this.octree.updatePickedBlock(this.toLocalSpace(GameScene.avatar.getCameraPos()), GameScene.avatar.getLookDir());
         this.octree.updateInteraction(delta, this.toLocalSpace(GameScene.avatar.getCameraPos()));
         this.octree.update(delta);
      }

      Point camPos = GameScene.avatar.getCameraPos();
      if (CollisionDetector.containsPoint(camPos, this.getBoundingBox(), this.pos, new Point())) {
         GameScene.avatar.setInSeafloorBase(true);
      }

      if (this.buildFlashIntensity < 1.0F) {
         this.buildFlashIntensity += delta * 0.5F;
      }

      this.visible = Frustum.isVisible(
         new AABB(
            this.getOctreeBounds().getCenter().plus(this.pos).plus(0.0F, -250.0F, 0.0F),
            this.getOctreeBounds().max.x - this.getOctreeBounds().min.x,
            this.getOctreeBounds().max.y - this.getOctreeBounds().min.y + 500.0F,
            this.getOctreeBounds().max.z - this.getOctreeBounds().min.z
         )
      );
   }

   public final void renderOpaque() {
      if (this.visible) {
         Shaders.setUniform("inside", this.octree.isAvatarInside());
         Shaders.setUniform("light_ambient_inside", new Point(0.5F, 0.4F, 0.4F).scaled(this.buildFlashIntensity));
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         GL11.glPushMatrix();
         GL11.glTranslatef(this.pos.x, this.pos.y, this.pos.z);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.octree.renderOpaque(this.buildFlashIntensity);
         GL11.glPopMatrix();
      }
   }

   public final void renderAlpha() {
      if (this.visible) {
         Shaders.setUniform("inside", this.octree.isAvatarInside());
         Shaders.setUniform("light_ambient_inside", new Point(0.5F, 0.4F, 0.4F).scaled(this.buildFlashIntensity));
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         GL11.glPushMatrix();
         GL11.glTranslatef(this.pos.x, this.pos.y, this.pos.z);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.octree.renderAlpha(this.buildFlashIntensity);
         GL11.glPopMatrix();
      }
   }

   public final Point getPos() {
      return this.pos;
   }

   private AABB getOctreeBounds() {
      AABB box = new AABB();
      box.copyFrom(this.octree.getBounds());
      return box;
   }

   public final AABB getBoundingBox() {
      AABB box = new AABB();
      box.copyFrom(this.octree.getCollisionBox());
      return box;
   }

   public final Octree.BaseType getBaseType() {
      return this.octree.getBaseType();
   }

   public final void triggerBuild() {
      SoundManager.playSound(SoundManager.sfxVesselCollision, this.pos, 0.9F + (float)Math.random() * 0.2F, 0.5F);
      this.buildFlashIntensity = 0.1F;
      this.octree.triggerWaterLeak();
   }

   public final State resolveCollision(State prevState, State state) {
      prevState.pos = this.toLocalSpace(prevState.pos);
      state.pos = this.toLocalSpace(state.pos);
      state = this.octree.resolveCollision(prevState, state);
      prevState.pos = this.fromLocalSpace(prevState.pos);
      state.pos = this.fromLocalSpace(state.pos);
      return state;
   }

   public final void rebuildDisplayLists() {
      if (this.octree != null) {
         this.octree.rebuildDisplayLists();
      }
   }

   public final Point getInteractionPoint() {
      return this.octree.getRandomSurfacePoint();
   }

   private Point toLocalSpace(Point worldPos) {
      worldPos.subtract(this.pos);
      return worldPos;
   }

   private Point fromLocalSpace(Point localPos) {
      localPos.add(this.pos);
      return localPos;
   }
}
