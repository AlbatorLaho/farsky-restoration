package game.environment;
import game.chunks.Chunk;
import game.chunks.ChunkManager;
import game.environment.life.SeaLifeManager;
import game.environment.particle.BurstParticle;
import game.environment.particle.FadingParticle;
import game.environment.particle.MovingParticle;
import game.environment.particle.Particle;
import game.environment.pickup.ItemPickup;
import game.environment.water.WaterSurface;
import game.gui.PlayerHud;
import game.inventory.ItemType;
import game.manager.GameScene;
import game.manager.Loading;
import game.manager.TextureManager;
import game.player.damage.Damage;
import game.player.weapons.Arrow;
import game.shader.Shaders;
import game.util.Coord;
import game.util.Segment;
import game.util.State;
import game.world.structure.GamePlayElmt;
import game.world.structure.GamePlayType;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class EnvironmentManager {
   public static int particleCount = 300;
   private static BubbleField bubbleField;
   private static PlanktonField planktonField;
   private static LightRayField lightRayField;
   private static SeaLifeManager seaLifeManager;
   private static ArrayList<BloodParticles> bloodParticles;
   private static ArrayList<Arrow> arrows;
   private static ArrayList<MovingParticle> movingParticles;
   private static ArrayList<BurstParticle> burstParticles;
   private static ArrayList<ParticleBurst> particleBursts;
   private static ArrayList<ItemPickup> itemPickups;
   private static ArrayList<FlyingRock> flyingRocks;
   private static ArrayList<DeathFragment> deathFragments;
   private static WaterSurface waterSurface;

   public static void init() {
      lightRayField = new LightRayField();
      bubbleField = new BubbleField();
      planktonField = new PlanktonField();
      seaLifeManager = new SeaLifeManager();
      bloodParticles = new ArrayList<>();
      arrows = new ArrayList<>();
      movingParticles = new ArrayList<>();
      burstParticles = new ArrayList<>();
      particleBursts = new ArrayList<>();
      itemPickups = new ArrayList<>();
      flyingRocks = new ArrayList<>();
      deathFragments = new ArrayList<>();
      waterSurface = new WaterSurface();
   }

   public static void update(float delta) {
      bubbleField.update(delta);
      planktonField.update(delta);
      lightRayField.update(delta);
      seaLifeManager.update(delta);

      for (int i = bloodParticles.size() - 1; i >= 0; i--) {
         bloodParticles.get(i).update(delta);
         if (bloodParticles.get(i).isDone()) {
            bloodParticles.remove(i);
         }
      }

      for (int i = movingParticles.size() - 1; i >= 0; i--) {
         movingParticles.get(i).update(delta);
         if (movingParticles.get(i).isDone()) {
            movingParticles.remove(i);
         }
      }

      for (int i = burstParticles.size() - 1; i >= 0; i--) {
         burstParticles.get(i).update(delta);
         if (burstParticles.get(i).isDone()) {
            burstParticles.remove(i);
         }
      }

      for (int i = particleBursts.size() - 1; i >= 0; i--) {
         particleBursts.get(i).update(delta);
         if (particleBursts.get(i).isDone()) {
            particleBursts.remove(i);
         }
      }

      for (int i = itemPickups.size() - 1; i >= 0; i--) {
         itemPickups.get(i).update(delta);
         if (itemPickups.get(i).isPickedUp()) {
            itemPickups.remove(i);
         }
      }

      for (int i = flyingRocks.size() - 1; i >= 0; i--) {
         flyingRocks.get(i).update(delta);
         if (flyingRocks.get(i).isDone()) {
            flyingRocks.remove(i);
         }
      }

      for (int i = arrows.size() - 1; i >= 0; i--) {
         arrows.get(i).update(delta);
         if (!arrows.get(i).isSpent()) {
            Damage hit = GameScene.enemyManager.applyHit(arrows.get(i).getSegment(), arrows.get(i).getDamage());
            hit.accumulate(applyFishHit(arrows.get(i).getSegment(), arrows.get(i).getDamage()));
            if (hit.getAmount() > 0.0F) {
               PlayerHud.addDamageEvent(hit);
               arrows.get(i).expire();
            }
         } else if (arrows.get(i).isExpired()) {
            arrows.remove(i);
         }
      }

      for (int i = deathFragments.size() - 1; i >= 0; i--) {
         deathFragments.get(i).update(delta);
         if (deathFragments.get(i).isDone()) {
            deathFragments.remove(i);
         }
      }

      if (GameScene.nearSurface) {
         WaterSurface.updateSound();
      }
   }

   public static State resolveCollision(State position, State velocity) {
      return seaLifeManager.resolveCollision(position, velocity);
   }

   public static void renderOpaque() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

      for (int i = 0; i < arrows.size(); i++) {
         arrows.get(i).render();
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.rockTexture);

      for (int i = 0; i < flyingRocks.size(); i++) {
         flyingRocks.get(i).render();
      }

      if (deathFragments.size() > 0) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Shaders.setUniform("emissive", true);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);

         for (int i = 0; i < deathFragments.size(); i++) {
            deathFragments.get(i).render();
         }

         Shaders.setUniform("emissive", false);
      }
   }

   public static void renderSeaLife() {
      seaLifeManager.render();
   }

   public static void renderTransparent() {
      lightRayField.render();
      if (GameScene.avatar == null || !GameScene.avatar.isInside()) {
         bubbleField.render();
      }

      planktonField.render();
      GL11.glDepthMask(false);
      Shaders.setUniform("emissive", true);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.oilParticle);

      for (int i = 0; i < burstParticles.size(); i++) {
         burstParticles.get(i).render();
      }

      GL11.glDepthMask(true);
      Shaders.setUniform("emissive", false);
      GL11.glDepthMask(false);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.sandParticle);

      for (int i = 0; i < particleBursts.size(); i++) {
         particleBursts.get(i).render();
      }

      FadingParticle.resetRenderState();

      for (int i = 0; i < bloodParticles.size(); i++) {
         bloodParticles.get(i).render();
      }

      Particle.beginRender();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.bubble);

      for (int i = 0; i < movingParticles.size(); i++) {
         movingParticles.get(i).render();
      }

      Particle.endRender();

      for (int i = 0; i < itemPickups.size(); i++) {
         itemPickups.get(i).render();
      }
   }

   public static void renderWater() {
      waterSurface.render();
   }

   public static ResourcesPercent getResources(Coord coord) {
      return getResources(coord, ChunkManager.getTerrainColor((int)coord.x, (int)coord.y).dominantAxis(), ChunkManager.getRockProperty((int)coord.x, (int)coord.y));
   }

   public static ResourcesPercent getResources(Coord coord, int axis, Chunk.RockProperty rockProperty) {
      ArrayList<Resource> mineResources = ChunkManager.getMineResources((int)coord.x, (int)coord.y, axis, rockProperty);
      ResourcesPercent resources = new ResourcesPercent(mineResources);
      GamePlayElmt overlay = Loading.worldManager.getGamePlayElmtAt(coord.x, coord.y);
      if (overlay != null && (overlay.getType() == GamePlayType.CORAL_OVERLAY || overlay.getType() == GamePlayType.GRASS_OVERLAY)) {
         int percent = Math.min(100, (int)(overlay.getCount() / 250.0F * 100.0F * 3.0F));
         resources.normalize();
         ResourcesPercent scaled = resources.scaled((100 - percent) / 100.0F);
         ArrayList<Resource> overlayResources = new ArrayList<>();
         if (overlay.getType() == GamePlayType.CORAL_OVERLAY) {
            overlayResources.add(new Resource(ItemType.DIRT, percent));
         }

         if (overlay.getType() == GamePlayType.GRASS_OVERLAY) {
            overlayResources.add(new Resource(ItemType.COAL, percent));
         }

         resources = scaled.merged(new ResourcesPercent(overlayResources));
      }

      resources.normalize();
      return resources;
   }

   public static void onOreMined(float x, float y, ItemType itemType) {
      ChunkManager.onOreMined(x, y, itemType);
      GamePlayElmt overlay = Loading.worldManager.getGamePlayElmtAt(x, y);
      if ((itemType == ItemType.DIRT || itemType == ItemType.COAL) && overlay != null && (overlay.getType() == GamePlayType.CORAL_OVERLAY || overlay.getType() == GamePlayType.GRASS_OVERLAY)) {
         int newCount = Math.max(0, overlay.getCount() - 1);
         if (newCount > 0) {
            Loading.worldManager.setGamePlayElmtAt(new GamePlayElmt(overlay.getType(), newCount), x, y);
         } else {
            Loading.worldManager.setGamePlayElmtAt(new GamePlayElmt(GamePlayType.NONE), x, y);
         }

         ChunkManager.rebuildLayerAt((int)x, (int)y);
      }
   }

   public static Damage applyFishHit(Segment segment, Damage damage) {
      ArrayList<Segment> segments = new ArrayList<>();
      segments.add(segment);
      Damage totalDamage = new Damage();
      totalDamage.accumulate(seaLifeManager.applyHit(segments, damage));
      return totalDamage;
   }

   public static void addBloodParticles(BloodParticles bp) {
      GameScene.enemyGenerator.alert(bp.getPosition());
      bloodParticles.add(bp);
   }

   public static void addArrow(Arrow arrow) {
      arrows.add(arrow);
   }

   public static void addMovingParticle(MovingParticle particle) {
      movingParticles.add(particle);
   }

   public static void addBurstParticle(BurstParticle particle) {
      burstParticles.add(particle);
   }

   public static void addParticleBurst(ParticleBurst burst) {
      particleBursts.add(burst);
   }

   public static void addItemPickup(ItemPickup pickup) {
      itemPickups.add(pickup);
   }

   public static void addItemPickups(ArrayList<ItemPickup> pickups) {
      itemPickups.addAll(pickups);
   }

   public static void addFlyingRock(FlyingRock rock) {
      flyingRocks.add(rock);
   }

   public static void addDeathFragment(DeathFragment fragment) {
      deathFragments.add(fragment);
   }
}
