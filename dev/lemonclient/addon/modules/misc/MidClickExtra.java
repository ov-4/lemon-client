package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import meteordevelopment.meteorclient.events.entity.player.FinishUsingItemEvent;
import meteordevelopment.meteorclient.events.entity.player.StoppedUsingItemEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1753;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public class MidClickExtra extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting mode;
   private final Setting switchMode;
   private final Setting noInventory;
   private final Setting notify;
   private boolean isUsing;

   public MidClickExtra() {
      super(LemonClient.Combat, "Mid Click Extra", "Lets you use items when you middle click.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description("Which item to use when you middle click.")).defaultValue(MidClickExtra.Mode.Pearl)).build());
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).defaultValue(MidClickExtra.SwitchMode.InvSwitch)).build());
      this.noInventory = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Anti-inventory")).description("Not work in inventory.")).defaultValue(true)).build());
      this.notify = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("notify")).description("Notifies you when you do not have the specified item in your hotbar.")).defaultValue(true)).build());
   }

   public void onDeactivate() {
      this.stopIfUsing();
   }

   @EventHandler
   private void onMouseButton(MouseButtonEvent event) {
      if (event.action == KeyAction.Press && event.button == 2) {
         if (!(Boolean)this.noInventory.get() || this.mc.field_1755 == null) {
            FindItemResult result = InvUtils.findInHotbar(new class_1792[]{((Mode)this.mode.get()).item});
            FindItemResult invResult = InvUtils.find(new class_1792[]{((Mode)this.mode.get()).item});
            if (!((SwitchMode)this.switchMode.get()).equals(MidClickExtra.SwitchMode.InvSwitch)) {
               if (!result.found()) {
                  if ((Boolean)this.notify.get()) {
                     this.warning("Unable to find specified item.", new Object[0]);
                  }

                  return;
               }
            } else if (!invResult.found()) {
               if ((Boolean)this.notify.get()) {
                  this.warning("Unable to find specified item.", new Object[0]);
               }

               return;
            }

            switch ((SwitchMode)this.switchMode.get()) {
               case Silent:
                  InvUtils.swap(result.slot(), true);
                  break;
               case InvSwitch:
                  InventoryUtils.invSwitch(invResult.slot());
                  break;
               case PickSilent:
                  InventoryUtils.pickSwitch(result.slot());
            }

            switch (((Mode)this.mode.get()).type) {
               case Immediate:
                  if (this.mc.field_1761 != null) {
                     this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                     InvUtils.swapBack();
                  }
                  break;
               case LongerSingleClick:
                  if (this.mc.field_1761 != null) {
                     this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
                  }
                  break;
               case Longer:
                  this.mc.field_1690.field_1904.method_23481(true);
                  this.isUsing = true;
            }

            switch ((SwitchMode)this.switchMode.get()) {
               case Silent:
                  InvUtils.swapBack();
                  break;
               case InvSwitch:
                  InventoryUtils.swapBack();
                  break;
               case PickSilent:
                  InventoryUtils.pickSwapBack();
            }

         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.isUsing) {
         boolean pressed = true;
         if (this.mc.field_1724 != null && this.mc.field_1724.method_6047().method_7909() instanceof class_1753) {
            pressed = class_1753.method_7722(this.mc.field_1724.method_6048()) < 1.0F;
         }

         this.mc.field_1690.field_1904.method_23481(pressed);
      }

   }

   @EventHandler
   private void onFinishUsingItem(FinishUsingItemEvent event) {
      this.stopIfUsing();
   }

   @EventHandler
   private void onStoppedUsingItem(StoppedUsingItemEvent event) {
      this.stopIfUsing();
   }

   private void stopIfUsing() {
      if (this.isUsing) {
         this.mc.field_1690.field_1904.method_23481(false);
         InvUtils.swapBack();
         this.isUsing = false;
      }

   }

   public static enum Mode {
      Pearl(class_1802.field_8634, MidClickExtra.Type.Immediate),
      Rocket(class_1802.field_8639, MidClickExtra.Type.Immediate),
      Rod(class_1802.field_8378, MidClickExtra.Type.LongerSingleClick),
      Bow(class_1802.field_8102, MidClickExtra.Type.Longer),
      Gap(class_1802.field_8463, MidClickExtra.Type.Longer),
      EGap(class_1802.field_8367, MidClickExtra.Type.Longer),
      Chorus(class_1802.field_8233, MidClickExtra.Type.Longer);

      private final class_1792 item;
      private final Type type;

      private Mode(class_1792 item, Type type) {
         this.item = item;
         this.type = type;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Pearl, Rocket, Rod, Bow, Gap, EGap, Chorus};
      }
   }

   public static enum SwitchMode {
      Silent,
      PickSilent,
      InvSwitch;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Silent, PickSilent, InvSwitch};
      }
   }

   private static enum Type {
      Immediate,
      LongerSingleClick,
      Longer;

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{Immediate, LongerSingleClick, Longer};
      }
   }
}
