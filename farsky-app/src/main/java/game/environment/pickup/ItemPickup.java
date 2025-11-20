package game.environment.pickup;

import game.chunks.ChunkManager;
import game.inventory.Item;
import game.inventory.ItemType;
import game.manager.Camera;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.shader.Shaders;
import game.manager.GameTime;
import game.util.Point;
import game.util.UnitQuad;
import org.lwjgl.opengl.GL11;

public final class ItemPickup {
   private ItemType itemType;
   private Point position;
   private Point velocity;
   private float lifetime;
   private float bobPhase;
   private float spawnProtection;

   public ItemPickup(Point position, ItemType itemType) {
      this.itemType = itemType;
      this.position = position.copy();
      this.velocity = new Point((Math.random() - 0.5) * 100.0, 0.0, (Math.random() - 0.5) * 100.0);
      this.lifetime = 120.0F;
      this.bobPhase = (float)(Math.random() * Math.PI * 2.0);
      this.spawnProtection = 0.3F;
   }

   public final void update(float delta) {
      this.lifetime -= delta;
      if (this.spawnProtection > 0.0F) {
         this.spawnProtection -= delta;
      }

      float groundHeight = ChunkManager.getHeightSmooth(this.position.x, this.position.z);
      if (this.position.y > groundHeight) {
         this.velocity.add(GameScene.gravity.scaled(delta * 120.0F));
         this.velocity.x /= 1.0F + delta * 2.0F;
         this.velocity.z /= 1.0F + delta * 2.0F;
      } else {
         this.velocity = new Point();
         this.position.y = groundHeight;
      }

      groundHeight = this.position.distanceTo(GameScene.avatar.getPos());
      if (this.spawnProtection <= 0.0F && groundHeight < 60.0F) {
         float prevVelY = this.velocity.y;
         this.velocity = GameScene.avatar.getPos().minus(this.position);
         this.velocity.normalize();
         this.velocity.scale(0.2F);
         if (groundHeight > 10.0F) {
            this.velocity.scale(100000.0F / groundHeight * delta);
         } else if (GameScene.avatar.pickupItem(new Item(this.itemType))) {
            this.lifetime = 0.0F;
         }

         this.velocity.y = prevVelY;
      }

      this.position.add(this.velocity.scaled(delta));
   }

   public final void render() {
      Shaders.setUniform("emissive", true);
      GL11.glDepthMask(false);
      switch (this.itemType) {
         case FISH:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.fishSmall);
            break;
         case ANGLERFISH:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.anglerfish);
            break;
         case SHARK_MEAT:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.fishBig);
            break;
         case FERTILIZER:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.fertilizer);
            break;
         case SEAWEED:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.seaweedItem);
            break;
         case BARRACUDA_MEAT:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.barracudaMeat);
            break;
         case MANTA_RAY_MEAT:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.mantaRay);
            break;
         case FRILLED_SHARK_MEAT:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.frilledSharkMeat);
            break;
         case ENERGY_SPHERE:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.energySphere);
            break;
         case JELLY:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.jelly);
            break;
         case WHALE_MEAT:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.whaleMeat);
            break;
         case TUNA_MEAT:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.tunaMeat);
            break;
         case DOLPHIN_MEAT:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.dolphinMeat);
		default:
			break;
      }

      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y + 10.0F + (float)Math.cos(GameTime.elapsedMillis / 1000.0F + this.bobPhase), this.position.z);
      Camera.applyYawPitch();
      GL11.glScalef(8.0F, 8.0F, 8.0F);
      UnitQuad.render();
      GL11.glPopMatrix();
      GL11.glDepthMask(true);
      Shaders.setUniform("emissive", false);
   }

   public final boolean isPickedUp() {
      return this.lifetime <= 0.0F;
   }
}
