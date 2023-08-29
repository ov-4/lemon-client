package dev.lemonclient.addon.utils.others;

public class Task {
   private boolean called = false;

   public void run(Runnable task) {
      if (!this.isCalled()) {
         task.run();
         this.setCalled();
      }

   }

   public void run(Runnable task, int times) {
      if (!this.isCalled()) {
         int i;
         for(i = 0; i < times; ++i) {
            task.run();
         }

         if (i >= times) {
            this.setCalled();
         }
      }

   }

   public void reset() {
      this.called = false;
   }

   public boolean isCalled() {
      return this.called;
   }

   public void setCalled() {
      this.called = true;
   }
}
