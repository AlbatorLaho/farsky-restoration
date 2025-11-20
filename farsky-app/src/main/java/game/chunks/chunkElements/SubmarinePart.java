package game.chunks.chunkElements;

import game.collision.AABB;
import game.gui.InteractionHint;
import game.input.InputManager;
import game.manager.GameScene;
import game.manager.GameTime;
import game.manager.Loading;
import game.manager.TextureManager;
import game.shader.Shaders;
import game.submarine.SubmarineHud;
import game.submarine.SubmarinePiece;
import game.util.Point;
import game.util.UnitQuad;
import game.world.structure.GamePlayElmt;
import game.world.structure.GamePlayType;

import org.lwjgl.opengl.GL11;

public final class SubmarinePart extends ChunkElement {
   private boolean collected = false;
   private Point surfaceNormal;
   private boolean inSight;
   private AABB interactionBounds;
   private float glowFade = 1.0F;
   private SubmarinePiece piece;
   private int chunkX;
   private int chunkZ;

   public SubmarinePart(SubmarinePiece piece, Point pos, Point aabbPos, Point normalDir, int chunkX, int chunkZ) {
      this.piece = piece;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.position = pos.plus(0.0F, 5.0F, 0.0F);
      this.surfaceNormal = normalDir.plus(0.0F, 1.0F, 0.0F);
      this.surfaceNormal.normalize();
      this.interactionBounds = new AABB(aabbPos, 40.0F, 40.0F, 40.0F);
   }

   @Override
   public final void update(float deltaTime) {
      if (GameScene.avatar != null && this.piece != null) {
         this.inSight = this.interactionBounds.isInPlayerSight(new Point(), 40.0F);
         if (this.inSight && !this.collected) {
            InteractionHint.setInteractionTarget(InputManager.getKeyName("Interaction") + " - Take the piece of submarine", null);
            this.collected = GameScene.avatar.isInteractPressed();
         }

         if (this.collected) {
            GameScene.avatar.collectSubmarinePiece(this.piece);
            SubmarineHud.onPieceFound(this.piece);
            this.piece = null;
            Loading.worldManager.setGamePlayElmtAt(new GamePlayElmt(GamePlayType.NONE), this.chunkX, this.chunkZ);
            this.collected = false;
            return;
         }
      } else {
         if (this.glowFade > 0.0F) {
            this.glowFade -= deltaTime;
            return;
         }

         this.glowFade = 0.0F;
      }
   }

   @Override
   public final void renderTransparent() {
      if (this.glowFade > 0.0F) {
         float glowScale = 100.0F * this.glowFade;
         GL11.glDepthMask(false);
         GL11.glDisable(GL11.GL_CULL_FACE);
         Shaders.setUniform("emissive", true);
         GL11.glPushMatrix();
         GL11.glTranslatef(this.position.x, this.position.y, this.position.z);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.shine);
         GL11.glScalef(glowScale, glowScale, glowScale);

         for (int i = 0; i < 12; i++) {
            GL11.glRotatef(i * 360 / 10, 0.38F, 0.51F, 0.45F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)(Math.cos(GameTime.elapsedMillis / 250.0F + i * 7.564F) * 0.3F + 0.7F));
            UnitQuad.render();
         }

         GL11.glPopMatrix();
         GL11.glDepthMask(true);
         GL11.glEnable(GL11.GL_CULL_FACE);
         Shaders.setUniform("emissive", false);
      }
   }

   @Override
   public final AABB getBoundingBox() {
      return this.interactionBounds;
   }
}
