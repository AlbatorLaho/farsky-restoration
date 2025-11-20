package game.collision;

import game.chunks.Chunk;
import game.chunks.ChunkManager;
import game.environment.EnvironmentManager;
import game.manager.Camera;
import game.manager.GameScene;
import game.sounds.SoundManager;
import game.submarine.Submarine;
import game.submarine.SubmarineState;
import game.util.Coord;
import game.util.Point;
import game.util.Segment;
import game.util.State;

public final class CollisionDetector {
   private static float hitDist = 0.0F;
   private static float hitDistFar = 0.0F;
   private static Point terrainHitPoint = new Point();
   private static Point hitPoint = new Point();

   public static State resolveCollision(State prevState, State curState) {
      curState.onGround = false;
      if (curState.pos.y <= ChunkManager.getHeightSmooth(curState.pos.x, curState.pos.z)) {
         Point normal = ChunkManager.computeNormal(curState.pos.x, curState.pos.z);
         if (new Point(Math.abs(normal.x), 0.0F, Math.abs(normal.z)).magnitude() > 0.7F) {
            Point delta = curState.pos.minus(prevState.pos);
            normal = normal.scaled(-delta.dot(normal));
            curState.pos.add(normal);
         } else {
            curState.pos.y = ChunkManager.getHeightSmooth(curState.pos.x, curState.pos.z);
            curState.land();
         }

         if (curState.pos.y <= ChunkManager.getHeightSmooth(curState.pos.x, curState.pos.z)) {
            curState.pos.y = ChunkManager.getHeightSmooth(curState.pos.x, curState.pos.z);
            curState.land();
         }
      } else {
         Point normal = ChunkManager.computeNormal(curState.pos.x, curState.pos.z);
         Point horizNormal = new Point(Math.abs(normal.x), 0.0F, Math.abs(normal.z));
         if (curState.pos.y < ChunkManager.getHeightSmooth(curState.pos.x, curState.pos.z) + 1.0F && prevState.onGround && !curState.isMoving && horizNormal.magnitude() < 0.7F) {
            curState.pos.y = ChunkManager.getHeightSmooth(curState.pos.x, curState.pos.z);
            curState.land();
         }
      }

      curState = ChunkManager.resolveCollision(prevState, curState);
      curState = GameScene.resolveCollision(prevState, curState);
      return EnvironmentManager.resolveCollision(prevState, curState);
   }

   public static SubmarineState resolveSubmarineCollision(SubmarineState prevState, SubmarineState curState, AABB bounds, float dt, Submarine submarine) {
      float speed = prevState.state.vel.length();

      for (float scanX = curState.state.pos.x + bounds.min.x; scanX <= curState.state.pos.x + bounds.max.x; scanX += Chunk.TERRAIN_STEP << 1) {
         for (float scanZ = curState.state.pos.z + bounds.min.z; scanZ <= curState.state.pos.z + bounds.max.z; scanZ += Chunk.TERRAIN_STEP << 1) {
            Point testPoint = new Point(scanX, 0.0F, scanZ).minus(curState.state.pos);
            testPoint.rotateY(curState.rot.y);
            testPoint.add(curState.state.pos);
            testPoint.y = ChunkManager.getHeight((int)testPoint.x, (int)testPoint.z);
            if (containsPoint(testPoint, bounds, curState.state.pos, curState.rot)) {
               testPoint = ChunkManager.computeNormal((float)((int)scanX), (float)((int)scanZ));
               testPoint.normalize();
               curState.state.vel.add(testPoint.scaled(-curState.state.vel.dot(testPoint)));
               curState.state.pos = prevState.state.pos.plus(curState.state.vel.scaled(dt));
               curState.state.pos.add(0.0F, dt * 20.0F, 0.0F);
               if (speed > 75.0F && !SoundManager.isTransientSoundPlaying(SoundManager.sfxVesselCollision)) {
                  SoundManager.playSound(SoundManager.sfxVesselCollision, null, 0.9F + (float)Math.random() * 0.2F, 0.2F);
                  Camera.addShake(0.2F);
               }
            }
         }
      }

      for (float sampleX = bounds.min.x; sampleX <= bounds.max.x; sampleX += (bounds.max.x - bounds.min.x) / 3.0F) {
         for (float sampleZ = bounds.min.z; sampleZ <= bounds.max.z; sampleZ += (bounds.max.z - bounds.min.z) / 3.0F) {
            Point offset = new Point(sampleX, bounds.min.y - 10.0F, sampleZ);
            prevState.state.pos.add(offset);
            curState.state.pos.add(offset);
            curState.state = GameScene.resolveCollision(prevState.state, curState.state, submarine);
            prevState.state.pos.subtract(offset);
            curState.state.pos.subtract(offset);
         }
      }

      curState.state = EnvironmentManager.resolveCollision(prevState.state, curState.state);
      return curState;
   }

