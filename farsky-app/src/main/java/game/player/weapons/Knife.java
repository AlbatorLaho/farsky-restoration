package game.player.weapons;

import game.render.ModelLoader;
import game.render.Vbo;
import game.sounds.SoundManager;
import game.util.Point;
import game.util.Segment;

import org.lwjgl.opengl.GL11;

public final class Knife extends Weapon {
   private static int knifeTexture;
   private static Vbo knifeMesh;
   private float attackCooldown = 0.0F;

   public Knife() {
      this.weaponType = WeaponType.KNIFE;
   }

   public static void loadAssets() {
      knifeMesh = ModelLoader.loadMesh("knife");
      knifeTexture = ModelLoader.loadTexture("knife");
   }

   @Override
   public final void tick(float deltaTime) {
      if (this.attackCooldown <= 0.0F) {
         this.attackCooldown = 0.0F;
         this.attackReady = true;
      } else {
         this.attackCooldown -= deltaTime;
      }
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.3F, 1.9F, -0.5F);
      GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, knifeTexture);
      GL11.glScalef(2.5F, 2.5F, 2.5F);
      knifeMesh.render();
      GL11.glPopMatrix();
   }

   @Override
   public final void onUse(Point pos, Point forward, Point right, Point up) {
      this.attackReady = false;
      this.attackCooldown = 0.6F;
      pos = pos.plus(right.scaled(3.0F)).plus(up.scaled(2.0F));
      this.pendingHits.add(new PendingHit(new Segment(pos, pos.plus(forward.scaled(15.0F))), this.rollDamage(), 0.2F));
      SoundManager.playSound(SoundManager.sfxKnife, null, (float)Math.random() * 0.3F + 0.75F, 0.15F);
   }

   @Override
   public final void renderExtra() {
   }

   @Override
   public final void onDeselect() {
   }
}
