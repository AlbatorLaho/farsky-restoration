package game.enemy.AI;

import game.chunks.ChunkManager;
import game.collision.CollisionBox;
import game.collision.CollisionDetector;
import game.manager.GameScene;
import game.util.Coord;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class EnemyNavigator {
   private Point position;
   private Point direction;
   private Point target;
   private float bodyRadius;
   private boolean isDying = false;
   private float dyingProgress = 1.0F;
   private float distanceTraveled = 0.0F;
   private Intelligence intelligence;
   private float avoidSpeed = 0.0F;
   private boolean isAvoiding = false;

   public EnemyNavigator(Point position, float bodyRadius, Intelligence intelligence) {
      this.position = position.copy();
      this.target = position.copy();
      this.direction = GameScene.avatar.getCameraPos().minus(position);
      this.bodyRadius = bodyRadius;
      this.intelligence = intelligence;
   }

   public final void navigate(float deltaTime, float speed) {
      if (this.isDying) {
         this.dyingProgress -= deltaTime;
         speed *= 0.3F;
      }

      if (this.distanceTraveled >= 100.0F) {
         this.recalculatePath();
      }

      Point prevDir = this.direction.copy();
      CollisionBox collBox = GameScene.getCollisionBoxAt(this.position);
      if (collBox != null) {
         this.direction = new Point(this.position.x - collBox.getPosition().x, 0.0F, this.position.z - collBox.getPosition().z);
         this.direction.normalize();
         this.avoidSpeed += deltaTime * 150.0F;
         speed = this.avoidSpeed;
         this.isAvoiding = true;
      } else {
         this.avoidSpeed = speed;
         this.isAvoiding = false;
         this.direction = this.target.minus(this.position);
         this.direction.normalize();
      }

      if (!this.isAvoiding && !(this.position.y < ChunkManager.getHeight(this.position.x, this.position.z) + this.bodyRadius / 4.0F)) {
         this.direction = this.direction.scaled(0.02F).plus(prevDir.scaled(0.98F));
         this.direction.normalize();
      } else {
         this.direction = this.direction.scaled(0.1F).plus(prevDir.scaled(0.9F));
         this.direction.normalize();
      }

      this.position = this.position.plus(this.direction.scaled(speed * deltaTime));
      this.distanceTraveled += speed * deltaTime;
      if (this.intelligence == Intelligence.AGGRESSIVE && this.position.y < ChunkManager.getHeight(this.position.x, this.position.z) + this.bodyRadius / 2.0F) {
         this.recalculatePath();
      }

      if (this.position.y < ChunkManager.getHeight(this.position.x, this.position.z) + this.bodyRadius / 10.0F) {
         this.position.y = ChunkManager.getHeight(this.position.x, this.position.z) + this.bodyRadius / 10.0F;
      }
   }

   private void recalculatePath() {
      Coord nextTarget = new Coord();
      Coord dir2D = new Coord(this.direction.x, this.direction.z);
      if (dir2D.x == 0.0F && dir2D.y == 0.0F) {
         dir2D.y = 1.0F;
      }

      dir2D.normalize();
      ArrayList<PathCandidate> candidates = new ArrayList<>();
      float angle = 0.0F;
      boolean found = false;

      for (int i = 0; i < 8; i++) {
         switch (i) {
            case 0:
               angle = 0.0F;
               break;
            case 1:
               angle = (float) (Math.PI / 4);
               break;
            case 2:
               angle = (float) (-Math.PI / 4);
               break;
            case 3:
               angle = (float) (Math.PI / 2);
               break;
            case 4:
               angle = (float) (-Math.PI / 2);
               break;
            case 5:
               angle = (float) (Math.PI * 3.0 / 4.0);
               break;
            case 6:
               angle = (float) (-Math.PI * 3.0 / 4.0);
               break;
            case 7:
               angle = (float) Math.PI;
         }

         float jitter;
         if (this.intelligence == Intelligence.AGGRESSIVE) {
            jitter = (float)(angle + (Math.random() - 0.5) / 8.0);
         } else {
            jitter = angle;
         }

         dir2D.rotateZRad(jitter);
         candidates.add(new PathCandidate(this.position, dir2D, 5000.0F, (1.0F - Math.abs(angle / (float) Math.PI)) * 0.5F + 0.5F));
         dir2D.rotateZRad(-jitter);
         if (this.intelligence == Intelligence.PASSIVE && candidates.get(candidates.size() - 1).getScore() >= 0.5F) {
            found = true;
            Coord passiveDir = candidates.get(candidates.size() - 1).getDirection();
            nextTarget = this.position.toCoord().plus(passiveDir.toPixels(100.0F));
            break;
         }
      }

      if (!found) {
         int bestIdx = 0;
         float bestScore = 0.0F;

         for (int i = 0; i < candidates.size(); i++) {
            float score = candidates.get(i).getScore();
            if (bestScore < score) {
               bestScore = score;
               bestIdx = i;
            }
         }

         Coord bestDir = candidates.get(bestIdx).getDirection();
         nextTarget = this.position.toCoord().plus(bestDir.toPixels(100.0F));
      }

      if (this.isDying) {
         nextTarget = this.position.toCoord().plus(dir2D.toPixels(100.0F));
      }

      this.target = new Point(nextTarget.x, Math.max(ChunkManager.getHeight(nextTarget.x, nextTarget.y) + this.bodyRadius, this.position.y - this.bodyRadius * 0.1F), nextTarget.y);
      this.distanceTraveled = 0.0F;
   }

   public final boolean trySetTarget(Point target) {
      Point dir = target.minus(this.position);
      dir.normalize();
      if (!this.isDying && !this.isAvoiding && CollisionDetector.raycast(this.position, dir, target.distanceTo(this.position)) == -1.0F) {
         this.target = target.copy();
         this.direction = dir.scaled(0.1F).plus(this.direction.scaled(0.9F));
         this.direction.normalize();
         this.distanceTraveled = 0.0F;
         return true;
      } else {
         return false;
      }
   }

   public final void startDying() {
      if (!this.isDying) {
         this.isDying = true;
         this.dyingProgress = 2.0F;
      }
   }

   public final boolean isDyingComplete() {
      return this.isDying && this.dyingProgress <= 0.0F;
   }

   public final boolean isDying() {
      return this.isDying;
   }

   public final void setPosition(Point pos) {
      this.position = pos.copy();
   }

   public final void setDirection(Point dir) {
      this.direction = dir.copy();
   }

   public final Point getPosition() {
      return this.position;
   }

   public final Point getDirection() {
      return this.direction;
   }

   public final float getDyingProgress() {
      return this.dyingProgress;
   }

   public final void applyGLRotation() {
      this.direction.applyGLRotation();
      GL11.glRotatef(!this.isDying ? 0.0F : (2.0F - this.dyingProgress) / 2.0F * 180.0F, 0.0F, 0.0F, 1.0F);
   }
}
