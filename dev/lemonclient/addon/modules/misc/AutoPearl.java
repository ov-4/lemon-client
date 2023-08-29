package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.player.RotationUtils;
import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2828;

public class AutoPearl extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting ccBypass;
   private final Setting ccSwitchMode;
   private final Setting switchMode;
   private final Setting pitch;
   private final Setting instaRot;
   private final Setting swing;
   private final Setting swingHand;
   private boolean placed;

   public AutoPearl() {
      super(LemonClient.Combat, "Auto Pearl Clip", "Easily clip inside walls with pearls.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.ccBypass = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("CC Bypass")).description("Does funny stuff to bypass cc's anti delay.")).defaultValue(false)).build());
      this.ccSwitchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("CC Switch Mode")).description("Which method of switching should be used for cc items.")).defaultValue(AutoPearl.SwitchMode.Silent)).build());
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Which method of switching should be used.")).defaultValue(AutoPearl.SwitchMode.Silent)).build());
      this.pitch = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Pitch")).description("How deep down to look.")).defaultValue(85)).range(-90, 90).sliderRange(0, 90).build());
      this.instaRot = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant Rotation")).description("Instantly rotates.")).defaultValue(false)).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders swing animation when throwing an ender pearl.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.swing;
      Objects.requireNonNull(var10003);
      this.swingHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.placed = false;
   }

   public void onActivate() {
      this.placed = false;
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         class_1268 hand;
         label98: {
            hand = this.getHand();
            switch ((SwitchMode)this.switchMode.get()) {
               case Normal:
               case Silent:
                  if (InvUtils.findInHotbar(new class_1792[]{class_1802.field_8634}).found()) {
                     break label98;
                  }
                  break;
               case PickSilent:
               case InvSwitch:
                  if (InvUtils.find(new class_1792[]{class_1802.field_8634}).found()) {
                     break label98;
                  }
                  break;
               default:
                  throw new IncompatibleClassChangeError();
            }

            return;
         }

         if (!(Boolean)this.ccBypass.get() || this.cc() || this.placed) {
            boolean rotated = (Boolean)this.instaRot.get() || Managers.ROTATION.start((double)this.getYaw(), (double)(Integer)this.pitch.get(), (double)this.priority, RotationType.Other, (long)Objects.hash(new Object[]{this.name + "look"})) || RotationUtils.yawAngle((double)Managers.ROTATION.lastDir[0], (double)this.getYaw()) < 0.1 && (double)((float)(Integer)this.pitch.get() - Managers.ROTATION.lastDir[1]) < 0.1;
            if (rotated) {
               if ((Boolean)this.instaRot.get()) {
                  this.sendPacket(new class_2828.class_2831((float)this.getYaw(), (float)(Integer)this.pitch.get(), Managers.ON_GROUND.isOnGround()));
               }

               boolean switched = hand != null;
               if (!switched) {
                  switch ((SwitchMode)this.switchMode.get()) {
                     case Silent:
                        InvUtils.swap(InvUtils.findInHotbar(new class_1792[]{class_1802.field_8634}).slot(), true);
                        switched = true;
                        break;
                     case PickSilent:
                        switched = InventoryUtils.pickSwitch(InvUtils.find(new class_1792[]{class_1802.field_8634}).slot());
                        break;
                     case InvSwitch:
                        switched = InventoryUtils.invSwitch(InvUtils.find(new class_1792[]{class_1802.field_8634}).slot());
                  }
               }

               if (switched) {
                  this.useItem(hand == null ? class_1268.field_5808 : hand);
                  Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "look"}));
                  if ((Boolean)this.swing.get()) {
                     this.clientSwing((SwingHand)this.swingHand.get(), hand == null ? class_1268.field_5808 : hand);
                  }

                  this.toggle();
                  this.sendToggledMsg("success");
                  if (hand == null) {
                     switch ((SwitchMode)this.switchMode.get()) {
                        case Silent:
                           InvUtils.swapBack();
                           break;
                        case PickSilent:
                           InventoryUtils.pickSwapBack();
                           break;
                        case InvSwitch:
                           InventoryUtils.swapBack();
                     }
                  }

               }
            }
         }
      }
   }

   private boolean cc() {
      label94: {
         switch ((SwitchMode)this.ccSwitchMode.get()) {
            case Normal:
            case Silent:
               if (!InvUtils.findInHotbar((item) -> {
                  return item.method_7909() instanceof class_1747;
               }).found()) {
                  break label94;
               }
               break;
            case PickSilent:
            case InvSwitch:
               if (!InvUtils.find((item) -> {
                  return item.method_7909() instanceof class_1747;
               }).found()) {
                  break label94;
               }
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         class_2338 pos = this.mc.field_1724.method_24515();
         boolean rotated = (Boolean)this.instaRot.get() || !SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(pos.method_10074(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
         if (!rotated) {
            return false;
         }

         if ((Boolean)this.instaRot.get()) {
            this.sendPacket(new class_2828.class_2831((float)RotationUtils.getYaw(this.mc.field_1724.method_33571(), pos.method_46558()), (float)RotationUtils.getPitch(this.mc.field_1724.method_33571(), pos.method_46558()), Managers.ON_GROUND.isOnGround()));
         }

         class_1268 hand = this.mc.field_1724.method_6079().method_7909() instanceof class_1747 ? class_1268.field_5810 : (Managers.HOLDING.getStack().method_7909() instanceof class_1747 ? class_1268.field_5808 : null);
         boolean switched = false;
         if (hand == null) {
            switch ((SwitchMode)this.ccSwitchMode.get()) {
               case Silent:
                  InvUtils.swap(InvUtils.findInHotbar((item) -> {
                     return item.method_7909() instanceof class_1747;
                  }).slot(), true);
                  switched = true;
                  break;
               case PickSilent:
                  switched = InventoryUtils.pickSwitch(InvUtils.find((item) -> {
                     return item.method_7909() instanceof class_1747;
                  }).slot());
                  break;
               case InvSwitch:
                  switched = InventoryUtils.invSwitch(InvUtils.find((item) -> {
                     return item.method_7909() instanceof class_1747;
                  }).slot());
            }
         }

         if (hand == null && !switched) {
            return false;
         }

         this.placeBlock(hand == null ? class_1268.field_5808 : hand, pos.method_10074().method_46558(), class_2350.field_11036, pos.method_10074());
         if (!(Boolean)this.instaRot.get() && SettingUtils.shouldRotate(RotationType.BlockPlace)) {
            Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
         }

         this.placed = true;
         if (hand == null) {
            switch ((SwitchMode)this.ccSwitchMode.get()) {
               case Silent:
                  InvUtils.swapBack();
                  break;
               case PickSilent:
                  InventoryUtils.pickSwapBack();
                  break;
               case InvSwitch:
                  InventoryUtils.swapBack();
            }
         }

         return true;
      }

      this.toggle();
      this.sendToggledMsg("cc blocks not found");
      return false;
   }

   private int getYaw() {
      return (int)Math.round(Rotations.getYaw(new class_243(Math.floor(this.mc.field_1724.method_23317()) + 0.5, 0.0, Math.floor(this.mc.field_1724.method_23321()) + 0.5))) + 180;
   }

   private class_1268 getHand() {
      if (Managers.HOLDING.isHolding(class_1802.field_8634)) {
         return class_1268.field_5808;
      } else {
         return this.mc.field_1724.method_6079().method_7909() == class_1802.field_8634 ? class_1268.field_5810 : null;
      }
   }

   public static enum SwitchMode {
      Normal,
      Silent,
      PickSilent,
      InvSwitch;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Normal, Silent, PickSilent, InvSwitch};
      }
   }
}
