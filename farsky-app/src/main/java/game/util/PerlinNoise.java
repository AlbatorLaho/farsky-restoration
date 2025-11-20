package game.util;

public final class PerlinNoise {
   public static float seed = 0.0F;

   private static float noise2d(float x, float z) {
      int xi = (int)x;
      int zi = (int)z;
      int hash1 = (int)(xi + zi * 57 + seed);
      int hash2 = hash1 << 13 ^ hash1;
      return 1.0F - (hash2 * (hash2 * hash2 * 15731 + 789221) + 1376312589 & 2147483647) / 1.0737418E9F;
   }

   private static float smoothNoise(float x, float z) {
      float corners = (noise2d(x - 1.0F, z - 1.0F) + noise2d(x + 1.0F, z - 1.0F) + noise2d(x - 1.0F, z + 1.0F) + noise2d(x + 1.0F, z + 1.0F)) / 16.0F;
      float edges = (noise2d(x - 1.0F, z) + noise2d(x + 1.0F, z) + noise2d(x, z - 1.0F) + noise2d(x, z + 1.0F)) / 8.0F;
      x = noise2d(x, z) / 4.0F;
      return corners + edges + x;
   }

   private static float cosInterp(float a, float b, float t) {
      t *= (float) Math.PI;
      t = (1.0F - (float)Math.cos(t)) * 0.5F;
      return a * (1.0F - t) + b * t;
   }

   public static float noise(float x, float z, int octaves) {
      float acc = 0.0F;

      for (int octave = 0; octave < octaves; octave++) {
         float freq = (float)Math.pow(2.0, octave);
         float amp = (float)Math.pow(0.7F, octave);
         float scaledX = x * freq;
         freq = z * freq;
         acc = scaledX;
         float ix = (int)scaledX;
         acc -= ix;
         float iz = (int)freq;
         freq -= iz;
         float n00 = smoothNoise(ix, iz);
         float n10 = smoothNoise(ix + 1.0F, iz);
         float n01 = smoothNoise(ix, iz + 1.0F);
         ix = smoothNoise(ix + 1.0F, iz + 1.0F);
         iz = cosInterp(n00, n10, acc);
         acc = cosInterp(n01, ix, acc);
         acc += cosInterp(iz, acc, freq) * amp;
      }

      return acc;
   }
}
