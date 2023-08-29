package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.render.RenderUtils;
import dev.lemonclient.addon.utils.world.BlockInfo;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.WorldRendererAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2281;
import net.minecraft.class_2336;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2482;
import net.minecraft.class_2596;
import net.minecraft.class_2620;
import net.minecraft.class_2793;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3191;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;

public class NewSurround extends LemonModule {
   private static final List posPlaceBlocks = new ArrayList();
   private final SettingGroup sgGeneral;
   private final SettingGroup sgForce;
   private final SettingGroup sgSafe;
   private final SettingGroup sgCenter;
   private final SettingGroup sgMisc;
   private final SettingGroup sgRender;
   private final Setting bpt;
   private final Setting helper;
   private final Setting packet;
   private final Setting doubleH;
   private final Setting blocks;
   private final Setting forceSurround;
   private final Setting forceDouble;
   private final Setting forceTrap;
   private final Setting forceAntiCity;
   private final Setting fagMode;
   private final Setting AGB;
   private final Setting antiCrystal;
   private final Setting antiCrystalMode;
   private final Setting antiCrystalDelay;
   private final Setting antiCrystalSwapDelay;
   private final Setting antiCrystalBlock;
   private final Setting antiCrystalBlockSwap;
   private final Setting centerMode;
   private final Setting centerDelay;
   private final Setting stop;
   private final Setting anchor;
   private final Setting pauseOnUse;
   private final Setting onlyOnGround;
   private final Setting disableOnJump;
   private final Setting disableOnTp;
   private final Setting disableOnYChange;
   private final Setting swing;
   private final Setting render;
   private final Setting newRender;
   private final Setting lineSize;
   private final Setting sideColor2;
   private final Setting lineColor2;
   private final Setting shapeMode;
   private final Setting sideColor;
   private final Setting lineColor;
   private final Pool renderBlockPool;
   private final List renderBlocks;
   Boolean obb;
   private class_2338 prevBreakPos;
   private class_2338 antiCrystalPos;
   private boolean fagNorth;
   private boolean fagEast;
   private boolean fagSouth;
   private boolean fagWest;
   private int ticks;
   private int centerDelayLeft;
   private int crystalDelay;
   private int swapDelay;

