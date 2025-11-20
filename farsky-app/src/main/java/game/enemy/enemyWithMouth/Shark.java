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

public final class Shark extends EnemyWithMouth {
   private static int sharkTexture;
   private static int hammerheadTexture;
   private static int greatWhiteTexture;

   public static void loadResources() {
      sharkTexture = ModelLoader.loadTexture("shark", false, false);
      hammerheadTexture = ModelLoader.loadTexture("sharkHammerhead", false, false);
      greatWhiteTexture = ModelLoader.loadTexture("whiteShark", false, false);
   }

   public Shark(Point position, EnemyType type) {
      super(position, 100.0F);
      this.type = type;
      if (type == EnemyType.SHARK) {
         this.bodyMesh = ModelLoader.loadAnimatedMesh("shark", 2);
         this.maxHealth = 14.0F;
         this.attackDamage = 32.0F;
         this.scale = 6.0F;
      } else if (type == EnemyType.HAMMERHEAD) {
         this.bodyMesh = ModelLoader.loadAnimatedMesh("sharkHammerhead", 2);
         this.maxHealth = 22.0F;
         this.attackDamage = 20.0F;
         this.scale = 8.0F;
      } else if (type == EnemyType.GREAT_WHITE) {
         this.bodyMesh = ModelLoader.loadAnimatedMesh("whiteShark", 2);
         this.maxHealth = 45.0F;
         this.attackDamage = 40.0F;
         this.scale = 14.0F;
         this.speedMult = 1.4F;
      }

      this.health = this.maxHealth;
      this.hitbox = new AABB(new Point(0.0F, 0.2F * this.scale, -2.0F * this.scale), 1.5F * this.scale, 1.25F * this.scale, 6.85F * this.scale);
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
      Shaders.setUniform("wave", new Point(1.0F, 0.0F, 0.5F));
      if (this.type == EnemyType.SHARK) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, sharkTexture);
      } else if (this.type == EnemyType.HAMMERHEAD) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, hammerheadTexture);
      } else {
         if (this.type == EnemyType.GREAT_WHITE) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, greatWhiteTexture);
         }
      }
   }

   @Override
   public final void cleanupRender() {
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 0.0F));
      Shaders.setUniform("axisSign", new Point(0.0F, 0.0F, 0.0F));
   }

   @Override
   protected final void onDeath() {
      EnvironmentManager.addItemPickup(new ItemPickup(this.position, ItemType.SHARK_MEAT));
      if (this.type == EnemyType.GREAT_WHITE) {
         for (int i = 0; i < 3; i++) {
            EnvironmentManager.addItemPickup(new ItemPickup(this.position.plus(new Point(Math.random(), Math.random(), Math.random())), ItemType.SHARK_MEAT));
         }
      }
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
