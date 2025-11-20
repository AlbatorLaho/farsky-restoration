package game.environment;

import game.render.ModelLoader;
import game.render.Vbo;
import game.util.PhysicsPoint;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class FlyingRock {
   private PhysicsPoint physics;
   private static Vbo model;
   private float lifetime;

   public static void loadModel() {
      model = ModelLoader.loadMesh("rock0");
   }

   public FlyingRock(Point position, Point velocity) {
      this.physics = new PhysicsPoint(
         position,
         velocity,
         new Point(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).scaled(360.0F),
         new Point(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).scaled(360.0F),
         100.0F,
         0.5F + (float)Math.random()
      );
      this.lifetime = 2.0F;
   }

   public final void update(float delta) {
      this.physics.update(delta);
      this.lifetime -= delta;
   }

   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(this.physics.getPosition().x, this.physics.getPosition().y, this.physics.getPosition().z);
      this.physics.getAngularPosition().applyGLRotation();
      GL11.glScalef(this.physics.getScale(), this.physics.getScale(), this.physics.getScale());
      model.render();
      GL11.glPopMatrix();
   }

   public final boolean isDone() {
      return this.lifetime <= 0.0F;
   }
}
