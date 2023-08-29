package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.enums.SwingState;
import dev.lemonclient.addon.enums.SwingType;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.DamageInfo;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.player.PlaceData;
import dev.lemonclient.addon.utils.timers.TimerList;
import dev.lemonclient.addon.utils.world.hole.HoleUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
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
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2620;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_3341;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_2846.class_2847;

public class SelfProtect extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgProtection;
   private final SettingGroup sgSpeed;
   private final SettingGroup sgBlocks;
   private final SettingGroup sgAttack;
   private final SettingGroup sgDamage;
   private final SettingGroup sgRender;
   private final Setting oldVer;
   private final Setting pauseEat;
   private final Setting switchMode;
   private final Setting mineTime;
   private final Setting maxMineTime;
   private final Setting packet;
   private final Setting onlyHole;
   private final Setting surroundFloor;
   private final Setting surroundFloorBottom;
   private final Setting surroundSides;
   private final Setting surroundTop;
   private final Setting surroundBottom;
   private final Setting trapCev;
   private final Setting cev;
   private final Setting tntAura;
   private final Setting placeDelayMode;
   private final Setting placeDelayT;
   private final Setting placeDelayS;
   private final Setting places;
   private final Setting cooldown;
   private final Setting blocks;
   private final Setting attack;
   private final Setting attackSpeed;
   private final Setting always;
   private final Setting maxDmg;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting attackSwing;
   private final Setting attackHand;
   private final Setting shapeMode;
   private final Setting lineColor;
   private final Setting sideColor;
   private final List mining;
   private MineStart mineStart;
   private final List toProtect;
   private final List placePositions;
   private final List render;
   private final TimerList placed;
   private int blocksLeft;
   private int placesLeft;
   private FindItemResult result;
   private boolean switched;
   private boolean tntAured;
   private boolean ceved;
   private class_1268 hand;
   private int tickTimer;
   private double timer;
   private long lastTime;
   private long lastAttack;

   public SelfProtect() {
      super(LemonClient.Combat, "Self Protect", "Covers you if any enemy tries to attack you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgProtection = this.settings.createGroup("Protection");
      this.sgSpeed = this.settings.createGroup("Speed");
      this.sgBlocks = this.settings.createGroup("Blocks");
      this.sgAttack = this.settings.createGroup("Attack");
      this.sgDamage = this.settings.createGroup("Damage");
      this.sgRender = this.settings.createGroup("Render");
      this.oldVer = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("1.12 Crystals")).description("Uses 1.12.2 crystal mechanics.")).defaultValue(false)).build());
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause On Eat")).description("Pause while eating.")).defaultValue(false)).build());
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Method of switching. Silent is the most reliable.")).defaultValue(SelfProtect.SwitchMode.Silent)).build());
      this.mineTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Mine Time")).description("How long do we let enemies mine our surround for before protecting it.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.maxMineTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Max Mine Time")).description("Ignores mining after x seconds.")).defaultValue(5.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.packet = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Packet")).description(".")).defaultValue(false)).build());
      this.onlyHole = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Only Hole")).description("Only protects when you are in a hole.")).defaultValue(true)).build());
      this.surroundFloor = this.sgProtection.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Surround Floor")).description("Places blocks around to surround floor blocks.")).defaultValue(true)).build());
      this.surroundFloorBottom = this.sgProtection.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Surround Floor Bottom")).description("Places blocks under surround floor blocks.")).defaultValue(true)).build());
      this.surroundSides = this.sgProtection.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Surround Sides")).description("Places blocks next to surround blocks.")).defaultValue(true)).build());
      this.surroundTop = this.sgProtection.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Surround Side Top")).description("Places a block on top of surround.")).defaultValue(true)).build());
      this.surroundBottom = this.sgProtection.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Surround Side Bottom")).description("Places a block on bottom of surround.")).defaultValue(true)).build());
      this.trapCev = this.sgProtection.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Trap Cev")).description("Places on top of trap side block.")).defaultValue(true)).build());
      this.cev = this.sgProtection.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Cev")).description("Places on top of trap top blocks.")).defaultValue(true)).build());
      this.tntAura = this.sgProtection.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("TNT Aura")).description("Prevents TNT Aura.")).defaultValue(true)).build());
      this.placeDelayMode = this.sgSpeed.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Delay Mode")).description(".")).defaultValue(SurroundPlus.PlaceDelayMode.Ticks)).build());
      this.placeDelayT = this.sgSpeed.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Place Tick Delay")).description("Tick delay between places.")).defaultValue(1)).min(1).sliderRange(0, 20).visible(() -> {
         return this.placeDelayMode.get() == SurroundPlus.PlaceDelayMode.Ticks;
      })).build());
      this.placeDelayS = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Delay")).description("Delay between places.")).defaultValue(0.1).min(0.0).sliderRange(0.0, 1.0).visible(() -> {
         return this.placeDelayMode.get() == SurroundPlus.PlaceDelayMode.Seconds;
      })).build());
      this.places = this.sgSpeed.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Places")).description("How many blocks to place each time.")).defaultValue(1)).min(1).sliderRange(0, 20).build());
      this.cooldown = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Cooldown")).description("Waits x seconds before trying to place at the same position if there is only 1 missing block.")).defaultValue(0.5).min(0.0).sliderRange(0.0, 1.0).build());
      this.blocks = this.sgBlocks.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("Blocks")).description("Blocks to use.")).defaultValue(new class_2248[]{class_2246.field_10540}).build());
      this.attack = this.sgAttack.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Attack")).description("Attacks crystals blocking surround.")).defaultValue(true)).build());
      this.attackSpeed = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Speed")).description("How many times to attack every second.")).defaultValue(4.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.always = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Always")).description("Doesn't check for damages.")).defaultValue(true)).build());
      this.maxDmg = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Damage")).description("Doesn't place if you would take less damage than this.")).defaultValue(6.0).min(0.0).sliderRange(0.0, 20.0).visible(() -> {
         return !(Boolean)this.always.get();
      })).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Place Swing")).description("Renders swing animation when placing a block.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.attackSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Attack Swing")).description("Renders swing animation when placing a crystal.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Attack Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.attackSwing;
      Objects.requireNonNull(var10003);
      this.attackHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts of boxes should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.mining = new ArrayList();
      this.mineStart = null;
      this.toProtect = new ArrayList();
      this.placePositions = new ArrayList();
      this.render = new ArrayList();
      this.placed = new TimerList();
      this.blocksLeft = 0;
      this.placesLeft = 0;
      this.result = null;
      this.switched = false;
      this.tntAured = false;
      this.ceved = false;
      this.hand = null;
      this.tickTimer = 0;
      this.timer = 0.0;
      this.lastTime = 0L;
      this.lastAttack = 0L;
   }

   @EventHandler(
      priority = 200
   )
   private void onBlock(BlockUpdateEvent event) {
      if (event.oldState.method_26204() != event.newState.method_26204() && !LemonUtils.replaceable(event.pos) && this.placePositions.contains(event.pos)) {
         this.render.add(new Render(event.pos, System.currentTimeMillis()));
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onTickPre(TickEvent.Pre event) {
      ++this.tickTimer;
   }

   @EventHandler
   private void onSlowTickPre(TickEvent.Pre event) {
      class_2338 top = this.mc.field_1724.method_24515().method_10086(2);
      if ((Boolean)this.tntAura.get()) {
         if (this.mc.field_1687.method_8320(top).method_26204().equals(class_2246.field_10375)) {
            if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
               Managers.ROTATION.start(top, (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
               this.mine(top);
            } else {
               this.mine(top);
            }

            this.tntAured = true;
         } else if (this.tntAured) {
            if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
               Managers.ROTATION.start(top, 50.0, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
            }

            BlockUtils.place(top, InvUtils.findInHotbar(new class_1792[]{class_1802.field_8281}), 50, true);
            this.tntAured = false;
         }
      }

      if (this.mc.field_1687.method_8320(top).method_26204() == class_2246.field_10540) {
         Iterator iterator = this.mc.field_1687.method_18112().iterator();

         while(true) {
            while(iterator.hasNext()) {
               class_1297 crystal = (class_1297)iterator.next();
               if (crystal instanceof class_1511 && crystal.method_24515().equals(top.method_10084())) {
                  this.mc.field_1724.field_3944.method_2883(class_2824.method_34206(crystal, this.mc.field_1724.method_5715()));
                  this.ceved = true;
               } else if (this.ceved) {
                  if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     Managers.ROTATION.start(top.method_10084(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
                  }

                  BlockUtils.place(top.method_10084(), InvUtils.findInHotbar(new class_1792[]{class_1802.field_8281}), 50, true);
                  this.ceved = false;
               }
            }

            return;
         }
      }
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      this.placed.update();
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.timer += (double)(System.currentTimeMillis() - this.lastTime) / 1000.0;
         this.lastTime = System.currentTimeMillis();
         this.updateBlocks();
         if (this.mineStart != null && this.contains()) {
            this.mineStart = null;
         }

         this.mining.removeIf((m) -> {
            return (double)System.currentTimeMillis() > (double)m.time + (Double)this.maxMineTime.get() * 1000.0 || this.mineStart != null && m.id == this.mineStart.id || !LemonUtils.solid2(m.pos);
         });
         if (this.mineStart != null) {
            this.mining.add(this.mineStart);
            this.mineStart = null;
         }

         this.updatePlacing();
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
      priority = 200
   )
   private void onReceive(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2620 p) {
         this.mineStart = new MineStart(p.method_11277(), p.method_11280(), System.currentTimeMillis());
      }

   }

   private void updatePlacing() {
      if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
         this.updateResult();
         this.updatePlaces();
         this.blocksLeft = Math.min(this.placesLeft, this.result.count());
         this.hand = this.getHand();
         this.switched = false;
         this.placePositions.clear();
         this.toProtect.stream().filter(this::shouldProtect).forEach(this::addPlacePositions);
         this.updateAttack();
         this.placePositions.stream().filter((pos) -> {
            return !EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(pos)), (entity) -> {
               return entity instanceof class_1511 && System.currentTimeMillis() - this.lastAttack > 100L;
            });
         }).forEach(this::place);
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
   }

   private void addPlacePositions(ProtectBlock p) {
      switch (p.type) {
         case 0:
         case 1:
            class_2350[] var8 = class_2350.values();
            int var9 = var8.length;

            for(int var4 = 0; var4 < var9; ++var4) {
               class_2350 dir = var8[var4];
               if (p.type == 1) {
                  if (!(Boolean)this.surroundSides.get() && dir.method_10166().method_10179() || !(Boolean)this.surroundTop.get() && dir == class_2350.field_11036 || !(Boolean)this.surroundBottom.get() && dir == class_2350.field_11033) {
                     continue;
                  }
               } else if (dir == class_2350.field_11036 || !(Boolean)this.surroundFloor.get() && dir.method_10166().method_10179() || !(Boolean)this.surroundFloorBottom.get() && dir == class_2350.field_11033) {
                  continue;
               }

               class_2338 pos = p.pos.method_10093(dir);
               if (LemonUtils.replaceable(pos)) {
                  PlaceData data = SettingUtils.getPlaceData(pos);
                  if (data.valid() && SettingUtils.inPlaceRange(data.pos()) && !EntityUtils.intersectsWithEntity(new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1)), this::validForIntersects)) {
                     this.placePositions.add(pos);
                  }
               }
            }

            return;
         case 2:
         case 3:
            class_2338 pos = p.pos.method_10084();
            if (!LemonUtils.replaceable(pos)) {
               return;
            }

            PlaceData data = SettingUtils.getPlaceData(pos);
            if (!data.valid()) {
               return;
            }

            if (!SettingUtils.inPlaceRange(data.pos())) {
               return;
            }

            if (EntityUtils.intersectsWithEntity(new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1)), this::validForIntersects)) {
               return;
            }

            this.placePositions.add(pos);
      }

   }

   private void updateAttack() {
      if ((Boolean)this.attack.get()) {
         if (!((double)(System.currentTimeMillis() - this.lastAttack) < 1000.0 / (Double)this.attackSpeed.get())) {
            class_1297 blocking = this.getBlocking();
            if (blocking != null) {
               if (!SettingUtils.shouldRotate(RotationType.Attacking) || Managers.ROTATION.start(blocking.method_5829(), (double)this.priority - 0.1, RotationType.Attacking, (long)Objects.hash(new Object[]{this.name + "attacking"}))) {
                  SettingUtils.swing(SwingState.Pre, SwingType.Attacking, class_1268.field_5808);
                  this.sendPacket(class_2824.method_34206(blocking, this.mc.field_1724.method_5715()));
                  SettingUtils.swing(SwingState.Post, SwingType.Attacking, class_1268.field_5808);
                  if (SettingUtils.shouldRotate(RotationType.Attacking)) {
                     Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "attacking"}));
                  }

                  if ((Boolean)this.attackSwing.get()) {
                     this.clientSwing((SwingHand)this.attackHand.get(), class_1268.field_5808);
                  }

                  this.lastAttack = System.currentTimeMillis();
               }
            }
         }
      }
   }

   private class_1297 getBlocking() {
      class_1297 crystal = null;
      double lowest = 1000.0;
      Iterator var4 = this.mc.field_1687.method_18112().iterator();

      while(true) {
         class_1297 entity;
         do {
            do {
               do {
                  if (!var4.hasNext()) {
                     return crystal;
                  }

                  entity = (class_1297)var4.next();
               } while(!(entity instanceof class_1511));
            } while(this.mc.field_1724.method_5739(entity) > 5.0F);
         } while(!SettingUtils.inAttackRange(entity.method_5829()));

         Iterator var6 = this.placePositions.iterator();

         while(var6.hasNext()) {
            class_2338 pos = (class_2338)var6.next();
            if (class_238.method_19316(new class_3341(pos)).method_994(entity.method_5829())) {
               double dmg = DamageInfo.crystal(this.mc.field_1724, this.mc.field_1724.method_5829(), entity.method_19538(), (class_2338)null, false);
               if (dmg < lowest) {
                  crystal = entity;
                  lowest = dmg;
               }
            }
         }
      }
   }

   private class_1268 getHand() {
      if (this.valid(Managers.HOLDING.getStack())) {
         return class_1268.field_5808;
      } else {
         return this.valid(this.mc.field_1724.method_6079()) ? class_1268.field_5810 : null;
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

   private void updatePlaces() {
      switch ((SurroundPlus.PlaceDelayMode)this.placeDelayMode.get()) {
         case Ticks:
            if (this.placesLeft >= (Integer)this.places.get() || this.tickTimer >= (Integer)this.placeDelayT.get()) {
               this.placesLeft = (Integer)this.places.get();
               this.tickTimer = 0;
            }
            break;
         case Seconds:
            if (this.placesLeft >= (Integer)this.places.get() || this.timer >= (Double)this.placeDelayS.get()) {
               this.placesLeft = (Integer)this.places.get();
               this.timer = 0.0;
            }
      }

   }

   private void place(class_2338 pos) {
      if (this.blocksLeft > 0) {
         TimerList var10001 = this.placed;
         Objects.requireNonNull(var10001);
         PlaceData data = SettingUtils.getPlaceDataOR(pos, var10001::contains);
         if (data != null && data.valid()) {
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
                  this.placeBlock(this.hand == null ? class_1268.field_5808 : this.hand, data.pos().method_46558(), data.dir(), data.pos());
                  if ((Boolean)this.placeSwing.get()) {
                     this.clientSwing((SwingHand)this.placeHand.get(), this.hand == null ? class_1268.field_5808 : this.hand);
                  }

                  if (!(Boolean)this.packet.get()) {
                     this.setBlock(pos);
                  }

                  this.placed.add(pos, (Double)this.cooldown.get());
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

   private void setBlock(class_2338 pos) {
      class_1792 item = this.mc.field_1724.method_31548().method_5438(this.result.slot()).method_7909();
      if (item instanceof class_1747 block) {
         this.mc.field_1687.method_8501(pos, block.method_7711().method_9564());
         this.mc.field_1687.method_8486((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), class_3417.field_14574, class_3419.field_15245, 1.0F, 1.0F, false);
      }
   }

   private boolean shouldProtect(ProtectBlock p) {
      class_2338 pos = p.pos;
      switch (p.type) {
         case 1:
            if (!LemonUtils.solid2(pos) || this.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_9987) {
               return false;
            }
            break;
         case 2:
         case 3:
            if (this.mc.field_1687.method_8320(p.pos).method_26204() != class_2246.field_10540) {
               return false;
            }

            if (!(this.mc.field_1687.method_8320(p.pos.method_10084()).method_26204() instanceof class_2189)) {
               return false;
            }

            if ((Boolean)this.oldVer.get() && !(this.mc.field_1687.method_8320(p.pos.method_10086(2)).method_26204() instanceof class_2189)) {
               return false;
            }
      }

      if (!this.containsPos(pos)) {
         return false;
      } else if (!this.damageCheck(pos, p.type)) {
         return false;
      } else {
         return true;
      }
   }

   private void mine(class_2338 blockPos) {
      this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, blockPos, class_2350.field_11036));
      this.mc.field_1724.method_6104(class_1268.field_5808);
      this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, class_2350.field_11036));
   }

   private void updateBlocks() {
      this.toProtect.clear();
      if (!(Boolean)this.onlyHole.get() || HoleUtils.inHole(this.mc.field_1724)) {
         class_2338 e = class_2338.method_49637(this.mc.field_1724.method_23317(), this.mc.field_1724.method_5829().field_1325, this.mc.field_1724.method_23321());
         class_2338 pos = new class_2338(this.mc.field_1724.method_31477(), (int)Math.round(this.mc.field_1724.method_23318()), this.mc.field_1724.method_31479());
         int[] size = new int[4];
         double xOffset = this.mc.field_1724.method_23317() - (double)this.mc.field_1724.method_31477();
         double zOffset = this.mc.field_1724.method_23321() - (double)this.mc.field_1724.method_31479();
         if (xOffset < 0.3) {
            size[0] = -1;
         }

         if (xOffset > 0.7) {
            size[1] = 1;
         }

         if (zOffset < 0.3) {
            size[2] = -1;
         }

         if (zOffset > 0.7) {
            size[3] = 1;
         }

         this.updateSurround(pos, size);
         if ((Boolean)this.trapCev.get()) {
            this.updateEyes(e, size);
         }

         if ((Boolean)this.cev.get()) {
            this.updateTop(this.mc.field_1724.method_24515().method_10086(2));
         }

      }
   }

   private void updateTop(class_2338 pos) {
      this.toProtect.add(new ProtectBlock(pos, 3));
   }

   private void updateEyes(class_2338 pos, int[] size) {
      for(int x = size[0] - 1; x <= size[1] + 1; ++x) {
         for(int z = size[2] - 1; z <= size[3] + 1; ++z) {
            if (x != size[0] - 1 && x != size[1] + 1 || z != size[2] - 1 && z != size[3] + 1) {
               this.toProtect.add(new ProtectBlock(pos.method_10069(x, 0, z), 2));
            }
         }
      }

   }

   private void updateSurround(class_2338 pos, int[] size) {
      for(int y = -1; y <= 0; ++y) {
         for(int x = size[0] - 1; x <= size[1] + 1; ++x) {
            for(int z = size[2] - 1; z <= size[3] + 1; ++z) {
               boolean bx = x == size[0] - 1 || x == size[1] + 1;
               boolean by = y == -1;
               boolean bz = z == size[2] - 1 || z == size[3] + 1;
               if (by) {
                  if (!bx && !bz) {
                     this.toProtect.add(new ProtectBlock(pos.method_10069(x, y, z), 0));
                  }
               } else if (!bx || !bz) {
                  this.toProtect.add(new ProtectBlock(pos.method_10069(x, y, z), 1));
               }
            }
         }
      }

   }

   private boolean validForIntersects(class_1297 entity) {
      return !(entity instanceof class_1542) && !(entity instanceof class_1511);
   }

   private boolean damageCheck(class_2338 blockPos, int type) {
      if ((Boolean)this.always.get()) {
         return true;
      } else {
         switch (type) {
            case 1:
               for(int x = -2; x <= 2; ++x) {
                  for(int y = -2; y <= 2; ++y) {
                     for(int z = -2; z <= 2; ++z) {
                        class_2338 pos = blockPos.method_10069(x, y, z);
                        if (this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2189 && (!(Boolean)this.oldVer.get() || this.mc.field_1687.method_8320(pos.method_10084()).method_26204() instanceof class_2189)) {
                           double self = DamageInfo.crystal(this.mc.field_1724, this.mc.field_1724.method_5829(), this.feet(pos), blockPos, true);
                           if (self >= (Double)this.maxDmg.get()) {
                              return true;
                           }
                        }
                     }
                  }
               }

               return false;
            case 2:
            case 3:
               if (!(this.mc.field_1687.method_8320(blockPos).method_26204() instanceof class_2189)) {
                  return false;
               }

               if ((Boolean)this.oldVer.get() && !(this.mc.field_1687.method_8320(blockPos.method_10084()).method_26204() instanceof class_2189)) {
                  return false;
               }

               double self = DamageInfo.crystal(this.mc.field_1724, this.mc.field_1724.method_5829(), this.feet(blockPos.method_10084()), blockPos, true);
               if (self >= (Double)this.maxDmg.get()) {
                  return true;
               }
         }

         return false;
      }
   }

   private class_243 feet(class_2338 pos) {
      return new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264(), (double)pos.method_10260() + 0.5);
   }

   private boolean contains() {
      Iterator var1 = this.mining.iterator();

      MineStart m;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         m = (MineStart)var1.next();
      } while(m.id != this.mineStart.id || !m.pos.equals(this.mineStart.pos));

      return true;
   }

   private boolean containsPos(class_2338 pos) {
      Iterator var2 = this.mining.iterator();

      MineStart m;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         m = (MineStart)var2.next();
      } while(!((double)System.currentTimeMillis() > (double)m.time + (Double)this.mineTime.get() * 1000.0) || !m.pos.equals(pos));

      return true;
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

   private static record MineStart(class_2338 pos, int id, long time) {
      private MineStart(class_2338 pos, int id, long time) {
         this.pos = pos;
         this.id = id;
         this.time = time;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public int id() {
         return this.id;
      }

      public long time() {
         return this.time;
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

   private static record ProtectBlock(class_2338 pos, int type) {
      private ProtectBlock(class_2338 pos, int type) {
         this.pos = pos;
         this.type = type;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public int type() {
         return this.type;
      }
   }
}
