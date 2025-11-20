package game.environment.life;
import game.environment.BloodParticles;
import game.environment.pickup.ItemPickup;
import game.chunks.ChunkManager;
import game.collision.AABB;
import game.inventory.ItemType;
import game.manager.GameTime;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Coord;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class AbyssalFish extends Fish {
   private static int textureTuna;
   private static int textureDolphin;
   private static Vbo modelTuna;
   private static Vbo modelDolphin;
   private float animOffset;

   public AbyssalFish(Point position, FishType fishType) {
      this.roamingRadius = 100.0F;
      this.position = position.plus(new Point(this.roamingRadius * (Math.random() - 0.5) * 2.0, 0.0, this.roamingRadius * (Math.random() - 0.5) * 2.0));
      this.direction = new Coord(0, 1);
      this.direction.rotateZ((float)Math.random() * 360.0F);
      this.fishType = fishType;
      this.animOffset = (float)Math.random() * 1000.0F;
      this.targetSpeed = 5.0F + (float)Math.random() * 3.0F;
      this.speed = this.targetSpeed;
      this.health = 1.0F;
      this.boundingBox = new AABB(new Point(), 7.5F, 10.0F, 15.0F);
      this.heightOffset = 30.0F + (float)Math.random() * 25.0F;
      this.position.y = ChunkManager.getHeight(position.x, position.z) + this.heightOffset;
      this.prevDirection = this.direction.copy();
      this.spawnCenter = position.toCoord();
      this.movingState = Fish.MovingState.ROAMING;
   }

   public static void loadAssets() {
      modelTuna = ModelLoader.loadMesh("abyssalFish", "abyssalFish0");
      textureTuna = ModelLoader.loadTexture("abyssalFish", "abyssalFish0");
      modelDolphin = ModelLoader.loadMesh("abyssalFish", "abyssalFish1");
      textureDolphin = ModelLoader.loadTexture("abyssalFish", "abyssalFish1");
   }

   public static void setupDraw(FishType fishType) {
      Shaders.setUniform("topLight", true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(GL11.GL_CULL_FACE);
      Shaders.setUniform("invertAlphaLight", true);
      if (fishType == FishType.TUNA) {
         Shaders.setUniform("factor", 15.0);
         Shaders.setUniform("axis", new Point(0.0F, 1.0F, 0.0F));
         Shaders.setUniform("wave", new Point(1.0F, 0.0F, 1.0F));
         Shaders.setUniform("axisSign", new Point(-1.0F, -1.0F, -1.0F));
         Shaders.setUniform("height", 4.0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureTuna);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
         Shaders.setUniform("alphaLightcolor", new Point(1.0F, 1.0F, 1.0F));
      }

      if (fishType == FishType.DOLPHIN) {
         Shaders.setUniform("factor", 2.0);
         Shaders.setUniform("height", 3.0);
         Shaders.setUniform("axis", new Point(0.0F, 0.0F, 1.0F));
         Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
         Shaders.setUniform("wave", new Point(1.0F, 1.0F, 0.0F));
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureDolphin);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
         Shaders.setUniform("alphaLightcolor", new Point(0.0F, 0.3F, 1.0F));
      }
   }

   @Override
   public final void draw() {
      Shaders.setUniform("offset", this.animOffset);
      Shaders.setUniform("alphaLightPercent", Math.cos(GameTime.elapsedMillis / 50.0F + this.animOffset) * 0.2F + 0.8F);
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glRotatef(this.rotation.y, 0.0F, 1.0F, 0.0F);
      if (this.prevDirection != null) {
         GL11.glRotatef(this.rotation.x, 1.0F, 0.0F, 0.0F);
      }

      GL11.glScalef(4.0F, 4.0F, 4.0F);
      if (this.fishType == FishType.TUNA) {
         modelTuna.render();
      }

      if (this.fishType == FishType.DOLPHIN) {
         modelDolphin.render();
      }

      GL11.glPopMatrix();
   }

   @Override
   protected final void updateRotation() {
      this.rotation = new Point((float)Math.toDegrees(Math.atan(this.prevDirection.y)), -90.0F - (float)Math.toDegrees(new Coord(this.prevDirection.x, this.prevDirection.z).angle()), 0.0F);
   }

   @Override
   public final FishType getFishType() {
      return this.fishType;
   }

   @Override
   public final ArrayList<ItemPickup> getDrops() {
      ArrayList<ItemPickup> drops = new ArrayList<>();
      drops.add(new ItemPickup(this.position, ItemType.JELLY));
      return drops;
   }

   @Override
   protected final BloodParticles.BloodType getBloodType() {
      return BloodParticles.BloodType.BLUE;
   }

   @Override
   public final void onUpdate(float delta) {
   }
}
