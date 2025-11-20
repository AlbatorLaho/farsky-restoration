package game.sounds;

import game.Main;
import game.util.Point;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public final class SoundManager {
   public static int musicAwakening;
   public static int musicFarsky;
   public static int musicKraken;
   public static int musicWithTheSeaAsACage;
   public static int musicTimeDoesntFlow;
   public static int musicIntoColdDarkness;
   public static int musicDarkCrushingDark;
   public static int musicLurker;
   public static int musicUnderwaterSunrise;
   public static int ambientBackground;
   public static int ambientTension;
   public static int ambientUnderwater;
   public static int sfxEnemyScream;
   public static int sfxClick;
   public static int sfxOctopusNoise;
   public static int sfxEnemyScream2;
   public static int sfxGroundExploder;
   public static int sfxHover;
   public static int sfxCapaCharged;
   public static int sfxBass;
   public static int sfxFloorExplosion;
   public static int sfxHeartbeat;
   public static int sfxHurt;
   public static int sfxHarpoon;
   public static int sfxChestOpening;
   public static int sfxChestClosing;
   public static int sfxItem;
   public static int sfxWallFalling;
   public static int sfxOutFromGround;
   public static int sfxTouched;
   public static int sfxSpear;
   public static int sfxAir;
   public static int sfxHurtFloor;
   public static int sfxSynaps;
   public static int sfxKnife;
   public static int sfxCrack;
   public static int sfxMovement;
   public static int sfxCoralHide;
   public static int sfxSteps;
   public static int sfxInWater;
   public static int sfxVesselCollision;
   public static int sfxEngine;
   public static int sfxBuild;
   public static int sfxRisingFromFloor;
   public static int sfxBreathing;
   public static int sfxGrab;
   public static int sfxSwim;
   public static int sfxRadio;
   public static int sfxDrill;
   public static int sfxStepsWater;
   public static int sfxWaterLeak;
   public static int sfxKrakenDie;
   public static int sfxDoorOpen;
   public static int sfxDoorClose;
   public static int sfxNewPiece;
   public static int sfxCinematic;
   public static int sfxScooter;
   public static int sfxWaves;
   public static int sfxWhale;
   public static int sfxLightning;
   public static int sfxMoney;
   public static int sfxPowerOn;
   public static int sfxMoneySpent;
   public static int sfxDolphin;
   private static ArrayList<Integer> bufferIds = new ArrayList<>();
   private static ArrayList<SoundSource> transientSources = new ArrayList<>();
   private static Hashtable<Integer, SoundSource> loopingSources = new Hashtable<>();
   private static int nextSourceId = 0;
   public static float sfxVolume = 1.0F;
   public static float ambientVolume = 1.0F;
   public static float musicVolume = 1.0F;

   public static void setListenerState(Point pos, Point velocity, Point forward, Point up) {
      FloatBuffer posBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{pos.x, pos.y, pos.z}).rewind();
      FloatBuffer velocityBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{velocity.x, velocity.y, velocity.z}).rewind();
      FloatBuffer orientationBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[]{forward.x, forward.y, forward.z, up.x, up.y, up.z}).rewind();
      AL10.alListener(AL10.AL_POSITION, posBuffer);
      AL10.alListener(AL10.AL_VELOCITY, velocityBuffer);
      AL10.alListener(AL10.AL_ORIENTATION, orientationBuffer);
      String context = "listener";
      int error = AL10.alGetError();
      if (Main.isVerbose && error != 0) {
         System.out.println("SoundManager Error while setting " + context + ", error: " + error);
      }
   }

   public static void destroy() {
      for (int i = 0; i < bufferIds.size(); i++) {
         AL10.alDeleteBuffers(bufferIds.get(i));
      }

      bufferIds = null;
      AL.destroy();
   }

   public static void update(float delta) {
      ChunkLayer.update(delta);

      for (int i = transientSources.size() - 1; i >= 0; i--) {
         if (!transientSources.get(i).isPlaying()) {
            transientSources.get(i).destroy();
            transientSources.remove(i);
         }
      }
   }

   public static void pauseAll() {
      for (int i = 0; i < transientSources.size(); i++) {
         transientSources.get(i).pause();
      }

      for (int id : loopingSources.keySet()) {
         loopingSources.get(id).pause();
      }
   }

   public static void resumeAll() {
      for (int i = 0; i < transientSources.size(); i++) {
         transientSources.get(i).resume();
      }

      for (int id : loopingSources.keySet()) {
         loopingSources.get(id).resume();
      }
   }

   public static void refreshVolumes() {
      for (int i = 0; i < transientSources.size(); i++) {
         transientSources.get(i).applyVolume();
      }

      for (int id : loopingSources.keySet()) {
         loopingSources.get(id).applyVolume();
      }

      ChunkLayer.stopAll();
   }

   public static void stopAll() {
      for (int i = 0; i < transientSources.size(); i++) {
         transientSources.get(i).destroy();
      }

      for (int id : loopingSources.keySet()) {
         loopingSources.get(id).stop();
      }

      transientSources.clear();
   }

   public static void playSound(int bufferId, Point pos, float pitch) {
      playSound(bufferId, pos, pitch, 1.0F);
   }

   public static void playSound(int bufferId, Point pos, float pitch, float volume) {
      if (pos == null) {
         transientSources.add(new SoundSource(bufferId));
      } else {
         transientSources.add(new SoundSource(bufferId, pos));
      }

      transientSources.get(transientSources.size() - 1).setPitch(pitch);
      transientSources.get(transientSources.size() - 1).setVolume(volume);
      transientSources.get(transientSources.size() - 1).play();
   }

   public static int addLoopingSource(int bufferId, Point pos) {
      nextSourceId++;
      if (pos == null) {
         loopingSources.put(nextSourceId, new SoundSource(bufferId));
      } else {
         loopingSources.put(nextSourceId, new SoundSource(bufferId, pos));
      }

      loopingSources.get(nextSourceId).setLooping(true);
      setLoopingSourceVolume(nextSourceId, 1.0F);
      return nextSourceId;
   }

   public static boolean isTransientSoundPlaying(int bufferId) {
      for (int i = 0; i < transientSources.size(); i++) {
         if (transientSources.get(i).getBufferId() == bufferId) {
            return transientSources.get(i).isPlaying();
         }
      }

      return false;
   }

   public static int loadSound(String filename) {
      try {
         Audio audio = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(filename));
         if (Main.isVerbose) {
            System.out.println("SoundManager id: " + audio.getBufferID() + ", for file: " + filename);
         }

         bufferIds.add(audio.getBufferID());
         return audio.getBufferID();
      } catch (IOException e) {
         e.printStackTrace();
         return 0;
      }
   }

   public static void playLoopingSource(int sourceId) {
      if (loopingSources.get(sourceId) != null) {
         loopingSources.get(sourceId).play();
      }
   }

   public static void pauseLoopingSource(int sourceId) {
      if (loopingSources.get(sourceId) != null) {
         loopingSources.get(sourceId).pause();
      }
   }

   public static void stopLoopingSource(int sourceId) {
      if (loopingSources.get(sourceId) != null) {
         loopingSources.get(sourceId).stop();
      }
   }

   public static boolean isLoopingSourcePlaying(int sourceId) {
      return loopingSources.get(sourceId) != null ? loopingSources.get(sourceId).isPlaying() : false;
   }

   public static void setLoopingSourcePosition(int sourceId, Point pos) {
      if (loopingSources.get(sourceId) != null) {
         loopingSources.get(sourceId).setPosition(pos);
      }
   }

   public static void setLoopingSourceVolume(int sourceId, float volume) {
      if (loopingSources.get(sourceId) != null) {
         loopingSources.get(sourceId).setVolume(volume * sfxVolume);
      }
   }

   public static void setLoopingSourcePitch(int sourceId, float pitch) {
      if (loopingSources.get(sourceId) != null) {
         loopingSources.get(sourceId).setPitch(pitch);
      }
   }

   public static void removeLoopingSource(int sourceId) {
      if (loopingSources.get(sourceId) != null) {
         loopingSources.get(sourceId).destroy();
         loopingSources.remove(sourceId);
      }
   }
}
