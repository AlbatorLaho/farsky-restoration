package game.enemy.lightning;

import game.manager.Camera;
import game.util.Point;
import game.util.UnitQuad;
import org.lwjgl.opengl.GL11;

public final class LightningFlash {
   private Point position;
   private float size;
   private float rotation;

   public LightningFlash(Point position, float size) {
      this.position = position;
      this.size = size;
      this.rotation = (float)(Math.random() * 360.0);
   }

   public final void tick(float deltaTime) {
      this.size -= deltaTime * 80.0F;
   }

   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      Camera.applyYawPitch();
      GL11.glRotatef(this.rotation, 0.0F, 0.0F, 1.0F);
      GL11.glScalef(this.size, this.size, this.size);
      UnitQuad.render();
      GL11.glPopMatrix();
   }

   public final boolean isDone() {
      return this.size <= 0.0F;
   }
}
