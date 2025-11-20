package game.player.weapons;

public enum WeaponType {
   KNIFE(0, 2, 0.0F),
   SPEARGUN(1, 0, 0.0F),
   STANDARD_DRILL(2, 0, 0.0F),
   SCOOTER(3, 0, 0.0F),
   OVERPOWERED_DRILL(4, 0, 0.0F),
   IRON_SPEAR(5, 3, 0.0F),
   IRON_STUN_SPEAR(6, 5, 0.3F),
   COPPER_SPEAR(7, 8, 0.0F),
   COPPER_STUN_SPEAR(8, 10, 0.3F),
   MANGANESE_SPEAR(9, 13, 0.0F),
   MANGANESE_STUN_SPEAR(10, 15, 0.3F);

   private int nb;
   private int damageValue;
   private float criticalHit;

   private WeaponType(int nb, int damageValue, float criticalHit) {
      this.nb = nb;
      this.damageValue = damageValue;
      this.criticalHit = criticalHit;
   }

   public final int getNumber() {
      return this.nb;
   }

   public final int getDamage() {
      return this.damageValue;
   }

   public final float getCriticalChance() {
      return this.criticalHit;
   }
}
