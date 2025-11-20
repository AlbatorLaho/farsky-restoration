package game.environment.life;

import game.chunks.ChunkManager;
import game.collision.AABB;
import game.collision.CollisionBox;
import game.collision.CollisionDetector;
import game.environment.BloodParticles;
import game.environment.EnvironmentManager;
import game.environment.pickup.ItemPickup;
import game.manager.Camera;
import game.manager.GameScene;
import game.player.damage.Damage;
import game.util.Coord;
import game.util.Point;
import game.util.Segment;

import java.util.ArrayList;

public abstract class Fish {
   public static enum MovingState {
      ROAMING,
      SWIMMING,
      HEIGHT_LOCKED;
   }

   public float roamingRadius = 100.0F;
   protected Point position;
   protected Point prevDirection;
   protected Point rotation = new Point();
   private Point velocity;
   protected Coord direction;
   protected float speed;
   protected float targetSpeed;
   protected float heightOffset;
   protected float health = 1.0F;
   protected AABB boundingBox;
   protected FishType fishType;
   protected MovingState movingState = MovingState.SWIMMING;
   protected float lockedHeight = -700.0F;
   protected Coord spawnCenter;

   public final void update(float delta) {
      if (this.speed < this.targetSpeed) {
         this.speed = this.speed + Math.min(delta * 10.0F, this.targetSpeed - this.speed);
      } else if (this.speed > this.targetSpeed) {
         this.speed = this.speed - Math.min(delta * 10.0F, this.speed - this.targetSpeed);
      }

      switch (this.movingState) {
         case ROAMING:
            if (this.position.toCoord().distanceTo(this.spawnCenter) > this.roamingRadius) {
               Coord target = this.spawnCenter.plus(new Coord(((float)Math.random() - 0.5F) * 2.0F * this.roamingRadius, ((float)Math.random() - 0.5F) * 2.0F * this.roamingRadius));
               this.direction = target.minus(this.position.toCoord());
               this.direction.normalize();
            }
         case SWIMMING:
         case HEIGHT_LOCKED:
         default:
            Point nextPos = this.position.plus(new Point(this.direction.x * this.speed * delta, 0.0F, this.direction.y * this.speed * delta));
            nextPos.y = ChunkManager.getHeight(this.position.x, this.position.z) + this.heightOffset;
            CollisionBox collisionBox = GameScene.getCollisionBoxAt(this.position);
            if (collisionBox != null) {
               this.fleeFrom(collisionBox.getPosition(), false);
            }

            if (this.movingState == MovingState.HEIGHT_LOCKED) {
               nextPos.y = this.lockedHeight;
            }

            Point moveDir = nextPos.minus(this.position);
            moveDir.normalize();
            if (this.prevDirection != null) {
               moveDir = moveDir.scaled(0.03F).plus(this.prevDirection.scaled(0.93F));
            }

            this.position.add(moveDir.scaled(this.speed * delta));
            this.velocity = moveDir.scaled(this.speed * delta);
            if (this.prevDirection != null) {
               this.updateRotation();
            }

            this.prevDirection = moveDir.copy();
            this.onUpdate(delta);
      }
   }

   public static void setupDraw(FishType fishType) {
      if (fishType.isAbyss()) {
         AbyssalFish.setupDraw(fishType);
      } else {
         StandardFish.setupDraw(fishType);
      }
   }

   public abstract void draw();

   public abstract void onUpdate(float delta);

   public abstract ArrayList<ItemPickup> getDrops();

   protected abstract void updateRotation();

   protected abstract BloodParticles.BloodType getBloodType();

   public final AABB getWorldBoundingBox() {
      AABB box = new AABB();
      box.copyFrom(this.boundingBox);
      box.rotate(this.rotation.y);
      box.translate(this.position);
      return box;
   }

   public final Point getVelocity() {
      return this.velocity == null ? new Point() : this.velocity;
   }

   public final boolean shouldRemove() {
      return this.health <= 0.0F || new Coord(this.position.x, this.position.z).distanceTo(new Coord(Camera.getPosition().x, Camera.getPosition().z)) > ChunkManager.viewDistance * 1.2F;
   }

   public final void fleeFrom(Point source, boolean faster) {
      if (this.movingState != MovingState.HEIGHT_LOCKED) {
         this.movingState = MovingState.SWIMMING;
         if (faster) {
            this.targetSpeed *= 2.0F;
         }

         this.direction = new Coord(this.position.x - source.x, this.position.z - source.z);
         this.direction.normalize();
      }
   }

   public final Damage checkHit(ArrayList<Segment> segments, Damage weaponDamage) {
      boolean hit = false;
      Damage damage = new Damage();

      for (int i = 0; i < segments.size(); i++) {
         if (CollisionDetector.segmentIntersects(segments.get(i), this.boundingBox, this.position, this.rotation)) {
            hit = true;
            damage.setSource(segments.get(i).start);
         }
      }

      if (hit) {
         EnvironmentManager.addBloodParticles(new BloodParticles(damage.getSource(), 7, this.getBloodType()));
         damage.accumulate(weaponDamage);
         this.health = this.health - weaponDamage.getAmount();
         if (this.health < 0.0F) {
            this.health = 0.0F;
            GameScene.stats.recordFishKilled();
            EnvironmentManager.addItemPickups(this.getDrops());
         }
      }

      return damage;
   }

   public FishType getFishType() {
      return this.fishType;
   }
}
