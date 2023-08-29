package dev.lemonclient.addon.utils.timers;

public final class MSTimer {
   private long time = -1L;
   private boolean loaded = false;

   public boolean hasTimePassed(long MS) {
      return System.currentTimeMillis() >= this.time + MS;
   }

   public long hasTimeLeft(long MS) {
      return MS + this.time - System.currentTimeMillis();
   }

   public void reset() {
      this.time = System.currentTimeMillis();
      this.loaded = true;
   }

   public void init() {
      if (!this.loaded) {
         this.reset();
      }

   }
}
