package game.collision;

import game.render.QuadVbo;
import game.render.Vertex;
import game.manager.TextureManager;
import game.util.Coord;
import game.util.Plan;
import game.util.Point;
import game.util.State;
import java.io.Serializable;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public class BoundingBox implements Serializable {
   private static final long serialVersionUID = -4095763844949400493L;
   protected Plan[] faces = new Plan[6];
   protected boolean[] toTest = new boolean[6];
   protected Point[] corners = new Point[8];
   public static boolean groundHit = false;

   public BoundingBox() {
   }

   public BoundingBox(Point corner0, Point corner1, Point corner2, Point corner3, Point corner4, Point corner5, Point corner6, Point corner7) {
      this.corners[0] = corner0;
      this.corners[1] = corner1;
      this.corners[2] = corner2;
      this.corners[3] = corner3;
      this.corners[4] = corner4;
      this.corners[5] = corner5;
      this.corners[6] = corner6;
      this.corners[7] = corner7;

      for (int i = 0; i < 6; i++) {
         this.toTest[i] = true;
      }

      this.buildFaces();
   }

   private void buildFaces() {
      this.faces[0] = computeFacePlane(this.corners[0], this.corners[3], this.corners[1]);
      this.faces[1] = computeFacePlane(this.corners[4], this.corners[5], this.corners[7]);
      this.faces[2] = computeFacePlane(this.corners[1], this.corners[2], this.corners[5]);
      this.faces[3] = computeFacePlane(this.corners[0], this.corners[4], this.corners[3]);
      this.faces[4] = computeFacePlane(this.corners[0], this.corners[1], this.corners[4]);
      this.faces[5] = computeFacePlane(this.corners[3], this.corners[7], this.corners[2]);
      this.faces[0] = fixFacePlane(this.faces[0], this.corners[0], this.faces[1]);
      this.faces[1] = fixFacePlane(this.faces[1], this.corners[4], this.faces[0]);
      this.faces[2] = fixFacePlane(this.faces[2], this.corners[1], this.faces[3]);
      this.faces[3] = fixFacePlane(this.faces[3], this.corners[0], this.faces[2]);
      this.faces[4] = fixFacePlane(this.faces[4], this.corners[0], this.faces[5]);
      this.faces[5] = fixFacePlane(this.faces[5], this.corners[3], this.faces[4]);
   }

   public void copyFrom(AABB source) {
      for (int i = 0; i < 6; i++) {
         this.faces[i].copyFrom(source.faces[i]);
      }

      for (int i = 0; i < 6; i++) {
         this.toTest[i] = source.toTest[i];
      }

      for (int i = 0; i < 8; i++) {
         this.corners[i].set(source.corners[i]);
      }
   }

   public final Point getCorner(int index) {
      return this.corners[index];
   }

   public void translate(Point offset) {
      for (int i = 0; i < 8; i++) {
         this.corners[i].add(offset);
      }

      this.buildFaces();
   }

   public final void rotate(float angle) {
      for (int i = 0; i < 8; i++) {
         this.corners[i].rotateY(angle);
      }

      this.buildFaces();
   }

   private static Plan computeFacePlane(Point p0, Point p1, Point p2) {
      p1 = p0.minus(p1);
      p2 = p0.minus(p2);
      p1 = p1.cross(p2);
      return new Plan(p1, p0);
   }

   private static Plan fixFacePlane(Plan face, Point point, Plan oppositeFace) {
      if (face.getNormal().x == 0.0F && face.getNormal().y == 0.0F && face.getNormal().z == 0.0F) {
         Point reversedNormal = new Point(-oppositeFace.getNormal().x, -oppositeFace.getNormal().y, -oppositeFace.getNormal().z);
         face = new Plan(reversedNormal, point);
      }

      return face;
   }

   public final void setFacesToTest(boolean top, boolean bottom, boolean right, boolean left, boolean front, boolean back) {
      this.toTest[0] = true;
      this.toTest[1] = true;
      this.toTest[2] = false;
      this.toTest[3] = false;
      this.toTest[4] = true;
      this.toTest[5] = true;
   }

   public final void debugDraw(boolean wireframe) {
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      ArrayList<Vertex> vertices = new ArrayList<>();
      vertices.add(new Vertex(new Point(this.corners[0].x, this.corners[0].y, this.corners[0].z), new Coord(0, 0)));
      vertices.add(new Vertex(new Point(this.corners[1].x, this.corners[1].y, this.corners[1].z), new Coord(0, 1)));
      vertices.add(new Vertex(new Point(this.corners[2].x, this.corners[2].y, this.corners[2].z), new Coord(1, 1)));
      vertices.add(new Vertex(new Point(this.corners[3].x, this.corners[3].y, this.corners[3].z), new Coord(1, 0)));
      vertices.add(new Vertex(new Point(this.corners[4].x, this.corners[4].y, this.corners[4].z), new Coord(0, 0)));
      vertices.add(new Vertex(new Point(this.corners[5].x, this.corners[5].y, this.corners[5].z), new Coord(0, 1)));
      vertices.add(new Vertex(new Point(this.corners[6].x, this.corners[6].y, this.corners[6].z), new Coord(1, 1)));
      vertices.add(new Vertex(new Point(this.corners[7].x, this.corners[7].y, this.corners[7].z), new Coord(1, 0)));
      vertices.add(new Vertex(new Point(this.corners[1].x, this.corners[1].y, this.corners[1].z), new Coord(0, 0)));
      vertices.add(new Vertex(new Point(this.corners[2].x, this.corners[2].y, this.corners[2].z), new Coord(0, 1)));
      vertices.add(new Vertex(new Point(this.corners[6].x, this.corners[6].y, this.corners[6].z), new Coord(1, 1)));
      vertices.add(new Vertex(new Point(this.corners[5].x, this.corners[5].y, this.corners[5].z), new Coord(1, 0)));
      vertices.add(new Vertex(new Point(this.corners[0].x, this.corners[0].y, this.corners[0].z), new Coord(0, 0)));
      vertices.add(new Vertex(new Point(this.corners[3].x, this.corners[3].y, this.corners[3].z), new Coord(0, 1)));
      vertices.add(new Vertex(new Point(this.corners[7].x, this.corners[7].y, this.corners[7].z), new Coord(1, 1)));
      vertices.add(new Vertex(new Point(this.corners[4].x, this.corners[4].y, this.corners[4].z), new Coord(1, 0)));
      vertices.add(new Vertex(new Point(this.corners[0].x, this.corners[0].y, this.corners[0].z), new Coord(0, 0)));
      vertices.add(new Vertex(new Point(this.corners[1].x, this.corners[1].y, this.corners[1].z), new Coord(0, 1)));
      vertices.add(new Vertex(new Point(this.corners[5].x, this.corners[5].y, this.corners[5].z), new Coord(1, 1)));
      vertices.add(new Vertex(new Point(this.corners[4].x, this.corners[4].y, this.corners[4].z), new Coord(1, 0)));
      vertices.add(new Vertex(new Point(this.corners[2].x, this.corners[2].y, this.corners[2].z), new Coord(0, 0)));
      vertices.add(new Vertex(new Point(this.corners[3].x, this.corners[3].y, this.corners[3].z), new Coord(0, 1)));
      vertices.add(new Vertex(new Point(this.corners[7].x, this.corners[7].y, this.corners[7].z), new Coord(1, 1)));
      vertices.add(new Vertex(new Point(this.corners[6].x, this.corners[6].y, this.corners[6].z), new Coord(1, 0)));
      QuadVbo vbo = new QuadVbo(vertices, true);
      vbo.render();
      vbo.dispose();
      GL11.glEnable(GL11.GL_CULL_FACE);
   }

   public final State resolveCollision(State prevState, State curState) {
      return this.resolveCollision(prevState, curState, false);
   }

   public final State resolveCollision(State prevState, State curState, boolean canClimb) {
      groundHit = false;
      float maxScanY = 21.3F;
      if (!prevState.player) {
         maxScanY = 70.0F;
      }

      for (float scanY = 0.0F; scanY <= maxScanY; scanY += maxScanY / 10.0F) {
         curState.pos.add(new Point(0.0F, scanY, 0.0F));
         prevState.pos.add(new Point(0.0F, scanY, 0.0F));
         Point pos = curState.pos;
         int faceIdx = 0;

         boolean inside;
         while (true) {
            if (faceIdx >= 6) {
               inside = true;
               break;
            }

            if (this.faces[faceIdx].sideOf(pos) > 0) {
               inside = false;
               break;
            }

            faceIdx++;
         }

         if (inside) {
            for (int i = 0; i < 6; i++) {
               if (this.toTest[i] && this.faces[i].sideOf(prevState.pos) * this.faces[i].sideOf(curState.pos) <= 0) {
                  if (Math.abs((float)Math.toDegrees((Math.PI / 2) - Math.abs(Math.asin(this.faces[i].getNormal().y)))) < 45.0F
                     && this.faces[i].getNormal().dot(new Point(0.0F, 1.0F, 0.0F)) > 0.0F) {
                     curState.pos = this.faces[i].intersect(curState.pos, new Point(0.0F, 1.0F, 0.0F));
                     curState.pos.add(new Point(0.0, 0.01, 0.0));
                     curState.land();
                     groundHit = true;
                     break;
                  }

                  if (canClimb && curState.pos.x != prevState.pos.x && curState.pos.z != prevState.pos.z) {
                     float horizDist = Math.abs(curState.pos.x - prevState.pos.x) + Math.abs(curState.pos.z - prevState.pos.z);
                     curState.pos.y += horizDist / 1.5F;
                     curState.pos.x = prevState.pos.x + (curState.pos.x - prevState.pos.x) / 5.0F;
                     curState.pos.z = prevState.pos.z + (curState.pos.z - prevState.pos.z) / 5.0F;
                     curState.vel.y = 0.0F;
                  }

                  curState.pos = this.faces[i].intersect(curState.pos, this.faces[i].getNormal());
                  curState.pos.add(this.faces[i].getNormal().scaled(0.05F));
                  if (this.faces[i].getNormal().dot(new Point(0.0F, 1.0F, 0.0F)) < 0.0F) {
                     curState.stopVertical();
                  }
                  break;
               }
            }
         }

         prevState.pos.subtract(new Point(0.0F, scanY, 0.0F));
         curState.pos.subtract(new Point(0.0F, scanY, 0.0F));
      }

      return curState;
   }
}
