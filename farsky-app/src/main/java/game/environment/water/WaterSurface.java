package game.environment.water;

import game.render.QuadVbo;
import game.render.Vbo;
import game.render.Vertex;
import game.shader.Shaders;
import game.manager.TextureManager;
import game.manager.Camera;
import game.manager.GameScene;
import game.manager.GameTime;
import game.sounds.SoundManager;
import game.util.Coord;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public final class WaterSurface {
   private static Vbo mesh;
   private static int soundSourceId = -1;
   private static float SOUND_RANGE = 100.0F;
   private static float waveHeight = 0.0F;

   public static void buildMesh() {
      ArrayList<Vertex> vertices = new ArrayList<>();

      for (int x = 0; x < 64; x++) {
         for (int z = 0; z < 64; z++) {
            vertices.add(new Vertex(new Point(-100.0F + x / 64.0F * 200.0F, 0.0F, -100.0F + z / 64.0F * 200.0F), new Coord(x / 64.0F, z / 64.0F)));
            vertices.add(
               new Vertex(new Point(-100.0F + x / 64.0F * 200.0F, 0.0F, -100.0F + (z + 1) / 64.0F * 200.0F), new Coord(x / 64.0F, (z + 1) / 64.0F))
            );
            vertices.add(
               new Vertex(
                  new Point(-100.0F + (x + 1) / 64.0F * 200.0F, 0.0F, -100.0F + (z + 1) / 64.0F * 200.0F),
                  new Coord((x + 1) / 64.0F, (z + 1) / 64.0F)
               )
            );
            vertices.add(
               new Vertex(new Point(-100.0F + (x + 1) / 64.0F * 200.0F, 0.0F, -100.0F + z / 64.0F * 200.0F), new Coord((x + 1) / 64.0F, z / 64.0F))
            );
         }
      }

      mesh = new QuadVbo(vertices);
   }

   public static void updateSound() {
      if (soundSourceId == -1) {
         soundSourceId = SoundManager.addLoopingSource(SoundManager.sfxWaves, null);
      }

      float camDepth = Math.abs(Camera.getPosition().y);
      if (camDepth < SOUND_RANGE) {
         if (!SoundManager.isLoopingSourcePlaying(soundSourceId)) {
            SoundManager.playLoopingSource(soundSourceId);
         }

         SoundManager.setLoopingSourceVolume(soundSourceId, 0.15F * Math.max(1.0F - camDepth / SOUND_RANGE, 0.0F));
      } else {
         SoundManager.stopLoopingSource(soundSourceId);
      }

      waveHeight = 0.0F;
      if (GameScene.avatar != null) {
         waveHeight += (float)(Math.cos(GameTime.elapsedMillis / 287.0F + GameScene.avatar.getCameraPos().x * 0.056F + GameScene.avatar.getCameraPos().z * 0.225F) * 0.7F);
         waveHeight += (float)(Math.sin(GameTime.elapsedMillis / 323.0F + GameScene.avatar.getCameraPos().x * 0.155F + GameScene.avatar.getCameraPos().z * 0.031F) * 0.3F);
         waveHeight += (float)(Math.cos(GameTime.elapsedMillis / 931.0F + GameScene.avatar.getCameraPos().x * 0.005F + GameScene.avatar.getCameraPos().z * 0.02F) * 5.0);
         waveHeight += (float)(Math.sin(GameTime.elapsedMillis / 675.0F + GameScene.avatar.getCameraPos().x * 0.05F + GameScene.avatar.getCameraPos().z * 0.03F) * 3.0);
      }
   }

   public final void render() {
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glPushMatrix();
      GL11.glTranslatef(Camera.getPosition().x, 0.0F, Camera.getPosition().z);
      Shaders.setUniform("water", false);
      Shaders.setUniform("directColor", true);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.clouds);
      float tileX = GameTime.getLightLevel();
      GL11.glColor4f(tileX, tileX, tileX, 1.0F - Camera.getPosition().y / -500.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-1800.0F, 700.0F, -1800.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-1800.0F, 700.0F, 1800.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(1800.0F, 700.0F, 1800.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(1800.0F, 700.0F, -1800.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      Shaders.setUniform("water", true);
      Shaders.setUniform("directColor", false);
      Shaders.setUniform("smallWaveCosFactorX", 0.056F);
      Shaders.setUniform("smallWaveCosFactorZ", 0.225F);
      Shaders.setUniform("smallWaveCosTimeDiv", 287.0);
      Shaders.setUniform("smallWaveCosGain", 0.7F);
      Shaders.setUniform("smallWaveSinFactorX", 0.155F);
      Shaders.setUniform("smallWaveSinFactorZ", 0.031F);
      Shaders.setUniform("smallWaveSinTimeDiv", 323.0);
      Shaders.setUniform("smallWaveSinGain", 0.3F);
      Shaders.setUniform("bigWaveCosFactorX", 0.005F);
      Shaders.setUniform("bigWaveCosFactorZ", 0.02F);
      Shaders.setUniform("bigWaveCosTimeDiv", 931.0);
      Shaders.setUniform("bigWaveCosGain", 5.0);
      Shaders.setUniform("bigWaveSinFactorX", 0.05F);
      Shaders.setUniform("bigWaveSinFactorZ", 0.03F);
      Shaders.setUniform("bigWaveSinTimeDiv", 675.0);
      Shaders.setUniform("bigWaveSinGain", 3.0);
      Shaders.setUniform("lightFactor", 1.7F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.water);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
      GL11.glPushMatrix();
      tileX = Camera.getPosition().x - Camera.getPosition().x % 200.0F;
      float tileZ = Camera.getPosition().z - Camera.getPosition().z % 200.0F;
      GL11.glTranslatef(tileX, 0.0F, tileZ);

      for (short xOffset = -600; xOffset <= 600; xOffset += 200) {
         for (short zOffset = -600; zOffset <= 600; zOffset += 200) {
            Shaders.setUniform("xOffset", tileX + xOffset);
            Shaders.setUniform("zOffset", tileZ + zOffset);
            GL11.glPushMatrix();
            GL11.glTranslatef(xOffset, 0.0F, zOffset);
            mesh.render();
            GL11.glPopMatrix();
         }
      }

      GL11.glPopMatrix();
      GL11.glEnable(GL11.GL_CULL_FACE);
   }

   public static float getWaveHeight() {
      return waveHeight;
   }
}
