package game.chunks.chunkElements;

import game.Main;
import game.collision.AABB;
import game.gui.InteractionHint;
import game.gui.PlayerHud;
import game.input.InputManager;
import game.inventory.types.Inventory;
import game.manager.GameScene;
import game.manager.Loading;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Point;
import game.world.structure.GamePlayElmt;
import game.world.structure.GamePlayType;

import org.lwjgl.opengl.GL11;

public final class TreasureChest extends ChunkElement {
   private static Vbo bodyVbo;
   private static Vbo coinsVbo;
   private static int bodyTextureId;
   private static int coinsTextureId;
   private Inventory inventory;
   private Point normal;
   private AABB interactionBounds;
   private int chunkX;
   private int chunkZ;

   public TreasureChest(Inventory inventory, Point pos, Point aabbPos, Point normalDir, int chunkX, int chunkZ) {
      this.inventory = inventory;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.position = new Point();
      this.position.set(pos);
      this.position.addY(-1.0F);
      this.normal = normalDir.plus(0.0F, 1.0F, 0.0F);
      this.normal.normalize();
      this.interactionBounds = new AABB(aabbPos, 15.0F, 15.0F, 15.0F);
   }

   public static void loadAssets() {
      bodyVbo = ModelLoader.loadMesh("moneyChest", "chest");
      bodyTextureId = ModelLoader.loadTexture("moneyChest", "chest");
      coinsVbo = ModelLoader.loadMesh("moneyChest", "coins");
      coinsTextureId = ModelLoader.loadTexture("moneyChest", "coins");
   }

   @Override
   public final void update(float deltaTime) {
      if (GameScene.avatar != null && this.inventory != null && this.inventory.getStorageArray().get(0, 0).getItem() != null && this.interactionBounds.isInPlayerSight(new Point(), 40.0F)) {
         int coinCount = this.inventory.getStorageArray().get(0, 0).getItem().getCount();
         InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Take the " + coinCount + " coins", null);
         if (GameScene.avatar.isInteractPressed()) {
            Main.achievements.addMoney(coinCount);
            Loading.worldManager.setGamePlayElmtAt(new GamePlayElmt(GamePlayType.NONE), this.chunkX, this.chunkZ);
            SoundManager.playSound(SoundManager.sfxMoney, null, 1.0F, 0.4F);
            PlayerHud.showCoinBonus(coinCount);
            this.inventory = null;
         }
      }
   }

   @Override
   public final void render() {
      if (this.inventory != null) {
         GL11.glPushMatrix();
         GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
         this.normal.applyGLRotation();
         GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GL11.glScalef(4.0F, 4.0F, 4.0F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, bodyTextureId);
         bodyVbo.render();
         Shaders.setUniform("emissive", true);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, coinsTextureId);
         coinsVbo.render();
         Shaders.setUniform("emissive", false);
         GL11.glPopMatrix();
      }
   }

   @Override
   public final AABB getBoundingBox() {
      return this.interactionBounds;
   }
}
