package game.gui.menu;
import game.enemy.EnemyGenerator;

import game.Main;
import game.gui.util.Button;
import game.gui.util.ButtonType;
import game.manager.GameMode;
import game.manager.GameScene;
import game.manager.GameState;
import game.manager.Loading;
import game.manager.TextureManager;
import game.saving.SaveManager;
import game.sounds.ChunkLayer;
import game.util.FontFamily;
import game.util.FontRenderer;
import game.world.gen.SeedInput;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class NewGameMenu extends MenuScreen {
   public static boolean survivorLocked = true;
   private Button cancelButton;
   private int adventurerOffsetY = -100;

   protected NewGameMenu() {
      this.refreshLayout();
   }

   @Override
   protected final void draw() {
      for (int i = 0; i < this.buttons.size(); i++) {
         this.buttons.get(i).render();
      }

      this.cancelButton.render();
      FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.6F);
      FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 + this.adventurerOffsetY + 20, "Your journey begins here!", 0.5F);
      FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 + 20, "Predators are more numerous", 0.5F);
      FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 + 40, "No indication on pieces location", 0.5F);
      FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 + 60, "Only one life", 0.5F);
      if (survivorLocked) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.8F);
         ChunkLayer.drawQuad(Display.getWidth() / 2, Display.getHeight() / 2 + 48, 360.0F, 115.0F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.lock);
         GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.8F);
         ChunkLayer.drawQuad(Display.getWidth() / 2, Display.getHeight() / 2 + 60, 49.0F, 64.25F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
         FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 + 40, "Finish the game in Adventurer", 0.5F);
         FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 + 60, "to unlock this mode", 0.5F);
      }
   }

   @Override
   protected final void update(float delta) {
      for (int i = 0; i < this.buttons.size(); i++) {
         if (i != 1 || !survivorLocked) {
            this.buttons.get(i).update(delta);
         }
      }

      this.cancelButton.update(delta);

      for (int i = 0; i < this.buttons.size(); i++) {
         if ((i != 1 || !survivorLocked) && this.buttons.get(i).isClicked()) {
             this.onButtonClicked(this.buttons.get(i));
         }
      }

      if (this.cancelButton.isClicked()) {
         this.onButtonClicked(this.cancelButton);
      }
   }

   @Override
   protected final void onButtonClicked(Button button) {
      if (button.hasLabel("Adventurer")) {
         SeedInput.randomize();
         GameScene.gameMode = GameMode.ADVENTURE;
         SaveManager.generateSavePath();
         Loading.newWorld(10.0F, 7.0F, EnemyGenerator.SpawningLevel.NORMAL);
         Main.gameState = GameState.LOADING_GAME;
      }

      if (button.hasLabel("Survivor")) {
         SeedInput.randomize();
         GameScene.gameMode = GameMode.SURVIVOR;
         SaveManager.generateSavePath();
         Loading.newWorld(10.0F, 10.0F, EnemyGenerator.SpawningLevel.HIGH);
         Main.gameState = GameState.LOADING_GAME;
      }

      if (button.hasLabel("Cancel")) {
         MenuController.currentMenuState = MenuState.MAIN;
      }
   }

   @Override
   public final void refreshLayout() {
      this.buttons.clear();
      this.buttons.add(new Button("Adventurer", Display.getWidth() / 2, Display.getHeight() / 2 + this.adventurerOffsetY, 350.0F, 60.0F, FontFamily.ECCENTRIC));
      this.buttons.add(new Button("Survivor", Display.getWidth() / 2, Display.getHeight() / 2, 350.0F, 105.0F, FontFamily.ECCENTRIC));
      this.cancelButton = new Button("Cancel", Display.getWidth() / 2, Display.getHeight() - 100, ButtonType.ACTION_BUTTON);
   }
}
