package game.environment;

import game.manager.Camera;
import game.manager.GameTime;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class LightRay {
   private float alpha;
   private Point position;
   private float angle;
   private float spreadRadius;
   private float phaseAngle;
   private float tiltX;
   private float tiltZ;
   private float height;
   private float width;
   private Point origin;
   private Point color;

   public LightRay(float height, float width, float spreadRadius, float tiltX, float tiltZ, Point origin) {
      this.height = height;
      this.width = width;
      this.spreadRadius = spreadRadius;
      this.tiltX = tiltX;
      this.tiltZ = tiltZ;
      this.origin = origin;
      this.color = new Point(1.0F, 1.0F, 1.0F);
      this.angle = (float)(Math.random() * 360.0);
      this.phaseAngle = (float)(Math.random() * 360.0);
      this.position = Camera.getPosition().plus(new Point(spreadRadius * Math.cos(this.angle), 100.0, spreadRadius * Math.sin(this.angle)));
      this.alpha = (float)((Math.cos(this.phaseAngle) + 1.0) / 2.0);
   }

   public final void update(float delta) {
      this.phaseAngle += delta * 0.7F;
      if (this.phaseAngle > Math.PI * 2) {
         this.phaseAngle = (float)(this.phaseAngle - (Math.PI * 2));
      }

      this.alpha = (float)((Math.cos(this.phaseAngle) + 1.0) / 2.0) / 2.5F;
      this.alpha = Math.min(this.alpha, DepthAtmosphere.getLightRayAlpha());
      if (this.alpha < 0.02F) {
         if (this.origin == null) {
            this.position = Camera.getPosition().plus(new Point(this.spreadRadius * Math.cos(this.angle), 0.0, this.spreadRadius * Math.sin(this.angle)).scaled((float)Math.random() * 0.7F + 0.1F));
         } else {
            this.position = this.origin.plus(new Point(this.spreadRadius * Math.cos(this.angle), 0.0, this.spreadRadius * Math.sin(this.angle)).scaled((float)Math.random() * 0.7F + 0.1F));
         }

         this.position.y = Math.min(this.position.y, -this.height);
      }
   }

   public final void render() {
      if (this.alpha > 0.02F) {
         float lightLevel = Math.min(GameTime.getLightLevel() * 4.0F - 3.0F, 1.0F);
         GL11.glPushMatrix();
         GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
         GL11.glRotatef(this.tiltX, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(this.tiltZ, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(Camera.getYaw(), 0.0F, 1.0F, 0.0F);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glColor4f(this.color.x, this.color.y, this.color.z, this.alpha * lightLevel);
         GL11.glVertex3f(-this.width, this.height / 2.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glColor4f(this.color.x, this.color.y, this.color.z, 0.0F);
         GL11.glVertex3f(-this.width, 0.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glColor4f(this.color.x, this.color.y, this.color.z, 0.0F);
         GL11.glVertex3f(this.width, 0.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glColor4f(this.color.x, this.color.y, this.color.z, this.alpha * lightLevel);
         GL11.glVertex3f(this.width, this.height / 2.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glColor4f(this.color.x, this.color.y, this.color.z, this.alpha * lightLevel);
         GL11.glVertex3f(-this.width, this.height / 2.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glColor4f(this.color.x, this.color.y, this.color.z, 0.0F);
         GL11.glVertex3f(-this.width, this.height, 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glColor4f(this.color.x, this.color.y, this.color.z, 0.0F);
         GL11.glVertex3f(this.width, this.height, 0.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glColor4f(this.color.x, this.color.y, this.color.z, this.alpha * lightLevel);
         GL11.glVertex3f(this.width, this.height / 2.0F, 0.0F);
         GL11.glEnd();
         GL11.glPopMatrix();
      }
   }
}
