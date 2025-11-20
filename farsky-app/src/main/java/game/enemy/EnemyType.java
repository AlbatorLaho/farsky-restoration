package game.enemy;

public enum EnemyType {
   KRAKEN(true, "a Kraken"),
   JELLYFISH(false, "a jellyfish"),
   SHARK(false, "a shark"),
   GREAT_WHITE(false, "a great white shark"),
   HAMMERHEAD(false, "a great hammerhead"),
   BARRACUDA(false, "a barracuda"),
   ANGLERFISH(false, "an anglerfish"),
   FRILLED_SHARK(false, "a frilled shark"),
   DEEP_SEA_FISH(false, "a deep-sea fish");

   private boolean boss;
   private String name;

   private EnemyType(boolean boss, String name) {
      this.boss = boss;
      this.name = name;
   }

   public final boolean isBoss() {
      return this.boss;
   }

   public final String getName() {
      return this.name;
   }
}
