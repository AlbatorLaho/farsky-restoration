package game.environment.life;
import game.environment.BloodParticles;
import game.environment.pickup.ItemPickup;
import game.collision.AABB;
import game.inventory.ItemType;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Coord;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class Whale extends Fish {
   private static int texture;
   private static Vbo model;
   private float animOffset;
   private float soundTimer;
   private float soundInterval = 4.0F;
   private float scale = 50.0F;

   public Whale(Point position, Coord direction) {
      this.position = position.copy();
      this.position.y = this.lockedHeight;
      this.direction = direction.copy();
      this.animOffset = (float)Math.random() * 1000.0F;
      this.targetSpeed = 30.0F + (float)Math.random() * 5.0F;
      this.speed = this.targetSpeed;
      this.heightOffset = 200.0F + (float)Math.random() * 50.0F;
      this.lockedHeight = (float)(this.lockedHeight + (Math.random() - 0.5) * 50.0);
      this.health = 150.0F;
      this.scale = 35.0F + (float)Math.random() * 15.0F;
      this.boundingBox = new AABB(new Point(0.0F, 0.0F, -1.6F * this.scale), 2.0F * this.scale, 1.8F * this.scale, 6.7F * this.scale);
      this.movingState = Fish.MovingState.HEIGHT_LOCKED;
      this.soundTimer = this.soundInterval;
   }

   public static void loadAssets() {
      model = ModelLoader.loadMesh("whale");
      texture = ModelLoader.loadTexture("whale");
   }

   @Override
   public final void onUpdate(float delta) {
      this.soundTimer -= delta;
      if (this.soundTimer <= 0.0F) {
         SoundManager.playSound(SoundManager.sfxWhale, this.position, 1.0F + ((float)Math.random() - 0.5F) * 0.4F);
         this.soundTimer = (float)(this.soundInterval + this.soundInterval * Math.random() * 2.0);
      }
   }

   public static void setupDraw() {
      Shaders.setUniform("topLight", true);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 1.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
      Shaders.setUniform("wave", new Point(0.0F, 2.0F, 0.0F));
      Shaders.setUniform("height", 9.0);
      Shaders.setUniform("factor", 3.5);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
   }

   @Override
   public final void draw() {
      Shaders.setUniform("offset", this.animOffset);
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glRotatef(this.rotation.y, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(this.rotation.x, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(this.scale, this.scale, this.scale);
      model.render();
      GL11.glPopMatrix();
   }

   @Override
   protected final void updateRotation() {
      this.rotation = new Point(-((float)Math.toDegrees(Math.atan(this.prevDirection.y))) / 3.0F, 90.0F - (float)Math.toDegrees(new Coord(this.prevDirection.x, this.prevDirection.z).angle()), 0.0F);
   }

   @Override
   public final ArrayList<ItemPickup> getDrops() {
      ArrayList<ItemPickup> drops = new ArrayList<>();

      for (int i = 0; i < 6; i++) {
         drops.add(new ItemPickup(this.position.plus(new Point(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).scaled(50.0F)), ItemType.WHALE_MEAT));
      }

      return drops;
   }

   @Override
   protected final BloodParticles.BloodType getBloodType() {
      return BloodParticles.BloodType.RED;
   }
}
