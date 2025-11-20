package game.world.structure;

import game.chunks.InteractiveElmt;
import game.util.Coord;
import java.io.Serializable;

public class InteractionUsed implements Serializable {
   private static final long serialVersionUID = -4182708407766018801L;
   private Coord coord;
   private InteractiveElmt interactiveElmt;

   public InteractionUsed(Coord coord, InteractiveElmt element) {
      this.coord = coord;
      this.interactiveElmt = element;
   }

   public final Coord getCoord() {
      return this.coord;
   }

   public final InteractiveElmt getInteractiveElmt() {
      return this.interactiveElmt;
   }
}
