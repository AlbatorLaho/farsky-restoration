package game.inventory.types;

import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.StorageArray;

import java.io.IOException;
import java.io.ObjectInputStream;

public class WorkShopInventory extends TableInventory {
   private static final long serialVersionUID = -5999871067308356589L;
   private static transient String WORKSHOP_NAME = "Main WorkShop";

   public WorkShopInventory() {
      super(WORKSHOP_NAME);
      this.initWorkshopItems();
   }

   private void initWorkshopItems() {
      this.craftingItems = new StorageArray(5, 1);
      this.craftingItems.addItem(new Item(ItemType.MAIN_WORKSHOP));
      this.craftingItems.addItem(new Item(ItemType.EQUIPMENT_WORKSHOP));
      this.craftingItems.addItem(new Item(ItemType.WEAPON_WORKSHOP));
      this.craftingItems.addItem(new Item(ItemType.FURNITURE_WORKSHOP));
      this.craftingItems.addItem(new Item(ItemType.BUILDING_WORKSHOP));
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.initWorkshopItems();
      if (this.storageArray == null || this.storageArray.getWidth() > 1) {
         this.storageArray = new StorageArray(1, 1);
      }

      this.name = WORKSHOP_NAME;
   }
}
