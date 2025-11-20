package game.inventory;

import java.io.Serializable;
import org.lwjgl.opengl.GL11;

public class Item implements Serializable {
   private static final long serialVersionUID = -8699750089667028951L;
   private ItemType type;
   private int count;
   private float param = 1.0F;
   private transient boolean isNew = false;

   public Item(ItemType type) {
      this(type, 1);
   }

   public Item(ItemType type, int count) {
      this.type = type;
      this.count = count;
   }

   public final void draw() {
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(this.type.getXOffset() / 10.0F + 0.01F, this.type.getYOffset() / 20.0F);
      GL11.glVertex2f(-27.0F, -27.0F);
      GL11.glTexCoord2f(this.type.getXOffset() / 10.0F + 0.01F, (this.type.getYOffset() + 1) / 20.0F);
      GL11.glVertex2f(-27.0F, 27.0F);
      GL11.glTexCoord2f((this.type.getXOffset() + 1) / 10.0F, (this.type.getYOffset() + 1) / 20.0F);
      GL11.glVertex2f(27.0F, 27.0F);
      GL11.glTexCoord2f((this.type.getXOffset() + 1) / 10.0F, this.type.getYOffset() / 20.0F);
      GL11.glVertex2f(27.0F, -27.0F);
      GL11.glEnd();
   }

   public final ItemType getType() {
      return this.type;
   }

   public final int getCount() {
      return this.count;
   }

   public final void consume() {
      this.count--;
   }

   public final int addCount(int amount) {
      int prev = this.count;
      this.count += amount;
      if (this.count > 75) {
         this.count = 75;
      }

      return prev + amount - this.count;
   }

   public final void setCount(int count) {
      this.count = count;
   }

   public final void setParam(float value) {
      this.param = value;
   }

   public final float getParam() {
      return this.param;
   }

   public final void setIsNew(boolean isNew) {
      this.isNew = isNew;
   }

   public final boolean isNew() {
      return this.isNew;
   }
}
