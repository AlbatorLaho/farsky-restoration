package game.gui.util;

import game.Main;
import game.manager.GameState;
import game.manager.TextureManager;
import game.shader.BlurEffect;
import game.manager.GameTime;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class MenuBackground {
   public static int captureTexture = -1;

   public static void update() {
      switch (Main.getGameState()) {
         case MAIN_MENU:
            return;
         case PAUSED:
            if (Main.hasStateChanged()) {
               captureTexture = TextureManager.captureFramebuffer(captureTexture);
            }
		default:
			break;
      }
   }

   public static void applyBlur() {
      if (Main.getGameState() == GameState.PAUSED) {
         BlurEffect.apply(captureTexture, 2.0F / Display.getWidth());
      }
   }

   public static void render() {
      if (Main.getGameState() == GameState.MAIN_MENU) {
         GL11.glPushMatrix();
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.godRay);
         GL11.glTranslatef(Display.getWidth() / 2, -100.0F, 0.0F);

         for (int i = -2; i <= 2; i++) {
            float brightness = (float)Math.cos(GameTime.elapsedMillis / 500.0F + i * 3) / 4.0F + 0.6F;
            GL11.glPushMatrix();
            GL11.glScalef(1.0F, 1.1F + (float)Math.sin(i * 23) / 8.0F, 1.0F);
            GL11.glTranslatef(-i * 25, 0.0F, 0.0F);
            GL11.glRotatef(i * 15, 0.0F, 0.0F, 1.0F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, brightness);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(-50.0F, -50.0F);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex2f(50.0F, -50.0F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex2f(75.0F, 240.0F);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(-75.0F, 240.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, brightness);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(-75.0F, 240.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, brightness);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex2f(75.0F, 240.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex2f(80.0F, 300.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(-80.0F, 300.0F);
            GL11.glEnd();
            GL11.glPopMatrix();
         }

         GL11.glPopMatrix();
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.titleTexture);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(Display.getWidth() / 2 - 170, 20.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(Display.getWidth() / 2 + 170, 20.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth() / 2 + 170, 175.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth() / 2 - 170, 175.0F);
         GL11.glEnd();
      }
   }
}
