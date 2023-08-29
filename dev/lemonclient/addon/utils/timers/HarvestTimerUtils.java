package dev.lemonclient.addon.utils.timers;

public class HarvestTimerUtils {
   private long nanoTime = -1L;

   public void reset() {
      this.nanoTime = System.nanoTime();
   }

   public void setTicks(long ticks) {
      this.nanoTime = System.nanoTime() - this.convertTicksToNano(ticks);
   }

   public void setMillis(long time) {
      this.nanoTime = System.nanoTime() - this.convertMillisToNano(time);
   }

   public void setSec(long time) {
      this.nanoTime = System.nanoTime() - this.convertSecToNano(time);
   }

   public long getTicks() {
      return this.convertNanoToTicks(this.nanoTime);
   }

   public long getMillis() {
      return this.convertNanoToMillis(this.nanoTime);
   }

   public long getSec() {
      return this.convertNanoToSec(this.nanoTime);
   }

   public boolean passedTicks(long ticks) {
      return this.passedNano(this.convertTicksToNano(ticks));
   }

   public boolean passedNano(long time) {
      return System.nanoTime() - this.nanoTime >= time;
   }

   public boolean passedMillis(long time) {
      return this.passedNano(this.convertMillisToNano(time));
   }

   public boolean passedSec(long time) {
      return this.passedNano(this.convertSecToNano(time));
   }

   public long convertMillisToTicks(long time) {
      return time / 50L;
   }

   public long convertTicksToMillis(long ticks) {
      return ticks * 50L;
   }

   public long convertNanoToTicks(long time) {
      return this.convertMillisToTicks(this.convertNanoToMillis(time));
   }

   public long convertTicksToNano(long ticks) {
      return this.convertMillisToNano(this.convertTicksToMillis(ticks));
   }

   public long convertSecToMillis(long time) {
      return time * 1000L;
   }

   public long convertSecToMicro(long time) {
      return this.convertMillisToMicro(this.convertSecToMillis(time));
   }

   public long convertSecToNano(long time) {
      return this.convertMicroToNano(this.convertMillisToMicro(this.convertSecToMillis(time)));
   }

   public long convertMillisToMicro(long time) {
      return time * 1000L;
   }

   public long convertMillisToNano(long time) {
      return this.convertMicroToNano(this.convertMillisToMicro(time));
   }

   public long convertMicroToNano(long time) {
      return time * 1000L;
   }

   public long convertNanoToMicro(long time) {
      return time / 1000L;
   }

   public long convertNanoToMillis(long time) {
      return this.convertMicroToMillis(this.convertNanoToMicro(time));
   }

   public long convertNanoToSec(long time) {
      return this.convertMillisToSec(this.convertMicroToMillis(this.convertNanoToMicro(time)));
   }

   public long convertMicroToMillis(long time) {
      return time / 1000L;
   }

   public long convertMicroToSec(long time) {
      return this.convertMillisToSec(this.convertMicroToMillis(time));
   }

   public long convertMillisToSec(long time) {
      return time / 1000L;
   }
}
