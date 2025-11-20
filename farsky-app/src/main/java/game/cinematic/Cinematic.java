package game.cinematic;

import game.Main;
import game.environment.SkyDome;
import game.gui.dialog.Character;
import game.gui.dialog.DialogBox;
import game.gui.dialog.DialogManager;
import game.gui.menu.MenuState;
import game.gui.menu.MenuController;
import game.gui.menu.NewGameMenu;
import game.input.InputManager;
import game.manager.GameState;
import game.manager.Camera;
import game.manager.GameScene;
import game.manager.GameTime;
import game.manager.RenderManager;
import game.manager.TextureManager;
import game.player.Avatar;
import game.shader.BlurEffect;
import game.sounds.ChunkLayer;
import game.sounds.SoundManager;
import game.util.FontFamily;
import game.util.Point;
import game.util.FontRenderer;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import game.render.FullscreenQuad;
import org.lwjgl.opengl.GL11;

public final class Cinematic {
   public static enum CinematicState {
      INTRO,
      ENDING;
   }

   private static CinematicState currentState = CinematicState.INTRO;
   private static ArrayList<CinematicStage> stages;
   private static float letterboxAmount = 0.0F;
   private static float blackOverlayAlpha = 0.0F;
   private static String displayText = "";
   private static DialogBox dialogBox;
   private static boolean isCrashing = false;
   private static boolean survivorAchievementGranted;
   private static boolean survivorModeJustUnlocked = false;
   private static boolean showingCredits = false;
   public static boolean skipCredits = false;
   private static boolean allSkipped = false;

   public static void init(CinematicState state) {
      currentState = state;
      allSkipped = false;
      if (currentState == CinematicState.INTRO) {
         stages = new ArrayList<>();
         stages.add(new CinematicStage("synchro", 2.0F));
         stages.add(new CinematicStage("crashStart", 6.0F));
         stages.add(new CinematicStage("crashAppear", 1.0F));
         stages.add(new CinematicStage("crash", 3.0F));
         stages.add(new CinematicStage("crashBlackScreen", 6.0F));
         stages.add(new CinematicStage("Eyes closed", 9.0F));
         stages.add(new CinematicStage("Open eyes", 2.0F));
         stages.add(new CinematicStage("Blink eyes", 1.5F));
         stages.add(new CinematicStage("Get Up", 3.0F));
         CinematicStage.initCrashScene();
         dialogBox = new DialogBox();
         dialogBox.addLine("Mayday! Mayday!! MAYDAY!!!", Character.NATHAN);
         dialogBox.addLine("My submarine is breaking into pieces!!", Character.NATHAN);
      }

      if (currentState == CinematicState.ENDING) {
         survivorModeJustUnlocked = NewGameMenu.survivorLocked;
         stages = new ArrayList<>();
         stages.add(new CinematicStage("endReset", 0.1F));
         stages.add(new CinematicStage("endTransition", 1.0F));
         stages.add(new CinematicStage("sky", 16.0F));
         stages.add(new CinematicStage("blackScreen", 2.0F));
         stages.add(new CinematicStage("credits", 3600.0F));
         stages.add(new CinematicStage("Go back to menu", 4.0F));
         dialogBox = new DialogBox();
         dialogBox.addLine("I made it! I'm finally out!", Character.NATHAN);
         dialogBox.addLine("Great! I'm sending a boat to your position.", Character.MADISON);
         dialogBox.addLine("Time to go back home...", Character.NATHAN);
      }
   }

