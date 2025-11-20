package game.chunks.chunkElements;

import game.environment.EnvironmentManager;
import game.environment.particle.BurstParticle;
import game.manager.TextureManager;
import game.render.ModelLoader;
import game.render.Vbo;
import game.util.Point;

import org.lwjgl.opengl.GL11;

public final class RockRing extends ChunkElement {
   private float emitTimer;
   private static Vbo vbo;

   public static void loadAssets() {
      vbo = ModelLoader.loadMesh("rock1");
   }

   @Override
   public final void update(float deltaTime) {
      this.emitTimer += deltaTime;
      if (this.emitTimer >= 0.15F) {
         this.emitTimer -= 0.15F;

         for (int i = 0; i < 3; i++) {
            EnvironmentManager.addBurstParticle(new BurstParticle(null, new Point((Math.random() - 0.5) * 0.3F, 1.0, (Math.random() - 0.5) * 0.3F), 40.0F, 3.5F));
         }
      }
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(2.5F, 2.5F, 2.5F);

      for (int i = 0; i < 5; i++) {
         GL11.glRotatef(72.0F, 0.0F, 1.0F, 0.0F);
         GL11.glTranslatef(2.0F, 0.0F, 0.0F);
         vbo.render();
         GL11.glTranslatef(-2.0F, 0.0F, 0.0F);
      }

      GL11.glPopMatrix();
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.rockTexture);
   }
}
