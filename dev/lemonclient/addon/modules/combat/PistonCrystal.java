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
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.player.PlaceData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2358;
import net.minecraft.class_238;
import net.minecraft.class_2459;
import net.minecraft.class_2527;
import net.minecraft.class_2665;
import net.minecraft.class_2667;
import net.minecraft.class_2671;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2846;
import net.minecraft.class_3341;
import net.minecraft.class_2350.class_2353;
import net.minecraft.class_2846.class_2847;

public class PistonCrystal extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgDelay;
   private final SettingGroup sgSwitch;
   private final SettingGroup sgSwing;
   private final SettingGroup sgRender;
   private final Setting pauseEat;
   private final Setting fire;
   private final Setting redstone;
   private final Setting alwaysAttack;
   private final Setting attackSpeed;
   private final Setting pcDelay;
   private final Setting cfDelay;
   private final Setting crDelay;
   private final Setting rmDelay;
   private final Setting mpDelay;
   private final Setting crystalSwitch;
   private final Setting pistonSwitch;
   private final Setting redstoneSwitch;
   private final Setting fireSwitch;
   private final Setting crystalSwing;
   private final Setting crystalHand;
   private final Setting attackSwing;
   private final Setting attackHand;
   private final Setting pistonSwing;
   private final Setting pistonHand;
   private final Setting redstoneSwing;
   private final Setting redstoneHand;
   private final Setting fireSwing;
   private final Setting fireHand;
   private final Setting crystalHeight;
   private final Setting crystalShapeMode;
   private final Setting crystalLineColor;
   public final Setting crystalColor;
   private final Setting pistonHeight;
   private final Setting pistonShapeMode;
   private final Setting pistonLineColor;
   public final Setting pistonColor;
   private final Setting redstoneHeight;
   private final Setting redstoneShapeMode;
   private final Setting redstoneLineColor;
   public final Setting redstoneColor;
   private long lastAttack;
   public class_2338 crystalPos;
   private class_2338 pistonPos;
   private class_2338 firePos;
   private class_2338 redstonePos;
   private class_2338 lastCrystalPos;
   private class_2338 lastPistonPos;
   private class_2338 lastRedstonePos;
   private class_1297 lastTarget;
   private class_2350 pistonDir;
   private PlaceData pistonData;
   private class_2350 crystalPlaceDir;
   private class_2350 crystalDir;
   private PlaceData redstoneData;
   private class_1297 target;
   private class_2338 closestCrystalPos;
   private class_2338 closestPistonPos;
   private class_2338 closestRedstonePos;
   private class_2350 closestPistonDir;
   private PlaceData closestPistonData;
   private class_2350 closestCrystalPlaceDir;
   private class_2350 closestCrystalDir;
   private PlaceData closestRedstoneData;
   private long pistonTime;
   private long redstoneTime;
   private long mineTime;
   private long crystalTime;
   private boolean minedThisTick;
   private boolean pistonPlaced;
   private boolean redstonePlaced;
   private boolean mined;
   private boolean crystalPlaced;
   private boolean firePlaced;
   private double cd;
   private double d;
   private class_2338 lastPos;
   private class_2338 lastEnemyPos;

   public PistonCrystal() {
      super(LemonClient.Combat, "Piston Crystal", "Pushes crystals into your enemies to deal massive damage.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgDelay = this.settings.createGroup("Delay");
      this.sgSwitch = this.settings.createGroup("Switch");
      this.sgSwing = this.settings.createGroup("Swing");
      this.sgRender = this.settings.createGroup("Render");
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause On Eat")).description("Pause while eating.")).defaultValue(true)).build());
      this.fire = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Fire")).description("Uses fire to blow up the crystal.")).defaultValue(false)).build());
      this.redstone = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Redstone")).description("What kind of redstone to use.")).defaultValue(PistonCrystal.Redstone.Block)).build());
      this.alwaysAttack = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Always Attack")).description("Attacks all crystals blocking crystal placing.")).defaultValue(false)).build());
      this.attackSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Attack Speed")).description("How many times to attack the crystal every second.")).defaultValue(4.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.pcDelay = this.sgDelay.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Piston > Crystal")).description("How many seconds to wait between placing piston and redstone.")).defaultValue(0.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.cfDelay = this.sgDelay.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Crystal > Fire")).description("How many seconds to wait after mining the redstone before starting a new cycle.")).defaultValue(0.2).min(0.0).sliderRange(0.0, 20.0).build());
      this.crDelay = this.sgDelay.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Crystal > Redstone")).description("How many seconds to wait between placing redstone and starting to mine it.")).defaultValue(0.2).min(0.0).sliderRange(0.0, 20.0).build());
      this.rmDelay = this.sgDelay.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Redstone > Mine")).description("How many seconds to wait after mining the redstone before starting a new cycle.")).defaultValue(0.2).min(0.0).sliderRange(0.0, 20.0).build());
      this.mpDelay = this.sgDelay.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Mine > Piston")).description("How many seconds to wait after mining the redstone before starting a new cycle.")).defaultValue(0.2).min(0.0).sliderRange(0.0, 20.0).build());
      this.crystalSwitch = this.sgSwitch.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Crystal Switch")).description("Method of switching. Silent is the most reliable.")).defaultValue(PistonCrystal.SwitchMode.Silent)).build());
      this.pistonSwitch = this.sgSwitch.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Piston Switch")).description("Method of switching. Silent is the most reliable.")).defaultValue(PistonCrystal.SwitchMode.Silent)).build());
      this.redstoneSwitch = this.sgSwitch.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Redstone Switch")).description("Method of switching. Silent is the most reliable.")).defaultValue(PistonCrystal.SwitchMode.Silent)).build());
      this.fireSwitch = this.sgSwitch.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Fire Switch")).description("Method of switching. Silent is the most reliable.")).defaultValue(PistonCrystal.SwitchMode.Silent)).build());
      this.crystalSwing = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Crystal Swing")).description("Renders swing animation when placing a crystal.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgSwing;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Crystal Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.crystalSwing;
      Objects.requireNonNull(var10003);
      this.crystalHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.attackSwing = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Attack Swing")).description("Renders swing animation when attacking a crystal.")).defaultValue(true)).build());
      var10001 = this.sgSwing;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Attack Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.attackSwing;
      Objects.requireNonNull(var10003);
      this.attackHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.pistonSwing = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Piston Swing")).description("Renders swing animation when placing a piston.")).defaultValue(true)).build());
      var10001 = this.sgSwing;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Piston Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.pistonSwing;
      Objects.requireNonNull(var10003);
      this.pistonHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.redstoneSwing = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Piston Swing")).description("Renders swing animation when placing redstone.")).defaultValue(true)).build());
      var10001 = this.sgSwing;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Redstone Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.redstoneSwing;
      Objects.requireNonNull(var10003);
      this.redstoneHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.fireSwing = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Fire Swing")).description("Renders swing animation when placing fire.")).defaultValue(true)).build());
      var10001 = this.sgSwing;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Fire Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.fireSwing;
      Objects.requireNonNull(var10003);
      this.fireHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.crystalHeight = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Crystal Height")).description(".")).defaultValue(0.25).sliderRange(-1.0, 1.0).build());
      this.crystalShapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Crystal Shape Mode")).description(".")).defaultValue(ShapeMode.Both)).build());
      this.crystalLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Crystal Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
      this.crystalColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Crystal Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 0, 0, 50)).build());
      this.pistonHeight = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Piston Height")).description(".")).defaultValue(1.0).sliderRange(-1.0, 1.0).build());
      this.pistonShapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Piston Shape Mode")).description(".")).defaultValue(ShapeMode.Both)).build());
      this.pistonLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Piston Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.pistonColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Piston Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.redstoneHeight = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Redstone Height")).description(".")).defaultValue(1.0).sliderRange(-1.0, 1.0).build());
      this.redstoneShapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Redstone Shape Mode")).description(".")).defaultValue(ShapeMode.Both)).build());
      this.redstoneLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Redstone Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
      this.redstoneColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Redstone Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 0, 0, 50)).build());
      this.lastAttack = 0L;
      this.crystalPos = null;
      this.pistonPos = null;
      this.firePos = null;
      this.redstonePos = null;
      this.lastCrystalPos = null;
      this.lastPistonPos = null;
      this.lastRedstonePos = null;
      this.lastTarget = null;
      this.pistonDir = null;
      this.pistonData = null;
      this.crystalPlaceDir = null;
      this.crystalDir = null;
      this.redstoneData = null;
      this.target = null;
      this.closestCrystalPos = null;
      this.closestPistonPos = null;
      this.closestRedstonePos = null;
      this.closestPistonDir = null;
      this.closestPistonData = null;
      this.closestCrystalPlaceDir = null;
      this.closestCrystalDir = null;
      this.closestRedstoneData = null;
      this.pistonTime = 0L;
      this.redstoneTime = 0L;
      this.mineTime = 0L;
      this.crystalTime = 0L;
      this.minedThisTick = false;
      this.pistonPlaced = false;
      this.redstonePlaced = false;
      this.mined = false;
      this.crystalPlaced = false;
      this.firePlaced = false;
      this.lastPos = null;
      this.lastEnemyPos = null;
   }

   public void onActivate() {
      this.resetPos();
      this.lastCrystalPos = null;
      this.lastPistonPos = null;
      this.lastRedstonePos = null;
      this.pistonPlaced = false;
      this.redstonePlaced = false;
      this.mined = false;
      this.crystalPlaced = false;
      this.firePlaced = false;
   }

   @EventHandler(
      priority = 200
   )
   private void onTick(TickEvent.Pre event) {
      this.minedThisTick = false;
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.updatePos();
         if (this.crystalPos != null) {
            event.renderer.box(this.getBox(this.crystalPos, (Double)this.crystalHeight.get()), (Color)this.crystalColor.get(), (Color)this.crystalLineColor.get(), (ShapeMode)this.crystalShapeMode.get(), 0);
            event.renderer.box(this.getBox(this.pistonPos, (Double)this.pistonHeight.get()), (Color)this.pistonColor.get(), (Color)this.pistonLineColor.get(), (ShapeMode)this.pistonShapeMode.get(), 0);
            event.renderer.box(this.getBox(this.redstonePos, (Double)this.redstoneHeight.get()), (Color)this.redstoneColor.get(), (Color)this.redstoneLineColor.get(), (ShapeMode)this.redstoneShapeMode.get(), 0);
         }

         if (this.crystalPos != null) {
            if ((double)(System.currentTimeMillis() - this.mineTime) > (Double)this.mpDelay.get() * 1000.0 && this.crystalPlaced && this.redstonePlaced && this.pistonPlaced && this.mined && (this.firePlaced || !(Boolean)this.fire.get())) {
               this.redstonePlaced = false;
               this.pistonPlaced = false;
               this.mined = false;
               this.firePlaced = false;
               this.crystalPlaced = false;
               this.pistonTime = 0L;
               this.redstoneTime = 0L;
               this.mineTime = 0L;
               this.crystalTime = 0L;
               this.lastAttack = 0L;
            }

            if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
               this.updateAttack();
               this.updatePiston();
               this.updateFire();
               this.updateCrystal();
               this.updateRedstone();
               this.mineUpdate();
            }
         }
      }
   }

   private class_238 getBox(class_2338 pos, double height) {
      return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + height, (double)(pos.method_10260() + 1));
   }

   private void mineUpdate() {
      if (!((double)(System.currentTimeMillis() - this.redstoneTime) < (Double)this.rmDelay.get() * 1000.0)) {
         if (this.redstonePlaced) {
            if (!this.minedThisTick) {
               AutoMine autoMine = (AutoMine)Modules.get().get(AutoMine.class);
               if (autoMine.isActive()) {
                  if (this.redstonePos.equals(autoMine.targetPos())) {
                     return;
                  }

                  autoMine.onStart(this.redstonePos);
               } else {
                  class_2350 mineDir = SettingUtils.getPlaceOnDirection(this.redstonePos);
                  if (mineDir != null) {
                     this.sendPacket(new class_2846(class_2847.field_12968, this.redstonePos, mineDir));
                     this.sendPacket(new class_2846(class_2847.field_12973, this.redstonePos, mineDir));
                  }
               }

               if (!this.mined) {
                  this.mineTime = System.currentTimeMillis();
               }

               this.mined = true;
               this.minedThisTick = true;
            }
         }
      }
   }

   private void updateAttack() {
      if (this.redstonePlaced) {
         class_1511 crystal = null;
         double cd = 10000.0;
         Iterator var4 = this.mc.field_1687.method_18112().iterator();

         while(true) {
            class_1511 c;
            do {
               do {
                  class_1297 entity;
                  do {
                     if (!var4.hasNext()) {
                        if (crystal == null) {
                           return;
                        }

                        if (SettingUtils.shouldRotate(RotationType.Attacking) && !Managers.ROTATION.start(crystal.method_5829(), (double)this.priority - 0.1, RotationType.Attacking, (long)Objects.hash(new Object[]{this.name + "attack"}))) {
                           return;
                        }

                        if ((double)(System.currentTimeMillis() - this.lastAttack) < 1000.0 / (Double)this.attackSpeed.get()) {
                           return;
                        }

                        SettingUtils.swing(SwingState.Pre, SwingType.Attacking, class_1268.field_5808);
                        this.sendPacket(class_2824.method_34206(crystal, this.mc.field_1724.method_5715()));
                        SettingUtils.swing(SwingState.Post, SwingType.Attacking, class_1268.field_5808);
                        if (SettingUtils.shouldRotate(RotationType.Attacking)) {
                           Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "attacking"}));
                        }

                        if ((Boolean)this.attackSwing.get()) {
                           this.clientSwing((SwingHand)this.attackHand.get(), class_1268.field_5808);
                        }

                        this.lastAttack = System.currentTimeMillis();
                        return;
                     }

                     entity = (class_1297)var4.next();
                  } while(!(entity instanceof class_1511));

                  c = (class_1511)entity;
               } while(c.method_23317() == (double)this.crystalPos.method_10263() + 0.5 && c.method_23321() == (double)this.crystalPos.method_10260() + 0.5);
            } while(!(Boolean)this.alwaysAttack.get() && c.method_23317() - (double)c.method_31477() == 0.5 && c.method_23321() - (double)c.method_31479() == 0.5);

            if (c.method_5829().method_994(class_238.method_19316(new class_3341(this.crystalPos)).method_35578((double)(this.crystalPos.method_10264() + 1)))) {
               double d = this.mc.field_1724.method_33571().method_1022(c.method_19538());
               if (d < cd) {
                  cd = d;
                  crystal = c;
               }
            }
         }
      }
   }

   private void updatePiston() {
      if (!this.pistonPlaced) {
         if (this.pistonData != null) {
            class_1268 hand = this.getHand(class_1802.field_8249);
            boolean available = hand != null;
            if (!available) {
               switch ((SwitchMode)this.pistonSwitch.get()) {
                  case Silent:
                     available = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8249}).found();
                     break;
                  case PickSilent:
                  case InvSwitch:
                     available = InvUtils.find(new class_1792[]{class_1802.field_8249}).found();
               }
            }

            if (available) {
               if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(this.pistonData.pos(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "piston"}))) {
                  boolean switched = false;
                  if (hand == null) {
                     switch ((SwitchMode)this.pistonSwitch.get()) {
                        case Silent:
                           InvUtils.swap(InvUtils.findInHotbar(new class_1792[]{class_1802.field_8249}).slot(), true);
                           switched = true;
                           break;
                        case PickSilent:
                           switched = InventoryUtils.pickSwitch(InvUtils.find(new class_1792[]{class_1802.field_8249}).slot());
                           break;
                        case InvSwitch:
                           switched = InventoryUtils.invSwitch(InvUtils.find(new class_1792[]{class_1802.field_8249}).slot());
                     }
                  }

                  if (hand != null || switched) {
                     this.sendPacket(new class_2828.class_2831(this.pistonDir.method_10153().method_10144(), Managers.ROTATION.lastDir[1], Managers.ON_GROUND.isOnGround()));
                     hand = hand == null ? class_1268.field_5808 : hand;
                     this.placeBlock(hand, this.pistonData.pos().method_46558(), this.pistonData.dir(), this.pistonData.pos());
                     if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                        Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "piston"}));
                     }

                     if ((Boolean)this.pistonSwing.get()) {
                        this.clientSwing((SwingHand)this.pistonHand.get(), hand);
                     }

                     this.pistonTime = System.currentTimeMillis();
                     this.pistonPlaced = true;
                     if (switched) {
                        switch ((SwitchMode)this.pistonSwitch.get()) {
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
      }
   }

   private void updateCrystal() {
      if (this.pistonPlaced && !this.crystalPlaced) {
         if (!((double)(System.currentTimeMillis() - this.pistonTime) < (Double)this.pcDelay.get() * 1000.0)) {
            if (this.crystalPlaceDir != null) {
               if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(this.crystalPos)), (entity) -> {
                  return !entity.method_7325() && !(entity instanceof class_1511);
               })) {
                  class_1268 hand = this.getHand(class_1802.field_8301);
                  boolean available = hand != null;
                  if (!available) {
                     switch ((SwitchMode)this.crystalSwitch.get()) {
                        case Silent:
                           available = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8301}).found();
                           break;
                        case PickSilent:
                        case InvSwitch:
                           available = InvUtils.find(new class_1792[]{class_1802.field_8301}).found();
                     }
                  }

                  if (available) {
                     if (!SettingUtils.shouldRotate(RotationType.Interact) || Managers.ROTATION.start(this.crystalPos.method_10074(), (double)this.priority, RotationType.Interact, (long)Objects.hash(new Object[]{this.name + "crystal"}))) {
                        boolean switched = false;
                        if (hand == null) {
                           switch ((SwitchMode)this.crystalSwitch.get()) {
                              case Silent:
                                 InvUtils.swap(InvUtils.findInHotbar(new class_1792[]{class_1802.field_8301}).slot(), true);
                                 switched = true;
                                 break;
                              case PickSilent:
                                 switched = InventoryUtils.pickSwitch(InvUtils.find(new class_1792[]{class_1802.field_8301}).slot());
                                 break;
                              case InvSwitch:
                                 switched = InventoryUtils.invSwitch(InvUtils.find(new class_1792[]{class_1802.field_8301}).slot());
                           }
                        }

                        if (hand != null || switched) {
                           hand = hand == null ? class_1268.field_5808 : hand;
                           this.interactBlock(hand, this.crystalPos.method_10074().method_46558(), this.crystalPlaceDir, this.crystalPos.method_10074());
                           if (SettingUtils.shouldRotate(RotationType.Interact)) {
                              Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "crystal"}));
                           }

                           if ((Boolean)this.crystalSwing.get()) {
                              this.clientSwing((SwingHand)this.crystalHand.get(), hand);
                           }

                           this.crystalTime = System.currentTimeMillis();
                           this.crystalPlaced = true;
                           if (switched) {
                              switch ((SwitchMode)this.crystalSwitch.get()) {
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
            }
         }
      }
   }

   private void updateRedstone() {
      if (this.crystalPlaced && !this.redstonePlaced) {
         if (!((double)(System.currentTimeMillis() - this.crystalTime) < (Double)this.crDelay.get() * 1000.0)) {
            if (this.redstoneData != null) {
               class_1268 hand = this.getHand(((Redstone)this.redstone.get()).i);
               boolean available = hand != null;
               if (!available) {
                  switch ((SwitchMode)this.redstoneSwitch.get()) {
                     case Silent:
                        available = InvUtils.findInHotbar(new class_1792[]{((Redstone)this.redstone.get()).i}).found();
                        break;
                     case PickSilent:
                     case InvSwitch:
                        available = InvUtils.find(new class_1792[]{((Redstone)this.redstone.get()).i}).found();
                  }
               }

               if (available) {
                  if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(this.redstoneData.pos(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "restone"}))) {
                     boolean switched = false;
                     if (hand == null) {
                        switch ((SwitchMode)this.redstoneSwitch.get()) {
                           case Silent:
                              InvUtils.swap(InvUtils.findInHotbar(new class_1792[]{((Redstone)this.redstone.get()).i}).slot(), true);
                              switched = true;
                              break;
                           case PickSilent:
                              switched = InventoryUtils.pickSwitch(InvUtils.find(new class_1792[]{((Redstone)this.redstone.get()).i}).slot());
                              break;
                           case InvSwitch:
                              switched = InventoryUtils.invSwitch(InvUtils.find(new class_1792[]{((Redstone)this.redstone.get()).i}).slot());
                        }
                     }

                     if (hand != null || switched) {
                        hand = hand == null ? class_1268.field_5808 : hand;
                        this.placeBlock(hand, this.redstoneData.pos().method_46558(), this.redstoneData.dir(), this.redstoneData.pos());
                        if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                           Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "redstone"}));
                        }

                        if ((Boolean)this.redstoneSwing.get()) {
                           this.clientSwing((SwingHand)this.redstoneHand.get(), hand);
                        }

                        this.redstoneTime = System.currentTimeMillis();
                        this.redstonePlaced = true;
                        if (switched) {
                           switch ((SwitchMode)this.redstoneSwitch.get()) {
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
         }
      }
   }

   private void updateFire() {
      if ((Boolean)this.fire.get()) {
         if (this.crystalPlaced && !this.firePlaced) {
            if (!((double)(System.currentTimeMillis() - this.crystalTime) < (Double)this.cfDelay.get() * 1000.0)) {
               double closesD = 10000.0;
               this.firePos = null;
               PlaceData data = null;
               boolean found = false;

               for(int x = this.crystalDir.method_10153().method_10148() == 0 ? -1 : Math.min(0, this.crystalDir.method_10148()); x <= (this.crystalDir.method_10153().method_10148() == 0 ? 1 : Math.max(0, this.crystalDir.method_10153().method_10148())); ++x) {
                  for(int y = 0; y <= 1; ++y) {
                     for(int z = this.crystalDir.method_10153().method_10165() == 0 ? -1 : Math.min(0, this.crystalDir.method_10165()); z <= (this.crystalDir.method_10153().method_10165() == 0 ? 1 : Math.max(0, this.crystalDir.method_10153().method_10165())) && !found; ++z) {
                        class_2338 pos = this.crystalPos.method_10093(this.crystalDir.method_10153()).method_10069(x, y, z);
                        if (!pos.equals(this.crystalPos) && !pos.equals(this.pistonPos) && !pos.equals(this.redstonePos) && !pos.equals(this.pistonPos.method_10093(this.pistonDir.method_10153()))) {
                           if (this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2358) {
                              found = true;
                              this.firePos = pos;
                              data = SettingUtils.getPlaceData(pos);
                           }

                           if (LemonUtils.solid(pos.method_10074()) && this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2189) {
                              double d = pos.method_46558().method_1022(this.mc.field_1724.method_33571());
                              if (!(d >= closesD)) {
                                 PlaceData da = SettingUtils.getPlaceData(pos);
                                 if (da.valid() && SettingUtils.inPlaceRange(da.pos())) {
                                    data = da;
                                    closesD = d;
                                    this.firePos = pos;
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (this.firePos == null) {
                  this.firePlaced = true;
               } else if (data != null && data.valid()) {
                  class_1268 hand = this.getHand(class_1802.field_8884);
                  boolean available = hand != null;
                  if (!available) {
                     switch ((SwitchMode)this.fireSwitch.get()) {
                        case Silent:
                           available = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8884}).found();
                           break;
                        case PickSilent:
                        case InvSwitch:
                           available = InvUtils.find(new class_1792[]{class_1802.field_8884}).found();
                     }
                  }

                  if (available) {
                     if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(data.pos(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "fire"}))) {
                        boolean switched = false;
                        if (hand == null) {
                           switch ((SwitchMode)this.fireSwitch.get()) {
                              case Silent:
                                 InvUtils.swap(InvUtils.findInHotbar(new class_1792[]{class_1802.field_8884}).slot(), true);
                                 switched = true;
                                 break;
                              case PickSilent:
                                 switched = InventoryUtils.pickSwitch(InvUtils.find(new class_1792[]{class_1802.field_8884}).slot());
                                 break;
                              case InvSwitch:
                                 switched = InventoryUtils.invSwitch(InvUtils.find(new class_1792[]{class_1802.field_8884}).slot());
                           }
                        }

                        if (hand != null || switched) {
                           hand = hand == null ? class_1268.field_5808 : hand;
                           this.interactBlock(hand, data.pos().method_46558(), data.dir(), data.pos());
                           if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                              Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "fire"}));
                           }

                           if ((Boolean)this.fireSwing.get()) {
                              this.clientSwing((SwingHand)this.fireHand.get(), hand);
                           }

                           this.firePlaced = true;
                           if (switched) {
                              switch ((SwitchMode)this.fireSwitch.get()) {
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
            }
         }
      }
   }

   private void updatePos() {
      this.lastCrystalPos = this.crystalPos;
      this.lastPistonPos = this.pistonPos;
      this.lastRedstonePos = this.redstonePos;
      this.lastTarget = this.target;
      this.closestCrystalPos = null;
      this.closestPistonPos = null;
      this.closestRedstonePos = null;
      this.closestPistonDir = null;
      this.closestPistonData = null;
      this.closestCrystalPlaceDir = null;
      this.closestCrystalDir = null;
      this.closestRedstoneData = null;
      this.resetPos();
      this.mc.field_1687.method_18456().stream().filter((player) -> {
         return player != this.mc.field_1724 && player.method_19538().method_1022(this.mc.field_1724.method_19538()) < 10.0 && player.method_6032() > 0.0F && !Friends.get().isFriend(player) && !player.method_7325();
      }).sorted(Comparator.comparingDouble((i) -> {
         return i.method_19538().method_1022(this.mc.field_1724.method_19538());
      })).forEach((player) -> {
         if (this.crystalPos == null) {
            this.update(player, true);
            if (this.crystalPos != null) {
               return;
            }

            this.update(player, false);
         }

      });
   }

   private void update(class_1657 player, boolean top) {
      this.cd = 10000.0;
      Iterator var3 = class_2353.field_11062.iterator();

      while(var3.hasNext()) {
         class_2350 dir = (class_2350)var3.next();
         this.resetPos();
         class_2338 cPos = top ? class_2338.method_49638(player.method_33571()).method_10093(dir).method_10084() : class_2338.method_49638(player.method_33571()).method_10093(dir);
         this.d = cPos.method_46558().method_1022(this.mc.field_1724.method_19538());
         if (cPos.equals(this.lastCrystalPos) || !(this.d > this.cd)) {
            class_2248 b = this.mc.field_1687.method_8320(cPos).method_26204();
            if (b instanceof class_2189 || b == class_2246.field_10379 || b == class_2246.field_10008) {
               b = this.mc.field_1687.method_8320(cPos.method_10084()).method_26204();
               if ((!SettingUtils.oldCrystals() || b instanceof class_2189 || b == class_2246.field_10379 || b == class_2246.field_10008) && (this.mc.field_1687.method_8320(cPos.method_10074()).method_26204() == class_2246.field_10540 || this.mc.field_1687.method_8320(cPos.method_10074()).method_26204() == class_2246.field_9987) && !EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(cPos)).method_35578((double)(cPos.method_10264() + (SettingUtils.cc() ? 1 : 2))), (entity) -> {
                  return !entity.method_7325() && entity instanceof class_1657;
               }) && SettingUtils.inPlaceRange(cPos)) {
                  class_2350 cDir = SettingUtils.getPlaceOnDirection(cPos);
                  if (cDir != null) {
                     this.getPistonPos(cPos, dir);
                     if (this.pistonPos != null) {
                        this.cd = this.d;
                        this.crystalPos = cPos;
                        this.crystalPlaceDir = cDir;
                        this.crystalDir = dir;
                        this.closestCrystalPos = this.crystalPos;
                        this.closestPistonPos = this.pistonPos;
                        this.closestRedstonePos = this.redstonePos;
                        this.closestPistonDir = this.pistonDir;
                        this.closestPistonData = this.pistonData;
                        this.closestCrystalPlaceDir = this.crystalPlaceDir;
                        this.closestCrystalDir = this.crystalDir;
                        this.closestRedstoneData = this.redstoneData;
                        if (this.crystalPos.equals(this.lastCrystalPos)) {
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      this.crystalPos = this.closestCrystalPos;
      this.pistonPos = this.closestPistonPos;
      this.redstonePos = this.closestRedstonePos;
      this.pistonDir = this.closestPistonDir;
      this.pistonData = this.closestPistonData;
      this.crystalPlaceDir = this.closestCrystalPlaceDir;
      this.crystalDir = this.closestCrystalDir;
      this.redstoneData = this.closestRedstoneData;
      this.target = player;
   }

   private void getPistonPos(class_2338 pos, class_2350 dir) {
      List pistonBlocks = this.pistonBlocks(pos, dir);
      this.cd = 10000.0;
      class_2338 cPos = null;
      PlaceData cData = null;
      class_2350 cDir = null;
      class_2338 cRedstonePos = null;
      PlaceData cRedstoneData = null;
      Iterator var9 = pistonBlocks.iterator();

      while(var9.hasNext()) {
         class_2338 position = (class_2338)var9.next();
         this.d = this.mc.field_1724.method_33571().method_1022(position.method_46558());
         if (position.equals(this.lastPistonPos) || !(this.cd < this.d)) {
            PlaceData placeData = SettingUtils.getPlaceDataAND(position, (d) -> {
               return true;
            }, (b) -> {
               return !this.isRedstone(b) && !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2665) && !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2671) && !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2667) && this.mc.field_1687.method_8320(b).method_26204() != class_2246.field_10008 && !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2358);
            });
            if (placeData.valid() && SettingUtils.inPlaceRange(placeData.pos())) {
               this.redstonePos(position, dir.method_10153(), pos);
               if (this.redstonePos != null) {
                  this.cd = this.d;
                  cRedstonePos = this.redstonePos;
                  cRedstoneData = this.redstoneData;
                  cPos = position;
                  cDir = dir.method_10153();
                  cData = placeData;
                  if (position.equals(this.lastPistonPos)) {
                     break;
                  }
               }
            }
         }
      }

      this.pistonPos = cPos;
      this.pistonDir = cDir;
      this.pistonData = cData;
      this.redstonePos = cRedstonePos;
      this.redstoneData = cRedstoneData;
   }

   private List pistonBlocks(class_2338 pos, class_2350 dir) {
      List blocks = new ArrayList();

      for(int x = dir.method_10148() == 0 ? -1 : dir.method_10148(); x <= (dir.method_10148() == 0 ? 1 : dir.method_10148()); ++x) {
         for(int z = dir.method_10165() == 0 ? -1 : dir.method_10165(); z <= (dir.method_10165() == 0 ? 1 : dir.method_10165()); ++z) {
            for(int y = 0; y <= 1; ++y) {
               if ((x != 0 || y != 0 || z != 0) && (!SettingUtils.oldCrystals() || x != 0 || y != 1 || z != 0) && this.upCheck(pos.method_10069(x, y, z))) {
                  blocks.add(pos.method_10069(x, y, z));
               }
            }
         }
      }

      return blocks.stream().filter((b) -> {
         if (this.blocked(b.method_10093(dir.method_10153()))) {
            return false;
         } else if (EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(b)), (entity) -> {
            return !entity.method_7325() && entity instanceof class_1657;
         })) {
            return false;
         } else {
            return !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2665) && this.mc.field_1687.method_8320(b).method_26204() != class_2246.field_10008 && !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2358) ? LemonUtils.replaceable(b) : true;
         }
      }).toList();
   }

   private void redstonePos(class_2338 pos, class_2350 pDir, class_2338 cPos) {
      this.cd = 10000.0;
      this.redstonePos = null;
      class_2338 cRedstonePos = null;
      PlaceData cRedstoneData = null;
      class_2350[] var6;
      int var7;
      int var8;
      class_2350 direction;
      class_2338 position;
      if (this.redstone.get() == PistonCrystal.Redstone.Torch) {
         var6 = class_2350.values();
         var7 = var6.length;

         for(var8 = 0; var8 < var7; ++var8) {
            direction = var6[var8];
            if (direction != pDir && direction != class_2350.field_11033) {
               position = pos.method_10093(direction);
               this.d = position.method_46558().method_1022(this.mc.field_1724.method_33571());
               if ((position.equals(this.lastPistonPos) || !(this.cd < this.d)) && !position.equals(cPos) && (!SettingUtils.oldCrystals() || !position.equals(cPos.method_10084())) && (LemonUtils.replaceable(position) || this.mc.field_1687.method_8320(position).method_26204() instanceof class_2459 || this.mc.field_1687.method_8320(position).method_26204() instanceof class_2358)) {
                  this.redstoneData = SettingUtils.getPlaceDataAND(position, (d) -> {
                     if (d == class_2350.field_11036 && !LemonUtils.solid(position.method_10074())) {
                        return false;
                     } else {
                        return direction != d.method_10153();
                     }
                  }, (b) -> {
                     if (pos.equals(b)) {
                        return false;
                     } else if (this.mc.field_1687.method_8320(b).method_26204() instanceof class_2527) {
                        return false;
                     } else {
                        return !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2665) && !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2671);
                     }
                  });
                  if (this.redstoneData.valid() && SettingUtils.inPlaceRange(this.redstoneData.pos()) && SettingUtils.inMineRange(position)) {
                     this.cd = this.d;
                     cRedstonePos = position;
                     cRedstoneData = this.redstoneData;
                     if (position.equals(this.lastRedstonePos)) {
                        break;
                     }
                  }
               }
            }
         }

         this.redstonePos = cRedstonePos;
         this.redstoneData = cRedstoneData;
      } else {
         var6 = class_2350.values();
         var7 = var6.length;

         for(var8 = 0; var8 < var7; ++var8) {
            direction = var6[var8];
            if (direction != pDir) {
               position = pos.method_10093(direction);
               this.d = position.method_46558().method_1022(this.mc.field_1724.method_33571());
               if ((position.equals(this.lastPistonPos) || !(this.cd < this.d)) && !position.equals(cPos) && (LemonUtils.replaceable(position) || this.mc.field_1687.method_8320(position).method_26204() == class_2246.field_10002) && !class_238.method_19316(new class_3341(position)).method_994(LemonUtils.getCrystalBox(cPos)) && !EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(position)), (entity) -> {
                  return !entity.method_7325() && entity instanceof class_1657;
               })) {
                  Objects.requireNonNull(pos);
                  this.redstoneData = SettingUtils.getPlaceDataOR(position, pos::equals);
                  if (this.redstoneData.valid()) {
                     this.cd = this.d;
                     cRedstonePos = position;
                     cRedstoneData = this.redstoneData;
                     if (position.equals(this.lastRedstonePos)) {
                        break;
                     }
                  }
               }
            }
         }

         this.redstonePos = cRedstonePos;
         this.redstoneData = cRedstoneData;
      }
   }

   private class_1297 crystalAt() {
      Iterator var1 = this.mc.field_1687.method_18112().iterator();

      class_1297 entity;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         entity = (class_1297)var1.next();
      } while(!entity.method_24515().equals(this.crystalPos));

      return entity;
   }

   private boolean upCheck(class_2338 pos) {
      double dx = this.mc.field_1724.method_33571().field_1352 - (double)pos.method_10263() - 0.5;
      double dz = this.mc.field_1724.method_33571().field_1350 - (double)pos.method_10260() - 0.5;
      return Math.sqrt(dx * dx + dz * dz) > Math.abs(this.mc.field_1724.method_33571().field_1351 - (double)pos.method_10264() - 0.5);
   }

   private boolean isRedstone(class_2338 pos) {
      return this.mc.field_1687.method_8320(pos).method_26219();
   }

   private boolean blocked(class_2338 pos) {
      class_2248 b = this.mc.field_1687.method_8320(pos).method_26204();
      if (b == class_2246.field_10008) {
         return false;
      } else if (b == class_2246.field_10379) {
         return false;
      } else if (b == class_2246.field_10523) {
         return false;
      } else if (b instanceof class_2358) {
         return false;
      } else {
         return !(this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2189);
      }
   }

   private class_1268 getHand(class_1792 item) {
      return Managers.HOLDING.isHolding(item) ? class_1268.field_5808 : (this.mc.field_1724.method_6079().method_7909() == item ? class_1268.field_5810 : null);
   }

   private void resetPos() {
      this.crystalPos = null;
      this.pistonPos = null;
      this.firePos = null;
      this.redstonePos = null;
      this.pistonDir = null;
      this.pistonData = null;
      this.crystalPlaceDir = null;
      this.crystalDir = null;
      this.redstoneData = null;
   }

   public static enum Redstone {
      Torch(class_1802.field_8530, class_2246.field_10523),
      Block(class_1802.field_8793, class_2246.field_10002);

      public final class_1792 i;
      public final class_2248 b;

      private Redstone(class_1792 i, class_2248 b) {
         this.i = i;
         this.b = b;
      }

      // $FF: synthetic method
      private static Redstone[] $values() {
         return new Redstone[]{Torch, Block};
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
