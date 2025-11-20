package game.player.weapons;

import game.environment.EnvironmentManager;
import game.inventory.Item;
import game.manager.GameScene;
import game.render.ModelLoader;
import game.render.Vbo;
import game.sounds.SoundManager;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class HarpoonGun extends Weapon {
   private static int harpoonTexture;
   private static Vbo harpoonMesh;
   public static Item loadedAmmo;
   private static Arrow ironSpearDisplay;
   private static Arrow ironStunSpearDisplay;
   private static Arrow copperSpearDisplay;
   private static Arrow copperStunSpearDisplay;
   private static Arrow manganeseSpearDisplay;
   private static Arrow manganeseStunSpearDisplay;

   public HarpoonGun() {
      this.weaponType = WeaponType.SPEARGUN;
      ironSpearDisplay = new Arrow(new Point(), new Point(0.0F, 0.0F, -1.0F), WeaponType.IRON_SPEAR);
      ironStunSpearDisplay = new Arrow(new Point(), new Point(0.0F, 0.0F, -1.0F), WeaponType.IRON_STUN_SPEAR);
      copperSpearDisplay = new Arrow(new Point(), new Point(0.0F, 0.0F, -1.0F), WeaponType.COPPER_SPEAR);
      copperStunSpearDisplay = new Arrow(new Point(), new Point(0.0F, 0.0F, -1.0F), WeaponType.COPPER_STUN_SPEAR);
      manganeseSpearDisplay = new Arrow(new Point(), new Point(0.0F, 0.0F, -1.0F), WeaponType.MANGANESE_SPEAR);
      manganeseStunSpearDisplay = new Arrow(new Point(), new Point(0.0F, 0.0F, -1.0F), WeaponType.MANGANESE_STUN_SPEAR);
   }

   public static void loadAssets() {
      harpoonMesh = ModelLoader.loadMesh("harpoon");
      harpoonTexture = ModelLoader.loadTexture("harpoon");
   }

   @Override
   public final void tick(float deltaTime) {
      ironStunSpearDisplay.updateGlows(deltaTime);
      copperStunSpearDisplay.updateGlows(deltaTime);
      manganeseStunSpearDisplay.updateGlows(deltaTime);
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.3F, 0.03F, -1.1F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, harpoonTexture);
      GL11.glScalef(5.5F, 5.5F, 5.5F);
      harpoonMesh.render();
      GL11.glPopMatrix();
   }

   @Override
   public final void renderExtra() {
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.3F, 2.5F, -2.0F);
      if (getAmmoCount() > 0 && this.ammoReady) {
         switch (loadedAmmo.getType()) {
            case IRON_SPEAR:
               ironSpearDisplay.render();
               break;
            case IRON_STUN_SPEAR:
               ironStunSpearDisplay.render();
               break;
            case COPPER_SPEAR:
               copperSpearDisplay.render();
               break;
            case COPPER_STUN_SPEAR:
               copperStunSpearDisplay.render();
               break;
            case MANGANESE_SPEAR:
               manganeseSpearDisplay.render();
               break;
            case MANGANESE_STUN_SPEAR:
               manganeseStunSpearDisplay.render();
			default:
				break;
         }
      }

      GL11.glPopMatrix();
   }

   @Override
   public final void onUse(Point pos, Point forward, Point right, Point up) {
      if (getAmmoCount() > 0) {
         switch (loadedAmmo.getType()) {
            case IRON_SPEAR:
               EnvironmentManager.addArrow(new Arrow(pos.plus(0.0F, 0.4F, 0.0F), forward, WeaponType.IRON_SPEAR, true));
               break;
            case IRON_STUN_SPEAR:
               EnvironmentManager.addArrow(new Arrow(pos.plus(0.0F, 0.4F, 0.0F), forward, WeaponType.IRON_STUN_SPEAR, true));
               break;
            case COPPER_SPEAR:
               EnvironmentManager.addArrow(new Arrow(pos.plus(0.0F, 0.4F, 0.0F), forward, WeaponType.COPPER_SPEAR, true));
               break;
            case COPPER_STUN_SPEAR:
               EnvironmentManager.addArrow(new Arrow(pos.plus(0.0F, 0.4F, 0.0F), forward, WeaponType.COPPER_STUN_SPEAR, true));
               break;
            case MANGANESE_SPEAR:
               EnvironmentManager.addArrow(new Arrow(pos.plus(0.0F, 0.4F, 0.0F), forward, WeaponType.MANGANESE_SPEAR, true));
               break;
            case MANGANESE_STUN_SPEAR:
               EnvironmentManager.addArrow(new Arrow(pos.plus(0.0F, 0.4F, 0.0F), forward, WeaponType.MANGANESE_STUN_SPEAR, true));
			default:
				break;
         }

         GameScene.stats.recordSpearShot();
         SoundManager.playSound(SoundManager.sfxHarpoon, null, (float)Math.random() * 0.2F + 0.9F, 0.3F);
         loadedAmmo.consume();
         if (loadedAmmo.getCount() == 0) {
            loadedAmmo = null;
            GameScene.avatar.clearEmptyInventorySlots();
            return;
         }

         this.fired = true;
         this.ammoReady = false;
      }
   }

   public static int getAmmoCount() {
      return loadedAmmo == null ? 0 : loadedAmmo.getCount();
   }

   @Override
   public final void onDeselect() {
   }
}
