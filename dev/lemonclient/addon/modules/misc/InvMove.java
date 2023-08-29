package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.events.ClickWindowEvent;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.mixin.CreativeInventoryScreenAccessor;
import meteordevelopment.meteorclient.mixin.KeyBindingAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_2848;
import net.minecraft.class_304;
import net.minecraft.class_3532;
import net.minecraft.class_3675;
import net.minecraft.class_408;
import net.minecraft.class_463;
import net.minecraft.class_471;
import net.minecraft.class_481;
import net.minecraft.class_497;
import net.minecraft.class_498;
import net.minecraft.class_1761.class_7916;
import net.minecraft.class_2848.class_2849;
import net.minecraft.class_3675.class_307;

public class InvMove extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting bypassSetting;
   private final Setting noSprintSetting;
   private final Setting noMoveClicks;
   private final Setting screens;
   private final Setting jump;
   private final Setting sneak;
   private final Setting arrowsRotate;
   private final Setting rotateSpeed;

   public InvMove() {
      super(LemonClient.Misc, "Inv Move", "Move in inventories.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.bypassSetting = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Bypass")).description("Bypass mode.")).defaultValue(InvMove.Bypass.None)).build());
      this.noSprintSetting = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("NoSprint")).description("NoSprint Bypass mode.")).defaultValue(InvMove.NoSprint.None)).build());
      this.noMoveClicks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("NoMoveClicks")).description("Block clicks in move.")).defaultValue(true)).build());
      this.screens = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("gUIs")).description("Which GUIs to move in.")).defaultValue(InvMove.Screens.Inventory)).build());
      this.jump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("jump")).description("Allows you to jump while in GUIs.")).defaultValue(true)).onChanged((aBoolean) -> {
         if (this.isActive() && !aBoolean) {
            this.set(this.mc.field_1690.field_1903, false);
         }

      })).build());
      this.sneak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sneak")).description("Allows you to sneak while in GUIs.")).defaultValue(true)).onChanged((aBoolean) -> {
         if (this.isActive() && !aBoolean) {
            this.set(this.mc.field_1690.field_1832, false);
         }

      })).build());
      this.arrowsRotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("arrows-rotate")).description("Allows you to use your arrow keys to rotate while in GUIs.")).defaultValue(true)).build());
      this.rotateSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rotate-speed")).description("Rotation speed while in GUIs.")).defaultValue(4.0).min(0.0).build());
   }

   public void onDeactivate() {
      this.set(this.mc.field_1690.field_1894, false);
      this.set(this.mc.field_1690.field_1881, false);
      this.set(this.mc.field_1690.field_1913, false);
      this.set(this.mc.field_1690.field_1849, false);
      if ((Boolean)this.jump.get()) {
         this.set(this.mc.field_1690.field_1903, false);
      }

      if ((Boolean)this.sneak.get()) {
         this.set(this.mc.field_1690.field_1832, false);
      }

      if (this.noSprintSetting.get() == InvMove.NoSprint.None) {
         this.set(this.mc.field_1690.field_1867, false);
      }

   }

   @EventHandler
   private void onClickSlot(ClickWindowEvent event) {
      if ((Boolean)this.noMoveClicks.get() && PlayerUtils.isMoving()) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2848 packet) {
         if (packet.method_12365() == class_2849.field_12988 && this.bypassSetting.get() == InvMove.Bypass.NoOpenPacket) {
            if (this.noSprintSetting.get() == InvMove.NoSprint.PacketSpoof) {
               if (this.mc.field_1724.method_5624()) {
                  this.mc.field_1724.field_3944.method_2883(new class_2848(this.mc.field_1724, class_2849.field_12985));
               }

               if (this.mc.field_1724.method_5715()) {
                  this.mc.field_1724.field_3944.method_2883(new class_2848(this.mc.field_1724, class_2849.field_12984));
               }
            }

            event.cancel();
         }

         class_2596 var4 = event.packet;
         if (var4 instanceof class_2815 closeHandledScreenC2SPacket) {
            if (this.noSprintSetting.get() == InvMove.NoSprint.PacketSpoof) {
               if (this.mc.field_1724.method_5624()) {
                  this.mc.field_1724.field_3944.method_2883(new class_2848(this.mc.field_1724, class_2849.field_12981));
               }

               if (this.mc.field_1724.method_5715()) {
                  this.mc.field_1724.field_3944.method_2883(new class_2848(this.mc.field_1724, class_2849.field_12979));
               }
            }
         }

         var4 = event.packet;
         if (var4 instanceof class_2815 closeHandledScreenC2SPacket) {
            if (this.noSprintSetting.get() == InvMove.NoSprint.PacketSpoof) {
               if (this.mc.field_1724.method_5624()) {
                  this.mc.field_1724.field_3944.method_2883(new class_2848(this.mc.field_1724, class_2849.field_12981));
               }

               if (this.mc.field_1724.method_5715()) {
                  this.mc.field_1724.field_3944.method_2883(new class_2848(this.mc.field_1724, class_2849.field_12979));
               }
            }
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (!this.skip()) {
         if (this.screens.get() != InvMove.Screens.GUI || this.mc.field_1755 instanceof WidgetScreen) {
            if (this.screens.get() != InvMove.Screens.Inventory || !(this.mc.field_1755 instanceof WidgetScreen)) {
               this.set(this.mc.field_1690.field_1894, Input.isPressed(this.mc.field_1690.field_1894));
               this.set(this.mc.field_1690.field_1881, Input.isPressed(this.mc.field_1690.field_1881));
               this.set(this.mc.field_1690.field_1913, Input.isPressed(this.mc.field_1690.field_1913));
               this.set(this.mc.field_1690.field_1849, Input.isPressed(this.mc.field_1690.field_1849));
               if ((Boolean)this.jump.get()) {
                  this.set(this.mc.field_1690.field_1903, Input.isPressed(this.mc.field_1690.field_1903));
               }

               if ((Boolean)this.sneak.get()) {
                  this.set(this.mc.field_1690.field_1832, Input.isPressed(this.mc.field_1690.field_1832));
               }

               if (this.noSprintSetting.get() == InvMove.NoSprint.None) {
                  this.set(this.mc.field_1690.field_1867, Input.isPressed(this.mc.field_1690.field_1867));
               }

               if ((Boolean)this.arrowsRotate.get()) {
                  float yaw = this.mc.field_1724.method_36454();
                  float pitch = this.mc.field_1724.method_36455();

                  for(int i = 0; (double)i < (Double)this.rotateSpeed.get() * 2.0; ++i) {
                     if (Input.isKeyPressed(263)) {
                        yaw = (float)((double)yaw - 0.5);
                     }

                     if (Input.isKeyPressed(262)) {
                        yaw = (float)((double)yaw + 0.5);
                     }

                     if (Input.isKeyPressed(265)) {
                        pitch = (float)((double)pitch - 0.5);
                     }

                     if (Input.isKeyPressed(264)) {
                        pitch = (float)((double)pitch + 0.5);
                     }
                  }

                  pitch = class_3532.method_15363(pitch, -90.0F, 90.0F);
                  this.mc.field_1724.method_36456(yaw);
                  this.mc.field_1724.method_36457(pitch);
               }

            }
         }
      }
   }

   private void set(class_304 bind, boolean pressed) {
      boolean wasPressed = bind.method_1434();
      bind.method_23481(pressed);
      class_3675.class_306 key = ((KeyBindingAccessor)bind).getKey();
      if (wasPressed != pressed && key.method_1442() == class_307.field_1668) {
         MeteorClient.EVENT_BUS.post(KeyEvent.get(key.method_1444(), 0, pressed ? KeyAction.Press : KeyAction.Release));
      }

   }

   public boolean skip() {
      return this.mc.field_1755 == null || this.mc.field_1755 instanceof class_481 && CreativeInventoryScreenAccessor.getSelectedTab().method_47312() == class_7916.field_41055 || this.mc.field_1755 instanceof class_408 || this.mc.field_1755 instanceof class_498 || this.mc.field_1755 instanceof class_471 || this.mc.field_1755 instanceof class_463 || this.mc.field_1755 instanceof class_497;
   }

   public static enum Bypass {
      NoOpenPacket,
      None;

      // $FF: synthetic method
      private static Bypass[] $values() {
         return new Bypass[]{NoOpenPacket, None};
      }
   }

   public static enum NoSprint {
      Real,
      PacketSpoof,
      None;

      // $FF: synthetic method
      private static NoSprint[] $values() {
         return new NoSprint[]{Real, PacketSpoof, None};
      }
   }

   public static enum Screens {
      GUI,
      Inventory,
      Both;

      // $FF: synthetic method
      private static Screens[] $values() {
         return new Screens[]{GUI, Inventory, Both};
      }
   }
}
