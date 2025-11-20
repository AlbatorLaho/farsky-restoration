package game.seafloorBase;

import game.collision.AABB;
import game.inventory.Item;
import game.inventory.ItemType;
import game.inventory.types.BuildingTableInventory;
import game.inventory.types.CookerInventory;
import game.inventory.types.EquipmentTableInventory;
import game.inventory.types.FurnitureTableInventory;
import game.inventory.types.Inventory;
import game.inventory.types.WeaponTableInventory;
import game.inventory.types.WorkShopInventory;
import game.manager.TextureManager;
import game.render.ModelLoader;
import game.render.QuadVbo;
import game.render.Vbo;
import game.render.Vertex;
import game.manager.GameScene;
import game.manager.GameTime;
import game.seafloorBase.util.BlockType;
import game.seafloorBase.util.Dir;
import game.seafloorBase.util.Material;
import game.shader.Shaders;
import game.sounds.SoundManager;
import game.util.Coord;
import game.util.Point;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

public class Element implements Serializable {
   private static final long serialVersionUID = -2008719842854495882L;
   private BlockType type;
   private Material material;
   private Dir dir;
   private float param = 0.0F;
   public Inventory inventory = null;
   private boolean waterLeak = false;
   private boolean drawWaterLevel0 = false;
   private boolean drawWaterLevel1 = false;
   public static transient float scale = 5.0F;
   public transient boolean highlighted = false;
   private transient float prevParam = 0.0F;
   private transient Point worldPos;
   private static transient Vbo wallMesh;
   private static transient Vbo wallAcuteAngleMesh;
   private static transient Vbo wallObtuseAngleMesh;
   private static transient Vbo wallSeparationMesh;
   private static transient Vbo roofMesh;
   private static transient Vbo topMesh;
   private static transient Vbo floorMesh;
   private static transient Vbo glassWallMesh;
   private static transient Vbo glassMesh;
   private static transient Vbo waterLevelMesh;
   private static transient Vbo hatchMesh;
   private static transient Vbo pillarMesh;
   private static transient Vbo ladderMesh;
   private static transient Vbo outsidePillarMesh;
   private static transient Vbo workshopTableMesh;
   private static transient Vbo equipmentTableMesh;
   private static transient Vbo weaponTableMesh;
   private static transient Vbo buildingTableMesh;
   private static transient Vbo furnitureTableMesh;
   private static transient Vbo plantPotMesh;
   private static transient Vbo plantMesh;
   private static transient Vbo chestSmallMesh;
   private static transient Vbo chestSmallTopMesh;
   private static transient int chestSmallTopTexture;
   private static transient Vbo tableMesh;
   private static transient Vbo stoolMesh;
   private static transient Vbo doorMesh;
   private static transient Vbo doorSliceMesh;
   private static transient int doorSliceTexture;
   private static transient Vbo cookerMesh;

   public Element(BlockType blockType) {
      this(blockType, Dir.NORTH);
   }

   public Element(BlockType blockType, Dir direction) {
      this.type = blockType;
      this.dir = direction;
      this.material = Material.SOLID;
      if (blockType == BlockType.WORKSHOP) {
         this.inventory = new WorkShopInventory();
      }

      if (blockType == BlockType.EQUIPMENT_WORKSHOP) {
         this.inventory = new EquipmentTableInventory();
      }

      if (blockType == BlockType.WEAPON_WORKSHOP) {
         this.inventory = new WeaponTableInventory();
      }

      if (blockType == BlockType.FURNITURE_WORKSHOP) {
         this.inventory = new FurnitureTableInventory();
      }

      if (blockType == BlockType.BUILDING_WORKSHOP) {
         this.inventory = new BuildingTableInventory();
      }

      if (blockType == BlockType.CHEST) {
         this.inventory = new Inventory("Chest", 3, 2);
      }

      if (blockType == BlockType.LARGE_CHEST) {
         this.inventory = new Inventory("Large Chest", 4, 3);
      }

      if (blockType == BlockType.COOKER) {
         this.inventory = new CookerInventory();
      }

      if (blockType == BlockType.PLANT_POT) {
         this.inventory = new Inventory("", 1, 1);
      }
   }

