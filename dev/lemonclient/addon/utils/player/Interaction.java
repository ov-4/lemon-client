package dev.lemonclient.addon.utils.player;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import net.minecraft.class_1268;
import net.minecraft.class_1511;
import net.minecraft.class_1541;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3965;

public class Interaction {
   private static int prevSlot = -1;

   public static void updateSlot(FindItemResult result, boolean packet) {
      updateSlot(result.slot(), packet);
   }

   public static boolean updateSlot(int slot, boolean packet) {
      if (slot >= 0 && slot <= 8) {
         if (prevSlot == -1) {
            prevSlot = MeteorClient.mc.field_1724.method_31548().field_7545;
         }

         MeteorClient.mc.field_1724.method_31548().field_7545 = slot;
         if (packet) {
            MeteorClient.mc.method_1562().method_2883(new class_2868(slot));
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean swapBack(int slot) {
      return updateSlot(slot, true);
   }

   public static boolean swapBack() {
      if (prevSlot == -1) {
         return false;
      } else {
         boolean return_ = updateSlot(prevSlot, true);
         prevSlot = -1;
         return return_;
      }
   }

   public static void doSwing(SwingHand swingHand, boolean packetSwing, @Nullable class_1268 autoHand) {
      switch (swingHand) {
         case MainHand:
            doSwing(class_1268.field_5808, packetSwing);
            break;
         case OffHand:
            doSwing(class_1268.field_5810, packetSwing);
            break;
         case Auto:
            doSwing(autoHand, packetSwing);
      }

   }

   public static void doSwing(class_1268 hand, boolean packetSwing) {
      if (packetSwing) {
         MeteorClient.mc.method_1562().method_2883(new class_2879(hand));
      } else {
         MeteorClient.mc.field_1724.method_6104(hand);
      }

   }

   public static class_1268 toHand(SwingHand swingHand) {
      class_1268 var10000;
      switch (swingHand) {
         case MainHand:
         case Auto:
            var10000 = class_1268.field_5808;
            break;
         case OffHand:
            var10000 = class_1268.field_5810;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public static void placeBlock(class_1268 hand, class_3965 result, boolean packetPlace) {
      if (packetPlace) {
         MeteorClient.mc.method_1562().method_2883(new class_2885(hand, result, 0));
      } else {
         MeteorClient.mc.field_1761.method_2896(MeteorClient.mc.field_1724, hand, result);
      }

   }

   public static boolean hasEntity(class_238 box) {
      return hasEntity(box, (entity) -> {
         return entity instanceof class_1657 || entity instanceof class_1511 || entity instanceof class_1541;
      });
   }

   public static boolean hasEntity(class_238 box, Predicate predicate) {
      return EntityUtils.intersectsWithEntity(box, predicate);
   }

   public static enum SwingHand {
      MainHand,
      OffHand,
      Auto;

      // $FF: synthetic method
      private static SwingHand[] $values() {
         return new SwingHand[]{MainHand, OffHand, Auto};
      }
   }
}
