package game.manager;

import game.chunks.chunkElements.AbyssElement;
import game.chunks.chunkElements.AbyssalAlga;
import game.chunks.chunkElements.Alga;
import game.chunks.chunkElements.Chest;
import game.chunks.chunkElements.Vent;
import game.chunks.chunkElements.JellyPlant;
import game.chunks.chunkElements.TreasureChest;
import game.chunks.chunkElements.Coral;
import game.chunks.chunkElements.GiantAlga;
import game.chunks.chunkElements.GiantAlgaLight;
import game.chunks.chunkElements.RockRing;
import game.chunks.chunkElements.Rock;
import game.chunks.chunkElements.OreDeposit;
import game.chunks.chunkElements.SeaSponge;
import game.chunks.chunkElements.Seaweed;
import game.chunks.chunkElements.LargeSeaweed;
import game.chunks.chunkElements.Shipwreck;
import game.enemy.RockWallResources;
import game.enemy.enemyWithMouth.Anglerfish;
import game.enemy.enemyWithMouth.Barracuda;
import game.enemy.enemyWithMouth.FrilledShark;
import game.enemy.enemyWithMouth.Shark;
import game.enemy.jellyFish.JellyFish;
import game.enemy.kraken.KrakenBody;
import game.enemy.kraken.KrakenTentacle;
import game.enemy.lightning.AbyssalLightningFish;
import game.environment.DepthAtmosphere;
import game.environment.FlyingRock;
import game.environment.life.Dolphin;
import game.environment.life.Skatefish;
import game.environment.life.StandardFish;
import game.environment.life.Tuna;
import game.environment.life.Whale;
import game.environment.water.WaterSurface;
import game.gui.GuiRenderer;
import game.gui.menu.MenuController;
import game.map.MapRenderer;
import game.outsideObj.Extractor;
import game.outsideObj.HarpoonCannon;
import game.outsideObj.Lamp;
import game.player.PlayerArm;
import game.player.WorldChest;
import game.player.droid.Droid;
import game.player.weapons.Arrow;
import game.player.weapons.HandDrill;
import game.player.weapons.HarpoonGun;
import game.player.weapons.Knife;
import game.player.weapons.UnderwaterScooter;
import game.render.FullscreenQuad;
import game.render.ModelLoader;
import game.render.SimpleVbo;
import game.render.Vertex;
import game.saving.SaveManager;
import game.seafloorBase.BaseModels;
import game.seafloorBase.Element;
import game.sounds.ChunkLayer;
import game.sounds.SoundManager;
import game.submarine.Submarine;
import game.util.Color;
import game.util.Coord;
import game.util.FontFamily;
import game.util.FontRenderer;
import game.util.Point;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public final class AssetLoader {
   private static transient float loadStep = 0.0F;

   public static boolean loadNextAsset() {
      if (loadStep == 0.0F) {
         SoundManager.musicAwakening = SoundManager.loadSound("sounds/music/awakening.ogg");
      }

      if (loadStep == 1.0F) {
         SoundManager.musicFarsky = SoundManager.loadSound("sounds/music/farsky.ogg");
      }

      if (loadStep == 2.0F) {
         SoundManager.musicKraken = SoundManager.loadSound("sounds/music/kraken.ogg");
      }

      if (loadStep == 3.0F) {
         SoundManager.musicWithTheSeaAsACage = SoundManager.loadSound("sounds/music/with_the_sea_as_a_cage.ogg");
      }

      if (loadStep == 4.0F) {
         SoundManager.musicTimeDoesntFlow = SoundManager.loadSound("sounds/music/time_doesn_t_flow_down_here.ogg");
      }

      if (loadStep == 5.0F) {
         SoundManager.musicIntoColdDarkness = SoundManager.loadSound("sounds/music/into_cold_darkness.ogg");
      }

      if (loadStep == 6.0F) {
         SoundManager.musicDarkCrushingDark = SoundManager.loadSound("sounds/music/dark_crushing_dark.ogg");
      }

      if (loadStep == 7.0F) {
         SoundManager.musicLurker = SoundManager.loadSound("sounds/music/lurker.ogg");
      }

      if (loadStep == 8.0F) {
         SoundManager.musicUnderwaterSunrise = SoundManager.loadSound("sounds/music/underwater_sunrise.ogg");
      }

      if (loadStep == 9.0F) {
         SoundManager.ambientBackground = SoundManager.loadSound("sounds/ambient/background.ogg");
      }

      if (loadStep == 10.0F) {
         SoundManager.ambientTension = SoundManager.loadSound("sounds/ambient/tension.ogg");
      }

      if (loadStep == 11.0F) {
         SoundManager.ambientUnderwater = SoundManager.loadSound("sounds/ambient/underwater.ogg");
      }

      if (loadStep == 12.0F) {
         SoundManager.sfxEnemyScream = SoundManager.loadSound("sounds/soundEffects/enemyScream.ogg");
      }

      if (loadStep == 13.0F) {
         SoundManager.sfxClick = SoundManager.loadSound("sounds/soundEffects/click.ogg");
      }

      if (loadStep == 14.0F) {
         SoundManager.sfxHover = SoundManager.loadSound("sounds/soundEffects/hover.ogg");
      }

      if (loadStep == 15.0F) {
         SoundManager.sfxOctopusNoise = SoundManager.loadSound("sounds/soundEffects/octopusNoise.ogg");
      }

      if (loadStep == 16.0F) {
         SoundManager.sfxEnemyScream2 = SoundManager.loadSound("sounds/soundEffects/enemyScream.ogg");
      }

      if (loadStep == 17.0F) {
         SoundManager.sfxGroundExploder = SoundManager.loadSound("sounds/soundEffects/groundExploder.ogg");
      }

      if (loadStep == 18.0F) {
         SoundManager.sfxCapaCharged = SoundManager.loadSound("sounds/soundEffects/capaCharged.ogg");
      }

      if (loadStep == 19.0F) {
         SoundManager.sfxBass = SoundManager.loadSound("sounds/soundEffects/bass.ogg");
      }

      if (loadStep == 20.0F) {
         SoundManager.sfxFloorExplosion = SoundManager.loadSound("sounds/soundEffects/floorExplosion.ogg");
      }

      if (loadStep == 21.0F) {
         SoundManager.sfxHeartbeat = SoundManager.loadSound("sounds/soundEffects/heartBeat.ogg");
      }

      if (loadStep == 22.0F) {
         SoundManager.sfxHurt = SoundManager.loadSound("sounds/soundEffects/hurt.ogg");
      }

      if (loadStep == 23.0F) {
         SoundManager.sfxHarpoon = SoundManager.loadSound("sounds/soundEffects/harpoon.ogg");
      }

      if (loadStep == 24.0F) {
         SoundManager.sfxChestOpening = SoundManager.loadSound("sounds/soundEffects/chestOpening.ogg");
      }

      if (loadStep == 25.0F) {
         SoundManager.sfxChestClosing = SoundManager.loadSound("sounds/soundEffects/chestClosing.ogg");
      }

      if (loadStep == 26.0F) {
         SoundManager.sfxItem = SoundManager.loadSound("sounds/soundEffects/item.ogg");
      }

      if (loadStep == 27.0F) {
         SoundManager.sfxWallFalling = SoundManager.loadSound("sounds/soundEffects/wallFalling.ogg");
      }

      if (loadStep == 28.0F) {
         SoundManager.sfxOutFromGround = SoundManager.loadSound("sounds/soundEffects/outFromGround.ogg");
      }

      if (loadStep == 29.0F) {
         SoundManager.sfxTouched = SoundManager.loadSound("sounds/soundEffects/touched.ogg");
      }

      if (loadStep == 30.0F) {
         SoundManager.sfxSpear = SoundManager.loadSound("sounds/soundEffects/spear.ogg");
      }

      if (loadStep == 31.0F) {
         SoundManager.sfxAir = SoundManager.loadSound("sounds/soundEffects/air.ogg");
      }

      if (loadStep == 32.0F) {
         SoundManager.sfxHurtFloor = SoundManager.loadSound("sounds/soundEffects/hurtFloor.ogg");
      }

      if (loadStep == 33.0F) {
         SoundManager.sfxSynaps = SoundManager.loadSound("sounds/soundEffects/synaps.ogg");
      }

      if (loadStep == 34.0F) {
         SoundManager.sfxKnife = SoundManager.loadSound("sounds/soundEffects/knife.ogg");
      }

      if (loadStep == 35.0F) {
         SoundManager.sfxCrack = SoundManager.loadSound("sounds/soundEffects/crack.ogg");
      }

      if (loadStep == 36.0F) {
         SoundManager.sfxMovement = SoundManager.loadSound("sounds/soundEffects/movement.ogg");
      }

      if (loadStep == 37.0F) {
         SoundManager.sfxCoralHide = SoundManager.loadSound("sounds/soundEffects/coralHide.ogg");
      }

      if (loadStep == 38.0F) {
         SoundManager.sfxSteps = SoundManager.loadSound("sounds/soundEffects/steps.ogg");
      }

      if (loadStep == 39.0F) {
         SoundManager.sfxInWater = SoundManager.loadSound("sounds/soundEffects/inWater.ogg");
      }

      if (loadStep == 40.0F) {
         SoundManager.sfxVesselCollision = SoundManager.loadSound("sounds/soundEffects/vesselCollision.ogg");
      }

      if (loadStep == 41.0F) {
         SoundManager.sfxEngine = SoundManager.loadSound("sounds/soundEffects/engine.ogg");
      }

      if (loadStep == 42.0F) {
         SoundManager.sfxBuild = SoundManager.loadSound("sounds/soundEffects/build.ogg");
      }

      if (loadStep == 43.0F) {
         SoundManager.sfxRisingFromFloor = SoundManager.loadSound("sounds/soundEffects/risingFromFloor.ogg");
      }

      if (loadStep == 44.0F) {
         SoundManager.sfxBreathing = SoundManager.loadSound("sounds/soundEffects/breathing.ogg");
      }

      if (loadStep == 45.0F) {
         SoundManager.sfxGrab = SoundManager.loadSound("sounds/soundEffects/grab.ogg");
      }

      if (loadStep == 46.0F) {
         SoundManager.sfxSwim = SoundManager.loadSound("sounds/soundEffects/swim.ogg");
      }

      if (loadStep == 47.0F) {
         SoundManager.sfxRadio = SoundManager.loadSound("sounds/soundEffects/radio.ogg");
      }

      if (loadStep == 48.0F) {
         SoundManager.sfxDrill = SoundManager.loadSound("sounds/soundEffects/drill.ogg");
      }

      if (loadStep == 49.0F) {
         SoundManager.sfxStepsWater = SoundManager.loadSound("sounds/soundEffects/stepsWater.ogg");
      }

      if (loadStep == 50.0F) {
         SoundManager.sfxWaterLeak = SoundManager.loadSound("sounds/soundEffects/waterLeak.ogg");
      }

      if (loadStep == 51.0F) {
         SoundManager.sfxKrakenDie = SoundManager.loadSound("sounds/soundEffects/krakenDie.ogg");
      }

      if (loadStep == 52.0F) {
         SoundManager.sfxDoorOpen = SoundManager.loadSound("sounds/soundEffects/doorOpen.ogg");
      }

      if (loadStep == 53.0F) {
         SoundManager.sfxDoorClose = SoundManager.loadSound("sounds/soundEffects/doorClose.ogg");
      }

      if (loadStep == 54.0F) {
         SoundManager.sfxNewPiece = SoundManager.loadSound("sounds/soundEffects/newPiece.ogg");
      }

      if (loadStep == 55.0F) {
         SoundManager.sfxCinematic = SoundManager.loadSound("sounds/soundEffects/cinematic.ogg");
      }

      if (loadStep == 56.0F) {
         SoundManager.sfxScooter = SoundManager.loadSound("sounds/soundEffects/scooter.ogg");
      }

      if (loadStep == 57.0F) {
         SoundManager.sfxWaves = SoundManager.loadSound("sounds/soundEffects/waves.ogg");
      }

      if (loadStep == 58.0F) {
         SoundManager.sfxWhale = SoundManager.loadSound("sounds/soundEffects/whale.ogg");
      }

      if (loadStep == 59.0F) {
         SoundManager.sfxLightning = SoundManager.loadSound("sounds/soundEffects/lightning.ogg");
      }

      if (loadStep == 60.0F) {
         SoundManager.sfxMoney = SoundManager.loadSound("sounds/soundEffects/money.ogg");
      }

      if (loadStep == 61.0F) {
         SoundManager.sfxPowerOn = SoundManager.loadSound("sounds/soundEffects/powerOn.ogg");
      }

      if (loadStep == 62.0F) {
         SoundManager.sfxMoneySpent = SoundManager.loadSound("sounds/soundEffects/moneySpent.ogg");
      }

      if (loadStep == 63.0F) {
         SoundManager.sfxDolphin = SoundManager.loadSound("sounds/soundEffects/dolphin.ogg");
      }

      if (loadStep == 64.0F) {
         ChunkLayer.init();
      }

      if (loadStep == 65.0F) {
         MenuController.init();
      }

      if (loadStep == 66.0F) {
         DepthAtmosphere.init();
      }

      if (loadStep == 67.0F) {
         MapRenderer.init();
      }

      if (loadStep == 68.0F) {
         Submarine.loadAssets();
      }

      if (loadStep == 69.0F) {
         WorldChest.loadAssets();
      }

      if (loadStep == 70.0F) {
         FlyingRock.loadModel();
      }

      if (loadStep == 71.0F) {
         WaterSurface.buildMesh();
      }

      if (loadStep == 72.0F) {
         Skatefish.loadAssets();
      }

      if (loadStep == 73.0F) {
         Tuna.loadAssets();
      }

      if (loadStep == 74.0F) {
         Dolphin.loadAssets();
      }

      if (loadStep == 75.0F) {
         Whale.loadAssets();
      }

      if (loadStep == 76.0F) {
         StandardFish.loadAssets();
      }

      if (loadStep == 77.0F) {
         game.environment.life.AbyssalFish.loadAssets();
      }

      if (loadStep == 78.0F) {
         Element.loadModels();
      }

      if (loadStep == 79.0F) {
         BaseModels.loadTextures();
      }

      if (loadStep == 80.0F) {
         Extractor.loadModels();
      }

      if (loadStep == 81.0F) {
         HarpoonCannon.loadModels();
      }

      if (loadStep == 82.0F) {
         Lamp.loadModels();
      }

      if (loadStep == 83.0F) {
         PlayerArm.loadTexture();
      }

      if (loadStep == 84.0F) {
         HarpoonGun.loadAssets();
      }

      if (loadStep == 85.0F) {
         HandDrill.loadAssets();
      }

      if (loadStep == 86.0F) {
         UnderwaterScooter.loadAssets();
      }

      if (loadStep == 87.0F) {
         Knife.loadAssets();
      }

      if (loadStep == 88.0F) {
         Arrow.loadAssets();
      }

      if (loadStep == 89.0F) {
         RockWallResources.loadResources();
      }

      if (loadStep == 90.0F) {
         JellyFish.bodyMesh = ModelLoader.loadMesh("jellyfish");
         JellyFish.texture = ModelLoader.loadTexture("jellyfish");
         ArrayList<Vertex> vertices = new ArrayList<>();

         for (float angle = 0.0F; angle < (float) (Math.PI * 2); angle = (float)(angle + (Math.PI / 4))) {
            for (int i = 0; i < 4; i++) {
               vertices.add(
                  new Vertex(
                     new Point((float)Math.cos(angle) * 0.9F, -i * 0.7F + 0.1F, (float)Math.sin(angle) * 0.9F),
                     new Coord(0, 0),
                     new Color(1.0F, 1.0F, 1.0F, (4 - i) / 2.0F)
                  )
               );
               vertices.add(
                  new Vertex(
                     new Point((float)Math.cos(angle) * 0.9F, -(i + 1) * 0.7F + 0.1F, (float)Math.sin(angle) * 0.9F),
                     new Coord(0, 0),
                     new Color(1.0F, 1.0F, 1.0F, (4 - (i + 1)) / 2.0F)
                  )
               );
            }
         }

         JellyFish.tentacleMesh = new SimpleVbo(vertices, true);
      }

      if (loadStep == 91.0F) {
         Shark.loadResources();
      }

      if (loadStep == 92.0F) {
         KrakenTentacle.loadResources();
         KrakenBody.loadResources();
      }

      if (loadStep == 93.0F) {
         Barracuda.loadResources();
      }

      if (loadStep == 94.0F) {
         Anglerfish.loadResources();
      }

      if (loadStep == 95.0F) {
         FrilledShark.loadResources();
      }

      if (loadStep == 96.0F) {
         AbyssalLightningFish.loadResources();
      }

      if (loadStep == 97.0F) {
         Alga.loadAssets();
      }

      if (loadStep == 98.0F) {
         GiantAlga.loadAssets();
      }

      if (loadStep == 99.0F) {
         GiantAlgaLight.loadAssets();
      }

      if (loadStep == 100.0F) {
         Chest.loadAssets();
      }

      if (loadStep == 101.0F) {
         Rock.loadAssets();
      }

      if (loadStep == 102.0F) {
         SeaSponge.loadAssets();
      }

      if (loadStep == 103.0F) {
         Seaweed.loadAssets();
      }

      if (loadStep == 104.0F) {
         Coral.loadAssets();
      }

      if (loadStep == 105.0F) {
         RockRing.loadAssets();
      }

      if (loadStep == 106.0F) {
         Alga.loadAssets();
      }

      if (loadStep == 107.0F) {
         OreDeposit.loadAssets();
      }

      if (loadStep == 108.0F) {
         Shipwreck.loadAssets();
      }

      if (loadStep == 109.0F) {
         JellyPlant.loadAssets();
      }

      if (loadStep == 110.0F) {
         AbyssElement.loadAssets();
      }

      if (loadStep == 111.0F) {
         Vent.loadAssets();
      }

      if (loadStep == 112.0F) {
         AbyssalAlga.loadAssets();
      }

      if (loadStep == 113.0F) {
         LargeSeaweed.loadAssets();
      }

      if (loadStep == 114.0F) {
         TreasureChest.loadAssets();
      }

      if (loadStep == 115.0F) {
         Droid.loadAssets();
      }

      if (loadStep == 116.0F) {
         SaveManager.loadAchievements();
      }

      if (loadStep == 117.0F) {
         loadStep = 0.0F;
         return false;
      } else {
         GuiRenderer.loadPercent = (int)(loadStep / 118.0F * 100.0F);
         loadStep++;
         return true;
      }
   }

   public static void renderSplashScreen() {
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.white);
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
      FullscreenQuad.draw();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.companyLogo);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth() / 2 - 400, Display.getHeight() / 2 - 231);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth() / 2 + 400, Display.getHeight() / 2 - 231);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth() / 2 + 400, Display.getHeight() / 2 + 231);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth() / 2 - 400, Display.getHeight() / 2 + 231);
      GL11.glEnd();
   }

   public static void renderLoadingScreen(String text) {
      int width = 2080;
      int height = 1168;
      float scale = Display.getHeight() / 1168.0F;
      if (scale < 1.0F) {
         width = (int)(2080.0F * scale);
         height = (int)(1168.0F * scale);
      }

      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.loadingImage);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth() / 2 - width / 2, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(Display.getWidth() / 2 + width / 2, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth() / 2 + width / 2, height);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(Display.getWidth() / 2 - width / 2, height);
      GL11.glEnd();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.loadingCircle);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(Display.getWidth() - 84, Display.getHeight() - 84, 0.0F);
      GL11.glRotatef((int)((System.currentTimeMillis() - GameTime.getStartMillis()) / 200L) * 360 / 8, 0.0F, 0.0F, 1.0F);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex2f(-42.0F, -42.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(42.0F, -42.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(42.0F, 42.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(-42.0F, 42.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
      FontRenderer.setFontFamily(FontFamily.ECCENTRIC);
      GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
      FontRenderer.draw(Display.getWidth() - 84 - 42 - 10 - FontRenderer.getTextWidth(text, 0.6F), Display.getHeight() - 84 - 10, text, 0.6F);
   }
}
