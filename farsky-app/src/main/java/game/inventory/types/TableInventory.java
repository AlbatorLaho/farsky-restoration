package game.inventory.types;

import game.input.RawInput;
import game.inventory.Item;
import game.inventory.Storage;
import game.inventory.StorageArray;
import game.inventory.InventoryHud;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.sounds.SoundManager;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class TableInventory extends Inventory {
   private static final long serialVersionUID = -7903453737085750872L;
   private transient StorageArray requiredResources = new StorageArray(4, 1);
   protected transient StorageArray craftingItems;

   public TableInventory(String name) {
      super(name, 1, 1);
   }

   @Override
   public final Storage getStorageAt(Coord coord) {
      coord = coord.plus(new Coord(-Display.getWidth() / 2, -Display.getHeight() / 2));
      Storage outputSlot = this.storageArray.getStorageAt(coord.plus(new Coord(0.0F, -146.79999F)));
      Storage hoveredCraftItem = this.craftingItems.getStorageAt(coord.plus(new Coord(0.0F, -11.800003F)));
      if (hoveredCraftItem != null && hoveredCraftItem.getItem() != null) {
         this.requiredResources = hoveredCraftItem.getItem().getType().getResources();
         if (RawInput.leftMouseDown) {
            StorageArray craftingResources = hoveredCraftItem.getItem().getType().getResources();
            if ((
                  this.storageArray.get(0, 0).getItem() == null
                     || this.storageArray.get(0, 0).getItem().getType() == hoveredCraftItem.getItem().getType() && this.storageArray.get(0, 0).getItem().getType().isStackable() && this.storageArray.get(0, 0).getItem().getCount() < 75
               )
               && GameScene.avatar.getInventory().hasResources(craftingResources)) {
               this.storageArray.addItem(new Item(hoveredCraftItem.getItem().getType()));
               SoundManager.playSound(SoundManager.sfxBuild, null, 0.8F, 0.5F);
            }
         }
      } else {
         this.requiredResources = new StorageArray(4, 1);
      }

      return outputSlot != null ? outputSlot : null;
   }

   @Override
   public final void update(float dt) {
      Storage hoveredStorage = this.craftingItems.getHoveredStorage();
      if (hoveredStorage != null) {
         InventoryHud.setHoveredItem(hoveredStorage.getItem());
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
      GL11.glTranslatef(0.0F, 226.79999F, 0.0F);
      this.storageArray.render();
      GL11.glPopMatrix();
      if (!this.requiredResources.isEmpty()) {
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, -91.8F, 0.0F);
         FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
         FontRenderer.drawCentered(0, 0, "Required", 0.4F);
         GL11.glTranslatef(-96.0F, 48.6F, 0.0F);

         for (int col = 0; col < this.requiredResources.getWidth(); col++) {
            for (int row = 0; row < this.requiredResources.getHeight(); row++) {
               if (this.requiredResources.get(col, row).getItem() != null) {
                  GL11.glTranslatef(col * 64, row * 54 * 0.75F, 0.0F);
                  Storage.renderItem(this.requiredResources.get(col, row).getItem().getType(), 0.6F);
                  FontRenderer.draw(16, -FontRenderer.getCharHeight(0.4F) / 2, "" + this.requiredResources.get(col, row).getItem().getCount(), 0.4F);
                  GL11.glTranslatef(-(col * 64), -row * 54 * 0.75F, 0.0F);
               }
            }
         }

         GL11.glPopMatrix();
      }

      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 91.8F, 0.0F);
      this.craftingItems.render(false, true);
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.requiredResources = new StorageArray(4, 1);
   }
}
