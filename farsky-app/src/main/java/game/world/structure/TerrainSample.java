package game.world.structure;

import game.util.Point;
import game.util.Vec4;

public final class TerrainSample {
   public float height = 0.0F;
   public Vec4 color = new Vec4();
   public Point normal = new Point();
   public ZoneId zoneId = ZoneId.OPEN_OCEAN;

   public final void accumulate(TerrainSample other) {
      this.height = this.height + other.height;
      this.color = this.color.add(other.color);
      this.normal = this.normal.plus(other.normal);
      this.zoneId = other.zoneId;
   }
}
