package game.render;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class FullscreenQuad {
   private FullscreenQuad() {
   }

   public static void draw() {
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth(), 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth(), Display.getHeight());
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, Display.getHeight());
      GL11.glEnd();
   }

   public static void drawFlipped() {
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth(), 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth(), Display.getHeight());
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, Display.getHeight());
      GL11.glEnd();
   }
}
