package game.map;
import game.seafloorBase.Octree;

import game.Main;
import game.render.Vbo;
import game.inventory.ItemType;
import game.manager.InGameState;
import game.manager.Loading;
import game.manager.Camera;
import game.manager.GameScene;
import game.manager.TextureManager;
import game.manager.GameTime;
import game.outsideObj.Extractor;
import game.outsideObj.HarpoonCannon;
import game.player.PlayerInput;
import game.shader.Shaders;
import game.util.Coord;
import game.util.Point;
import game.world.structure.GamePlayElmt;
import game.world.structure.GamePlayType;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public final class MapRenderer {
   private static int worldWidth;
   private static int worldHeight;
   private static Vbo fullMapVbo;
   private static Point[][] terrainPoints;
   private static Color[][] visibilityColors;
   private static boolean fullMapDirty = false;
   private static Vbo minimapVbo;
   private static Point lastMinimapPos;
   private static int minimapWidthChunks = 50;
   private static int minimapHeightChunks = 50;
   private static float echoPhase = 0.0F;
   private static float echoPower = 1.0F;
   public static PlayerInput mapInput;
   private static Point mapOffset;
   private static Point playerPos;
   private static float targetPanY = -100.0F;
   private static float currentPanY = -100.0F;
   private static float mapScale = 3.0F;
   private static float mapVerticalScale = 3.0F / 150.0F;

   public static void init() {
      mapOffset = new Point();
      lastMinimapPos = new Point();
      mapInput = new PlayerInput();
      fullMapVbo = new Vbo();
      minimapVbo = new Vbo();
   }

   public static void buildMap() {
      worldWidth = Loading.worldManager.getWorldWidth();
      worldHeight = Loading.worldManager.getWorldHeight();
      terrainPoints = new Point[worldWidth][worldHeight];
      visibilityColors = new Color[worldWidth][worldHeight];

      for (int z = 0; z < worldHeight; z++) {
         for (int x = 0; x < worldWidth; x++) {
            terrainPoints[x][z] = new Point((float)x, Loading.worldManager.getTerrainHeightAt((float)(x << 7), (float)(z << 7)), (float)z);
            if (Loading.worldManager.isVisibleAt(x, z)) {
               visibilityColors[x][z] = new Color(255, 255, 255, 60);
            } else if (Main.isRelease) {
               visibilityColors[x][z] = new Color(255, 255, 255, 0);
            } else {
               visibilityColors[x][z] = new Color(255, 255, 255, 0);
            }
         }
      }

      fullMapVbo.dispose();
      fullMapVbo.buildFromGrid(terrainPoints, null, visibilityColors, null, 0, 0, worldWidth, worldHeight, false);
      if (minimapWidthChunks > worldWidth) {
         minimapWidthChunks = worldWidth;
      }

      if (minimapHeightChunks > worldHeight) {
         minimapHeightChunks = worldHeight;
      }

      minimapVbo.dispose();
      minimapVbo.buildFromGrid(terrainPoints, null, visibilityColors, null, 0, 0, minimapWidthChunks, minimapHeightChunks, false);
      markVisibleArea();
      rebuildFullMap();
      rebuildMinimap();
   }

   public static void update(float dt) {
      if (fullMapDirty) {
         rebuildFullMap();
         mapOffset = new Point(-GameScene.avatar.getCameraPos().x / 128.0F * mapScale, -50.0F, -GameScene.avatar.getCameraPos().z / 128.0F * mapScale - 50.0F);
         fullMapDirty = false;
      }

      Point moveDir = new Point();
      if (mapInput.moveForward) {
         moveDir.add(new Point(0.0F, 0.0F, 1.0F));
      }

      if (mapInput.moveBackward) {
         moveDir.add(new Point(0.0F, 0.0F, -1.0F));
      }

      if (mapInput.strafeLeft) {
         moveDir.add(new Point(1.0F, 0.0F, 0.0F));
      }

      if (mapInput.strafeRight) {
         moveDir.add(new Point(-1.0F, 0.0F, 0.0F));
      }

      moveDir.normalize();
      mapOffset.add(moveDir.scaled(dt * 75.0F));
      if (Math.abs(currentPanY - targetPanY) > 0.05F) {
         currentPanY = currentPanY + (targetPanY - currentPanY) * 5.0F * dt;
      } else {
         currentPanY = targetPanY;
      }

      mapOffset.y = currentPanY;
      if (GameScene.avatar != null) {
         playerPos = new Point(GameScene.avatar.getCameraPos().x / 128.0F, GameScene.avatar.getCameraPos().y, GameScene.avatar.getCameraPos().z / 128.0F);
      } else {
         playerPos = new Point();
      }

      echoPhase = (echoPhase += dt * 3.0F) % 8.0F;
      echoPower = Math.min(-0.6F * echoPhase + 4.1000004F, 3.5F);
      if (GameScene.avatar != null && lastMinimapPos.distanceTo(GameScene.avatar.getCameraPos()) > 128.0F) {
         markVisibleArea();
         rebuildMinimap();
      }
   }

   private static void rebuildFullMap() {
      fullMapVbo.dispose();
      fullMapVbo.buildFromGrid(terrainPoints, null, visibilityColors, null, 0, 0, worldWidth, worldHeight, false);
   }

   private static void rebuildMinimap() {
      lastMinimapPos = GameScene.avatar.getCameraPos();
      int startX = (int)(GameScene.avatar.getCameraPos().x / 128.0F) - minimapWidthChunks / 2;
      int startZ = (int)(GameScene.avatar.getCameraPos().z / 128.0F) - minimapHeightChunks / 2;
      if (startX < 0) {
         startX = 0;
      }

      if (startZ < 0) {
         startZ = 0;
      }

      int width = Math.min(minimapWidthChunks, worldWidth - startX);
      int height = Math.min(minimapHeightChunks, worldHeight - startZ);
      if (width > 0 && height > 0) {
         minimapVbo.dispose();
         minimapVbo.buildFromGrid(terrainPoints, null, visibilityColors, null, startX, startZ, width, height, false);
      }
   }

   public static void renderFullMap() {
      GL11.glLineWidth(1.0F);
      Shaders.setUniform("playerPos", playerPos);
      Shaders.setUniform("echoDistance", echoPhase);
      Shaders.setUniform("echoPower", echoPower);
      GL11.glPushMatrix();
      GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GL11.glTranslated(mapOffset.x, mapOffset.y, mapOffset.z);
      GL11.glPushMatrix();
      Shaders.setUniform("drawMap", true);
      GL11.glScalef(mapScale, mapVerticalScale, mapScale);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      fullMapVbo.render();
      Shaders.setUniform("drawMap", false);
      Shaders.unbind();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(playerPos.x, playerPos.y, playerPos.z);
      GL11.glRotatef(GameScene.avatar.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.mapCursor);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-(38F / 15F), 0.0F, -(10F / 3F));
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(38F / 15F, 0.0F, -(10F / 3F));
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(38F / 15F, 0.0F, 1F / 3F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-(38F / 15F), 0.0F, 1F / 3F);
      GL11.glEnd();
      GL11.glPopMatrix();
      renderMapIcons(true);
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }

   public static void renderMinimapBackground() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth() - 10, 10.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth() - 220 - 10, 10.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth() - 220 - 10, 230.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth() - 10, 230.0F);
      GL11.glEnd();
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
      GL11.glLineWidth(1.0F);
      GL11.glBegin(GL11.GL_LINES);
      GL11.glVertex2f(Display.getWidth() - 10, 10.0F);
      GL11.glVertex2f(Display.getWidth() - 220 - 10, 10.0F);
      GL11.glVertex2f(Display.getWidth() - 10, 230.0F);
      GL11.glVertex2f(Display.getWidth() - 220 - 10, 230.0F);
      GL11.glVertex2f(Display.getWidth() - 10, 10.0F);
      GL11.glVertex2f(Display.getWidth() - 10, 230.0F);
      GL11.glVertex2f(Display.getWidth() - 220 - 10, 10.0F);
      GL11.glVertex2f(Display.getWidth() - 220 - 10, 230.0F);
      GL11.glEnd();
   }

   public static void renderMinimap() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glViewport(Display.getWidth() - 220 - 10, Display.getHeight() - 220 - 10, 220, 220);
      Shaders.setUniform("playerPos", playerPos);
      Shaders.setUniform("echoDistance", echoPhase / 4.0F);
      Shaders.setUniform("echoPower", echoPower);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glPushMatrix();
      GL11.glTranslated(0.0, 0.0, -20.0);
      GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(-GameScene.avatar.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
      GL11.glTranslated(-GameScene.avatar.getCameraPos().x / 128.0F * mapScale, -10.0F - playerPos.y * mapVerticalScale, -GameScene.avatar.getCameraPos().z / 128.0F * mapScale);
      GL11.glPushMatrix();
      Shaders.setUniform("drawMap", true);
      GL11.glScalef(mapScale, mapVerticalScale, mapScale);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      minimapVbo.render();
      Shaders.setUniform("drawMap", false);
      Shaders.unbind();
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(playerPos.x, playerPos.y, playerPos.z);
      GL11.glRotatef(GameScene.avatar.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.mapCursor);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(-(38F / 15F), 0.0F, -(10F / 3F));
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(38F / 15F, 0.0F, -(10F / 3F));
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(38F / 15F, 0.0F, 1F / 3F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-(38F / 15F), 0.0F, 1F / 3F);
      GL11.glEnd();
      GL11.glPopMatrix();
      renderMapIcons(false);
      GL11.glPopMatrix();
      GL11.glPopMatrix();
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
   }

   private static void renderMapIcons(boolean billboarded) {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.items);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(GL11.GL_DEPTH_TEST);

      for (int x = 0; x < worldWidth; x++) {
         for (int z = 0; z < worldHeight; z++) {
            GamePlayElmt elmt = Loading.worldManager.getGamePlayElmtAt(x, z);
            if (elmt.getType() != GamePlayType.NONE) {
               GL11.glPushMatrix();
               GL11.glTranslatef(terrainPoints[x][z].x + 0.5F, terrainPoints[x][z].y + 1.0F, terrainPoints[x][z].z + 0.5F);
               switch (elmt.getType()) {
                  case CHEST:
                     if (visibilityColors[x][z].getAlpha() <= 0) {
                        break;
                     }

                     if (elmt.getInventory() != null && !elmt.getInventory().isEmpty()) {
                        renderIcon(ItemType.UNNAMED_BUILDING_1, billboarded);
                        break;
                     }

                     renderIcon(ItemType.UNNAMED_BUILDING_2, billboarded);
                     break;
                  case GOLD_DEPOSIT:
                     if (visibilityColors[x][z].getAlpha() > 0) {
                        renderIcon(ItemType.GOLD, billboarded);
                     }
                     break;
                  case CRYSTAL_DEPOSIT:
                     if (visibilityColors[x][z].getAlpha() > 0) {
                        renderIcon(ItemType.CRYSTAL, billboarded);
                     }
                     break;
                  case CORAL_OVERLAY:
                     if (visibilityColors[x][z].getAlpha() > 0) {
                        renderIcon(ItemType.DIRT, billboarded);
                     }
                     break;
                  case GRASS_OVERLAY:
                     if (visibilityColors[x][z].getAlpha() > 0) {
                        renderIcon(ItemType.COAL, billboarded);
                     }
                     break;
                  case SILVER_DEPOSIT:
                     if (visibilityColors[x][z].getAlpha() > 0) {
                        renderIcon(ItemType.SILVER, billboarded);
                     }
                     break;
                  case SHIPWRECK:
                     if (visibilityColors[x][z].getAlpha() > 0) {
                        renderIcon(ItemType.SHIPWRECK, billboarded);
                     }
                     break;
                  case SUBMARINE_PART:
                     if ((GameScene.gameMode.showsSubPieces() || !GameScene.gameMode.showsSubPieces() && visibilityColors[x][z].getAlpha() > 0) && GameScene.getInGameState() != InGameState.INITIALIZING) {
                        float phase = (GameTime.elapsedMillis / 2000.0F + Math.abs((terrainPoints[x][z].x + terrainPoints[x][z].y + terrainPoints[x][z].z) / 100.0F)) % 1.0F;
                        GL11.glScalef(phase, phase, phase);
                        GL11.glColor4f(0.9F, 0.9F, 0.2F, -phase * (phase - 1.0F) * 6.0F);
                        renderIcon(ItemType.SUBMARINE_PIECE, billboarded);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                     }
				default:
					break;
               }

               GL11.glPopMatrix();
            }
         }
      }

      if (GameScene.outsideObjects != null) {
         for (int i = 0; i < GameScene.outsideObjects.size(); i++) {
            GL11.glPushMatrix();
            GL11.glTranslatef(
               GameScene.outsideObjects.get(i).getPosition().x / 128.0F,
               GameScene.outsideObjects.get(i).getPosition().y,
               GameScene.outsideObjects.get(i).getPosition().z / 128.0F
            );
            if (GameScene.outsideObjects.get(i) instanceof Extractor) {
               renderIcon(ItemType.EXTRACTOR, billboarded);
            }

            if (GameScene.outsideObjects.get(i) instanceof HarpoonCannon) {
               renderIcon(ItemType.HARPOON_CANNON, billboarded);
            }

            GL11.glPopMatrix();
         }
      }

      if (GameScene.seafloorBases != null) {
         for (int i = 0; i < GameScene.seafloorBases.size(); i++) {
            if (GameScene.seafloorBases.get(i).getBaseType() != Octree.BaseType.EXTRA
               || visibilityColors[clampX((int)(GameScene.seafloorBases.get(i).getPos().x / 128.0F))][clampZ((int)(GameScene.seafloorBases.get(i).getPos().z / 128.0F))]
                     .getAlpha()
                  > 0) {
               GL11.glPushMatrix();
               GL11.glTranslatef(
                  GameScene.seafloorBases.get(i).getPos().x / 128.0F,
                  GameScene.seafloorBases.get(i).getPos().y,
                  GameScene.seafloorBases.get(i).getPos().z / 128.0F
               );
               renderIcon(ItemType.NEW_BASE, billboarded);
               GL11.glPopMatrix();
            }
         }
      }

      if (GameScene.submarines != null) {
         for (int i = 0; i < GameScene.submarines.size(); i++) {
            GL11.glPushMatrix();
            GL11.glTranslatef(
               GameScene.submarines.get(i).getPosition().x / 128.0F,
               GameScene.submarines.get(i).getPosition().y,
               GameScene.submarines.get(i).getPosition().z / 128.0F
            );
            renderIcon(ItemType.NEW_SUBMARINE, billboarded);
            GL11.glPopMatrix();
         }
      }

      if (GameScene.droids != null) {
         for (int i = 0; i < GameScene.droids.size(); i++) {
            if (GameScene.droids.get(i).isWorking()
               || visibilityColors[clampX((int)(GameScene.droids.get(i).getPosition().x / 128.0F))][clampZ((int)(GameScene.droids.get(i).getPosition().z / 128.0F))].getAlpha() > 0) {
               GL11.glPushMatrix();
               GL11.glTranslatef(
                  GameScene.droids.get(i).getPosition().x / 128.0F,
                  GameScene.droids.get(i).getPosition().y,
                  GameScene.droids.get(i).getPosition().z / 128.0F
               );
               renderIcon(ItemType.DROID, billboarded);
               GL11.glPopMatrix();
            }
         }
      }

      if (GameScene.worldChest != null) {
         GL11.glPushMatrix();
         GL11.glTranslatef(GameScene.worldChest.getPosition().x / 128.0F, GameScene.worldChest.getPosition().y, GameScene.worldChest.getPosition().z / 128.0F);
         renderIcon(ItemType.TOMB, billboarded);
         GL11.glPopMatrix();
      }

      GL11.glEnable(GL11.GL_DEPTH_TEST);
   }

   private static void renderIcon(ItemType itemType, boolean billboarded) {
      GL11.glPushMatrix();
      if (!billboarded) {
         Camera.applyYaw();
      }

      GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(itemType.getXOffset() / 10.0F, itemType.getYOffset() / 20.0F);
      GL11.glVertex3f(-1.0F, 0.0F, 1.0F);
      GL11.glTexCoord2f(itemType.getXOffset() / 10.0F, (itemType.getYOffset() + 1) / 20.0F);
      GL11.glVertex3f(-1.0F, 0.0F, -1.0F);
      GL11.glTexCoord2f((itemType.getXOffset() + 1) / 10.0F, (itemType.getYOffset() + 1) / 20.0F);
      GL11.glVertex3f(1.0F, 0.0F, -1.0F);
      GL11.glTexCoord2f((itemType.getXOffset() + 1) / 10.0F, itemType.getYOffset() / 20.0F);
      GL11.glVertex3f(1.0F, 0.0F, 1.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
   }

   private static void markVisibleArea() {
      fullMapDirty = true;
      Coord playerChunkCoord = new Coord(GameScene.avatar.getCameraPos().x, GameScene.avatar.getCameraPos().z);

      for (int dx = -6; dx <= 6; dx++) {
         for (int dz = -6; dz <= 6; dz++) {
            if (new Coord(dx, dz).magnitude() <= 6.0F) {
               float worldX = playerChunkCoord.x + (dx << 7);
               float worldZ = playerChunkCoord.y + (dz << 7);
               int chunkX = (int)worldX / 128;
               int chunkZ = (int)worldZ / 128;
               if (chunkX > 0 && chunkZ > 0 && chunkX < worldWidth && chunkZ < worldHeight) {
                  visibilityColors[chunkX][chunkZ].setAlpha(60);
                  Loading.worldManager.setVisibleAt(chunkX, chunkZ, true);
               }
            }
         }
      }
   }

   private static int clampX(int x) {
      return Math.max(Math.min(x, worldWidth - 1), 0);
   }

   private static int clampZ(int z) {
      return Math.max(Math.min(z, worldHeight - 1), 0);
   }

   public static void scroll(float delta) {
      targetPanY += delta;
      targetPanY = Math.max(-150.0F, targetPanY);
      targetPanY = Math.min(-30.0F, targetPanY);
   }
}
