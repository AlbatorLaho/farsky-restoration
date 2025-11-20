package game.environment;

import game.inventory.ItemType;
import java.io.Serializable;

public class Resource implements Serializable {
   private static final long serialVersionUID = -5800202896967352753L;
   public ItemType itemType;
   public int percent;

   public Resource(ItemType itemType, int percent) {
      this.itemType = itemType;
      this.percent = percent;
   }
}
