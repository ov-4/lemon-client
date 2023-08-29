package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.modules.settings.SwingSettings;
import dev.lemonclient.addon.utils.SettingUtils;
import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2527;
import net.minecraft.class_2846;
import net.minecraft.class_2846.class_2847;

public class LightsOut extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting delay;
   private final Setting swing;
   private final Setting swingHand;
   private double timer;

   public LightsOut() {
      super(LemonClient.Misc, "Lights Out", "A tribute to Reliant.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.delay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Delay")).description("Delay between button clicks.")).defaultValue(2.0).range(0.0, 10.0).sliderRange(0.0, 10.0).build());
      this.swing = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders swing animation when breaking a torch.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.swing;
      Objects.requireNonNull(var10003);
      this.swingHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.timer = 0.0;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      class_2338 block = this.getLightSource(this.mc.field_1724.method_33571(), SettingUtils.getMineRange());
      if (block != null && this.timer >= (Double)this.delay.get()) {
         this.timer = 0.0;
         SettingUtils.mineSwing(SwingSettings.MiningSwingState.Start);
         this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, block, class_2350.field_11036));
         this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, block, class_2350.field_11036));
         SettingUtils.mineSwing(SwingSettings.MiningSwingState.End);
         if ((Boolean)this.swing.get()) {
            this.clientSwing((SwingHand)this.swingHand.get(), class_1268.field_5808);
         }
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      this.timer = Math.min((Double)this.delay.get(), this.timer + event.frameTime);
   }

   private class_2338 getLightSource(class_243 vec, double r) {
      int c = (int)(Math.ceil(r) + 1.0);
      class_2338 closest = null;
      float closestDist = -1.0F;

      for(int x = -c; x <= c; ++x) {
         for(int y = -c; y <= c; ++y) {
            for(int z = -c; z <= c; ++z) {
               class_2338 pos = this.mc.field_1724.method_24515().method_10069(x, y, z);
               if (this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2527) {
                  float dist = (float)vec.method_1022(pos.method_46558());
                  if ((double)dist <= r && (closest == null || dist < closestDist)) {
                     closest = pos;
                     closestDist = dist;
                  }
               }
            }
         }
      }

      return closest;
   }
}
