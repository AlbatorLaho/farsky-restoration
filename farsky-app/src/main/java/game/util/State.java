package game.util;

import java.io.Serializable;

public class State implements Serializable {
   private static final long serialVersionUID = -2213322121949847434L;
   public Point pos;
   public Point vel;
   public transient boolean onGround = true;
   public transient boolean isMoving = false;
   public boolean player = false;

   public State() {
      this.pos = new Point();
      this.vel = new Point();
   }

   public final void copyFrom(State other) {
      this.pos.set(other.pos);
      this.vel.set(other.vel);
      this.onGround = other.onGround;
      this.isMoving = other.isMoving;
      this.player = other.player;
   }

   public final void land() {
      this.vel.y = 0.0F;
      this.onGround = true;
      this.isMoving = false;
   }

   public final void stopVertical() {
      this.vel.y = 0.0F;
   }

   public final void startMoving() {
      this.isMoving = true;
   }
}
