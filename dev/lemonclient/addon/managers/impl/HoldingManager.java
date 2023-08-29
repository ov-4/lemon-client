package dev.lemonclient.addon.managers.impl;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2596;
import net.minecraft.class_2868;

public class HoldingManager {
   public int slot;
   public long modifyStartTime = 0L;

   public HoldingManager() {
      MeteorClient.EVENT_BUS.subscribe(this);
      this.slot = 0;
   }

   @EventHandler(
      priority = 200
   )
   private void onSend(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2868 packet) {
         if (packet.method_12442() >= 0 && packet.method_12442() <= 8) {
            this.slot = packet.method_12442();
         }
      }

   }

   public class_1799 getStack() {
      return MeteorClient.mc.field_1724 == null ? null : MeteorClient.mc.field_1724.method_31548().method_5438(this.slot);
   }

   public int getSlot() {
      return this.slot;
   }

   public boolean isHolding(class_1792... items) {
      class_1799 stack = this.getStack();
      if (stack == null) {
         return false;
      } else {
         class_1792[] var3 = items;
         int var4 = items.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            class_1792 item = var3[var5];
            if (item.equals(stack.method_7909())) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isHolding(class_1792 item) {
      class_1799 stack = this.getStack();
      return stack == null ? false : stack.method_7909().equals(item);
   }
}
