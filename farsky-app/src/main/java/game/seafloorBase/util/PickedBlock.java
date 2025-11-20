package game.seafloorBase.util;

import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.types.Inventory;
import game.seafloorBase.Block;
import game.seafloorBase.Element;
import game.util.Point;
import java.util.ArrayList;

public class PickedBlock {
   public static enum PickType {
      FLOOR,
      WALL,
      CEILING;
   }

   public int x;
   public int y;
   public int z;
   public PickType pickType = PickType.FLOOR;
   public Block block;
   private static RandomBase[][][] roomGrid;
   private static RandomBase[][][] prevRoomGrid;
   private static Block[][][] blockGrid;

   public PickedBlock(int x, int y, int z, PickType pickType) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.pickType = pickType;
   }

   public final Point getPosition() {
      return new Point((float)this.x, (float)this.y, (float)this.z);
   }

   public static Block[][][] generateRandomBase(Block[][][] blocks, int levelCount) {
      blockGrid = blocks;
      roomGrid = new RandomBase[4][8][4];

      for (int level = 1; level < Math.min(7, 5); level++) {
         addRoom(2, level, 2, 0, 0, RandomBase.RoomType.HUB);

         for (int pass = 0; pass < 2; pass++) {
            prevRoomGrid = roomGrid;

            for (int rx = 1; rx < 3; rx++) {
               for (int rz = 1; rz < 3; rz++) {
                  if (prevRoomGrid[rx][level][rz] != null) {
                     if (Math.random() < 0.5) {
                        addRoom(rx, level, rz, 0, 1);
                     }

                     if (Math.random() < 0.5) {
                        addRoom(rx, level, rz, 1, 0);
                     }

                     if (Math.random() < 0.5) {
                        addRoom(rx, level, rz, 0, -1);
                     }

                     if (Math.random() < 0.5) {
                        addRoom(rx, level, rz, -1, 0);
                     }
                  }
               }
            }
         }
      }

      for (int level2 = 6; level2 > 0; level2--) {
         ArrayList<RandomBase.RoomType> roomTypes = new ArrayList<>();
         roomTypes.add(RandomBase.RoomType.BASIC);
         if (level2 == 1) {
            roomTypes.add(RandomBase.RoomType.WORKSHOP);
         }

         if (Math.random() < 0.5) {
            roomTypes.add(RandomBase.RoomType.GARDEN);
         }

         if (Math.random() < 0.5) {
            roomTypes.add(RandomBase.RoomType.STORAGE);
         }

         roomTypes.add(RandomBase.RoomType.LOUNGE);

         for (int rx = 0; rx < 4; rx++) {
            for (int rz = 0; rz < 4; rz++) {
               if (prevRoomGrid[rx][level2][rz] != null && prevRoomGrid[rx][level2][rz].roomType == RandomBase.RoomType.PLACEHOLDER && roomTypes.size() > 0) {
                  prevRoomGrid[rx][level2][rz].roomType = roomTypes.remove(0);
                  if (prevRoomGrid[rx][level2][rz].roomType == RandomBase.RoomType.BASIC && level2 > 1 && prevRoomGrid[rx][level2 - 1][rz] != null) {
                     prevRoomGrid[rx][level2 - 1][rz] = new RandomBase(RandomBase.RoomType.STAIRWELL);
                  }
               }
            }
         }
      }

      buildRooms();
      return blockGrid;
   }

   private static void addRoom(int rx, int ry, int rz, int dx, int dz) {
      addRoom(rx, ry, rz, dx, dz, RandomBase.RoomType.PLACEHOLDER);
   }

   private static void addRoom(int rx, int ry, int rz, int dx, int dz, RandomBase.RoomType roomType) {
      if (roomGrid[rx + dx][ry][rz + dz] == null) {
         roomGrid[rx + dx][ry][rz + dz] = new RandomBase(roomType);
         int x0 = (rx << 3) + 4;
         int x1 = (rx + dx << 3) + 4;
         int z0 = (rz << 3) + 4;
         int z1 = (rz + dz << 3) + 4;
         if (dx < 0) {
            int tmp = x0; x0 = x1; x1 = tmp;
         }
         if (dz < 0) {
            int tmp = z0; z0 = z1; z1 = tmp;
         }
         for (int bx = x0; bx <= x1; bx++) {
            for (int bz = z0; bz <= z1; bz++) {
               blockAt(bx, ry, bz).build();
            }
         }
      }
   }

   private static void buildRooms() {
      for (int rx = 0; rx < 4; rx++) {
         for (int ry = 1; ry < 7; ry++) {
            for (int rz = 0; rz < 4; rz++) {
               if (prevRoomGrid[rx][ry][rz] != null && prevRoomGrid[rx][ry][rz].roomType != RandomBase.RoomType.PLACEHOLDER) {
                  int roomW = (int)(4.0 + 2.0 * Math.random());
                  int roomD = (int)(4.0 + 2.0 * Math.random());
                  if (prevRoomGrid[rx][ry][rz].roomType == RandomBase.RoomType.LOUNGE || prevRoomGrid[rx][ry][rz].roomType == RandomBase.RoomType.HUB) {
                     roomW = 6;
                     roomD = 6;
                  }

                  for (int dx = -roomW / 2; dx < roomW - roomW / 2; dx++) {
                     for (int dz = -roomD / 2; dz < roomD - roomD / 2; dz++) {
                        blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4).build();
                     }
                  }

                  if (prevRoomGrid[rx][ry][rz].roomType == RandomBase.RoomType.BASIC) {
                     for (int dx = -roomW / 2 + 1; dx < roomW - roomW / 2 - 1; dx++) {
                        for (int dz = -roomD / 2 + 1; dz < roomD - roomD / 2 - 1; dz++) {
                           blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4).clearElements();
                        }
                     }

                     blockAt((rx << 3) + 4, ry, (rz << 3) + -roomD / 2 + 4)
                        .addElement(BlockType.LADDER, Dir.SOUTH, getNeighbors(new Point((float)((rx << 3) + 4), (float)ry, (float)((rz << 3) + -roomD / 2 + 4))), false);
                  }

                  if (prevRoomGrid[rx][ry][rz].roomType == RandomBase.RoomType.GARDEN) {
                     for (int dx = -roomW / 2; dx < roomW - roomW / 2; dx++) {
                        for (int dz = -roomD / 2; dz < roomD - roomD / 2; dz++) {
                           if (dx != 0 && dz != 0 && Math.random() < 0.6F) {
                              blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4)
                                 .addElement(BlockType.PLANT_POT, Dir.SOUTH, getNeighbors(new Point((float)((rx << 3) + dx + 4), (float)ry, (float)((rz << 3) + dz + 4))), false);
                           }
                        }
                     }
                  }

                  if (prevRoomGrid[rx][ry][rz].roomType == RandomBase.RoomType.WORKSHOP) {
                     blockAt((rx << 3) + -roomW / 2 + 4, ry, (rz << 3) + roomD - roomD / 2 - 1 + 4)
                        .addElement(
                           BlockType.WORKSHOP,
                           Dir.NORTH,
                           getNeighbors(new Point((float)((rx << 3) + -roomW / 2 + 4), (float)ry, (float)((rz << 3) + roomD - roomD / 2 - 1 + 4))),
                           false
                        );
                     blockAt((rx << 3) + roomW - roomW / 2 - 1 + 4, ry, (rz << 3) + roomD - roomD / 2 - 1 + 4)
                        .addElement(
                           BlockType.COOKER,
                           Dir.SOUTH,
                           getNeighbors(new Point((float)((rx << 3) + roomW - roomW / 2 - 1 + 4), (float)ry, (float)((rz << 3) + roomD - roomD / 2 - 1 + 4))),
                           false
                        );
                     blockAt((rx << 3) + -roomW / 2 + 4, ry, (rz << 3) + -roomD / 2 + 4)
                        .addElement(BlockType.CHEST, Dir.NORTH, getNeighbors(new Point((float)((rx << 3) + -roomW / 2 + 4), (float)ry, (float)((rz << 3) + -roomD / 2 + 4))), false);
                     blockAt((rx << 3) + -roomW / 2 + 4, ry, (rz << 3) + -roomD / 2 + 4).getElement(BlockType.CHEST).inventory.addItem(new Item(ItemType.DRILL));
                     blockAt((rx << 3) + -roomW / 2 + 4, ry, (rz << 3) + -roomD / 2 + 4).getElement(BlockType.CHEST).inventory.addItem(new Item(ItemType.EXTRACTOR));
                     blockAt((rx << 3) + -roomW / 2 + 4, ry, (rz << 3) + -roomD / 2 + 4).getElement(BlockType.CHEST).inventory.addItem(new Item(ItemType.COOKED_FISH));
                     blockAt((rx << 3) + -roomW / 2 + 4, ry, (rz << 3) + -roomD / 2 + 4).getElement(BlockType.CHEST).inventory.addItem(new Item(ItemType.IRON, 15));
                  }

                  if (prevRoomGrid[rx][ry][rz].roomType == RandomBase.RoomType.HUB) {
                     for (int dx = -roomW / 2 + 1; dx < roomW - roomW / 2 - 1; dx++) {
                        for (int dz = -roomD / 2 + 1; dz < roomD - roomD / 2 - 1; dz++) {
                           blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4).clearElements();
                        }
                     }

                     if (ry % 2 == 0) {
                        blockAt((rx << 3) + -roomW / 2 + 1 + 4, ry, (rz << 3) + -roomD / 2 + 1 + 4).build();
                        blockAt((rx << 3) + roomW - roomW / 2 - 2 + 4, ry, (rz << 3) + -roomD / 2 + 4)
                           .addElement(
                              BlockType.LADDER,
                              Dir.SOUTH,
                              getNeighbors(new Point((float)((rx << 3) + roomW - roomW / 2 - 2 + 4), (float)ry, (float)((rz << 3) + -roomD / 2 + 4))),
                              false
                           );
                     } else {
                        blockAt((rx << 3) + roomW - roomW / 2 - 2 + 4, ry, (rz << 3) + -roomD / 2 + 1 + 4).build();
                        blockAt((rx << 3) + -roomW / 2 + 1 + 4, ry, (rz << 3) + -roomD / 2 + 4)
                           .addElement(
                              BlockType.LADDER,
                              Dir.SOUTH,
                              getNeighbors(new Point((float)((rx << 3) + -roomW / 2 + 1 + 4), (float)ry, (float)((rz << 3) + -roomD / 2 + 4))),
                              false
                           );
                     }
                  }

                  if (prevRoomGrid[rx][ry][rz].roomType == RandomBase.RoomType.STORAGE) {
                     for (int dx = -roomW / 2; dx < roomW - roomW / 2; dx++) {
                        for (int dz = -roomD / 2; dz < roomD - roomD / 2; dz++) {
                           if (dx != 0 && dz != 0 && Math.random() < 0.2F) {
                              blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4)
                                 .addElement(BlockType.CHEST, Dir.SOUTH, getNeighbors(new Point((float)((rx << 3) + dx + 4), (float)ry, (float)((rz << 3) + dz + 4))), false);
                              Element chestElement = blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4).getElement(BlockType.CHEST);
                              Inventory chestInventory = new Inventory("Chest", 4, 2);
                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.POTATO, (int)(1.0 + Math.random() * 2.0)));
                              }

                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.CARROT_SEED, (int)(1.0 + Math.random() * 2.0)));
                              }

                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.GREEN_BEAN, (int)(1.0 + Math.random() * 2.0)));
                              }

                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.IRON_SPEAR, (int)(5.0 + Math.random() * 10.0)));
                              }

                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.IRON_STUN_SPEAR, (int)(5.0 + Math.random() * 10.0)));
                              }

                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.COPPER_SPEAR, (int)(5.0 + Math.random() * 10.0)));
                              }

                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.COPPER_STUN_SPEAR, (int)(5.0 + Math.random() * 10.0)));
                              }

                              if (Math.random() < 0.5) {
                                 chestInventory.addItem(new Item(ItemType.GLASS_WALL, (int)(5.0 + Math.random() * 10.0)));
                              }

                              if (Math.random() < 0.6F) {
                                 chestInventory.addItem(new Item(ItemType.FLOOR, (int)(5.0 + Math.random() * 10.0)));
                              }

                              if (Math.random() < 0.2F) {
                                 chestInventory.addItem(new Item(ItemType.FISH, (int)(1.0 + Math.random() * 2.0)));
                              }

                              if (Math.random() < 0.2F) {
                                 chestInventory.addItem(new Item(ItemType.MANTA_RAY_MEAT, (int)(1.0 + Math.random() * 2.0)));
                              }

                              if (Math.random() < 0.2F) {
                                 chestInventory.addItem(new Item(ItemType.BARRACUDA_MEAT, (int)(1.0 + Math.random() * 2.0)));
                              }

                              if (Math.random() < 0.4F) {
                                 chestInventory.addItem(new Item(ItemType.IRON, (int)(10.0 + Math.random() * 20.0)));
                              }

                              if (Math.random() < 0.3F) {
                                 chestInventory.addItem(new Item(ItemType.ENERGY_SPHERE, (int)(5.0 + Math.random() * 10.0)));
                              }

                              if (Math.random() < 0.2F) {
                                 chestInventory.addItem(new Item(ItemType.COPPER, (int)(3.0 + Math.random() * 5.0)));
                              }

                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.GOLD, (int)(1.0 + Math.random() * 2.0)));
                              }

                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.EXTRACTOR));
                              }

                              if (Math.random() < 0.1F) {
                                 chestInventory.addItem(new Item(ItemType.MANGANESE, (int)(1.0 + Math.random() * 2.0)));
                              }

                              if (chestInventory.isEmpty()) {
                                 chestInventory.addItem(new Item(ItemType.POTATO, (int)(1.0 + Math.random() * 2.0)));
                              }

                              chestElement.inventory = chestInventory;
                           }
                        }
                     }
                  }

                  if (prevRoomGrid[rx][ry][rz].roomType == RandomBase.RoomType.LOUNGE) {
                     for (int dx = -roomW / 2; dx < roomW - roomW / 2; dx++) {
                        for (int dz = -roomD / 2; dz < roomD - roomD / 2; dz++) {
                           if (dx != 0 && dz != 0) {
                              if (Math.random() < 0.8F) {
                                 if (dx == -roomW / 2) {
                                    blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4)
                                       .addElement(
                                          BlockType.STOOL,
                                          Dir.WEST,
                                          getNeighbors(new Point((float)((rx << 3) + dx + 4), (float)ry, (float)((rz << 3) + dz + 4))),
                                          false
                                       );
                                 } else if (dz == -roomD / 2) {
                                    blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4)
                                       .addElement(
                                          BlockType.STOOL,
                                          Dir.NORTH,
                                          getNeighbors(new Point((float)((rx << 3) + dx + 4), (float)ry, (float)((rz << 3) + dz + 4))),
                                          false
                                       );
                                 } else if (dx == roomW - roomW / 2 - 1) {
                                    blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4)
                                       .addElement(
                                          BlockType.STOOL,
                                          Dir.EAST,
                                          getNeighbors(new Point((float)((rx << 3) + dx + 4), (float)ry, (float)((rz << 3) + dz + 4))),
                                          false
                                       );
                                 } else if (dz == roomD - roomD / 2 - 1) {
                                    blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4)
                                       .addElement(
                                          BlockType.STOOL,
                                          Dir.SOUTH,
                                          getNeighbors(new Point((float)((rx << 3) + dx + 4), (float)ry, (float)((rz << 3) + dz + 4))),
                                          false
                                       );
                                 }
                              }

                              if (Math.random() < 0.5
                                 && (dx == -roomW / 2 + 1 || dx == roomW - roomW / 2 - 2 || dz == -roomD / 2 + 1 || dz == roomD - roomD / 2 - 2)) {
                                 blockAt((rx << 3) + dx + 4, ry, (rz << 3) + dz + 4)
                                    .addElement(BlockType.TABLE, Dir.SOUTH, getNeighbors(new Point((float)((rx << 3) + dx + 4), (float)ry, (float)((rz << 3) + dz + 4))), false);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static Block blockAt(Point pos) {
      return blockAt((int)pos.x, (int)pos.y, (int)pos.z);
   }

   private static Block blockAt(int x, int y, int z) {
      return x > 0 && x < 32 && y > 0 && y < 8 && z > 0 && z < 32 ? blockGrid[x][y][z] : new Block(new Point());
   }

   private static Block[] getNeighbors(Point pos) {
      Block[] neighbors = new Block[10];
      neighbors[Neighbor.UP.getIndex()] = blockAt(pos.plus(Neighbor.UP.getOffset()));
      neighbors[Neighbor.DOWN.getIndex()] = blockAt(pos.plus(Neighbor.DOWN.getOffset()));
      neighbors[Neighbor.WEST.getIndex()] = blockAt(pos.plus(Neighbor.WEST.getOffset()));
      neighbors[Neighbor.EAST.getIndex()] = blockAt(pos.plus(Neighbor.EAST.getOffset()));
      neighbors[Neighbor.NORTH.getIndex()] = blockAt(pos.plus(Neighbor.NORTH.getOffset()));
      neighbors[Neighbor.SOUTH.getIndex()] = blockAt(pos.plus(Neighbor.SOUTH.getOffset()));
      neighbors[Neighbor.NORTHEAST.getIndex()] = blockAt(pos.plus(Neighbor.NORTHEAST.getOffset()));
      neighbors[Neighbor.NORTHWEST.getIndex()] = blockAt(pos.plus(Neighbor.NORTHWEST.getOffset()));
      neighbors[Neighbor.SOUTHEAST.getIndex()] = blockAt(pos.plus(Neighbor.SOUTHEAST.getOffset()));
      neighbors[Neighbor.SOUTHWEST.getIndex()] = blockAt(pos.plus(Neighbor.SOUTHWEST.getOffset()));
      return neighbors;
   }
}
