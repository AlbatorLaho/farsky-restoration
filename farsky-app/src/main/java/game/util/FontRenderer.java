package game.util;

import java.util.Hashtable;
import org.lwjgl.opengl.GL11;

import game.manager.TextureManager;

public final class FontRenderer {
   private static Hashtable<Character, Coord> charUvMap = new Hashtable<>();
   private static Hashtable<Character, Integer> chillerSpacing = new Hashtable<>();
   private static Hashtable<Character, Integer> chaparralSpacing = new Hashtable<>();
   private static Hashtable<Character, Integer> eccentricSpacing = new Hashtable<>();
   private static int tileCols = 16;
   private static int tileRows = 8;
   private static float tileWidth = 512 / tileCols;
   private static float tileHeight = 512 / tileRows;
   private static FontFamily currentFont = FontFamily.CHAPARRAL;
   private static FontFamily savedFont = FontFamily.CHAPARRAL;

   public static void init() {
      charUvMap.put('A', new Coord(0, 0));
      charUvMap.put('B', new Coord(1, 0));
      charUvMap.put('C', new Coord(2, 0));
      charUvMap.put('D', new Coord(3, 0));
      charUvMap.put('E', new Coord(4, 0));
      charUvMap.put('F', new Coord(5, 0));
      charUvMap.put('G', new Coord(6, 0));
      charUvMap.put('H', new Coord(7, 0));
      charUvMap.put('I', new Coord(8, 0));
      charUvMap.put('J', new Coord(9, 0));
      charUvMap.put('K', new Coord(10, 0));
      charUvMap.put('L', new Coord(11, 0));
      charUvMap.put('M', new Coord(12, 0));
      charUvMap.put('N', new Coord(13, 0));
      charUvMap.put('O', new Coord(14, 0));
      charUvMap.put('P', new Coord(15, 0));
      charUvMap.put('Q', new Coord(0, 1));
      charUvMap.put('R', new Coord(1, 1));
      charUvMap.put('S', new Coord(2, 1));
      charUvMap.put('T', new Coord(3, 1));
      charUvMap.put('U', new Coord(4, 1));
      charUvMap.put('V', new Coord(5, 1));
      charUvMap.put('W', new Coord(6, 1));
      charUvMap.put('X', new Coord(7, 1));
      charUvMap.put('Y', new Coord(8, 1));
      charUvMap.put('Z', new Coord(9, 1));
      charUvMap.put('.', new Coord(10, 1));
      charUvMap.put('?', new Coord(11, 1));
      charUvMap.put(',', new Coord(12, 1));
      charUvMap.put(';', new Coord(13, 1));
      charUvMap.put(':', new Coord(14, 1));
      charUvMap.put('!', new Coord(15, 1));
      charUvMap.put('a', new Coord(0, 2));
      charUvMap.put('b', new Coord(1, 2));
      charUvMap.put('c', new Coord(2, 2));
      charUvMap.put('d', new Coord(3, 2));
      charUvMap.put('e', new Coord(4, 2));
      charUvMap.put('f', new Coord(5, 2));
      charUvMap.put('g', new Coord(6, 2));
      charUvMap.put('h', new Coord(7, 2));
      charUvMap.put('i', new Coord(8, 2));
      charUvMap.put('j', new Coord(9, 2));
      charUvMap.put('k', new Coord(10, 2));
      charUvMap.put('l', new Coord(11, 2));
      charUvMap.put('m', new Coord(12, 2));
      charUvMap.put('n', new Coord(13, 2));
      charUvMap.put('o', new Coord(14, 2));
      charUvMap.put('p', new Coord(15, 2));
      charUvMap.put('q', new Coord(0, 3));
      charUvMap.put('r', new Coord(1, 3));
      charUvMap.put('s', new Coord(2, 3));
      charUvMap.put('t', new Coord(3, 3));
      charUvMap.put('u', new Coord(4, 3));
      charUvMap.put('v', new Coord(5, 3));
      charUvMap.put('w', new Coord(6, 3));
      charUvMap.put('x', new Coord(7, 3));
      charUvMap.put('y', new Coord(8, 3));
      charUvMap.put('z', new Coord(9, 3));
      charUvMap.put('@', new Coord(10, 3));
      charUvMap.put('/', new Coord(11, 3));
      charUvMap.put('$', new Coord(12, 3));
      charUvMap.put('^', new Coord(13, 3));
      charUvMap.put('&', new Coord(14, 3));
      charUvMap.put('-', new Coord(15, 3));
      charUvMap.put('(', new Coord(0, 4));
      charUvMap.put(')', new Coord(1, 4));
      charUvMap.put('[', new Coord(2, 4));
      charUvMap.put(']', new Coord(3, 4));
      charUvMap.put('{', new Coord(4, 4));
      charUvMap.put('}', new Coord(5, 4));
      charUvMap.put('\'', new Coord(6, 4));
      charUvMap.put('=', new Coord(7, 4));
      charUvMap.put('%', new Coord(8, 4));
      charUvMap.put('>', new Coord(9, 4));
      charUvMap.put('<', new Coord(10, 4));
      charUvMap.put('*', new Coord(11, 4));
      charUvMap.put('+', new Coord(12, 4));
      charUvMap.put('"', new Coord(13, 4));
      charUvMap.put('"', new Coord(14, 4));
      charUvMap.put('|', new Coord(15, 4));
      charUvMap.put('0', new Coord(0, 5));
      charUvMap.put('1', new Coord(1, 5));
      charUvMap.put('2', new Coord(2, 5));
      charUvMap.put('3', new Coord(3, 5));
      charUvMap.put('4', new Coord(4, 5));
      charUvMap.put('5', new Coord(5, 5));
      charUvMap.put('6', new Coord(6, 5));
      charUvMap.put('7', new Coord(7, 5));
      charUvMap.put('8', new Coord(8, 5));
      charUvMap.put('9', new Coord(9, 5));
      charUvMap.put(' ', new Coord(10, 5));
      charUvMap.put('_', new Coord(11, 5));
      charUvMap.put('°', new Coord(12, 5));
      chillerSpacing.put('A', 5);
      chillerSpacing.put('B', 5);
      chillerSpacing.put('C', 5);
      chillerSpacing.put('D', 6);
      chillerSpacing.put('E', 5);
      chillerSpacing.put('F', 6);
      chillerSpacing.put('G', 6);
      chillerSpacing.put('H', 5);
      chillerSpacing.put('I', 6);
      chillerSpacing.put('J', 4);
      chillerSpacing.put('K', 4);
      chillerSpacing.put('L', 5);
      chillerSpacing.put('M', 4);
      chillerSpacing.put('N', 5);
      chillerSpacing.put('O', 6);
      chillerSpacing.put('P', 5);
      chillerSpacing.put('Q', 4);
      chillerSpacing.put('R', 5);
      chillerSpacing.put('S', 7);
      chillerSpacing.put('T', 4);
      chillerSpacing.put('U', 5);
      chillerSpacing.put('V', 5);
      chillerSpacing.put('W', 3);
      chillerSpacing.put('X', 3);
      chillerSpacing.put('Y', 5);
      chillerSpacing.put('Z', 3);
      chillerSpacing.put('.', 11);
      chillerSpacing.put('?', 9);
      chillerSpacing.put(',', 11);
      chillerSpacing.put(';', 11);
      chillerSpacing.put(':', 11);
      chillerSpacing.put('!', 10);
      chillerSpacing.put('a', 8);
      chillerSpacing.put('b', 8);
      chillerSpacing.put('c', 8);
      chillerSpacing.put('d', 7);
      chillerSpacing.put('e', 8);
      chillerSpacing.put('f', 8);
      chillerSpacing.put('g', 8);
      chillerSpacing.put('h', 6);
      chillerSpacing.put('i', 10);
      chillerSpacing.put('j', 8);
      chillerSpacing.put('k', 8);
      chillerSpacing.put('l', 10);
      chillerSpacing.put('m', 5);
      chillerSpacing.put('n', 9);
      chillerSpacing.put('o', 9);
      chillerSpacing.put('p', 8);
      chillerSpacing.put('q', 7);
      chillerSpacing.put('r', 9);
      chillerSpacing.put('s', 8);
      chillerSpacing.put('t', 8);
      chillerSpacing.put('u', 7);
      chillerSpacing.put('v', 8);
      chillerSpacing.put('w', 8);
      chillerSpacing.put('x', 5);
      chillerSpacing.put('y', 7);
      chillerSpacing.put('z', 6);
      chillerSpacing.put('@', 3);
      chillerSpacing.put('/', 6);
      chillerSpacing.put('$', 4);
      chillerSpacing.put('^', 5);
      chillerSpacing.put('&', 4);
      chillerSpacing.put('-', 5);
      chillerSpacing.put('(', 5);
      chillerSpacing.put(')', 5);
      chillerSpacing.put('[', 4);
      chillerSpacing.put(']', 4);
      chillerSpacing.put('{', 4);
      chillerSpacing.put('}', 4);
      chillerSpacing.put('\'', 8);
      chillerSpacing.put('=', 5);
      chillerSpacing.put('%', 4);
      chillerSpacing.put('>', 5);
      chillerSpacing.put('<', 5);
      chillerSpacing.put('*', 6);
      chillerSpacing.put('+', 3);
      chillerSpacing.put('"', 5);
      chillerSpacing.put('"', 5);
      chillerSpacing.put('|', 8);
      chillerSpacing.put('0', 5);
      chillerSpacing.put('1', 7);
      chillerSpacing.put('2', 5);
      chillerSpacing.put('3', 4);
      chillerSpacing.put('4', 5);
      chillerSpacing.put('5', 4);
      chillerSpacing.put('6', 6);
      chillerSpacing.put('7', 4);
      chillerSpacing.put('8', 4);
      chillerSpacing.put('9', 5);
      chillerSpacing.put(' ', 8);
      chillerSpacing.put('°', 8);
      chaparralSpacing.put('A', 5);
      chaparralSpacing.put('B', 5);
      chaparralSpacing.put('C', 5);
      chaparralSpacing.put('D', 5);
      chaparralSpacing.put('E', 5);
      chaparralSpacing.put('F', 5);
      chaparralSpacing.put('G', 5);
      chaparralSpacing.put('H', 3);
      chaparralSpacing.put('I', 9);
      chaparralSpacing.put('J', 6);
      chaparralSpacing.put('K', 6);
      chaparralSpacing.put('L', 5);
      chaparralSpacing.put('M', 1);
      chaparralSpacing.put('N', 4);
      chaparralSpacing.put('O', 5);
      chaparralSpacing.put('P', 4);
      chaparralSpacing.put('Q', 5);
      chaparralSpacing.put('R', 5);
      chaparralSpacing.put('S', 7);
      chaparralSpacing.put('T', 7);
      chaparralSpacing.put('U', 5);
      chaparralSpacing.put('V', 2);
      chaparralSpacing.put('W', 0);
      chaparralSpacing.put('X', 4);
      chaparralSpacing.put('Y', 4);
      chaparralSpacing.put('Z', 5);
      chaparralSpacing.put('.', 12);
      chaparralSpacing.put('?', 5);
      chaparralSpacing.put(',', 12);
      chaparralSpacing.put(';', 10);
      chaparralSpacing.put(':', 10);
      chaparralSpacing.put('!', 10);
      chaparralSpacing.put('a', 7);
      chaparralSpacing.put('b', 7);
      chaparralSpacing.put('c', 9);
      chaparralSpacing.put('d', 7);
      chaparralSpacing.put('e', 7);
      chaparralSpacing.put('f', 7);
      chaparralSpacing.put('g', 8);
      chaparralSpacing.put('h', 6);
      chaparralSpacing.put('i', 11);
      chaparralSpacing.put('j', 9);
      chaparralSpacing.put('k', 8);
      chaparralSpacing.put('l', 10);
      chaparralSpacing.put('m', 2);
      chaparralSpacing.put('n', 6);
      chaparralSpacing.put('o', 7);
      chaparralSpacing.put('p', 7);
      chaparralSpacing.put('q', 8);
      chaparralSpacing.put('r', 8);
      chaparralSpacing.put('s', 8);
      chaparralSpacing.put('t', 10);
      chaparralSpacing.put('u', 6);
      chaparralSpacing.put('v', 6);
      chaparralSpacing.put('w', 3);
      chaparralSpacing.put('x', 3);
      chaparralSpacing.put('y', 8);
      chaparralSpacing.put('z', 6);
      chaparralSpacing.put('@', 0);
      chaparralSpacing.put('/', 7);
      chaparralSpacing.put('$', 0);
      chaparralSpacing.put('^', 0);
      chaparralSpacing.put('&', 0);
      chaparralSpacing.put('-', 8);
      chaparralSpacing.put('(', 7);
      chaparralSpacing.put(')', 7);
      chaparralSpacing.put('[', 0);
      chaparralSpacing.put(']', 0);
      chaparralSpacing.put('{', 0);
      chaparralSpacing.put('}', 0);
      chaparralSpacing.put('\'', 8);
      chaparralSpacing.put('=', 0);
      chaparralSpacing.put('%', 6);
      chaparralSpacing.put('>', 0);
      chaparralSpacing.put('<', 0);
      chaparralSpacing.put('*', 0);
      chaparralSpacing.put('+', 0);
      chaparralSpacing.put('"', 0);
      chaparralSpacing.put('"', 0);
      chaparralSpacing.put('|', 0);
      chaparralSpacing.put('0', 6);
      chaparralSpacing.put('1', 8);
      chaparralSpacing.put('2', 6);
      chaparralSpacing.put('3', 6);
      chaparralSpacing.put('4', 6);
      chaparralSpacing.put('5', 5);
      chaparralSpacing.put('6', 8);
      chaparralSpacing.put('7', 6);
      chaparralSpacing.put('8', 7);
      chaparralSpacing.put('9', 6);
      chaparralSpacing.put(' ', 8);
      chaparralSpacing.put('_', 3);
      chaparralSpacing.put('°', 8);
      eccentricSpacing.put('A', 8);
      eccentricSpacing.put('B', 8);
      eccentricSpacing.put('C', 8);
      eccentricSpacing.put('D', 8);
      eccentricSpacing.put('E', 8);
      eccentricSpacing.put('F', 8);
      eccentricSpacing.put('G', 8);
      eccentricSpacing.put('H', 8);
      eccentricSpacing.put('I', 8);
      eccentricSpacing.put('J', 8);
      eccentricSpacing.put('K', 8);
      eccentricSpacing.put('L', 8);
      eccentricSpacing.put('M', 8);
      eccentricSpacing.put('N', 8);
      eccentricSpacing.put('O', 8);
      eccentricSpacing.put('P', 8);
      eccentricSpacing.put('Q', 8);
      eccentricSpacing.put('R', 8);
      eccentricSpacing.put('S', 8);
      eccentricSpacing.put('T', 8);
      eccentricSpacing.put('U', 8);
      eccentricSpacing.put('V', 8);
      eccentricSpacing.put('W', 8);
      eccentricSpacing.put('X', 8);
      eccentricSpacing.put('Y', 8);
      eccentricSpacing.put('Z', 8);
      eccentricSpacing.put('.', 8);
      eccentricSpacing.put('?', 8);
      eccentricSpacing.put(',', 8);
      eccentricSpacing.put(';', 8);
      eccentricSpacing.put(':', 8);
      eccentricSpacing.put('!', 8);
      eccentricSpacing.put('a', 8);
      eccentricSpacing.put('b', 8);
      eccentricSpacing.put('c', 8);
      eccentricSpacing.put('d', 8);
      eccentricSpacing.put('e', 9);
      eccentricSpacing.put('f', 8);
      eccentricSpacing.put('g', 8);
      eccentricSpacing.put('h', 8);
      eccentricSpacing.put('i', 11);
      eccentricSpacing.put('j', 8);
      eccentricSpacing.put('k', 8);
      eccentricSpacing.put('l', 8);
      eccentricSpacing.put('m', 8);
      eccentricSpacing.put('n', 8);
      eccentricSpacing.put('o', 8);
      eccentricSpacing.put('p', 8);
      eccentricSpacing.put('q', 8);
      eccentricSpacing.put('r', 8);
      eccentricSpacing.put('s', 10);
      eccentricSpacing.put('t', 9);
      eccentricSpacing.put('u', 8);
      eccentricSpacing.put('v', 8);
      eccentricSpacing.put('w', 8);
      eccentricSpacing.put('x', 8);
      eccentricSpacing.put('y', 8);
      eccentricSpacing.put('z', 8);
      eccentricSpacing.put('@', 8);
      eccentricSpacing.put('/', 8);
      eccentricSpacing.put('$', 8);
      eccentricSpacing.put('^', 8);
      eccentricSpacing.put('&', 8);
      eccentricSpacing.put('-', 8);
      eccentricSpacing.put('(', 8);
      eccentricSpacing.put(')', 8);
      eccentricSpacing.put('[', 8);
      eccentricSpacing.put(']', 8);
      eccentricSpacing.put('{', 8);
      eccentricSpacing.put('}', 8);
      eccentricSpacing.put('\'', 8);
      eccentricSpacing.put('=', 3);
      eccentricSpacing.put('%', 4);
      eccentricSpacing.put('>', 8);
      eccentricSpacing.put('<', 8);
      eccentricSpacing.put('*', 8);
      eccentricSpacing.put('+', 4);
      eccentricSpacing.put('"', 8);
      eccentricSpacing.put('"', 8);
      eccentricSpacing.put('|', 8);
      eccentricSpacing.put('0', 8);
      eccentricSpacing.put('1', 8);
      eccentricSpacing.put('2', 8);
      eccentricSpacing.put('3', 8);
      eccentricSpacing.put('4', 8);
      eccentricSpacing.put('5', 8);
      eccentricSpacing.put('6', 8);
      eccentricSpacing.put('7', 8);
      eccentricSpacing.put('8', 8);
      eccentricSpacing.put('9', 8);
      eccentricSpacing.put(' ', 8);
      eccentricSpacing.put('°', 8);
   }

