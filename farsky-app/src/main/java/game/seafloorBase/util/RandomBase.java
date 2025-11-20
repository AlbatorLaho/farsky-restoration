package game.seafloorBase.util;

final class RandomBase {
   public static enum RoomType {
      BASIC,
      WORKSHOP,
      STAIRWELL,
      PLACEHOLDER,
      LOUNGE,
      GARDEN,
      HUB,
      STORAGE;
   }

   public RoomType roomType;

   public RandomBase(RoomType roomType) {
      this.roomType = roomType;
   }
}
