package game.outsideObj;
import game.chunks.Chunk;

import game.Main;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.chunks.ChunkManager;
import game.collision.AABB;
import game.environment.ResourcesPercent;
import game.environment.particle.MovingParticle;
import game.gui.InteractionHint;
import game.input.InputManager;
import game.environment.EnvironmentManager;
import game.inventory.InventoryHud;
import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.types.ExtractorInventory;
import game.manager.GameState;
import game.manager.GameScene;
import game.manager.GameTime;
import game.util.Coord;
import game.util.Point;
import game.util.State;
import org.lwjgl.opengl.GL11;

public class Extractor extends OutsideObj {
   private static final long serialVersionUID = -8889801297197276933L;
   private ExtractorInventory inventory;
   private ResourcesPercent resourcesPercents;
   private ExtractorType extractorType;
   private int floorIndex;
   private Chunk.RockProperty rockProperty;
   private static transient Vbo bodyMesh;
   private static transient Vbo drillMesh;
   private static transient int bodyTexture;
   private static transient int drillTexture;
   private transient boolean pendingOpen = false;
   private transient float extractTimer = 0.0F;
   private transient float particleTimer = 0.0F;

   public static void loadModels() {
      bodyMesh = ModelLoader.loadMesh("extractor", "extractor");
      bodyTexture = ModelLoader.loadTexture("extractor", "extractor");
      drillMesh = ModelLoader.loadMesh("extractor", "drill");
      drillTexture = ModelLoader.loadTexture("extractor", "drill");
   }

   public Extractor(Point position, ExtractorType type) {
      this.pos = position.plus(0.0F, -4.0F, 0.0F);
      this.extractorType = type;
      this.aabb = new AABB(position, 20.0F, 90.0F, 20.0F);
      this.floorIndex = ChunkManager.getTerrainColor((int)position.x, (int)position.z).dominantAxis();
      this.rockProperty = ChunkManager.getRockProperty((int)position.x, (int)position.z);
      this.resourcesPercents = EnvironmentManager.getResources(new Coord(position.x, position.z), this.floorIndex, this.rockProperty);
      if (type == ExtractorType.STANDARD) {
         this.type = ItemType.EXTRACTOR;
         this.inventory = new ExtractorInventory("Extractor", 2, 2, this.resourcesPercents);
      } else {
         this.type = ItemType.OVERPOWERED_EXTRACTOR;
         this.inventory = new ExtractorInventory("Extractor", 3, 2, this.resourcesPercents);
      }
   }

   @Override
   public final void tick(float delta) {
      this.extractTimer += delta;
      if (this.extractTimer >= this.extractorType.getTimerThreshold()) {
         this.extractTimer = this.extractTimer - this.extractorType.getTimerThreshold();
         ItemType resourceType = this.resourcesPercents.pickRandom();
         this.inventory.addItem(new Item(resourceType));
         EnvironmentManager.onOreMined((int)this.pos.x, (int)this.pos.z, resourceType);
         this.resourcesPercents = EnvironmentManager.getResources(new Coord(this.pos.x, this.pos.z), this.floorIndex, this.rockProperty);
         this.inventory.setResourcesPercents(this.resourcesPercents);
      }

      if (GameScene.avatar.getCameraPos().distanceTo(this.pos) < ChunkManager.viewDistance) {
         this.particleTimer += delta;
         if (this.particleTimer > 0.15F) {
            for (int i = 0; i < 4; i++) {
               EnvironmentManager.addMovingParticle(new MovingParticle(this.pos, new Point(Math.random() - 0.5, 1.0, Math.random() - 0.5), 10.0F, 4.0F));
            }

            this.particleTimer -= 0.15F;
         }
      }

      if (this.nearPlayer && !this.pendingOpen) {
         InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Open extractor", this.inventory);
         this.pendingOpen = GameScene.avatar.isInteractPressed();
      }

      if (this.pendingOpen) {
         Main.gameState = GameState.INVENTORY;
         InventoryHud.setInventory(this.inventory);
         this.pendingOpen = false;
      }
   }

   @Override
   public final void drawModel() {
      if (this.nearPlayer) {
         Shaders.setUniform("selected", true);
      }

      Shaders.setUniform("alphaLight", true);
      Shaders.setUniform("lightColor", new Point(1.0F, 1.0F, 1.0F).scaled(0.9F + (float)Math.cos(GameTime.elapsedMillis / 250.0F) * 0.2F));
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslatef(this.pos.x, this.pos.y, this.pos.z);
      GL11.glScalef(12.0F, 12.0F, 12.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, bodyTexture);
      bodyMesh.render();
      if (this.extractorType == ExtractorType.STANDARD) {
         Shaders.setUniform("lightColor", new Point(0.0F, 0.0F, 0.0F));
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, drillTexture);
      if (this.extractorType == ExtractorType.STANDARD) {
         GL11.glRotatef(-GameTime.elapsedMillis * 0.4F, 0.0F, 1.0F, 0.0F);
      } else {
         GL11.glRotatef(-GameTime.elapsedMillis * 0.7F, 0.0F, 1.0F, 0.0F);
      }

      drillMesh.render();
      GL11.glPopMatrix();
      Shaders.setUniform("alphaLight", false);
      if (this.nearPlayer) {
         Shaders.setUniform("selected", false);
      }
   }

   @Override
   public final State resolveCollision(State from, State to) {
      return this.aabb.resolveCollision(from, to);
   }

   @Override
   public final void drawEffects() {
   }

   @Override
   public final boolean canPickUp() {
      return this.inventory.isEmpty();
   }
}
