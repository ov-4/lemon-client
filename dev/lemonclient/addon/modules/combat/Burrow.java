package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.misc.PacketFly;
import dev.lemonclient.addon.modules.misc.ScaffoldPlus;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_2708;
import net.minecraft.class_2828;

public class Burrow extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRubberband;
   private final SettingGroup sgRender;
   private final Setting switchMode;
   private final Setting blocks;
   private final Setting breakCrystal;
   private final Setting pauseOnEat;
   private final Setting breakDelay;
   public final Setting safeHealth;
   private final Setting instaRot;
   private final Setting pFly;
   private final Setting scaffold;
   private final Setting rubberbandOffset;
   private final Setting rubberbandPackets;
   private final Setting placeSwing;
   private final Setting placeHand;
   private boolean success;
   private boolean enabledPFly;
   private boolean enabledScaffold;
   private final Predicate predicate;

   public Burrow() {
      super(LemonClient.Combat, "Burrow Plus", "Clip you into a block.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRubberband = this.settings.createGroup("Rubberband");
      this.sgRender = this.settings.createGroup("Render");
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Which method of switching should be used.")).defaultValue(Burrow.SwitchMode.Silent)).build());
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("Blocks")).description("Blocks to use.")).defaultValue(new class_2248[]{class_2246.field_10540, class_2246.field_10443}).build());
      this.breakCrystal = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Break Crystal")).description("Automatically break crystals to help you place burrow block.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause On Eat")).description("Pause while eating.")).defaultValue(false);
      Setting var10003 = this.breakCrystal;
      Objects.requireNonNull(var10003);
      this.pauseOnEat = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      DoubleSetting.Builder var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Break Delay")).description("Break crystals delay.")).defaultValue(1.0).sliderRange(0.0, 10.0);
      var10003 = this.breakCrystal;
      Objects.requireNonNull(var10003);
      this.breakDelay = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var1 = ((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Safe Health")).defaultValue(16.0).sliderRange(0.0, 36.0);
      var10003 = this.breakCrystal;
      Objects.requireNonNull(var10003);
      this.safeHealth = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      this.instaRot = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant Rotation")).description("Instantly rotates.")).defaultValue(false)).build());
      this.pFly = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Packet Fly")).description("Enables packetfly after lagging back inside the block.")).defaultValue(false)).build());
      var10001 = this.sgGeneral;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Scaffold")).description("Enables scaffold+ after lagging back inside the block.")).defaultValue(false);
      var10003 = this.pFly;
      Objects.requireNonNull(var10003);
      this.scaffold = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.rubberbandOffset = this.sgRubberband.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Rubberband Offset")).description("Y offset of rubberband packet.")).defaultValue(9.0).sliderRange(-10.0, 10.0).build());
      this.rubberbandPackets = this.sgRubberband.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Rubberband Packets")).description("How many offset packets to send.")).defaultValue(1)).min(0).sliderRange(0, 10).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders swing animation when placing a block.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var2 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var2.visible(var10003::get)).build());
      this.success = false;
      this.enabledPFly = false;
      this.enabledScaffold = false;
      this.predicate = (itemStack) -> {
         class_1792 patt5485$temp = itemStack.method_7909();
         if (patt5485$temp instanceof class_1747 block) {
            return ((List)this.blocks.get()).contains(block.method_7711());
         } else {
            return false;
         }
      };
   }

   public void onActivate() {
      this.success = false;
      this.enabledPFly = false;
      this.enabledScaffold = false;
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         class_1268 hand = this.predicate.test(Managers.HOLDING.getStack()) ? class_1268.field_5808 : (this.predicate.test(this.mc.field_1724.method_6079()) ? class_1268.field_5810 : null);
         boolean blocksPresent = hand != null;
         if (!blocksPresent) {
            switch ((SwitchMode)this.switchMode.get()) {
               case Normal:
               case Silent:
                  blocksPresent = InvUtils.findInHotbar(this.predicate).found();
                  break;
               case PickSilent:
               case InvSwitch:
                  blocksPresent = InvUtils.find(this.predicate).found();
            }
         }

         if (blocksPresent) {
            boolean rotated = (Boolean)this.instaRot.get() || !SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.startPitch(90.0, (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
            if (rotated) {
               boolean switched = hand != null;
               if (!switched) {
                  boolean var10000;
                  switch ((SwitchMode)this.switchMode.get()) {
                     case Normal:
                     case Silent:
                        var10000 = InvUtils.swap(InvUtils.findInHotbar(this.predicate).slot(), true);
                        break;
                     case PickSilent:
                        var10000 = InventoryUtils.pickSwitch(InvUtils.find(this.predicate).slot());
                        break;
                     case InvSwitch:
                        var10000 = InventoryUtils.invSwitch(InvUtils.find(this.predicate).slot());
                        break;
                     default:
                        throw new IncompatibleClassChangeError();
                  }

                  switched = var10000;
               }

               if (!switched) {
                  this.toggle();
                  this.sendToggledMsg("correct blocks not found");
               } else {
                  if ((Boolean)this.instaRot.get() && SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     this.sendPacket(new class_2828.class_2831(Managers.ROTATION.lastDir[0], 90.0F, Managers.ON_GROUND.isOnGround()));
                  }

                  double y = 0.0;
                  double velocity = 0.42;

                  while(y < 1.1) {
                     y += velocity;
                     velocity = (velocity - 0.08) * 0.98;
                     this.sendPacket(new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + y, this.mc.field_1724.method_23321(), false));
                  }

                  this.placeBlockAndAttackCrystal(this.mc.field_1724.method_24515().method_10074(), class_2350.field_11036, (Boolean)this.pauseOnEat.get(), (Boolean)this.placeSwing.get(), (SwingHand)this.placeHand.get(), (Boolean)this.breakCrystal.get(), ((Double)this.breakDelay.get()).longValue(), (Double)this.safeHealth.get());
                  if (!(Boolean)this.instaRot.get() && SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
                  }

                  if ((Boolean)this.placeSwing.get()) {
                     this.clientSwing((SwingHand)this.placeHand.get(), class_1268.field_5808);
                  }

                  for(int i = 0; i < (Integer)this.rubberbandPackets.get(); ++i) {
                     this.sendPacket(new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + y + (Double)this.rubberbandOffset.get(), this.mc.field_1724.method_23321(), false));
                  }

                  this.success = true;
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

                  if (!(Boolean)this.pFly.get()) {
                     this.toggle();
                     this.sendToggledMsg("success");
                  }

               }
            }
         }
      }
   }

   public void onDeactivate() {
      if (this.enabledPFly && Modules.get().isActive(PacketFly.class)) {
         ((PacketFly)Modules.get().get(PacketFly.class)).toggle();
         ((PacketFly)Modules.get().get(PacketFly.class)).sendToggledMsg("disabled by burrow+");
      }

      if (this.enabledScaffold && Modules.get().isActive(ScaffoldPlus.class)) {
         ((ScaffoldPlus)Modules.get().get(ScaffoldPlus.class)).toggle();
         ((ScaffoldPlus)Modules.get().get(ScaffoldPlus.class)).sendToggledMsg("disabled by burrow+");
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onPacket(PacketEvent.Receive event) {
      if ((Boolean)this.pFly.get() && this.success && event.packet instanceof class_2708) {
         if (!Modules.get().isActive(PacketFly.class)) {
            ((PacketFly)Modules.get().get(PacketFly.class)).toggle();
            ((PacketFly)Modules.get().get(PacketFly.class)).sendToggledMsg("enabled by burrow+");
            this.enabledPFly = true;
         }

         if ((Boolean)this.scaffold.get() && !Modules.get().isActive(ScaffoldPlus.class)) {
            ((ScaffoldPlus)Modules.get().get(ScaffoldPlus.class)).toggle();
            ((ScaffoldPlus)Modules.get().get(ScaffoldPlus.class)).sendToggledMsg("enabled by burrow+");
            this.enabledScaffold = true;
         }
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