   public NewSurround() {
      super(LemonClient.Combat, "New Surround", "Surrounds you in blocks to prevent crystal damage");
      this.sgGeneral = this.settings.createGroup("General", true);
      this.sgForce = this.settings.createGroup("Force", true);
      this.sgSafe = this.settings.createGroup("Safe", true);
      this.sgCenter = this.settings.createGroup("Center", true);
      this.sgMisc = this.settings.createGroup("Misc", true);
      this.sgRender = this.settings.createGroup("Render", true);
      this.bpt = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("blocks-per-tick")).description("Number of blocks that can be placed per tick")).defaultValue(3)).min(1).sliderMax(10).build());
      this.helper = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Helper")).description("Help to Place Surround.")).defaultValue(false)).build());
      this.packet = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("packet")).description("Packet block placing method.")).defaultValue(false)).build());
      this.doubleH = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("double")).description("Places obsidian in face place positions")).defaultValue(false)).build());
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("block")).description("Which blocks used for surround.")).defaultValue(Collections.singletonList(class_2246.field_10540))).filter(this::blockFilter).build());
      this.forceSurround = this.sgForce.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("force-surround")).description("Force places surround blocks(cool for ping players or bad servers)")).defaultValue(false)).build());
      this.forceDouble = this.sgForce.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-doube")).description("Force double height surround")).defaultValue(Keybind.fromKey(-1))).build());
      this.forceTrap = this.sgForce.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-trap")).description("Force self trap")).defaultValue(Keybind.fromKey(-1))).build());
      this.forceAntiCity = this.sgForce.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-anti-city")).description("Force anti city blocks")).defaultValue(Keybind.fromKey(-1))).build());
      this.fagMode = this.sgSafe.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break-safety")).description("Force some blocks while surround is attacked.")).defaultValue(false)).build());
      this.AGB = this.sgSafe.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-ghost-block")).description("Removing client-side surround blocks.")).defaultValue(true)).build());
      this.antiCrystal = this.sgSafe.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-crystal")).description("Destroys all nearby crystals which can block surround blocks.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgSafe;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Events to trigger crystal breaker.")).defaultValue(NewSurround.ACMode.Break);
      Setting var10003 = this.antiCrystal;
      Objects.requireNonNull(var10003);
      this.antiCrystalMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgSafe;
      IntSetting.Builder var1 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-delay")).description("Delay for breaking crystal.")).defaultValue(3)).min(0).sliderMax(20);
      var10003 = this.antiCrystal;
      Objects.requireNonNull(var10003);
      this.antiCrystalDelay = var10001.add(((IntSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgSafe;
      var1 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("swap-delay")).description("Delay before swapping(bypasses some anticheats).")).defaultValue(2)).min(0).sliderMax(10);
      var10003 = this.antiCrystal;
      Objects.requireNonNull(var10003);
      this.antiCrystalSwapDelay = var10001.add(((IntSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgSafe;
      BoolSetting.Builder var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("block-on-crystal")).description("Placing block on crystal.")).defaultValue(false);
      var10003 = this.antiCrystal;
      Objects.requireNonNull(var10003);
      this.antiCrystalBlock = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgSafe;
      var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("block-swap")).description("Swapping to obsidian block while breaking crystal(for servers with break delay after switch)")).defaultValue(false);
      var10003 = this.antiCrystal;
      Objects.requireNonNull(var10003);
      this.antiCrystalBlockSwap = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      this.centerMode = this.sgCenter.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("center-mode")).description("Teleports you to the center of the surround.")).defaultValue(NewSurround.TpMode.Default)).build());
      this.centerDelay = this.sgCenter.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("Delay for teleporting to the center.")).defaultValue(5)).min(1).sliderMax(20).visible(() -> {
         return this.centerMode.get() == NewSurround.TpMode.Smooth;
      })).build());
      this.stop = this.sgCenter.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("stop-moving")).description("Stop all movements")).defaultValue(false)).build());
      this.anchor = this.sgCenter.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anchor")).description("Slows you to prevent massive cope")).defaultValue(true)).build());
      this.pauseOnUse = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-use")).description("Pauses surround if players is using item(eating etc).")).defaultValue(false)).build());
      this.onlyOnGround = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Works only when you standing on blocks.")).defaultValue(true)).build());
      this.disableOnJump = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-jump")).description("Automatically disables when you jump.")).defaultValue(true)).build());
      this.disableOnTp = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-tp")).description("Automatically disables when you teleport (chorus or pearl).")).defaultValue(true)).build());
      this.disableOnYChange = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-y-change")).description("Automatically disables when your y level changes.")).defaultValue(true)).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Client side hand-swing")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.newRender = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("new-render")).description("New type of block render")).defaultValue(true)).build());
      var10001 = this.sgRender;
      DoubleSetting.Builder var3 = ((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("line-size")).defaultValue(0.02).sliderRange(0.01, 1.0);
      var10003 = this.newRender;
      Objects.requireNonNull(var10003);
      this.lineSize = var10001.add(((DoubleSetting.Builder)var3.visible(var10003::get)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var4 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("second-side-color")).description("Side color.")).defaultValue(new SettingColor(0, 170, 255, 25));
      var10003 = this.newRender;
      Objects.requireNonNull(var10003);
      this.sideColor2 = var10001.add(((ColorSetting.Builder)var4.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var4 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("second-line-color")).description("Line color.")).defaultValue(new SettingColor(0, 170, 255, 55));
      var10003 = this.newRender;
      Objects.requireNonNull(var10003);
      this.lineColor2 = var10001.add(((ColorSetting.Builder)var4.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("Side color.")).defaultValue(new SettingColor(0, 170, 255, 25)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("Line color.")).defaultValue(new SettingColor(0, 170, 255, 55)).build());
      this.renderBlockPool = new Pool(() -> {
         return new RenderBlock();
      });
      this.renderBlocks = new ArrayList();
   }

   public void onActivate() {
      this.swapDelay = 0;
      this.fagNorth = false;
      this.fagEast = false;
      this.fagSouth = false;
      this.fagWest = false;
      this.centerDelayLeft = 0;
      this.crystalDelay = (Integer)this.antiCrystalDelay.get();
      switch ((TpMode)this.centerMode.get()) {
         case Default:
            PlayerUtils.centerPlayer();
            this.pause();
            break;
         case Smooth:
            this.centerDelayLeft = (Integer)this.centerDelay.get();
            if (this.inCenter()) {
               this.centerDelayLeft = 0;
            }

            if ((Boolean)this.anchor.get()) {
               ((IVec3d)this.mc.field_1724.method_18798()).set(0.0, this.mc.field_1724.method_18798().field_1351, 0.0);
            }
      }

      Iterator var1 = this.renderBlocks.iterator();

      while(var1.hasNext()) {
         RenderBlock renderBlock = (RenderBlock)var1.next();
         this.renderBlockPool.free(renderBlock);
      }

      this.renderBlocks.clear();
   }

   public void onDeactivate() {
      Iterator var1 = this.renderBlocks.iterator();

      while(var1.hasNext()) {
         RenderBlock renderBlock = (RenderBlock)var1.next();
         this.renderBlockPool.free(renderBlock);
      }

      this.renderBlocks.clear();
   }

   @EventHandler
   public void onBreakPacket(PacketEvent.Receive event) {
      if ((Boolean)this.fagMode.get()) {
         assert this.mc.field_1687 != null;

         assert this.mc.field_1724 != null;

         class_2596 var3 = event.packet;
         if (var3 instanceof class_2620) {
            class_2620 bbpp = (class_2620)var3;
            class_2338 bbp = bbpp.method_11277();
            if (bbp.equals(this.prevBreakPos) && bbpp.method_11278() > 0) {
               return;
            }

            class_2338 playerBlockPos = this.mc.field_1724.method_24515();
            this.obb = this.mc.field_1687.method_8320(bbp).method_26204().method_9520() >= 600.0F && BlockInfo.getHardness(bbp) > 0.0F;
            if (this.obb && bbp.equals(playerBlockPos.method_10095())) {
               this.fagNorth = true;
            } else if (this.obb && bbp.equals(playerBlockPos.method_10078())) {
               this.fagEast = true;
            } else if (this.obb && bbp.equals(playerBlockPos.method_10072())) {
               this.fagSouth = true;
            } else if (this.obb && bbp.equals(playerBlockPos.method_10067())) {
               this.fagWest = true;
            }

            this.prevBreakPos = bbp;
         }
      }

   }

   @EventHandler
   private void onPostTick(TickEvent.Post event) {
      --this.swapDelay;
      --this.crystalDelay;
      this.antiCrystalPos = null;
      FindItemResult block = InvUtils.findInHotbar((itemStack) -> {
         return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
      });
      Iterator var3;
      class_2338 p;
      if ((Boolean)this.AGB.get()) {
         var3 = posPlaceBlocks.iterator();

         while(var3.hasNext()) {
            p = (class_2338)var3.next();
            Iterator var5 = this.mc.field_1687.method_18112().iterator();

            while(var5.hasNext()) {
               class_1297 e = (class_1297)var5.next();
               if (e instanceof class_1511 && e.method_24515().equals(p)) {
                  this.mc.field_1687.method_8501(p, class_2246.field_10124.method_9564());
               }
            }
         }
      }

      if (this.crystalDelay <= 0) {
         this.crystalDelay = (Integer)this.antiCrystalDelay.get();
         var3 = posPlaceBlocks.iterator();

         label76:
         while(true) {
            do {
               if (!var3.hasNext()) {
                  break label76;
               }

               p = (class_2338)var3.next();
            } while(!(Boolean)this.antiCrystal.get());

            boolean mine = BlockInfo.isReplaceable(p) || this.antiCrystalMode.get() != NewSurround.ACMode.Break;
            ObjectIterator var12 = ((WorldRendererAccessor)this.mc.field_1769).getBlockBreakingInfos().values().iterator();

            while(var12.hasNext()) {
               class_3191 value = (class_3191)var12.next();
               if (value.method_13991().equals(p)) {
                  mine = true;
                  break;
               }
            }

            class_238 pBox = new class_238((double)(p.method_10263() - 1), (double)(p.method_10264() - 1), (double)(p.method_10260() - 1), (double)(p.method_10263() + 1), (double)(p.method_10264() + 1), (double)(p.method_10260() + 1));
            Predicate ePr = (entity) -> {
               return entity instanceof class_1511 && mine;
            };
            Iterator var9 = this.mc.field_1687.method_8333((class_1297)null, pBox, ePr).iterator();

            while(var9.hasNext()) {
               class_1297 crstal = (class_1297)var9.next();
               if ((double)this.mc.field_1724.method_5739(crstal) <= 2.6) {
                  if ((Boolean)this.antiCrystal.get() && (Boolean)this.antiCrystalBlockSwap.get()) {
                     InvUtils.swap(block.slot(), false);
                  }

                  if (this.swapDelay > 0) {
                     return;
                  }

                  Managers.ROTATION.start(crstal.method_5829(), (double)this.priority, RotationType.Attacking, (long)Objects.hash(new Object[]{this.name + "attacking"}));
                  this.mc.field_1724.field_3944.method_2883(class_2824.method_34206(crstal, this.mc.field_1724.method_5715()));
               } else {
                  this.mc.field_1724.field_3944.method_2883(class_2824.method_34206(crstal, this.mc.field_1724.method_5715()));
               }

               this.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
               if ((Boolean)this.antiCrystalBlock.get()) {
                  this.antiCrystalPos = crstal.method_24515();
               }
            }
         }
      }

      posPlaceBlocks.clear();
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      if ((Boolean)this.onlyOnGround.get() && !this.mc.field_1724.method_24828()) {
         this.toggle();
      } else if ((!(Boolean)this.disableOnJump.get() || !this.mc.field_1690.field_1903.method_1434() && !this.mc.field_1724.field_3913.field_3904) && (!(Boolean)this.disableOnYChange.get() || !(this.mc.field_1724.field_6036 < this.mc.field_1724.method_23318()))) {
         class_2338 blockPos = this.notSafeBlock(BlockInfo.getBlock(EntityInfo.getBlockPos((class_1657)this.mc.field_1724))) ? EntityInfo.getBlockPos((class_1657)this.mc.field_1724).method_10084() : EntityInfo.getBlockPos((class_1657)this.mc.field_1724);
         if (!this.mc.field_1724.method_6115() || !(Boolean)this.pauseOnUse.get()) {
            if (this.centerMode.get() == NewSurround.TpMode.Smooth && this.centerDelayLeft > 0) {
               this.pause();

               assert this.mc.field_1724 != null;

               double decrX = (double)class_3532.method_15357(this.mc.field_1724.method_23317()) + 0.5 - this.mc.field_1724.method_23317();
               double decrZ = (double)class_3532.method_15357(this.mc.field_1724.method_23321()) + 0.5 - this.mc.field_1724.method_23321();
               double sqrtPos = Math.sqrt(Math.pow(decrX, 2.0) + Math.pow(decrZ, 2.0));
               double div = Math.sqrt(0.5) / (double)(Integer)this.centerDelay.get();
               double x;
               double z;
               if (sqrtPos <= div) {
                  this.centerDelayLeft = 0;
                  x = (double)class_3532.method_15357(this.mc.field_1724.method_23317()) + 0.5;
                  z = (double)class_3532.method_15357(this.mc.field_1724.method_23321()) + 0.5;
                  this.mc.field_1724.method_5814(x, this.mc.field_1724.method_23318(), z);
                  return;
               }

               x = this.mc.field_1724.method_23317();
               z = this.mc.field_1724.method_23321();
               double incX = (double)class_3532.method_15357(this.mc.field_1724.method_23317()) + 0.5;
               double incZ = (double)class_3532.method_15357(this.mc.field_1724.method_23321()) + 0.5;
               double incResult = 0.0;
               double decrResult = 0.0;
               double x_ = this.mc.field_1724.method_23317();
               double z_ = this.mc.field_1724.method_23321();
               if (Math.sqrt(Math.pow(decrX, 2.0)) > Math.sqrt(Math.pow(decrZ, 2.0))) {
                  if (decrX > 0.0) {
                     incResult = 0.5 / (double)(Integer)this.centerDelay.get();
                  } else if (decrX < 0.0) {
                     incResult = -0.5 / (double)(Integer)this.centerDelay.get();
                  }

                  x_ = this.mc.field_1724.method_23317() + incResult;
                  z_ = this.z(x, z, incX, incZ, x_);
               } else if (Math.sqrt(Math.pow(decrX, 2.0)) < Math.sqrt(Math.pow(decrZ, 2.0))) {
                  if (decrZ > 0.0) {
                     decrResult = 0.5 / (double)(Integer)this.centerDelay.get();
                  } else if (decrZ < 0.0) {
                     decrResult = -0.5 / (double)(Integer)this.centerDelay.get();
                  }

                  z_ = this.mc.field_1724.method_23321() + decrResult;
                  x_ = this.x(x, z, incX, incZ, z_);
               } else if (Math.sqrt(Math.pow(decrX, 2.0)) == Math.sqrt(Math.pow(decrZ, 2.0))) {
                  if (decrX > 0.0) {
                     incResult = 0.5 / (double)(Integer)this.centerDelay.get();
                  } else if (decrX < 0.0) {
                     incResult = -0.5 / (double)(Integer)this.centerDelay.get();
                  }

                  x_ = this.mc.field_1724.method_23317() + incResult;
                  if (decrZ > 0.0) {
                     decrResult = 0.5 / (double)(Integer)this.centerDelay.get();
                  } else if (decrZ < 0.0) {
                     decrResult = -0.5 / (double)(Integer)this.centerDelay.get();
                  }

                  z_ = this.mc.field_1724.method_23321() + decrResult;
               }

               this.pause();
               this.mc.field_1724.method_5814(x_, this.mc.field_1724.method_23318(), z_);
            }

            this.ticks = 0;
            this.renderBlocks.forEach(RenderBlock::tick);
            this.renderBlocks.removeIf((renderBlock) -> {
               return renderBlock.ticks <= 0;
            });
            class_2350[] var27 = class_2350.values();
            int var4 = var27.length;

            int prevSlot;
            for(prevSlot = 0; prevSlot < var4; ++prevSlot) {
               class_2350 side = var27[prevSlot];
               if (side != class_2350.field_11036 && side != class_2350.field_11033 && (Boolean)this.forceSurround.get()) {
                  if ((Boolean)this.packet.get()) {
                     int prevSlot = this.mc.field_1724.method_31548().field_7545;
                     this.packetPlace(blockPos.method_10093(side));
                     InvUtils.swap(prevSlot, false);
                  } else if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     Managers.ROTATION.start(blockPos.method_10093(side), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
                  }

                  BlockUtils.place(blockPos.method_10093(side), InvUtils.findInHotbar((itemStack) -> {
                     return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
                  }), false, 20, (Boolean)this.swing.get(), false);
                  this.renderBlocks.add(((RenderBlock)this.renderBlockPool.get()).set(blockPos.method_10093(side)));
               }
            }

            posPlaceBlocks.clear();
            if (!(Boolean)this.onlyOnGround.get()) {
               posPlaceBlocks.add(blockPos.method_10074());
            }

            CardinalDirection[] var28 = CardinalDirection.values();
            var4 = var28.length;

            for(prevSlot = 0; prevSlot < var4; ++prevSlot) {
               CardinalDirection direction = var28[prevSlot];
               posPlaceBlocks.add(blockPos.method_10093(direction.toDirection()));
               if ((Boolean)this.doubleH.get() || ((Keybind)this.forceDouble.get()).isPressed() || ((Keybind)this.forceTrap.get()).isPressed()) {
                  posPlaceBlocks.add(blockPos.method_10093(direction.toDirection()).method_10084());
                  if (((Keybind)this.forceTrap.get()).isPressed()) {
                     posPlaceBlocks.add(blockPos.method_10086(2));
                  }
               }

               if (((Keybind)this.forceAntiCity.get()).isPressed()) {
                  posPlaceBlocks.add(blockPos.method_10079(direction.toDirection(), 2));
               }
            }

            if ((Boolean)this.helper.get()) {
               posPlaceBlocks.add(blockPos.method_10069(1, -1, 0));
               posPlaceBlocks.add(blockPos.method_10069(-1, -1, 0));
               posPlaceBlocks.add(blockPos.method_10069(0, -1, 1));
               posPlaceBlocks.add(blockPos.method_10069(0, -1, -1));
            }

            if (((Keybind)this.forceAntiCity.get()).isPressed()) {
               posPlaceBlocks.add(blockPos.method_10069(1, 0, 1));
               posPlaceBlocks.add(blockPos.method_10069(1, 0, -1));
               posPlaceBlocks.add(blockPos.method_10069(-1, 0, 1));
               posPlaceBlocks.add(blockPos.method_10069(-1, 0, -1));
            }

            if ((Boolean)this.fagMode.get()) {
               if (this.fagNorth) {
                  posPlaceBlocks.add(blockPos.method_10069(0, 0, -2));
                  posPlaceBlocks.add(blockPos.method_10069(0, 1, -1));
                  posPlaceBlocks.add(blockPos.method_10069(1, 0, -1));
                  posPlaceBlocks.add(blockPos.method_10069(-1, 0, -1));
                  posPlaceBlocks.add(blockPos.method_10069(0, -1, -1));
               }

               if (this.fagSouth) {
                  posPlaceBlocks.add(blockPos.method_10069(0, 0, 2));
                  posPlaceBlocks.add(blockPos.method_10069(0, 1, 1));
                  posPlaceBlocks.add(blockPos.method_10069(1, 0, 1));
                  posPlaceBlocks.add(blockPos.method_10069(-1, 0, 1));
                  posPlaceBlocks.add(blockPos.method_10069(0, -1, 1));
               }

               if (this.fagWest) {
                  posPlaceBlocks.add(blockPos.method_10069(-2, 0, 0));
                  posPlaceBlocks.add(blockPos.method_10069(-1, 1, 0));
                  posPlaceBlocks.add(blockPos.method_10069(-1, 0, 1));
                  posPlaceBlocks.add(blockPos.method_10069(-1, 0, -1));
                  posPlaceBlocks.add(blockPos.method_10069(-1, -1, 0));
               }

               if (this.fagEast) {
                  posPlaceBlocks.add(blockPos.method_10069(2, 0, 0));
                  posPlaceBlocks.add(blockPos.method_10069(1, 1, 0));
                  posPlaceBlocks.add(blockPos.method_10069(1, 0, 1));
                  posPlaceBlocks.add(blockPos.method_10069(1, 0, -1));
                  posPlaceBlocks.add(blockPos.method_10069(1, -1, 0));
               }
            }

            if ((Boolean)this.antiCrystalBlock.get() && this.antiCrystalPos != null) {
               posPlaceBlocks.add(this.antiCrystalPos);
            }

            Iterator var29 = posPlaceBlocks.iterator();

            while(var29.hasNext()) {
               class_2338 p = (class_2338)var29.next();
               if (BlockUtils.canPlace(p, false) && this.ticks <= (Integer)this.bpt.get()) {
                  if ((Boolean)this.anchor.get()) {
                     ((IVec3d)this.mc.field_1724.method_18798()).set(0.0, this.mc.field_1724.method_18798().field_1351, 0.0);
                  }

                  if ((Boolean)this.packet.get()) {
                     prevSlot = this.mc.field_1724.method_31548().field_7545;
                     this.packetPlace(p);
                     InvUtils.swap(prevSlot, false);
                  } else if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     Managers.ROTATION.start(p, (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
                  }

                  BlockUtils.place(p, InvUtils.findInHotbar((itemStack) -> {
                     return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
                  }), false, 20, (Boolean)this.swing.get(), false);
                  if ((Boolean)this.AGB.get()) {
                     this.mc.field_1724.field_3944.method_2883(new class_2846(class_2847.field_12973, p, class_2350.field_11036));
                  }

                  this.renderBlocks.add(((RenderBlock)this.renderBlockPool.get()).set(p));
                  ++this.ticks;
               }
            }

         }
      } else {
         this.toggle();
      }
   }

   private boolean notSafeBlock(class_2248 block) {
      if (block instanceof class_2281) {
         return true;
      } else {
         return block instanceof class_2336 ? true : block instanceof class_2482;
      }
   }

   private boolean blockFilter(class_2248 block) {
      return BlockInfo.isCombatBlock(block);
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         this.renderBlocks.sort(Comparator.comparingInt((o) -> {
            return -o.ticks;
         }));
         this.renderBlocks.forEach((renderBlock) -> {
            renderBlock.render(event, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (Color)this.sideColor2.get(), (Color)this.lineColor2.get(), (ShapeMode)this.shapeMode.get(), (Double)this.lineSize.get());
         });
      }

   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (event.packet instanceof class_2793 && (Boolean)this.disableOnTp.get()) {
         this.toggle();
      }

      if (event.packet instanceof class_2868) {
         this.swapDelay = (Integer)this.antiCrystalSwapDelay.get();
      }

   }

   private void packetPlace(class_2338 blockPos) {
      if (BlockUtils.canPlace(blockPos, false)) {
         int prevSlot = this.mc.field_1724.method_31548().field_7545;
         class_243 hitPos = class_243.method_24953(blockPos);
         class_2350 side = BlockUtils.getPlaceSide(blockPos);
         class_2338 neighbour;
         if (side == null) {
            side = class_2350.field_11036;
            neighbour = blockPos;
         } else {
            neighbour = blockPos.method_10093(side.method_10153());
            hitPos.method_1031((double)side.method_10148() * 0.5, (double)side.method_10164() * 0.5, (double)side.method_10165() * 0.5);
         }

         class_3965 bhr = new class_3965(hitPos, side, neighbour, false);
         FindItemResult iresult = InvUtils.findInHotbar((itemStack) -> {
            return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
         });
         if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
            Managers.ROTATION.start(class_238.method_29968(hitPos), 20.0, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
            InvUtils.swap(iresult.slot(), false);
            this.mc.method_1562().method_2883(new class_2885(class_1268.field_5808, bhr, 0));
            InvUtils.swap(prevSlot, false);
            InvUtils.swap(prevSlot, false);
         }

      }
   }

   private boolean inCenter() {
      if (this.mc.field_1724 == null) {
         return false;
      } else if (this.mc.field_1687 == null) {
         return false;
      } else if (this.mc.field_1761 == null) {
         return false;
      } else {
         int count = 0;
         if (this.mc.field_1724.method_24515().equals(class_2338.method_49637(this.mc.field_1724.method_23317() - ((double)this.mc.field_1724.method_17681() + 0.1) / 2.0, this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321() - ((double)this.mc.field_1724.method_17681() + 0.1) / 2.0))) {
            ++count;
         }

         if (this.mc.field_1724.method_24515().equals(class_2338.method_49637(this.mc.field_1724.method_23317() + ((double)this.mc.field_1724.method_17681() + 0.1) / 2.0, this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321() + ((double)this.mc.field_1724.method_17681() + 0.1) / 2.0))) {
            ++count;
         }

         if (this.mc.field_1724.method_24515().equals(class_2338.method_49637(this.mc.field_1724.method_23317() - ((double)this.mc.field_1724.method_17681() + 0.1) / 2.0, this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321() + ((double)this.mc.field_1724.method_17681() + 0.1) / 2.0))) {
            ++count;
         }

         if (this.mc.field_1724.method_24515().equals(class_2338.method_49637(this.mc.field_1724.method_23317() + ((double)this.mc.field_1724.method_17681() + 0.1) / 2.0, this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321() - ((double)this.mc.field_1724.method_17681() + 0.1) / 2.0))) {
            ++count;
         }

         return count == 4;
      }
   }

   private double z(double a, double b, double c, double d, double e) {
      return (e - a) * (d - b) / (c - a) + b;
   }

   private double x(double a, double b, double c, double d, double e) {
      return (e - b) * (c - a) / (d - b) + a;
   }

   private void pause() {
      if ((Boolean)this.stop.get()) {
         this.mc.field_1690.field_1903.method_23481(false);
         this.mc.field_1690.field_1867.method_23481(false);
         this.mc.field_1690.field_1894.method_23481(false);
         this.mc.field_1690.field_1881.method_23481(false);
         this.mc.field_1690.field_1913.method_23481(false);
         this.mc.field_1690.field_1849.method_23481(false);
      }

   }

   public static enum ACMode {
      Always,
      Break;

      // $FF: synthetic method
      private static ACMode[] $values() {
         return new ACMode[]{Always, Break};
      }
   }

   public static enum TpMode {
      Default,
      Smooth,
      None;

      // $FF: synthetic method
      private static TpMode[] $values() {
         return new TpMode[]{Default, Smooth, None};
      }
   }

   private class RenderBlock {
      public class_2338.class_2339 pos = new class_2338.class_2339();
      public int ticks;

      public RenderBlock set(class_2338 blockPos) {
         this.pos.method_10101(blockPos);
         this.ticks = 8;
         return this;
      }

      public void tick() {
         --this.ticks;
      }

      public void render(Render3DEvent event, Color sides, Color lines, Color sides2, Color lines2, ShapeMode shapeMode, double lineSize) {
         int preSideA = sides.a;
         int preLineA = lines.a;
         int preSideA2 = sides2.a;
         int preLineA2 = lines2.a;
         sides.a = (int)((double)sides.a * ((double)this.ticks / 8.0));
         lines.a = (int)((double)lines.a * ((double)this.ticks / 8.0));
         sides2.a = (int)((double)sides2.a * ((double)this.ticks / 8.0));
         lines2.a = (int)((double)lines2.a * ((double)this.ticks / 8.0));
         if (!(Boolean)NewSurround.this.newRender.get()) {
            event.renderer.box(this.pos, sides, lines, shapeMode, 0);
         } else {
            RenderUtils.thickRender(event, this.pos, shapeMode, lines, lines2, sides, sides2, lineSize);
         }

         sides.a = preSideA;
         lines.a = preLineA;
         sides2.a = preSideA2;
         lines2.a = preLineA2;
      }
   }
}
