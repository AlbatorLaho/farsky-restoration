package game.chunks.chunkElements;

import game.environment.EnvironmentManager;
import game.environment.particle.MovingParticle;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.render.QuadVbo;
import game.render.Vertex;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Coord;
import game.util.Point;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class Seaweed extends ChunkElement {
   private Point normalAngles;
   private float scale = 1.0F;
   private float openAmount = 1.0F;
   private Point worldPos;
   private static QuadVbo quadVbo;

   public Seaweed(Point pos, Point worldPos, Point normalDir) {
      this.position = new Point();
      this.position.set(pos);
      this.worldPos = worldPos.copy();
      this.scale = 2.0F + (float)(Math.random() * 3.0);
      this.normalAngles = normalDir.toAngles();
   }

   public static void loadAssets() {
      ArrayList<Vertex> vertices = new ArrayList<>();

      for (int i = 0; i < 3; i++) {
         vertices.add(
            new Vertex(
               new Point(-1.0F * (float)Math.cos((Math.PI * 2.0 / 3.0) * i), 1.0F, -1.0F * (float)Math.sin((Math.PI * 2.0 / 3.0) * i)), new Coord(0, 0)
            )
         );
         vertices.add(
            new Vertex(
               new Point(-1.0F * (float)Math.cos((Math.PI * 2.0 / 3.0) * i), 0.0F, -1.0F * (float)Math.sin((Math.PI * 2.0 / 3.0) * i)), new Coord(0, 1)
            )
         );
         vertices.add(
            new Vertex(
               new Point(1.0F * (float)Math.cos((Math.PI * 2.0 / 3.0) * i), 0.0F, 1.0F * (float)Math.sin((Math.PI * 2.0 / 3.0) * i)), new Coord(1, 1)
            )
         );
         vertices.add(
            new Vertex(
               new Point(1.0F * (float)Math.cos((Math.PI * 2.0 / 3.0) * i), 1.0F, 1.0F * (float)Math.sin((Math.PI * 2.0 / 3.0) * i)), new Coord(1, 0)
            )
         );
      }

      quadVbo = new QuadVbo(vertices);
   }

   @Override
   public final void update(float deltaTime) {
      if (GameScene.avatar != null) {
         if (this.worldPos.distanceTo(GameScene.avatar.getCameraPos()) < 50.0F) {
            if (this.openAmount == 1.0F) {
               for (int i = 0; i < 3; i++) {
                  EnvironmentManager.addMovingParticle(
                     new MovingParticle(
                        this.worldPos.plus(new Point(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5)),
                        new Point(Math.random() - 0.5, 1.0, Math.random() - 0.5),
                        10.0F,
                        1.5F
                     )
                  );
               }

               SoundManager.playSound(SoundManager.sfxCoralHide, this.worldPos, 1.0F, 0.015F);
            }

            if (this.openAmount > 0.0F) {
               this.openAmount -= deltaTime * 15.0F;
               return;
            }

            this.openAmount = 0.0F;
            return;
         }

         if (this.openAmount < 1.0F) {
            this.openAmount += deltaTime * 8.0F;
            return;
         }

         this.openAmount = 1.0F;
      }
   }

   @Override
   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
      GL11.glScalef(this.scale * this.openAmount, this.scale * this.openAmount, this.scale * this.openAmount);
      GL11.glRotatef(this.normalAngles.y, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(this.normalAngles.x + 90.0F, 1.0F, 0.0F, 0.0F);
      quadVbo.render();
      GL11.glPopMatrix();
   }

   public static void beginBatch() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.coralAlpha);
      Shaders.setUniform("highlight", true);
      Shaders.setUniform("wave", true);
      Shaders.setUniform("height", 4.0);
      Shaders.setUniform("factor", 1.5);
      Shaders.setUniform("amplitude", 8.0);
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glDepthMask(false);
   }

   public static void endBatch() {
      Shaders.setUniform("highlight", false);
      Shaders.setUniform("wave", false);
      GL11.glEnable(GL11.GL_CULL_FACE);
      GL11.glDepthMask(true);
   }
}
