package game.player.weapons;

import game.player.damage.Damage;
import game.util.Segment;

public final class PendingHit {
   private Segment segment;
   private Damage damage;
   private float timeRemaining;

   public PendingHit(Segment segment, Damage damage, float timeRemaining) {
      this.segment = segment;
      this.damage = damage;
      this.timeRemaining = timeRemaining;
   }

   public final void update(float delta) {
      this.timeRemaining -= delta;
   }

   public final boolean isExpired() {
      return this.timeRemaining <= 0.0F;
   }

   public final Segment getSegment() {
      return this.segment;
   }

   public final Damage getDamage() {
      return this.damage;
   }
}
