package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.events.PreRotationEvent;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.misc.ExtrapolationUtils;
import dev.lemonclient.addon.utils.player.DamageInfo;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.player.PlaceData;
import dev.lemonclient.addon.utils.timers.TimerList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1542;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_742;

public class AnchorAuraPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgDamage;
   private final SettingGroup sgExtrapolation;
   private final SettingGroup sgRender;
   private final Setting pauseEat;
   private final Setting switchMode;
   private final Setting packet;
   private final Setting placeSpeed;
   private final Setting explodeSpeed;
   private final Setting minDmg;
   private final Setting maxDmg;
   private final Setting minRatio;
   private final Setting selfExt;
   private final Setting extrapolation;
   private final Setting extSmoothness;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting interactSwing;
   private final Setting interactHand;
   public final Setting shapeMode;
   private final Setting lineColor;
   public final Setting color;
   private final List blocks;
   private int lastIndex;
   private int length;
   private long tickTime;
   private double bestDmg;
   private long lastTime;
   private class_2338 placePos;
   private PlaceData placeData;
   private class_2338 calcPos;
   private PlaceData calcData;
   private class_2338 renderPos;
   private final List targets;
   private final Map extMap;
   private final TimerList anchors;
   private class_2338 explodePos;
   private class_2350 explodeDir;
   private double dmg;
   private double self;
   private double friend;
   private long lastPlace;
   private long lastExplode;

   public AnchorAuraPlus() {
      super(LemonClient.Combat, "Anchor Aura Plus", "Automatically destroys people using anchors.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgDamage = this.settings.createGroup("Damage");
      this.sgExtrapolation = this.settings.createGroup("Extrapolation");
      this.sgRender = this.settings.createGroup("Render");
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause Eat")).description("Pauses when you are eating.")).defaultValue(true)).build());
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Switching method. Silent is the most reliable but doesn't work everywhere.")).defaultValue(AnchorAuraPlus.SwitchMode.Silent)).build());
      this.packet = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Packet")).description("Doesn't place blocks client side.")).defaultValue(true)).build());
      this.placeSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Speed")).description("How many anchors should be placed every second.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.explodeSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Explode Speed")).description("How many anchors should be blown every second.")).defaultValue(4.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.minDmg = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Damage")).description("Minimum damage required to place.")).defaultValue(8.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.maxDmg = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Max Damage")).description("Maximum damage to self.")).defaultValue(6.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.minRatio = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Damage Ratio")).description("Damage ratio between enemy damage and self damage (enemy / self).")).defaultValue(2.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.selfExt = this.sgExtrapolation.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Self Extrapolation")).description("How many ticks of movement should be predicted for self damage checks.")).defaultValue(0)).range(0, 100).sliderMax(20).build());
      this.extrapolation = this.sgExtrapolation.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Extrapolation")).description("How many ticks of movement should be predicted for enemy damage checks.")).defaultValue(0)).range(0, 100).sliderMax(20).build());
      this.extSmoothness = this.sgExtrapolation.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Extrapolation Smoothening")).description("How many earlier ticks should be used in average calculation for extrapolation motion.")).defaultValue(2)).range(1, 20).sliderRange(1, 20).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Place Swing")).description("Renders swing animation when placing a block.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.interactSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Interact Swing")).description("Renders swing animation when interacting with a block.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Interact Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.interactSwing;
      Objects.requireNonNull(var10003);
      this.interactHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts should be renderer.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Line color of rendered stuff")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
      this.color = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Side color of rendered stuff")).defaultValue(new SettingColor(255, 0, 0, 50)).build());
      this.blocks = new ArrayList();
      this.lastIndex = 0;
      this.length = 0;
      this.tickTime = -1L;
      this.bestDmg = -1.0;
      this.lastTime = 0L;
      this.placePos = null;
      this.placeData = null;
      this.calcPos = null;
      this.calcData = null;
      this.renderPos = null;
      this.targets = new ArrayList();
      this.extMap = new HashMap();
      this.anchors = new TimerList();
      this.explodePos = null;
      this.explodeDir = null;
      this.dmg = 0.0;
      this.self = 0.0;
      this.friend = 0.0;
      this.lastPlace = 0L;
      this.lastExplode = 0L;
   }

   public void onActivate() {
      this.anchors.clear();
      this.targets.clear();
      this.extMap.clear();
      this.placePos = null;
   }

   @EventHandler(
      priority = 200
   )
   private void onRotation(PreRotationEvent event) {
      this.calculate(this.length - 1);
      this.renderPos = this.calcPos;
      this.placePos = this.calcPos;
      this.placeData = this.calcData;
      this.getBlocks(this.mc.field_1724.method_33571(), Math.max(SettingUtils.getPlaceRange(), SettingUtils.getPlaceWallsRange()));
      this.tickTime = System.currentTimeMillis();
      this.length = this.blocks.size();
      this.lastIndex = 0;
      this.bestDmg = -1.0;
      this.calcPos = null;
      this.calcData = null;
      this.updateTargets();
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      double delta = (double)((float)(System.currentTimeMillis() - this.lastTime) / 1000.0F);
      this.lastTime = System.currentTimeMillis();
      if (this.tickTime >= 0L && this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.update();
         if (this.renderPos != null && this.pauseCheck()) {
            event.renderer.box(this.renderPos, (Color)this.color.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         }

      }
   }

   private boolean pauseCheck() {
      return !(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115();
   }

   private void updateTargets() {
      this.targets.clear();
      double closestDist = 1000.0;

      label44:
      for(int i = 4; i > 0; --i) {
         class_742 closest = null;
         Iterator var5 = this.mc.field_1687.method_18456().iterator();

         while(true) {
            class_742 player;
            double dist;
            do {
               do {
                  do {
                     if (!var5.hasNext()) {
                        if (closest != null) {
                           this.targets.add(closest);
                        }
                        continue label44;
                     }

                     player = (class_742)var5.next();
                  } while(this.targets.contains(player));

                  dist = (double)player.method_5739(this.mc.field_1724);
               } while(dist > 15.0);
            } while(closest != null && !(dist < closestDist));

            closestDist = dist;
            closest = player;
         }
      }

      ExtrapolationUtils.extrapolateMap(this.extMap, (playerx) -> {
         return playerx == this.mc.field_1724 ? (Integer)this.selfExt.get() : (Integer)this.extrapolation.get();
      }, (playerx) -> {
         return (Integer)this.extSmoothness.get();
      });
   }

   private void getBlocks(class_243 middle, double radius) {
      this.blocks.clear();
      int i = (int)Math.ceil(radius);

      for(int x = -i; x <= i; ++x) {
         for(int y = -i; y <= i; ++y) {
            for(int z = -i; z <= i; ++z) {
               class_2338 pos = new class_2338((int)(Math.floor(middle.field_1352) + (double)x), (int)(Math.floor(middle.field_1351) + (double)y), (int)(Math.floor(middle.field_1350) + (double)z));
               if ((LemonUtils.replaceable(pos) || this.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_23152) && this.inRangeToTargets(pos)) {
                  this.blocks.add(pos);
               }
            }
         }
      }

   }

   private void update() {
      int index = Math.min((int)Math.ceil((double)((float)(System.currentTimeMillis() - this.tickTime) / 50.0F * (float)this.length)), this.length - 1);
      this.calculate(index);
      if (!(Boolean)this.pauseEat.get() || !this.pauseCheck()) {
         this.updatePlacing();
         this.updateExploding();
      }
   }

   private void updatePlacing() {
      if (!((double)(System.currentTimeMillis() - this.lastPlace) < 1000.0 / (Double)this.placeSpeed.get())) {
         if (this.placeData.valid()) {
            if (this.placePos != null) {
               Anchor a = this.getAnchor(this.placePos);
               if (a.state == AnchorAuraPlus.AnchorState.Air) {
                  class_1268 hand = this.getHand((stack) -> {
                     return stack.method_7909() == class_1802.field_23141;
                  });
                  FindItemResult result = this.getResult((SwitchMode)this.switchMode.get(), (stack) -> {
                     return stack.method_7909() == class_1802.field_23141;
                  });
                  boolean present = hand != null || result.found();
                  if (present) {
                     if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(this.placePos, (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}))) {
                        boolean switched = false;
                        if (hand == null) {
                           switch ((SwitchMode)this.switchMode.get()) {
                              case Silent:
                              case Normal:
                                 InvUtils.swap(result.slot(), true);
                                 switched = true;
                                 break;
                              case PickSilent:
                                 switched = InventoryUtils.pickSwitch(result.slot());
                                 break;
                              case InvSwitch:
                                 switched = InventoryUtils.invSwitch(result.slot());
                           }
                        }

                        if (hand == null) {
                           if (!switched) {
                              return;
                           }

                           hand = class_1268.field_5808;
                        }

                        this.placeBlock(hand, this.placeData.pos().method_46558(), this.placeData.dir(), this.placeData.pos());
                        this.anchors.remove((t) -> {
                           return ((Anchor)t.value).pos.equals(this.placePos);
                        });
                        this.anchors.add(new Anchor(this.placePos, AnchorAuraPlus.AnchorState.Anchor, 0), 0.5);
                        this.lastPlace = System.currentTimeMillis();
                        if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                           Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
                        }

                        if ((Boolean)this.placeSwing.get()) {
                           this.clientSwing((SwingHand)this.placeHand.get(), hand);
                        }

                        if (switched) {
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
      }
   }

   private void updateExploding() {
      this.bestDmg = -1.0;
      this.explodePos = null;
      this.explodeDir = null;
      int i = (int)Math.ceil(Math.max(SettingUtils.getPlaceRange(), SettingUtils.getPlaceWallsRange()));

      for(int x = -i; x <= i; ++x) {
         for(int y = -i; y <= i; ++y) {
            for(int z = -i; z <= i; ++z) {
               class_2338 pos = class_2338.method_49638(this.mc.field_1724.method_33571());
               Anchor anchor = this.get(pos);
               if (anchor == null) {
                  return;
               }

               if (anchor.state == AnchorAuraPlus.AnchorState.Anchor || anchor.state == AnchorAuraPlus.AnchorState.Loaded) {
                  class_2350 dir = SettingUtils.getPlaceOnDirection(pos);
                  if (dir != null) {
                     return;
                  }

                  this.getDmg(pos);
                  if (this.explodeDmgCheck()) {
                     this.bestDmg = this.dmg;
                     this.explodePos = pos;
                     this.explodeDir = dir;
                  }
               }
            }
         }
      }

      this.explode();
   }

   private void explode() {
      Anchor anchor = this.getAnchor(this.explodePos);
      if (!((double)(System.currentTimeMillis() - this.lastExplode) < 1000.0 / (Double)this.explodeSpeed.get())) {
         if (anchor.state != AnchorAuraPlus.AnchorState.Air) {
            class_1268 glowHand = this.getHand((stack) -> {
               return stack.method_7909() == class_1802.field_8801;
            });
            class_1268 explodeHand = this.getHand((stack) -> {
               return stack.method_7909() != class_1802.field_8801;
            });
            FindItemResult glowResult = this.getResult((SwitchMode)this.switchMode.get(), (stack) -> {
               return stack.method_7909() == class_1802.field_8801;
            });
            FindItemResult explodeResult = this.getResult((SwitchMode)this.switchMode.get(), (stack) -> {
               return stack.method_7909() != class_1802.field_8801;
            });
            boolean glowPresent = glowHand != null || glowResult.found();
            boolean explodePresent = explodeHand != null || explodeResult.found();
            if (glowPresent && explodePresent) {
               if (!SettingUtils.shouldRotate(RotationType.Interact) || Managers.ROTATION.start(this.placePos, (double)this.priority, RotationType.Interact, (long)Objects.hash(new Object[]{this.name + "explode"}))) {
                  boolean switched = false;
                  if (glowHand == null) {
                     switch ((SwitchMode)this.switchMode.get()) {
                        case Silent:
                        case Normal:
                           InvUtils.swap(glowResult.slot(), true);
                           switched = true;
                           break;
                        case PickSilent:
                           switched = InventoryUtils.pickSwitch(glowResult.slot());
                           break;
                        case InvSwitch:
                           switched = InventoryUtils.invSwitch(glowResult.slot());
                     }
                  }

                  if (glowHand == null) {
                     if (!switched) {
                        return;
                     }

                     glowHand = class_1268.field_5808;
                  }

                  this.interactBlock(glowHand, this.explodePos.method_46558(), this.explodeDir, this.explodePos);
                  Anchor a = (Anchor)this.anchors.remove((t) -> {
                     return ((Anchor)t.value).pos.equals(this.placePos);
                  });
                  this.anchors.add(new Anchor(this.placePos, AnchorAuraPlus.AnchorState.Loaded, a.charges + 1), 0.5);
                  if (switched) {
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

                  switched = false;
                  if (explodeHand == null) {
                     switch ((SwitchMode)this.switchMode.get()) {
                        case Silent:
                        case Normal:
                           InvUtils.swap(explodeResult.slot(), true);
                           switched = true;
                           break;
                        case PickSilent:
                           switched = InventoryUtils.pickSwitch(explodeResult.slot());
                           break;
                        case InvSwitch:
                           switched = InventoryUtils.invSwitch(explodeResult.slot());
                     }
                  }

                  if (explodeHand == null) {
                     if (!switched) {
                        return;
                     }

                     explodeHand = class_1268.field_5808;
                  }

                  this.interactBlock(explodeHand, this.explodePos.method_46558(), this.explodeDir, this.explodePos);
                  this.lastExplode = System.currentTimeMillis();
                  this.anchors.remove((t) -> {
                     return ((Anchor)t.value).pos.equals(this.placePos);
                  });
                  this.anchors.add(new Anchor(this.placePos, AnchorAuraPlus.AnchorState.Air, 0), 0.5);
               }
            }
         }
      }
   }

   private Anchor getAnchor(class_2338 pos) {
      Anchor a = this.get(pos);
      if (a != null) {
         return a;
      } else {
         class_2680 state = this.mc.field_1687.method_8320(pos);
         return new Anchor(pos, state.method_26204() == class_2246.field_23152 ? ((Integer)state.method_11654(class_2741.field_23187) < 1 ? AnchorAuraPlus.AnchorState.Anchor : AnchorAuraPlus.AnchorState.Loaded) : AnchorAuraPlus.AnchorState.Air, state.method_26204() == class_2246.field_23152 ? (Integer)state.method_11654(class_2741.field_23187) : 0);
      }
   }

   private Anchor get(class_2338 pos) {
      List list = this.anchors.getList();
      Iterator var3 = list.iterator();

      Anchor a;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         a = (Anchor)var3.next();
      } while(!a.pos.equals(pos));

      return a;
   }

   private class_1268 getHand(Predicate predicate) {
      if (predicate.test(Managers.HOLDING.getStack())) {
         return class_1268.field_5808;
      } else {
         return predicate.test(this.mc.field_1724.method_6079()) ? class_1268.field_5810 : null;
      }
   }

   private FindItemResult getResult(SwitchMode mode, Predicate stackPredicate) {
      FindItemResult var10000;
      switch (mode) {
         case Silent:
         case Normal:
            var10000 = InvUtils.findInHotbar(stackPredicate);
            break;
         case PickSilent:
         case InvSwitch:
            var10000 = InvUtils.find(stackPredicate);
            break;
         case Disabled:
            var10000 = null;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private void calculate(int index) {
      for(int i = this.lastIndex; i < index; ++i) {
         class_2338 pos = (class_2338)this.blocks.get(i);
         PlaceData data = SettingUtils.getPlaceData(pos);
         if (data.valid() && !SettingUtils.inPlaceRange(data.pos())) {
            this.getDmg(pos);
            if (this.placeDmgCheck() && !EntityUtils.intersectsWithEntity(new class_238(pos), (entity) -> {
               return !(entity instanceof class_1542);
            })) {
               this.calcData = data;
               this.calcPos = pos;
               this.bestDmg = this.dmg;
            }
         }
      }

      this.lastIndex = index;
   }

   private boolean inRangeToTargets(class_2338 pos) {
      Iterator var2 = this.targets.iterator();

      class_742 target;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         target = (class_742)var2.next();
      } while(!(target.method_19538().method_1031(0.0, 1.0, 0.0).method_1022(class_243.method_24953(pos)) < 3.5));

      return true;
   }

   private boolean placeDmgCheck() {
      if (this.dmg < this.bestDmg) {
         return false;
      } else if (this.dmg < (Double)this.minDmg.get()) {
         return false;
      } else if (this.self > (Double)this.maxDmg.get()) {
         return false;
      } else {
         return !(this.dmg / this.self < (Double)this.minRatio.get());
      }
   }

   private boolean explodeDmgCheck() {
      if (this.dmg < this.bestDmg) {
         return false;
      } else if (this.dmg < (Double)this.minDmg.get()) {
         return false;
      } else if (this.self > (Double)this.maxDmg.get()) {
         return false;
      } else {
         return !(this.dmg / this.self < (Double)this.minRatio.get());
      }
   }

   private void getDmg(class_2338 pos) {
      this.dmg = -1.0;
      this.friend = -1.0;
      this.self = -1.0;
      this.targets.forEach((target) -> {
         double d = DamageInfo.anchorDamage(target, this.extMap.containsKey(target) ? (class_238)this.extMap.get(target) : target.method_5829(), pos);
         if (target == this.mc.field_1724) {
            this.self = Math.max(this.self, d);
         } else if (Friends.get().isFriend(target)) {
            this.friend = Math.max(this.friend, d);
         } else {
            this.dmg = Math.max(this.dmg, d);
         }

      });
   }

   public static enum SwitchMode {
      Silent,
      Normal,
      PickSilent,
      InvSwitch,
      Disabled;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Silent, Normal, PickSilent, InvSwitch, Disabled};
      }
   }

   private static record Anchor(class_2338 pos, AnchorState state, int charges) {
      private Anchor(class_2338 pos, AnchorState state, int charges) {
         this.pos = pos;
         this.state = state;
         this.charges = charges;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public AnchorState state() {
         return this.state;
      }

      public int charges() {
         return this.charges;
      }
   }

   public static enum AnchorState {
      Air,
      Anchor,
      Loaded;

      // $FF: synthetic method
      private static AnchorState[] $values() {
         return new AnchorState[]{Air, Anchor, Loaded};
      }
   }
}
