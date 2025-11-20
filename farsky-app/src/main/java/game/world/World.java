package game.world;
import game.enemy.EnemyGenerator;

import game.util.Coord;
import game.world.structure.GamePlayElmt;
import game.world.structure.GamePlayType;
import game.world.structure.InteractionUsed;
import game.world.structure.ZoneId;
import java.io.Serializable;
import java.util.ArrayList;

public class World implements Serializable {
   private static final long serialVersionUID = 6750684236065594746L;
   private int WIDTH;
   private int HEIGHT;
   private float[][] heightMap;
   private boolean[][] mapVisible;
   private ZoneId[][] zoneIdMap;
   private int[][] stage;
   private int[][] resNb;
   private GamePlayElmt[][] gamePlayElmt;
   private ArrayList<InteractionUsed> interactionUsed;
   private float randLevelGenCliff;
   private float randGenDunes;
   private float randGenRock;
   private float randGenSource;
   private float randLandscapeAlga;
   private float randLandscapeGiantAlga;
   private Coord avatarStartPosition;
   private Coord startBasePosition;
   private ArrayList<Coord> abandonedBasePosition;
   private ArrayList<Coord> droidPosition;
   private float dayTime;
   private float nightTime;
   private EnemyGenerator.SpawningLevel spawning;

   public World(int width, int height) {
      this.WIDTH = width;
      this.HEIGHT = height;
      this.heightMap = new float[this.WIDTH][this.HEIGHT];
      this.mapVisible = new boolean[this.WIDTH][this.HEIGHT];
      this.zoneIdMap = new ZoneId[this.WIDTH][this.HEIGHT];
      this.stage = new int[this.WIDTH][this.HEIGHT];
      this.resNb = new int[this.WIDTH][this.HEIGHT];
      this.gamePlayElmt = new GamePlayElmt[this.WIDTH][this.HEIGHT];
      this.interactionUsed = new ArrayList<>();
      this.abandonedBasePosition = new ArrayList<>();
      this.droidPosition = new ArrayList<>();

      for (int x = 0; x < width; x++) {
         for (int z = 0; z < height; z++) {
            this.setZoneIdAt(x, z, ZoneId.OPEN_OCEAN);
            this.setHeightAt(x, z, -4000.0F);
            this.setVisibleAt(x, z, false);
            this.setStageAt(x, z, -1);
            this.resNb[this.clampX(x)][this.clampZ(z)] = 250;
            this.setGamePlayElmtAt(x, z, new GamePlayElmt(GamePlayType.NONE));
         }
      }
   }

   public final void setAvatarStartPosition(Coord pos) {
      this.avatarStartPosition = pos;
   }

   public final Coord getAvatarStartPosition() {
      return this.avatarStartPosition;
   }

   public final void setStartBasePosition(Coord pos) {
      this.startBasePosition = pos;
   }

   public final void addAbandonedBasePosition(Coord pos) {
      this.abandonedBasePosition.add(pos);
   }

   public final void addDroidPosition(Coord pos) {
      this.droidPosition.add(pos);
   }

   public final Coord getStartBasePosition() {
      return this.startBasePosition;
   }

   public final ArrayList<Coord> getAbandonedBasePositions() {
      return this.abandonedBasePosition;
   }

   public final ArrayList<Coord> getDroidPositions() {
      return this.droidPosition;
   }

   public final float getRandLevelGenCliff() {
      return this.randLevelGenCliff;
   }

   public final float getRandGenDunes() {
      return this.randGenDunes;
   }

   public final float getRandGenRock() {
      return this.randGenRock;
   }

   public final float getRandGenSource() {
      return this.randGenSource;
   }

   public final float getRandLandscapeAlga() {
      return this.randLandscapeAlga;
   }

   public final float getRandLandscapeGiantAlga() {
      return this.randLandscapeGiantAlga;
   }

