package game.seafloorBase;

import game.Main;
import game.chunks.ChunkManager;
import game.collision.AABB;
import game.collision.CollisionDetector;
import game.gui.InteractionHint;
import game.gui.PlayerHud;
import game.input.InputManager;
import game.input.RawInput;
import game.inventory.InventoryElmtType;
import game.inventory.InventoryHud;
import game.inventory.Item;
import game.inventory.ItemType;
import game.manager.GameScene;
import game.manager.GameState;
import game.seafloorBase.util.BlockType;
import game.seafloorBase.util.Dir;
import game.seafloorBase.util.ElementBlockPair;
import game.seafloorBase.util.Material;
import game.seafloorBase.util.Neighbor;
import game.seafloorBase.util.PickedBlock;
import game.util.Coord;
import game.util.Point;
import game.util.State;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Octree implements Serializable {
   public static enum BaseType {
      STARTER,
      EXTRA,
      EMPTY;
   }

   private static final long serialVersionUID = -2872222139796153214L;
   private Block[][][] blocks;
   private boolean avatarInside = false;
   private Point pos;
   private BaseType baseType;
   private transient ArrayList<Block>[] textureRenderBuckets;
   private transient ArrayList<Point> animatedBlockPositions;
   private transient ArrayList<Point> leakBlockPositions;
   private transient ArrayList<Point> surfaceBlockPositions;
   private transient ArrayList<Element> interactiveElements;
   private transient PickedBlock pickedBlock;
   private transient Dir placementDir;
   private transient AABB outerBounds;
   private transient AABB innerBounds;
   private transient Element openingChest;
   private transient ElementBlockPair highlightedElement;
   private transient boolean chestOpening = false;
   private transient CraftingAnimation craftingAnim = new CraftingAnimation();
   private transient Block lastContactBlock = null;
   private transient RoomFiller roomFiller;

   @SuppressWarnings("unchecked")
   public Octree(Point pos) {
      this.pos = pos.copy();
      this.animatedBlockPositions = new ArrayList<>();
      this.leakBlockPositions = new ArrayList<>();
      this.surfaceBlockPositions = new ArrayList<>();
      this.textureRenderBuckets = (ArrayList<Block>[]) new ArrayList[BaseModels.textureCount];
      this.blocks = new Block[32][8][32];

      for (int x = 0; x < 32; x++) {
         for (int y = 0; y < 8; y++) {
            for (int z = 0; z < 32; z++) {
               this.blocks[x][y][z] = new Block(new Point((float)(x - 16), (float)y, (float)(z - 16)));
            }
         }
      }

      this.roomFiller = new RoomFiller(pos);
      this.baseType = BaseType.EMPTY;
   }

   public final void initialize(BaseType baseType) {
      this.baseType = baseType;
      switch (baseType) {
         case EXTRA:
            this.blocks = PickedBlock.generateRandomBase(this.blocks, 4);
            this.updateNeighborState();

            for (int x = 0; x < 32; x++) {
               for (int y = 0; y < 8; y++) {
                  for (int z = 0; z < 32; z++) {
                     if (this.blocks[x][y][z] != null && Math.random() < 0.3F) {
                        this.blocks[x][y][z].applyMaterial(Material.GLASS, false);
                     }
                  }
               }
            }

            this.updateNeighborState();
            int leakCount = 0;

            for (int level = 7; level >= 0; level--) {
               for (int x2 = 0; x2 < 32; x2++) {
                  for (int z2 = 0; z2 < 32; z2++) {
                     if (this.blocks[x2][level][z2] != null) {
                        if (this.blocks[x2][level][z2].hasElement(BlockType.WALL) && leakCount < 10 && (leakCount == 0 || Math.random() < 0.01F + 0.1F * (10 - leakCount) / 10.0F)
                           )
                         {
                           this.lastContactBlock = this.blocks[x2][level][z2];
                           this.triggerWaterLeak();
                           leakCount++;
                        }

                        if (Math.random() < 0.3F) {
                           this.blocks[x2][level][z2].applyMaterial(Material.GLASS, false);
                        }
                     }
                  }
               }
            }

            this.updateNeighborState();
            this.rebuildDisplayLists();
            if (this.roomFiller != null) {
               this.roomFiller.update(999.0F, this.blocks);
            }
            break;
         case STARTER:
            for (int x = -2; x <= 2; x++) {
               for (int z = -2; z <= 2; z++) {
                  this.blocks[x + 16][1][z + 16].build();
                  this.blocks[x + 16][2][z + 16].build();
                  this.blocks[x + 16][2][z + 16].clearElements();
               }
            }

            for (int x = 3; x <= 5; x++) {
               for (int z = -2; z <= 2; z++) {
                  this.blocks[x + 16][2][z + 16].build();
               }
            }

            for (int x = -1; x <= 0; x++) {
               for (int z = -2; z < 0; z++) {
                  this.blocks[x + 16][1][z + 16].clearElements();
               }
            }

            this.blocks[19][1][16].build();
            this.blocks[19][1][17].build();
            this.blocks[19][1][18].build();
            this.blocks[21][2][14].demolish();
            this.blocks[21][2][15].demolish();
            this.blocks[21][2][16].demolish();
            this.blocks[14][1][13].build();
            this.blocks[15][1][13].build();
            this.blocks[16][1][13].build();
            this.blocks[17][1][13].build();
            this.blocks[14][2][13].build();
            this.blocks[15][2][13].build();
            this.blocks[16][2][13].build();
            this.blocks[17][2][13].build();
            this.blocks[14][2][13].clearElements();
            this.blocks[15][2][13].clearElements();
            this.blocks[16][2][13].clearElements();
            this.blocks[17][2][13].clearElements();
            this.blocks[16][1][16].addElement(BlockType.LADDER, Dir.NORTH, this.getNeighbors(new Point(16.0F, 1.0F, 16.0F)), false);
            this.blocks[19][2][14].addElement(BlockType.LADDER, Dir.WEST, this.getNeighbors(new Point(19.0F, 2.0F, 14.0F)), false);
            this.blocks[21][2][17].addElement(BlockType.PLANT_POT, Dir.EAST, this.getNeighbors(new Point(21.0F, 2.0F, 17.0F)), false);
            seedPlantPot(ItemType.POTATO, this.blocks[21][2][17].getElement(BlockType.PLANT_POT));
            this.blocks[21][2][17].getElement(BlockType.PLANT_POT).setParam(1.0F);
            this.blocks[21][2][18].addElement(BlockType.PLANT_POT, Dir.EAST, this.getNeighbors(new Point(21.0F, 2.0F, 18.0F)), false);
            seedPlantPot(ItemType.POTATO, this.blocks[21][2][18].getElement(BlockType.PLANT_POT));
            this.blocks[21][2][18].getElement(BlockType.PLANT_POT).setParam(1.0F);
            this.blocks[19][1][16].addElement(BlockType.WORKSHOP, Dir.EAST, this.getNeighbors(new Point(18.0F, 1.0F, 16.0F)), false);
            this.blocks[19][1][18].addElement(BlockType.CHEST, Dir.EAST, this.getNeighbors(new Point(18.0F, 1.0F, 18.0F)), false);
            this.blocks[19][1][18].getElement(BlockType.CHEST).inventory.addItem(new Item(ItemType.DRILL));
            this.blocks[19][1][18].getElement(BlockType.CHEST).inventory.addItem(new Item(ItemType.EXTRACTOR));
            this.blocks[19][1][18].getElement(BlockType.CHEST).inventory.addItem(new Item(ItemType.FISH));
            this.blocks[19][1][18].getElement(BlockType.CHEST).inventory.addItem(new Item(ItemType.COAL, 5));
            this.blocks[19][1][18].getElement(BlockType.CHEST).inventory.addItem(new Item(ItemType.IRON, 25));
            this.blocks[19][1][17].addElement(BlockType.COOKER, Dir.EAST, this.getNeighbors(new Point(18.0F, 1.0F, 17.0F)), false);
            this.updateNeighborState();

            for (int x = 0; x < 32; x++) {
               for (int z = 0; z < 32; z++) {
                  if (Math.random() < 0.5) {
                     this.blocks[x][1][z].applyMaterial(Material.GLASS, false);
                  }

                  if (Math.random() < 0.5) {
                     this.blocks[x][2][z].applyMaterial(Material.GLASS, false);
                  }
               }
            }

            this.updateNeighborState();
            break;
         case EMPTY:
            for (int x = -2; x <= 2; x++) {
               for (int z = -2; z < 2; z++) {
                  this.blocks[x + 16][1][z + 16].build();
               }
            }

            for (int x = -1; x <= 0; x++) {
               for (int z = -1; z <= 0; z++) {
                  this.blocks[x + 16][1][z + 16].clearElements();
               }
            }

            this.updateNeighborState();

            for (int x = -3; x < 3; x++) {
               for (int z = -3; z < 3; z++) {
                  if (Math.random() < 0.5) {
                     this.blocks[x + 16][1][z + 16].applyMaterial(Material.GLASS, false);
                  }
               }
            }

            this.blocks[16][1][17].addElement(BlockType.LADDER, Dir.NORTH, this.getNeighbors(new Point(16.0F, 1.0F, 17.0F)), false);
            this.blocks[18][1][16].addElement(BlockType.WORKSHOP, this.placementDir, this.getNeighbors(new Point(18.0F, 1.0F, 16.0F)), false);
            this.updateNeighborState();
      }

      this.rebuildDisplayLists();
   }

   public final void update(float delta) {
      for (int i = 0; i < this.interactiveElements.size(); i++) {
         this.interactiveElements.get(i).update(delta);
      }

      if (RawInput.leftMouseDown && this.highlightedElement != null && this.highlightedElement.element != null && GameScene.avatar.getSelectedItem() != null && GameScene.avatar.getSelectedItem().getType() == ItemType.HAMMER) {
         if (this.highlightedElement.element.inventory != null && !this.highlightedElement.element.inventory.isEmpty()) {
            InteractionHint.addTimedHint("Must be empty", 1.0F);
         } else if (GameScene.avatar.pickupItem(new Item(this.highlightedElement.element.getBlockType().getItemType()))) {
            this.highlightedElement.block.removeElement(this.highlightedElement.element);
         }
      }

      if (Main.isDebug && RawInput.rightMouseDown && this.pickedBlock != null) {
         this.lastContactBlock = this.blocks[this.pickedBlock.x][this.pickedBlock.y][this.pickedBlock.z];
         this.triggerWaterLeak();
      }

      if (RawInput.leftMouseDown && this.pickedBlock != null && GameScene.avatar.getSelectedItem() != null) {
         switch (GameScene.avatar.getSelectedItem().getType()) {
            case FLOOR:
               this.getBlockAt(this.pickedBlock.getPosition()).build();
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               if (GameScene.stats != null) {
                  GameScene.stats.recordBaseBlockBuilt();
               }
               break;
            case NEW_LEVEL:
               if (this.pickedBlock.pickType == PickedBlock.PickType.FLOOR) {
                  Point below = new Point(0.0F, -1.0F, 0.0F);
                  Point lookDir = this.placementDir.offset();
                  Point lookOpp = this.placementDir.opposite().offset();
                  this.getBlockAt(this.pickedBlock.getPosition().plus(below)).build();
                  this.getBlockAt(this.pickedBlock.getPosition()).clearElements();
                  this.getBlockAt(this.pickedBlock.getPosition().plus(below).plus(lookDir)).build();
                  this.getBlockAt(this.pickedBlock.getPosition().plus(lookOpp)).addElement(BlockType.LADDER, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition().plus(below).plus(lookDir)), false);
               } else {
                  Point above = new Point(0.0F, 1.0F, 0.0F);
                  Point toSide = this.placementDir.offset();
                  this.getBlockAt(this.pickedBlock.getPosition().plus(above)).build();
                  this.getBlockAt(this.pickedBlock.getPosition().plus(above)).clearElements();
                  this.getBlockAt(this.pickedBlock.getPosition().plus(above).plus(toSide)).build();
                  this.getBlockAt(this.pickedBlock.getPosition().plus(above).plus(toSide)).addElement(BlockType.LADDER, this.placementDir.opposite(), this.getNeighbors(this.pickedBlock.getPosition().plus(above).plus(toSide)), false);
               }

               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case HAMMER:
               if (this.craftingAnim.actionType == CraftingAnimation.ActionType.DEMOLISH) {
                  if (this.pickedBlock.pickType == PickedBlock.PickType.FLOOR) {
                     if (!this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).hasInteractiveElement()
                        && this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).hasElement(BlockType.FLOOR)
                        && GameScene.avatar.pickupItem(new Item(ItemType.FLOOR))) {
                        this.getBlockAt(this.pickedBlock.getPosition()).clearElements();
                        this.craftingAnim.triggerEffect();
                     }
                  } else if (this.placementDir != Dir.WEST
                        && (!this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).hasElement(BlockType.WALL) || this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).getElement(BlockType.WALL).getDir() != Dir.EAST)
                     || this.blockAt(this.pickedBlock.x + 1, this.pickedBlock.y, this.pickedBlock.z).hasInteractiveElement()
                     || (!this.blockAt(this.pickedBlock.x + 1, this.pickedBlock.y, this.pickedBlock.z).hasElement(BlockType.FLOOR) || !GameScene.avatar.pickupItem(new Item(ItemType.FLOOR)))
                        && this.blockAt(this.pickedBlock.x + 1, this.pickedBlock.y, this.pickedBlock.z).hasElement(BlockType.FLOOR)) {
                     if (this.placementDir != Dir.EAST
                           && (!this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).hasElement(BlockType.WALL) || this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).getElement(BlockType.WALL).getDir() != Dir.WEST)
                        || this.blockAt(this.pickedBlock.x - 1, this.pickedBlock.y, this.pickedBlock.z).hasInteractiveElement()
                        || (!this.blockAt(this.pickedBlock.x - 1, this.pickedBlock.y, this.pickedBlock.z).hasElement(BlockType.FLOOR) || !GameScene.avatar.pickupItem(new Item(ItemType.FLOOR)))
                           && this.blockAt(this.pickedBlock.x - 1, this.pickedBlock.y, this.pickedBlock.z).hasElement(BlockType.FLOOR)) {
                        if (this.placementDir != Dir.NORTH
                              && (!this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).hasElement(BlockType.WALL) || this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).getElement(BlockType.WALL).getDir() != Dir.SOUTH)
                           || this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z + 1).hasInteractiveElement()
                           || (!this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z + 1).hasElement(BlockType.FLOOR) || !GameScene.avatar.pickupItem(new Item(ItemType.FLOOR)))
                              && this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z + 1).hasElement(BlockType.FLOOR)) {
                           if ((
                                 this.placementDir == Dir.SOUTH
                                    || this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).hasElement(BlockType.WALL) && this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z).getElement(BlockType.WALL).getDir() == Dir.NORTH
                              )
                              && !this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z - 1).hasInteractiveElement()
                              && (
                                 this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z - 1).hasElement(BlockType.FLOOR) && GameScene.avatar.pickupItem(new Item(ItemType.FLOOR))
                                    || !this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z - 1).hasElement(BlockType.FLOOR)
                              )) {
                              this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z - 1).demolish();
                           }
                        } else {
                           this.blockAt(this.pickedBlock.x, this.pickedBlock.y, this.pickedBlock.z + 1).demolish();
                        }
                     } else {
                        this.blockAt(this.pickedBlock.x - 1, this.pickedBlock.y, this.pickedBlock.z).demolish();
                     }
                  } else {
                     this.blockAt(this.pickedBlock.x + 1, this.pickedBlock.y, this.pickedBlock.z).demolish();
                  }
               }

               if (this.craftingAnim.actionType == CraftingAnimation.ActionType.REPAIR) {
                  if (GameScene.avatar.hasResources(new Item(ItemType.IRON, 2))) {
                     PlayerHud.addPickupNotification(ItemType.IRON, -2);
                     this.blocks[this.pickedBlock.x][this.pickedBlock.y][this.pickedBlock.z].disableWaterLeak();
                     this.craftingAnim.triggerEffect();
                  } else {
                     InteractionHint.addTimedHint("2 irons required!", 1.0F);
                  }
               }
               break;
            case GLASS_WALL:
               this.getBlockAt(this.pickedBlock.getPosition()).applyMaterial(Material.GLASS, false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case HOLE:
               this.getBlockAt(this.pickedBlock.getPosition()).clearElements();
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case MAIN_WORKSHOP:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.WORKSHOP, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case EQUIPMENT_WORKSHOP:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.EQUIPMENT_WORKSHOP, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case WEAPON_WORKSHOP:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.WEAPON_WORKSHOP, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case FURNITURE_WORKSHOP:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.FURNITURE_WORKSHOP, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case BUILDING_WORKSHOP:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.BUILDING_WORKSHOP, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case LADDER:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.LADDER, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case PLANT_POT:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.PLANT_POT, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case CHEST:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.CHEST, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case LARGE_CHEST:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.LARGE_CHEST, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case TABLE:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.TABLE, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case STOOL:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.STOOL, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case DOOR:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.DOOR, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
               break;
            case COOKER:
               this.getBlockAt(this.pickedBlock.getPosition()).addElement(BlockType.COOKER, this.placementDir, this.getNeighbors(this.pickedBlock.getPosition()), false);
               GameScene.avatar.consumeSelectedItem();
               this.craftingAnim.triggerEffect();
			default:
				break;
         }

         this.updateNeighborState();
         this.rebuildDisplayLists();
      }

      this.craftingAnim.update(delta);
      if (this.roomFiller != null) {
         this.roomFiller.update(delta, this.blocks);
      }
   }

   public final void renderOpaque(float lightMult) {
      for (int tex = 0; tex < BaseModels.textureCount; tex++) {
         if (!BaseModels.isAlphaTexture(tex)) {
            BaseModels.bindTexture(tex, this.avatarInside, lightMult);

            for (int i = 0; i < this.textureRenderBuckets[tex].size(); i++) {
               this.textureRenderBuckets[tex].get(i).renderElementsWithTexture(BaseModels.getTextureId(tex), BaseModels.isAlphaTexture(tex), this.avatarInside);
            }
         }
      }

      BaseModels.resetShaderState();

      for (int j = 0; j < this.animatedBlockPositions.size(); j++) {
         this.getBlockAt(this.animatedBlockPositions.get(j)).renderAnimated();
      }

      this.craftingAnim.render();
   }

   public final void renderAlpha(float lightMult) {
      for (int tex = 0; tex < BaseModels.textureCount; tex++) {
         if (BaseModels.isAlphaTexture(tex)) {
            BaseModels.bindTexture(tex, this.avatarInside, lightMult);

            for (int i = 0; i < this.textureRenderBuckets[tex].size(); i++) {
               this.textureRenderBuckets[tex].get(i).renderElementsWithTexture(BaseModels.getTextureId(tex), BaseModels.isAlphaTexture(tex), this.avatarInside);
            }
         }
      }

      BaseModels.resetShaderState();

      for (int j = 0; j < this.leakBlockPositions.size(); j++) {
         this.getBlockAt(this.leakBlockPositions.get(j)).renderLeakEffects();
      }
   }

   public final void rebuildDisplayLists() {
      for (int tex = 0; tex < BaseModels.textureCount; tex++) {
         this.textureRenderBuckets[tex] = new ArrayList<>();

         for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 8; y++) {
               for (int z = 0; z < 32; z++) {
                  if (this.blocks[x][y][z].hasElementWithTexture(BaseModels.getTextureId(tex), BaseModels.isAlphaTexture(tex))) {
                     this.textureRenderBuckets[tex].add(this.blocks[x][y][z]);
                  }
               }
            }
         }
      }

      this.animatedBlockPositions.clear();
      this.leakBlockPositions.clear();
      if (this.interactiveElements == null) {
         this.interactiveElements = new ArrayList<>();
      }

      this.interactiveElements.clear();

      for (int x = 0; x < 32; x++) {
         for (int y = 0; y < 8; y++) {
            for (int z = 0; z < 32; z++) {
               if (this.blocks[x][y][z].hasAnimatedElement()) {
                  this.animatedBlockPositions.add(new Point((float)x, (float)y, (float)z));
               }

               this.interactiveElements.addAll(this.blocks[x][y][z].getElementsAt(this.pos.plus(blockToWorldCoords(new Point((float)x, (float)y, (float)z)))));
               if (this.blocks[x][y][z].hasWaterLeak()) {
                  this.leakBlockPositions.add(new Point((float)x, (float)y, (float)z));
               }
            }
         }
      }

      this.surfaceBlockPositions.clear();

      for (int x = 0; x < 32; x++) {
         for (int z = 0; z < 32; z++) {
            for (int y = 7; y >= 0; y--) {
               if (this.blocks[x][y][z].isBuilt()) {
                  this.surfaceBlockPositions.add(new Point((float)x, (float)y, (float)z));
                  break;
               }
            }
         }
      }

      Point boundsMin = new Point(16.0F, 1.0F, 16.0F);
      Point boundsMax = new Point(16.0F, 1.0F, 16.0F);

      for (int x = 0; x < 32; x++) {
         for (int y = 0; y < 8; y++) {
            for (int z = 0; z < 32; z++) {
               if (this.blocks[x][y][z].isRoom()) {
                  if (boundsMin.x > x) {
                     boundsMin.x = x;
                  }

                  if (boundsMin.y > y) {
                     boundsMin.y = y;
                  }

                  if (boundsMin.z > z) {
                     boundsMin.z = z;
                  }

                  if (boundsMax.x < x) {
                     boundsMax.x = x;
                  }

                  if (boundsMax.y < y) {
                     boundsMax.y = y;
                  }

                  if (boundsMax.z < z) {
                     boundsMax.z = z;
                  }
               }
            }
         }
      }

      boundsMin.add(-2.0F, 0.0F, -2.0F);
      boundsMax.add(2.0F, 1.0F, 2.0F);
      this.outerBounds = new AABB(new Point(), blockToWorldCoords(boundsMin), blockToWorldCoords(boundsMax));
      boundsMin.add(-1.0F, -0.4F, -1.0F);
      boundsMax.add(1.0F, 0.4F, 1.0F);
      this.innerBounds = new AABB(new Point(), blockToWorldCoords(boundsMin), blockToWorldCoords(boundsMax));
      this.rebuildRoomFiller();
   }

   public final AABB getBounds() {
      return this.outerBounds;
   }

   public final AABB getCollisionBox() {
      return this.innerBounds;
   }

   public final void updatePickedBlock(Point camPos, Point lookDir) {
      this.pickedBlock = null;
      camPos = worldToBlockCoords(camPos);
      lookDir.x /= 10.0F;
      lookDir.y /= 35.0F;
      lookDir.z /= 10.0F;
      lookDir.normalize();
      this.placementDir = Dir.EAST;
      float angle = new Coord(lookDir.x, lookDir.z).angle();
      if (angle >= Math.PI / 4 && angle < Math.PI * 3.0 / 4.0) {
         this.placementDir = Dir.SOUTH;
      } else if (angle >= Math.PI * 3.0 / 4.0 && angle < Math.PI * 5.0 / 4.0) {
         this.placementDir = Dir.WEST;
      } else if (angle >= Math.PI * 5.0 / 4.0 && angle < Math.PI * 7.0 / 4.0) {
         this.placementDir = Dir.NORTH;
      }

      this.pickedBlock = null;
      this.craftingAnim.setPickedBlock(null);

      for (float dist = 0.0F; dist < 3.0F; dist += 0.1F) {
         Point samplePos = camPos.plus(lookDir.scaled(dist));
         if (samplePos.x >= 0.0F && samplePos.x < 32.0F && samplePos.y >= 0.0F && samplePos.y < 8.0F && samplePos.z >= 0.0F && samplePos.z < 32.0F) {
            if (this.getBlockAt(samplePos).isRoom()) {
               if (samplePos.y % 1.0F < 0.1F) {
                  this.pickedBlock = this.tryPickBlock(samplePos, PickedBlock.PickType.FLOOR);
                  if (this.pickedBlock != null) {
                     break;
                  }
               }

               if (samplePos.y % 1.0F > 0.9F) {
                  this.pickedBlock = this.tryPickBlock(samplePos, PickedBlock.PickType.CEILING);
                  if (this.pickedBlock != null) {
                     break;
                  }
               }
            } else {
               Block[] neighbors = this.getNeighbors(samplePos);
               if (neighbors[Neighbor.WEST.getIndex()].isRoom()
                  ? true
                  : (
                     neighbors[Neighbor.EAST.getIndex()].isRoom()
                        ? true
                        : (
                           neighbors[Neighbor.NORTH.getIndex()].isRoom()
                              ? true
                              : (
                                 neighbors[Neighbor.SOUTH.getIndex()].isRoom()
                                    ? true
                                    : (
                                       neighbors[Neighbor.NORTHEAST.getIndex()].isRoom()
                                          ? true
                                          : (neighbors[Neighbor.NORTHWEST.getIndex()].isRoom() ? true : (neighbors[Neighbor.SOUTHEAST.getIndex()].isRoom() ? true : neighbors[Neighbor.SOUTHWEST.getIndex()].isRoom()))
                                    )
                              )
                        )
                  )) {
                  this.pickedBlock = this.tryPickBlock(samplePos, PickedBlock.PickType.WALL);
                  if (this.pickedBlock != null) {
                     break;
                  }
               }
            }

            if (this.highlightedElement != null) {
               this.highlightedElement.element.highlighted = false;
            }

            this.highlightedElement = null;
            ArrayList<Element> elements = this.getBlockAt(samplePos).getElements();
            if (this.getBlockAt(samplePos).hasWall()) {
               this.highlightedElement = null;
               break;
            }

            for (int i = 0; i < elements.size(); i++) {
               if (elements.get(i).getBlockType().isInteractive()) {
                  float elemTopHeight = elements.get(i).getBoundingBox().max.y;
                  if (elements.get(i).getBlockType() == BlockType.LADDER) {
                     elemTopHeight = 35F / 3F;
                  }

                  elemTopHeight = elemTopHeight / Element.scale / 10.0F;
                  if (samplePos.y % 1.0F < elemTopHeight) {
                     this.highlightedElement = new ElementBlockPair();
                     this.highlightedElement.element = elements.get(i);
                     this.highlightedElement.block = this.getBlockAt(samplePos);
                  }
                  break;
               }
            }

            if (this.highlightedElement != null) {
               this.highlightedElement.element.highlighted = true;
               break;
            }
         }
      }

      if (GameScene.avatar.getSelectedItem() == null || GameScene.avatar.getSelectedItem().getType().getInventoryType() != InventoryElmtType.SLOT_BUILDING) {
         this.craftingAnim.setPickedBlock(null);
         this.pickedBlock = null;
      }
   }

   private PickedBlock tryPickBlock(Point blockPos, PickedBlock.PickType pickType) {
      PickedBlock candidate = new PickedBlock((int)blockPos.x, (int)blockPos.y, (int)blockPos.z, pickType);
      this.craftingAnim.actionType = CraftingAnimation.ActionType.PLACE;
      if (GameScene.avatar.getSelectedItem() != null) {
         int bx = (int)blockPos.x;
         int by = (int)blockPos.y;
         int bz = (int)blockPos.z;
         switch (GameScene.avatar.getSelectedItem().getType()) {
            case FLOOR:
               if (this.blockAt(bx, by, bz).hasElement(BlockType.FLOOR) && this.blockAt(bx, by, bz).isRoom()) {
                  candidate = null;
               }

               if (bx <= 0 || bx >= 31 || by <= 0 || by >= 7 || bz <= 0 || bz >= 31) {
                  candidate = null;
               }
               break;
            case NEW_LEVEL:
               if (candidate.pickType == PickedBlock.PickType.WALL || candidate.pickType == PickedBlock.PickType.FLOOR && by <= 1 || candidate.pickType == PickedBlock.PickType.CEILING && by >= 6) {
                  candidate = null;
               }
               break;
            case HAMMER:
               if (candidate.pickType == PickedBlock.PickType.FLOOR && !this.blockAt(bx, by, bz).hasElement(BlockType.FLOOR)
                  || candidate.pickType == PickedBlock.PickType.CEILING && !this.blockAt(bx, by, bz).hasElement(BlockType.CEILING)) {
                  candidate = null;
               }

               if (this.blockAt(bx, by, bz).hasWaterLeak()) {
                  this.craftingAnim.actionType = CraftingAnimation.ActionType.REPAIR;
               } else {
                  this.craftingAnim.actionType = CraftingAnimation.ActionType.DEMOLISH;
               }
               break;
            case GLASS_WALL:
               if (!this.blockAt(bx, by, bz).applyMaterial(Material.GLASS, true)) {
                  candidate = null;
               }
               break;
            case HOLE:
               if (!this.blockAt(bx, by, bz).isRoom() || !this.blockAt(bx, by, bz).hasElement(BlockType.FLOOR)) {
                  candidate = null;
               }
               break;
            case MAIN_WORKSHOP:
            case EQUIPMENT_WORKSHOP:
            case WEAPON_WORKSHOP:
            case FURNITURE_WORKSHOP:
            case BUILDING_WORKSHOP:
               if (!this.blockAt(bx, by, bz).addElement(BlockType.WORKSHOP, this.placementDir, this.getNeighbors(candidate.getPosition()), true)) {
                  candidate = null;
               }
               break;
            case LADDER:
               if (!this.blockAt(bx, by, bz).addElement(BlockType.LADDER, this.placementDir, this.getNeighbors(candidate.getPosition()), true) || candidate.pickType == PickedBlock.PickType.CEILING) {
                  candidate = null;
               }
               break;
            case PLANT_POT:
               if (!this.blockAt(bx, by, bz).addElement(BlockType.PLANT_POT, this.placementDir, this.getNeighbors(candidate.getPosition()), true)) {
                  candidate = null;
               }
               break;
            case CHEST:
               if (!this.blockAt(bx, by, bz).addElement(BlockType.CHEST, this.placementDir, this.getNeighbors(candidate.getPosition()), true)) {
                  candidate = null;
               }
               break;
            case LARGE_CHEST:
               if (!this.blockAt(bx, by, bz).addElement(BlockType.LARGE_CHEST, this.placementDir, this.getNeighbors(candidate.getPosition()), true)) {
                  candidate = null;
               }
               break;
            case TABLE:
               if (!this.blockAt(bx, by, bz).addElement(BlockType.TABLE, this.placementDir, this.getNeighbors(candidate.getPosition()), true)) {
                  candidate = null;
               }
               break;
            case STOOL:
               if (!this.blockAt(bx, by, bz).addElement(BlockType.STOOL, this.placementDir, this.getNeighbors(candidate.getPosition()), true)) {
                  candidate = null;
               }
               break;
            case DOOR:
               if (!this.blockAt(bx, by, bz).addElement(BlockType.DOOR, this.placementDir, this.getNeighbors(candidate.getPosition()), true)) {
                  candidate = null;
               }
               break;
            case COOKER:
               if (!this.blockAt(bx, by, bz).addElement(BlockType.COOKER, this.placementDir, this.getNeighbors(candidate.getPosition()), true)) {
                  candidate = null;
               }
               break;
            default:
               candidate = null;
         }
      }

      if (candidate != null) {
         candidate.block = this.blocks[candidate.x][candidate.y][candidate.z];
      }

      this.craftingAnim.setPickedBlock(candidate);
      return candidate;
   }

   private static void seedPlantPot(ItemType itemType, Element element) {
      element.inventory.addItem(new Item(itemType));
      element.setParam(0.05F);
   }

   public final void updateInteraction(float delta, Point localPos) {
      if (this.highlightedElement != null) {
         if (this.highlightedElement.element.getBlockType() == BlockType.PLANT_POT && this.highlightedElement.element.inventory != null) {
            if (this.highlightedElement.element.inventory.isEmpty()
               && GameScene.avatar.getSelectedItem() != null
               && (GameScene.avatar.getSelectedItem().getType() == ItemType.POTATO || GameScene.avatar.getSelectedItem().getType() == ItemType.CARROT_SEED || GameScene.avatar.getSelectedItem().getType() == ItemType.GREEN_BEAN)) {
               InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Plant " + GameScene.avatar.getSelectedItem().getType().getText(), null);
               if (GameScene.avatar.isInteractPressed()) {
                  seedPlantPot(GameScene.avatar.getSelectedItem().getType(), this.highlightedElement.element);
                  GameScene.avatar.consumeSelectedItem();
               }
            }

            if (this.highlightedElement.element.getParam() == 1.0F) {
               InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Harvest ", null);
               if (GameScene.avatar.isInteractPressed()) {
                  boolean harvested = false;
                  switch (this.highlightedElement.element.inventory.getStorageArray().get(0, 0).getItem().getType()) {
                     case POTATO:
                        harvested = GameScene.avatar.pickupItem(new Item(ItemType.POTATO, 3));
                        break;
                     case CARROT_SEED:
                        GameScene.avatar.pickupItem(new Item(ItemType.CARROT_SEED, 2));
                        harvested = GameScene.avatar.pickupItem(new Item(ItemType.CARROT, 2));
                        break;
                     case GREEN_BEAN:
                        harvested = GameScene.avatar.pickupItem(new Item(ItemType.GREEN_BEAN, 6));
					default:
						break;
                  }

                  if (harvested) {
                     this.highlightedElement.element.inventory.clearStorage();
                     this.highlightedElement.element.setParam(0.0F);
                  }
               }
            }
         }

         if (this.highlightedElement.element.getBlockType() == BlockType.WORKSHOP
            || this.highlightedElement.element.getBlockType() == BlockType.EQUIPMENT_WORKSHOP
            || this.highlightedElement.element.getBlockType() == BlockType.WEAPON_WORKSHOP
            || this.highlightedElement.element.getBlockType() == BlockType.FURNITURE_WORKSHOP
            || this.highlightedElement.element.getBlockType() == BlockType.BUILDING_WORKSHOP) {
            String workshopName = "Main Workshop";
            if (this.highlightedElement.element.getBlockType() == BlockType.EQUIPMENT_WORKSHOP) {
               workshopName = "Equipment Workshop";
            }

            if (this.highlightedElement.element.getBlockType() == BlockType.WEAPON_WORKSHOP) {
               workshopName = "Weapon Workshop";
            }

            if (this.highlightedElement.element.getBlockType() == BlockType.FURNITURE_WORKSHOP) {
               workshopName = "Furniture Workshop";
            }

            if (this.highlightedElement.element.getBlockType() == BlockType.BUILDING_WORKSHOP) {
               workshopName = "Building Workshop";
            }

            InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Use " + workshopName, this.highlightedElement.element.inventory);
            if (GameScene.avatar.isInteractPressed()) {
               Main.gameState = GameState.INVENTORY;
               InventoryHud.setInventory(this.highlightedElement.element.inventory);
            }
         }

         if (this.highlightedElement.element.getBlockType() == BlockType.CHEST || this.highlightedElement.element.getBlockType() == BlockType.LARGE_CHEST) {
            InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Open Chest", this.highlightedElement.element.inventory);
            if (GameScene.avatar.isInteractPressed()) {
               this.openingChest = this.highlightedElement.element;
               this.chestOpening = true;
            }
         }

         if (this.openingChest != null && (this.openingChest.getBlockType() == BlockType.CHEST || this.openingChest.getBlockType() == BlockType.LARGE_CHEST)) {
            if (this.openingChest.getParam() == 1.0F) {
               Main.gameState = GameState.INVENTORY;
               InventoryHud.setInventory(this.highlightedElement.element.inventory);
               this.chestOpening = false;
            }

            if (this.chestOpening) {
               this.openingChest.setParam(Math.min(this.openingChest.getParam() + delta * 2.0F, 1.0F));
            } else {
               this.openingChest.setParam(Math.max(this.openingChest.getParam() - delta * 2.0F, 0.0F));
            }
         }

         if (this.highlightedElement.element.getBlockType() == BlockType.COOKER) {
            InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Use Cooker", this.highlightedElement.element.inventory);
            if (GameScene.avatar.isInteractPressed()) {
               Main.gameState = GameState.INVENTORY;
               InventoryHud.setInventory(this.highlightedElement.element.inventory);
            }
         }
      }

      boolean insideRoom = true;
      if (!this.getBlockAt(worldToBlockCoords(localPos)).isRoom()) {
         insideRoom = false;
      } else {
         for (int dy = 0; dy >= -1; dy--) {
            AABB waterBox = this.getBlockAt(worldToBlockCoords(localPos).plus(0.0F, dy, 0.0F)).getWaterLevelBox(BlockType.WATER_LEVEL);
            if (waterBox != null
               && this.getBlockAt(worldToBlockCoords(localPos).plus(0.0F, dy, 0.0F)).getElement(BlockType.WATER_LEVEL).shouldShowWater()
               && localPos.x >= waterBox.min.x
               && localPos.x <= waterBox.max.x
               && localPos.z >= waterBox.min.z
               && localPos.z <= waterBox.max.z
               && localPos.y < waterBox.max.y) {
               insideRoom = false;
            }
         }

         GameScene.avatar.setWalkAnimPhase(-1.0F);
         if (this.getBlockAt(worldToBlockCoords(localPos)).hasElement(BlockType.WATER_LEVEL)) {
            GameScene.avatar.setWalkAnimPhase(this.getBlockAt(worldToBlockCoords(localPos)).getElement(BlockType.WATER_LEVEL).getParam() * 35.0F);
         }
      }

      if (this.avatarInside != insideRoom) {
         GameScene.avatar.setInside(insideRoom);
         this.avatarInside = insideRoom;
      } else if (!insideRoom && CollisionDetector.containsPoint(GameScene.avatar.getCameraPos(), this.outerBounds, this.pos, new Point())) {
         GameScene.avatar.setInside(false);
         this.avatarInside = false;
      }

      ArrayList<Block> nearDoors = this.findNearbyBlocksWithElement(localPos, BlockType.DOOR);
      if (nearDoors != null) {
         for (int i = 0; i < nearDoors.size(); i++) {
            Element door = nearDoors.get(i).getElement(BlockType.DOOR);
            door.setParam(Math.min(door.getParam() + delta * 2.0F, 1.0F));
         }
      }
   }

   public final State resolveCollision(State prevState, State state) {
      Point blockPos = worldToBlockCoords(prevState.pos);
      State prevPos = new State();

      for (int x = (int)blockPos.x - 2; x <= (int)blockPos.x + 2; x++) {
         for (int y = (int)blockPos.y - 2; y <= (int)blockPos.y + 2; y++) {
            for (int z = (int)blockPos.z - 2; z <= (int)blockPos.z + 2; z++) {
               if (x >= 0 && x < 32 && y >= 0 && y < 8 && z >= 0 && z < 32) {
                  state = this.blocks[x][y][z].resolveCollision(prevState, state, true);
               }
            }
         }
      }

      for (int x = (int)blockPos.x - 2; x <= (int)blockPos.x + 2; x++) {
         for (int y = 7; y >= 0; y--) {
            for (int z = (int)blockPos.z - 2; z <= (int)blockPos.z + 2; z++) {
               if (x >= 0 && x < 32 && y >= 0 && y < 8 && z >= 0 && z < 32) {
                  prevPos.pos = state.pos;
                  state = this.blocks[x][y][z].resolveCollision(prevState, state, false);
                  if (prevPos.pos != state.pos) {
                     this.lastContactBlock = this.blocks[x][y][z];
                     if (this.lastContactBlock.hasElement(BlockType.ROOF) && y > 0) {
                        this.lastContactBlock = this.blocks[x][y - 1][z];
                     }
                  }
               }
            }
         }
      }

      return state;
   }

   private ArrayList<Block> findNearbyBlocksWithElement(Point pos, BlockType blockType) {
      pos = worldToBlockCoords(pos);
      ArrayList<Block> result = new ArrayList<>();

      for (int x = (int)pos.x - 1; x <= (int)pos.x + 1; x++) {
         for (int z = (int)pos.z - 1; z <= (int)pos.z + 1; z++) {
            if (x >= 0 && x < 32 && pos.y >= 0.0F && pos.y < 8.0F && z >= 0 && z < 32 && this.blocks[x][(int)pos.y][z].hasElement(blockType)) {
               result.add(this.blocks[x][(int)pos.y][z]);
            }
         }
      }

      return result.size() > 0 ? result : null;
   }

   private static Point worldToBlockCoords(Point worldPos) {
      Point blockPos = new Point(worldPos.x / 10.0F, worldPos.y / 35.0F, worldPos.z / 10.0F);
      blockPos.add(16.5F, 0.0F, 16.5F);
      return blockPos;
   }

   private static Point blockToWorldCoords(Point blockPos) {
      Point worldPos = blockPos.minus(16.5F, 0.0F, 16.5F);
      worldPos.x *= 10.0F;
      worldPos.y *= 35.0F;
      worldPos.z *= 10.0F;
      return worldPos;
   }

   private void updateNeighborState() {
      for (int x = 0; x < 32; x++) {
         for (int y = 0; y < 8; y++) {
            for (int z = 0; z < 32; z++) {
               Block block = this.blocks[x][y][z];
               this.getNeighbors(new Point((float)x, (float)y, (float)z));
               block.prepareUpdate();
            }
         }
      }

      for (int x = 0; x < 32; x++) {
         for (int y = 0; y < 8; y++) {
            for (int z = 0; z < 32; z++) {
               this.blocks[x][y][z].updateFromNeighbors(this.getNeighbors(new Point((float)x, (float)y, (float)z)));
            }
         }
      }

      int seabedCount = 0;

      for (int x = 0; x < 32; x += Math.min(seabedCount + 2, 6)) {
         for (int z = 0; z < 32; z += Math.min(seabedCount + 2, 6)) {
            for (int y = 1; y < 8; y++) {
               if (this.blocks[x][y][z].isBuilt() && this.blocks[x][y - 1][z].hasElement(BlockType.SOFFIT)) {
                  Point worldBlockPos = blockToWorldCoords(new Point((float)x, (float)y, (float)z)).plus(this.pos);
                  float seabedHeight = (worldBlockPos.y - ChunkManager.getHeight(worldBlockPos.x, worldBlockPos.z)) / 35.0F + 1.0F;
                  if (seabedHeight < 8.0F) {
                     this.blocks[x][y - 1][z].addElement(BlockType.SEABED, this.getNeighbors(new Point((float)x, (float)(y - 1), (float)z)), false);
                     this.blocks[x][y - 1][z].getElement(BlockType.SEABED).setParam(seabedHeight);
                     seabedCount++;
                  }
                  break;
               }

               if (this.blocks[x][y][z].hasWall()) {
                  break;
               }
            }
         }
      }
   }

   private Block[] getNeighbors(Point blockPos) {
      Block[] neighbors = new Block[10];
      neighbors[Neighbor.UP.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.UP.getOffset()));
      neighbors[Neighbor.DOWN.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.DOWN.getOffset()));
      neighbors[Neighbor.WEST.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.WEST.getOffset()));
      neighbors[Neighbor.EAST.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.EAST.getOffset()));
      neighbors[Neighbor.NORTH.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.NORTH.getOffset()));
      neighbors[Neighbor.SOUTH.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.SOUTH.getOffset()));
      neighbors[Neighbor.NORTHEAST.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.NORTHEAST.getOffset()));
      neighbors[Neighbor.NORTHWEST.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.NORTHWEST.getOffset()));
      neighbors[Neighbor.SOUTHEAST.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.SOUTHEAST.getOffset()));
      neighbors[Neighbor.SOUTHWEST.getIndex()] = this.getBlockAt(blockPos.plus(Neighbor.SOUTHWEST.getOffset()));
      return neighbors;
   }

   private Block blockAt(int x, int y, int z) {
      return this.getBlockAt(new Point((float)x, (float)y, (float)z));
   }

   private Block getBlockAt(Point pos) {
      return pos.x >= 0.0F && pos.x < 32.0F && pos.y >= 0.0F && pos.y < 8.0F && pos.z >= 0.0F && pos.z < 32.0F
         ? this.blocks[(int)pos.x][(int)pos.y][(int)pos.z]
         : new Block(pos.minus(16.0F, 4.0F, 16.0F));
   }

   public final void triggerWaterLeak() {
      if (this.lastContactBlock != null) {
         this.lastContactBlock.enableWaterLeak();
         this.rebuildDisplayLists();
         this.lastContactBlock = null;
      }
   }

   private void rebuildRoomFiller() {
      this.roomFiller = new RoomFiller(this.pos);

      for (int x = 0; x < 32; x++) {
         for (int y = 0; y < 8; y++) {
            for (int z = 0; z < 32; z++) {
               if (this.blocks[x][y][z].hasWaterLeak()) {
                  this.roomFiller.registerRoom(this.blocks, new Point((float)x, (float)y, (float)z));
               }
            }
         }
      }
   }

   public final Point getRandomSurfacePoint() {
      return this.surfaceBlockPositions != null && this.surfaceBlockPositions.size() > 0 ? this.pos.plus(blockToWorldCoords(this.surfaceBlockPositions.get((int)(this.surfaceBlockPositions.size() * Math.random())))) : this.pos.copy();
   }

   public final boolean isAvatarInside() {
      return this.avatarInside;
   }

   public final BaseType getBaseType() {
      return this.baseType;
   }

   @SuppressWarnings("unchecked")
   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
      in.defaultReadObject();
      this.craftingAnim = new CraftingAnimation();
      this.animatedBlockPositions = new ArrayList<>();
      this.leakBlockPositions = new ArrayList<>();
      this.surfaceBlockPositions = new ArrayList<>();
      this.textureRenderBuckets = (ArrayList<Block>[]) new ArrayList[BaseModels.textureCount];
      if (this.baseType == null) {
         this.baseType = BaseType.EMPTY;
      }

      this.rebuildDisplayLists();
   }
}
