package game.chunks.chunkElements;

import game.Main;
import game.collision.AABB;
import game.gui.InteractionHint;
import game.input.InputManager;
import game.inventory.InventoryHud;
import game.inventory.types.Inventory;
import game.manager.GameScene;
import game.manager.GameState;
import game.manager.Loading;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Point;
import game.world.structure.GamePlayElmt;
import game.world.structure.GamePlayType;

import org.lwjgl.opengl.GL11;

public final class Chest extends ChunkElement {
   private static Vbo bodyVbo;
   private static Vbo lidVbo;
   private static int bodyTextureId;
   private static int lidTextureId;
   private Point soundPos;
   private float openAmount = 0.0F;
   private boolean opening = false;
   private Inventory inventory;
   private Point normal;
   private boolean inSight;
   private AABB interactionBounds;
   private int chunkX;
   private int chunkZ;

   public Chest(Inventory inventory, Point pos, Point soundPos, Point normalDir, int chunkX, int chunkZ) {
      this.inventory = inventory;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.position = new Point();
      this.position.set(pos);
      this.position.addY(-1.0F);
      this.soundPos = soundPos;
      this.normal = normalDir.plus(0.0F, 1.0F, 0.0F);
      this.normal.normalize();
      this.interactionBounds = new AABB(soundPos, 15.0F, 15.0F, 15.0F);
      if (inventory != null && !inventory.isEmpty()) {
         this.openAmount = 0.0F;
      } else {
         this.openAmount = 1.0F;
      }
   }

   public static void loadAssets() {
      bodyVbo = ModelLoader.loadMesh("chest");
      bodyTextureId = ModelLoader.loadTexture("chest");
      lidVbo = ModelLoader.loadMesh("chestTop");
      lidTextureId = ModelLoader.loadTexture("chestTop");
   }

   @Override
   public final void update(float deltaTime) {
      if (GameScene.avatar != null) {
         this.inSight = this.interactionBounds.isInPlayerSight(new Point(), 40.0F);
         if (this.inSight && !this.opening) {
            InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Open the chest", this.inventory);
            this.opening = GameScene.avatar.isInteractPressed();
         }

         if (this.opening) {
            if (this.openAmount == 0.0F) {
               SoundManager.playSound(SoundManager.sfxChestOpening, this.soundPos, 1.0F, 0.3F);
            }

            if (this.openAmount < 1.0F) {
               this.openAmount = this.openAmount + this.openAmount * deltaTime;
               this.openAmount += deltaTime * 0.8F;
               return;
            }

            this.openAmount = 1.0F;
            Main.gameState = GameState.INVENTORY;
            InventoryHud.setInventory(this.inventory);
            this.opening = false;
            return;
         }

         if (this.inventory != null && !this.inventory.isEmpty()) {
            if (this.openAmount == 1.0F) {
               SoundManager.playSound(SoundManager.sfxChestClosing, this.soundPos, 1.0F, 0.3F);
            }

            if (this.openAmount > 0.0F) {
               this.openAmount = this.openAmount - this.openAmount * deltaTime;
               this.openAmount -= deltaTime * 0.8F;
               return;
            }

            this.openAmount = 0.0F;
         }
      }
   }

   @Override
   public final void render() {
      if (this.inSight) {
         Shaders.setUniform("selected", true);
      }

      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      this.normal.applyGLRotation();
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(5.0F, 5.0F, 5.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, bodyTextureId);
      bodyVbo.render();
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, lidTextureId);
      GL11.glTranslatef(this.openAmount, 0.0F, 0.0F);
      lidVbo.render();
      GL11.glTranslatef(-this.openAmount, 0.0F, 0.0F);
      GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      GL11.glTranslatef(this.openAmount, 0.0F, 0.0F);
      lidVbo.render();
      GL11.glTranslatef(-this.openAmount, 0.0F, 0.0F);
      GL11.glPopMatrix();
      if (this.inSight) {
         Shaders.setUniform("selected", false);
      }
   }

   @Override
   public final AABB getBoundingBox() {
      return this.interactionBounds;
   }

   @Override
   public final void onUnload() {
      Loading.worldManager.setGamePlayElmtAt(new GamePlayElmt(GamePlayType.CHEST, this.inventory), this.chunkX, this.chunkZ);
   }
}
