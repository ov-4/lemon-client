package dev.lemonclient.addon.utils.network;

import dev.lemonclient.addon.utils.others.Task;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.class_1268;
import net.minecraft.class_1292;
import net.minecraft.class_1294;
import net.minecraft.class_1799;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3486;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;

public class PacketUtils {
   private double progress = 0.0;
   private class_2338 blockPos;

   public static void start(class_2338 pos) {
      MeteorClient.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, pos, class_2350.field_11036));
   }

   public static void stop(class_2338 pos) {
      MeteorClient.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, pos, class_2350.field_11036));
   }

   public static void abort(class_2338 pos) {
      MeteorClient.mc.method_1562().method_2883(new class_2846(class_2847.field_12971, pos, class_2350.field_11036));
   }

   public static void startPacketMine(class_2338 blockpos, boolean clientSwing) {
      start(blockpos);
      if (clientSwing) {
         MeteorClient.mc.field_1724.method_6104(class_1268.field_5808);
      } else {
         MeteorClient.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
      }

      stop(blockpos);
   }

   public static void abortPacketMine(class_2338 blockpos) {
      abort(blockpos);
   }

   public static void packetPlace(class_2338 pos, FindItemResult slot, boolean rotate, boolean clientSwing) {
      if (pos != null) {
         if (rotate) {
            Rotations.rotate(Rotations.getYaw(pos), Rotations.getPitch(pos));
         }

         class_3965 result = new class_3965(Utils.vec3d(pos), class_2350.field_11033, pos, true);
         int prevSlot = MeteorClient.mc.field_1724.method_31548().field_7545;
         InvUtils.swap(slot.slot(), false);
         MeteorClient.mc.method_1562().method_2883(new class_2885(class_1268.field_5808, result, 0));
         MeteorClient.mc.field_1724.method_31548().field_7545 = prevSlot;
         if (clientSwing) {
            MeteorClient.mc.field_1724.method_6104(class_1268.field_5808);
         } else {
            MeteorClient.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
         }
      }

   }

   public PacketUtils(class_2338 pos) {
      this.blockPos = pos;
      this.progress = 0.0;
   }

   public PacketUtils() {
      this.progress = 0.0;
   }

   public void reset() {
      this.progress = 0.0;
   }

   public double getProgress() {
      return this.progress;
   }

   public class_2338 getBlockPos() {
      return this.blockPos;
   }

   public boolean isReady() {
      return this.progress >= 1.0;
   }

   public boolean isReadyOn(double var) {
      return this.progress >= var;
   }

   public void setProgress(double progress) {
      this.progress = progress;
   }

   public void setBlockPos(class_2338 blockPos) {
      this.blockPos = blockPos;
   }

   public void mine(class_2338 blockPos, Task task) {
      task.run(() -> {
         MeteorClient.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, blockPos, class_2350.field_11036));
         MeteorClient.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, class_2350.field_11036));
      });
      class_2680 blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
      double bestScore = -1.0;
      int bestSlot = -1;

      for(int i = 0; i < 9; ++i) {
         double score = (double)MeteorClient.mc.field_1724.method_31548().method_5438(i).method_7924(blockState);
         if (score > bestScore) {
            bestScore = score;
            bestSlot = i;
         }
      }

      this.progress += this.getBreakDelta(bestSlot != -1 ? bestSlot : MeteorClient.mc.field_1724.method_31548().field_7545, blockState);
   }

   public void abortMining(class_2338 blockPos) {
      MeteorClient.mc.method_1562().method_2883(new class_2846(class_2847.field_12971, blockPos, class_2350.field_11036));
   }

   private double getBreakDelta(int slot, class_2680 state) {
      float hardness = state.method_26214((class_1922)null, (class_2338)null);
      return hardness == -1.0F ? 0.0 : this.getBlockBreakingSpeed(slot, state) / (double)hardness / (double)(state.method_29291() && !((class_1799)MeteorClient.mc.field_1724.method_31548().field_7547.get(slot)).method_7951(state) ? 100 : 30);
   }

   private double getBlockBreakingSpeed(int slot, class_2680 block) {
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
}
