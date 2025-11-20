package game.inventory.types;

import game.environment.ResourcesPercent;
import game.inventory.Storage;
import game.inventory.InventoryHud;
import game.manager.TextureManager;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class ExtractorInventory extends Inventory {
   private static final long serialVersionUID = 5043309444494421960L;
   private ResourcesPercent resourcesPercents;

   public ExtractorInventory(String name, int cols, int rows, ResourcesPercent resourcesPercent) {
      super(name, cols, 2);
      this.resourcesPercents = resourcesPercent;
   }

   @Override
   public final void update(float dt) {
      InventoryHud.setTakeAllButtonYPos(260);
   }

   @Override
   public final void render() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F, Display.getHeight() / 2.0F - 190.0F - 20.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.inventoryDescription0);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(123.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(-123.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(-123.0F, 497.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(123.0F, 497.0F);
      GL11.glEnd();
      FontRenderer.drawCentered(0, 20, this.name, 0.9F);
      GL11.glTranslatef(0.0F, 190.0F, 0.0F);
      this.storageArray.render();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F, Display.getHeight() / 2.0F + 30.0F, 0.0F);
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      FontRenderer.drawCentered(0, -FontRenderer.getCharHeight(0.5F) / 2 + 10, "Resources", 0.5F);
      GL11.glTranslatef(0.0F, 30.0F, 0.0F);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(81.0F, 172.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(-81.0F, 172.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(-81.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(81.0F, 0.0F);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
      GL11.glTranslatef(-54.8F, 26.2F, 0.0F);

      for (int i = 0; i < this.resourcesPercents.size(); i++) {
         Storage.renderItem(this.resourcesPercents.getItemType(i), 0.6F);
         FontRenderer.draw(25, -FontRenderer.getCharHeight(0.4F) / 2, this.resourcesPercents.getPercent(i) + "% (" + this.resourcesPercents.getItemType(i).getText() + ")", 0.4F);
         GL11.glTranslatef(0.0F, 40.5F, 0.0F);
      }

      GL11.glPopMatrix();
   }

   public final void setResourcesPercents(ResourcesPercent resourcesPercents) {
      this.resourcesPercents = resourcesPercents;
   }

   @Override
   public final Storage getStorageAt(Coord coord) {
      return this.storageArray.getStorageAt(coord.plus(new Coord(-Display.getWidth() / 2, -Display.getHeight() / 2 + 20)));
   }
}
