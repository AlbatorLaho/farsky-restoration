package game.enemy.enemyWithMouth;

import game.shader.Shaders;
import game.sounds.SoundManager;
import game.collision.AABB;
import game.enemy.EnemyType;
import game.environment.EnvironmentManager;
import game.environment.pickup.ItemPickup;
import game.inventory.ItemType;
import game.render.ModelLoader;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class Anglerfish extends EnemyWithMouth {
   private static int texture;

   public static void loadResources() {
      texture = ModelLoader.loadTexture("anglerfish", false, false);
   }

   public Anglerfish(Point position) {
      super(position, 70.0F);
      this.type = EnemyType.ANGLERFISH;
      this.bodyMesh = ModelLoader.loadAnimatedMesh("anglerfish", 2);
      this.maxHealth = 19.0F;
      this.scale = 7.0F;
      this.health = this.maxHealth;
      this.hitbox = new AABB(new Point(0.0F, 0.2F * this.scale, 0.0F), 1.8F * this.scale, 2.5F * this.scale, 4.0F * this.scale);
      this.attackDamage = 25.0F;
      this.speedMult = 0.7F;
      this.soundSource = SoundManager.addLoopingSource(SoundManager.sfxMovement, position);
      SoundManager.playLoopingSource(this.soundSource);
   }

   @Override
   public final void updateSubclass(float deltaTime) {
   }

   @Override
   public final void setupRender() {
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 1.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
      Shaders.setUniform("wave", new Point(4.0F, 0.0F, 0.5F));
      if (this.health > 0.0F) {
         Shaders.setUniform("alphaLightPercent", 1.0);
         Shaders.setUniform("alphaLightcolor", new Point(1.0F, 1.0F, 0.5F));
      } else {
         Shaders.setUniform("alphaLightPercent", 0.0);
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
   }

   @Override
   public final void cleanupRender() {
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("axisSign", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("alphaLightPercent", 0.0);
   }

   @Override
   protected final void onDeath() {
      EnvironmentManager.addItemPickup(new ItemPickup(this.position, ItemType.ANGLERFISH));
   }

   @Override
   public final void onRemove() {
      SoundManager.removeLoopingSource(this.soundSource);
      this.bodyMesh.freeVbo();
   }

   @Override
   public final boolean isAtChunk(int chunkX, int chunkZ) {
      return false;
   }
}
