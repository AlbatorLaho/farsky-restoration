package game.inventory.types;

import game.gui.PlayerHud;
import game.input.RawInput;
import game.inventory.InventoryElmtType;
import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.Storage;
import game.inventory.StorageArray;
import game.inventory.InventoryHud;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.manager.GameTime;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class PlayerInventory extends Inventory {
   private static final long serialVersionUID = -2945422457652133275L;
   private StorageArray bottomBar = null;
   private StorageArray equipment = null;
   private transient int selectedSlot = 0;
   private transient float tooltipTimer = 0.0F;
   private transient StorageArray hammerSlot = null;

   public PlayerInventory(String name, int width, int height) {
      super(name, 5, 4);
      this.bottomBar = new StorageArray(7, 1);
      this.hammerSlot = new StorageArray(1, 1);
      this.hammerSlot.get(0, 0).setItem(new Item(ItemType.HAMMER));
      this.equipment = new StorageArray(1, 3);
      this.equipment.setSlotType(0, 0, InventoryElmtType.SLOT_HELMET);
      this.equipment.setSlotType(0, 1, InventoryElmtType.SLOT_SUIT);
      this.equipment.setSlotType(0, 2, InventoryElmtType.SLOT_CYLINDER);
   }

   @Override
   public final void update(float delta) {
      if (this.tooltipTimer > 0.0F) {
         this.tooltipTimer -= delta;
      } else {
         this.tooltipTimer = 0.0F;
      }

      if (PlayerHud.hungerIconTimer > 0.0F) {
         PlayerHud.hungerIconTimer -= delta;
      } else {
         PlayerHud.hungerIconTimer = 0.0F;
      }
   }

   @Override
   public final Storage getStorageAt(Coord coord) {
      coord = coord.plus(new Coord(-Display.getWidth() / 2, -Display.getHeight() / 2));
      Storage mainStorage = this.storageArray.getStorageAt(coord.plus(new Coord(81.0F, 0.0F)));
      Storage bottomStorage = this.bottomBar.getStorageAt(coord.plus(new Coord(0, -54 * this.storageArray.getHeight() / 2 - 10)));
      Storage equipStorage = this.equipment.getStorageAt(coord.plus(new Coord(-243.0F, 0.0F)));
      coord = coord.plus(new Coord(-54.0F, 135.0F));
      if (GameScene.avatar.hasWoundedArm() && coord.x > 40.5F && coord.x < 61.0F && coord.y > 54.0F && coord.y < 98.0F) {
         if (this.findItem(ItemType.BANDAGE) == null) {
            InventoryHud.setTooltip("Bleeding", new String[]{"Need bandage"});
         } else {
            InventoryHud.setTooltip("Wounded", new String[]{"Click to put a bandage"});
            if (RawInput.leftMouseDown) {
               this.removeItem(ItemType.BANDAGE);
               GameScene.avatar.setWoundedArm(false);
            }
         }
      }

      if (GameScene.avatar.hasWoundedLeg() && coord.x > 40.5F && coord.x < 81.0F && coord.y > 118.0F && coord.y < 172.8F) {
         if (this.findItem(ItemType.BANDAGE) == null) {
            InventoryHud.setTooltip("Bleeding", new String[]{"Need bandage"});
         } else {
            InventoryHud.setTooltip("Wounded", new String[]{"Click to put a bandage"});
            if (RawInput.leftMouseDown) {
               this.removeItem(ItemType.BANDAGE);
               GameScene.avatar.setWoundedLeg(false);
            }
         }
      }

      if (GameScene.avatar.getHunger() < 0.3F && coord.x > 64.8F && coord.x < 97.200005F && coord.y > 54.0F && coord.y < 108.0F) {
         InventoryHud.setTooltip("Hungry", new String[]{"Need food"});
      }

      if (mainStorage != null) {
         return mainStorage;
      } else {
         return equipStorage != null ? equipStorage : bottomStorage;
      }
   }

   @Override
   public final void render() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.itemWheel);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F + 216.0F, Display.getHeight() / 2.0F - 81.0F, 0.0F);
      GL11.glRotatef(-GameTime.elapsedMillis / 100.0F, 0.0F, 0.0F, 1.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-41.0F, -41.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-41.0F, 41.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(41.0F, 41.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(41.0F, -41.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F + 243.0F, Display.getHeight() / 2, 0.0F);
      this.equipment.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      GL11.glTranslatef(Display.getWidth() / 2.0F, Display.getHeight() / 2.0F, 0.0F);
      FontRenderer.drawCentered(0, -200, this.name, 0.9F);
      GL11.glTranslatef(-81.0F, 0.0F, 0.0F);
      this.storageArray.render();
      GL11.glPopMatrix();
      float pulseAlpha = (float)Math.cos(GameTime.elapsedMillis / 100.0F) * 0.5F + 1.0F;
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F + 54.0F, Display.getHeight() / 2.0F - 135.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.avatarInventory);
      drawAvatarQuad();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, pulseAlpha);
      if (GameScene.avatar.hasWoundedArm()) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.woundedArm);
         drawAvatarQuad();
      }

      if (GameScene.avatar.hasWoundedLeg()) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.woundedLeg);
         drawAvatarQuad();
      }

      if (GameScene.avatar.getHunger() >= 0.4F && PlayerHud.hungerIconTimer <= 0.0F) {
         renderHungerText(0.35F);
      } else {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.starving);
         if (GameScene.avatar.getHunger() < 0.2F) {
            GL11.glColor4f(0.7F, 0.0F, 0.0F, pulseAlpha);
         } else if (GameScene.avatar.getHunger() < 0.4F) {
            GL11.glColor4f(0.7F, 0.5F, 0.0F, pulseAlpha);
         } else {
            GL11.glColor4f(0.1F, 0.8F, 0.1F, 1.0F);
         }

         drawAvatarQuad();
         renderHungerText(1.0F);
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F, Display.getHeight() / 2 + 54 * this.storageArray.getHeight() / 2, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.inventorySeparator);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-180.0F, -27.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-180.0F, -17.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(180.0F, -17.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(180.0F, -27.0F);
      GL11.glEnd();
      GL11.glTranslatef(0.0F, 10.0F, 0.0F);
      this.bottomBar.render();
      GL11.glPopMatrix();
   }

   private static void renderHungerText(float alpha) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      FontRenderer.drawCentered(81, 180, "Hunger:" + (int)((1.0F - GameScene.avatar.getHunger()) * 100.0F) + "%", 0.5F);
   }

   private static void drawAvatarQuad() {
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 216.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(162.0F, 216.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(162.0F, 0.0F);
      GL11.glEnd();
   }

   public final void renderBottomBar() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.itemWheel);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F - this.bottomBar.getWidth() * 54 / 2 - 27.0F, Display.getHeight(), 0.0F);
      GL11.glRotatef(GameTime.elapsedMillis / 100.0F, 0.0F, 0.0F, 1.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-41.0F, -41.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-41.0F, 41.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(41.0F, 41.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(41.0F, -41.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F + this.bottomBar.getWidth() * 54 / 2 + 27.0F + 10.0F, Display.getHeight(), 0.0F);
      GL11.glRotatef(-GameTime.elapsedMillis / 100.0F, 0.0F, 0.0F, 1.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-41.0F, -41.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-41.0F, 41.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(41.0F, 41.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(41.0F, -41.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F - 27.0F, Display.getHeight() - 27, 0.0F);
      this.bottomBar.render();
      GL11.glTranslatef(226.0F, 0.0F, 0.0F);
      this.hammerSlot.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F + this.bottomBar.getWidth() * 54 / 2 - 27.0F, Display.getHeight(), 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.inventorySeparator);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(0.0F, -54.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(10.0F, -54.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(10.0F, 0.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      Item item = this.getCurrentSlotStorage().getItem();
      if (item != null && this.tooltipTimer > 0.0F) {
         InventoryHud.renderTooltip(item, new Coord(Display.getWidth() / 2.0F, (float)(Display.getHeight() - 54)), this.tooltipTimer);
      }
   }

   public final void selectNextSlot() {
      this.selectedSlot++;
      if (this.selectedSlot >= this.bottomBar.getWidth() + 1) {
         this.selectedSlot = this.bottomBar.getWidth();
      }

      this.refreshSlotHighlights();
   }

   public final void selectPrevSlot() {
      this.selectedSlot--;
      if (this.selectedSlot < 0) {
         this.selectedSlot = 0;
      }

      this.refreshSlotHighlights();
   }

   public final void selectSlot(int slot) {
      this.selectedSlot = slot;
      this.refreshSlotHighlights();
   }

   public final void refreshSlotHighlights() {
      for (int i = 0; i < this.bottomBar.getWidth(); i++) {
         this.bottomBar.setHovered(i, 0, i == this.selectedSlot);
      }

      this.hammerSlot.setHovered(0, 0, this.selectedSlot == 7);
      this.tooltipTimer = 1.5F;
   }

   public final Storage getCurrentSlotStorage() {
      return this.selectedSlot == 7 ? this.hammerSlot.get(0, 0) : this.bottomBar.get(this.selectedSlot, 0);
   }

   public final Item getCurrentSlotItem() {
      return this.getCurrentSlotStorage().getItem();
   }

   @Override
   public final Item findItem(ItemType type) {
      Item found = this.bottomBar.findItem(type);
      return found != null ? found : this.storageArray.findItem(type);
   }

   public final ArrayList<Item> findAllItems(ItemType type) {
      ArrayList<Item> result = new ArrayList<>();
      result.addAll(this.bottomBar.findAllItems(type));
      result.addAll(this.storageArray.findAllItems(type));
      return result;
   }

   @Override
   public final Item addItem(Item item) {
      item = this.bottomBar.addItem(item);
      return this.storageArray.addItem(item);
   }

   @Override
   public final boolean isEmpty() {
      return this.bottomBar.isEmpty() && this.storageArray.isEmpty();
   }

   public final void consumeCurrentItem() {
      this.getCurrentSlotStorage().consume();
   }

   public final int getHelmetOxygenBonus() {
      short bonus = 0;
      Item item = this.equipment.get(0, 0).getItem();
      if (item != null) {
         switch (item.getType()) {
            case IRON_DIVING_HELMET:
               bonus = 250;
               break;
            case COPPER_DIVING_HELMET:
            case MANGANESE_DIVING_HELMET:
               bonus = 350;
			default:
				break;
         }
      }

      return bonus;
   }

   public final float getSuitDamageReduction() {
      float reduction = 1.0F;
      Item item = this.equipment.get(0, 1).getItem();
      if (item != null) {
         switch (item.getType()) {
            case IRON_DIVING_SUIT:
               reduction = 0.75F;
               break;
            case COPPER_DIVING_SUIT:
               reduction = 0.6F;
               break;
            case MANGANESE_DIVING_SUIT:
               reduction = 0.5F;
			default:
				break;
         }
      }

      item = this.equipment.get(0, 0).getItem();
      if (item != null) {
         switch (item.getType()) {
            case MANGANESE_DIVING_HELMET:
               reduction -= 0.1F;
			default:
				break;
         }
      }

      return reduction;
   }

   public final int getCylinderOxygenCapacity() {
      short capacity = 0;
      Item item = this.equipment.get(0, 2).getItem();
      if (item != null) {
         switch (item.getType()) {
            case IRON_DIVING_CYLINDER:
               capacity = 180;
               break;
            case COPPER_DIVING_CYLINDER:
               capacity = 300;
               break;
            case MANGANESE_DIVING_CYLINDER:
               capacity = 420;
			default:
				break;
         }
      }

      return capacity;
   }

   public final boolean canCraft(Item item) {
      StorageArray required = new StorageArray(new Item[][]{{item}}, 1, 1);
      return this.hasResources(required, false);
   }

   public final boolean hasResources(StorageArray required) {
      return this.hasResources(required, false);
   }

   public final boolean hasResources(StorageArray required, boolean checkOnly) {
      StorageArray combined = new StorageArray(this.storageArray.getWidth() * this.storageArray.getHeight() + this.bottomBar.getWidth() * this.bottomBar.getHeight(), 1);

      for (int col = 0; col < this.storageArray.getWidth(); col++) {
         for (int row = 0; row < this.storageArray.getHeight(); row++) {
            combined.set(col * this.storageArray.getHeight() + row, 0, this.storageArray.get(col, row));
         }
      }

      for (int col = 0; col < this.bottomBar.getWidth(); col++) {
         for (int row = 0; row < this.bottomBar.getHeight(); row++) {
            combined.set(this.storageArray.getWidth() * this.storageArray.getHeight() + col * this.bottomBar.getHeight() + row, 0, this.bottomBar.get(col, row));
         }
      }

      return combined.hasResources(required, checkOnly);
   }

   @Override
   public final void removeItem(ItemType type) {
      if (this.bottomBar.findItem(type) != null) {
         this.bottomBar.consumeOne(type);
      } else {
         if (this.storageArray.findItem(type) != null) {
            this.storageArray.consumeOne(type);
         }
      }
   }

   public final StorageArray takeAllItems() {
      StorageArray taken = new StorageArray(5, 6);

      for (int col = 0; col < this.storageArray.getWidth(); col++) {
         for (int row = 0; row < this.storageArray.getHeight(); row++) {
            if (this.storageArray.get(col, row).getItem() != null) {
               taken.addItem(this.storageArray.get(col, row).getItem());
               this.storageArray.get(col, row).setItem(null);
            }
         }
      }

      for (int col = 0; col < this.bottomBar.getWidth(); col++) {
         if (this.bottomBar.get(col, 0).getItem() != null) {
            taken.addItem(this.bottomBar.get(col, 0).getItem());
            this.bottomBar.get(col, 0).setItem(null);
         }
      }

      return taken;
   }

   public final void clearNewFlags() {
      for (int col = 0; col < this.storageArray.getWidth(); col++) {
         for (int row = 0; row < this.storageArray.getHeight(); row++) {
            if (this.storageArray.get(col, row).getItem() != null) {
               this.storageArray.get(col, row).getItem().setIsNew(false);
            }
         }
      }

      for (int col = 0; col < this.bottomBar.getWidth(); col++) {
         if (this.bottomBar.get(col, 0).getItem() != null) {
            this.bottomBar.get(col, 0).getItem().setIsNew(false);
         }
      }
   }

   public final void removeEmptyItems() {
      for (int col = 0; col < this.storageArray.getWidth(); col++) {
         for (int row = 0; row < this.storageArray.getHeight(); row++) {
            if (this.storageArray.get(col, row).getItem() != null && this.storageArray.get(col, row).getItem().getCount() == 0) {
               this.storageArray.get(col, row).setItem(null);
            }
         }
      }

      for (int col = 0; col < this.bottomBar.getWidth(); col++) {
         if (this.bottomBar.get(col, 0).getItem() != null && this.bottomBar.get(col, 0).getItem().getCount() == 0) {
            this.bottomBar.get(col, 0).setItem(null);
         }
      }
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.hammerSlot = new StorageArray(1, 1);
      this.hammerSlot.get(0, 0).setItem(new Item(ItemType.HAMMER));
   }
}
