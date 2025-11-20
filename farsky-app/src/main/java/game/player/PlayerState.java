package game.player;

import game.sounds.SoundManager;
import game.submarine.SubmarinePiece;
import game.util.Point;
import game.util.State;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayerState implements Serializable {
   private static final long serialVersionUID = 605924655425689560L;
   public State state;
   public float horizontalAngle;
   private float lifeLevel;
   private float oxygenLevel;
   private float starvingLevel;
   private boolean woundedLeg;
   private boolean woundedArm;
   private boolean inside;
   private boolean navigate;
   private boolean alreadyBeenInside;
   public Point lastSafeSpot;
   public float exploded = 0.0F;
   public ArrayList<SubmarinePiece> submarinePieces;
   public transient float verticalAngle;
   public transient boolean isDead;
   public transient Point knockback;
   public transient float starvingCooldown = 0.0F;

   public PlayerState() {
      this.state = new State();
      this.state.player = true;
      this.horizontalAngle = 0.0F;
      this.verticalAngle = 0.0F;
      this.starvingCooldown = 120.0F;
      this.inside = false;
      this.navigate = false;
      this.alreadyBeenInside = false;
      this.lastSafeSpot = new Point();
      this.knockback = new Point();
      this.submarinePieces = new ArrayList<>();
      this.resetStats();
   }

   private void resetStats() {
      this.lifeLevel = 100.0F;
      this.oxygenLevel = 200.0F;
      this.starvingLevel = 100.0F;
      this.starvingCooldown = 120.0F;
      this.woundedLeg = false;
      this.woundedArm = false;
      this.exploded = 0.0F;
      this.isDead = false;
   }

   public final void copyFrom(PlayerState source) {
      this.state.copyFrom(source.state);
      this.horizontalAngle = source.horizontalAngle;
      this.verticalAngle = source.verticalAngle;
      this.lifeLevel = source.lifeLevel;
      this.woundedLeg = source.woundedLeg;
      this.woundedArm = source.woundedArm;
      this.starvingLevel = source.starvingLevel;
      this.inside = source.inside;
      this.navigate = source.navigate;
      this.starvingCooldown = source.starvingCooldown;
      if (source.knockback != null) {
         this.knockback = source.knockback.copy();
      }

      this.submarinePieces.clear();

      for (int i = 0; i < source.submarinePieces.size(); i++) {
         this.submarinePieces.add(source.submarinePieces.get(i));
      }
   }

   public final float getLifeLevel() {
      return this.lifeLevel;
   }

   public final float getOxygenLevel() {
      return this.oxygenLevel;
   }

   public final float getHungerPercent() {
      return this.starvingLevel / 100.0F;
   }

   public final boolean isWoundedArm() {
      return this.woundedArm;
   }

   public final boolean isWoundedLeg() {
      return this.woundedLeg;
   }

   public final void addLifeLevel(float delta) {
      this.lifeLevel += delta;
      if (this.lifeLevel > 100.0F) {
         this.lifeLevel = 100.0F;
      }

      if (this.lifeLevel <= 0.0F) {
         this.lifeLevel = 0.0F;
         this.isDead = true;
      }
   }

   public final void adjustOxygenLevel(float delta, float max) {
      this.oxygenLevel += delta;
      if (this.oxygenLevel > max) {
         this.oxygenLevel = max;
      }

      if (this.oxygenLevel < 0.0F) {
         this.oxygenLevel = 0.0F;
      }
   }

   public final void adjustStarvingLevel(float delta) {
      if (!(this.starvingCooldown > 0.0F) || !(delta < 0.0F)) {
         if (delta > 0.0F) {
            if (delta > 15.0F) {
               this.starvingCooldown += 60.0F;
            } else {
               this.starvingCooldown += 30.0F;
            }

            if (this.starvingCooldown > 120.0F) {
               this.starvingCooldown = 120.0F;
            }
         }

         this.starvingLevel += delta;
         if (this.starvingLevel > 100.0F) {
            this.starvingLevel = 100.0F;
         }

         if (this.starvingLevel < 0.0F) {
            this.starvingLevel = 0.0F;
         }
      }
   }

   public final void setWoundedArm(boolean wounded) {
      this.woundedArm = wounded;
   }

   public final void setWoundedLeg(boolean wounded) {
      this.woundedLeg = wounded;
   }

   public final boolean isInside() {
      return this.inside;
   }

   public final void setInside(boolean inside) {
      if (inside != this.inside) {
         if (inside) {
            SoundManager.playSound(SoundManager.sfxInWater, null, 0.9F, 0.1F);
         } else {
            SoundManager.playSound(SoundManager.sfxInWater, null, 0.8F, 0.2F);
         }
      }

      this.inside = inside;
      if (this.inside) {
         this.alreadyBeenInside = true;
      }
   }

   public final void setLastSafeSpot(Point point) {
      this.lastSafeSpot = point.copy();
   }

   public final boolean isNavigating() {
      return this.navigate;
   }

   public final void setNavigating(boolean navigating) {
      this.navigate = navigating;
   }

   public final void respawn() {
      this.state.pos = this.lastSafeSpot.copy();
      this.inside = this.alreadyBeenInside;
      this.navigate = false;
      this.resetStats();
   }

   public final void addSubmarinePiece(SubmarinePiece piece) {
      this.submarinePieces.add(piece);
   }

   public final boolean hasSubmarinePiece(SubmarinePiece piece) {
      for (int i = 0; i < this.submarinePieces.size(); i++) {
         if (this.submarinePieces.get(i) == piece) {
            return true;
         }
      }

      return false;
   }

   public final int getSubmarinePieceCount() {
      int count = 0;

      SubmarinePiece[] pieces = SubmarinePiece.values();
      for (SubmarinePiece piece : pieces) {
         if (this.hasSubmarinePiece(piece)) {
            count++;
         }
      }

      return count;
   }

   public final boolean hasAllSubmarinePieces() {
      SubmarinePiece[] pieces = SubmarinePiece.values();
      for (SubmarinePiece piece : pieces) {
         if (!this.hasSubmarinePiece(piece)) {
            return false;
         }
      }

      return true;
   }
}
