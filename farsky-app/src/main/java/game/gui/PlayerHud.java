package game.gui;

import game.Main;
import game.environment.DepthAtmosphere;
import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.Storage;
import game.manager.GameScene;
import game.manager.GameState;
import game.manager.GameTime;
import game.manager.TextureManager;
import game.player.damage.Damage;
import game.player.weapons.HarpoonGun;
import game.sounds.ChunkLayer;
import game.sounds.SoundManager;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;
import game.util.Point;
import game.util.UnitQuad;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class PlayerHud {
   private static float hitFlashTimer = 0.0F;
   public static float hungerIconTimer = 0.0F;
   private static float coinDisplayTimer = 0.0F;
   private static int coinBonus = 0;
   private static float airWarningFlashTimer = 0.0F;
   private static float airWarningFlashDuration = 10.0F;
   private static boolean lowAirWarned = false;
   private static boolean halfAirWarned = false;
   private static ArrayList<ItemType> pickupItems = new ArrayList<>();
   private static ArrayList<Integer> pickupAmounts = new ArrayList<>();
   private static ArrayList<Float> pickupTimers = new ArrayList<>();
   private static ArrayList<Damage> damageEvents = new ArrayList<>();
   private static ArrayList<Coord> damagePositions = new ArrayList<>();
   private static ArrayList<Float> damageAngles = new ArrayList<>();
   private static ArrayList<Float> damageTimers = new ArrayList<>();

   public static void update(float delta) {
      if (Main.getGameState() == GameState.PLAYING) {
         if (hitFlashTimer > 0.0F) {
            hitFlashTimer -= delta;
         } else {
            hitFlashTimer = 0.0F;
         }

         for (int i = 0; i < damageTimers.size(); i++) {
            damageTimers.set(i, damageTimers.get(i) - delta);
            if (damageTimers.get(i) <= 0.0F) {
               damageTimers.remove(i);
               damageEvents.remove(i);
               damagePositions.remove(i);
               damageAngles.remove(i);
            }
         }

         if (hungerIconTimer > 0.0F) {
            hungerIconTimer -= delta;
         }

         for (int i = pickupTimers.size() - 1; i >= 0; i--) {
            pickupTimers.set(i, pickupTimers.get(i) - delta);
            if (pickupTimers.get(i) <= 0.0F) {
               pickupTimers.remove(i);
               pickupAmounts.remove(i);
               pickupItems.remove(i);
            }
         }

         if (coinDisplayTimer > 0.0F) {
            coinDisplayTimer -= delta;
         } else {
            coinDisplayTimer = 0.0F;
         }

         if (airWarningFlashTimer > 0.0F) {
            airWarningFlashTimer -= delta;
            return;
         }

         airWarningFlashTimer = 0.0F;
      }
   }

   public static void renderStatusHud() {
      GL11.glPushMatrix();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.avatarGuiBackground);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2i(0, 0);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2i(0, 119);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2i(80, 119);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2i(80, 0);
      GL11.glEnd();
      GL11.glTranslatef(60.0F, 25.0F, 0.0F);
      GL11.glScalef(0.95F, 0.95F, 0.95F);
      int skillTexture = TextureManager.skillArmor;
      float healthRatio = Math.min(GameScene.avatar.getHealth() / GameScene.avatar.getMaxOxygen(), 1.0F);
      Point barColor = new Point(0.6F, 0.0F, 0.0F);
      float flashBrightness = 1.0F;
      if (healthRatio < 0.2F) {
         flashBrightness = (float)Math.cos(GameTime.elapsedMillis / 88.0F) / 2.0F + 0.5F;
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.avatarGuiBackgroundBar);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 30.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(290.0F, 30.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(290.0F, 0.0F);
      GL11.glEnd();
      GL11.glColor3f(barColor.x * flashBrightness, barColor.y * flashBrightness, barColor.z * flashBrightness);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.avatarGuiColorBar);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 30.0F);
      GL11.glTexCoord2f(healthRatio * 0.9F + 0.1F, 1.0F);
      GL11.glVertex2f(259.0F * healthRatio + 31.0F, 30.0F);
      GL11.glTexCoord2f(healthRatio * 0.9F + 0.1F, 0.0F);
      GL11.glVertex2f(259.0F * healthRatio + 31.0F, 0.0F);
      GL11.glEnd();
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.avatarGuiBarEffect);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 30.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(290.0F, 30.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(290.0F, 0.0F);
      GL11.glEnd();
      GL11.glPushMatrix();
      GL11.glTranslatef(-15.0F, -3.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.skillBackground);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2i(0, 0);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2i(0, 32);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2i(32, 32);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2i(32, 0);
      GL11.glEnd();
      GL11.glColor3f(flashBrightness, flashBrightness, flashBrightness);
      GL11.glTranslatef(7.0F, 7.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, skillTexture);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2i(0, 0);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2i(0, 18);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2i(18, 18);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2i(18, 0);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glScalef(20F / 19F, 20F / 19F, 20F / 19F);
      GL11.glTranslatef(0.0F, 10.0F, 0.0F);
      if (GameScene.avatar.getInventoryYOffset() > DepthAtmosphere.getDepthInMeters()) {
         float depthFlash = (float)Math.cos(GameTime.elapsedMillis / 88.0F) / 2.0F + 0.5F;
         GL11.glColor3f(depthFlash, depthFlash, depthFlash);
      } else {
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
      }

      GL11.glTranslatef(-40.0F, 32.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.avatarGuiDepth);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 45.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(157.0F, 45.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(157.0F, 0.0F);
      GL11.glEnd();
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      FontRenderer.draw(15, 6, "Max Depth:" + GameScene.avatar.getInventoryYOffset() + "m", 0.5F);
      FontRenderer.restoreFontFamily();
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }

   public static void render() {
      if (GameScene.avatar.getSelectedItem() != null && GameScene.avatar.getSelectedItem().getType() == ItemType.SPEARGUN && HarpoonGun.getAmmoCount() > 0) {
         GL11.glPushMatrix();
         GL11.glTranslatef(Display.getWidth() - 38.5F, Display.getHeight() - 42.0F - 5.0F, 0.0F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.items);
         new Item(HarpoonGun.loadedAmmo.getType()).draw();
         FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
         FontRenderer.draw(-38 - FontRenderer.getTextWidth("" + HarpoonGun.getAmmoCount(), 0.8F), -21, "" + HarpoonGun.getAmmoCount(), 0.8F);
         GL11.glPopMatrix();
      }

      if (hitFlashTimer > 0.0F) {
         renderHitFlash();
      }

      if (damageTimers.size() > 0) {
         renderDamageNumbers();
      }

      renderAirBar();
      if (GameScene.avatar.hasWoundedArm() || GameScene.avatar.hasWoundedLeg() || GameScene.avatar.getHunger() < 0.4F || hungerIconTimer > 0.0F) {
         float pulse = (float)Math.cos(GameTime.elapsedMillis / 100.0F) * 0.5F + 1.0F;
         GL11.glPushMatrix();
         GL11.glTranslatef(130.0F, 220.0F, 0.0F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.avatarPortrait);
         renderStatusQuad();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, pulse);
         if (GameScene.avatar.hasWoundedArm()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.woundedArm);
            renderStatusQuad();
         }

         if (GameScene.avatar.hasWoundedLeg()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.woundedLeg);
            renderStatusQuad();
         }

         if (GameScene.avatar.getHunger() < 0.4F || hungerIconTimer > 0.0F) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.starving);
            if (GameScene.avatar.getHunger() < 0.2F) {
               GL11.glColor4f(0.7F, 0.0F, 0.0F, pulse);
            } else if (GameScene.avatar.getHunger() < 0.4F) {
               GL11.glColor4f(0.7F, 0.5F, 0.0F, pulse);
            } else {
               GL11.glColor4f(0.1F, 0.8F, 0.1F, 1.0F);
            }

            renderStatusQuad();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
            FontRenderer.drawCentered(0, 80, "Hunger:" + (int)((1.0F - GameScene.avatar.getHunger()) * 100.0F) + "%", 0.5F);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPopMatrix();
      }

      renderPickupNotifications();
      Item selectedItem = GameScene.avatar.getSelectedItem();
      if (selectedItem != null && selectedItem.getType() == ItemType.UNDERWATER_SCOOTER) {
         String batteryText = "Battery: " + (int)(selectedItem.getParam() * 100.0F) + "%";
         Point textColor = new Point(1.0F, 1.0F, 1.0F);
         GL11.glPushMatrix();
         GL11.glTranslatef(Display.getWidth() / 2, Display.getHeight() - 150, 0.0F);
         GL11.glTranslatef(-ChunkLayer.getLabelWidth(batteryText, 0.5F, 5) / 2.0F, 0.0F, 0.0F);
         ChunkLayer.drawLabel(batteryText, 0.5F, 5, 5, 1.0F, textColor);
         GL11.glPopMatrix();
      }

      if (DepthAtmosphere.toMeters(GameScene.avatar.getCameraPos().y) > -50.0F && DepthAtmosphere.toMeters(GameScene.avatar.getCameraPos().y) < 0.0F) {
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 - 80, "Surface in " + -((int)DepthAtmosphere.toMeters(GameScene.avatar.getCameraPos().y)) + "m", 0.7F);
      }

      if (coinDisplayTimer > 0.0F) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, coinDisplayTimer);
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         String coinText = Main.achievements.getMoney() + " (+" + coinBonus + ") bonus coins";
         float coinTextWidth = 54.85F + FontRenderer.getTextWidth(coinText, 0.5F);
         GL11.glPushMatrix();
         GL11.glTranslatef(Display.getWidth() / 2.0F - coinTextWidth / 2.0F, 50.0F, 0.0F);
         FontRenderer.draw(32, -FontRenderer.getCharHeight(0.6F) / 2, coinText, 0.5F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.coins);
         GL11.glScalef(44.85F, -23.4F, 0.0F);
         UnitQuad.render();
         GL11.glPopMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   private static void renderPickupNotifications() {
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);

      for (int i = 0; i < pickupItems.size(); i++) {
         float alpha = pickupTimers.get(i) * pickupTimers.get(i) * 3.0F;
         GL11.glPushMatrix();
         GL11.glTranslatef(Display.getWidth() / 2.0F, Display.getHeight() - 300.0F + pickupTimers.get(i) * 60.0F, 0.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
         Storage.renderItem(pickupItems.get(i), 1.0F);
         String prefix = "";
         Point color = new Point(1.0F, 0.0F, 0.0F);
         if (pickupAmounts.get(i) > 0) {
            prefix = prefix + "+";
            color = new Point(1.0F, 1.0F, 1.0F);
         }

         String labelText = prefix + pickupAmounts.get(i) + " " + (pickupItems.get(i)).getText();
         GL11.glTranslatef(-ChunkLayer.getLabelWidth(labelText, 0.4F, 5) / 2.0F, 30.0F, 0.0F);
         ChunkLayer.drawLabel(labelText, 0.4F, 5, 5, alpha, color);
         GL11.glPopMatrix();
      }
   }

   private static void renderStatusQuad() {
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-64.8F, -86.4F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-64.8F, 86.4F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(64.8F, 86.4F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(64.8F, -86.4F);
      GL11.glEnd();
   }

   private static void renderAirBar() {
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2.0F - 189.0F, Display.getHeight() - 54 - 13, 0.0F);

      for (int layer = 0; layer < 3; layer++) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (layer == 0) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.oxygenBarBackground);
         }

         float fillRatio;
         if (layer == 1) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.oxygenBar);
            fillRatio = GameScene.avatar.getOxygen() / GameScene.avatar.getMaxHealth();
            if (GameScene.avatar.getOxygen() < 60.0F) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)Math.cos(GameTime.elapsedMillis / 150.0F));
            }
         } else {
            fillRatio = 1.0F;
         }

         if (layer == 2) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.oxygenBarForeground);
         }

         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2f(0.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2f(0.0F, 13.0F);
         GL11.glTexCoord2f(fillRatio, 1.0F);
         GL11.glVertex2f(fillRatio * 378.0F, 13.0F);
         GL11.glTexCoord2f(fillRatio, 0.0F);
         GL11.glVertex2f(fillRatio * 378.0F, 0.0F);
         GL11.glEnd();
      }

      float oxygenRatio = GameScene.avatar.getOxygen() / GameScene.avatar.getMaxHealth();
      if (oxygenRatio < 0.25F) {
         if (!lowAirWarned) {
            airWarningFlashTimer = airWarningFlashDuration;
            lowAirWarned = true;
         }
      } else {
         lowAirWarned = false;
      }

      if (oxygenRatio < 0.5F) {
         if (!halfAirWarned) {
            airWarningFlashTimer = airWarningFlashDuration;
            halfAirWarned = true;
         }
      } else {
         halfAirWarned = false;
      }

      if (airWarningFlashTimer > 0.0F) {
         float warningAlpha = Math.min(airWarningFlashTimer, airWarningFlashDuration - airWarningFlashTimer);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, warningAlpha);
         GL11.glTranslatef((int)(oxygenRatio * 378.0F), -17.0F, 0.0F);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.oxygenWarning);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex2i(-57, -19);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex2i(-57, 19);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex2i(57, 19);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex2i(57, -19);
         GL11.glEnd();
         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, warningAlpha);
         int minutes = (int)(GameScene.avatar.getOxygen() / 60.0F);
         int seconds = (int)(GameScene.avatar.getOxygen() - minutes * 60);
         FontRenderer.drawCentered(0, -21, minutes + "min " + seconds + "sec", 0.45F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GL11.glPopMatrix();
   }

   private static void renderHitFlash() {
      GL11.glColor3f(0.0F, 0.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.ink);

      for (byte x = -80; x <= 80; x += 40) {
         for (byte y = -10; y <= 10; y += 20) {
            GL11.glPushMatrix();
            GL11.glTranslatef(Display.getWidth() / 2 + x, Display.getHeight() / 2 + y, 0.0F);
            GL11.glRotatef(x * 0.8F - y * 1.35F, 0.0F, 0.0F, 1.0F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(-100.0F, -100.0F);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(-100.0F, 100.0F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex2f(100.0F, 100.0F);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex2f(100.0F, -100.0F);
            GL11.glEnd();
            GL11.glPopMatrix();
         }
      }

      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      FontRenderer.saveFontFamily();
      FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2 - 5, Display.getHeight() / 2 - 20, 0.0F);
      GL11.glRotatef(-8.0F, 0.0F, 0.0F, 1.0F);
      FontRenderer.drawCentered(0, 0, null, 0.6F);
      GL11.glPopMatrix();
      FontRenderer.restoreFontFamily();
   }

   private static void renderDamageNumbers() {
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2, 0.0F);
      FontRenderer.saveFontFamily();
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);

      for (int i = 0; i < damageEvents.size(); i++) {
         int amount = (int)damageEvents.get(i).getAmount();
         GL11.glRotatef(damageAngles.get(i), 0.0F, 0.0F, 1.0F);
         GL11.glColor3f(0.0F, 0.0F, 0.0F);
         FontRenderer.drawCentered((int)damagePositions.get(i).x, (int)damagePositions.get(i).y - FontRenderer.getCharHeight(0.6F) / 2, "" + amount, 0.65F);
         switch (damageEvents.get(i).getType()) {
            case ELECTRIC:
               GL11.glColor3f(0.8F, 0.8F, 0.8F);
               break;
            case NORMAL:
               GL11.glColor3f(1.0F, 1.0F, 1.0F);
               break;
            case CRITICAL:
               GL11.glColor3f(1.0F, 0.9F, 0.1F);
         }

         FontRenderer.drawCentered((int)damagePositions.get(i).x, (int)damagePositions.get(i).y - FontRenderer.getCharHeight(0.6F) / 2, "" + amount, 0.6F);
         GL11.glRotatef(-damageAngles.get(i), 0.0F, 0.0F, 1.0F);
      }

      FontRenderer.restoreFontFamily();
      GL11.glPopMatrix();
   }

   public static void addPickupNotification(ItemType itemType, int amount) {
      pickupItems.add(itemType);
      pickupAmounts.add(amount);
      pickupTimers.add(1.5F);
   }

   public static void showCoinBonus(int bonus) {
      coinDisplayTimer = 3.0F;
      coinBonus = bonus;
   }

   public static void addDamageEvent(Damage damage) {
      switch (damage.getType()) {
         case ELECTRIC:
            SoundManager.playSound(SoundManager.sfxHurtFloor, null, 0.8F + (float)Math.random() * 0.1F, 0.2F);
            break;
         case NORMAL:
            SoundManager.playSound(SoundManager.sfxTouched, null, 0.8F + (float)Math.random() * 0.1F, 0.2F);
            break;
         case CRITICAL:
            SoundManager.playSound(SoundManager.sfxTouched, null, 1.2F + (float)Math.random() * 0.1F, 0.2F);
      }

      damageEvents.add(damage);
      damageTimers.add(0.7F);
      damagePositions.add(new Coord((Math.random() - 0.5) * 100.0, (Math.random() - 0.5) * 100.0));
      damageAngles.add(((float)Math.random() - 0.5F) * 30.0F);
   }
}
