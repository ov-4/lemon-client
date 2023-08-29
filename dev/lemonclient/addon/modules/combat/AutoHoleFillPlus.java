package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.HoleType;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.entity.LemonEntityUtils;
import dev.lemonclient.addon.utils.misc.ExtrapolationUtils;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.player.PlaceData;
import dev.lemonclient.addon.utils.player.RotationUtils;
import dev.lemonclient.addon.utils.timers.TimerList;
import dev.lemonclient.addon.utils.world.hole.Hole;
import dev.lemonclient.addon.utils.world.hole.HoleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1542;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3341;
import net.minecraft.class_3532;
import net.minecraft.class_742;

public class AutoHoleFillPlus extends LemonModule {
   private final SettingGroup sgNear;
   private final SettingGroup sgWalking;
   private final SettingGroup sgLooking;
   private final SettingGroup sgSelf;
   private final SettingGroup sgPlacing;
   private final SettingGroup sgRender;
   private final SettingGroup sgHole;
   private final Setting near;
   private final Setting nearDistance;
   private final Setting nearExt;
   private final Setting selfExt;
   private final Setting extSmooth;
   private final Setting efficient;
   private final Setting above;
   private final Setting iHole;
   private final Setting walking;
   private final Setting walkingDist;
   private final Setting walkMemory;
   private final Setting look;
   private final Setting lookDist;
   private final Setting lookMemory;
   private final Setting iSelfHole;
   private final Setting selfAbove;
   private final Setting selfDistance;
   private final Setting selfWalking;
   private final Setting selfWalkingDist;
   private final Setting selfWalkMemory;
   private final Setting switchMode;
   private final Setting placeDelayMode;
   private final Setting placeDelayT;
   private final Setting placeDelayS;
   private final Setting places;
   private final Setting delay;
   private final Setting blocks;
   private final Setting boxExt;
   private final Setting selfBoxExt;
   private final Setting boxExtSmooth;
   private final Setting single;
   private final Setting doubleHole;
   private final Setting quad;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting renderTime;
   private final Setting fadeTime;
   private final Setting shapeMode;
   private final Setting lineColor;
   private final Setting sideColor;
   private final List holes;
   private final TimerList timers;
   private final List render;
   private final Map walkAngles;
   private final Map lookAngles;
   private final Map nearPosition;
   private final Map boxes;
   private boolean shouldUpdate;
   private class_1268 hand;
   private int blocksLeft;
   private int placesLeft;
   private FindItemResult result;
   private boolean switched;
   private int tickTimer;
   private long lastTime;
   public static boolean placing = false;

