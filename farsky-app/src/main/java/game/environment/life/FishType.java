package game.environment.life;

public enum FishType {
   FISH(false),
   FRILLED_SHARK(false),
   BARRACUDA(false),
   SHARK(false),
   MANTA_RAY(false),
   ANGLERFISH(false),
   WHALE(false),
   TUNA(true),
   DOLPHIN(true);

   private boolean abyss;

   private FishType(boolean isAbyss) {
      this.abyss = isAbyss;
   }

   public final boolean isAbyss() {
      return this.abyss;
   }
}
