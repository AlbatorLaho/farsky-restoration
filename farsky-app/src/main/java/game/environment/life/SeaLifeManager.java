package game.environment.life;

import game.chunks.ChunkManager;
import game.collision.BoundingBox;
import game.manager.Camera;
import game.manager.GameScene;
import game.manager.Loading;
import game.player.damage.Damage;
import game.shader.Shaders;
import game.util.Coord;
import game.util.Point;
import game.util.Segment;
import game.util.State;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class SeaLifeManager {
   private ArrayList<Fish> skatefishes = new ArrayList<>();
   private ArrayList<Fish> tunas = new ArrayList<>();
   private ArrayList<Fish> dolphins = new ArrayList<>();
   private ArrayList<Fish> whales = new ArrayList<>();
   private ArrayList<Fish> standardFishes = new ArrayList<>();
   private float spawnTimer;
   private static float SPAWN_INTERVAL = 3.0F;
   private ArrayList<Coord> spawnedChunks = new ArrayList<>();

   public SeaLifeManager() {
      this.spawnTimer = SPAWN_INTERVAL;

      for (float x = -ChunkManager.viewDistance; x <= ChunkManager.viewDistance; x += 128.0F) {
         for (float z = -ChunkManager.viewDistance; z <= ChunkManager.viewDistance; z += 128.0F) {
            Point p = Camera.getPosition().plus(x, 0.0F, z);
            this.spawnFishAt(p.toCoord());
         }
      }
   }

   public final void update(float delta) {
      int stage = 0;
      if (GameScene.avatar != null) {
         stage = Loading.worldManager.getStageAt(Camera.getPosition().x, Camera.getPosition().z);
      }

      for (int i = 0; i < this.skatefishes.size(); i++) {
         this.skatefishes.get(i).update(delta);
      }

      for (int i = 0; i < this.tunas.size(); i++) {
         this.tunas.get(i).update(delta);
      }

      for (int i = 0; i < this.dolphins.size(); i++) {
         this.dolphins.get(i).update(delta);
      }

      for (int i = 0; i < this.standardFishes.size(); i++) {
         this.standardFishes.get(i).update(delta);
      }

      for (int i = 0; i < this.whales.size(); i++) {
         this.whales.get(i).update(delta);
      }

      this.spawnTimer += delta;
      if (this.spawnTimer >= SPAWN_INTERVAL) {
         this.spawnTimer = this.spawnTimer - SPAWN_INTERVAL;

         for (float angle = delta = (float)(Math.random() * Math.PI * 2.0); angle < (Math.PI * 2) + delta; angle = (float)(angle + (Math.PI / 16))) {
            Point spawnPoint = Camera.getPosition().plus(new Point(Math.cos(angle) * ChunkManager.viewDistance, 0.0, Math.sin(angle) * ChunkManager.viewDistance));
            this.spawnFishAt(spawnPoint.toCoord());
         }

         if (stage == 0 && Math.random() < 0.08F) {
            float yawOffset = ((float)Math.random() - 0.5F) * 90.0F;
            Coord spawnDir = new Coord(Math.sin(Math.toRadians(Camera.getYaw() + yawOffset)), Math.cos(Math.toRadians(Camera.getYaw() + yawOffset)));

            for (int i = 0; i < 5; i++) {
               Point p = Camera.getPosition()
                     .plus(new Point(-spawnDir.x * (ChunkManager.viewDistance + Math.random() * 210.0), 0.0, -spawnDir.y * (ChunkManager.viewDistance + Math.random() * 210.0)));
               p.y = ChunkManager.getHeight(p.x, p.z) + 250.0F;
               this.skatefishes.add(new Skatefish(p, spawnDir));
            }
         }

         if (stage == 0 && Math.random() < 0.05F || stage == 1 && Math.random() < 0.03F) {
            float yawOffset = ((float)Math.random() - 0.5F) * 90.0F;
            Coord spawnDir = new Coord(Math.sin(Math.toRadians(Camera.getYaw() + yawOffset)), Math.cos(Math.toRadians(Camera.getYaw() + yawOffset)));

            for (int i = 0; i < 15; i++) {
               Point p = Camera.getPosition()
                     .plus(new Point(-spawnDir.x * (ChunkManager.viewDistance + Math.random() * 210.0), 0.0, -spawnDir.y * (ChunkManager.viewDistance + Math.random() * 210.0)));
               p.y = ChunkManager.getHeight(p.x, p.z) + 250.0F;
               this.tunas.add(new Tuna(p, spawnDir));
            }
         }

         if (stage == 0 && Math.random() < 0.006F) {
            float yawOffset = ((float)Math.random() - 0.5F) * 90.0F;
            Coord spawnDir = new Coord(Math.sin(Math.toRadians(Camera.getYaw() + yawOffset)), Math.cos(Math.toRadians(Camera.getYaw() + yawOffset)));

            for (int i = 0; i < 15; i++) {
               Point p = Camera.getPosition()
                     .plus(new Point(-spawnDir.x * (ChunkManager.viewDistance + Math.random() * 210.0), 0.0, -spawnDir.y * (ChunkManager.viewDistance + Math.random() * 210.0)));
               p.y = ChunkManager.getHeight(p.x, p.z) + 250.0F;
               this.dolphins.add(new Dolphin(p, spawnDir));
            }
         }

         if (stage == 0 && Math.random() < 0.01F) {
            float yawOffset = ((float)Math.random() - 0.5F) * 90.0F;
            Coord spawnDir = new Coord(Math.sin(Math.toRadians(Camera.getYaw() + yawOffset)), Math.cos(Math.toRadians(Camera.getYaw() + yawOffset)));

            for (int i = 0; i <= 0; i++) {
               Point p = Camera.getPosition()
                     .plus(new Point(-spawnDir.x * (ChunkManager.viewDistance + Math.random() * 210.0), 0.0, -spawnDir.y * (ChunkManager.viewDistance + Math.random() * 210.0)));
               p.y = ChunkManager.getHeight(p.x, p.z) + 250.0F;
               this.whales.add(new Whale(p, spawnDir));
            }
         }
      }

      for (int i = this.skatefishes.size() - 1; i >= 0; i--) {
         if (this.skatefishes.get(i).shouldRemove()) {
            this.skatefishes.remove(i);
         }
      }

      for (int i = this.tunas.size() - 1; i >= 0; i--) {
         if (this.tunas.get(i).shouldRemove()) {
            this.tunas.remove(i);
         }
      }

      for (int i = this.dolphins.size() - 1; i >= 0; i--) {
         if (this.dolphins.get(i).shouldRemove()) {
            this.dolphins.remove(i);
         }
      }

      for (int i = this.standardFishes.size() - 1; i >= 0; i--) {
         if (this.standardFishes.get(i).shouldRemove()) {
            this.standardFishes.remove(i);
         }
      }

      for (int i = this.whales.size() - 1; i >= 0; i--) {
         if (this.whales.get(i).shouldRemove()) {
            this.whales.remove(i);
         }
      }

      for (int i = this.spawnedChunks.size() - 1; i >= 0; i--) {
         if (this.spawnedChunks.get(i).distanceTo(Camera.getPosition().toCoord()) > ChunkManager.viewDistance * 1.2F) {
            this.spawnedChunks.remove(i);
         }
      }
   }

   public final void render() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Skatefish.setupDraw();

      for (int i = 0; i < this.skatefishes.size(); i++) {
         this.skatefishes.get(i).draw();
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Tuna.setupDraw();

      for (int i = 0; i < this.tunas.size(); i++) {
         this.tunas.get(i).draw();
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Dolphin.setupDraw();

      for (int i = 0; i < this.dolphins.size(); i++) {
         this.dolphins.get(i).draw();
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Whale.setupDraw();

      for (int i = 0; i < this.whales.size(); i++) {
         this.whales.get(i).draw();
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.standardFishes.size() > 0) {
         FishType currentType = this.standardFishes.get(0).getFishType();
         Fish.setupDraw(currentType);

         for (int i = 0; i < this.standardFishes.size(); i++) {
            if (currentType != this.standardFishes.get(i).getFishType()) {
               currentType = this.standardFishes.get(i).getFishType();
               Fish.setupDraw(currentType);
            }

            this.standardFishes.get(i).draw();
         }
      }

      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("wave", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("alphaLightPercent", 0.0);
      Shaders.setUniform("offset", 0.0);
      GL11.glDepthMask(true);
      GL11.glEnable(GL11.GL_CULL_FACE);
      Shaders.setUniform("invertAlphaLight", false);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void spawnFishAt(Coord coord) {
      for (int i = 0; i < this.spawnedChunks.size(); i++) {
         if (this.spawnedChunks.get(i).distanceTo(coord) < 400.0F) {
            return;
         }
      }

      Point center = new Point(coord.x, 0.0F, coord.y);
      FishType fishType = FishType.FISH;
      int stage = 0;
      ArrayList<Fish> fishList = new ArrayList<>();
      if (GameScene.avatar != null) {
         stage = Loading.worldManager.getStageAt(coord.x, coord.y);
      }

      if (stage == 0) {
         switch ((int)(Math.random() * 4.0)) {
            case 0:
               fishType = FishType.FISH;
               break;
            case 1:
               fishType = FishType.BARRACUDA;
               break;
            case 2:
               fishType = FishType.SHARK;
               break;
            case 3:
               fishType = FishType.MANTA_RAY;
         }

         for (int i = 0; i < 20; i++) {
            fishList.add(new StandardFish(center, fishType));
         }
      } else if (stage == 1) {
         switch ((int)(Math.random() * 3.0)) {
            case 0:
               fishType = FishType.FRILLED_SHARK;
               break;
            case 1:
               fishType = FishType.ANGLERFISH;
               break;
            case 2:
               fishType = FishType.WHALE;
         }

         for (int i = 0; i < 25; i++) {
            fishList.add(new StandardFish(center, fishType));
         }
      } else {
         if (stage != 2) {
            return;
         }

         switch ((int)(Math.random() * 2.0)) {
            case 0:
               fishType = FishType.TUNA;
               break;
            case 1:
               fishType = FishType.DOLPHIN;
         }

         for (int i = 0; i < 30; i++) {
            fishList.add(new AbyssalFish(center, fishType));
         }
      }

      boolean inserted = false;

      for (int i = 0; i < this.standardFishes.size(); i++) {
         if (this.standardFishes.get(i).getFishType() == fishType) {
            this.standardFishes.addAll(i, fishList);
            inserted = true;
            break;
         }
      }

      if (!inserted) {
         this.standardFishes.addAll(fishList);
      }

      this.spawnedChunks.add(coord);
   }

   public final Damage applyHit(ArrayList<Segment> segments, Damage weaponDamage) {
      Damage totalDamage = new Damage();

      for (int i = this.skatefishes.size() - 1; i >= 0; i--) {
         totalDamage.accumulate(this.skatefishes.get(i).checkHit(segments, weaponDamage));
      }

      for (int i = this.tunas.size() - 1; i >= 0; i--) {
         totalDamage.accumulate(this.tunas.get(i).checkHit(segments, weaponDamage));
      }

      for (int i = this.dolphins.size() - 1; i >= 0; i--) {
         totalDamage.accumulate(this.dolphins.get(i).checkHit(segments, weaponDamage));
      }

      for (int i = this.whales.size() - 1; i >= 0; i--) {
         totalDamage.accumulate(this.whales.get(i).checkHit(segments, weaponDamage));
      }

      for (int i = this.standardFishes.size() - 1; i >= 0; i--) {
         totalDamage.accumulate(this.standardFishes.get(i).checkHit(segments, weaponDamage));
      }

      if (totalDamage.getAmount() > 0.0F) {
         for (int i = 0; i < this.skatefishes.size(); i++) {
            this.skatefishes.get(i).fleeFrom(totalDamage.getSource(), true);
         }

         for (int i = 0; i < this.tunas.size(); i++) {
            this.tunas.get(i).fleeFrom(totalDamage.getSource(), true);
         }

         for (int i = 0; i < this.dolphins.size(); i++) {
            this.dolphins.get(i).fleeFrom(totalDamage.getSource(), true);
         }

         for (int i = 0; i < this.whales.size(); i++) {
            this.whales.get(i).fleeFrom(totalDamage.getSource(), true);
         }

         for (int i = 0; i < this.standardFishes.size(); i++) {
            this.standardFishes.get(i).fleeFrom(totalDamage.getSource(), true);
         }
      }

      return totalDamage;
   }

   public final State resolveCollision(State position, State velocity) {
      for (int i = 0; i < this.whales.size(); i++) {
         velocity = this.whales.get(i).getWorldBoundingBox().resolveCollision(position, velocity);
         if (BoundingBox.groundHit) {
            velocity.vel = new Point();
            velocity.vel.add(this.whales.get(i).getVelocity());
            velocity.pos.add(velocity.vel);
         }
      }

      return velocity;
   }
}
