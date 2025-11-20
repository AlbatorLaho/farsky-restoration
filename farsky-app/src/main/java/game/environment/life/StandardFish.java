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

public final class StandardFish extends Fish {
   private static int textureFish;
   private static int textureFrilledShark;
   private static int textureBarracuda;
   private static int textureShark;
   private static int textureMantaRay;
   private static int textureAnglerfish;
   private static int textureWhale;
   private static Vbo modelFish;
   private static Vbo modelFrilledShark;
   private static Vbo modelBarracuda;
   private static Vbo modelShark;
   private static Vbo modelMantaRay;
   private static Vbo modelAnglerfish;
   private static Vbo modelWhale;
   private float animOffset;

   public StandardFish(Point position, FishType fishType) {
      this.position = position.plus(new Point(100.0 * (Math.random() - 0.5) * 2.0, 0.0, 100.0 * (Math.random() - 0.5) * 2.0));
      this.direction = new Coord(0, 1);
      this.direction.rotateZ((float)Math.random() * 360.0F);
      this.fishType = fishType;
      this.animOffset = (float)Math.random() * 1000.0F;
      this.targetSpeed = 25.0F + (float)Math.random() * 3.0F;
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
      modelFish = ModelLoader.loadMesh("standardFish", "standardFish0");
      modelFrilledShark = ModelLoader.loadMesh("standardFish", "standardFish1");
      modelBarracuda = ModelLoader.loadMesh("standardFish", "standardFish2");
      modelShark = ModelLoader.loadMesh("standardFish", "standardFish3");
      modelMantaRay = ModelLoader.loadMesh("standardFish", "standardFish4");
      modelAnglerfish = ModelLoader.loadMesh("standardFish", "standardFish5");
      modelWhale = ModelLoader.loadMesh("standardFish", "standardFish6");
      textureFish = ModelLoader.loadTexture("standardFish", "standardFish0");
      textureFrilledShark = ModelLoader.loadTexture("standardFish", "standardFish1");
      textureBarracuda = ModelLoader.loadTexture("standardFish", "standardFish2");
      textureShark = ModelLoader.loadTexture("standardFish", "standardFish3");
      textureMantaRay = ModelLoader.loadTexture("standardFish", "standardFish4");
      textureAnglerfish = ModelLoader.loadTexture("standardFish", "standardFish5");
      textureWhale = ModelLoader.loadTexture("standardFish", "standardFish6");
   }

   public static void setupDraw(FishType fishType) {
      Shaders.setUniform("topLight", true);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 1.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
      Shaders.setUniform("wave", new Point(1.0F, 0.0F, 0.0F));
      Shaders.setUniform("factor", 20.0);
      GL11.glDepthMask(true);
      GL11.glEnable(GL11.GL_CULL_FACE);
      Shaders.setUniform("invertAlphaLight", false);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (fishType == FishType.FISH) {
         Shaders.setUniform("height", 3.5);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureFish);
      }

      if (fishType == FishType.FRILLED_SHARK) {
         Shaders.setUniform("alphaLightcolor", new Point(1.0F, 1.0F, 1.0F));
         Shaders.setUniform("height", 5.0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureFrilledShark);
      }

      if (fishType == FishType.BARRACUDA) {
         Shaders.setUniform("height", 2.8F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureBarracuda);
      }

      if (fishType == FishType.SHARK) {
         Shaders.setUniform("height", 3.8F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureShark);
      }

      if (fishType == FishType.MANTA_RAY) {
         Shaders.setUniform("height", 3.8F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureMantaRay);
      }

      if (fishType == FishType.ANGLERFISH) {
         Shaders.setUniform("alphaLightcolor", new Point(1.0F, 1.0F, 1.0F));
         Shaders.setUniform("height", 5.0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureAnglerfish);
      }

      if (fishType == FishType.WHALE) {
         Shaders.setUniform("alphaLightcolor", new Point(1.0F, 1.0F, 1.0F));
         Shaders.setUniform("height", 5.0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureWhale);
      }
   }

   @Override
   public final void draw() {
      Shaders.setUniform("offset", this.animOffset);
      Shaders.setUniform("alphaLightPercent", (Math.cos(GameTime.elapsedMillis / 300.0F + this.animOffset) * 0.5 + 0.5) * 0.5);
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glRotatef(this.rotation.y, 0.0F, 1.0F, 0.0F);
      if (this.prevDirection != null) {
         GL11.glRotatef(this.rotation.x, 1.0F, 0.0F, 0.0F);
      }

      GL11.glScalef(3.0F, 3.0F, 3.0F);
      if (this.fishType == FishType.FISH) {
         modelFish.render();
      }

      if (this.fishType == FishType.FRILLED_SHARK) {
         modelFrilledShark.render();
      }

      if (this.fishType == FishType.BARRACUDA) {
         modelBarracuda.render();
      }

      if (this.fishType == FishType.SHARK) {
         modelShark.render();
      }

      if (this.fishType == FishType.MANTA_RAY) {
         modelMantaRay.render();
      }

      if (this.fishType == FishType.ANGLERFISH) {
         modelAnglerfish.render();
      }

      if (this.fishType == FishType.WHALE) {
         modelWhale.render();
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
      drops.add(new ItemPickup(this.position, ItemType.FISH));
      return drops;
   }

   @Override
   protected final BloodParticles.BloodType getBloodType() {
      return BloodParticles.BloodType.RED;
   }

   @Override
   public final void onUpdate(float delta) {
   }
}
