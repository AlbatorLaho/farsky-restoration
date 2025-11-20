package game.enemy;

import game.chunks.ChunkManager;
import game.enemy.enemyWithMouth.Anglerfish;
import game.enemy.enemyWithMouth.Barracuda;
import game.enemy.enemyWithMouth.FrilledShark;
import game.enemy.enemyWithMouth.Shark;
import game.enemy.jellyFish.JellyFish;
import game.enemy.kraken.Kraken;
import game.enemy.lightning.AbyssalLightningFish;
import game.environment.DepthAtmosphere;
import game.manager.Camera;
import game.manager.GameScene;
import game.player.damage.Damage;
import game.util.Coord;
import game.util.Point;
import game.util.Segment;
import game.util.State;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class EnemyManager {
   public boolean bossPresent;
   private ArrayList<Enemy> enemies;
   private float despawnDist = DepthAtmosphere.getMaxFogDistance() + 400.0F;

   public EnemyManager() {
      this.bossPresent = false;
      this.enemies = new ArrayList<>();
   }

   public final void tick(float deltaTime) {
      GameScene.enemyGenerator.tick(deltaTime);
      if (GameScene.avatar != null) {
         this.bossPresent = false;

         for (int i = 0; i < this.enemies.size(); i++) {
            if (this.enemies.get(i).getType().isBoss()) {
               this.bossPresent = true;
               break;
            }
         }
      }

      for (int i = 0; i < this.enemies.size(); i++) {
         this.enemies.get(i).tick(deltaTime);
      }

      if (GameScene.avatar != null) {
         for (int i = this.enemies.size() - 1; i >= 0; i--) {
            if (this.enemies.get(i).getPosition().distanceTo(Camera.getPosition()) > this.despawnDist) {
               this.enemies.get(i).onRemove();
               this.enemies.remove(i);
            } else if (this.enemies.get(i).isReadyForRemoval()) {
               this.enemies.get(i).onRemove();
               this.enemies.remove(i);
            }
         }
      }
   }

   public final void draw() {
      EnemyType lastType = null;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

      for (int i = 0; i < this.enemies.size(); i++) {
         if (lastType == null || this.enemies.get(i).getType() != lastType) {
            if (i > 0) {
               this.enemies.get(i - 1).cleanupRender();
            }

            this.enemies.get(i).setupRender();
            lastType = this.enemies.get(i).getType();
         }

         this.enemies.get(i).drawBody();
      }

      if (this.enemies.size() > 0) {
         this.enemies.get(this.enemies.size() - 1).cleanupRender();
      }
   }

   public final void drawExtra() {
      EnemyType lastType = null;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

      for (int i = 0; i < this.enemies.size(); i++) {
         if (lastType == null || this.enemies.get(i).getType() != lastType) {
            if (i > 0) {
               this.enemies.get(i).cleanupExtraRender();
            }

            this.enemies.get(i).setupExtraRender();
            lastType = this.enemies.get(i).getType();
         }

         this.enemies.get(i).drawExtra();
      }

      if (this.enemies.size() > 0) {
         this.enemies.get(this.enemies.size() - 1).cleanupExtraRender();
      }
   }

   public final synchronized void add(Enemy enemy) {
      this.enemies.add(enemy);
   }

   public final Enemy spawn(Coord coord, EnemyType type) {
      Point spawnPos = new Point(coord.x, 0.0F, coord.y);
      spawnPos.y = ChunkManager.getHeight((int)spawnPos.x, (int)spawnPos.z);
      switch (type) {
         case JELLYFISH:
            this.enemies.add(new JellyFish(spawnPos));
            break;
         case SHARK:
         case HAMMERHEAD:
         case GREAT_WHITE:
            this.enemies.add(new Shark(spawnPos, type));
            break;
         case BARRACUDA:
            this.enemies.add(new Barracuda(spawnPos));
            break;
         case ANGLERFISH:
            this.enemies.add(new Anglerfish(spawnPos));
            break;
         case FRILLED_SHARK:
            this.enemies.add(new FrilledShark(spawnPos));
            break;
         case DEEP_SEA_FISH:
            this.enemies.add(new AbyssalLightningFish(spawnPos));
            break;
         case KRAKEN:
            this.enemies.add(new Kraken(spawnPos, -1, -1, false));
      }

      if (type.isBoss()) {
         this.bossPresent = true;
      }

      return this.enemies.get(this.enemies.size() - 1);
   }

   public final void removeAll() {
      for (int i = this.enemies.size() - 1; i >= 0; i--) {
         this.enemies.get(i).onRemove();
         this.enemies.remove(i);
      }
   }

   public final State resolveCollision(State prevState, State curState) {
      return curState;
   }

   public final Damage applyHit(Segment segment, Damage damage) {
      ArrayList<Segment> segments = new ArrayList<>();
      segments.add(segment);
      return this.applyHitToAll(segments, damage);
   }

   private Damage applyHitToAll(ArrayList<Segment> segments, Damage damage) {
      Damage result = new Damage();

      for (int i = 0; i < this.enemies.size(); i++) {
         result.accumulate(this.enemies.get(i).applyHit(segments, damage));
      }

      return result;
   }

   public final float getNearestBossDistance(Coord from) {
      float minDist = 9999999.0F;

      for (int i = 0; i < this.enemies.size(); i++) {
         float dist = this.enemies.get(i).getPosition().toCoord().distanceTo(from);
         if (this.enemies.get(i).getType().isBoss() && dist < minDist) {
            minDist = dist;
         }
      }

      return minDist;
   }

   public final ArrayList<Enemy> getEnemies() {
      return this.enemies;
   }

   public final int countAtChunk(int chunkX, int chunkZ) {
      int count = 0;

      for (int i = 0; i < this.enemies.size(); i++) {
         if (this.enemies.get(i).isAtChunk(chunkX, chunkZ)) {
            count++;
         }
      }

      return count;
   }
}
