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

public final class Barracuda extends EnemyWithMouth {
   private static int texture;
   private float soundDelay;

   public static void loadResources() {
      texture = ModelLoader.loadTexture("barracuda", false, false);
   }

   public Barracuda(Point position) {
      super(position.plus(new Point(Math.random() - 0.5, 0.0, Math.random() - 0.5).scaled(100.0F)), 50.0F + (float)Math.random() * 40.0F);
      this.type = EnemyType.BARRACUDA;
      this.bodyMesh = ModelLoader.loadAnimatedMesh("barracuda", 2);
      this.maxHealth = 8.0F;
      this.health = this.maxHealth;
      this.scale = 12.0F;
      this.hitbox = new AABB(new Point(0.0F, 0.2F * this.scale, -1.0F * this.scale), 1.0F * this.scale, 1.0F * this.scale, 5.0F * this.scale);
      this.speedMult = 1.15F;
      this.attackDamage = 20.0F;
      this.soundSource = SoundManager.addLoopingSource(SoundManager.sfxMovement, position);
      this.soundDelay = (float)(Math.random() * 1.5);
   }

   @Override
   public final void updateSubclass(float deltaTime) {
      if (!SoundManager.isLoopingSourcePlaying(this.soundSource)) {
         this.soundDelay -= deltaTime;
         if (this.soundDelay < 0.0F) {
            SoundManager.playLoopingSource(this.soundSource);
            SoundManager.setLoopingSourceVolume(this.soundSource, 0.5F);
            SoundManager.setLoopingSourcePitch(this.soundSource, 1.1F);
         }
      }
   }

   @Override
   public final void setupRender() {
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 1.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
      Shaders.setUniform("wave", new Point(1.0F, 0.5F, 0.25F));
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
      EnvironmentManager.addItemPickup(new ItemPickup(this.position, ItemType.BARRACUDA_MEAT));
   }

   @Override
   public final void onRemove() {
      SoundManager.removeLoopingSource(this.soundSource);
      this.bodyMesh.freeVbo();
   }

   @Override
   protected final void setupBodyRender() {
      if (this.state == EnemyWithMouth.State.ATTACKING) {
         Shaders.setUniform("height", 6.0);
         Shaders.setUniform("factor", 22.0);
      } else {
         Shaders.setUniform("height", 6.0);
         Shaders.setUniform("factor", 17.0);
      }
   }

   @Override
   public final boolean isAtChunk(int chunkX, int chunkZ) {
      return false;
   }
}
