package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;

public class ElytraFlyPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSpeed;
   private final Setting mode;
   private final Setting horizontal;
   private final Setting up;
   private final Setting speed;
   private final Setting upMultiplier;
   private final Setting down;
   private final Setting smartFall;
   private final Setting fallSpeed;
   private boolean moving;
   private float yaw;
   private float pitch;
   private float p;
   private double velocity;

   public ElytraFlyPlus() {
      super(LemonClient.Misc, "Elytra Fly+", "Better efly.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSpeed = this.settings.createGroup("Speed");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description(".")).defaultValue(ElytraFlyPlus.Mode.Wasp)).build());
      this.horizontal = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Horizontal Speed")).description("How many blocks to move each tick horizontally.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 5.0).visible(() -> {
         return this.mode.get() == ElytraFlyPlus.Mode.Wasp;
      })).build());
      this.up = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Up Speed")).description("How many blocks to move up each tick.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 5.0).visible(() -> {
         return this.mode.get() == ElytraFlyPlus.Mode.Wasp;
      })).build());
      this.speed = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Speed")).description("How many blocks to move each tick.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 5.0).visible(() -> {
         return this.mode.get() == ElytraFlyPlus.Mode.Control;
      })).build());
      this.upMultiplier = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Up Multiplier")).description("How many times faster should we fly up.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 5.0).visible(() -> {
         return this.mode.get() == ElytraFlyPlus.Mode.Control;
      })).build());
      this.down = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Down Speed")).description("How many blocks to move down each tick.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 5.0).build());
      this.smartFall = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Smart Fall")).description("Only falls down when looking down.")).defaultValue(true)).visible(() -> {
         return this.mode.get() == ElytraFlyPlus.Mode.Wasp;
      })).build());
      this.fallSpeed = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Fall Speed")).description("How many blocks to fall down each tick.")).defaultValue(0.01).min(0.0).sliderRange(0.0, 1.0).build());
   }

   @EventHandler(
      priority = 200
   )
   private void onMove(PlayerMoveEvent event) {
      switch ((Mode)this.mode.get()) {
         case Wasp:
            this.waspTick(event);
            break;
         case Control:
            this.controlTick(event);
      }

   }

   private void waspTick(PlayerMoveEvent event) {
      if (this.mc.field_1724.method_6128()) {
         this.updateWaspMovement();
         this.pitch = this.mc.field_1724.method_36455();
         double cos = Math.cos(Math.toRadians((double)(this.yaw + 90.0F)));
         double sin = Math.sin(Math.toRadians((double)(this.yaw + 90.0F)));
         double x = this.moving ? cos * (Double)this.horizontal.get() : 0.0;
         double y = -(Double)this.fallSpeed.get();
         double z = this.moving ? sin * (Double)this.horizontal.get() : 0.0;
         if ((Boolean)this.smartFall.get()) {
            y *= Math.abs(Math.sin(Math.toRadians((double)this.pitch)));
         }

         if (this.mc.field_1690.field_1832.method_1434() && !this.mc.field_1690.field_1903.method_1434()) {
            y = -(Double)this.down.get();
         }

         if (!this.mc.field_1690.field_1832.method_1434() && this.mc.field_1690.field_1903.method_1434()) {
            y = (Double)this.up.get();
         }

         ((IVec3d)event.movement).set(x, y, z);
         this.mc.field_1724.method_18800(0.0, 0.0, 0.0);
      }
   }

   private void updateWaspMovement() {
      float yaw = this.mc.field_1724.method_36454();
      float f = this.mc.field_1724.field_3913.field_3905;
      float s = this.mc.field_1724.field_3913.field_3907;
      if (f > 0.0F) {
         this.moving = true;
         yaw += s > 0.0F ? -45.0F : (s < 0.0F ? 45.0F : 0.0F);
      } else if (f < 0.0F) {
         this.moving = true;
         yaw += s > 0.0F ? -135.0F : (s < 0.0F ? 135.0F : 180.0F);
      } else {
         this.moving = s != 0.0F;
         yaw += s > 0.0F ? -90.0F : (s < 0.0F ? 90.0F : 0.0F);
      }

      this.yaw = yaw;
   }

   private void controlTick(PlayerMoveEvent event) {
      if (this.mc.field_1724.method_6128()) {
         this.updateControlMovement();
         this.pitch = 0.0F;
         boolean movingUp = false;
         if (!this.mc.field_1690.field_1832.method_1434() && this.mc.field_1690.field_1903.method_1434() && this.velocity > (Double)this.speed.get() * 0.4) {
            this.p = (float)Math.min((double)this.p + 0.1 * (double)(1.0F - this.p) * (double)(1.0F - this.p) * (double)(1.0F - this.p), 1.0);
            this.pitch = Math.max(Math.max(this.p, 0.0F) * -90.0F, -90.0F);
            movingUp = true;
            this.moving = false;
         } else {
            this.velocity = (Double)this.speed.get();
            this.p = -0.2F;
         }

         this.velocity = this.moving ? (Double)this.speed.get() : Math.min(this.velocity + Math.sin(Math.toRadians((double)this.pitch)) * 0.08, (Double)this.speed.get());
         double cos = Math.cos(Math.toRadians((double)(this.yaw + 90.0F)));
         double sin = Math.sin(Math.toRadians((double)(this.yaw + 90.0F)));
         double x = this.moving && !movingUp ? cos * (Double)this.speed.get() : (movingUp ? this.velocity * Math.cos(Math.toRadians((double)this.pitch)) * cos : 0.0);
         double y = this.pitch < 0.0F ? this.velocity * (Double)this.upMultiplier.get() * -Math.sin(Math.toRadians((double)this.pitch)) * this.velocity : -(Double)this.fallSpeed.get();
         double z = this.moving && !movingUp ? sin * (Double)this.speed.get() : (movingUp ? this.velocity * Math.cos(Math.toRadians((double)this.pitch)) * sin : 0.0);
         y *= Math.abs(Math.sin(Math.toRadians(movingUp ? (double)this.pitch : (double)this.mc.field_1724.method_36455())));
         if (this.mc.field_1690.field_1832.method_1434() && !this.mc.field_1690.field_1903.method_1434()) {
            y = -(Double)this.down.get();
         }

         ((IVec3d)event.movement).set(x, y, z);
         this.mc.field_1724.method_18800(0.0, 0.0, 0.0);
      }
   }

   private void updateControlMovement() {
      float yaw = this.mc.field_1724.method_36454();
      float f = this.mc.field_1724.field_3913.field_3905;
      float s = this.mc.field_1724.field_3913.field_3907;
      if (f > 0.0F) {
         this.moving = true;
         yaw += s > 0.0F ? -45.0F : (s < 0.0F ? 45.0F : 0.0F);
      } else if (f < 0.0F) {
         this.moving = true;
         yaw += s > 0.0F ? -135.0F : (s < 0.0F ? 135.0F : 180.0F);
      } else {
         this.moving = s != 0.0F;
         yaw += s > 0.0F ? -90.0F : (s < 0.0F ? 90.0F : 0.0F);
      }

      this.yaw = yaw;
   }

   public static enum Mode {
      Wasp,
      Control;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Wasp, Control};
      }
   }
}
