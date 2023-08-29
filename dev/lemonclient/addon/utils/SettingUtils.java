package dev.lemonclient.addon.utils;

import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingState;
import dev.lemonclient.addon.enums.SwingType;
import dev.lemonclient.addon.modules.settings.FacingSettings;
import dev.lemonclient.addon.modules.settings.RangeSettings;
import dev.lemonclient.addon.modules.settings.RaytraceSettings;
import dev.lemonclient.addon.modules.settings.RotationSettings;
import dev.lemonclient.addon.modules.settings.ServerSettings;
import dev.lemonclient.addon.modules.settings.SwingSettings;
import dev.lemonclient.addon.utils.player.PlaceData;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1268;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class SettingUtils extends Utils {
   private static final FacingSettings facing = (FacingSettings)Modules.get().get(FacingSettings.class);
   private static final RangeSettings range = (RangeSettings)Modules.get().get(RangeSettings.class);
   private static final RaytraceSettings raytrace = (RaytraceSettings)Modules.get().get(RaytraceSettings.class);
   private static final RotationSettings rotation = (RotationSettings)Modules.get().get(RotationSettings.class);
   private static final ServerSettings server = (ServerSettings)Modules.get().get(ServerSettings.class);
   private static final SwingSettings swing = (SwingSettings)Modules.get().get(SwingSettings.class);

   public static void registerAttack(class_238 bb) {
      range.registerAttack(bb);
   }

   public static double getPlaceRange() {
      return (Double)range.placeRange.get();
   }

   public static double getPlaceWallsRange() {
      return (Double)range.placeRangeWalls.get();
   }

   public static double getAttackRange() {
      return (Double)range.attackRange.get();
   }

   public static double getAttackWallsRange() {
      return (Double)range.attackRangeWalls.get();
   }

   public static double getMineRange() {
      return (Double)range.miningRange.get();
   }

   public static double getMineWallsRange() {
      return (Double)range.miningRangeWalls.get();
   }

   public static double placeRangeTo(class_2338 pos) {
      return range.placeRangeTo(pos, (class_243)null);
   }

   public static boolean inPlaceRange(class_2338 pos) {
      return range.inPlaceRange(pos, (class_243)null);
   }

   public static boolean inPlaceRange(class_2338 pos, class_243 from) {
      return range.inPlaceRange(pos, from);
   }

   public static boolean inPlaceRangeNoTrace(class_2338 pos) {
      return range.inPlaceRangeNoTrace(pos, (class_243)null);
   }

   public static boolean inPlaceRangeNoTrace(class_2338 pos, class_243 from) {
      return range.inPlaceRangeNoTrace(pos, from);
   }

   public static boolean inAttackRange(class_238 bb) {
      return range.inAttackRange(bb, (class_243)null);
   }

   public static boolean inAttackRange(class_238 bb, class_243 from) {
      return range.inAttackRange(bb, from);
   }

   public static double mineRangeTo(class_2338 pos) {
      return range.miningRangeTo(pos, (class_243)null);
   }

   public static boolean inMineRange(class_2338 pos) {
      return range.inMineRange(pos);
   }

   public static boolean inMineRangeNoTrace(class_2338 pos) {
      return range.inMineRangeNoTrace(pos);
   }

   public static boolean inAttackRangeNoTrace(class_238 bb, double eyeHeight, class_243 feet) {
      return range.inAttackRangeNoTrace(bb, feet, (class_243)null);
   }

   public static boolean inAttackRangeNoTrace(class_238 bb, double eyeHeight, class_243 feet, class_243 from) {
      return range.inAttackRangeNoTrace(bb, feet, from);
   }

   public static double attackRangeTo(class_238 bb, class_243 feet) {
      return range.attackRangeTo(bb, feet, (class_243)null, true);
   }

   public static boolean startMineRot() {
      return rotation.startMineRot();
   }

   public static boolean endMineRot() {
      return rotation.endMineRot();
   }

   public static boolean shouldVanillaRotate() {
      return (Boolean)rotation.vanillaRotation.get();
   }

   public static boolean shouldRotate(RotationType type) {
      return rotation.shouldRotate(type);
   }

   public static boolean rotationCheck(class_238 box, RotationType type) {
      return rotation.rotationCheck(box, type);
   }

   public static void swing(SwingState state, SwingType type, class_1268 hand) {
      swing.swing(state, type, hand);
   }

   public static void mineSwing(SwingSettings.MiningSwingState state) {
      swing.mineSwing(state);
   }

   public static PlaceData getPlaceData(class_2338 pos) {
      return facing.getPlaceData(pos, true);
   }

   public static PlaceData getPlaceDataANDDir(class_2338 pos, Predicate predicate) {
      return facing.getPlaceDataAND(pos, predicate, (Predicate)null, true);
   }

   public static PlaceData getPlaceDataANDPos(class_2338 pos, Predicate predicate) {
      return facing.getPlaceDataAND(pos, (Predicate)null, predicate, true);
   }

   public static PlaceData getPlaceDataAND(class_2338 pos, Predicate predicateDir, Predicate predicate) {
      return facing.getPlaceDataAND(pos, predicateDir, predicate, true);
   }

   public static PlaceData getPlaceDataOR(class_2338 pos, Predicate predicate) {
      return facing.getPlaceDataOR(pos, predicate, true);
   }

   public static PlaceData getPlaceData(class_2338 pos, boolean ignoreContainers) {
      return facing.getPlaceData(pos, ignoreContainers);
   }

   public static PlaceData getPlaceDataANDDir(class_2338 pos, Predicate predicate, boolean ignoreContainers) {
      return facing.getPlaceDataAND(pos, predicate, (Predicate)null, ignoreContainers);
   }

   public static PlaceData getPlaceDataANDPos(class_2338 pos, Predicate predicate, boolean ignoreContainers) {
      return facing.getPlaceDataAND(pos, (Predicate)null, predicate, ignoreContainers);
   }

   public static PlaceData getPlaceDataAND(class_2338 pos, Predicate predicateDir, Predicate predicate, boolean ignoreContainers) {
      return facing.getPlaceDataAND(pos, predicateDir, predicate, ignoreContainers);
   }

   public static PlaceData getPlaceDataOR(class_2338 pos, Predicate predicate, boolean ignoreContainers) {
      return facing.getPlaceDataOR(pos, predicate, ignoreContainers);
   }

   public static class_2350 getPlaceOnDirection(class_2338 pos) {
      return facing.getPlaceOnDirection(pos);
   }

   public static boolean shouldPlaceTrace() {
      return (Boolean)raytrace.placeTrace.get();
   }

   public static boolean shouldAttackTrace() {
      return (Boolean)raytrace.attackTrace.get();
   }

   public static boolean placeTrace(class_2338 pos) {
      return !shouldPlaceTrace() || raytrace.placeTrace(pos);
   }

   public static boolean attackTrace(class_238 bb) {
      return !shouldAttackTrace() || raytrace.attackTrace(bb);
   }

   public static boolean oldDamage() {
      return (Boolean)server.oldVerDamage.get();
   }

   public static boolean oldCrystals() {
      return (Boolean)server.oldVerCrystals.get();
   }

   public static boolean cc() {
      return (Boolean)server.cc.get();
   }
}
