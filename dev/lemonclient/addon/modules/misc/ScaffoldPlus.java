package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.player.PlaceData;
import dev.lemonclient.addon.utils.timers.TimerList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.SafeWalk;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1799;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3341;

public class ScaffoldPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlacing;
   private final SettingGroup sgRender;
   private final Setting scaffoldMode;
   private final Setting smart;
   private final Setting tower;
   private final Setting sSprint;
   private final Setting safeWalk;
   private final Setting useTimer;
   private final Setting timer;
   private final Setting switchMode;
   private final Setting blocks;
   private final Setting placeDelay;
   private final Setting places;
   private final Setting cooldown;
   private final Setting extrapolation;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting shapeMode;
   private final Setting lineColor;
   private final Setting sideColor;
   private final TimerList timers;
   private class_243 motion;
   private double placeTimer;
   private int placesLeft;
   public static boolean shouldStopSprinting = false;
   private final List render;
   private int jumpProgress;
   private final double[] velocities;

   public ScaffoldPlus() {
      super(LemonClient.Misc, "Scaffold Plus", "Places blocks under your feet to help you to move.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlacing = this.settings.createGroup("Placing");
      this.sgRender = this.settings.createGroup("Render");
      this.scaffoldMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Scaffold Mode")).description("Mode for scaffold.")).defaultValue(ScaffoldPlus.ScaffoldMode.Normal)).build());
      this.smart = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Smart")).description("Only places on blocks that you can reach.")).defaultValue(true)).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.tower = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Tower")).description("Flies up with blocks.")).defaultValue(true)).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.sSprint = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Stop Sprint")).description("Stops you from sprinting.")).defaultValue(true)).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.safeWalk = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("SafeWalk")).description("Should SafeWalk be used.")).defaultValue(true)).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.useTimer = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Use timer")).description("Should we use timer.")).defaultValue(false)).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = new DoubleSetting.Builder();
      Setting var10003 = this.useTimer;
      Objects.requireNonNull(var10003);
      this.timer = var10001.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)var10002.visible(var10003::get)).name("Timer")).description("Sends more packets.")).defaultValue(1.088).min(0.0).sliderMax(10.0).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal && (Boolean)this.useTimer.get();
      })).build());
      this.switchMode = this.sgPlacing.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Method of switching. Silent is the most reliable.")).defaultValue(ScaffoldPlus.SwitchMode.Silent)).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.blocks = this.sgPlacing.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("Blocks")).description("Blocks to use.")).defaultValue(new class_2248[]{class_2246.field_10540, class_2246.field_22423, class_2246.field_22108}).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.placeDelay = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Delay")).description("Delay between places.")).defaultValue(0.125).range(0.0, 10.0).sliderRange(0.0, 10.0).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.places = this.sgPlacing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Places")).description("Blocks placed per place.")).defaultValue(1)).range(1, 10).sliderRange(1, 10).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.cooldown = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Cooldown")).description("Delay between places at each spot.")).defaultValue(0.3).range(0.0, 5.0).sliderRange(0.0, 5.0).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.extrapolation = this.sgPlacing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Extrapolation")).description("Predicts movement.")).defaultValue(3)).range(1, 20).sliderRange(0, 20).visible(() -> {
         return this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Normal;
      })).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders swing animation when placing a block.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var1 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts of boxes should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.timers = new TimerList();
      this.motion = null;
      this.placesLeft = 0;
      this.render = new ArrayList();
      this.jumpProgress = -1;
      this.velocities = new double[]{0.42, 0.33319999999999994, 0.2468};
   }

   public void onDeactivate() {
      switch ((ScaffoldMode)this.scaffoldMode.get()) {
         case Normal:
            this.placeTimer = 0.0;
            this.placesLeft = (Integer)this.places.get();
            ((Timer)Modules.get().get(Timer.class)).setOverride(1.0);
            if (((SafeWalk)Modules.get().get(SafeWalk.class)).isActive()) {
               ((SafeWalk)Modules.get().get(SafeWalk.class)).toggle();
            }
            break;
         case Legit:
            this.mc.field_1690.field_1832.method_23481(false);
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      this.timers.update();
      if (this.scaffoldMode.get() != ScaffoldPlus.ScaffoldMode.Legit) {
         this.placeTimer = Math.min((Double)this.placeDelay.get(), this.placeTimer + event.frameTime);
         if (this.placeTimer >= (Double)this.placeDelay.get()) {
            this.placesLeft = (Integer)this.places.get();
            this.placeTimer = 0.0;
         }

         this.render.removeIf((r) -> {
            return System.currentTimeMillis() - r.time > 1000L;
         });
         this.render.forEach((r) -> {
            double progress = 1.0 - (double)Math.min(System.currentTimeMillis() - r.time, 500L) / 500.0;
            event.renderer.box(r.pos, new Color(((SettingColor)this.sideColor.get()).r, ((SettingColor)this.sideColor.get()).g, ((SettingColor)this.sideColor.get()).b, (int)Math.round((double)((SettingColor)this.sideColor.get()).a * progress)), new Color(((SettingColor)this.lineColor.get()).r, ((SettingColor)this.lineColor.get()).g, ((SettingColor)this.lineColor.get()).b, (int)Math.round((double)((SettingColor)this.lineColor.get()).a * progress)), (ShapeMode)this.shapeMode.get(), 0);
         });
      }
   }

   @EventHandler(
      priority = 10000
   )
   private void onTick(TickEvent.Pre event) {
      if (this.scaffoldMode.get() == ScaffoldPlus.ScaffoldMode.Legit) {
         this.mc.field_1690.field_1832.method_23481(this.mc.field_1687.method_8320(this.mc.field_1724.method_24515().method_10074()).method_26204() instanceof class_2189);
      }

   }

   @EventHandler(
      priority = 10000
   )
   private void onMove(PlayerMoveEvent event) {
      shouldStopSprinting = false;
      if (this.scaffoldMode.get() != ScaffoldPlus.ScaffoldMode.Legit) {
         if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
            FindItemResult hotbar = InvUtils.findInHotbar((item) -> {
               return item.method_7909() instanceof class_1747 && ((List)this.blocks.get()).contains(((class_1747)item.method_7909()).method_7711());
            });
            FindItemResult inventory = InvUtils.find((item) -> {
               return item.method_7909() instanceof class_1747 && ((List)this.blocks.get()).contains(((class_1747)item.method_7909()).method_7711());
            });
            class_1268 hand = this.isValid(Managers.HOLDING.getStack()) ? class_1268.field_5808 : (this.isValid(this.mc.field_1724.method_6079()) ? class_1268.field_5810 : null);
            if (hand != null || (this.switchMode.get() == ScaffoldPlus.SwitchMode.PickSilent || this.switchMode.get() == ScaffoldPlus.SwitchMode.InvSwitch) && inventory.slot() >= 0 || (this.switchMode.get() == ScaffoldPlus.SwitchMode.Silent || this.switchMode.get() == ScaffoldPlus.SwitchMode.Normal) && hotbar.slot() >= 0) {
               if ((Boolean)this.safeWalk.get() && !((SafeWalk)Modules.get().get(SafeWalk.class)).isActive()) {
                  ((SafeWalk)Modules.get().get(SafeWalk.class)).toggle();
               }

               this.motion = event.movement;
               this.yVel();
               if ((Boolean)this.sSprint.get()) {
                  shouldStopSprinting = true;
                  this.mc.field_1724.method_5728(false);
               }

               if ((Boolean)this.useTimer.get()) {
                  ((Timer)Modules.get().get(Timer.class)).setOverride((Double)this.timer.get());
               }

               List placements = this.getBlocks();
               if (!placements.isEmpty() && this.placesLeft > 0) {
                  List toPlace = new ArrayList();
                  Iterator var7 = placements.iterator();

                  while(var7.hasNext()) {
                     class_2338 placement = (class_2338)var7.next();
                     if (toPlace.size() < this.placesLeft && this.canPlace(placement)) {
                        toPlace.add(placement);
                     }
                  }

                  if (!toPlace.isEmpty()) {
                     int obsidian = hand == class_1268.field_5808 ? Managers.HOLDING.getStack().method_7947() : (hand == class_1268.field_5810 ? this.mc.field_1724.method_6079().method_7947() : -1);
                     if (hand == null) {
                        switch ((SwitchMode)this.switchMode.get()) {
                           case Silent:
                           case Normal:
                              obsidian = hotbar.count();
                              break;
                           case PickSilent:
                           case InvSwitch:
                              obsidian = inventory.slot() >= 0 ? inventory.count() : -1;
                        }
                     }

                     if (obsidian >= 0) {
                        class_2248 block = null;
                        if (hand == class_1268.field_5808) {
                           block = ((class_1747)Managers.HOLDING.getStack().method_7909()).method_7711();
                        }

                        if (hand == class_1268.field_5810) {
                           block = ((class_1747)this.mc.field_1724.method_6079().method_7909()).method_7711();
                        } else {
                           switch ((SwitchMode)this.switchMode.get()) {
                              case Silent:
                              case Normal:
                                 obsidian = hotbar.count();
                                 InvUtils.swap(hotbar.slot(), true);
                                 block = ((class_1747)this.mc.field_1724.method_31548().method_5438(hotbar.slot()).method_7909()).method_7711();
                                 break;
                              case PickSilent:
                                 obsidian = InventoryUtils.pickSwitch(inventory.slot()) ? inventory.count() : -1;
                                 block = ((class_1747)this.mc.field_1724.method_31548().method_5438(inventory.slot()).method_7909()).method_7711();
                                 break;
                              case InvSwitch:
                                 obsidian = InventoryUtils.invSwitch(inventory.slot()) ? inventory.count() : -1;
                                 block = ((class_1747)this.mc.field_1724.method_31548().method_5438(inventory.slot()).method_7909()).method_7711();
                           }
                        }

                        for(int i = 0; i < Math.min(obsidian, toPlace.size()); ++i) {
                           PlaceData placeData = SettingUtils.getPlaceData((class_2338)toPlace.get(i));
                           if (placeData.valid()) {
                              boolean rotated = !SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(placeData.pos(), 1.0, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
                              if (!rotated) {
                                 break;
                              }

                              this.place(placeData, (class_2338)toPlace.get(i), hand == null ? class_1268.field_5808 : hand, block);
                           }
                        }

                        if (hand == null) {
                           switch ((SwitchMode)this.switchMode.get()) {
                              case Silent:
                                 InvUtils.swapBack();
                              case Normal:
                              default:
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
            } else if ((Boolean)this.safeWalk.get() && ((SafeWalk)Modules.get().get(SafeWalk.class)).isActive()) {
               ((SafeWalk)Modules.get().get(SafeWalk.class)).toggle();
            }

         }
      }
   }

   void yVel() {
      if ((Boolean)this.tower.get()) {
         if (this.mc.field_1690.field_1903.method_1434() && this.mc.field_1724.field_3913.field_3905 == 0.0F && this.mc.field_1724.field_3913.field_3907 == 0.0F) {
            if (this.mc.field_1724.method_24828() || this.jumpProgress == 3) {
               this.jumpProgress = 0;
            }

            if (this.jumpProgress > -1 && this.jumpProgress < 3) {
               ((IVec3d)this.motion).setXZ(0.0, 0.0);
               ((IVec3d)this.motion).setY(this.velocities[this.jumpProgress]);
               ((IVec3d)this.mc.field_1724.method_18798()).setY(this.velocities[this.jumpProgress]);
               ++this.jumpProgress;
            }
         } else {
            this.jumpProgress = -1;
         }

      }
   }

   private boolean isValid(class_1799 item) {
      return item.method_7909() instanceof class_1747 && ((List)this.blocks.get()).contains(((class_1747)item.method_7909()).method_7711());
   }

   private boolean canPlace(class_2338 pos) {
      return SettingUtils.getPlaceData(pos).valid();
   }

   private List getBlocks() {
      List list = new ArrayList();
      class_243 vec = this.mc.field_1724.method_19538();

      for(int i = 0; i < (Integer)this.extrapolation.get() * 10; ++i) {
         vec = vec.method_1031(this.motion.field_1352 / 10.0, 0.0, this.motion.field_1350 / 10.0);
         if ((Boolean)this.smart.get() && this.inside(this.getBox(vec))) {
            break;
         }

         class_2338 pos = class_2338.method_49638(vec).method_10074();
         if (!this.timers.contains(pos) && LemonUtils.replaceable(pos) && !list.contains(pos) && !this.mc.field_1724.method_5829().method_994(class_238.method_19316(new class_3341(pos)))) {
            list.add(pos);
         }
      }

      return list;
   }

   private class_238 getBox(class_243 vec) {
      class_238 box = this.mc.field_1724.method_5829();
      return new class_238(vec.field_1352 - 0.3, vec.field_1351, vec.field_1350 - 0.3, vec.field_1352 + 0.3, vec.field_1351 + (box.field_1325 - box.field_1322), vec.field_1350 + 0.3);
   }

   private boolean inside(class_238 bb) {
      return this.mc.field_1687.method_20812(this.mc.field_1724, bb).iterator().hasNext();
   }

   private void place(PlaceData d, class_2338 ogPos, class_1268 hand, class_2248 block) {
      this.timers.add(ogPos, (Double)this.cooldown.get());
      this.render.add(new Render(ogPos, System.currentTimeMillis()));
      --this.placesLeft;
      this.placeBlock(hand, d.pos().method_46558(), d.dir(), d.pos());
      if ((Boolean)this.placeSwing.get()) {
         this.clientSwing((SwingHand)this.placeHand.get(), hand);
      }

      this.mc.field_1687.method_8501(ogPos, block.method_9564());
      if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
         Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
      }

   }

   public static enum ScaffoldMode {
      Normal,
      Legit;

      // $FF: synthetic method
      private static ScaffoldMode[] $values() {
         return new ScaffoldMode[]{Normal, Legit};
      }
   }

   public static enum SwitchMode {
      Disabled,
      Normal,
      Silent,
      PickSilent,
      InvSwitch;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Disabled, Normal, Silent, PickSilent, InvSwitch};
      }
   }

   public static record Render(class_2338 pos, long time) {
      public Render(class_2338 pos, long time) {
         this.pos = pos;
         this.time = time;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public long time() {
         return this.time;
      }
   }
}