   public final void setMaterial(Material material) {
      this.material = material;
   }

   public final void update(float delta) {
      if (this.type == BlockType.PLANT_POT && this.inventory != null && this.inventory.getStorageArray().get(0, 0).getItem() != null) {
         if (this.param < 1.0F) {
            if (this.inventory.getStorageArray().get(0, 0).getItem().getType() == ItemType.POTATO) {
               this.param += delta / 1000.0F;
            } else if (this.inventory.getStorageArray().get(0, 0).getItem().getType() == ItemType.CARROT_SEED) {
               this.param += delta / 700.0F;
            } else if (this.inventory.getStorageArray().get(0, 0).getItem().getType() == ItemType.GREEN_BEAN) {
               this.param += delta / 900.0F;
            }
         } else {
            this.param = 1.0F;
         }
      }

      if (this.waterLeak) {
         this.prevParam += delta;
         float interval = 1.0F - this.param / 2.0F;
         if (this.prevParam >= interval * 1.0F) {
            this.prevParam--;
            if (this.worldPos.distanceTo(GameScene.avatar.getCameraPos()) < 300.0F) {
               float volume = 0.1F;
               if (!GameScene.avatar.isInside()) {
                  volume = 1F / 25F;
               }

               if (this.worldPos != null) {
                  SoundManager.playSound(SoundManager.sfxWaterLeak, this.worldPos, interval, volume);
               }
            }
         }
      }

      if (this.type == BlockType.DOOR && this.param > 0.0F) {
         this.param -= delta * 1.0F;
         if (this.param < 0.0F) {
            this.param = 0.0F;
         }

         if (this.param > 0.05F && this.param < 0.85F) {
            if (this.param > this.prevParam && !SoundManager.isTransientSoundPlaying(SoundManager.sfxDoorOpen)) {
               SoundManager.playSound(SoundManager.sfxDoorOpen, this.worldPos, 1.0F, 0.2F);
            }

            if (this.param < this.prevParam && !SoundManager.isTransientSoundPlaying(SoundManager.sfxDoorClose)) {
               SoundManager.playSound(SoundManager.sfxDoorClose, this.worldPos, 1.0F, 0.2F);
            }
         }

         this.prevParam = this.param;
      }

      if (this.type == BlockType.COOKER) {
         this.inventory.update(delta);
      }
   }

   public final boolean trackPosition(Point pos) {
      if (this.type != BlockType.PLANT_POT && this.type != BlockType.DOOR && this.type != BlockType.COOKER && !this.waterLeak) {
         return false;
      } else {
         this.worldPos = pos.copy();
         return true;
      }
   }

   private void applyDirRotation() {
      if (this.dir == Dir.EAST) {
         GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
      } else if (this.dir == Dir.SOUTH) {
         GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      } else if (this.dir == Dir.WEST) {
         GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
      }
   }

