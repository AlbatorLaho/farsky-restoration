package game.environment;

import game.manager.GameScene;
import game.util.Point;

public final class DepthAtmosphere {
   private static float zoneRatio = 0.0F;
   private static float zoneFraction = 0.0F;
   private static int zoneIndex = 0;
   public static float visibilityFactor = 1.0F;
   private static float depthMeters = 0.0F;
   private static Point[] fogColors = new Point[3];
   private static Point[] ambientColors = new Point[3];
   private static Point[] sunColors = new Point[3];
   private static float[] fogDistances = new float[3];
   private static float[] sunBrightnesses = new float[3];
   private static float[] scatterFactors = new float[3];
   private static int[] lightRayCounts = new int[3];
   private static float[] daySpawnIntervals = new float[3];
   private static float[] nightSpawnIntervals = new float[3];
   private static float[] causticIntensities = new float[3];

   public static void init() {
      fogColors[0] = new Point(0.15F, 0.53F, 0.69F);
      ambientColors[0] = new Point(0.13F, 0.48F, 0.63F).scaled(0.8F);
      sunColors[0] = new Point(0.2F, 0.4F, 0.53F);
      fogDistances[0] = 500.0F;
      sunBrightnesses[0] = 1.0F;
      scatterFactors[0] = 0.2F;
      lightRayCounts[0] = 3;
      daySpawnIntervals[0] = 25.0F;
      nightSpawnIntervals[0] = 18.0F;
      causticIntensities[0] = 1.0F;
      fogColors[1] = new Point(0.11F, 0.35F, 0.43F).scaled(1.0F);
      ambientColors[1] = new Point(0.08F, 0.3F, 0.4F).scaled(0.7F);
      sunColors[1] = new Point(0.2F, 0.4F, 0.53F);
      fogDistances[1] = 450.0F;
      sunBrightnesses[1] = 0.6F;
      scatterFactors[1] = 0.2F;
      lightRayCounts[1] = 4;
      daySpawnIntervals[1] = 22.0F;
      nightSpawnIntervals[1] = 17.0F;
      causticIntensities[1] = 0.3F;
      fogColors[2] = new Point(0.08F, 0.3F, 0.4F).scaled(0.6F);
      ambientColors[2] = new Point(0.03F, 0.1F, 0.2F);
      sunColors[2] = new Point(0.35F, 0.35F, 0.45F);
      fogDistances[2] = 350.0F;
      sunBrightnesses[2] = 0.4F;
      scatterFactors[2] = 0.2F;
      lightRayCounts[2] = 4;
      daySpawnIntervals[2] = 3.0F;
      nightSpawnIntervals[2] = 3.0F;
      causticIntensities[2] = 0.0F;
   }

   public static void update(float depth) {
      depthMeters = depth * 0.1F;
      zoneRatio = (depth + 1000.0F) / -1000.0F;
      if (zoneRatio < 0.0F) {
         zoneRatio = 0.0F;
      }

      if (zoneRatio > 2.0F) {
         zoneRatio = 1.99999F;
      }

      zoneFraction = zoneRatio % 1.0F;
      zoneIndex = (int)zoneRatio;
      if (zoneIndex >= 2) {
         zoneIndex = 1;
      }
   }

   public static float toMeters(float depth) {
      return depth * 0.1F;
   }

   public static int getZone(float depth) {
      return Math.round(Math.max((depth - -1000.0F) / -1000.0F, 0.0F));
   }

   public static int getDepthZone() {
      return GameScene.avatar == null ? 0 : getZone(GameScene.avatar.getCameraPos().y);
   }

   public static Point getFogColor() {
      Point from = fogColors[zoneIndex].scaled(1.0F - zoneFraction);
      Point to = fogColors[zoneIndex + 1].scaled(zoneFraction);
      return from.plus(to);
   }

   public static Point getAmbientColor() {
      Point from = ambientColors[zoneIndex].scaled(1.0F - zoneFraction);
      Point to = ambientColors[zoneIndex + 1].scaled(zoneFraction);
      return from.plus(to);
   }

   public static Point getSunColor() {
      Point from = sunColors[zoneIndex].scaled(1.0F - zoneFraction);
      Point to = sunColors[zoneIndex + 1].scaled(zoneFraction);
      return from.plus(to);
   }

   public static float getMaxFogDistance() {
      return fogDistances[0];
   }

   public static float getFogDistance() {
      float from = fogDistances[zoneIndex] * (1.0F - zoneFraction);
      float to = fogDistances[zoneIndex + 1] * zoneFraction;
      return Math.max(200.0F, (from + to) * visibilityFactor);
   }

   public static float getSunBrightness() {
      float from = sunBrightnesses[zoneIndex] * (1.0F - zoneFraction);
      float to = sunBrightnesses[zoneIndex + 1] * zoneFraction;
      return from + to;
   }

   public static float getCausticIntensity() {
      float from = causticIntensities[zoneIndex] * (1.0F - zoneFraction);
      float to = causticIntensities[zoneIndex + 1] * zoneFraction;
      return from + to;
   }

   public static float getLightRayAlpha() {
      return zoneIndex == 0 ? 1.0F - zoneRatio : 0.0F;
   }

   public static float getMinBloom() {
      return 0.1F;
   }

   public static float getDaySpawnInterval() {
      float from = daySpawnIntervals[zoneIndex] * (1.0F - zoneFraction);
      float to = daySpawnIntervals[zoneIndex + 1] * zoneFraction;
      return from + to;
   }

   public static float getNightSpawnInterval() {
      float from = nightSpawnIntervals[zoneIndex] * (1.0F - zoneFraction);
      float to = nightSpawnIntervals[zoneIndex + 1] * zoneFraction;
      return from + to;
   }

   public static float getDepthInMeters() {
      return depthMeters;
   }
}
