package game.player;

import game.Main;
import game.chunks.ChunkManager;
import game.collision.AABB;
import game.gui.InteractionHint;
import game.input.InputManager;
import game.inventory.InventoryHud;
import game.inventory.types.Inventory;
import game.manager.GameScene;
import game.manager.GameState;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class WorldChest {
   private static Vbo chestBodyMesh;
   private static Vbo chestTopMesh;
   private static Vbo tombMesh;
   private static int chestBodyTexture;
   private static int chestTopTexture;
   private static int tombTexture;
   private float openAmount = 0.0F;
   private boolean isOpening = false;
   private Inventory inventory;
   private Point terrainNormal;
   private boolean isInSight;
   private AABB bounds;
   private Point position;

   public WorldChest(Inventory inventory, Point pos) {
      this.inventory = inventory;
      this.position = pos.copy();
      this.position.y = ChunkManager.getHeight(pos.x, pos.z) - 1.0F;
      this.terrainNormal = ChunkManager.getTerrainNormal((int)pos.x, (int)pos.z).plus(0.0F, 1.0F, 0.0F);
      this.terrainNormal.normalize();
      this.bounds = new AABB(this.position, 15.0F, 15.0F, 15.0F);
      if (inventory != null && !inventory.isEmpty()) {
         this.openAmount = 0.0F;
      } else {
         this.openAmount = 1.0F;
      }
   }

   public static void loadAssets() {
      chestBodyMesh = ModelLoader.loadMesh("chest");
      chestBodyTexture = ModelLoader.loadTexture("chest");
      chestTopMesh = ModelLoader.loadMesh("chestTop");
      chestTopTexture = ModelLoader.loadTexture("chestTop");
      tombMesh = ModelLoader.loadMesh("tomb");
      tombTexture = ModelLoader.loadTexture("tomb");
   }

   public final void update(float delta) {
      if (GameScene.avatar != null) {
         this.isInSight = this.bounds.isInPlayerSight(new Point(), 40.0F);
         if (this.isInSight && !this.isOpening) {
            InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Open the chest", this.inventory);
            this.isOpening = GameScene.avatar.isInteractPressed();
         }

         if (this.isOpening) {
            if (this.openAmount == 0.0F) {
               SoundManager.playSound(SoundManager.sfxChestOpening, this.position, 1.0F, 0.3F);
            }

            if (this.openAmount < 1.0F) {
               this.openAmount = this.openAmount + this.openAmount * delta;
               this.openAmount += delta * 0.8F;
               return;
            }

            this.openAmount = 1.0F;
            Main.gameState = GameState.INVENTORY;
            InventoryHud.setInventory(this.inventory);
            this.isOpening = false;
            return;
         }

         if (this.inventory != null && !this.inventory.isEmpty()) {
            if (this.openAmount == 1.0F) {
               SoundManager.playSound(SoundManager.sfxChestClosing, this.position, 1.0F, 0.3F);
            }

            if (this.openAmount > 0.0F) {
               this.openAmount = this.openAmount - this.openAmount * delta;
               this.openAmount -= delta * 0.8F;
               return;
            }

            this.openAmount = 0.0F;
         }
      }
   }

   public final void render() {
      if (this.isInSight) {
         Shaders.setUniform("selected", true);
      }

      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      this.terrainNormal.applyGLRotation();
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.0F, -5.0F);
      GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
      GL11.glScalef(7.0F, 7.0F, 7.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, tombTexture);
      tombMesh.render();
      GL11.glPopMatrix();
      GL11.glScalef(5.0F, 5.0F, 5.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, chestBodyTexture);
      chestBodyMesh.render();
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, chestTopTexture);
      GL11.glTranslatef(this.openAmount, 0.0F, 0.0F);
      chestTopMesh.render();
      GL11.glTranslatef(-this.openAmount, 0.0F, 0.0F);
      GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      GL11.glTranslatef(this.openAmount, 0.0F, 0.0F);
      chestTopMesh.render();
      GL11.glTranslatef(-this.openAmount, 0.0F, 0.0F);
      GL11.glPopMatrix();
      if (this.isInSight) {
         Shaders.setUniform("selected", false);
      }
   }

   public final Point getPosition() {
      return this.position;
   }
}
