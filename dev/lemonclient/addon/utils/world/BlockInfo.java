package dev.lemonclient.addon.utils.world;

import dev.lemonclient.addon.utils.entity.EntityInfo;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1292;
import net.minecraft.class_1294;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3486;
import net.minecraft.class_3532;
import net.minecraft.class_3611;

public class BlockInfo {
   public static double progress = 0.0;
   private static final ArrayList blocks = new ArrayList();

   public static class_265 getShape(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_26218(MeteorClient.mc.field_1687, block);
   }

   public static class_238 getBox(class_2338 block) {
      return getShape(block).method_1107();
   }

   public static class_2248 getBlock(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_26204();
   }

   public static boolean isAir(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_26215();
   }

   public static float getBlastResistance(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_26204().method_9520();
   }

   public static float getBlastResistance(class_2248 block) {
      return block.method_9520();
   }

   public static boolean canBreak(int slot, class_2338 blockPos) {
      if (progress >= 1.0) {
         return true;
      } else {
         class_2680 blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
         if (progress < 1.0) {
            progress += getBreakDelta(slot != 420 ? slot : MeteorClient.mc.field_1724.method_31548().field_7545, blockState);
         }

         return false;
      }
   }

   public static boolean isReplaceable(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_45474();
   }

   public static boolean isSolid(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_51367();
   }

   public static boolean isBurnable(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_50011();
   }

   public static boolean isLiquid(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_51176();
   }

   public static float getHardness(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_26214(MeteorClient.mc.field_1687, block);
   }

   public static float getHardness(class_2248 block) {
      return block.method_36555();
   }

   public static boolean isBlastResist(class_2338 block) {
      return getBlastResistance(block) >= 600.0F;
   }

   public static boolean isBlastResist(class_2248 block) {
      return getBlastResistance(block) >= 600.0F;
   }

   public static boolean isBreakable(class_2338 pos) {
      return getHardness(pos) > 0.0F;
   }

   public static boolean isBreakable(class_2248 block) {
      return getHardness(block) > 0.0F;
   }

   public static boolean isCombatBlock(class_2338 block) {
      return isBlastResist(block) && isBreakable(block);
   }

   public static boolean isCombatBlock(class_2248 block) {
      return isBlastResist(block) && isBreakable(block);
   }

   public static class_243 getCenterVec3d(class_2338 block) {
      return new class_243((double)block.method_10263() + 0.5, (double)block.method_10264() + 0.5, (double)block.method_10260() + 0.5);
   }

   public static boolean notNull(class_2338 block) {
      return block != null;
   }

   public static boolean isNull(class_2338 block) {
      return block == null;
   }

   public static boolean isWithinRange(class_2338 block, double range) {
      return MeteorClient.mc.field_1724.method_24515().method_19771(block, range);
   }

   public static boolean isFullCube(class_2338 block) {
      return MeteorClient.mc.field_1687.method_8320(block).method_26234(MeteorClient.mc.field_1687, block);
   }

   public static double getBreakDelta(int slot, class_2680 state) {
      float hardness = state.method_26214((class_1922)null, (class_2338)null);
      return hardness == -1.0F ? 0.0 : getBlockBreakingSpeed(slot, state) / (double)hardness / (double)(state.method_29291() && !((class_1799)MeteorClient.mc.field_1724.method_31548().field_7547.get(slot)).method_7951(state) ? 100 : 30);
   }

   public static boolean canPlace(class_2338 pos, boolean breakCrystal, double safeHealth) {
      if (pos == null) {
         return false;
      } else if (!class_1937.method_25953(pos)) {
         return false;
      } else if (!MeteorClient.mc.field_1687.method_8320(pos).method_45474()) {
         return false;
      } else {
         return !EntityInfo.checkEntity(pos, breakCrystal, safeHealth);
      }
   }

