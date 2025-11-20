package game.inventory;

import game.manager.TextureManager;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;
import java.io.Serializable;
import org.lwjgl.opengl.GL11;

public class Storage implements Serializable {
   private static final long serialVersionUID = -6789630567896871707L;
   private Item item = null;
   private int x;
   private int y;
   private InventoryElmtType specialElmtType = InventoryElmtType.SLOT_EMPTY;
   private transient boolean hovered = false;
   private transient boolean selected = false;
   private transient boolean dragging = false;

   public Storage(int gridX, int gridY) {
      this.x = gridX;
      this.y = gridY;
      this.hovered = false;
   }

   public final boolean contains(Coord coord) {
      coord.toChunkSpace(54.0F);
      boolean hit = (int)Math.floor(coord.x) == this.x && (int)Math.floor(coord.y) == this.y;
      this.hovered = hit;
      return hit;
   }

   public final void render(boolean showCount) {
      GL11.glTranslatef(this.x * 54, this.y * 54, 0.0F);
      if (this.item != null && !this.dragging) {
         renderSlot(this.item.getType().getInventoryType());
         if (this.item.isNew()) {
            renderSlot(InventoryElmtType.SLOT_OVERLAY);
         }

         renderItemIcon(this.item.getType());
      } else {
         renderSlot(this.specialElmtType);
      }

      if (showCount && this.item != null && !this.dragging && this.item.getType().isStackable()) {
         renderSlot(InventoryElmtType.SLOT_STACK_COUNT);
      }

      if (this.hovered) {
         renderSlot(InventoryElmtType.SLOT_HIGHLIGHT);
      }

      if (this.selected) {
         renderSlot(InventoryElmtType.SLOT_HIGHLIGHT);
      }

      renderSlot(InventoryElmtType.SLOT_FRAME);
      GL11.glTranslatef(-this.x * 54, -this.y * 54, 0.0F);
   }

   public final void renderCount() {
      this.renderCount(1.0F);
   }

   public final void renderCount(float alpha) {
      FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glTranslatef(this.x * 54, this.y * 54, 0.0F);
      if (this.item != null && this.item.getType().isStackable()) {
         FontRenderer.drawCentered(0, 8, "" + this.item.getCount(), 0.3F);
      }

      GL11.glTranslatef(-this.x * 54, -this.y * 54, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public final void renderDragged() {
      if (this.item != null) {
         renderItemIcon(this.item.getType());
      } else {
         renderSlot(InventoryElmtType.SLOT_EMPTY);
      }

      if (this.item != null && this.item.getType().isStackable()) {
         renderSlot(InventoryElmtType.SLOT_STACK_COUNT);
      }

      GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
      FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
      if (this.item != null && this.item.getType().isStackable()) {
         FontRenderer.drawCentered(0, 8, "" + this.item.getCount(), 0.3F);
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void renderSlot(InventoryElmtType slotType) {
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(slotType.getXOffset() / 10.0F, slotType.getYOffset() / 20.0F);
      GL11.glVertex2f(-27.0F, -27.0F);
      GL11.glTexCoord2f(slotType.getXOffset() / 10.0F, (slotType.getYOffset() + 1) / 20.0F);
      GL11.glVertex2f(-27.0F, 27.0F);
      GL11.glTexCoord2f((slotType.getXOffset() + 1) / 10.0F, (slotType.getYOffset() + 1) / 20.0F);
      GL11.glVertex2f(27.0F, 27.0F);
      GL11.glTexCoord2f((slotType.getXOffset() + 1) / 10.0F, slotType.getYOffset() / 20.0F);
      GL11.glVertex2f(27.0F, -27.0F);
      GL11.glEnd();
   }

   private static void renderItemIcon(ItemType type) {
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(type.getXOffset() / 10.0F, type.getYOffset() / 20.0F);
      GL11.glVertex2f(-27.0F, -27.0F);
      GL11.glTexCoord2f(type.getXOffset() / 10.0F, (type.getYOffset() + 1) / 20.0F);
      GL11.glVertex2f(-27.0F, 27.0F);
      GL11.glTexCoord2f((type.getXOffset() + 1) / 10.0F, (type.getYOffset() + 1) / 20.0F);
      GL11.glVertex2f(27.0F, 27.0F);
      GL11.glTexCoord2f((type.getXOffset() + 1) / 10.0F, type.getYOffset() / 20.0F);
      GL11.glVertex2f(27.0F, -27.0F);
      GL11.glEnd();
   }

   public static void renderItem(ItemType type, float scale) {
      renderItem(type, scale, true);
   }

   public static void renderItem(ItemType type, float scale, boolean showFrame) {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.items);
      if (scale != 1.0F) {
         GL11.glScalef(scale, scale, scale);
      }

      if (type != null) {
         renderSlot(type.getInventoryType());
      } else {
         renderSlot(InventoryElmtType.SLOT_EMPTY);
      }

      if (type != null) {
         renderItemIcon(type);
      }

      if (showFrame) {
         renderSlot(InventoryElmtType.SLOT_FRAME);
      }

      if (scale != 1.0F) {
         GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
      }
   }

   public final void setItem(Item item) {
      this.item = item;
      this.dragging = false;
   }

   public final Item getItem() {
      return this.item;
   }

   public final Item addToStack(int count) {
      count = this.item.addCount(count);
      return new Item(this.item.getType(), count);
   }

   public final void consume() {
      this.item.consume();
      if (this.item.getCount() <= 0) {
         this.item = null;
      }
   }

   public final void setHovered(boolean hovered) {
      this.hovered = hovered;
   }

   public final boolean isHovered() {
      return this.hovered;
   }

   public final void setSelected(boolean selected) {
      this.selected = selected;
   }

   public final void setDragging(boolean dragging) {
      this.dragging = dragging;
   }

   public final void setSlotType(InventoryElmtType type) {
      this.specialElmtType = type;
   }

   public final InventoryElmtType getSlotType() {
      return this.specialElmtType;
   }
}
