package game.inventory;

import game.Main;
import game.input.RawInput;
import game.inventory.types.Inventory;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class InventoryHud {
   private static Inventory openInventory;
   private static int sideOffset;
   private static Storage dragStorage;
   private static Storage dragSource;
   private static Storage hoveredStorage;
   private static Storage selectedStorage;
   private static Item hoveredItem = null;
   private static String tooltipTitle;
   private static String[] tooltipLines;
   private static InventoryButton removeButton = new InventoryButton(-70, 180);
   private static InventoryButton useButton = new InventoryButton(70, 180);
   private static InventoryButton removeSingleButton = new InventoryButton(0, 180);
   private static InventoryButton takeAllButton;
   private static Item lastHoveredItem = null;
   private static float tooltipAlpha = 0.0F;

   public static void update(float deltaTime) {
      hoveredItem = null;
      tooltipTitle = null;
      tooltipLines = null;
      hoveredStorage = null;
      boolean isFromOpenInventory = false;
      GameScene.avatar.getInventory().update(deltaTime);
      if (Main.hasStateChanged()) {
         if (selectedStorage != null) {
            selectedStorage.setSelected(false);
         }

         selectedStorage = null;
      }

      if (openInventory != null) {
         openInventory.update(deltaTime);
         sideOffset = Math.min(Display.getWidth() / 4, 250);
      } else {
         sideOffset = 0;
      }

      Storage storage = GameScene.avatar.getInventory().getStorageAt(new Coord(RawInput.mouseX, RawInput.mouseY).plus(new Coord(sideOffset, 0)));
      if (storage != null) {
         hoveredStorage = storage;
      }

      storage = openInventory != null ? openInventory.getStorageAt(new Coord(RawInput.mouseX, RawInput.mouseY).plus(new Coord(-sideOffset, 0))) : null;
      if (openInventory != null && storage != null) {
         hoveredStorage = storage;
         isFromOpenInventory = true;
      }

      if (hoveredStorage != null && hoveredStorage.getItem() != null) {
         if (RawInput.doubleClick && isFromOpenInventory) {
            Item remainder = GameScene.avatar.getInventory().addItem(new Item(hoveredStorage.getItem().getType(), hoveredStorage.getItem().getCount()));
            if (remainder.getCount() == 0) {
               hoveredStorage.setItem(null);
            } else {
               hoveredStorage.setItem(remainder);
            }
         } else if (RawInput.leftMouseDown) {
            dragStorage = new Storage(0, 0);
            dragStorage.setItem(hoveredStorage.getItem());
            hoveredStorage.setDragging(true);
            dragSource = hoveredStorage;
         }
      }

      if (dragStorage != null && RawInput.leftMouseReleased) {
         if (hoveredStorage != null && hoveredStorage != dragSource) {
            if (dragStorage.getItem() == null || hoveredStorage.getItem() == null || !dragStorage.getItem().getType().isStackable() || dragStorage.getItem().getType() != hoveredStorage.getItem().getType()) {
               boolean canDrop = false;
               if (hoveredStorage.getSlotType() == InventoryElmtType.SLOT_EMPTY) {
                  canDrop = true;
               } else if (dragStorage.getItem() != null) {
                  switch (hoveredStorage.getSlotType()) {
                     case SLOT_HELMET:
                        if (dragStorage.getItem().getType() == ItemType.IRON_DIVING_HELMET || dragStorage.getItem().getType() == ItemType.COPPER_DIVING_HELMET || dragStorage.getItem().getType() == ItemType.MANGANESE_DIVING_HELMET) {
                           canDrop = true;
                        }
                        break;
                     case SLOT_SUIT:
                        if (dragStorage.getItem().getType() == ItemType.IRON_DIVING_SUIT || dragStorage.getItem().getType() == ItemType.COPPER_DIVING_SUIT || dragStorage.getItem().getType() == ItemType.MANGANESE_DIVING_SUIT) {
                           canDrop = true;
                        }
                        break;
                     case SLOT_CYLINDER:
                        if (dragStorage.getItem().getType() == ItemType.IRON_DIVING_CYLINDER || dragStorage.getItem().getType() == ItemType.COPPER_DIVING_CYLINDER || dragStorage.getItem().getType() == ItemType.MANGANESE_DIVING_CYLINDER) {
                           canDrop = true;
                        }
                        break;
                     case SLOT_COOKER:
                        if (dragStorage.getItem().getType() == ItemType.COAL) {
                           canDrop = true;
                        }
					default:
						break;
                  }
               }

               if (canDrop) {
                  Item swapped = hoveredStorage.getItem();
                  hoveredStorage.setItem(dragStorage.getItem());
                  dragSource.setItem(swapped);
               } else {
                  dragSource.setItem(dragStorage.getItem());
               }
            } else {
               Item stackResult = hoveredStorage.addToStack(dragStorage.getItem().getCount());
               if (stackResult.getCount() == 0) {
                  dragSource.setItem(null);
               } else {
                  dragSource.setItem(stackResult);
               }
            }
         } else {
            dragSource.setItem(dragStorage.getItem());
         }

         dragStorage = null;
      }

      if (hoveredItem == null && hoveredStorage != null) {
         hoveredItem = hoveredStorage.getItem();
      }

      boolean buttonClicked = false;
      if (selectedStorage != null && selectedStorage.getItem() != null) {
         if (selectedStorage.getItem().getType().getAction() != Action.NONE) {
            if (removeButton.checkClick((float)(-sideOffset))) {
               buttonClicked = true;
               selectedStorage.setItem(null);
            }

            if (useButton.checkClick((float)(-sideOffset))) {
               buttonClicked = true;
               GameScene.avatar.useItem(selectedStorage);
            }
         } else if (removeSingleButton.checkClick((float)(-sideOffset))) {
            buttonClicked = true;
            selectedStorage.setItem(null);
         }
      }

      if (openInventory != null && takeAllButton != null && takeAllButton.checkClick((float)sideOffset)) {
         buttonClicked = true;

         for (int col = 0; col < openInventory.getStorageArray().getWidth(); col++) {
            for (int row = 0; row < openInventory.getStorageArray().getHeight(); row++) {
               if (openInventory.getStorageArray().get(col, row).getItem() != null) {
                  Item taken = GameScene.avatar.getInventory().addItem(openInventory.getStorageArray().get(col, row).getItem());
                  if (taken != null && taken.getCount() == 0) {
                     taken = null;
                  }

                  openInventory.getStorageArray().get(col, row).setItem(taken);
               }
            }
         }
      }

      if (!buttonClicked && RawInput.leftMouseReleased) {
         if (hoveredStorage != null) {
            if (selectedStorage != null) {
               selectedStorage.setSelected(false);
            }

            selectedStorage = hoveredStorage;
            selectedStorage.setSelected(true);
         } else {
            if (selectedStorage != null) {
               selectedStorage.setSelected(false);
            }

            selectedStorage = null;
         }
      }

      if (tooltipAlpha > 0.0F) {
         tooltipAlpha -= deltaTime;
      } else {
         tooltipAlpha = 0.0F;
      }
   }

   public static void render() {
      GL11.glTranslatef(-sideOffset, 0.0F, 0.0F);
      GameScene.avatar.renderInventory();
      if (selectedStorage != null && selectedStorage.getItem() != null) {
         if (selectedStorage.getItem().getType().getAction() != Action.NONE) {
            removeButton.render("Remove");
            useButton.render(selectedStorage.getItem().getType().getAction().getName());
         } else {
            removeSingleButton.render("Remove");
         }
      }

      GL11.glTranslatef(sideOffset, 0.0F, 0.0F);
      if (openInventory != null) {
         GL11.glTranslatef(sideOffset, 0.0F, 0.0F);
         openInventory.render();
         if (takeAllButton != null) {
            takeAllButton.render("Take All");
         }

         GL11.glTranslatef(-sideOffset, 0.0F, 0.0F);
      }

      if (dragStorage != null) {
         GL11.glTranslatef(RawInput.mouseX, RawInput.mouseY, 0.0F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.items);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         dragStorage.renderDragged();
         GL11.glTranslatef(-RawInput.mouseX, -RawInput.mouseY, 0.0F);
      }

      if (hoveredItem != null) {
         if (lastHoveredItem != hoveredItem) {
            lastHoveredItem = hoveredItem;
            tooltipAlpha = 2.5F;
         }

         renderTooltip(hoveredItem, new Coord(RawInput.mouseX, RawInput.mouseY - 27), tooltipAlpha);
      }

      if (tooltipTitle != null) {
         drawTooltip(tooltipTitle, tooltipLines, new Coord(RawInput.mouseX, RawInput.mouseY - 27), 1.0F);
      }
   }

   public static void renderTooltip(Item item, Coord pos, float alpha) {
      if (item != null) {
         drawTooltip(item.getType().getText(), item.getType().getDescription(), pos, alpha);
      }
   }

   private static void drawTooltip(String title, String[] lines, Coord pos, float alpha) {
      if (!title.isEmpty()) {
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         int lineCount = 0;
         if (lines != null) {
            lineCount = lines.length;
         }

         int tooltipHeight = 30 + FontRenderer.getCharHeight(0.4F) * lineCount;
         GL11.glPushMatrix();
         GL11.glTranslatef(pos.x, pos.y, 0.0F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         int maxWidth = FontRenderer.getTextWidth(title, 0.5F);
         if (lines != null) {
            for (int lineIdx = 0; lineIdx < lines.length; lineIdx++) {
               maxWidth = Math.max(maxWidth, FontRenderer.getTextWidth(lines[lineIdx], 0.5F));
            }
         }

         GL11.glBegin(GL11.GL_QUADS);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
         GL11.glVertex2f(-maxWidth * 0.6F, 0.0F);
         GL11.glVertex2f(-maxWidth * 0.6F, -tooltipHeight);
         GL11.glVertex2f(maxWidth * 0.6F, -tooltipHeight);
         GL11.glVertex2f(maxWidth * 0.6F, 0.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
         GL11.glVertex2f(-maxWidth * 0.6F, 0.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
         GL11.glVertex2f(-maxWidth * 0.6F, -tooltipHeight);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glVertex2f(-(maxWidth * 0.6F + 30.0F), -tooltipHeight);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glVertex2f(-(maxWidth * 0.6F + 30.0F), 0.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
         GL11.glVertex2f(maxWidth * 0.6F, 0.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
         GL11.glVertex2f(maxWidth * 0.6F, -tooltipHeight);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glVertex2f(maxWidth * 0.6F + 30.0F, -tooltipHeight);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glVertex2f(maxWidth * 0.6F + 30.0F, 0.0F);
         GL11.glEnd();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
         FontRenderer.drawCentered(0, -FontRenderer.getCharHeight(0.5F) - lineCount * FontRenderer.getCharHeight(0.4F), title, 0.5F);
         if (lines != null) {
            FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
            GL11.glColor4f(1.0F, 1.0F, 0.0F, alpha);

            for (int lineIdx2 = 0; lineIdx2 < lines.length; lineIdx2++) {
               FontRenderer.drawCentered(0, -FontRenderer.getCharHeight(0.5F) - lineCount * FontRenderer.getCharHeight(0.3F) + (lineIdx2 + 1) * FontRenderer.getCharHeight(0.3F), lines[lineIdx2], 0.45F);
            }
         }

         GL11.glPopMatrix();
      }
   }

   public static void setHoveredItem(Item item) {
      hoveredItem = item;
   }

   public static void setTooltip(String title, String[] lines) {
      tooltipTitle = title;
      tooltipLines = lines;
   }

   public static void setInventory(Inventory inventory) {
      openInventory = inventory;
      if (inventory == null) {
         sideOffset = 0;
      }

      takeAllButton = null;
   }

   public static void setTakeAllButtonYPos(int yPos) {
      if (takeAllButton == null) {
         takeAllButton = new InventoryButton(0, 180);
      }

      takeAllButton.setYPos(yPos);
   }

   public static void deselectStorage() {
      if (selectedStorage != null) {
         selectedStorage.setSelected(false);
      }

      selectedStorage = null;
   }
}
