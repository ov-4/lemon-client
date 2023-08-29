package dev.lemonclient.addon.utils;

import dev.lemonclient.addon.mixins.IBlockSettings;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2189;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2404;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4770;
import net.minecraft.class_4970.class_2251;

public class LemonUtils {
   public static class_243 getMiddle(class_238 box) {
      return new class_243((box.field_1323 + box.field_1320) / 2.0, (box.field_1322 + box.field_1325) / 2.0, (box.field_1321 + box.field_1324) / 2.0);
   }

   public static boolean inside(class_1657 en, class_238 bb) {
      return MeteorClient.mc.field_1687 != null && MeteorClient.mc.field_1687.method_20812(en, bb).iterator().hasNext();
   }

   public static int closerToZero(int x) {
      return (int)((float)x - Math.signum((float)x));
   }

   public static class_243 getClosest(class_243 pPos, class_243 middle, double width, double height) {
      return new class_243(Math.min(Math.max(pPos.field_1352, middle.field_1352 - width / 2.0), middle.field_1352 + width / 2.0), Math.min(Math.max(pPos.field_1351, middle.field_1351), middle.field_1351 + height), Math.min(Math.max(pPos.field_1350, middle.field_1350 - width / 2.0), middle.field_1350 + width / 2.0));
   }

   public static boolean strictDir(class_2338 pos, class_2350 dir) {
      boolean var10000;
      switch (dir) {
         case field_11033:
            var10000 = MeteorClient.mc.field_1724.method_33571().field_1351 <= (double)pos.method_10264() + 0.5;
            break;
         case field_11036:
            var10000 = MeteorClient.mc.field_1724.method_33571().field_1351 >= (double)pos.method_10264() + 0.5;
            break;
         case field_11043:
            var10000 = MeteorClient.mc.field_1724.method_23321() < (double)pos.method_10260();
            break;
         case field_11035:
            var10000 = MeteorClient.mc.field_1724.method_23321() >= (double)(pos.method_10260() + 1);
            break;
         case field_11039:
            var10000 = MeteorClient.mc.field_1724.method_23317() < (double)pos.method_10263();
            break;
         case field_11034:
            var10000 = MeteorClient.mc.field_1724.method_23317() >= (double)(pos.method_10263() + 1);
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public static class_238 getCrystalBox(class_2338 pos) {
      return new class_238((double)pos.method_10263() - 0.5, (double)pos.method_10264(), (double)pos.method_10260() - 0.5, (double)pos.method_10263() + 1.5, (double)(pos.method_10264() + 2), (double)pos.method_10260() + 1.5);
   }

   public static class_238 getCrystalBox(class_243 pos) {
      return new class_238(pos.method_10216() - 1.0, pos.method_10214(), pos.method_10215() - 1.0, pos.method_10216() + 1.0, pos.method_10214() + 2.0, pos.method_10215() + 1.0);
   }

   public static boolean replaceable(class_2338 block) {
      return ((IBlockSettings)class_2251.method_9630(MeteorClient.mc.field_1687.method_8320(block).method_26204())).replaceable();
   }

   public static boolean solid2(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_51367();
   }

   public static boolean solid(class_2338 block) {
      class_2248 b = MeteorClient.mc.field_1687.method_8320(block).method_26204();
      return !(b instanceof class_4770) && !(b instanceof class_2404) && !(b instanceof class_2189);
   }

   public static boolean isGapple(class_1792 item) {
      return item == class_1802.field_8463 || item == class_1802.field_8367;
   }

   public static boolean isGapple(class_1799 stack) {
      return isGapple(stack.method_7909());
   }

   public static boolean collidable(class_2338 block) {
      return ((AbstractBlockAccessor)MeteorClient.mc.field_1687.method_8320(block).method_26204()).isCollidable();
   }

   public static boolean inFov(class_1297 entity, double fov) {
      if (fov >= 360.0) {
         return true;
      } else {
         float[] angle = PlayerUtils.calculateAngle(entity.method_5829().method_1005());
         double xDist = (double)class_3532.method_15356(angle[0], MeteorClient.mc.field_1724.method_36454());
         double yDist = (double)class_3532.method_15356(angle[1], MeteorClient.mc.field_1724.method_36455());
         double angleDistance = Math.hypot(xDist, yDist);
         return angleDistance <= fov;
      }
   }
}
