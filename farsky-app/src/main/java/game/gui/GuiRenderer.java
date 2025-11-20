package game.gui;

import game.Main;
import game.chunks.ChunkManager;
import game.gui.dialog.DialogManager;
import game.gui.menu.MenuController;
import game.gui.util.MenuBackground;
import game.manager.AssetLoader;
import game.manager.GameScene;
import game.manager.GameState;
import game.manager.Loading;
import game.manager.RenderManager;
import game.manager.TextureManager;
import game.submarine.SubmarineHud;
import game.util.FontFamily;
import game.util.FontRenderer;

import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.opengl.Display;
import game.render.FullscreenQuad;
import org.lwjgl.opengl.GL11;

public final class GuiRenderer {
   private static float cinematicBarHeight = 0.0F;
   public static boolean showSplash = true;
   public static int loadPercent = 0;

   public static void render() {
      switch (Main.getGameState()) {
         case PLAYING:
            GameOverlay.render(true);
            if (GameScene.avatar != null && !GameScene.avatar.isDead()) {
               CrosshairHud.render();
               PlayerHud.renderStatusHud();
               PlayerHud.render();
               GameScene.avatar.renderHudBar();
            }

            InteractionHint.render();
            if (GameScene.dialogManager != null) {
               DialogManager.render();
            }

            SubmarineHud.render();
            break;
         case MAIN_MENU:
         case PAUSED:
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            MenuController.render();
            break;
         case STARTUP:
            if (showSplash) {
               AssetLoader.renderSplashScreen();
            } else {
               AssetLoader.renderLoadingScreen(loadPercent + "% loaded...");
            }
            break;
         case LOADING_MENU:
         case LOADING_GAME:
            Loading.renderLoadingText();
         case RELOADING:
         default:
            break;
         case MAP:
         case INVENTORY:
            ScreenBlur.render();
            GameOverlay.render(false);
            PlayerHud.renderStatusHud();
            break;
         case CINEMATIC_INGAME:
            if (GameScene.dialogManager != null) {
               DialogManager.render();
            }
      }

      if (!RenderManager.hideHud && Main.getGameState() != GameState.MAIN_MENU && Main.getGameState() != GameState.LOADING_MENU && Main.getGameState() != GameState.LOADING_GAME && cinematicBarHeight != 0.0F) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(0.0F, Display.getHeight() - cinematicBarHeight);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(Display.getWidth(), Display.getHeight() - cinematicBarHeight);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth(), Display.getHeight());
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(0.0F, Display.getHeight());
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(0.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(Display.getWidth(), 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth(), cinematicBarHeight);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(0.0F, cinematicBarHeight);
         GL11.glEnd();
      }
   }

   public static void renderDebugOverlay() {
      if (Main.isDebug) {
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         FontRenderer.draw(370, 10, "FPS: " + Main.fps, 0.5F);
         FontRenderer.draw(370, 35, "Sleep time: " + Main.syncSleepMs + "ms", 0.5F);
         FontRenderer.draw(370, 60, "Chunks In Gen Thread: " + ChunkManager.pendingCount, 0.5F);
         FontRenderer.draw(370, 85, "Loaded chunks: " + ChunkManager.activeCount, 0.5F);
         if (GameScene.avatar != null) {
            FontRenderer.draw(370, 110, "x: " + GameScene.avatar.getCameraPos().x, 0.5F);
            FontRenderer.draw(370, 130, "y: " + GameScene.avatar.getCameraPos().y, 0.5F);
            FontRenderer.draw(370, 150, "z: " + GameScene.avatar.getCameraPos().z, 0.5F);
         }

         byte yPos = 10;

         for (Iterator<Entry<String, Integer>> iter = GameScene.debugTimings.entrySet().iterator(); iter.hasNext(); yPos += 25) {
            FontRenderer.draw(Display.getWidth() - 500, yPos, iter.next() + "us", 0.5F);
         }
      }

      if (!RenderManager.hideHud && Main.getGameState() == GameState.MAIN_MENU) {
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public static void renderOpenGLError(String version) {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
      FullscreenQuad.draw();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 - 20, "Your computer does not support openGL " + version + ".", 0.8F);
      FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 + 20, "Sorry. Please try on another computer.", 0.8F);
   }

   public static void update(float delta) {
      MenuBackground.update();
      ScreenBlur.update(delta);
      switch (Main.getGameState()) {
         case PLAYING:
            CrosshairHud.update(delta);
            SubmarineHud.update(delta);
            break;
         case MAIN_MENU:
         case PAUSED:
            MenuController.update(delta);
		default:
			break;
      }

      if (GameScene.avatar != null) {
         PlayerHud.update(delta);
      }

      if (Main.getGameState() != GameState.CINEMATIC_INGAME && Main.getGameState() != GameState.CINEMATIC_INTRO) {
         if (cinematicBarHeight <= 0.0F) {
            cinematicBarHeight = 0.0F;
         } else {
            cinematicBarHeight -= delta * 150.0F;
         }
      } else if (cinematicBarHeight >= 100.0F) {
         cinematicBarHeight = 100.0F;
      } else {
         cinematicBarHeight += delta * 150.0F;
      }
   }
}
