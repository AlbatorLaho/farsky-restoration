package game.gui;

import game.inventory.types.Inventory;
import game.manager.GameTime;
import game.sounds.ChunkLayer;
import game.util.FontFamily;
import game.util.FontRenderer;

import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class InteractionHint {
   private static ArrayList<String> sideHints = new ArrayList<>();
   private static String interactionLabel = null;
   private static ArrayList<String> timedHints = new ArrayList<>();
   private static ArrayList<Float> hintTimers = new ArrayList<>();

   public static void render() {
      float pulse = (float)(Math.cos(GameTime.elapsedMillis / 300.0F) / 2.0 + 0.5) + 0.3F;
      GL11.glLineWidth(2.0F);
      FontRenderer.saveFontFamily();
      FontRenderer.setFontFamily(FontFamily.CHAPARRAL);

      for (int i = 0; i < sideHints.size(); i++) {
         GL11.glPushMatrix();
         GL11.glTranslatef(20.0F, 150 + i * 40, 0.0F);
         ChunkLayer.drawLabel(sideHints.get(i), 0.5F, 20, 100, pulse);
         GL11.glPopMatrix();
      }

      FontRenderer.restoreFontFamily();
      String label = null;
      if (timedHints.size() > 0) {
         label = timedHints.get(0);
      } else if (interactionLabel != null) {
         label = interactionLabel;
      }

      if (label != null) {
         FontRenderer.saveFontFamily();
         FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
         GL11.glPushMatrix();
         GL11.glTranslatef(Display.getWidth() / 2.0F - ChunkLayer.getLabelWidth(label, 0.6F, 40) / 2.0F, Display.getHeight() - 150.0F, 0.0F);
         ChunkLayer.drawLabel(label, 0.6F, 40, 100, pulse);
         GL11.glPopMatrix();
         FontRenderer.restoreFontFamily();
      }
   }

   public static void update(float delta) {
      if (timedHints.size() > 0) {
         hintTimers.set(0, hintTimers.get(0) - delta);
         if (hintTimers.get(0) <= 0.0F) {
            timedHints.remove(0);
            hintTimers.remove(0);
         }
      }
   }

   public static void addTimedHint(String text, float duration) {
      if (timedHints.size() < 5) {
         timedHints.add(text);
         hintTimers.add(duration);
      }
   }

   public static void setInteractionTarget(String label, Inventory inventory) {
      interactionLabel = label;
      CrosshairHud.targetInventory = inventory;
   }

   public static void clearAll() {
      sideHints.clear();
      interactionLabel = null;
      CrosshairHud.targetInventory = null;
   }
}
