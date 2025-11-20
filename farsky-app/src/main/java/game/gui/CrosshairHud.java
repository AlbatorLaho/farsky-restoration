package game.gui;

import game.inventory.Item;
import game.inventory.Storage;
import game.inventory.types.Inventory;
import game.manager.GameScene;
import game.manager.TextureManager;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class CrosshairHud {
   private static float interactionRingTimer = 0.5F;
   public static Inventory targetInventory;

   public static void update(float delta) {
      if (GameScene.avatar != null && GameScene.avatar.getJumpCharges() == 5) {
         if (interactionRingTimer < 0.5F) {
            interactionRingTimer += delta;
            return;
         }
      } else {
         interactionRingTimer = 0.0F;
      }
   }

   public static void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F, Display.getHeight() / 2, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.sight);
      renderQuad(16.0F);
      if (GameScene.avatar != null && interactionRingTimer < 0.5F) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.stamina);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.25F);

         for (int i = 5 - GameScene.avatar.getJumpCharges(); i < 5; i++) {
            float angle = i * 360 / 5;
            GL11.glRotatef(angle, 0.0F, 0.0F, 1.0F);
            renderQuad(38.0F);
            GL11.glRotatef(-angle, 0.0F, 0.0F, 1.0F);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (targetInventory != null) {
         Item[] items = new Item[4];
         int itemCount = collectTargetItems(items);

         GL11.glPushMatrix();
         GL11.glTranslatef(-18.1F * (itemCount - 1) - 3.0F, 50.0F, 0.0F);

         for (int i = 0; i < itemCount; i++) {
            if (items[i] != null) {
               Storage.renderItem(items[i].getType(), 0.6F, false);
               GL11.glTranslatef(40.0F, 0.0F, 0.0F);
            }
         }

         GL11.glPopMatrix();
      }

      GL11.glPopMatrix();
   }

   private static int collectTargetItems(Item[] items) {
      int count = 0;
      for (int row = 0; row < targetInventory.getStorageArray().getHeight(); row++) {
         for (int col = 0; col < targetInventory.getStorageArray().getWidth(); col++) {
            if (targetInventory.getStorageArray().get(col, row).getItem() != null) {
               items[count++] = targetInventory.getStorageArray().get(col, row).getItem();
               if (count == 4) return count;
            }
         }
      }
      return count;
   }

   private static void renderQuad(float size) {
      size /= 2.0F;
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-size, -size);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-size, size);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(size, size);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(size, -size);
      GL11.glEnd();
   }
}
