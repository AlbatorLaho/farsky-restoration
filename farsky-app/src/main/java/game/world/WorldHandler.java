package game.world;
import game.enemy.EnemyGenerator;

import game.Main;
import game.util.Coord;
import game.util.CubicPolynomial;
import game.world.gen.WorldGenerator;
import game.world.structure.GamePlayElmt;
import game.world.structure.GamePlayType;
import game.world.structure.InteractionUsed;
import game.world.structure.ZoneId;
import java.util.ArrayList;

public final class WorldHandler {
   static enum WorkingVar {
      TERRAIN,
      ZONE;
   }

   private World world;
   private CubicPolynomial[][] polyX;
   private CubicPolynomial[][] polyZ;
   private ZoneId queryZone = ZoneId.OPEN_OCEAN;
   private WorkingVar workingVar = WorkingVar.TERRAIN;
   private float bl00;
   private float bl10;
   private float bl11;
   private float bl01;
   private float rowLeft;
   private float rowRight;
   private float colBottom;
   private float colTop;
   private float localX;
   private float localZ;

   public final void generateWorld(float dayTime, float nightTime, EnemyGenerator.SpawningLevel spawning) {
      WorldGenerator.generate(dayTime, nightTime, spawning);
      this.world = WorldGenerator.getWorld();
      this.initPolynomials();
   }

   public final void loadWorld(World w) {
      this.world = w;
      this.initPolynomials();
   }

   public final void createTestWorld() {
      this.world = new World(10, 10);
      this.world.setRandLevelGenCliff(0.0F);
      this.world.setRandGenDunes(0.0F);
      this.world.setRandGenRock(92.0F);
      this.world.setRandGenSource(0.0F);
      this.world.setRandLandscapeAlga(0.0F);
      this.world.setRandLandscapeGiantAlga(130.0F);
      this.world.setDayTime(1.0F);
      this.world.setNightTime(0.0F);
      this.world.setSpawning(EnemyGenerator.SpawningLevel.NEVER);

      for (int x = 0; x < this.world.getWidth(); x++) {
         for (int z = 0; z < this.world.getHeight(); z++) {
            this.world.setHeightAt(x, z, 0.0F);
            this.world.setZoneIdAt(x, z, ZoneId.KELP_FOREST);
            this.world.setGamePlayElmtAt(x, z, new GamePlayElmt(GamePlayType.NONE));
            this.world.setStageAt(x, z, 0);
         }
      }

      this.initPolynomials();
   }

   private void initPolynomials() {
      this.workingVar = WorkingVar.TERRAIN;
      this.polyX = new CubicPolynomial[this.world.getWidth()][this.world.getHeight()];
      this.polyZ = new CubicPolynomial[this.world.getWidth()][this.world.getHeight()];

      for (int x = 0; x < this.world.getWidth(); x++) {
         for (int z = 0; z < this.world.getHeight(); z++) {
            this.polyX[x][z] = this.buildPolynomial(new Coord(x << 7, z << 7), new Coord(x + 1 << 7, z << 7));
            this.polyZ[x][z] = this.buildPolynomial(new Coord(x << 7, z << 7), new Coord(x << 7, z + 1 << 7));
         }
      }
   }

   public final float interpolateHeight(float worldX, float worldZ) {
      if (worldX < 0.0F) {
         worldX = 0.0F;
      } else if (worldX >= (this.world.getWidth() << 7) - 1) {
         worldX = (this.world.getWidth() << 7) - 2;
      }

      if (worldZ < 0.0F) {
         worldZ = 0.0F;
      } else if (worldZ >= (this.world.getHeight() << 7) - 1) {
         worldZ = (this.world.getHeight() << 7) - 2;
      }

      this.localX = worldX - ((int)worldX / 128 << 7);
      this.localZ = worldZ - ((int)worldZ / 128 << 7);
      this.workingVar = WorkingVar.TERRAIN;
      this.rowLeft = this.getPolyZ((int)worldX / 128, (int)worldZ / 128).evaluate(this.localZ);
      this.rowRight = this.getPolyZ((int)worldX / 128 + 1, (int)worldZ / 128).evaluate(this.localZ);
      this.colBottom = this.getPolyX((int)worldX / 128, (int)worldZ / 128).evaluate(this.localX);
      this.colTop = this.getPolyX((int)worldX / 128, (int)worldZ / 128 + 1).evaluate(this.localX);
      return this.bilinearInterp();
   }

