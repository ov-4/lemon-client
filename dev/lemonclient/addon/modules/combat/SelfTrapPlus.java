package dev.lemonclient.addon.modules.combat;

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
import meteordevelopment.meteorclient.events.render.Render3DEvent;
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
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1299;
import net.minecraft.class_1542;
import net.minecraft.class_1747;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3341;

public class SelfTrapPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlacing;
   private final SettingGroup sgToggle;
   private final SettingGroup sgRender;
   private final Setting pauseEat;
   private final Setting onlyConfirmed;
   private final Setting switchMode;
   private final Setting trapMode;
   private final Setting blocks;
   private final Setting placeDelay;
   private final Setting places;
   private final Setting delay;
   private final Setting toggleMove;
   private final Setting toggleY;
   private final Setting toggleSneak;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting shapeMode;
   private final Setting lineColor;
   private final Setting sideColor;
   private final Setting supportLineColor;
   private final Setting supportSideColor;
   private final TimerList timers;
   private final TimerList placed;
   private double placeTimer;
   private int placesLeft;
   private class_2338 startPos;
   private boolean lastSneak;
   private final List render;
   public static boolean placing = false;

   public SelfTrapPlus() {
      super(LemonClient.Combat, "Self Trap Plus", "Traps yourself with blocks.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlacing = this.settings.createGroup("Placing");
      this.sgToggle = this.settings.createGroup("Toggle");
      this.sgRender = this.settings.createGroup("Render");
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause Eat")).description("Pauses when you are eating.")).defaultValue(true)).build());
      this.onlyConfirmed = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Only Confirmed")).description("Only places on blocks the server has confirmed to exist.")).defaultValue(true)).build());
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Method of switching. Silent is the most reliable.")).defaultValue(SelfTrapPlus.SwitchMode.Silent)).build());
      this.trapMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Trap Mode")).description("Where to place blocks.")).defaultValue(SelfTrapPlus.TrapMode.Both)).build());
      this.blocks = this.sgPlacing.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("Blocks")).description("Blocks to use.")).defaultValue(new class_2248[]{class_2246.field_10540, class_2246.field_22423, class_2246.field_22108}).build());
      this.placeDelay = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Delay")).description("Delay between places.")).defaultValue(0.125).range(0.0, 10.0).sliderRange(0.0, 10.0).build());
      this.places = this.sgPlacing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Places")).description("Blocks placed per place.")).defaultValue(1)).range(1, 10).sliderRange(1, 10).build());
      this.delay = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Delay")).description("Delay between places at each spot.")).defaultValue(0.3).range(0.0, 10.0).sliderRange(0.0, 10.0).build());
      this.toggleMove = this.sgToggle.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Toggle Move")).description("Toggles when you move horizontally.")).defaultValue(true)).build());
      this.toggleY = this.sgToggle.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Toggle Y")).description("Toggles when you move vertically.")).defaultValue(SelfTrapPlus.ToggleYMode.Full)).build());
      this.toggleSneak = this.sgToggle.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Toggle Sneak")).description("Toggles when you sneak.")).defaultValue(false)).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders swing animation when placing a block.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts of the boxes should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color of the outlines")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Color of the sides.")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.supportLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Support Line Color")).description("Color of the outlines for support blocks")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.supportSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Support Side Color")).description("Color of the sides for support blocks.")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.timers = new TimerList();
      this.placed = new TimerList();
      this.placeTimer = 0.0;
      this.placesLeft = 0;
      this.startPos = new class_2338(0, 0, 0);
      this.lastSneak = false;
      this.render = new ArrayList();
   }

   public void onActivate() {
      super.onActivate();
      if (this.mc.field_1724 == null || this.mc.field_1687 == null) {
         this.toggle();
      }

      this.startPos = this.mc.field_1724.method_24515();
   }

   public void onDeactivate() {
      super.onDeactivate();
      this.placesLeft = (Integer)this.places.get();
      this.placeTimer = 0.0;
      ((Timer)Modules.get().get(Timer.class)).setOverride(1.0);
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      this.timers.update();
      this.placed.update();
      placing = false;
      this.placeTimer = Math.min((Double)this.placeDelay.get(), this.placeTimer + event.frameTime);
      if (this.placeTimer >= (Double)this.placeDelay.get()) {
         this.placesLeft = (Integer)this.places.get();
         this.placeTimer = 0.0;
      }

      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if ((Boolean)this.toggleMove.get() && (this.mc.field_1724.method_24515().method_10263() != this.startPos.method_10263() || this.mc.field_1724.method_24515().method_10260() != this.startPos.method_10260())) {
            this.sendDisableMsg("moved");
            this.toggle();
            return;
         }

         switch ((ToggleYMode)this.toggleY.get()) {
            case Full:
               if (this.mc.field_1724.method_24515().method_10264() != this.startPos.method_10264()) {
                  this.sendDisableMsg("moved vertically");
                  this.toggle();
                  return;
               }
               break;
            case Up:
               if (this.mc.field_1724.method_24515().method_10264() > this.startPos.method_10264()) {
                  this.sendDisableMsg("moved up");
                  this.toggle();
                  return;
               }
               break;
            case Down:
               if (this.mc.field_1724.method_24515().method_10264() < this.startPos.method_10264()) {
                  this.sendDisableMsg("moved down");
                  this.toggle();
                  return;
               }
         }

         if ((Boolean)this.toggleSneak.get()) {
            boolean isClicked = this.mc.field_1690.field_1832.method_1434();
            if (isClicked && !this.lastSneak) {
               this.sendDisableMsg("sneaked");
               this.toggle();
               return;
            }

            this.lastSneak = isClicked;
         }

         List blocksList = this.getBlocks(this.getSize(this.mc.field_1724.method_24515().method_10084()), this.mc.field_1724.method_5829().method_994(class_238.method_19316(new class_3341(this.mc.field_1724.method_24515().method_10086(2)))));
         this.render.clear();
         List placements = this.getValid(blocksList);
         this.render.forEach((item) -> {
            event.renderer.box(class_238.method_19316(new class_3341(item.pos)), item.support ? (Color)this.supportSideColor.get() : (Color)this.sideColor.get(), item.support ? (Color)this.supportLineColor.get() : (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         });
         FindItemResult hotbar = InvUtils.findInHotbar((item) -> {
            return item.method_7909() instanceof class_1747 && ((List)this.blocks.get()).contains(((class_1747)item.method_7909()).method_7711());
         });
         FindItemResult inventory = InvUtils.find((item) -> {
            return item.method_7909() instanceof class_1747 && ((List)this.blocks.get()).contains(((class_1747)item.method_7909()).method_7711());
         });
         class_1268 hand = this.isValid(Managers.HOLDING.getStack()) ? class_1268.field_5808 : (this.isValid(this.mc.field_1724.method_6079()) ? class_1268.field_5810 : null);
         if ((!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) && (hand != null || (this.switchMode.get() == SelfTrapPlus.SwitchMode.Silent || this.switchMode.get() == SelfTrapPlus.SwitchMode.Normal) && hotbar.slot() >= 0 || (this.switchMode.get() == SelfTrapPlus.SwitchMode.PickSilent || this.switchMode.get() == SelfTrapPlus.SwitchMode.InvSwitch) && inventory.slot() >= 0) && this.placesLeft > 0 && !placements.isEmpty()) {
            List toPlace = new ArrayList();
            Iterator var8 = placements.iterator();

            while(var8.hasNext()) {
               class_2338 placement = (class_2338)var8.next();
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
                  if (hand == null) {
                     switch ((SwitchMode)this.switchMode.get()) {
                        case Silent:
                        case Normal:
                           obsidian = hotbar.count();
                           InvUtils.swap(hotbar.slot(), true);
                           break;
                        case PickSilent:
                           obsidian = InventoryUtils.pickSwitch(inventory.slot()) ? inventory.count() : -1;
                           break;
                        case InvSwitch:
                           obsidian = InventoryUtils.invSwitch(inventory.slot()) ? inventory.count() : -1;
                     }
                  }

                  if (obsidian <= 0) {
                     return;
                  }

                  placing = true;

                  for(int i = 0; i < Math.min(obsidian, toPlace.size()); ++i) {
                     PlaceData var10000;
                     if ((Boolean)this.onlyConfirmed.get()) {
                        var10000 = SettingUtils.getPlaceData((class_2338)toPlace.get(i));
                     } else {
                        class_2338 var15 = (class_2338)toPlace.get(i);
                        TimerList var10001 = this.placed;
                        Objects.requireNonNull(var10001);
                        var10000 = SettingUtils.getPlaceDataOR(var15, var10001::contains);
                     }

                     PlaceData placeData = var10000;
                     if (placeData.valid()) {
                        boolean rotated = !SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(placeData.pos(), 1.0, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
                        if (!rotated) {
                           break;
                        }

                        this.place(placeData, (class_2338)toPlace.get(i), hand == null ? class_1268.field_5808 : hand);
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
      }

   }

   private boolean isValid(class_1799 item) {
      return item.method_7909() instanceof class_1747 && ((List)this.blocks.get()).contains(((class_1747)item.method_7909()).method_7711());
   }

   private boolean canPlace(class_2338 pos) {
      return SettingUtils.getPlaceData(pos).valid();
   }

   private void place(PlaceData d, class_2338 ogPos, class_1268 hand) {
      this.timers.add(ogPos, (Double)this.delay.get());
      if ((Boolean)this.onlyConfirmed.get()) {
         this.placed.add(ogPos, 1.0);
      }

      this.placeTimer = 0.0;
      --this.placesLeft;
      this.placeBlock(hand, d.pos().method_46558(), d.dir(), d.pos());
      if ((Boolean)this.placeSwing.get()) {
         this.clientSwing((SwingHand)this.placeHand.get(), hand);
      }

      if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
         Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
      }

   }

   private List getValid(List blocks) {
      List list = new ArrayList();
      if (blocks.isEmpty()) {
         return list;
      } else {
         blocks.forEach((block) -> {
            if (LemonUtils.replaceable(block)) {
               PlaceData var10000;
               if ((Boolean)this.onlyConfirmed.get()) {
                  var10000 = SettingUtils.getPlaceData(block);
               } else {
                  TimerList var10001 = this.placed;
                  Objects.requireNonNull(var10001);
                  var10000 = SettingUtils.getPlaceDataOR(block, var10001::contains);
               }

               PlaceData data = var10000;
               if (data.valid() && SettingUtils.inPlaceRange(data.pos())) {
                  this.render.add(new Render(block, false));
                  if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(block)), (entity) -> {
                     return !entity.method_7325() && !(entity instanceof class_1542);
                  }) && !this.timers.contains(block)) {
                     list.add(block);
                  }

               } else {
                  class_2350 support1 = this.getSupport(block);
                  if (support1 != null) {
                     this.render.add(new Render(block, false));
                     this.render.add(new Render(block.method_10093(support1), true));
                     if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(block.method_10093(support1))), (entity) -> {
                        return !entity.method_7325() && !(entity instanceof class_1542);
                     }) && !this.timers.contains(block.method_10093(support1))) {
                        list.add(block.method_10093(support1));
                     }

                  } else {
                     class_2350[] var5 = class_2350.values();
                     int var6 = var5.length;

                     for(int var7 = 0; var7 < var6; ++var7) {
                        class_2350 dir = var5[var7];
                        if (LemonUtils.replaceable(block.method_10093(dir)) && SettingUtils.inPlaceRange(block.method_10093(dir))) {
                           class_2350 support2 = this.getSupport(block.method_10093(dir));
                           if (support2 != null) {
                              this.render.add(new Render(block, false));
                              this.render.add(new Render(block.method_10093(dir), true));
                              this.render.add(new Render(block.method_10093(dir).method_10093(support2), true));
                              if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(block.method_10093(dir).method_10093(support2))), (entity) -> {
                                 return !entity.method_7325() && !(entity instanceof class_1542);
                              }) && !this.timers.contains(block.method_10093(dir).method_10093(support2))) {
                                 list.add(block.method_10093(dir).method_10093(support2));
                              }

                              return;
                           }
                        }
                     }

                  }
               }
            }
         });
         return list;
      }
   }

   private class_2350 getSupport(class_2338 position) {
      class_2350 cDir = null;
      double cDist = 1000.0;
      int value = -1;
      class_2350[] var6 = class_2350.values();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         class_2350 dir = var6[var8];
         PlaceData var10000;
         if ((Boolean)this.onlyConfirmed.get()) {
            var10000 = SettingUtils.getPlaceData(position.method_10093(dir));
         } else {
            class_2338 var13 = position.method_10093(dir);
            TimerList var10001 = this.placed;
            Objects.requireNonNull(var10001);
            var10000 = SettingUtils.getPlaceDataOR(var13, var10001::contains);
         }

         PlaceData data = var10000;
         if (data.valid() && SettingUtils.inPlaceRange(data.pos())) {
            double dist;
            if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(position.method_10093(dir))), (entity) -> {
               return !entity.method_7325() && entity.method_5864() != class_1299.field_6052;
            })) {
               dist = this.mc.field_1724.method_33571().method_1022(class_243.method_24953(position.method_10093(dir)));
               if (dist < cDist || value < 2) {
                  value = 2;
                  cDir = dir;
                  cDist = dist;
               }
            }

            if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(position.method_10093(dir))), (entity) -> {
               return !entity.method_7325() && entity.method_5864() != class_1299.field_6052 && entity.method_5864() != class_1299.field_6110;
            })) {
               dist = this.mc.field_1724.method_33571().method_1022(class_243.method_24953(position.method_10093(dir)));
               if (dist < cDist || value < 1) {
                  value = 1;
                  cDir = dir;
                  cDist = dist;
               }
            }
         }
      }

      return cDir;
   }

   private List getBlocks(int[] size, boolean higher) {
      List list = new ArrayList();
      class_2338 pos = this.mc.field_1724.method_24515().method_10086(higher ? 2 : 1);
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         for(int x = size[0] - 1; x <= size[1] + 1; ++x) {
            for(int z = size[2] - 1; z <= size[3] + 1; ++z) {
               boolean isX = x == size[0] - 1 || x == size[1] + 1;
               boolean isZ = z == size[2] - 1 || z == size[3] + 1;
               boolean ignore = isX && !isZ ? !LemonUtils.replaceable(pos.method_10069(LemonUtils.closerToZero(x), 0, z)) || this.placed.contains(pos.method_10069(LemonUtils.closerToZero(x), 0, z)) : !isX && isZ && (!LemonUtils.replaceable(pos.method_10069(x, 0, LemonUtils.closerToZero(z))) || this.placed.contains(pos.method_10069(x, 0, LemonUtils.closerToZero(z))));
               class_2338 bPos = null;
               if (this.eye() && isX != isZ && !ignore) {
                  bPos = (new class_2338(x, pos.method_10264(), z)).method_10069(pos.method_10263(), 0, pos.method_10260());
               } else if (this.top() && !isX && !isZ && LemonUtils.replaceable(pos.method_10069(x, 0, z)) && !this.placed.contains(pos.method_10069(x, 0, z))) {
                  bPos = (new class_2338(x, pos.method_10264(), z)).method_10069(pos.method_10263(), 1, pos.method_10260());
               }

               if (bPos != null) {
                  list.add(bPos);
               }
            }
         }
      }

      return list;
   }

   private boolean top() {
      return this.trapMode.get() == SelfTrapPlus.TrapMode.Both || this.trapMode.get() == SelfTrapPlus.TrapMode.Top;
   }

   private boolean eye() {
      return this.trapMode.get() == SelfTrapPlus.TrapMode.Both || this.trapMode.get() == SelfTrapPlus.TrapMode.Eyes;
   }

   private int[] getSize(class_2338 pos) {
      int minX = 0;
      int maxX = 0;
      int minZ = 0;
      int maxZ = 0;
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         class_238 box = this.mc.field_1724.method_5829();
         if (box.method_994(class_238.method_19316(new class_3341(pos.method_10095())))) {
            --minZ;
         }

         if (box.method_994(class_238.method_19316(new class_3341(pos.method_10072())))) {
            ++maxZ;
         }

         if (box.method_994(class_238.method_19316(new class_3341(pos.method_10067())))) {
            --minX;
         }

         if (box.method_994(class_238.method_19316(new class_3341(pos.method_10078())))) {
            ++maxX;
         }
      }

      return new int[]{minX, maxX, minZ, maxZ};
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

   public static enum TrapMode {
      Top,
      Eyes,
      Both;

      // $FF: synthetic method
      private static TrapMode[] $values() {
         return new TrapMode[]{Top, Eyes, Both};
      }
   }

   public static enum ToggleYMode {
      Disabled,
      Up,
      Down,
      Full;

      // $FF: synthetic method
      private static ToggleYMode[] $values() {
         return new ToggleYMode[]{Disabled, Up, Down, Full};
      }
   }

   private static record Render(class_2338 pos, boolean support) {
      private Render(class_2338 pos, boolean support) {
         this.pos = pos;
         this.support = support;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public boolean support() {
         return this.support;
      }
   }
}
