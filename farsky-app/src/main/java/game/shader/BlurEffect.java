package game.shader;

import game.manager.TextureManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class BlurEffect {
   private static int intermediateTextureId = -1;
   private static int capturedTextureId = -1;

   public static void apply(float bwPercent, float luminosity) {
      capturedTextureId = TextureManager.captureFramebuffer(capturedTextureId);
      apply(capturedTextureId, 5.0E-4F, 0.0F, 1.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
      TextureManager.drawTexture(capturedTextureId, Display.getWidth(), Display.getHeight());
   }

   public static void apply(int textureId, float blurSize) {
      apply(textureId, blurSize, Display.getWidth(), Display.getHeight(), 0.0F, 1.0F);
   }

   public static void apply(int textureId, float blurSize, float bwPercent, float luminosity) {
      apply(textureId, blurSize, Display.getWidth(), Display.getHeight(), bwPercent, luminosity);
   }

   public static void apply(int textureId, float blurSize, int width, int height, float bwPercent, float luminosity) {
      Shaders.blurShader.bind();
      Shaders.setUniform("blurSize", blurSize);
      Shaders.setUniform("blackAndWhitePercent", bwPercent);
      Shaders.setUniform("luminosity", luminosity);
      Shaders.setUniform("vertical", false);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      TextureManager.drawTexture(textureId, width, height);
      intermediateTextureId = TextureManager.captureFramebuffer(intermediateTextureId, width, height, true);
      Shaders.setUniform("vertical", true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      TextureManager.drawTexture(intermediateTextureId, width, height);
      Shaders.unbind();
   }
}
