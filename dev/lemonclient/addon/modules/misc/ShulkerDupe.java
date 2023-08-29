package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1713;
import net.minecraft.class_1810;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_2846;
import net.minecraft.class_3965;
import net.minecraft.class_495;
import net.minecraft.class_2846.class_2847;

public class ShulkerDupe extends LemonModule {
   private final SettingGroup sgAutoTool;
   private final Setting autoT;
   public static boolean shouldDupe;
   public static boolean shouldDupeAll;
   private boolean timerWASon;

   public ShulkerDupe() {
      super(LemonClient.Misc, "Shulker Dupe", "ShulkerDupe only works in vanilla, forge, and fabric servers version 1.19 and below.");
      this.sgAutoTool = this.settings.createGroup("AutoTool");
      this.autoT = this.sgAutoTool.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("UsePickaxeWhenDupe")).description("Uses Pickaxe when breaking shulker.")).defaultValue(true)).build());
      this.timerWASon = false;
   }

   public void onActivate() {
      this.timerWASon = false;
      shouldDupeAll = false;
      shouldDupe = false;
   }

   @EventHandler
   private void onScreenOpen(OpenScreenEvent event) {
      if (event.screen instanceof class_495) {
         shouldDupeAll = false;
         shouldDupe = false;
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (shouldDupe | shouldDupeAll) {
         if (((Timer)Modules.get().get(Timer.class)).isActive()) {
            this.timerWASon = true;
            ((Timer)Modules.get().get(Timer.class)).toggle();
         }

         for(int i = 0; i < 8; ++i) {
            if ((Boolean)this.autoT.get() && (this.mc.field_1724.method_31548().method_5438(0).method_7909() instanceof class_1810 || this.mc.field_1724.method_31548().method_5438(1).method_7909() instanceof class_1810 || this.mc.field_1724.method_31548().method_5438(2).method_7909() instanceof class_1810 || this.mc.field_1724.method_31548().method_5438(3).method_7909() instanceof class_1810 || this.mc.field_1724.method_31548().method_5438(4).method_7909() instanceof class_1810 || this.mc.field_1724.method_31548().method_5438(5).method_7909() instanceof class_1810 || this.mc.field_1724.method_31548().method_5438(6).method_7909() instanceof class_1810 || this.mc.field_1724.method_31548().method_5438(7).method_7909() instanceof class_1810 || this.mc.field_1724.method_31548().method_5438(8).method_7909() instanceof class_1810) && !(this.mc.field_1724.method_31548().method_7391().method_7909() instanceof class_1810)) {
               ++this.mc.field_1724.method_31548().field_7545;
               if (this.mc.field_1724.method_31548().field_7545 > 8) {
                  this.mc.field_1724.method_31548().field_7545 = 0;
               }
            }
         }
      } else if (!shouldDupe | !shouldDupeAll && !((Timer)Modules.get().get(Timer.class)).isActive() && this.timerWASon) {
         this.timerWASon = false;
         ((Timer)Modules.get().get(Timer.class)).toggle();
      }

   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1755 instanceof class_495 && this.mc.field_1724 != null) {
         class_239 wow = this.mc.field_1765;
         class_3965 a = (class_3965)wow;
         if (shouldDupe | shouldDupeAll) {
            this.mc.field_1761.method_2902(a.method_17777(), class_2350.field_11033);
         }
      }

   }

   @EventHandler
   public void onSendPacket(PacketEvent.Sent event) {
      if (event.packet instanceof class_2846) {
         if (shouldDupeAll) {
            if (((class_2846)event.packet).method_12363() == class_2847.field_12973) {
               for(int i = 0; i < 27; ++i) {
                  this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, i, 0, class_1713.field_7794, this.mc.field_1724);
               }

               shouldDupeAll = false;
            }
         } else if (shouldDupe && ((class_2846)event.packet).method_12363() == class_2847.field_12973) {
            this.mc.field_1761.method_2906(this.mc.field_1724.field_7512.field_7763, 0, 0, class_1713.field_7794, this.mc.field_1724);
            shouldDupe = false;
         }
      }

   }
}
