package game.chunks.chunkElements;

import game.collision.AABB;
import game.enemy.kraken.Kraken;
import game.inventory.ItemType;
import game.inventory.types.Inventory;
import game.manager.GameScene;
import game.manager.GameTime;
import game.manager.Loading;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Point;
import game.world.structure.GamePlayElmt;
import game.world.structure.GamePlayType;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class OreDeposit extends ChunkElement {
   private static Vbo vbo;
   private static int textureId;
   private ItemType oreType;
   private int count;
   private boolean spawnKraken;
   private Inventory inventory;
   private Point spawnPoint;
   private int chunkX;
   private int chunkZ;
   private static ArrayList<Point> offsets;

   public OreDeposit(Point pos, Point spawnPoint, GamePlayType depositType, int count, int chunkX, int chunkZ, Inventory inventory) {
      this.position = pos.plus(0.0F, -5.0F, 0.0F);
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.count = count;
      if (depositType == GamePlayType.GOLD_DEPOSIT) {
         this.oreType = ItemType.GOLD;
      }

      if (depositType == GamePlayType.CRYSTAL_DEPOSIT) {
         this.oreType = ItemType.CRYSTAL;
      }

      if (depositType == GamePlayType.SILVER_DEPOSIT) {
         this.oreType = ItemType.SILVER;
      }

      this.spawnKraken = depositType == GamePlayType.CRYSTAL_DEPOSIT;
      this.inventory = inventory;
      this.spawnPoint = spawnPoint.copy();
   }

   public static void loadAssets() {
      vbo = ModelLoader.loadMesh("gold");
      textureId = ModelLoader.loadTexture("gold");
      offsets = new ArrayList<>();
      offsets.add(new Point(2.0F, 0.0F, -1.0F));
      offsets.add(new Point(-1.0F, -2.0F, 2.0F));
      offsets.add(new Point(0.0F, -1.0F, 1.0F));
      offsets.add(new Point(-2.0F, 0.0F, 0.0F));
      offsets.add(new Point(0.0F, -3.0F, -1.0F));
      offsets.add(new Point(2.0F, -1.0F, -1.0F));
      offsets.add(new Point(1.0F, -2.0F, -2.0F));
      offsets.add(new Point(-1.0F, 0.0F, 2.0F));
   }

   @Override
   public final void update(float deltaTime) {
      if (this.spawnKraken && this.inventory != null && this.inventory.getStorageArray().get(0, 0).getItem() != null && GameScene.enemyManager != null) {
         int existingKrakenCount = GameScene.enemyManager.countAtChunk(this.chunkX, this.chunkZ);

         for (int i = 0; i < this.inventory.getStorageArray().get(0, 0).getItem().getCount() - existingKrakenCount; i++) {
            GameScene.enemyManager.add(new Kraken(this.spawnPoint, this.chunkX, this.chunkZ, true));
         }

         this.spawnKraken = false;
      }
   }

   @Override
   public final void render() {
      Shaders.setUniform("alphaLightPercent", Math.cos(GameTime.elapsedMillis / 500.0F) * 0.2F + 0.9F);
      if (this.oreType == ItemType.GOLD) {
         Shaders.setUniform("alphaLightcolor", new Point(0.7F, 0.5F, 0.0F));
      } else if (this.oreType == ItemType.CRYSTAL) {
         Shaders.setUniform("alphaLightcolor", new Point(0.1F, 0.5F, 1.0F));
      } else if (this.oreType == ItemType.SILVER) {
         Shaders.setUniform("alphaLightcolor", new Point(1.0F, 1.0F, 1.0F));
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

      for (int i = 0; i < this.count; i++) {
         if (i < offsets.size()) {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.position.x + offsets.get(i).x * 5.0F, this.position.y + offsets.get(i).y * 5.0F, this.position.z + offsets.get(i).z * 5.0F);
            GL11.glScalef(15.0F, 15.0F, 15.0F);
            GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            vbo.render();
            GL11.glPopMatrix();
         }
      }
   }

   public static void endBatch() {
      Shaders.setUniform("alphaLightPercent", 0.0);
   }

   @Override
   public final AABB getBoundingBox() {
      return this.count <= 0 ? new AABB(this.position, 0.01F, -5.0F, 0.01F) : new AABB(this.position.plus(10.0F, 0.0F, 0.0F), 40.0F, 80.0F, 30.0F);
   }

   @Override
   public final AABB getLocalBoundingBox() {
      return new AABB(new Point(10.0F, 0.0F, 0.0F), 40.0F, 80.0F, 30.0F);
   }

   @Override
   public final ItemType harvest(boolean consume) {
      ItemType result = null;
      if (this.count > 0) {
         result = this.oreType;
      }

      if (consume && this.count > 0) {
         this.count--;
         if (this.count > 0) {
            if (this.oreType == ItemType.GOLD) {
               Loading.worldManager.setGamePlayElmtAt(new GamePlayElmt(GamePlayType.GOLD_DEPOSIT, this.count), this.chunkX, this.chunkZ);
            } else if (this.oreType == ItemType.CRYSTAL) {
               Loading.worldManager.setGamePlayElmtAt(new GamePlayElmt(GamePlayType.CRYSTAL_DEPOSIT, this.count), this.chunkX, this.chunkZ);
            }
         } else {
            Loading.worldManager.setGamePlayElmtAt(new GamePlayElmt(GamePlayType.NONE), this.chunkX, this.chunkZ);
         }
      }

      return result;
   }
}
