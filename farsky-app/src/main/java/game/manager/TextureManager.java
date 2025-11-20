package game.manager;

import game.Main;
import game.exceptions.TextureWidthException;
import game.util.FontFamily;
import game.util.FontRenderer;
import game.util.ImageData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public final class TextureManager {
   private static int lastTextureIndex = 119;
   private static int loadingIndex;
   public static int textureQuality = 1;
   private static GameState targetState;
   private static ArrayList<Integer> textureIds = new ArrayList<>();
   private static boolean isLoading = false;
   public static int terrainTextures;
   public static int terrainNormals;
   public static int causticTextures;
   public static int particle;
   public static int white;
   public static int godRay;
   public static int grass;
   public static int sandParticle;
   public static int rockTexture;
   public static int sight;
   public static int chillerFont;
   public static int chaparalFont;
   public static int eccentricFont;
   public static int optionElement;
   public static int optionArrow;
   public static int titleTexture;
   public static int mapCursor;
   public static int avatarGuiBackground;
   public static int avatarGuiBackgroundBar;
   public static int avatarGuiBarEffect;
   public static int avatarGuiColorBar;
   public static int avatarGuiDepth;
   public static int skillBackground;
   public static int skillArmor;
   public static int ink;
   public static int bubble;
   public static int alphaTexture;
   public static int lightning;
   public static int blood;
   public static int crack;
   public static int seabedAlpha;
   public static int seabedBeta;
   public static int pebble;
   public static int coralAlpha;
   public static int moss;
   public static int itemWheel;
   public static int items;
   public static int picking;
   public static int oilParticle;
   public static int oxygenBar;
   public static int avatarInventory;
   public static int oxygenBarBackground;
   public static int oxygenBarForeground;
   public static int inventoryDescription0;
   public static int inventoryDescription1;
   public static int fishBig;
   public static int fishSmall;
   public static int glowParticle;
   public static int fertilizer;
   public static int seaweedItem;
   public static int woundedLeg;
   public static int woundedArm;
   public static int avatarPortrait;
   public static int potatoPlantGrowing;
   public static int potatoPlantDone;
   public static int starving;
   public static int radio;
   public static int nathanPortrait;
   public static int jodiePortrait;
   public static int nobodyPortrait;
   public static int tvEffect;
   public static int saveSlot0;
   public static int saveSlot1;
   public static int saveArrow;
   public static int storageAction;
   public static int dirt;
   public static int companyLogo;
   public static int inventorySeparator;
   public static int leak;
   public static int coal;
   public static int fire;
   public static int stamina;
   public static int barracudaMeat;
   public static int subGui;
   public static int subBell;
   public static int subTop;
   public static int subLeftTopBottle;
   public static int subLeftBottomBottle;
   public static int subLeftMotor;
   public static int subRightTopBottle;
   public static int subRightBottomBottle;
   public static int subRightMotor;
   public static int subMain;
   public static int clouds;
   public static int water;
   public static int shine;
   public static int mantaRay;
   public static int lock;
   public static int unlock;
   public static int anglerfish;
   public static int frilledSharkMeat;
   public static int energySphere;
   public static int grassDepth;
   public static int jelly;
   public static int saveSlot2;
   public static int carrotPlantGrowing;
   public static int carrotPlantDone;
   public static int greenBeanPlantGrowing;
   public static int greenBeanPlantDone;
   public static int loadingImage;
   public static int loadingCircle;
   public static int menuButton;
   public static int coins;
   public static int whaleMeat;
   public static int oxygenWarning;
   public static int tunaMeat;
   public static int dolphinMeat;

   public static void initMenuTextures() {
      beginLoading(GameState.LOADING_MENU);

      try {
         chillerFont = loadSingleTexture("textures/gui/chillerFont.png", true, true);
         chaparalFont = loadSingleTexture("textures/gui/chaparalFont.png", true, true);
         eccentricFont = loadSingleTexture("textures/gui/eccentricFont.png", true, true);
         titleTexture = loadSingleTexture("textures/gui/title.png", true, true);
         white = loadSingleTexture("textures/white.png", true, false);
         companyLogo = loadSingleTexture("textures/gui/companyLogo.png", true, false);
         loadingImage = loadSingleTexture("textures/gui/loadingImage.png", true, true);
         loadingCircle = loadSingleTexture("textures/gui/loadingCircle.png", true, true);
      } catch (TextureWidthException e) {
         e.printStackTrace();
      }
   }

   public static void beginLoading(GameState state) {
      deleteAll();
      loadingIndex = 0;
      targetState = state;
   }

   public static void deleteAll() {
      for (int i = 0; i < textureIds.size(); i++) {
         GL11.glDeleteTextures(textureIds.get(i));
      }

      textureIds.clear();
   }

   public static int getResolution() {
      return textureQuality;
   }

   public static void loadNextTexture() {
      isLoading = true;

      try {
         if (loadingIndex ==0) {
            terrainTextures = loadArrayTexture(new String[]{"textures/Tsand.png", "textures/Trock.png", "textures/TsandAlga.png", "textures/Tabyss.png"}, true, false);
         }

         if (loadingIndex ==1) {
            terrainNormals = loadArrayTexture(new String[]{"textures/Nsand.png", "textures/Nrock.png", "textures/Nsand.png", "textures/Nsand.png"}, false, false);
         }

         if (loadingIndex ==2) {
            particle = loadSingleTexture("textures/particle.png", true, false);
         }

         if (loadingIndex ==3) {
            godRay = loadSingleTexture("textures/godRay.png", true, false);
         }

         if (loadingIndex ==4) {
            grass = loadSingleTexture("textures/grass.png", true, false);
         }

         if (loadingIndex ==5) {
            loadSingleTexture("textures/eye.png", true, false);
         }

         if (loadingIndex ==6) {
            loadSingleTexture("textures/grad.png", true, false);
         }

         if (loadingIndex ==7) {
            loadSingleTexture("textures/pickCursor.png", true, false);
         }

         if (loadingIndex ==8) {
            loadSingleTexture("textures/disk.png", true, true);
         }

         if (loadingIndex ==9) {
            loadSingleTexture("textures/floorHole.png", true, true);
         }

         if (loadingIndex ==10) {
            sandParticle = loadSingleTexture("textures/sandParticle.png", true, true);
         }

         if (loadingIndex ==11) {
            rockTexture = loadSingleTexture("textures/Trock.png", false, false);
         }

         if (loadingIndex ==12) {
            loadSingleTexture("textures/gui/bar.png", true, false);
         }

         if (loadingIndex ==13) {
            skillArmor = loadSingleTexture("textures/gui/skills/skillArmor.png", true, true);
         }

         if (loadingIndex ==14) {
            loadSingleTexture("textures/gui/skills/skillPower.png", true, true);
         }

         if (loadingIndex ==15) {
            loadSingleTexture("textures/gui/skills/skillDamage.png", true, true);
         }

         if (loadingIndex ==16) {
            loadSingleTexture("textures/gui/skills/skillPressure.png", true, true);
         }

         if (loadingIndex ==17) {
            sight = loadSingleTexture("textures/sight.png", true, true);
         }

         if (loadingIndex ==18) {
            optionElement = loadSingleTexture("textures/gui/optionElement.png", true, true);
         }

         if (loadingIndex ==19) {
            optionArrow = loadSingleTexture("textures/gui/optionArrow.png", true, true);
         }

         if (loadingIndex ==20) {
            mapCursor = loadSingleTexture("textures/gui/mapCursor.png", true, true);
         }

         if (loadingIndex ==21) {
            loadSingleTexture("textures/gui/loading.png", true, true);
         }

         if (loadingIndex ==22) {
            loadSingleTexture("textures/gui/loadingLines.png", true, true);
         }

         if (loadingIndex ==23) {
            avatarGuiBackground = loadSingleTexture("textures/gui/avatarGUIBackground.png", true, true);
         }

         if (loadingIndex ==24) {
            avatarGuiBackgroundBar = loadSingleTexture("textures/gui/avatarGUIbackgroundBar.png", true, true);
         }

         if (loadingIndex ==25) {
            avatarGuiBarEffect = loadSingleTexture("textures/gui/avatarGUIBarEffect.png", true, true);
         }

         if (loadingIndex ==26) {
            avatarGuiColorBar = loadSingleTexture("textures/gui/avatarGUIColorBar.png", true, true);
         }

         if (loadingIndex ==27) {
            avatarGuiDepth = loadSingleTexture("textures/gui/avatarGUIDepth.png", true, true);
         }

         if (loadingIndex ==28) {
            skillBackground = loadSingleTexture("textures/gui/skillBackground.png", true, true);
         }

         if (loadingIndex ==29) {
            ink = loadSingleTexture("textures/gui/ink.png", true, true);
         }

         if (loadingIndex ==30) {
            loadSingleTexture("textures/currentParticle.png", true, true);
         }

         if (loadingIndex ==31) {
            bubble = loadSingleTexture("textures/bubble.png", true, true);
         }

         if (loadingIndex ==32) {
            alphaTexture = loadSingleTexture("textures/alpha.png", true, false);
         }

         if (loadingIndex ==33) {
            lightning = loadSingleTexture("textures/lightning.png", true, true);
         }

         if (loadingIndex ==34) {
            blood = loadSingleTexture("textures/blood.png", true, true);
         }

         if (loadingIndex ==35) {
            loadSingleTexture("textures/gui/skills/skillCenterBar.png", true, true);
         }

         if (loadingIndex ==36) {
            crack = loadSingleTexture("textures/gui/crack.png", true, true);
         }

         if (loadingIndex ==37) {
            seabedAlpha = loadSingleTexture("textures/seabedAlpha.png", true, false);
         }

         if (loadingIndex ==38) {
            seabedBeta = loadSingleTexture("textures/seabedBeta.png", true, true);
         }

         if (loadingIndex ==39) {
            pebble = loadSingleTexture("textures/pebble.png", true, true);
         }

         if (loadingIndex ==40) {
            loadSingleTexture("textures/gui/selector.png", true, true);
         }

         if (loadingIndex ==41) {
            coralAlpha = loadSingleTexture("textures/coralAlpha.png", true, true);
         }

         if (loadingIndex ==42) {
            moss = loadSingleTexture("textures/moss.png", true, true);
         }

         if (loadingIndex ==43) {
            itemWheel = loadSingleTexture("textures/gui/items/wheel.png", true, false);
         }

         if (loadingIndex ==44) {
            items = loadSingleTexture("textures/gui/items/items.png", true, true);
         }

         if (loadingIndex ==45) {
            picking = loadSingleTexture("textures/picking.png", true, true);
         }

         if (loadingIndex ==46) {
            loadSingleTexture("textures/gui/bigWheel.png", true, true);
         }

         if (loadingIndex ==47) {
            loadSingleTexture("textures/gui/boltGui.png", true, true);
         }

         if (loadingIndex ==48) {
            oilParticle = loadSingleTexture("textures/oilParticle.png", true, true);
         }

         if (loadingIndex ==49) {
            oxygenBar = loadSingleTexture("textures/gui/oxygenBar.png", true, true);
         }

         if (loadingIndex ==50) {
            avatarInventory = loadSingleTexture("textures/gui/items/avatarInventory.png", true, true);
         }

         if (loadingIndex ==51) {
            oxygenBarBackground = loadSingleTexture("textures/gui/oxygenBarBackground.png", true, true);
         }

         if (loadingIndex ==52) {
            oxygenBarForeground = loadSingleTexture("textures/gui/oxygenBarForeground.png", true, true);
         }

         if (loadingIndex ==53) {
            inventoryDescription0 = loadSingleTexture("textures/gui/items/inventoryDescription0.png", true, true);
         }

         if (loadingIndex ==54) {
            inventoryDescription1 = loadSingleTexture("textures/gui/items/inventoryDescription1.png", true, true);
         }

         if (loadingIndex ==55) {
            fishBig = loadSingleTexture("textures/gui/items/fishBig.png", true, true);
         }

         if (loadingIndex ==56) {
            fishSmall = loadSingleTexture("textures/gui/items/fishSmall.png", true, true);
         }

         if (loadingIndex ==57) {
            loadSingleTexture("textures/gui/skills/skillHook.png", true, true);
         }

         if (loadingIndex ==58) {
            glowParticle = loadSingleTexture("textures/gui/glowParticle.png", true, true);
         }

         if (loadingIndex ==59) {
            fertilizer = loadSingleTexture("textures/gui/items/fertilizer.png", true, true);
         }

         if (loadingIndex ==60) {
            seaweedItem = loadSingleTexture("textures/gui/items/seaweed.png", true, true);
         }

         if (loadingIndex ==61) {
            woundedLeg = loadSingleTexture("textures/gui/woundedLeg.png", true, true);
         }

         if (loadingIndex ==62) {
            woundedArm = loadSingleTexture("textures/gui/woundedArm.png", true, true);
         }

         if (loadingIndex ==63) {
            avatarPortrait = loadSingleTexture("textures/gui/avatar.png", true, true);
         }

         if (loadingIndex ==64) {
            potatoPlantGrowing = loadSingleTexture("textures/potatoPlantGrowing.png", true, false);
         }

         if (loadingIndex ==65) {
            potatoPlantDone = loadSingleTexture("textures/potatoPlantDone.png", true, false);
         }

         if (loadingIndex ==66) {
            starving = loadSingleTexture("textures/gui/starving.png", true, true);
         }

         if (loadingIndex ==67) {
            radio = loadSingleTexture("textures/gui/dialog/radio.png", true, true);
         }

         if (loadingIndex ==68) {
            nathanPortrait = loadSingleTexture("textures/gui/dialog/nathan.png", true, true);
         }

         if (loadingIndex ==69) {
            jodiePortrait = loadSingleTexture("textures/gui/dialog/jodie.png", true, true);
         }

         if (loadingIndex ==70) {
            nobodyPortrait = loadSingleTexture("textures/gui/dialog/nobody.png", true, true);
         }

         if (loadingIndex ==71) {
            tvEffect = loadSingleTexture("textures/gui/dialog/tvEffect.png", true, true);
         }

         if (loadingIndex ==72) {
            saveSlot0 = loadSingleTexture("textures/gui/save/save0.png", true, false);
         }

         if (loadingIndex ==73) {
            saveSlot1 = loadSingleTexture("textures/gui/save/save1.png", true, false);
         }

         if (loadingIndex ==74) {
            saveArrow = loadSingleTexture("textures/gui/save/saveArrow.png", true, false);
         }

         if (loadingIndex ==75) {
            storageAction = loadSingleTexture("textures/gui/storageAction.png", true, true);
         }

         if (loadingIndex ==76) {
            dirt = loadSingleTexture("textures/dirt.png", true, true);
         }

         if (loadingIndex ==77) {
            inventorySeparator = loadSingleTexture("textures/gui/inventorySeparator.png", true, false);
         }

         if (loadingIndex ==78) {
            leak = loadSingleTexture("textures/leak.png", true, false);
         }

         if (loadingIndex ==79) {
            coal = loadSingleTexture("textures/coal.png", true, true);
         }

         if (loadingIndex ==80) {
            fire = loadSingleTexture("textures/gui/items/fire.png", true, true);
         }

         if (loadingIndex ==81) {
            stamina = loadSingleTexture("textures/gui/stamina.png", true, true);
         }

         if (loadingIndex ==82) {
            barracudaMeat = loadSingleTexture("textures/gui/items/barracudaMeat.png", true, true);
         }

         if (loadingIndex ==83) {
            subGui = loadSingleTexture("textures/gui/sub/sub.png", true, true);
         }

         if (loadingIndex ==84) {
            subBell = loadSingleTexture("textures/gui/sub/bell.png", true, true);
         }

         if (loadingIndex ==85) {
            subTop = loadSingleTexture("textures/gui/sub/top.png", true, true);
         }

         if (loadingIndex ==86) {
            subLeftTopBottle = loadSingleTexture("textures/gui/sub/leftTopBottle.png", true, true);
         }

         if (loadingIndex ==87) {
            subLeftBottomBottle = loadSingleTexture("textures/gui/sub/leftBottomBottle.png", true, true);
         }

         if (loadingIndex ==88) {
            subLeftMotor = loadSingleTexture("textures/gui/sub/leftMotor.png", true, true);
         }

         if (loadingIndex ==89) {
            subRightTopBottle = loadSingleTexture("textures/gui/sub/rightTopBottle.png", true, true);
         }

         if (loadingIndex ==90) {
            subRightTopBottle = loadSingleTexture("textures/gui/sub/rightTopBottle.png", true, true);
         }

         if (loadingIndex ==91) {
            subRightBottomBottle = loadSingleTexture("textures/gui/sub/rightBottomBottle.png", true, true);
         }

         if (loadingIndex ==92) {
            subRightMotor = loadSingleTexture("textures/gui/sub/rightMotor.png", true, true);
         }

         if (loadingIndex ==93) {
            subMain = loadSingleTexture("textures/gui/sub/main.png", true, true);
         }

         if (loadingIndex ==94) {
            loadSingleTexture("textures/echo.png", true, true);
         }

         if (loadingIndex ==95) {
            clouds = loadSingleTexture("textures/clouds.png", true, true);
         }

         if (loadingIndex ==96) {
            loadSingleTexture("textures/cinematic.png", true, true);
         }

         if (loadingIndex ==97) {
            loadSingleTexture("textures/cinematicFront.png", true, true);
         }

         if (loadingIndex ==98) {
            water = loadSingleTexture("textures/water.png", true, true);
         }

         if (loadingIndex ==99) {
            shine = loadSingleTexture("textures/shine.png", true, true);
         }

         if (loadingIndex ==100) {
            mantaRay = loadSingleTexture("textures/gui/items/mantaRay.png", true, true);
         }

         if (loadingIndex ==101) {
            lock = loadSingleTexture("textures/gui/lock.png", true, true);
         }

         if (loadingIndex ==102) {
            unlock = loadSingleTexture("textures/gui/unlock.png", true, true);
         }

         if (loadingIndex ==103) {
            anglerfish = loadSingleTexture("textures/gui/items/anglerfish.png", true, true);
         }

         if (loadingIndex ==104) {
            frilledSharkMeat = loadSingleTexture("textures/gui/items/frilledSharkMeat.png", true, true);
         }

         if (loadingIndex ==105) {
            energySphere = loadSingleTexture("textures/gui/items/energySphere.png", true, true);
         }

         if (loadingIndex ==106) {
            grassDepth = loadSingleTexture("textures/grassDepth.png", true, false);
         }

         if (loadingIndex ==107) {
            jelly = loadSingleTexture("textures/gui/items/jelly.png", true, true);
         }

         if (loadingIndex ==108) {
            saveSlot2 = loadSingleTexture("textures/gui/save/save2.png", true, false);
         }

         if (loadingIndex ==109) {
            carrotPlantGrowing = loadSingleTexture("textures/carrotPlantGrowing.png", true, false);
         }

         if (loadingIndex ==110) {
            carrotPlantDone = loadSingleTexture("textures/carrotPlantDone.png", true, false);
         }

         if (loadingIndex ==111) {
            greenBeanPlantDone = loadSingleTexture("textures/greenBeanPlantDone.png", true, false);
         }

         if (loadingIndex ==112) {
            greenBeanPlantGrowing = loadSingleTexture("textures/greenBeanPlantGrowing.png", true, false);
         }

         if (loadingIndex ==113) {
            causticTextures = loadArrayTexture(new String[]{"textures/caustic0.png", "textures/caustic1.png"}, false, false);
         }

         if (loadingIndex ==114) {
            menuButton = loadSingleTexture("textures/gui/menuButton.png", true, true);
         }

         if (loadingIndex ==115) {
            coins = loadSingleTexture("textures/gui/coins.png", true, true);
         }

         if (loadingIndex ==116) {
            whaleMeat = loadSingleTexture("textures/gui/items/whaleMeat.png", true, true);
         }

         if (loadingIndex ==117) {
            oxygenWarning = loadSingleTexture("textures/gui/oxygenWarning.png", true, false);
         }

         if (loadingIndex ==118) {
            tunaMeat = loadSingleTexture("textures/gui/items/tunaMeat.png", true, true);
         }

         if (loadingIndex ==119) {
            dolphinMeat = loadSingleTexture("textures/gui/items/dolphinMeat.png", true, true);
         }
      } catch (TextureWidthException e) {
         e.printStackTrace();
      }

      isLoading = false;
      if (loadingIndex == lastTextureIndex) {
         Main.gameState = targetState;
      }

      loadingIndex++;
   }

   public static void renderLoadingProgress() {
      FontRenderer.saveFontFamily();
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 - FontRenderer.getCharHeight(0.7F) - 10, "Loading textures", 0.7F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
      FontRenderer.drawCentered(Display.getWidth() / 2, Display.getHeight() / 2 + 10, (int)((float)loadingIndex / lastTextureIndex * 100.0F) + "%", 0.6F);
      FontRenderer.restoreFontFamily();
   }

   private static int loadArrayTexture(String[] paths, boolean hasAlpha, boolean smooth) throws TextureWidthException {
      ImageData imageData = new ImageData();
      int layerCount = paths.length;

      for (int i = 0; i < layerCount; i++) {
         imageData.load(paths[i], false, false);
      }

      if (!hasAlpha && imageData.getWidth() % 4 != 0) {
         throw new TextureWidthException("RGB Texture width must be a multiple of 4 !");
      } else {
         ByteBuffer buffer;
         if (!hasAlpha) {
            buffer = ByteBuffer.allocateDirect(3 * imageData.getWidth() * imageData.getHeight() * layerCount / (textureQuality * textureQuality));
         } else {
            buffer = ByteBuffer.allocateDirect(4 * imageData.getWidth() * imageData.getHeight() * layerCount / (textureQuality * textureQuality));
         }

         for (int layer = 0; layer < layerCount; layer++) {
            for (int row = 0; row < imageData.getHeight(); row += textureQuality) {
               for (int col = 0; col < imageData.getWidth(); col += textureQuality) {
                  buffer.put(imageData.getByte(layer * imageData.getHeight() * imageData.getWidth() + col + row * imageData.getWidth() << 2));
                  buffer.put(imageData.getByte((layer * imageData.getHeight() * imageData.getWidth() + col + row * imageData.getWidth() << 2) + 1));
                  buffer.put(imageData.getByte((layer * imageData.getHeight() * imageData.getWidth() + col + row * imageData.getWidth() << 2) + 2));
                  if (hasAlpha) {
                     buffer.put(imageData.getByte((layer * imageData.getHeight() * imageData.getWidth() + col + row * imageData.getWidth() << 2) + 3));
                  }
               }
            }
         }

         buffer.rewind();
         IntBuffer texIdBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
         GL11.glGenTextures(texIdBuf);
         GL11.glBindTexture(GL12.GL_TEXTURE_3D, texIdBuf.get(0));
         GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
         GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
         if (!hasAlpha) {
            GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGB, imageData.getWidth() / textureQuality, imageData.getHeight() / textureQuality, layerCount, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
         } else {
            GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGBA, imageData.getWidth() / textureQuality, imageData.getHeight() / textureQuality, layerCount, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
         }

         if (Main.isVerbose) {
            System.out.println("New texture Id: " + texIdBuf.get(0) + ", for: " + paths[0]);
         }

         if (isLoading) {
            textureIds.add(texIdBuf.get(0));
         }

         return texIdBuf.get(0);
      }
   }

   private static int loadSingleTexture(String path, boolean hasAlpha, boolean smooth) throws TextureWidthException {
      return loadTexture(path, hasAlpha, smooth, false, false);
   }

   public static int loadTexture(String path, boolean hasAlpha, boolean smooth, boolean flipX, boolean flipY) throws TextureWidthException {
      ImageData imageData = new ImageData(path, flipX, flipY);
      if (!hasAlpha && imageData.getWidth() % 4 != 0) {
         throw new TextureWidthException("RGB Texture width must be a multiple of 4 !");
      } else {
         ByteBuffer buffer;
         if (!hasAlpha) {
            buffer = ByteBuffer.allocateDirect(3 * imageData.getWidth() * imageData.getHeight());
         } else {
            buffer = ByteBuffer.allocateDirect(4 * imageData.getWidth() * imageData.getHeight());
         }

         for (int row = 0; row < imageData.getHeight(); row++) {
            for (int col = 0; col < imageData.getWidth(); col++) {
               buffer.put(imageData.getByte(col + row * imageData.getWidth() << 2));
               buffer.put(imageData.getByte((col + row * imageData.getWidth() << 2) + 1));
               buffer.put(imageData.getByte((col + row * imageData.getWidth() << 2) + 2));
               if (hasAlpha) {
                  buffer.put(imageData.getByte((col + row * imageData.getWidth() << 2) + 3));
               }
            }
         }

         buffer.rewind();
         IntBuffer texIdBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
         GL11.glGenTextures(texIdBuf);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIdBuf.get(0));
         if (smooth) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
         } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
         }

         if (!hasAlpha) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, imageData.getWidth(), imageData.getHeight(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
         } else {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, imageData.getWidth(), imageData.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
         }

         if (Main.isVerbose) {
            System.out.println("New texture Id: " + texIdBuf.get(0) + ", for: " + path);
         }

         if (isLoading) {
            textureIds.add(texIdBuf.get(0));
         }

         return texIdBuf.get(0);
      }
   }

   public static int captureFramebuffer(int textureId) {
      return captureFramebuffer(textureId, Display.getWidth(), Display.getHeight(), false);
   }

   public static int captureFramebuffer(int textureId, int width, int height, boolean smooth) {
      if (textureId == -1) {
         IntBuffer texIdBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
         GL11.glGenTextures(texIdBuf);
         textureId = texIdBuf.get(0);
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
      if (smooth) {
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
      } else {
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
      }

      GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, width, height, 0);
      return textureId;
   }

   public static void drawTexture(int textureId, int width, int height) {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(width, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(width, height);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, height);
      GL11.glEnd();
   }
}
