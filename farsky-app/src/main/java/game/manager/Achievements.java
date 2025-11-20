package game.manager;

import java.io.Serializable;

import game.gui.menu.NewGameMenu;
import game.saving.SaveManager;

public class Achievements implements Serializable {
   private static final long serialVersionUID = -183592109345318880L;
   private int money = 0;
   private boolean unlockSurvivor = false;

   public final void unlockSurvivor() {
      this.unlockSurvivor = true;
      this.updateSurvivorLock();
      SaveManager.saveAchievements();
   }

   public final void addMoney(int amount) {
      this.money += amount;
      SaveManager.saveAchievements();
   }

   public final boolean spendMoney(int amount) {
      if (this.money >= amount) {
         this.money -= amount;
         SaveManager.saveAchievements();
         return true;
      } else {
         return false;
      }
   }

   public final int getMoney() {
      return this.money;
   }

   public final void updateSurvivorLock() {
      NewGameMenu.survivorLocked = !this.unlockSurvivor;
   }
}
