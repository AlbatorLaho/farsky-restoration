package game.outsideObj;

public enum ExtractorType {
   STANDARD(10.0F),
   OVERPOWERED(5.0F);

   private float timerThreshold;

   private ExtractorType(float timerThreshold) {
      this.timerThreshold = timerThreshold;
   }

   public final float getTimerThreshold() {
      return this.timerThreshold;
   }
}
