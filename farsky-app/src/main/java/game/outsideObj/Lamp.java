package game.outsideObj;

import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.collision.AABB;
import game.inventory.ItemType;
import game.manager.TextureManager;
import game.manager.Camera;
import game.manager.GameTime;
import game.util.Point;
import game.util.State;
import game.util.UnitQuad;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.lwjgl.opengl.GL11;

public class Lamp extends OutsideObj {
   private static final long serialVersionUID = -4053344332453526065L;
   private static transient Vbo lampMesh;
   private static transient int lampTexture;

   public static void loadModels() {
      lampMesh = ModelLoader.loadMesh("lamp");
      lampTexture = ModelLoader.loadTexture("lamp");
   }

   public Lamp(Point position) {
      this.type = ItemType.LAMP;
      this.pos = position.plus(0.0F, -3.0F, 0.0F);
      this.aabb = new AABB(position, 8.0F, 27.0F, 8.0F);
   }

   @Override
   public final void tick(float delta) {
   }

   @Override
   public final void drawModel() {
      Shaders.setUniform("alphaLight", true);
      Shaders.setUniform("lightColor", new Point(1.0F, 1.0F, 0.4F));
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslatef(this.pos.x, this.pos.y, this.pos.z);
      GL11.glScalef(12.0F, 12.0F, 12.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, lampTexture);
      lampMesh.render();
      GL11.glPopMatrix();
      Shaders.setUniform("alphaLight", false);
   }

   @Override
   public final void drawEffects() {
      Shaders.setUniform("emissive", true);
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glDepthMask(false);
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glTranslatef(this.pos.x, this.pos.y + 17.0F, this.pos.z);
      float glowSize = 25.0F + (float)Math.cos(GameTime.elapsedMillis / 1000.0F + (this.pos.x + this.pos.z) * 0.01F) * 5.0F;
      GL11.glScalef(glowSize, glowSize, glowSize);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.shine);
      Camera.applyYawPitch();
      UnitQuad.render();
      GL11.glPopMatrix();
      GL11.glDepthMask(true);
      GL11.glEnable(GL11.GL_CULL_FACE);
      Shaders.setUniform("emissive", false);
   }

   @Override
   public final State resolveCollision(State from, State to) {
      return this.aabb.resolveCollision(from, to);
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.type = ItemType.LAMP;
   }

   @Override
   public final boolean canPickUp() {
      return true;
   }
}
