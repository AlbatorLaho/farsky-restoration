package game.shader;

import game.manager.TextureManager;
import game.render.FullscreenQuad;
import org.lwjgl.opengl.GL11;

public final class BlackBordersEffect {
   private static int textureId = -1;

   public static void render() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
      textureId = TextureManager.captureFramebuffer(textureId);
      Shaders.blackBordersShader.bind();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
      FullscreenQuad.drawFlipped();
      Shaders.unbind();
   }
}
