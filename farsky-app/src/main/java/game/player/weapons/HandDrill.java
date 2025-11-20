package game.player.weapons;

import game.chunks.ChunkManager;
import game.environment.EnvironmentManager;
import game.environment.FlyingRock;
import game.environment.ParticleBurst;
import game.environment.ResourcesPercent;
import game.inventory.Item;
import game.inventory.ItemType;
import game.manager.GameScene;
import game.manager.GameTime;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Coord;
import game.util.Point;
import game.util.Segment;

import org.lwjgl.opengl.GL11;

public final class HandDrill extends Weapon {
   private static int drillTexture;
   private static int knobTexture;
   private static Vbo drillMesh;
   private static Vbo knobMesh;
   private float spinAngle = 0.0F;
   private float particleTimer = 0.0F;
   private float drillDuration = 0.0F;
   private Point drillTarget = null;
   private ItemType targetItemType;
   private ItemType targetSurface;
   private float drillRate;
   private static int drillSoundSource = -1;

   public HandDrill(boolean overpowered) {
      if (overpowered) {
         this.weaponType = WeaponType.OVERPOWERED_DRILL;
         this.drillRate = 0.6F;
      } else {
         this.weaponType = WeaponType.STANDARD_DRILL;
         this.drillRate = 1.0F;
      }
   }

   public static void loadAssets() {
      drillMesh = ModelLoader.loadMesh("handDrill");
      drillTexture = ModelLoader.loadTexture("handDrill");
      knobMesh = ModelLoader.loadMesh("handDrill", "handDrillKnob");
      knobTexture = ModelLoader.loadTexture("handDrill", "handDrillKnob");
   }

   @Override
   public final void tick(float deltaTime) {
      if (this.inUse) {
         this.spinAngle += deltaTime * 600.0F;
         if (this.weaponType == WeaponType.OVERPOWERED_DRILL) {
            this.spinAngle += deltaTime * 300.0F;
         }

         if (drillSoundSource == -1) {
            drillSoundSource = SoundManager.addLoopingSource(SoundManager.sfxDrill, null);
            SoundManager.setLoopingSourceVolume(drillSoundSource, 0.5F);
         }

         if (!SoundManager.isLoopingSourcePlaying(drillSoundSource)) {
            if (this.weaponType == WeaponType.OVERPOWERED_DRILL) {
               SoundManager.setLoopingSourcePitch(drillSoundSource, 0.8F);
            } else {
               SoundManager.setLoopingSourcePitch(drillSoundSource, 0.6F);
            }

            SoundManager.playLoopingSource(drillSoundSource);
         }

         if (this.drillTarget != null && this.targetItemType != null) {
            this.particleTimer -= deltaTime;
            if (this.particleTimer < 0.0F) {
               this.particleTimer += 0.2F;
               if (this.targetSurface == ItemType.SAND) {
                  EnvironmentManager.addParticleBurst(new ParticleBurst(this.drillTarget.plus(0.0F, -10.0F, 0.0F), new Point(Math.random() - 0.5, 1.0, Math.random() - 0.5), 50.0F, 1));
               } else {
                  for (int i = 0; i < 3; i++) {
                     EnvironmentManager.addFlyingRock(
                        new FlyingRock(this.drillTarget.plus(0.0F, 0.0F, 0.0F), new Point((Math.random() - 0.5) * 20.0, 50.0, (Math.random() - 0.5) * 20.0))
                     );
                  }
               }
            }
         }

         if (this.drillTarget != null) {
            this.drillDuration += deltaTime;
         }
      } else {
         this.spinAngle += deltaTime * 50.0F;
         this.drillDuration = 0.0F;
         SoundManager.stopLoopingSource(drillSoundSource);
      }

      if (this.spinAngle > 360.0F) {
         this.spinAngle -= 360.0F;
      }

      this.drillTarget = null;
      this.inUse = false;
   }

   @Override
   public final void render() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.weaponType == WeaponType.OVERPOWERED_DRILL) {
         Shaders.setUniform("percent", 0.7F + (float)Math.cos(GameTime.elapsedMillis / 100.0F) * 0.3F);
         Shaders.setUniform("color", new Point(1.0F, 1.0F, 1.0F));
      } else {
         Shaders.setUniform("percent", 0.0);
      }

      GL11.glPushMatrix();
      GL11.glTranslatef(-0.4F, 1.7F, -0.3F);
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-5.0F, 0.0F, 0.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glRotatef(this.spinAngle, 1.0F, 0.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, drillTexture);
      drillMesh.render();
      GL11.glPopMatrix();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, knobTexture);
      knobMesh.render();
      GL11.glPopMatrix();
      if (this.weaponType == WeaponType.OVERPOWERED_DRILL) {
         Shaders.setUniform("percent", 0.0);
      }
   }

   @Override
   public final void onUse(Point pos, Point forward, Point right, Point up) {
      this.inUse = true;
      this.targetItemType = null;
      this.drillTarget = null;
      this.targetItemType = ChunkManager.pickItem(new Segment(pos, pos.plus(forward.scaled(50.0F))), false);
      if (this.targetItemType != null) {
         if (this.drillDuration >= this.drillRate) {
            if (GameScene.avatar.pickupItem(new Item(this.targetItemType))) {
               ChunkManager.pickItem(new Segment(pos, pos.plus(forward.scaled(50.0F))), true);
               if (this.targetItemType == ItemType.CRYSTAL) {
                  GameScene.stats.recordCrystalCollected();
                  GameScene.onBossKilled();
               }
            }

            this.drillDuration = this.drillDuration - this.drillRate;
         }

         this.drillTarget = pos.plus(forward.scaled(25.0F));
         this.targetSurface = ItemType.IRON;
      } else {
         float dist = 0.0F;

         Point hitPoint;
         while (true) {
            if (!(dist < 50.0F)) {
               hitPoint = null;
               break;
            }

            right = pos.plus(forward.scaled(dist));
            if (right.y <= ChunkManager.getHeightSmooth(right.x, right.z)) {
               hitPoint = right;
               break;
            }

            dist++;
         }

         if ((this.drillTarget = hitPoint) != null) {
            ResourcesPercent resources = EnvironmentManager.getResources(new Coord(this.drillTarget.x, this.drillTarget.z));
            this.targetItemType = resources.pickRandom();
            int dominantAxis = ChunkManager.getTerrainColor((int)this.drillTarget.x, (int)this.drillTarget.z).dominantAxis();
            if (dominantAxis != 0 && dominantAxis != 2 && dominantAxis != 3) {
               this.targetSurface = ItemType.IRON;
            } else {
               this.targetSurface = ItemType.SAND;
            }
         }

         if (this.drillDuration >= this.drillRate && this.targetItemType != null) {
            this.drillDuration = this.drillDuration - this.drillRate;
            if (GameScene.avatar.pickupItem(new Item(this.targetItemType))) {
               EnvironmentManager.onOreMined((int)this.drillTarget.x, (int)this.drillTarget.z, this.targetItemType);
            }
         }
      }
   }

   @Override
   public final void renderExtra() {
   }

   @Override
   public final void onDeselect() {
      SoundManager.stopLoopingSource(drillSoundSource);
   }
}
