package game.player.droid;

import game.chunks.ChunkManager;
import game.collision.CollisionBox;
import game.enemy.AI.PathCandidate;
import game.manager.GameScene;
import game.util.Coord;
import game.util.Point;
import java.io.Serializable;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public class AIdroid implements Serializable {
   private static final long serialVersionUID = 8712744820994430937L;
   private Point pos;
   private Point dir;
   private Point goal;
   private Point stayPos;
   private float distTraveled = 0.0F;
   private float speed = 60.0F;
   private float speedGoal = 60.0F;
   private DroidState state = DroidState.IDLE;
   private transient boolean isAvoiding = false;
   private transient Point targetEnemy;

   public AIdroid(Point startPos) {
      this.pos = startPos.copy();
      this.goal = startPos.copy();
      this.dir = new Point(Math.random() - 0.5, 1.0, Math.random() - 0.5);
      this.dir.normalize();
      this.distTraveled = 100.0F;
   }

   public final void update(float delta) {
      if (this.distTraveled >= 100.0F) {
         this.pickNewGoal();
      }

      this.speed = this.speed + Math.signum(this.speedGoal - this.speed) * delta * 20.0F;
      Point prevDir = this.dir.copy();
      CollisionBox collisionBox = GameScene.getCollisionBoxAt(this.pos);
      if (collisionBox != null) {
         this.dir = new Point(this.pos.x - collisionBox.getPosition().x, 0.0F, this.pos.z - collisionBox.getPosition().z);
         this.dir.normalize();
         this.isAvoiding = true;
      } else {
         this.isAvoiding = false;
         this.dir = this.goal.minus(this.pos);
         this.dir.normalize();
      }

      if (!this.isAvoiding && !(this.pos.y < ChunkManager.getHeight(this.pos.x, this.pos.z) + 15.0F)) {
         this.dir = this.dir.scaled(0.04F).plus(prevDir.scaled(0.96F));
         this.dir.normalize();
      } else {
         this.dir = this.dir.scaled(0.1F).plus(prevDir.scaled(0.9F));
         this.dir.normalize();
      }

      if (this.state == DroidState.HARVESTING || this.state == DroidState.FOLLOWING) {
         this.dir.y *= 0.8F;
      }

      this.pos = this.pos.plus(this.dir.scaled(this.speed * delta));
      this.distTraveled = this.distTraveled + this.speed * delta;
      if (this.pos.y < ChunkManager.getHeight(this.pos.x, this.pos.z) + 30.0F) {
         this.pos.y += delta * 20.0F;
      }
   }

   private void pickNewGoal() {
      if (GameScene.avatar.getCameraPos() != null) {
         if (this.state != DroidState.FOLLOWING && GameScene.avatar.getCameraPos().distanceTo(this.pos) > 700.0F) {
            this.pos = GameScene.avatar.getCameraPos().plus(new Point(Math.sin(Math.toRadians(GameScene.avatar.getHorizontalAngle())) * 150.0, 0.0, Math.cos(Math.toRadians(GameScene.avatar.getHorizontalAngle())) * 150.0));
         }

         switch (this.state) {
            case IDLE:
               boolean tooFar = GameScene.avatar.getCameraPos().distanceTo(this.pos) > 150.0F;
               Point playerPos = GameScene.avatar.getCameraPos();
               if (this.state != DroidState.FOLLOWING) {
                  Coord currentDir = new Coord(this.dir.x, this.dir.z);
                  if (currentDir.x == 0.0F && currentDir.y == 0.0F) {
                     currentDir.y = 1.0F;
                  }

                  currentDir.normalize();
                  ArrayList<PathCandidate> candidates = new ArrayList<>();
                  Coord bestCoord = null;
                  Coord toPlayer = playerPos.toCoord().minus(this.pos.toCoord());
                  toPlayer.normalize();
                  float angle = 0.0F;

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

                     float jitteredAngle = (float)(angle + (Math.random() - 0.5) / 8.0);
                     currentDir.rotateZRad(jitteredAngle);
                     float score;
                     if (tooFar) {
                        score = toPlayer.dot(currentDir);
                        if (score < 0.0F) {
                           score = 0.0F;
                        }
                     } else {
                        score = (1.0F - Math.abs(angle / (float) Math.PI)) * 0.5F + 0.5F;
                     }

                     candidates.add(new PathCandidate(this.pos, currentDir, 5000.0F, score));
                     currentDir.rotateZRad(-jitteredAngle);
                  }

                  int bestIdx = 0;
                  float bestScore = 0.0F;

                  for (int j = 0; j < candidates.size(); j++) {
                     float candidateScore = candidates.get(j).getScore();
                     if (bestScore < candidateScore) {
                        bestScore = candidateScore;
                        bestIdx = j;
                     }
                  }

                  Coord bestDir = candidates.get(bestIdx).getDirection();
                  bestCoord = this.pos.toCoord().plus(bestDir.toPixels(100.0F));
                  this.goal = new Point(bestCoord.x, Math.max(ChunkManager.getHeight(bestCoord.x, bestCoord.y) + 60.0F, this.pos.y - 24.0F), bestCoord.y);
                  this.distTraveled = 0.0F;
               }

               for (int k = 0; k < GameScene.enemyManager.getEnemies().size(); k++) {
                  if (GameScene.enemyManager.getEnemies().get(k).isAggressive()) {
                     this.state = DroidState.NAVIGATING;
                     break;
                  }
               }

               if (GameScene.avatar.getCameraPos().distanceTo(this.pos) > 150.0F) {
                  this.speedGoal = 60.0F;
                  return;
               }

               this.speedGoal = 30.0F;
               return;
            case ATTACKING:
               this.goal = GameScene.avatar.getCameraPos();
               this.speedGoal = 20.0F;
               return;
            case HARVESTING:
               this.goal = this.stayPos;
               this.speedGoal = 2.0F;
               return;
            case FOLLOWING:
               this.goal = this.stayPos;
               this.speedGoal = 2.0F;
               return;
            case NAVIGATING:
               this.speedGoal = 60.0F;
               this.targetEnemy = null;

               for (int i = 0; i < GameScene.enemyManager.getEnemies().size(); i++) {
                  if (GameScene.enemyManager.getEnemies().get(i).isAggressive()) {
                     this.targetEnemy = GameScene.enemyManager.getEnemies().get(i).getPosition();
                  }
               }

               if (this.targetEnemy == null) {
                  this.state = DroidState.IDLE;
                  return;
               } else {
                  this.goal = this.targetEnemy.copy();
               }
         }
      }
   }

   public final void applyTransform() {
      GL11.glTranslatef(this.pos.x, this.pos.y, this.pos.z);
      this.dir.applyGLRotation();
   }

   public final DroidState getState() {
      return this.state;
   }

   public final void setState(DroidState state) {
      this.state = state;
      if (this.state == DroidState.HARVESTING) {
         this.stayPos = GameScene.avatar.getCameraPos();
      }

      if (this.state == DroidState.ATTACKING) {
         this.pickNewGoal();
      }
   }

   public final Point getTargetPosition() {
      return this.targetEnemy == null ? this.pos : this.targetEnemy;
   }

   public final Point getPosition() {
      return this.pos;
   }
}