   public static void update(float deltaTime) {
      for (int stageIdx = 0; stageIdx < stages.size(); stageIdx++) {
         if (stages.get(stageIdx).tick(deltaTime)) {
            if (stages.get(stageIdx).getName().equals("synchro")) {
               letterboxAmount = 0.0F;
               blackOverlayAlpha = 1.0F;
               displayText = "";
               Camera.setPosition(new Point(), 0.0F, 0.0F);
            }

            if (stages.get(stageIdx).getName().equals("crashStart")) {
               isCrashing = true;
               if (!SoundManager.isTransientSoundPlaying(SoundManager.sfxCinematic)) {
                  SoundManager.playSound(SoundManager.sfxCinematic, null, 1.0F, 0.3F);
               }

               blackOverlayAlpha = 1.0F;
               displayText = "";
               dialogBox.update(deltaTime);
            }

            if (stages.get(stageIdx).getName().equals("crashAppear")) {
               letterboxAmount = 0.0F;
               blackOverlayAlpha = 1.0F - stages.get(stageIdx).getProgress();
               displayText = "";
               CinematicStage.updateParticles(deltaTime);
            }

            if (stages.get(stageIdx).getName().equals("crash")) {
               letterboxAmount = 0.0F;
               blackOverlayAlpha = 0.0F;
               CinematicStage.triggerExplosion(0);
               CinematicStage.updateParticles(deltaTime);
               dialogBox.update(deltaTime);
            }

            if (stages.get(stageIdx).getName().equals("crashBlackScreen")) {
               blackOverlayAlpha = 1.0F;
               displayText = "";
               dialogBox.update(deltaTime);
            }

            if (stages.get(stageIdx).getName().equals("blackScreen")) {
               dialogBox = null;
               blackOverlayAlpha = 1.0F;
               displayText = "";
            }

            if (stages.get(stageIdx).getName().equals("Eyes closed")) {
               Camera.setFromAvatar(GameScene.avatar);
               isCrashing = false;
               Main.gameState = GameState.CINEMATIC_INGAME;
               letterboxAmount = 1.0F;
               blackOverlayAlpha = 0.0F;
            }

            if (stages.get(stageIdx).getName().equals("Open eyes")) {
               Camera.setFromAvatar(GameScene.avatar);
               Camera.setRoll(-60.0F);
               Camera.addOffset(new Point(0.0F, -16.0F, 0.0F));
               letterboxAmount = 1.0F - stages.get(stageIdx).getProgress();
            }

            if (stages.get(stageIdx).getName().equals("Blink eyes")) {
               Camera.setFromAvatar(GameScene.avatar);
               Camera.setRoll(-60.0F);
               Camera.addOffset(new Point(0.0F, -16.0F, 0.0F));
               letterboxAmount = (float)(Math.cos(stages.get(stageIdx).getProgress() * 2.0F * Math.PI * 2.0 + Math.PI) * 0.3F);
            }

            if (stages.get(stageIdx).getName().equals("Get Up")) {
               Camera.setFromAvatar(GameScene.avatar);
               Camera.addOffset(new Point(0.0F, -16.0F * (1.0F - stages.get(stageIdx).getProgress()), 0.0F));
               Camera.setRoll(-(1.0F - (float)Math.sin(stages.get(stageIdx).getProgress() * Math.PI / 2.0)) * 60.0F);
               SoundManager.stopAll();
            }

            if (stages.get(stageIdx).getName().equals("endReset")) {
               blackOverlayAlpha = 1.0F;
               SoundManager.stopAll();
               DialogManager.clear();
            }

            if (stages.get(stageIdx).getName().equals("endTransition")) {
               blackOverlayAlpha = 1.0F - stages.get(stageIdx).getProgress();
               GameScene.avatar.setVerticalAngle(0.0F);
               Avatar.godMode = true;
               Point cameraPos = new Point(0.0F, 1.0F, 0.0F);
               Point rightDir;
               rightDir = GameScene.avatar.getRightDir();
               cameraPos = rightDir.cross(cameraPos);
               cameraPos = GameScene.avatar.getCameraPos().plus(rightDir.scaled(-150.0F).plus(cameraPos.scaled(-60.0F).plus(0.0F, 10.0F, 0.0F)));
               Camera.setPosition(new Point(cameraPos.x, 10.0F, cameraPos.z), GameScene.avatar.getHorizontalAngle() + 50.0F, -45.0F);
            }

            if (stages.get(stageIdx).getName().equals("sky")) {
               if (dialogBox != null) {
                  dialogBox.update(deltaTime);
               }

               blackOverlayAlpha = stages.get(stageIdx).getProgress() * 10.0F - 9.0F;
               Point cameraPos = new Point(0.0F, 1.0F, 0.0F);
               Point rightDir;
               rightDir = GameScene.avatar.getRightDir();
               cameraPos = rightDir.cross(cameraPos);
               cameraPos = GameScene.avatar.getCameraPos().plus(rightDir.scaled(-150.0F).plus(cameraPos.scaled(-60.0F).plus(0.0F, 10.0F, 0.0F)));
               Camera.setPosition(new Point(cameraPos.x, 10.0F, cameraPos.z), GameScene.avatar.getHorizontalAngle() + 50.0F, -45.0F + 135.0F * stages.get(stageIdx).getProgress());
               survivorAchievementGranted = false;
            }

            if (stages.get(stageIdx).getName().equals("credits")) {
               blackOverlayAlpha = 0.0F;
               if (!survivorAchievementGranted) {
                  Main.achievements.unlockSurvivor();
                  survivorAchievementGranted = true;
               }

               Camera.setPosition(Camera.getPosition().plus(-50.0F * deltaTime, 0.0F, 0.0F), 90.0F, -10.0F);
               showingCredits = true;
               if (skipCredits) {
                  stages.get(stageIdx).skip();
               }
            }

            if (stages.get(stageIdx).getName().equals("Go back to menu")) {
               showingCredits = false;
               blackOverlayAlpha = 1.0F;
               GameScene.markGameComplete();
               Main.gameState = GameState.LOADING_MENU;
               MenuController.currentMenuState = MenuState.MAIN;
               Avatar.godMode = false;
               SoundManager.stopAll();
               stages.get(stageIdx).skip();
            }
            break;
         }
      }

      if (stages.size() > 0 && stages.get(stages.size() - 1).isComplete()) {
         Main.gameState = GameState.PLAYING;
      }
   }

