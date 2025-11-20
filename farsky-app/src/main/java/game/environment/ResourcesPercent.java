package game.environment;

import game.inventory.ItemType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class ResourcesPercent implements Serializable {
   private static final long serialVersionUID = 6085568626539920284L;
   private ArrayList<Resource> resources;

   public ResourcesPercent(ArrayList<Resource> list) {
      this.resources = list;
   }

   public final ItemType pickRandom() {
      float roll = (float)Math.random() * 100.0F;
      ItemType result = this.resources.get(0).itemType;

      for (int i = 0; i < this.resources.size(); i++) {
         if (roll <= this.resources.get(i).percent) {
            result = this.resources.get(i).itemType;
            break;
         }

         roll -= this.resources.get(i).percent;
      }

      return result;
   }

   private Resource getResource(int index) {
      return this.resources.get(index);
   }

   public final ItemType getItemType(int index) {
      return this.resources.get(index).itemType;
   }

   public final int getPercent(int index) {
      return this.resources.get(index).percent;
   }

   public final int size() {
      return this.resources.size();
   }

   public final ResourcesPercent scaled(float factor) {
      ArrayList<Resource> scaled = new ArrayList<>();

      for (int i = 0; i < this.resources.size(); i++) {
         scaled.add(new Resource(this.resources.get(i).itemType, (int)(this.resources.get(i).percent * factor)));
      }

      return new ResourcesPercent(scaled);
   }

   public final ResourcesPercent merged(ResourcesPercent other) {
      ArrayList<Resource> merged = new ArrayList<>();

      for (int i = 0; i < this.resources.size(); i++) {
         merged.add(new Resource(this.resources.get(i).itemType, this.resources.get(i).percent));
      }

      for (int j = 0; j < other.resources.size(); j++) {
         boolean isNew = true;

         for (int k = 0; k < merged.size(); k++) {
            if (merged.get(k).itemType == other.getItemType(j)) {
               isNew = false;
               Resource existing = merged.get(k);
               existing.percent = existing.percent + other.getResource(j).percent;
            }
         }

         if (isNew) {
            merged.add(other.getResource(j));
         }
      }

      return new ResourcesPercent(merged);
   }

   public final void normalize() {
      while (this.resources.size() > 4) {
         int minIdx = 0;

         for (int i = 0; i < this.resources.size(); i++) {
            if (this.resources.get(i).percent < this.resources.get(minIdx).percent) {
               minIdx = i;
            }
         }

         this.resources.remove(minIdx);
      }

      int total = 0;

      for (int i = 0; i < this.resources.size(); i++) {
         total += this.resources.get(i).percent;
      }

      for (int i = 0; i < this.resources.size(); i++) {
         this.resources.get(i).percent = (int)(this.resources.get(i).percent * 100.0F / total);
      }

      total = 0;

      for (int i = 0; i < this.resources.size(); i++) {
         total += this.resources.get(i).percent;
      }

      this.resources.get(0).percent += 100 - total;

      for (int i = 0; i < this.resources.size(); i++) {
         for (int j = 1; j < this.resources.size(); j++) {
            if (this.resources.get(j).percent > this.resources.get(j - 1).percent) {
               Collections.swap(this.resources, j, j - 1);
            }
         }
      }

      for (int i = this.resources.size() - 1; i >= 0; i--) {
         if (this.resources.get(i).percent == 0) {
            this.resources.remove(i);
         }
      }
   }
}
