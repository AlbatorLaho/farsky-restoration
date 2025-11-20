package game.inventory;

import game.input.RawInput;
import game.manager.TextureManager;
import game.util.FontFamily;
import game.util.FontRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class InventoryButton {
   private int xOffset;
   private int yOffset;
   private boolean hovered = false;

   public InventoryButton(int xOffset, int yOffset) {
      this.xOffset = xOffset;
      this.yOffset = yOffset;
   }

   public final boolean checkClick(float xShift) {
      int screenX = Display.getWidth() / 2 + this.xOffset;
      int screenY = Display.getHeight() / 2 + this.yOffset;
      if (RawInput.mouseX >= screenX - 46 + xShift && RawInput.mouseX <= screenX + 46 + xShift && RawInput.mouseY >= screenY - 17 && RawInput.mouseY <= screenY + 17) {
         this.hovered = true;
      } else {
         this.hovered = false;
      }

      return this.hovered && RawInput.leftMouseReleased;
   }

   public final void render(String label) {
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2 + this.xOffset, Display.getHeight() / 2 + this.yOffset, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      if (this.hovered) {
         GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
      } else {
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
      }

      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-46.0F, -17.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-46.0F, 17.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(46.0F, 17.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(46.0F, -17.0F);
      GL11.glEnd();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.storageAction);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-46.0F, -17.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-46.0F, 17.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(46.0F, 17.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(46.0F, -17.0F);
      GL11.glEnd();
      if (!this.hovered) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
      }

      FontRenderer.drawCentered(0, -FontRenderer.getCharHeight(0.5F) / 2, label, 0.5F);
      GL11.glPopMatrix();
   }

   public final void setYPos(int yPos) {
      this.yOffset = yPos;
   }
}
