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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_3341;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_2350.class_2353;

public class SurroundPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgToggle;
   private final SettingGroup sgSpeed;
   private final SettingGroup sgBlocks;
   private final SettingGroup sgAttack;
   private final SettingGroup sgRender;
   private final Setting center;
   private final Setting smartCenter;
   private final Setting phaseCenter;
   private final Setting pauseEat;
   private final Setting packet;
   private final Setting switchMode;
   private final Setting extend;
   private final Setting toggleMove;
   private final Setting toggleVertical;
   private final Setting placeDelayMode;
   private final Setting placeDelayT;
   private final Setting placeDelayS;
   private final Setting places;
   private final Setting cooldown;
   private final Setting singleCooldown;
   private final Setting blocks;
   private final Setting supportBlocks;
   private final Setting attack;
   private final Setting attackSpeed;
   private final Setting alwaysAttack;
   private final Setting antiCev;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting attackSwing;
   private final Setting attackHand;
   private final Setting shapeMode;
   private final Setting lineColor;
   private final Setting sideColor;
   private final Setting supportShapeMode;
   private final Setting supportLineColor;
   private final Setting supportSideColor;
   private int tickTimer;
   private double timer;
   private final List insideBlocks;
   public final List surroundBlocks;
   private final List supportPositions;
   private final List valids;
   private final TimerList placed;
   private final List render;
   private boolean support;
   private class_1268 hand;
   private int blocksLeft;
   private int placesLeft;
   private FindItemResult result;
   private boolean switched;
   private class_2338 lastPos;
   private boolean centered;
   private long lastAttack;
   private class_2338 currentPos;
   public static boolean placing = false;

   public SurroundPlus() {
      super(LemonClient.Combat, "Surround Plus", "Places blocks around your legs to protect from explosions.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgToggle = this.settings.createGroup("Toggle");
      this.sgSpeed = this.settings.createGroup("Speed");
      this.sgBlocks = this.settings.createGroup("Blocks");
      this.sgAttack = this.settings.createGroup("Attack");
      this.sgRender = this.settings.createGroup("Render");
      this.center = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Center")).description("Moves to block center before surrounding.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Smart Center")).description("Only moves until whole hitbox is inside target block.")).defaultValue(true);
      Setting var10003 = this.center;
      Objects.requireNonNull(var10003);
      this.smartCenter = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Phase Friendly")).description("Doesn't center if clipped inside a block.")).defaultValue(true);
      var10003 = this.center;
      Objects.requireNonNull(var10003);
      this.phaseCenter = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause On Eat")).description("Pause while eating.")).defaultValue(false)).build());
      this.packet = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Packet")).description(".")).defaultValue(false)).build());
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Method of switching. Silent is the most reliable but delays crystals on some servers.")).defaultValue(SurroundPlus.SwitchMode.Silent)).build());
      this.extend = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Extend")).description(".")).defaultValue(true)).build());
      this.toggleMove = this.sgToggle.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Toggle Move")).description(".")).defaultValue(false)).build());
      this.toggleVertical = this.sgToggle.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Toggle Vertical")).description(".")).defaultValue(SurroundPlus.VerticalToggleMode.Up)).build());
      this.placeDelayMode = this.sgSpeed.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Delay Mode")).description(".")).defaultValue(SurroundPlus.PlaceDelayMode.Ticks)).build());
      this.placeDelayT = this.sgSpeed.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Place Tick Delay")).description("Tick delay between places.")).defaultValue(1)).min(1).sliderRange(0, 20).visible(() -> {
         return this.placeDelayMode.get() == SurroundPlus.PlaceDelayMode.Ticks;
      })).build());
      this.placeDelayS = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Delay")).description("Delay between places.")).defaultValue(0.1).min(0.0).sliderRange(0.0, 1.0).visible(() -> {
         return this.placeDelayMode.get() == SurroundPlus.PlaceDelayMode.Seconds;
      })).build());
      this.places = this.sgSpeed.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Places")).description("How many blocks to place each time.")).defaultValue(1)).min(1).sliderRange(0, 20).build());
      this.cooldown = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Multi Cooldown")).description("Waits x seconds before trying to place at the same position if there is more than 1 missing block.")).defaultValue(0.3).min(0.0).sliderRange(0.0, 1.0).build());
      this.singleCooldown = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Single Cooldown")).description("Waits x seconds before trying to place at the same position if there is only 1 missing block.")).defaultValue(0.02).min(0.0).sliderRange(0.0, 1.0).build());
      this.blocks = this.sgBlocks.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("Blocks")).description("Blocks to use.")).defaultValue(new class_2248[]{class_2246.field_10540}).build());
      this.supportBlocks = this.sgBlocks.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("Support Blocks")).description("Blocks to use for support.")).defaultValue(new class_2248[]{class_2246.field_10540}).build());
      this.attack = this.sgAttack.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Attack")).description("Attacks crystals blocking surround.")).defaultValue(false)).build());
      this.attackSpeed = this.sgAttack.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Speed")).description("How many times to attack every second.")).defaultValue(4.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.alwaysAttack = this.sgAttack.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Always Attack")).description("Attacks crystals even when surround block isn't broken.")).defaultValue(false)).build());
      this.antiCev = this.sgAttack.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Anti CEV")).description("Attacks crystals placed on surround blocks.")).defaultValue(false)).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Place Swing")).description("Renders swing animation when placing a block.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var1 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      this.attackSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Attack Swing")).description("Renders swing animation when placing a crystal.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var1 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Attack Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.attackSwing;
      Objects.requireNonNull(var10003);
      this.attackHand = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts of boxes should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.supportShapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Support Shape Mode")).description("Which parts of boxes should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.supportLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Support Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 150)).build());
      this.supportSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Support Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.tickTimer = 0;
      this.timer = 0.0;
      this.insideBlocks = new ArrayList();
      this.surroundBlocks = new ArrayList();
      this.supportPositions = new ArrayList();
      this.valids = new ArrayList();
      this.placed = new TimerList();
      this.render = new ArrayList();
      this.support = false;
      this.hand = null;
      this.blocksLeft = 0;
      this.placesLeft = 0;
      this.result = null;
      this.switched = false;
      this.lastPos = null;
      this.centered = false;
      this.lastAttack = 0L;
      this.currentPos = null;
   }

   public void onActivate() {
      this.tickTimer = (Integer)this.placeDelayT.get();
      this.timer = (Double)this.placeDelayS.get();
      this.placesLeft = (Integer)this.places.get();
      this.centered = false;
      this.lastPos = this.getPos();
      this.currentPos = this.getPos();
   }

   @EventHandler(
      priority = 200
   )
   private void onBlock(BlockUpdateEvent event) {
      if (event.oldState.method_26204() != event.newState.method_26204() && !LemonUtils.replaceable(event.pos) && this.surroundBlocks.contains(event.pos)) {
         this.render.add(new Render(event.pos, System.currentTimeMillis()));
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onTick(TickEvent.Pre event) {
      ++this.tickTimer;
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      this.placed.update();
      placing = false;
      this.timer += event.frameTime;
      this.lastPos = this.currentPos;
      this.currentPos = this.getPos();
      this.setBB();
      if (!this.checkToggle()) {
         this.updateBlocks();
         this.updateSupport();
         this.surroundBlocks.stream().filter(LemonUtils::replaceable).forEach((block) -> {
            event.renderer.box(block, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         });
         this.supportPositions.forEach((block) -> {
            event.renderer.box(block, (Color)this.supportSideColor.get(), (Color)this.supportLineColor.get(), (ShapeMode)this.supportShapeMode.get(), 0);
         });
         this.render.removeIf((r) -> {
            return System.currentTimeMillis() - r.time > 1000L;
         });
         this.render.forEach((r) -> {
            double progress = 1.0 - (double)Math.min(System.currentTimeMillis() - r.time, 500L) / 500.0;
            event.renderer.box(r.pos, new Color(((SettingColor)this.sideColor.get()).r, ((SettingColor)this.sideColor.get()).g, ((SettingColor)this.sideColor.get()).b, (int)Math.round((double)((SettingColor)this.sideColor.get()).a * progress)), new Color(((SettingColor)this.lineColor.get()).r, ((SettingColor)this.lineColor.get()).g, ((SettingColor)this.lineColor.get()).b, (int)Math.round((double)((SettingColor)this.lineColor.get()).a * progress)), (ShapeMode)this.shapeMode.get(), 0);
         });
         if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
            this.placeBlocks();
         }
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

         Iterator var6;
         class_2338 pos;
         double dmg;
         if ((Boolean)this.antiCev.get()) {
            var6 = this.surroundBlocks.iterator();

            while(var6.hasNext()) {
               pos = (class_2338)var6.next();
               if (entity.method_24515().equals(pos.method_10084())) {
                  dmg = Math.max(10.0, DamageInfo.crystal(this.mc.field_1724, this.mc.field_1724.method_5829(), entity.method_19538(), (class_2338)null, false));
                  if (dmg < lowest) {
                     lowest = dmg;
                     crystal = entity;
                  }
               }
            }
         }

         var6 = ((Boolean)this.alwaysAttack.get() ? this.surroundBlocks : this.valids).iterator();

         while(var6.hasNext()) {
            pos = (class_2338)var6.next();
            if (class_238.method_19316(new class_3341(pos)).method_994(entity.method_5829())) {
               dmg = DamageInfo.crystal(this.mc.field_1724, this.mc.field_1724.method_5829(), entity.method_19538(), (class_2338)null, false);
               if (dmg < lowest) {
                  crystal = entity;
                  lowest = dmg;
               }
            }
         }
      }
   }

   private void setBB() {
      if (!this.centered && (Boolean)this.center.get() && this.mc.field_1724.method_24828() && (!(Boolean)this.phaseCenter.get() || !LemonUtils.inside(this.mc.field_1724, this.mc.field_1724.method_5829().method_1002(0.01, 0.01, 0.01)))) {
         double targetX;
         double targetZ;
         if ((Boolean)this.smartCenter.get()) {
            targetX = class_3532.method_15350(this.mc.field_1724.method_23317(), (double)this.currentPos.method_10263() + 0.31, (double)this.currentPos.method_10263() + 0.69);
            targetZ = class_3532.method_15350(this.mc.field_1724.method_23321(), (double)this.currentPos.method_10260() + 0.31, (double)this.currentPos.method_10260() + 0.69);
         } else {
            targetX = (double)this.currentPos.method_10263() + 0.5;
            targetZ = (double)this.currentPos.method_10260() + 0.5;
         }

         double dist = (new class_243(targetX, 0.0, targetZ)).method_1022(new class_243(this.mc.field_1724.method_23317(), 0.0, this.mc.field_1724.method_23321()));
         if (dist < 0.2873) {
            this.sendPacket(new class_2828.class_2829(targetX, this.mc.field_1724.method_23318(), targetZ, Managers.ON_GROUND.isOnGround()));
         }

         double x = this.mc.field_1724.method_23317();
         double z = this.mc.field_1724.method_23321();

         for(int i = 0; (double)i < Math.ceil(dist / 0.2873); ++i) {
            double yaw = Rotations.getYaw(new class_243(targetX, 0.0, targetZ)) + 90.0;
            x += Math.cos(Math.toRadians(yaw)) * 0.2873;
            z += Math.sin(Math.toRadians(yaw)) * 0.2873;
            this.sendPacket(new class_2828.class_2829(x, this.mc.field_1724.method_23318(), z, Managers.ON_GROUND.isOnGround()));
         }

         this.mc.field_1724.method_23327(targetX, this.mc.field_1724.method_23318(), targetZ);
         this.mc.field_1724.method_5857(new class_238(targetX - 0.3, this.mc.field_1724.method_23318(), targetZ - 0.3, targetX + 0.3, this.mc.field_1724.method_23318() + (this.mc.field_1724.method_5829().field_1325 - this.mc.field_1724.method_5829().field_1322), targetZ + 0.3));
         this.centered = true;
      }

   }

   private boolean checkToggle() {
      if (this.lastPos != null) {
         if ((Boolean)this.toggleMove.get() && (this.currentPos.method_10263() != this.lastPos.method_10263() || this.currentPos.method_10260() != this.lastPos.method_10260())) {
            this.toggle();
            this.sendToggledMsg("moved horizontally");
            return true;
         }

         if ((this.toggleVertical.get() == SurroundPlus.VerticalToggleMode.Up || this.toggleVertical.get() == SurroundPlus.VerticalToggleMode.Any) && this.currentPos.method_10264() > this.lastPos.method_10264()) {
            this.toggle();
            this.sendToggledMsg("moved up");
            return true;
         }

         if ((this.toggleVertical.get() == SurroundPlus.VerticalToggleMode.Down || this.toggleVertical.get() == SurroundPlus.VerticalToggleMode.Any) && this.currentPos.method_10264() < this.lastPos.method_10264()) {
            this.toggle();
            this.sendToggledMsg("moved down");
            return true;
         }
      }

      return false;
   }

   private void placeBlocks() {
      List positions = new ArrayList();
      this.setSupport();
      if (this.support) {
         positions.addAll(this.supportPositions);
      } else {
         positions.addAll(this.surroundBlocks);
      }

      this.valids.clear();
      this.valids.addAll(positions.stream().filter(this::validBlock).toList());
      this.updateAttack();
      this.updateResult();
      this.updatePlaces();
      this.blocksLeft = Math.min(this.placesLeft, this.result.count());
      this.hand = this.getHand();
      this.switched = false;
      this.valids.stream().filter((pos) -> {
         return !EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(pos)), this::validEntity);
      }).sorted(Comparator.comparingDouble(Rotations::getYaw)).forEach(this::place);
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

   private void updatePlaces() {
      switch ((PlaceDelayMode)this.placeDelayMode.get()) {
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

   private boolean validBlock(class_2338 pos) {
      if (!LemonUtils.replaceable(pos)) {
         return false;
      } else {
         TimerList var10001 = this.placed;
         Objects.requireNonNull(var10001);
         PlaceData data = SettingUtils.getPlaceDataOR(pos, var10001::contains);
         if (!data.valid()) {
            return false;
         } else if (!SettingUtils.inPlaceRange(data.pos())) {
            return false;
         } else {
            return !this.placed.contains(pos);
         }
      }
   }

   private void place(class_2338 pos) {
      if (this.blocksLeft > 0) {
         TimerList var10001 = this.placed;
         Objects.requireNonNull(var10001);
         PlaceData data = SettingUtils.getPlaceDataOR(pos, var10001::contains);
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

                  this.placed.add(pos, this.oneMissing() ? (Double)this.singleCooldown.get() : (Double)this.cooldown.get());
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

   private boolean oneMissing() {
      boolean alreadyFound = false;
      Iterator var2 = this.surroundBlocks.iterator();

      while(var2.hasNext()) {
         class_2338 pos = (class_2338)var2.next();
         if (LemonUtils.replaceable(pos)) {
            if (alreadyFound) {
               return false;
            }

            alreadyFound = true;
         }
      }

      return true;
   }

   private void setBlock(class_2338 pos) {
      class_1792 item = this.mc.field_1724.method_31548().method_5438(this.result.slot()).method_7909();
      if (item instanceof class_1747 block) {
         this.mc.field_1687.method_8501(pos, block.method_7711().method_9564());
         this.mc.field_1687.method_8486((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), class_3417.field_14574, class_3419.field_15245, 1.0F, 1.0F, false);
      }
   }

   private void setSupport() {
      this.support = false;
      double min = 10000.0;
      Iterator var3 = this.surroundBlocks.iterator();

      class_2338 pos;
      double y;
      while(var3.hasNext()) {
         pos = (class_2338)var3.next();
         if (this.validBlock(pos)) {
            y = Rotations.getYaw(pos.method_46558());
            if (y < min) {
               this.support = false;
               min = y;
            }
         }
      }

      var3 = this.supportPositions.iterator();

      while(var3.hasNext()) {
         pos = (class_2338)var3.next();
         if (this.validBlock(pos)) {
            y = Rotations.getYaw(pos.method_46558());
            if (y < min) {
               this.support = true;
               min = y;
            }
         }
      }

   }

   private boolean valid(class_1799 stack) {
      class_1792 var3 = stack.method_7909();
      boolean var10000;
      if (var3 instanceof class_1747 block) {
         if (((List)(this.support ? this.supportBlocks : this.blocks).get()).contains(block.method_7711())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
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

   private void updateSupport() {
      this.supportPositions.clear();
      this.surroundBlocks.forEach(this::addSupport);
   }

   private void addSupport(class_2338 pos) {
      if (LemonUtils.replaceable(pos)) {
         if (!this.hasSupport(pos, true)) {
            PlaceData data = SettingUtils.getPlaceData(pos);
            if (!data.valid()) {
               class_2350[] var3 = class_2350.values();
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  class_2350 dir = var3[var5];
                  if (dir != class_2350.field_11036 && !this.surroundBlocks.contains(pos.method_10093(dir)) && !this.insideBlocks.contains(pos.method_10093(dir)) && !EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(pos.method_10093(dir))), (entity) -> {
                     return entity instanceof class_1657 && !entity.method_7325();
                  }) && SettingUtils.getPlaceData(pos.method_10093(dir)).valid() && SettingUtils.inPlaceRange(pos.method_10093(dir))) {
                     this.supportPositions.add(pos.method_10093(dir));
                     return;
                  }
               }

            }
         }
      }
   }

   private boolean hasSupport(class_2338 pos, boolean checkNext) {
      class_2350[] var3 = class_2350.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         class_2350 dir = var3[var5];
         if (this.supportPositions.contains(pos.method_10093(dir)) || checkNext && this.hasSupport(pos.method_10093(dir), false)) {
            return true;
         }
      }

      return false;
   }

   private void updateBlocks() {
      this.updateInsideBlocks();
      this.getSurroundBlocks();
      this.insideBlocks.forEach((pos) -> {
         this.surroundBlocks.add(pos.method_10074());
      });
   }

   private void updateInsideBlocks() {
      this.insideBlocks.clear();
      this.addBlocks(this.getPos(), this.getSize(this.mc.field_1724));
      if ((Boolean)this.extend.get()) {
         this.mc.field_1687.method_18456().stream().filter((player) -> {
            return this.mc.field_1724.method_5739(player) < 5.0F && player != this.mc.field_1724;
         }).sorted(Comparator.comparingDouble((player) -> {
            return (double)this.mc.field_1724.method_5739(player);
         })).forEach((player) -> {
            if (this.intersects(player)) {
               this.addBlocks(player.method_24515(), this.getSize(player));
            }
         });
      }

   }

   private boolean intersects(class_1657 player) {
      this.getSurroundBlocks();
      Iterator var2 = this.surroundBlocks.iterator();

      class_2338 pos;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         pos = (class_2338)var2.next();
      } while(!player.method_5829().method_994(class_238.method_19316(new class_3341(pos))));

      return true;
   }

   private void getSurroundBlocks() {
      this.surroundBlocks.clear();
      this.insideBlocks.forEach((pos) -> {
         Iterator var2 = class_2353.field_11062.iterator();

         while(var2.hasNext()) {
            class_2350 dir = (class_2350)var2.next();
            if (!this.surroundBlocks.contains(pos.method_10093(dir)) && !this.insideBlocks.contains(pos.method_10093(dir))) {
               this.surroundBlocks.add(pos.method_10093(dir));
            }
         }

      });
   }

   private void addBlocks(class_2338 pos, int[] size) {
      for(int x = size[0]; x <= size[1]; ++x) {
         for(int z = size[2]; z <= size[3]; ++z) {
            class_2338 p = pos.method_10069(x, 0, z);
            if ((!(this.mc.field_1687.method_8320(p).method_26204().method_9520() > 600.0F) || p.equals(this.currentPos)) && !this.insideBlocks.contains(pos.method_10069(x, 0, z).method_33096(this.currentPos.method_10264()))) {
               this.insideBlocks.add(pos.method_10069(x, 0, z).method_33096(this.currentPos.method_10264()));
            }
         }
      }

   }

   private boolean validEntity(class_1297 entity) {
      if (entity instanceof class_1511 && System.currentTimeMillis() - this.lastAttack < 100L) {
         return false;
      } else {
         return !(entity instanceof class_1542);
      }
   }

   private int[] getSize(class_1657 player) {
      int[] size = new int[4];
      double x = player.method_23317() - (double)player.method_31477();
      double z = player.method_23321() - (double)player.method_31479();
      if (x < 0.3) {
         size[0] = -1;
      }

      if (x > 0.7) {
         size[1] = 1;
      }

      if (z < 0.3) {
         size[2] = -1;
      }

      if (z > 0.7) {
         size[3] = 1;
      }

      return size;
   }

   public class_2338 getPos() {
      return new class_2338(this.mc.field_1724.method_31477(), (int)Math.round(this.mc.field_1724.method_23318()), this.mc.field_1724.method_31479());
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

   public static enum VerticalToggleMode {
      Disabled,
      Up,
      Down,
      Any;

      // $FF: synthetic method
      private static VerticalToggleMode[] $values() {
         return new VerticalToggleMode[]{Disabled, Up, Down, Any};
      }
   }

   public static enum PlaceDelayMode {
      Ticks,
      Seconds;

      // $FF: synthetic method
      private static PlaceDelayMode[] $values() {
         return new PlaceDelayMode[]{Ticks, Seconds};
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
