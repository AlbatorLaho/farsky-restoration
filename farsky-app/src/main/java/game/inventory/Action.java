package game.inventory;

public enum Action {
   NONE(""),
   USE("Use"),
   EAT("Eat"),
   EQUIP("Equip");

   String name = "";

   private Action(String name) {
      this.name = name;
   }

   public final String getName() {
      return this.name;
   }
}
