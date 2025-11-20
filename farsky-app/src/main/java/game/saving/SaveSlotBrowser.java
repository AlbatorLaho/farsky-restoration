package game.saving;

import game.Main;
import game.input.RawInput;
import game.manager.TextureManager;
import game.manager.GameTime;
import game.util.FontFamily;
import game.util.Point;
import game.util.FontRenderer;
import java.io.File;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class SaveSlotBrowser {
   private float scrollOffset = 0.0F;
   private ArrayList<SaveSlot> slots;
   private int selectedIndex = 0;

   public SaveSlotBrowser() {
      this.loadSlots();
   }

   private void loadSlots() {
      this.slots = new ArrayList<>();
      File saveDir = new File(Main.dataPath + "save");
      if (saveDir.isDirectory()) {
         for (File file : saveDir.listFiles()) {
            this.slots.add(0, new SaveSlot(file.getAbsolutePath()));
         }
      }

      for (int i = this.slots.size() - 1; i >= 0; i--) {
         if (this.slots.get(i).isEmpty()) {
            this.slots.remove(i);
         }
      }
   }

   public final void update(float delta) {
      float targetScroll = -this.selectedIndex * 380.0F;
      if (this.scrollOffset > targetScroll) {
         this.scrollOffset = this.scrollOffset - Math.min(delta * 1500.0F, this.scrollOffset - targetScroll);
      }

      if (this.scrollOffset < targetScroll) {
         this.scrollOffset = this.scrollOffset + Math.min(delta * 1500.0F, targetScroll - this.scrollOffset);
      }

      if (RawInput.mouseX > Display.getWidth() / 2 - 250.0F - 40.0F
         && RawInput.mouseX < Display.getWidth() / 2 - 250.0F + 40.0F
         && RawInput.mouseY > Display.getHeight() / 2 - 40
         && RawInput.mouseY < Display.getHeight() / 2 + 40
         && RawInput.leftMouseDown
         && this.selectedIndex > 0) {
         this.selectedIndex--;
      }

      if (RawInput.mouseX > Display.getWidth() / 2 + 250.0F - 40.0F
         && RawInput.mouseX < Display.getWidth() / 2 + 250.0F + 40.0F
         && RawInput.mouseY > Display.getHeight() / 2 - 40
         && RawInput.mouseY < Display.getHeight() / 2 + 40
         && RawInput.leftMouseDown
         && this.selectedIndex < this.slots.size() - 1) {
         this.selectedIndex++;
      }
   }

   public final void render(float alpha) {
      if (this.slots.size() > 0) {
         GL11.glPushMatrix();
         GL11.glTranslatef(Display.getWidth() / 2 + this.scrollOffset, Display.getHeight() / 2, 0.0F);

         for (int i = 0; i < this.slots.size(); i++) {
            if (i == this.selectedIndex) {
               this.slots.get(i).render(1.0F);
            } else {
               this.slots.get(i).render(0.5F);
            }

            GL11.glTranslatef(380.0F, 0.0F, 0.0F);
         }

         GL11.glPopMatrix();
      } else {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         FontRenderer.drawCenteredGradient(Display.getWidth() / 2, Display.getHeight() / 2, "No saved game", 0.8F, new Point(0.5F, 0.5F, 0.5F), 1.0F, new Point(1.0F, 1.0F, 1.0F), 1.0F);
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.saveArrow);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha * 0.75F);
      if (this.selectedIndex > 0) {
         GL11.glPushMatrix();
         GL11.glTranslatef(Display.getWidth() / 2 - (250.0F + (float)Math.cos(GameTime.elapsedMillis / 300.0F) * 10.0F), Display.getHeight() / 2, 0.0F);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(-30.0F, -38.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(-30.0F, 38.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(30.0F, 38.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(30.0F, -38.0F);
         GL11.glEnd();
         GL11.glPopMatrix();
      }

      if (this.selectedIndex < this.slots.size() - 1) {
         GL11.glPushMatrix();
         GL11.glTranslatef(Display.getWidth() / 2 + (250.0F + (float)Math.cos(GameTime.elapsedMillis / 300.0F) * 10.0F), Display.getHeight() / 2, 0.0F);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(-30.0F, -38.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(-30.0F, 38.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(30.0F, 38.0F);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(30.0F, -38.0F);
         GL11.glEnd();
         GL11.glPopMatrix();
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public final boolean loadSelectedSlot() {
      if (this.selectedIndex >= 0 && this.selectedIndex < this.slots.size()) {
         this.slots.get(this.selectedIndex).load();
         return true;
      } else {
         return false;
      }
   }

   public final void deleteSelectedSlot() {
      if (this.selectedIndex >= 0 && this.selectedIndex < this.slots.size()) {
         this.slots.get(this.selectedIndex).delete();
      }
   }

   public final int getSlotCount() {
      return this.slots.size();
   }
}
