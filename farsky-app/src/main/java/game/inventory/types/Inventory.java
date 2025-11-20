package game.inventory.types;

import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.Storage;
import game.inventory.StorageArray;
import game.inventory.InventoryHud;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;
import java.io.Serializable;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Inventory implements Serializable {
   private static final long serialVersionUID = 4089340110453498850L;
   protected StorageArray storageArray;
   protected String name;

   public Inventory(String name, int cols, int rows) {
      this(name, new Item[cols][rows], cols, rows);
   }

   private Inventory(String name, Item[][] items, int cols, int rows) {
      this.name = name;
      this.storageArray = new StorageArray(items, cols, rows);
   }

   public Inventory(String name, StorageArray storageArray) {
      this.name = name;
      this.storageArray = storageArray;
   }

   public void update(float dt) {
      InventoryHud.setTakeAllButtonYPos((int)(this.storageArray.getHeight() / 2.0F * 54.0F) + 30);
   }

   public Storage getStorageAt(Coord coord) {
      return this.storageArray.getStorageAt(coord.plus(new Coord(-Display.getWidth() / 2, -Display.getHeight() / 2)));
   }

   public void render() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F, Display.getHeight() / 2.0F, 0.0F);
      FontRenderer.drawCentered(0, -200, this.name, 0.9F);
      this.storageArray.render();
      GL11.glPopMatrix();
   }

   public Item findItem(ItemType itemType) {
      return this.storageArray.findItem(itemType);
   }

   public Item addItem(Item item) {
      return this.storageArray.addItem(item);
   }

   public boolean isEmpty() {
      return this.storageArray.isEmpty();
   }

   public void removeItem(ItemType itemType) {
      this.storageArray.consumeOne(itemType);
   }

   public final StorageArray getStorageArray() {
      return this.storageArray;
   }

   public final void clearStorage() {
      this.storageArray.clear();
   }
}
