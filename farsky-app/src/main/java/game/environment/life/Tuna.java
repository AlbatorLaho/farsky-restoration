package game.environment.life;
import game.environment.BloodParticles;
import game.environment.pickup.ItemPickup;
import game.collision.AABB;
import game.inventory.ItemType;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.util.Coord;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class Tuna extends Fish {
   private static int texture;
   private static Vbo model;
   private float animOffset;
   private float scale = 5.0F;

   public Tuna(Point position, Coord direction) {
      this.position = position.copy();
      this.direction = direction.copy();
      this.scale = (float)(3.0 + Math.random() * 3.0);
      this.animOffset = (float)Math.random() * 1000.0F;
      this.targetSpeed = 110.0F + (float)Math.random() * 20.0F;
      this.speed = this.targetSpeed;
      this.heightOffset = 125.0F + (float)Math.random() * 100.0F;
      this.health = 6.0F;
      this.boundingBox = new AABB(new Point(), 3.0F * this.scale, 3.0F * this.scale, 10.0F * this.scale);
   }

   public static void loadAssets() {
      model = ModelLoader.loadMesh("tuna");
      texture = ModelLoader.loadTexture("tuna");
   }

   public static void setupDraw() {
      Shaders.setUniform("topLight", true);
      Shaders.setUniform("axis", new Point(0.0F, 0.0F, 1.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
      Shaders.setUniform("wave", new Point(1.5F, 0.0F, 0.0F));
      Shaders.setUniform("factor", 25.0);
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
      drops.add(new ItemPickup(this.position, ItemType.TUNA_MEAT));
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