   public final float interpolateZoneBlend(float worldX, float worldZ, ZoneId zone) {
      if (worldX < 0.0F) {
         worldX = 0.0F;
      } else if (worldX >= (this.world.getWidth() << 7) - 1) {
         worldX = (this.world.getWidth() << 7) - 2;
      }

      if (worldZ < 0.0F) {
         worldZ = 0.0F;
      } else if (worldZ >= (this.world.getHeight() << 7) - 1) {
         worldZ = (this.world.getHeight() << 7) - 2;
      }

      this.localX = worldX - ((int)worldX / 128 << 7);
      this.localZ = worldZ - ((int)worldZ / 128 << 7);
      this.workingVar = WorkingVar.ZONE;
      this.queryZone = zone;
      this.bl00 = this.sampleValue((int)worldX / 128, (int)worldZ / 128);
      this.bl10 = this.sampleValue((int)worldX / 128 + 1, (int)worldZ / 128);
      this.bl11 = this.sampleValue((int)worldX / 128 + 1, (int)worldZ / 128 + 1);
      this.bl01 = this.sampleValue((int)worldX / 128, (int)worldZ / 128 + 1);
      this.rowLeft = (this.bl00 * (128.0F - this.localZ) + this.bl01 * this.localZ) / 128.0F;
      this.rowRight = (this.bl10 * (128.0F - this.localZ) + this.bl11 * this.localZ) / 128.0F;
      this.colBottom = (this.bl00 * (128.0F - this.localX) + this.bl10 * this.localX) / 128.0F;
      this.colTop = (this.bl01 * (128.0F - this.localX) + this.bl11 * this.localX) / 128.0F;
      return this.bilinearInterp();
   }

   public final World getWorld() {
      return this.world;
   }

   public final float getDayTime() {
      return this.world.getDayTime();
   }

   public final float getNightTime() {
      return this.world.getNightTime();
   }

   public final EnemyGenerator.SpawningLevel getSpawning() {
      return this.world.getSpawning();
   }

   public final Coord getAvatarStartPosition() {
      return this.world.getAvatarStartPosition();
   }

   public final Coord getStartBasePosition() {
      return this.world.getStartBasePosition();
   }

   public final ArrayList<Coord> getAbandonedBasePositions() {
      return this.world.getAbandonedBasePositions();
   }

   public final ArrayList<Coord> getDroidPositions() {
      return this.world.getDroidPositions();
   }

   public final float getRandLevelGenCliff() {
      return this.world.getRandLevelGenCliff();
   }

   public final float getRandGenDunes() {
      return this.world.getRandGenDunes();
   }

   public final float getRandGenRock() {
      return this.world.getRandGenRock();
   }

   public final float getRandGenSource() {
      return this.world.getRandGenSource();
   }

   public final float getRandLandscapeAlga() {
      return this.world.getRandLandscapeAlga();
   }

   public final float getRandLandscapeGiantAlga() {
      return this.world.getRandLandscapeGiantAlga();
   }

   public final boolean hasInteractionUsed(InteractionUsed interaction) {
      return this.world.hasInteractionUsed(interaction);
   }

   public final void setGamePlayElmtAt(GamePlayElmt elmt, int x, int z) {
      this.world.setGamePlayElmtAt(x, z, elmt);
   }

   public final void decrementResourceAt(int x, int z) {
      this.world.decrementResourceAt(x, z);
   }

   public final void addInteractionUsed(InteractionUsed interaction) {
      this.world.addInteractionUsed(interaction);
   }

   public final void setVisibleAt(int x, int z, boolean visible) {
      this.world.setVisibleAt(x, z, visible);
   }

