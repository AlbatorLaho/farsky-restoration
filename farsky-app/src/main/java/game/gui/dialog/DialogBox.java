package game.gui.dialog;

import game.manager.TextureManager;
import game.sounds.SoundManager;
import game.manager.GameTime;
import game.util.FontRenderer;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class DialogBox {
   private ArrayList<DialogLine> activeLines = new ArrayList<>();
   private ArrayList<DialogLine> queuedLines = new ArrayList<>();
   private float visibility = 0.0F;
   private int soundSourceId;
   private String id = "";

   public DialogBox() {
      this("");
   }

   public DialogBox(String id) {
      this.id = id;
      this.visibility = 0.0F;
      this.soundSourceId = SoundManager.addLoopingSource(SoundManager.sfxRadio, null);
      SoundManager.setLoopingSourceVolume(this.soundSourceId, 0.0F);
   }

   public final void update(float delta) {
      if (this.visibility == 1.0F) {
         for (int i = this.activeLines.size() - 1; i >= 0; i--) {
            this.activeLines.get(i).update(delta);
            if (this.activeLines.get(i).isDone()) {
               this.activeLines.remove(i);
            }
         }
      }

      if ((this.activeLines.size() == 0 || this.activeLines.size() == 1 && this.activeLines.get(0).isExpired()) && this.queuedLines.size() > 0) {
         this.activeLines.add(this.queuedLines.remove(0));
      }

      if (this.activeLines.size() <= 0 && this.queuedLines.size() <= 0) {
         if (this.visibility > 0.0F) {
            this.visibility -= delta;
         } else {
            SoundManager.removeLoopingSource(this.soundSourceId);
            this.visibility = 0.0F;
         }
      } else if (this.visibility < 1.0F) {
         if (!SoundManager.isLoopingSourcePlaying(this.soundSourceId)) {
            SoundManager.playLoopingSource(this.soundSourceId);
         }

         this.visibility += delta;
      } else {
         this.visibility = 1.0F;
      }

      if (this.visibility != 0.0F) {
         SoundManager.setLoopingSourceVolume(this.soundSourceId, this.visibility * 0.3F);
      }
   }

   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2 - 150, 0.0F);

      for (int i = 0; i < this.activeLines.size(); i++) {
         this.activeLines.get(i).render();
      }

      Character character = Character.NONE;
      if (this.activeLines.size() > 0) {
         character = this.activeLines.get(this.activeLines.size() - 1).getCharacter();
      }

      GL11.glTranslatef(-280.0F, 0.0F, 0.0F);
      float alpha = this.visibility;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      switch (character) {
         case MADISON:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.jodiePortrait);
            break;
         case NATHAN:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.nathanPortrait);
            break;
         default:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.nobodyPortrait);
      }

      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-32.0F, -61.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-32.0F, 31.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(32.0F, 31.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(32.0F, -61.0F);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha * 0.3F);
      float tvScrollOffset = (float)Math.cos(GameTime.elapsedMillis / 10.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.tvEffect);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, tvScrollOffset);
      GL11.glVertex2f(-32.0F, -61.0F);
      GL11.glTexCoord2f(0.0F, tvScrollOffset + 1.0F);
      GL11.glVertex2f(-32.0F, 31.0F);
      GL11.glTexCoord2f(1.0F, tvScrollOffset + 1.0F);
      GL11.glVertex2f(32.0F, 31.0F);
      GL11.glTexCoord2f(1.0F, tvScrollOffset);
      GL11.glVertex2f(32.0F, -61.0F);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.radio);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-45.5F, -73.5F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-45.5F, 73.5F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(45.5F, 73.5F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(45.5F, -73.5F);
      GL11.glEnd();
      GL11.glPopMatrix();
   }

   public final void addLine(String text, Character character) {
      text = FontRenderer.wrapText(text, 0.4F, 360.0F);
      this.queuedLines.add(new DialogLine(text, character, 0.9F + text.length() * 0.045F));
   }

   public final boolean isDone() {
      return this.activeLines.size() == 0 && this.queuedLines.size() == 0 && this.visibility == 0.0F;
   }

   public final String getId() {
      return this.id;
   }
}
