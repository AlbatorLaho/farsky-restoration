package game.gui.dialog;

import game.Main;
import game.chunks.ChunkManager;
import game.gui.InteractionHint;
import game.input.InputManager;
import game.manager.GameState;
import game.manager.InGameState;
import game.manager.RenderManager;
import game.manager.GameScene;
import game.manager.GameTime;
import game.util.Coord;
import java.io.Serializable;
import java.util.ArrayList;

public class DialogManager implements Serializable {
   private static final long serialVersionUID = -3125310620090903693L;
   private boolean triggerStart;
   private boolean triggerWakeUp;
   private boolean triggerFirstInside;
   private boolean triggerOxygen;
   private boolean triggerHunger;
   private boolean triggerLife;
   private boolean triggerBleeding;
   private boolean triggerCliff;
   private boolean triggerKrakensDetected;
   private boolean triggerWaterFlood;
   private boolean triggerSubmarineBuilt;
   private boolean triggerNight;
   private transient float cliffCheckTimer;
   private boolean triggerDeath = false;
   public static transient boolean waterFloodTriggered = false;
   private static transient ArrayList<DialogBox> dialogQueue;

   public DialogManager() {
      this.triggerOxygen = false;
      this.triggerHunger = false;
      this.triggerLife = false;
      this.triggerBleeding = false;
      this.triggerCliff = false;
      this.triggerKrakensDetected = false;
      this.triggerWaterFlood = false;
      this.triggerSubmarineBuilt = false;
      this.triggerNight = false;
      this.cliffCheckTimer = 4.0F;
      dialogQueue = new ArrayList<>();
   }

   public static void render() {
      if (!RenderManager.hideHud) {
         if (dialogQueue.size() > 0) {
            dialogQueue.get(0).render();
         }
      }
   }

