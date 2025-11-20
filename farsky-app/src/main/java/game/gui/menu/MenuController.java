package game.gui.menu;

import game.gui.util.MenuBackground;

public final class MenuController {
   public static MenuState currentMenuState = MenuState.MAIN;
   public static MenuState prevMenuState = MenuState.MAIN;
   private static MainMenu mainMenu;
   private static PauseMenu pauseMenu;
   private static NewGameMenu newGameMenu;
   private static OptionsMenu optionsMenu;
   private static LoadSaveMenu loadSaveMenu;
   private static SandboxMenu sandboxMenu;

   public static void init() {
      mainMenu = new MainMenu();
      pauseMenu = new PauseMenu();
      newGameMenu = new NewGameMenu();
      optionsMenu = new OptionsMenu();
      loadSaveMenu = new LoadSaveMenu();
      sandboxMenu = new SandboxMenu();
   }

   public static void update(float delta) {
      switch (currentMenuState) {
         case MAIN:
            mainMenu.update(delta);
            prevMenuState = MenuState.MAIN;
            return;
         case PAUSE:
            pauseMenu.update(delta);
            prevMenuState = MenuState.PAUSE;
            return;
         case NEW_GAME:
            newGameMenu.update(delta);
            return;
         case SANDBOX:
            sandboxMenu.update(delta);
            return;
         case OPTIONS:
            optionsMenu.update(delta);
            return;
         case LOAD:
            loadSaveMenu.update(delta);
      }
   }

   public static void render() {
      MenuBackground.applyBlur();
      switch (currentMenuState) {
         case NEW_GAME:
            newGameMenu.draw();
            break;
         case SANDBOX:
            sandboxMenu.draw();
            break;
         case OPTIONS:
            optionsMenu.draw();
            break;
         case LOAD:
            loadSaveMenu.draw();
		default:
			break;
      }

      MenuBackground.render();
      switch (prevMenuState) {
         case MAIN:
            mainMenu.draw();
            return;
         case PAUSE:
            pauseMenu.draw();
		default:
			break;
      }
   }

   public static void refreshLayouts() {
      mainMenu.refreshLayout();
      pauseMenu.refreshLayout();
      newGameMenu.refreshLayout();
      optionsMenu.refreshLayout();
      loadSaveMenu.refreshLayout();
      sandboxMenu.refreshLayout();
   }

   public static void refreshSaveList() {
      loadSaveMenu.refreshSaveSlots();
   }
}