   private static double getBlockBreakingSpeed(int slot, class_2680 block) {
      double speed = (double)((class_1799)MeteorClient.mc.field_1724.method_31548().field_7547.get(slot)).method_7924(block);
      if (speed > 1.0) {
         class_1799 tool = MeteorClient.mc.field_1724.method_31548().method_5438(slot);
         int efficiency = class_1890.method_8225(class_1893.field_9131, tool);
         if (efficiency > 0 && !tool.method_7960()) {
            speed += (double)(efficiency * efficiency + 1);
         }
      }

      if (class_1292.method_5576(MeteorClient.mc.field_1724)) {
         speed *= (double)(1.0F + (float)(class_1292.method_5575(MeteorClient.mc.field_1724) + 1) * 0.2F);
      }

      if (MeteorClient.mc.field_1724.method_6059(class_1294.field_5901)) {
         float var10000;
         switch (MeteorClient.mc.field_1724.method_6112(class_1294.field_5901).method_5578()) {
            case 0:
               var10000 = 0.3F;
               break;
            case 1:
               var10000 = 0.09F;
               break;
            case 2:
               var10000 = 0.0027F;
               break;
            default:
               var10000 = 8.1E-4F;
         }

         float k = var10000;
         speed *= (double)k;
      }

      if (MeteorClient.mc.field_1724.method_5777(class_3486.field_15517) && !class_1890.method_8200(MeteorClient.mc.field_1724)) {
         speed /= 5.0;
      }

      if (!MeteorClient.mc.field_1724.method_24828()) {
         speed /= 5.0;
      }

      return speed;
   }

   public static int getBlockBreakingSpeed(class_2680 block, class_2338 pos, int slot) {
      class_1657 player = MeteorClient.mc.field_1724;
      float f = player.method_31548().method_5438(slot).method_7924(block);
      if (f > 1.0F) {
         int i = (Integer)class_1890.method_8222(player.method_31548().method_5438(slot)).getOrDefault(class_1893.field_9131, 0);
         if (i > 0) {
            f += (float)(i * i + 1);
         }
      }

      if (class_1292.method_5576(player)) {
         f *= 1.0F + (float)(class_1292.method_5575(player) + 1) * 0.2F;
      }

      float t;
      if (player.method_6059(class_1294.field_5901)) {
         float var10000;
         switch (player.method_6112(class_1294.field_5901).method_5578()) {
            case 0:
               var10000 = 0.3F;
               break;
            case 1:
               var10000 = 0.09F;
               break;
            case 2:
               var10000 = 0.0027F;
               break;
            default:
               var10000 = 8.1E-4F;
         }

         t = var10000;
         f *= t;
      }

      if (player.method_5777(class_3486.field_15517) && !class_1890.method_8200(player)) {
         f /= 5.0F;
      }

      if (!player.method_24828()) {
         f /= 5.0F;
      }

      t = block.method_26214(MeteorClient.mc.field_1687, pos);
      return t == -1.0F ? 0 : (int)Math.ceil((double)(1.0F / (f / t / 30.0F)));
   }

