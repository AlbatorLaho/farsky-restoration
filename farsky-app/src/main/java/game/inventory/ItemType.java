package game.inventory;

import game.player.weapons.WeaponType;

public enum ItemType {
   ROCK(1, 7, "Rock", InventoryElmtType.SLOT_MATERIAL, true),
   IRON(1, 2, "Iron", InventoryElmtType.SLOT_MATERIAL, true),
   DIRT(7, 3, "Dirt", InventoryElmtType.SLOT_MATERIAL, true),
   COAL(1, 5, "Coal", InventoryElmtType.SLOT_MATERIAL, true),
   GOLD(8, 2, "Gold", InventoryElmtType.SLOT_MATERIAL, true),
   MANGANESE(9, 7, "Manganese", InventoryElmtType.SLOT_MATERIAL, true),
   COPPER(2, 7, "Copper", InventoryElmtType.SLOT_MATERIAL, true),
   GLASS(5, 2, "Glass", InventoryElmtType.SLOT_MATERIAL, true),
   SAND(4, 2, "Sand", null, InventoryElmtType.SLOT_MATERIAL, true, null, Action.NONE, 6, GLASS),
   CRYSTAL(0, 5, "Crystal", InventoryElmtType.SLOT_MATERIAL, true),
   SILVER(5, 8, "Silver", InventoryElmtType.SLOT_MATERIAL, true),
   FERTILIZER(3, 3, "Fertilizer", InventoryElmtType.SLOT_MATERIAL, true),
   SEAWEED(5, 3, "Seaweed", InventoryElmtType.SLOT_MATERIAL, true),
   ENERGY_SPHERE(9, 5, "Energy Sphere", InventoryElmtType.SLOT_MATERIAL, true),
   NEW_BASE(2, 1, "New Base", InventoryElmtType.SLOT_BUILDING, false, new Item[]{new Item(IRON, 50), new Item(GOLD, 2), new Item(CRYSTAL, 1)}),
   NEW_SUBMARINE(0, 4, "New Submarine", InventoryElmtType.SLOT_BUILDING, false, new Item[]{new Item(IRON, 35), new Item(GLASS, 4), new Item(GOLD, 1), new Item(CRYSTAL, 1)}),
   FLOOR(3, 1, "Floor", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 3)}),
   GLASS_WALL(5, 1, "Glass Wall", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(GLASS, 2), new Item(COPPER, 2)}),
   HOLE(6, 1, "Hole", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 1)}),
   LADDER(7, 1, "Ladder", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 4)}),
   EXTRACTOR(2, 2, "Extractor", InventoryElmtType.SLOT_BUILDING, false, new Item[]{new Item(IRON, 30), new Item(GOLD, 1)}),
   OVERPOWERED_EXTRACTOR(7, 8, "Overpowered Extractor", InventoryElmtType.SLOT_BUILDING, false, new Item[]{new Item(MANGANESE, 30), new Item(GOLD, 1), new Item(SILVER, 2)}),
   MAIN_WORKSHOP(3, 2, "Main Workshop", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 15)}),
   EQUIPMENT_WORKSHOP(7, 4, "Equipment Workshop", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 15)}),
   WEAPON_WORKSHOP(8, 4, "Weapon Workshop", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 15)}),
   BUILDING_WORKSHOP(6, 4, "Building Workshop", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 15)}),
   FURNITURE_WORKSHOP(9, 4, "Furniture Workshop", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 15)}),
   PLANT_POT(6, 3, "Plant Pot", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 4), new Item(DIRT, 4), new Item(FERTILIZER, 2)}),
   CHEST(9, 3, "Chest", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 7)}),
   LARGE_CHEST(7, 9, "Large Chest", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 10), new Item(COPPER, 4)}),
   NEW_LEVEL(5, 4, "New Level", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 7)}),
   LAMP(5, 7, "Lamp", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 5), new Item(ENERGY_SPHERE, 2)}),
   COOKED_SHARK_MEAT(3, 5, "Cooked Shark Meat", new String[]{"Hunger: -50%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 50),
   SHARK_MEAT(6, 2, "Shark Meat", new String[]{"Hunger: -10%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 10, COOKED_SHARK_MEAT),
   COOKED_FRILLED_SHARK_MEAT(9, 6, "Cooked Frilled Shark Meat", new String[]{"Hunger: -50%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 50),
   FRILLED_SHARK_MEAT(8, 6, "Frilled Shark Meat", new String[]{"Hunger: -10%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 10, COOKED_FRILLED_SHARK_MEAT),
   COOKED_BARRACUDA_MEAT(1, 6, "Cooked Barracuda Meat", new String[]{"Hunger: -40%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 40),
   BARRACUDA_MEAT(0, 6, "Barracuda Meat", new String[]{"Hunger: -7%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 7, COOKED_BARRACUDA_MEAT),
   COOKED_FISH(2, 5, "Cooked Fish", new String[]{"Hunger: -15%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 15),
   FISH(7, 2, "Fish", new String[]{"Hunger: -4%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 4, COOKED_FISH),
   COOKED_ANGLERFISH(7, 6, "Cooked Anglerfish", new String[]{"Hunger: -20%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 15),
   ANGLERFISH(6, 6, "Anglerfish", new String[]{"Hunger: -5%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 4, COOKED_ANGLERFISH),
   COOKED_MANTA_RAY_MEAT(5, 6, "Cooked Manta Ray Meat", new String[]{"Hunger: -20%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 20),
   MANTA_RAY_MEAT(4, 6, "Manta Ray Meat", new String[]{"Hunger: -5%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 5, COOKED_MANTA_RAY_MEAT),
   COOKED_POTATO(4, 5, "Cooked Potato", new String[]{"Hunger: -25%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 25),
   POTATO(8, 3, "Potato", new String[]{"Hunger: -6%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 6, COOKED_POTATO),
   COOKED_CARROT(9, 8, "Cooked Carrot", new String[]{"Hunger: -20%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 20),
   CARROT(8, 8, "Carrot", new String[]{"Hunger: -5%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 5, COOKED_CARROT),
   CARROT_SEED(3, 9, "Carrot Seed", InventoryElmtType.SLOT_FOOD, true, null, Action.NONE),
   COOKED_GREEN_BEAN(5, 9, "Cooked Green Bean", new String[]{"Hunger: -8%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 8),
   GREEN_BEAN(4, 9, "Green Bean", new String[]{"Hunger: -2%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 2, COOKED_GREEN_BEAN),
   JELLY(3, 8, "Jelly", new String[]{"Hunger: -5%", "Cannot be cooked"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 5),
   COOKED_WHALE_MEAT(9, 9, "Cooked Whale Meat", new String[]{"Hunger: -50%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 50),
   WHALE_MEAT(8, 9, "Whale Meat", new String[]{"Hunger: -6%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 6, COOKED_WHALE_MEAT),
   COOKED_TUNA_MEAT(1, 10, "Cooked Tuna Meat", new String[]{"Hunger: -40%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 40),
   TUNA_MEAT(0, 10, "Tuna Meat", new String[]{"Hunger: -5%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 5, COOKED_TUNA_MEAT),
   COOKED_DOLPHIN_MEAT(3, 10, "Cooked Dolphin Meat", new String[]{"Hunger: -45%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 45),
   DOLPHIN_MEAT(2, 10, "Dolphin Meat", new String[]{"Hunger: -4%"}, InventoryElmtType.SLOT_FOOD, true, null, Action.EAT, 4, COOKED_DOLPHIN_MEAT),
   IRON_DIVING_HELMET(0, 3, "Iron Diving Helmet", new String[]{"Pressure Resistance: -250m"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(IRON, 15), new Item(GOLD, 1)}, Action.NONE),
   IRON_DIVING_SUIT(1, 3, "Iron Diving Suit", new String[]{"Damage reduction: 25%"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(IRON, 15), new Item(GOLD, 2)}, Action.NONE),
   IRON_DIVING_CYLINDER(2, 3, "Iron Diving Cylinder", new String[]{"Oxygen: +3min"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(IRON, 15), new Item(GOLD, 2)}, Action.NONE),
   COPPER_DIVING_HELMET(
      6,
      7,
      "Copper Diving Helmet",
      new String[]{"Pressure Resistance: -350m"},
      InventoryElmtType.SLOT_EQUIPMENT,
      false,
      new Item[]{new Item(COPPER, 15), new Item(GOLD, 1)},
      Action.NONE
   ),
   COPPER_DIVING_SUIT(7, 7, "Copper Diving Suit", new String[]{"Damage reduction: 40%"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(COPPER, 15), new Item(GOLD, 2)}, Action.NONE),
   COPPER_DIVING_CYLINDER(8, 7, "Copper Diving Cylinder", new String[]{"Oxygen: +5min"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(COPPER, 15), new Item(GOLD, 2)}, Action.NONE),
   MANGANESE_DIVING_HELMET(
      0,
      9,
      "Manganese Diving Helmet",
      new String[]{"Pressure Resistance: -350m", "Damage reduction: 10%"},
      InventoryElmtType.SLOT_EQUIPMENT,
      false,
      new Item[]{new Item(MANGANESE, 15), new Item(GOLD, 1)},
      Action.NONE
   ),
   MANGANESE_DIVING_SUIT(1, 9, "Manganese Diving Suit", new String[]{"Damage reduction: 50%"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(MANGANESE, 15), new Item(GOLD, 2)}, Action.NONE),
   MANGANESE_DIVING_CYLINDER(2, 9, "Manganese Diving Cylinder", new String[]{"Oxygen: +7min"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(MANGANESE, 15), new Item(GOLD, 2)}, Action.NONE),
   KNIFE(8, 1, "Knife", new String[]{"Damage: " + WeaponType.KNIFE.getDamage()}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(IRON, 8)}, Action.NONE),
   SPEARGUN(9, 1, "Speargun", new String[]{"Fire spears"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(IRON, 12), new Item(GOLD, 1)}, Action.NONE),
   DRILL(3, 4, "Drill", new String[]{"Extract resources"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(IRON, 18)}, Action.NONE),
   OVERPOWERED_DRILL(6, 8, "Overpowered Drill", new String[]{"Extract resources"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(MANGANESE, 18), new Item(SILVER, 3)}, Action.NONE),
   UNDERWATER_SCOOTER(3, 6, "Underwater Scooter", new String[]{"Propels you"}, InventoryElmtType.SLOT_EQUIPMENT, false, new Item[]{new Item(COPPER, 20), new Item(GOLD, 2)}, Action.NONE),
   IRON_SPEAR(0, 2, "Iron Spear", new String[]{"Damage: " + WeaponType.IRON_SPEAR.getDamage()}, InventoryElmtType.SLOT_EQUIPMENT, true, new Item[]{new Item(IRON, 3)}, Action.EQUIP),
   IRON_STUN_SPEAR(
      0,
      7,
      "Iron Stunning Spear",
      new String[]{"Damage: " + WeaponType.IRON_STUN_SPEAR.getDamage(), "Critical hit: " + (int)(WeaponType.IRON_STUN_SPEAR.getCriticalChance() * 100.0F) + "%"},
      InventoryElmtType.SLOT_EQUIPMENT,
      true,
      new Item[]{new Item(IRON, 3), new Item(ENERGY_SPHERE, 1)},
      Action.EQUIP
   ),
   COPPER_SPEAR(3, 7, "Copper Spear", new String[]{"Damage: " + WeaponType.COPPER_SPEAR.getDamage()}, InventoryElmtType.SLOT_EQUIPMENT, true, new Item[]{new Item(COPPER, 3)}, Action.EQUIP),
   COPPER_STUN_SPEAR(
      4,
      7,
      "Copper Stunning Spear",
      new String[]{"Damage: " + WeaponType.COPPER_STUN_SPEAR.getDamage(), "Critical hit: " + (int)(WeaponType.COPPER_STUN_SPEAR.getCriticalChance() * 100.0F) + "%"},
      InventoryElmtType.SLOT_EQUIPMENT,
      true,
      new Item[]{new Item(COPPER, 3), new Item(ENERGY_SPHERE, 1)},
      Action.EQUIP
   ),
   MANGANESE_SPEAR(1, 8, "Manganese Spear", new String[]{"Damage: " + WeaponType.MANGANESE_SPEAR.getDamage()}, InventoryElmtType.SLOT_EQUIPMENT, true, new Item[]{new Item(MANGANESE, 3)}, Action.EQUIP),
   MANGANESE_STUN_SPEAR(
      2,
      8,
      "Manganese Stunning Spear",
      new String[]{"Damage: " + WeaponType.MANGANESE_STUN_SPEAR.getDamage(), "Critical hit: " + (int)(WeaponType.MANGANESE_STUN_SPEAR.getCriticalChance() * 100.0F) + "%"},
      InventoryElmtType.SLOT_EQUIPMENT,
      true,
      new Item[]{new Item(MANGANESE, 3), new Item(ENERGY_SPHERE, 1)},
      Action.EQUIP
   ),
   UNNAMED_EQUIPMENT(0, 1, "", InventoryElmtType.SLOT_EQUIPMENT, true, new Item[]{new Item(IRON, 2)}),
   BANDAGE(4, 3, "Bandage", InventoryElmtType.SLOT_CONSUMABLE, true, new Item[]{new Item(SEAWEED, 2)}, Action.USE),
   UNNAMED_BUILDING_1(1, 4, "", InventoryElmtType.SLOT_BUILDING, true),
   UNNAMED_BUILDING_2(2, 4, "", InventoryElmtType.SLOT_BUILDING, true),
   HARPOON_CANNON(4, 4, "Harpoon Cannon", InventoryElmtType.SLOT_BUILDING, false, new Item[]{new Item(COPPER, 10), new Item(MANGANESE, 25), new Item(GOLD, 1)}),
   HAMMER(4, 1, "Hammer", new String[]{"Removes and repairs"}, InventoryElmtType.SLOT_BUILDING, false, new Item[]{new Item(IRON, 3)}, Action.NONE),
   TABLE(9, 2, "Table", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 10)}),
   STOOL(4, 8, "Stool", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 8)}),
   DOOR(7, 5, "Door", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 10), new Item(COPPER, 15)}),
   COOKER(6, 5, "Cooker", InventoryElmtType.SLOT_BUILDING, true, new Item[]{new Item(IRON, 10), new Item(COPPER, 15)}),
   SHIPWRECK(5, 5, "Shipwreck", InventoryElmtType.SLOT_BUILDING, true),
   TOMB(8, 5, "Tomb", InventoryElmtType.SLOT_BUILDING, true),
   DROID(6, 9, "Droid", InventoryElmtType.SLOT_BUILDING, true),
   SUBMARINE_PIECE(2, 6, "Submarine Piece", InventoryElmtType.SLOT_BUILDING, true);

   private int xOffset;
   private int yOffset;
   private String text;
   private String[] description;
   private StorageArray resources;
   private InventoryElmtType invType;
   private boolean stackable;
   private Action action;
   private int param;
   private ItemType cookInto;

   private ItemType(int xOffset, int yOffset, String text, InventoryElmtType invType, boolean stackable) {
      this(xOffset, yOffset, text, invType, stackable, null, Action.NONE);
   }

   private ItemType(int xOffset, int yOffset, String text, InventoryElmtType invType, boolean stackable, Item[] items) {
      this(xOffset, yOffset, text, null, invType, stackable, items, Action.NONE);
   }

   private ItemType(int xOffset, int yOffset, String text, InventoryElmtType invType, boolean stackable, Item[] items, Action action) {
      this(xOffset, yOffset, text, null, invType, stackable, items, action);
   }

   private ItemType(int xOffset, int yOffset, String text, String[] description, InventoryElmtType invType, boolean stackable, Item[] items, Action action) {
      this(xOffset, yOffset, text, description, invType, stackable, items, action, 0);
   }

   private ItemType(int xOffset, int yOffset, String text, String[] description, InventoryElmtType invType, boolean stackable, Item[] items, Action action, int param) {
      this(xOffset, yOffset, text, description, invType, stackable, items, action, param, null);
   }

   private ItemType(int xOffset, int yOffset, String text, String[] description, InventoryElmtType invType, boolean stackable, Item[] items, Action action, int param, ItemType cookInto) {
      this.xOffset = xOffset;
      this.yOffset = yOffset;
      this.text = text;
      this.description = description;
      this.invType = invType;
      this.stackable = stackable;
      this.action = action;
      this.param = param;
      this.cookInto = cookInto;
      this.resources = new StorageArray(4, 1);
      if (items != null) {
         for (int i = 0; i < items.length; i++) {
            this.resources.addItem(items[i]);
         }
      }
   }

   public final int getXOffset() {
      return this.xOffset;
   }

   public final int getYOffset() {
      return this.yOffset;
   }

   public final String getText() {
      return this.text;
   }

   public final String[] getDescription() {
      return this.description;
   }

   public final StorageArray getResources() {
      return this.resources;
   }

   public final InventoryElmtType getInventoryType() {
      return this.invType;
   }

   public final boolean isStackable() {
      return this.stackable;
   }

   public final Action getAction() {
      return this.action;
   }

   public final int getParam() {
      return this.param;
   }

   public final ItemType getCookInto() {
      return this.cookInto;
   }
}
