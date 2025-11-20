package game.world.gen;

public final class SeedInput {
   private static int digitCount = 0;
   private static int seed = (int)(Math.random() * Math.pow(10.0, 6.0));

   public static void randomize() {
      seed = (int)(Math.random() * Math.pow(10.0, 6.0));
   }

   public static void reset() {
      digitCount = 0;
   }

   public static void processKey(int keyCode) {
      if (digitCount == 0) {
         seed = 0;
      }

      digitCount++;
      byte digit = -1;
      switch (keyCode) {
         case 2:
         case 79:
            digit = 1;
            break;
         case 3:
         case 80:
            digit = 2;
            break;
         case 4:
         case 81:
            digit = 3;
            break;
         case 5:
         case 75:
            digit = 4;
            break;
         case 6:
         case 76:
            digit = 5;
            break;
         case 7:
         case 77:
            digit = 6;
            break;
         case 8:
         case 71:
            digit = 7;
            break;
         case 9:
         case 72:
            digit = 8;
            break;
         case 10:
         case 73:
            digit = 9;
            break;
         case 11:
         case 82:
            digit = 0;
            break;
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 74:
         case 78:
      }

      if (digit >= 0) {
         seed = seed * 10 + digit;
      }
   }

   public static boolean isComplete() {
      return digitCount == 6;
   }

   public static int getSeed() {
      return seed;
   }
}
