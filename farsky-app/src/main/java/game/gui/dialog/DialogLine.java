package game.gui.dialog;

import game.manager.TextureManager;
import game.util.FontFamily;
import game.util.FontRenderer;
import org.lwjgl.opengl.GL11;

public final class DialogLine {
   private String text;
   private float displayTime;
   private float slideOffset;
   private boolean visible = false;
   private Character character;

   public DialogLine(String text, Character character, float duration) {
      this.text = text;
      this.character = character;
      this.displayTime = duration;
      this.slideOffset = 50.0F;
   }

   public final void update(float delta) {
      if (this.visible) {
         this.displayTime -= delta;
      }

      if (!this.visible) {
         if (this.slideOffset > 0.0F) {
            this.slideOffset -= delta * 50.0F;
         } else {
            this.slideOffset = 0.0F;
            this.visible = true;
         }
      } else {
         if (this.displayTime < 0.0F) {
            if (this.slideOffset > -50.0F) {
               this.slideOffset -= delta * 50.0F;
               return;
            }

            this.slideOffset = 50.0F;
         }
      }
   }

   public final void render() {
      float alpha = 1.0F - Math.abs(this.slideOffset / 50.0F);
      float textHeight = FontRenderer.getTextHeight(this.text, 0.4F);
      GL11.glTranslatef(0.0F, this.slideOffset, 0.0F);
      GL11.glLineWidth(1.0F);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-238.0F, -textHeight / 2.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-238.0F, textHeight / 2.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(168.0F, textHeight / 2.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(168.0F, -textHeight / 2.0F, 0.0F);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glBegin(GL11.GL_LINES);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-238.0F, -textHeight / 2.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(168.0F, -textHeight / 2.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-238.0F, textHeight / 2.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(168.0F, textHeight / 2.0F, 0.0F);
      GL11.glEnd();
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-238.0F, -textHeight / 2.0F, 0.0F);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-238.0F, textHeight / 2.0F, 0.0F);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(-238.0F, textHeight / 2.0F, 0.0F);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(-238.0F, -textHeight / 2.0F, 0.0F);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(238.0F, -textHeight / 2.0F, 0.0F);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(238.0F, textHeight / 2.0F, 0.0F);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(168.0F, textHeight / 2.0F, 0.0F);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(168.0F, -textHeight / 2.0F, 0.0F);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glBegin(GL11.GL_LINES);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-238.0F, -textHeight / 2.0F, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-237.0F, -textHeight / 2.0F, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-238.0F, textHeight / 2.0F, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-237.0F, textHeight / 2.0F, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(238.0F, -textHeight / 2.0F, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(167.0F, -textHeight / 2.0F, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(238.0F, textHeight / 2.0F, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(167.0F, textHeight / 2.0F, 0.0F);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
      FontRenderer.draw(-200, -((int)textHeight) / 2, this.text, 0.4F);
      GL11.glTranslatef(0.0F, -this.slideOffset, 0.0F);
   }

   public final boolean isExpired() {
      return this.displayTime <= 0.0F;
   }

   public final boolean isDone() {
      return this.displayTime <= 0.0F && this.slideOffset == 50.0F;
   }

   public final Character getCharacter() {
      return this.character;
   }
}
