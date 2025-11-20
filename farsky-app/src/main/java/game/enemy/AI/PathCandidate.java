package game.enemy.AI;

import game.collision.CollisionDetector;
import game.util.Coord;
import game.util.Point;

public final class PathCandidate {
   private float clearance;
   private float directionWeight;
   private Coord direction;

   public PathCandidate(Point origin, Coord dir, float maxDist, float weight) {
      this.direction = dir.copy();
      this.directionWeight = weight;
      float hitDist = CollisionDetector.raycast(origin, new Point(dir.x, 0.0F, dir.y), 5000.0F);
      if (hitDist >= 0.0F) {
         this.clearance = hitDist / 5000.0F;
      } else {
         this.clearance = 1.0F;
      }
   }

   public final float getScore() {
      return this.clearance * this.directionWeight;
   }

   public final Coord getDirection() {
      return this.direction;
   }
}
