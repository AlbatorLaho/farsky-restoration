package game.util;

import game.Main;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public final class ImageData {
   private ArrayList<Byte> pixelData = new ArrayList<>();
   private int width = 0;
   private int height = 0;

   public ImageData() {
   }

   public ImageData(String path, boolean rotate, boolean flipX) {
      this.load(path, rotate, flipX);
   }

   public final void load(String path, boolean rotate, boolean flipX) {
      BufferedImage img = null;

      try {
         img = ImageIO.read(this.getClass().getResource("/" + path));
      } catch (IOException e) {
         e.printStackTrace();
         if (Main.isVerbose) {
            System.out.println(path + ": Texture Loading Error!");
         }
      }

      this.processImage(img, rotate, flipX);
   }

   private void processImage(BufferedImage img, boolean rotate, boolean flipX) {
      int imgType = img.getType();
      if (imgType == 0) {
         imgType = 6;
      }

      if (flipX) {
         BufferedImage flipped = new BufferedImage(img.getWidth(), img.getHeight(), imgType);

         for (int row = 0; row < img.getHeight(); row++) {
            for (int col = 0; col < img.getWidth(); col++) {
               flipped.setRGB(col, row, img.getRGB(img.getWidth() - 1 - col, row));
            }
         }

         img = flipped;
      }

      if (rotate) {
         BufferedImage rotated = new BufferedImage(img.getHeight(), img.getWidth(), imgType);

         for (int col = 0; col < img.getWidth(); col++) {
            for (int row = 0; row < img.getHeight(); row++) {
               rotated.setRGB(img.getHeight() - 1 - row, img.getWidth() - 1 - col, img.getRGB(col, row));
            }
         }

         img = rotated;
      }

      this.width = img.getWidth();
      this.height = img.getHeight();
      int[] pixels = new int[this.width * this.height];
      img.getRGB(0, 0, this.width, this.height, pixels, 0, this.width);

      for (int i = 0; i < pixels.length; i++) {
         this.pixelData.add((byte)(pixels[i] >> 16));
         this.pixelData.add((byte)(pixels[i] >> 8));
         this.pixelData.add((byte)pixels[i]);
         this.pixelData.add((byte)(pixels[i] >>> 24));
      }
   }

   public final byte getByte(int idx) {
      return this.pixelData.get(idx);
   }

   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }
}
