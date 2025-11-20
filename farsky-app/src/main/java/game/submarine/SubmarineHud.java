package game.submarine;

import game.shader.Shaders;
import game.sounds.SoundManager;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.util.FontFamily;
import game.util.FontRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class SubmarineHud {
   private static SubmarinePiece highlightedPiece;
   private static float highlightProgress = 1.0F;
   private static float displayTimer = 0.0F;

   public static void render() {
      if (!(displayTimer <= 0.0F)) {
         Shaders.subShader.bind();
         Shaders.setUniform("colorFactor", 0.0);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, displayTimer);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subGui);
         drawHudQuad();
         if (GameScene.avatar != null) {
            for (SubmarinePiece piece : SubmarinePiece.values()) {
               if (GameScene.avatar.hasSubmarinePiece(piece)) {
                  if (piece == highlightedPiece) {
                     Shaders.setUniform("colorFactor", -highlightProgress * (highlightProgress - 1.0F) * 4.0F);
                     GL11.glColor4f(1.0F, 1.0F, 1.0F, highlightProgress);
                  } else {
                     Shaders.setUniform("colorFactor", 0.0);
                     GL11.glColor4f(1.0F, 1.0F, 1.0F, displayTimer);
                  }

                  switch (piece) {
                     case CANOPY:
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subBell);
                        break;
                     case STABILIZER_RIGHT_LOWER:
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subLeftBottomBottle);
                        break;
                     case PROPELLER_RIGHT:
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subLeftMotor);
                        break;
                     case STABILIZER_RIGHT_UPPER:
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subLeftTopBottle);
                        break;
                     case HULL:
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subMain);
                        break;
                     case STABILIZER_LEFT_LOWER:
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subRightBottomBottle);
                        break;
                     case PROPELLER_LEFT:
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subRightMotor);
                        break;
                     case STABILIZER_LEFT_UPPER:
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subRightTopBottle);
                        break;
                     case PORTHOLE:
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.subTop);
                  }

                  drawHudQuad();
               }
            }

            FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
            FontRenderer.drawCentered(Display.getWidth() / 2, 10, "Submarine", 0.9F);
            if (GameScene.avatar.getSubmarinePiecesCount() <= 1) {
               FontRenderer.drawCentered(Display.getWidth() / 2, 50, GameScene.avatar.getSubmarinePiecesCount() + " piece found out of " + SubmarinePiece.values().length, 0.6F);
               return;
            }

            FontRenderer.drawCentered(Display.getWidth() / 2, 50, GameScene.avatar.getSubmarinePiecesCount() + " pieces found out of " + SubmarinePiece.values().length, 0.6F);
         }
      }
   }

   public static void update(float delta) {
      if (displayTimer > 0.0F) {
         displayTimer -= delta;
      }

      if (highlightProgress < 1.0F) {
         highlightProgress += delta * 0.5F;
      } else {
         highlightProgress = 1.0F;
         highlightedPiece = null;
      }
   }

   public static void onPieceFound(SubmarinePiece piece) {
      highlightedPiece = piece;
      highlightProgress = 0.0F;
      displayTimer = 8.0F;
      SoundManager.playSound(SoundManager.sfxNewPiece, null, 1.0F, 0.1F);
   }

   private static void drawHudQuad() {
      float size = Math.min(1.0F, (Display.getWidth() - 200.0F) / 600.0F) * 0.45F;
      size = 600.0F * size;
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth() / 2.0F - size / 2.0F, 200.0F - size / 2.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth() / 2.0F - size / 2.0F, 200.0F + size / 2.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth() / 2.0F + size / 2.0F, 200.0F + size / 2.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth() / 2.0F + size / 2.0F, 200.0F - size / 2.0F);
      GL11.glEnd();
   }
}
