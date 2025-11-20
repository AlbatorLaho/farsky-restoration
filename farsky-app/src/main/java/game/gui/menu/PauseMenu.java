package game.gui.menu;

import game.Main;
import game.gui.util.Button;
import game.gui.util.ButtonType;
import game.manager.GameScene;
import game.manager.GameState;

public final class PauseMenu extends MenuScreen {
   public PauseMenu() {
      this.buttons.add(new Button(ButtonType.MENU_BUTTON, 0, "Resume"));
      this.buttons.add(new Button(ButtonType.MENU_BUTTON, 1, "Options"));
      this.buttons.add(new Button(ButtonType.MENU_BUTTON, 2, "Save & Quit"));
   }

   @Override
   public final void draw() {
      for (int i = 0; i < this.buttons.size(); i++) {
         this.buttons.get(i).render();
      }
   }

   @Override
   public final void onButtonClicked(Button button) {
      if (button.hasLabel("Resume")) {
         Main.gameState = GameState.PLAYING;
      }

      if (button.hasLabel("Options")) {
         MenuController.currentMenuState = MenuState.OPTIONS;
      }

      if (button.hasLabel("Save & Quit")) {
         GameScene.save();
         Main.gameState = GameState.LOADING_MENU;
         MenuController.currentMenuState = MenuState.MAIN;
      }
   }
}
