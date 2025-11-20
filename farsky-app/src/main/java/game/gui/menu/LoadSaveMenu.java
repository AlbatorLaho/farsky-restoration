package game.gui.menu;

import game.Main;
import game.gui.util.Button;
import game.gui.util.ButtonType;
import game.manager.GameState;
import game.saving.SaveSlotBrowser;
import game.util.FontRenderer;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;

public final class LoadSaveMenu extends MenuScreen {
   private static SaveSlotBrowser saveSlotBrowser;
   private ArrayList<Button> confirmButtons = new ArrayList<>();
   private boolean confirmingDelete = false;
   private float fadeAmount = 1.0F;
   private boolean deleting = false;

   protected LoadSaveMenu() {
      this.refreshSaveSlots();
      this.refreshLayout();
   }

   public final void refreshSaveSlots() {
      saveSlotBrowser = new SaveSlotBrowser();
      boolean hasSaves = saveSlotBrowser.getSlotCount() > 0;

      for (int i = 0; i < this.buttons.size(); i++) {
         if (this.buttons.get(i).hasLabel("Play")) {
            this.buttons.get(i).setEnabled(hasSaves);
         }

         if (this.buttons.get(i).hasLabel("Delete")) {
            this.buttons.get(i).setEnabled(hasSaves);
         }
      }
   }

   @Override
   public final void update(float delta) {
      if (!this.confirmingDelete) {
         for (int i = 0; i < this.buttons.size(); i++) {
            this.buttons.get(i).update(delta);
         }
      } else {
         for (int i = 0; i < this.confirmButtons.size(); i++) {
            this.confirmButtons.get(i).update(delta);
         }
      }

      if (!this.confirmingDelete) {
         for (int i = 0; i < this.buttons.size(); i++) {
             if (this.buttons.get(i).isClicked()) {
                 this.onButtonClicked(this.buttons.get(i));
            }
         }
      } else {
         for (int i = 0; i < this.confirmButtons.size(); i++) {
             if (this.confirmButtons.get(i).isClicked()) {
                 this.onButtonClicked(this.confirmButtons.get(i));
            }
         }
      }

      saveSlotBrowser.update(delta);
      if (this.deleting) {
         this.fadeAmount -= delta * 2.0F;
         if (this.fadeAmount < 0.0F) {
            this.fadeAmount = 0.0F;
            this.deleting = false;
            this.refreshSaveSlots();
            return;
         }
      } else if (this.fadeAmount < 1.0F) {
         this.fadeAmount += delta * 2.0F;
      }
   }

   @Override
   public final void draw() {
      if (!this.confirmingDelete) {
         for (int i = 0; i < this.buttons.size(); i++) {
            this.buttons.get(i).render();
         }
      } else {
         FontRenderer.drawCenteredGradient(
            Display.getWidth() / 2, Display.getHeight() - 180, "Delete this slot?", 0.7F, new Point(0.5F, 0.5F, 0.5F), 1.0F, new Point(1.0F, 1.0F, 1.0F), 1.0F
         );

         for (int i = 0; i < this.confirmButtons.size(); i++) {
            this.confirmButtons.get(i).render();
         }
      }

      saveSlotBrowser.render(this.fadeAmount);
   }

   @Override
   public final void onButtonClicked(Button button) {
      if (button.hasLabel("Play") && saveSlotBrowser.loadSelectedSlot()) {
         Main.gameState = GameState.LOADING_GAME;
      }

      if (button.hasLabel("Cancel")) {
         MenuController.currentMenuState = MenuState.MAIN;
      }

      if (button.hasLabel("Delete")) {
         this.confirmingDelete = true;
      }

      if (this.confirmingDelete) {
         if (button.hasLabel("Yes")) {
            saveSlotBrowser.deleteSelectedSlot();
            this.deleting = true;
            this.confirmingDelete = false;
         }

         if (button.hasLabel("No")) {
            this.confirmingDelete = false;
         }
      }
   }

   @Override
   public final void checkClicks() {
      if (!this.confirmingDelete) {
         for (int i = 0; i < this.buttons.size(); i++) {
            if (this.buttons.get(i).isClicked()) {
               this.onButtonClicked(this.buttons.get(i));
            }
         }
      } else {
         for (int i = 0; i < this.confirmButtons.size(); i++) {
            if (this.confirmButtons.get(i).isClicked()) {
               this.onButtonClicked(this.confirmButtons.get(i));
            }
         }
      }
   }

   @Override
   public final void refreshLayout() {
      this.buttons.clear();
      this.buttons.add(new Button("Delete", Display.getWidth() / 2 - 250, Display.getHeight() - 100, ButtonType.ACTION_BUTTON));
      this.buttons.add(new Button("Play", Display.getWidth() / 2, Display.getHeight() - 100, ButtonType.ACTION_BUTTON));
      this.buttons.add(new Button("Cancel", Display.getWidth() / 2 + 250, Display.getHeight() - 100, ButtonType.ACTION_BUTTON));
      this.confirmButtons.clear();
      this.confirmButtons.add(new Button("Yes", Display.getWidth() / 2 - 150, Display.getHeight() - 100, ButtonType.ACTION_BUTTON));
      this.confirmButtons.add(new Button("No", Display.getWidth() / 2 + 150, Display.getHeight() - 100, ButtonType.ACTION_BUTTON));
   }
}
