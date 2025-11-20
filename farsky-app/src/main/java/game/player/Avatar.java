package game.player;

import game.Main;
import game.chunks.ChunkManager;
import game.collision.CollisionDetector;
import game.environment.BloodParticles;
import game.environment.DepthAtmosphere;
import game.environment.EnvironmentManager;
import game.environment.water.WaterSurface;
import game.gui.InteractionHint;
import game.gui.PlayerHud;
import game.gui.menu.MenuController;
import game.gui.menu.MenuState;
import game.gui.menu.SandboxMenu;
import game.input.RawInput;
import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.Storage;
import game.inventory.types.Inventory;
import game.inventory.types.PlayerInventory;
import game.manager.GameMode;
import game.manager.GameScene;
import game.manager.GameState;
import game.manager.GameTime;
import game.manager.Loading;
import game.manager.TextureManager;
import game.outsideObj.Extractor;
import game.outsideObj.ExtractorType;
import game.outsideObj.HarpoonCannon;
import game.outsideObj.Lamp;
import game.player.droid.Droid;
import game.player.weapons.HarpoonGun;
import game.player.weapons.WeaponType;
import game.saving.SaveManager;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.submarine.SubmarinePiece;
import game.util.Coord;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class Avatar {
   static enum MoveState {
      GROUNDED,
      WALKING,
      SWIMMING,
      KNOCKED_BACK;
   }

   public PlayerInput input;
   private PlayerState currentState;
   private PlayerState nextState;
   private PlayerArms arms;
   private PlayerInventory inventory;
   private static Point acceleration = new Point(0.0F, 0.0F, 0.0F);
   private static Point impulse = new Point(0.0F, 0.0F, 0.0F);
   private static Point frameForce = new Point(0.0F, 0.0F, 0.0F);
   private MoveState moveState = MoveState.GROUNDED;
   private static boolean isMoving = false;
   private float damageCooldown = 0.0F;
   public static float deathTimer = 0.0F;
   public static boolean godMode = false;
   private float scale;
   public static String deathMessage = "";
   private Point placementTarget = null;
   private Item lastSelectedItem = null;
   private int hitCount = 0;
   private float walkAnimPhase = 0.0F;
   private boolean inSeafloorBase = false;
   private int jumpCharges = 0;
   private float jumpCooldown = 0.0F;
   private float jumpRechargeTimer = 0.0F;
   private static int heartbeatSource = -1;
   private static int stepsSource = -1;
   private static int stepsWaterSource = -1;
   private static int breathingSource = -1;
   private float bloodTimer = 0.0F;
   private float oxygenDamageTimer = 0.0F;

   public Avatar() {
      this(null, null);
   }

   public Avatar(PlayerState savedState, PlayerInventory savedInventory) {
      this.input = new PlayerInput();
      if (savedState != null) {
         this.currentState = savedState;
      } else {
         this.currentState = new PlayerState();
      }

      this.nextState = new PlayerState();
      this.scale = 1.0F;
      deathMessage = "";
      if (savedInventory != null) {
         this.inventory = savedInventory;
      } else {
         this.inventory = new PlayerInventory("Inventory", 5, 4);
         if (GameScene.gameMode == GameMode.SANDBOX) {
            this.inventory.addItem(new Item(ItemType.DRILL));
            this.inventory.addItem(new Item(ItemType.NEW_BASE));
            if (SandboxMenu.startingItems != null && SandboxMenu.startingItems.size() > 0) {
               for (int i = 0; i < SandboxMenu.startingItems.size(); i++) {
                  this.inventory.addItem(SandboxMenu.startingItems.get(i));
                  SandboxMenu.startingItems.get(i).setCount(0);
               }
            }
         }

         if (!Main.isRelease) {
            this.inventory.addItem(new Item(ItemType.KNIFE));
            this.inventory.addItem(new Item(ItemType.OVERPOWERED_DRILL));
            this.inventory.addItem(new Item(ItemType.UNDERWATER_SCOOTER));
            this.inventory.addItem(new Item(ItemType.IRON, 75));
            this.inventory.addItem(new Item(ItemType.PLANT_POT, 75));
            this.inventory.addItem(new Item(ItemType.CARROT_SEED, 75));
            this.inventory.addItem(new Item(ItemType.POTATO, 75));
            this.inventory.addItem(new Item(ItemType.GREEN_BEAN, 75));
            this.inventory.addItem(new Item(ItemType.CARROT, 75));
            this.inventory.addItem(new Item(ItemType.FLOOR, 75));
            this.inventory.addItem(new Item(ItemType.GLASS_WALL, 75));
            this.inventory.addItem(new Item(ItemType.COOKED_CARROT, 75));
            this.inventory.addItem(new Item(ItemType.LARGE_CHEST, 10));
            this.inventory.addItem(new Item(ItemType.NEW_BASE));
            this.inventory.addItem(new Item(ItemType.NEW_SUBMARINE));
            this.inventory.addItem(new Item(ItemType.SPEARGUN));
            this.inventory.addItem(new Item(ItemType.DROID));
            this.inventory.addItem(new Item(ItemType.COPPER_STUN_SPEAR, 75));
            this.inventory.addItem(new Item(ItemType.COPPER_DIVING_HELMET));
            this.inventory.addItem(new Item(ItemType.COPPER_DIVING_CYLINDER));
            this.inventory.addItem(new Item(ItemType.ENERGY_SPHERE, 15));
         }
      }

      this.inventory.refreshSlotHighlights();
      this.arms = new PlayerArms(this);
      deathTimer = 0.0F;
   }

   public final void update(float delta) {
      this.nextState.copyFrom(this.currentState);
      if (!this.currentState.isNavigating()) {
         this.nextState.horizontalAngle = wrapHorizontalAngle(this.nextState.horizontalAngle + this.input.lookHorizontalDelta);
         this.nextState.verticalAngle = clampVerticalAngle(this.nextState.verticalAngle + this.input.lookVerticalDelta);
      }

      Point moveDir = new Point(0.0F, 0.0F, 0.0F);
      acceleration = new Point(0.0F, 0.0F, 0.0F);
      if (!this.currentState.isDead && this.moveState != MoveState.KNOCKED_BACK) {
         if (this.input.moveForward) {
            moveDir.add(new Point(0.0F, 0.0F, -1.0F));
         }

         if (this.input.moveBackward) {
            moveDir.add(new Point(0.0F, 0.0F, 1.0F));
         }

         if (this.input.strafeLeft) {
            moveDir.add(new Point(-1.0F, 0.0F, 0.0F));
         }

         if (this.input.strafeRight) {
            moveDir.add(new Point(1.0F, 0.0F, 0.0F));
         }

         isMoving = this.input.moveForward || this.input.moveBackward || this.input.strafeLeft || this.input.strafeRight;
         moveDir.normalize();
         moveDir.rotateY(this.nextState.horizontalAngle);
         if (godMode) {
            moveDir.scale(250.0F);
         } else if (this.moveState == MoveState.SWIMMING) {
            moveDir.scale(60.0F);
         } else {
            moveDir.scale(40.0F);
         }

         if (this.currentState.isInside()) {
            if (this.input.ascend && this.currentState.state.onGround) {
               this.nextState.state.startMoving();
               moveDir.add(new Point(0.0F, 60.0F, 0.0F));
            }
         } else if (this.jumpCooldown > 0.0F) {
            this.jumpCooldown -= delta;
         } else if (this.input.ascend && this.jumpCharges > 0 && !this.isAboveWater()) {
            if (this.currentState.state.onGround) {
               moveDir.add(new Point(0.0F, 50.0F, 0.0F));
               this.jumpCooldown = 0.3F;
               this.nextState.state.startMoving();
            } else {
               moveDir.add(new Point(0.0F, 70.0F, 0.0F));
               this.jumpCooldown = 0.6F;
               this.nextState.state.startMoving();
               if (!godMode) {
                  this.jumpCharges--;
               }

               SoundManager.playSound(SoundManager.sfxSwim, null, 1.0F, 0.7F);
            }
         }
      }

      this.nextState.state.vel.x = moveDir.x;
      if (moveDir.y != 0.0F) {
         this.nextState.state.vel.y = Math.max(moveDir.y, this.currentState.state.vel.y);
      }

      this.nextState.state.vel.z = moveDir.z;
      acceleration.add(GameScene.gravity.scaled(50.0F));
      this.nextState.state.vel.add(acceleration.scaled(delta));
      this.nextState.state.vel.add(frameForce.scaled(delta));
      this.nextState.state.pos.add(this.nextState.state.vel.scaled(delta));
      this.nextState.state.pos.add(impulse.scaled(delta));
      this.nextState.state.pos.add(this.nextState.knockback.scaled(delta));
      switch (this.moveState) {
         case GROUNDED:
         case WALKING:
            this.moveState = MoveState.GROUNDED;
            if (!this.input.ascend && this.currentState.state.onGround) {
               this.updateJumpCharges(delta);
            } else {
               this.moveState = MoveState.SWIMMING;
            }
            break;
         case SWIMMING:
            this.nextState.state.vel.add(this.nextState.knockback.copy());
            this.nextState.knockback = new Point();
            if (this.nextState.state.onGround) {
               this.moveState = MoveState.GROUNDED;
            }

            if (this.currentState.isNavigating()) {
               this.updateJumpCharges(delta);
            }
            break;
         case KNOCKED_BACK:
            this.moveState = MoveState.SWIMMING;
      }

      if (this.moveState == MoveState.GROUNDED && isMoving) {
         this.moveState = MoveState.WALKING;
      }

      float impulseLen = impulse.length() * 0.95F;
      if (impulseLen < 0.05F) {
         impulse = new Point();
      } else {
         impulse.normalize();
         impulse.scale(impulseLen);
      }

      frameForce = new Point();
      CollisionDetector.resolveCollision(this.currentState.state, this.nextState.state);
      if (this.nextState.state.onGround) {
         impulse = new Point();
         frameForce = new Point();
      }

      this.currentState.copyFrom(this.nextState);
      SoundManager.setListenerState(this.currentState.state.pos, new Point(), this.getLookDir(), this.getUpDir());
      if (!this.currentState.isDead) {
         if (RawInput.scrollDelta < 0.0F) {
            this.inventory.selectNextSlot();
         }

         if (RawInput.scrollDelta > 0.0F) {
            this.inventory.selectPrevSlot();
         }

         if (this.input.slot0) {
            this.inventory.selectSlot(0);
         }

         if (this.input.slot1) {
            this.inventory.selectSlot(1);
         }

         if (this.input.slot2) {
            this.inventory.selectSlot(2);
         }

         if (this.input.slot3) {
            this.inventory.selectSlot(3);
         }

         if (this.input.slot4) {
            this.inventory.selectSlot(4);
         }

         if (this.input.slot5) {
            this.inventory.selectSlot(5);
         }

         if (this.input.slot6) {
            this.inventory.selectSlot(6);
         }

         if (this.input.slot7) {
            this.inventory.selectSlot(7);
         }

         if (!this.currentState.isNavigating() && this.inventory.getCurrentSlotItem() != null) {
            switch (this.inventory.getCurrentSlotItem().getType()) {
               case SPEARGUN:
                  if (this.lastSelectedItem != this.inventory.getCurrentSlotItem()) {
                     GameScene.avatar.setWeaponHidden(false);
                     this.arms.selectWeapon(WeaponType.SPEARGUN);
                  }

                  if (this.input.primaryMouseHeld && !this.currentState.isInside()) {
                     this.arms.tryAttack();
                  }
                  break;
               case KNIFE:
                  if (this.lastSelectedItem != this.inventory.getCurrentSlotItem()) {
                     GameScene.avatar.setWeaponHidden(false);
                     this.arms.selectWeapon(WeaponType.KNIFE);
                  }

                  if (this.input.primaryMouseHeld && !this.currentState.isInside()) {
                     this.arms.tryAttack();
                  }
                  break;
               case DRILL:
                  if (this.lastSelectedItem != this.inventory.getCurrentSlotItem()) {
                     GameScene.avatar.setWeaponHidden(false);
                     this.arms.selectWeapon(WeaponType.STANDARD_DRILL);
                  }

                  if (this.input.primaryMouseHeld && !this.currentState.isInside()) {
                     this.arms.tryAttack();
                  }
                  break;
               case OVERPOWERED_DRILL:
                  if (this.lastSelectedItem != this.inventory.getCurrentSlotItem()) {
                     GameScene.avatar.setWeaponHidden(false);
                     this.arms.selectWeapon(WeaponType.OVERPOWERED_DRILL);
                  }

                  if (this.input.primaryMouseHeld && !this.currentState.isInside()) {
                     this.arms.tryAttack();
                  }
                  break;
               case UNDERWATER_SCOOTER:
                  if (this.lastSelectedItem != this.inventory.getCurrentSlotItem()) {
                     GameScene.avatar.setWeaponHidden(false);
                     this.arms.selectWeapon(WeaponType.SCOOTER);
                  }

                  if (this.input.primaryMouseHeld && !this.currentState.isInside()) {
                     this.arms.tryAttack();
                  }
                  break;
               default:
                  if (this.lastSelectedItem != this.inventory.getCurrentSlotItem()) {
                     GameScene.avatar.setWeaponHidden(true);
                  }
            }
         }

         this.placementTarget = null;
         if (!this.currentState.isInside()
            && this.inventory.getCurrentSlotItem() != null
            && (
               this.inventory.getCurrentSlotItem().getType() == ItemType.NEW_BASE
                  || this.inventory.getCurrentSlotItem().getType() == ItemType.NEW_SUBMARINE
                  || this.inventory.getCurrentSlotItem().getType() == ItemType.EXTRACTOR
                  || this.inventory.getCurrentSlotItem().getType() == ItemType.OVERPOWERED_EXTRACTOR
                  || this.inventory.getCurrentSlotItem().getType() == ItemType.HARPOON_CANNON
                  || this.inventory.getCurrentSlotItem().getType() == ItemType.LAMP
                  || this.inventory.getCurrentSlotItem().getType() == ItemType.DROID
            )) {
            if (CollisionDetector.raycastTerrain(this.getCameraPos(), this.getCameraPos().plus(this.getLookDir().scaled(150.0F)), true)) {
               this.placementTarget = CollisionDetector.getHitPoint();
               this.placementTarget.x = (int)(this.placementTarget.x / 20.0F) * 20.0F + 10.0F;
               this.placementTarget.z = (int)(this.placementTarget.z / 20.0F) * 20.0F + 10.0F;
               this.placementTarget.y = ChunkManager.getHeight(this.placementTarget.x, this.placementTarget.z);
            }

            if (this.placementTarget != null && RawInput.leftMouseDown) {
               switch (this.inventory.getCurrentSlotItem().getType()) {
                  case NEW_BASE:
                     GameScene.spawnSeafloorBase(this.placementTarget);
                     break;
                  case NEW_SUBMARINE:
                     GameScene.spawnSubmarine(this.placementTarget, true);
                     break;
                  case EXTRACTOR:
                     GameScene.spawnOutsideObject(new Extractor(this.placementTarget, ExtractorType.STANDARD));
                     break;
                  case OVERPOWERED_EXTRACTOR:
                     GameScene.spawnOutsideObject(new Extractor(this.placementTarget, ExtractorType.OVERPOWERED));
                     break;
                  case HARPOON_CANNON:
                     GameScene.spawnOutsideObject(new HarpoonCannon(this.placementTarget));
                     break;
                  case LAMP:
                     GameScene.spawnOutsideObject(new Lamp(this.placementTarget));
                     break;
                  case DROID:
                     GameScene.spawnDroid(new Droid(this.placementTarget, true));
				default:
					break;
               }
            }
         }

         if (!this.currentState.isInside() && this.inventory.getCurrentSlotItem() != null && this.inventory.getCurrentSlotItem().getType() == ItemType.HAMMER && RawInput.leftMouseDown) {
            GameScene.pickUpNearbyObjects();
         }

         if (RawInput.leftMouseDown && this.inventory.getCurrentSlotItem() != null) {
            this.useItem(this.inventory.getCurrentSlotStorage());
         }

         if (this.currentState.isNavigating()) {
            this.lastSelectedItem = null;
         } else {
            this.lastSelectedItem = this.inventory.getCurrentSlotItem();
         }
      }

      this.arms.update(delta);
      if (Main.getGameState() == GameState.PLAYING) {
         int woundCount = 0;
         if (this.currentState.isWoundedArm()) {
            this.applyDamage(delta * 0.08F, false, false, "You bled to death");
            woundCount++;
         }

         if (this.currentState.isWoundedLeg()) {
            this.applyDamage(delta * 0.08F, false, false, "You bled to death");
            woundCount++;
         }

         if (woundCount > 0 && !this.currentState.isInside()) {
            this.bloodTimer += delta;
            if (this.bloodTimer >= 12.0F - woundCount * 4.0F) {
               EnvironmentManager.addBloodParticles(new BloodParticles(this.currentState.state.pos.plus(0.0F, 10.0F, 0.0F), 7));
               this.bloodTimer -= 12.0F - woundCount * 4.0F;
            }
         }

         if (this.currentState.starvingCooldown > 0.0F) {
            this.currentState.starvingCooldown -= delta;
         } else {
            this.currentState.starvingCooldown = 0.0F;
         }

         this.currentState.adjustStarvingLevel(-delta * 0.2F);
         if (this.currentState.getHungerPercent() == 0.0F) {
            this.applyDamage(delta * 0.7F, false, false, "You died of starvation");
         }

         if (!this.currentState.isInside() && this.getInventoryYOffset() > DepthAtmosphere.getDepthInMeters()) {
            this.currentState.exploded += delta * 0.03F;
         } else {
            this.currentState.exploded -= delta * 0.05F;
         }

         if (this.currentState.exploded < 0.0F || godMode) {
            this.currentState.exploded = 0.0F;
         }

         if (this.currentState.exploded > 1.0F) {
            this.currentState.exploded = 1.0F;
            this.applyDamage(100000000F, false, true, "Your suit imploded due to pressure");
         }

         if ((!this.currentState.isInside() || this.currentState.isNavigating()) && !this.isAboveWater()) {
            this.currentState.adjustOxygenLevel(-delta, this.getMaxHealth());
            if (this.currentState.getOxygenLevel() <= 0.0F) {
               this.oxygenDamageTimer -= delta;
               if (this.oxygenDamageTimer <= 0.0F) {
                  this.oxygenDamageTimer = 1.0F;
                  this.applyDamage(5.0F, false, true, "You died from asphyxiation");
               }
            }
         } else {
            this.currentState.adjustOxygenLevel(0.2F * delta * this.getMaxHealth(), this.getMaxHealth());
         }
      }

      if (heartbeatSource == -1) {
         heartbeatSource = SoundManager.addLoopingSource(SoundManager.sfxHeartbeat, null);
      }

      if (this.currentState.getLifeLevel() / 100.0F < 0.3F && !this.currentState.isDead && Main.getGameState() == GameState.PLAYING) {
         if (!SoundManager.isLoopingSourcePlaying(heartbeatSource)) {
            SoundManager.playLoopingSource(heartbeatSource);
         }

         SoundManager.setLoopingSourceVolume(heartbeatSource, (0.3F - this.currentState.getLifeLevel() / 100.0F) * 2.0F);
         SoundManager.setLoopingSourcePitch(heartbeatSource, 1.0F + (0.3F - this.currentState.getLifeLevel() / 100.0F) * 2.0F);
      } else {
         SoundManager.stopLoopingSource(heartbeatSource);
      }

      if (breathingSource == -1) {
         breathingSource = SoundManager.addLoopingSource(SoundManager.sfxBreathing, null);
      }

      if (this.currentState.getOxygenLevel() < 60.0F && !this.currentState.isDead && Main.getGameState() == GameState.PLAYING) {
         if (!SoundManager.isLoopingSourcePlaying(breathingSource)) {
            SoundManager.playLoopingSource(breathingSource);
         }

         SoundManager.setLoopingSourceVolume(breathingSource, Math.min((60.0F - this.currentState.getOxygenLevel()) * 0.003F, 0.1F));
         SoundManager.setLoopingSourcePitch(breathingSource, 1.0F + (60.0F - this.currentState.getOxygenLevel()) * 0.002F);
      } else {
         SoundManager.stopLoopingSource(breathingSource);
      }

      if (stepsSource == -1) {
         stepsSource = SoundManager.addLoopingSource(SoundManager.sfxSteps, null);
      }

      if (stepsWaterSource == -1) {
         stepsWaterSource = SoundManager.addLoopingSource(SoundManager.sfxStepsWater, null);
      }

      if (this.moveState == MoveState.WALKING && this.currentState.isInside()) {
         if (this.walkAnimPhase < 0.25F || this.walkAnimPhase >= (20F / 3F)) {
            SoundManager.stopLoopingSource(stepsWaterSource);
         } else if (!SoundManager.isLoopingSourcePlaying(stepsWaterSource)) {
            SoundManager.playLoopingSource(stepsWaterSource);
            SoundManager.setLoopingSourceVolume(stepsWaterSource, 0.15F);
            SoundManager.setLoopingSourcePitch(stepsWaterSource, 1.1F - this.walkAnimPhase / (20F / 3F) * 0.2F);
         }

         if (this.walkAnimPhase < 1.4F) {
            if (!SoundManager.isLoopingSourcePlaying(stepsSource)) {
               SoundManager.playLoopingSource(stepsSource);
               SoundManager.setLoopingSourceVolume(stepsSource, 0.15F);
            }
         } else {
            SoundManager.stopLoopingSource(stepsSource);
         }
      } else {
         SoundManager.stopLoopingSource(stepsSource);
         SoundManager.stopLoopingSource(stepsWaterSource);
      }

      this.currentState.addLifeLevel(0.0F);
      if (this.currentState.isDead && (deathTimer += delta) >= 4.0F && this.input.interact) {
         deathTimer = 0.0F;
         this.currentState.setNavigating(false);
         GameScene.onPlayerDeath();
         if (GameScene.gameMode.isOneLife()) {
            SaveManager.deleteSave();
            Main.gameState = GameState.LOADING_MENU;
            MenuController.currentMenuState = MenuState.MAIN;
         } else {
            GameScene.worldChest = new WorldChest(new Inventory("Tomb Chest", this.inventory.takeAllItems()), this.currentState.state.pos);
            this.currentState.respawn();
            Loading.reloadGame();
         }
      }

      this.scale = 1.0F;
      ArrayList<Item> scooterItems = this.inventory.findAllItems(ItemType.UNDERWATER_SCOOTER);

      for (int i = 0; i < scooterItems.size(); i++) {
         scooterItems.get(i).setParam(Math.min(scooterItems.get(i).getParam() + delta * 0.02F, 1.0F));
      }

      if (this.currentState.isInside() && this.currentState.state.onGround) {
         this.currentState.setLastSafeSpot(this.currentState.state.pos);
      }

      if (Main.hasStateChanged()) {
         this.inventory.refreshSlotHighlights();
      }

      this.inventory.update(delta);
      if (this.damageCooldown > 0.0F) {
         this.damageCooldown -= delta;
      }

      this.input.resetFrameInputs();
   }

   public final void renderExtra() {
      if (!this.currentState.isDead) {
         if (this.placementTarget != null) {
            Point pos = this.placementTarget;
            Point color = new Point(1.0F, 0.95F, 0.2F);
            float radius = 5.0F;
            Shaders.setUniform("emissive", true);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDepthMask(false);
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.x, pos.y, pos.z);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
            GL11.glColor4f(color.x, color.y, color.z, 0.5F);
            GL11.glRotatef(GameTime.elapsedMillis / 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glBegin(GL11.GL_QUADS);

            for (int i = 0; i < 12; i++) {
               GL11.glColor4f(color.x, color.y, color.z, 0.5F);
               GL11.glVertex3f(radius * (float)Math.cos((Math.PI / 6) * i), -5.0F, radius * (float)Math.sin((Math.PI / 6) * i));
               GL11.glColor4f(color.x, color.y, color.z, 0.5F);
               GL11.glVertex3f(radius * (float)Math.cos((Math.PI / 6) * (i + 1)), -5.0F, radius * (float)Math.sin((Math.PI / 6) * (i + 1)));
               GL11.glColor4f(color.x, color.y, color.z, 0.0F);
               GL11.glVertex3f(radius * (float)Math.cos((Math.PI / 6) * (i + 1)), 20.0F, radius * (float)Math.sin((Math.PI / 6) * (i + 1)));
               GL11.glColor4f(color.x, color.y, color.z, 0.0F);
               GL11.glVertex3f(radius * (float)Math.cos((Math.PI / 6) * i), 20.0F, radius * (float)Math.sin((Math.PI / 6) * i));
            }

            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINES);

            for (int i = 0; i < 12; i++) {
               GL11.glColor4f(color.x, color.y, color.z, 1.0F);
               GL11.glVertex3f(radius * (float)Math.cos((Math.PI / 6) * i), -5.0F, radius * (float)Math.sin((Math.PI / 6) * i));
               GL11.glColor4f(color.x, color.y, color.z, 0.0F);
               GL11.glVertex3f(radius * (float)Math.cos((Math.PI / 6) * i), 20.0F, radius * (float)Math.sin((Math.PI / 6) * i));
            }

            GL11.glEnd();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_CULL_FACE);
            Shaders.setUniform("emissive", false);
         }

         this.arms.renderExtra();
      }
   }

   public final void render() {
      if (!this.currentState.isDead) {
         this.arms.render();
      }
   }

   public final void renderHudBar() {
      this.inventory.renderBottomBar();
   }

   public final void renderInventory() {
      this.inventory.render();
   }

   public final void useItem(Storage slot) {
      if (slot != null && slot.getItem() != null && slot.getItem().getType() != null) {
         switch (slot.getItem().getType().getAction()) {
            case EAT:
               this.currentState.addLifeLevel(slot.getItem().getType().getParam());
               this.currentState.adjustStarvingLevel(slot.getItem().getType().getParam());
               slot.consume();
               PlayerHud.hungerIconTimer = 2.5F;
               return;
            case USE:
               switch (slot.getItem().getType()) {
                  case BANDAGE:
                     if (this.currentState.isWoundedLeg() || this.currentState.isWoundedArm()) {
                        slot.consume();
                        GameScene.stats.recordBandageUsed();
                     }

                     if (this.currentState.isWoundedLeg()) {
                        this.setWoundedLeg(false);
                        return;
                     } else {
                        if (this.currentState.isWoundedArm()) {
                           this.setWoundedArm(false);
                        }

                        return;
                     }
                  default:
                     return;
               }
            case EQUIP:
               this.inventory.clearNewFlags();
               slot.getItem().setIsNew(true);
               HarpoonGun.loadedAmmo = slot.getItem();
			default:
				break;
         }
      }
   }

   private void updateJumpCharges(float delta) {
      if (this.jumpRechargeTimer > 0.15F) {
         if (this.jumpCharges < 5) {
            this.jumpRechargeTimer -= 0.15F;
            this.jumpCharges++;
            return;
         }
      } else {
         this.jumpRechargeTimer += delta;
      }
   }

   public final Point getCameraPos() {
      Point pos = this.currentState.state.pos.copy();
      if (!this.currentState.isDead) {
         pos.addY(20.0F);
      } else {
         pos.addY(2.0F);
      }

      return pos;
   }

   public final Coord getPos2D() {
      return new Coord(this.currentState.state.pos.x, this.currentState.state.pos.z);
   }

   public final Point getPos() {
      return this.currentState.state.pos.copy();
   }

   public final float getHorizontalAngle() {
      return this.currentState.horizontalAngle;
   }

   public final float getVerticalAngle() {
      return this.currentState.verticalAngle;
   }

   public final Point getLookDir() {
      Point dir = new Point(
            -Math.sin(Math.toRadians(this.currentState.horizontalAngle)) * Math.cos(Math.toRadians(this.currentState.verticalAngle)),
            Math.sin(Math.toRadians(this.currentState.verticalAngle)),
            -Math.cos(Math.toRadians(this.currentState.horizontalAngle)) * Math.cos(Math.toRadians(this.currentState.verticalAngle))
         );
      dir.normalize();
      return dir;
   }

   public final Point getRightDir() {
      Point dir = new Point(-Math.cos(Math.toRadians(this.currentState.horizontalAngle)), 0.0, Math.sin(Math.toRadians(this.currentState.horizontalAngle)));
      dir.normalize();
      return dir;
   }

   public final Point getUpDir() {
      return this.getLookDir().cross(this.getRightDir());
   }

   public final void setPos(Point pos) {
      this.currentState.state.pos = pos;
      this.currentState.state.vel = new Point();
   }

   public final void setLastSafeSpot(Point pos) {
      this.currentState.setLastSafeSpot(pos);
   }

   public final void setPosByFeet(Point pos) {
      this.setPos(pos.plus(0.0F, -20.0F, 0.0F));
   }

   public final void applyKnockback(Point knockback) {
      this.moveState = MoveState.KNOCKED_BACK;
      this.currentState.knockback.add(knockback);
      if (this.currentState.knockback.length() > 80.0F) {
         this.currentState.knockback.normalize();
         this.currentState.knockback.scale(80.0F);
      }

      this.setOnGround(false);
      if (!this.currentState.state.vel.equals(new Point())) {
         this.currentState.knockback.add(this.currentState.state.vel);
      }

      this.currentState.state.vel.y = 0.0F;
   }

   public final void setHorizontalAngle(float angle) {
      this.currentState.horizontalAngle = angle;
   }

   public final void setVerticalAngle(float angle) {
      this.currentState.verticalAngle = angle;
   }

   public final void addHorizontalAngle(float delta) {
      this.currentState.horizontalAngle = wrapHorizontalAngle(this.currentState.horizontalAngle + delta);
   }

   private static float wrapHorizontalAngle(float angle) {
      if (angle > 360.0F) {
         angle -= 360.0F;
      }

      if (angle < -360.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public final void addVerticalAngle(float delta) {
      this.currentState.verticalAngle = clampVerticalAngle(this.currentState.verticalAngle + delta);
   }

   private static float clampVerticalAngle(float angle) {
      if (angle > 80.0F) {
         angle = 80.0F;
      }

      if (angle < -80.0F) {
         angle = -80.0F;
      }

      return angle;
   }

   public final int getMaxHealth() {
      return 200 + this.inventory.getCylinderOxygenCapacity();
   }

   public final float getMaxOxygen() {
      return 100.0F;
   }

   public final float getHealth() {
      return this.currentState.getLifeLevel();
   }

   public final float getOxygen() {
      return this.currentState.getOxygenLevel();
   }

   public final boolean hasWoundedArm() {
      return this.currentState.isWoundedArm();
   }

   public final boolean hasWoundedLeg() {
      return this.currentState.isWoundedLeg();
   }

   public final float getHunger() {
      return this.currentState.getHungerPercent();
   }

   public final boolean isInside() {
      return this.currentState.isInside();
   }

   public final boolean isOnGround() {
      return this.currentState.state.onGround;
   }

   public final boolean isNavigating() {
      return this.currentState.isNavigating();
   }

   public final void setWoundedArm(boolean wounded) {
      this.currentState.setWoundedArm(wounded);
   }

   public final void setWoundedLeg(boolean wounded) {
      this.currentState.setWoundedLeg(wounded);
   }

   public final void setInside(boolean inside) {
      this.currentState.setInside(inside);
   }

   public final void setOnGround(boolean onGround) {
      this.currentState.state.onGround = onGround;
   }

   public final void setNavigating(boolean navigating) {
      this.currentState.setNavigating(navigating);
   }

   public final float getExplosionEffect() {
      return this.currentState.exploded;
   }

   public final float getScale() {
      return this.scale;
   }

   public final boolean chargeCurrentItem(float amount) {
      Item item = this.inventory.getCurrentSlotItem();
      if (item != null) {
         item.setParam(Math.max(Math.min(item.getParam() + amount, 1.0F), 0.0F));
         return item.getParam() > 0.0F;
      } else {
         return false;
      }
   }

   public final int getInventoryYOffset() {
      return -Math.max(180, this.inventory.getHelmetOxygenBonus());
   }

   public final void takeDamage(float amount, String message) {
      this.applyDamage(amount, true, true, message);
   }

   private void applyDamage(float amount, boolean useCooldown, boolean playSound, String message) {
      if (useCooldown) {
         if (this.damageCooldown > 0.0F) {
            return;
         }

         amount *= this.inventory.getSuitDamageReduction();
      }

      if (!godMode && this.currentState.getLifeLevel() > 0.0F && amount > 0.0F) {
         deathMessage = message;
         this.currentState.addLifeLevel(-amount);
         if (useCooldown) {
            this.damageCooldown = 1.0F;
         }

         if (playSound) {
            SoundManager.playSound(SoundManager.sfxHurt, null, 0.8F + (float)Math.random() * 0.2F, 0.4F);
         }

         if (useCooldown) {
            if (Math.random() < this.hitCount * 0.05F - 0.01F) {
               if (this.currentState.isWoundedArm()) {
                  this.setWoundedLeg(true);
               } else {
                  this.setWoundedArm(true);
               }

               this.hitCount = 0;
               return;
            }

            this.hitCount++;
         }
      }
   }

   public final float getHitFlash() {
      return this.currentState.isDead ? 0.7F : this.damageCooldown;
   }

   public static float getDeathFade() {
      return Math.min(1.0F, deathTimer / 4.0F);
   }

   public final void applyImpulse(Point impulseVec) {
      impulse = impulseVec;
      acceleration = new Point();
      this.currentState.state.vel = new Point();
   }

   public final Item getSelectedItem() {
      return this.inventory.getCurrentSlotItem();
   }

   public final PlayerInventory getInventory() {
      return this.inventory;
   }

   public final void consumeSelectedItem() {
      this.inventory.consumeCurrentItem();
   }

   public final boolean hasResources(Item item) {
      return this.inventory.canCraft(item);
   }

   public final void clearEmptyInventorySlots() {
      this.inventory.removeEmptyItems();
   }

   public final boolean pickupItem(Item item) {
      Item overflow = this.inventory.addItem(new Item(item.getType(), item.getCount()));
      if (overflow == null || overflow.getCount() <= 0) {
         SoundManager.playSound(SoundManager.sfxGrab, null, 0.8F + (float)Math.random() * 0.1F, 0.2F);
         if (item.getCount() == 0) {
            PlayerHud.addPickupNotification(item.getType(), 1);
         } else {
            PlayerHud.addPickupNotification(item.getType(), item.getCount());
         }

         return true;
      } else {
         InteractionHint.addTimedHint("Inventory Full", 1.0F);
         return false;
      }
   }

   public final void setWalkAnimPhase(float phase) {
      this.walkAnimPhase = phase;
   }

   public final void collectSubmarinePiece(SubmarinePiece piece) {
      this.currentState.addSubmarinePiece(piece);
      if (this.currentState.hasAllSubmarinePieces()) {
         GameScene.spawnSubmarine(this.currentState.state.pos, false);
      }
   }

   public final boolean hasSubmarinePiece(SubmarinePiece piece) {
      return this.currentState.hasSubmarinePiece(piece);
   }

   public final int getSubmarinePiecesCount() {
      return this.currentState.getSubmarinePieceCount();
   }

   public final void dispose() {
      this.arms.free();
   }

   public final boolean isWalking() {
      return this.moveState == MoveState.WALKING;
   }

   public final boolean isSwimming() {
      return this.moveState == MoveState.SWIMMING;
   }

   public final Point getColor() {
      return new Point(1.0F, 1.0F, 1.0F);
   }

   public final boolean isInteractPressed() {
      return this.input.interact;
   }

   public final boolean isDescendPressed() {
      return this.input.descend;
   }

   private void setWeaponHidden(boolean hidden) {
      this.arms.setWeaponHidden(hidden);
   }

   public final boolean isDead() {
      return this.currentState.isDead;
   }

   public final boolean isInSeafloorBase() {
      return this.inSeafloorBase;
   }

   public final void setInSeafloorBase(boolean inBase) {
      this.inSeafloorBase = inBase;
   }

   public final int getJumpCharges() {
      return this.jumpCharges;
   }

   public final boolean isAboveWater() {
      return this.getCameraPos().y > WaterSurface.getWaveHeight();
   }

   public final PlayerState getPlayerState() {
      return this.currentState;
   }
}
