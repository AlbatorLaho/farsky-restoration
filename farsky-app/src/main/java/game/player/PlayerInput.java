package game.player;

public final class PlayerInput {
   public boolean strafeLeft;
   public boolean strafeRight;
   public boolean moveForward;
   public boolean moveBackward;
   public boolean ascend;
   public boolean descend;
   public boolean primaryMouseDown = false;
   public boolean primaryMouseHeld = false;
   public boolean secondaryMouseDown = false;
   public boolean secondaryMouseHeld = false;
   public boolean interact = false;
   public boolean slot0 = false;
   public boolean slot1 = false;
   public boolean slot2 = false;
   public boolean slot3 = false;
   public boolean slot4 = false;
   public boolean slot5 = false;
   public boolean slot6 = false;
   public boolean slot7 = false;
   public float lookHorizontalDelta;
   public float lookVerticalDelta;

   public final void resetFrameInputs() {
      this.strafeLeft = false;
      this.strafeRight = false;
      this.moveForward = false;
      this.moveBackward = false;
      this.ascend = false;
      this.lookHorizontalDelta = 0.0F;
      this.lookVerticalDelta = 0.0F;
   }
}
