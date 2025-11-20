package game.cinematic;

import game.shader.Shaders;
import game.cinematic.particle.CinematicDebris;
import game.cinematic.particle.CrashParticle;
import game.environment.DepthAtmosphere;
import game.environment.SkyDome;
import game.environment.particle.Particle;
import game.manager.RenderManager;
import game.manager.TextureManager;
import game.manager.Camera;
import game.manager.GameScene;
import game.manager.GameTime;
import game.submarine.Submarine;
import game.submarine.SubmarinePiece;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public class CinematicStage {
   private float timeRemaining;
   private float totalDuration;
   private String name;
   private static ArrayList<CrashParticle> crashParticles;
   private static CinematicDebris debrisA;
   private static CinematicDebris debrisB;
   private static CinematicDebris debrisC;
   private static CinematicDebris debrisD;
   private static CinematicDebris debrisE;

   public CinematicStage(String name, float duration) {
      this.name = name;
      this.timeRemaining = duration;
      this.totalDuration = duration;
   }

   public final boolean tick(float deltaTime) {
      boolean wasActive = this.timeRemaining > 0.0F;
      this.timeRemaining -= deltaTime;
      return wasActive;
   }

   public final boolean isComplete() {
      return this.timeRemaining <= 0.0F;
   }

   public final float getProgress() {
      return 1.0F - this.timeRemaining / this.totalDuration;
   }

   public final String getName() {
      return this.name;
   }

   public final void skip() {
      this.timeRemaining = 0.001F;
   }

   public static void initCrashScene() {
      crashParticles = new ArrayList<>();

      for (int i = 0; i < 600; i++) {
         crashParticles.add(new CrashParticle(new Point(0.0F, 0.2F, 1.0F), 150.0F));
      }

      Point origin = new Point(400.0F, 160.0F, -450.0F);
      Point direction = new Point(-1.0F, -0.2F, 0.4F);
      debrisA = new CinematicDebris(origin, direction, 150.0F);
      debrisB = new CinematicDebris(origin, direction, 150.0F);
      debrisC = new CinematicDebris(origin, direction, 150.0F);
      debrisD = new CinematicDebris(origin, direction, 150.0F);
      debrisE = new CinematicDebris(origin, direction, 150.0F);
   }

   public static void updateParticles(float deltaTime) {
      for (int i = 0; i < crashParticles.size(); i++) {
         crashParticles.get(i).update(deltaTime);
      }

      debrisA.update(deltaTime);
      debrisB.update(deltaTime);
      debrisC.update(deltaTime);
      debrisD.update(deltaTime);
      debrisE.update(deltaTime);
   }

   public static void triggerExplosion(int index) {
      debrisA.explode();
   }

   public static void renderCrash() {
      DepthAtmosphere.update(0.0F);
      Particle.beginRender();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.particle);

      for (int i = 0; i < crashParticles.size(); i++) {
         crashParticles.get(i).render(1.2F);
      }

      Particle.endRender();
      Shaders.insideShader.bind();
      RenderManager.setLight();
      Shaders.setUniform("alphaLight", false);
      Shaders.setUniform("emissive", false);
      Shaders.setUniform("inside", false);
      Shaders.setUniform("discardTransparency", false);
      Shaders.setUniform("selected", false);
      Shaders.setUniform("lightLimit", 100.0);
      Shaders.setUniform("visibleLimit", DepthAtmosphere.getFogDistance());
      Shaders.setUniform("glowColor", SkyDome.skyColor);
      Shaders.setUniform("time", GameTime.elapsedMillis);
      Shaders.setUniform("selectedFactor", 2.0 + (Math.cos(GameTime.elapsedMillis / 250.0F) * 0.5 + 0.5) * 0.5);
      float lightFlicker = (float)Math.cos(GameTime.elapsedMillis / 200.0F) * 0.5F + 0.5F + 0.75F;
      Shaders.setUniform("lightColor", new Point(lightFlicker * 0.9F, lightFlicker * 0.9F, lightFlicker));
      Shaders.setUniform("emissive", false);
      Shaders.setUniform("inside", false);
      Shaders.setUniform("toplight", true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 7.0F, 2.0F);
      GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      Shaders.setUniform("alphaLight", true);
      GL11.glPushMatrix();
      GL11.glScalef(30.0F, 30.0F, 30.0F);
      Submarine.renderPiece(SubmarinePiece.HULL);
      GL11.glPopMatrix();
      Shaders.setUniform("alphaLight", false);
      Shaders.setUniform("alphaLight", true);
      GL11.glPushMatrix();
      GL11.glScalef(30.0F, 30.0F, 30.0F);
      Submarine.renderPiece(SubmarinePiece.PORTHOLE);
      GL11.glPopMatrix();
      Shaders.setUniform("alphaLight", false);
      Shaders.setUniform("toplight", false);
      GL11.glPushMatrix();
      GL11.glScalef(30.0F, 30.0F, 30.0F);
      GL11.glTranslatef(0.0F, 0.12F, 0.34F);
      GL11.glTranslatef(0.0F, 0.0F, -0.05F);
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
      Submarine.renderPiece(SubmarinePiece.CANOPY);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      Shaders.worldShader.bind();
      RenderManager.setLight();
      RenderManager.disableCubemap();
      Shaders.setUniform("time", GameTime.elapsedMillis);
      Shaders.setUniform("lightLimit", 100.0);
      Shaders.setUniform("visibleLimit", DepthAtmosphere.getFogDistance());
      Shaders.setUniform("glowColor", SkyDome.skyColor);
      Point topLightPos = GameScene.avatar.getCameraPos().plus(0.0F, 500.0F, 0.0F);
      Shaders.setUniform("topLightPos", Camera.toViewSpace(topLightPos));
      Shaders.setUniform("topLight", false);
      Shaders.setUniform("selected", false);
      Shaders.setUniform("selectedFactor", 2.0 + (Math.cos(GameTime.elapsedMillis / 250.0F) * 0.5 + 0.5) * 0.5);
   }
}
