package game.environment;

import game.Main;
import game.manager.Camera;
import game.manager.GameScene;
import game.manager.GameState;
import game.manager.GameTime;
import game.manager.RenderManager;
import game.manager.TextureManager;
import game.shader.Shaders;
import game.util.Point;
import org.lwjgl.opengl.GL11;

public final class SkyDome {
   public static Point skyColor = new Point();

   public static void render() {
      Point fogColor = DepthAtmosphere.getFogColor().scaled(Math.max(GameTime.getLightLevel(), 0.5F));
      Point ambientColor = DepthAtmosphere.getAmbientColor().scaled(Math.max(GameTime.getLightLevel(), 0.5F));
      if (Main.getGameState() != GameState.MAIN_MENU
         && (
            GameScene.avatar != null && GameScene.avatar.isAboveWater()
               || Main.getGameState() != GameState.PLAYING && Camera.getPosition().y > 0.0F
               || RenderManager.freeCam && Camera.getPosition().y > 0.0F
         )) {
         fogColor = new Point(130F / 255F, 170F / 255F, 220F / 255F).scaled(GameTime.getLightLevel());
         ambientColor = new Point(130F / 255F, 170F / 255F, 220F / 255F).scaled(0.8F * GameTime.getLightLevel());
      }

      if (Main.getGameState() == GameState.MAIN_MENU) {
         fogColor = new Point(0.15F, 0.53F, 0.69F);
         ambientColor = new Point(0.13F, 0.48F, 0.63F);
      }

      Shaders.worldShader.bind();
      RenderManager.disableCubemap();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      Shaders.setUniform("directColor", true);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glPushMatrix();
      GL11.glTranslatef(Camera.getPosition().x, Camera.getPosition().y, Camera.getPosition().z);
      float radius = 5.0F;
      float tMax = 0.75F;
      float tMin = -0.75F;
      byte latSegs = 30;
      byte lonSegs = 30;
      float gradR = (fogColor.x - ambientColor.x) / 1.5F;
      float baseR = fogColor.x - gradR * tMax;
      float gradG = (fogColor.y - ambientColor.y) / 1.5F;
      float baseG = fogColor.y - gradG * tMax;
      float gradB = (fogColor.z - ambientColor.z) / 1.5F;
      float baseB = fogColor.z - gradB * tMax;

      for (int i = 0; i < latSegs; i++) {
         float theta = i * (float)(Math.PI / latSegs);
         GL11.glBegin(GL11.GL_QUAD_STRIP);

         for (int j = 0; j <= lonSegs; j++) {
            float phi = j == lonSegs ? 0.0F : j * (float)(2.0 * Math.PI / lonSegs);
            float vx = (float)(-Math.sin(phi) * Math.sin(theta));
            float vy = (float)(Math.cos(phi) * Math.sin(theta));
            float vz = (float)(-1.0 * Math.cos(theta));
            float t = Math.min(tMax, Math.max(tMin, vy * radius));
            GL11.glColor3f(gradR * t + baseR, gradG * t + baseG, gradB * t + baseB);
            GL11.glVertex3f(vx * radius, vy * radius, vz * radius);
            vx = (float)(-Math.sin(phi) * Math.sin(theta + (float)(Math.PI / latSegs)));
            vy = (float)(Math.cos(phi) * Math.sin(theta + (float)(Math.PI / latSegs)));
            vz = (float)(-1.0 * Math.cos(theta + (float)(Math.PI / latSegs)));
            t = Math.min(tMax, Math.max(tMin, vy * radius));
            GL11.glColor3f(gradR * t + baseR, gradG * t + baseG, gradB * t + baseB);
            GL11.glVertex3f(vx * radius, vy * radius, vz * radius);
         }

         GL11.glEnd();
      }

      GL11.glPopMatrix();
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      Shaders.setUniform("directColor", false);
      skyColor = new Point((fogColor.x + ambientColor.x) / 2.0F, (fogColor.y + ambientColor.y) / 2.0F, (fogColor.z + ambientColor.z) / 2.0F);
   }
}
