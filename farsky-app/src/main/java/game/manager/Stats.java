package game.manager;

import java.io.Serializable;

public class Stats implements Serializable {
   private static final long serialVersionUID = -3130715661072989919L;
   private int timePlayedInMin = 0;
   private int nbFishKilled = 0;
   private int nbPredatorsKilled = 0;
   private int nbOfDeath = 0;
   private int nbOfSpearShoot = 0;
   private int nbOfShootLost = 0;
   private int nbOfBandagesUsed = 0;
   private int crystalCollected = 0;
   private int baseBlockBuilt = 0;

   public final void recordFishKilled() {
      this.nbFishKilled++;
   }

   public final void recordPredatorKilled() {
      this.nbPredatorsKilled++;
   }

   public final void recordDeath() {
      this.nbOfDeath++;
   }

   public final void recordSpearShot() {
      this.nbOfSpearShoot++;
   }

   public final void recordShotLost() {
      this.nbOfShootLost++;
   }

   public final void recordBandageUsed() {
      this.nbOfBandagesUsed++;
   }

   public final void recordCrystalCollected() {
      this.crystalCollected++;
   }

   public final void recordBaseBlockBuilt() {
      this.baseBlockBuilt++;
   }

   public final void updateTimePlayed() {
      this.timePlayedInMin = (int)(GameTime.totalPlayTime / 60.0F);
   }

   public final String getStatLine(int index) {
      if (index == 0) {
         return "Time Played: " + this.timePlayedInMin / 60 + "h " + (this.timePlayedInMin - this.timePlayedInMin / 60 * 60) + "min";
      } else if (index == 1) {
         return "Fish killed: " + this.nbFishKilled;
      } else if (index == 2) {
         return "Predators killed: " + this.nbPredatorsKilled;
      } else if (index == 3) {
         return "Speargun accuracy: " + (int)((float)(this.nbOfSpearShoot - this.nbOfShootLost) / this.nbOfSpearShoot * 100.0F) + "%";
      } else if (index == 4) {
         return "Deaths: " + this.nbOfDeath;
      } else if (index == 5) {
         return "Bandages used: " + this.nbOfBandagesUsed;
      } else if (index == 6) {
         return "Crystal Collected: " + this.crystalCollected;
      } else {
         return index == 7 ? "Base blocks built: " + this.baseBlockBuilt : "";
      }
   }
}