   public AutoHoleFillPlus() {
      super(LemonClient.Combat, "Auto Hole Fill+", "Automatically is a cunt to your enemies.");
      this.sgNear = this.settings.createGroup("Near");
      this.sgWalking = this.settings.createGroup("Walking");
      this.sgLooking = this.settings.createGroup("Looking");
      this.sgSelf = this.settings.createGroup("Self");
      this.sgPlacing = this.settings.createGroup("Placing");
      this.sgRender = this.settings.createGroup("Render");
      this.sgHole = this.settings.createGroup("Hole");
      this.near = this.sgNear.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Near")).description(".")).defaultValue(true)).build());
      this.nearDistance = this.sgNear.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Near Distance")).description(".")).defaultValue(3.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.nearExt = this.sgNear.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Extrapolation")).description(".")).defaultValue(5)).min(0).sliderRange(0, 20).build());
      this.selfExt = this.sgNear.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Self Extrapolation")).description(".")).defaultValue(2)).min(0).sliderRange(0, 20).build());
      this.extSmooth = this.sgNear.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Extrapolation Smoothening")).description(".")).defaultValue(2)).min(1).sliderRange(0, 20).build());
      this.efficient = this.sgNear.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Efficient")).description("Only places if the hole is closer to target.")).defaultValue(true)).build());
      this.above = this.sgNear.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Above")).description("Only places if target is above the hole.")).defaultValue(true)).build());
      this.iHole = this.sgNear.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore Hole")).description("Doesn't place if enemy is in a hole.")).defaultValue(true)).build());
      this.walking = this.sgWalking.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Walking")).description(".")).defaultValue(true)).build());
      this.walkingDist = this.sgWalking.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Walking Dist")).description(".")).defaultValue(6.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.walkMemory = this.sgWalking.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Walk Memory")).description("Fills the hole is enemy was walking to it during previous x ticks.")).defaultValue(5)).min(0).sliderRange(0, 20).build());
      this.look = this.sgLooking.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Looking")).description(".")).defaultValue(true)).build());
      this.lookDist = this.sgLooking.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Look Dist")).description(".")).defaultValue(10.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.lookMemory = this.sgWalking.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Look Memory")).description("Fills the hole is enemy was looking at it during previous x ticks.")).defaultValue(5)).min(0).sliderRange(0, 20).build());
      this.iSelfHole = this.sgSelf.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore Self Hole")).description("Doesn't check 'efficient' if you are in a hole.")).defaultValue(true)).build());
      this.selfAbove = this.sgSelf.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Self Above")).description("Only checks 'efficient' if you are above the hole.")).defaultValue(true)).build());
      this.selfDistance = this.sgSelf.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Self Distance")).description("Doesn't place if the block is this close to you.")).defaultValue(0.0).sliderRange(0.0, 10.0).build());
      SettingGroup var10001 = this.sgSelf;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Self Walking")).description("Doesn't check 'efficient' if you are in a hole.")).defaultValue(true);
      Setting var10003 = this.efficient;
      Objects.requireNonNull(var10003);
      this.selfWalking = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.selfWalkingDist = this.sgSelf.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Self Walk Dist")).description(".")).defaultValue(3.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.selfWalkMemory = this.sgSelf.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Self Walk Memory")).description("Doesn't fill any hole you were walking to during past x ticks.")).defaultValue(2)).min(0).sliderRange(0, 20).build());
      this.switchMode = this.sgPlacing.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Method of switching. Silent is the most reliable but delays crystals on some servers.")).defaultValue(AutoHoleFillPlus.SwitchMode.Silent)).build());
      this.placeDelayMode = this.sgPlacing.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Delay Mode")).description(".")).defaultValue(SurroundPlus.PlaceDelayMode.Ticks)).build());
      this.placeDelayT = this.sgPlacing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Place Tick Delay")).description("Tick delay between places.")).defaultValue(1)).min(1).sliderRange(0, 20).visible(() -> {
         return this.placeDelayMode.get() == SurroundPlus.PlaceDelayMode.Ticks;
      })).build());
      this.placeDelayS = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Delay")).description("Delay between places.")).defaultValue(0.1).min(0.0).sliderRange(0.0, 1.0).visible(() -> {
         return this.placeDelayMode.get() == SurroundPlus.PlaceDelayMode.Seconds;
      })).build());
      this.places = this.sgPlacing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Places")).description("How many blocks to place each time.")).defaultValue(1)).min(1).sliderRange(0, 20).build());
      this.delay = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Delay")).description("Waits x seconds before trying to place at the same position if there is more than 1 missing block.")).defaultValue(0.3).min(0.0).sliderRange(0.0, 1.0).build());
      this.blocks = this.sgPlacing.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("Blocks")).description("Blocks to use.")).defaultValue(new class_2248[]{class_2246.field_10540}).build());
      this.boxExt = this.sgPlacing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Box Extrapolation")).description("Enemy hitbox extrapolation")).defaultValue(0)).min(0).sliderRange(0, 20).build());
      this.selfBoxExt = this.sgPlacing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Self Box Extrapolation")).description("Enemy hitbox extrapolation")).defaultValue(0)).min(0).sliderRange(0, 20).build());
      this.boxExtSmooth = this.sgPlacing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Box Extrapolation Smoothening")).description(".")).defaultValue(2)).min(1).sliderRange(0, 20).build());
      this.single = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Single")).description("Fills 1x1 holes")).defaultValue(true)).build());
      this.doubleHole = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Double")).description("Fills 2x1 block holes")).defaultValue(true)).build());
      this.quad = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Quad")).description("Fills 2x2 block holes")).defaultValue(true)).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders swing animation when placing a block.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var1 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      this.renderTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Render Time")).description("How long the box should remain in full alpha.")).defaultValue(0.3).min(0.0).sliderRange(0.0, 10.0).build());
      this.fadeTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Fade Time")).description("How long the fading should take.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts of boxes should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(0, 255, 0, 255)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(0, 255, 0, 50)).build());
      this.holes = new ArrayList();
      this.timers = new TimerList();
      this.render = new ArrayList();
      this.walkAngles = new HashMap();
      this.lookAngles = new HashMap();
      this.nearPosition = new HashMap();
      this.boxes = new HashMap();
      this.shouldUpdate = false;
      this.hand = null;
      this.blocksLeft = 0;
      this.placesLeft = 0;
      this.result = null;
      this.switched = false;
      this.tickTimer = 0;
      this.lastTime = 0L;
   }

   public void onActivate() {
      super.onActivate();
   }

   @EventHandler(
      priority = 200
   )
   private void onTick(TickEvent.Post event) {
      this.shouldUpdate = true;
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      this.timers.update();
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.shouldUpdate) {
            this.update();
            this.shouldUpdate = false;
         }

         this.render.removeIf((r) -> {
            return System.currentTimeMillis() - r.time > 1000L;
         });
         this.render.forEach((r) -> {
            double progress = 1.0 - Math.min((double)(System.currentTimeMillis() - r.time) + (Double)this.renderTime.get() * 1000.0, (Double)this.fadeTime.get() * 1000.0) / ((Double)this.fadeTime.get() * 1000.0);
            event.renderer.box(r.pos, new Color(((SettingColor)this.sideColor.get()).r, ((SettingColor)this.sideColor.get()).g, ((SettingColor)this.sideColor.get()).b, (int)Math.round((double)((SettingColor)this.sideColor.get()).a * progress)), new Color(((SettingColor)this.lineColor.get()).r, ((SettingColor)this.lineColor.get()).g, ((SettingColor)this.lineColor.get()).b, (int)Math.round((double)((SettingColor)this.lineColor.get()).a * progress)), (ShapeMode)this.shapeMode.get(), 0);
         });
      }
   }

   private void update() {
      ++this.tickTimer;
      this.updateMaps();
      this.updateHoles();
      this.updateResult();
      this.updatePlaces();
      this.updatePlacing();
   }

   private void updatePlacing() {
      this.blocksLeft = Math.min(this.placesLeft, this.result.count());
      this.hand = this.getHand();
      this.switched = false;
      this.holes.stream().sorted(Comparator.comparingDouble((pos) -> {
         return pos.method_46558().method_1022(this.mc.field_1724.method_33571());
      })).forEach(this::place);
      if (this.switched && this.hand == null) {
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

   private void updateResult() {
      FindItemResult var10001;
      switch ((SwitchMode)this.switchMode.get()) {
         case Silent:
         case Normal:
            var10001 = InvUtils.findInHotbar(this::valid);
            break;
         case PickSilent:
         case InvSwitch:
            var10001 = InvUtils.find(this::valid);
            break;
         case Disabled:
            var10001 = null;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      this.result = var10001;
   }

   private class_1268 getHand() {
      if (this.valid(Managers.HOLDING.getStack())) {
         return class_1268.field_5808;
      } else {
         return this.valid(this.mc.field_1724.method_6079()) ? class_1268.field_5810 : null;
      }
   }

   private boolean valid(class_1799 stack) {
      class_1792 var3 = stack.method_7909();
      boolean var10000;
      if (var3 instanceof class_1747 block) {
         if (((List)this.blocks.get()).contains(block.method_7711())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   private void updateMaps() {
      this.updateWalk();
      this.updateLook();
      ExtrapolationUtils.extrapolateMap(this.nearPosition, (player) -> {
         return player == this.mc.field_1724 ? (Integer)this.selfExt.get() : (Integer)this.nearExt.get();
      }, (player) -> {
         return (Integer)this.extSmooth.get();
      });
      ExtrapolationUtils.extrapolateMap(this.boxes, (player) -> {
         return player == this.mc.field_1724 ? (Integer)this.selfBoxExt.get() : (Integer)this.boxExt.get();
      }, (player) -> {
         return (Integer)this.boxExtSmooth.get();
      });
   }

   private void updateWalk() {
      Map newMap = new HashMap();
      Iterator var2 = this.mc.field_1687.method_18456().iterator();

      while(var2.hasNext()) {
         class_742 player = (class_742)var2.next();
         Movement m = new Movement(class_3532.method_15393((float)Math.toDegrees(Math.atan2(player.method_23321() - player.field_5969, player.method_23317() - player.field_6014)) - 90.0F), player.method_19538());
         if (!this.walkAngles.containsKey(player)) {
            List l = new ArrayList();
            l.add(m);
            newMap.put(player, l);
         } else {
            List l = (List)this.walkAngles.get(player);
            l.add(0, m);
            if (l.size() > 20) {
               l.subList(20, l.size()).clear();
            }

            newMap.put(player, l);
         }
      }

      this.walkAngles.clear();
      this.walkAngles.putAll(newMap);
      newMap.clear();
   }

   private void updateLook() {
      Map newMap = new HashMap();
      Iterator var2 = this.mc.field_1687.method_18456().iterator();

      while(var2.hasNext()) {
         class_742 player = (class_742)var2.next();
         Look e = new Look(class_3532.method_15393(player.method_36454()), player.method_36455(), player.method_33571());
         if (!this.lookAngles.containsKey(player)) {
            List l = new ArrayList();
            l.add(e);
            newMap.put(player, l);
         } else {
            List l = (List)this.lookAngles.get(player);
            l.add(0, e);
            if (l.size() > 20) {
               l.subList(20, l.size()).clear();
            }

            newMap.put(player, l);
         }
      }

      this.lookAngles.clear();
      this.lookAngles.putAll(newMap);
      newMap.clear();
   }

   private void updateHoles() {
      this.holes.clear();
      int range = (int)Math.ceil(Math.max(SettingUtils.getPlaceRange(), SettingUtils.getPlaceWallsRange()) + 1.0);
      class_2338 p = class_2338.method_49638(this.mc.field_1724.method_33571());
      List holeList = new ArrayList();

      for(int x = -range; x <= range; ++x) {
         for(int y = -range; y <= range; ++y) {
            for(int z = -range; z <= range; ++z) {
               Hole hole = HoleUtils.getHole(p.method_10069(x, y, z));
               if (hole.type != HoleType.NotHole && ((Boolean)this.single.get() || hole.type != HoleType.Single) && ((Boolean)this.doubleHole.get() || hole.type != HoleType.DoubleX && hole.type != HoleType.DoubleZ) && ((Boolean)this.quad.get() || hole.type != HoleType.Quad)) {
                  holeList.add(hole);
               }
            }
         }
      }

      holeList.forEach((holex) -> {
         if (this.validHole(holex)) {
            Stream var10000 = Arrays.stream(holex.positions).filter(this::validPos);
            List var10001 = this.holes;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::add);
         }
      });
   }

   private boolean validPos(class_2338 pos) {
      if (this.timers.contains(pos)) {
         return false;
      } else if (!LemonUtils.replaceable(pos)) {
         return false;
      } else {
         PlaceData data = SettingUtils.getPlaceData(pos);
         if (!data.valid()) {
            return false;
         } else if (!SettingUtils.inPlaceRange(data.pos())) {
            return false;
         } else {
            return !LemonEntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(pos)), (entity) -> {
               return !entity.method_7325() && !(entity instanceof class_1542);
            }, this.boxes);
         }
      }
   }

   private boolean validHole(Hole hole) {
      double pDist = this.mc.field_1724.method_19538().method_1022(hole.middle);
      if (this.selfCheck(hole)) {
         return false;
      } else {
         Iterator var4 = this.mc.field_1687.method_18456().iterator();

         while(var4.hasNext()) {
            class_742 player = (class_742)var4.next();
            if (!player.method_7325() && player != this.mc.field_1724 && !(player.method_6032() <= 0.0F) && !Friends.get().isFriend(player)) {
               if (this.nearCheck(player, hole, pDist)) {
                  return true;
               }

               if (this.walkingCheck(player, hole)) {
                  return true;
               }

               if (this.lookCheck(player, hole)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean selfCheck(Hole hole) {
      if (this.selfNearCheck(hole)) {
         return true;
      } else {
         return (Boolean)this.selfWalking.get() && this.walkCheck(this.mc.field_1724, hole, (Integer)this.selfWalkMemory.get(), (Double)this.selfWalkingDist.get());
      }
   }

   private boolean selfNearCheck(Hole hole) {
      class_2338 pos = new class_2338(this.mc.field_1724.method_31477(), (int)Math.round(this.mc.field_1724.method_23318()), this.mc.field_1724.method_31479());
      if ((Boolean)this.iSelfHole.get() && (HoleUtils.inHole(this.mc.field_1724) || LemonUtils.collidable(pos))) {
         return false;
      } else if ((Boolean)this.selfAbove.get() && this.mc.field_1724.method_23318() <= hole.middle.field_1351) {
         return false;
      } else {
         return this.mc.field_1724.method_19538().method_1022(hole.middle) <= (Double)this.selfDistance.get();
      }
   }

   private boolean nearCheck(class_742 player, Hole hole, double pDist) {
      if (!(Boolean)this.near.get()) {
         return false;
      } else {
         class_2338 pos = new class_2338(player.method_31477(), (int)Math.round(player.method_23318()), player.method_31479());
         if ((HoleUtils.inHole(player) || LemonUtils.collidable(pos)) && (Boolean)this.iHole.get()) {
            return false;
         } else if ((Boolean)this.above.get() && player.method_23318() <= hole.middle.field_1351) {
            return false;
         } else {
            double eDist = player.method_19538().method_1022(hole.middle);
            if (eDist > (Double)this.nearDistance.get()) {
               return false;
            } else {
               return !(Boolean)this.efficient.get() || !(pDist < eDist);
            }
         }
      }
   }

   private boolean walkingCheck(class_742 player, Hole hole) {
      return !(Boolean)this.walking.get() ? false : this.walkCheck(player, hole, (Integer)this.walkMemory.get(), (Double)this.walkingDist.get());
   }

   private boolean walkCheck(class_742 player, Hole hole, int ticks, double dist) {
      if (this.walkAngles.get(player) == null) {
         return false;
      } else {
         int i = 0;
         Iterator var7 = ((List)this.walkAngles.get(player)).iterator();

         while(var7.hasNext()) {
            Movement m = (Movement)var7.next();
            ++i;
            if (i > ticks) {
               break;
            }

            if (m.movementAngle != null && !(m.vec().method_1022(hole.middle) > dist)) {
               double yawToHole = RotationUtils.getYaw(m.vec(), hole.middle);
               double highestAngle = class_3532.method_16436(Math.min(player.method_19538().method_1022(hole.middle) / 8.0, 1.0), 90.0, 0.0);
               if (Math.abs(RotationUtils.yawAngle(yawToHole, (double)m.movementAngle)) < highestAngle) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean lookCheck(class_742 player, Hole hole) {
      if (!(Boolean)this.look.get()) {
         return false;
      } else if (this.lookAngles.get(player) == null) {
         return false;
      } else {
         int i = 0;
         Iterator var4 = ((List)this.lookAngles.get(player)).iterator();

         while(var4.hasNext()) {
            Look l = (Look)var4.next();
            ++i;
            if (i > (Integer)this.lookMemory.get()) {
               break;
            }

            if (!(l.vec().method_1022(hole.middle) > (Double)this.lookDist.get())) {
               double yawToHole = RotationUtils.getYaw(l.vec(), hole.middle);
               double highestAngle = class_3532.method_16436(Math.min(player.method_19538().method_1022(hole.middle) / 20.0, 1.0), 35.0, 5.0);
               if (Math.abs(RotationUtils.yawAngle(yawToHole, (double)l.yaw)) < highestAngle && Math.abs(RotationUtils.getPitch(l.vec, hole.middle) - (double)l.pitch()) < highestAngle) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void updatePlaces() {
      switch ((SurroundPlus.PlaceDelayMode)this.placeDelayMode.get()) {
         case Ticks:
            if (this.placesLeft >= (Integer)this.places.get() || this.tickTimer >= (Integer)this.placeDelayT.get()) {
               this.placesLeft = (Integer)this.places.get();
               this.tickTimer = 0;
            }
            break;
         case Seconds:
            if (this.placesLeft >= (Integer)this.places.get() || (double)(System.currentTimeMillis() - this.lastTime) >= (Double)this.placeDelayS.get() * 1000.0) {
               this.placesLeft = (Integer)this.places.get();
               this.lastTime = System.currentTimeMillis();
            }
      }

   }

   private void place(class_2338 pos) {
      if (this.blocksLeft > 0) {
         PlaceData data = SettingUtils.getPlaceData(pos);
         if (data != null && data.valid()) {
            placing = true;
            if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(data.pos(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}))) {
               if (!this.switched && this.hand == null) {
                  switch ((SwitchMode)this.switchMode.get()) {
                     case Silent:
                     case Normal:
                        InvUtils.swap(this.result.slot(), true);
                        this.switched = true;
                        break;
                     case PickSilent:
                        this.switched = InventoryUtils.pickSwitch(this.result.slot());
                        break;
                     case InvSwitch:
                        this.switched = InventoryUtils.invSwitch(this.result.slot());
                     case Disabled:
                  }
               }

               if (this.switched || this.hand != null) {
                  this.render.add(new Render(pos, System.currentTimeMillis()));
                  this.timers.add(pos, (Double)this.delay.get());
                  this.placeBlock(this.hand == null ? class_1268.field_5808 : this.hand, data.pos().method_46558(), data.dir(), data.pos());
                  if ((Boolean)this.placeSwing.get()) {
                     this.clientSwing((SwingHand)this.placeHand.get(), this.hand == null ? class_1268.field_5808 : this.hand);
                  }

                  --this.blocksLeft;
                  --this.placesLeft;
                  if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
                  }

               }
            }
         }
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

   private static record Movement(Float movementAngle, class_243 vec) {
      private Movement(Float movementAngle, class_243 vec) {
         this.movementAngle = movementAngle;
         this.vec = vec;
      }

      public Float movementAngle() {
         return this.movementAngle;
      }

      public class_243 vec() {
         return this.vec;
      }
   }

   private static record Look(float yaw, float pitch, class_243 vec) {
      private Look(float yaw, float pitch, class_243 vec) {
         this.yaw = yaw;
         this.pitch = pitch;
         this.vec = vec;
      }

      public float yaw() {
         return this.yaw;
      }

      public float pitch() {
         return this.pitch;
      }

      public class_243 vec() {
         return this.vec;
      }
   }

   private static record Render(class_2338 pos, Long time) {
      private Render(class_2338 pos, Long time) {
         this.pos = pos;
         this.time = time;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public Long time() {
         return this.time;
      }
   }

   public static enum LookCheckMode {
      // $FF: synthetic method
      private static LookCheckMode[] $values() {
         return new LookCheckMode[0];
      }
   }
}
