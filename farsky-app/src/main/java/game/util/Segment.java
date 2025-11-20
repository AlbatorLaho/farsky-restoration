package game.util;

public final class Segment {
   public Point start;
   public Point end;

   public Segment(Point start, Point end) {
      this.start = start;
      this.end = end;
   }

   public final float length() {
      return this.end.distanceTo(this.start);
   }

   public final void translate(Point offset) {
      this.start.add(offset);
      this.end.add(offset);
   }

   public final Segment copy() {
      return new Segment(this.start.copy(), this.end.copy());
   }
}
