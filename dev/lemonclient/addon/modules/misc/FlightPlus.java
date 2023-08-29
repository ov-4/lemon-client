package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;

public class FlightPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting flyMode;
   private final Setting useTimer;
   private final Setting timer;
   private final Setting speed;
   private final Setting ySpeed;
   private final Setting antiKickDelay;
   private final Setting antiKickAmount;
   private final Setting keepY;
   private final Setting glideAmount;
   private double startY;
   private int tick;

   public FlightPlus() {
      super(LemonClient.Misc, "Flight+", "KasumsSoft Flight.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.flyMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Flight Mode")).description("Method of flying.")).defaultValue(FlightPlus.FlightMode.Momentum)).build());
      this.useTimer = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Use Timer")).description("Should we use timer.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = new DoubleSetting.Builder();
      Setting var10003 = this.useTimer;
      Objects.requireNonNull(var10003);
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)var10002.visible(var10003::get)).name("Timer")).description("How many times more packets should be sent.")).defaultValue(1.088).min(0.0).sliderMax(10.0);
      var10003 = this.useTimer;
      Objects.requireNonNull(var10003);
      this.timer = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.speed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Speed")).description("How many blocks should be moved each tick.")).defaultValue(0.6).min(0.0).sliderMax(10.0).visible(() -> {
         return this.flyMode.get() == FlightPlus.FlightMode.Momentum;
      })).build());
      this.ySpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Y Speed")).description("DA Y SPEEDOS.")).defaultValue(0.5).min(0.0).sliderMax(10.0).visible(() -> {
         return this.flyMode.get() == FlightPlus.FlightMode.Momentum;
      })).build());
      this.antiKickDelay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Anti-Kick Delay")).description("How many ticks should be waited between antikick packets.")).defaultValue(10.0).min(0.0).sliderMax(100.0).visible(() -> {
         return this.flyMode.get() == FlightPlus.FlightMode.Momentum;
      })).build());
      this.antiKickAmount = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Anti-Kick Amount")).description("How much to move down.")).defaultValue(1.0).min(0.0).sliderMax(10.0).visible(() -> {
         return this.flyMode.get() == FlightPlus.FlightMode.Momentum;
      })).build());
      this.keepY = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("KeepY")).description("Should we try to keep the same y level when jump flying.")).defaultValue(true)).visible(() -> {
         return this.flyMode.get() == FlightPlus.FlightMode.Jump;
      })).build());
      this.glideAmount = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Glide amount")).description("How much to glide down.")).defaultValue(0.2).min(0.0).sliderMax(1.0).visible(() -> {
         return this.flyMode.get() == FlightPlus.FlightMode.Glide;
      })).build());
      this.startY = 0.0;
      this.tick = 0;
   }

   public void onActivate() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.startY = this.mc.field_1724.method_23318();
         ((Timer)Modules.get().get(Timer.class)).setOverride((Double)this.timer.get());
      }

   }

   @EventHandler
   private void onMove(PlayerMoveEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         double[] result = this.getYaw((double)this.mc.field_1724.field_3913.field_3905, (double)this.mc.field_1724.field_3913.field_3907);
         float yaw = (float)result[0] + 90.0F;
         double x = 0.0;
         double y = (double)this.tick % (Double)this.antiKickDelay.get() == 0.0 ? (Double)this.antiKickAmount.get() * -0.04 : 0.0;
         double z = 0.0;
         if (((FlightMode)this.flyMode.get()).equals(FlightPlus.FlightMode.Momentum)) {
            if (this.mc.field_1690.field_1903.method_1434() && y == 0.0) {
               y = (Double)this.ySpeed.get();
            } else if (this.mc.field_1690.field_1832.method_1434()) {
               y = -(Double)this.ySpeed.get();
            }

            if (result[1] == 1.0) {
               x = Math.cos(Math.toRadians((double)yaw)) * (Double)this.speed.get();
               z = Math.sin(Math.toRadians((double)yaw)) * (Double)this.speed.get();
            }

            ((IVec3d)event.movement).set(x, y, z);
         }

         if (((FlightMode)this.flyMode.get()).equals(FlightPlus.FlightMode.Jump)) {
            if (this.mc.field_1690.field_1903.method_1436()) {
               this.mc.field_1724.method_6043();
               this.startY += 0.4;
            }

            if (this.mc.field_1690.field_1832.method_1436() && !this.mc.field_1690.field_1832.method_1434()) {
               this.startY = this.mc.field_1724.method_23318();
            }

            if ((Boolean)this.keepY.get() && this.mc.field_1724.method_23318() <= this.startY && !this.mc.field_1690.field_1832.method_1434()) {
               this.mc.field_1724.method_6043();
            }

            if (result[1] == 1.0) {
               x = Math.cos(Math.toRadians((double)yaw)) * (Double)this.speed.get();
               z = Math.sin(Math.toRadians((double)yaw)) * (Double)this.speed.get();
            }

            ((IVec3d)event.movement).setXZ(x, z);
         }

         if (((FlightMode)this.flyMode.get()).equals(FlightPlus.FlightMode.Glide) && !this.mc.field_1724.method_24828()) {
            ((IVec3d)event.movement).setY(-(Double)this.glideAmount.get());
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ++this.tick;
   }

   public void onDeactivate() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         ((Timer)Modules.get().get(Timer.class)).setOverride(1.0);
      }

   }

   private double[] getYaw(double f, double s) {
      double yaw = (double)this.mc.field_1724.method_36454();
      double move;
      if (f > 0.0) {
         move = 1.0;
         yaw += s > 0.0 ? -45.0 : (s < 0.0 ? 45.0 : 0.0);
      } else if (f < 0.0) {
         move = 1.0;
         yaw += s > 0.0 ? -135.0 : (s < 0.0 ? 135.0 : 180.0);
      } else {
         move = s != 0.0 ? 1.0 : 0.0;
         yaw += s > 0.0 ? -90.0 : (s < 0.0 ? 90.0 : 0.0);
      }

      return new double[]{yaw, move};
   }

   public static enum FlightMode {
      Momentum,
      Jump,
      Glide;

      // $FF: synthetic method
      private static FlightMode[] $values() {
         return new FlightMode[]{Momentum, Jump, Glide};
      }
   }
}
