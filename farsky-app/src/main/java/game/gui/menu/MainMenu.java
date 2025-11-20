package game.gui.menu;

import game.Main;
import game.gui.util.Button;
import game.gui.util.ButtonType;
import game.world.gen.SeedInput;

public final class MainMenu extends MenuScreen {
   public MainMenu() {
      this.buttons.add(new Button(ButtonType.MENU_BUTTON, 0, "Play"));
      this.buttons.add(new Button(ButtonType.MENU_BUTTON, 1, "Sandbox"));
      this.buttons.add(new Button(ButtonType.MENU_BUTTON, 2, "Load"));
      this.buttons.add(new Button(ButtonType.MENU_BUTTON, 3, "Options"));
      this.buttons.add(new Button(ButtonType.MENU_BUTTON, 4, "Quit"));
   }

   @Override
   public final void draw() {
      for (int i = 0; i < this.buttons.size(); i++) {
         this.buttons.get(i).render();
      }
   }

   @Override
   public final void onButtonClicked(Button button) {
      if (button.hasLabel("Play")) {
         MenuController.currentMenuState = MenuState.NEW_GAME;
      }

      if (button.hasLabel("Sandbox")) {
         SeedInput.randomize();
         MenuController.currentMenuState = MenuState.SANDBOX;
      }

      if (button.hasLabel("Load")) {
         MenuController.refreshSaveList();
         MenuController.currentMenuState = MenuState.LOAD;
      }

      if (button.hasLabel("Options")) {
         MenuController.currentMenuState = MenuState.OPTIONS;
      }

      if (button.hasLabel("Quit")) {
         Main.exitRequested = true;
      }
   }
}