   private CubicPolynomial buildPolynomial(Coord from, Coord to) {
      double h0 = this.sampleValue((int)from.x / 128, (int)from.y / 128);
      double h1 = this.sampleValue((int)to.x / 128, (int)to.y / 128);
      double t0;
      double t1;
      if (from.x == to.x) {
         t0 = (h1 - this.sampleValue((int)from.x / 128, (int)from.y / 128 - 1)) / 256.0;
         t1 = (this.sampleValue((int)from.x / 128, (int)from.y / 128 + 2) - h0) / 256.0;
      } else {
         t0 = (h1 - this.sampleValue((int)from.x / 128 - 1, (int)from.y / 128)) / 256.0;
         t1 = (this.sampleValue((int)from.x / 128 + 2, (int)from.y / 128) - h0) / 256.0;
      }

      CubicPolynomial poly = new CubicPolynomial();
      double segEnd = 128.0;
      double segStart = 0.0;
      double adjustedDelta = h1 - h0 - t0 * 128.0;
      double c3 = (t1 - t0 - adjustedDelta * 256.0 / 16384.0) / 16384.0;
      double c2 = (adjustedDelta - c3 * 2097152.0) / 16384.0;
      double c1 = t0 - c3 * 0.0 - c2 * 0.0;
      double c0 = h0 - c3 * 0.0 - c2 * 0.0 - c1 * 0.0;
      poly.c3 = (float)c3;
      poly.c2 = (float)c2;
      poly.c1 = (float)c1;
      poly.c0 = (float)c0;
      if (Main.isVerbose) {
         double verifyQ0 = c0 + c1 * segStart + c2 * segStart * segStart + c3 * segStart * segStart * segStart;
         double verifyT0 = c1 + c2 * 2.0 * segStart + c3 * 3.0 * segStart * segStart;
         double verifyQ1 = c0 + c1 * segEnd + c2 * segEnd * segEnd + c3 * segEnd * segEnd * segEnd;
         double verifyT1 = c1 + c2 * 2.0 * segEnd + c3 * 3.0 * segEnd * segEnd;
         if (Math.abs(verifyQ0 / h0) < 0.99 || Math.abs(verifyQ0 / h0) > 1.01) {
            System.out.println("Polynomial Error Q0: " + verifyQ0 / h0 * 100.0 + "%");
         }

         if (Math.abs(verifyT0 / t0) < 0.99 || Math.abs(verifyT0 / t0) > 1.01) {
            System.out.println("Polynomial Error T0: " + verifyT0 / t0 * 100.0 + "%");
         }

         if (Math.abs(verifyQ1 / h1) < 0.99 || Math.abs(verifyQ1 / h1) > 1.01) {
            System.out.println("Polynomial Error Q1: " + verifyQ1 / h1 * 100.0 + "%");
         }

         if (Math.abs(verifyT1 / t1) < 0.99 || Math.abs(verifyT1 / t1) > 1.01) {
            System.out.println("Polynomial Error T1: " + verifyT1 / t1 * 100.0 + "%");
         }
      }

      return poly;
   }

   private float bilinearInterp() {
      float interpRow = (this.rowLeft * (128.0F - this.localX) + this.rowRight * this.localX) / 128.0F;
      float interpCol = (this.colBottom * (128.0F - this.localZ) + this.colTop * this.localZ) / 128.0F;
      return (interpRow + interpCol) / 2.0F;
   }

   private float sampleValue(int x, int z) {
      if (x < 0) {
         x = 0;
      }

      if (x >= this.world.getWidth()) {
         x = this.world.getWidth() - 1;
      }

      if (z < 0) {
         z = 0;
      }

      if (z >= this.world.getHeight()) {
         z = this.world.getHeight() - 1;
      }

      if (this.workingVar == WorkingVar.TERRAIN) {
         return this.world.getHeightAt(x, z);
      } else if (this.workingVar == WorkingVar.ZONE) {
         return this.world.getZoneIdAt(x, z) == this.queryZone ? 1.0F : 0.0F;
      } else {
         return 0.0F;
      }
   }

   private CubicPolynomial getPolyX(int x, int z) {
      if (x < 0) {
         x = 0;
      }

      if (x >= this.world.getWidth()) {
         x = this.world.getWidth() - 1;
      }

      if (z < 0) {
         z = 0;
      }

      if (z >= this.world.getHeight()) {
         z = this.world.getHeight() - 1;
      }

      return this.polyX[x][z];
   }

   private CubicPolynomial getPolyZ(int x, int z) {
      if (x < 0) {
         x = 0;
      }

      if (x >= this.world.getWidth()) {
         x = this.world.getWidth() - 1;
      }

      if (z < 0) {
         z = 0;
      }

      if (z >= this.world.getHeight()) {
         z = this.world.getHeight() - 1;
      }

      return this.polyZ[x][z];
   }
}
