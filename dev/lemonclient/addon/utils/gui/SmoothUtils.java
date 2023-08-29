package dev.lemonclient.addon.utils.gui;

public class SmoothUtils {
   public static double smoothMove(double start, double end) {
      double speed = (end - start) * 0.1;
      if (speed > 0.0) {
         speed = Math.max(0.1, speed);
         speed = Math.min(end - start, speed);
      } else if (speed < 0.0) {
         speed = Math.min(-0.1, speed);
         speed = Math.max(end - start, speed);
      }

      return start + speed;
   }
}
