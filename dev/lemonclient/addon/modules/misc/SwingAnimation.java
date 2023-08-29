package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;

public class SwingAnimation extends LemonModule {
   private final SettingGroup sgMainHand;
   private final SettingGroup sgOffHand;
   private final Setting mSpeed;
   private final Setting mStart;
   private final Setting mEnd;
   private final Setting myStart;
   private final Setting myEnd;
   private final Setting mReset;
   private final Setting oSpeed;
   private final Setting oStart;
   private final Setting oEnd;
   private final Setting oyStart;
   private final Setting oyEnd;
   private final Setting oReset;
   private static boolean mainSwinging = false;
   private float mainProgress;
   private boolean offSwinging;
   private float offProgress;

   public SwingAnimation() {
      super(LemonClient.Misc, "Swing Animation", "Modifies swing rendering.");
      this.sgMainHand = this.settings.createGroup("Main Hand");
      this.sgOffHand = this.settings.createGroup("Off Hand");
      this.mSpeed = this.sgMainHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Main Speed")).description("Speed of swinging.")).defaultValue(1.0).min(0.0).sliderMax(10.0).build());
      this.mStart = this.sgMainHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Main Start Progress")).description("Starts swing at this progress.")).defaultValue(0.0).sliderMax(10.0).build());
      this.mEnd = this.sgMainHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Main End Progress")).description("Swings until reaching this progress.")).defaultValue(1.0).sliderMax(10.0).build());
      this.myStart = this.sgMainHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Main Start Y")).description("Hand Y value in the beginning.")).defaultValue(0.0).sliderRange(-10.0, 10.0).build());
      this.myEnd = this.sgMainHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Main End Y")).description("Hand Y value in the end.")).defaultValue(0.0).sliderRange(-10.0, 10.0).build());
      this.mReset = this.sgMainHand.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Reset")).description("Resets swing when swinging again.")).defaultValue(false)).build());
      this.oSpeed = this.sgOffHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Off Speed")).description("Speed of swinging for offhand")).defaultValue(1.0).min(0.0).sliderMax(10.0).build());
      this.oStart = this.sgOffHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Off Start Progress")).description("Starts swing at this progress.")).defaultValue(0.0).sliderMax(10.0).build());
      this.oEnd = this.sgOffHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Off End Progress")).description("Swings until reaching this progress.")).defaultValue(1.0).sliderMax(10.0).build());
      this.oyStart = this.sgOffHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Off Start Y")).description("Start Y value for offhand.")).defaultValue(0.0).sliderRange(-10.0, 10.0).build());
      this.oyEnd = this.sgOffHand.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Off End Y")).description("End Y value for offhand.")).defaultValue(0.0).sliderRange(-10.0, 10.0).build());
      this.oReset = this.sgOffHand.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Reset")).description("Resets swing when swinging again.")).defaultValue(false)).build());
      this.mainProgress = 0.0F;
      this.offSwinging = false;
      this.offProgress = 0.0F;
   }

   public void startSwing(class_1268 hand) {
      if (hand == class_1268.field_5808) {
         if ((Boolean)this.mReset.get() || !mainSwinging) {
            this.mainProgress = 0.0F;
            mainSwinging = true;
         }
      } else if ((Boolean)this.oReset.get() || !this.offSwinging) {
         this.offProgress = 0.0F;
         this.offSwinging = true;
      }

   }

   @EventHandler
   public void onRender(Render3DEvent event) {
      if (mainSwinging) {
         if (this.mainProgress >= 1.0F) {
            mainSwinging = false;
            this.mainProgress = 0.0F;
         } else {
            this.mainProgress = (float)((double)this.mainProgress + event.frameTime * (Double)this.mSpeed.get());
         }
      }

      if (this.offSwinging) {
         if (this.offProgress >= 1.0F) {
            this.offSwinging = false;
            this.offProgress = 0.0F;
         } else {
            this.offProgress = (float)((double)this.offProgress + event.frameTime * (Double)this.oSpeed.get());
         }
      }

   }

   public float getSwing(class_1268 hand) {
      return hand == class_1268.field_5808 ? (float)((Double)this.mStart.get() + ((Double)this.mEnd.get() - (Double)this.mStart.get()) * (double)this.mainProgress) : (float)((Double)this.oStart.get() + ((Double)this.oEnd.get() - (Double)this.oStart.get()) * (double)this.offProgress);
   }

   public float getY(class_1268 hand) {
      return hand == class_1268.field_5808 ? (float)((Double)this.myStart.get() + ((Double)this.myEnd.get() - (Double)this.myStart.get()) * (double)this.mainProgress) / -10.0F : (float)((Double)this.oyStart.get() + ((Double)this.oyEnd.get() - (Double)this.oyStart.get()) * (double)this.offProgress) / -10.0F;
   }
}
