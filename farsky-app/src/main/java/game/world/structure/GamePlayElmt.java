package game.world.structure;

import game.inventory.types.Inventory;
import game.submarine.SubmarinePiece;
import java.io.Serializable;

public class GamePlayElmt implements Serializable {
   private static final long serialVersionUID = -6959725856641856734L;
   private GamePlayType gamePlayType;
   private Inventory inventory;
   private SubmarinePiece submarinePiece;
   private int nb;

   public GamePlayElmt(GamePlayType type) {
      this.gamePlayType = type;
   }

   public GamePlayElmt(GamePlayType type, int count) {
      this.gamePlayType = type;
      this.nb = count;
   }

   public GamePlayElmt(GamePlayType type, int count, Inventory inventory) {
      this.gamePlayType = type;
      this.nb = count;
      this.inventory = inventory;
   }

   public GamePlayElmt(GamePlayType type, Inventory inventory) {
      this.gamePlayType = type;
      this.inventory = inventory;
   }

   public GamePlayElmt(GamePlayType type, SubmarinePiece piece) {
      this.submarinePiece = piece;
      this.gamePlayType = type;
   }

   public final GamePlayType getType() {
      return this.gamePlayType;
   }

   public final Inventory getInventory() {
      return this.inventory;
   }

   public final SubmarinePiece getSubmarinePiece() {
      return this.submarinePiece;
   }

   public final int getCount() {
      return this.nb;
   }

   public final Inventory setInventory(Inventory inventory) {
      return this.inventory = inventory;
   }
}
