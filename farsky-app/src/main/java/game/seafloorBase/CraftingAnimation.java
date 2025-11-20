package game.seafloorBase;

import game.collision.CollisionBox;
import game.manager.TextureManager;
import game.seafloorBase.util.PickedBlock;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class CraftingAnimation {
   public static enum ActionType {
      PLACE,
      DEMOLISH,
      REPAIR;
   }

   private PickedBlock pickedBlock = null;
   public ActionType actionType;
   private ArrayList<Point> effectPositions;
   private ArrayList<Float> effectTimers;

   public CraftingAnimation() {
      this.actionType = ActionType.PLACE;
      this.effectPositions = new ArrayList<>();
      this.effectTimers = new ArrayList<>();
   }

   public final void update(float delta) {
      for (int i = this.effectTimers.size() - 1; i >= 0; i--) {
         this.effectTimers.set(i, this.effectTimers.get(i) - delta);
         if (this.effectTimers.get(i) <= 0.0F) {
            this.effectTimers.remove(i);
            this.effectPositions.remove(i);
         }
      }
   }

   public final void render() {
      if (this.actionType == ActionType.PLACE) {
         GL11.glColor4f(1.0F, 1.0F, 0.75F, 1.0F);
      }

      if (this.actionType == ActionType.DEMOLISH) {
         GL11.glColor4f(1.0F, 0.75F, 0.75F, 1.0F);
      }

      if (this.actionType == ActionType.REPAIR) {
         GL11.glColor4f(0.75F, 1.0F, 0.75F, 1.0F);
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.picking);
      if (this.pickedBlock != null) {
         Point offset = this.pickedBlock.block.getOffset();
         GL11.glDisable(GL11.GL_CULL_FACE);
         GL11.glDepthMask(false);
         GL11.glPushMatrix();
         Shaders.setUniform("emissive", true);
         if (this.pickedBlock.pickType == PickedBlock.PickType.FLOOR) {
            GL11.glTranslatef(offset.x, offset.y + 1.75F, offset.z);
            GL11.glScalef(10.0F, 3.5F, 10.0F);
         }

         if (this.pickedBlock.pickType == PickedBlock.PickType.CEILING) {
            GL11.glTranslatef(offset.x, offset.y + 35.0F - 1.75F, offset.z);
            GL11.glScalef(10.0F, 3.5F, 10.0F);
         }

         if (this.pickedBlock.pickType == PickedBlock.PickType.WALL) {
            GL11.glTranslatef(offset.x, offset.y + 17.5F, offset.z);
            GL11.glScalef(10.0F, 35.0F, 10.0F);
         }

         CollisionBox.renderCube();
         GL11.glPopMatrix();
         GL11.glDepthMask(true);
         GL11.glEnable(GL11.GL_CULL_FACE);
      }

      for (int i = 0; i < this.effectTimers.size(); i++) {
         if (this.actionType == ActionType.PLACE) {
            GL11.glColor4f(1.0F, 1.0F, 0.75F, this.effectTimers.get(i));
         }

         if (this.actionType == ActionType.DEMOLISH) {
            GL11.glColor4f(1.0F, 0.75F, 0.75F, this.effectTimers.get(i));
         }

         if (this.actionType == ActionType.REPAIR) {
            GL11.glColor4f(0.75F, 1.0F, 0.75F, this.effectTimers.get(i));
         }

         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         Point pos = this.effectPositions.get(i);
         GL11.glDisable(GL11.GL_CULL_FACE);
         GL11.glPushMatrix();
         Shaders.setUniform("emissive", true);
         GL11.glTranslatef(pos.x, pos.y + 17.5F, pos.z);
         GL11.glScalef(10.0F, 35.0F, 10.0F);
         CollisionBox.renderCube();
         GL11.glPopMatrix();
         GL11.glEnable(GL11.GL_CULL_FACE);
      }
   }

   public final void setPickedBlock(PickedBlock block) {
      this.pickedBlock = block;
   }

   public final void triggerEffect() {
      SoundManager.playSound(SoundManager.sfxBuild, null, 1.0F, 0.2F);
      this.effectPositions.add(this.pickedBlock.block.getOffset().copy());
      this.effectTimers.add(0.5F);
   }
}
