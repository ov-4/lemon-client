package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.HoleType;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.player.PlaceData;
import dev.lemonclient.addon.utils.timers.TimerList;
import dev.lemonclient.addon.utils.world.hole.Hole;
import dev.lemonclient.addon.utils.world.hole.HoleUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3341;
import net.minecraft.class_2350.class_2353;

public class AutoHoleFill extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlacing;
   private final SettingGroup sgRender;
   private final SettingGroup sgHole;
   private final Setting pauseEat;
   private final Setting efficient;
   private final Setting above;
   private final Setting iHole;
   private final Setting holeRange;
   private final Setting switchMode;
   private final Setting blocks;
   private final Setting placeDelay;
   private final Setting places;
   private final Setting delay;
   private final Setting single;
   private final Setting doubleHole;
   private final Setting quad;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting shapeMode;
   private final Setting renderTime;
   private final Setting fadeTime;
   public final Setting lineColor;
   public final Setting color;
   private List holes;
   private final TimerList timers;
   private double placeTimer;
   private final Map toRender;

   public AutoHoleFill() {
      super(LemonClient.Combat, "Auto Hole Fill", "Automatically is a cunt to your enemies.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlacing = this.settings.createGroup("Placing");
      this.sgRender = this.settings.createGroup("Render");
      this.sgHole = this.settings.createGroup("Hole");
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause On Eat")).description("Pauses when you are eating")).defaultValue(true)).build());
      this.efficient = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Efficient")).description("Only places if the hole is closer to target")).defaultValue(true)).build());
      this.above = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Above")).description("Only places if target is above the hole")).defaultValue(true)).build());
      this.iHole = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore Hole")).description("Doesn't place if enemy is in a hole")).defaultValue(true)).build());
      this.holeRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Hole Range")).description("Places when enemy is close enough to target hole")).defaultValue(3.0).min(0.0).sliderMax(10.0).build());
      this.switchMode = this.sgPlacing.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Method of switching. Silent is the most reliable but delays crystals on some servers.")).defaultValue(AutoHoleFill.SwitchMode.Silent)).build());
      this.blocks = this.sgPlacing.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("Blocks")).description("Which blocks to use.")).defaultValue(new class_2248[]{class_2246.field_10540, class_2246.field_22423, class_2246.field_22108}).build());
      this.placeDelay = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Delay")).description("Delay between places.")).defaultValue(0.125).min(0.0).sliderRange(0.0, 10.0).build());
      this.places = this.sgPlacing.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Places")).description("Blocks placed per place")).defaultValue(1)).min(1).sliderRange(1, 10).build());
      this.delay = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Delay")).description("Delay between places at single spot.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.single = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Single")).description("Fills 1x1 holes")).defaultValue(true)).build());
      this.doubleHole = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Double")).description("Fills 2x1 block holes")).defaultValue(true)).build());
      this.quad = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Quad")).description("Fills 2x2 block holes")).defaultValue(true)).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders swing animation when placing a block.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description(".")).defaultValue(ShapeMode.Both)).build());
      this.renderTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Render Time")).description("How long the box should remain in full alpha.")).defaultValue(0.3).min(0.0).sliderRange(0.0, 10.0).build());
      this.fadeTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Fade Time")).description("How long the fading should take.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color of the outline.")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.color = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Color")).description("Color of the sides.")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.holes = new ArrayList();
      this.timers = new TimerList();
      this.placeTimer = 0.0;
      this.toRender = new HashMap();
   }

   public void onActivate() {
      super.onActivate();
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      this.timers.update();
      double d = event.frameTime;
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.placeTimer = Math.min(this.placeTimer + event.frameTime, (Double)this.placeDelay.get());
         this.update();
         List toRemove = new ArrayList();
         Iterator var5 = this.toRender.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry entry = (Map.Entry)var5.next();
            class_2338 pos = (class_2338)entry.getKey();
            Double[] alpha = (Double[])entry.getValue();
            if (alpha[0] <= d) {
               toRemove.add(pos);
            } else {
               event.renderer.box(class_238.method_19316(new class_3341(pos)), new Color(((SettingColor)this.color.get()).r, ((SettingColor)this.color.get()).g, ((SettingColor)this.color.get()).b, (int)Math.round((double)((SettingColor)this.color.get()).a * Math.min(1.0, alpha[0] / alpha[1]))), new Color(((SettingColor)this.lineColor.get()).r, ((SettingColor)this.lineColor.get()).g, ((SettingColor)this.lineColor.get()).b, (int)Math.round((double)((SettingColor)this.lineColor.get()).a * Math.min(1.0, alpha[0] / alpha[1]))), (ShapeMode)this.shapeMode.get(), 0);
               entry.setValue(new Double[]{alpha[0] - d, alpha[1]});
            }
         }

         Map var10001 = this.toRender;
         Objects.requireNonNull(var10001);
         toRemove.forEach(var10001::remove);
      }

   }

   private void update() {
      this.updateHoles(Math.max(SettingUtils.getPlaceRange(), SettingUtils.getPlaceWallsRange()) + 1.0);
      List placements = this.getValid(this.holes);
      FindItemResult result = InvUtils.findInHotbar((itemStack) -> {
         return itemStack.method_7909() instanceof class_1747 && ((List)this.blocks.get()).contains(((class_1747)itemStack.method_7909()).method_7711());
      });
      FindItemResult invResult = InvUtils.find((itemStack) -> {
         return itemStack.method_7909() instanceof class_1747 && ((List)this.blocks.get()).contains(((class_1747)itemStack.method_7909()).method_7711());
      });
      class_1268 hand = this.isValid(Managers.HOLDING.getStack()) ? class_1268.field_5808 : (this.isValid(this.mc.field_1724.method_6079()) ? class_1268.field_5810 : null);
      if (!placements.isEmpty() && (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) && this.placeTimer >= (Double)this.placeDelay.get() && (hand != null || this.switchMode.get() == AutoHoleFill.SwitchMode.Silent && result.slot() >= 0 || (this.switchMode.get() == AutoHoleFill.SwitchMode.PickSilent || this.switchMode.get() == AutoHoleFill.SwitchMode.InvSwitch) && invResult.slot() >= 0)) {
         List toPlace = new ArrayList();
         Iterator var6 = placements.iterator();

         while(var6.hasNext()) {
            class_2338 pos = (class_2338)var6.next();
            if (toPlace.size() < (Integer)this.places.get() && this.canPlace(pos)) {
               toPlace.add(pos);
            }
         }

         if (!toPlace.isEmpty()) {
            int obsidian = hand == class_1268.field_5808 ? Managers.HOLDING.getStack().method_7947() : (hand == class_1268.field_5810 ? this.mc.field_1724.method_6079().method_7947() : -1);
            if (hand == null) {
               switch ((SwitchMode)this.switchMode.get()) {
                  case Silent:
                     obsidian = result.count();
                     break;
                  case PickSilent:
                  case InvSwitch:
                     obsidian = invResult.slot() >= 0 ? invResult.count() : -1;
               }
            }

            if (obsidian >= 0) {
               if (hand == null) {
                  switch ((SwitchMode)this.switchMode.get()) {
                     case Silent:
                        obsidian = result.count();
                        InvUtils.swap(result.slot(), true);
                        break;
                     case PickSilent:
                        obsidian = InventoryUtils.pickSwitch(invResult.slot()) ? invResult.count() : -1;
                        break;
                     case InvSwitch:
                        obsidian = InventoryUtils.invSwitch(invResult.slot()) ? invResult.count() : -1;
                  }
               }

               this.placeTimer = 0.0;

               for(int i = 0; i < Math.min(obsidian, toPlace.size()); ++i) {
                  PlaceData placeData = SettingUtils.getPlaceData((class_2338)toPlace.get(i));
                  if (placeData.valid()) {
                     boolean rotated = !SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(placeData.pos(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
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

   private boolean isValid(class_1799 itemStack) {
      return itemStack.method_7909() instanceof class_1747 && ((List)this.blocks.get()).contains(((class_1747)itemStack.method_7909()).method_7711());
   }

   private List getValid(List positions) {
      List list = new ArrayList();
      Iterator var3 = positions.iterator();

      while(var3.hasNext()) {
         class_2338 pos = (class_2338)var3.next();
         if (!this.timers.contains(pos)) {
            list.add(pos);
         }
      }

      return list;
   }

   private void updateHoles(double range) {
      this.holes = new ArrayList();

      for(int x = (int)(-Math.ceil(range)); (double)x <= Math.ceil(range); ++x) {
         for(int y = (int)(-Math.ceil(range)); (double)y <= Math.ceil(range); ++y) {
            for(int z = (int)(-Math.ceil(range)); (double)z <= Math.ceil(range); ++z) {
               class_2338 pos = this.mc.field_1724.method_24515().method_10069(x, y, z);
               Hole h = HoleUtils.getHole(pos, (Boolean)this.single.get(), (Boolean)this.doubleHole.get(), (Boolean)this.quad.get(), 3, true);
               if (h.type != HoleType.NotHole) {
                  class_2338[] var8 = h.positions();
                  int var9 = var8.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                     class_2338 p = var8[var10];
                     if (LemonUtils.replaceable(p) && !EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(p)), (entity) -> {
                        return !entity.method_7325() && !(entity instanceof class_1542);
                     })) {
                        double closest = this.closestDist(p);
                        PlaceData d = SettingUtils.getPlaceData(p);
                        if (d.valid() && closest >= 0.0 && closest <= (Double)this.holeRange.get() && (!(Boolean)this.efficient.get() || this.mc.field_1724.method_19538().method_1022(class_243.method_24953(p)) > closest) && SettingUtils.inPlaceRange(d.pos())) {
                           this.holes.add(p);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private double closestDist(class_2338 pos) {
      double closest = -1.0;
      Iterator var4 = this.mc.field_1687.method_18456().iterator();

      while(true) {
         class_1657 pl;
         double dist;
         do {
            do {
               do {
                  do {
                     do {
                        if (!var4.hasNext()) {
                           return closest;
                        }

                        pl = (class_1657)var4.next();
                        dist = pl.method_19538().method_1022(class_243.method_24953(pos));
                     } while((Boolean)this.iHole.get() && this.inHole(pl));
                  } while((Boolean)this.above.get() && !(pl.method_23318() > (double)pos.method_10264()));
               } while(pl == this.mc.field_1724);
            } while(Friends.get().isFriend(pl));
         } while(!(closest < 0.0) && !(dist < closest));

         closest = dist;
      }
   }

   private boolean inHole(class_1657 pl) {
      Iterator var2 = class_2353.field_11062.iterator();

      class_2350 dir;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         dir = (class_2350)var2.next();
      } while(this.mc.field_1687.method_8320(pl.method_24515().method_10093(dir)).method_26204() != class_2246.field_10124);

      return false;
   }

   private boolean canPlace(class_2338 pos) {
      return SettingUtils.getPlaceData(pos).valid();
   }

   private void place(PlaceData d, class_2338 ogPos, class_1268 hand) {
      this.timers.add(ogPos, (Double)this.delay.get());
      this.placeBlock(hand, d.pos().method_46558(), d.dir(), d.pos());
      if ((Boolean)this.placeSwing.get()) {
         this.clientSwing((SwingHand)this.placeHand.get(), hand);
      }

      if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
         Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
      }

      if (!this.toRender.containsKey(ogPos)) {
         this.toRender.put(ogPos, new Double[]{(Double)this.fadeTime.get() + (Double)this.renderTime.get(), (Double)this.fadeTime.get()});
      } else {
         this.toRender.replace(ogPos, new Double[]{(Double)this.fadeTime.get() + (Double)this.renderTime.get(), (Double)this.fadeTime.get()});
      }

   }

   public static enum SwitchMode {
      Disabled,
      Silent,
      PickSilent,
      InvSwitch;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Disabled, Silent, PickSilent, InvSwitch};
      }
   }
}
