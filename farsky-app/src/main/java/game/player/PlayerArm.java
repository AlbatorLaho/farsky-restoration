package game.player;

import game.player.weapons.WeaponType;
import game.render.AnimatedMesh;
import game.render.ModelLoader;
import game.player.weapons.HandDrill;
import game.player.weapons.HarpoonGun;
import game.player.weapons.Knife;
import game.player.weapons.UnderwaterScooter;
import game.player.weapons.Weapon;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class PlayerArm {
   private boolean isRightArm = false;
   private AnimatedMesh handMesh;
   private static int handTextureId;
   private float blendProgress = 0.0F;
   private HandState currentHandState = HandState.IDLE;
   private HandState targetHandState = HandState.EXTENDED;
   private ArmState armState = ArmState.IDLE;
   private float blendSpeed = 1.0F;
   private Point weaponRotation = new Point();
   private Point weaponOffset = new Point();
   private ArmTweener tweener;
   private float animTime;
   private int actionPhase = 0;
   private ArrayList<Weapon> weapons;
   private WeaponType activeWeapon;
   private boolean weaponHidden = false;
   private boolean holdingItem = false;
   private float reloadTimer = 0.0F;
   private boolean isReloading = false;

   public PlayerArm(boolean rightArm) {
      this.handMesh = ModelLoader.loadAnimatedMesh("hand", 5);
      this.isRightArm = rightArm;
      if (!rightArm) {
         this.weapons = new ArrayList<>();
         this.weapons.add(new Knife());
         this.weapons.add(new HarpoonGun());
         this.weapons.add(new HandDrill(false));
         this.weapons.add(new UnderwaterScooter());
         this.weapons.add(new HandDrill(true));
         this.activeWeapon = this.weapons.get(0).getType();
      }

      this.tweener = new ArmTweener();
      this.animTime = 0.0F;
   }

   public static void loadTexture() {
      handTextureId = ModelLoader.loadTexture("hand", false, false);
   }

   public final void free() {
      this.handMesh.freeVbo();
   }

   public final void render() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, handTextureId);
      GL11.glPushMatrix();
      this.applyTransform();
      GL11.glPushMatrix();
      GL11.glTranslatef(this.weaponOffset.x * 0.3F, this.weaponOffset.y * 0.3F, this.weaponOffset.z * 0.3F);
      GL11.glRotatef(90.0F + this.weaponRotation.y, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-10.0F + this.weaponRotation.x, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(this.weaponRotation.z, 0.0F, 0.0F, 1.0F);
      GL11.glScalef(0.24F, 0.24F, 0.24F);
      this.handMesh.render();
      GL11.glPopMatrix();
      if (this.weapons != null && !this.weaponHidden) {
         this.weapons.get(this.activeWeapon.getNumber()).render();
      }

      GL11.glPopMatrix();
   }

   public final void renderExtra() {
      if (this.weapons != null && !this.weaponHidden) {
         GL11.glPushMatrix();
         this.applyTransform();
         this.weapons.get(this.activeWeapon.getNumber()).renderExtra();
         GL11.glPopMatrix();
      }
   }

   private void applyTransform() {
      GL11.glTranslatef(-2.6F + this.tweener.getPosition().x * 0.3F, -4.2F + this.tweener.getPosition().y * 0.3F, 0.9F + this.tweener.getPosition().z * 0.3F);
      GL11.glRotatef(this.tweener.getRotation().y, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(this.tweener.getRotation().x, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(this.tweener.getRotation().z, 0.0F, 0.0F, 1.0F);
   }

   public final void update(float delta) {
      if (this.weapons != null && !this.weaponHidden) {
         this.weapons.get(this.activeWeapon.getNumber()).update(delta);
         if (this.weapons.get(this.activeWeapon.getNumber()).hasFired() && this.armState != ArmState.SHOOTING && !this.isReloading && HarpoonGun.getAmmoCount() > 0) {
            this.startReload(0.5F);
         }
      }

      this.blendProgress = this.blendProgress + delta * this.blendSpeed;
      this.animTime += delta;
      this.tweener.update(delta);
      if (this.blendProgress >= 1.0F) {
         this.currentHandState = this.targetHandState;
         this.blendProgress--;
         this.handMesh.commitBlend();
         switch (this.targetHandState) {
            case IDLE:
            case HOLDING:
            case FIRING:
            default:
               break;
            case OPEN:
               this.targetHandState = HandState.IDLE;
               this.blendSpeed = 15.0F;
               break;
            case EXTENDED:
               this.targetHandState = HandState.EXTENDED;
               this.blendSpeed = 0.3F;
         }
      }

      switch (this.armState) {
         case RECOILING:
            this.tweener.setTargetPosition(
               new Point(
                  (float)Math.random() / 6.0F,
                  3.0F + (float)Math.cos(this.animTime) / 3.0F + (float)Math.random() / 6.0F,
                  -3.0F + (float)Math.sin(this.animTime * 0.9F) / 3.0F + (float)Math.random() / 6.0F
               )
            );
            this.tweener.setTargetRotation(new Point());
            break;
         case STABBING:
            if (this.isRightArm) {
               this.tweener.setTargetPosition(new Point(0.0F, ((float)Math.sin(this.animTime * 5.0F) * 0.5F + 0.5F) * 1.2F, (float)Math.sin(this.animTime * 5.0F) * 0.5F + 0.5F));
            } else {
               this.tweener.setTargetPosition(new Point(0.0F, ((float)Math.cos(this.animTime * 5.0F) * 0.5F + 0.5F) * 1.2F, (float)Math.sin(this.animTime * 5.0F) * 0.5F + 0.5F));
            }

            this.tweener.setTargetRotation(new Point());
            break;
         case RAISING:
            this.tweener.setTargetPosition(new Point(0.0F, 0.0F, ((float)Math.sin(this.animTime * 5.0F) * 0.5F + 0.5F) * 2.0F));
            this.tweener.setTargetRotation(new Point());
            break;
         case IDLE:
            if (this.isRightArm) {
               this.tweener.setTargetPosition(new Point(0.0F, (float)Math.cos(this.animTime) / 3.0F, (float)Math.sin(this.animTime * 0.9F) / 3.0F));
            } else {
               this.tweener.setTargetPosition(new Point(0.0F, (float)Math.sin(this.animTime) / 3.0F, (float)Math.cos(this.animTime * 0.9F) / 3.0F));
            }

            this.tweener.setTargetRotation(new Point());
            break;
         case CHARGING:
            this.tweener.setTargetPosition(new Point(0.0F, 5.0F, -7.0F));
            this.tweener.setTargetRotation(new Point(10.0F, 10.0F, 0.0F));
            if (this.tweener.isComplete()) {
               this.returnToIdle();
            }
            break;
         case SHOOTING:
            this.tweener.setTargetPosition(new Point());
            this.tweener.setTargetRotation(new Point(10.0F, 0.0F, 0.0F));
            if (this.tweener.isComplete()) {
               this.actionPhase--;
               this.transitionToIdle(300.0F);
            }
            break;
         case SLASHING:
            if (this.actionPhase == 2) {
               this.tweener.setTargetPosition(new Point(0.0F, 3.0F, 0.0F));
               this.tweener.setTargetRotation(new Point(-20.0F, 0.0F, 50.0F));
               if (this.tweener.isComplete()) {
                  this.tweener.startTween(20.0F);
                  this.actionPhase--;
               }
            }

            if (this.actionPhase == 1) {
               this.tweener.setTargetPosition(new Point(25.0F, 1.0F, -5.0F));
               this.tweener.setTargetRotation(new Point(-10.0F, -50.0F, 50.0F));
               if (this.tweener.isComplete()) {
                  this.actionPhase--;
               }
            }
            break;
         case WORKING:
            if (this.actionPhase == 3) {
               this.tweener.setTargetPosition(new Point(0.0F, -7.0F, 0.0F));
               this.tweener.setTargetRotation(new Point(-50.0F, -50.0F, 0.0F));
               if (this.tweener.isComplete()) {
                  this.actionPhase--;
               }
            }

            if (this.actionPhase == 2) {
               if (this.reloadTimer <= 0.0F) {
                  this.tweener.startTween(3.0F);
                  this.actionPhase--;
                  this.weapons.get(this.activeWeapon.getNumber()).setAmmoReady();
               } else {
                  this.reloadTimer -= delta;
               }
            }

            if (this.actionPhase == 1) {
               this.tweener.setTargetPosition(new Point(0.0F, 0.0F, 0.0F));
               this.tweener.setTargetRotation(new Point());
               if (this.tweener.isComplete()) {
                  this.actionPhase--;
                  this.weapons.get(this.activeWeapon.getNumber()).clearFired();
                  this.isReloading = false;
               }
            }
      }

      if (this.currentHandState != this.targetHandState) {
         this.handMesh.blendToFrame(this.targetHandState.getKeyFrame(), (float)Math.sin(this.blendProgress * Math.PI - (Math.PI / 2)) / 2.0F + 0.5F);
      }
   }

   private void setDefaultPose(float speed) {
      if ((this.weapons == null || this.weaponHidden) && !this.holdingItem) {
         if (this.targetHandState != HandState.EXTENDED) {
            this.blendToHandState(HandState.EXTENDED, 3.0F);
            this.weaponRotation = new Point();
            this.weaponOffset = new Point();
         }
      } else {
         this.blendToHandState(HandState.HOLDING, 20.0F);
         this.weaponRotation = new Point(-80.0F, -40.0F, 0.0F);
         this.weaponOffset = new Point(1.3F, 4.5F, 2.0F);
      }
   }

   private void blendToHandState(HandState state, float speed) {
      if (state != this.targetHandState) {
         this.targetHandState = state;
         this.blendSpeed = speed;
         this.blendProgress = 0.0F;
         this.handMesh.commitBlend();
      }
   }

   public final void startStab() {
      if (this.actionPhase == 0) {
         this.setDefaultPose(3.0F);
         this.armState = ArmState.STABBING;
         this.tweener.startTween(3.0F);
      }
   }

   public final void startRaise() {
      if (this.actionPhase == 0) {
         this.setDefaultPose(3.0F);
         this.armState = ArmState.RAISING;
         this.tweener.startTween(3.0F);
      }
   }

   public final boolean tryAttack(Point origin, Point forward, Point right, Point up) {
      if (this.weapons != null && !this.weaponHidden && this.weapons.get(this.activeWeapon.getNumber()).canAttack() && !this.weapons.get(this.activeWeapon.getNumber()).hasFired()) {
         switch (this.activeWeapon) {
            case SPEARGUN:
               this.actionPhase = 1;
               this.weapons.get(this.activeWeapon.getNumber()).onUse(origin, forward, right, up);
               this.blendToHandState(HandState.FIRING, 15.0F);
               this.armState = ArmState.SHOOTING;
               this.tweener.startTween(50.0F);
               return true;
            case KNIFE:
               this.weapons.get(this.activeWeapon.getNumber()).onUse(origin, forward, right, up);
               this.blendToHandState(HandState.FIRING, 15.0F);
               this.actionPhase = 2;
               this.armState = ArmState.SLASHING;
               this.tweener.startTween(25.0F);
               return true;
            case STANDARD_DRILL:
            case OVERPOWERED_DRILL:
               this.weapons.get(this.activeWeapon.getNumber()).onUse(origin, forward, right, up);
               return true;
            case SCOOTER:
               this.weapons.get(this.activeWeapon.getNumber()).onUse(origin, forward, right, up);
               return true;
			default:
				break;
         }
      }

      return false;
   }

   private void startReload(float delay) {
      this.isReloading = true;
      this.reloadTimer = delay;
      this.actionPhase = 3;
      this.blendToHandState(HandState.FIRING, 15.0F);
      this.armState = ArmState.WORKING;
      this.tweener.startTween(3.0F);
   }

   public final void returnToIdle() {
      if (this.actionPhase == 0) {
         this.transitionToIdle(3.0F);
      }
   }

   private void transitionToIdle(float speed) {
      if (this.actionPhase == 0) {
         this.setDefaultPose(3.0F);
         if (this.armState != ArmState.IDLE) {
            this.armState = ArmState.IDLE;
            this.tweener.startTween(speed);
         }
      }
   }

   public final void selectWeapon(WeaponType type) {
      int prevNum = this.activeWeapon.getNumber();
      WeaponType[] all = WeaponType.values();
      int len = all.length;

      for (int i = 0; i < len; i++) {
         WeaponType wt = all[i];
         if (wt == type) {
            this.activeWeapon = wt;
            break;
         }
      }

      if (prevNum != this.activeWeapon.getNumber()) {
         this.weapons.get(prevNum).onDeselect();
         if (this.weapons.get(this.activeWeapon.getNumber()).hasFired()) {
            this.startReload(0.3F);
         }
      }
   }

   public final void setWeaponHidden(boolean hidden) {
      if (this.weapons != null) {
         this.weapons.get(this.activeWeapon.getNumber()).onDeselect();
      }

      this.weaponHidden = hidden;
   }

   public final void setHoldingItem(boolean holding) {
      this.holdingItem = holding;
   }
}
