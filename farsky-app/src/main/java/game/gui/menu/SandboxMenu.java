package game.gui.menu;
import game.enemy.EnemyGenerator;

import game.Main;
import game.gui.util.Button;
import game.gui.util.ButtonType;
import game.input.RawInput;
import game.inventory.InventoryHud;
import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.Storage;
import game.manager.GameMode;
import game.manager.GameScene;
import game.manager.GameState;
import game.manager.GameTime;
import game.manager.Loading;
import game.manager.TextureManager;
import game.saving.SaveManager;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;
import game.util.Point;
import game.util.UnitQuad;
import game.world.gen.SeedInput;

import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class SandboxMenu extends MenuScreen {
   private static int PANEL_X = 250;
   private static int ITEM_SPACING_Y = 40;
   private static int PANEL_WIDTH = 430;
   private static int PANEL_HEIGHT = 110;
   private static float scrollPosition = 0.0F;
   private static int targetScroll = 0;
   private static int hoveredItemIndex = -1;
   private static int PANEL_Y = PANEL_X + (ITEM_SPACING_Y << 2) + 10;
   private static int ITEMS_OFFSET_Y = 5;
   private static int ITEM_WIDTH = 59;
   private static int ARROW_WIDTH = 60;
   private static boolean leftArrowHovered = false;
   private static boolean rightArrowHovered = false;
   public static ArrayList<Item> startingItems;

   protected SandboxMenu() {
      this.refreshLayout();
      startingItems = new ArrayList<>();
      startingItems.add(new Item(ItemType.FLOOR, 0));
      startingItems.add(new Item(ItemType.GLASS_WALL, 0));
      startingItems.add(new Item(ItemType.POTATO, 0));
      startingItems.add(new Item(ItemType.COOKED_POTATO, 0));
      startingItems.add(new Item(ItemType.LAMP, 0));
      startingItems.add(new Item(ItemType.EXTRACTOR, 0));
      startingItems.add(new Item(ItemType.UNDERWATER_SCOOTER, 0));
      startingItems.add(new Item(ItemType.IRON_DIVING_HELMET, 0));
      startingItems.add(new Item(ItemType.IRON_DIVING_SUIT, 0));
      startingItems.add(new Item(ItemType.IRON_DIVING_CYLINDER, 0));
      startingItems.add(new Item(ItemType.OVERPOWERED_EXTRACTOR, 0));
      startingItems.add(new Item(ItemType.COPPER_DIVING_HELMET, 0));
      startingItems.add(new Item(ItemType.COPPER_DIVING_SUIT, 0));
      startingItems.add(new Item(ItemType.COPPER_DIVING_CYLINDER, 0));
      startingItems.add(new Item(ItemType.OVERPOWERED_DRILL, 0));
      startingItems.add(new Item(ItemType.MANGANESE_DIVING_HELMET, 0));
      startingItems.add(new Item(ItemType.MANGANESE_DIVING_SUIT, 0));
      startingItems.add(new Item(ItemType.MANGANESE_DIVING_CYLINDER, 0));
      startingItems.add(new Item(ItemType.DROID, 0));
      startingItems.add(new Item(ItemType.NEW_SUBMARINE, 0));
   }

   @Override
   protected final void draw() {
      for (int i = 0; i < this.buttons.size(); i++) {
         this.buttons.get(i).render();
      }

      Shaders.guiEffectShader.bind();
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
      Button.drawBackground(Display.getWidth() / 2 - PANEL_WIDTH / 2, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FontRenderer.drawGradient(Display.getWidth() / 2 - 150, PANEL_Y - 5, "Starting equipment", 0.55F, new Point(0.0F, 0.0F, 0.0F), 1.0F, new Point(0.5F, 0.5F, 0.5F), 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2 + 50, PANEL_Y + 9.0F + 5.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.coins);
      GL11.glScalef(34.5F, -18.0F, 0.0F);
      UnitQuad.render();
      GL11.glPopMatrix();
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.8F);
      FontRenderer.draw(Display.getWidth() / 2 + 70, PANEL_Y, "" + Main.achievements.getMoney(), 0.46F);
      this.renderItems();
      if (leftArrowHovered) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
      } else {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.17F);
      }

      Button.drawBackground(Display.getWidth() / 2 - PANEL_WIDTH / 2, PANEL_Y, ARROW_WIDTH, PANEL_HEIGHT);
      if (rightArrowHovered) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
      } else {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.17F);
      }

      Button.drawBackground(Display.getWidth() / 2 + PANEL_WIDTH / 2 - ARROW_WIDTH, PANEL_Y, ARROW_WIDTH, PANEL_HEIGHT);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.saveArrow);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2 - PANEL_WIDTH / 2 + 30.0F, PANEL_Y + PANEL_HEIGHT / 2, 0.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-30.0F, -38.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-30.0F, 38.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(30.0F, 38.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(30.0F, -38.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2 + PANEL_WIDTH / 2 - 30.0F, PANEL_Y + PANEL_HEIGHT / 2, 0.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(30.0F, -38.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(30.0F, 38.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(-30.0F, 38.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(-30.0F, -38.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (hoveredItemIndex >= 0 && hoveredItemIndex < startingItems.size()) {
         InventoryHud.renderTooltip(startingItems.get(hoveredItemIndex), new Coord(RawInput.mouseX, RawInput.mouseY - 30), 0.7F);
      }

      Shaders.unbind();
   }

   private void renderItems() {
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2, PANEL_Y + ITEMS_OFFSET_Y, 0.0F);
      GL11.glTranslatef(-ITEM_WIDTH * scrollPosition, 54.0F, 0.0F);
      Storage storage = new Storage(0, 0);

      for (int i = 0; i < startingItems.size(); i++) {
         float alpha = Math.min(2.5F - Math.abs(i - scrollPosition) * 0.8F, 1.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
         if (i == hoveredItemIndex) {
            Shaders.setUniform("additiveLight", 0.35F);
         } else {
            Shaders.setUniform("additiveLight", 0.0);
         }

         storage.setItem(startingItems.get(i));
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.items);
         if (startingItems.get(i).getCount() > 0) {
            Shaders.setUniform("blackAndWhite", false);
            if (i != hoveredItemIndex) {
               Shaders.setUniform("additiveLight", Math.cos(GameTime.elapsedMillis / 400.0F + i) * 0.05F + 0.05F);
            }

            storage.render(true);
            storage.renderCount(alpha);
         } else {
            Shaders.setUniform("blackAndWhite", true);
            storage.render(false);
         }

         FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha * 0.6F);
         FontRenderer.drawCentered(0, 25, ""  + getItemCost(startingItems.get(i).getType()), 0.4F);
         GL11.glTranslatef(ITEM_WIDTH, 0.0F, 0.0F);
      }

      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Shaders.setUniform("blackAndWhite", false);
      Shaders.setUniform("additiveLight", 0.0);
   }

   @Override
   protected final void update(float delta) {
      leftArrowHovered = false;
      rightArrowHovered = false;
      hoveredItemIndex = -1;

      for (int i = 0; i < this.buttons.size(); i++) {
         this.buttons.get(i).update(delta);
      }

      this.checkClicks();
      if (SeedInput.isComplete()) {
         this.findButton("Seed").setSelected(false);
      }

      if (RawInput.mouseX > Display.getWidth() / 2 - PANEL_WIDTH / 2
         && RawInput.mouseX < Display.getWidth() / 2 - PANEL_WIDTH / 2 + ARROW_WIDTH
         && RawInput.mouseY > PANEL_Y
         && RawInput.mouseY < PANEL_Y + PANEL_HEIGHT) {
         leftArrowHovered = true;
         if (RawInput.leftMouseDown && targetScroll > 0) {
            targetScroll -= 4;
         }
      } else if (RawInput.mouseX > Display.getWidth() / 2 + PANEL_WIDTH / 2 - ARROW_WIDTH
         && RawInput.mouseX < Display.getWidth() / 2 + PANEL_WIDTH / 2
         && RawInput.mouseY > PANEL_Y
         && RawInput.mouseY < PANEL_Y + PANEL_HEIGHT) {
         rightArrowHovered = true;
         if (RawInput.leftMouseDown && targetScroll < startingItems.size() - 1) {
            targetScroll += 4;
         }
      } else {
         hoveredItemIndex = RawInput.mouseX - Display.getWidth() / 2 + 27 + (int)(ITEM_WIDTH * scrollPosition);
         if (RawInput.mouseX > Display.getWidth() / 2 - PANEL_WIDTH / 2
            && RawInput.mouseX < Display.getWidth() / 2 + PANEL_WIDTH / 2
            && RawInput.mouseY > PANEL_Y
            && RawInput.mouseY < PANEL_Y + PANEL_HEIGHT
            && hoveredItemIndex >= 0) {
            hoveredItemIndex = hoveredItemIndex / ITEM_WIDTH;
         } else {
            hoveredItemIndex = -1;
         }

         if (RawInput.leftMouseDown && hoveredItemIndex >= 0 && hoveredItemIndex < startingItems.size() && (startingItems.get(hoveredItemIndex).getCount() == 0 || startingItems.get(hoveredItemIndex).getType().isStackable()) && Main.achievements.spendMoney(getItemCost(startingItems.get(hoveredItemIndex).getType()))) {
            startingItems.get(hoveredItemIndex).setCount(startingItems.get(hoveredItemIndex).getCount() + 1);
            SoundManager.playSound(SoundManager.sfxMoneySpent, null, (float)(Math.random() - 0.5) * 0.3F + 1.0F);
         }
      }

      if (scrollPosition < targetScroll) {
         scrollPosition += delta * 15.0F;
      }

      if (scrollPosition > targetScroll) {
         scrollPosition -= delta * 15.0F;
      }
   }

   @Override
   protected final void onButtonClicked(Button button) {
      this.findButton("Seed").setSelected(false);
      this.findButton("Predator Spawn").setSelected(false);
      this.findButton("Day Time").setSelected(false);
      this.findButton("Night Time").setSelected(false);
      if (button.hasLabel("Play")) {
         GameScene.gameMode = GameMode.SANDBOX;
         SaveManager.generateSavePath();
         float dayTime = this.getTimeMinutes("Day Time");
         float nightTime = this.getTimeMinutes("Night Time");
         String spawningLabel = "Predator Spawn";
         EnemyGenerator.SpawningLevel spawningLevel = EnemyGenerator.SpawningLevel.NORMAL;
         if (spawningLabel.equals("Predator Spawn")) {
            if (this.findButton(spawningLabel).getSelectedOption().equals("Never")) {
               spawningLevel = EnemyGenerator.SpawningLevel.NEVER;
            }

            if (this.findButton(spawningLabel).getSelectedOption().equals("Low")) {
               spawningLevel = EnemyGenerator.SpawningLevel.LOW;
            }

            if (this.findButton(spawningLabel).getSelectedOption().equals("Normal")) {
               spawningLevel = EnemyGenerator.SpawningLevel.NORMAL;
            }

            if (this.findButton(spawningLabel).getSelectedOption().equals("High")) {
               spawningLevel = EnemyGenerator.SpawningLevel.HIGH;
            }
         }

         Loading.newWorld(dayTime, nightTime, spawningLevel);
         Main.gameState = GameState.LOADING_GAME;
      } else if (!button.hasLabel("Cancel")) {
         if (button.hasLabel("Seed")) {
            button.setSelected(true);
            SeedInput.reset();
         } else {
            button.setSelected(true);
         }
      } else {
         for (int i = 0; i < startingItems.size(); i++) {
            Main.achievements.addMoney(startingItems.get(i).getCount() * getItemCost(startingItems.get(i).getType()));
            startingItems.get(i).setCount(0);
         }

         MenuController.currentMenuState = MenuState.MAIN;
      }
   }

   private static int getItemCost(ItemType itemType) {
      switch (itemType) {
         case FLOOR:
            return 15;
         case GLASS_WALL:
            return 15;
         case POTATO:
            return 20;
         case COOKED_POTATO:
            return 30;
         case NEW_SUBMARINE:
            return 1500;
         case DROID:
            return 1500;
         case IRON_DIVING_HELMET:
            return 420;
         case IRON_DIVING_SUIT:
            return 420;
         case IRON_DIVING_CYLINDER:
            return 420;
         case COPPER_DIVING_HELMET:
            return 840;
         case COPPER_DIVING_SUIT:
            return 840;
         case COPPER_DIVING_CYLINDER:
            return 840;
         case MANGANESE_DIVING_HELMET:
            return 1200;
         case MANGANESE_DIVING_SUIT:
            return 1200;
         case MANGANESE_DIVING_CYLINDER:
            return 1200;
         case EXTRACTOR:
            return 360;
         case OVERPOWERED_EXTRACTOR:
            return 720;
         case OVERPOWERED_DRILL:
            return 900;
         case UNDERWATER_SCOOTER:
            return 360;
         case LAMP:
            return 30;
         default:
            return 0;
      }
   }

   @Override
   public final void refreshLayout() {
      this.buttons.clear();
      this.buttons.add(new Button(ButtonType.TEXT_INPUT, 0, "Seed"));
      this.buttons.get(this.buttons.size() - 1).setPosition(Display.getWidth() / 2 - 150, PANEL_X);
      this.buttons.add(new Button(ButtonType.TOGGLE, 1, "Predator Spawn"));
      this.buttons.get(this.buttons.size() - 1).setPosition(Display.getWidth() / 2 - 150, PANEL_X + ITEM_SPACING_Y);
      this.findButton("Predator Spawn").addOption("Never");
      this.findButton("Predator Spawn").addOption("Low");
      this.findButton("Predator Spawn").addOption("Normal");
      this.findButton("Predator Spawn").addOption("High");
      this.findButton("Predator Spawn").selectOption("Normal");
      this.buttons.add(new Button(ButtonType.TOGGLE, 2, "Day Time"));
      this.buttons.get(this.buttons.size() - 1).setPosition(Display.getWidth() / 2 - 150, PANEL_X + (ITEM_SPACING_Y << 1));
      this.findButton("Day Time").addOption("2 min");
      this.findButton("Day Time").addOption("3 min");
      this.findButton("Day Time").addOption("5 min");
      this.findButton("Day Time").addOption("7 min");
      this.findButton("Day Time").addOption("10 min");
      this.findButton("Day Time").addOption("15 min");
      this.findButton("Day Time").addOption("20 min");
      this.findButton("Day Time").addOption("30 min");
      this.findButton("Day Time").addOption("40 min");
      this.findButton("Day Time").addOption("60 min");
      this.findButton("Day Time").selectOption("10 min");
      this.buttons.add(new Button(ButtonType.TOGGLE, 3, "Night Time"));
      this.buttons.get(this.buttons.size() - 1).setPosition(Display.getWidth() / 2 - 150, PANEL_X + ITEM_SPACING_Y * 3);
      this.findButton("Night Time").addOption("None");
      this.findButton("Night Time").addOption("2 min");
      this.findButton("Night Time").addOption("3 min");
      this.findButton("Night Time").addOption("5 min");
      this.findButton("Night Time").addOption("7 min");
      this.findButton("Night Time").addOption("10 min");
      this.findButton("Night Time").addOption("15 min");
      this.findButton("Night Time").addOption("20 min");
      this.findButton("Night Time").addOption("30 min");
      this.findButton("Night Time").addOption("40 min");
      this.findButton("Night Time").addOption("60 min");
      this.findButton("Night Time").selectOption("7 min");
      this.buttons.add(new Button("Play", Display.getWidth() / 2 - 125, Display.getHeight() - 100, ButtonType.ACTION_BUTTON));
      this.buttons.add(new Button("Cancel", Display.getWidth() / 2 + 125, Display.getHeight() - 100, ButtonType.ACTION_BUTTON));
   }

   private float getTimeMinutes(String buttonName) {
      float minutes = 0.0F;
      if (buttonName.equals("Night Time") || buttonName.equals("Day Time")) {
         if (this.findButton(buttonName).getSelectedOption().equals("None")) {
            minutes = 0.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("2 min")) {
            minutes = 2.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("3 min")) {
            minutes = 3.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("5 min")) {
            minutes = 5.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("7 min")) {
            minutes = 7.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("10 min")) {
            minutes = 10.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("15 min")) {
            minutes = 15.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("20 min")) {
            minutes = 20.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("30 min")) {
            minutes = 30.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("40 min")) {
            minutes = 40.0F;
         }

         if (this.findButton(buttonName).getSelectedOption().equals("60 min")) {
            minutes = 60.0F;
         }
      }

      return minutes;
   }
}
