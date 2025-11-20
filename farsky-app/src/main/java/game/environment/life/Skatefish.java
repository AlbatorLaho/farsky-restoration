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

public final class Skatefish extends Fish {
   private static int texture;
   private static Vbo model;
   private float animOffset;

   public Skatefish(Point position, Coord direction) {
      this.position = position.copy();
      this.direction = direction.copy();
      this.animOffset = (float)Math.random() * 1000.0F;
      this.targetSpeed = 30.0F + (float)Math.random() * 5.0F;
      this.speed = this.targetSpeed;
      this.heightOffset = 175.0F + (float)Math.random() * 150.0F;
      this.health = 6.0F;
      this.boundingBox = new AABB(new Point(), 60.0F, 10.0F, 40.0F);
   }

   public static void loadAssets() {
      model = ModelLoader.loadMesh("skatefish");
      texture = ModelLoader.loadTexture("skatefish");
   }

   public static void setupDraw() {
      Shaders.setUniform("topLight", true);
      Shaders.setUniform("axis", new Point(1.0F, 0.0F, 0.0F));
      Shaders.setUniform("axisSign", new Point(1.0F, 1.0F, 1.0F));
      Shaders.setUniform("wave", new Point(0.0F, 1.0F, 0.0F));
      Shaders.setUniform("height", 4.5);
      Shaders.setUniform("factor", 7.0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
   }

   @Override
   public final void draw() {
      Shaders.setUniform("offset", this.animOffset);
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glRotatef(this.rotation.y, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(this.rotation.x, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(13.0F, 13.0F, 13.0F);
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
      drops.add(new ItemPickup(this.position, ItemType.MANTA_RAY_MEAT));
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
