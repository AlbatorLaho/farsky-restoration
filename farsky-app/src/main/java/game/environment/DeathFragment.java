package game.environment;

import game.util.PhysicsPoint;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class DeathFragment {
   private PhysicsPoint physics;
   private float alpha = 1.0F;

   public DeathFragment(Point position, float speed) {
      float angle = (float)(Math.random() * Math.PI * 2.0);
      Point velocity = new Point(Math.cos(angle) * speed, 15.0 + speed * Math.random(), Math.sin(angle) * speed);
      this.physics = new PhysicsPoint(position.copy(), velocity, new Point(), new Point(), 10.0F, 1.0F + (float)Math.random() * 2.0F);
   }

   public final void render() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, this.alpha);
      GL11.glLineWidth(this.physics.getScale());
      GL11.glBegin(GL11.GL_LINES);
      GL11.glVertex3f(this.physics.getPosition().x, this.physics.getPosition().y, this.physics.getPosition().z);
      GL11.glVertex3f(this.physics.getPosition().x + this.physics.getVelocity().x * 0.2F, this.physics.getPosition().y + this.physics.getVelocity().y * 0.2F, this.physics.getPosition().z + this.physics.getVelocity().z * 0.2F);
      GL11.glEnd();
   }

   public final void update(float delta) {
      this.alpha -= delta * 0.5F;
      this.physics.update(delta);
   }

   public final boolean isDone() {
      return this.alpha <= 0.0F;
   }
}
