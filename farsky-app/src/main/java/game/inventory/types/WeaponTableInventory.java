package game.inventory.types;

import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.StorageArray;

import java.io.IOException;
import java.io.ObjectInputStream;

public class WeaponTableInventory extends TableInventory {
   private static final long serialVersionUID = -5999871067308356589L;
   private static transient String WORKSHOP_NAME = "Weapon Workshop";

   public WeaponTableInventory() {
      super(WORKSHOP_NAME);
      this.initWeaponItems();
   }

   private void initWeaponItems() {
      this.craftingItems = new StorageArray(3, 3);
      this.craftingItems.addItem(new Item(ItemType.IRON_SPEAR));
      this.craftingItems.addItem(new Item(ItemType.IRON_STUN_SPEAR));
      this.craftingItems.addItem(new Item(ItemType.KNIFE));
      this.craftingItems.addItem(new Item(ItemType.COPPER_SPEAR));
      this.craftingItems.addItem(new Item(ItemType.COPPER_STUN_SPEAR));
      this.craftingItems.addItem(new Item(ItemType.SPEARGUN));
      this.craftingItems.addItem(new Item(ItemType.MANGANESE_SPEAR));
      this.craftingItems.addItem(new Item(ItemType.MANGANESE_STUN_SPEAR));
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.initWeaponItems();
      if (this.storageArray == null || this.storageArray.getWidth() > 1) {
         this.storageArray = new StorageArray(1, 1);
      }

      this.name = WORKSHOP_NAME;
   }
}
