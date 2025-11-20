package game.player.droid;

import game.Main;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.collision.AABB;
import game.environment.DepthAtmosphere;
import game.environment.EnvironmentManager;
import game.environment.particle.MovingParticle;
import game.gui.InteractionHint;
import game.gui.PlayerHud;
import game.input.InputManager;
import game.inventory.InventoryHud;
import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.types.Inventory;
import game.manager.GameState;
import game.manager.GameScene;
import game.manager.GameTime;
import game.player.weapons.Arrow;
import game.player.weapons.WeaponType;
import game.sounds.SoundManager;
import game.util.Point;
import java.io.Serializable;
import org.lwjgl.opengl.GL11;

public class Droid implements Serializable {
   private static final long serialVersionUID = 7317789896463842577L;
   private static transient int droidTexture;
   private static transient int droidItemTexture;
   private static transient int fanTexture;
   private static transient int eyeTexture;
   private static transient Vbo droidMesh;
   @SuppressWarnings("unused")
   private static transient Vbo droidItemMesh;
   private static transient Vbo fanMesh;
   private static transient Vbo eyeMesh;
   private static final transient AABB interactionBounds = new AABB(new Point(), 40.0F, 10.0F, 40.0F);
   private AIdroid ai;
   private boolean working;
   private Inventory inventory;
   private transient float attackCooldown = 0.0F;
   private transient float particleTimer = 0.0F;

   public Droid(Point pos) {
      this(pos, false);
   }

   public Droid(Point pos, boolean working) {
      this.ai = new AIdroid(pos);
      this.working = working;
      this.inventory = new Inventory("Droid Inventory", 3, 2);
   }

   public static void loadAssets() {
      droidMesh = ModelLoader.loadMesh("droid");
      droidTexture = ModelLoader.loadTexture("droid");
      droidItemMesh = ModelLoader.loadMesh("droid", "droidItem");
      droidItemTexture = ModelLoader.loadTexture("droid", "droidItem");
      fanMesh = ModelLoader.loadMesh("droid", "fan");
      fanTexture = ModelLoader.loadTexture("droid", "fan");
      eyeMesh = ModelLoader.loadMesh("droid", "eye");
      eyeTexture = ModelLoader.loadTexture("droid", "eye");
   }

   public final void update(float delta) {
      if (!this.working) {
         if (interactionBounds.isInPlayerSight(this.ai.getPosition(), 60.0F)) {
            InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Fix the droid", null);
            if (GameScene.avatar.isInteractPressed()) {
               if (GameScene.avatar.hasResources(new Item(ItemType.ENERGY_SPHERE, 5))) {
                  PlayerHud.addPickupNotification(ItemType.ENERGY_SPHERE, -5);
                  this.working = true;
                  SoundManager.playSound(SoundManager.sfxPowerOn, this.ai.getPosition(), 0.5F, 0.5F);
                  return;
               }

               InteractionHint.addTimedHint("5 energy spheres required!", 1.0F);
            }
         }
      } else {
         this.ai.update(delta);

         switch (this.ai.getState()) {
            case NAVIGATING:
               this.attackCooldown -= delta;
               if (this.attackCooldown < 0.0F && this.ai.getTargetPosition().distanceTo(this.ai.getPosition()) < 300.0F) {
                  this.attackCooldown = 3.0F;
                  Point dir = this.ai.getTargetPosition().minus(this.ai.getPosition());
                  dir.normalize();
                  EnvironmentManager.addArrow(new Arrow(this.ai.getPosition(), dir, WeaponType.IRON_SPEAR));
               }
               break;
            case IDLE:
               if (interactionBounds.isInPlayerSight(this.ai.getPosition(), 300.0F)) {
                  InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Call the droid", this.inventory);
                  if (GameScene.avatar.isInteractPressed()) {
                     this.ai.setState(DroidState.ATTACKING);
                  }
               }
               break;
            case ATTACKING:
               if (GameScene.avatar.getCameraPos().distanceTo(this.ai.getPosition()) < 30.0F) {
                  this.ai.setState(DroidState.HARVESTING);
               }
               break;
            case HARVESTING:
               if (interactionBounds.isInPlayerSight(this.ai.getPosition(), 60.0F)) {
                  InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Open droid inventory | " + InputManager.getKeyName("Go down") + " - Sleep mode", this.inventory);
                  if (GameScene.avatar.isInteractPressed()) {
                     Main.gameState = GameState.INVENTORY;
                     InventoryHud.setInventory(this.inventory);
                  }

                  if (GameScene.avatar.isDescendPressed()) {
                     this.ai.setState(DroidState.FOLLOWING);
                  }
               }

               if (GameScene.avatar.getCameraPos().distanceTo(this.ai.getPosition()) >= 100.0F) {
                  this.ai.setState(DroidState.IDLE);
               }
               break;
            case FOLLOWING:
               if (interactionBounds.isInPlayerSight(this.ai.getPosition(), 60.0F)) {
                  InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Wake up", this.inventory);
                  if (GameScene.avatar.isInteractPressed()) {
                     this.ai.setState(DroidState.IDLE);
                     SoundManager.playSound(SoundManager.sfxPowerOn, this.ai.getPosition(), 0.5F, 0.5F);
                  }
               }
         }

         this.particleTimer += delta;
         if (this.particleTimer > 0.1F) {
            this.particleTimer -= 0.1F;

            for (int n = 0; n < 2; n++) {
               EnvironmentManager.addMovingParticle(
                  new MovingParticle(
                     this.ai.getPosition().plus(new Point(Math.random() - 0.5, 0.0, Math.random() - 0.5).scaled(15.0F)),
                     new Point(Math.random() - 0.5, 1.0, Math.random() - 0.5),
                     15.0F,
                     3.0F
                  )
               );
            }
         }
      }
   }

