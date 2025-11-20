package game.sounds;

import game.Main;
import game.util.Point;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

public final class SoundSource {
   private int bufferId;
   private int sourceId;
   private boolean fadingOut = false;
   private boolean fadingIn = false;
   private SoundType soundType = SoundType.SFX;
   private float volume = 1.0F;
   private float maxVolume = 1.0F;

   public SoundSource(int bufferId) {
      this.bufferId = bufferId;
      this.sourceId = this.createSource();
      this.initSourceParams();
      this.setVolume(1.0F);
      AL10.alSourcei(this.sourceId, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
   }

   public SoundSource(int bufferId, Point pos) {
      this.bufferId = bufferId;
      this.sourceId = this.createSource();
      this.initSourceParams();
      this.setVolume(1.0F);
      this.setPosition(pos);
   }

   public final void update(float delta) {
      if (this.fadingOut) {
         this.volume = Math.max(0.0F, this.volume - this.maxVolume * delta / 1.5F);
         this.setVolume(this.volume);
         if (this.volume == 0.0F) {
            this.pause();
            this.fadingOut = false;
         }
      }

      if (this.fadingIn) {
         this.volume = Math.min(this.maxVolume, this.volume + this.maxVolume * delta / 1.5F);
         this.setVolume(this.volume);
         if (this.volume == this.maxVolume) {
            this.fadingIn = false;
         }
      }
   }

   private int createSource() {
      IntBuffer sourceBuffer = BufferUtils.createIntBuffer(1);
      AL10.alGenSources(sourceBuffer);
      this.checkAlError("source");
      return sourceBuffer.get(0);
   }

   private void initSourceParams() {
      AL10.alSourcei(this.sourceId, AL10.AL_BUFFER, this.bufferId);
      AL10.alSourcef(this.sourceId, AL10.AL_PITCH, 1.0F);
      AL10.alSourcef(this.sourceId, AL10.AL_MIN_GAIN, 0.0F);
      AL10.alSourcef(this.sourceId, AL10.AL_MAX_GAIN, 1.0F);
      AL10.alSourcef(this.sourceId, AL10.AL_REFERENCE_DISTANCE, 50.0F);
      AL10.alSourcef(this.sourceId, AL10.AL_ROLLOFF_FACTOR, 1.0F);
      AL10.alSourcef(this.sourceId, AL10.AL_MAX_DISTANCE, 999999.0F);
      this.checkAlError("parameters");
   }

   public final void setSoundType(SoundType type) {
      this.soundType = type;
   }

   public final void setVolume(float vol) {
      if (vol < 0.0F) {
         vol = 0.0F;
      }

      if (vol > this.maxVolume) {
         vol = this.maxVolume;
      }

      switch (this.soundType) {
         case SFX:
            AL10.alSourcef(this.sourceId, AL10.AL_GAIN, vol * SoundManager.sfxVolume);
            this.checkAlError("gain to" + vol * SoundManager.sfxVolume);
            break;
         case AMBIENT:
            AL10.alSourcef(this.sourceId, AL10.AL_GAIN, vol * SoundManager.ambientVolume);
            this.checkAlError("gain to" + vol * SoundManager.ambientVolume);
            break;
         case MUSIC:
            AL10.alSourcef(this.sourceId, AL10.AL_GAIN, vol * SoundManager.musicVolume);
            this.checkAlError("gain to" + vol * SoundManager.musicVolume);
      }

      this.volume = vol;
   }

   public final void applyVolume() {
      this.setVolume(this.volume);
   }

   public final void setMaxVolume(float vol) {
      this.maxVolume = vol;
      this.setVolume(vol);
   }

   public final void setPitch(float pitch) {
      AL10.alSourcef(this.sourceId, AL10.AL_PITCH, pitch);
      this.checkAlError("pitch to " + pitch);
   }

   public final void setPosition(Point pos) {
      FloatBuffer posBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{pos.x, pos.y, pos.z}).rewind();
      AL10.alSource(this.sourceId, AL10.AL_POSITION, posBuffer);
      this.checkAlError("position to " + pos);
   }

   public final void setLooping(boolean loop) {
      AL10.alSourcei(this.sourceId, AL10.AL_LOOPING, AL10.AL_TRUE);
      AL10.alSourceStop(this.sourceId);
   }

   public final void play() {
      AL10.alSourcePlay(this.sourceId);
   }

   public final void pause() {
      if (this.isPlaying()) {
         AL10.alSourcePause(this.sourceId);
      }
   }

   public final void resume() {
      if (AL10.alGetSourcei(this.sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED) {
         AL10.alSourcePlay(this.sourceId);
      }
   }

   public final void stop() {
      AL10.alSourceStop(this.sourceId);
   }

   public final void startFadeOut(boolean immediate) {
      this.fadingOut = true;
      this.fadingIn = false;
   }

   public final void startFadeIn(boolean immediate) {
      this.fadingIn = true;
      this.fadingOut = false;
   }

   public final void destroy() {
      AL10.alSourceStop(this.sourceId);
      AL10.alDeleteSources(this.sourceId);
      this.checkAlError("delete buffer");
   }

   public final boolean isPlaying() {
      return AL10.alGetSourcei(this.sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
   }

   private void checkAlError(String context) {
      int error = AL10.alGetError();
      if (Main.isVerbose && error != 0) {
         System.out.println("SoundSource id " + this.sourceId + " Error while setting " + context + ", error: " + error);
      }
   }

   public final int getBufferId() {
      return this.bufferId;
   }
}
