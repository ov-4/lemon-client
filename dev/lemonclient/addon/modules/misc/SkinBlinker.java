package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1664;

public class SkinBlinker extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting mode;
   private final Setting seqMode;
   private final Setting sequentialDelay;
   private final Setting cape;
   private final Setting capeDelay;
   private final Setting head;
   private final Setting headDelay;
   private final Setting jacket;
   private final Setting jacketDelay;
   private final Setting leftArm;
   private final Setting leftArmDelay;
   private final Setting rightArm;
   private final Setting rightArmDelay;
   private final Setting leftLeg;
   private final Setting leftLegDelay;
   private final Setting rightLeg;
   private final Setting rightLegDelay;
   private int ticksPassed;
   private int headTimer;
   private int jacketTimer;
   private int leftArmTimer;
   private int rightArmTimer;
   private int leftLegTimer;
   private int rightLegTimer;
   private int capeTimer;

   public SkinBlinker() {
      super(LemonClient.Misc, "Skin Blinker", "Blinks different parts of your skin.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("What mode the skin blinker should behave in.")).defaultValue(SkinBlinker.Mode.Sequential)).build());
      this.seqMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("sequential-mode")).description("Whether to toggle your skin parts on or off.")).defaultValue(SkinBlinker.SequentialMode.On)).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Sequential;
      })).build());
      this.sequentialDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("sequential-delay")).description("Delay in ticks between each part of skin to toggle.")).defaultValue(5)).min(1).sliderRange(1, 15).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Sequential;
      })).build());
      this.cape = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("cape")).description("Blinks the cape part of your skin (only works if you have a Mojang cape).")).defaultValue(true)).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual;
      })).build());
      this.capeDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("cape-delay")).description("Delay in ticks between toggling the cape part of the skin.")).defaultValue(10)).min(1).sliderRange(1, 15).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual && (Boolean)this.cape.get();
      })).build());
      this.head = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("head")).description("Blinks the head part of your skin.")).defaultValue(true)).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual;
      })).build());
      this.headDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("head-delay")).description("Delay in ticks between toggling the head part of the skin.")).defaultValue(10)).min(1).sliderRange(1, 15).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual && (Boolean)this.head.get();
      })).build());
      this.jacket = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("jacket")).description("Blinks the torso part of your skin.")).defaultValue(true)).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual;
      })).build());
      this.jacketDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("jacket-delay")).description("Delay in ticks between toggling the jacket part of the skin.")).defaultValue(10)).min(1).sliderRange(1, 15).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual && (Boolean)this.jacket.get();
      })).build());
      this.leftArm = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("left-arm")).description("Blinks the left arm of your skin.")).defaultValue(true)).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual;
      })).build());
      this.leftArmDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("left-arm-delay")).description("Delay in ticks between toggling the left arm part of the skin.")).defaultValue(10)).min(1).sliderRange(1, 15).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual && (Boolean)this.leftArm.get();
      })).build());
      this.rightArm = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("right-arm")).description("Blinks the right arm of your skin.")).defaultValue(true)).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual;
      })).build());
      this.rightArmDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("right-arm-delay")).description("Delay in ticks between toggling the right arm part of the skin.")).defaultValue(10)).min(1).sliderRange(1, 15).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual && (Boolean)this.rightArm.get();
      })).build());
      this.leftLeg = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("left-leg")).description("Blinks the head left leg of your skin.")).defaultValue(true)).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual;
      })).build());
      this.leftLegDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("left-leg-delay")).description("Delay in ticks between toggling the left leg part of the skin.")).defaultValue(10)).min(1).sliderRange(1, 15).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual && (Boolean)this.leftLeg.get();
      })).build());
      this.rightLeg = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("right-leg")).description("Blinks the head right leg of your skin.")).defaultValue(true)).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual;
      })).build());
      this.rightLegDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("right-leg-delay")).description("Delay in ticks between toggling the right leg part of the skin.")).defaultValue(10)).min(1).sliderRange(1, 15).visible(() -> {
         return this.mode.get() == SkinBlinker.Mode.Individual && (Boolean)this.rightLeg.get();
      })).build());
   }

   public void onActivate() {
      this.ticksPassed = 0;
      this.headTimer = 0;
      this.jacketTimer = 0;
      this.capeTimer = 0;
      this.leftArmTimer = 0;
      this.rightArmTimer = 0;
      this.leftLegTimer = 0;
      this.rightLegTimer = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mode.get() == SkinBlinker.Mode.Sequential) {
         if (this.ticksPassed < (Integer)this.sequentialDelay.get() * 5) {
            ++this.ticksPassed;
         } else {
            this.ticksPassed = 0;
         }

         if (this.ticksPassed > 0) {
            this.mc.field_1690.method_1631(class_1664.field_7563, this.hat());
         }

         if (this.ticksPassed > (Integer)this.sequentialDelay.get()) {
            this.mc.field_1690.method_1631(class_1664.field_7568, this.arm());
            this.mc.field_1690.method_1631(class_1664.field_7570, this.arm());
         }

         if (this.ticksPassed > (Integer)this.sequentialDelay.get() * 2) {
            this.mc.field_1690.method_1631(class_1664.field_7564, this.mid());
         }

         if (this.ticksPassed > (Integer)this.sequentialDelay.get() * 3) {
            this.mc.field_1690.method_1631(class_1664.field_7566, this.legs());
            this.mc.field_1690.method_1631(class_1664.field_7565, this.legs());
         }
      } else {
         if ((Boolean)this.cape.get()) {
            if (this.capeTimer < (Integer)this.capeDelay.get() * 2) {
               ++this.capeTimer;
               this.mc.field_1690.method_1631(class_1664.field_7559, this.capeTimer <= (Integer)this.capeDelay.get());
            } else {
               this.capeTimer = 0;
            }
         }

         if ((Boolean)this.head.get()) {
            if (this.headTimer < (Integer)this.headDelay.get() * 2) {
               ++this.headTimer;
               this.mc.field_1690.method_1631(class_1664.field_7563, this.headTimer <= (Integer)this.headDelay.get());
            } else {
               this.headTimer = 0;
            }
         }

         if ((Boolean)this.jacket.get()) {
            if (this.jacketTimer < (Integer)this.jacketDelay.get() * 2) {
               ++this.jacketTimer;
               this.mc.field_1690.method_1631(class_1664.field_7564, this.jacketTimer <= (Integer)this.jacketDelay.get());
            } else {
               this.jacketTimer = 0;
            }
         }

         if ((Boolean)this.leftArm.get()) {
            if (this.leftArmTimer < (Integer)this.leftArmDelay.get() * 2) {
               ++this.leftArmTimer;
               this.mc.field_1690.method_1631(class_1664.field_7568, this.leftArmTimer <= (Integer)this.leftArmDelay.get());
            } else {
               this.leftArmTimer = 0;
            }
         }

         if ((Boolean)this.rightArm.get()) {
            if (this.rightArmTimer < (Integer)this.rightArmDelay.get() * 2) {
               ++this.rightArmTimer;
               this.mc.field_1690.method_1631(class_1664.field_7570, this.rightArmTimer <= (Integer)this.rightArmDelay.get());
            } else {
               this.rightArmTimer = 0;
            }
         }

         if ((Boolean)this.leftLeg.get()) {
            if (this.leftLegTimer < (Integer)this.leftLegDelay.get() * 2) {
               ++this.leftLegTimer;
               this.mc.field_1690.method_1631(class_1664.field_7566, this.leftLegTimer <= (Integer)this.leftLegDelay.get());
            } else {
               this.leftLegTimer = 0;
            }
         }

         if ((Boolean)this.rightLeg.get()) {
            if (this.rightLegTimer < (Integer)this.rightLegDelay.get() * 2) {
               ++this.rightLegTimer;
               this.mc.field_1690.method_1631(class_1664.field_7565, this.rightLegTimer <= (Integer)this.rightLegDelay.get());
            } else {
               this.rightLegTimer = 0;
            }
         }
      }

   }

   private boolean hat() {
      if (this.seqMode.get() == SkinBlinker.SequentialMode.Off) {
         return this.ticksPassed <= (Integer)this.sequentialDelay.get();
      } else {
         return this.ticksPassed > (Integer)this.sequentialDelay.get();
      }
   }

   private boolean arm() {
      if (this.seqMode.get() == SkinBlinker.SequentialMode.Off) {
         return this.ticksPassed <= (Integer)this.sequentialDelay.get() * 2;
      } else {
         return this.ticksPassed > (Integer)this.sequentialDelay.get() * 2;
      }
   }

   private boolean mid() {
      if (this.seqMode.get() == SkinBlinker.SequentialMode.Off) {
         return this.ticksPassed <= (Integer)this.sequentialDelay.get() * 3;
      } else {
         return this.ticksPassed > (Integer)this.sequentialDelay.get() * 3;
      }
   }

   private boolean legs() {
      if (this.seqMode.get() == SkinBlinker.SequentialMode.Off) {
         return this.ticksPassed <= (Integer)this.sequentialDelay.get() * 4;
      } else {
         return this.ticksPassed > (Integer)this.sequentialDelay.get() * 4;
      }
   }

   public static enum Mode {
      Sequential,
      Individual;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Sequential, Individual};
      }
   }

   public static enum SequentialMode {
      On,
      Off;

      // $FF: synthetic method
      private static SequentialMode[] $values() {
         return new SequentialMode[]{On, Off};
      }
   }
}
