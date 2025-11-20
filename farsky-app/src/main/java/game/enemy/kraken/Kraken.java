package game.enemy.kraken;

import game.collision.AABB;
import game.collision.CollisionDetector;
import game.enemy.Enemy;
import game.enemy.EnemyType;
import game.enemy.AI.EnemyNavigator;
import game.enemy.AI.Intelligence;
import game.environment.BloodParticles;
import game.environment.EnvironmentManager;
import game.gui.dialog.DialogManager;
import game.inventory.types.Inventory;
import game.manager.InGameState;
import game.manager.Loading;
import game.player.damage.Damage;
import game.manager.Camera;
import game.manager.GameScene;
import game.manager.GameTime;
import game.seafloorBase.SeafloorBase;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Point;
import game.util.Segment;
import game.util.State;
import game.world.structure.GamePlayElmt;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class Kraken extends Enemy {
   static enum KrakenState {
      ROAMING,
      DORMANT,
      IDLE,
      RISING,
      CHARGING,
      TARGETING_BASE,
      REPELLED;
   }

   private KrakenState krakenState;
   private float timer;
   private float idleDuration;
   private EnemyNavigator navigator;
   private boolean wasHit;
   private Point baseTarget;
   private SeafloorBase targetBase;
   private State prevCollState;
   private State curCollState;
   private ArrayList<KrakenTentacle> tentacles;
   @SuppressWarnings("unused")
   private KrakenBody body;
   private AABB hitbox;
   private int soundSource;
   private Point spawnPos;
   private int chunkX;
   private int chunkZ;

   public Kraken(Point spawnPos, int chunkX, int chunkZ, boolean isDormant) {
      this.type = EnemyType.KRAKEN;
      this.spawnPos = spawnPos.copy();
      this.position = spawnPos.plus(0.0F, 100.0F, 0.0F);
      if (!isDormant) {
         this.chunkX = -1;
         this.chunkZ = -1;
         this.krakenState = KrakenState.ROAMING;
      } else {
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
         this.krakenState = KrakenState.DORMANT;
         if (GameScene.getInGameState() == InGameState.BOSS_KILLED) {
            this.krakenState = KrakenState.IDLE;
         }
      }

      this.timer = 0.0F;
      this.navigator = new EnemyNavigator(this.position, 150.0F, Intelligence.AGGRESSIVE);
      this.idleDuration = 10.0F + (float)Math.random() * 10.0F;
      this.aggressive = false;
      this.tentacles = new ArrayList<>();

      for (int i = 0; i < 8; i++) {
         float angle = i * 360.0F / 8.0F + 22.5F;
         this.tentacles.add(new KrakenTentacle(this, 90.0F - angle, 0.0F));
      }

      this.body = new KrakenBody();
      this.hitbox = new AABB(new Point(0.0F, 0.0F, -17.0F), 42.5F, 42.5F, 85.0F);
      this.soundSource = SoundManager.addLoopingSource(SoundManager.sfxMovement, this.position);
      SoundManager.playLoopingSource(this.soundSource);
      SoundManager.setLoopingSourceVolume(this.soundSource, 0.3F);
      SoundManager.setLoopingSourcePitch(this.soundSource, 1.4F);
      this.maxHealth = 45.0F;
      this.health = this.maxHealth;
   }

   @Override
   public final void update(float deltaTime) {
      this.aggressive = this.krakenState != KrakenState.DORMANT && this.krakenState != KrakenState.ROAMING;

      if (this.health == 0.0F) {
         this.krakenState = KrakenState.IDLE;
      }

      switch (this.krakenState) {
         case ROAMING:
            this.navigator.navigate(deltaTime, 70.0F);
            this.position = this.navigator.getPosition();

            for (int i = 0; i < this.tentacles.size(); i++) {
               this.tentacles.get(i).moveToAngle(-10.0F, deltaTime, 100.0F);
            }

            if (this.wasHit) {
               GameScene.onBossKilled();
               SoundManager.playSound(SoundManager.sfxEnemyScream, this.position, 0.7F, 0.3F);
               Camera.addShake(1.0F);
               this.krakenState = KrakenState.IDLE;
            }
            break;
         case DORMANT:
            this.navigator.navigate(deltaTime, 70.0F);
            this.position = this.navigator.getPosition();

            for (int i = 0; i < this.tentacles.size(); i++) {
               this.tentacles.get(i).moveToAngle(-10.0F, deltaTime, 100.0F);
            }

            if (this.wasHit) {
               GameScene.onBossKilled();
            }

            if (GameScene.getInGameState() == InGameState.BOSS_KILLED) {
               SoundManager.playSound(SoundManager.sfxEnemyScream, this.position, 0.7F, 0.3F);
               Camera.addShake(1.0F);
               this.krakenState = KrakenState.IDLE;
            }
            break;
         case IDLE:
            this.navigator.navigate(deltaTime, 70.0F);
            this.position = this.navigator.getPosition();

            for (int i = 0; i < this.tentacles.size(); i++) {
               this.tentacles.get(i).moveToAngle(-10.0F, deltaTime, 100.0F);
            }

            this.timer += deltaTime;
            if (this.timer >= this.idleDuration) {
               this.krakenState = KrakenState.RISING;
               this.timer = 0.0F;
               this.idleDuration = 10.0F + (float)Math.random() * 10.0F;
            }
            break;
         case RISING:
            Point upDir = new Point(0.0F, 1.0F, 0.0F);
            this.navigator.setDirection(upDir.scaled(0.05F).plus(this.navigator.getDirection().scaled(0.95F)));

            for (int i = 0; i < this.tentacles.size(); i++) {
               this.tentacles.get(i).moveToAngle(30.0F, deltaTime, 100.0F);
            }

            SoundManager.pauseLoopingSource(this.soundSource);
            if (this.health <= 0.0F) {
               this.krakenState = KrakenState.IDLE;
            }

            this.timer += deltaTime;
            if (this.timer >= 5.0F) {
               this.krakenState = KrakenState.CHARGING;
               ArrayList<Integer> nearbyBases = new ArrayList<>();

               for (int i = 0; i < GameScene.getSeafloorBases().size(); i++) {
                  if (this.position.distanceTo(GameScene.getSeafloorBases().get(i).getPos()) < 400.0F) {
                     nearbyBases.add(i);
                  }
               }

               if (nearbyBases.size() > 0 && (GameScene.avatar.isInside() || GameScene.avatar.isInSeafloorBase() || !(Math.random() < 0.33F))) {
                  this.targetBase = GameScene.getSeafloorBases().get(nearbyBases.get((int)(nearbyBases.size() * Math.random())));
                  this.baseTarget = this.targetBase.getInteractionPoint();
                  this.krakenState = KrakenState.TARGETING_BASE;
                  this.prevCollState = new State();
                  this.curCollState = new State();
                  this.prevCollState.pos = this.position.copy();
                  this.curCollState.pos = this.position.copy();
                  if (this.baseTarget.distanceTo(this.position) > 400.0F) {
                     this.krakenState = KrakenState.CHARGING;
                  }
               }

               this.timer = 0.0F;
               SoundManager.playLoopingSource(this.soundSource);
            }
            break;
         case CHARGING:
            for (int i = 0; i < this.tentacles.size(); i++) {
               this.tentacles.get(i).moveToAngle(-20.0F, deltaTime, 200.0F);
            }

            this.navigator.trySetTarget(GameScene.avatar.getCameraPos());
            this.navigator.navigate(deltaTime, 300.0F);
            this.position = this.navigator.getPosition();
            this.timer += deltaTime;
            if (this.timer >= 6.0F || this.wasHit || GameScene.avatar.isInside()) {
               this.krakenState = KrakenState.IDLE;
               this.timer = 0.0F;
            }

            if (CollisionDetector.containsPoint(GameScene.avatar.getCameraPos(), this.hitbox, this.position, this.navigator.getDirection().toAngles())) {
               GameScene.avatar.takeDamage(30.0F, "You were killed by a kraken");
               GameScene.avatar.applyImpulse(new Point(0.0F, 1.0F, 0.0F).scaled(75.0F));
               this.krakenState = KrakenState.IDLE;
               this.timer = 0.0F;
            }
            break;
         case TARGETING_BASE:
            for (int i = 0; i < this.tentacles.size(); i++) {
               this.tentacles.get(i).moveToAngle(-20.0F, deltaTime, 200.0F);
            }

            this.navigator.trySetTarget(this.baseTarget);
            this.navigator.navigate(deltaTime, 300.0F);
            this.position = this.navigator.getPosition();
            this.prevCollState.pos = this.curCollState.pos.copy();
            this.curCollState.pos = this.position.copy();
            this.curCollState = this.targetBase.resolveCollision(this.prevCollState, this.curCollState);
            if (this.baseTarget.distanceTo(this.position) < 10.0F) {
               this.krakenState = KrakenState.IDLE;
            }

            if (!this.curCollState.pos.equals(this.position)) {
               this.krakenState = KrakenState.REPELLED;
            }
            break;
         case REPELLED:
            this.navigator.setDirection(this.navigator.getDirection().scaled(-1.0F));
            this.navigator.setPosition(this.navigator.getPosition().plus(0.0F, this.hitbox.getHeight(), 0.0F));
            this.krakenState = KrakenState.IDLE;
            this.targetBase.triggerBuild();
            DialogManager.waterFloodTriggered = true;
      }

      if (this.krakenState != KrakenState.ROAMING) {
         if (this.krakenState == KrakenState.TARGETING_BASE) {
            this.steerToward(GameScene.avatar.getCameraPos(), 400.0F);
         } else if (this.krakenState == KrakenState.DORMANT) {
            this.steerToward(this.spawnPos, 150.0F);
         } else {
            this.steerToward(GameScene.avatar.getCameraPos(), 300.0F);
         }
      }

      this.wasHit = false;
      if (!this.dead) {
         SoundManager.setLoopingSourcePosition(this.soundSource, this.position);
      }
   }

   private void steerToward(Point target, float minDist) {
      if (target.distanceTo(this.position) >= minDist) {
         Point dir = target.minus(this.position);
         dir.normalize();
         target = dir.scaled(0.03F).plus(this.navigator.getDirection().scaled(0.97F));
         target.normalize();
         this.navigator.setDirection(target);
      }
   }

   @Override
   public final void setupRender() {
      if (this.krakenState != KrakenState.DORMANT && this.krakenState != KrakenState.ROAMING) {
         Shaders.setUniform("alphaLightcolor", new Point(0.8F, 0.4F, 0.4F));
      } else {
         Shaders.setUniform("alphaLightcolor", new Point(0.0F, 0.5F, 0.5F));
      }

      Shaders.setUniform("alphaLightPercent", 0.5);
   }

   @Override
   public final void renderBody() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y + (float)Math.cos(GameTime.elapsedMillis / 500.0F) * 5.0F, this.position.z);
      this.navigator.applyGLRotation();
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(1.7F, 1.7F, 1.7F);
      KrakenTentacle.setupRender();

      for (int i = 0; i < this.tentacles.size(); i++) {
         this.tentacles.get(i).render();
      }

      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("axisSign", new Point(0.0F, 0.0F, 0.0F));
      KrakenBody.render();
      GL11.glPopMatrix();
   }

   @Override
   public final void cleanupRender() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public final void setupExtraRender() {
   }

   @Override
   public final void renderExtra() {
   }

   @Override
   public final void cleanupExtraRender() {
   }

   @Override
   public final void setTarget(Point target) {
   }

   @Override
   public final void onRemove() {
      SoundManager.removeLoopingSource(this.soundSource);
   }

   @Override
   public final Damage checkHit(ArrayList<Segment> segments, Damage damage) {
      Damage result = new Damage();
      boolean hit = accumulateSegmentHits(segments, this.hitbox, this.position, this.navigator.getDirection().toAngles(), result);

      if (hit) {
         EnvironmentManager.addBloodParticles(new BloodParticles(result.getSource(), 7));
         this.wasHit = true;
         this.hitFlash = 1.0F;
         this.health = this.health - damage.getAmount();
         result.accumulate(damage);
         if (this.health <= 0.0F) {
            this.health = 0.0F;
            ArrayList<Segment> deathSegs = new ArrayList<>();
            deathSegs.add(new Segment(this.position, this.position.plus(0.0F, 34.0F, 0.0F)));
            this.die(deathSegs);
            SoundManager.playSound(SoundManager.sfxKrakenDie, this.position, 0.7F, 1.0F);
            GamePlayElmt gameElmt = Loading.worldManager.getGamePlayElmtAt(this.chunkX, this.chunkZ);
            Inventory inventory = gameElmt.getInventory();
            if (inventory != null && inventory.getStorageArray().get(0, 0).getItem() != null) {
               inventory.getStorageArray().get(0, 0).getItem().setCount(inventory.getStorageArray().get(0, 0).getItem().getCount() - 1);
               gameElmt.setInventory(inventory);
            }

            SoundManager.removeLoopingSource(this.soundSource);
         }
      }

      return result;
   }

   @Override
   public final boolean isAtChunk(int chunkX, int chunkZ) {
      return this.chunkX == chunkX && this.chunkZ == chunkZ;
   }
}