   public static void saveFontFamily() {
      savedFont = currentFont;
   }

   public static void restoreFontFamily() {
      currentFont = savedFont;
   }

   public static void setFontFamily(FontFamily family) {
      currentFont = family;
   }

   public static String wrapText(String text, float maxWidth, float currentWidth) {
      int lastSpace = 0;
      currentWidth = 0.0F;

      for (int i = 0; i < text.length(); i++) {
         currentWidth += getTextWidth("" + text.charAt(i), 0.4F);
         if (text.charAt(i) == ' ') {
            lastSpace = i;
         }

         if (text.charAt(i) == '\n') {
            currentWidth = 0.0F;
         }

         if (currentWidth > 360.0F) {
            text = text.substring(0, lastSpace) + '\n' + text.substring(lastSpace + 1, text.length());
            i = lastSpace + 1;
            currentWidth = 0.0F;
         }
      }

      return text;
   }

   public static int getTextWidth(String text, float scale) {
      int spacing = 0;
      int maxWidth = 0;
      int lineWidth = 0;

      for (int i = 0; i < text.length(); i++) {
         if (text.charAt(i) == '\n') {
            maxWidth = Math.max(maxWidth, lineWidth);
            lineWidth = 0;
         } else {
            spacing = getSpacing(text.charAt(i));

            lineWidth += (int)(tileWidth - 2 * spacing);
         }
      }

      return (int)(Math.max(maxWidth, lineWidth) * scale);
   }

