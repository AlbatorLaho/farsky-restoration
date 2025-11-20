package game.inventory;

public enum InventoryElmtType {
   SLOT_FRAME(0, 0),
   SLOT_HIGHLIGHT(0, 1),
   SLOT_STACK_COUNT(1, 1),
   SLOT_EMPTY(1, 0),
   SLOT_EQUIPMENT(2, 0),
   SLOT_OVERLAY(6, 0),
   SLOT_BUILDING(3, 0),
   SLOT_MATERIAL(4, 0),
   SLOT_CONSUMABLE(5, 0),
   SLOT_FOOD(5, 0),
   SLOT_HELMET(7, 0),
   SLOT_SUIT(8, 0),
   SLOT_CYLINDER(9, 0),
   SLOT_COOKER(0, 8);

   private int Xoffset;
   private int Yoffset;

   private InventoryElmtType(int xOffset, int yOffset) {
      this.Xoffset = xOffset;
      this.Yoffset = yOffset;
   }

   public final int getXOffset() {
      return this.Xoffset;
   }

   public final int getYOffset() {
      return this.Yoffset;
   }
}
