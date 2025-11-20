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

public final class FrilledShark extends EnemyWithMouth {
   private static int texture;

   public static void loadResources() {
      texture = ModelLoader.loadTexture("frilledshark", false, false);
   }

   public FrilledShark(Point position) {
      super(position, 100.0F);
      this.type = EnemyType.FRILLED_SHARK;
      this.bodyMesh = ModelLoader.loadAnimatedMesh("frilledshark", 2);
      this.maxHealth = 23.0F;
      this.scale = 9.0F;
      this.health = this.maxHealth;
      this.hitbox = new AABB(new Point(0.0F, 0.2F * this.scale, -2.0F * this.scale), 1.5F * this.scale, 1.25F * this.scale, 6.85F * this.scale);
      this.attackDamage = 40.0F;
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
      Shaders.setUniform("wave", new Point(2.0F, 1.0F, 0.0F));
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
   }

   @Override
   public final void cleanupRender() {
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("axisSign", new Point(0.0F, 0.0F, 0.0F));
   }

   @Override
   protected final void onDeath() {
      EnvironmentManager.addItemPickup(new ItemPickup(this.position, ItemType.FRILLED_SHARK_MEAT));
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