   public static boolean raycastTerrain(Point origin, Point end, boolean findClosest) {
      float step = 3.0F;
      findClosest = true;
      Point dir = end.minus(origin);
      boolean hit = false;
      float maxDist = dir.length();
      dir.normalize();
      hitPoint = null;
      float minX = Math.min(origin.x, end.x);
      float maxX = Math.max(origin.x, end.x);
      float minZ = Math.min(origin.z, end.z);
      float maxZ = Math.max(origin.z, end.z);

      for (int i = 0; i < ChunkManager.activeChunks.size(); i++) {
         Coord chunkCenter = new Coord(ChunkManager.activeChunks.get(i).x + 64, ChunkManager.activeChunks.get(i).z + 64);
         boolean inRange = false;
         if (chunkCenter.x >= minX - 64.0F && chunkCenter.x <= maxX + 64.0F && chunkCenter.y >= minZ - 64.0F && chunkCenter.y <= maxZ + 64.0F) {
            inRange = true;
         }

         ChunkManager.activeChunks.get(i).debugHighlight = inRange;
         if (inRange && rayIntersectsAABB(origin, dir, ChunkManager.activeChunks.get(i).getBounds()) && hitDist < maxDist) {
            float tFar = Math.min(hitDistFar, maxDist);
            Point nearHit = origin.plus(dir.scaled(hitDist));
            Point farHit = origin.plus(dir.scaled(tFar));
            end = nearHit;
            float t = 0.0F;
            Point segDir = farHit.minus(end);
            float segLen = segDir.length();
            segDir.normalize();

            boolean terrainHit;
            while (true) {
               if (!(t < segLen)) {
                  terrainHit = false;
                  break;
               }

               Point sample;
               sample = end.plus(segDir.scaled(t));
               if (ChunkManager.getHeightSmooth(sample.x, sample.z) > sample.y) {
                  terrainHitPoint = new Point(sample.x, ChunkManager.getHeightSmooth(sample.x, sample.z), sample.z);
                  terrainHit = true;
                  break;
               }

               t += step;
            }

            if (terrainHit) {
               if (!findClosest) {
                  return true;
               }

               if (hitPoint == null || origin.distanceTo(hitPoint) > origin.distanceTo(terrainHitPoint)) {
                  hitPoint = terrainHitPoint.copy();
               }

               hit = true;
            }
         }
      }

      return hit;
   }

   public static float raycast(Point origin, Point dir, float maxDist) {
      float dist = maxDist * 2.0F;
      if (raycastTerrain(origin, origin.plus(dir.scaled(maxDist)), true)) {
         dist = hitPoint.distanceTo(origin);
      }

      for (int i = 0; i < GameScene.getCollisionBoxes().size(); i++) {
         if (GameScene.getCollisionBoxes().get(i).getPosition().distanceTo(origin) < maxDist && rayIntersectsBox(origin, dir, GameScene.getCollisionBoxes().get(i).getBounds(), GameScene.getCollisionBoxes().get(i).getPosition(), GameScene.getCollisionBoxes().get(i).getRotation())) {
            dist = Math.min(dist, hitDist);
         }
      }

      if (dist > maxDist) {
         dist = -1.0F;
      }

      return dist;
   }

   public static boolean containsPoint(Point point, AABB bounds, Point position, Point rotation) {
      Point localPoint = applyInverseRotation(point.minus(position), rotation);
      return localPoint.x >= bounds.min.x
         && localPoint.x <= bounds.max.x
         && localPoint.y >= bounds.min.y
         && localPoint.y <= bounds.max.y
         && localPoint.z >= bounds.min.z
         && localPoint.z <= bounds.max.z;
   }

