package game.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

public class IconLoader {
   public static ByteBuffer[] loadIcons(String filename) {
      BufferedImage image = null;

      try {
         image = ImageIO.read(IconLoader.class.getResource("/" + filename));
      } catch (IOException e) {
         e.printStackTrace();
      }

      String osName = System.getProperty("os.name").toUpperCase();
      ByteBuffer[] result;
      if (osName.contains("WIN")) {
         result = new ByteBuffer[2];
         result[0] = resizeToSquare(image, 16);
         result[1] = resizeToSquare(image, 32);
      } else if (osName.contains("MAC")) {
         result = new ByteBuffer[1];
         result[0] = resizeToSquare(image, 128);
      } else {
         result = new ByteBuffer[1];
         result[0] = resizeToSquare(image, 32);
      }

      return result;
   }

   private static ByteBuffer resizeToSquare(BufferedImage image, int size) {
      BufferedImage scaled = new BufferedImage(size, size, 3);
      Graphics2D g = scaled.createGraphics();
      double scale;
      if (image.getWidth() > scaled.getWidth()) {
         scale = (double)scaled.getWidth() / image.getWidth();
      } else {
         scale = scaled.getWidth() / image.getWidth();
      }

      if (image.getHeight() > scaled.getHeight()) {
         double heightScale = (double)scaled.getHeight() / image.getHeight();
         if (heightScale < scale) {
            scale = heightScale;
         }
      } else {
         double heightScale = scaled.getHeight() / image.getHeight();
         if (heightScale < scale) {
            scale = heightScale;
         }
      }

      double scaledWidth = image.getWidth() * scale;
      double scaledHeight = image.getHeight() * scale;
      g.drawImage(image, (int)((scaled.getWidth() - scaledWidth) / 2.0), (int)((scaled.getHeight() - scaledHeight) / 2.0), (int)scaledWidth, (int)scaledHeight, null);
      g.dispose();
      return toByteBuffer(scaled);
   }

   private static ByteBuffer toByteBuffer(BufferedImage image) {
      byte[] pixels = new byte[image.getWidth() * image.getHeight() << 2];
      int idx = 0;

      for (int row = 0; row < image.getHeight(); row++) {
         for (int col = 0; col < image.getWidth(); col++) {
            int argb = image.getRGB(col, row);
            pixels[idx] = (byte)(argb << 8 >> 24);
            pixels[idx + 1] = (byte)(argb << 16 >> 24);
            pixels[idx + 2] = (byte)((byte)argb);
            pixels[idx + 3] = (byte)(argb >> 24);
            idx += 4;
         }
      }

      return ByteBuffer.wrap(pixels);
   }
}
