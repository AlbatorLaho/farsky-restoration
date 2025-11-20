package game.enemy;

import game.collision.AABB;
import game.collision.CollisionDetector;
import game.environment.DeathFragment;
import game.environment.EnvironmentManager;
import game.manager.GameScene;
import game.player.damage.Damage;
import game.util.Point;
import game.util.Segment;

import java.util.ArrayList;

public abstract class Enemy {
   protected EnemyType type;
   protected float health;
   protected float maxHealth;
   protected Point position;
   protected boolean aggressive = false;
   protected float hitFlash = 0.0F;
   protected boolean dead = false;
   private float deathTimer;
   private boolean readyForRemoval = false;

   public abstract void update(float deltaTime);

   public abstract void setupRender();

   public abstract void renderBody();

   public abstract void cleanupRender();

   public abstract void setupExtraRender();

   public abstract void renderExtra();

   public abstract void cleanupExtraRender();

   public abstract void setTarget(Point target);

   public abstract void onRemove();

   public abstract Damage checkHit(ArrayList<Segment> segments, Damage damage);

   public abstract boolean isAtChunk(int chunkX, int chunkZ);

   public final Damage applyHit(ArrayList<Segment> segments, Damage damage) {
      return this.dead ? new Damage() : this.checkHit(segments, damage);
   }

   public final void tick(float deltaTime) {
      if (GameScene.avatar != null && GameScene.avatar.isInside() && !this.type.isBoss() || this.dead) {
         this.aggressive = false;
      }

      if (this.dead) {
         if (this.deathTimer <= 0.0F) {
            this.readyForRemoval = true;
            this.deathTimer = 0.0F;
         } else {
            this.deathTimer -= deltaTime;
         }
      } else {
         this.update(deltaTime);
      }
   }

   public final void drawBody() {
      if (!this.dead) {
         this.renderBody();
      }
   }

   public void drawExtra() {
      if (!this.dead) {
         this.renderExtra();
      }
   }

   public Point getPosition() {
      return this.position;
   }

   public final EnemyType getType() {
      return this.type;
   }

   public final boolean isAggressive() {
      return this.aggressive;
   }

   protected static boolean accumulateSegmentHits(ArrayList<Segment> segments, AABB hitbox, Point position, Point rotation, Damage result) {
      boolean hit = false;

      for (int i = 0; i < segments.size(); i++) {
         if (CollisionDetector.segmentIntersects(segments.get(i), hitbox, position, rotation)) {
            hit = true;
            result.setSource(segments.get(i).start);
         }
      }

      return hit;
   }

   protected final void die(ArrayList<Segment> segments) {
      this.dead = true;
      this.deathTimer = 2.5F;
      GameScene.stats.recordPredatorKilled();
      if (segments != null) {
         for (int i = 0; i < segments.size(); i++) {
            for (float t = 0.0F; t < segments.get(i).length(); t += 0.3F) {
               Point segStart = segments.get(i).start;
               Segment seg = segments.get(i);
               Point dir = seg.end.minus(seg.start);
               dir.normalize();
               Point fragPos = segStart.plus(dir.scaled(t));
               EnvironmentManager.addDeathFragment(new DeathFragment(fragPos, 30.0F));
            }
         }
      }
   }

   public final boolean isReadyForRemoval() {
      return this.readyForRemoval;
   }
}
