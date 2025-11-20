package game.player.damage;

import game.util.Point;

public final class Damage {
   private float amount;
   private DamageType type;
   private Point source;

   public Damage() {
      this.amount = 0.0F;
      this.type = DamageType.NORMAL;
      this.source = new Point();
   }

   public Damage(float amount, DamageType type) {
      this(amount, type, new Point());
   }

   private Damage(float amount, DamageType type, Point source) {
      this.amount = amount;
      this.type = type;
      this.source = source;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final DamageType getType() {
      return this.type;
   }

   public final Point getSource() {
      return this.source;
   }

   public final void setSource(Point point) {
      this.source = point.copy();
   }

   public final void accumulate(Damage other) {
      this.amount = this.amount + other.amount;
      if (other.amount > 0.0F) {
         this.type = this.type.highest(other.type);
      }

      if (!other.source.equals(new Point())) {
         this.source = other.source;
      }
   }
}
