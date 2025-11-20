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

public final class Dolphin extends Fish {
   private static int texture;
   private static Vbo model;
   private float animOffset;
   private float scale = 8.0F;
   private float soundTimer;

   public Dolphin(Point position, Coord direction) {
      this.position = position.copy();
      this.direction = direction.copy();
      this.animOffset = (float)Math.random() * 1000.0F;
      this.targetSpeed = 140.0F + (float)Math.random() * 40.0F;
      this.speed = this.targetSpeed;
      this.heightOffset = 125.0F + (float)Math.random() * 100.0F;
      this.health = 9.0F;
      this.boundingBox = new AABB(new Point(0.0F, 0.0F, -2.0F * this.scale), 2.0F * this.scale, 2.0F * this.scale, 7.0F * this.scale);
      this.soundTimer = (float)(5.0 + Math.random() * 65.0);
   }

   public static void loadAssets() {
      model = ModelLoader.loadMesh("dolphin");
      texture = ModelLoader.loadTexture("dolphin");
   }

   public static void setupDraw() {
      Shaders.setUniform("topLight", true);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 1.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
      Shaders.setUniform("wave", new Point(0.0F, 1.7F, 0.0F));
      Shaders.setUniform("factor", 15.0);
      Shaders.setUniform("height", 10.0);
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
      this.rotation = new Point(-((float)Math.toDegrees(Math.atan(this.prevDirection.y))), 90.0F - (float)Math.toDegrees(new Coord(this.prevDirection.x, this.prevDirection.z).angle()), 0.0F);
   }

   @Override
   public final ArrayList<ItemPickup> getDrops() {
      ArrayList<ItemPickup> drops = new ArrayList<>();
      drops.add(new ItemPickup(this.position, ItemType.DOLPHIN_MEAT));
      return drops;
   }

   @Override
   protected final BloodParticles.BloodType getBloodType() {
      return BloodParticles.BloodType.RED;
   }

   @Override
   public final void onUpdate(float delta) {
      this.soundTimer -= delta;
      if (this.soundTimer <= 0.0F) {
         SoundManager.playSound(SoundManager.sfxDolphin, this.position, (float)(1.0 + (Math.random() - 0.5) * 0.5), 0.3F);
         this.soundTimer = (float)(20.0 + Math.random() * 65.0);
      }
   }
}
