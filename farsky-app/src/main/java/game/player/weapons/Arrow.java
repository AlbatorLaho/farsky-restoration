package game.player.weapons;

import game.chunks.ChunkManager;
import game.environment.EnvironmentManager;
import game.environment.particle.MovingParticle;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.player.damage.Damage;
import game.player.damage.DamageType;
import game.player.weapons.effect.ArrowGlow;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Point;
import game.util.Segment;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class Arrow {
   private static int arrowTexture;
   private static Vbo arrowMesh;
   private ArrayList<ArrowGlow> glows;
   private Point pos;
   private Point dir;
   private Damage damage;
   private boolean expired;
   private WeaponType weaponType;
   private boolean trackMiss;
   private float particleTimer;

   public static void loadAssets() {
      arrowMesh = ModelLoader.loadMesh("arrow");
      arrowTexture = ModelLoader.loadTexture("arrow");
   }

   public Arrow(Point pos, Point dir, WeaponType weaponType) {
      this(pos, dir, weaponType, false);
   }

   public Arrow(Point pos, Point dir, WeaponType weaponType, boolean trackMiss) {
      this.trackMiss = trackMiss;
      Damage dmg = new Damage(weaponType.getDamage(), DamageType.NORMAL);
      if (Math.random() <= weaponType.getCriticalChance()) {
         dmg = new Damage(1.5F * weaponType.getDamage(), DamageType.CRITICAL);
      }

      this.weaponType = weaponType;
      this.pos = pos.copy();
      this.dir = dir.copy();
      if (this.dir.equals(new Point())) {
         this.dir = new Point(0.0F, -1.0F, 0.0F);
      }

      this.dir.normalize();
      this.damage = dmg;
      this.expired = false;
      this.particleTimer = -((float)Math.random()) * 0.05F;
      this.glows = new ArrayList<>();
      if (weaponType == WeaponType.IRON_STUN_SPEAR || weaponType == WeaponType.COPPER_STUN_SPEAR || weaponType == WeaponType.MANGANESE_STUN_SPEAR) {
         for (int i = 0; i < 10; i++) {
            this.glows.add(new ArrowGlow());
         }
      }
   }

   public final void update(float delta) {
      this.particleTimer += delta;
      if (!this.expired && this.particleTimer >= 0.03F) {
         EnvironmentManager.addMovingParticle(
            new MovingParticle(
               this.pos.plus(new Point(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5)),
               new Point(Math.random() - 0.5, 1.0, Math.random() - 0.5),
               10.0F,
               1.5F
            )
         );
         this.particleTimer = (float)(this.particleTimer - (0.03F + Math.random() * 0.03F));
      }

      this.pos.add(this.dir.scaled(400.0F * delta));
      if (!this.expired) {
         if (this.pos.distanceTo(GameScene.avatar.getCameraPos()) > 1000.0F) {
            this.expired = true;
         }

         if (this.pos.y <= ChunkManager.getHeight(this.pos.x, this.pos.z)) {
            this.expired = true;
            SoundManager.playSound(SoundManager.sfxHurtFloor, this.pos, 1.0F, 0.4F);
         }

         if (this.expired && this.trackMiss) {
            GameScene.stats.recordShotLost();
         }
      }

      this.updateGlows(delta);
   }

   public final void updateGlows(float delta) {
      for (int i = 0; i < this.glows.size(); i++) {
         this.glows.get(i).update(delta);
      }
   }

   public final void render() {
      if (!this.expired) {
         Shaders.setUniform("emissive", true);
         GL11.glPushMatrix();
         GL11.glTranslatef(this.pos.x, this.pos.y, this.pos.z);
         this.dir.applyGLRotation();
         GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, arrowTexture);
         GL11.glScalef(5.5F, 5.5F, 5.5F);
         if (this.weaponType == WeaponType.IRON_STUN_SPEAR || this.weaponType == WeaponType.IRON_SPEAR) {
            GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
         }

         if (this.weaponType == WeaponType.COPPER_STUN_SPEAR || this.weaponType == WeaponType.COPPER_SPEAR) {
            GL11.glColor4f(0.9F, 0.6F, 0.3F, 1.0F);
         }

         if (this.weaponType == WeaponType.MANGANESE_STUN_SPEAR || this.weaponType == WeaponType.MANGANESE_SPEAR) {
            GL11.glColor4f(0.6F, 0.9F, 1.0F, 1.0F);
         }

         arrowMesh.render();
         GL11.glDepthMask(false);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.lightning);
         GL11.glPushMatrix();
         GL11.glScalef(0.25F, 0.25F, 1.0F);

         for (int i = 0; i < this.glows.size(); i++) {
            this.glows.get(i).render();
         }

         GL11.glPopMatrix();
         GL11.glDepthMask(true);
         GL11.glPopMatrix();
         Shaders.setUniform("emissive", false);
      }
   }

   public final Segment getSegment() {
      return new Segment(this.pos, this.pos.plus(this.dir.scaled(3.7F)));
   }

   public final Damage getDamage() {
      return this.damage;
   }

   public final boolean isExpired() {
      return this.expired;
   }

   public final boolean isSpent() {
      return this.expired;
   }

   public final void expire() {
      this.expired = true;
   }
}
