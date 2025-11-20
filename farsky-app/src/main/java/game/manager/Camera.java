package game.manager;

import game.player.Avatar;
import game.player.PlayerInput;
import game.util.Coord;
import game.util.Point;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public final class Camera {
   private static Matrix4f viewMatrix = new Matrix4f();
   private static Point worldOffset;
   private static Point positionOffset = new Point();
   private static Point position = new Point();
   private static Point moduloPos = new Point();
   private static float yaw;
   private static float pitch;
   private static float freeCamYawLock;
   private static float roll;
   private static float prevLookH = 0.0F;
   private static float prevLookV = 0.0F;
   private static float shakeTime = 0.0F;
   public static PlayerInput input = new PlayerInput();
   public static float speedMultiplier = 1.0F;
   private static boolean hasRoll = false;

   public static void setFromAvatar(Avatar avatar) {
      position.set(avatar.getCameraPos());
      yaw = avatar.getHorizontalAngle();
      pitch = avatar.getVerticalAngle();
   }

   public static void setPosition(Point pos, float horizontalAngle, float verticalAngle) {
      position.set(pos);
      yaw = horizontalAngle;
      pitch = verticalAngle;
   }

   public static void applyTransform() {
      viewMatrix = new Matrix4f();
      position.add(positionOffset);
      positionOffset = new Point();
      moduloPos = new Point(position.x % 1000.0F, position.y, position.z % 1000.0F);
      worldOffset = new Point(position.x - moduloPos.x, 0.0F, position.z - moduloPos.z);
      viewMatrix.translate(new Vector3f(moduloPos.x, moduloPos.y, moduloPos.z));
      viewMatrix.rotate((float)(yaw * Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F));
      viewMatrix.rotate((float)(pitch * Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F));
      viewMatrix.rotate((float)(roll * Math.PI / 180.0), new Vector3f(0.0F, 0.0F, 1.0F));
      viewMatrix.invert();
   }

   public static void applyMatrix() {
      FloatBuffer buf = BufferUtils.createFloatBuffer(16);
      buf.put(viewMatrix.m00);
      buf.put(viewMatrix.m01);
      buf.put(viewMatrix.m02);
      buf.put(viewMatrix.m03);
      buf.put(viewMatrix.m10);
      buf.put(viewMatrix.m11);
      buf.put(viewMatrix.m12);
      buf.put(viewMatrix.m13);
      buf.put(viewMatrix.m20);
      buf.put(viewMatrix.m21);
      buf.put(viewMatrix.m22);
      buf.put(viewMatrix.m23);
      buf.put(viewMatrix.m30);
      buf.put(viewMatrix.m31);
      buf.put(viewMatrix.m32);
      buf.put(viewMatrix.m33);
      buf.rewind();
      GL11.glMultMatrix(buf);
      Frustum.update();
   }

   public static void update(float dt) {
      if (shakeTime <= 0.0F) {
         shakeTime = 0.0F;
         if (!hasRoll) {
            roll = 0.0F;
         }

         hasRoll = false;
      } else {
         roll = (float)Math.cos(shakeTime * 50.0F);
         shakeTime -= dt;
      }

      if (RenderManager.freeCam) {
         float smoothDelta = (prevLookH + input.lookHorizontalDelta) / 3.0F;
         if ((yaw += smoothDelta) > 360.0F) {
            yaw -= 360.0F;
         }

         if (yaw < -360.0F) {
            yaw += 360.0F;
         }

         smoothDelta = (prevLookV + input.lookVerticalDelta) / 3.0F;
         if ((pitch += smoothDelta) > 80.0F) {
            pitch = 80.0F;
         }

         if (pitch < -80.0F) {
            pitch = -80.0F;
         }

         prevLookH = input.lookHorizontalDelta;
         prevLookV = input.lookVerticalDelta;
         Point moveDir = new Point();
         if (input.moveForward) {
            moveDir.add(0.0F, 0.0F, -1.0F);
         }

         if (input.moveBackward) {
            moveDir.add(0.0F, 0.0F, 1.0F);
         }

         if (input.strafeLeft) {
            moveDir.add(-1.0F, 0.0F, 0.0F);
         }

         if (input.strafeRight) {
            moveDir.add(1.0F, 0.0F, 0.0F);
         }

         if (input.ascend) {
            moveDir.add(0.0F, 1.0F, 0.0F);
         }

         if (input.descend) {
            moveDir.add(0.0F, -1.0F, 0.0F);
         }

         moveDir.normalize();
         if (!input.primaryMouseHeld) {
            freeCamYawLock = yaw;
            moveDir.rotateY(yaw);
         } else {
            moveDir.rotateY(freeCamYawLock);
         }

         moveDir.scale(dt * 100.0F * speedMultiplier);
         position.add(moveDir);
      }
   }

   public static void addShake(float duration) {
      shakeTime = Math.max(duration, shakeTime);
   }

   public static void applyYawPitch() {
      GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
   }

   public static void applyYaw() {
      GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
   }

   public static void lookAt(Point target) {
      target = target.minus(position);
      Coord dir2d = new Coord(target.x, target.z);
      GL11.glRotatef(-90.0F - (float)Math.toDegrees(dir2d.angle()), 0.0F, 1.0F, 0.0F);
   }

   public static Point getWorldOffset() {
      return worldOffset;
   }

   public static Matrix4f getMatrix() {
      return viewMatrix;
   }

   public static Point getPosition() {
      return position;
   }

   public static float getYaw() {
      return yaw;
   }

   public static Point toViewSpace(Point worldPoint) {
      Point viewPoint = worldPoint.copy();
      viewPoint.subtract(position);
      viewPoint.rotateY(-yaw);
      viewPoint.rotateX(-pitch);
      return viewPoint;
   }

   public static void setRoll(float angle) {
      roll = angle;
      hasRoll = true;
   }

   public static void addOffset(Point offset) {
      positionOffset = offset.copy();
   }
}
