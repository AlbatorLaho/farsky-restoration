package game.submarine;

import game.Main;
import game.chunks.ChunkManager;
import game.render.ModelLoader;
import game.render.Vbo;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.collision.AABB;
import game.collision.CollisionDetector;
import game.environment.water.WaterSurface;
import game.gui.InteractionHint;
import game.input.InputManager;
import game.manager.GameState;
import game.manager.GameScene;
import game.manager.GameTime;
import game.player.PlayerInput;
import game.util.Point;
import game.util.State;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public class Submarine implements Serializable {
   private static final long serialVersionUID = -6197738475145449673L;
   private SubmarineState submarineState;
   private boolean closed = false;
   private boolean moving = false;
   private transient Point bobOffset = new Point();
   public transient PlayerInput input = new PlayerInput();
   private transient float exitCooldown = 0.0F;
   private transient float canopyAngle = 0.0F;
   private static transient ArrayList<AABB> interiorBoxes;
   private static transient ArrayList<AABB> interiorCeilingBoxes;
   private static final transient AABB entryTrigger = new AABB(new Point(0.0F, -20.0F, 15.0F), 30.0F, 50.0F, 30.0F);
   private static final transient AABB collisionBox = new AABB(new Point(0.0F, 0.0F, 10.0F), 50.0F, 50.0F, 80.0F);
   private static transient Point seatOffset = new Point(0.0F, -5.0F, 3.0F);
   private static transient float exitHintCooldown = 5.0F;
   private static transient int engineSourceId = -1;
   private static transient Vbo hullMesh;
   private static transient int hullTexture;
   private static transient Vbo canopyMesh;
   private static transient int canopyTexture;
   private static transient Vbo canopyGlassMesh;
   private static transient int canopyGlassTexture;
   private static transient Vbo propellerFanMesh;
   private static transient Vbo motorMesh;
   private static transient Vbo topMesh;
   private static transient Vbo stabilizerMesh;

   public static void loadAssets() {
      hullMesh = ModelLoader.loadMesh("submarine");
      hullTexture = ModelLoader.loadTexture("submarine");
      canopyMesh = ModelLoader.loadMesh("submarine", "bell");
      canopyTexture = ModelLoader.loadTexture("submarine", "bell");
      canopyGlassMesh = ModelLoader.loadMesh("submarine", "bellGlass");
      canopyGlassTexture = ModelLoader.loadTexture("submarine", "bellGlass");
      propellerFanMesh = ModelLoader.loadMesh("submarine", "fan");
      motorMesh = ModelLoader.loadMesh("submarine", "motor");
      stabilizerMesh = ModelLoader.loadMesh("submarine", "bottle");
      topMesh = ModelLoader.loadMesh("submarine", "top");
      interiorCeilingBoxes = new ArrayList<>();
      interiorCeilingBoxes.add(new AABB(new Point(0.0F, 10.0F, 20.0F), 35.0F, 20.0F, 35.0F));
      interiorBoxes = new ArrayList<>();
      interiorBoxes.add(new AABB(new Point(0.0F, 0.0F, -7.0F), 50.0F, 40.0F, 50.0F));
   }

   public Submarine(Point position) {
      this.submarineState = new SubmarineState(position.copy());
      if (engineSourceId == -1) {
         engineSourceId = SoundManager.addLoopingSource(SoundManager.sfxEngine, null);
      }
   }

   public final void update(float delta) {
      if (GameScene.avatar == null || GameScene.avatar.isDead()) {
         this.closed = false;
         this.moving = false;
         this.exitCooldown = 0.0F;
      } else if (!this.closed) {
         if (CollisionDetector.containsPoint(GameScene.avatar.getCameraPos(), entryTrigger, this.submarineState.state.pos, this.submarineState.rot)) {
            InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Go inside the submarine", null);
            if (GameScene.avatar.isInteractPressed()) {
               this.closed = true;
               GameScene.avatar.setNavigating(true);
               GameScene.avatar.setOnGround(true);
               GameScene.avatar.setHorizontalAngle(this.submarineState.rot.y + 180.0F);
               GameScene.avatar.setVerticalAngle(-this.submarineState.rot.x);
               this.moving = true;
            }
         }
      } else {
         if ((exitHintCooldown -= delta) > 0.0F) {
            InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Go outside", null);
         } else {
            exitHintCooldown = 0.0F;
         }

         if (GameScene.avatar.isInteractPressed()) {
            this.closed = false;
            GameScene.avatar.setNavigating(false);
            this.moving = false;
            this.exitCooldown = 1.5F;
         }
      }

      if (this.exitCooldown > 0.0F) {
         this.exitCooldown -= delta;
      }

      if (this.closed) {
         if (this.canopyAngle < 90.0F) {
            this.canopyAngle += delta * 60.0F;
         } else {
            this.canopyAngle = 90.0F;
         }
      } else if (this.canopyAngle > 0.0F) {
         this.canopyAngle -= delta * 60.0F;
      } else {
         this.canopyAngle = 0.0F;
      }

      if (this.canopyAngle > 0.0F && Main.getGameState() == GameState.PLAYING) {
         if (!SoundManager.isLoopingSourcePlaying(engineSourceId)) {
            SoundManager.playLoopingSource(engineSourceId);
         }

         SoundManager.setLoopingSourceVolume(engineSourceId, this.canopyAngle / 90.0F * 0.2F);
         SoundManager.setLoopingSourcePitch(engineSourceId, 0.8F + Math.abs(this.submarineState.state.vel.length() / 130.0F));
      } else {
         SoundManager.pauseLoopingSource(engineSourceId);
      }

      this.bobOffset = new Point(Math.cos(GameTime.elapsedMillis / 1356.0F), Math.cos(GameTime.elapsedMillis / 1887.0F), Math.cos(GameTime.elapsedMillis / 1564.0F));
      this.bobOffset.scale(2.0F);
      SubmarineState newState = this.submarineState.copy();
      if (this.moving) {
         float rotY = this.submarineState.rot.y;
         float rotX = this.submarineState.rot.x;
         GameScene.avatar.addHorizontalAngle(this.input.lookHorizontalDelta);
         GameScene.avatar.addVerticalAngle(this.input.lookVerticalDelta);
         float angleDelta = GameScene.avatar.getHorizontalAngle() + 180.0F - rotY;
         if (Math.abs(angleDelta) > Math.abs(angleDelta - 360.0F)) {
            angleDelta -= 360.0F;
         }

         if (Math.abs(angleDelta) > Math.abs(angleDelta + 360.0F)) {
            angleDelta += 360.0F;
         }

         if (Math.abs(angleDelta) < 2.0F) {
            angleDelta = 0.0F;
         }

         rotY = rotY + angleDelta * delta * 2.0F;
         if (rotY > 360.0F) {
            rotY -= 360.0F;
         }

         if (rotY < -360.0F) {
            rotY += 360.0F;
         }

         angleDelta = -GameScene.avatar.getVerticalAngle() - rotX;
         if (Math.abs(angleDelta) < 2.0F) {
            angleDelta = 0.0F;
         }

         rotX = rotX + angleDelta * delta;
         if (rotX > 60.0F) {
            rotX = 60.0F;
         }

         if (rotX < -60.0F) {
            rotX = -60.0F;
         }

         newState.rot.x = rotX;
         newState.rot.y = rotY;
         Point dir = new Point();
         if (this.input.moveForward) {
            dir.add(0.0F, 0.0F, 1.0F);
         }

         if (this.input.moveBackward) {
            dir.add(0.0F, 0.0F, -1.0F);
         }

         if (this.input.strafeRight) {
            dir.add(-1.0F, 0.0F, 0.0F);
         }

         if (this.input.strafeLeft) {
            dir.add(1.0F, 0.0F, 0.0F);
         }

         dir.normalize();
         dir.rotateX(newState.rot.x);
         dir.rotateY(newState.rot.y);
         if (this.input.ascend) {
            dir.add(0.0F, 1.0F, 0.0F);
         }

         if (this.input.descend) {
            dir.add(0.0F, -1.0F, 0.0F);
         }

         dir.normalize();
         if (newState.state.pos.y > WaterSurface.getWaveHeight() + 10.0F) {
            dir.y = Math.min(dir.y, 0.0F);
            if (!GameScene.gameMode.hasSurfaceGameplay()) {
               newState.state.vel.y = Math.min(newState.state.vel.y, 0.0F);
            } else {
               newState.state.vel.y--;
            }
         }

         newState.state.vel.add(dir.scaled(delta * 70.0F));
         if (newState.state.vel.length() > 95.0F) {
            newState.state.vel.normalize();
            newState.state.vel.scale(95.0F);
         }
      } else {
         if (newState.state.pos.y > ChunkManager.getHeight(newState.state.pos.x, newState.state.pos.z) + 200.0F) {
            newState.state.vel.add(0.0F, -delta * 50.0F, 0.0F);
            if (newState.state.vel.y < -20.0F) {
               newState.state.vel.y = -20.0F;
            }
         }

         float tiltRate = delta * 10.0F;
         newState.rot.x = newState.rot.x - Math.min(tiltRate, newState.rot.x);
      }

      if (!newState.state.vel.equals(new Point())) {
         Point prevVel = newState.state.vel.copy();
         Point drag = new Point(-newState.state.vel.x, -newState.state.vel.y, -newState.state.vel.z);
         drag.normalize();
         drag.scale(delta * 40.0F);
         newState.state.vel.add(drag);
         if (newState.state.vel.dot(prevVel) < 0.0F) {
            newState.state.vel = new Point();
         }
      }

      newState.state.pos.add(newState.state.vel.scaled(delta));
      if (this.moving || !this.submarineState.state.pos.equals(newState.state.pos)) {
         newState = CollisionDetector.resolveSubmarineCollision(this.submarineState, newState, collisionBox, delta, this);
      }

      if (newState.state.pos.y <= WaterSurface.getWaveHeight() + 10.0F
         && this.submarineState.state.pos.y > WaterSurface.getWaveHeight() + 10.0F
         && newState.state.vel.y < -30.0F
         && !SoundManager.isTransientSoundPlaying(SoundManager.sfxInWater)) {
         SoundManager.playSound(SoundManager.sfxInWater, null, 0.35F + (float)Math.random() * 0.1F, 0.3F);
      }

      this.submarineState = newState.copy();
      if (this.moving) {
         Point seatPos = seatOffset.copy();
         seatPos.rotateX(this.submarineState.rot.x);
         seatPos.rotateY(this.submarineState.rot.y);
         GameScene.avatar.setPosByFeet(this.submarineState.state.pos.plus(this.bobOffset.plus(seatPos)));
      } else if (this.exitCooldown > 0.0F) {
         Point exitImpulse = new Point(0.0F, 0.0F, 20.0F);
         exitImpulse.rotateY(this.submarineState.rot.y);
         GameScene.avatar.applyImpulse(exitImpulse);
      }

      this.input.resetFrameInputs();
   }

   private void beginRender() {
      float light = (float)Math.cos(GameTime.elapsedMillis / 200.0F) * 0.5F + 0.5F + 0.75F;
      Shaders.setUniform("lightColor", new Point(light * 0.9F, light * 0.9F, light));
      Shaders.setUniform("emissive", false);
      Shaders.setUniform("inside", false);
      Shaders.setUniform("toplight", true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(this.submarineState.state.pos.x + this.bobOffset.x, this.submarineState.state.pos.y + this.bobOffset.y, this.submarineState.state.pos.z + this.bobOffset.z);
      GL11.glRotatef(this.submarineState.rot.y, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(this.submarineState.rot.x, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(30.0F, 30.0F, 30.0F);
   }

   public final void renderExterior() {
      this.beginRender();
      Shaders.setUniform("alphaLight", true);
      renderPiece(SubmarinePiece.HULL);
      Shaders.setUniform("alphaLight", false);
      Shaders.setUniform("alphaLight", true);
      renderPiece(SubmarinePiece.PORTHOLE);
      Shaders.setUniform("alphaLight", false);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.45F, -0.5F, -0.1F);
      renderPiece(SubmarinePiece.STABILIZER_RIGHT_LOWER);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.45F, -0.5F, -0.1F);
      renderPiece(SubmarinePiece.STABILIZER_LEFT_LOWER);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.45F, -0.1F, -0.1F);
      renderPiece(SubmarinePiece.STABILIZER_RIGHT_UPPER);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.45F, -0.1F, -0.1F);
      renderPiece(SubmarinePiece.STABILIZER_LEFT_UPPER);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(0.6F, -0.25F, 0.0F);
      renderPiece(SubmarinePiece.PROPELLER_RIGHT);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glTranslatef(-0.6F, -0.25F, 0.0F);
      renderPiece(SubmarinePiece.PROPELLER_LEFT);
      GL11.glPopMatrix();
      endRender();
   }

   public final void renderCanopy() {
      this.beginRender();
      Shaders.setUniform("toplight", false);
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.2F + 0.8F * (1.0F - this.canopyAngle / 90.0F));
      GL11.glTranslatef(0.0F, 0.12F, 0.34F);
      GL11.glTranslatef(0.0F, 0.0F, -0.05F * this.canopyAngle / 90.0F);
      GL11.glRotatef(this.canopyAngle, 1.0F, 0.0F, 0.0F);
      renderPiece(SubmarinePiece.CANOPY);
      GL11.glPopMatrix();
      endRender();
   }

   private static void endRender() {
      GL11.glPopMatrix();
      Shaders.setUniform("toplight", false);
   }

   public static void renderPiece(SubmarinePiece piece) {
      GL11.glDisable(GL11.GL_CULL_FACE);
      if (piece == SubmarinePiece.STABILIZER_LEFT_UPPER || piece == SubmarinePiece.STABILIZER_RIGHT_UPPER) {
         GL11.glScalef(0.8F, 0.8F, 0.8F);
      }

      switch (piece) {
         case CANOPY:
            GL11.glDepthMask(false);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, canopyTexture);
            canopyMesh.render();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, canopyGlassTexture);
            canopyGlassMesh.render();
            GL11.glDepthMask(true);
            break;
         case STABILIZER_LEFT_LOWER:
         case STABILIZER_LEFT_UPPER:
            GL11.glScalef(-1.0F, 1.0F, 1.0F);
         case STABILIZER_RIGHT_LOWER:
         case STABILIZER_RIGHT_UPPER:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, hullTexture);
            stabilizerMesh.render();
            break;
         case PROPELLER_LEFT:
            GL11.glScalef(-1.0F, 1.0F, 1.0F);
         case PROPELLER_RIGHT:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, hullTexture);
            motorMesh.render();
            GL11.glRotatef(GameTime.elapsedMillis / 4.0F, 0.0F, 0.0F, 1.0F);
            propellerFanMesh.render();
            break;
         case HULL:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, hullTexture);
            hullMesh.render();
            break;
         case PORTHOLE:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, hullTexture);
            topMesh.render();
      }

      if (piece == SubmarinePiece.STABILIZER_LEFT_UPPER || piece == SubmarinePiece.STABILIZER_RIGHT_UPPER) {
         GL11.glScalef(1.25F, 1.25F, 1.25F);
      }

      GL11.glEnable(GL11.GL_CULL_FACE);
   }

   public final boolean isMoving() {
      return this.moving;
   }

   public final State resolveInteriorCollision(State movement, State state) {
      movement.pos = this.toLocalSpace(movement.pos);
      state.pos = this.toLocalSpace(state.pos);

      for (int i = 0; i < interiorBoxes.size(); i++) {
         state = interiorBoxes.get(i).resolveCollision(movement, state);
      }

      for (int i = 0; i < interiorCeilingBoxes.size(); i++) {
         state = interiorCeilingBoxes.get(i).resolveCollision(movement, state);
      }

      movement.pos = this.toWorldSpace(movement.pos);
      state.pos = this.toWorldSpace(state.pos);
      return state;
   }

   private Point toLocalSpace(Point worldPos) {
      Point localPos = worldPos.copy();
      localPos.subtract(this.submarineState.state.pos);
      localPos.rotateY(-this.submarineState.rot.y);
      return localPos;
   }

   private Point toWorldSpace(Point localPos) {
      Point worldPos = localPos.copy();
      worldPos.rotateY(this.submarineState.rot.y);
      worldPos.add(this.submarineState.state.pos);
      return worldPos;
   }

   public final Point getPosition() {
      return this.submarineState.state.pos;
   }

   public final Point getRotation() {
      return this.submarineState.rot;
   }

   public static AABB getBoundingBox() {
      return new AABB(new Point(0.0F, -20.0F, 15.0F), 50.0F, 50.0F, 60.0F);
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.bobOffset = new Point();
      this.input = new PlayerInput();
      this.exitCooldown = 0.0F;
      if (engineSourceId == -1) {
         engineSourceId = SoundManager.addLoopingSource(SoundManager.sfxEngine, null);
      }

      if (this.closed) {
         this.canopyAngle = 90.0F;
      } else {
         this.canopyAngle = 0.0F;
      }
   }
}
