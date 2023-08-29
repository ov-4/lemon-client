package dev.lemonclient.addon.utils.world;

import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.misc.Vec3dInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class CityUtils {
   public static class_2338 getBreakPos(class_1657 target) {
      if (!EntityInfo.notNull(target)) {
         return null;
      } else {
         ArrayList blockArray = new ArrayList();
         class_2338 tPos = EntityInfo.getBlockPos(target);
         class_2350[] var3 = class_2350.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            class_2350 dir = var3[var5];
            if (!dir.equals(class_2350.field_11036) && !dir.equals(class_2350.field_11033) && BlockInfo.isCombatBlock(tPos.method_10093(dir))) {
               blockArray.add(tPos.method_10093(dir));
            }
         }

         if (blockArray.isEmpty()) {
            return null;
         } else {
            class_2338 prevBP = (class_2338)blockArray.get(0);
            blockArray.removeIf(CityUtils::isOurSurround);
            if (blockArray.isEmpty()) {
               blockArray.add(prevBP);
            }

            blockArray.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
            return (class_2338)blockArray.get(0);
         }
      }
   }

   public static class_2338 getCrystalPos(class_2338 breakPos, boolean support) {
      if (BlockInfo.notNull(breakPos)) {
         return null;
      } else {
         ArrayList blockPos = new ArrayList();
         class_2350[] var3 = class_2350.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            class_2350 direction = var3[var5];
            if (!direction.equals(class_2350.field_11036) && !direction.equals(class_2350.field_11033) && canCrystal(breakPos.method_10093(direction), support)) {
               blockPos.add(breakPos.method_10093(direction));
            }
         }

         blockPos.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
         return blockPos.isEmpty() ? null : ((class_2338)blockPos.get(0)).method_10074();
      }
   }

   private static boolean canCrystal(class_2338 blockPos, boolean support) {
      Iterator var2 = MeteorClient.mc.field_1687.method_18112().iterator();

      class_1297 entity;
      do {
         do {
            if (!var2.hasNext()) {
               boolean var10000;
               label61: {
                  if (MeteorClient.mc.field_1687.method_8320(blockPos).method_27852(class_2246.field_10124)) {
                     if (support) {
                        if (MeteorClient.mc.field_1687.method_8320(blockPos.method_10074()).method_27852(class_2246.field_10124) || MeteorClient.mc.field_1687.method_8320(blockPos.method_10074()).method_27852(class_2246.field_10540) || MeteorClient.mc.field_1687.method_8320(blockPos.method_10074()).method_27852(class_2246.field_9987)) {
                           break label61;
                        }
                     } else if (MeteorClient.mc.field_1687.method_8320(blockPos.method_10074()).method_27852(class_2246.field_10540) || MeteorClient.mc.field_1687.method_8320(blockPos.method_10074()).method_27852(class_2246.field_9987)) {
                        break label61;
                     }
                  }

                  var10000 = false;
                  return var10000;
               }

               var10000 = true;
               return var10000;
            }

            entity = (class_1297)var2.next();
         } while(!(entity instanceof class_1657) && !(entity instanceof class_1511) && !(entity instanceof class_1542));
      } while(!EntityInfo.getBlockPos(entity).equals(blockPos));

      return false;
   }

   public static boolean isOurSurround(class_2338 blockPos) {
      class_2338 pos = MeteorClient.mc.field_1724.method_24515();
      class_2350[] var2 = class_2350.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         class_2350 dir = var2[var4];
         if (dir != class_2350.field_11036 && dir != class_2350.field_11033 && pos.method_10093(dir).equals(blockPos)) {
            return true;
         }
      }

      return false;
   }

   public static class_2350 getDirection(class_2338 pos) {
      if (pos == null) {
         return class_2350.field_11036;
      } else {
         class_243 eyesPos = Vec3dInfo.getEyeVec(MeteorClient.mc.field_1724);
         class_2350[] var2 = class_2350.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            class_2350 direction = var2[var4];
            class_3959 raycastContext = new class_3959(eyesPos, new class_243((double)pos.method_10263() + 0.5 + (double)direction.method_10163().method_10263() * 0.5, (double)pos.method_10264() + 0.5 + (double)direction.method_10163().method_10264() * 0.5, (double)pos.method_10260() + 0.5 + (double)direction.method_10163().method_10260() * 0.5), class_3960.field_17558, class_242.field_1348, MeteorClient.mc.field_1724);
            class_3965 result = MeteorClient.mc.field_1687.method_17742(raycastContext);
            if (result != null && result.method_17783() == class_240.field_1332 && result.method_17777().equals(pos)) {
               return direction;
            }
         }

         if ((double)pos.method_10264() > eyesPos.field_1351) {
            return class_2350.field_11033;
         } else {
            return class_2350.field_11036;
         }
      }
   }
}
