package game.shader;

import game.manager.TextureManager;
import org.lwjgl.opengl.Display;
import game.render.FullscreenQuad;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.GLU;

public final class BloomEffect {
   private static int bloomTextureId = -1;
   private static int sceneTextureId = -1;
   private static int downsampleFactor = 2;

   public static void apply(float brightPassLevel) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      sceneTextureId = TextureManager.captureFramebuffer(sceneTextureId);
      setDownsampledViewport(downsampleFactor);
      Shaders.brightPassShader.bind();
      Shaders.setUniform("brightPassLevel", brightPassLevel);
      TextureManager.drawTexture(sceneTextureId, Display.getWidth() / downsampleFactor, Display.getHeight() / downsampleFactor);
      BlurEffect.apply(
         bloomTextureId = TextureManager.captureFramebuffer(bloomTextureId, Display.getWidth() / downsampleFactor, Display.getHeight() / downsampleFactor, true),
         4.0F / Display.getWidth() / downsampleFactor,
         Display.getWidth() / downsampleFactor,
         Display.getHeight() / downsampleFactor,
         0.0F,
         1.0F
      );
      bloomTextureId = TextureManager.captureFramebuffer(bloomTextureId, Display.getWidth() / downsampleFactor, Display.getHeight() / downsampleFactor, true);
      setDownsampledViewport(1);
      Shaders.unbind();
      Shaders.bloomAdditiveShader.bind();
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, sceneTextureId);
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL13.glActiveTexture(GL13.GL_TEXTURE1);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, bloomTextureId);
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FullscreenQuad.drawFlipped();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Shaders.unbind();
   }

   private static void setDownsampledViewport(int divisor) {
      GL11.glViewport(0, 0, Display.getWidth() / divisor, Display.getHeight() / divisor);
      GL11.glMatrixMode(GL11.GL_PROJECTION);
      GL11.glLoadIdentity();
      GLU.gluOrtho2D(0.0F, Display.getWidth() / divisor, Display.getHeight() / divisor, 0.0F);
   }
}
