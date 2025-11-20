package game.world;

import game.chunks.Chunk;
import java.util.ArrayList;

public final class WorldDataQueue {
   private ArrayList<Chunk> queue = new ArrayList<>();

   public final synchronized void enqueue(ArrayList<Chunk> items) {
      this.queue.addAll(items);
      this.notify();
   }

   public final synchronized ArrayList<Chunk> drain() {
      ArrayList<Chunk> items = new ArrayList<>(this.queue);
      this.queue.clear();
      return items;
   }
}
