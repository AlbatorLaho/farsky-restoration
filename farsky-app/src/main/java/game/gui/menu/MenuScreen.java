package game.gui.menu;

import java.util.ArrayList;

import game.gui.util.Button;

public abstract class MenuScreen {
   protected ArrayList<Button> buttons = new ArrayList<>();

   protected abstract void draw();

   protected abstract void onButtonClicked(Button button);

   protected void update(float delta) {
      for (int i = 0; i < this.buttons.size(); i++) {
         this.buttons.get(i).update(delta);
      }

      this.checkClicks();
   }

   protected void checkClicks() {
      for (int i = 0; i < this.buttons.size(); i++) {
         if (this.buttons.get(i).isClicked()) {
            this.onButtonClicked(this.buttons.get(i));
         }
      }
   }

   protected void refreshLayout() {
      for (int i = 0; i < this.buttons.size(); i++) {
         this.buttons.get(i).layout();
      }
   }

   protected final Button findButton(String label) {
      for (int i = 0; i < this.buttons.size(); i++) {
         if (this.buttons.get(i).hasLabel(label)) {
            return this.buttons.get(i);
         }
      }

      return null;
   }
}
