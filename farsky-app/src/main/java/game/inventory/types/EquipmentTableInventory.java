package game.inventory.types;

import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.StorageArray;

import java.io.IOException;
import java.io.ObjectInputStream;

public class EquipmentTableInventory extends TableInventory {
   private static final long serialVersionUID = -5999871067308356589L;
   private static transient String WORKSHOP_NAME = "Equipment Workshop";

   public EquipmentTableInventory() {
      super(WORKSHOP_NAME);
      this.initEquipmentItems();
   }

   private void initEquipmentItems() {
      this.craftingItems = new StorageArray(5, 3);
      this.craftingItems.addItem(new Item(ItemType.IRON_DIVING_HELMET));
      this.craftingItems.addItem(new Item(ItemType.IRON_DIVING_SUIT));
      this.craftingItems.addItem(new Item(ItemType.IRON_DIVING_CYLINDER));
      this.craftingItems.addItem(new Item(ItemType.DRILL));
      this.craftingItems.addItem(new Item(ItemType.UNDERWATER_SCOOTER));
      this.craftingItems.addItem(new Item(ItemType.COPPER_DIVING_HELMET));
      this.craftingItems.addItem(new Item(ItemType.COPPER_DIVING_SUIT));
      this.craftingItems.addItem(new Item(ItemType.COPPER_DIVING_CYLINDER));
      this.craftingItems.addItem(new Item(ItemType.OVERPOWERED_DRILL));
      this.craftingItems.addItem(new Item(ItemType.BANDAGE));
      this.craftingItems.addItem(new Item(ItemType.MANGANESE_DIVING_HELMET));
      this.craftingItems.addItem(new Item(ItemType.MANGANESE_DIVING_SUIT));
      this.craftingItems.addItem(new Item(ItemType.MANGANESE_DIVING_CYLINDER));
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.initEquipmentItems();
      if (this.storageArray == null || this.storageArray.getWidth() > 1) {
         this.storageArray = new StorageArray(1, 1);
      }

      this.name = WORKSHOP_NAME;
   }
}
