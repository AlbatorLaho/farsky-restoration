package game.outsideObj;

import game.render.ModelLoader;
import game.render.Vbo;
import game.collision.AABB;
import game.environment.EnvironmentManager;
import game.inventory.ItemType;
import game.manager.GameScene;
import game.player.weapons.Arrow;
import game.player.weapons.WeaponType;
import game.util.Point;
import game.util.State;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.lwjgl.opengl.GL11;

public class HarpoonCannon extends OutsideObj {
   private static final long serialVersionUID = 8852459974503254629L;
   private static transient Vbo cannonMesh;
   private static transient Vbo supportMesh;
   private static transient Vbo pillarMesh;
   private static transient int cannonTexture;
   private static transient int supportTexture;
   private static transient int pillarTexture;
   private Point rot;
   private Point dir;
   private float shootTimer;

   public static void loadModels() {
      cannonMesh = ModelLoader.loadMesh("harpoonCannon", "harpoonCannon");
      cannonTexture = ModelLoader.loadTexture("harpoonCannon", "harpoonCannon");
      supportMesh = ModelLoader.loadMesh("harpoonCannon", "harpoonCannonSupport");
      supportTexture = ModelLoader.loadTexture("harpoonCannon", "harpoonCannonSupport");
      pillarMesh = ModelLoader.loadMesh("harpoonCannon", "harpoonCannonPillar");
      pillarTexture = ModelLoader.loadTexture("harpoonCannon", "harpoonCannonPillar");
   }

   public HarpoonCannon(Point position) {
      this.type = ItemType.HARPOON_CANNON;
      this.aabb = new AABB(position, 11.25F, 37.5F, 11.25F);
      this.pos = position.plus(0.0F, 15.0F, 0.0F);
      this.rot = new Point();
      this.dir = new Point(0.0F, 0.0F, -1.0F);
      this.shootTimer = 0.0F;
   }

   @Override
   public final void tick(float delta) {
      this.shootTimer += delta;
      float minDist = 99999.0F;
      int closestIndex = 0;

      for (int i = 0; i < GameScene.enemyManager.getEnemies().size(); i++) {
         float dist = GameScene.enemyManager.getEnemies().get(i).getPosition().distanceTo(this.pos);
         if (GameScene.enemyManager.getEnemies().get(i).isAggressive() && dist < minDist) {
            closestIndex = i;
            minDist = dist;
         }
      }

      if (minDist < 400.0F) {
         Point toTarget = GameScene.enemyManager.getEnemies().get(closestIndex).getPosition().minus(this.pos);
         toTarget.normalize();
         this.dir = toTarget.scaled(0.1F).plus(this.dir.scaled(0.9F));
         this.rot = this.dir.toAngles();
         if (this.shootTimer >= 2.0F) {
            EnvironmentManager.addArrow(new Arrow(this.pos.copy(), this.dir, WeaponType.IRON_SPEAR));
            this.shootTimer = 0.0F;
         }
      }
   }

   @Override
   public final void drawModel() {
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslatef(this.pos.x, this.pos.y - 15.0F, this.pos.z);
      GL11.glScalef(30.0F, 30.0F, 30.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, pillarTexture);
      pillarMesh.render();
      GL11.glRotatef(this.rot.y + 90.0F, 0.0F, 1.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, supportTexture);
      supportMesh.render();
      GL11.glTranslatef(0.0F, 0.5F, 0.0F);
      GL11.glRotatef(this.rot.x, 0.0F, 0.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, cannonTexture);
      cannonMesh.render();
      GL11.glPopMatrix();
      if (this.shootTimer >= 1.0F) {
         GL11.glPushMatrix();
         GL11.glTranslatef(this.pos.x, this.pos.y, this.pos.z);
         GL11.glRotatef(this.rot.y + 90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(this.rot.x + 90.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
         GL11.glScalef(3.0F, 3.0F, 3.0F);
         new Arrow(new Point(), new Point(0.0F, 0.0F, 0.0F), WeaponType.IRON_SPEAR).render();
         GL11.glPopMatrix();
      }
   }

   @Override
   public final State resolveCollision(State from, State to) {
      return this.aabb.resolveCollision(from, to);
   }

   @Override
   public final void drawEffects() {
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.type = ItemType.HARPOON_CANNON;
   }

   @Override
   public final boolean canPickUp() {
      return true;
   }
}
