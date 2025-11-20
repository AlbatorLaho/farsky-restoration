package game.inventory.types;

import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.StorageArray;

import java.io.IOException;
import java.io.ObjectInputStream;

public class FurnitureTableInventory extends TableInventory {
   private static final long serialVersionUID = -5999871067308356589L;
   private static transient String WORKSHOP_NAME = "Furniture Workshop";

   public FurnitureTableInventory() {
      super(WORKSHOP_NAME);
      this.initFurnitureItems();
   }

   private void initFurnitureItems() {
      this.craftingItems = new StorageArray(3, 3);
      this.craftingItems.addItem(new Item(ItemType.PLANT_POT));
      this.craftingItems.addItem(new Item(ItemType.CHEST));
      this.craftingItems.addItem(new Item(ItemType.LARGE_CHEST));
      this.craftingItems.addItem(new Item(ItemType.DOOR));
      this.craftingItems.addItem(new Item(ItemType.COOKER));
      this.craftingItems.addItem(new Item(ItemType.STOOL));
      this.craftingItems.addItem(new Item(ItemType.TABLE));
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.initFurnitureItems();
      if (this.storageArray == null || this.storageArray.getWidth() > 1) {
         this.storageArray = new StorageArray(1, 1);
      }

      this.name = WORKSHOP_NAME;
   }
}