   public final void setRandLevelGenCliff(float value) {
      this.randLevelGenCliff = value;
   }

   public final void setRandGenDunes(float value) {
      this.randGenDunes = value;
   }

   public final void setRandGenRock(float value) {
      this.randGenRock = value;
   }

   public final void setRandGenSource(float value) {
      this.randGenSource = value;
   }

   public final void setRandLandscapeAlga(float value) {
      this.randLandscapeAlga = value;
   }

   public final void setRandLandscapeGiantAlga(float value) {
      this.randLandscapeGiantAlga = value;
   }

   public final int getWidth() {
      return this.WIDTH;
   }

   public final int getHeight() {
      return this.HEIGHT;
   }

   public final float getHeightAt(int x, int z) {
      return this.heightMap[this.clampX(x)][this.clampZ(z)];
   }

   public final boolean isVisibleAt(int x, int z) {
      return this.mapVisible[this.clampX(x)][this.clampZ(z)];
   }

   public final ZoneId getZoneIdAt(int x, int z) {
      return this.zoneIdMap[this.clampX(x)][this.clampZ(z)];
   }

   public final int getStageAt(int x, int z) {
      return this.stage[this.clampX(x)][this.clampZ(z)];
   }

   public final int getResourceCountAt(int x, int z) {
      return this.resNb[this.clampX(x)][this.clampZ(z)];
   }

   public final GamePlayElmt getGamePlayElmtAt(int x, int z) {
      return this.gamePlayElmt[this.clampX(x)][this.clampZ(z)];
   }

   public final boolean hasInteractionUsed(InteractionUsed interaction) {
      for (int i = 0; i < this.interactionUsed.size(); i++) {
         if (this.interactionUsed.get(i).getCoord().equals(interaction.getCoord()) && this.interactionUsed.get(i).getInteractiveElmt() == interaction.getInteractiveElmt()) {
            return true;
         }
      }

      return false;
   }

   public final float getDayTime() {
      return this.dayTime;
   }

   public final float getNightTime() {
      return this.nightTime;
   }

   public final EnemyGenerator.SpawningLevel getSpawning() {
      return this.spawning;
   }

   public final void setHeightAt(int x, int z, float height) {
      this.heightMap[this.clampX(x)][this.clampZ(z)] = height;
   }

   public final void setVisibleAt(int x, int z, boolean visible) {
      this.mapVisible[this.clampX(x)][this.clampZ(z)] = visible;
   }

   public final void setZoneIdAt(int x, int z, ZoneId zone) {
      this.zoneIdMap[this.clampX(x)][this.clampZ(z)] = zone;
   }

   public final void setStageAt(int x, int z, int stageVal) {
      this.stage[this.clampX(x)][this.clampZ(z)] = stageVal;
   }

   public final void decrementResourceAt(int x, int z) {
      if (this.resNb[this.clampX(x)][this.clampZ(z)] > 0) {
         this.resNb[this.clampX(x)][this.clampZ(z)]--;
      }
   }

   public final void setGamePlayElmtAt(int x, int z, GamePlayElmt elmt) {
      this.gamePlayElmt[this.clampX(x)][this.clampZ(z)] = elmt;
   }

   public final void addInteractionUsed(InteractionUsed interaction) {
      this.interactionUsed.add(interaction);
   }

   public final void setDayTime(float time) {
      this.dayTime = time;
   }

   public final void setNightTime(float time) {
      this.nightTime = time;
   }

   public final void setSpawning(EnemyGenerator.SpawningLevel level) {
      this.spawning = level;
   }

   private int clampX(int x) {
      if (x < 0) {
         x = 0;
      }

      if (x >= this.WIDTH) {
         x = this.WIDTH - 1;
      }

      return x;
   }

   private int clampZ(int z) {
      if (z < 0) {
         z = 0;
      }

      if (z >= this.HEIGHT) {
         z = this.HEIGHT - 1;
      }

      return z;
   }
}
