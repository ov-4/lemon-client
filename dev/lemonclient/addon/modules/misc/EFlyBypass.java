package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2848;
import net.minecraft.class_3532;
import net.minecraft.class_2848.class_2849;

public class EFlyBypass extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgAutomatic;
   private final Setting takeoff;
   public final Setting mode;
   private final Setting factor;
   private final Setting startAt;
   private final Setting targetY;
   private final Setting boostingTicks;
   boolean boosting;
   int boostTicks;

   public EFlyBypass() {
      super(LemonClient.Misc, "EFly Bypass", "Elytra Fly that works on strict servers.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgAutomatic = this.settings.createGroup("Automatic");
      this.takeoff = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Take Off")).defaultValue(false)).build());
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).defaultValue(EFlyBypass.Mode.Boost)).build());
      this.factor = this.sgGeneral.add(((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Factor")).defaultValue(1.3).min(0.1).sliderMax(4.0).build());
      this.startAt = this.sgAutomatic.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Start Fly At")).description("The Y coordinate to start automatic module. Recommended 70~")).defaultValue(65)).min(25).sliderMin(25).sliderMax(255).build());
      this.targetY = this.sgAutomatic.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Target Y")).description("The target coordinate for boosting.")).defaultValue(100)).min(50).sliderMin(50).sliderMax(255).build());
      this.boostingTicks = this.sgAutomatic.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("BoostingTicks")).description("Amount of ticks to boost")).defaultValue(20)).min(20).max(80).sliderMin(20).sliderMax(80).build());
   }

   public void onActivate() {
      this.boosting = false;
      this.boostTicks = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      float yaw = (float)Math.toRadians((double)this.mc.field_1724.method_36454());
      if (!this.mc.field_1724.method_6128()) {
         if ((Boolean)this.takeoff.get() && !this.mc.field_1724.method_24828() && this.mc.field_1690.field_1903.method_1434()) {
            this.mc.field_1724.field_3944.method_2883(new class_2848(this.mc.field_1724, class_2849.field_12982));
         }

      } else {
         if (this.mc.field_1724.method_31549().field_7479) {
            this.mc.field_1724.method_31549().field_7479 = false;
         }

         if (this.mode.get() == EFlyBypass.Mode.Boost && this.mc.field_1690.field_1894.method_1434()) {
            this.mc.field_1724.method_5762((double)(-class_3532.method_15374(yaw)) * (Double)this.factor.get() / 10.0, 0.0, (double)class_3532.method_15362(yaw) * (Double)this.factor.get() / 10.0);
         }

         if (this.mode.get() == EFlyBypass.Mode.Automatic) {
            int y = (int)this.mc.field_1724.method_23318();
            int b = (Integer)this.boostingTicks.get() / 4;
            if (this.boosting || y >= (Integer)this.startAt.get() && y < (Integer)this.targetY.get()) {
               this.boosting = true;
               ++this.boostTicks;
               if (this.boostTicks < b * 2) {
                  this.mc.field_1724.method_36457(35.0F);
                  this.mc.field_1724.method_5762((double)(-class_3532.method_15374(yaw)) * (Double)this.factor.get() / 10.0, 0.0, (double)class_3532.method_15362(yaw) * (Double)this.factor.get() / 10.0);
               } else {
                  float i = (float)this.boostTicks / 1.5F;
                  if (i > 35.0F) {
                     i = 35.0F;
                  }

                  this.mc.field_1724.method_36457(-i);
               }

               if (this.boostTicks > (Integer)this.boostingTicks.get() + 10) {
                  this.boosting = false;
                  this.boostTicks = 0;
               }
            }
         }

      }
   }

   public static enum Mode {
      Boost,
      Automatic;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Boost, Automatic};
      }
   }
}
