package dev.lemonclient.addon.utils.player;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class RotationUtils {
   public static float nextYaw(double current, double target, double step) {
      double i = yawAngle(current, target);
      return step >= Math.abs(i) ? (float)(current + i) : (float)(current + (double)(i < 0.0 ? -1 : 1) * step);
   }

   public static double yawAngle(double current, double target) {
      double c = class_3532.method_15338(current) + 180.0;
      double t = class_3532.method_15338(target) + 180.0;
      if (c > t) {
         return t + 360.0 - c < Math.abs(c - t) ? 360.0 - c + t : t - c;
      } else {
         return 360.0 - t + c < Math.abs(c - t) ? -(360.0 - t + c) : t - c;
      }
   }

   public static float nextPitch(double current, double target, double step) {
      double i = target - current;
      return (float)(Math.abs(i) <= step ? target : (i >= 0.0 ? current + step : current - step));
   }

   public static double radAngle(class_241 vec1, class_241 vec2) {
      double p = (double)(vec1.field_1343 * vec2.field_1343 + vec1.field_1342 * vec2.field_1342);
      p /= Math.sqrt((double)(vec1.field_1343 * vec1.field_1343 + vec1.field_1342 * vec1.field_1342));
      p /= Math.sqrt((double)(vec2.field_1343 * vec2.field_1343 + vec2.field_1342 * vec2.field_1342));
      return Math.acos(p);
   }

   public static double getYaw(class_243 start, class_243 target) {
      return (double)(MeteorClient.mc.field_1724.method_36454() + class_3532.method_15393((float)Math.toDegrees(Math.atan2(target.method_10215() - start.method_10215(), target.method_10216() - start.method_10216())) - 90.0F - MeteorClient.mc.field_1724.method_36454()));
   }

   public static double getPitch(class_243 start, class_243 target) {
      double diffX = target.method_10216() - start.method_10216();
      double diffY = target.method_10214() - start.method_10214();
      double diffZ = target.method_10215() - start.method_10215();
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      return (double)(MeteorClient.mc.field_1724.method_36455() + class_3532.method_15393((float)(-Math.toDegrees(Math.atan2(diffY, diffXZ))) - MeteorClient.mc.field_1724.method_36455()));
   }
}
