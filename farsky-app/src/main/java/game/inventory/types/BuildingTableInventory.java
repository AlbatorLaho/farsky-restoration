package game.inventory.types;

import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.StorageArray;
import game.manager.GameScene;

import java.io.IOException;
import java.io.ObjectInputStream;

public class BuildingTableInventory extends TableInventory {
   private static final long serialVersionUID = -5999871067308356589L;
   private static transient String WORKSHOP_NAME = "Building Workshop";

   public BuildingTableInventory() {
      super(WORKSHOP_NAME);
      this.initBuildingItems();
   }

   private void initBuildingItems() {
      this.craftingItems = new StorageArray(4, 3);
      this.craftingItems.addItem(new Item(ItemType.EXTRACTOR));
      this.craftingItems.addItem(new Item(ItemType.OVERPOWERED_EXTRACTOR));
      this.craftingItems.addItem(new Item(ItemType.NEW_BASE));
      this.craftingItems.addItem(new Item(ItemType.HARPOON_CANNON));
      this.craftingItems.addItem(new Item(ItemType.LAMP));
      this.craftingItems.addItem(new Item(ItemType.FLOOR));
      this.craftingItems.addItem(new Item(ItemType.GLASS_WALL));
      this.craftingItems.addItem(new Item(ItemType.LADDER));
      this.craftingItems.addItem(new Item(ItemType.NEW_LEVEL));
      if (GameScene.gameMode.hasCraftableSubmarine()) {
         this.craftingItems.addItem(new Item(ItemType.NEW_SUBMARINE));
      }
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.initBuildingItems();
      if (this.storageArray == null || this.storageArray.getWidth() > 1) {
         this.storageArray = new StorageArray(1, 1);
      }

      this.name = WORKSHOP_NAME;
   }
}
