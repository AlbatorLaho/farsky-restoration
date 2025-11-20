package game.gui;

import game.environment.DepthAtmosphere;
import game.input.InputManager;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.player.Avatar;
import game.shader.CrackEffect;
import game.sounds.SoundManager;
import game.util.FontFamily;
import game.util.FontRenderer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class GameOverlay {
   private static float crackLevel = 0.0F;

   public static void render(boolean showEffects) {
      if (GameScene.avatar != null) {
         FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (showEffects) {
            if (GameScene.avatar.getExplosionEffect() > 0.0F) {
               float crackAmount = (int)(GameScene.avatar.getExplosionEffect() * 4.0F + 0.8F) / 4.0F;
               CrackEffect.render(crackAmount);
               if (crackAmount > crackLevel) {
                  crackLevel = crackAmount;
                  SoundManager.playSound(SoundManager.sfxCrack, null, 0.9F + (float)Math.random() * 0.2F);
               }
            } else {
               crackLevel = 0.0F;
            }
         }

         float depth = DepthAtmosphere.getDepthInMeters();
         float depthTop = depth + 80.0F;
         float depthBottom = depth - (Display.getHeight() - 200) * 2.5F;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.35F);

         for (int marker = (int)(depthTop - depthTop % 50.0F); marker > (int)(depthBottom + depthBottom % 50.0F - 50.0F); marker -= 50) {
            FontRenderer.draw(0, (int)((depthTop - marker) * 2.5F) - 15, marker + "m", 0.4F);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.55F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(0.0F, 192.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(55.0F, 192.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(55.0F, 208.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(0.0F, 208.0F);
         GL11.glEnd();
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.9F);
         FontRenderer.draw(0, 200 - FontRenderer.getCharHeight(0.4F) + 11, (int)depth + "m", 0.4F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.35F);
         GL11.glLineWidth(1.0F);
         GL11.glBegin(GL11.GL_LINES);
         GL11.glVertex2f(55.0F, 0.0F);
         GL11.glVertex2f(55.0F, Display.getHeight());

         for (int marker = (int)(depthTop - depthTop % 50.0F); marker > (int)(depthBottom + depthBottom % 50.0F - 50.0F); marker -= 50) {
            for (int tick = 0; tick < 5; tick++) {
               GL11.glVertex2f(55.0F, (depthTop - marker + tick * 50 / 5) * 2.5F);
               GL11.glVertex2f(45.0F, (depthTop - marker + tick * 50 / 5) * 2.5F);
            }

            GL11.glVertex2f(55.0F, (depthTop - marker) * 2.5F);
            GL11.glVertex2f(65.0F, (depthTop - marker) * 2.5F);
         }

         GL11.glEnd();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
         GL11.glColor4f(0.5F, 0.0F, 0.0F, GameScene.avatar.getHitFlash());
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(0.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth(), 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth(), Display.getHeight());
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(0.0F, Display.getHeight());
         GL11.glEnd();
         if (GameScene.avatar.isDead()) {
            GL11.glColor4f(0.0F, 0.0F, 0.0F, Avatar.getDeathFade() * 0.75F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(Display.getWidth(), 0.0F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex2f(Display.getWidth(), Display.getHeight());
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex2f(0.0F, Display.getHeight());
            GL11.glEnd();
            FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 - FontRenderer.getCharHeight(0.75F), Avatar.deathMessage, 0.75F);
            if (Avatar.deathTimer >= 4.0F) {
               if (GameScene.gameMode.isOneLife()) {
                  if (GameScene.avatar.getSubmarinePiecesCount() > 1) {
                     FontRenderer.drawCentered(
                        Display.getWidth() / 2,
                        Display.getHeight() / 2 + FontRenderer.getCharHeight(0.65F),
                        "You found " + GameScene.avatar.getSubmarinePiecesCount() + " pieces of the submarine",
                        0.65F
                     );
                  } else {
                     FontRenderer.drawCentered(
                        Display.getWidth() / 2,
                        Display.getHeight() / 2 + FontRenderer.getCharHeight(0.65F),
                        "You found " + GameScene.avatar.getSubmarinePiecesCount() + " piece of the submarine",
                        0.65F
                     );
                  }

                  FontRenderer.drawCentered(
                     Display.getWidth() / 2,
                     Display.getHeight() / 2 + (int)(FontRenderer.getCharHeight(0.65F) * 1.9F),
                     "Press " + InputManager.getKeyName("Interaction") + " to exit",
                     0.65F
                  );
               } else {
                  FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 + FontRenderer.getCharHeight(0.65F), "You dropped all your items on the floor!", 0.65F);
                  FontRenderer.drawCentered(
                     Display.getWidth() / 2,
                     Display.getHeight() / 2 + (int)(FontRenderer.getCharHeight(0.65F) * 1.9F),
                     "Press " + InputManager.getKeyName("Interaction") + " to continue",
                     0.65F
                  );
               }
            }
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
