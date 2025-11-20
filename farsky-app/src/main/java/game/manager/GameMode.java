package game.manager;

public enum GameMode {
   ADVENTURE("Adventurer", false, true, true, false, true, false),
   ADVENTURE_DONE("Adventurer: Done", false, false, true, true, true, false),
   SURVIVOR("Survivor", true, true, false, false, true, false),
   SURVIVOR_DONE("Survivor: Done", true, false, false, true, true, false),
   SANDBOX("Sandbox", false, false, false, true, false, true);

   private String name;
   private boolean oneLife;
   private boolean cinematic;
   private boolean showSubPieces;
   private boolean surfaceGameplay;
   private boolean hasDialog;
   private boolean craftableSubmarine;

   private GameMode(String name, boolean oneLife, boolean cinematic, boolean showSubPieces, boolean surfaceGameplay, boolean hasDialog, boolean craftableSubmarine) {
      this.name = name;
      this.oneLife = oneLife;
      this.cinematic = cinematic;
      this.showSubPieces = showSubPieces;
      this.surfaceGameplay = surfaceGameplay;
      this.hasDialog = hasDialog;
      this.craftableSubmarine = craftableSubmarine;
   }

   @Override
   public final String toString() {
      return this.name;
   }

   public final boolean isOneLife() {
      return this.oneLife;
   }

   public final boolean hasCinematic() {
      return this.cinematic;
   }

   public final boolean showsSubPieces() {
      return this.showSubPieces;
   }

   public final boolean hasSurfaceGameplay() {
      return this.surfaceGameplay;
   }

   public final boolean hasDialog() {
      return this.hasDialog;
   }

   public final boolean hasCraftableSubmarine() {
      return this.craftableSubmarine;
   }
}
