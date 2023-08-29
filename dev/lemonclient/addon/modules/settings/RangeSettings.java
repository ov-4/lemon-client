package dev.lemonclient.addon.modules.settings;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.RotationUtils;
import java.util.Objects;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_241;
import net.minecraft.class_243;

public class RangeSettings extends LemonModule {
   private final SettingGroup sgPlace;
   private final SettingGroup sgAttack;
   private final SettingGroup sgMining;
   public final Setting placeRange;
   public final Setting placeRangeWalls;
   private final Setting placeRangeFrom;
   private final Setting placeRangeMode;
   private final Setting blockWidth;
   private final Setting blockHeight;
   private final Setting placeHeight;
   public final Setting attackRange;
   public final Setting attackRangeWalls;
   public final Setting reduce;
   public final Setting reduceAmount;
   public final Setting reduceStep;
   private final Setting attackRangeFrom;
   private final Setting attackRangeMode;
   private final Setting closestAttackWidth;
   private final Setting closestAttackHeight;
   private final Setting attackHeight;
   public final Setting miningRange;
   public final Setting miningRangeWalls;
   private final Setting miningRangeFrom;
   private final Setting miningRangeMode;
   private final Setting closestMiningWidth;
   private final Setting closestMiningHeight;
   private final Setting miningHeight;
   public double rangeMulti;

