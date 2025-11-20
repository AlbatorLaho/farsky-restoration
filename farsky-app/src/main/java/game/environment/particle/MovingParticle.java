package game.environment.particle;

import game.manager.Camera;
import game.util.Point;
import game.util.UnitQuad;
import org.lwjgl.opengl.GL11;

public final class MovingParticle extends Particle {
   private Point direction;
   private float speed;
   private float elapsedTime;
   private float lifetime;
   private float sizeScale;
   private float rotation;

   public MovingParticle(Point position, Point direction, float speed, float lifetime) {
      super(position, 0.5F);
      this.direction = direction;
      this.speed = speed;
      this.lifetime = lifetime;
      this.elapsedTime = 0.0F;
      this.sizeScale = 1.0F;
      this.rotation = (float)Math.random() * 360.0F;
   }

   public final void update(float delta) {
      this.sizeScale = 1.0F - this.elapsedTime / this.lifetime;
      this.elapsedTime += delta;
      this.position = this.position.plus(this.direction.scaled(delta * this.speed));
   }

   public final void render() {
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, this.alpha);
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      Camera.applyYawPitch();
      GL11.glRotatef(this.rotation, 0.0F, 0.0F, 1.0F);
      GL11.glScalef(this.sizeScale * 1.5F, this.sizeScale * 1.5F, 0.0F);
      UnitQuad.render();
      GL11.glPopMatrix();
   }

   public final boolean isDone() {
      return this.elapsedTime >= this.lifetime;
   }
}
