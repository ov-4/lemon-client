package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.managers.Managers;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2596;
import net.minecraft.class_2868;
import net.minecraft.class_2886;

public class StrictNoSlow extends LemonModule {
   private final SettingGroup sgGeneral;
   public final Setting onlyGap;
   public final Setting single;
   public final Setting delay;
   private int timer;

   public StrictNoSlow() {
      super(LemonClient.Misc, "Strict No Slow", "Should only be used on very strict servers. Requires any other noslow to work.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.onlyGap = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Only Gapples")).description("Only sends packets when eating gapples.")).defaultValue(true)).build());
      this.single = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Single Packet")).description("Only sends 1 switch packet after starting to eat. Works on most servers that require this module.")).defaultValue(true)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Delay")).description("Tick delay between switch packets.")).defaultValue(0)).min(0).sliderRange(0, 20).visible(() -> {
         return !(Boolean)this.single.get();
      })).build());
      this.timer = 0;
   }

   @EventHandler
   private void onSend(PacketEvent.Sent event) {
      if (this.mc.field_1724 != null) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2886) {
            class_2886 packet = (class_2886)var3;
            if (this.shouldSend(packet.method_12551() == class_1268.field_5808 ? Managers.HOLDING.getStack() : this.mc.field_1724.method_6079())) {
               this.send();
               this.timer = 0;
            }
         }
      }

   }

   @EventHandler
   private void onMove(PlayerMoveEvent event) {
      ++this.timer;
      if (this.timer > (Integer)this.delay.get() && !(Boolean)this.single.get()) {
         this.send();
         this.timer = 0;
      }

   }

   private void send() {
      this.sendPacket(new class_2868(Managers.HOLDING.slot));
   }

   private boolean shouldSend(class_1799 stack) {
      return this.mc.field_1724 != null && ((Boolean)this.onlyGap.get() || stack != null && !stack.method_7960() && stack.method_7909() == class_1802.field_8367 || stack.method_7909() == class_1802.field_8463);
   }
}