   public final void update(float delta) {
      if (dialogQueue == null) {
         dialogQueue = new ArrayList<>();
      }

      if (GameScene.avatar != null && GameScene.gameMode.hasDialog()) {
         DialogBox newDialog = null;
         if (GameScene.avatar.isDead()) {
            if (!this.triggerDeath) {
               this.triggerDeath = true;
               dialogQueue.clear();
               newDialog = new DialogBox();
               newDialog.addLine("Nathan, do you reach me?", Character.MADISON);
               newDialog.addLine("Nathan! Nathan!!!!", Character.MADISON);
            }
         } else {
            this.triggerDeath = false;
            if (!this.triggerStart) {
               newDialog = new DialogBox();
               newDialog.addLine("Nathan? Nathan, are you alright?", Character.MADISON);
               newDialog.addLine("...", Character.NONE);
               newDialog.addLine("Nathan! Answer me!", Character.MADISON);
               this.triggerStart = true;
            }

            if (!this.triggerWakeUp && Main.getGameState() == GameState.PLAYING) {
               newDialog = new DialogBox("GoToBase");
               newDialog.addLine("I'm fine, don't worry Madison.", Character.NATHAN);
               newDialog.addLine("Thank god! I was so scared.", Character.MADISON);
               newDialog.addLine("I won't survive long without oxygen.", Character.NATHAN);
               newDialog.addLine("There should be a base near your position.\nI'm sending you the coordinates.", Character.MADISON);
               this.triggerWakeUp = true;
            }

            if (!this.triggerFirstInside && GameScene.getInGameState() == InGameState.ACTIVE) {
               newDialog = new DialogBox();
               newDialog.addLine("I'm in the base.", Character.NATHAN);
               newDialog.addLine("Good! There may be some supplies left.\nAre you gonna be okay?", Character.MADISON);
               newDialog.addLine("I've been better.", Character.NATHAN);
               newDialog.addLine("I think I should be able to find the pieces of the submarine to fix it back.", Character.NATHAN);
               newDialog.addLine("Okay. I'll be able to send a boat to rescue you once you reach the surface.\nStay safe!", Character.MADISON);
               this.triggerFirstInside = true;
            }

            if (!this.triggerOxygen && GameScene.avatar.getOxygen() < 60.0F) {
               newDialog = new DialogBox();
               newDialog.addLine("My oxygen level is low! I'm hardly breathing!", Character.NATHAN);
               newDialog.addLine("Get back to the base. Your oxygen should refill once inside.", Character.MADISON);
               this.triggerOxygen = true;
            }

            if (!this.triggerNight && GameTime.isNight()) {
               newDialog = new DialogBox();
               newDialog.addLine("It's getting dark. Night is coming.", Character.NATHAN);
               newDialog.addLine("You'd better watch out, predators are hunting at night!", Character.MADISON);
               this.triggerNight = true;
            }

            if (!this.triggerHunger && GameScene.avatar.getHunger() < 0.2F) {
               newDialog = new DialogBox();
               if (this.triggerLife) {
                  newDialog.addLine("I'm so hungry!", Character.NATHAN);
               } else {
                  newDialog.addLine("I'm so hungry!", Character.NATHAN);
                  newDialog.addLine("Why don't you kill and eat some fish?", Character.MADISON);
               }

               this.triggerHunger = true;
            }

            if (!this.triggerLife && GameScene.avatar.getHealth() / GameScene.avatar.getMaxOxygen() < 0.5F) {
               newDialog = new DialogBox();
               if (this.triggerHunger) {
                  newDialog.addLine("I'm feeling weak. I won't last long like this.", Character.NATHAN);
                  newDialog.addLine("Eat something, it will cheer you up!", Character.MADISON);
               } else {
                  newDialog.addLine("I'm feeling weak. I won't last long like this.", Character.NATHAN);
                  newDialog.addLine("Why don't you kill and eat some fish?", Character.MADISON);
               }

               this.triggerLife = true;
            }

            if (!this.triggerBleeding && (GameScene.avatar.hasWoundedArm() || GameScene.avatar.hasWoundedLeg())) {
               newDialog = new DialogBox();
               newDialog.addLine("I'm bleeding! If I don't stop that, I'll attract all predators around here!", Character.NATHAN);
               newDialog.addLine("Find a way to make a bandage.", Character.MADISON);
               newDialog.addLine("Maybe I could make one with seaweeds...", Character.NATHAN);
               this.triggerBleeding = true;
            }

            if (!this.triggerCliff) {
               this.cliffCheckTimer -= delta;
               if (this.cliffCheckTimer <= 0.0F) {
                  Coord pos = GameScene.avatar.getPos2D();
                  if (!(ChunkManager.getHeight(pos.x + 256.0F, pos.y) < -1600.0F)
                     && !(ChunkManager.getHeight(pos.x, pos.y + 256.0F) < -1600.0F)
                     && !(ChunkManager.getHeight(pos.x - 256.0F, pos.y) < -1600.0F)
                     && !(ChunkManager.getHeight(pos.x, pos.y - 256.0F) < -1600.0F)) {
                     this.cliffCheckTimer = 1.0F;
                  } else {
                     newDialog = new DialogBox();
                     newDialog.addLine("Be careful Nathan! The pressure is really high down there.", Character.MADISON);
                     newDialog.addLine("I'll wait to be prepared.", Character.NATHAN);
                     this.triggerCliff = true;
                  }
               }
            }

            if (!this.triggerKrakensDetected && GameScene.enemyManager.bossPresent && GameScene.enemyManager.getNearestBossDistance(GameScene.avatar.getPos2D()) < 300.0F) {
               newDialog = new DialogBox();
               newDialog.addLine("What is this? A giant octopus!?", Character.NATHAN);
               newDialog.addLine("A Kraken actually! They rely on the crystal to survive.\nThey protect it and won't let you take it away!", Character.MADISON);
               newDialog.addLine("Hum...", Character.NATHAN);
               this.triggerKrakensDetected = true;
            }

            if (!this.triggerWaterFlood && waterFloodTriggered) {
               newDialog = new DialogBox();
               newDialog.addLine("They are breaking the base! I need to fix the leak before the base gets flooded!", Character.NATHAN);
               newDialog.addLine("A hammer should do the job.", Character.MADISON);
               this.triggerWaterFlood = true;
            }

            if (!this.triggerSubmarineBuilt && GameScene.submarines.size() > 0) {
               newDialog = new DialogBox();
               newDialog.addLine("I did it! The submarine is fixed now!", Character.NATHAN);
               newDialog.addLine("Great Nathan! I'm sending a boat to get you as soon as you reach the surface!", Character.MADISON);
               this.triggerSubmarineBuilt = true;
            }
         }

         if (newDialog != null) {
            dialogQueue.add(newDialog);
         }
      }

      if (dialogQueue.size() > 0) {
         dialogQueue.get(0).update(delta);
         if (dialogQueue.get(0).isDone()) {
            if (dialogQueue.get(0).getId().equals("GoToBase")) {
               InteractionHint.addTimedHint("Press " + InputManager.getKeyName("Map") + " to see the map", 10.0F);
            }

            dialogQueue.remove(0);
         }
      }
   }

   public static void clear() {
      if (dialogQueue != null) {
         dialogQueue.clear();
      }
   }
}
