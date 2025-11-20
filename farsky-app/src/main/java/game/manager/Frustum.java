package game.manager;

import game.collision.AABB;
import game.environment.DepthAtmosphere;
import game.util.Plan;
import game.util.Point;

public final class Frustum {
   private static Point forward;
   private static Point camPos;
   private static Point farCenter;
   private static Point farTopLeft;
   private static Point farTopRight;
   private static Point farBottomRight;
   private static Point farBottomLeft;
   private static float farHalfHeight = 50.0F;
   private static float farHalfWidth = 50.0F;
   private static float farDist;
   private static Point nearCenter;
   private static Point nearTopRight;
   private static Point nearBottomRight;
   private static Point nearBottomLeft;
   private static float nearHalfHeight = 50.0F;
   private static float nearHalfWidth = 50.0F;
   private static float nearDist = 10.0F;
   private static Plan leftPlane;
   private static Plan rightPlane;
   private static Plan farPlane;
   private static Plan nearPlane;
   private static boolean initialized = false;

   public static void update() {
      farDist = DepthAtmosphere.getFogDistance();
      farHalfHeight = (float)(farDist * Math.tan(Math.toRadians(RenderManager.fov / 2.0F))) * 2.0F;
      farHalfWidth = farHalfHeight * RenderManager.aspectRatio;
      nearHalfHeight = (float)(nearDist * Math.tan(Math.toRadians(RenderManager.fov / 2.0F))) * 2.0F;
      nearHalfWidth = nearHalfHeight * RenderManager.aspectRatio;
      camPos = new Point(Camera.getPosition().x, Camera.getPosition().y, Camera.getPosition().z);
      forward = new Point(-Camera.getMatrix().m02, -Camera.getMatrix().m12, -Camera.getMatrix().m22);
      Point up = new Point(Camera.getMatrix().m01, Camera.getMatrix().m11, Camera.getMatrix().m21);
      Point right = new Point(Camera.getMatrix().m00, Camera.getMatrix().m10, Camera.getMatrix().m20);
      farCenter = forward.scaled(farDist).plus(camPos);
      farTopLeft = farCenter.plus(up.scaled(farHalfHeight / 2.0F)).plus(right.scaled(-farHalfWidth / 2.0F));
      farTopRight = farCenter.plus(up.scaled(farHalfHeight / 2.0F)).plus(right.scaled(farHalfWidth / 2.0F));
      farBottomLeft = farCenter.plus(up.scaled(-farHalfHeight / 2.0F)).plus(right.scaled(-farHalfWidth / 2.0F));
      farBottomRight = farCenter.plus(up.scaled(-farHalfHeight / 2.0F)).plus(right.scaled(farHalfWidth / 2.0F));
      nearCenter = forward.scaled(nearDist).plus(camPos);
      nearTopRight = nearCenter.plus(up.scaled(nearHalfHeight / 2.0F)).plus(right.scaled(nearHalfWidth / 2.0F));
      nearBottomLeft = nearCenter.plus(up.scaled(-nearHalfHeight / 2.0F)).plus(right.scaled(-nearHalfWidth / 2.0F));
      nearBottomRight = nearCenter.plus(up.scaled(-nearHalfHeight / 2.0F)).plus(right.scaled(nearHalfWidth / 2.0F));
      up = farTopLeft.minus(camPos).cross(farTopLeft.minus(farBottomLeft));
      leftPlane = new Plan(up, farTopLeft);
      up = farTopRight.minus(farBottomRight).cross(farTopRight.minus(camPos));
      rightPlane = new Plan(up, farTopRight);
      up = farTopRight.minus(camPos).cross(farTopRight.minus(farTopLeft));
      up = farBottomRight.minus(farBottomLeft).cross(farBottomRight.minus(camPos));
      up = farBottomRight.minus(farTopRight).cross(farBottomRight.minus(farBottomLeft));
      farPlane = new Plan(up, farBottomRight);
      up = nearBottomRight.minus(nearBottomLeft).cross(nearBottomRight.minus(nearTopRight));
      nearPlane = new Plan(up, nearBottomRight);
      initialized = true;
   }

   public static boolean isVisible(AABB aabb) {
      if (!initialized) {
         return false;
      } else {
         int farSideSum = farPlane.sideOf(aabb.getCorner(0)) + farPlane.sideOf(aabb.getCorner(1)) + farPlane.sideOf(aabb.getCorner(2)) + farPlane.sideOf(aabb.getCorner(3)) + farPlane.sideOf(aabb.getCorner(4)) + farPlane.sideOf(aabb.getCorner(5)) + farPlane.sideOf(aabb.getCorner(6)) + farPlane.sideOf(aabb.getCorner(7));
         int nearSideSum = nearPlane.sideOf(aabb.getCorner(0)) + nearPlane.sideOf(aabb.getCorner(1)) + nearPlane.sideOf(aabb.getCorner(2)) + nearPlane.sideOf(aabb.getCorner(3)) + nearPlane.sideOf(aabb.getCorner(4)) + nearPlane.sideOf(aabb.getCorner(5)) + nearPlane.sideOf(aabb.getCorner(6)) + nearPlane.sideOf(aabb.getCorner(7));
         if (farSideSum == 8 && nearSideSum == 8 || Math.abs(farSideSum) < 8 || Math.abs(nearSideSum) < 8) {
            farSideSum = leftPlane.sideOf(aabb.getCorner(0)) + leftPlane.sideOf(aabb.getCorner(1)) + leftPlane.sideOf(aabb.getCorner(2)) + leftPlane.sideOf(aabb.getCorner(3)) + leftPlane.sideOf(aabb.getCorner(4)) + leftPlane.sideOf(aabb.getCorner(5)) + leftPlane.sideOf(aabb.getCorner(6)) + leftPlane.sideOf(aabb.getCorner(7));
            int leftSideSum = rightPlane.sideOf(aabb.getCorner(0)) + rightPlane.sideOf(aabb.getCorner(1)) + rightPlane.sideOf(aabb.getCorner(2)) + rightPlane.sideOf(aabb.getCorner(3)) + rightPlane.sideOf(aabb.getCorner(4)) + rightPlane.sideOf(aabb.getCorner(5)) + rightPlane.sideOf(aabb.getCorner(6)) + rightPlane.sideOf(aabb.getCorner(7));
            if (farSideSum == 8 && leftSideSum == 8 || Math.abs(farSideSum) < 8 || Math.abs(leftSideSum) < 8) {
               return true;
            }
         }

         return false;
      }
   }
}
