package dev.lemonclient.addon.utils.player;

import java.util.ArrayList;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.class_1739;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;

public class CraftUtils {
   public static ArrayList wools = new ArrayList() {
      {
         this.add(class_1802.field_19044);
         this.add(class_1802.field_19045);
         this.add(class_1802.field_19046);
         this.add(class_1802.field_19047);
         this.add(class_1802.field_19048);
         this.add(class_1802.field_19049);
         this.add(class_1802.field_19050);
         this.add(class_1802.field_19051);
         this.add(class_1802.field_19052);
         this.add(class_1802.field_19053);
         this.add(class_1802.field_19054);
         this.add(class_1802.field_19055);
         this.add(class_1802.field_19056);
         this.add(class_1802.field_19057);
         this.add(class_1802.field_19058);
         this.add(class_1802.field_19059);
      }
   };
   public static ArrayList planks = new ArrayList() {
      {
         this.add(class_1802.field_8118);
         this.add(class_1802.field_8113);
         this.add(class_1802.field_8191);
         this.add(class_1802.field_8842);
         this.add(class_1802.field_8651);
         this.add(class_1802.field_8404);
      }
   };

   public static FindItemResult findCraftTable() {
      return InvUtils.findInHotbar(new class_1792[]{class_2246.field_9980.method_8389()});
   }

   public static Integer getEmptySlots() {
      int emptySlots = 0;

      for(int i = 0; i < 36; ++i) {
         class_1799 itemStack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
         if (itemStack == null || itemStack.method_7909() instanceof class_1739) {
            ++emptySlots;
         }
      }

      return emptySlots;
   }

   public static boolean isInventoryFull() {
      for(int i = 0; i < 36; ++i) {
         class_1799 itemStack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
         if (itemStack == null || itemStack.method_7909() instanceof class_1739) {
            return false;
         }
      }

      return true;
   }
}
