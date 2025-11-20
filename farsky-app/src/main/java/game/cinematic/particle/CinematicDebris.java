package game.cinematic.particle;

import game.util.Point;

public class CinematicDebris {
   private Point position;
   private Point velocity;
   private boolean exploded;
   private float scale;

   public CinematicDebris(Point origin, Point direction, float scale) {
      this.position = origin.copy();
      this.velocity = direction.copy();
      this.scale = scale;
      this.exploded = false;
   }

   public final void update(float deltaTime) {
      this.velocity.addY(-5.0F * deltaTime / this.scale);
      this.position.add(this.velocity.scaled(deltaTime * this.scale));
      if (this.exploded) {
         this.velocity.addX(60.0F * deltaTime / this.scale);
         this.velocity.addY(-20.0F * deltaTime / this.scale);
      }
   }

   public final void explode() {
      this.exploded = true;
   }
}
