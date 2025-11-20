package game.player;

import game.manager.Camera;
import game.manager.GameScene;
import game.player.weapons.WeaponType;
import game.shader.Shaders;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class PlayerArms {
   private Avatar avatar;
   private float actionCooldown = 0.0F;
   private PlayerArm rightArm;
   private PlayerArm leftArm;
   private Point attackOrigin;
   private Point anchorPosition;

   public PlayerArms(Avatar owner) {
      this.attackOrigin = new Point();
      this.anchorPosition = new Point();
      this.avatar = owner;
      this.rightArm = new PlayerArm(true);
      this.leftArm = new PlayerArm(false);
   }

   public final void update(float delta) {
      this.rightArm.update(delta);
      this.leftArm.update(delta);
      Point upDir = this.avatar.getUpDir();
      Point rightDir = this.avatar.getRightDir();
      this.attackOrigin = this.avatar.getCameraPos().plus(this.avatar.getLookDir().scaled(2.0F).plus(upDir.scaled(-2.2F).plus(rightDir.scaled(-2.0F))));
      if (this.actionCooldown > 0.0F) {
         this.actionCooldown -= delta;
      } else {
         this.actionCooldown = 0.0F;
         if (this.avatar.isSwimming()) {
            this.rightArm.startStab();
            this.leftArm.startStab();
         } else if (this.avatar.isWalking()) {
            this.rightArm.startRaise();
            this.leftArm.startRaise();
         } else {
            this.rightArm.returnToIdle();
            this.leftArm.returnToIdle();
         }
      }

      this.anchorPosition = this.avatar.getCameraPos().plus(GameScene.avatar.getLookDir().scaled(4.0F)).plus(0.0F, -0.5F, 0.0F);
   }

   public final void render() {
      Shaders.setUniform("directColor", true);
      this.beginRender();
      this.rightArm.render();
      GL11.glScalef(-1.0F, 1.0F, 1.0F);
      this.leftArm.render();
      endRender();
      Shaders.setUniform("directColor", false);
   }

   public final void renderExtra() {
      this.beginRender();
      this.rightArm.renderExtra();
      GL11.glScalef(-1.0F, 1.0F, 1.0F);
      this.leftArm.renderExtra();
      endRender();
   }

   private void beginRender() {
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glColor4f(1.3F - (1.0F - GameScene.avatar.getScale()), 1.3F - (1.0F - GameScene.avatar.getScale()), 1.3F - (1.0F - GameScene.avatar.getScale()), 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(this.anchorPosition.x, this.anchorPosition.y, this.anchorPosition.z);
      Camera.applyYawPitch();
   }

   private static void endRender() {
      GL11.glPopMatrix();
      GL11.glEnable(GL11.GL_CULL_FACE);
   }

   public final void free() {
      this.rightArm.free();
      this.leftArm.free();
   }

   public final boolean tryAttack() {
      return this.leftArm.tryAttack(this.attackOrigin, this.avatar.getLookDir(), this.avatar.getRightDir(), this.avatar.getUpDir());
   }

   public final void selectWeapon(WeaponType type) {
      this.leftArm.selectWeapon(type);
      this.rightArm.setHoldingItem(type == WeaponType.SCOOTER);
   }

   public final void setWeaponHidden(boolean hidden) {
      this.leftArm.setWeaponHidden(hidden);
      this.rightArm.setWeaponHidden(hidden);
      if (hidden) {
         this.rightArm.setHoldingItem(false);
      }
   }
}
