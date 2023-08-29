package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_3726;

public class AutoWither extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting horizontalRadius;
   private final Setting verticalRadius;
   private final Setting priority;
   private final Setting witherDelay;
   private final Setting blockDelay;
   private final Setting rotate;
   private final Setting turnOff;
   private final Setting shapeMode;
   private final Setting sideColor;
   private final Setting lineColor;
   private final Pool witherPool;
   private final ArrayList withers;
   private Wither wither;
   private int witherTicksWaited;
   private int blockTicksWaited;

   public AutoWither() {
      super(LemonClient.Misc, "Auto Wither", "Automatically builds withers.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.horizontalRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("horizontal-radius")).description("Horizontal radius for placement")).defaultValue(4)).min(0).sliderMax(6).build());
      this.verticalRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("vertical-radius")).description("Vertical radius for placement")).defaultValue(3)).min(0).sliderMax(6).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("Priority")).defaultValue(AutoWither.Priority.Random)).build());
      this.witherDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("wither-delay")).description("Delay in ticks between wither placements")).defaultValue(1)).min(1).sliderMax(10).build());
      this.blockDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("block-delay")).description("Delay in ticks between block placements")).defaultValue(1)).min(0).sliderMax(10).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Whether or not to rotate while building")).defaultValue(true)).build());
      this.turnOff = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("turn-off")).description("Turns off automatically after building a single wither.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232)).build());
      this.witherPool = new Pool(Wither::new);
      this.withers = new ArrayList();
   }

   public void onDeactivate() {
      this.wither = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.wither == null) {
         if (this.witherTicksWaited < (Integer)this.witherDelay.get() - 1) {
            return;
         }

         Iterator var2 = this.withers.iterator();

         while(var2.hasNext()) {
            Wither wither = (Wither)var2.next();
            this.witherPool.free(wither);
         }

         this.withers.clear();
         BlockIterator.register((Integer)this.horizontalRadius.get(), (Integer)this.verticalRadius.get(), (blockPos, blockState) -> {
            class_2350 dir = class_2350.method_10150(Rotations.getYaw(blockPos)).method_10153();
            if (this.isValidSpawn(blockPos, dir)) {
               this.withers.add(((Wither)this.witherPool.get()).set(blockPos, dir));
            }

         });
      }

   }

   @EventHandler
   private void onPostTick(TickEvent.Post event) {
      if (this.wither == null) {
         if (this.witherTicksWaited < (Integer)this.witherDelay.get() - 1) {
            ++this.witherTicksWaited;
            return;
         }

         if (this.withers.isEmpty()) {
            return;
         }

         switch ((Priority)this.priority.get()) {
            case Closest:
               this.withers.sort(Comparator.comparingDouble((w) -> {
                  return PlayerUtils.distanceTo(w.foot);
               }));
            case Furthest:
               this.withers.sort((w1, w2) -> {
                  int sort = Double.compare(PlayerUtils.distanceTo(w1.foot), PlayerUtils.distanceTo(w2.foot));
                  if (sort == 0) {
                     return 0;
                  } else {
                     return sort > 0 ? -1 : 1;
                  }
               });
            case Random:
               Collections.shuffle(this.withers);
            default:
               this.wither = (Wither)this.withers.get(0);
         }
      }

      FindItemResult findSoulSand = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8067});
      if (!findSoulSand.found()) {
         findSoulSand = InvUtils.findInHotbar(new class_1792[]{class_1802.field_21999});
      }

      FindItemResult findWitherSkull = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8791});
      if (findSoulSand.found() && findWitherSkull.found()) {
         if ((Integer)this.blockDelay.get() == 0) {
            BlockUtils.place(this.wither.foot, findSoulSand, (Boolean)this.rotate.get(), -50);
            BlockUtils.place(this.wither.foot.method_10084(), findSoulSand, (Boolean)this.rotate.get(), -50);
            BlockUtils.place(this.wither.foot.method_10084().method_30513(this.wither.axis, -1), findSoulSand, (Boolean)this.rotate.get(), -50);
            BlockUtils.place(this.wither.foot.method_10084().method_30513(this.wither.axis, 1), findSoulSand, (Boolean)this.rotate.get(), -50);
            BlockUtils.place(this.wither.foot.method_10084().method_10084(), findWitherSkull, (Boolean)this.rotate.get(), -50);
            BlockUtils.place(this.wither.foot.method_10084().method_10084().method_30513(this.wither.axis, -1), findWitherSkull, (Boolean)this.rotate.get(), -50);
            BlockUtils.place(this.wither.foot.method_10084().method_10084().method_30513(this.wither.axis, 1), findWitherSkull, (Boolean)this.rotate.get(), -50);
            if ((Boolean)this.turnOff.get()) {
               this.wither = null;
               this.toggle();
            }
         } else {
            if (this.blockTicksWaited < (Integer)this.blockDelay.get() - 1) {
               ++this.blockTicksWaited;
               return;
            }

            switch (this.wither.stage) {
               case 0:
                  if (BlockUtils.place(this.wither.foot, findSoulSand, (Boolean)this.rotate.get(), -50)) {
                     ++this.wither.stage;
                  }
                  break;
               case 1:
                  if (BlockUtils.place(this.wither.foot.method_10084(), findSoulSand, (Boolean)this.rotate.get(), -50)) {
                     ++this.wither.stage;
                  }
                  break;
               case 2:
                  if (BlockUtils.place(this.wither.foot.method_10084().method_30513(this.wither.axis, -1), findSoulSand, (Boolean)this.rotate.get(), -50)) {
                     ++this.wither.stage;
                  }
                  break;
               case 3:
                  if (BlockUtils.place(this.wither.foot.method_10084().method_30513(this.wither.axis, 1), findSoulSand, (Boolean)this.rotate.get(), -50)) {
                     ++this.wither.stage;
                  }
                  break;
               case 4:
                  if (BlockUtils.place(this.wither.foot.method_10084().method_10084(), findWitherSkull, (Boolean)this.rotate.get(), -50)) {
                     ++this.wither.stage;
                  }
                  break;
               case 5:
                  if (BlockUtils.place(this.wither.foot.method_10084().method_10084().method_30513(this.wither.axis, -1), findWitherSkull, (Boolean)this.rotate.get(), -50)) {
                     ++this.wither.stage;
                  }
                  break;
               case 6:
                  if (BlockUtils.place(this.wither.foot.method_10084().method_10084().method_30513(this.wither.axis, 1), findWitherSkull, (Boolean)this.rotate.get(), -50)) {
                     ++this.wither.stage;
                  }
                  break;
               case 7:
                  if ((Boolean)this.turnOff.get()) {
                     this.wither = null;
                     this.toggle();
                  }
            }
         }

         this.witherTicksWaited = 0;
      } else {
         this.error("Not enough resources in hotbar", new Object[0]);
         this.toggle();
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.wither != null) {
         event.renderer.box(this.wither.foot, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         event.renderer.box(this.wither.foot.method_10084(), (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         event.renderer.box(this.wither.foot.method_10084().method_30513(this.wither.axis, -1), (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         event.renderer.box(this.wither.foot.method_10084().method_30513(this.wither.axis, 1), (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         class_2338 midHead = this.wither.foot.method_10084().method_10084();
         class_2338 leftHead = this.wither.foot.method_10084().method_10084().method_30513(this.wither.axis, -1);
         class_2338 rightHead = this.wither.foot.method_10084().method_10084().method_30513(this.wither.axis, 1);
         event.renderer.box((double)midHead.method_10263() + 0.2, (double)midHead.method_10263(), (double)midHead.method_10263() + 0.2, (double)midHead.method_10263() + 0.8, (double)midHead.method_10263() + 0.7, (double)midHead.method_10263() + 0.8, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         event.renderer.box((double)leftHead.method_10263() + 0.2, (double)leftHead.method_10263(), (double)leftHead.method_10263() + 0.2, (double)leftHead.method_10263() + 0.8, (double)leftHead.method_10263() + 0.7, (double)leftHead.method_10263() + 0.8, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         event.renderer.box((double)rightHead.method_10263() + 0.2, (double)rightHead.method_10263(), (double)rightHead.method_10263() + 0.2, (double)rightHead.method_10263() + 0.8, (double)rightHead.method_10263() + 0.7, (double)rightHead.method_10263() + 0.8, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
      }
   }

   private boolean isValidSpawn(class_2338 blockPos, class_2350 direction) {
      if (blockPos.method_10264() > 252) {
         return false;
      } else {
         int widthX = 0;
         int widthZ = 0;
         if (direction == class_2350.field_11034 || direction == class_2350.field_11039) {
            widthZ = 1;
         }

         if (direction == class_2350.field_11043 || direction == class_2350.field_11035) {
            widthX = 1;
         }

         class_2338.class_2339 bp = new class_2338.class_2339();

         for(int x = blockPos.method_10263() - widthX; x <= blockPos.method_10263() + widthX; ++x) {
            for(int z = blockPos.method_10260() - widthZ; z <= blockPos.method_10260(); ++z) {
               for(int y = blockPos.method_10264(); y <= blockPos.method_10264() + 2; ++y) {
                  bp.method_10103(x, y, z);
                  if (!this.mc.field_1687.method_8320(bp).method_45474()) {
                     return false;
                  }

                  if (!this.mc.field_1687.method_8628(class_2246.field_10340.method_9564(), bp, class_3726.method_16194())) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   public static enum Priority {
      Closest,
      Furthest,
      Random;

      // $FF: synthetic method
      private static Priority[] $values() {
         return new Priority[]{Closest, Furthest, Random};
      }
   }

   private static class Wither {
      public int stage;
      public class_2338.class_2339 foot = new class_2338.class_2339();
      public class_2350 facing;
      public class_2350.class_2351 axis;

      public Wither set(class_2338 pos, class_2350 dir) {
         this.stage = 0;
         this.foot.method_10101(pos);
         this.facing = dir;
         this.axis = dir.method_10166();
         return this;
      }
   }
}
