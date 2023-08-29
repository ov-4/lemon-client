package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1294;
import net.minecraft.class_2338;
import net.minecraft.class_2708;
import net.minecraft.class_2743;
import net.minecraft.class_3486;

public class Strafe extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPause;
   private final Setting mode;
   private final Setting accelerationAmount;
   private final Setting rbReset;
   private final Setting airStrafe;
   private final Setting onlyPressed;
   private final Setting strafeBind;
   private final Setting speed;
   private final Setting knockBack;
   private final Setting kbFactor;
   private final Setting pauseSneak;
   private final Setting pauseElytra;
   private final Setting pauseFly;
   private final Setting pauseWater;
   private final Setting pauseLava;
   private boolean move;
   public double velocity;
   private double acceleration;
   private double ax;
   private double az;
   private int jumpPhase;

   public Strafe() {
      super(LemonClient.Combat, "Strafe", "Modifies your movement speed when moving on the ground.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPause = this.settings.createGroup("Pause");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description("Mode for speed.")).defaultValue(Strafe.SpeedMode.Instant)).build());
      this.accelerationAmount = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Acceleration")).description("How much should the speed increase every movement tick.")).defaultValue(0.3).min(0.0).sliderMax(10.0).visible(() -> {
         return this.mode.get() == Strafe.SpeedMode.Accelerate;
      })).build());
      this.rbReset = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Reset On Rubberband")).description("Resets speed when rubberbanding.")).defaultValue(false)).visible(() -> {
         return this.mode.get() == Strafe.SpeedMode.Accelerate;
      })).build());
      this.airStrafe = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Air Strafe")).description("Lets you move fast in air too.")).defaultValue(false)).visible(() -> {
         return this.mode.get() == Strafe.SpeedMode.Accelerate;
      })).build());
      this.onlyPressed = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Only Pressed")).description("Uses instant mode when you arent pressing jump key.")).defaultValue(false)).visible(() -> {
         return this.mode.get() == Strafe.SpeedMode.CCStrafe;
      })).build());
      this.strafeBind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("Strafe Bind")).description("Strafes when this key is pressed.")).defaultValue(Keybind.fromKey(-1))).visible(() -> {
         return this.mode.get() == Strafe.SpeedMode.CCStrafe && (Boolean)this.onlyPressed.get();
      })).build());
      this.speed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Speed")).description("How many blocks to move every movement tick")).defaultValue(0.287).min(0.0).sliderMax(10.0).build());
      this.knockBack = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Damage Boost")).description("Turns knockback into velocity.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Damage Boost Factor")).description("Knockback multiplier")).defaultValue(1.0).min(0.0).sliderMax(10.0);
      Setting var10003 = this.knockBack;
      Objects.requireNonNull(var10003);
      this.kbFactor = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.pauseSneak = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause Sneak")).description("Doesn't modify movement while sneaking.")).defaultValue(true)).build());
      this.pauseElytra = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause Elytra")).description("Doesn't modify movement while flying with elytra.")).defaultValue(true)).build());
      this.pauseFly = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause Fly")).description("Doesn't modify movement while flying.")).defaultValue(true)).build());
      this.pauseWater = this.sgPause.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Pause Water")).description("Doesn't modify movement when in water.")).defaultValue(Strafe.LiquidMode.Submerged)).build());
      this.pauseLava = this.sgPause.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Pause Lava")).description("Doesn't modify movement when in lava.")).defaultValue(Strafe.LiquidMode.Both)).build());
      this.move = false;
      this.acceleration = 0.0;
      this.ax = 0.0;
      this.az = 0.0;
      this.jumpPhase = 1;
   }

   public void onActivate() {
      super.onActivate();
   }

   public void onDeactivate() {
      super.onDeactivate();
      ((Timer)Modules.get().get(Timer.class)).setOverride(1.0);
   }

   @EventHandler
   private void onKB(PacketEvent.Receive event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if ((Boolean)this.knockBack.get() && event.packet instanceof class_2743) {
            class_2743 packet = (class_2743)event.packet;
            if (packet.method_11818() == this.mc.field_1724.method_5628()) {
               double x = (double)((float)packet.method_11815() / 8000.0F);
               double z = (double)((float)packet.method_11819() / 8000.0F);
               this.velocity = Math.max(this.velocity, Math.sqrt(x * x + z * z) * (Double)this.kbFactor.get());
            }
         }

         if ((Boolean)this.rbReset.get() && event.packet instanceof class_2708) {
            this.acceleration = 0.0;
         }
      }

   }

   @EventHandler(
      priority = 200
   )
   public void onMove(PlayerMoveEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (((HoleSnap)Modules.get().get(HoleSnap.class)).isActive()) {
            return;
         }

         if ((Boolean)this.pauseSneak.get() && this.mc.field_1724.method_5715()) {
            return;
         }

         if ((Boolean)this.pauseElytra.get() && this.mc.field_1724.method_6128()) {
            return;
         }

         if ((Boolean)this.pauseFly.get() && this.mc.field_1724.method_31549().field_7479) {
            return;
         }

         switch ((LiquidMode)this.pauseWater.get()) {
            case Touching:
               if (this.mc.field_1724.method_5799()) {
                  return;
               }
               break;
            case Submerged:
               if (this.mc.field_1724.method_5777(class_3486.field_15517)) {
                  return;
               }
               break;
            case Both:
               if (this.mc.field_1724.method_5799() || this.mc.field_1724.method_5777(class_3486.field_15517)) {
                  return;
               }
         }

         switch ((LiquidMode)this.pauseLava.get()) {
            case Touching:
               if (this.mc.field_1724.method_5771()) {
                  return;
               }
               break;
            case Submerged:
               if (this.mc.field_1724.method_5777(class_3486.field_15518)) {
                  return;
               }
               break;
            case Both:
               if (this.mc.field_1724.method_5771() || this.mc.field_1724.method_5777(class_3486.field_15518)) {
                  return;
               }
         }

         double forward = (double)this.mc.field_1724.field_3913.field_3905;
         double sideways = (double)this.mc.field_1724.field_3913.field_3907;
         double yaw = this.getYaw(forward, sideways);
         if (this.mode.get() != Strafe.SpeedMode.CCStrafe || (Boolean)this.onlyPressed.get() && !((Keybind)this.strafeBind.get()).isPressed()) {
            this.velocity = Math.max((Double)this.speed.get(), this.velocity * 0.98);
         } else {
            if (this.jumpPhase == 4) {
               this.velocity *= 0.9888888889;
               if (this.mc.field_1724.method_24828()) {
                  this.jumpPhase = 1;
               }
            }

            if (this.jumpPhase == 3) {
               this.velocity += (0.2873 - this.velocity) * 0.6;
               this.jumpPhase = 4;
            }

            if (this.jumpPhase == 2) {
               ((IVec3d)event.movement).setY(0.4);
               this.velocity *= 1.85;
               this.jumpPhase = 3;
            }

            if (this.jumpPhase == 1 && this.mc.field_1724.method_24828() && this.move) {
               this.velocity = 0.2873;
               this.jumpPhase = 2;
            }

            this.velocity = Math.max(this.velocity, 0.2873);
         }

         double motion = this.velocity;
         if (this.velocity < 0.01) {
            motion = 0.0;
         }

         if (this.mc.field_1724.method_6059(class_1294.field_5904)) {
            motion *= 1.2 + (double)this.mc.field_1724.method_6112(class_1294.field_5904).method_5578() * 0.2;
         }

         if (this.mc.field_1724.method_6059(class_1294.field_5909)) {
            motion /= 1.2 + (double)this.mc.field_1724.method_6112(class_1294.field_5909).method_5578() * 0.2;
         }

         double x = Math.cos(Math.toRadians(yaw + 90.0));
         double y = this.mc.field_1724.method_18798().method_10214();
         double z = Math.sin(Math.toRadians(yaw + 90.0));
         switch ((SpeedMode)this.mode.get()) {
            case CCStrafe:
            case Instant:
               if (this.move) {
                  ((IVec3d)event.movement).set(motion * x, y, motion * z);
               } else {
                  ((IVec3d)event.movement).set(0.0, y, 0.0);
               }
               break;
            case Accelerate:
               this.acceleration = Math.min(1.0, (!this.move ? this.acceleration : this.acceleration + (!this.mc.field_1724.method_24828() && !(Boolean)this.airStrafe.get() ? 0.02 : (Double)this.accelerationAmount.get() / 10.0)) * this.slipperiness(this.move));
               if (this.move && this.mc.field_1724.method_24828() || (Boolean)this.airStrafe.get()) {
                  this.ax = x;
                  this.az = z;
               }

               ((IVec3d)event.movement).setXZ((Double)this.speed.get() * this.ax * this.acceleration, (Double)this.speed.get() * this.az * this.acceleration);
         }
      }

   }

   private double slipperiness(boolean moving) {
      if (moving) {
         return 1.0;
      } else {
         return this.mc.field_1724.method_24828() ? (double)this.mc.field_1687.method_8320(new class_2338((int)this.mc.field_1724.method_23317(), (int)Math.ceil(this.mc.field_1724.method_23318() - 1.0), (int)this.mc.field_1724.method_23321())).method_26204().method_9499() : 0.98;
      }
   }

   private double getYaw(double f, double s) {
      double yaw = (double)this.mc.field_1724.method_36454();
      if (f > 0.0) {
         this.move = true;
         yaw += s > 0.0 ? -45.0 : (s < 0.0 ? 45.0 : 0.0);
      } else if (f < 0.0) {
         this.move = true;
         yaw += s > 0.0 ? -135.0 : (s < 0.0 ? 135.0 : 180.0);
      } else {
         this.move = s != 0.0;
         yaw += s > 0.0 ? -90.0 : (s < 0.0 ? 90.0 : 0.0);
      }

      return yaw;
   }

   public static enum SpeedMode {
      CCStrafe,
      Instant,
      Accelerate;

      // $FF: synthetic method
      private static SpeedMode[] $values() {
         return new SpeedMode[]{CCStrafe, Instant, Accelerate};
      }
   }

   public static enum LiquidMode {
      Disabled,
      Submerged,
      Touching,
      Both;

      // $FF: synthetic method
      private static LiquidMode[] $values() {
         return new LiquidMode[]{Disabled, Submerged, Touching, Both};
      }
   }
}
