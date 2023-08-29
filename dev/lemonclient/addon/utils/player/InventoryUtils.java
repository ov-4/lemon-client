package dev.lemonclient.addon.utils.player;

import dev.lemonclient.addon.managers.Managers;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_2813;
import net.minecraft.class_2838;

public class InventoryUtils {
   private static int[] slots;
   public static int pickSlot = -1;

   public static boolean pickSwitch(int slot) {
      if (slot >= 0) {
         Managers.HOLDING.modifyStartTime = System.currentTimeMillis();
         pickSlot = slot;
         MeteorClient.mc.method_1562().method_2883(new class_2838(slot));
         return true;
      } else {
         return false;
      }
   }

   public static void pickSwapBack() {
      if (pickSlot >= 0) {
         MeteorClient.mc.method_1562().method_2883(new class_2838(pickSlot));
         pickSlot = -1;
      }

   }

   public static boolean invSwitch(int slot) {
      if (slot >= 0) {
         class_1703 handler = MeteorClient.mc.field_1724.field_7512;
         Int2ObjectArrayMap stack = new Int2ObjectArrayMap();
         stack.put(slot, handler.method_7611(slot).method_7677());
         MeteorClient.mc.method_1562().method_2883(new class_2813(handler.field_7763, handler.method_37421(), 36 + Managers.HOLDING.slot, slot, class_1713.field_7791, handler.method_7611(slot).method_7677(), stack));
         ((IClientPlayerInteractionManager)MeteorClient.mc.field_1761).syncSelected();
         slots = new int[]{slot, Managers.HOLDING.slot};
         return true;
      } else {
         return false;
      }
   }

   public static void swapBack() {
      class_1703 handler = MeteorClient.mc.field_1724.field_7512;
      Int2ObjectArrayMap stack = new Int2ObjectArrayMap();
      stack.put(slots[0], handler.method_7611(slots[0]).method_7677());
      MeteorClient.mc.method_1562().method_2883(new class_2813(handler.field_7763, handler.method_37421(), 36 + slots[1], slots[0], class_1713.field_7791, handler.method_7611(slots[0]).method_7677().method_7972(), stack));
      ((IClientPlayerInteractionManager)MeteorClient.mc.field_1761).syncSelected();
   }
}
