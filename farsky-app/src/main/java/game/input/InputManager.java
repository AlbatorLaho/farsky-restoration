package game.input;

import game.Main;
import game.chunks.Chunk;
import game.chunks.ChunkManager;
import game.cinematic.Cinematic;
import game.gui.menu.MenuState;
import game.inventory.InventoryHud;
import game.gui.menu.MenuController;
import game.manager.GameState;
import game.manager.RenderManager;
import game.map.MapRenderer;
import game.manager.Camera;
import game.manager.GameScene;
import game.player.Avatar;
import game.player.droid.Droid;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.submarine.Submarine;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class InputManager {
   private static int keyForward;
   private static int keyBack;
   private static int keyStrafeLeft;
   private static int keyStrafeRight;
   private static int keyAscend;
   private static int keyDescend;
   private static int keyEsc;
   private static int keyLeftArrow;
   private static int keyRightArrow;
   private static int keyUpArrow;
   private static int keyDownArrow;
   private static int keyP;
   private static int keyF1;
   private static int keyE;
   private static int keyF2;
   private static int keyMap;
   private static int keyL;
   private static int keyF3;
   private static int keyF4;
   private static int keyF5;
   private static int keyInventory;
   private static int keyInteract;
   private static int keyF6;
   private static int keyF7;
   private static int keyF8;
   private static int keySlot0;
   private static int keySlot1;
   private static int keySlot2;
   private static int keySlot3;
   private static int keySlot4;
   private static int keySlot5;
   private static int keySlot6;
   private static int keySlot7;
   private static int keySwitchWeapon;
   private static int keyReload;
   private static GameState prevStateBeforePause;
   private static ArrayList<Integer> registeredKeys = new ArrayList<>();
   private static ArrayList<KeyState> keyStates = new ArrayList<>();
   private static int konamiStep = 0;
   private static float freeCamRoll = 0.0F;

   public static void init() {
      if (Main.isRelease) {
         keyForward = 17;
         keyBack = 31;
         keyStrafeLeft = 30;
         keyStrafeRight = 32;
         keySwitchWeapon = 16;
      } else {
         keyForward = 44;
         keyBack = 31;
         keyStrafeLeft = 16;
         keyStrafeRight = 32;
         keySwitchWeapon = 30;
      }

      keyAscend = 57;
      keyDescend = 42;
      keyEsc = 1;
      keyLeftArrow = 203;
      keyRightArrow = 205;
      keyUpArrow = 200;
      keyDownArrow = 208;
      keyP = 25;
      keyE = 18;
      keyL = 38;
      keyInventory = 15;
      keyInteract = 18;
      keyF1 = 59;
      keyF2 = 60;
      keyF3 = 61;
      keyF4 = 62;
      keyF5 = 63;
      keyF6 = 64;
      keyF7 = 65;
      keyF8 = 66;
      keySlot0 = 2;
      keySlot1 = 3;
      keySlot2 = 4;
      keySlot3 = 5;
      keySlot4 = 6;
      keySlot5 = 7;
      keySlot6 = 8;
      keySlot7 = 9;
      keyReload = 19;
      keyMap = 50;
      rebuildKeyLists();
   }

   private static void rebuildKeyLists() {
      registeredKeys.clear();
      keyStates.clear();
      registeredKeys.add(keyForward);
      registeredKeys.add(keyBack);
      registeredKeys.add(keyStrafeLeft);
      registeredKeys.add(keyStrafeRight);
      registeredKeys.add(keyAscend);
      registeredKeys.add(keyDescend);
      registeredKeys.add(keyEsc);
      registeredKeys.add(keyLeftArrow);
      registeredKeys.add(keyRightArrow);
      registeredKeys.add(keyUpArrow);
      registeredKeys.add(keyDownArrow);
      registeredKeys.add(keyP);
      registeredKeys.add(keyF1);
      registeredKeys.add(keyE);
      registeredKeys.add(keyF2);
      registeredKeys.add(keyL);
      registeredKeys.add(keyF3);
      registeredKeys.add(keyF4);
      registeredKeys.add(keyF5);
      registeredKeys.add(keyInventory);
      registeredKeys.add(keyInteract);
      registeredKeys.add(keyF6);
      registeredKeys.add(keyF7);
      registeredKeys.add(keyF8);
      registeredKeys.add(keySlot0);
      registeredKeys.add(keySlot1);
      registeredKeys.add(keySlot2);
      registeredKeys.add(keySlot3);
      registeredKeys.add(keySlot4);
      registeredKeys.add(keySlot5);
      registeredKeys.add(keySlot6);
      registeredKeys.add(keySlot7);
      registeredKeys.add(keySwitchWeapon);
      registeredKeys.add(keyReload);
      registeredKeys.add(keyMap);

      for (int i = 0; i < registeredKeys.size(); i++) {
         keyStates.add(KeyState.UP);
      }
   }

   public static void update(float deltaTime) {
      RawInput.update(deltaTime);

      for (int idx = 0; idx < registeredKeys.size(); idx++) {
         if (RawInput.keys[registeredKeys.get(idx)]) {
            if (keyStates.get(idx) == KeyState.UP) {
               keyStates.set(idx, KeyState.JUST_PRESSED);
            } else if (keyStates.get(idx) == KeyState.JUST_PRESSED) {
               keyStates.set(idx, KeyState.HELD);
            }
         } else if (keyStates.get(idx) != KeyState.JUST_PRESSED && keyStates.get(idx) != KeyState.HELD) {
            keyStates.set(idx, KeyState.UP);
         } else {
            keyStates.set(idx, KeyState.JUST_RELEASED);
         }
      }

      switch (Main.getGameState()) {
         case MAP:
            MapRenderer.mapInput.moveForward = isKey(keyForward, KeyState.HELD);
            MapRenderer.mapInput.moveBackward = isKey(keyBack, KeyState.HELD);
            MapRenderer.mapInput.strafeLeft = isKey(keyStrafeLeft, KeyState.HELD);
            MapRenderer.mapInput.strafeRight = isKey(keyStrafeRight, KeyState.HELD);
            MapRenderer.scroll(RawInput.scrollDelta * 10.0F);
            RawInput.mouseCaptured = false;
            if (isKey(keyEsc, KeyState.JUST_PRESSED) || isKey(keyMap, KeyState.JUST_PRESSED) || isKey(keyInteract, KeyState.JUST_PRESSED)) {
               Main.gameState = GameState.PLAYING;
            }

            if (Main.hasStateChanged()) {
               SoundManager.pauseAll();
            }
            break;
         case INVENTORY:
            RawInput.mouseCaptured = false;
            if (isKey(keyEsc, KeyState.JUST_PRESSED) || isKey(keyInventory, KeyState.JUST_PRESSED) || isKey(keyInteract, KeyState.JUST_PRESSED)) {
               Main.gameState = GameState.PLAYING;
            }

            if (Main.hasStateChanged()) {
               SoundManager.pauseAll();
            }
            break;
         case PLAYING:
            if (!RenderManager.freeCam && GameScene.avatar != null) {
               Submarine sub = GameScene.getActiveSubmarine();
               if (GameScene.avatar.isNavigating() && sub != null) {
                  sub.input.moveForward = isKey(keyForward, KeyState.HELD);
                  sub.input.moveBackward = isKey(keyBack, KeyState.HELD);
                  sub.input.strafeLeft = isKey(keyStrafeLeft, KeyState.HELD);
                  sub.input.strafeRight = isKey(keyStrafeRight, KeyState.HELD);
                  sub.input.ascend = isKey(keyAscend, KeyState.HELD);
                  sub.input.descend = isKey(keyDescend, KeyState.HELD);
               }

               GameScene.avatar.input.moveForward = isKey(keyForward, KeyState.HELD) && !GameScene.avatar.isNavigating();
               GameScene.avatar.input.moveBackward = isKey(keyBack, KeyState.HELD) && !GameScene.avatar.isNavigating();
               GameScene.avatar.input.strafeLeft = isKey(keyStrafeLeft, KeyState.HELD) && !GameScene.avatar.isNavigating();
               GameScene.avatar.input.strafeRight = isKey(keyStrafeRight, KeyState.HELD) && !GameScene.avatar.isNavigating();
               GameScene.avatar.input.ascend = isKey(keyAscend, KeyState.HELD) && !GameScene.avatar.isNavigating();
               GameScene.avatar.input.descend = isKey(keyDescend, KeyState.HELD);
               GameScene.avatar.input.primaryMouseDown = RawInput.leftMouseDown;
               GameScene.avatar.input.primaryMouseHeld = RawInput.leftMouseHeld && (GameScene.avatar.input.primaryMouseDown || GameScene.avatar.input.primaryMouseHeld);
               GameScene.avatar.input.secondaryMouseDown = RawInput.rightMouseDown;
               GameScene.avatar.input.secondaryMouseHeld = RawInput.rightMouseHeld && (GameScene.avatar.input.secondaryMouseDown || GameScene.avatar.input.secondaryMouseHeld);
               GameScene.avatar.input.interact = isKey(keyInteract, KeyState.JUST_PRESSED);
               GameScene.avatar.input.slot0 = isKey(keySlot0, KeyState.JUST_PRESSED);
               GameScene.avatar.input.slot1 = isKey(keySlot1, KeyState.JUST_PRESSED);
               GameScene.avatar.input.slot2 = isKey(keySlot2, KeyState.JUST_PRESSED);
               GameScene.avatar.input.slot3 = isKey(keySlot3, KeyState.JUST_PRESSED);
               GameScene.avatar.input.slot4 = isKey(keySlot4, KeyState.JUST_PRESSED);
               GameScene.avatar.input.slot5 = isKey(keySlot5, KeyState.JUST_PRESSED);
               GameScene.avatar.input.slot6 = isKey(keySlot6, KeyState.JUST_PRESSED);
               GameScene.avatar.input.slot7 = isKey(keySlot7, KeyState.JUST_PRESSED);
               isKey(keySwitchWeapon, KeyState.JUST_PRESSED);
               isKey(keyReload, KeyState.JUST_PRESSED);
            } else {
               Camera.input.moveForward = isKey(keyForward, KeyState.HELD);
               Camera.input.moveBackward = isKey(keyBack, KeyState.HELD);
               Camera.input.strafeLeft = isKey(keyStrafeLeft, KeyState.HELD);
               Camera.input.strafeRight = isKey(keyStrafeRight, KeyState.HELD);
               Camera.input.ascend = isKey(keyAscend, KeyState.HELD);
               Camera.input.descend = isKey(keyDescend, KeyState.HELD);
               Camera.input.primaryMouseHeld = RawInput.leftMouseHeld;
               Camera.speedMultiplier = Camera.speedMultiplier + RawInput.scrollDelta * 0.05F;
               if (RawInput.leftMouseHeld) {
                  freeCamRoll += deltaTime * 10.0F;
               }

               if (RawInput.rightMouseHeld) {
                  freeCamRoll -= deltaTime * 10.0F;
               }

               Camera.setRoll(freeCamRoll);
            }

            RawInput.mouseCaptured = true;
            if (isKey(keyInventory, KeyState.JUST_PRESSED)) {
               InventoryHud.setHoveredItem(null);
               Main.gameState = GameState.INVENTORY;
            }

            if (isKey(keyMap, KeyState.JUST_PRESSED)) {
               Main.gameState = GameState.MAP;
            }

            if (isKey(keyEsc, KeyState.JUST_PRESSED)) {
               prevStateBeforePause = Main.getGameState();
               Main.gameState = GameState.PAUSED;
               MenuController.currentMenuState = MenuState.PAUSE;
            }

            if (Main.hasStateChanged()) {
               SoundManager.resumeAll();
            }
            break;
         case CINEMATIC_INGAME:
            RawInput.mouseCaptured = true;
            Cinematic.skipCredits = isKey(keyInteract, KeyState.JUST_PRESSED);
         case CINEMATIC_INTRO:
            if (isKey(keyEsc, KeyState.JUST_PRESSED) || isKey(keyAscend, KeyState.JUST_PRESSED)) {
               Cinematic.skipAll();
            }
            break;
         case MAIN_MENU:
            RawInput.mouseCaptured = false;
            if (konamiStep == 7) {
               if (isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep++;
               } else if (isKey(keyForward, KeyState.JUST_PRESSED) || isKey(keyBack, KeyState.JUST_PRESSED) || isKey(keyStrafeLeft, KeyState.JUST_PRESSED) || isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep = 0;
               }
            }

            if (konamiStep == 6) {
               if (isKey(keyStrafeLeft, KeyState.JUST_PRESSED)) {
                  konamiStep++;
               } else if (isKey(keyForward, KeyState.JUST_PRESSED) || isKey(keyBack, KeyState.JUST_PRESSED) || isKey(keyStrafeLeft, KeyState.JUST_PRESSED) || isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep = 0;
               }
            }

            if (konamiStep == 5) {
               if (isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep++;
               } else if (isKey(keyForward, KeyState.JUST_PRESSED) || isKey(keyBack, KeyState.JUST_PRESSED) || isKey(keyStrafeLeft, KeyState.JUST_PRESSED) || isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep = 0;
               }
            }

            if (konamiStep == 4) {
               if (isKey(keyStrafeLeft, KeyState.JUST_PRESSED)) {
                  konamiStep++;
               } else if (isKey(keyForward, KeyState.JUST_PRESSED) || isKey(keyBack, KeyState.JUST_PRESSED) || isKey(keyStrafeLeft, KeyState.JUST_PRESSED) || isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep = 0;
               }
            }

            if (konamiStep == 3) {
               if (isKey(keyBack, KeyState.JUST_PRESSED)) {
                  konamiStep++;
               } else if (isKey(keyForward, KeyState.JUST_PRESSED) || isKey(keyBack, KeyState.JUST_PRESSED) || isKey(keyStrafeLeft, KeyState.JUST_PRESSED) || isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep = 0;
               }
            }

            if (konamiStep == 2) {
               if (isKey(keyBack, KeyState.JUST_PRESSED)) {
                  konamiStep++;
               } else if (isKey(keyForward, KeyState.JUST_PRESSED) || isKey(keyBack, KeyState.JUST_PRESSED) || isKey(keyStrafeLeft, KeyState.JUST_PRESSED) || isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep = 0;
               }
            }

            if (konamiStep == 1) {
               if (isKey(keyForward, KeyState.JUST_PRESSED)) {
                  konamiStep++;
               } else if (isKey(keyForward, KeyState.JUST_PRESSED) || isKey(keyBack, KeyState.JUST_PRESSED) || isKey(keyStrafeLeft, KeyState.JUST_PRESSED) || isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep = 0;
               }
            }

            if (konamiStep == 0) {
               if (isKey(keyForward, KeyState.JUST_PRESSED)) {
                  konamiStep++;
               } else if (isKey(keyForward, KeyState.JUST_PRESSED) || isKey(keyBack, KeyState.JUST_PRESSED) || isKey(keyStrafeLeft, KeyState.JUST_PRESSED) || isKey(keyStrafeRight, KeyState.JUST_PRESSED)) {
                  konamiStep = 0;
               }
            }

            if (konamiStep == 8) {
               Main.achievements.addMoney(45000);
               konamiStep = 0;
            }
            break;
         case PAUSED:
            RawInput.mouseCaptured = false;
            if (isKey(keyEsc, KeyState.JUST_PRESSED)) {
               Main.gameState = prevStateBeforePause;
            }

            if (Main.hasStateChanged()) {
               SoundManager.pauseAll();
            }
            break;
         case STARTUP:
            RawInput.mouseCaptured = false;
            break;
         case LOADING_MENU:
            RawInput.mouseCaptured = false;
            if (Main.hasStateChanged()) {
               SoundManager.pauseAll();
            }
            break;
         case RELOADING:
            RawInput.mouseCaptured = false;
            break;
         case LOADING_GAME:
            RawInput.mouseCaptured = false;
      }

      if (isKey(keyF1, KeyState.JUST_PRESSED)) {
         Main.isDebug = !Main.isDebug;
      }

      if (!Main.isRelease) {
         if (isKey(keyF2, KeyState.JUST_PRESSED)) {
            Mouse.setGrabbed(false);
         }

         if (isKey(keyF3, KeyState.JUST_PRESSED)) {
            Shaders.testShaderEnabled = !Shaders.testShaderEnabled;
         }

         if (isKey(keyF4, KeyState.JUST_PRESSED)) {
            Chunk.debugBounds = !Chunk.debugBounds;
         }

         if (isKey(keyF5, KeyState.JUST_PRESSED)) {
            GameScene.droids.add(new Droid(GameScene.avatar.getCameraPos()));
         }

         if (isKey(keyF6, KeyState.JUST_PRESSED)) {
            RenderManager.freeCam = !RenderManager.freeCam;
         }

         if (isKey(keyF7, KeyState.JUST_PRESSED) && Main.isDebug) {
            Avatar.godMode = !Avatar.godMode;
         }

         if (isKey(keyF8, KeyState.JUST_PRESSED)) {
            RenderManager.hideHud = !RenderManager.hideHud;
         }
      }

      if (Main.isDebug) {
         switch (Main.getGameState()) {
            case PLAYING:
               if (isKey(keyL, KeyState.JUST_PRESSED)) {
                  ChunkManager.disposeAll();
               }
			default:
				break;
         }
      }

      if (Main.hasStateChanged() || !RawInput.mouseCaptured) {
         Mouse.setGrabbed(RawInput.mouseCaptured);
      }
   }

   public static int getKeyCode(String action) {
      if (action.equals("Forward")) {
         return keyForward;
      } else if (action.equals("Back")) {
         return keyBack;
      } else if (action.equals("Left")) {
         return keyStrafeLeft;
      } else if (action.equals("Right")) {
         return keyStrafeRight;
      } else if (action.equals("Jump / Go up")) {
         return keyAscend;
      } else if (action.equals("Go down")) {
         return keyDescend;
      } else if (action.equals("Interaction")) {
         return keyInteract;
      } else if (action.equals("Map")) {
         return keyMap;
      } else if (action.equals("Inventory")) {
         return keyInventory;
      } else {
         return action.equals("Switch Weapon") ? keySwitchWeapon : -1;
      }
   }

   public static String getKeyName(String action) {
      return Keyboard.getKeyName(getKeyCode(action));
   }

   public static void rebindKey(String action, int keyCode) {
      if (action.equals("Forward")) {
         keyForward = keyCode;
      }

      if (action.equals("Back")) {
         keyBack = keyCode;
      }

      if (action.equals("Left")) {
         keyStrafeLeft = keyCode;
      }

      if (action.equals("Right")) {
         keyStrafeRight = keyCode;
      }

      if (action.equals("Jump / Go up")) {
         keyAscend = keyCode;
      }

      if (action.equals("Go down")) {
         keyDescend = keyCode;
      }

      if (action.equals("Interaction")) {
         keyInteract = keyCode;
      }

      if (action.equals("Inventory")) {
         keyInventory = keyCode;
      }

      if (action.equals("Map")) {
         keyMap = keyCode;
      }

      if (action.equals("Switch Weapon")) {
         keySwitchWeapon = keyCode;
      }

      rebuildKeyLists();
   }

   private static boolean isKey(int keyCode, KeyState state) {
      int idx = registeredKeys.indexOf(keyCode);
      return idx != -1 && keyStates.get(idx) == state;
   }

   public static boolean isRightArrow(KeyState state) {
      int idx = registeredKeys.indexOf(keyRightArrow);
      return idx != -1 && keyStates.get(idx) == state;
   }

   public static boolean isLeftArrow(KeyState state) {
      int idx = registeredKeys.indexOf(keyLeftArrow);
      return idx != -1 && keyStates.get(idx) == state;
   }
}