   public static boolean doesBoxTouchBlock(class_238 box, class_2248 block) {
      for(int x = (int)Math.floor(box.field_1323); (double)x < Math.ceil(box.field_1320); ++x) {
         for(int y = (int)Math.floor(box.field_1322); (double)y < Math.ceil(box.field_1325); ++y) {
            for(int z = (int)Math.floor(box.field_1321); (double)z < Math.ceil(box.field_1324); ++z) {
               if (MeteorClient.mc.field_1687.method_8320(new class_2338(x, y, z)).method_26204() == block) {
                  return true;
               }
            }
         }
      }

      return false;
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

   private static class_243 closestVec3d(class_238 box) {
      if (box == null) {
         return new class_243(0.0, 0.0, 0.0);
      } else {
         class_243 eyePos = new class_243(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318() + (double)MeteorClient.mc.field_1724.method_18381(MeteorClient.mc.field_1724.method_18376()), MeteorClient.mc.field_1724.method_23321());
         double x = class_3532.method_15350(eyePos.method_10216(), box.field_1323, box.field_1320);
         double y = class_3532.method_15350(eyePos.method_10214(), box.field_1322, box.field_1325);
         double z = class_3532.method_15350(eyePos.method_10215(), box.field_1321, box.field_1324);
         return new class_243(x, y, z);
      }
   }

   public static class_243 closestVec3d2(class_2338 pos) {
      return closestVec3d(box(pos));
   }

   public static List getSphere(class_2338 centerPos, int radius, int height) {
      blocks.clear();

      for(int i = centerPos.method_10263() - radius; i < centerPos.method_10263() + radius; ++i) {
         for(int j = centerPos.method_10264() - height; j < centerPos.method_10264() + height; ++j) {
            for(int k = centerPos.method_10260() - radius; k < centerPos.method_10260() + radius; ++k) {
               class_2338 pos = new class_2338(i, j, k);
               if (distanceBetween(centerPos, pos) <= (double)radius && !blocks.contains(pos)) {
                  blocks.add(pos);
               }
            }
         }
      }

      return blocks;
   }

   public static List getSphere(class_2338 centerPos, double radius, double height) {
      ArrayList blocks = new ArrayList();

      for(int i = centerPos.method_10263() - (int)radius; (double)i < (double)centerPos.method_10263() + radius; ++i) {
         for(int j = centerPos.method_10264() - (int)height; (double)j < (double)centerPos.method_10264() + height; ++j) {
            for(int k = centerPos.method_10260() - (int)radius; (double)k < (double)centerPos.method_10260() + radius; ++k) {
               class_2338 pos = new class_2338(i, j, k);
               if (distanceTo(centerPos, pos) <= radius && !blocks.contains(pos)) {
                  blocks.add(pos);
               }
            }
         }
      }

      return blocks;
   }

   public static double distanceBetween(class_2338 blockPos1, class_2338 blockPos2) {
      double d = (double)(blockPos1.method_10263() - blockPos2.method_10263());
      double e = (double)(blockPos1.method_10264() - blockPos2.method_10264());
      double f = (double)(blockPos1.method_10260() - blockPos2.method_10260());
      return (double)class_3532.method_15355((float)(d * d + e * e + f * f));
   }

   public static class_238 box(class_2338 blockPos) {
      return new class_238((double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260(), (double)(blockPos.method_10263() + 1), (double)(blockPos.method_10264() + 1), (double)(blockPos.method_10260() + 1));
   }

   public static double distanceTo(class_2338 pos) {
      class_243 eyePos = new class_243(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318() + (double)MeteorClient.mc.field_1724.method_18381(MeteorClient.mc.field_1724.method_18376()), MeteorClient.mc.field_1724.method_23321());
      float f = (float)(eyePos.method_10216() - closestVec3d2(pos).field_1352);
      float g = (float)(eyePos.method_10214() - closestVec3d2(pos).field_1351);
      float h = (float)(eyePos.method_10215() - closestVec3d2(pos).field_1350);
      return (double)class_3532.method_15355(f * f + g * g + h * h);
   }

   public static double distanceTo(class_2338 blockPos1, class_2338 blockPos2) {
      double d = (double)(blockPos1.method_10263() - blockPos2.method_10263());
      double e = (double)(blockPos1.method_10264() - blockPos2.method_10264());
      double f = (double)(blockPos1.method_10260() - blockPos2.method_10260());
      return (double)class_3532.method_15355((float)(d * d + e * e + f * f));
   }

   public static double distanceTo(double x, double y, double z) {
      class_243 eyePos = new class_243(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318() + (double)MeteorClient.mc.field_1724.method_18381(MeteorClient.mc.field_1724.method_18376()), MeteorClient.mc.field_1724.method_23321());
      class_243 vec3d = closestVec3d(class_238.method_29968(new class_243(x, y, z)));
      float f = (float)(eyePos.method_10216() - vec3d.field_1352);
      float g = (float)(eyePos.method_10214() - vec3d.field_1351);
      float h = (float)(eyePos.method_10215() - vec3d.field_1350);
      return (double)class_3532.method_15355(f * f + g * g + h * h);
   }

   public static class_2338 roundBlockPos(class_243 vec) {
      return new class_2338((int)vec.field_1352, (int)Math.round(vec.field_1351), (int)vec.field_1350);
   }

   public static void state(class_2248 block, class_2338 pos) {
      MeteorClient.mc.field_1687.method_8501(pos, block.method_9564());
   }

   public static boolean of(class_2248 block, class_2338 pos) {
      return MeteorClient.mc.field_1687.method_8320(pos).method_27852(block);
   }

   public boolean of(Class klass, class_2338 pos) {
      return klass.isInstance(MeteorClient.mc.field_1687.method_8320(pos).method_26204());
   }

   public boolean of(class_3611 fluid, class_2338 pos) {
      return MeteorClient.mc.field_1687.method_8320(pos).method_26227().method_39360(fluid);
   }

   public static class_2338 getBlockPos(class_2338 blockPos) {
      return new class_2338(blockPos);
   }
}
