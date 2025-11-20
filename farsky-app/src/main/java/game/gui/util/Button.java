package game.gui.util;

import game.input.InputManager;
import game.input.KeyState;
import game.input.RawInput;
import game.manager.TextureManager;
import game.sounds.SoundManager;
import game.util.FontFamily;
import game.util.FontRenderer;
import game.util.Point;
import game.world.gen.SeedInput;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public final class Button {
   private int x;
   private int y;
   private int textWidth;
   private int textHeight;
   private Rect bounds;
   private String label;
   private int index;
   private boolean selected = false;
   private float animTimer = 0.0F;
   private boolean hovered = false;
   private boolean prevHovered = false;
   private boolean enabled = true;
   private FontFamily fontFamily = FontFamily.CHAPARRAL;
   private ButtonType type;
   private Button parentTab = null;
   private ArrayList<String> options;
   private int selectedOptionIndex = 0;
   private boolean keyHandled = false;

   public Button(ButtonType type, int index, String label, Button parentTab) {
      this(type, index, label);
      this.parentTab = parentTab;
   }

   public Button(ButtonType type, int index, String label) {
      this.textWidth = FontRenderer.getTextWidth(label, 1.0F);
      this.textHeight = FontRenderer.getCharHeight(1.0F);
      switch (type) {
         case TOGGLE:
            this.options = new ArrayList<>();
         default:
            this.type = type;
            this.label = label;
            this.index = index;
            if (type == ButtonType.TAB && index == 0) {
               this.selected = true;
            }

            this.layout();
      }
   }

   public Button(String label, int x, int y, ButtonType type) {
      this.type = type;
      this.label = label;
      this.x = x;
      this.y = y;
      this.layout();
   }

   public Button(String label, int x, int y, float width, float height, FontFamily fontFamily) {
      this.type = ButtonType.TEXT_LABEL;
      this.label = label;
      this.fontFamily = fontFamily;
      this.x = x;
      this.y = y;
      this.initLayout(350.0F, height);
   }

   public final void update(float delta) {
      if (this.enabled) {
         if (this.isClicked()) {
            SoundManager.playSound(SoundManager.sfxClick, null, 1.0F, 0.2F);
         }

         if (this.parentTab != null && !this.parentTab.selected) {
            this.selected = false;
         }

         if (this.parentTab == null || this.parentTab.selected) {
            int pressedKey = RawInput.getFirstPressedKey();
            int mouseY = RawInput.mouseY;
            int mouseX = RawInput.mouseX;
            Rect rect = this.bounds;
            this.hovered = mouseX > rect.x && mouseX < rect.x + rect.width && mouseY > rect.y && mouseY < rect.y + rect.height;
            if (this.hovered && !this.prevHovered) {
               SoundManager.playSound(SoundManager.sfxHover, null, 1.0F, 0.2F);
            }

            if (!this.selected) {
               this.animTimer = 0.0F;
            } else {
               switch (this.type) {
                  case TOGGLE:
                     float arrowX = this.x + this.bounds.width - 160.0F;
                     this.animTimer += delta;
                     if (this.animTimer > 1.0F) {
                        this.animTimer--;
                     }

                     if (InputManager.isLeftArrow(KeyState.JUST_PRESSED)) {
                        this.selectedOptionIndex--;
                     }

                     if (InputManager.isRightArrow(KeyState.JUST_PRESSED)) {
                        this.selectedOptionIndex++;
                     }

                     if (this.isClicked()) {
                        if (RawInput.mouseX > arrowX && RawInput.mouseX < arrowX + 50.0F) {
                           this.selectedOptionIndex--;
                        } else {
                           this.selectedOptionIndex++;
                        }
                     }

                     if (this.selectedOptionIndex < 0) {
                        this.selectedOptionIndex = this.options.size() - 1;
                     }

                     if (this.selectedOptionIndex > this.options.size() - 1) {
                        this.selectedOptionIndex = 0;
                     }
                     break;
                  case KEY_BINDING:
                     this.animTimer += delta;
                     if (this.animTimer > 1.0F) {
                        this.animTimer--;
                     }

                     if (pressedKey != -1) {
                        if (pressedKey != 1) {
                           InputManager.rebindKey(this.label, pressedKey);
                        }

                        this.selected = false;
                     }
                     break;
                  case TEXT_INPUT:
                     this.animTimer += delta;
                     if (this.animTimer > 1.0F) {
                        this.animTimer--;
                     }

                     if (pressedKey != -1) {
                        if (!this.keyHandled) {
                           SeedInput.processKey(pressedKey);
                        }

                        this.keyHandled = true;
                     } else {
                        this.keyHandled = false;
                     }
				default:
					break;
               }
            }
         }

         this.prevHovered = this.hovered;
      }
   }

   public final void render() {
      switch (this.type) {
         case TOGGLE:
         case KEY_BINDING:
         case TEXT_INPUT:
            if (this.parentTab != null && !this.parentTab.selected) {
               break;
            }

            byte selectedOffset = 0;
            float width = this.bounds.width;
            if (this.selected) {
               selectedOffset = 7;
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
            } else if (this.hovered) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            } else {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
            }

            drawBackground(this.x - 5.0F + selectedOffset, this.y + 10.0F, width, 30.0F);
            float optionX = this.x + width + selectedOffset - 140.0F;
            float optionY = this.y + 15.0F;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.optionElement);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(optionX, optionY);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(optionX, optionY + 20.0F);
            GL11.glTexCoord2f(0.5F, 1.0F);
            GL11.glVertex2f(optionX + 32.0F, optionY + 20.0F);
            GL11.glTexCoord2f(0.5F, 0.0F);
            GL11.glVertex2f(optionX + 32.0F, optionY);
            GL11.glTexCoord2f(0.45F, 0.0F);
            GL11.glVertex2f(optionX + 32.0F, optionY);
            GL11.glTexCoord2f(0.45F, 1.0F);
            GL11.glVertex2f(optionX + 32.0F, optionY + 20.0F);
            GL11.glTexCoord2f(0.55F, 1.0F);
            GL11.glVertex2f(optionX + 120.0F - 32.0F, optionY + 20.0F);
            GL11.glTexCoord2f(0.55F, 0.0F);
            GL11.glVertex2f(optionX + 120.0F - 32.0F, optionY);
            GL11.glTexCoord2f(0.5F, 0.0F);
            GL11.glVertex2f(optionX + 120.0F - 32.0F, optionY);
            GL11.glTexCoord2f(0.5F, 1.0F);
            GL11.glVertex2f(optionX + 120.0F - 32.0F, optionY + 20.0F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex2f(optionX + 120.0F, optionY + 20.0F);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex2f(optionX + 120.0F, optionY);
            GL11.glEnd();
            if (this.type == ButtonType.TOGGLE && this.selected) {
               float arrowX = this.x + width + selectedOffset - 138.0F;
               float arrowY = this.y + 15.0F;
               float arrowBounce = 12.0F + Math.abs(this.animTimer - 0.5F) * 15.0F;
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.optionArrow);
               GL11.glBegin(GL11.GL_QUADS);
               GL11.glTexCoord2f(0.0F, 0.0F);
               GL11.glVertex2f(arrowX - arrowBounce, arrowY);
               GL11.glTexCoord2f(0.0F, 1.0F);
               GL11.glVertex2f(arrowX - arrowBounce, arrowY + 20.0F);
               GL11.glTexCoord2f(1.0F, 1.0F);
               GL11.glVertex2f(arrowX + 32.0F - arrowBounce, arrowY + 20.0F);
               GL11.glTexCoord2f(1.0F, 0.0F);
               GL11.glVertex2f(arrowX + 32.0F - arrowBounce, arrowY);
               GL11.glEnd();
               GL11.glBegin(GL11.GL_QUADS);
               GL11.glTexCoord2f(1.0F, 0.0F);
               GL11.glVertex2f(arrowX + 115.0F - 32.0F + arrowBounce, arrowY);
               GL11.glTexCoord2f(1.0F, 1.0F);
               GL11.glVertex2f(arrowX + 115.0F - 32.0F + arrowBounce, arrowY + 20.0F);
               GL11.glTexCoord2f(0.0F, 1.0F);
               GL11.glVertex2f(arrowX + 115.0F + arrowBounce, arrowY + 20.0F);
               GL11.glTexCoord2f(0.0F, 0.0F);
               GL11.glVertex2f(arrowX + 115.0F + arrowBounce, arrowY);
               GL11.glEnd();
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            FontRenderer.saveFontFamily();
            FontRenderer.setFontFamily(this.fontFamily);
            FontRenderer.drawGradient(this.x + selectedOffset, this.y + 6, this.label, 0.55F, new Point(0.0F, 0.0F, 0.0F), 1.0F, new Point(0.5F, 0.5F, 0.5F), 1.0F);
            int keyCode = InputManager.getKeyCode(this.label);
            if (this.type == ButtonType.KEY_BINDING && keyCode != -1) {
               if (this.selected && this.animTimer < 0.5F) {
                  FontRenderer.drawCentered((int)(this.x + width + selectedOffset - 65.0F - 16.0F), this.y + 8, "_", 0.5F);
               } else {
                  FontRenderer.drawCentered((int)(this.x + width + selectedOffset - 65.0F - 16.0F), this.y + 8, Keyboard.getKeyName(keyCode), 0.5F);
               }
            }

            if (this.type == ButtonType.TEXT_INPUT) {
               if (this.selected && this.animTimer < 0.5F) {
                  FontRenderer.drawCentered((int)(this.x + width + selectedOffset - 65.0F + 8.0F), this.y + 8, "_", 0.5F);
               }

               FontRenderer.draw(
                  (int)(this.x + width + selectedOffset - 65.0F - 16.0F) - FontRenderer.getTextWidth("" + SeedInput.getSeed(), 0.5F) + 27, this.y + 8, "" + SeedInput.getSeed(), 0.5F
               );
            }

            if (this.type == ButtonType.TOGGLE) {
               FontRenderer.drawCentered((int)(this.x + width + selectedOffset - 65.0F - 16.0F), this.y + 8, this.options.get(this.selectedOptionIndex), 0.5F);
            }

            FontRenderer.restoreFontFamily();
            return;
         case MENU_BUTTON:
            if (this.hovered) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            } else {
               GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.8F);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(-50.0F, this.y + 22, 0.0F);
            GL11.glRotatef(-7.0F, 0.0F, 0.0F, 1.0F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.menuButton);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.995F, 1.0F);
            GL11.glVertex2i(250, 55);
            GL11.glTexCoord2f(0.995F, 0.0F);
            GL11.glVertex2i(250, 0);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2i(0, 0);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2i(0, 55);
            GL11.glEnd();
            GL11.glPopMatrix();
            if (this.enabled) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            }

            FontRenderer.saveFontFamily();
            FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
            GL11.glPushMatrix();
            GL11.glTranslatef(this.x, this.y, 0.0F);
            GL11.glRotatef(-7.0F, 0.0F, 0.0F, 1.0F);
            FontRenderer.drawGradient(85 - FontRenderer.getTextWidth(this.label, 1.0F), 0, this.label, 1.0F, new Point(0.4F, 0.4F, 0.4F), 1.0F, new Point(1.0F, 1.0F, 1.0F), 1.0F);
            GL11.glPopMatrix();
            FontRenderer.restoreFontFamily();
            return;
         case TAB:
            FontRenderer.saveFontFamily();
            FontRenderer.setFontFamily(FontFamily.CHAPARRAL);
            float tabAlpha;
            if (this.selected) {
               tabAlpha = 1.0F;
            } else if (this.hovered) {
               tabAlpha = 0.8F;
            } else {
               tabAlpha = 0.5F;
            }

            FontRenderer.drawGradient(this.x, this.y + 5, this.label, 0.8F, new Point(0.5F, 0.5F, 0.5F), tabAlpha, new Point(1.0F, 1.0F, 1.0F), tabAlpha);
            FontRenderer.restoreFontFamily();
            return;
         case TEXT_LABEL:
            if (this.hovered) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
            } else {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            }

            if (!this.enabled) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.1F);
            }

            drawBackground(this.x - this.bounds.width / 2, this.y + 10.0F, this.bounds.width, this.bounds.height);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (!this.enabled) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
            }

            FontRenderer.saveFontFamily();
            FontRenderer.setFontFamily(this.fontFamily);
            FontRenderer.drawCenteredGradient(this.x - 5, this.y + 5, this.label, 0.65F, new Point(0.0F, 0.0F, 0.0F), 1.0F, new Point(0.5F, 0.5F, 0.5F), 1.0F);
            FontRenderer.restoreFontFamily();
            return;
         case ACTION_BUTTON:
            if (this.hovered) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            } else {
               GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.9F);
            }

            if (!this.enabled) {
               GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.2F);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(this.x - 93 - 4, this.y + 6, 0.0F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.menuButton);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.995F, 1.0F);
            GL11.glVertex2f(187.0F, 41.0F);
            GL11.glTexCoord2f(0.995F, 0.0F);
            GL11.glVertex2f(187.0F, 0.0F);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(0.0F, 41.0F);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (!this.enabled) {
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
            }

            FontRenderer.saveFontFamily();
            FontRenderer.setFontFamily(this.fontFamily);
            FontRenderer.drawCenteredGradient(this.x - 5, this.y + 5, this.label, 0.65F, new Point(0.35F, 0.35F, 0.35F), 1.0F, new Point(1.0F, 1.0F, 1.0F), 1.0F);
            FontRenderer.restoreFontFamily();
      }
   }

   public static void renderPanel() {
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() / 2 - 400, Display.getHeight() / 2 - 140.0F, 0.0F);
      drawRect(new Color(255, 255, 255, 255), 150.0F, 500.0F, 1.0F);
      GL11.glTranslatef(0.0F, 2.0F, 0.0F);
      drawRect(new Color(0, 0, 0, 125), 150.0F, 500.0F, 40.0F);
      GL11.glTranslatef(0.0F, 40.0F, 0.0F);
      drawRect(new Color(255, 255, 255, 255), 150.0F, 500.0F, 1.0F);
      GL11.glPopMatrix();
   }

   private static void drawRect(Color color, float fadeWidth, float solidWidth, float height) {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glColor4f(color.getRed() / 255.0F, color.getBlue() / 255.0F, color.getGreen() / 255.0F, 0.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glColor4f(color.getRed() / 255.0F, color.getBlue() / 255.0F, color.getGreen() / 255.0F, color.getAlpha() / 255.0F);
      GL11.glVertex2f(150.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glColor4f(color.getRed() / 255.0F, color.getBlue() / 255.0F, color.getGreen() / 255.0F, color.getAlpha() / 255.0F);
      GL11.glVertex2f(150.0F, height);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glColor4f(color.getRed() / 255.0F, color.getBlue() / 255.0F, color.getGreen() / 255.0F, 0.0F);
      GL11.glVertex2f(0.0F, height);
      GL11.glColor4f(color.getRed() / 255.0F, color.getBlue() / 255.0F, color.getGreen() / 255.0F, color.getAlpha() / 255.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(150.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(650.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(650.0F, height);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(150.0F, height);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glColor4f(color.getRed() / 255.0F, color.getBlue() / 255.0F, color.getGreen() / 255.0F, color.getAlpha() / 255.0F);
      GL11.glVertex2f(650.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glColor4f(color.getRed() / 255.0F, color.getBlue() / 255.0F, color.getGreen() / 255.0F, 0.0F);
      GL11.glVertex2f(800.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glColor4f(color.getRed() / 255.0F, color.getBlue() / 255.0F, color.getGreen() / 255.0F, 0.0F);
      GL11.glVertex2f(800.0F, height);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glColor4f(color.getRed() / 255.0F, color.getBlue() / 255.0F, color.getGreen() / 255.0F, color.getAlpha() / 255.0F);
      GL11.glVertex2f(650.0F, height);
      GL11.glEnd();
   }

   public static void drawBackground(float x, float y, float width, float height) {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glVertex2f(x, y);
      GL11.glVertex2f(x + width, y);
      GL11.glVertex2f(x + width, y + height);
      GL11.glVertex2f(x, y + height);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
      GL11.glLineWidth(1.0F);
      GL11.glBegin(GL11.GL_LINES);
      GL11.glVertex2f(x, y);
      GL11.glVertex2f(x + width, y);
      GL11.glVertex2f(x + width, y);
      GL11.glVertex2f(x + width, y + height);
      GL11.glVertex2f(x + width, y + height);
      GL11.glVertex2f(x, y + height);
      GL11.glVertex2f(x, y + height);
      GL11.glVertex2f(x, y);
      GL11.glEnd();
   }

   public final void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public final void layout() {
      this.initLayout(200.0F, 30.0F);
   }

   private void initLayout(float width, float height) {
      switch (this.type) {
         case TOGGLE:
         case KEY_BINDING:
         case TEXT_INPUT:
            if (this.index % 2 == 0) {
               this.setPosition(Display.getWidth() / 2 - 300, Display.getHeight() / 2 - 90 + 35 * (this.index / 2));
            } else {
               this.setPosition(Display.getWidth() / 2 + 10, Display.getHeight() / 2 - 90 + 35 * (this.index / 2));
            }

            this.bounds = new Rect(this.x - 5.0F, this.y + 10.0F, 300.0F, 30.0F);
            return;
         case MENU_BUTTON:
            this.setPosition(95, Display.getHeight() / 2 - 160 + 80 * this.index);
            this.bounds = new Rect(this.x - 150.0F, this.y + 5, 250.0F, this.textHeight - 10.0F);
            return;
         case TAB:
            this.setPosition(Display.getWidth() / 2 - 300 + 250 * this.index, Display.getHeight() / 2 - 150);
            this.bounds = new Rect(this.x - 10.0F, this.y + 5, this.textWidth + 10.0F, this.textHeight - 10.0F);
            return;
         case TEXT_LABEL:
         case ACTION_BUTTON:
            this.y -= 15;
            this.bounds = new Rect(this.x - width / 2.0F, this.y + 10.0F, width, height);
      }
   }

   public final void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
      switch (this.type) {
         case TOGGLE:
         case KEY_BINDING:
         case TEXT_INPUT:
            this.bounds = new Rect(x - 5.0F, y + 10.0F, 300.0F, 30.0F);
         default:
            return;
         case MENU_BUTTON:
            this.bounds = new Rect(x - 10.0F, y + 5, this.textWidth + 10.0F, this.textHeight - 10.0F);
            return;
         case TAB:
            this.bounds = new Rect(x - 10.0F, y + 5, this.textWidth + 10.0F, this.textHeight - 10.0F);
      }
   }

   public final void addOption(String option) {
      this.options.add(option);
   }

   public final void selectOption(String option) {
      for (int i = 0; i < this.options.size(); i++) {
         if (this.options.get(i).equals(option)) {
            this.selectedOptionIndex = i;
         }
      }
   }

   public final String getSelectedOption() {
      return this.selectedOptionIndex > this.options.size() ? "" : this.options.get(this.selectedOptionIndex);
   }

   public final void setSelected(boolean selected) {
      this.selected = selected;
   }

   public final boolean hasLabel(String label) {
      return this.label.equals(label);
   }

   public final boolean isClicked() {
      return this.hovered && this.enabled && RawInput.leftMouseDown;
   }
}
