package game.enemy.lightning;

import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.collision.AABB;
import game.enemy.EnemyType;
import game.environment.EnvironmentManager;
import game.environment.pickup.ItemPickup;
import game.inventory.ItemType;
import game.manager.GameTime;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class AbyssalLightningFish extends LightningFish {
   private static Vbo mesh;
   private static int texture;

   public AbyssalLightningFish(Point position) {
      super(position, 70.0F);
      this.type = EnemyType.DEEP_SEA_FISH;
      this.maxHealth = 30.0F;
      this.scale = 10.0F;
      this.health = this.maxHealth;
      this.hitbox = new AABB(new Point(0.0F, 0.2F * this.scale, 0.0F), 1.8F * this.scale, 2.5F * this.scale, 4.0F * this.scale);
      this.attackDamage = 25.0F;
      this.aggroRange = 100.0F;
      this.speedMult = 0.7F;
      this.soundSource = SoundManager.addLoopingSource(SoundManager.sfxMovement, position);
      SoundManager.playLoopingSource(this.soundSource);
   }

   public static void loadResources() {
      mesh = ModelLoader.loadMesh("abyssalFishAlpha");
      texture = ModelLoader.loadTexture("abyssalFishAlpha");
   }

   @Override
   public final void setupRender() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 1.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
      Shaders.setUniform("wave", new Point(1.0F, 0.35F, 0.0F));
      Shaders.setUniform("height", 4.7F);
      if (this.state != LightningFish.State.IDLE && this.state != LightningFish.State.CHASING) {
         Shaders.setUniform("factor", 15.0);
      } else {
         Shaders.setUniform("factor", 10.0);
      }

      Shaders.setUniform("alphaLightPercent", (float)Math.cos(GameTime.elapsedMillis / 70.0F) * 0.2F + 0.8F);
      Shaders.setUniform("alphaLightcolor", new Point(0.5F, 0.5F, 2.0F));
      Shaders.setUniform("invertAlphaLight", true);
   }

   @Override
   protected final void drawMesh() {
      GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      mesh.render();
   }

   @Override
   public final void cleanupRender() {
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("axisSign", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("alphaLightPercent", 0.0);
      Shaders.setUniform("invertAlphaLight", false);
   }

   @Override
   protected final void onDeath() {
      EnvironmentManager.addItemPickup(new ItemPickup(this.position, ItemType.JELLY));
   }

   @Override
   public final void onRemove() {
   }

   @Override
   public final boolean isAtChunk(int chunkX, int chunkZ) {
      return false;
   }
}
