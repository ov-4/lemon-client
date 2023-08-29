package dev.lemonclient.addon.modules.settings;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.PlaceData;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.class_2189;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;

public class FacingSettings extends LemonModule {
   private final SettingGroup sgGeneral;
   public final Setting strictDir;
   public final Setting unblocked;
   public final Setting airPlace;
   public final Setting maxHeight;

   public FacingSettings() {
      super(LemonClient.Settings, "Facing", "Global facing settings for every lemon module.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.strictDir = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Strict Direction")).description("Doesn't place on faces which aren't in your direction.")).defaultValue(false)).build());
      this.unblocked = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Unblocked")).description("Doesn't place on faces that have block on them.")).defaultValue(false)).build());
      this.airPlace = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Air Place")).description("Allows placing blocks in air.")).defaultValue(false)).build());
      this.maxHeight = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Max Height")).description("Doesn't place on top sides of blocks at max height. Old: 1.12, New: 1.17+")).defaultValue(FacingSettings.MaxHeight.New)).build());
   }

   public PlaceData getPlaceDataOR(class_2338 pos, Predicate predicate, boolean ignoreContainers) {
      if (pos == null) {
         return new PlaceData((class_2338)null, (class_2350)null, false);
      } else {
         class_2350 best = null;
         if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
            if ((Boolean)this.airPlace.get()) {
               return new PlaceData(pos, class_2350.field_11036, true);
            }

            double cDist = -1.0;
            class_2350[] var7 = class_2350.values();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               class_2350 dir = var7[var9];
               if (!this.heightCheck(pos.method_10093(dir)) && (!ignoreContainers || !this.mc.field_1687.method_8320(pos.method_10093(dir)).method_31709()) && (LemonUtils.solid(pos.method_10093(dir)) || predicate == null || predicate.test(pos.method_10093(dir))) && (!(Boolean)this.strictDir.get() || LemonUtils.strictDir(pos.method_10093(dir), dir.method_10153()))) {
                  double dist = SettingUtils.placeRangeTo(pos.method_10093(dir));
                  if (dist >= 0.0 && (cDist < 0.0 || dist < cDist)) {
                     best = dir;
                     cDist = dist;
                  }
               }
            }
         }

         return best == null ? new PlaceData((class_2338)null, (class_2350)null, false) : new PlaceData(pos.method_10093(best), best.method_10153(), true);
      }
   }

   public PlaceData getPlaceDataAND(class_2338 pos, Predicate predicate, Predicate predicatePos, boolean ignoreContainers) {
      if (pos == null) {
         return new PlaceData((class_2338)null, (class_2350)null, false);
      } else {
         class_2350 best = null;
         if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
            if ((Boolean)this.airPlace.get()) {
               return new PlaceData(pos, class_2350.field_11036, true);
            }

            double cDist = -1.0;
            class_2350[] var8 = class_2350.values();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               class_2350 dir = var8[var10];
               if (!this.heightCheck(pos.method_10093(dir)) && (!ignoreContainers || !this.mc.field_1687.method_8320(pos.method_10093(dir)).method_31709()) && LemonUtils.solid(pos.method_10093(dir)) && (predicate == null || predicate.test(dir)) && (predicatePos == null || predicatePos.test(pos.method_10093(dir))) && (!(Boolean)this.strictDir.get() || LemonUtils.strictDir(pos.method_10093(dir), dir.method_10153()))) {
                  double dist = SettingUtils.placeRangeTo(pos.method_10093(dir));
                  if (dist >= 0.0 && (cDist < 0.0 || dist < cDist)) {
                     best = dir;
                     cDist = dist;
                  }
               }
            }
         }

         return best == null ? new PlaceData((class_2338)null, (class_2350)null, false) : new PlaceData(pos.method_10093(best), best.method_10153(), true);
      }
   }

   public PlaceData getPlaceData(class_2338 pos, boolean ignoreContainers) {
      if (pos == null) {
         return new PlaceData((class_2338)null, (class_2350)null, false);
      } else {
         class_2350 best = null;
         if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
            if ((Boolean)this.airPlace.get()) {
               return new PlaceData(pos, class_2350.field_11036, true);
            }

            double cDist = -1.0;
            class_2350[] var6 = class_2350.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               class_2350 dir = var6[var8];
               if (!this.heightCheck(pos.method_10093(dir)) && (!ignoreContainers || !this.mc.field_1687.method_8320(pos.method_10093(dir)).method_31709()) && LemonUtils.solid(pos.method_10093(dir)) && (!(Boolean)this.strictDir.get() || LemonUtils.strictDir(pos.method_10093(dir), dir.method_10153()))) {
                  double dist = SettingUtils.placeRangeTo(pos.method_10093(dir));
                  if (dist >= 0.0 && (cDist < 0.0 || dist < cDist)) {
                     best = dir;
                     cDist = dist;
                  }
               }
            }
         }

         return best == null ? new PlaceData((class_2338)null, (class_2350)null, false) : new PlaceData(pos.method_10093(best), best.method_10153(), true);
      }
   }

   public class_2350 getPlaceOnDirection(class_2338 pos) {
      if (pos == null) {
         return null;
      } else {
         class_2350 best = null;
         if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
            double cDist = -1.0;
            class_2350[] var5 = class_2350.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               class_2350 dir = var5[var7];
               if (!this.heightCheck(pos.method_10093(dir)) && (!(Boolean)this.unblocked.get() || this.getBlock(pos.method_10093(dir)) instanceof class_2189) && (!(Boolean)this.strictDir.get() || LemonUtils.strictDir(pos, dir))) {
                  double dist = this.dist(pos, dir);
                  if (dist >= 0.0 && (cDist < 0.0 || dist < cDist)) {
                     best = dir;
                     cDist = dist;
                  }
               }
            }
         }

         return best;
      }
   }

   private boolean heightCheck(class_2338 pos) {
      int var10000 = pos.method_10264();
      short var10001;
      switch ((MaxHeight)this.maxHeight.get()) {
         case Old:
            var10001 = 255;
            break;
         case New:
            var10001 = 319;
            break;
         case Disabled:
            var10001 = 1000;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000 >= var10001;
   }

   private double dist(class_2338 pos, class_2350 dir) {
      if (this.mc.field_1724 == null) {
         return 0.0;
      } else {
         class_243 vec = new class_243((double)((float)pos.method_10263() + (float)dir.method_10148() / 2.0F), (double)((float)pos.method_10264() + (float)dir.method_10164() / 2.0F), (double)((float)pos.method_10260() + (float)dir.method_10165() / 2.0F));
         class_243 dist = this.mc.field_1724.method_33571().method_1031(-vec.field_1352, -vec.field_1351, -vec.field_1350);
         return Math.sqrt(dist.field_1352 * dist.field_1352 + dist.field_1351 * dist.field_1351 + dist.field_1350 * dist.field_1350);
      }
   }

   private class_2248 getBlock(class_2338 pos) {
      return this.mc.field_1687.method_8320(pos).method_26204();
   }

   public static enum MaxHeight {
      Old,
      New,
      Disabled;

      // $FF: synthetic method
      private static MaxHeight[] $values() {
         return new MaxHeight[]{Old, New, Disabled};
      }
   }
}
