package dev.lemonclient.addon.modules.settings;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.managers.impl.RotationManager;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.player.NCPRaytracer;
import dev.lemonclient.addon.utils.player.RotationUtils;
import java.util.List;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2404;
import net.minecraft.class_243;
import net.minecraft.class_2510;
import net.minecraft.class_2680;

public class RotationSettings extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgInteract;
   private final SettingGroup sgBlockPlace;
   private final SettingGroup sgMining;
   private final SettingGroup sgAttack;
   private final SettingGroup sgUse;
   public final Setting vanillaRotation;
   public final Setting yawStep;
   public final Setting pitchStep;
   public final Setting yawRandomization;
   public final Setting pitchRandomization;
   private final Setting interactRotate;
   public final Setting interactTime;
   public final Setting interactMode;
   public final Setting interactYawAngle;
   public final Setting interactPitchAngle;
   public final Setting interactMemory;
   private final Setting blockRotate;
   public final Setting blockTime;
   public final Setting blockMode;
   public final Setting blockYawAngle;
   public final Setting blockPitchAngle;
   public final Setting blockMemory;
   private final Setting mineRotate;
   public final Setting mineTime;
   public final Setting mineMode;
   public final Setting mineTiming;
   public final Setting mineYawAngle;
   public final Setting minePitchAngle;
   public final Setting mineMemory;
   private final Setting attackRotate;
   public final Setting attackTime;
   public final Setting attackMode;
   public final Setting attackYawAngle;
   public final Setting attackPitchAngle;
   public final Setting attackMemory;
   public final Setting useTime;
   public final class_243 vec;

   public RotationSettings() {
      super(LemonClient.Settings, "Rotate", "Global rotation settings for every lemon module.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgInteract = this.settings.createGroup("Interact");
      this.sgBlockPlace = this.settings.createGroup("Block Place");
      this.sgMining = this.settings.createGroup("Mining");
      this.sgAttack = this.settings.createGroup("Attack");
      this.sgUse = this.settings.createGroup("Use");
      this.vanillaRotation = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Vanilla Rotation")).description("Turns your head.")).defaultValue(false)).build());
      this.yawStep = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Yaw Step")).description("How many yaw degrees should be rotated each packet.")).defaultValue(90.0).range(0.0, 180.0).sliderRange(0.0, 180.0).build());
      this.pitchStep = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Pitch Step")).description("How many pitch degrees should be rotated each packet.")).defaultValue(45.0).range(0.0, 180.0).sliderRange(0.0, 180.0).build());
      this.yawRandomization = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Yaw Randomization")).description(".")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.pitchRandomization = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Pitch Randomization")).description(".")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.interactRotate = this.rotateSetting("Interact", "interacting with a block", this.sgInteract);
      this.interactTime = this.timeSetting("Interact", this.sgInteract);
      this.interactMode = this.modeSetting("Interact", this.sgInteract);
      this.interactYawAngle = this.yawAngleSetting("Interact", this.sgInteract, () -> {
         return this.interactMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.interactPitchAngle = this.pitchAngleSetting("Interact", this.sgInteract, () -> {
         return this.interactMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.interactMemory = this.memorySetting("Interact", this.sgInteract);
      this.blockRotate = this.rotateSetting("Block Place", "placing a block", this.sgBlockPlace);
      this.blockTime = this.timeSetting("Block Place", this.sgBlockPlace);
      this.blockMode = this.modeSetting("Block Place", this.sgBlockPlace);
      this.blockYawAngle = this.yawAngleSetting("Block Place", this.sgBlockPlace, () -> {
         return this.blockMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.blockPitchAngle = this.pitchAngleSetting("Block Place", this.sgBlockPlace, () -> {
         return this.blockMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.blockMemory = this.memorySetting("Block Place", this.sgBlockPlace);
      this.mineRotate = this.rotateSetting("Mining", "mining a block", this.sgMining);
      this.mineTime = this.timeSetting("Mining", this.sgMining);
      this.mineMode = this.modeSetting("Mining", this.sgMining);
      this.mineTiming = this.sgMining.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mining Rotate Timing")).description(".")).defaultValue(RotationSettings.MiningRotMode.End)).build());
      this.mineYawAngle = this.yawAngleSetting("Mining", this.sgMining, () -> {
         return this.mineMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.minePitchAngle = this.pitchAngleSetting("Mining", this.sgMining, () -> {
         return this.mineMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.mineMemory = this.memorySetting("Mining", this.sgMining);
      this.attackRotate = this.rotateSetting("Attack", "attacking an entity", this.sgAttack);
      this.attackTime = this.timeSetting("Attack", this.sgAttack);
      this.attackMode = this.modeSetting("Attack", this.sgAttack);
      this.attackYawAngle = this.yawAngleSetting("Attack", this.sgAttack, () -> {
         return this.attackMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.attackPitchAngle = this.pitchAngleSetting("Attack", this.sgAttack, () -> {
         return this.attackMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.attackMemory = this.memorySetting("Attack", this.sgAttack);
      this.useTime = this.timeSetting("Use", this.sgUse);
      this.vec = new class_243(0.0, 0.0, 0.0);
   }

   private Setting rotateSetting(String type, String verb, SettingGroup sg) {
      return sg.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name(type + " Rotate")).description("Rotates when + " + verb)).defaultValue(false)).build());
   }

   private Setting timeSetting(String type, SettingGroup sg) {
      return sg.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name(type + " Rotation Time")).description("Keeps the rotation for x seconds after ending.")).defaultValue(0.5).min(0.0).sliderRange(0.0, 1.0).build());
   }

   private Setting modeSetting(String type, SettingGroup sg) {
      return sg.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name(type + " Rotation Mode")).description(".")).defaultValue(RotationSettings.RotationCheckMode.Raytrace)).build());
   }

   private Setting yawAngleSetting(String type, SettingGroup sg, IVisible visible) {
      return sg.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name(type + " Yaw Angle")).description("Accepts rotation if yaw angle to target is under this.")).defaultValue(90.0).range(0.0, 180.0).sliderRange(0.0, 180.0).visible(visible)).build());
   }

   private Setting pitchAngleSetting(String type, SettingGroup sg, IVisible visible) {
      return sg.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name(type + " Pitch Angle")).description("Accepts rotation if pitch angle to target is under this.")).defaultValue(45.0).range(0.0, 180.0).sliderRange(0.0, 180.0).visible(visible)).build());
   }

   private Setting memorySetting(String type, SettingGroup sg) {
      return sg.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name(type + " Memory")).description("Accepts rotation if looked at it x packets earlier.")).defaultValue(1)).range(1, 20).sliderRange(1, 20).build());
   }

   public boolean rotationCheck(class_238 box, RotationType type) {
      List history = RotationManager.history;
      if (box == null) {
         return false;
      } else {
         int r;
         RotationManager.Rotation rot;
         switch (this.mode(type)) {
            case Raytrace:
               for(r = 0; r < this.memory(type) && history.size() > r; ++r) {
                  rot = (RotationManager.Rotation)history.get(r);
                  if (this.raytraceCheck(rot.vec(), rot.yaw(), rot.pitch(), box)) {
                     return true;
                  }
               }

               return false;
            case StrictRaytrace:
               for(r = 0; r < this.memory(type) && history.size() > r; ++r) {
                  rot = (RotationManager.Rotation)history.get(r);
                  double range = 7.0;
                  class_243 end = (new class_243(range * Math.cos(Math.toRadians(rot.yaw() + 90.0)) * Math.abs(Math.cos(Math.toRadians(rot.pitch()))), range * -Math.sin(Math.toRadians(rot.pitch())), range * Math.sin(Math.toRadians(rot.yaw() + 90.0)) * Math.abs(Math.cos(Math.toRadians(rot.pitch()))))).method_1019(rot.vec());
                  if (NCPRaytracer.raytrace(rot.vec(), end, box)) {
                     return true;
                  }
               }

               return false;
            case Angle:
               for(r = 0; r < this.memory(type) && history.size() > r; ++r) {
                  rot = (RotationManager.Rotation)history.get(r);
                  if (this.angleCheck(rot.vec(), rot.yaw(), rot.pitch(), box, type)) {
                     return true;
                  }
               }
         }

         return false;
      }
   }

   public boolean shouldRotate(RotationType type) {
      boolean var10000;
      switch (type) {
         case Interact:
            var10000 = (Boolean)this.interactRotate.get();
            break;
         case BlockPlace:
            var10000 = (Boolean)this.blockRotate.get();
            break;
         case Attacking:
            var10000 = (Boolean)this.attackRotate.get();
            break;
         case Mining:
            var10000 = (Boolean)this.mineRotate.get();
            break;
         default:
            var10000 = true;
      }

      return var10000;
   }

   public RotationCheckMode mode(RotationType type) {
      RotationCheckMode var10000;
      switch (type) {
         case Interact:
            var10000 = (RotationCheckMode)this.interactMode.get();
            break;
         case BlockPlace:
            var10000 = (RotationCheckMode)this.blockMode.get();
            break;
         case Attacking:
            var10000 = (RotationCheckMode)this.attackMode.get();
            break;
         case Mining:
            var10000 = (RotationCheckMode)this.mineMode.get();
            break;
         default:
            var10000 = null;
      }

      return var10000;
   }

   public double time(RotationType type) {
      double var10000;
      switch (type) {
         case Interact:
            var10000 = (Double)this.interactTime.get();
            break;
         case BlockPlace:
            var10000 = (Double)this.blockTime.get();
            break;
         case Attacking:
            var10000 = (Double)this.attackTime.get();
            break;
         case Mining:
            var10000 = (Double)this.mineTime.get();
            break;
         case Use:
            var10000 = (Double)this.useTime.get();
            break;
         case Other:
            var10000 = 1.0;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public int memory(RotationType type) {
      int var10000;
      switch (type) {
         case Interact:
            var10000 = (Integer)this.interactMemory.get();
            break;
         case BlockPlace:
            var10000 = (Integer)this.blockMemory.get();
            break;
         case Attacking:
            var10000 = (Integer)this.attackMemory.get();
            break;
         case Mining:
            var10000 = (Integer)this.mineMemory.get();
            break;
         default:
            var10000 = 1;
      }

      return var10000;
   }

   public double yawStep(RotationType type) {
      double var10000;
      switch (type) {
         case Use:
         case Other:
            var10000 = 42069.0;
            break;
         default:
            var10000 = (Double)this.yawStep.get() + (Math.random() - 0.5) * 2.0 * (Double)this.yawRandomization.get();
      }

      return var10000;
   }

   public double pitchStep(RotationType type) {
      double var10000;
      switch (type) {
         case Use:
         case Other:
            var10000 = 42069.0;
            break;
         default:
            var10000 = (Double)this.pitchStep.get() + (Math.random() - 0.5) * 2.0 * (Double)this.pitchRandomization.get();
      }

      return var10000;
   }

   public double yawAngle(RotationType type) {
      double var10000;
      switch (type) {
         case Interact:
            var10000 = (Double)this.interactYawAngle.get();
            break;
         case BlockPlace:
            var10000 = (Double)this.blockYawAngle.get();
            break;
         case Attacking:
            var10000 = (Double)this.attackYawAngle.get();
            break;
         case Mining:
            var10000 = (Double)this.mineYawAngle.get();
            break;
         default:
            var10000 = 0.0;
      }

      return var10000;
   }

   public double pitchAngle(RotationType type) {
      double var10000;
      switch (type) {
         case Interact:
            var10000 = (Double)this.interactPitchAngle.get();
            break;
         case BlockPlace:
            var10000 = (Double)this.blockPitchAngle.get();
            break;
         case Attacking:
            var10000 = (Double)this.attackPitchAngle.get();
            break;
         case Mining:
            var10000 = (Double)this.minePitchAngle.get();
            break;
         default:
            var10000 = 0.0;
      }

      return var10000;
   }

   public boolean angleCheck(class_243 pos, double y, double p, class_238 box, RotationType type) {
      return RotationUtils.yawAngle(y, RotationUtils.getYaw(pos, box.method_1005())) <= this.yawAngle(type) && Math.abs(p - RotationUtils.getPitch(pos, box.method_1005())) <= this.pitchAngle(type);
   }

   public boolean raytraceCheck(class_243 pos, double y, double p, class_238 box) {
      double range = pos.method_1022(LemonUtils.getMiddle(box)) + 3.0;
      class_243 end = (new class_243(range * Math.cos(Math.toRadians(y + 90.0)) * Math.abs(Math.cos(Math.toRadians(p))), range * -Math.sin(Math.toRadians(p)), range * Math.sin(Math.toRadians(y + 90.0)) * Math.abs(Math.cos(Math.toRadians(p))))).method_1019(pos);

      for(float i = 0.0F; i < 1.0F; i = (float)((double)i + 0.01)) {
         if (box.method_1008(pos.field_1352 + (end.field_1352 - pos.field_1352) * (double)i, pos.field_1351 + (end.field_1351 - pos.field_1351) * (double)i, pos.field_1350 + (end.field_1350 - pos.field_1350) * (double)i)) {
            return true;
         }
      }

      return false;
   }

   private double lerp(double from, double to, double delta) {
      return from + (to - from) * delta;
   }

   public boolean validForCheck(class_2338 pos, class_2680 state) {
      if (state.method_51367()) {
         return true;
      } else if (state.method_26204() instanceof class_2404) {
         return false;
      } else if (state.method_26204() instanceof class_2510) {
         return false;
      } else {
         return state.method_31709() ? false : state.method_26234(this.mc.field_1687, pos);
      }
   }

   public boolean endMineRot() {
      if (!(Boolean)this.mineRotate.get()) {
         return false;
      } else {
         return this.mineTiming.get() == RotationSettings.MiningRotMode.End || this.mineTiming.get() == RotationSettings.MiningRotMode.Double;
      }
   }

   public boolean startMineRot() {
      if (!(Boolean)this.mineRotate.get()) {
         return false;
      } else {
         return this.mineTiming.get() == RotationSettings.MiningRotMode.Start || this.mineTiming.get() == RotationSettings.MiningRotMode.Double;
      }
   }

   public static enum MiningRotMode {
      Start,
      End,
      Double;

      // $FF: synthetic method
      private static MiningRotMode[] $values() {
         return new MiningRotMode[]{Start, End, Double};
      }
   }

   public static enum RotationCheckMode {
      Raytrace,
      StrictRaytrace,
      Angle;

      // $FF: synthetic method
      private static RotationCheckMode[] $values() {
         return new RotationCheckMode[]{Raytrace, StrictRaytrace, Angle};
      }
   }
}
