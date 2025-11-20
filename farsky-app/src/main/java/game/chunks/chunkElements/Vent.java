package game.chunks.chunkElements;

import game.collision.AABB;
import game.environment.EnvironmentManager;
import game.environment.particle.MovingParticle;
import game.render.ModelLoader;
import game.render.Vbo;
import game.util.Point;

import org.lwjgl.opengl.GL11;

public final class Vent extends ChunkElement {
   private static Vbo vbo;
   private static int textureId;
   private float yRotation = 0.0F;
   private float scale = 1.0F;
   private float brightness = 1.0F;
   private float emitTimer = 0.0F;
   private Point emitPos;

   public Vent(Point pos, int offsetX, int offsetZ) {
      this.position = new Point();
      this.position.set(pos);
      this.emitPos = pos.plus(offsetX, 0.0F, offsetZ);
      this.yRotation = (float)(Math.random() * 180.0);
      this.scale = 14.0F + (float)(Math.random() * 2.0);
      this.brightness = (float)(Math.random() / 4.0 + 0.25);
   }

   public static void loadAssets() {
      vbo = ModelLoader.loadMesh("hole");
      textureId = ModelLoader.loadTexture("hole");
   }

   @Override
   public final void update(float deltaTime) {
      this.emitTimer += deltaTime;
      if (this.emitTimer > 0.1F) {
         this.emitTimer -= 0.1F;

         for (int i = 0; i < 2; i++) {
            EnvironmentManager.addMovingParticle(
               new MovingParticle(
                  this.emitPos.plus(new Point(Math.random() - 0.5, 10.0 + Math.random(), Math.random() - 0.5).scaled(1.0F)),
                  new Point(Math.random() - 0.5, 0.0, Math.random() - 0.5).scaled(0.5F).plus(0.0F, 1.0F, 0.0F),
                  40.0F,
                  2.0F
               )
            );
         }
      }
   }

   @Override
   public final AABB getBoundingBox() {
      return new AABB(this.position, 20.0F, 30.0F, 20.0F);
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glColor4f(this.brightness, this.brightness, this.brightness, 1.0F);
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.scale, this.scale, this.scale);
      GL11.glRotatef(this.yRotation, 0.0F, 1.0F, 0.0F);
      vbo.render();
      GL11.glPopMatrix();
   }
}
