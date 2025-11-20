package game.environment;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

import game.manager.TextureManager;
import game.shader.Shaders;

public final class LightRayField {
   private ArrayList<LightRay> rays = new ArrayList<>();
   private static float rayHeight = 1000.0F;
   private static float rayWidth = 20.0F;

   public LightRayField() {
      for (int i = 0; i < 12; i++) {
         this.rays.add(new LightRay(rayHeight, rayWidth, DepthAtmosphere.getFogDistance() - (float)(Math.random() * 100.0), -15.0F, -15.0F, null));
      }
   }

   public final void update(float delta) {
      for (int i = 0; i < this.rays.size(); i++) {
         this.rays.get(i).update(delta);
      }
   }

   public final void render() {
      Shaders.setUniform("directColor", true);
      GL11.glDepthMask(false);
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.godRay);

      for (int i = 0; i < this.rays.size(); i++) {
         this.rays.get(i).render();
      }

      Shaders.setUniform("directColor", false);
      GL11.glDepthMask(true);
      GL11.glEnable(GL11.GL_CULL_FACE);
   }
}
