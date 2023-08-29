package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.LemonUtils;
import java.util.List;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2828;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class StepPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   public final Setting slow;
   private final Setting strict;
   public final Setting height;
   public final Setting cooldown;
   public boolean stepping;
   double targetY;
   public int index;
   public double[] currentOffsets;
   public long lastStep;

   public StepPlus() {
      super(LemonClient.Misc, "Step+", "Step be worked in strict servers.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.slow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Slow")).description("Moves up slowly to prevent lagbacks.")).defaultValue(false)).build());
      this.strict = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Strict")).description("Strict 2b2tpvp step.")).defaultValue(false)).build());
      this.height = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Height")).description("Starts stepping if target block can be reached in x movement ticks.")).defaultValue(2.5).min(0.6).sliderRange(0.6, 2.5).visible(() -> {
         return !(Boolean)this.strict.get();
      })).build());
      this.cooldown = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Cooldown")).description("Waits x seconds between steps.")).defaultValue(0.25).min(0.0).sliderRange(0.0, 1.0).build());
      this.stepping = false;
      this.targetY = 0.0;
      this.index = 0;
      this.currentOffsets = null;
      this.lastStep = 0L;
   }

   public void onActivate() {
      this.index = 0;
      this.stepping = false;
      this.currentOffsets = null;
      this.targetY = 0.0;
   }

   public void slowStep(class_1297 entity, class_243 movement, CallbackInfoReturnable cir) {
      class_238 box = entity.method_5829();
      List list = entity.method_37908().method_20743(entity, box.method_18804(movement));
      class_243 vec3d = movement.method_1027() == 0.0 ? movement : class_1297.method_20736(entity, movement, box, entity.method_37908(), list);
      if (movement.field_1352 != vec3d.field_1352 || movement.field_1350 != vec3d.field_1350 || this.stepping) {
         class_243 vec3d4;
         if (entity.method_24828() && !this.stepping && (double)(System.currentTimeMillis() - this.lastStep) > (Double)this.cooldown.get() * 1000.0) {
            class_243 vec3d2 = class_1297.method_20736(entity, new class_243(movement.field_1352, (Double)this.height.get(), movement.field_1350), box, entity.method_37908(), list);
            class_243 vec3d3 = class_1297.method_20736(entity, new class_243(0.0, (Double)this.height.get(), 0.0), box.method_1012(movement.field_1352, 0.0, movement.field_1350), entity.method_37908(), list);
            if (vec3d3.field_1351 < (Double)this.height.get()) {
               vec3d4 = class_1297.method_20736(entity, new class_243(movement.field_1352, 0.0, movement.field_1350), box.method_997(vec3d3), entity.method_37908(), list).method_1019(vec3d3);
               if (vec3d4.method_37268() > vec3d2.method_37268()) {
                  vec3d2 = vec3d4;
               }
            }

            if (vec3d2.method_37268() > vec3d.method_37268()) {
               vec3d4 = vec3d2.method_1019(class_1297.method_20736(entity, new class_243(0.0, -vec3d2.field_1351 + movement.field_1351, 0.0), box.method_997(vec3d2), entity.method_37908(), list));
               double[] o = this.getOffsets(vec3d4.field_1351);
               if (o != null) {
                  this.lastStep = System.currentTimeMillis();
                  this.currentOffsets = o;
                  this.targetY = this.mc.field_1724.method_23318() + vec3d4.field_1351;
                  this.stepping = true;
                  this.index = -1;
               }
            }
         }

         if (this.stepping && this.currentOffsets != null) {
            ++this.index;
            double offset = 0.0;
            if (this.index < this.currentOffsets.length) {
               offset = this.currentOffsets[this.index];
            }

            if (this.index >= this.currentOffsets.length) {
               if (!(Boolean)this.strict.get()) {
                  offset = this.targetY - this.mc.field_1724.method_23318();
               }

               this.stepping = false;
            }

            if ((Boolean)this.strict.get() && this.index <= 1) {
               vec3d4 = class_1297.method_20736(entity, new class_243(0.0, offset, 0.0), box.method_1012(0.0, 0.0, 0.0), entity.method_37908(), list);
            } else {
               class_243 vec3d3 = class_1297.method_20736(entity, new class_243(0.0, offset, 0.0), box.method_1012(0.0, 0.0, 0.0), entity.method_37908(), list);
               vec3d4 = class_1297.method_20736(entity, new class_243(movement.field_1352, 0.0, movement.field_1350), box.method_997(vec3d3), entity.method_37908(), list).method_1019(vec3d3);
            }

            cir.setReturnValue(vec3d4);
            return;
         }
      }

      cir.setReturnValue(vec3d);
   }

   public double[] getOffsets(double step) {
      if ((Boolean)this.strict.get()) {
         return step > 0.6 && step <= 1.000001 ? new double[]{0.424, 0.33712, 0.25197759999999997} : null;
      } else if (step > 2.019) {
         return new double[]{0.425, 0.39599999999999996, -0.122, -0.09999999999999998, 0.42300000000000004, 0.3500000000000001, 0.2799999999999998, 0.21700000000000008, 0.15000000000000013, -0.10000000000000009};
      } else if (step > 1.5) {
         return new double[]{0.42, 0.36000000000000004, -0.15000000000000002, -0.12, 0.39, 0.30999999999999994, 0.24, -0.020000000000000018};
      } else if (step > 1.015) {
         return new double[]{0.42, 0.3332, 0.25680000000000003, 0.08299999999999996, -0.07800000000000007};
      } else {
         return step > 0.6 ? new double[]{0.42, 0.3332} : null;
      }
   }

   public void step(double[] offsets) {
      if (offsets != null) {
         double offset = 0.0;
         double[] var4 = offsets;
         int var5 = offsets.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            double v = var4[var6];
            offset += v;
            this.sendPacket(new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + offset, this.mc.field_1724.method_23321(), false));
         }

         this.lastStep = System.currentTimeMillis();
      }
   }

   private boolean i(class_238 b) {
      return LemonUtils.inside(this.mc.field_1724, b);
   }
}