   public static boolean segmentIntersects(Segment segment, AABB bounds, Point position, Point rotation) {
      float radius = bounds.max.x + bounds.max.y + bounds.max.z;
      if (segment.start.distanceTo(bounds.getCenter().plus(position)) > radius && segment.end.distanceTo(bounds.getCenter().plus(position)) > radius) {
         return false;
      } else {
         Point dir = segment.end.minus(segment.start);
         dir.normalize();
         if (rayIntersectsBox(segment.start, dir, bounds, position, rotation)) {
            if (hitDist <= segment.length()) {
               return true;
            }

            if (containsPoint(segment.start, bounds, position, rotation)) {
               return true;
            }

            if (containsPoint(segment.end, bounds, position, rotation)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean rayIntersectsBox(Point origin, Point dir, AABB bounds, Point position, Point rotation) {
      origin = origin.minus(position);
      dir = applyInverseRotation(dir, rotation);
      return rayIntersectsAABB(applyInverseRotation(origin, rotation), dir, bounds);
   }

   public static boolean rayIntersectsAABB(Point origin, Point dir, AABB bounds) {
      float tNear = -1.0F;
      float tFar = -1.0F;
      boolean hit = false;
      float t = (bounds.max.x - origin.x) / dir.x;
      float hitX = dir.x * t + origin.x;
      float hitY = dir.y * t + origin.y;
      t = dir.z * t + origin.z;
      if (isHitOnFace(hitX, hitY, t, bounds, false, true, true)) {
         tNear = new Point(hitX, hitY, t).minus(origin).length();
         tFar = new Point(hitX, hitY, t).minus(origin).length();
         hit = true;
      }

      t = (bounds.min.x - origin.x) / dir.x;
      hitX = dir.x * t + origin.x;
      hitY = dir.y * t + origin.y;
      t = dir.z * t + origin.z;
      if (isHitOnFace(hitX, hitY, t, bounds, false, true, true)) {
         if (tNear > new Point(hitX, hitY, t).minus(origin).length() || tNear < 0.0F) {
            tNear = new Point(hitX, hitY, t).minus(origin).length();
         }

         if (tFar < new Point(hitX, hitY, t).minus(origin).length() || tFar < 0.0F) {
            tFar = new Point(hitX, hitY, t).minus(origin).length();
         }

         hit = true;
      }

      t = (bounds.max.y - origin.y) / dir.y;
      hitX = dir.x * t + origin.x;
      hitY = dir.y * t + origin.y;
      t = dir.z * t + origin.z;
      if (isHitOnFace(hitX, hitY, t, bounds, true, false, true)) {
         if (tNear > new Point(hitX, hitY, t).minus(origin).length() || tNear < 0.0F) {
            tNear = new Point(hitX, hitY, t).minus(origin).length();
         }

         if (tFar < new Point(hitX, hitY, t).minus(origin).length() || tFar < 0.0F) {
            tFar = new Point(hitX, hitY, t).minus(origin).length();
         }

         hit = true;
      }

      t = (bounds.min.y - origin.y) / dir.y;
      hitX = dir.x * t + origin.x;
      hitY = dir.y * t + origin.y;
      t = dir.z * t + origin.z;
      if (isHitOnFace(hitX, hitY, t, bounds, true, false, true)) {
         if (tNear > new Point(hitX, hitY, t).minus(origin).length() || tNear < 0.0F) {
            tNear = new Point(hitX, hitY, t).minus(origin).length();
         }

         if (tFar < new Point(hitX, hitY, t).minus(origin).length() || tFar < 0.0F) {
            tFar = new Point(hitX, hitY, t).minus(origin).length();
         }

         hit = true;
      }

      t = (bounds.max.z - origin.z) / dir.z;
      hitX = dir.x * t + origin.x;
      hitY = dir.y * t + origin.y;
      t = dir.z * t + origin.z;
      if (isHitOnFace(hitX, hitY, t, bounds, true, true, false)) {
         if (tNear > new Point(hitX, hitY, t).minus(origin).length() || tNear < 0.0F) {
            tNear = new Point(hitX, hitY, t).minus(origin).length();
         }

         if (tFar < new Point(hitX, hitY, t).minus(origin).length() || tFar < 0.0F) {
            tFar = new Point(hitX, hitY, t).minus(origin).length();
         }

         hit = true;
      }

      t = (bounds.min.z - origin.z) / dir.z;
      hitX = dir.x * t + origin.x;
      hitY = dir.y * t + origin.y;
      t = dir.z * t + origin.z;
      if (isHitOnFace(hitX, hitY, t, bounds, true, true, false)) {
         if (tNear > new Point(hitX, hitY, t).minus(origin).length() || tNear < 0.0F) {
            tNear = new Point(hitX, hitY, t).minus(origin).length();
         }

         if (tFar < new Point(hitX, hitY, t).minus(origin).length() || tFar < 0.0F) {
            tFar = new Point(hitX, hitY, t).minus(origin).length();
         }

         hit = true;
      }

      hitDist = tNear;
      hitDistFar = tFar;
      return hit;
   }

   public static float getHitDistance() {
      return hitDist;
   }

   public static Point getHitPoint() {
      return hitPoint;
   }

   private static boolean isHitOnFace(float x, float y, float z, AABB bounds, boolean checkX, boolean checkY, boolean checkZ) {
      return (!checkX || x <= bounds.max.x && x >= bounds.min.x)
         && (!checkY || y <= bounds.max.y && y >= bounds.min.y)
         && (!checkZ || z <= bounds.max.z && z >= bounds.min.z);
   }

   private static Point applyInverseRotation(Point point, Point rotation) {
      Point result = point.copy();
      result.rotateY(-rotation.y);
      result.rotateX(-rotation.x);
      return result;
   }
}
