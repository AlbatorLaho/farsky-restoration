package game.inventory.types;

import game.inventory.InventoryElmtType;
import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.Storage;
import game.inventory.StorageArray;
import game.manager.TextureManager;
import game.manager.GameTime;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class CookerInventory extends Inventory {
   private static final long serialVersionUID = -7903453737085750872L;
   protected StorageArray food;
   protected StorageArray cookedFood;
   private float fireTimer;
   private boolean cooking = false;
   private static transient float COOK_DURATION = 6.0F;

   public CookerInventory() {
      super("Cooker", 1, 1);
      this.food = new StorageArray(1, 1);
      this.cookedFood = new StorageArray(1, 1);
      this.storageArray.setSlotType(0, 0, InventoryElmtType.SLOT_COOKER);
      this.fireTimer = 0.0F;
   }

   @Override
   public final Storage getStorageAt(Coord coord) {
      coord = coord.plus(new Coord((float)(-Display.getWidth() / 2), -Display.getHeight() / 2 + 80.0F));
      Storage storage = this.storageArray.getStorageAt(coord.plus(new Coord(81.0F, -162.0F)));
      if (storage == null) {
         storage = this.food.getStorageAt(coord.plus(new Coord(81.0F, 0.0F)));
      }

      if (storage == null) {
         storage = this.cookedFood.getStorageAt(coord.plus(new Coord(-81.0F, 0.0F)));
      }

      return storage;
   }

   @Override
   public final void update(float dt) {
      if (this.fireTimer > 0.0F) {
         this.fireTimer -= dt;
      } else {
         this.fireTimer = 0.0F;
         if (this.cooking) {
            if (this.food.get(0, 0).getItem() != null) {
               ItemType itemType = this.food.get(0, 0).getItem().getType();
               if (itemType.getCookInto() != null && this.cookedFood.addItem(new Item(itemType.getCookInto())).getCount() == 0) {
                  this.food.get(0, 0).consume();
                  this.cooking = false;
               }
            } else {
               this.cooking = false;
            }
         } else {
            if (this.storageArray.findItem(ItemType.COAL) != null && this.food.get(0, 0).getItem() != null && this.food.get(0, 0).getItem().getType().getCookInto() != null) {
               this.storageArray.get(0, 0).consume();
               this.fireTimer = COOK_DURATION;
               this.cooking = true;
            }
         }
      }
   }

   @Override
   public final void render() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F, Display.getHeight() / 2.0F - 80.0F, 0.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, -180.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.inventoryDescription1);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(175.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(-175.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(-175.0F, 460.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(175.0F, 460.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      FontRenderer.drawCentered(0, -170, this.name, 0.9F);
      GL11.glPushMatrix();
      GL11.glTranslatef(-81.0F, 162.0F, 0.0F);
      this.storageArray.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-81.0F, 0.0F, 0.0F);
      this.food.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(81.0F, 0.0F, 0.0F);
      this.cookedFood.render();
      GL11.glPopMatrix();
      float cookProgress = 1.0F - this.fireTimer / COOK_DURATION;
      if (!this.cooking) {
         cookProgress = 0.0F;
      }

      GL11.glPushMatrix();
      GL11.glTranslatef(-81.0F, 35.5F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.fire);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)Math.cos(GameTime.elapsedMillis / 40.0F) * 0.1F + 0.9F);
      GL11.glTexCoord2f(0.0F, 1.0F - cookProgress);
      GL11.glVertex2f(-31.5F, 91.0F - 91.0F * cookProgress);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-31.5F, 91.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(31.5F, 91.0F);
      GL11.glTexCoord2f(1.0F, 1.0F - cookProgress);
      GL11.glVertex2f(31.5F, 91.0F - 91.0F * cookProgress);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.2F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-31.5F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F - cookProgress);
      GL11.glVertex2f(-31.5F, 91.0F - 91.0F * cookProgress);
      GL11.glTexCoord2f(1.0F, 1.0F - cookProgress);
      GL11.glVertex2f(31.5F, 91.0F - 91.0F * cookProgress);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(31.5F, 0.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }

   public final boolean isCooking() {
      return this.cooking;
   }

   public final void resetCooker() {
      this.storageArray.get(0, 0).setItem(null);
      this.cooking = false;
      this.fireTimer = 0.0F;
   }

   @Override
   public final boolean isEmpty() {
      return this.storageArray.isEmpty() && this.food.isEmpty() && this.cookedFood.isEmpty();
   }
}
