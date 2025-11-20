package game.player;

public enum HandState {
   IDLE(0),
   OPEN(1),
   EXTENDED(2),
   HOLDING(3),
   FIRING(4);

   private final int keyFrame;

   private HandState(int keyFrame) {
      this.keyFrame = keyFrame;
   }

   public final int getKeyFrame() {
      return this.keyFrame;
   }
}
