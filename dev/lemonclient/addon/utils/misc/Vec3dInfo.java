package dev.lemonclient.addon.utils.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.hwid.Hwid;
import java.util.Base64;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class Vec3dInfo {
   public static boolean isInRange(class_243 vec3d, double radius) {
      return vec3d.method_24802(vec3d, radius);
   }

   public static boolean isWithinRange(class_243 vec3d, double range) {
      return MeteorClient.mc.field_1724.method_24515().method_19769(vec3d, range);
   }

   public static class_243 add(class_243 vec3d, class_243 added) {
      return new class_243(vec3d.method_1019(added).method_10216(), vec3d.method_1019(added).method_10214(), vec3d.method_1019(added).method_10215());
   }

   public static class_243 add(class_243 vec3d, double x, double y, double z) {
      return new class_243(vec3d.method_1031(x, y, z).method_10216(), vec3d.method_1031(x, y, z).method_10214(), vec3d.method_1031(x, y, z).method_10215());
   }

   public static boolean notNull(class_243 vec3d) {
      return vec3d != null;
   }

   public static class_243 getEyeVec(class_1657 entity) {
      return entity.method_19538().method_1031(0.0, (double)entity.method_18381(entity.method_18376()), 0.0);
   }

   public static class_243 closestVec3d(class_2338 blockPos) {
      if (blockPos == null) {
         return new class_243(0.0, 0.0, 0.0);
      } else {
         double x = class_3532.method_15350(MeteorClient.mc.field_1724.method_23317() - (double)blockPos.method_10263(), 0.0, 1.0);
         double y = class_3532.method_15350(MeteorClient.mc.field_1724.method_23318() - (double)blockPos.method_10264(), 0.0, 0.6);
         double z = class_3532.method_15350(MeteorClient.mc.field_1724.method_23321() - (double)blockPos.method_10260(), 0.0, 1.0);
         return new class_243((double)blockPos.method_10263() + x, (double)blockPos.method_10264() + y, (double)blockPos.method_10260() + z);
      }
   }

   public static void init() {
      if (!Hwid.getBoolean()) {
         String var10000 = new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBmYWlsZWQhIEhXSUQ6IA=="));
         LemonClient.log(var10000 + Hwid.getValue());
         System.exit(-114514);
      } else {
         LemonClient.log(new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBzdWNjZXNzZnVsISBXZWxjb21lIHRvIHVzZSBMZW1vbkNsaWVudCE=")));
      }

   }
}
