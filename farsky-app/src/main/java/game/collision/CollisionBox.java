package game.collision;

import game.util.Point;
import org.lwjgl.opengl.GL11;

public class CollisionBox {
   private AABB bounds;
   private Point position;
   private Point rotation;

   public CollisionBox(AABB bounds, Point position, Point rotation) {
      this.bounds = bounds;
      this.position = position;
      this.rotation = rotation;
   }

   public final Point getPosition() {
      return this.position;
   }

   public final Point getRotation() {
      return this.rotation;
   }

   public final AABB getBounds() {
      return this.bounds;
   }

   public static void renderCube() {
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(0.5F, 0.5F, -0.5F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(-0.5F, 0.5F, -0.5F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(-0.5F, 0.5F, 0.5F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(0.5F, 0.5F, 0.5F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-0.5F, -0.5F, -0.5F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(0.5F, -0.5F, -0.5F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(0.5F, -0.5F, 0.5F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-0.5F, -0.5F, 0.5F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-0.5F, 0.5F, -0.5F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(0.5F, 0.5F, -0.5F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(0.5F, -0.5F, -0.5F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-0.5F, -0.5F, -0.5F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(0.5F, 0.5F, 0.5F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(-0.5F, 0.5F, 0.5F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(-0.5F, -0.5F, 0.5F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(0.5F, -0.5F, 0.5F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-0.5F, -0.5F, 0.5F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(-0.5F, 0.5F, 0.5F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(-0.5F, 0.5F, -0.5F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-0.5F, -0.5F, -0.5F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(0.5F, 0.5F, 0.5F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(0.5F, -0.5F, 0.5F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(0.5F, -0.5F, -0.5F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(0.5F, 0.5F, -0.5F);
      GL11.glEnd();
   }
}
