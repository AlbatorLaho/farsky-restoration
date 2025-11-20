package game.player.damage;

public enum DamageType {
   NORMAL(0),
   ELECTRIC(1),
   CRITICAL(2);

   private int priority;

   private DamageType(int priority) {
      this.priority = priority;
   }

   public final DamageType highest(DamageType other) {
      return other.priority > this.priority ? other : this;
   }
}
