package game.util;

import java.util.ArrayList;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public final class DisplayModes {
   private static DisplayMode fullscreenMode;
   private static ArrayList<DisplayMode> availableModes = new ArrayList<>();
   private static DisplayMode currentMode = null;

   public static void init() {
      int maxPixels = 0;

      try {
         DisplayMode[] modes = Display.getAvailableDisplayModes();

         for (int i = 0; i < modes.length; i++) {
            if (modes[i].isFullscreenCapable()
               && modes[i].getFrequency() == Display.getDesktopDisplayMode().getFrequency()
               && modes[i].getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) {
               if (modes[i].getWidth() * modes[i].getHeight() >= maxPixels) {
                  maxPixels = modes[i].getWidth() * modes[i].getHeight();
                  fullscreenMode = modes[i];
               }

               availableModes.add(modes[i]);
            }
         }
      } catch (LWJGLException e) {
         e.printStackTrace();
      }

      for (int i = 0; i < availableModes.size(); i++) {
         for (int j = 0; j < availableModes.size() - 1; j++) {
            if (availableModes.get(j).getWidth() * availableModes.get(j).getHeight()
               > availableModes.get(j + 1).getWidth() * availableModes.get(j + 1).getHeight()) {
               DisplayMode temp = availableModes.get(j);
               availableModes.set(j, availableModes.get(j + 1));
               availableModes.set(j + 1, temp);
            }
         }
      }

      for (int i = 0; i < availableModes.size(); i++) {
         for (int j = 0; j < availableModes.size() - 1; j++) {
            if (availableModes.get(j).getWidth() > availableModes.get(j + 1).getWidth()) {
               DisplayMode temp = availableModes.get(j);
               availableModes.set(j, availableModes.get(j + 1));
               availableModes.set(j + 1, temp);
            }
         }
      }
   }

   public static ArrayList<DisplayMode> getAvailableModes() {
      return availableModes;
   }

   public static DisplayMode getCurrentMode() {
      return currentMode != null ? currentMode : new DisplayMode(Display.getWidth(), Display.getHeight());
   }

   public static void switchToFullscreen() {
      applyMode(fullscreenMode);
      setFullscreenEnabled(true);
   }

   public static void enterFullscreen() {
      try {
         Display.setFullscreen(true);
         Display.setDisplayMode(fullscreenMode);
         currentMode = fullscreenMode;
      } catch (LWJGLException e) {
         e.printStackTrace();
      }
   }

   public static void exitFullscreen() {
      try {
         Display.setFullscreen(false);
         Display.setDisplayMode(availableModes.get(availableModes.size() / 4));
         currentMode = availableModes.get(availableModes.size() / 4);
      } catch (LWJGLException e) {
         e.printStackTrace();
      }
   }

   private static void applyMode(DisplayMode mode) {
      if (mode != currentMode) {
         try {
            Display.setDisplayMode(mode);
            GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
            currentMode = mode;
            return;
         } catch (LWJGLException e) {
            e.printStackTrace();
         }
      }
   }

   public static void setFullscreenEnabled(boolean enabled) {
      if (Display.isFullscreen() != enabled) {
         try {
            Display.setFullscreen(enabled);
            return;
         } catch (LWJGLException e) {
            e.printStackTrace();
         }
      }
   }

   public static void setModeByResolution(String resolution) {
      if (resolution.split("x").length == 2) {
         int width = Integer.parseInt(resolution.split("x")[0]);
         int height = Integer.parseInt(resolution.split("x")[1]);

         for (int i = 0; i < availableModes.size(); i++) {
            if (availableModes.get(i).getWidth() == width && availableModes.get(i).getHeight() == height) {
               applyMode(availableModes.get(i));
               return;
            }
         }
      }
   }
}
