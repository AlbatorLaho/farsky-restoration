package game.inventory;

import game.manager.GameScene;
import game.manager.TextureManager;
import game.shader.Shaders;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;
import java.io.Serializable;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public class StorageArray implements Serializable {
   private static final long serialVersionUID = 9069985943010927653L;
   private Storage[][] storage = null;
   private int width;
   private int height;
   private int xDraw;
   private int yDraw;

   public StorageArray(int width, int height) {
      this(new Item[width][height], width, height);
   }

   public StorageArray(Item[][] items, int width, int height) {
      this.width = width;
      this.height = height;
      this.storage = new Storage[width][height];

      for (int col = 0; col < width; col++) {
         for (int row = 0; row < height; row++) {
            this.storage[col][row] = new Storage(col, row);
            this.storage[col][row].setItem(items[col][row]);
         }
      }

      this.xDraw = -((int)(54.0 * (Math.floor(width / 2) - (1 - width % 2) / 2.0F)));
      this.yDraw = -((int)(54.0 * Math.floor(height / 2)));
   }

   public final void render() {
      boolean showCounts = true;
      this.render(showCounts, false);
   }

   public final void render(boolean showCounts, boolean grayIfUncraftable) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      GL11.glTranslatef(this.xDraw, this.yDraw, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.items);

      for (int col = 0; col < this.width; col++) {
         for (int row = 0; row < this.height; row++) {
            if (grayIfUncraftable && this.get(col, row).getItem() != null) {
               StorageArray resources = this.get(col, row).getItem().getType().getResources();
               if (!GameScene.avatar.getInventory().hasResources(resources, true)) {
                  Shaders.setUniform("blackAndWhite", true);
               }
            }

            this.storage[col][row].render(showCounts);
            if (grayIfUncraftable) {
               Shaders.setUniform("blackAndWhite", false);
            }
         }
      }

      if (showCounts) {
         for (int col2 = 0; col2 < this.width; col2++) {
            for (int row2 = 0; row2 < this.height; row2++) {
               this.storage[col2][row2].renderCount();
            }
         }
      }

      GL11.glTranslatef(-this.xDraw, -this.yDraw, 0.0F);
   }

   public final Storage getStorageAt(Coord pos) {
      Storage found = null;
      pos = pos.plus(new Coord(-this.xDraw + 27, -this.yDraw + 27));

      for (int col = 0; col < this.width; col++) {
         for (int row = 0; row < this.height; row++) {
            if (this.storage[col][row].contains(pos.copy())) {
               found = this.storage[col][row];
            }
         }
      }

      return found;
   }

   public final Storage get(int col, int row) {
      return this.storage[col][row];
   }

   public final void set(int col, int row, Storage storage) {
      this.storage[col][row] = storage;
   }

   public final Item findItem(ItemType type) {
      for (int col = 0; col < this.width; col++) {
         for (int row = 0; row < this.height; row++) {
            if (this.storage[col][row].getItem() != null && this.storage[col][row].getItem().getType() == type) {
               return this.storage[col][row].getItem();
            }
         }
      }

      return null;
   }

   public final ArrayList<Item> findAllItems(ItemType type) {
      ArrayList<Item> result = new ArrayList<>();

      for (int col = 0; col < this.width; col++) {
         for (int row = 0; row < this.height; row++) {
            if (this.storage[col][row].getItem() != null && this.storage[col][row].getItem().getType() == type) {
               result.add(this.storage[col][row].getItem());
            }
         }
      }

      return result;
   }

   public final Item addItem(Item item) {
      if (item == null) {
         return item;
      } else {
         for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
               if (this.storage[col][row].getItem() != null
                  && this.storage[col][row].getItem().getType().isStackable()
                  && this.storage[col][row].getItem().getType() == item.getType()) {
                  item = this.storage[col][row].addToStack(item.getCount());
                  if (item.getCount() == 0) {
                     return item;
                  }
               }
            }
         }

         for (int row = 0; row < this.height; row++) {
            for (int col = 0; col < this.width; col++) {
               if (item.getCount() > 0 && this.storage[col][row].getItem() == null) {
                  this.storage[col][row].setItem(new Item(item.getType(), item.getCount()));
                  item.setCount(0);
                  return item;
               }
            }
         }

         return item;
      }
   }

   public final boolean isEmpty() {
      for (int col = 0; col < this.width; col++) {
         for (int row = 0; row < this.height; row++) {
            if (this.storage[col][row].getItem() != null) {
               return false;
            }
         }
      }

      return true;
   }

   public final void clear() {
      for (int col = 0; col < this.width; col++) {
         for (int row = 0; row < this.height; row++) {
            this.storage[col][row].setItem(null);
         }
      }
   }

   public final void consumeOne(ItemType type) {
      for (int col = 0; col < this.width; col++) {
         for (int row = 0; row < this.height; row++) {
             if (this.storage[col][row].getItem() != null && this.storage[col][row].getItem().getType() == type) {
                 this.storage[col][row].getItem().addCount(-1);
                 if (this.storage[col][row].getItem().getCount() <= 0) {
                     this.storage[col][row].setItem(null);
               }

               return;
            }
         }
      }
   }

   public final boolean hasResources(StorageArray required, boolean checkOnly) {
      boolean canCraft = true;

      for (int col = 0; col < required.width; col++) {
         for (int row = 0; row < required.height; row++) {
            Item needed = required.get(col, row).getItem();
            boolean hasEnough;
            if (needed == null) {
               hasEnough = true;
            } else {
               int totalCount = 0;

               for (int col2 = 0; col2 < this.width; col2++) {
                  for (int row2 = 0; row2 < this.height; row2++) {
                      if (this.storage[col2][row2].getItem() != null && this.storage[col2][row2].getItem().getType() == needed.getType()) {
                          totalCount += this.storage[col2][row2].getItem().getCount();
                     }
                  }
               }

               hasEnough = totalCount >= needed.getCount();
            }

            if (!hasEnough) {
               canCraft = false;
            }

            if (!canCraft) {
               return false;
            }
         }
      }

      if (!checkOnly) {
         for (int col3 = 0; col3 < required.width; col3++) {
            for (int row3 = 0; row3 < required.height; row3++) {
               Item needed2 = required.get(col3, row3).getItem();
                  if (needed2 != null) {
                  int needed2Count = needed2.getCount();

                  for (int col4 = 0; col4 < this.width; col4++) {
                     for (int row4 = 0; row4 < this.height; row4++) {
                         if (this.storage[col4][row4].getItem() != null && this.storage[col4][row4].getItem().getType() == needed2.getType()) {
                             int slotCount = this.storage[col4][row4].getItem().getCount();
                             this.storage[col4][row4].getItem().addCount(-Math.min(needed2Count, slotCount));
                             if (this.storage[col4][row4].getItem().getCount() == 0) {
                                 this.storage[col4][row4].setItem(null);
                           }

                           needed2Count -= Math.min(needed2Count, slotCount);
                        }
                     }
                  }
               }
            }
         }
      }

      return canCraft;
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   public final void setHovered(int col, int row, boolean hovered) {
      this.storage[col][row].setHovered(hovered);
   }

   public final Storage getHoveredStorage() {
      for (int col = 0; col < this.width; col++) {
         for (int row = 0; row < this.height; row++) {
            if (this.storage[col][row].isHovered()) {
               return this.storage[col][row];
            }
         }
      }

      return null;
   }

   public final void setSlotType(int col, int row, InventoryElmtType type) {
      this.storage[col][row].setSlotType(type);
   }
}
