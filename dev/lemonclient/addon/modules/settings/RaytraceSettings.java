package dev.lemonclient.addon.modules.settings;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.mixins.IRaycastContext;
import dev.lemonclient.addon.utils.player.DamageInfo;
import java.util.Objects;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class RaytraceSettings extends LemonModule {
   private final SettingGroup sgPlace;
   private final SettingGroup sgAttack;
   public final Setting placeTrace;
   private final Setting placeMode;
   private final Setting placeHeight;
   private final Setting placeHeight1;
   private final Setting placeHeight2;
   private final Setting exposure;
   public final Setting attackTrace;
   private final Setting attackMode;
   private final Setting attackHeight;
   private final Setting attackHeight1;
   private final Setting attackHeight2;
   private final Setting attackExposure;
   private final class_243 vec;
   public class_3959 raycastContext;
   public class_3965 result;
   public int hit;

   public RaytraceSettings() {
      super(LemonClient.Settings, "Raytrace", "Global raytrace settings for every lemon module.");
      this.sgPlace = this.settings.createGroup("Placing");
      this.sgAttack = this.settings.createGroup("Attacking");
      this.placeTrace = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Place Traces")).description("Raytraces when placing.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgPlace;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Mode")).description("Place trace mode.")).defaultValue(RaytraceSettings.PlaceTraceMode.SinglePoint);
      Setting var10003 = this.placeTrace;
      Objects.requireNonNull(var10003);
      this.placeMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.placeHeight = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Height")).description("Raytraces to x blocks above the bottom.")).defaultValue(0.5).sliderRange(-2.0, 2.0).visible(() -> {
         return this.placeMode.get() == RaytraceSettings.PlaceTraceMode.SinglePoint && (Boolean)this.placeTrace.get();
      })).build());
      this.placeHeight1 = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Height 1")).description("Raytraces to x blocks above the bottom.")).defaultValue(0.25).sliderRange(-2.0, 1.5).visible(() -> {
         return this.placeMode.get() == RaytraceSettings.PlaceTraceMode.DoublePoint && (Boolean)this.placeTrace.get();
      })).build());
      this.placeHeight2 = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Height 2")).description("Raytraces to x blocks above the bottom.")).defaultValue(0.75).sliderRange(-2.0, 2.0).visible(() -> {
         return this.placeMode.get() == RaytraceSettings.PlaceTraceMode.DoublePoint && (Boolean)this.placeTrace.get();
      })).build());
      this.exposure = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Exposure")).description("How many % of the block should be seen.")).defaultValue(50.0).range(0.0, 100.0).sliderRange(0.0, 100.0).visible(() -> {
         return this.placeMode.get() == RaytraceSettings.PlaceTraceMode.Exposure && (Boolean)this.placeTrace.get();
      })).build());
      this.attackTrace = this.sgAttack.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Attack Traces")).description("Raytraces when attacking.")).defaultValue(false)).build());
      var10001 = this.sgAttack;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Attack Mode")).description("Attack trace mode.")).defaultValue(RaytraceSettings.AttackTraceMode.SinglePoint);
      var10003 = this.attackTrace;
      Objects.requireNonNull(var10003);
      this.attackMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.attackHeight = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Height")).description("Raytraces to x blocks above the bottom.")).defaultValue(1.5).sliderRange(-2.0, 2.0).visible(() -> {
         return ((AttackTraceMode)this.attackMode.get()).equals(RaytraceSettings.AttackTraceMode.SinglePoint) && (Boolean)this.attackTrace.get();
      })).build());
      this.attackHeight1 = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Height 1")).description("Raytraces to x * hitbox height above the bottom.")).defaultValue(0.5).sliderRange(-2.0, 2.0).visible(() -> {
         return ((AttackTraceMode)this.attackMode.get()).equals(RaytraceSettings.AttackTraceMode.DoublePoint) && (Boolean)this.attackTrace.get();
      })).build());
      this.attackHeight2 = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Height 2")).description("Raytraces to x * hitbox height above the bottom.")).defaultValue(0.5).sliderRange(-2.0, 2.0).visible(() -> {
         return ((AttackTraceMode)this.attackMode.get()).equals(RaytraceSettings.AttackTraceMode.DoublePoint) && (Boolean)this.attackTrace.get();
      })).build());
      this.attackExposure = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Exposure")).description("How many % of the entity should be seen.")).defaultValue(50.0).range(0.0, 100.0).sliderRange(0.0, 100.0).visible(() -> {
         return this.placeMode.get() == RaytraceSettings.PlaceTraceMode.Exposure && (Boolean)this.attackTrace.get();
      })).build());
      this.vec = new class_243(0.0, 0.0, 0.0);
      this.hit = 0;
   }

   public boolean placeTrace(class_2338 pos) {
      if (!(Boolean)this.placeTrace.get()) {
         return true;
      } else {
         this.updateContext();
         int x;
         int y;
         int z;
         switch ((PlaceTraceMode)this.placeMode.get()) {
            case SinglePoint:
               ((IRaycastContext)this.raycastContext).setEnd(new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + (Double)this.placeHeight.get(), (double)pos.method_10260() + 0.5));
               this.result = DamageInfo.raycast(this.raycastContext);
               return this.result.method_17777().equals(pos);
            case DoublePoint:
               ((IRaycastContext)this.raycastContext).setEnd(new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + (Double)this.placeHeight1.get(), (double)pos.method_10260() + 0.5));
               this.result = DamageInfo.raycast(this.raycastContext);
               if (this.result.method_17777().equals(pos)) {
                  return true;
               }

               ((IRaycastContext)this.raycastContext).setEnd(new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + (Double)this.placeHeight2.get(), (double)pos.method_10260() + 0.5));
               this.result = DamageInfo.raycast(this.raycastContext);
               return this.result.method_17777().equals(pos);
            case Sides:
               ((IVec3d)this.vec).set((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5);
               class_2350[] var6 = class_2350.values();
               y = var6.length;

               for(z = 0; z < y; ++z) {
                  class_2350 dir = var6[z];
                  ((IRaycastContext)this.raycastContext).setEnd(this.vec.method_1031((double)((float)dir.method_10148() / 2.0F), (double)((float)dir.method_10164() / 2.0F), (double)((float)dir.method_10165() / 2.0F)));
                  this.result = DamageInfo.raycast(this.raycastContext);
                  if (this.result.method_17777().equals(pos)) {
                     return true;
                  }
               }

               return false;
            case Exposure:
               ((IVec3d)this.vec).set((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260());
               this.hit = 0;

               for(x = 0; x <= 2; ++x) {
                  for(y = 0; y <= 2; ++y) {
                     for(z = 0; z <= 2; ++z) {
                        ((IRaycastContext)this.raycastContext).setEnd(this.vec.method_1031(0.1 + (double)x * 0.4, 0.1 + (double)y * 0.4, 0.1 + (double)z * 0.4));
                        this.result = DamageInfo.raycast(this.raycastContext);
                        if (this.result.method_17777().equals(pos)) {
                           ++this.hit;
                           if ((double)this.hit >= (Double)this.exposure.get() / 100.0 * 27.0) {
                              return true;
                           }
                        }
                     }
                  }
               }

               return false;
            case Any:
               ((IVec3d)this.vec).set((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260());
               this.hit = 0;

               for(x = 0; x <= 2; ++x) {
                  for(y = 0; y <= 2; ++y) {
                     for(z = 0; z <= 2; ++z) {
                        ((IRaycastContext)this.raycastContext).setEnd(this.vec.method_1031(0.1 + (double)x * 0.4, 0.1 + (double)y * 0.4, 0.1 + (double)z * 0.4));
                        this.result = DamageInfo.raycast(this.raycastContext);
                        if (this.result.method_17777().equals(pos)) {
                           return true;
                        }
                     }
                  }
               }
         }

         return false;
      }
   }

   public boolean attackTrace(class_238 box) {
      if (!(Boolean)this.attackTrace.get()) {
         return true;
      } else {
         this.updateContext();
         double xw;
         double yh;
         double zw;
         int x;
         int y;
         int z;
         switch ((AttackTraceMode)this.attackMode.get()) {
            case SinglePoint:
               ((meteordevelopment.meteorclient.mixininterface.IRaycastContext)DamageInfo.raycastContext).set(this.mc.field_1724.method_33571(), new class_243((box.field_1323 + box.field_1320) / 2.0, box.field_1322 + (Double)this.attackHeight.get(), (box.field_1321 + box.field_1324) / 2.0), class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
               return DamageInfo.raycast(DamageInfo.raycastContext).method_17783() != class_240.field_1332;
            case DoublePoint:
               ((meteordevelopment.meteorclient.mixininterface.IRaycastContext)DamageInfo.raycastContext).set(this.mc.field_1724.method_33571(), new class_243((box.field_1323 + box.field_1320) / 2.0, box.field_1322 + (Double)this.attackHeight1.get(), (box.field_1321 + box.field_1324) / 2.0), class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
               if (DamageInfo.raycast(DamageInfo.raycastContext).method_17783() != class_240.field_1332) {
                  return true;
               }

               ((meteordevelopment.meteorclient.mixininterface.IRaycastContext)DamageInfo.raycastContext).set(this.mc.field_1724.method_33571(), new class_243((box.field_1323 + box.field_1320) / 2.0, box.field_1322 + (Double)this.attackHeight2.get(), (box.field_1321 + box.field_1324) / 2.0), class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
               return DamageInfo.raycast(DamageInfo.raycastContext).method_17783() != class_240.field_1332;
            case Exposure:
               ((IVec3d)this.vec).set(box.field_1323, box.field_1322, box.field_1321);
               xw = box.field_1320 - box.field_1323;
               yh = box.field_1325 - box.field_1322;
               zw = box.field_1324 - box.field_1321;
               this.hit = 0;

               for(x = 0; x <= 2; ++x) {
                  for(y = 0; y <= 2; ++y) {
                     for(z = 0; z <= 2; ++z) {
                        ((IRaycastContext)this.raycastContext).setEnd(this.vec.method_1031(class_3532.method_16436((double)((float)x / 2.0F), 0.1, xw - 0.1), class_3532.method_16436((double)((float)y / 2.0F), 0.0, yh - 0.1), class_3532.method_16436((double)((float)z / 2.0F), 0.1, zw - 0.1)));
                        this.result = DamageInfo.raycast(this.raycastContext);
                        if (this.result.method_17783() != class_240.field_1332) {
                           ++this.hit;
                           if ((double)this.hit >= (Double)this.attackExposure.get() / 100.0 * 27.0) {
                              return true;
                           }
                        }
                     }
                  }
               }

               return false;
            case Any:
               ((IVec3d)this.vec).set(box.field_1323, box.field_1322, box.field_1321);
               xw = box.field_1320 - box.field_1323;
               yh = box.field_1325 - box.field_1322;
               zw = box.field_1324 - box.field_1321;

               for(x = 0; x <= 2; ++x) {
                  for(y = 0; y <= 2; ++y) {
                     for(z = 0; z <= 2; ++z) {
                        ((IRaycastContext)this.raycastContext).setEnd(this.vec.method_1031(class_3532.method_16436((double)((float)x / 2.0F), 0.1, xw - 0.1), class_3532.method_16436((double)((float)y / 2.0F), 0.0, yh - 0.1), class_3532.method_16436((double)((float)z / 2.0F), 0.1, zw - 0.1)));
                        this.result = DamageInfo.raycast(this.raycastContext);
                        if (this.result.method_17783() != class_240.field_1332) {
                           return true;
                        }
                     }
                  }
               }
         }

         return false;
      }
   }

   private void updateContext() {
      if (this.raycastContext == null) {
         this.raycastContext = new class_3959(this.mc.field_1724.method_33571(), (class_243)null, class_3960.field_17558, class_242.field_1347, this.mc.field_1724);
      } else {
         ((IRaycastContext)this.raycastContext).setStart(this.mc.field_1724.method_33571());
      }

   }

   public static enum PlaceTraceMode {
      SinglePoint,
      DoublePoint,
      Sides,
      Exposure,
      Any;

      // $FF: synthetic method
      private static PlaceTraceMode[] $values() {
         return new PlaceTraceMode[]{SinglePoint, DoublePoint, Sides, Exposure, Any};
      }
   }

   public static enum AttackTraceMode {
      SinglePoint,
      DoublePoint,
      Exposure,
      Any;

      // $FF: synthetic method
      private static AttackTraceMode[] $values() {
         return new AttackTraceMode[]{SinglePoint, DoublePoint, Exposure, Any};
      }
   }
}
