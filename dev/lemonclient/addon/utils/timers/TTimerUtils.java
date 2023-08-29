package dev.lemonclient.addon.utils.timers;

public class TTimerUtils {
   private final long current = System.currentTimeMillis();
   private long time = this.getCurrentTime();

   protected final long getCurrentTime() {
      return System.currentTimeMillis();
   }

   public final long getTime() {
      return this.time;
   }

   protected final void setTime(long l2) {
      this.time = l2;
   }

   public boolean passed(long ms) {
      return System.currentTimeMillis() - this.time >= ms;
   }

   public boolean passed(double ms) {
      return (double)(System.currentTimeMillis() - this.time) >= ms;
   }

   public long convertToNS(long time) {
      return time * 1000000L;
   }

   public void setMs(long ms) {
      this.time = System.nanoTime() - this.convertToNS(ms);
   }

   public boolean tickAndReset(long ms) {
      if (System.currentTimeMillis() - this.time >= ms) {
         this.reset();
         return true;
      } else {
         return false;
      }
   }

   public void reset() {
      this.time = System.currentTimeMillis();
   }

   public boolean hasReached(long var1) {
      return System.currentTimeMillis() - this.current >= var1;
   }

   public boolean hasReached(long var1, boolean var3) {
      if (var3) {
         this.reset();
      }

      return System.currentTimeMillis() - this.current >= var1;
   }

   public boolean passedS(double s) {
      return this.passedMs((long)s * 1000L);
   }

   public boolean passedDms(double dms) {
      return this.passedMs((long)dms * 10L);
   }

   public boolean passedDs(double ds) {
      return this.passedMs((long)ds * 100L);
   }

   public boolean passedMs(long ms) {
      return System.currentTimeMillis() - this.time >= ms;
   }

   public long timePassed(long n) {
      return System.currentTimeMillis() - n;
   }

   public long getPassedTimeMs() {
      return System.currentTimeMillis() - this.time;
   }

   public final boolean passedTicks(int ticks) {
      return this.passed(ticks * 50);
   }

   public void resetTimeSkipTo(long p_MS) {
      this.time = System.currentTimeMillis() + p_MS;
   }

   public boolean passed(float ms) {
      return (float)(System.currentTimeMillis() - this.time) >= ms;
   }

   public boolean passed(int ms) {
      return System.currentTimeMillis() - this.time >= (long)ms;
   }
}
