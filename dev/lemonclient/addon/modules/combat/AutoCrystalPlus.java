package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.enums.SwingState;
import dev.lemonclient.addon.enums.SwingType;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.mixins.IInteractEntityC2SPacket;
import dev.lemonclient.addon.modules.misc.Suicide;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.entity.LemonEntityUtils;
import dev.lemonclient.addon.utils.misc.ExtrapolationUtils;
import dev.lemonclient.addon.utils.player.DamageInfo;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.timers.TimerList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2604;
import net.minecraft.class_2824;
import net.minecraft.class_2868;
import net.minecraft.class_2885;
import net.minecraft.class_3532;
import net.minecraft.class_742;
import net.minecraft.class_1297.class_5529;

public class AutoCrystalPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlace;
   private final SettingGroup sgExplode;
   private final SettingGroup sgSwitch;
   private final SettingGroup sgDamage;
   private final SettingGroup sgID;
   private final SettingGroup sgExtrapolation;
   private final SettingGroup sgRender;
   private final SettingGroup sgCompatibility;
   private final SettingGroup sgDebug;
   private final Setting place;
   private final Setting explode;
   private final Setting pauseEat;
   private final Setting performance;
   private final Setting smartRot;
   private final Setting ignoreTerrain;
   private final Setting instantPlace;
   private final Setting speedLimit;
   private final Setting placeSpeed;
   private final Setting placeDelayMode;
   private final Setting placeDelay;
   private final Setting placeDelayTicks;
   private final Setting slowDamage;
   private final Setting slowSpeed;
   private final Setting onlyOwn;
   private final Setting existedMode;
   private final Setting existed;
   private final Setting existedTicks;
   private final Setting sequential;
   private final Setting instantAttack;
   private final Setting expSpeedLimit;
   private final Setting expSpeed;
   private final Setting setDead;
   private final Setting setDeadDelay;
   private final Setting switchMode;
   private final Setting switchPenalty;
   private final Setting dmgCheckMode;
   private final Setting minPlace;
   private final Setting maxPlace;
   private final Setting minPlaceRatio;
   private final Setting maxFriendPlace;
   private final Setting minFriendPlaceRatio;
   private final Setting expMode;
   private final Setting minExplode;
   private final Setting maxExp;
   private final Setting minExpRatio;
   private final Setting maxFriendExp;
   private final Setting minFriendExpRatio;
   private final Setting forcePop;
   private final Setting antiFriendPop;
   private final Setting antiSelfPop;
   private final Setting idPredict;
   private final Setting idStartOffset;
   private final Setting idOffset;
   private final Setting idPackets;
   private final Setting idDelay;
   private final Setting idPacketDelay;
   private final Setting selfExt;
   private final Setting extrapolation;
   private final Setting rangeExtrapolation;
   private final Setting hitboxExtrapolation;
   private final Setting extSmoothness;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting attackSwing;
   private final Setting attackHand;
   private final Setting render;
   private final Setting renderMode;
   private final Setting renderTime;
   private final Setting fadeMode;
   private final Setting earthFadeMode;
   private final Setting fadeTime;
   private final Setting animationSpeed;
   private final Setting animationMoveExponent;
   private final Setting animationExponent;
   private final Setting shapeMode;
   private final Setting lineColor;
   public final Setting color;
   private final Setting autoMineDamage;
   private final Setting amPlace;
   private final Setting amProgress;
   private final Setting amSpam;
   private final Setting amBroken;
   private final Setting paAttack;
   private final Setting paPlace;
   private final Setting renderExt;
   private final Setting renderSelfExt;
   private long ticksEnabled;
   private double placeTimer;
   private double placeLimitTimer;
   private double delayTimer;
   private int delayTicks;
   private class_2338 placePos;
   private class_2350 placeDir;
   private class_1297 expEntity;
   private class_238 expEntityBB;
   private final TimerList attackedList;
   private final Map existedList;
   private final Map existedTicksList;
   private final Map own;
   private final Map extPos;
   private final Map extHitbox;
   private class_243 rangePos;
   private final List blocked;
   private final Map earthMap;
   private double attackTimer;
   private double switchTimer;
   private int confirmed;
   private long lastMillis;
   private boolean suicide;
   public static boolean placing = false;
   private long lastAttack;
   private class_243 renderTarget;
   private class_243 renderPos;
   private double renderProgress;
   private AutoMine autoMine;
   private int placed;
   private double cps;
   private double infoCps;
   private long lastExplosion;
   private final List predicts;
   private final List setDeads;

   public AutoCrystalPlus() {
      super(LemonClient.Combat, "Auto Crystal+", "Automatically place and attack crystals in strict anti-cheat servers.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlace = this.settings.createGroup("Place");
      this.sgExplode = this.settings.createGroup("Explode");
      this.sgSwitch = this.settings.createGroup("Switch");
      this.sgDamage = this.settings.createGroup("Damage");
      this.sgID = this.settings.createGroup("ID Predict");
      this.sgExtrapolation = this.settings.createGroup("Extrapolation");
      this.sgRender = this.settings.createGroup("Render");
      this.sgCompatibility = this.settings.createGroup("Compatibility");
      this.sgDebug = this.settings.createGroup("Debug");
      this.place = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Place")).description("Places crystals.")).defaultValue(true)).build());
      this.explode = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Explode")).description("Explodes crystals.")).defaultValue(true)).build());
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause Eat")).description("Pauses while eating.")).defaultValue(true)).build());
      this.performance = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Performance Mode")).description("Doesn't calculate placements as often.")).defaultValue(false)).build());
      this.smartRot = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Smart Rotations")).description("Looks at the top of placement block to make the ca faster.")).defaultValue(true)).build());
      this.ignoreTerrain = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore Terrain")).description("Spams trough terrain to kill your enemy.")).defaultValue(true)).build());
      this.instantPlace = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant Place")).description("Ignores delay after crystal has disappeared.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgPlace;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Speed Limit")).description("Maximum amount of place packets every second. 0 = no limit.")).defaultValue(0.0).min(0.0).sliderRange(0.0, 20.0);
      Setting var10003 = this.instantPlace;
      Objects.requireNonNull(var10003);
      this.speedLimit = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.placeSpeed = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Speed")).description("How many times should the module place per second.")).defaultValue(10.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.placeDelayMode = this.sgPlace.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Delay Mode")).description("Should we count the delay in seconds or ticks.")).defaultValue(AutoCrystalPlus.DelayMode.Ticks)).build());
      this.placeDelay = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place Delay")).description("How many seconds after attacking a crystal should we place.")).defaultValue(0.0).min(0.0).sliderRange(0.0, 1.0).visible(() -> {
         return this.placeDelayMode.get() == AutoCrystalPlus.DelayMode.Seconds;
      })).build());
      this.placeDelayTicks = this.sgPlace.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Place Delay Ticks")).description("How many ticks should the crystal exist before attacking.")).defaultValue(0)).min(0).sliderRange(0, 20).visible(() -> {
         return this.placeDelayMode.get() == AutoCrystalPlus.DelayMode.Ticks;
      })).build());
      this.slowDamage = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Slow Damage")).description("Switches to slow speed when the target would take under this amount of damage.")).defaultValue(3.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.slowSpeed = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Slow Speed")).description("How many times should the module place per second when damage is under slow damage.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.onlyOwn = this.sgExplode.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Only Own")).description("Only attacks own crystals.")).defaultValue(false)).build());
      this.existedMode = this.sgExplode.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Existed Mode")).description("Should crystal existed times be counted in seconds or ticks.")).defaultValue(AutoCrystalPlus.DelayMode.Seconds)).build());
      this.existed = this.sgExplode.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Existed")).description("How many seconds should the crystal exist before attacking.")).defaultValue(0.0).min(0.0).sliderRange(0.0, 1.0).visible(() -> {
         return this.existedMode.get() == AutoCrystalPlus.DelayMode.Seconds;
      })).build());
      this.existedTicks = this.sgExplode.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Existed Ticks")).description("How many ticks should the crystal exist before attacking.")).defaultValue(0)).min(0).sliderRange(0, 20).visible(() -> {
         return this.existedMode.get() == AutoCrystalPlus.DelayMode.Ticks;
      })).build());
      this.sequential = this.sgExplode.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Sequential")).description("Doesn't place and attack during the same tick.")).defaultValue(AutoCrystalPlus.SequentialMode.Disabled)).build());
      this.instantAttack = this.sgExplode.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Instant Attack")).description("Delay isn't calculated for first attack.")).defaultValue(true)).build());
      var10001 = this.sgExplode;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Explode Speed Limit")).description("How many times to hit any crystal each second. 0 = no limit")).defaultValue(0.0).min(0.0).sliderRange(0.0, 20.0);
      var10003 = this.instantAttack;
      Objects.requireNonNull(var10003);
      this.expSpeedLimit = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.expSpeed = this.sgExplode.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Explode Speed")).description("How many times to hit crystal each second.")).defaultValue(4.0).range(0.01, 20.0).sliderRange(0.01, 20.0).build());
      this.setDead = this.sgExplode.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Set Dead")).description("Hides the crystal after hitting it. Not needed since the module already is smart enough.")).defaultValue(false)).build());
      var10001 = this.sgExplode;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Set Dead Delay")).description("How long after hitting should the crystal disappear.")).defaultValue(0.05).range(0.0, 1.0).sliderRange(0.0, 1.0);
      var10003 = this.setDead;
      Objects.requireNonNull(var10003);
      this.setDeadDelay = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.switchMode = this.sgSwitch.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Mode for switching to crystal in main hand.")).defaultValue(AutoCrystalPlus.SwitchMode.Simple)).build());
      this.switchPenalty = this.sgSwitch.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Switch Penalty")).description("Time to wait after switching before hitting crystals.")).defaultValue(0.25).min(0.0).sliderRange(0.0, 1.0).build());
      this.dmgCheckMode = this.sgDamage.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Dmg Check Mode")).description("How safe are the placements (normal is good).")).defaultValue(AutoCrystalPlus.DmgCheckMode.Normal)).build());
      this.minPlace = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Place")).description("Minimum damage to place.")).defaultValue(4.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.maxPlace = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Max Place")).description("Max self damage for placing.")).defaultValue(8.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.minPlaceRatio = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Place Ratio")).description("Max self damage ratio for placing (enemy / self).")).defaultValue(1.4).min(0.0).sliderRange(0.0, 5.0).build());
      this.maxFriendPlace = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Max Friend Place")).description("Max friend damage for placing.")).defaultValue(8.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.minFriendPlaceRatio = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Friend Place Ratio")).description("Max friend damage ratio for placing (enemy / friend).")).defaultValue(2.0).min(0.0).sliderRange(0.0, 5.0).build());
      this.expMode = this.sgDamage.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Explode Damage Mode")).description("Which things should be checked for exploding.")).defaultValue(AutoCrystalPlus.ExplodeMode.FullCheck)).build());
      this.minExplode = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Explode")).description("Minimum enemy damage for exploding a crystal.")).defaultValue(2.5).min(0.0).sliderRange(0.0, 20.0).build());
      this.maxExp = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Max Explode")).description("Max self damage for exploding a crystal.")).defaultValue(9.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.minExpRatio = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Explode Ratio")).description("Max self damage ratio for exploding a crystal (enemy / self).")).defaultValue(1.1).min(0.0).sliderRange(0.0, 5.0).build());
      this.maxFriendExp = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Max Friend Explode")).description("Max friend damage for exploding a crystal.")).defaultValue(12.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.minFriendExpRatio = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Friend Explode Ratio")).description("Min friend damage ratio for exploding a crystal (enemy / friend).")).defaultValue(2.0).min(0.0).sliderRange(0.0, 5.0).build());
      this.forcePop = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Force Pop")).description("Ignores damage checks if any enemy will be popped in x hits.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.antiFriendPop = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Anti Friend Pop")).description("Cancels any action if any friend will be popped in x hits.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.antiSelfPop = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Anti Self Pop")).description("Cancels any action if you will be popped in x hits.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.idPredict = this.sgID.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ID Predict")).description("Hits the crystal before it spawns.")).defaultValue(false)).build());
      this.idStartOffset = this.sgID.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Id Start Offset")).description("How many id's ahead should we attack.")).defaultValue(1)).min(0).sliderMax(10).build());
      this.idOffset = this.sgID.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Id Packet Offset")).description("How many id's ahead should we attack between id packets.")).defaultValue(1)).min(1).sliderMax(10).build());
      this.idPackets = this.sgID.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Id Packets")).description("How many packets to send.")).defaultValue(1)).min(1).sliderMax(10).build());
      this.idDelay = this.sgID.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("ID Start Delay")).description("Starts sending id predict packets after this many seconds.")).defaultValue(0.05).min(0.0).sliderRange(0.0, 1.0).build());
      this.idPacketDelay = this.sgID.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("ID Packet Delay")).description("Waits this many seconds between sending ID packets.")).defaultValue(0.05).min(0.0).sliderRange(0.0, 1.0).build());
      this.selfExt = this.sgExtrapolation.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Self Extrapolation")).description("How many ticks of movement should be predicted for self damage checks.")).defaultValue(0)).range(0, 100).sliderMax(20).build());
      this.extrapolation = this.sgExtrapolation.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Extrapolation")).description("How many ticks of movement should be predicted for enemy damage checks.")).defaultValue(0)).range(0, 100).sliderMax(20).build());
      this.rangeExtrapolation = this.sgExtrapolation.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Range Extrapolation")).description("How many ticks of movement should be predicted for attack ranges before placing.")).defaultValue(0)).range(0, 100).sliderMax(20).build());
      this.hitboxExtrapolation = this.sgExtrapolation.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Hitbox Extrapolation")).description("How many ticks of movement should be predicted for hitboxes in placing checks.")).defaultValue(0)).range(0, 100).sliderMax(20).build());
      this.extSmoothness = this.sgExtrapolation.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Extrapolation Smoothening")).description("How many earlier ticks should be used in average calculation for extrapolation motion.")).defaultValue(2)).range(1, 20).sliderRange(1, 20).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Place Swing")).description("Renders swing animation when placing a crystal.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var1 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      this.attackSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Attack Swing")).description("Renders swing animation when placing a crystal.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var1 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Attack Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.attackSwing;
      Objects.requireNonNull(var10003);
      this.attackHand = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Render")).description("Renders box on placement.")).defaultValue(true)).build());
      this.renderMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Render Mode")).description("What should the render look like.")).defaultValue(AutoCrystalPlus.RenderMode.Future)).build());
      this.renderTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Render Time")).description("How long the box should remain in full alpha value.")).defaultValue(0.3).min(0.0).sliderRange(0.0, 10.0).visible(() -> {
         return ((RenderMode)this.renderMode.get()).equals(AutoCrystalPlus.RenderMode.Earthhack) || ((RenderMode)this.renderMode.get()).equals(AutoCrystalPlus.RenderMode.Future);
      })).build());
      this.fadeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Fade Mode")).description("How long the fading should take.")).defaultValue(AutoCrystalPlus.FadeMode.Normal)).visible(() -> {
         return this.renderMode.get() == AutoCrystalPlus.RenderMode.Boze;
      })).build());
      this.earthFadeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Earth Fade Mode")).description(".")).defaultValue(AutoCrystalPlus.EarthFadeMode.Normal)).visible(() -> {
         return this.renderMode.get() == AutoCrystalPlus.RenderMode.Earthhack;
      })).build());
      this.fadeTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Fade Time")).description("How long the fading should take.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).visible(() -> {
         return ((RenderMode)this.renderMode.get()).equals(AutoCrystalPlus.RenderMode.Earthhack) || ((RenderMode)this.renderMode.get()).equals(AutoCrystalPlus.RenderMode.Future);
      })).build());
      this.animationSpeed = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Animation Move Speed")).description("How fast should blackout mode box move.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).visible(() -> {
         return ((RenderMode)this.renderMode.get()).equals(AutoCrystalPlus.RenderMode.Boze);
      })).build());
      this.animationMoveExponent = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Animation Move Exponent")).description("Moves faster when longer away from the target.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 10.0).visible(() -> {
         return ((RenderMode)this.renderMode.get()).equals(AutoCrystalPlus.RenderMode.Boze);
      })).build());
      this.animationExponent = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Animation Exponent")).description("How fast should blackout mode box grow.")).defaultValue(3.0).min(0.0).sliderRange(0.0, 10.0).visible(() -> {
         return ((RenderMode)this.renderMode.get()).equals(AutoCrystalPlus.RenderMode.Boze);
      })).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts of render should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Line color of rendered boxes")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
      this.color = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Side color of rendered boxes")).defaultValue(new SettingColor(255, 0, 0, 50)).build());
      this.autoMineDamage = this.sgCompatibility.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Auto Mine Damage")).description("Prioritizes placing on automine target block.")).defaultValue(1.1).min(1.0).sliderRange(1.0, 5.0).build());
      this.amPlace = this.sgCompatibility.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Auto Mine Place")).description("Ignores automine block before if actually breaks.")).defaultValue(true)).build());
      var10001 = this.sgCompatibility;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Auto Mine Progress")).description("Ignores the block after it has reached this progress.")).defaultValue(0.95).range(0.0, 1.0).sliderRange(0.0, 1.0);
      var10003 = this.amPlace;
      Objects.requireNonNull(var10003);
      this.amProgress = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgCompatibility;
      BoolSetting.Builder var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Auto Mine Spam")).description("Spams crystals before the block breaks.")).defaultValue(false);
      var10003 = this.amPlace;
      Objects.requireNonNull(var10003);
      this.amSpam = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      this.amBroken = this.sgCompatibility.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Auto Mine Broken")).description("Doesn't place on automine block.")).defaultValue(AutoCrystalPlus.AutoMineBrokenMode.Near)).build());
      this.paAttack = this.sgCompatibility.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Piston Crystal Attack")).description("Doesn't attack the crystal placed by piston crystal.")).defaultValue(true)).build());
      this.paPlace = this.sgCompatibility.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Piston Crystal Placing")).description("Doesn't place crystals when piston crystal is enabled.")).defaultValue(true)).build());
      this.renderExt = this.sgDebug.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Render Extrapolation")).description("Renders boxes at players' predicted positions.")).defaultValue(false)).build());
      this.renderSelfExt = this.sgDebug.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Render Self Extrapolation")).description("Renders box at your predicted position.")).defaultValue(false)).build());
      this.ticksEnabled = 0L;
      this.placeTimer = 0.0;
      this.placeLimitTimer = 0.0;
      this.delayTimer = 0.0;
      this.delayTicks = 0;
      this.placePos = null;
      this.placeDir = null;
      this.expEntity = null;
      this.expEntityBB = null;
      this.attackedList = new TimerList();
      this.existedList = new HashMap();
      this.existedTicksList = new HashMap();
      this.own = new HashMap();
      this.extPos = new HashMap();
      this.extHitbox = new HashMap();
      this.rangePos = null;
      this.blocked = new ArrayList();
      this.earthMap = new HashMap();
      this.attackTimer = 0.0;
      this.switchTimer = 0.0;
      this.confirmed = Integer.MIN_VALUE;
      this.lastMillis = System.currentTimeMillis();
      this.suicide = false;
      this.lastAttack = 0L;
      this.renderTarget = null;
      this.renderPos = null;
      this.renderProgress = 0.0;
      this.autoMine = null;
      this.placed = 0;
      this.cps = 0.0;
      this.infoCps = 0.0;
      this.lastExplosion = 0L;
      this.predicts = new ArrayList();
      this.setDeads = new ArrayList();
   }

   public void onActivate() {
      super.onActivate();
      this.ticksEnabled = 0L;
      this.earthMap.clear();
      this.existedTicksList.clear();
      this.existedList.clear();
      this.blocked.clear();
      this.extPos.clear();
      this.own.clear();
      this.renderPos = null;
      this.renderProgress = 0.0;
      this.lastMillis = System.currentTimeMillis();
      this.attackedList.clear();
      this.lastAttack = 0L;
      this.predicts.clear();
      this.setDeads.clear();
   }

   public String getInfoString() {
      super.getInfoString();
      float var10000 = (float)Math.round(this.infoCps * 10.0);
      return var10000 / 10.0F + " CPS";
   }

   @EventHandler(
      priority = 200
   )
   private void onTickPost(TickEvent.Post event) {
      ++this.delayTicks;
      ++this.ticksEnabled;
      ++this.placed;
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.autoMine == null) {
            this.autoMine = (AutoMine)Modules.get().get(AutoMine.class);
         }

         ExtrapolationUtils.extrapolateMap(this.extPos, (player) -> {
            return player == this.mc.field_1724 ? (Integer)this.selfExt.get() : (Integer)this.extrapolation.get();
         }, (player) -> {
            return (Integer)this.extSmoothness.get();
         });
         ExtrapolationUtils.extrapolateMap(this.extHitbox, (player) -> {
            return (Integer)this.hitboxExtrapolation.get();
         }, (player) -> {
            return (Integer)this.extSmoothness.get();
         });
         class_238 rangeBox = ExtrapolationUtils.extrapolate(this.mc.field_1724, (Integer)this.rangeExtrapolation.get(), (Integer)this.extSmoothness.get());
         if (rangeBox == null) {
            this.rangePos = this.mc.field_1724.method_33571();
         } else {
            this.rangePos = new class_243((rangeBox.field_1323 + rangeBox.field_1320) / 2.0, rangeBox.field_1322 + (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), (rangeBox.field_1321 + rangeBox.field_1324) / 2.0);
         }

         List toRemove = new ArrayList();
         this.existedList.forEach((key, val) -> {
            if ((double)(System.currentTimeMillis() - val) >= 5000.0 + (Double)this.existed.get() * 1000.0) {
               toRemove.add(key);
            }

         });
         Map var10001 = this.existedList;
         Objects.requireNonNull(var10001);
         toRemove.forEach(var10001::remove);
         toRemove.clear();
         this.existedTicksList.forEach((key, val) -> {
            if (this.ticksEnabled - val >= (long)(100 + (Integer)this.existedTicks.get())) {
               toRemove.add(key);
            }

         });
         var10001 = this.existedTicksList;
         Objects.requireNonNull(var10001);
         toRemove.forEach(var10001::remove);
         toRemove.clear();
         this.own.forEach((key, val) -> {
            if (System.currentTimeMillis() - val >= 5000L) {
               toRemove.add(key);
            }

         });
         var10001 = this.own;
         Objects.requireNonNull(var10001);
         toRemove.forEach(var10001::remove);
         if ((Boolean)this.performance.get()) {
            this.updatePlacement();
         }

      }
   }

   @EventHandler(
      priority = 201
   )
   private void onRender3D(Render3DEvent event) {
      this.attackedList.update();
      if (this.autoMine == null) {
         this.autoMine = (AutoMine)Modules.get().get(AutoMine.class);
      }

      this.suicide = Modules.get().isActive(Suicide.class);
      double d = (double)((float)(System.currentTimeMillis() - this.lastMillis) / 1000.0F);
      this.lastMillis = System.currentTimeMillis();
      if (System.currentTimeMillis() - this.lastExplosion > 5000L) {
         this.cps = 0.0;
      }

      this.infoCps = class_3532.method_16436(d / 3.0, this.infoCps, this.cps);
      this.attackedList.update();
      this.attackTimer = Math.max(this.attackTimer - d, 0.0);
      this.placeTimer = Math.max(this.placeTimer - d * this.getSpeed(), 0.0);
      this.placeLimitTimer += d;
      this.delayTimer += d;
      this.switchTimer = Math.max(0.0, this.switchTimer - d);
      this.update();
      this.checkDelayed();
      if ((Boolean)this.render.get()) {
         switch ((RenderMode)this.renderMode.get()) {
            case Boze:
               if (this.placePos != null && !this.isPaused() && this.holdingCheck()) {
                  this.renderProgress = Math.min(1.0, this.renderProgress + d);
                  this.renderTarget = new class_243((double)this.placePos.method_10263(), (double)this.placePos.method_10264(), (double)this.placePos.method_10260());
               } else {
                  this.renderProgress = Math.max(0.0, this.renderProgress - d);
               }

               if (this.renderTarget != null) {
                  this.renderPos = this.smoothMove(this.renderPos, this.renderTarget, d * (Double)this.animationSpeed.get() * 5.0);
               }

               if (this.renderPos != null) {
                  double r = 0.5 - Math.pow(1.0 - this.renderProgress, (Double)this.animationExponent.get()) / 2.0;
                  if (r >= 0.001) {
                     double down = -0.5;
                     double up = -0.5;
                     double width = 0.5;
                     switch ((FadeMode)this.fadeMode.get()) {
                        case Up:
                           up = 0.0;
                           down = -(r * 2.0);
                           break;
                        case Down:
                           up = -1.0 + r * 2.0;
                           down = -1.0;
                           break;
                        case Normal:
                           up = -0.5 + r;
                           down = -0.5 - r;
                           width = r;
                     }

                     class_238 box = new class_238(this.renderPos.method_10216() + 0.5 - width, this.renderPos.method_10214() + down, this.renderPos.method_10215() + 0.5 - width, this.renderPos.method_10216() + 0.5 + width, this.renderPos.method_10214() + up, this.renderPos.method_10215() + 0.5 + width);
                     event.renderer.box(box, new Color(((SettingColor)this.color.get()).r, ((SettingColor)this.color.get()).g, ((SettingColor)this.color.get()).b, Math.round((float)((SettingColor)this.color.get()).a)), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
                  }
               }
               break;
            case Future:
               if (this.placePos != null && !this.isPaused() && this.holdingCheck()) {
                  this.renderPos = new class_243((double)this.placePos.method_10263(), (double)this.placePos.method_10264(), (double)this.placePos.method_10260());
                  this.renderProgress = (Double)this.fadeTime.get() + (Double)this.renderTime.get();
               } else {
                  this.renderProgress = Math.max(0.0, this.renderProgress - d);
               }

               if (this.renderProgress > 0.0 && this.renderPos != null) {
                  event.renderer.box(new class_238(this.renderPos.method_10216(), this.renderPos.method_10214() - 1.0, this.renderPos.method_10215(), this.renderPos.method_10216() + 1.0, this.renderPos.method_10214(), this.renderPos.method_10215() + 1.0), new Color(((SettingColor)this.color.get()).r, ((SettingColor)this.color.get()).g, ((SettingColor)this.color.get()).b, (int)Math.round((double)((SettingColor)this.color.get()).a * Math.min(1.0, this.renderProgress / (Double)this.fadeTime.get()))), new Color(((SettingColor)this.lineColor.get()).r, ((SettingColor)this.lineColor.get()).g, ((SettingColor)this.lineColor.get()).b, (int)Math.round((double)((SettingColor)this.lineColor.get()).a * Math.min(1.0, this.renderProgress / (Double)this.fadeTime.get()))), (ShapeMode)this.shapeMode.get(), 0);
               }
               break;
            case Earthhack:
               List toRemove = new ArrayList();
               Iterator var5 = this.earthMap.entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry entry = (Map.Entry)var5.next();
                  class_2338 pos = (class_2338)entry.getKey();
                  Double[] alpha = (Double[])entry.getValue();
                  if (alpha[0] <= d) {
                     toRemove.add(pos);
                  } else {
                     double r = Math.min(1.0, alpha[0] / alpha[1]) / 2.0;
                     double down = -0.5;
                     double up = -0.5;
                     double width = 0.5;
                     switch ((EarthFadeMode)this.earthFadeMode.get()) {
                        case Normal:
                           up = 1.0;
                           down = 0.0;
                           break;
                        case Up:
                           up = 1.0;
                           down = 1.0 - r * 2.0;
                           break;
                        case Down:
                           up = r * 2.0;
                           down = 0.0;
                           break;
                        case Shrink:
                           up = 0.5 + r;
                           down = 0.5 - r;
                           width = r;
                     }

                     class_238 box = new class_238((double)pos.method_10263() + 0.5 - width, (double)pos.method_10264() + down, (double)pos.method_10260() + 0.5 - width, (double)pos.method_10263() + 0.5 + width, (double)pos.method_10264() + up, (double)pos.method_10260() + 0.5 + width);
                     event.renderer.box(box, new Color(((SettingColor)this.color.get()).r, ((SettingColor)this.color.get()).g, ((SettingColor)this.color.get()).b, (int)Math.round((double)((SettingColor)this.color.get()).a * Math.min(1.0, alpha[0] / alpha[1]))), new Color(((SettingColor)this.lineColor.get()).r, ((SettingColor)this.lineColor.get()).g, ((SettingColor)this.lineColor.get()).b, (int)Math.round((double)((SettingColor)this.lineColor.get()).a * Math.min(1.0, alpha[0] / alpha[1]))), (ShapeMode)this.shapeMode.get(), 0);
                     entry.setValue(new Double[]{alpha[0] - d, alpha[1]});
                  }
               }

               Map var10001 = this.earthMap;
               Objects.requireNonNull(var10001);
               toRemove.forEach(var10001::remove);
         }
      }

      if (this.mc.field_1724 != null && (Boolean)this.renderExt.get()) {
         this.extPos.forEach((name, bb) -> {
            if ((Boolean)this.renderSelfExt.get() || !name.equals(this.mc.field_1724)) {
               event.renderer.box(bb, (Color)this.color.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
            }

         });
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onEntity(EntityAddedEvent event) {
      this.confirmed = event.entity.method_5628();
   }

   @EventHandler(
      priority = 200
   )
   private void onSend(PacketEvent.Send event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (event.packet instanceof class_2868) {
            this.switchTimer = (Double)this.switchPenalty.get();
         }

         class_2596 var3 = event.packet;
         if (var3 instanceof class_2885) {
            class_2885 packet = (class_2885)var3;
            if (packet.method_12546() == class_1268.field_5808) {
               if (!Managers.HOLDING.isHolding(class_1802.field_8301)) {
                  return;
               }
            } else if (this.mc.field_1724.method_6079().method_7909() != class_1802.field_8301) {
               return;
            }

            if (this.isOwn(packet.method_12543().method_17777().method_10084())) {
               this.own.remove(packet.method_12543().method_17777().method_10084());
            }

            this.own.put(packet.method_12543().method_17777().method_10084(), System.currentTimeMillis());
            this.blocked.add(LemonUtils.getCrystalBox(packet.method_12543().method_17777().method_10084()));
            this.addExisted(packet.method_12543().method_17777().method_10084());
         }
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onReceive(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2604 packet) {
         if (packet.method_11169() == class_1299.field_6110) {
            class_243 pos = new class_243(packet.method_11175(), packet.method_11174(), packet.method_11176());
            if (!this.isOwn(pos)) {
               return;
            }

            double delta = (double)(System.currentTimeMillis() - this.lastExplosion);
            this.lastExplosion = System.currentTimeMillis();
            this.cps = 1000.0 / delta;
         }
      }

   }

   private void update() {
      placing = false;
      this.expEntity = null;
      class_1268 hand = this.getHand((stack) -> {
         return stack.method_7909() == class_1802.field_8301;
      });
      class_1268 handToUse = hand;
      if (!(Boolean)this.performance.get()) {
         this.updatePlacement();
      }

      int silentSlot;
      int hotbar;
      switch ((SwitchMode)this.switchMode.get()) {
         case Simple:
            silentSlot = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8301}).slot();
            if (this.placePos != null && hand == null && silentSlot >= 0) {
               InvUtils.swap(silentSlot, false);
               handToUse = class_1268.field_5808;
            }
            break;
         case Gapple:
            silentSlot = InvUtils.findInHotbar(LemonUtils::isGapple).slot();
            if (this.mc.field_1690.field_1904.method_1434() && Managers.HOLDING.isHolding(class_1802.field_8301, class_1802.field_8367, class_1802.field_8463) && silentSlot >= 0) {
               if (this.getHand(LemonUtils::isGapple) == null) {
                  InvUtils.swap(silentSlot, false);
               }

               handToUse = this.getHand((itemStack) -> {
                  return itemStack.method_7909() == class_1802.field_8301;
               });
            } else if (Managers.HOLDING.isHolding(class_1802.field_8301, class_1802.field_8367, class_1802.field_8463)) {
               hotbar = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8301}).slot();
               if (this.placePos != null && hand == null && hotbar >= 0) {
                  InvUtils.swap(hotbar, false);
                  handToUse = class_1268.field_5808;
               }
            }
      }

      if (this.placePos != null && this.placeDir != null && !this.isPaused() && (!(Boolean)this.paPlace.get() || !Modules.get().isActive(PistonCrystal.class))) {
         silentSlot = InvUtils.find((itemStack) -> {
            return itemStack.method_7909() == class_1802.field_8301;
         }).slot();
         hotbar = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8301}).slot();
         if (handToUse != null || this.switchMode.get() == AutoCrystalPlus.SwitchMode.Silent && hotbar >= 0 || (this.switchMode.get() == AutoCrystalPlus.SwitchMode.PickSilent || this.switchMode.get() == AutoCrystalPlus.SwitchMode.InvSilent) && silentSlot >= 0) {
            placing = true;
            if ((!SettingUtils.shouldRotate(RotationType.Interact) || Managers.ROTATION.start(this.placePos.method_10074(), (Boolean)this.smartRot.get() ? new class_243((double)this.placePos.method_10263() + 0.5, (double)this.placePos.method_10264(), (double)this.placePos.method_10260() + 0.5) : null, (double)this.priority, RotationType.Interact, (long)Objects.hash(new Object[]{this.name + "placing"}))) && this.speedCheck() && this.delayCheck()) {
               this.placeCrystal(this.placePos.method_10074(), this.placeDir, handToUse, silentSlot, hotbar);
            }
         }
      }

      PistonCrystal pa = (PistonCrystal)Modules.get().get(PistonCrystal.class);
      double[] value = null;
      if (!this.isPaused() && (hand != null || this.switchMode.get() == AutoCrystalPlus.SwitchMode.Silent || this.switchMode.get() == AutoCrystalPlus.SwitchMode.PickSilent || this.switchMode.get() == AutoCrystalPlus.SwitchMode.InvSilent) && (Boolean)this.explode.get()) {
         Iterator var5 = this.mc.field_1687.method_18112().iterator();

         label146:
         while(true) {
            class_1297 en;
            double[] dmg;
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var5.hasNext()) {
                              break label146;
                           }

                           en = (class_1297)var5.next();
                        } while((Boolean)this.paAttack.get() && pa.isActive() && en.method_24515().equals(pa.crystalPos));
                     } while(!(en instanceof class_1511));
                  } while(this.switchTimer > 0.0);

                  dmg = this.getDmg(en.method_19538(), true)[0];
               } while(!this.canExplode(en.method_19538()));
            } while(this.expEntity != null && value != null && (!((DmgCheckMode)this.dmgCheckMode.get()).equals(AutoCrystalPlus.DmgCheckMode.Normal) || !(dmg[0] > value[0])) && (!((DmgCheckMode)this.dmgCheckMode.get()).equals(AutoCrystalPlus.DmgCheckMode.Safe) || !(dmg[2] / dmg[0] < value[2] / dmg[0])));

            this.expEntity = en;
            value = dmg;
         }
      }

      if (this.expEntity != null && this.multiTaskCheck() && !this.isAttacked(this.expEntity.method_5628()) && this.attackDelayCheck() && this.existedCheck(this.expEntity.method_24515()) && (!SettingUtils.shouldRotate(RotationType.Attacking) || this.startAttackRot())) {
         if (SettingUtils.shouldRotate(RotationType.Attacking)) {
            this.expEntityBB = this.expEntity.method_5829();
         }

         this.explode(this.expEntity.method_5628(), this.expEntity.method_19538());
      }

      if (!this.isAlive(this.expEntityBB) && SettingUtils.shouldRotate(RotationType.Attacking)) {
         Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "attacking"}));
      }

   }

   private boolean attackDelayCheck() {
      if (!(Boolean)this.instantAttack.get()) {
         return (double)System.currentTimeMillis() > (double)this.lastAttack + 1000.0 / (Double)this.expSpeed.get();
      } else {
         return (Double)this.expSpeedLimit.get() <= 0.0 || (double)System.currentTimeMillis() > (double)this.lastAttack + 1000.0 / (Double)this.expSpeedLimit.get();
      }
   }

   private boolean startAttackRot() {
      this.expEntityBB = this.expEntity.method_5829();
      return Managers.ROTATION.start(this.expEntity.method_5829(), (Boolean)this.smartRot.get() ? this.expEntity.method_19538() : null, (double)this.priority + (!this.isAttacked(this.expEntity.method_5628()) && this.blocksPlacePos(this.expEntity) ? -0.1 : 0.1), RotationType.Attacking, (long)Objects.hash(new Object[]{this.name + "attacking"}));
   }

   private boolean blocksPlacePos(class_1297 entity) {
      return this.placePos != null && entity.method_5829().method_994(new class_238((double)this.placePos.method_10263(), (double)this.placePos.method_10264(), (double)this.placePos.method_10260(), (double)(this.placePos.method_10263() + 1), (double)(this.placePos.method_10264() + (SettingUtils.cc() ? 1 : 2)), (double)(this.placePos.method_10260() + 1)));
   }

   private boolean isAlive(class_238 box) {
      if (box == null) {
         return true;
      } else {
         Iterator var2 = this.mc.field_1687.method_18112().iterator();

         class_1297 en;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            en = (class_1297)var2.next();
         } while(!(en instanceof class_1511) || !this.bbEquals(box, en.method_5829()));

         return true;
      }
   }

   private boolean bbEquals(class_238 box1, class_238 box2) {
      return box1.field_1323 == box2.field_1323 && box1.field_1322 == box2.field_1322 && box1.field_1321 == box2.field_1321 && box1.field_1320 == box2.field_1320 && box1.field_1325 == box2.field_1325 && box1.field_1324 == box2.field_1324;
   }

   private boolean speedCheck() {
      if ((Double)this.speedLimit.get() > 0.0 && this.placeLimitTimer < 1.0 / (Double)this.speedLimit.get()) {
         return false;
      } else if ((Boolean)this.instantPlace.get() && !this.shouldSlow() && !this.isBlocked(this.placePos)) {
         return true;
      } else {
         return this.placeTimer <= 0.0;
      }
   }

   private boolean holdingCheck() {
      switch ((SwitchMode)this.switchMode.get()) {
         case Silent:
            return InvUtils.findInHotbar(new class_1792[]{class_1802.field_8301}).slot() >= 0;
         case PickSilent:
         case InvSilent:
            return InvUtils.find(new class_1792[]{class_1802.field_8301}).slot() >= 0;
         default:
            return this.getHand((itemStack) -> {
               return itemStack.method_7909() == class_1802.field_8301;
            }) != null;
      }
   }

   private void updatePlacement() {
      if (!(Boolean)this.place.get()) {
         this.placePos = null;
         this.placeDir = null;
      } else {
         this.placePos = this.getPlacePos();
      }
   }

   private void placeCrystal(class_2338 pos, class_2350 dir, class_1268 handToUse, int sl, int hsl) {
      if (pos != null && this.mc.field_1724 != null) {
         if (((RenderMode)this.renderMode.get()).equals(AutoCrystalPlus.RenderMode.Earthhack)) {
            if (!this.earthMap.containsKey(pos)) {
               this.earthMap.put(pos, new Double[]{(Double)this.fadeTime.get() + (Double)this.renderTime.get(), (Double)this.fadeTime.get()});
            } else {
               this.earthMap.replace(pos, new Double[]{(Double)this.fadeTime.get() + (Double)this.renderTime.get(), (Double)this.fadeTime.get()});
            }
         }

         this.blocked.add(new class_238((double)pos.method_10263() - 0.5, (double)(pos.method_10264() + 1), (double)pos.method_10260() - 0.5, (double)pos.method_10263() + 1.5, (double)(pos.method_10264() + 2), (double)pos.method_10260() + 1.5));
         boolean switched = handToUse == null;
         if (switched) {
            switch ((SwitchMode)this.switchMode.get()) {
               case Silent:
                  InvUtils.swap(hsl, true);
                  break;
               case PickSilent:
                  InventoryUtils.pickSwitch(sl);
                  break;
               case InvSilent:
                  InventoryUtils.invSwitch(sl);
            }
         }

         this.addExisted(pos.method_10084());
         if (!this.isOwn(pos.method_10084())) {
            this.own.put(pos.method_10084(), System.currentTimeMillis());
         } else {
            this.own.remove(pos.method_10084());
            this.own.put(pos.method_10084(), System.currentTimeMillis());
         }

         this.placeLimitTimer = 0.0;
         this.placeTimer = 1.0;
         this.placed = 0;
         this.interactBlock(switched ? class_1268.field_5808 : handToUse, pos.method_46558(), dir, pos);
         if ((Boolean)this.placeSwing.get()) {
            this.clientSwing((SwingHand)this.placeHand.get(), switched ? class_1268.field_5808 : handToUse);
         }

         if (SettingUtils.shouldRotate(RotationType.Interact)) {
            Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
         }

         if (switched) {
            switch ((SwitchMode)this.switchMode.get()) {
               case Silent:
                  InvUtils.swapBack();
                  break;
               case PickSilent:
                  InventoryUtils.pickSwapBack();
                  break;
               case InvSilent:
                  InventoryUtils.swapBack();
            }
         }

         if ((Boolean)this.idPredict.get()) {
            int highest = this.getHighest();
            int id = highest + (Integer)this.idStartOffset.get();

            for(int i = 0; i < (Integer)this.idPackets.get() * (Integer)this.idOffset.get(); i += (Integer)this.idOffset.get()) {
               this.addPredict(id + i, new class_243((double)pos.method_10263() + 0.5, (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.5), (Double)this.idDelay.get() + (Double)this.idPacketDelay.get() * (double)i);
            }
         }
      }

   }

   private boolean delayCheck() {
      if (this.placeDelayMode.get() == AutoCrystalPlus.DelayMode.Seconds) {
         return this.delayTimer >= (Double)this.placeDelay.get();
      } else {
         return this.delayTicks >= (Integer)this.placeDelayTicks.get();
      }
   }

   private boolean multiTaskCheck() {
      return this.placed >= ((SequentialMode)this.sequential.get()).ticks;
   }

   private int getHighest() {
      int highest = this.confirmed;
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(var2.hasNext()) {
         class_1297 entity = (class_1297)var2.next();
         if (entity.method_5628() > highest) {
            highest = entity.method_5628();
         }
      }

      if (highest > this.confirmed) {
         this.confirmed = highest;
      }

      return highest;
   }

   private boolean isBlocked(class_2338 pos) {
      class_238 box = new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 2), (double)(pos.method_10260() + 1));
      Iterator var3 = this.blocked.iterator();

      class_238 bb;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         bb = (class_238)var3.next();
      } while(!bb.method_994(box));

      return true;
   }

   private boolean isAttacked(int id) {
      return this.attackedList.contains(id);
   }

   private void explode(int id, class_243 vec) {
      this.attackEntity(id, LemonUtils.getCrystalBox(vec), vec);
   }

   private void attackEntity(int id, class_238 bb, class_243 vec) {
      if (this.mc.field_1724 != null) {
         this.lastAttack = System.currentTimeMillis();
         this.attackedList.add(id, 1.0 / (Double)this.expSpeed.get());
         this.delayTimer = 0.0;
         this.delayTicks = 0;
         this.removeExisted(class_2338.method_49638(vec));
         SettingUtils.registerAttack(bb);
         class_2824 packet = class_2824.method_34206(this.mc.field_1724, this.mc.field_1724.method_5715());
         ((IInteractEntityC2SPacket)packet).setId(id);
         SettingUtils.swing(SwingState.Pre, SwingType.Attacking, class_1268.field_5808);
         this.sendPacket(packet);
         SettingUtils.swing(SwingState.Post, SwingType.Attacking, class_1268.field_5808);
         if ((Boolean)this.attackSwing.get()) {
            this.clientSwing((SwingHand)this.attackHand.get(), class_1268.field_5808);
         }

         this.blocked.clear();
         if ((Boolean)this.setDead.get()) {
            class_1297 entity = this.mc.field_1687.method_8469(id);
            if (entity == null) {
               return;
            }

            this.addSetDead(entity, (Double)this.setDeadDelay.get());
         }
      }

   }

   private boolean existedCheck(class_2338 pos) {
      if (this.existedMode.get() == AutoCrystalPlus.DelayMode.Seconds) {
         return !this.existedList.containsKey(pos) || (double)System.currentTimeMillis() > (double)(Long)this.existedList.get(pos) + (Double)this.existed.get() * 1000.0;
      } else {
         return !this.existedTicksList.containsKey(pos) || this.ticksEnabled >= (Long)this.existedTicksList.get(pos) + (long)(Integer)this.existedTicks.get();
      }
   }

   private void addExisted(class_2338 pos) {
      if (this.existedMode.get() == AutoCrystalPlus.DelayMode.Seconds) {
         if (!this.existedList.containsKey(pos)) {
            this.existedList.put(pos, System.currentTimeMillis());
         }
      } else if (!this.existedTicksList.containsKey(pos)) {
         this.existedTicksList.put(pos, this.ticksEnabled);
      }

   }

   private void removeExisted(class_2338 pos) {
      if (this.existedMode.get() == AutoCrystalPlus.DelayMode.Seconds) {
         if (this.existedList.containsKey(pos)) {
            this.existedList.remove(pos);
         }
      } else if (this.existedTicksList.containsKey(pos)) {
         this.existedTicksList.remove(pos);
      }

   }

   private boolean canExplode(class_243 vec) {
      if ((Boolean)this.onlyOwn.get() && !this.isOwn(vec)) {
         return false;
      } else if (!this.inExplodeRange(vec)) {
         return false;
      } else {
         double[][] result = this.getDmg(vec, true);
         return this.explodeDamageCheck(result[0], result[1], this.isOwn(vec));
      }
   }

   private boolean canExplodePlacing(class_243 vec) {
      if ((Boolean)this.onlyOwn.get() && !this.isOwn(vec)) {
         return false;
      } else if (!this.inExplodeRangePlacing(vec)) {
         return false;
      } else {
         double[][] result = this.getDmg(vec, false);
         return this.explodeDamageCheck(result[0], result[1], this.isOwn(vec));
      }
   }

   private class_1268 getHand(Predicate predicate) {
      return predicate.test(Managers.HOLDING.getStack()) ? class_1268.field_5808 : (predicate.test(this.mc.field_1724.method_6079()) ? class_1268.field_5810 : null);
   }

   private boolean isPaused() {
      return (Boolean)this.pauseEat.get() && this.mc.field_1724.method_6115();
   }

   private void setEntityDead(class_1297 en) {
      this.mc.field_1687.method_2945(en.method_5628(), class_5529.field_26998);
   }

   private class_2338 getPlacePos() {
      int r = (int)Math.ceil(Math.max(SettingUtils.getPlaceRange(), SettingUtils.getPlaceWallsRange()));
      class_2338 bestPos = null;
      class_2350 bestDir = null;
      double[] highest = null;
      class_2338 pPos = class_2338.method_49638(this.mc.field_1724.method_33571());

      for(int x = -r; x <= r; ++x) {
         for(int y = -r; y <= r; ++y) {
            for(int z = -r; z <= r; ++z) {
               class_2338 pos = pPos.method_10069(x, y, z);
               if (this.air(pos) && (!SettingUtils.oldCrystals() || this.air(pos.method_10084())) && this.crystalBlock(pos.method_10074()) && !this.blockBroken(pos.method_10074())) {
                  class_2350 dir = SettingUtils.getPlaceOnDirection(pos.method_10074());
                  if (dir != null && this.inPlaceRange(pos.method_10074()) && this.inExplodeRangePlacing(new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264(), (double)pos.method_10260() + 0.5))) {
                     double[][] result = this.getDmg(new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264(), (double)pos.method_10260() + 0.5), false);
                     if (this.placeDamageCheck(result[0], result[1], highest)) {
                        class_238 box = new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + (SettingUtils.cc() ? 1 : 2)), (double)(pos.method_10260() + 1));
                        if (!LemonEntityUtils.intersectsWithEntity(box, this::validForIntersect, this.extHitbox)) {
                           bestDir = dir;
                           bestPos = pos;
                           highest = result[0];
                        }
                     }
                  }
               }
            }
         }
      }

      this.placeDir = bestDir;
      return bestPos;
   }

   private boolean placeDamageCheck(double[] dmg, double[] health, double[] highest) {
      if (highest != null) {
         if (((DmgCheckMode)this.dmgCheckMode.get()).equals(AutoCrystalPlus.DmgCheckMode.Normal) && dmg[0] < highest[0]) {
            return false;
         }

         if (((DmgCheckMode)this.dmgCheckMode.get()).equals(AutoCrystalPlus.DmgCheckMode.Safe) && dmg[2] / dmg[0] > highest[2] / highest[0]) {
            return false;
         }
      }

      double playerHP = (double)(this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067());
      if (playerHP >= 0.0 && dmg[2] * (Double)this.antiSelfPop.get() >= playerHP) {
         return false;
      } else if (health[1] >= 0.0 && dmg[1] * (Double)this.antiFriendPop.get() >= health[1]) {
         return false;
      } else if (health[0] >= 0.0 && dmg[0] * (Double)this.forcePop.get() >= health[0]) {
         return true;
      } else if (dmg[0] < (Double)this.minPlace.get()) {
         return false;
      } else if (dmg[1] > (Double)this.maxFriendPlace.get()) {
         return false;
      } else if (dmg[1] >= 0.0 && dmg[0] / dmg[1] < (Double)this.minFriendPlaceRatio.get()) {
         return false;
      } else if (dmg[2] > (Double)this.maxPlace.get()) {
         return false;
      } else {
         return !(dmg[2] >= 0.0) || !(dmg[0] / dmg[2] < (Double)this.minPlaceRatio.get());
      }
   }

   private boolean explodeDamageCheck(double[] dmg, double[] health, boolean own) {
      boolean checkOwn = this.expMode.get() == AutoCrystalPlus.ExplodeMode.FullCheck || this.expMode.get() == AutoCrystalPlus.ExplodeMode.SelfDmgCheck || this.expMode.get() == AutoCrystalPlus.ExplodeMode.SelfDmgOwn || this.expMode.get() == AutoCrystalPlus.ExplodeMode.AlwaysOwn;
      boolean checkDmg = this.expMode.get() == AutoCrystalPlus.ExplodeMode.FullCheck || this.expMode.get() == AutoCrystalPlus.ExplodeMode.SelfDmgOwn && !own || this.expMode.get() == AutoCrystalPlus.ExplodeMode.AlwaysOwn && !own;
      double playerHP = (double)(this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067());
      if (checkOwn) {
         if (playerHP >= 0.0 && dmg[2] * (Double)this.forcePop.get() >= playerHP) {
            return false;
         }

         if (health[1] >= 0.0 && dmg[1] * (Double)this.antiFriendPop.get() >= health[1]) {
            return false;
         }
      }

      if (checkDmg && health[0] >= 0.0 && dmg[0] * (Double)this.forcePop.get() >= health[0]) {
         return true;
      } else {
         if (checkDmg) {
            if (dmg[0] < (Double)this.minExplode.get()) {
               return false;
            }

            if (dmg[1] >= 0.0 && dmg[0] / dmg[1] < (Double)this.minFriendExpRatio.get()) {
               return false;
            }

            if (dmg[2] >= 0.0 && dmg[0] / dmg[2] < (Double)this.minExpRatio.get()) {
               return false;
            }
         }

         if (checkOwn) {
            if (dmg[1] > (Double)this.maxFriendExp.get()) {
               return false;
            }

            if (dmg[2] > (Double)this.maxExp.get()) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isOwn(class_243 vec) {
      return this.isOwn(class_2338.method_49638(vec));
   }

   private boolean isOwn(class_2338 pos) {
      Iterator var2 = this.own.entrySet().iterator();

      Map.Entry entry;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entry = (Map.Entry)var2.next();
      } while(!((class_2338)entry.getKey()).equals(pos));

      return true;
   }

   private double[][] getDmg(class_243 vec, boolean attack) {
      double self = DamageInfo.crystal(this.mc.field_1724, this.extPos.containsKey(this.mc.field_1724) ? (class_238)this.extPos.get(this.mc.field_1724) : this.mc.field_1724.method_5829(), vec, this.ignorePos(attack), (Boolean)this.ignoreTerrain.get());
      if (this.suicide) {
         return new double[][]{{self, -1.0, -1.0}, {20.0, 20.0}};
      } else {
         double highestEnemy = -1.0;
         double highestFriend = -1.0;
         double enemyHP = -1.0;
         double friendHP = -1.0;
         Iterator var13 = this.extPos.entrySet().iterator();

         while(var13.hasNext()) {
            Map.Entry entry = (Map.Entry)var13.next();
            class_742 player = (class_742)entry.getKey();
            class_238 box = (class_238)entry.getValue();
            if (!(player.method_6032() <= 0.0F) && player != this.mc.field_1724) {
               double dmg = DamageInfo.crystal(player, box, vec, this.ignorePos(attack), (Boolean)this.ignoreTerrain.get());
               if (class_2338.method_49638(vec).method_10074().equals(this.autoMine.targetPos())) {
                  dmg *= (Double)this.autoMineDamage.get();
               }

               double hp = (double)(player.method_6032() + player.method_6067());
               if (Friends.get().isFriend(player)) {
                  if (dmg > highestFriend) {
                     highestFriend = dmg;
                     friendHP = hp;
                  }
               } else if (dmg > highestEnemy) {
                  highestEnemy = dmg;
                  enemyHP = hp;
               }
            }
         }

         return new double[][]{{highestEnemy, highestFriend, self}, {enemyHP, friendHP}};
      }
   }

   private boolean air(class_2338 pos) {
      return this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2189;
   }

   private boolean crystalBlock(class_2338 pos) {
      return this.mc.field_1687.method_8320(pos).method_26204().equals(class_2246.field_10540) || this.mc.field_1687.method_8320(pos).method_26204().equals(class_2246.field_9987);
   }

   private boolean inPlaceRange(class_2338 pos) {
      return SettingUtils.inPlaceRange(pos);
   }

   private boolean inExplodeRangePlacing(class_243 vec) {
      return SettingUtils.inAttackRange(new class_238(vec.method_10216() - 1.0, vec.method_10214(), vec.method_10215() - 1.0, vec.method_10216() + 1.0, vec.method_10214() + 2.0, vec.method_10215() + 1.0), this.rangePos != null ? this.rangePos : null);
   }

   private boolean inExplodeRange(class_243 vec) {
      return SettingUtils.inAttackRange(new class_238(vec.method_10216() - 1.0, vec.method_10214(), vec.method_10215() - 1.0, vec.method_10216() + 1.0, vec.method_10214() + 2.0, vec.method_10215() + 1.0));
   }

   private double getSpeed() {
      return this.shouldSlow() ? (Double)this.slowSpeed.get() : (Double)this.placeSpeed.get();
   }

   private boolean shouldSlow() {
      return this.placePos != null && this.getDmg(new class_243((double)this.placePos.method_10263() + 0.5, (double)this.placePos.method_10264(), (double)this.placePos.method_10260() + 0.5), false)[0][0] <= (Double)this.slowDamage.get();
   }

   private class_243 smoothMove(class_243 current, class_243 target, double delta) {
      if (current == null) {
         return target;
      } else {
         double absX = Math.abs(current.field_1352 - target.field_1352);
         double absY = Math.abs(current.field_1351 - target.field_1351);
         double absZ = Math.abs(current.field_1350 - target.field_1350);
         double x = (absX + Math.pow(absX, (Double)this.animationMoveExponent.get() - 1.0)) * delta;
         double y = (absX + Math.pow(absY, (Double)this.animationMoveExponent.get() - 1.0)) * delta;
         double z = (absX + Math.pow(absZ, (Double)this.animationMoveExponent.get() - 1.0)) * delta;
         return new class_243(current.field_1352 > target.field_1352 ? Math.max(target.field_1352, current.field_1352 - x) : Math.min(target.field_1352, current.field_1352 + x), current.field_1351 > target.field_1351 ? Math.max(target.field_1351, current.field_1351 - y) : Math.min(target.field_1351, current.field_1351 + y), current.field_1350 > target.field_1350 ? Math.max(target.field_1350, current.field_1350 - z) : Math.min(target.field_1350, current.field_1350 + z));
      }
   }

   private boolean validForIntersect(class_1297 entity) {
      if (entity instanceof class_1511 && this.canExplodePlacing(entity.method_19538())) {
         return false;
      } else {
         return !(entity instanceof class_1657) || !entity.method_7325();
      }
   }

   private class_2338 ignorePos(boolean attack) {
      if (!(Boolean)this.amPlace.get()) {
         return null;
      } else if (!(Boolean)this.amSpam.get() && attack) {
         return null;
      } else if (this.autoMine != null && this.autoMine.isActive()) {
         if (this.autoMine.targetPos() == null) {
            return null;
         } else {
            return this.autoMine.getMineProgress() > (Double)this.amProgress.get() ? this.autoMine.targetPos() : null;
         }
      } else {
         return null;
      }
   }

   private boolean blockBroken(class_2338 pos) {
      if (!(Boolean)this.amPlace.get()) {
         return false;
      } else if (this.autoMine != null && this.autoMine.isActive()) {
         if (this.autoMine.targetPos() == null) {
            return false;
         } else if (!this.autoMine.targetPos().equals(pos)) {
            return false;
         } else {
            double progress = this.autoMine.getMineProgress();
            if (progress >= 1.0 && !((AutoMineBrokenMode)this.amBroken.get()).broken) {
               return true;
            } else if (progress >= (Double)this.amProgress.get() && !((AutoMineBrokenMode)this.amBroken.get()).near) {
               return true;
            } else {
               return progress < (Double)this.amProgress.get() && !((AutoMineBrokenMode)this.amBroken.get()).normal;
            }
         }
      } else {
         return false;
      }
   }

   private void addPredict(int id, class_243 pos, double delay) {
      this.predicts.add(new Predict(id, pos, Math.round((double)System.currentTimeMillis() + delay * 1000.0)));
   }

   private void addSetDead(class_1297 entity, double delay) {
      this.setDeads.add(new SetDead(entity, Math.round((double)System.currentTimeMillis() + delay * 1000.0)));
   }

   private void checkDelayed() {
      List toRemove = new ArrayList();
      Iterator var2 = this.predicts.iterator();

      while(var2.hasNext()) {
         Predict p = (Predict)var2.next();
         if (System.currentTimeMillis() >= p.time) {
            this.explode(p.id, p.pos);
            toRemove.add(p);
         }
      }

      List var10001 = this.predicts;
      Objects.requireNonNull(var10001);
      toRemove.forEach(var10001::remove);
      List toRemove2 = new ArrayList();
      Iterator var6 = this.setDeads.iterator();

      while(var6.hasNext()) {
         SetDead p = (SetDead)var6.next();
         if (System.currentTimeMillis() >= p.time) {
            this.setEntityDead(p.entity);
            toRemove2.add(p);
         }
      }

      var10001 = this.setDeads;
      Objects.requireNonNull(var10001);
      toRemove2.forEach(var10001::remove);
   }

   public static enum DelayMode {
      Seconds,
      Ticks;

      // $FF: synthetic method
      private static DelayMode[] $values() {
         return new DelayMode[]{Seconds, Ticks};
      }
   }

   public static enum SequentialMode {
      Disabled(0),
      Weak(1),
      Strong(2),
      Strict(3);

      public final int ticks;

      private SequentialMode(int ticks) {
         this.ticks = ticks;
      }

      // $FF: synthetic method
      private static SequentialMode[] $values() {
         return new SequentialMode[]{Disabled, Weak, Strong, Strict};
      }
   }

   public static enum SwitchMode {
      Disabled,
      Simple,
      Gapple,
      Silent,
      InvSilent,
      PickSilent;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Disabled, Simple, Gapple, Silent, InvSilent, PickSilent};
      }
   }

   public static enum DmgCheckMode {
      Normal,
      Safe;

      // $FF: synthetic method
      private static DmgCheckMode[] $values() {
         return new DmgCheckMode[]{Normal, Safe};
      }
   }

   public static enum ExplodeMode {
      FullCheck,
      SelfDmgCheck,
      SelfDmgOwn,
      AlwaysOwn,
      Always;

      // $FF: synthetic method
      private static ExplodeMode[] $values() {
         return new ExplodeMode[]{FullCheck, SelfDmgCheck, SelfDmgOwn, AlwaysOwn, Always};
      }
   }

   public static enum RenderMode {
      Boze,
      Future,
      Earthhack;

      // $FF: synthetic method
      private static RenderMode[] $values() {
         return new RenderMode[]{Boze, Future, Earthhack};
      }
   }

   public static enum FadeMode {
      Up,
      Down,
      Normal;

      // $FF: synthetic method
      private static FadeMode[] $values() {
         return new FadeMode[]{Up, Down, Normal};
      }
   }

   public static enum EarthFadeMode {
      Normal,
      Up,
      Down,
      Shrink;

      // $FF: synthetic method
      private static EarthFadeMode[] $values() {
         return new EarthFadeMode[]{Normal, Up, Down, Shrink};
      }
   }

   public static enum AutoMineBrokenMode {
      Near(true, false, false),
      Broken(true, true, false),
      Never(false, false, false),
      Always(true, true, true);

      public final boolean normal;
      public final boolean near;
      public final boolean broken;

      private AutoMineBrokenMode(boolean normal, boolean near, boolean broken) {
         this.normal = normal;
         this.near = near;
         this.broken = broken;
      }

      // $FF: synthetic method
      private static AutoMineBrokenMode[] $values() {
         return new AutoMineBrokenMode[]{Near, Broken, Never, Always};
      }
   }

   private static record Predict(int id, class_243 pos, long time) {
      private Predict(int id, class_243 pos, long time) {
         this.id = id;
         this.pos = pos;
         this.time = time;
      }

      public int id() {
         return this.id;
      }

      public class_243 pos() {
         return this.pos;
      }

      public long time() {
         return this.time;
      }
   }

   private static record SetDead(class_1297 entity, long time) {
      private SetDead(class_1297 entity, long time) {
         this.entity = entity;
         this.time = time;
      }

      public class_1297 entity() {
         return this.entity;
      }

      public long time() {
         return this.time;
      }
   }
}