   public static void skipAll() {
      for (int i = 0; i < stages.size(); i++) {
         stages.get(i).skip();
      }

      allSkipped = true;
   }

   public static void render() {
      applyBlurEffect(false);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, blackOverlayAlpha);
      if (allSkipped) {
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
      }

      FullscreenQuad.draw();
      if (letterboxAmount > 0.0F) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(0.0F, Display.getHeight() - letterboxAmount * Display.getHeight() / 2.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(Display.getWidth(), Display.getHeight() - letterboxAmount * Display.getHeight() / 2.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth(), Display.getHeight());
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(0.0F, Display.getHeight());
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(0.0F, Display.getHeight() - letterboxAmount * Display.getHeight() / 2.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(Display.getWidth(), Display.getHeight() - letterboxAmount * Display.getHeight() / 2.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth(), Display.getHeight() - letterboxAmount * Display.getHeight() / 0.5F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(0.0F, Display.getHeight() - letterboxAmount * Display.getHeight() / 0.5F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(0.0F, 0.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2f(Display.getWidth(), 0.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth(), letterboxAmount * Display.getHeight() / 2.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(0.0F, letterboxAmount * Display.getHeight() / 2.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth(), letterboxAmount * Display.getHeight() / 2.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(0.0F, letterboxAmount * Display.getHeight() / 2.0F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2f(0.0F, letterboxAmount * Display.getHeight() / 0.5F);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(Display.getWidth(), letterboxAmount * Display.getHeight() / 0.5F);
         GL11.glEnd();
      }

      if (!displayText.equals("")) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 - 30, displayText, 1.0F);
      }

      if (showingCredits) {
         int lineHeight = FontRenderer.getCharHeight(0.6F) - 10;
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.titleTexture);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         ChunkLayer.drawQuad(Display.getWidth() / 2, 250, 340.0F, 155.0F);
         if (survivorModeJustUnlocked) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
            ChunkLayer.drawQuad(Display.getWidth() / 2, Display.getHeight() / 2 + -50, Display.getWidth(), 50.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.unlock);
            ChunkLayer.drawQuad(Display.getWidth() / 2 - 230, Display.getHeight() / 2 + -50, 45.0F, 46.0F);
            FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 - 25 + -50, "You unlocked the Survivor mode!", 0.8F);
         }

         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
         ChunkLayer.drawQuad(Display.getWidth() / 2, Display.getHeight() / 2 + 50 + 5, Display.getWidth(), 4 * lineHeight + 10);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPushMatrix();
         GL11.glTranslatef(-150.0F, 0.0F, 0.0F);

         for (int col = 0; col <= 1; col++) {
            if (col == 1) {
               GL11.glTranslatef(300.0F, 0.0F, 0.0F);
            }

            for (int row = 0; row < 4; row++) {
               FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 - (lineHeight << 2) / 2 + 50 + row * lineHeight, GameScene.stats.getStatLine(row + (col << 2)), 0.6F);
            }
         }

         GL11.glPopMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)Math.cos(GameTime.elapsedMillis / 600.0F) * 0.4F + 0.5F);
         FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() - 200, "Press " + InputManager.getKeyName("Interaction") + " to continue", 0.7F);
      }

      if (dialogBox != null && !RenderManager.hideHud) {
         dialogBox.render();
      }
   }

   public static void applyBlurEffect(boolean skip) {
      if (isCrashing && currentState == CinematicState.INTRO && !skip) {
         BlurEffect.apply(5.0E-4F, 0.0F);
      }
   }

   public static void renderCrashScene() {
      if (currentState == CinematicState.INTRO) {
         GL11.glPushMatrix();
         Camera.applyMatrix();
         GL11.glTranslatef(-Camera.getWorldOffset().x, 0.0F, -Camera.getWorldOffset().z);
         GL11.glTranslatef((float)(Math.random() - 0.5) * 0.25F, (float)(Math.random() - 0.5) * 0.25F, (float)(Math.random() - 0.5) * 0.25F);
         SkyDome.render();
         CinematicStage.renderCrash();
         GL11.glPopMatrix();
      }
   }

   public static boolean isShowingCredits() {
      return showingCredits;
   }
}
