package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.enums.SwingState;
import dev.lemonclient.addon.enums.SwingType;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.settings.SwingSettings;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1292;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_3341;
import net.minecraft.class_3532;
import net.minecraft.class_742;
import net.minecraft.class_2350.class_2353;
import net.minecraft.class_2846.class_2847;

public class AutoMine extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSpeed;
   private final SettingGroup sgExplode;
   private final SettingGroup sgCev;
   private final SettingGroup sgAntiSurround;
   private final SettingGroup sgAntiBurrow;
   private final SettingGroup sgRender;
   private final Setting pauseEat;
   private final Setting pauseSword;
   private final Setting pickAxeSwitchMode;
   private final Setting crystalSwitchMode;
   private final Setting autoMine;
   private final Setting manualMine;
   private final Setting manualInsta;
   private final Setting manualRemine;
   private final Setting fastRemine;
   private final Setting manualRangeReset;
   private final Setting resetOnSwitch;
   private final Setting speed;
   private final Setting instaDelay;
   private final Setting onGroundCheck;
   private final Setting effectCheck;
   private final Setting waterCheck;
   private final Setting explodeSpeed;
   private final Setting explodeTime;
   private final Setting cevPriority;
   private final Setting instaCev;
   private final Setting trapCevPriority;
   private final Setting instaTrapCev;
   private final Setting surroundCevPriority;
   private final Setting instaSurroundCev;
   private final Setting surroundMinerPriority;
   private final Setting instaSurroundMiner;
   private final Setting autoCityPriority;
   private final Setting instaAutoCity;
   private final Setting explodeCrystal;
   private final Setting antiBurrowPriority;
   private final Setting mineStartSwing;
   private final Setting mineEndSwing;
   private final Setting mineHand;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting attackSwing;
   private final Setting attackHand;
   private final Setting animationExp;
   private final Setting shapeMode;
   private final Setting lineStartColor;
   private final Setting lineEndColor;
   private final Setting startColor;
   private final Setting endColor;
   private double minedFor;
   private Target target;
   private boolean started;
   private class_2338 civPos;
   private List enemies;
   private long lastTime;
   private long lastPlace;
   private long lastExplode;
   private long lastCiv;
   private double render;
   private double delta;
   private final Map explodeAt;
   private boolean reset;
   private boolean mined;
   private class_2680 lastState;
   private class_2338 lastPos;

   public AutoMine() {
      super(LemonClient.Combat, "Auto Mine", "Automatically mines blocks to destroy your enemies.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSpeed = this.settings.createGroup("Speed");
      this.sgExplode = this.settings.createGroup("Explode");
      this.sgCev = this.settings.createGroup("Cev");
      this.sgAntiSurround = this.settings.createGroup("Anti Surround");
      this.sgAntiBurrow = this.settings.createGroup("Anti Burrow");
      this.sgRender = this.settings.createGroup("Render");
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause On Eat")).description("Pause while eating.")).defaultValue(false)).build());
      this.pauseSword = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause Sword")).description("Doesn't mine while holding sword.")).defaultValue(false)).build());
      this.pickAxeSwitchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Pickaxe Switch Mode")).description("Method of switching. InvSwitch is used in most clients.")).defaultValue(AutoMine.SwitchMode.Silent)).build());
      this.crystalSwitchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Crystal Switch Mode")).description("Method of switching. InvSwitch is used in most clients.")).defaultValue(AutoMine.SwitchMode.Silent)).build());
      this.autoMine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Auto Mine")).description("Sets target block to the block you clicked.")).defaultValue(true)).build());
      this.manualMine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Manual Mine")).description("Sets target block to the block you clicked.")).defaultValue(true)).build());
      this.manualInsta = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Manual Instant")).description("Uses civ mine when mining manually.")).defaultValue(false)).build());
      this.manualRemine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Manual Remine")).description("Mines the manually mined block again.")).defaultValue(false)).build());
      this.fastRemine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Fast Remine")).description("Calculates mining progress from last block broken.")).defaultValue(false)).build());
      this.manualRangeReset = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Manual Range Reset")).description("Resets manual mining if out of range.")).defaultValue(true)).build());
      this.resetOnSwitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Reset On Switch")).description("Resets mining when switched held item.")).defaultValue(false)).build());
      this.speed = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Speed")).description("Vanilla speed multiplier.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 2.0).build());
      this.instaDelay = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Instant Delay")).description("Delay between civ mines.")).defaultValue(0.5).min(0.0).sliderRange(0.0, 1.0).build());
      this.onGroundCheck = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("On Ground Check")).description("Mines 5x slower when not on ground.")).defaultValue(true)).build());
      this.effectCheck = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Effect Check")).description("Modifies mining speed depending on haste and mining fatigue.")).defaultValue(true)).build());
      this.waterCheck = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Water Check")).description("Mines 5x slower while submerged in water.")).defaultValue(true)).build());
      this.explodeSpeed = this.sgExplode.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Explode Speed")).description("How many times to attack a crystal every second.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 2.0).build());
      this.explodeTime = this.sgExplode.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Explode Time")).description("Tries to attack a crystal for this many seconds.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.cevPriority = this.sgCev.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Cev Priority")).description("Priority of cev.")).defaultValue(AutoMine.Priority.Normal)).build());
      this.instaCev = this.sgCev.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant Cev")).description("Only sends 1 mine start packet for each block.")).defaultValue(false)).build());
      this.trapCevPriority = this.sgCev.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Trap Cev Priority")).description("Priority of trap cev.")).defaultValue(AutoMine.Priority.Normal)).build());
      this.instaTrapCev = this.sgCev.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant Trap Cev")).description("Only sends 1 mine start packet for each block.")).defaultValue(false)).build());
      this.surroundCevPriority = this.sgCev.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Surround Cev Priority")).description("Priority of trap cev.")).defaultValue(AutoMine.Priority.Normal)).build());
      this.instaSurroundCev = this.sgCev.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant Surround Cev")).description("Only sends 1 mine start packet for each block.")).defaultValue(false)).build());
      this.surroundMinerPriority = this.sgAntiSurround.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Surround Miner Priority")).description("Priority of surround miner.")).defaultValue(AutoMine.Priority.Normal)).build());
      this.instaSurroundMiner = this.sgAntiSurround.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant Surround Miner")).description("Only sends 1 mine start packet for each block.")).defaultValue(false)).build());
      this.autoCityPriority = this.sgAntiSurround.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Auto City Priority")).description("Priority of anti surround. Places crystal next to enemy's surround block.")).defaultValue(AutoMine.Priority.Normal)).build());
      this.instaAutoCity = this.sgAntiSurround.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant Auto City")).description("Only sends 1 mine start packet for each block.")).defaultValue(false)).build());
      this.explodeCrystal = this.sgAntiSurround.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Explode Crystal")).description("Attacks the crystal we placed.")).defaultValue(false)).build());
      this.antiBurrowPriority = this.sgAntiBurrow.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Anti Burrow Priority")).description("Priority of anti burrow.")).defaultValue(AutoMine.Priority.Normal)).build());
      this.mineStartSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Mine Start Swing")).description("Renders swing animation when starting mining.")).defaultValue(true)).build());
      this.mineEndSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Mine End Swing")).description("Renders swing animation when ending mining.")).defaultValue(true)).build());
      this.mineHand = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mine Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand)).visible(() -> {
         return (Boolean)this.mineStartSwing.get() || (Boolean)this.mineEndSwing.get();
      })).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Place Swing")).description("Renders swing animation when placing a crystal.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.attackSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Attack Swing")).description("Renders swing animation when attacking a crystal.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Attack Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.attackSwing;
      Objects.requireNonNull(var10003);
      this.attackHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.animationExp = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Animation Exponent")).description("3 - 4 look cool.")).defaultValue(3.0).range(0.0, 10.0).sliderRange(0.0, 10.0).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts of render should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineStartColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Start Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 0)).build());
      this.lineEndColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line End Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.startColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Start Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 0)).build());
      this.endColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side End Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.minedFor = 0.0;
      this.target = null;
      this.started = false;
      this.civPos = null;
      this.enemies = new ArrayList();
      this.lastTime = 0L;
      this.lastPlace = 0L;
      this.lastExplode = 0L;
      this.lastCiv = 0L;
      this.render = 1.0;
      this.delta = 0.0;
      this.explodeAt = new HashMap();
      this.reset = false;
      this.mined = false;
      this.lastState = null;
      this.lastPos = null;
   }

   public void onActivate() {
      this.target = null;
      this.minedFor = 0.0;
      this.started = false;
      this.lastTime = System.currentTimeMillis();
      this.civPos = null;
      this.reset = false;
   }

   @EventHandler(
      priority = 200
   )
   private void onSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2868 && (Boolean)this.resetOnSwitch.get()) {
         this.reset = true;
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.target != null) {
            if (this.lastState != null && this.target.pos.equals(this.lastPos) && this.target.manual && (Boolean)this.manualRemine.get() && !(Boolean)this.fastRemine.get() && !this.lastState.method_51367() && LemonUtils.solid2(this.target.pos)) {
               this.started = false;
            }

            this.lastPos = this.target.pos;
            this.lastState = this.mc.field_1687.method_8320(this.target.pos);
         } else {
            this.lastPos = null;
            this.lastState = null;
         }

         this.delta = (double)(System.currentTimeMillis() - this.lastTime) / 1000.0;
         this.lastTime = System.currentTimeMillis();
         this.update();
         this.explodeUpdate();
         if (this.target != null) {
            int slot = this.fastestSlot();
            this.render = class_3532.method_15350(this.getMineTicks(slot, true) == this.getMineTicks(slot, false) ? this.render + this.delta * 2.0 : this.render - this.delta * 2.0, -2.0, 2.0);
            double p = 1.0 - class_3532.method_15350(this.minedFor / (double)this.getMineTicks(slot, false), 0.0, 1.0);
            p = Math.pow(p, (Double)this.animationExp.get());
            p = 1.0 - p;
            event.renderer.box(this.getRenderBox(p / 2.0), this.getColor((Color)this.startColor.get(), (Color)this.endColor.get(), p, class_3532.method_15350(this.render, 0.0, 1.0)), this.getColor((Color)this.lineStartColor.get(), (Color)this.lineEndColor.get(), p, class_3532.method_15350(this.render, 0.0, 1.0)), (ShapeMode)this.shapeMode.get(), 0);
            p = 1.0 - class_3532.method_15350(this.minedFor / (double)this.getMineTicks(slot, true), 0.0, 1.0);
            p = Math.pow(p, (Double)this.animationExp.get());
            p = 1.0 - p;
            event.renderer.box(this.getRenderBox(p / 2.0), this.getColor((Color)this.startColor.get(), (Color)this.endColor.get(), p, class_3532.method_15350(-this.render, 0.0, 1.0)), this.getColor((Color)this.lineStartColor.get(), (Color)this.lineEndColor.get(), p, class_3532.method_15350(-this.render, 0.0, 1.0)), (ShapeMode)this.shapeMode.get(), 0);
         }
      }
   }

   private void explodeUpdate() {
      class_1297 targetCrystal = null;
      List toRemove = new ArrayList();
      Iterator var3 = this.explodeAt.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         if ((double)(System.currentTimeMillis() - (Long)entry.getValue()) > (Double)this.explodeTime.get() * 1000.0) {
            toRemove.add((class_2338)entry.getKey());
         }

         class_1511 crystal = this.crystalAt((class_2338)entry.getKey());
         if (crystal != null) {
            targetCrystal = crystal;
            break;
         }
      }

      Map var10001 = this.explodeAt;
      Objects.requireNonNull(var10001);
      toRemove.forEach(var10001::remove);
      if (targetCrystal != null && !this.isPaused() && this.mined && (double)(System.currentTimeMillis() - this.lastExplode) > 1000.0 / (Double)this.explodeSpeed.get() && (!SettingUtils.shouldRotate(RotationType.Attacking) || Managers.ROTATION.start(targetCrystal.method_5829(), (double)this.priority, RotationType.Attacking, (long)Objects.hash(new Object[]{this.name + "attacking"})))) {
         SettingUtils.swing(SwingState.Pre, SwingType.Attacking, class_1268.field_5808);
         this.sendPacket(class_2824.method_34206(targetCrystal, this.mc.field_1724.method_5715()));
         SettingUtils.swing(SwingState.Post, SwingType.Attacking, class_1268.field_5808);
         if ((Boolean)this.attackSwing.get()) {
            this.clientSwing((SwingHand)this.attackHand.get(), class_1268.field_5808);
         }

         this.lastExplode = System.currentTimeMillis();
         if (SettingUtils.shouldRotate(RotationType.Attacking)) {
            Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "attacking"}));
         }
      }

   }

   public double getMineProgress() {
      return this.target == null ? -1.0 : this.minedFor / (double)this.getMineTicks(this.fastestSlot(), true);
   }

   private void update() {
      if (this.mc.field_1687 != null) {
         if (this.reset) {
            if (this.target != null && !this.target.manual) {
               this.target = null;
            }

            this.started = false;
            this.reset = false;
         }

         this.enemies = this.mc.field_1687.method_18456().stream().filter((player) -> {
            return player != this.mc.field_1724 && !Friends.get().isFriend(player) && player.method_5739(this.mc.field_1724) < 10.0F;
         }).toList();
         class_2338 lastPos = this.target != null && this.target.pos != null ? this.target.pos : null;
         if (this.target != null && this.target.manual && (Boolean)this.manualRangeReset.get() && !SettingUtils.inMineRange(this.target.pos)) {
            this.target = null;
            this.started = false;
         }

         if (this.target == null || !this.target.manual) {
            this.target = this.getTarget();
         }

         if (this.target != null) {
            if (this.target.pos != null && !this.target.pos.equals(lastPos)) {
               if (this.started) {
                  this.sendPacket(new class_2846(class_2847.field_12971, this.target.pos, class_2350.field_11033, 0));
               }

               this.started = false;
            }

            if (!this.started) {
               boolean rotated = !SettingUtils.startMineRot() || Managers.ROTATION.start(this.target.pos, (double)this.priority, RotationType.Mining, (long)Objects.hash(new Object[]{this.name + "mining"}));
               if (rotated) {
                  this.started = true;
                  this.minedFor = 0.0;
                  this.civPos = null;
                  if (this.getMineTicks(this.fastestSlot(), true) == this.getMineTicks(this.fastestSlot(), false)) {
                     this.render = 2.0;
                  } else {
                     this.render = -2.0;
                  }

                  class_2350 dir = SettingUtils.getPlaceOnDirection(this.target.pos);
                  this.sendSequenced((s) -> {
                     return new class_2846(class_2847.field_12968, this.target.pos, dir == null ? class_2350.field_11036 : dir, s);
                  });
                  SettingUtils.mineSwing(SwingSettings.MiningSwingState.Start);
                  this.mined = false;
                  if ((Boolean)this.mineStartSwing.get()) {
                     this.clientSwing((SwingHand)this.mineHand.get(), class_1268.field_5808);
                  }

                  if (SettingUtils.startMineRot()) {
                     Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "mining"}));
                  }
               }
            }

            if (this.started) {
               this.minedFor += this.delta * 20.0;
               if (!this.isPaused()) {
                  if (this.miningCheck(this.fastestSlot())) {
                     if (this.civCheck()) {
                        if (this.crystalCheck()) {
                           if (LemonUtils.solid2(this.target.pos)) {
                              this.endMine();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isPaused() {
      if ((Boolean)this.pauseEat.get() && this.mc.field_1724.method_6115()) {
         return true;
      } else {
         return (Boolean)this.pauseSword.get() && this.mc.field_1724.method_6047().method_7909() instanceof class_1829;
      }
   }

   private boolean civCheck() {
      if (this.civPos == null) {
         return true;
      } else {
         return !((double)(System.currentTimeMillis() - this.lastCiv) < (Double)this.instaDelay.get() * 1000.0);
      }
   }

   private void endMine() {
      int slot = this.fastestSlot();
      boolean switched = this.miningCheck(Managers.HOLDING.slot);
      boolean swapBack = false;
      class_2350 dir = SettingUtils.getPlaceOnDirection(this.target.pos);
      if (dir != null) {
         if (!SettingUtils.shouldRotate(RotationType.Mining) || Managers.ROTATION.start(this.target.pos, (double)this.priority, RotationType.Mining, (long)Objects.hash(new Object[]{this.name + "mining"}))) {
            if (!switched) {
               switch ((SwitchMode)this.pickAxeSwitchMode.get()) {
                  case Silent:
                     switched = true;
                     InvUtils.swap(slot, true);
                     break;
                  case PickSilent:
                     switched = true;
                     InventoryUtils.pickSwitch(slot);
                     break;
                  case InvSwitch:
                     switched = InventoryUtils.invSwitch(slot);
               }

               swapBack = switched;
            }

            if (switched) {
               this.sendSequenced((s) -> {
                  return new class_2846(class_2847.field_12973, this.target.pos, dir, s);
               });
               this.mined = true;
               SettingUtils.mineSwing(SwingSettings.MiningSwingState.End);
               if ((Boolean)this.mineEndSwing.get()) {
                  this.clientSwing((SwingHand)this.mineHand.get(), class_1268.field_5808);
               }

               if (this.target.civ) {
                  this.civPos = this.target.pos;
               }

               if (SettingUtils.endMineRot()) {
                  Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "mining"}));
               }

               if (swapBack) {
                  switch ((SwitchMode)this.pickAxeSwitchMode.get()) {
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

               if (this.target.civ) {
                  this.civPos = this.target.pos;
                  this.lastCiv = System.currentTimeMillis();
               } else if (this.target.manual && (Boolean)this.manualRemine.get()) {
                  this.minedFor = 0.0;
               } else {
                  this.target = null;
                  this.minedFor = 0.0;
               }

            }
         }
      }
   }

   private boolean crystalCheck() {
      switch (this.target.type) {
         case Cev:
         case TrapCev:
         case SurroundCev:
            if (this.crystalAt(this.target.crystalPos) != null) {
               return true;
            }

            if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(this.target.crystalPos)).method_35578((double)(this.target.crystalPos.method_10264() + (SettingUtils.cc() ? 1 : 2))), (entity) -> {
               return !entity.method_7325();
            })) {
               this.placeCrystal();
               return false;
            }
            break;
         case AutoCity:
            if (this.crystalAt(this.target.crystalPos) != null) {
               return true;
            }

            if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(this.target.crystalPos)).method_35578((double)(this.target.crystalPos.method_10264() + (SettingUtils.cc() ? 1 : 2))), (entity) -> {
               return !entity.method_7325();
            })) {
               return this.placeCrystal();
            }
            break;
         default:
            return true;
      }

      return false;
   }

   private class_1511 crystalAt(class_2338 pos) {
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(var2.hasNext()) {
         class_1297 entity = (class_1297)var2.next();
         if (entity instanceof class_1511 crystal) {
            if (entity.method_24515().equals(pos)) {
               return crystal;
            }
         }
      }

      return null;
   }

   private boolean placeCrystal() {
      if (System.currentTimeMillis() - this.lastPlace < 250L) {
         return false;
      } else {
         class_1268 hand = this.getHand();
         int crystalSlot = InvUtils.find(new class_1792[]{class_1802.field_8301}).slot();
         if (hand == null && crystalSlot < 0) {
            return false;
         } else {
            class_2350 dir = SettingUtils.getPlaceOnDirection(this.target.crystalPos.method_10074());
            if (dir == null) {
               return false;
            } else {
               boolean rotated = !SettingUtils.shouldRotate(RotationType.Interact) || Managers.ROTATION.start(this.target.crystalPos.method_10074(), (double)this.priority, RotationType.Interact, (long)Objects.hash(new Object[]{this.name + "placing"}));
               if (!rotated) {
                  return false;
               } else {
                  boolean switched = hand != null;
                  if (!switched) {
                     switch ((SwitchMode)this.crystalSwitchMode.get()) {
                        case Silent:
                           switched = true;
                           InvUtils.swap(crystalSlot, true);
                           break;
                        case PickSilent:
                           switched = InventoryUtils.pickSwitch(crystalSlot);
                           break;
                        case InvSwitch:
                           switched = InventoryUtils.invSwitch(crystalSlot);
                     }
                  }

                  if (!switched) {
                     return false;
                  } else {
                     this.interactBlock(hand == null ? class_1268.field_5808 : hand, this.target.crystalPos.method_10074().method_46558(), dir, this.target.crystalPos.method_10074());
                     if ((Boolean)this.placeSwing.get()) {
                        this.clientSwing((SwingHand)this.placeHand.get(), hand == null ? class_1268.field_5808 : hand);
                     }

                     this.lastPlace = System.currentTimeMillis();
                     if (this.shouldExplode()) {
                        this.addExplode();
                     }

                     if (SettingUtils.shouldRotate(RotationType.Interact)) {
                        Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
                     }

                     if (hand == null) {
                        switch ((SwitchMode)this.crystalSwitchMode.get()) {
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

                     return true;
                  }
               }
            }
         }
      }
   }

   private void addExplode() {
      this.explodeAt.remove(this.target.crystalPos);
      this.explodeAt.put(this.target.crystalPos, System.currentTimeMillis());
   }

   private boolean shouldExplode() {
      boolean var10000;
      switch (this.target.type) {
         case Cev:
         case TrapCev:
         case SurroundCev:
            var10000 = true;
            break;
         case AutoCity:
            var10000 = (Boolean)this.explodeCrystal.get();
            break;
         case SurroundMiner:
         case AntiBurrow:
         case Manual:
            var10000 = false;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private Target getTarget() {
      Target target = null;
      if (!(Boolean)this.autoMine.get()) {
         return target;
      } else {
         Target t;
         if (this.priorityCheck(target, (Priority)this.cevPriority.get())) {
            t = this.getCev();
            if (t != null) {
               target = t;
            }
         }

         if (this.priorityCheck(target, (Priority)this.trapCevPriority.get())) {
            t = this.getTrapCev();
            if (t != null) {
               target = t;
            }
         }

         if (this.priorityCheck(target, (Priority)this.surroundCevPriority.get())) {
            t = this.getSurroundCev();
            if (t != null) {
               target = t;
            }
         }

         if (this.priorityCheck(target, (Priority)this.surroundMinerPriority.get())) {
            t = this.getSurroundMiner();
            if (t != null) {
               target = t;
            }
         }

         if (this.priorityCheck(target, (Priority)this.autoCityPriority.get())) {
            t = this.getAutoCity();
            if (t != null) {
               target = t;
            }
         }

         if (this.priorityCheck(target, (Priority)this.antiBurrowPriority.get())) {
            t = this.getAntiBurrow();
            if (t != null) {
               target = t;
            }
         }

         return target;
      }
   }

   private Target getCev() {
      boolean civ = (Boolean)this.instaCev.get();
      Target best = null;
      double distance = 1000.0;
      Iterator var5 = this.enemies.iterator();

      while(true) {
         class_2338 pos;
         double d;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    if (!var5.hasNext()) {
                                       return best;
                                    }

                                    class_742 player = (class_742)var5.next();
                                    pos = new class_2338(player.method_31477(), (int)Math.floor(player.method_5829().field_1325) + 1, player.method_31479());
                                 } while((!civ || !pos.equals(this.civPos)) && this.getBlock(pos) != class_2246.field_10540);
                              } while(civ && pos.equals(this.civPos) && !(this.getBlock(pos) instanceof class_2189) && this.getBlock(pos) != class_2246.field_10540);
                           } while(this.getBlock(pos.method_10084()) != class_2246.field_10124);
                        } while(SettingUtils.oldCrystals() && this.getBlock(pos.method_10086(2)) != class_2246.field_10124);
                     } while(!SettingUtils.inMineRange(pos));
                  } while(!SettingUtils.inPlaceRange(pos));
               } while(!SettingUtils.inAttackRange(LemonUtils.getCrystalBox(pos.method_10084())));
            } while(this.blocked(pos.method_10084()));

            d = this.mc.field_1724.method_33571().method_1022(class_243.method_24953(pos));
         } while(!this.distanceCheck(civ, pos, distance, d));

         best = new Target(pos, pos.method_10084(), AutoMine.MineType.Cev, (double)((Priority)this.cevPriority.get()).priority + (civ && pos.equals(this.civPos) ? 0.1 : 0.0), civ, false);
         distance = d;
      }
   }

   Target getTrapCev() {
      boolean civ = (Boolean)this.instaTrapCev.get();
      Target best = null;
      double distance = 1000.0;
      Iterator var5 = this.enemies.iterator();

      label98:
      while(var5.hasNext()) {
         class_742 player = (class_742)var5.next();
         Iterator var7 = class_2353.field_11062.iterator();

         while(true) {
            class_2338 pos;
            double d;
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       if (!var7.hasNext()) {
                                          continue label98;
                                       }

                                       class_2350 dir = (class_2350)var7.next();
                                       pos = (new class_2338(player.method_31477(), (int)Math.floor(player.method_5829().field_1325), player.method_31479())).method_10093(dir);
                                    } while((!civ || !pos.equals(this.civPos)) && this.getBlock(pos) != class_2246.field_10540);
                                 } while(civ && pos.equals(this.civPos) && !(this.getBlock(pos) instanceof class_2189) && this.getBlock(pos) != class_2246.field_10540);
                              } while(this.getBlock(pos.method_10084()) != class_2246.field_10124);
                           } while(SettingUtils.oldCrystals() && this.getBlock(pos.method_10086(2)) != class_2246.field_10124);
                        } while(!SettingUtils.inMineRange(pos));
                     } while(!SettingUtils.inPlaceRange(pos));
                  } while(!SettingUtils.inAttackRange(LemonUtils.getCrystalBox(pos.method_10084())));
               } while(this.blocked(pos.method_10084()));

               d = this.mc.field_1724.method_33571().method_1022(class_243.method_24953(pos));
            } while(!this.distanceCheck(civ, pos, distance, d));

            best = new Target(pos, pos.method_10084(), AutoMine.MineType.TrapCev, (double)((Priority)this.trapCevPriority.get()).priority + (civ && pos.equals(this.civPos) ? 0.1 : 0.0), civ, false);
            distance = d;
         }
      }

      return best;
   }

   private Target getSurroundCev() {
      boolean civ = (Boolean)this.instaSurroundCev.get();
      Target best = null;
      double distance = 1000.0;
      Iterator var5 = this.enemies.iterator();

      label98:
      while(var5.hasNext()) {
         class_742 player = (class_742)var5.next();
         Iterator var7 = class_2353.field_11062.iterator();

         while(true) {
            class_2338 pos;
            double d;
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       if (!var7.hasNext()) {
                                          continue label98;
                                       }

                                       class_2350 dir = (class_2350)var7.next();
                                       pos = this.getPos(player.method_19538()).method_10093(dir);
                                    } while((!civ || !pos.equals(this.civPos)) && this.getBlock(pos) != class_2246.field_10540);
                                 } while(civ && pos.equals(this.civPos) && !(this.getBlock(pos) instanceof class_2189) && this.getBlock(pos) != class_2246.field_10540);
                              } while(this.getBlock(pos.method_10084()) != class_2246.field_10124);
                           } while(SettingUtils.oldCrystals() && this.getBlock(pos.method_10086(2)) != class_2246.field_10124);
                        } while(!SettingUtils.inMineRange(pos));
                     } while(!SettingUtils.inPlaceRange(pos));
                  } while(!SettingUtils.inAttackRange(LemonUtils.getCrystalBox(pos.method_10084())));
               } while(this.blocked(pos.method_10084()));

               d = this.mc.field_1724.method_33571().method_1022(class_243.method_24953(pos));
            } while(!this.distanceCheck(civ, pos, distance, d));

            best = new Target(pos, pos.method_10084(), AutoMine.MineType.SurroundCev, (double)((Priority)this.surroundCevPriority.get()).priority + (civ && pos.equals(this.civPos) ? 0.1 : 0.0), civ, false);
            distance = d;
         }
      }

      return best;
   }

   private Target getSurroundMiner() {
      boolean civ = (Boolean)this.instaSurroundMiner.get();
      Target best = null;
      double distance = 1000.0;
      Iterator var5 = this.enemies.iterator();

      label55:
      while(var5.hasNext()) {
         class_742 player = (class_742)var5.next();
         Iterator var7 = class_2353.field_11062.iterator();

         while(true) {
            class_2338 pos;
            double d;
            do {
               do {
                  do {
                     do {
                        if (!var7.hasNext()) {
                           continue label55;
                        }

                        class_2350 dir = (class_2350)var7.next();
                        pos = this.getPos(player.method_19538()).method_10093(dir);
                     } while((!civ || !pos.equals(this.civPos)) && !LemonUtils.solid2(pos));
                  } while(this.getBlock(pos) == class_2246.field_9987);
               } while(!SettingUtils.inMineRange(pos));

               d = this.mc.field_1724.method_33571().method_1022(class_243.method_24953(pos));
            } while(!this.distanceCheck(civ, pos, distance, d));

            best = new Target(pos, (class_2338)null, AutoMine.MineType.SurroundMiner, (double)((Priority)this.surroundMinerPriority.get()).priority + (civ && pos.equals(this.civPos) ? 0.1 : 0.0), civ, false);
            distance = d;
         }
      }

      return best;
   }

   private Target getAutoCity() {
      boolean civ = (Boolean)this.instaAutoCity.get();
      Target best = null;
      double distance = 1000.0;
      Iterator var5 = this.enemies.iterator();

      label88:
      while(var5.hasNext()) {
         class_742 player = (class_742)var5.next();
         Iterator var7 = class_2353.field_11062.iterator();

         while(true) {
            class_2350 dir;
            class_2338 pos;
            double d;
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       if (!var7.hasNext()) {
                                          continue label88;
                                       }

                                       dir = (class_2350)var7.next();
                                       pos = this.getPos(player.method_19538()).method_10093(dir);
                                    } while((!civ || !pos.equals(this.civPos)) && !LemonUtils.solid2(pos));
                                 } while(this.getBlock(pos) == class_2246.field_9987);
                              } while(this.getBlock(pos.method_10093(dir)) != class_2246.field_10124);
                           } while(SettingUtils.oldCrystals() && this.getBlock(pos.method_10093(dir).method_10084()) != class_2246.field_10124);
                        } while(!this.crystalBlock(pos.method_10093(dir).method_10074()));
                     } while(!SettingUtils.inMineRange(pos));
                  } while(!SettingUtils.inPlaceRange(pos.method_10093(dir).method_10074()));
               } while(this.blocked(pos.method_10093(dir)));

               d = this.mc.field_1724.method_33571().method_1022(class_243.method_24953(pos));
            } while(!this.distanceCheck(civ, pos, distance, d));

            best = new Target(pos, pos.method_10093(dir), AutoMine.MineType.AutoCity, (double)((Priority)this.autoCityPriority.get()).priority + (civ && pos.equals(this.civPos) ? 0.1 : 0.0), civ, false);
            distance = d;
         }
      }

      return best;
   }

   private Target getAntiBurrow() {
      Target best = null;
      double distance = 1000.0;
      Iterator var4 = this.enemies.iterator();

      while(var4.hasNext()) {
         class_742 player = (class_742)var4.next();
         class_2338 pos = this.getPos(player.method_19538());
         if (LemonUtils.solid2(pos) && this.getBlock(pos) != class_2246.field_9987 && SettingUtils.inMineRange(pos)) {
            double d = this.mc.field_1724.method_33571().method_1022(class_243.method_24953(pos));
            if (d < distance) {
               best = new Target(pos, (class_2338)null, AutoMine.MineType.AntiBurrow, (double)((Priority)this.antiBurrowPriority.get()).priority, false, false);
               distance = d;
            }
         }
      }

      return best;
   }

   private boolean distanceCheck(boolean civ, class_2338 pos, double closest, double distance) {
      if (civ && pos.equals(this.civPos)) {
         return true;
      } else if (this.target != null && pos.equals(this.target.pos)) {
         return true;
      } else {
         return distance < closest;
      }
   }

   private boolean priorityCheck(Target current, Priority priority) {
      if (priority.priority < 0) {
         return false;
      } else if (current == null) {
         return true;
      } else {
         return (double)priority.priority >= current.priority;
      }
   }

   private void abort(class_2338 pos) {
      this.mc.method_1562().method_2883(new class_2846(class_2847.field_12971, pos, class_2350.field_11036));
      this.started = false;
   }

   private class_2248 getBlock(class_2338 pos) {
      return this.mc.field_1687.method_8320(pos).method_26204();
   }

   private class_1268 getHand() {
      if (this.mc.field_1724.method_6079().method_7909() == class_1802.field_8301) {
         return class_1268.field_5810;
      } else {
         return Managers.HOLDING.isHolding(class_1802.field_8301) ? class_1268.field_5808 : null;
      }
   }

   private boolean miningCheck(int slot) {
      if (this.target != null && this.target.pos != null) {
         return this.minedFor * (Double)this.speed.get() >= (double)this.getMineTicks(slot, true);
      } else {
         return false;
      }
   }

   private float getTime(class_2338 pos, int slot, boolean speedMod) {
      class_2680 state = this.mc.field_1687.method_8320(pos);
      float f = state.method_26214(this.mc.field_1687, pos);
      if (f == -1.0F) {
         return 0.0F;
      } else {
         float i = state.method_29291() && !this.mc.field_1724.method_31548().method_5438(slot).method_7951(state) ? 100.0F : 30.0F;
         return this.getSpeed(state, slot, speedMod) / f / i;
      }
   }

   private float getMineTicks(int slot, boolean speedMod) {
      return slot == -1 ? (float)slot : (float)(1.0 / ((double)this.getTime(this.target.pos, slot, speedMod) * (Double)this.speed.get()));
   }

   private float getSpeed(class_2680 state, int slot, boolean speedMod) {
      class_1799 stack = this.mc.field_1724.method_31548().method_5438(slot);
      float f = this.mc.field_1724.method_31548().method_5438(slot).method_7924(state);
      if ((double)f > 1.0) {
         int i = class_1890.method_8225(class_1893.field_9131, stack);
         if (i > 0 && !stack.method_7960()) {
            f += (float)(i * i + 1);
         }
      }

      if (!speedMod) {
         return f;
      } else {
         if ((Boolean)this.effectCheck.get()) {
            if (class_1292.method_5576(this.mc.field_1724)) {
               f = (float)((double)f * (1.0 + (double)((float)(class_1292.method_5575(this.mc.field_1724) + 1) * 0.2F)));
            }

            if (this.mc.field_1724.method_6059(class_1294.field_5901)) {
               f = (float)((double)f * Math.pow(0.3, (double)(this.mc.field_1724.method_6112(class_1294.field_5901).method_5578() + 1)));
            }
         }

         if ((Boolean)this.waterCheck.get() && this.mc.field_1724.method_5869() && !class_1890.method_8200(this.mc.field_1724)) {
            f = (float)((double)f / 5.0);
         }

         if ((Boolean)this.onGroundCheck.get() && !this.mc.field_1724.method_24828()) {
            f = (float)((double)f / 5.0);
         }

         return f;
      }
   }

   public void onStart(class_2338 pos) {
      if (this.target != null && this.target.manual && pos.equals(this.target.pos)) {
         this.abort(this.target.pos);
         this.civPos = null;
         this.target = null;
      } else {
         if ((Boolean)this.manualMine.get() && this.getBlock(pos) != class_2246.field_9987) {
            this.started = false;
            this.target = new Target(pos, (class_2338)null, AutoMine.MineType.Manual, 0.0, (Boolean)this.manualInsta.get(), true);
         }

      }
   }

   public void onAbort(class_2338 pos) {
   }

   public void onStop() {
      this.target = null;
      this.started = false;
   }

   private int fastestSlot() {
      int slot = -1;
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         for(int i = 0; i < (this.pickAxeSwitchMode.get() == AutoMine.SwitchMode.Silent ? 9 : 35); ++i) {
            if (slot == -1 || this.mc.field_1724.method_31548().method_5438(i).method_7924(this.mc.field_1687.method_8320(this.target.pos)) > this.mc.field_1724.method_31548().method_5438(slot).method_7924(this.mc.field_1687.method_8320(this.target.pos))) {
               slot = i;
            }
         }

         return slot;
      } else {
         return -1;
      }
   }

   private Color getColor(Color start, Color end, double progress, double alphaMulti) {
      return new Color(this.lerp((double)start.r, (double)end.r, progress, 1.0), this.lerp((double)start.g, (double)end.g, progress, 1.0), this.lerp((double)start.b, (double)end.b, progress, 1.0), this.lerp((double)start.a, (double)end.a, progress, alphaMulti));
   }

   private int lerp(double start, double end, double d, double multi) {
      return (int)Math.round((start + (end - start) * d) * multi);
   }

   private boolean crystalBlock(class_2338 pos) {
      return this.getBlock(pos) == class_2246.field_10540 || this.getBlock(pos) == class_2246.field_9987;
   }

   private class_238 getRenderBox(double progress) {
      return new class_238((double)this.target.pos.method_10263() + 0.5 - progress, (double)this.target.pos.method_10264() + 0.5 - progress, (double)this.target.pos.method_10260() + 0.5 - progress, (double)this.target.pos.method_10263() + 0.5 + progress, (double)this.target.pos.method_10264() + 0.5 + progress, (double)this.target.pos.method_10260() + 0.5 + progress);
   }

   private boolean blocked(class_2338 pos) {
      class_238 box = new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + (SettingUtils.cc() ? 1 : 2)), (double)(pos.method_10260() + 1));
      return EntityUtils.intersectsWithEntity(box, (entity) -> {
         return entity instanceof class_1657 && !entity.method_7325();
      });
   }

   public class_2338 targetPos() {
      return this.target == null ? null : this.target.pos;
   }

   private class_2338 getPos(class_243 vec) {
      return new class_2338((int)Math.floor(vec.field_1352), (int)Math.round(vec.field_1351), (int)Math.floor(vec.field_1350));
   }

   public static enum SwitchMode {
      Silent,
      PickSilent,
      InvSwitch;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Silent, PickSilent, InvSwitch};
      }
   }

   public static enum Priority {
      Highest(6),
      Higher(5),
      High(4),
      Normal(3),
      Low(2),
      Lower(1),
      Lowest(0),
      Disabled(-1);

      public final int priority;

      private Priority(int priority) {
         this.priority = priority;
      }

      // $FF: synthetic method
      private static Priority[] $values() {
         return new Priority[]{Highest, Higher, High, Normal, Low, Lower, Lowest, Disabled};
      }
   }

   private static record Target(class_2338 pos, class_2338 crystalPos, MineType type, double priority, boolean civ, boolean manual) {
      private Target(class_2338 pos, class_2338 crystalPos, MineType type, double priority, boolean civ, boolean manual) {
         this.pos = pos;
         this.crystalPos = crystalPos;
         this.type = type;
         this.priority = priority;
         this.civ = civ;
         this.manual = manual;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public class_2338 crystalPos() {
         return this.crystalPos;
      }

      public MineType type() {
         return this.type;
      }

      public double priority() {
         return this.priority;
      }

      public boolean civ() {
         return this.civ;
      }

      public boolean manual() {
         return this.manual;
      }
   }

   public static enum MineType {
      Cev,
      TrapCev,
      SurroundCev,
      SurroundMiner,
      AutoCity,
      AntiBurrow,
      Manual;

      // $FF: synthetic method
      private static MineType[] $values() {
         return new MineType[]{Cev, TrapCev, SurroundCev, SurroundMiner, AutoCity, AntiBurrow, Manual};
      }
   }
}