   public RangeSettings() {
      super(LemonClient.Settings, "Range", "Global range settings for every lemon module.");
      this.sgPlace = this.settings.createGroup("Placing");
      this.sgAttack = this.settings.createGroup("Attacking");
      this.sgMining = this.settings.createGroup("Mining");
      this.placeRange = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Range")).description("Range for placing.")).defaultValue(5.2).range(0.0, 6.0).sliderRange(0.0, 6.0).build());
      this.placeRangeWalls = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Range Walls")).description("Range for placing behind blocks.")).defaultValue(5.2).range(0.0, 6.0).sliderRange(0.0, 6.0).build());
      this.placeRangeFrom = this.sgPlace.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Range From")).description("Where to calculate place ranges from.")).defaultValue(RangeSettings.FromMode.Eyes)).build());
      this.placeRangeMode = this.sgPlace.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Range Mode")).description("Where to calculate place ranges from.")).defaultValue(RangeSettings.PlaceRangeMode.NCP)).build());
      this.blockWidth = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Block Width")).description("How wide should the box be for closest range.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 3.0).visible(() -> {
         return ((PlaceRangeMode)this.placeRangeMode.get()).equals(RangeSettings.PlaceRangeMode.CustomBox);
      })).build());
      this.blockHeight = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Block Height")).description("How tall should the box be for closest range.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 3.0).visible(() -> {
         return ((PlaceRangeMode)this.placeRangeMode.get()).equals(RangeSettings.PlaceRangeMode.CustomBox);
      })).build());
      this.placeHeight = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Height")).description("The height to calculate ranges from.")).defaultValue(0.5).sliderRange(0.0, 1.0).visible(() -> {
         return ((PlaceRangeMode)this.placeRangeMode.get()).equals(RangeSettings.PlaceRangeMode.Height);
      })).build());
      this.attackRange = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Range")).description("Range for attacking entities.")).defaultValue(4.8).range(0.0, 6.0).sliderRange(0.0, 6.0).build());
      this.attackRangeWalls = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Range Walls")).description("Range for attacking entities behind blocks.")).defaultValue(4.8).range(0.0, 6.0).sliderRange(0.0, 6.0).build());
      this.reduce = this.sgAttack.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Reduce")).description("Reduces range on every hit by reduce step until it reaches (range - reduce amount).")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgAttack;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Reduce Amount")).description("Check description from 'Reduce' setting.")).defaultValue(0.8).range(0.0, 6.0).sliderRange(0.0, 6.0);
      Setting var10003 = this.reduce;
      Objects.requireNonNull(var10003);
      this.reduceAmount = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgAttack;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Reduce Step")).description("Check description from 'Reduce' setting.")).defaultValue(0.14).range(0.0, 6.0).sliderRange(0.0, 6.0);
      var10003 = this.reduce;
      Objects.requireNonNull(var10003);
      this.reduceStep = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.attackRangeFrom = this.sgAttack.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Attack Range From")).description("Where to calculate ranges from.")).defaultValue(RangeSettings.FromMode.Eyes)).build());
      this.attackRangeMode = this.sgAttack.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Attack Range Mode")).description("Where to calculate ranges from.")).defaultValue(RangeSettings.AttackRangeMode.NCP)).build());
      this.closestAttackWidth = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Closest Attack Width")).description("How wide should the box be for closest range.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 3.0).visible(() -> {
         return ((AttackRangeMode)this.attackRangeMode.get()).equals(RangeSettings.AttackRangeMode.CustomBox);
      })).build());
      this.closestAttackHeight = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Closest Attack Height")).description("How tall should the box be for closest range.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 3.0).visible(() -> {
         return ((AttackRangeMode)this.attackRangeMode.get()).equals(RangeSettings.AttackRangeMode.CustomBox);
      })).build());
      this.attackHeight = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Height")).description("The height above feet to calculate ranges from.")).defaultValue(1.0).sliderRange(-2.0, 2.0).visible(() -> {
         return ((AttackRangeMode)this.attackRangeMode.get()).equals(RangeSettings.AttackRangeMode.Height);
      })).build());
      this.miningRange = this.sgMining.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Mining Range")).description("Range for mining blocks.")).defaultValue(4.8).range(0.0, 6.0).sliderRange(0.0, 6.0).build());
      this.miningRangeWalls = this.sgMining.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Mining Range Walls")).description("Range for mining blocks behind other blocks.")).defaultValue(4.8).range(0.0, 6.0).sliderRange(0.0, 6.0).build());
      this.miningRangeFrom = this.sgMining.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mining Range From")).description("Where to calculate mining ranges from.")).defaultValue(RangeSettings.FromMode.Eyes)).build());
      this.miningRangeMode = this.sgMining.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mining Range Mode")).description("Where to calculate mining ranges from.")).defaultValue(RangeSettings.MiningRangeMode.NCP)).build());
      this.closestMiningWidth = this.sgMining.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Closest Mining Width")).description("How wide should the box be for closest range.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 3.0).visible(() -> {
         return ((MiningRangeMode)this.miningRangeMode.get()).equals(RangeSettings.MiningRangeMode.CustomBox);
      })).build());
      this.closestMiningHeight = this.sgMining.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Closest Mining Height")).description("How tall should the box be for closest range.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 3.0).visible(() -> {
         return ((MiningRangeMode)this.miningRangeMode.get()).equals(RangeSettings.MiningRangeMode.CustomBox);
      })).build());
      this.miningHeight = this.sgMining.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Mining Height")).description("The height above block bottom to calculate ranges from.")).defaultValue(0.5).sliderRange(0.0, 1.0).visible(() -> {
         return ((MiningRangeMode)this.miningRangeMode.get()).equals(RangeSettings.MiningRangeMode.Height);
      })).build());
      this.rangeMulti = 0.0;
   }

   public boolean inPlaceRange(class_2338 pos, class_243 from) {
      if (this.mc.field_1724 == null) {
         return false;
      } else {
         double dist = this.placeRangeTo(pos, from);
         return dist >= 0.0 && dist <= SettingUtils.placeTrace(pos) ? (Double)this.placeRange.get() : (Double)this.placeRangeWalls.get();
      }
   }

   public boolean inPlaceRangeNoTrace(class_2338 pos, class_243 from) {
      if (this.mc.field_1724 == null) {
         return false;
      } else {
         double dist = this.placeRangeTo(pos, from);
         return dist >= 0.0 && dist <= Math.max((Double)this.placeRange.get(), (Double)this.placeRangeWalls.get());
      }
   }

   public double placeRangeTo(class_2338 pos, class_243 from) {
      class_238 pBB = this.mc.field_1724.method_5829();
      class_243 feet;
      if (from == null) {
         from = this.mc.field_1724.method_33571();
         feet = this.mc.field_1724.method_19538();
         switch ((FromMode)this.placeRangeFrom.get()) {
            case Middle:
               ((IVec3d)from).set((pBB.field_1323 + pBB.field_1320) / 2.0, (pBB.field_1322 + pBB.field_1325) / 2.0, (pBB.field_1321 + pBB.field_1324) / 2.0);
               break;
            case Feet:
               ((IVec3d)from).set(feet.field_1352, feet.field_1351, feet.field_1350);
         }
      }

      feet = new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264(), (double)pos.method_10260() + 0.5);
      switch ((PlaceRangeMode)this.placeRangeMode.get()) {
         case NCP:
            return this.getRange(from, feet.method_1031(0.0, 0.5, 0.0));
         case Height:
            return this.getRange(from, feet.method_1031(0.0, (Double)this.placeHeight.get(), 0.0));
         case Vanilla:
            return this.getRange(from, LemonUtils.getClosest(this.mc.field_1724.method_33571(), feet, 1.0, 1.0));
         case CustomBox:
            return this.getRange(from, LemonUtils.getClosest(this.mc.field_1724.method_33571(), feet, (Double)this.blockWidth.get(), (Double)this.blockHeight.get()));
         default:
            return -1.0;
      }
   }

   public boolean inAttackRange(class_238 bb, class_243 from) {
      return this.inAttackRange(bb, this.getFeet(bb), from);
   }

   public boolean inAttackRange(class_238 bb, class_243 feet, class_243 from) {
      if (this.mc.field_1724 == null) {
         return false;
      } else if (SettingUtils.attackTrace(bb)) {
         return this.attackRangeTo(bb, feet, from, true) < (Double)this.attackRange.get();
      } else {
         return this.attackRangeTo(bb, feet, from, false) < (Double)this.attackRangeWalls.get();
      }
   }

   public boolean inAttackRangeNoTrace(class_238 bb, class_243 feet, class_243 from) {
      if (this.mc.field_1724 == null) {
         return false;
      } else {
         return this.attackRangeTo(bb, feet, from, true) <= Math.max((Double)this.attackRange.get(), (Double)this.attackRangeWalls.get());
      }
   }

   public double attackRangeTo(class_238 bb, class_243 feet, class_243 from, boolean countReduce) {
      class_238 pBB = this.mc.field_1724.method_5829();
      if (from == null) {
         from = this.mc.field_1724.method_33571();
         switch ((FromMode)this.attackRangeFrom.get()) {
            case Middle:
               ((IVec3d)from).set((pBB.field_1323 + pBB.field_1320) / 2.0, (pBB.field_1322 + pBB.field_1325) / 2.0, (pBB.field_1321 + pBB.field_1324) / 2.0);
               break;
            case Feet:
               from = this.mc.field_1724.method_19538();
         }
      } else {
         switch ((FromMode)this.attackRangeFrom.get()) {
            case Middle:
               from = from.method_1031(0.0, (double)(this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()) / 2.0F), 0.0);
               break;
            case Eyes:
               from = from.method_1031(0.0, (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), 0.0);
         }
      }

      double var10000;
      switch ((AttackRangeMode)this.attackRangeMode.get()) {
         case Height:
            var10000 = this.getRange(from, feet.method_1031(0.0, (Double)this.attackHeight.get(), 0.0));
            break;
         case NCP:
            var10000 = this.getRange(from, new class_243(feet.field_1352, Math.min(Math.max(from.method_10214(), bb.field_1322), bb.field_1325), feet.field_1350));
            break;
         case Vanilla:
            var10000 = this.getRange(from, LemonUtils.getClosest(this.mc.field_1724.method_33571(), feet, Math.abs(bb.field_1323 - bb.field_1320), Math.abs(bb.field_1322 - bb.field_1325)));
            break;
         case Middle:
            var10000 = this.getRange(from, new class_243((bb.field_1323 + bb.field_1320) / 2.0, (bb.field_1322 + bb.field_1325) / 2.0, (bb.field_1321 + bb.field_1324) / 2.0));
            break;
         case CustomBox:
            var10000 = this.getRange(from, LemonUtils.getClosest(this.mc.field_1724.method_33571(), feet, Math.abs(bb.field_1323 - bb.field_1320) * (Double)this.closestAttackWidth.get(), Math.abs(bb.field_1322 - bb.field_1325) * (Double)this.closestAttackHeight.get()));
            break;
         case UpdatedNCP:
            var10000 = this.getRange(from, new class_243(feet.field_1352, Math.min(Math.max(from.method_10214(), bb.field_1322), bb.field_1325), feet.field_1350)) - this.getDistFromCenter(bb, feet, from);
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      double dist = var10000;
      return dist * (countReduce && (Boolean)this.reduce.get() ? this.rangeMulti : 1.0);
   }

   public double getDistFromCenter(class_238 bb, class_243 feet, class_243 from) {
      class_243 startPos = new class_243(feet.field_1352, Math.min(Math.max(from.method_10214(), bb.field_1322), bb.field_1325), feet.field_1350);
      class_243 rangePos = new class_243(feet.field_1352, Math.min(Math.max(from.method_10214(), bb.field_1322), bb.field_1325), feet.field_1350);
      double halfWidth = Math.abs(bb.field_1323 - bb.field_1320) / 2.0;
      if (from.field_1352 == rangePos.field_1352 && from.field_1350 == rangePos.field_1350) {
         return 0.0;
      } else {
         class_243 dist = new class_243(from.field_1352 - rangePos.field_1352, 0.0, from.field_1350 - rangePos.field_1350);
         if (this.getDistXZ(dist) < halfWidth * Math.sqrt(2.0)) {
            return 0.0;
         } else {
            if (dist.method_10215() > 0.0) {
               ((IVec3d)rangePos).setXZ(rangePos.field_1352, rangePos.field_1350 + halfWidth);
            } else if (dist.method_10215() < 0.0) {
               ((IVec3d)rangePos).setXZ(rangePos.field_1352, rangePos.field_1350 - halfWidth);
            } else if (dist.method_10216() > 0.0) {
               ((IVec3d)rangePos).setXZ(rangePos.field_1352 + halfWidth, rangePos.field_1350);
            } else {
               ((IVec3d)rangePos).setXZ(rangePos.field_1352 - halfWidth, rangePos.field_1350);
            }

            class_243 vec2 = rangePos.method_1020(startPos);
            double angle = RotationUtils.radAngle(new class_241((float)dist.field_1352, (float)dist.field_1350), new class_241((float)vec2.field_1352, (float)vec2.field_1350));
            if (angle > 0.7853981633974483) {
               angle = 1.5707963267948966 - angle;
            }

            return angle >= 0.0 && angle <= 0.7853981633974483 ? halfWidth / Math.cos(angle) : 0.0;
         }
      }
   }

   private double getRange(class_243 from, class_243 to) {
      double x = Math.abs(from.field_1352 - to.field_1352);
      double y = Math.abs(from.field_1351 - to.field_1351);
      double z = Math.abs(from.field_1350 - to.field_1350);
      return Math.sqrt(x * x + y * y + z * z);
   }

   private class_243 getFeet(class_238 bb) {
      return new class_243((bb.field_1323 + bb.field_1320) / 2.0, bb.field_1322, (bb.field_1321 + bb.field_1324) / 2.0);
   }

   public boolean inMineRange(class_2338 pos) {
      if (this.mc.field_1724 == null) {
         return false;
      } else {
         double dist = this.miningRangeTo(pos, (class_243)null);
         return dist >= 0.0 && dist <= SettingUtils.placeTrace(pos) ? (Double)this.miningRange.get() : (Double)this.miningRangeWalls.get();
      }
   }

   public boolean inMineRangeNoTrace(class_2338 pos) {
      if (this.mc.field_1724 == null) {
         return false;
      } else {
         double dist = this.miningRangeTo(pos, (class_243)null);
         return dist >= 0.0 && dist <= Math.max((Double)this.miningRange.get(), (Double)this.miningRangeWalls.get());
      }
   }

   public double miningRangeTo(class_2338 pos, class_243 from) {
      class_238 pBB = this.mc.field_1724.method_5829();
      class_243 pPos = this.mc.field_1724.method_19538();
      if (from == null) {
         from = this.mc.field_1724.method_33571();
         switch ((FromMode)this.miningRangeFrom.get()) {
            case Middle:
               ((IVec3d)from).set((pBB.field_1323 + pBB.field_1320) / 2.0, (pBB.field_1322 + pBB.field_1325) / 2.0, (pBB.field_1323 + pBB.field_1320) / 2.0);
               break;
            case Feet:
               ((IVec3d)from).set(pPos.field_1352, pPos.field_1351, pPos.field_1350);
         }
      }

      class_243 feet = new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264(), (double)pos.method_10260() + 0.5);
      switch ((MiningRangeMode)this.miningRangeMode.get()) {
         case NCP:
            return this.getRange(from, feet.method_1031(0.0, 0.5, 0.0));
         case Height:
            return this.getRange(from, feet.method_1031(0.0, (Double)this.miningHeight.get(), 0.0));
         case Vanilla:
            return this.getRange(from, LemonUtils.getClosest(this.mc.field_1724.method_33571(), feet, 1.0, 1.0));
         case CustomBox:
            return this.getRange(from, LemonUtils.getClosest(this.mc.field_1724.method_33571(), feet, (Double)this.closestMiningWidth.get(), (Double)this.closestMiningHeight.get()));
         default:
            return -1.0;
      }
   }

   private double getDistXZ(class_243 vec) {
      return Math.sqrt(vec.field_1352 * vec.field_1352 + vec.field_1350 * vec.field_1350);
   }

   public void registerAttack(class_238 bb) {
      if (this.attackRangeTo(bb, this.getFeet(bb), (class_243)null, false) <= (Double)this.attackRange.get() - (Double)this.reduceAmount.get()) {
         this.rangeMulti = Math.min(this.rangeMulti + (Double)this.reduceStep.get(), 1.0);
      } else {
         this.rangeMulti = Math.max(this.rangeMulti - (Double)this.reduceStep.get(), ((Double)this.attackRange.get() - (Double)this.reduceStep.get() / (Double)this.attackRange.get()) / (Double)this.attackRange.get());
      }

   }

   public static enum FromMode {
      Eyes,
      Middle,
      Feet;

      // $FF: synthetic method
      private static FromMode[] $values() {
         return new FromMode[]{Eyes, Middle, Feet};
      }
   }

   public static enum PlaceRangeMode {
      NCP,
      Height,
      Vanilla,
      CustomBox;

      // $FF: synthetic method
      private static PlaceRangeMode[] $values() {
         return new PlaceRangeMode[]{NCP, Height, Vanilla, CustomBox};
      }
   }

   public static enum AttackRangeMode {
      NCP,
      UpdatedNCP,
      Height,
      Vanilla,
      Middle,
      CustomBox;

      // $FF: synthetic method
      private static AttackRangeMode[] $values() {
         return new AttackRangeMode[]{NCP, UpdatedNCP, Height, Vanilla, Middle, CustomBox};
      }
   }

   public static enum MiningRangeMode {
      NCP,
      Height,
      Vanilla,
      CustomBox;

      // $FF: synthetic method
      private static MiningRangeMode[] $values() {
         return new MiningRangeMode[]{NCP, Height, Vanilla, CustomBox};
      }
   }
}
