package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.world.BlockInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;

public class TNTAura extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgAutoBreak;
   private final SettingGroup sgPause;
   private final SettingGroup sgSwing;
   private final SettingGroup sgObsidianRender;
   private final SettingGroup sgTNTRender;
   private final SettingGroup sgBreakRender;
   private final Setting range;
   private final Setting delay;
   private final Setting autoBreak;
   public final Setting breakMode;
   private final Setting burrowPause;
   private final Setting antiSelf;
   private final Setting holePause;
   private final Setting pauseOnEat;
   private final Setting pauseOnDrink;
   private final Setting pauseOnMine;
   private final Setting swing;
   private final Setting placeHand;
   private final Setting obsidianRender;
   private final Setting obsidianShapeMode;
   private final Setting obsidianSideColor;
   private final Setting obsidianLineColor;
   private final Setting obsidianNextSideColor;
   private final Setting obsidianNextLineColor;
   private final Setting tntRender;
   private final Setting tntShapeMode;
   private final Setting tntSideColor;
   private final Setting tntLineColor;
   private final Setting breakRender;
   private final Setting breakShapeMode;
   private final Setting breakSideColor;
   private final Setting breakLineColor;
   private class_1657 target;
   private final List obsidianPos;
   private int ticks;
   private class_2350 direction;
   private boolean rofl;
   private boolean toggled;

   public TNTAura() {
      super(LemonClient.Combat, "TNT Aura", "Placing & igniting TNT around enemy");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgAutoBreak = this.settings.createGroup("Auto-Break");
      this.sgPause = this.settings.createGroup("Pause");
      this.sgSwing = this.settings.createGroup("Swing");
      this.sgObsidianRender = this.settings.createGroup("Obsidian-Render");
      this.sgTNTRender = this.settings.createGroup("TNT-Render");
      this.sgBreakRender = this.settings.createGroup("Break-Render");
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("target-range")).description("max range to target")).defaultValue(4)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("How many ticks between obsidian placement")).defaultValue(1)).build());
      this.autoBreak = this.sgAutoBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-break")).description("attemps to auto break")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgAutoBreak;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("break-mode")).defaultValue(TNTAura.mineMode.Normal);
      Setting var10003 = this.autoBreak;
      Objects.requireNonNull(var10003);
      this.breakMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.burrowPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-burrow")).description("will pause if enemy is burrowed")).defaultValue(true)).build());
      this.antiSelf = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-self")).description("pause if enemy in your hole")).defaultValue(true)).build());
      this.holePause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-in-hole")).description("pause if enemy isnt in hole")).defaultValue(false)).build());
      this.pauseOnEat = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-eat")).description("Pauses while eating.")).defaultValue(true)).build());
      this.pauseOnDrink = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-drink")).description("Pauses while drinking.")).defaultValue(true)).build());
      this.pauseOnMine = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-mine")).description("Pauses while mining.")).defaultValue(true)).build());
      this.swing = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders your swing client-side.")).defaultValue(true)).build());
      var10001 = this.sgSwing;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.swing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.obsidianRender = this.sgObsidianRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.obsidianShapeMode = this.sgObsidianRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.obsidianSideColor = this.sgObsidianRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(0, 255, 0, 60)).build());
      this.obsidianLineColor = this.sgObsidianRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(0, 255, 0, 190)).build());
      this.obsidianNextSideColor = this.sgObsidianRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-side-color")).description("The side color of the next block to be placed.")).defaultValue(new SettingColor(255, 0, 0, 60)).build());
      this.obsidianNextLineColor = this.sgObsidianRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-line-color")).description("The line color of the next block to be placed.")).defaultValue(new SettingColor(255, 0, 0, 190)).build());
      this.tntRender = this.sgTNTRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.tntShapeMode = this.sgTNTRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.tntSideColor = this.sgTNTRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(0, 255, 0, 60)).build());
      this.tntLineColor = this.sgTNTRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(0, 255, 0, 190)).build());
      this.breakRender = this.sgBreakRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.breakShapeMode = this.sgBreakRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.breakSideColor = this.sgBreakRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(0, 0, 255, 60)).build());
      this.breakLineColor = this.sgBreakRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(0, 0, 255, 190)).build());
      this.obsidianPos = new ArrayList();
   }

   public void onActivate() {
      this.obsidianPos.clear();
      this.ticks = 0;
      this.rofl = false;
      this.toggled = false;
   }

   public void onDeactivate() {
      this.obsidianPos.clear();
   }

   @EventHandler
   private void onStartBreakingBlock(StartBreakingBlockEvent event) {
      this.direction = event.direction;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      FindItemResult obsidian = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8281});
      if (!obsidian.isHotbar() && !this.toggled) {
         this.obsidianPos.clear();
         this.sendDisableMsg("No obsidian found in hotbar");
         this.toggle();
         this.toggled = true;
      }

      FindItemResult flint = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8884});
      if (!flint.isHotbar() && !this.toggled) {
         this.obsidianPos.clear();
         this.sendDisableMsg("No flint and steel found in hotbar");
         this.toggle();
         this.toggled = true;
      }

      FindItemResult tnt = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8626});
      if (!tnt.isHotbar() && !this.toggled) {
         this.obsidianPos.clear();
         this.sendDisableMsg("No TNT found in hotbar");
         this.toggle();
         this.toggled = true;
      }

      FindItemResult pickaxe = InvUtils.find((itemStack) -> {
         return itemStack.method_7909() == class_1802.field_8377 || itemStack.method_7909() == class_1802.field_22024;
      });
      if (!pickaxe.isHotbar() && !this.toggled) {
         this.obsidianPos.clear();
         this.sendDisableMsg("No pickaxe found in hotbar");
         this.toggle();
         this.toggled = true;
      }

      if (TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
         this.target = TargetUtils.getPlayerTarget((double)(Integer)this.range.get(), SortPriority.LowestDistance);
      }

      if (this.target != null) {
         if ((Boolean)this.burrowPause.get() && this.isBurrowed(this.target) && !this.toggled) {
            this.obsidianPos.clear();
            ChatUtils.error("Target is burrowed", new Object[0]);
            this.toggle();
            this.toggled = true;
         }

         if ((Boolean)this.antiSelf.get() && this.antiSelf(this.target) && !this.toggled) {
            this.obsidianPos.clear();
            ChatUtils.error("Target in your hole!", new Object[0]);
            this.toggle();
            this.toggled = true;
         }

         if ((Boolean)this.holePause.get() && !EntityInfo.isSurrounded(this.target) && !this.toggled) {
            this.obsidianPos.clear();
            ChatUtils.error("Target is not surrounded", new Object[0]);
            this.toggle();
            this.toggled = true;
         }

         if (TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get()) && !this.toggled) {
            ChatUtils.error("Enemy is too far away", new Object[0]);
            this.toggle();
            this.toggled = true;
         }

         if (!PlayerUtils.shouldPause((Boolean)this.pauseOnMine.get(), (Boolean)this.pauseOnEat.get(), (Boolean)this.pauseOnDrink.get())) {
            if (this.allowTNT(this.target)) {
               this.placeTNT(this.target);
               this.igniteTNT(this.target.method_24515().method_10086(2), flint);
            }

            if (!this.mineBlockstate(this.target.method_24515().method_10086(2)) && (Boolean)this.autoBreak.get()) {
               this.mine(this.target.method_24515().method_10086(2), pickaxe);
            }

            this.placeObsidian(this.target);
            if (this.ticks >= (Integer)this.delay.get() && this.obsidianPos.size() > 0) {
               class_2338 blockPos = (class_2338)this.obsidianPos.get(this.obsidianPos.size() - 1);
               if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                  Managers.ROTATION.start(blockPos, (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
               }

               if (BlockUtils.place(blockPos, obsidian, 50, true)) {
                  this.obsidianPos.remove(blockPos);
               }

               this.ticks = 0;
            } else {
               ++this.ticks;
            }

         }
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.obsidianRender.get() && !this.obsidianPos.isEmpty()) {
         Iterator var2 = this.obsidianPos.iterator();

         while(var2.hasNext()) {
            class_2338 pos = (class_2338)var2.next();
            boolean isFirst = pos.equals(this.obsidianPos.get(this.obsidianPos.size() - 1));
            Color side = isFirst ? (Color)this.obsidianNextSideColor.get() : (Color)this.obsidianSideColor.get();
            Color line = isFirst ? (Color)this.obsidianNextLineColor.get() : (Color)this.obsidianLineColor.get();
            event.renderer.box(pos, side, line, (ShapeMode)this.obsidianShapeMode.get(), 0);
         }
      }

      if ((Boolean)this.tntRender.get() && this.target != null && this.allowTNT(this.target) && this.tntBlockstate(this.target.method_24515().method_10069(0, 2, 0))) {
         event.renderer.box(this.target.method_24515().method_10069(0, 2, 0), (Color)this.tntSideColor.get(), (Color)this.tntLineColor.get(), (ShapeMode)this.tntShapeMode.get(), 0);
      }

      if ((Boolean)this.breakRender.get() && this.target != null && (Boolean)this.autoBreak.get() && !this.mineBlockstate(this.target.method_24515().method_10069(0, 2, 0))) {
         event.renderer.box(this.target.method_24515().method_10069(0, 2, 0), (Color)this.breakSideColor.get(), (Color)this.breakLineColor.get(), (ShapeMode)this.breakShapeMode.get(), 0);
      }

   }

   private void placeObsidian(class_1657 target) {
      this.obsidianPos.clear();
      class_2338 targetPos = EntityInfo.getBlockPos(target);
      this.add(targetPos.method_10069(0, 3, 0));
      this.add(targetPos.method_10069(1, 2, 0));
      this.add(targetPos.method_10069(-1, 2, 0));
      this.add(targetPos.method_10069(0, 2, 1));
      this.add(targetPos.method_10069(0, 2, -1));
   }

   private void placeTNT(class_1657 target) {
      FindItemResult tnt = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8626});
      class_2338 targetPos = EntityInfo.getBlockPos(target);
      class_2338 tntPos = targetPos.method_10069(0, 2, 0);
      if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
         Managers.ROTATION.start(tntPos, (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
      }

      BlockUtils.place(tntPos, tnt, false, 50, true, true);
   }

   private void add(class_2338 blockPos) {
      if (!this.obsidianPos.contains(blockPos) && BlockUtils.canPlace(blockPos)) {
         this.obsidianPos.add(blockPos);
      }

   }

   private void igniteTNT(class_2338 pos, FindItemResult item) {
      InvUtils.swap(item.slot(), true);
      this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, new class_3965(new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5), class_2350.field_11036, pos, true));
      InvUtils.swapBack();
   }

   public boolean tntBlockstate(class_2338 Pos) {
      return BlockInfo.getBlock(Pos) == class_2246.field_10124 || BlockInfo.getBlock(Pos) == class_2246.field_10375;
   }

   public boolean allowTNT(class_1309 target) {
      assert this.mc.field_1687 != null;

      return !BlockInfo.isAir(target.method_24515().method_10069(1, 2, 0)) && !BlockInfo.isAir(target.method_24515().method_10069(-1, 2, 0)) && !BlockInfo.isAir(target.method_24515().method_10069(0, 2, 1)) && !BlockInfo.isAir(target.method_24515().method_10069(0, 2, -1)) && !BlockInfo.isAir(target.method_24515().method_10069(0, 3, 0));
   }

   public boolean mineBlockstate(class_2338 Pos) {
      return BlockInfo.getBlock(Pos) == class_2246.field_10124 || BlockInfo.getBlock(Pos) == class_2246.field_10375 || BlockInfo.getBlock(Pos) == class_2246.field_9987;
   }

   public void mine(class_2338 blockPos, FindItemResult item) {
      if (this.breakMode.get() == TNTAura.mineMode.Normal) {
         InvUtils.swap(item.slot(), false);
         this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, blockPos, class_2350.field_11036));
         if ((Boolean)this.swing.get()) {
            this.clientSwing((SwingHand)this.placeHand.get(), class_1268.field_5808);
         }

         this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, class_2350.field_11036));
      }

      if (this.breakMode.get() == TNTAura.mineMode.Instant) {
         InvUtils.swap(item.slot(), false);
         if (!this.rofl) {
            this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, blockPos, class_2350.field_11036));
            this.rofl = true;
         }

         if (SettingUtils.shouldRotate(RotationType.Mining)) {
            Managers.ROTATION.start(blockPos, (double)this.priority, RotationType.Mining, (long)Objects.hash(new Object[]{this.name + "mining"}));
            this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, this.direction));
         } else {
            this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, this.direction));
         }

         this.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
      }

   }

   private boolean isBurrowed(class_1309 target) {
      assert this.mc.field_1687 != null;

      return !BlockInfo.isAir(EntityInfo.getBlockPos((class_1297)target));
   }

   private boolean antiSelf(class_1309 target) {
      return this.mc.field_1724.method_24515().method_10263() == target.method_24515().method_10263() && this.mc.field_1724.method_24515().method_10260() == target.method_24515().method_10260() && this.mc.field_1724.method_24515().method_10264() == target.method_24515().method_10264();
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }

   public static enum mineMode {
      Normal,
      Instant;

      // $FF: synthetic method
      private static mineMode[] $values() {
         return new mineMode[]{Normal, Instant};
      }
   }
}
