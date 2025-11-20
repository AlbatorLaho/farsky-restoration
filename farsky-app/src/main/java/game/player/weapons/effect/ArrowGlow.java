package game.player.weapons.effect;

import game.util.UnitQuad;
import org.lwjgl.opengl.GL11;

public final class ArrowGlow {
   private float offset = -((float)Math.random());
   private float rotation = (float)Math.random() * 360.0F;
   private float lifetime = (float)Math.random();

   public final void update(float delta) {
      this.lifetime -= delta;
      if (this.lifetime < 0.0F) {
         this.lifetime = 1.0F;
         this.offset = -((float)Math.random());
      }
   }

   public final void render() {
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.0F, this.offset);
      GL11.glRotatef(this.rotation, 0.0F, 0.0F, 1.0F);
      GL11.glScalef(1.0F - this.lifetime, 1.0F - this.lifetime, 1.0F - this.lifetime);
      GL11.glColor4f(1.0F, 1.0F, 0.8F, this.lifetime);
      UnitQuad.render();
      GL11.glPopMatrix();
   }
}
