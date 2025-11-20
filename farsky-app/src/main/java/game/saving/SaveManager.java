package game.saving;

import game.Main;
import game.environment.DepthAtmosphere;
import game.gui.dialog.DialogManager;
import game.gui.menu.OptionsMenu;
import game.inventory.types.PlayerInventory;
import game.manager.Achievements;
import game.manager.GameMode;
import game.manager.InGameState;
import game.manager.Loading;
import game.manager.Stats;
import game.manager.GameScene;
import game.manager.GameTime;
import game.outsideObj.OutsideObj;
import game.player.Avatar;
import game.player.PlayerState;
import game.player.droid.Droid;
import game.seafloorBase.SeafloorBase;
import game.submarine.Submarine;
import game.world.World;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public final class SaveManager {
   private static String currentSavePath = "";

   public static synchronized void saveGame() {
      if (GameScene.avatar != null) {
         SlotPresentation presentation = new SlotPresentation((int)DepthAtmosphere.getDepthInMeters(), (int)(GameTime.totalPlayTime / 60.0F), (int)(GameTime.dayTime / 60.0F), GameScene.gameMode, GameScene.avatar.getSubmarinePiecesCount());

         try {
            new File(Main.dataPath + "save").mkdir();
            FileOutputStream fos = new FileOutputStream(currentSavePath + ".tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(presentation);
            oos.writeObject(Main.VERSION);
            oos.writeObject(GameScene.gameMode);
            oos.writeObject(GameScene.avatar.getPlayerState());
            oos.writeObject(GameScene.avatar.getInventory());
            oos.writeObject(Loading.getPendingWorld());
            if (GameScene.getSeafloorBases() != null) {
               for (int i = 0; i < GameScene.getSeafloorBases().size(); i++) {
                  oos.writeObject(GameScene.getSeafloorBases().get(i));
               }
            }

            if (GameScene.getOutsideObjects() != null) {
               for (int i = 0; i < GameScene.getOutsideObjects().size(); i++) {
                  oos.writeObject(GameScene.getOutsideObjects().get(i));
               }
            }

            if (GameScene.getSubmarines() != null) {
               for (int i = 0; i < GameScene.getSubmarines().size(); i++) {
                  oos.writeObject(GameScene.getSubmarines().get(i));
               }
            }

            if (GameScene.getSubmarines() != null) {
               for (int i = 0; i < GameScene.getDroids().size(); i++) {
                  oos.writeObject(GameScene.getDroids().get(i));
               }
            }

            if (GameScene.dialogManager != null) {
               oos.writeObject(GameScene.dialogManager);
            }

            if (GameScene.getInGameState() != null) {
               oos.writeObject(GameScene.getInGameState());
            }

            if (GameScene.stats != null) {
               oos.writeObject(GameScene.stats);
            }

            oos.close();
            fos.close();
            File tmpFile = new File(currentSavePath + ".tmp");
            File saveFile = new File(currentSavePath);
            saveFile.delete();
            tmpFile.renameTo(saveFile);
            if (Main.isVerbose) {
               System.out.println("Saved: " + currentSavePath);
            }

            return;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private static void closeStreams(ObjectInputStream ois, FileInputStream fis) {
      try {
         if (ois != null) {
            ois.close();
         }

         if (fis != null) {
            fis.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void loadGame(String path) {
      currentSavePath = path;
      FileInputStream fis = null;
      ObjectInputStream ois = null;
      PlayerInventory inventory = null;
      PlayerState playerState = null;

      try {
         fis = new FileInputStream(currentSavePath);
         ois = new ObjectInputStream(fis);
         GameScene.clear();

         Object obj;
         while ((obj = ois.readObject()) != null) {
            if (obj instanceof GameMode) {
               GameScene.gameMode = (GameMode)obj;
            }

            if (obj instanceof PlayerState) {
               playerState = (PlayerState)obj;
            }

            if (obj instanceof PlayerInventory) {
               inventory = (PlayerInventory)obj;
            }

            if (obj instanceof World) {
               Loading.loadGame((World)obj);
            }

            if (obj instanceof SeafloorBase) {
               GameScene.registerSeafloorBase((SeafloorBase)obj);
            }

            if (obj instanceof OutsideObj) {
               GameScene.registerOutsideObject((OutsideObj)obj);
            }

            if (obj instanceof Submarine) {
               GameScene.registerSubmarine((Submarine)obj);
            }

            if (obj instanceof Droid) {
               GameScene.registerDroid((Droid)obj);
            }

            if (obj instanceof DialogManager) {
               GameScene.registerDialogManager((DialogManager)obj);
            }

            if (obj instanceof InGameState) {
               GameScene.setInitialState((InGameState)obj);
            }

            if (obj instanceof Stats) {
               GameScene.registerStats((Stats)obj);
            }
         }
      } catch (EOFException e) {
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }

      closeStreams(ois, fis);

      GameScene.registerAvatar(new Avatar(playerState, inventory));
      if (Main.isVerbose) {
         System.out.println("Loaded: " + currentSavePath);
      }
   }

   public static SlotPresentation readSlotPresentation(String path) {
      SlotPresentation slot = null;

      try {
         FileInputStream fis = new FileInputStream(path);
         ObjectInputStream ois = new ObjectInputStream(fis);
         slot = (SlotPresentation)ois.readObject();
         Object obj = ois.readObject();
         if (obj instanceof String && ((String)obj).contains("Beta")) {
            slot = null;
         }

         ois.close();
         fis.close();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }

      return slot;
   }

   public static void generateSavePath() {
      currentSavePath = Main.dataPath + "save/farsky" + System.currentTimeMillis() / 1000L + ".sav";
      if (Main.isVerbose) {
         System.out.println(currentSavePath);
      }
   }

   public static void deleteSave() {
      new File(currentSavePath).delete();
   }

   public static void deleteSave(String path) {
      new File(path).delete();
   }

   public static void saveOptions(ArrayList<String> keys, ArrayList<String> values) {
      try {
         FileOutputStream fos = new FileOutputStream(Main.dataPath + "options.sav.tmp");
         ObjectOutputStream oos = new ObjectOutputStream(fos);

         for (int i = 0; i < keys.size(); i++) {
            oos.writeObject(keys.get(i));
            oos.writeObject(values.get(i));
         }

         oos.close();
         fos.close();
         File tmpFile = new File(Main.dataPath + "options.sav.tmp");
         File optFile = new File(Main.dataPath + "options.sav");
         optFile.delete();
         tmpFile.renameTo(optFile);
         if (Main.isVerbose) {
            System.out.println("Options saved: " + Main.dataPath + "options.sav");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void loadOptions() {
      FileInputStream fis = null;
      ObjectInputStream ois = null;

      try {
         if (new File(Main.dataPath + "options.sav").exists()) {
            fis = new FileInputStream(Main.dataPath + "options.sav");
            ois = new ObjectInputStream(fis);

            Object obj;
            while ((obj = ois.readObject()) != null) {
               String key = (String)obj;
               obj = ois.readObject();
               if (obj != null) {
                  OptionsMenu.applySetting(key, (String)obj);
               }
            }
         }
      } catch (EOFException e) {
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }

      closeStreams(ois, fis);

      if (Main.isVerbose) {
         System.out.println("Loaded Options: " + Main.dataPath + "options.sav");
      }
   }

   public static void saveAchievements() {
      try {
         FileOutputStream fos = new FileOutputStream(Main.dataPath + "achievement.lck.tmp");
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         oos.writeObject(Main.achievements);
         oos.close();
         fos.close();
         File tmpFile = new File(Main.dataPath + "achievement.lck.tmp");
         File achievFile = new File(Main.dataPath + "achievement.lck");
         achievFile.delete();
         tmpFile.renameTo(achievFile);
         if (Main.isVerbose) {
            System.out.println("Achievement saved: " + Main.dataPath + "achievement.lck");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void loadAchievements() {
      FileInputStream fis = null;
      ObjectInputStream ois = null;

      try {
         if (new File(Main.dataPath + "achievement.lck").exists()) {
            fis = new FileInputStream(Main.dataPath + "achievement.lck");
            ois = new ObjectInputStream(fis);

            Object obj;
            while ((obj = ois.readObject()) != null) {
               if (obj instanceof GameMode && (GameMode)obj == GameMode.ADVENTURE) {
                  Main.achievements.unlockSurvivor();
               }

               if (obj instanceof Achievements) {
                  Main.achievements = (Achievements)obj;
               }
            }
         }
      } catch (EOFException e) {
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }

      closeStreams(ois, fis);

      Main.achievements.updateSurvivorLock();
      if (Main.isVerbose) {
         System.out.println("Read achievement: " + Main.dataPath + "achievement.lck");
      }
   }
}