   public final void render(int droidIndex) {
      if (!(this.ai.getPosition().distanceTo(GameScene.avatar.getCameraPos()) > DepthAtmosphere.getFogDistance() * 1.5F)) {
         Point eyeColor = new Point(1.0F, 1.0F, 1.0F);
         switch (droidIndex % 4) {
            case 0:
               eyeColor = new Point(1.0F, 1.0F, 0.0F);
               break;
            case 1:
               eyeColor = new Point(0.0F, 0.5F, 1.0F);
               break;
            case 2:
               eyeColor = new Point(0.0F, 1.0F, 0.0F);
               break;
            case 3:
               eyeColor = new Point(1.0F, 0.5F, 0.5F);
         }

         Shaders.setUniform("topLight", true);
         GL11.glPushMatrix();
         this.ai.applyTransform();
         if (!this.working) {
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
         }

         GL11.glScalef(15.0F, 15.0F, 15.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Shaders.setUniform("alphaLightPercent", 1.0);
         Shaders.setUniform("alphaLightcolor", eyeColor);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, droidTexture);
         droidMesh.render();
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, droidItemTexture);
         GameScene.clear();
         Shaders.setUniform("alphaLightPercent", 0.0);
         if (this.attackCooldown < 0.5F) {
            GL11.glPushMatrix();
            GL11.glScalef(0.1F, 0.1F, 0.1F);
            new Arrow(new Point(0.5F, 1.2F, 1.7F), new Point(0.0F, 0.0F, 1.0F), WeaponType.IRON_SPEAR).render();
            GL11.glPopMatrix();
         }

         if (this.working && this.ai.getState() != DroidState.FOLLOWING) {
            Shaders.setUniform("emissive", true);
            GL11.glColor4f(eyeColor.x, eyeColor.y, eyeColor.z, (float)Math.cos(GameTime.elapsedMillis / 200.0F) * 0.3F + 0.7F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, eyeTexture);
            eyeMesh.render();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Shaders.setUniform("emissive", false);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (this.working) {
            GL11.glRotatef(GameTime.elapsedMillis / 4.0F, 0.0F, 1.0F, 0.0F);
         }

         GL11.glBindTexture(GL11.GL_TEXTURE_2D, fanTexture);
         fanMesh.render();
         GL11.glPopMatrix();
         Shaders.setUniform("topLight", false);
      }
   }

   public final Point getPosition() {
      return this.ai.getPosition();
   }

   public final boolean isWorking() {
      return this.working;
   }
}
