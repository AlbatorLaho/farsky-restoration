package game.player.weapons;

import game.chunks.ChunkManager;
import game.environment.EnvironmentManager;
import game.gui.PlayerHud;
import game.manager.GameScene;
import game.player.damage.Damage;
import game.player.damage.DamageType;
import game.util.Point;
import java.util.ArrayList;

public abstract class Weapon {
   protected ArrayList<PendingHit> pendingHits;
   protected boolean attackReady;
   protected boolean fired;
   protected boolean ammoReady;
   protected boolean inUse;
   protected WeaponType weaponType;

   public Weapon() {
      this.pendingHits = new ArrayList<>();
      this.attackReady = true;
      this.fired = false;
      this.ammoReady = true;
      this.inUse = false;
   }

   public abstract void tick(float deltaTime);

   public abstract void render();

   public abstract void onUse(Point pos, Point forward, Point right, Point up);

   public abstract void onDeselect();

   public final void update(float deltaTime) {
      this.tick(deltaTime);

      for (int i = this.pendingHits.size() - 1; i >= 0; i--) {
         this.pendingHits.get(i).update(deltaTime);
         Damage totalDamage = GameScene.enemyManager.applyHit(this.pendingHits.get(i).getSegment(), this.pendingHits.get(i).getDamage());
         ChunkManager.traceSegment(this.pendingHits.get(i).getSegment());
         totalDamage.accumulate(EnvironmentManager.applyFishHit(this.pendingHits.get(i).getSegment(), this.pendingHits.get(i).getDamage()));
         if (totalDamage.getAmount() > 0.0F) {
            PlayerHud.addDamageEvent(totalDamage);
            this.pendingHits.remove(i);
         } else if (this.pendingHits.get(i).isExpired()) {
            this.pendingHits.remove(i);
         }
      }
   }

   public abstract void renderExtra();

   public final void clearFired() {
      this.fired = false;
   }

   public final void setAmmoReady() {
      this.ammoReady = true;
   }

   public final boolean canAttack() {
      return this.attackReady;
   }

   public final WeaponType getType() {
      return this.weaponType;
   }

   public final boolean hasFired() {
      return this.fired;
   }

   protected final Damage rollDamage() {
      Damage dmg = new Damage(this.weaponType.getDamage(), DamageType.NORMAL);
      if (Math.random() <= 0.1F) {
         dmg = new Damage(1.5F * this.weaponType.getDamage(), DamageType.CRITICAL);
      }

      return dmg;
   }
}
