package game.shader;

import game.manager.TextureManager;
import game.render.FullscreenQuad;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public final class CrackEffect {
   private static int textureId = -1;

   public static void render(float percent) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      textureId = TextureManager.captureFramebuffer(textureId);
      Shaders.crackEffectShader.bind();
      Shaders.setUniform("percent", percent);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
      GL13.glActiveTexture(GL13.GL_TEXTURE1);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.crack);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FullscreenQuad.drawFlipped();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Shaders.unbind();
   }
}
