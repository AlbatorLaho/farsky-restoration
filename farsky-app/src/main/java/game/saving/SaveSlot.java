package game.saving;

import game.environment.DepthAtmosphere;
import game.manager.GameMode;
import game.manager.TextureManager;
import game.manager.GameTime;
import game.util.FontFamily;
import game.util.Point;
import game.util.FontRenderer;
import org.lwjgl.opengl.GL11;

public final class SaveSlot {
   private String filePath = "";
   private SlotPresentation presentation;

   public SaveSlot(String filePath) {
      this.filePath = filePath;
      this.presentation = SaveManager.readSlotPresentation(filePath);
   }

   public final void render(float alpha) {
      int texture = TextureManager.saveSlot0;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      if (this.presentation != null) {
         if (this.presentation.getDepth() < DepthAtmosphere.toMeters(-1500.0F)) {
            texture = TextureManager.saveSlot1;
         }

         if (this.presentation.getDepth() < DepthAtmosphere.toMeters(-2500.0F)) {
            texture = TextureManager.saveSlot2;
         }
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-200.5F, -112.5F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-200.5F, 112.5F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(200.5F, 112.5F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(200.5F, -112.5F);
      GL11.glEnd();
      if (this.presentation != null) {
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         FontRenderer.drawGradient(-160, -82, "" + this.presentation.getGameMode(), 0.9F, new Point(0.5F, 0.5F, 0.5F), alpha, new Point(1.0F, 1.0F, 1.0F), alpha);
         GL11.glColor4f(0.8F, 0.8F, 0.8F, alpha);
         FontRenderer.draw(-160, -12, "Deepness: " + this.presentation.getDepth() + "m", 0.5F);
         int hours = this.presentation.getMinutesPlayed() / 60;
         int minutes = this.presentation.getMinutesPlayed() - hours * 60;
         String timeStr = "";
         if (hours > 0) {
            timeStr = timeStr + hours + "h ";
         }

         timeStr = timeStr + minutes + "min";
         FontRenderer.draw(-160, 18, "Time Played: " + timeStr, 0.5F);
         if (this.presentation.getGameMode() != GameMode.SANDBOX) {
            FontRenderer.draw(-160, 48, "Pieces found: " + this.presentation.getPieceCount(), 0.5F);
         }
      }
   }

   public final void load() {
      SaveManager.loadGame(this.filePath);
      GameTime.setTime(this.presentation.getMinutesPlayed() * 60.0F, this.presentation.getInGameMinutes());
   }

   public final boolean isEmpty() {
      return this.presentation == null;
   }

   public final void delete() {
      SaveManager.deleteSave(this.filePath);
   }
}
