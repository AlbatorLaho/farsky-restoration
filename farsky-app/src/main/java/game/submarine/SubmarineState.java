package game.submarine;

import game.util.Point;
import game.util.State;
import java.io.Serializable;

public class SubmarineState implements Serializable {
   private static final long serialVersionUID = 7532255741815328412L;
   public State state = new State();
   public Point rot;

   public SubmarineState() {
      this(new Point());
   }

   public SubmarineState(Point pos) {
      this.state.pos = pos.copy();
      this.rot = new Point();
   }

   public final SubmarineState copy() {
      SubmarineState copy = new SubmarineState();
      copy.state.copyFrom(this.state);
      copy.rot = this.rot.copy();
      return copy;
   }
}
