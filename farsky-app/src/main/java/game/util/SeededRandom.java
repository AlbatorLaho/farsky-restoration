package game.util;

import java.util.Random;

public final class SeededRandom {
   public static float seed = 0.0F;

   public static float nextFloat(float x, float y) {
      return new Random((int)(x + y + x * y + seed * 463.0F)).nextFloat();
   }
}
