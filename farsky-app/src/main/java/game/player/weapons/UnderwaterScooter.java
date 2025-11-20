package game.player.weapons;

import game.environment.EnvironmentManager;
import game.environment.particle.MovingParticle;
import game.manager.GameScene;
import game.render.ModelLoader;
import game.render.Vbo;
import game.sounds.SoundManager;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class UnderwaterScooter extends Weapon {
   private static int scooterTexture;
   private static int fanTexture;
   private static Vbo scooterMesh;
   private static Vbo fanMesh;
   private Point thrustDir = new Point();
   private float fanAngle = 0.0F;
   private Point exhaustPos = new Point();
   private float particleTimer = 0.0F;
   private static int scooterSoundSource = -1;

   public UnderwaterScooter() {
      this.weaponType = WeaponType.SCOOTER;
   }

   public static void loadAssets() {
      scooterMesh = ModelLoader.loadMesh("underwaterScooter");
      scooterTexture = ModelLoader.loadTexture("underwaterScooter");
      fanMesh = ModelLoader.loadMesh("underwaterScooter", "fan");
      fanTexture = ModelLoader.loadTexture("underwaterScooter", "fan");
   }

   @Override
   public final void tick(float deltaTime) {
      if (!this.inUse || !GameScene.avatar.chargeCurrentItem(-deltaTime * 0.08F)) {
         this.inUse = false;
      } else if (GameScene.avatar.getCameraPos().y < -(1000F / 3F)) {
         GameScene.avatar.applyKnockback(this.thrustDir.scaled(150.0F * deltaTime));
      }

      if (this.inUse) {
         this.fanAngle += 360.0F * deltaTime * 2.0F;
         if (this.fanAngle > 360.0F) {
            this.fanAngle -= 360.0F;
         }

         if (scooterSoundSource == -1) {
            scooterSoundSource = SoundManager.addLoopingSource(SoundManager.sfxScooter, null);
            SoundManager.setLoopingSourcePitch(scooterSoundSource, 1.0F);
            SoundManager.setLoopingSourceVolume(scooterSoundSource, 0.2F);
         }

         if (!SoundManager.isLoopingSourcePlaying(scooterSoundSource)) {
            SoundManager.playLoopingSource(scooterSoundSource);
         }

         this.particleTimer += deltaTime;
         if (this.particleTimer > 0.1F) {
            this.particleTimer -= 0.1F;
            EnvironmentManager.addMovingParticle(
               new MovingParticle(
                  this.exhaustPos.plus(new Point((float)Math.random() - 0.5F, 0.0F, (float)Math.random() - 0.5F).scaled(5.0F)),
                  new Point((float)Math.random() - 0.5F, 1.0F, (float)Math.random() - 0.5F),
                  10.0F,
                  5.0F
               )
            );
         }
      } else {
         this.fanAngle += 360.0F * deltaTime * 0.5F;
         if (this.fanAngle > 360.0F) {
            this.fanAngle -= 360.0F;
         }

         SoundManager.stopLoopingSource(scooterSoundSource);
      }

      this.inUse = false;
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(2.6F, 1.5F, 0.2F);
      GL11.glScalef(2.5F, 2.5F, 2.5F);
      GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
      GL11.glPushMatrix();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, scooterTexture);
      scooterMesh.render();
      GL11.glRotatef(this.fanAngle, 0.0F, 0.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, fanTexture);
      fanMesh.render();
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }

   @Override
   public final void onUse(Point pos, Point forward, Point right, Point up) {
      this.inUse = true;
      this.thrustDir = forward.copy();
      this.thrustDir.normalize();
      this.exhaustPos = pos.plus(forward.scaled(10.0F)).plus(right.scaled(2.6F)).plus(up.scaled(-3.0F));
   }

   @Override
   public final void renderExtra() {
   }

   @Override
   public final void onDeselect() {
      SoundManager.stopLoopingSource(scooterSoundSource);
   }
}