   public final void renderStatic(boolean isAlpha) {
      this.applyDirRotation();

      if (this.highlighted) {
         Shaders.setUniform("selected", true);
      }

      GL11.glScalef(scale, scale, scale);
      switch (this.type) {
         case WALL:
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            if (this.material == Material.SOLID) {
               wallMesh.render();
            } else if (this.material == Material.GLASS) {
               if (!isAlpha) {
                  glassWallMesh.render();
               } else {
                  GL11.glColor4f(0.25F, 0.25F, 0.25F, 0.5F);
                  glassMesh.render();
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               }
            }
            break;
         case FLOOR:
            floorMesh.render();
            break;
         case CEILING:
            roofMesh.render();
            break;
         case WALL_CORNER:
            if (this.material == Material.SOLID) {
               wallObtuseAngleMesh.render();
            }
            break;
         case WALL_DIAGONAL:
            wallAcuteAngleMesh.render();
            break;
         case WALL_T:
            wallSeparationMesh.render();
            break;
         case WALL_CROSS:
            pillarMesh.render();
            break;
         case SEABED:
            GL11.glTranslatef(0.0F, 35.0F / scale, 0.0F);
            GL11.glScalef(0.7F, this.param, 0.7F);
            outsidePillarMesh.render();
            break;
         case SOFFIT:
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(0.0F, -35.0F / scale + 0.1F, 0.0F);
            topMesh.render();
            break;
         case ROOF:
            GL11.glTranslatef(0.0F, 0.1F, 0.0F);
            topMesh.render();
            break;
         case WATER_LEVEL:
            if ((this.param > 0.0F || this.drawWaterLevel0) && (this.param < 1.0F || this.drawWaterLevel1)) {
               GL11.glColor4f(0.375F, 0.525F, 0.75F, 0.75F);
               GL11.glTranslatef(0.0F, 35.0F / scale * this.param, 0.0F);
               waterLevelMesh.render();
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
            break;
         case HOLE:
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            hatchMesh.render();
            break;
         case LADDER:
            ladderMesh.render();
            break;
         case WORKSHOP:
            workshopTableMesh.render();
            break;
         case EQUIPMENT_WORKSHOP:
            equipmentTableMesh.render();
            break;
         case BUILDING_WORKSHOP:
            buildingTableMesh.render();
            break;
         case FURNITURE_WORKSHOP:
            furnitureTableMesh.render();
            break;
         case WEAPON_WORKSHOP:
            weaponTableMesh.render();
            break;
         case PLANT_POT:
            plantPotMesh.render();
            break;
         case LARGE_CHEST:
            GL11.glScalef(1.2F, 1.2F, 1.2F);
            chestSmallMesh.render();
            break;
         case CHEST:
            GL11.glScalef(0.9F, 0.9F, 0.9F);
            chestSmallMesh.render();
            break;
         case TABLE:
            GL11.glScalef(1.1F, 1.3F, 1.1F);
            tableMesh.render();
            break;
         case STOOL:
            stoolMesh.render();
            break;
         case DOOR:
            GL11.glTranslatef(0.0F, 0.0F, -1.0F);
            doorMesh.render();
            break;
         case COOKER:
            if (((CookerInventory)this.inventory).isCooking()) {
               float cookPulse = ((float)Math.cos(GameTime.elapsedMillis / 150.0F + (float)Math.random() * 2.0F) * 0.5F + 0.5F) * 0.2F + 0.5F;
               Shaders.setUniform("lightColor", new Point(cookPulse * 0.9F, cookPulse * 0.4F, 0.0F));
            } else {
               Shaders.setUniform("lightColor", new Point(0.0F, 0.0F, 0.0F));
            }

            cookerMesh.render();
      }

      if (this.highlighted) {
         Shaders.setUniform("selected", false);
      }
   }

   public final int getTextureId(boolean isAlpha) {
      switch (this.type) {
         case WALL:
            if (this.material == Material.SOLID) {
               return BaseModels.wall;
            }

            if (this.material == Material.GLASS) {
               if (!isAlpha) {
                  return BaseModels.glassWall;
               }

               return BaseModels.glass;
            }
            break;
         case FLOOR:
            return BaseModels.floor;
         case CEILING:
            return BaseModels.roof;
         case WALL_CORNER:
            if (this.material == Material.SOLID) {
               return BaseModels.wall;
            }

            if (this.material == Material.GLASS) {
               if (!isAlpha) {
                  return BaseModels.glassWall;
               }

               return BaseModels.glass;
            }
            break;
         case WALL_DIAGONAL:
         case WALL_T:
         case WALL_CROSS:
            return BaseModels.wall;
         case SEABED:
            return BaseModels.outsidePillar;
         case SOFFIT:
         case ROOF:
            return BaseModels.top;
         case WATER_LEVEL:
            return BaseModels.water;
         case HOLE:
            return BaseModels.hatch;
         case LADDER:
            return BaseModels.ladder;
         case WORKSHOP:
            return BaseModels.workshopTable;
         case EQUIPMENT_WORKSHOP:
            return BaseModels.equipmentTable;
         case BUILDING_WORKSHOP:
            return BaseModels.buildingTable;
         case FURNITURE_WORKSHOP:
            return BaseModels.furnitureTable;
         case WEAPON_WORKSHOP:
            return BaseModels.weaponTable;
         case PLANT_POT:
            return BaseModels.plantPot;
         case LARGE_CHEST:
         case CHEST:
            return BaseModels.chestSmall;
         case TABLE:
            return BaseModels.table;
         case STOOL:
            return BaseModels.stool;
         case DOOR:
            return BaseModels.door;
         case COOKER:
            return BaseModels.cooker;
      }

      return 0;
   }

   public final boolean isAnimated() {
      switch (this.type) {
         case PLANT_POT:
         case LARGE_CHEST:
         case CHEST:
         case DOOR:
            return true;
         case TABLE:
         case STOOL:
         default:
            return false;
      }
   }

   public final void renderAnimated() {
      this.applyDirRotation();

      if (this.highlighted) {
         Shaders.setUniform("selected", true);
      }

      GL11.glScalef(scale, scale, scale);
      switch (this.type) {
         case PLANT_POT:
            Shaders.setUniform("discardTransparency", true);
            if (this.inventory != null && this.inventory.getStorageArray().get(0, 0).getItem() != null) {
               if (this.inventory.getStorageArray().get(0, 0).getItem().getType() == ItemType.POTATO) {
                  if (this.param == 1.0F) {
                     GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.potatoPlantDone);
                  } else {
                     GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.potatoPlantGrowing);
                  }
               } else if (this.inventory.getStorageArray().get(0, 0).getItem().getType() == ItemType.CARROT_SEED) {
                  if (this.param == 1.0F) {
                     GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.carrotPlantDone);
                  } else {
                     GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.carrotPlantGrowing);
                  }
               } else if (this.inventory.getStorageArray().get(0, 0).getItem().getType() == ItemType.GREEN_BEAN) {
                  if (this.param == 1.0F) {
                     GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.greenBeanPlantDone);
                  } else {
                     GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.greenBeanPlantGrowing);
                  }
               }
            }

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glTranslatef(0.0F, 35.0F / scale * 0.1F, 0.0F);
            float plantScale = 2.0F * this.param;
            GL11.glScalef(plantScale, plantScale, plantScale);
            plantMesh.render();
            GL11.glEnable(GL11.GL_CULL_FACE);
            Shaders.setUniform("discardTransparency", false);
            break;
         case LARGE_CHEST:
         case CHEST:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, chestSmallTopTexture);
            GL11.glPushMatrix();
            if (this.type == BlockType.LARGE_CHEST) {
               GL11.glScalef(1.2F, 1.2F, 1.2F);
            } else {
               GL11.glScalef(0.9F, 0.9F, 0.9F);
            }

            GL11.glTranslatef(0.72F, 1.3F, 0.0F);
            GL11.glRotatef(-this.param * 50.0F, 0.0F, 0.0F, 1.0F);
            chestSmallTopMesh.render();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            if (this.type == BlockType.LARGE_CHEST) {
               GL11.glScalef(1.2F, 1.2F, 1.2F);
            } else {
               GL11.glScalef(0.9F, 0.9F, 0.9F);
            }

            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.72F, 1.3F, 0.0F);
            GL11.glRotatef(-this.param * 50.0F, 0.0F, 0.0F, 1.0F);
            chestSmallTopMesh.render();
            GL11.glPopMatrix();
            break;
         case TABLE:
         case STOOL:
         default:
            break;
         case DOOR:
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, doorSliceTexture);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, this.param * 35.0F / scale, -1.0F);

            for (int sliceIndex = (int)(9.0F * this.param + 0.5F); sliceIndex < 9; sliceIndex++) {
               GL11.glTranslatef(0.0F, (35.0F / scale - 1.0F) / 9.0F, 0.0F);
               doorSliceMesh.render();
            }

            GL11.glPopMatrix();
      }

      if (this.highlighted) {
         Shaders.setUniform("selected", false);
      }
   }

   public final void renderLeakEffect() {
      this.applyDirRotation();

      GL11.glScalef(scale, scale, scale);
      switch (this.type) {
         case WALL:
            renderLeakQuad(new Point(0.0F, 35.0F / scale - 0.3F, -0.95F));
         default:
            return;
         case CEILING:
            renderLeakQuad(new Point(0.0F, 35.0F / scale - 0.5F, 0.7F));
      }
   }

   private static void renderLeakQuad(Point origin) {
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glDepthMask(false);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.leak);
      GL11.glPushMatrix();
      GL11.glTranslatef(origin.x, origin.y, origin.z);
      GL11.glRotatef(25.0F, 1.0F, 0.0F, 0.0F);
      GL11.glColor4f(0.8F, 0.9F, 1.0F, 1.0F);
      GL11.glScalef(2.5F, 2.5F, 2.5F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(0.5F, 0.5F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(0.5F, -0.5F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(-0.5F, -0.5F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(-0.5F, 0.5F, 0.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      float maxDist = 35.0F / scale / 1.2F;

      for (int i = 0; i < 9; i++) {
         float dropPos = (i / 9.0F * maxDist + GameTime.elapsedMillis / 200.0F) % maxDist;
         float fade = (maxDist - dropPos) / maxDist;
         GL11.glPushMatrix();
         GL11.glTranslatef(origin.x, origin.y, origin.z);
         GL11.glRotatef(25.0F - dropPos * 4.0F, 1.0F, 0.0F, 0.0F);
         GL11.glTranslatef(0.0F, -dropPos, 0.0F);
         GL11.glScalef(fade * 1.5F + 1.0F, fade * 1.5F + 1.0F, fade * 1.5F + 1.0F);
         Point dropColor = new Point(0.8F * fade, 0.9F * fade, fade * 1.0F);
         GL11.glColor4f(dropColor.x, dropColor.y, dropColor.z, fade);
         GL11.glBegin(GL11.GL_QUADS);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex3f(0.5F, 0.5F, 0.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex3f(0.5F, -0.5F, 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex3f(-0.5F, -0.5F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex3f(-0.5F, 0.5F, 0.0F);
         GL11.glEnd();
         fade = -(fade - 0.1F) * (fade - 0.8F) * 4.0F;
         if (fade > 0.0F) {
            GL11.glColor4f(dropColor.x, dropColor.y, dropColor.z, fade);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex3f(0.0F, 0.5F, 0.5F);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex3f(0.0F, -0.5F, 0.5F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex3f(0.0F, -0.5F, -0.5F);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex3f(0.0F, 0.5F, -0.5F);
            GL11.glEnd();
         }

         GL11.glPopMatrix();
      }

      GL11.glDepthMask(true);
      GL11.glEnable(GL11.GL_CULL_FACE);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public final AABB getBoundingBox() {
      AABB box = null;
      switch (this.type) {
         case WALL:
            box = new AABB(new Point(0.0F, 17.5F, -2.0F), 14.0F, 43.666668F, 8.0F);
            box.setFacesToTest(true, true, false, false, true, true);
            break;
         case FLOOR:
            box = new AABB(new Point(0.0F, 1.1666666F, 0.0F), 14.0F, 6.333333F, 14.0F);
            break;
         case CEILING:
            box = new AABB(new Point(0.0F, 33.833332F, 0.0F), 14.0F, 6.333333F, 14.0F);
            break;
         case WALL_CORNER:
         case WALL_T:
         case WALL_CROSS:
            box = new AABB(new Point(0.0F, 17.5F, 0.0F), 14.0F, 43.666668F, 14.0F);
            break;
         case WALL_DIAGONAL:
            box = new AABB(new Point(-2.0F, 17.5F, -2.0F), 8.0F, 43.666668F, 8.0F);
            break;
         case SEABED:
            box = new AABB(new Point(0.0F, -35.0F * this.param / 2.0F + 17.5F, 0.0F), 14.0F, 35.0F * this.param, 14.0F);
            break;
         case SOFFIT:
            box = new AABB(new Point(0.0F, 33.833332F, 0.0F), 14.0F, 6.333333F, 14.0F);
            break;
         case ROOF:
            box = new AABB(new Point(0.0F, 1.1666666F, 0.0F), 14.0F, 6.333333F, 14.0F);
         case WATER_LEVEL:
         case HOLE:
         default:
            break;
         case LADDER:
            box = new AABB(new Point(0.0F, -7.666667F, -6.25F), 14.0F, 24.0F, 9.0F);
            break;
         case WORKSHOP:
            box = new AABB(new Point(0.0F, 8.75F, 0.0F), 14.0F, 21.5F, 14.0F);
            break;
         case EQUIPMENT_WORKSHOP:
            box = new AABB(new Point(0.0F, 13.461539F, 0.0F), 14.0F, 30.923079F, 14.0F);
            break;
         case BUILDING_WORKSHOP:
            box = new AABB(new Point(0.0F, 11.666667F, -2.5F), 14.0F, 27.333334F, 9.0F);
            break;
         case FURNITURE_WORKSHOP:
            box = new AABB(new Point(0.0F, 14.583333F, -2.5F), 14.0F, 33.166664F, 9.0F);
            break;
         case WEAPON_WORKSHOP:
            box = new AABB(new Point(0.0F, 15.909091F, -2.5F), 14.0F, 35.818184F, 9.0F);
            break;
         case PLANT_POT:
            box = new AABB(new Point(0.0F, 3.5F, 0.0F), 14.0F, 11.0F, 14.0F);
            break;
         case LARGE_CHEST:
            box = new AABB(new Point(0.0F, 5.0F, 0.0F), 14.0F, 14.0F, 14.0F);
            break;
         case CHEST:
            box = new AABB(new Point(0.0F, 2.9166667F, 0.0F), 14.0F, 9.833334F, 14.0F);
            break;
         case TABLE:
         case STOOL:
            box = new AABB(new Point(0.0F, 7.0F, 0.0F), 14.0F, 18.0F, 14.0F);
            break;
         case DOOR:
            box = new AABB(new Point(0.0F, 17.5F + 17.5F * this.param, -5.0F), 14.0F, 35.0F * (1.0F - this.param) + 4.0F, 8.0F);
            break;
         case COOKER:
            box = new AABB(new Point(0.0F, 8.75F, 0.0F), 14.0F, 21.5F, 14.0F);
      }

      return rotateBoxForDir(box);
   }

   private AABB rotateBoxForDir(AABB box) {
      if (box == null) {
         return null;
      } else {
         if (this.dir == Dir.EAST) {
            box.rotate(-90.0F);
         } else if (this.dir == Dir.SOUTH) {
            box.rotate(180.0F);
         } else if (this.dir == Dir.WEST) {
            box.rotate(90.0F);
         }

         return box;
      }
   }

   public final AABB getWaterLevelBox() {
      AABB box = null;
      switch (this.type) {
         case WATER_LEVEL:
            box = new AABB(new Point(0.0F, 35.0F * this.param, 0.0F), 14.0F, 0.5833333F, 14.0F);
         default:
            return rotateBoxForDir(box);
      }
   }

   public final BlockType getBlockType() {
      return this.type;
   }

   public final Dir getDir() {
      return this.dir;
   }

   public final Material getMaterial() {
      return this.material;
   }

   public final float getParam() {
      return this.param;
   }

   public final void setParam(float param) {
      this.param = param;
   }

   public static void loadModels() {
      wallMesh = ModelLoader.loadMesh("seafloorBase", "wall");
      wallAcuteAngleMesh = ModelLoader.loadMesh("seafloorBase", "wallAcuteAngle");
      wallObtuseAngleMesh = ModelLoader.loadMesh("seafloorBase", "wallObtuseAngle");
      wallSeparationMesh = ModelLoader.loadMesh("seafloorBase", "wallSeparation");
      roofMesh = ModelLoader.loadMesh("seafloorBase", "roof");
      topMesh = ModelLoader.loadMesh("seafloorBase", "top");
      ArrayList<Vertex> vertices = new ArrayList<>();
      vertices.add(new Vertex(new Point(-1.0F, 0.2F, -1.0F), new Coord(0, 0)));
      vertices.add(new Vertex(new Point(-1.0F, 0.2F, 1.0F), new Coord(0, 1)));
      vertices.add(new Vertex(new Point(1.0F, 0.2F, 1.0F), new Coord(1, 1)));
      vertices.add(new Vertex(new Point(1.0F, 0.2F, -1.0F), new Coord(1, 0)));
      vertices.add(new Vertex(new Point(-1.0F, 0.0F, -1.0F), new Coord(0, 0)));
      vertices.add(new Vertex(new Point(1.0F, 0.0F, -1.0F), new Coord(0, 1)));
      vertices.add(new Vertex(new Point(1.0F, 0.0F, 1.0F), new Coord(1, 1)));
      vertices.add(new Vertex(new Point(-1.0F, 0.0F, 1.0F), new Coord(1, 0)));
      floorMesh = new QuadVbo(vertices);
      vertices = new ArrayList<>();

      for (int row = 0; row < 4; row++) {
         for (int col = 0; col < 4; col++) {
            vertices.add(new Vertex(new Point(-1.0F + row / 4.0F * 2.0F, 0.0F, -1.0F + col / 4.0F * 2.0F), new Coord(row / 4.0F, col / 4.0F)));
            vertices.add(new Vertex(new Point(-1.0F + row / 4.0F * 2.0F, 0.0F, -1.0F + (col + 1) / 4.0F * 2.0F), new Coord(row / 4.0F, (col + 1) / 4.0F)));
            vertices.add(
               new Vertex(
                  new Point(-1.0F + (row + 1) / 4.0F * 2.0F, 0.0F, -1.0F + (col + 1) / 4.0F * 2.0F), new Coord((row + 1) / 4.0F, (col + 1) / 4.0F)
               )
            );
            vertices.add(new Vertex(new Point(-1.0F + (row + 1) / 4.0F * 2.0F, 0.0F, -1.0F + col / 4.0F * 2.0F), new Coord((row + 1) / 4.0F, col / 4.0F)));
         }
      }

      waterLevelMesh = new QuadVbo(vertices);
      glassWallMesh = ModelLoader.loadMesh("seafloorBase", "glassWall");
      glassMesh = ModelLoader.loadMesh("seafloorBase", "glass");
      hatchMesh = ModelLoader.loadMesh("seafloorBase", "hatch");
      pillarMesh = ModelLoader.loadMesh("seafloorBase", "pillar");
      ladderMesh = ModelLoader.loadMesh("seafloorBase", "ladder");
      outsidePillarMesh = ModelLoader.loadMesh("seafloorBase", "outsidePillar");
      workshopTableMesh = ModelLoader.loadMesh("seafloorBase", "workshopTable");
      weaponTableMesh = ModelLoader.loadMesh("seafloorBase", "weaponTable");
      equipmentTableMesh = ModelLoader.loadMesh("seafloorBase", "equipmentTable");
      buildingTableMesh = ModelLoader.loadMesh("seafloorBase", "buildingTable");
      furnitureTableMesh = ModelLoader.loadMesh("seafloorBase", "furnitureTable");
      plantPotMesh = ModelLoader.loadMesh("seafloorBase", "plantPot");
      ArrayList<Vertex> plantVertices = new ArrayList<>();

      for (int side = 0; side < 3; side++) {
         plantVertices.add(
            new Vertex(
               new Point(-0.5F * (float)Math.cos((Math.PI * 2.0 / 3.0) * side), 1.0F, -0.5F * (float)Math.sin((Math.PI * 2.0 / 3.0) * side)), new Coord(0, 0)
            )
         );
         plantVertices.add(
            new Vertex(
               new Point(-0.5F * (float)Math.cos((Math.PI * 2.0 / 3.0) * side), 0.0F, -0.5F * (float)Math.sin((Math.PI * 2.0 / 3.0) * side)),
               new Coord(0.0F, 0.95F)
            )
         );
         plantVertices.add(
            new Vertex(
               new Point(0.5F * (float)Math.cos((Math.PI * 2.0 / 3.0) * side), 0.0F, 0.5F * (float)Math.sin((Math.PI * 2.0 / 3.0) * side)),
               new Coord(1.0F, 0.95F)
            )
         );
         plantVertices.add(
            new Vertex(
               new Point(0.5F * (float)Math.cos((Math.PI * 2.0 / 3.0) * side), 1.0F, 0.5F * (float)Math.sin((Math.PI * 2.0 / 3.0) * side)), new Coord(1, 0)
            )
         );
      }

      plantMesh = new QuadVbo(plantVertices);
      chestSmallMesh = ModelLoader.loadMesh("seafloorBase", "chestSmall");
      chestSmallTopMesh = ModelLoader.loadMesh("seafloorBase", "chestSmallTop");
      chestSmallTopTexture = ModelLoader.loadTexture("seafloorBase", "chestSmallTop");
      tableMesh = ModelLoader.loadMesh("seafloorBase", "table");
      stoolMesh = ModelLoader.loadMesh("seafloorBase", "stool");
      doorMesh = ModelLoader.loadMesh("seafloorBase", "door");
      doorSliceMesh = ModelLoader.loadMesh("seafloorBase", "doorSlice");
      doorSliceTexture = ModelLoader.loadTexture("seafloorBase", "doorSlice");
      cookerMesh = ModelLoader.loadMesh("seafloorBase", "cooker");
   }

   public final void enableWaterLeak() {
      if (this.type == BlockType.CEILING || this.type == BlockType.WALL) {
         this.waterLeak = true;
      }
   }

   public final void disableWaterLeak() {
      this.waterLeak = false;
   }

   public final boolean hasWaterLeak() {
      return this.waterLeak;
   }

   public final void setDrawWaterLevel0(boolean draw) {
      this.drawWaterLevel0 = draw;
   }

   public final void setDrawWaterLevel1(boolean draw) {
      this.drawWaterLevel1 = draw;
   }

   public final boolean shouldShowWater() {
      return this.param > 0.0F || this.drawWaterLevel0;
   }

   public final void reset() {
      if (this.type == BlockType.PLANT_POT) {
         this.param = 0.0F;
      }

      if (this.type == BlockType.COOKER) {
         ((CookerInventory)this.inventory).resetCooker();
      }
   }

   @Override
   public String toString() {
      return "Element: type=" + this.type + ",\tdir=" + this.dir;
   }

   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      if (this.type == BlockType.PLANT_POT && this.inventory == null) {
         this.inventory = new Inventory("", 1, 1);
         if (this.param > 0.0F) {
            this.inventory.addItem(new Item(ItemType.POTATO));
         }
      }
   }
}
