package game.saving;

import game.manager.GameMode;
import java.io.Serializable;

public class SlotPresentation implements Serializable {
   private static final long serialVersionUID = -4058766155671767702L;
   private int deepness = 0;
   private int minPlayed = 0;
   private int inGameMinTime = 0;
   private GameMode gameMode;
   private int pieceNb;

   public SlotPresentation(int depth, int minutesPlayed, int inGameMinutes, GameMode gameMode, int pieceCount) {
      this.deepness = depth;
      this.minPlayed = minutesPlayed;
      this.inGameMinTime = inGameMinutes;
      this.gameMode = gameMode;
      this.pieceNb = pieceCount;
   }

   public final int getDepth() {
      return this.deepness;
   }

   public final int getMinutesPlayed() {
      return this.minPlayed;
   }

   public final int getInGameMinutes() {
      return this.inGameMinTime;
   }

   public final GameMode getGameMode() {
      return this.gameMode;
   }

   public final int getPieceCount() {
      return this.pieceNb;
   }
}
