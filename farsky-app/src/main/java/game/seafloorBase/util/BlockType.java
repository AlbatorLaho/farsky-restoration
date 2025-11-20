package game.seafloorBase.util;

import game.inventory.ItemType;

public enum BlockType {
   FLOOR(false, false, false, ItemType.FLOOR),
   WALL(false, false, false),
   WALL_DIAGONAL(false, false, false),
   WALL_CORNER(false, false, false),
   WALL_T(false, false, false),
   WALL_CROSS(false, false, false),
   SEABED(false, false, false),
   CEILING(false, false, false),
   ROOF(false, false, false),
   SOFFIT(false, false, false),
   WATER_LEVEL(false, false, false),
   HOLE(false, false, false, ItemType.HOLE),
   LADDER(true, true, false, ItemType.LADDER),
   WORKSHOP(true, false, true, ItemType.MAIN_WORKSHOP),
   EQUIPMENT_WORKSHOP(true, false, true, ItemType.EQUIPMENT_WORKSHOP),
   WEAPON_WORKSHOP(true, false, true, ItemType.WEAPON_WORKSHOP),
   BUILDING_WORKSHOP(true, false, true, ItemType.BUILDING_WORKSHOP),
   FURNITURE_WORKSHOP(true, false, true, ItemType.FURNITURE_WORKSHOP),
   PLANT_POT(true, false, true, ItemType.PLANT_POT),
   CHEST(true, false, true, ItemType.CHEST),
   LARGE_CHEST(true, false, true, ItemType.LARGE_CHEST),
   TABLE(true, false, true, ItemType.TABLE),
   STOOL(true, false, true, ItemType.STOOL),
   DOOR(true, false, true, ItemType.DOOR),
   COOKER(true, false, true, ItemType.COOKER);

   private boolean interaction;
   private boolean hung;
   private boolean blockSize;
   private ItemType itemType;

   private BlockType(boolean interaction, boolean hung, boolean blockSize) {
      this(interaction, hung, blockSize, null);
   }

   private BlockType(boolean interaction, boolean hung, boolean blockSize, ItemType itemType) {
      this.interaction = interaction;
      this.hung = hung;
      this.blockSize = blockSize;
      this.itemType = itemType;
   }

   public final boolean isInteractive() {
      return this.interaction;
   }

   public final boolean isHung() {
      return this.hung;
   }

   public final boolean isBlockSized() {
      return this.blockSize;
   }

   public final ItemType getItemType() {
      return this.itemType;
   }
}
