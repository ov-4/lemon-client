package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;

public class TickShift extends LemonModule {
   private final SettingGroup sgGeneral;
   public final Setting smooth;
   public final Setting packets;
   private final Setting timer;
   public int unSent;
   private boolean lastTimer;
   private boolean lastMoving;
   private final Timer timerModule;

   public TickShift() {
      super(LemonClient.Combat, "Tick Shift", "Stores packets when standing still and uses them when you start moving.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.smooth = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Smoothness")).description(".")).defaultValue(TickShift.SmoothMode.Exponent)).build());
      this.packets = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Packets")).description("How many packets to store for later use.")).defaultValue(50)).min(0).sliderRange(0, 100).build());
      this.timer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Timer")).description("How many packets to send every movement tick.")).defaultValue(2.0).min(1.0).sliderRange(0.0, 10.0).build());
      this.unSent = 0;
      this.lastTimer = false;
      this.lastMoving = false;
      this.timerModule = (Timer)Modules.get().get(Timer.class);
   }

   public void onActivate() {
      super.onActivate();
      this.unSent = 0;
   }

   public void onDeactivate() {
      super.onDeactivate();
      if (this.lastTimer) {
         this.lastTimer = false;
         this.timerModule.setOverride(1.0);
      }

   }

   public String getInfoString() {
      return String.valueOf(this.unSent);
   }

   @EventHandler
   private void onTick(TickEvent.Pre e) {
      if (this.unSent > 0 && this.lastMoving) {
         this.lastMoving = false;
         this.lastTimer = true;
         this.timerModule.setOverride(this.getTimer());
      } else if (this.lastTimer) {
         this.lastTimer = false;
         this.timerModule.setOverride(1.0);
      }

   }

   @EventHandler
   private void onMove(PlayerMoveEvent e) {
      if (e.movement.method_1033() > 0.0 && (!(e.movement.method_1033() > 0.0784) || !(e.movement.method_1033() < 0.0785))) {
         this.unSent = Math.max(0, this.unSent - 1);
         this.lastMoving = true;
      }

   }

   private double getTimer() {
      if (this.smooth.get() == TickShift.SmoothMode.Disabled) {
         return (Double)this.timer.get();
      } else {
         double progress = (double)(1.0F - (float)this.unSent / (float)(Integer)this.packets.get());
         if (this.smooth.get() == TickShift.SmoothMode.Exponent) {
            progress *= progress * progress * progress * progress;
         }

         return 1.0 + ((Double)this.timer.get() - 1.0) * (1.0 - progress);
      }
   }

   public static enum SmoothMode {
      Disabled,
      Normal,
      Exponent;

      // $FF: synthetic method
      private static SmoothMode[] $values() {
         return new SmoothMode[]{Disabled, Normal, Exponent};
      }
   }
}