   public static int getCharHeight(float scale) {
      return (int)(tileHeight * scale);
   }

   public static int getTextHeight(String text, float scale) {
      scale = 1.0F;

      for (int i = 0; i < text.length(); i++) {
         if (text.charAt(i) == '\n') {
            scale++;
         }
      }

      return (int)(tileHeight * 0.4F * scale);
   }

   public static void drawCentered(int x, int y, String text, float scale) {
      float textWidth = getTextWidth(text, scale);
      GL11.glTranslatef(-textWidth / 2.0F, 0.0F, 0.0F);
      drawText(x, y, text, scale, 0.0F);
      GL11.glTranslatef(textWidth / 2.0F, 0.0F, 0.0F);
   }

   public static void draw(int x, int y, String text, float scale) {
      drawText(x, y, text, scale, 0.0F);
   }

   private static void bindCurrentFont() {
      if (currentFont == FontFamily.CHILLER) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.chillerFont);
      }

      if (currentFont == FontFamily.CHAPARRAL) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.chaparalFont);
      }

      if (currentFont == FontFamily.ECCENTRIC) {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.eccentricFont);
      }
   }

   private static int getSpacing(char c) {
      if (currentFont == FontFamily.CHILLER) {
         return chillerSpacing.get(c);
      }

      if (currentFont == FontFamily.CHAPARRAL) {
         return chaparralSpacing.get(c);
      }

      if (currentFont == FontFamily.ECCENTRIC) {
         return eccentricSpacing.get(c);
      }

      return 0;
   }

   private static void drawText(int x, int y, String text, float scale, float outline) {
      int spacing = 0;
      int xOffset = 0;
      bindCurrentFont();

      GL11.glPushMatrix();
      GL11.glTranslatef(x, y, 0.0F);
      GL11.glScalef(scale, scale, scale);

      for (int i = 0; i < text.length(); i++) {
         if (text.charAt(i) == '\n') {
            GL11.glTranslatef(-xOffset, (int)(tileHeight * scale) * 2.0F, 0.0F);
            xOffset = 0;
         } else {
            spacing = getSpacing(text.charAt(i));

            GL11.glTranslatef(-spacing, 0.0F, 0.0F);
            if (outline > 0.0F) {
               drawChar(text.charAt(i), outline);
            } else {
               drawChar(text.charAt(i), 0.0F);
            }

            GL11.glTranslatef(tileWidth - spacing, 0.0F, 0.0F);
            xOffset = (int)(xOffset + (tileWidth - 2 * spacing));
         }
      }

      GL11.glPopMatrix();
   }

   public static void drawCenteredGradient(int x, int y, String text, float scale, Point topColor, float topAlpha, Point bottomColor, float bottomAlpha) {
      float textWidth = getTextWidth(text, scale);
      GL11.glTranslatef(-textWidth / 2.0F, 0.0F, 0.0F);
      drawGradient(x, y, text, scale, topColor, 1.0F, bottomColor, 1.0F);
      GL11.glTranslatef(textWidth / 2.0F, 0.0F, 0.0F);
   }

   public static void drawGradient(int x, int y, String text, float scale, Point topColor, float topAlpha, Point bottomColor, float bottomAlpha) {
      int spacing = 0;
      int xOffset = 0;
      bindCurrentFont();

      GL11.glPushMatrix();
      GL11.glTranslatef(x, y, 0.0F);
      GL11.glScalef(scale, scale, scale);

      for (int i = 0; i < text.length(); i++) {
         if (text.charAt(i) == '\n') {
            GL11.glTranslatef(-xOffset, (int)(tileHeight * scale) * 2.0F, 0.0F);
            xOffset = 0;
         } else {
            spacing = getSpacing(text.charAt(i));

            GL11.glTranslatef(-spacing, 0.0F, 0.0F);
            char charValue = text.charAt(i);
            Coord uv = charUvMap.get(charValue);
            if (uv != null) {
               GL11.glBegin(GL11.GL_QUADS);
               GL11.glColor4f(topColor.x, topColor.y, topColor.z, topAlpha);
               GL11.glTexCoord2f(uv.x / tileCols, (uv.y + 1.0F) / tileRows);
               GL11.glVertex2f(-0.0F, tileHeight + 0.0F);
               GL11.glColor4f(bottomColor.x, bottomColor.y, bottomColor.z, bottomAlpha);
               GL11.glTexCoord2f(uv.x / tileCols, uv.y / tileRows);
               GL11.glVertex2f(-0.0F, -0.0F);
               GL11.glColor4f(bottomColor.x, bottomColor.y, bottomColor.z, bottomAlpha);
               GL11.glTexCoord2f((uv.x + 1.0F) / tileCols, uv.y / tileRows);
               GL11.glVertex2f(tileWidth + 0.0F, -0.0F);
               GL11.glColor4f(topColor.x, topColor.y, topColor.z, topAlpha);
               GL11.glTexCoord2f((uv.x + 1.0F) / tileCols, (uv.y + 1.0F) / tileRows);
               GL11.glVertex2f(tileWidth + 0.0F, tileHeight + 0.0F);
               GL11.glEnd();
            }

            GL11.glTranslatef(tileWidth - spacing, 0.0F, 0.0F);
            xOffset = (int)(xOffset + (tileWidth - 2 * spacing));
         }
      }

      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void drawChar(char ch, float outline) {
      Coord uv = charUvMap.get(ch);
      if (uv != null) {
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(uv.x / tileCols, (uv.y + 1.0F) / tileRows);
         GL11.glVertex2f(-outline, tileHeight + outline);
         GL11.glTexCoord2f(uv.x / tileCols, uv.y / tileRows);
         GL11.glVertex2f(-outline, -outline);
         GL11.glTexCoord2f((uv.x + 1.0F) / tileCols, uv.y / tileRows);
         GL11.glVertex2f(tileWidth + outline, -outline);
         GL11.glTexCoord2f((uv.x + 1.0F) / tileCols, (uv.y + 1.0F) / tileRows);
         GL11.glVertex2f(tileWidth + outline, tileHeight + outline);
         GL11.glEnd();
      }
   }
}
