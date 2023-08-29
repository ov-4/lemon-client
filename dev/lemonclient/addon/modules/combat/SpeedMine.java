package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.timers.HarvestTimerUtils;
import dev.lemonclient.addon.utils.world.BlockInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
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
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2813;
import net.minecraft.class_2846;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;

public class SpeedMine extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgFastBreak;
   private final SettingGroup sgRender;
   private final Setting offset;
   private final Setting switchMode;
   private final Setting actionDelay;
   private final Setting bypass;
   private final Setting instant;
   private final Setting crystal;
   private final Setting age;
   private final Setting surroundOnly;
   private final Setting amplifier;
   private final Setting ignoreAir;
   private final Setting fastBreak;
   private final Setting haste;
   private final Setting swing;
   private final Setting placeHand;
   private final Setting shapeMode;
   private final Setting sideColor;
   private final Setting lineColor;
   private class_2338 blockPos;
   private class_2350 direction;
   private FindItemResult crystalItem;
   private int pickSlot;
   private int breakTimes;
   private long start;
   private long total;
   private final HarvestTimerUtils timer;
   private final HarvestTimerUtils mineTimer;

   public SpeedMine() {
      super(LemonClient.Combat, "Speed Mine+", "Sends packets to mine blocks without the mining animation.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgFastBreak = this.settings.createGroup("Fast Break");
      this.sgRender = this.settings.createGroup("Render");
      this.offset = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Offset")).defaultValue(0)).sliderRange(0, 200).build());
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).defaultValue(SpeedMine.SwitchMode.Fastest)).build());
      this.actionDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Action Delay")).defaultValue(0)).sliderRange(0, 5).build());
      this.bypass = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Bypass")).defaultValue(SpeedMine.BypassMode.Auto)).build());
      this.instant = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant")).defaultValue(false)).build());
      this.crystal = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Crystal")).description("Place crystal on target block.")).defaultValue(false)).build());
      this.age = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Age")).defaultValue(1)).sliderRange(0, 3).build());
      this.surroundOnly = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Only Surround")).defaultValue(false)).build());
      this.amplifier = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Amplifiter")).defaultValue(2)).sliderRange(1, 2).build());
      this.ignoreAir = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore Air")).defaultValue(true)).build());
      this.fastBreak = this.sgFastBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Fast Break")).defaultValue(false)).build());
      this.haste = this.sgFastBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Haste")).defaultValue(false)).build());
      this.swing = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders your swing client-side.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.swing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 100)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 100)).build());
      this.blockPos = null;
      this.direction = null;
      this.timer = new HarvestTimerUtils();
      this.mineTimer = new HarvestTimerUtils();
   }

   public void onActivate() {
      this.breakTimes = 0;
   }

   public void onDeactivate() {
      this.blockPos = null;
      this.direction = null;
      if (this.mc.field_1724.method_6059(class_1294.field_5917)) {
         this.mc.field_1724.method_6016(class_1294.field_5917);
      }

   }

   @EventHandler
   public void onStartBreaking(StartBreakingBlockEvent event) {
      class_2338 blockPos = new class_2338(event.blockPos);
      if (BlockInfo.isBreakable(blockPos)) {
         if (!(Boolean)this.surroundOnly.get() || EntityInfo.isPlayerNear(blockPos)) {
            this.blockPos = blockPos;
            this.direction = event.direction;
            this.breakTimes = 0;
            BlockInfo.progress = 0.0;
            this.start = System.currentTimeMillis();
            this.total = -1L;
         }
      }
   }

   @EventHandler
   public void onTick(TickEvent.Post event) {
      if (this.blockPos != null) {
         this.pickSlot = this.pickSlot();
         if (this.pickSlot != 420) {
            if (SettingUtils.inMineRange(this.blockPos)) {
               this.addhaste((Boolean)this.haste.get());
               this.crystalItem = InvUtils.find(new class_1792[]{class_1802.field_8301});
               if (!BlockInfo.canBreak(this.pickSlot, this.blockPos)) {
                  this.timer.reset();
                  this.total = System.currentTimeMillis() - this.start + (long)(Integer)this.offset.get();
                  this.mineTimer.reset();
               } else {
                  if ((Boolean)this.crystal.get() && this.crystalItem.found()) {
                     this.swap(this.crystalItem.slot(), this.blockPos, true, true);
                  }

                  this.swap(this.pickSlot, this.blockPos, false, true);
               }

               if (((BypassMode)this.bypass.get()).equals(SpeedMine.BypassMode.Auto) && !BlockInfo.isAir(this.blockPos) && BlockInfo.distanceTo(this.blockPos) < 5.0) {
                  long total = this.total;
                  long age = total - (long)((Integer)this.age.get() * 20);
                  if (this.mineTimer.passedMillis(age) && (Boolean)this.crystal.get() && this.crystalItem.found()) {
                     this.swap(this.crystalItem.slot(), this.blockPos, true, false);
                  }

                  if (SettingUtils.shouldRotate(RotationType.Mining)) {
                     Managers.ROTATION.start(this.blockPos, (double)this.priority, RotationType.Mining, (long)Objects.hash(new Object[]{this.name + "mining"}));
                  }

                  if (this.mineTimer.passedMillis(total)) {
                     this.swap(this.pickSlot, this.blockPos, false, false);
                     this.mineTimer.reset();
                  }
               }

            }
         }
      }
   }

   @EventHandler
   public void onBreak(BreakBlockEvent event) {
      if (this.pickSlot != 420) {
         if (((BypassMode)this.bypass.get()).equals(SpeedMine.BypassMode.Manual)) {
            if ((Boolean)this.crystal.get() && this.crystalItem.found()) {
               this.swap(this.crystalItem.slot(), this.blockPos, true, false);
            }

            this.swap(this.pickSlot, this.blockPos, false, false);
         }
      }
   }

   private void swap(int slot, class_2338 pos, boolean crystal, boolean check) {
      if (!check || slot != 420 && !(BlockInfo.progress < 1.0) && (!(Boolean)this.ignoreAir.get() || !BlockInfo.isAir(this.blockPos)) && ((Boolean)this.instant.get() || this.breakTimes < 1) && this.timer.passedTicks((long)(Integer)this.actionDelay.get())) {
         this.move(this.mc.field_1724.method_31548().field_7545, slot);
         if (crystal) {
            class_3965 hitResult = new class_3965(BlockInfo.closestVec3d2(this.blockPos), class_2350.field_11036, new class_2338(this.blockPos), false);
            if (BlockInfo.of(class_2246.field_10540, this.blockPos) && EntityInfo.isPlayerNear(this.blockPos)) {
               this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, hitResult);
            }
         } else {
            this.mine(pos);
         }

         this.move(this.mc.field_1724.method_31548().field_7545, slot);
         this.timer.reset();
      }
   }

   private int pickSlot() {
      FindItemResult pick = ((SwitchMode)this.switchMode.get()).equals(SpeedMine.SwitchMode.Fastest) ? InvUtils.findFastestTool(this.mc.field_1687.method_8320(this.blockPos)) : InvUtils.find(new class_1792[]{class_1802.field_8335, class_1802.field_8403});
      return pick.found() ? pick.slot() : 420;
   }

   private void move(int from, int to) {
      class_1703 handler = this.mc.field_1724.field_7512;
      Int2ObjectArrayMap stack = new Int2ObjectArrayMap();
      stack.put(to, handler.method_7611(to).method_7677());
      this.sendPacket(new class_2813(handler.field_7763, handler.method_37421(), 36 + from, to, class_1713.field_7791, handler.method_34255().method_7972(), stack));
   }

   private void mine(class_2338 blockPos) {
      this.sendPacket(new class_2846(class_2847.field_12973, blockPos, this.direction));
      if ((Boolean)this.swing.get()) {
         this.clientSwing((SwingHand)this.placeHand.get(), class_1268.field_5808);
      }

      if ((Boolean)this.fastBreak.get()) {
         BlockInfo.state(class_2246.field_10124, blockPos);
      }

      ++this.breakTimes;
   }

   private void addhaste(boolean haste) {
      if (!this.mc.field_1724.method_6059(class_1294.field_5917) && haste) {
         this.mc.field_1724.method_6092(new class_1293(class_1294.field_5917, 255, (Integer)this.amplifier.get() - 1, false, false, true));
      }
   }

   @EventHandler
   public void onRender(Render3DEvent event) {
      if (this.blockPos != null) {
         int slot = this.pickSlot();
         if (slot != 420) {
            double min = BlockInfo.progress / 2.0;
            class_243 vec3d = this.blockPos.method_46558();
            class_238 box = new class_238(vec3d.field_1352 - min, vec3d.field_1351 - min, vec3d.field_1350 - min, vec3d.field_1352 + min, vec3d.field_1351 + min, vec3d.field_1350 + min);
            event.renderer.box(box, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         }
      }
   }

   public static enum SwitchMode {
      NoDrop,
      Fastest;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{NoDrop, Fastest};
      }
   }

   public static enum BypassMode {
      Auto,
      Manual,
      OFF;

      // $FF: synthetic method
      private static BypassMode[] $values() {
         return new BypassMode[]{Auto, Manual, OFF};
      }
   }
}
