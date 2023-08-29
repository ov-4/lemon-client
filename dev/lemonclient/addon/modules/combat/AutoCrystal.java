package dev.lemonclient.addon.modules.combat;

import com.google.common.util.concurrent.AtomicDouble;
import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.player.CrystalUtils;
import dev.lemonclient.addon.utils.player.Interaction;
import dev.lemonclient.addon.utils.render.RenderInfo;
import dev.lemonclient.addon.utils.render.RenderUtils;
import dev.lemonclient.addon.utils.world.BlockInfo;
import dev.lemonclient.addon.utils.world.PredictionUtils;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.EntityVelocityUpdateS2CPacketAccessor;
import meteordevelopment.meteorclient.mixininterface.IBox;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1794;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1831;
import net.minecraft.class_1832;
import net.minecraft.class_1834;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2743;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2868;
import net.minecraft.class_2885;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.joml.Vector3d;

public class AutoCrystal extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlace;
   private final SettingGroup sgBreak;
   private final SettingGroup sgDamage;
   private final SettingGroup sgPredict;
   private final SettingGroup sgFacePlace;
   private final SettingGroup sgMisc;
   private final SettingGroup sgPause;
   private final SettingGroup sgRender;
   private final Setting targetRange;
   private final Setting predictCrystal;
   public final Setting ignoreTerrain;
   private final Setting autoSwitch;
   private final Setting rotate;
   private final Setting yawStepMode;
   private final Setting yawSteps;
   private final Setting doPlace;
   private final Setting placeDelay;
   private final Setting placeRange;
   private final Setting placeWallsRange;
   private final Setting placement;
   private final Setting support;
   private final Setting supportDelay;
   private final Setting doBreak;
   public final Setting breakDelay;
   private final Setting switchDelay;
   private final Setting breakRange;
   private final Setting breakWallsRange;
   private final Setting onlyBreakOwn;
   private final Setting breakAttempts;
   private final Setting cancelCrystal;
   private final Setting crystalAge;
   private final Setting attackFrequency;
   private final Setting fastBreak;
   private final Setting antiWeakness;
   public final Setting ignoreBreakDmg;
   public final Setting minDmg;
   public final Setting maxDmg;
   public final Setting antiFriendPop;
   public final Setting maxFriendDmg;
   public final Setting dmgToPlayer;
   public final Setting predict;
   public final Setting predictIncrease;
   public final Setting predictCollision;
   public final Setting facePlace;
   public final Setting facePlaceHealth;
   public final Setting armorDurability;
   public final Setting facePlaceHurt;
   public final Setting facePlaceArmor;
   public final Setting forceFacePlace;
   public final Setting surroundBreak;
   public final Setting antiSelf;
   public final Setting surroundHold;
   public final Setting extraPlaces;
   private final Setting eatPause;
   private final Setting drinkPause;
   private final Setting minePause;
   public final Setting swing;
   public final Setting packetSwing;
   private final Setting renderMode;
   private final Setting smoothness;
   public final Setting height;
   private final Setting fade;
   public final Setting fadeTime;
   public final Setting fadeAmount;
   private final Setting renderBreak;
   private final Setting renderBreakTime;
   private final Setting shapeMode;
   private final Setting sideColor;
   private final Setting lineColor;
   private final Setting renderTime;
   private final Setting damageText;
   private final Setting damageTextScale;
   private int breakTimer;
   private int placeTimer;
   private int switchTimer;
   private int ticksPassed;
   public final List targets;
   private final class_243 vec3d;
   private final class_243 playerEyePos;
   private final Vector3d vec3;
   private final class_2338.class_2339 blockPos;
   private final class_238 box;
   private final class_243 vec3dRayTraceEnd;
   private class_3959 raycastContext;
   private final IntSet placedCrystals;
   private boolean placing;
   private int placingTimer;
   private final class_2338.class_2339 placingCrystalBlockPos;
   private final IntSet removed;
   private final Int2IntMap attemptedBreaks;
   private final Int2IntMap waitingToExplode;
   private int attacks;
   private double serverYaw;
   public static float ticksBehind;
   public class_1657 bestTarget;
   private double bestTargetDamage;
   private int bestTargetTimer;
   public class_1511 sbCrystal;
   private boolean didRotateThisTick;
   private boolean isLastRotationPos;
   private final class_243 lastRotationPos;
   private double lastYaw;
   private double lastPitch;
   private int lastRotationTimer;
   private int lastEntityId;
   private int last;
   private int renderTimer;
   private int breakRenderTimer;
   public final class_2338.class_2339 renderPos;
   private final class_2338.class_2339 breakRenderPos;
   private class_238 renderBoxOne;
   private class_238 renderBoxTwo;
   private double renderDamage;
   private final Pool renderBlockPool;
   private final List renderBlocks;
   private final Pool renderBreakBlockPool;
   private final List renderBreakBlocks;
   private final int[] second;
   private static int cps;
   private int tick;
   private int i;
   private int lastSpawned;

   public AutoCrystal() {
      super(LemonClient.Combat, "Auto Crystal", "Automatically place and attack crystals in less strict anti-cheat servers.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlace = this.settings.createGroup("Place");
      this.sgBreak = this.settings.createGroup("Break");
      this.sgDamage = this.settings.createGroup("Damage");
      this.sgPredict = this.settings.createGroup("Predict");
      this.sgFacePlace = this.settings.createGroup("Face Place");
      this.sgMisc = this.settings.createGroup("Misc");
      this.sgPause = this.settings.createGroup("Pause");
      this.sgRender = this.settings.createGroup("Render");
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("Range in which to target players.")).defaultValue(10.0).min(0.0).sliderMax(16.0).build());
      this.predictCrystal = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-crystal")).description("Predicts crystal position.")).defaultValue(false)).build());
      this.ignoreTerrain = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-terrain")).description("Completely ignores terrain if it can be blown up by crystals.")).defaultValue(true)).build());
      this.autoSwitch = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("auto-switch")).description("Switches to crystals in your hotbar once a target is found.")).defaultValue(AutoCrystal.AutoSwitchMode.Normal)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates server-side towards the crystals being hit/placed.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("yaw-steps-mode")).description("When to run the yaw steps check.")).defaultValue(AutoCrystal.YawStepMode.Break);
      Setting var10003 = this.rotate;
      Objects.requireNonNull(var10003);
      this.yawStepMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      DoubleSetting.Builder var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("yaw-steps")).description("Maximum number of degrees its allowed to rotate in one tick.")).defaultValue(180.0).range(1.0, 180.0);
      var10003 = this.rotate;
      Objects.requireNonNull(var10003);
      this.yawSteps = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      this.doPlace = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place")).description("If the CA should place crystals.")).defaultValue(true)).build());
      this.placeDelay = this.sgPlace.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The delay in ticks to wait to place a crystal after it's exploded.")).defaultValue(0)).min(0).sliderMax(20).build());
      this.placeRange = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("Range in which to place crystals.")).defaultValue(4.5).min(0.0).sliderMax(6.0).build());
      this.placeWallsRange = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-walls-range")).description("Range in which to place crystals when behind blocks.")).defaultValue(4.5).min(0.0).sliderMax(6.0).build());
      this.placement = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("1.12-placement")).description("Uses 1.12 crystal placement.")).defaultValue(false)).build());
      this.support = this.sgPlace.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("support")).description("Places a support block in air if no other position have been found.")).defaultValue(AutoCrystal.SupportMode.Disabled)).build());
      this.supportDelay = this.sgPlace.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("support-delay")).description("Delay in ticks after placing support block.")).defaultValue(1)).min(0).visible(() -> {
         return this.support.get() != AutoCrystal.SupportMode.Disabled;
      })).build());
      this.doBreak = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break")).description("If the CA should break crystals.")).defaultValue(true)).build());
      this.breakDelay = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-delay")).description("The delay in ticks to wait to break a crystal after it's placed.")).defaultValue(0)).min(0).sliderMax(20).build());
      this.switchDelay = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("switch-delay")).description("The delay in ticks to wait to break a crystal after switching hotbar slot.")).defaultValue(0)).min(0).build());
      this.breakRange = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-range")).description("Range in which to break crystals.")).defaultValue(4.5).min(0.0).sliderMax(6.0).build());
      this.breakWallsRange = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-walls-range")).description("Range in which to break crystals when behind blocks.")).defaultValue(4.5).min(0.0).sliderMax(6.0).build());
      this.onlyBreakOwn = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-own")).description("Only breaks own crystals.")).defaultValue(false)).build());
      this.breakAttempts = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-attempts")).description("How many times to hit a crystal before stopping to target it.")).defaultValue(2)).sliderMin(1).sliderMax(5).build());
      this.cancelCrystal = this.sgBreak.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("cancel-crystal")).description("The way for removing crystals.")).defaultValue(AutoCrystal.CancelCrystal.NoDesync)).build());
      this.crystalAge = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("crystal-age")).description("Amount of ticks a crystal needs to have lived for it to be attacked by AutoCrystal.")).defaultValue(0)).min(0).build());
      this.attackFrequency = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("attack-frequency")).description("Maximum hits to do per second.")).defaultValue(25)).min(1).sliderRange(1, 30).build());
      this.fastBreak = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fast-break")).description("Ignores break delay and tries to break the crystal as soon as it's spawned in the world.")).defaultValue(true)).build());
      this.antiWeakness = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-weakness")).description("Switches to tools with high enough damage to explode the crystal with weakness effect.")).defaultValue(true)).build());
      this.ignoreBreakDmg = this.sgDamage.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-break-dmg")).description("Ignores break damage, useful if crystals didn't breaks on move.")).defaultValue(false)).build());
      this.minDmg = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-dmg")).description("Minimum damage the crystal needs to deal to your target.")).defaultValue(8.5).min(0.0).build());
      this.maxDmg = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-dmg")).description("Maximum damage crystals can deal to yourself.")).defaultValue(6.0).range(0.0, 36.0).sliderMax(36.0).build());
      this.antiFriendPop = this.sgDamage.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-friend-pop")).description("Prevents from popping friends.")).defaultValue(false)).build());
      var10001 = this.sgDamage;
      var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-dmg")).description("Maximum damage crystals can deal to your friends.")).defaultValue(6.0).range(0.0, 36.0).sliderMax(36.0);
      var10003 = this.antiFriendPop;
      Objects.requireNonNull(var10003);
      this.maxFriendDmg = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      this.dmgToPlayer = this.sgBreak.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("dmg-to-player")).description("Will not place and break crystals if they will kill you.")).defaultValue(AutoCrystal.DamageToPlayer.AntiSuicide)).build());
      this.predict = this.sgPredict.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-position")).description("Predicts target position.")).defaultValue(true)).build());
      var10001 = this.sgPredict;
      IntSetting.Builder var2 = (IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("predict-increase")).description("Increasing range from predicted position to target.")).defaultValue(2);
      var10003 = this.predict;
      Objects.requireNonNull(var10003);
      this.predictIncrease = var10001.add(((IntSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgPredict;
      BoolSetting.Builder var3 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-collision")).description("Whether to consider collision when predicting.")).defaultValue(true);
      var10003 = this.predict;
      Objects.requireNonNull(var10003);
      this.predictCollision = var10001.add(((BoolSetting.Builder)var3.visible(var10003::get)).build());
      this.facePlace = this.sgFacePlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("face-place")).description("Will face-place when target is below a certain health or armor durability threshold.")).defaultValue(true)).build());
      var10001 = this.sgFacePlace;
      var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("face-place-health")).description("The health the target has to be at to start face placing.")).defaultValue(8.0).min(1.0).sliderMin(1.0).sliderMax(36.0);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.facePlaceHealth = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgFacePlace;
      var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("armor-durability")).description("The durability threshold percentage to be able to face-place.")).defaultValue(2.0).min(1.0).sliderMin(1.0).sliderMax(100.0);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.armorDurability = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgFacePlace;
      var3 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("delayed-break")).description("Breaks crystals only while target can receive damage.")).defaultValue(true);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.facePlaceHurt = var10001.add(((BoolSetting.Builder)var3.visible(var10003::get)).build());
      var10001 = this.sgFacePlace;
      var3 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("face-place-missing-armor")).description("Automatically starts face placing when a target misses a piece of armor.")).defaultValue(false);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.facePlaceArmor = var10001.add(((BoolSetting.Builder)var3.visible(var10003::get)).build());
      this.forceFacePlace = this.sgFacePlace.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-face-place")).description("Starts face place when this button is pressed.")).defaultValue(Keybind.none())).build());
      this.surroundBreak = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("surround-break")).description("Placing crystal next to surrounded player to prevent re-surround")).defaultValue(true)).build());
      var10001 = this.sgMisc;
      var3 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-self")).description("Prevent placing crystals if they will block your own surround")).defaultValue(true);
      var10003 = this.surroundBreak;
      Objects.requireNonNull(var10003);
      this.antiSelf = var10001.add(((BoolSetting.Builder)var3.visible(var10003::get)).build());
      this.surroundHold = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("surround-hold")).description("Attacks crystals slowly if crystal position equals target surround.")).defaultValue(true)).build());
      var10001 = this.sgMisc;
      var3 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("extra-places")).description("Using more position for surround holding.")).defaultValue(false);
      var10003 = this.surroundHold;
      Objects.requireNonNull(var10003);
      this.extraPlaces = var10001.add(((BoolSetting.Builder)var3.visible(var10003::get)).build());
      this.eatPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-eat")).description("Pauses Crystal Aura when eating.")).defaultValue(true)).build());
      this.drinkPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-drink")).description("Pauses Crystal Aura when drinking.")).defaultValue(true)).build());
      this.minePause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-mine")).description("Pauses Crystal Aura when mining.")).defaultValue(false)).build());
      this.swing = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("swing")).description("How the renred are rendered.")).defaultValue(Interaction.SwingHand.Auto)).build());
      this.packetSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("packet-swing")).description("Renders hand swinging client side.")).defaultValue(true)).build());
      this.renderMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("render-mode")).description("How the render are rendered.")).defaultValue(RenderUtils.RenderMode.UpperSide)).build());
      this.smoothness = this.sgRender.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("smoothness")).description("How smoothly the render should move around.")).defaultValue(10)).min(0).sliderMax(20).visible(() -> {
         return this.renderMode.get() == RenderUtils.RenderMode.Smooth;
      })).build());
      this.height = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("height")).description("Maximum damage anchors can deal to yourself.")).defaultValue(0.99).sliderRange(0.0, 1.0).visible(() -> {
         return RenderUtils.visibleHeight((RenderUtils.RenderMode)this.renderMode.get());
      })).build());
      this.fade = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fade")).description("Fade mode.")).defaultValue(false)).build());
      var10001 = this.sgRender;
      var2 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("fade-time")).description("Duration for render place pos.")).defaultValue(10)).range(0, 50);
      var10003 = this.fade;
      Objects.requireNonNull(var10003);
      this.fadeTime = var10001.add(((IntSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var2 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("fade-amount")).description("Amount of smooth.")).defaultValue(10)).range(0, 50);
      var10003 = this.fade;
      Objects.requireNonNull(var10003);
      this.fadeAmount = var10001.add(((IntSetting.Builder)var2.visible(var10003::get)).build());
      this.renderBreak = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break")).description("Renders a block overlay over the block the crystals are broken on.")).defaultValue(false)).build());
      var10001 = this.sgRender;
      var2 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-time")).description("How long to render breaking for.")).defaultValue(13)).min(0).sliderMax(20);
      var10003 = this.renderBreak;
      Objects.requireNonNull(var10003);
      this.renderBreakTime = var10001.add(((IntSetting.Builder)var2.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the block overlay.")).defaultValue(new SettingColor(255, 255, 255, 45)).visible(() -> {
         return RenderUtils.visibleSide((ShapeMode)this.shapeMode.get());
      })).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the block overlay.")).defaultValue(new SettingColor(255, 255, 255)).visible(() -> {
         return RenderUtils.visibleLine((ShapeMode)this.shapeMode.get());
      })).build());
      this.renderTime = this.sgRender.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("render-time")).description("How long to render for.")).defaultValue(10)).min(0).sliderMax(20).build());
      this.damageText = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("damage")).description("Renders crystal damage text in the block overlay.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("damage-scale")).description("How big the damage text should be.")).defaultValue(1.25).min(1.0).sliderMax(4.0);
      var10003 = this.damageText;
      Objects.requireNonNull(var10003);
      this.damageTextScale = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      this.targets = new ArrayList();
      this.vec3d = new class_243(0.0, 0.0, 0.0);
      this.playerEyePos = new class_243(0.0, 0.0, 0.0);
      this.vec3 = new Vector3d();
      this.blockPos = new class_2338.class_2339();
      this.box = new class_238(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
      this.vec3dRayTraceEnd = new class_243(0.0, 0.0, 0.0);
      this.placedCrystals = new IntOpenHashSet();
      this.placingCrystalBlockPos = new class_2338.class_2339();
      this.removed = new IntOpenHashSet();
      this.attemptedBreaks = new Int2IntOpenHashMap();
      this.waitingToExplode = new Int2IntOpenHashMap();
      this.lastRotationPos = new class_243(0.0, 0.0, 0.0);
      this.renderPos = new class_2338.class_2339();
      this.breakRenderPos = new class_2338.class_2339();
      this.renderBlockPool = new Pool(() -> {
         return new RenderBlock();
      });
      this.renderBlocks = new ArrayList();
      this.renderBreakBlockPool = new Pool(() -> {
         return new RenderBlock();
      });
      this.renderBreakBlocks = new ArrayList();
      this.second = new int[20];
      this.lastSpawned = 20;
   }

   public void onActivate() {
      this.breakTimer = 0;
      this.placeTimer = 0;
      this.ticksPassed = 0;
      this.raycastContext = new class_3959(new class_243(0.0, 0.0, 0.0), new class_243(0.0, 0.0, 0.0), class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
      this.placing = false;
      this.placingTimer = 0;
      this.attacks = 0;
      this.serverYaw = (double)this.mc.field_1724.method_36454();
      this.bestTargetDamage = 0.0;
      this.bestTargetTimer = 0;
      this.sbCrystal = null;
      this.lastRotationTimer = this.getLastRotationStopDelay();
      this.renderTimer = 0;
      this.breakRenderTimer = 0;
      Iterator var1 = this.renderBlocks.iterator();

      RenderBlock renderBlock;
      while(var1.hasNext()) {
         renderBlock = (RenderBlock)var1.next();
         this.renderBlockPool.free(renderBlock);
      }

      this.renderBlocks.clear();
      var1 = this.renderBreakBlocks.iterator();

      while(var1.hasNext()) {
         renderBlock = (RenderBlock)var1.next();
         this.renderBreakBlockPool.free(renderBlock);
      }

      this.renderBlocks.clear();
      this.tick = 0;
      Arrays.fill(this.second, 0);
      this.i = 0;
   }

   public void onDeactivate() {
      this.targets.clear();
      this.placedCrystals.clear();
      this.attemptedBreaks.clear();
      this.waitingToExplode.clear();
      this.removed.clear();
      this.bestTarget = null;
      Iterator var1 = this.renderBlocks.iterator();

      RenderBlock renderBlock;
      while(var1.hasNext()) {
         renderBlock = (RenderBlock)var1.next();
         this.renderBlockPool.free(renderBlock);
      }

      this.renderBlocks.clear();
      var1 = this.renderBreakBlocks.iterator();

      while(var1.hasNext()) {
         renderBlock = (RenderBlock)var1.next();
         this.renderBreakBlockPool.free(renderBlock);
      }

      this.renderBlocks.clear();
   }

   private int getLastRotationStopDelay() {
      return Math.max(10, (Integer)this.placeDelay.get() / 2 + (Integer)this.breakDelay.get() / 2 + 10);
   }

   @EventHandler(
      priority = 100
   )
   private void onPreTick(TickEvent.Pre event) {
      this.calcCPS();
      this.didRotateThisTick = false;
      ++this.lastRotationTimer;
      if (this.placing) {
         if (this.placingTimer > 0) {
            --this.placingTimer;
         } else {
            this.placing = false;
         }
      }

      if (this.ticksPassed < 20) {
         ++this.ticksPassed;
      } else {
         this.ticksPassed = 0;
         this.attacks = 0;
      }

      if (this.bestTargetTimer > 0) {
         --this.bestTargetTimer;
      }

      this.bestTargetDamage = 0.0;
      if (this.breakTimer > 0) {
         --this.breakTimer;
      }

      if (this.placeTimer > 0) {
         --this.placeTimer;
      }

      if (this.switchTimer > 0) {
         --this.switchTimer;
      }

      if (!this.shouldSurroundHold() && this.breakTimer > (Integer)this.breakDelay.get()) {
         this.breakTimer = 0;
      }

      if (this.renderTimer > 0) {
         --this.renderTimer;
      }

      if (this.breakRenderTimer > 0) {
         --this.breakRenderTimer;
      }

      if (this.mc.method_1562().method_2871(this.mc.field_1724.method_5667()) != null) {
         ticksBehind = (float)this.mc.method_1562().method_2871(this.mc.field_1724.method_5667()).method_2959() / (50.0F * (20.0F / TickRate.INSTANCE.getTickRate()));
      }

      IntIterator var2 = this.waitingToExplode.keySet().iterator();

      while(var2.hasNext()) {
         int it = (Integer)var2.next();
         int ticks = this.waitingToExplode.get(it);
         if (ticks > 3) {
            this.waitingToExplode.remove(it);
            this.removed.remove(it);
         } else {
            this.waitingToExplode.put(it, ticks + 1);
         }
      }

      if (!PlayerUtils.shouldPause((Boolean)this.minePause.get(), (Boolean)this.eatPause.get(), (Boolean)this.drinkPause.get())) {
         ((IVec3d)this.playerEyePos).set(this.mc.field_1724.method_19538().field_1352, this.mc.field_1724.method_19538().field_1351 + (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), this.mc.field_1724.method_19538().field_1350);
         this.findTargets();
         if (this.targets.size() > 0) {
            if (!this.didRotateThisTick) {
               this.doBreak();
            }

            if (!this.didRotateThisTick) {
               this.doPlace();
            }
         }

         this.renderBlocks.forEach(RenderBlock::tick);
         this.renderBlocks.removeIf((renderBlock) -> {
            return renderBlock.ticks <= 0;
         });
         this.renderBreakBlocks.forEach(RenderBlock::tick);
         this.renderBreakBlocks.removeIf((renderBlock) -> {
            return renderBlock.ticks <= 0;
         });
      }
   }

   @EventHandler(
      priority = -866
   )
   private void onPreTickLast(TickEvent.Pre event) {
      if ((Boolean)this.rotate.get() && this.lastRotationTimer < this.getLastRotationStopDelay() && !this.didRotateThisTick) {
         Rotations.rotate(this.isLastRotationPos ? Rotations.getYaw(this.lastRotationPos) : this.lastYaw, this.isLastRotationPos ? Rotations.getPitch(this.lastRotationPos) : this.lastPitch, -100, (Runnable)null);
      }

   }

   @EventHandler
   private void onEntityAdded(EntityAddedEvent event) {
      if (event.entity instanceof class_1511) {
         this.last = event.entity.method_5628() - this.lastEntityId;
         this.lastEntityId = event.entity.method_5628();
         if (this.placing && event.entity.method_24515().equals(this.placingCrystalBlockPos)) {
            this.placing = false;
            this.placingTimer = 0;
            this.placedCrystals.add(event.entity.method_5628());
         }

         if ((Boolean)this.fastBreak.get() && !this.didRotateThisTick && this.attacks < (Integer)this.attackFrequency.get()) {
            double damage = this.getBreakDamage(event.entity, true);
            if (damage > (Double)this.minDmg.get()) {
               this.doBreak(event.entity);
            }
         }

      }
   }

   @EventHandler
   private void onEntityRemoved(EntityRemovedEvent event) {
      if (event.entity instanceof class_1511) {
         if (this.placedCrystals.contains(event.entity.method_5628())) {
            this.lastSpawned = 20;
            ++this.tick;
         }

         this.placedCrystals.remove(event.entity.method_5628());
         this.removed.remove(event.entity.method_5628());
         this.waitingToExplode.remove(event.entity.method_5628());
      }

   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2743 packet) {
         if (this.bestTarget != null && ((class_2743)event.packet).method_11818() == this.bestTarget.method_5628() && this.bestTarget.field_6235 >= 10) {
            double velX = ((double)packet.method_11815() / 8000.0 - this.bestTarget.method_18798().field_1352) * 0.0;
            double velY = ((double)packet.method_11816() / 8000.0 - this.bestTarget.method_18798().field_1351) * 0.0;
            double velZ = ((double)packet.method_11819() / 8000.0 - this.bestTarget.method_18798().field_1350) * 0.0;
            ((EntityVelocityUpdateS2CPacketAccessor)packet).setX((int)(velX * 8000.0 + this.bestTarget.method_18798().field_1352 * 8000.0));
            ((EntityVelocityUpdateS2CPacketAccessor)packet).setY((int)(velY * 8000.0 + this.bestTarget.method_18798().field_1351 * 8000.0));
            ((EntityVelocityUpdateS2CPacketAccessor)packet).setZ((int)(velZ * 8000.0 + this.bestTarget.method_18798().field_1350 * 8000.0));
         }
      }

   }

   private void setRotation(boolean isPos, class_243 pos, double yaw, double pitch) {
      this.didRotateThisTick = true;
      this.isLastRotationPos = isPos;
      if (isPos) {
         ((IVec3d)this.lastRotationPos).set(pos.field_1352, pos.field_1351, pos.field_1350);
      } else {
         this.lastYaw = yaw;
         this.lastPitch = pitch;
      }

      this.lastRotationTimer = 0;
   }

   private void doBreak() {
      if ((Boolean)this.doBreak.get() && this.breakTimer <= 0 && this.switchTimer <= 0 && this.attacks < (Integer)this.attackFrequency.get()) {
         double bestDamage = 0.0;
         class_1297 crystal = null;
         Iterator var4 = this.mc.field_1687.method_18112().iterator();

         while(var4.hasNext()) {
            class_1297 entity = (class_1297)var4.next();
            double damage = this.getBreakDamage(entity, true);
            if (damage > bestDamage) {
               bestDamage = damage;
               crystal = entity;
            }
         }

         if (crystal != null) {
            this.doBreak(crystal);
         }

      }
   }

   private double getBreakDamage(class_1297 entity, boolean checkCrystalAge) {
      if (!(entity instanceof class_1511)) {
         return 0.0;
      } else if ((Boolean)this.onlyBreakOwn.get() && !this.placedCrystals.contains(entity.method_5628())) {
         return 0.0;
      } else if (this.removed.contains(entity.method_5628())) {
         return 0.0;
      } else if (this.attemptedBreaks.get(entity.method_5628()) > (Integer)this.breakAttempts.get()) {
         return 0.0;
      } else if (checkCrystalAge && entity.field_6012 < (Integer)this.crystalAge.get()) {
         return 0.0;
      } else if (this.isOutOfRange(entity.method_19538(), entity.method_24515(), false)) {
         return 0.0;
      } else {
         this.blockPos.method_10101(entity.method_24515()).method_10100(0, -1, 0);
         double selfDamage = CrystalUtils.crystalDamage(this.mc.field_1724, entity.method_19538());
         switch ((DamageToPlayer)this.dmgToPlayer.get()) {
            case AntiSuicide:
               if (!(Boolean)this.ignoreBreakDmg.get() && (selfDamage > (Double)this.maxDmg.get() || selfDamage >= (double)EntityUtils.getTotalHealth(this.mc.field_1724))) {
                  return 0.0;
               }
            case Invincibility:
               if (!(Boolean)this.ignoreBreakDmg.get() && selfDamage > (Double)this.maxDmg.get() && this.mc.field_1724.field_6235 > 0) {
                  return 0.0;
               }
         }

         double damage = this.getDamageToTargets(entity.method_19538(), true, false);
         return !this.shouldFacePlace() && damage < (Double)this.minDmg.get() ? 0.0 : damage;
      }
   }

   private void doBreak(class_1297 crystal) {
      if ((Boolean)this.antiWeakness.get()) {
         class_1293 weakness = this.mc.field_1724.method_6112(class_1294.field_5911);
         class_1293 strength = this.mc.field_1724.method_6112(class_1294.field_5910);
         if (weakness != null && (strength == null || strength.method_5578() <= weakness.method_5578()) && !this.isValidWeaknessItem(this.mc.field_1724.method_6047())) {
            if (!InvUtils.swap(InvUtils.findInHotbar(this::isValidWeaknessItem).slot(), false)) {
               return;
            }

            this.switchTimer = 1;
            return;
         }
      }

      if ((Boolean)this.rotate.get()) {
         double yaw = Rotations.getYaw(crystal);
         double pitch = Rotations.getPitch(crystal, Target.Feet);
         if (this.doYawSteps(yaw, pitch)) {
            this.setRotation(true, crystal.method_19538(), 0.0, 0.0);
            Rotations.rotate(yaw, pitch, 50, () -> {
               this.attackCrystal(crystal);
            });
            this.breakTimer = this.getDelay();
         }
      } else {
         this.attackCrystal(crystal);
         this.breakTimer = this.getDelay();
      }

      if (this.cancelCrystal.get() == AutoCrystal.CancelCrystal.OnHit) {
         this.placedCrystals.remove(crystal.method_5628());
         this.placedCrystals.remove(crystal.method_5628());
         crystal.method_5768();
      }

      this.removed.add(crystal.method_5628());
      this.attemptedBreaks.put(crystal.method_5628(), this.attemptedBreaks.get(crystal.method_5628()) + 1);
      this.waitingToExplode.put(crystal.method_5628(), 0);
      this.renderBreakBlocks.add(((RenderBlock)this.renderBreakBlockPool.get()).set(crystal.method_24515().method_10074()));
      this.breakRenderPos.method_10101(crystal.method_24515().method_10074());
      this.breakRenderTimer = (Integer)this.renderBreakTime.get();
   }

   private boolean isValidWeaknessItem(class_1799 itemStack) {
      if (itemStack.method_7909() instanceof class_1831 && !(itemStack.method_7909() instanceof class_1794)) {
         class_1832 material = ((class_1831)itemStack.method_7909()).method_8022();
         return material == class_1834.field_8930 || material == class_1834.field_22033;
      } else {
         return false;
      }
   }

   private void attackCrystal(class_1297 entity) {
      this.mc.field_1724.field_3944.method_2883(class_2824.method_34206(entity, this.mc.field_1724.method_5715()));
      class_1268 hand = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8301}).getHand();
      if (hand == null) {
         hand = class_1268.field_5808;
      }

      Interaction.doSwing((Interaction.SwingHand)this.swing.get(), (Boolean)this.packetSwing.get(), hand);
      ++this.attacks;
   }

   private void calcCPS() {
      ++this.i;
      if (this.i >= this.second.length) {
         this.i = 0;
      }

      this.second[this.i] = this.tick;
      this.tick = 0;
      cps = 0;
      int[] var1 = this.second;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int i = var1[var3];
         cps += i;
      }

      --this.lastSpawned;
      if (this.lastSpawned >= 0 && cps > 0) {
         --cps;
      }

   }

   public static int getCPS() {
      return cps;
   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2868) {
         this.switchTimer = (Integer)this.switchDelay.get();
      }

   }

   private void doPlace() {
      if ((Boolean)this.doPlace.get() && this.placeTimer <= 0) {
         if (InvUtils.findInHotbar(new class_1792[]{class_1802.field_8301}).found()) {
            if (this.autoSwitch.get() != AutoCrystal.AutoSwitchMode.None || this.mc.field_1724.method_6079().method_7909() == class_1802.field_8301 || this.mc.field_1724.method_6047().method_7909() == class_1802.field_8301) {
               Iterator var1 = this.mc.field_1687.method_18112().iterator();

               class_1297 entity;
               do {
                  if (!var1.hasNext()) {
                     AtomicDouble bestDamage = new AtomicDouble(0.0);
                     AtomicReference bestBlockPos = new AtomicReference(new class_2338.class_2339());
                     AtomicBoolean isSupport = new AtomicBoolean(this.support.get() != AutoCrystal.SupportMode.Disabled);
                     AtomicInteger i = new AtomicInteger();
                     BlockIterator.register((int)Math.ceil((Double)this.placeRange.get()), (int)Math.ceil((Double)this.placeRange.get()), (bp, blockState) -> {
                        boolean hasBlock = blockState.method_27852(class_2246.field_9987) || blockState.method_27852(class_2246.field_10540);
                        if (hasBlock || isSupport.get() && blockState.method_45474()) {
                           this.blockPos.method_10103(bp.method_10263(), bp.method_10264() + 1, bp.method_10260());
                           if (this.mc.field_1687.method_8320(this.blockPos).method_26215()) {
                              if ((Boolean)this.placement.get()) {
                                 this.blockPos.method_10100(0, 1, 0);
                                 if (!this.mc.field_1687.method_8320(this.blockPos).method_26215()) {
                                    return;
                                 }
                              }

                              ((IVec3d)this.vec3d).set((double)bp.method_10263() + 0.5, (double)(bp.method_10264() + 1), (double)bp.method_10260() + 0.5);
                              this.blockPos.method_10101(bp).method_10100(0, 1, 0);
                              if (!this.isOutOfRange(this.vec3d, this.blockPos, true)) {
                                 double selfDamage = CrystalUtils.crystalDamage(this.mc.field_1724, this.vec3d);
                                 switch ((DamageToPlayer)this.dmgToPlayer.get()) {
                                    case AntiSuicide:
                                       if (selfDamage > (Double)this.maxDmg.get() || selfDamage >= (double)EntityUtils.getTotalHealth(this.mc.field_1724)) {
                                          return;
                                       }
                                    case Invincibility:
                                       if (selfDamage > (Double)this.maxDmg.get() && this.mc.field_1724.field_6235 <= 0) {
                                          return;
                                       }
                                    default:
                                       Iterator var10 = this.mc.field_1687.method_18112().iterator();

                                       double friendDamage;
                                       while(var10.hasNext()) {
                                          class_1297 entity = (class_1297)var10.next();
                                          if ((Boolean)this.antiFriendPop.get() && entity instanceof class_1657) {
                                             class_1657 friend = (class_1657)entity;
                                             if (Friends.get().isFriend(friend)) {
                                                friendDamage = CrystalUtils.crystalDamage(friend, this.vec3d);
                                                if (friendDamage > (Double)this.maxFriendDmg.get()) {
                                                   return;
                                                }
                                             }
                                          }
                                       }

                                       double damage = this.getDamageToTargets(this.vec3d, false, !hasBlock && this.support.get() == AutoCrystal.SupportMode.Fast);
                                       boolean surroundBreaking = this.shouldSurroundBreak(this.blockPos) && !this.shouldFacePlace() && i.get() == 0;
                                       if (surroundBreaking) {
                                          i.getAndIncrement();
                                       }

                                       if (this.shouldFacePlace() || surroundBreaking || !(damage < (Double)this.minDmg.get())) {
                                          friendDamage = (double)bp.method_10263();
                                          double y = (double)(bp.method_10264() + 1);
                                          double z = (double)bp.method_10260();
                                          ((IBox)this.box).set(friendDamage, y, z, friendDamage + 1.0, y + (double)((Boolean)this.placement.get() ? 1 : 2), z + 1.0);
                                          if (!this.intersectsWithEntities(this.box)) {
                                             if ((Boolean)this.predict.get()) {
                                                try {
                                                   class_238 bx = PredictionUtils.returnPredictBox(this.bestTarget, (Boolean)this.predictCollision.get(), (Integer)this.predictIncrease.get());
                                                   if (this.box.method_994(bx)) {
                                                      return;
                                                   }
                                                } catch (Exception var20) {
                                                }
                                             }

                                             if (damage > bestDamage.get() || isSupport.get() && hasBlock) {
                                                bestDamage.set(damage);
                                                ((class_2338.class_2339)bestBlockPos.get()).method_10101(bp);
                                             }

                                             if (hasBlock) {
                                                isSupport.set(false);
                                             }

                                          }
                                       }
                                 }
                              }
                           }
                        }
                     });
                     BlockIterator.after(() -> {
                        if (bestDamage.get() != 0.0) {
                           class_3965 result = this.getPlaceInfo((class_2338)bestBlockPos.get());
                           class_243 pos = Utils.vec3d((class_2338)bestBlockPos.get());
                           ((IVec3d)this.vec3d).set((double)result.method_17777().method_10263() + 0.5 + (double)result.method_17780().method_10163().method_10263() * 1.0 / 2.0, (double)result.method_17777().method_10264() + 0.5 + (double)result.method_17780().method_10163().method_10264() * 1.0 / 2.0, (double)result.method_17777().method_10260() + 0.5 + (double)result.method_17780().method_10163().method_10260() * 1.0 / 2.0);
                           if ((Boolean)this.rotate.get()) {
                              double yaw = Rotations.getYaw(this.vec3d);
                              double pitch = Rotations.getPitch(this.vec3d);
                              if (this.yawStepMode.get() == AutoCrystal.YawStepMode.Break || this.doYawSteps(yaw, pitch)) {
                                 this.setRotation(true, this.vec3d, 0.0, 0.0);
                                 Rotations.rotate(yaw, pitch, 50, () -> {
                                    this.placeCrystal(result, bestDamage.get(), isSupport.get() ? (class_2338)bestBlockPos.get() : null);
                                 });
                                 this.placeTimer += (Integer)this.placeDelay.get();
                              }
                           } else {
                              this.placeCrystal(result, bestDamage.get(), isSupport.get() ? (class_2338)bestBlockPos.get() : null);
                              this.placeTimer += (Integer)this.placeDelay.get();
                           }

                           if ((Boolean)this.predictCrystal.get()) {
                              class_1511 crystal = new class_1511(this.mc.field_1687, pos.field_1352 + 0.5, pos.field_1351 + 1.0, pos.field_1350 + 0.5);
                              crystal.method_5838(this.lastEntityId + this.last);
                              this.doBreak(crystal);
                           }

                        }
                     });
                     return;
                  }

                  entity = (class_1297)var1.next();
               } while(!(this.getBreakDamage(entity, false) > 0.0));

            }
         }
      }
   }

   private class_3965 getPlaceInfo(class_2338 blockPos) {
      ((IVec3d)this.vec3d).set(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), this.mc.field_1724.method_23321());
      class_2350[] var2 = class_2350.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         class_2350 side = var2[var4];
         ((IVec3d)this.vec3dRayTraceEnd).set((double)blockPos.method_10263() + 0.5 + (double)side.method_10163().method_10263() * 0.5, (double)blockPos.method_10264() + 0.5 + (double)side.method_10163().method_10264() * 0.5, (double)blockPos.method_10260() + 0.5 + (double)side.method_10163().method_10260() * 0.5);
         ((IRaycastContext)this.raycastContext).set(this.vec3d, this.vec3dRayTraceEnd, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
         class_3965 result = this.mc.field_1687.method_17742(this.raycastContext);
         if (result != null && result.method_17783() == class_240.field_1332 && result.method_17777().equals(blockPos)) {
            return result;
         }
      }

      class_2350 side = (double)blockPos.method_10264() > this.vec3d.field_1351 ? class_2350.field_11033 : class_2350.field_11036;
      return new class_3965(this.vec3d, side, blockPos, false);
   }

   private void placeCrystal(class_3965 result, double damage, class_2338 supportBlock) {
      class_1792 targetItem = supportBlock == null ? class_1802.field_8301 : class_1802.field_8281;
      FindItemResult item = InvUtils.findInHotbar(new class_1792[]{targetItem});
      if (item.found()) {
         int prevSlot = this.mc.field_1724.method_31548().field_7545;
         if (this.autoSwitch.get() != AutoCrystal.AutoSwitchMode.None && !item.isOffhand()) {
            Interaction.updateSlot(item, true);
         }

         class_1268 hand = item.getHand();
         if (hand != null) {
            if (supportBlock == null) {
               this.mc.field_1724.field_3944.method_2883(new class_2885(hand, result, 0));
               Interaction.doSwing((Interaction.SwingHand)this.swing.get(), (Boolean)this.packetSwing.get(), hand);
               this.placing = true;
               this.placingTimer = 4;
               this.placingCrystalBlockPos.method_10101(result.method_17777()).method_10100(0, 1, 0);
               this.renderBlocks.add(((RenderBlock)this.renderBlockPool.get()).set(result.method_17777()));
               this.renderTimer = (Integer)this.renderTime.get();
               this.renderPos.method_10101(result.method_17777());
               this.renderDamage = damage;
            } else {
               BlockUtils.place(supportBlock, item, false, 0, true, true, false);
               this.placeTimer += (Integer)this.supportDelay.get();
               if ((Integer)this.supportDelay.get() == 0) {
                  this.placeCrystal(result, damage, (class_2338)null);
               }
            }

            if (this.autoSwitch.get() == AutoCrystal.AutoSwitchMode.Silent) {
               Interaction.swapBack(prevSlot);
            }

         }
      }
   }

   @EventHandler
   private void onPacketSent(PacketEvent.Sent event) {
      if (event.packet instanceof class_2828) {
         this.serverYaw = (double)((class_2828)event.packet).method_12271((float)this.serverYaw);
      }

   }

   public boolean doYawSteps(double targetYaw, double targetPitch) {
      targetYaw = class_3532.method_15338(targetYaw) + 180.0;
      double serverYaw = class_3532.method_15338(this.serverYaw) + 180.0;
      if (distanceBetweenAngles(serverYaw, targetYaw) <= (Double)this.yawSteps.get()) {
         return true;
      } else {
         double delta = Math.abs(targetYaw - serverYaw);
         double yaw = this.serverYaw;
         if (serverYaw < targetYaw) {
            if (delta < 180.0) {
               yaw += (Double)this.yawSteps.get();
            } else {
               yaw -= (Double)this.yawSteps.get();
            }
         } else if (delta < 180.0) {
            yaw -= (Double)this.yawSteps.get();
         } else {
            yaw += (Double)this.yawSteps.get();
         }

         this.setRotation(false, (class_243)null, yaw, targetPitch);
         Rotations.rotate(yaw, targetPitch, -100, (Runnable)null);
         return false;
      }
   }

   private static double distanceBetweenAngles(double alpha, double beta) {
      double phi = Math.abs(beta - alpha) % 360.0;
      return phi > 180.0 ? 360.0 - phi : phi;
   }

   private boolean isOutOfRange(class_243 vec3d, class_2338 blockPos, boolean place) {
      ((IRaycastContext)this.raycastContext).set(this.playerEyePos, vec3d, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
      class_3965 result = this.mc.field_1687.method_17742(this.raycastContext);
      boolean behindWall = result == null || !result.method_17777().equals(blockPos);
      double distance = this.mc.field_1724.method_19538().method_1022(vec3d);
      return distance > behindWall ? (Double)(place ? this.placeWallsRange : this.breakWallsRange).get() : (Double)(place ? this.placeRange : this.breakRange).get();
   }

   private class_1657 getNearestTarget() {
      class_1657 nearestTarget = null;
      double nearestDistance = Double.MAX_VALUE;
      Iterator var4 = this.targets.iterator();

      while(var4.hasNext()) {
         class_1657 target = (class_1657)var4.next();
         double distance = target.method_5858(this.mc.field_1724);
         if (distance < nearestDistance) {
            nearestTarget = target;
            nearestDistance = distance;
         }
      }

      return nearestTarget;
   }

   private double getDamageToTargets(class_243 vec3d, boolean breaking, boolean fast) {
      double damage = 0.0;
      double dmg;
      if (fast) {
         class_1657 target = this.getNearestTarget();
         if (!breaking || target.field_6235 <= 0) {
            damage = CrystalUtils.crystalDamage(target, vec3d, (Boolean)this.predict.get(), (Boolean)this.predictCollision.get(), (Integer)this.predictIncrease.get(), (class_2338)null, (Boolean)this.ignoreTerrain.get());
         }
      } else {
         for(Iterator var10 = this.targets.iterator(); var10.hasNext(); damage += dmg) {
            class_1657 target = (class_1657)var10.next();
            dmg = CrystalUtils.crystalDamage(target, vec3d, (Boolean)this.predict.get(), (Boolean)this.predictCollision.get(), (Integer)this.predictIncrease.get(), (class_2338)null, (Boolean)this.ignoreTerrain.get());
            if (dmg > this.bestTargetDamage) {
               this.bestTarget = target;
               this.bestTargetDamage = dmg;
               this.bestTargetTimer = 10;
            }
         }
      }

      return damage;
   }

   public String getInfoString() {
      return this.bestTarget != null && this.bestTargetTimer > 0 ? this.bestTarget.method_7334().getName() : null;
   }

   private void findTargets() {
      this.targets.clear();
      Iterator var1 = this.mc.field_1687.method_18456().iterator();

      class_1657 player;
      while(var1.hasNext()) {
         player = (class_1657)var1.next();
         if (!EntityInfo.isCreative(player) && player != this.mc.field_1724 && !EntityInfo.isDead(player) && EntityInfo.isAlive(player) && Friends.get().shouldAttack(player) && (double)player.method_5739(this.mc.field_1724) <= (Double)this.targetRange.get()) {
            this.targets.add(player);
         }
      }

      var1 = FakePlayerManager.getFakePlayers().iterator();

      while(var1.hasNext()) {
         player = (class_1657)var1.next();
         if (!EntityInfo.isDead(player) && EntityInfo.isAlive(player) && Friends.get().shouldAttack(player) && (double)player.method_5739(this.mc.field_1724) <= (Double)this.targetRange.get()) {
            this.targets.add(player);
         }
      }

   }

   private boolean intersectsWithEntities(class_238 box) {
      return EntityUtils.intersectsWithEntity(box, (entity) -> {
         return !entity.method_7325() && !this.removed.contains(entity.method_5628());
      });
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      RenderInfo ri = new RenderInfo(event, (RenderUtils.RenderMode)this.renderMode.get(), (ShapeMode)this.shapeMode.get());
      if (this.renderTimer > 0 && this.renderMode.get() != RenderUtils.RenderMode.None) {
         if ((Boolean)this.fade.get()) {
            this.renderBlocks.sort(Comparator.comparingInt((o) -> {
               return -o.ticks;
            }));
            this.renderBlocks.forEach((renderBlock) -> {
               renderBlock.render(ri, (Color)this.sideColor.get(), (Color)this.lineColor.get());
            });
            return;
         }

         RenderUtils.render(ri, this.renderPos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (Double)this.height.get());
      }

      if (this.renderMode.get() == RenderUtils.RenderMode.Smooth) {
         if (this.renderTimer <= 0) {
            return;
         }

         if (this.renderBoxOne == null) {
            this.renderBoxOne = new class_238(this.renderPos);
         }

         if (this.renderBoxTwo == null) {
            this.renderBoxTwo = new class_238(this.renderPos);
         } else {
            ((IBox)this.renderBoxTwo).set(this.renderPos);
         }

         double offsetX = (this.renderBoxTwo.field_1323 - this.renderBoxOne.field_1323) / (double)(Integer)this.smoothness.get();
         double offsetY = (this.renderBoxTwo.field_1322 - this.renderBoxOne.field_1322) / (double)(Integer)this.smoothness.get();
         double offsetZ = (this.renderBoxTwo.field_1321 - this.renderBoxOne.field_1321) / (double)(Integer)this.smoothness.get();
         ((IBox)this.renderBoxOne).set(this.renderBoxOne.field_1323 + offsetX, this.renderBoxOne.field_1322 + offsetY, this.renderBoxOne.field_1321 + offsetZ, this.renderBoxOne.field_1320 + offsetX, this.renderBoxOne.field_1325 + offsetY, this.renderBoxOne.field_1324 + offsetZ);
         event.renderer.box(this.renderBoxOne, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
      }

      if (this.breakRenderTimer > 0 && (Boolean)this.renderBreak.get() && !this.mc.field_1687.method_8320(this.breakRenderPos).method_26215()) {
         int preSideA = ((SettingColor)this.sideColor.get()).a;
         SettingColor var10000 = (SettingColor)this.sideColor.get();
         var10000.a -= 20;
         ((SettingColor)this.sideColor.get()).validate();
         int preLineA = ((SettingColor)this.lineColor.get()).a;
         var10000 = (SettingColor)this.lineColor.get();
         var10000.a -= 20;
         ((SettingColor)this.lineColor.get()).validate();
         if ((Boolean)this.fade.get()) {
            this.renderBlocks.sort(Comparator.comparingInt((o) -> {
               return -o.ticks;
            }));
            this.renderBlocks.forEach((renderBlock) -> {
               renderBlock.render(ri, (Color)this.sideColor.get(), (Color)this.lineColor.get());
            });
         } else {
            RenderUtils.render(ri, this.breakRenderPos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (Double)this.height.get());
         }

         ((SettingColor)this.sideColor.get()).a = preSideA;
         ((SettingColor)this.lineColor.get()).a = preLineA;
      }

   }

   public boolean shouldFacePlace() {
      if (!(Boolean)this.facePlace.get()) {
         return false;
      } else {
         Iterator var1 = this.targets.iterator();

         label64:
         while(var1.hasNext()) {
            class_1657 target = (class_1657)var1.next();
            if (EntityInfo.isSurrounded(target) && !this.isTrapped(target)) {
               if ((Boolean)this.facePlaceHurt.get() && target.field_6235 != 0) {
                  return false;
               }

               if (((Keybind)this.forceFacePlace.get()).isPressed()) {
                  return true;
               }

               if ((double)EntityUtils.getTotalHealth(target) <= (Double)this.facePlaceHealth.get()) {
                  return true;
               }

               Iterator var3 = target.method_5661().iterator();

               boolean shouldBreakArmor;
               do {
                  class_1799 itemStack;
                  do {
                     if (!var3.hasNext()) {
                        continue label64;
                     }

                     itemStack = (class_1799)var3.next();
                  } while(!(Boolean)this.facePlaceArmor.get());

                  if (itemStack == null || itemStack.method_7960()) {
                     return true;
                  }

                  shouldBreakArmor = (double)(itemStack.method_7936() - itemStack.method_7919()) / (double)itemStack.method_7936() * 100.0 <= (Double)this.armorDurability.get();
               } while(!shouldBreakArmor);

               return true;
            }

            return false;
         }

         return false;
      }
   }

   public int getDelay() {
      return this.shouldSurroundHold() ? 10 : (Integer)this.breakDelay.get();
   }

   public boolean shouldSurroundBreak(class_2338 crystal) {
      if (!(Boolean)this.surroundBreak.get()) {
         return false;
      } else {
         Iterator var2 = this.targets.iterator();

         while(true) {
            class_1657 target;
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               target = (class_1657)var2.next();
            } while(target != this.bestTarget);

            if (!this.trueSurround(this.bestTarget)) {
               if (this.sbCrystal != null) {
                  this.attackCrystal(this.sbCrystal);
               }

               return false;
            }

            class_2338 targetPos = EntityInfo.getBlockPos(target);
            class_243 crystalVec = class_243.method_24953(crystal);
            if ((Boolean)this.antiSelf.get() && (double)crystal.method_10264() <= this.mc.field_1724.method_23318() && this.mc.field_1724.method_19538().method_1022(crystalVec) <= 2.7) {
               return false;
            }

            class_2350[] var6 = class_2350.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               class_2350 direction = var6[var8];
               if (!direction.equals(class_2350.field_11036) && !direction.equals(class_2350.field_11033)) {
                  class_2338 offsetPos = targetPos.method_10093(direction);
                  class_2350[] var11 = class_2350.values();
                  int var12 = var11.length;

                  for(int var13 = 0; var13 < var12; ++var13) {
                     class_2350 direction2 = var11[var13];
                     if (!direction2.equals(class_2350.field_11036) && !direction2.equals(class_2350.field_11033) && BlockInfo.isCombatBlock(offsetPos) && crystal.equals(offsetPos.method_10093(direction2))) {
                        this.sbCrystal = this.getSbCrystal(offsetPos.method_10093(direction2));
                        return true;
                     }
                  }
               }
            }
         }
      }
   }

   public class_1511 getSbCrystal(class_2338 blockPos) {
      Iterator var2 = EntityInfo.getEntities().iterator();

      while(var2.hasNext()) {
         class_1297 entity = (class_1297)var2.next();
         if (entity instanceof class_1511 crystal) {
            if (EntityInfo.getBlockPos((class_1297)crystal).equals(blockPos)) {
               return crystal;
            }
         }
      }

      return null;
   }

   public boolean shouldSurroundHold() {
      if (!(Boolean)this.surroundHold.get()) {
         return false;
      } else {
         Iterator var1 = this.targets.iterator();

         while(var1.hasNext()) {
            class_1657 target = (class_1657)var1.next();
            class_2338 targetPos = EntityInfo.getBlockPos(target);
            class_2350[] var4 = class_2350.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               class_2350 direction = var4[var6];
               if (!direction.equals(class_2350.field_11036)) {
                  class_2338 offsetPos = targetPos.method_10093(direction);
                  class_2350[] var9 = class_2350.values();
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     class_2350 direction2 = var9[var11];
                     if (!direction2.equals(class_2350.field_11036)) {
                        Iterator var13 = this.mc.field_1687.method_18112().iterator();

                        while(var13.hasNext()) {
                           class_1297 entity = (class_1297)var13.next();
                           if (entity instanceof class_1511) {
                              class_1511 crystal = (class_1511)entity;
                              class_2338 crystalPos = EntityInfo.getBlockPos((class_1297)crystal);
                              if (crystalPos.equals(offsetPos)) {
                                 return true;
                              }

                              if ((Boolean)this.extraPlaces.get() && crystalPos.equals(offsetPos.method_10093(direction2))) {
                                 return true;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   public boolean trueSurround(class_1309 entity) {
      class_2338 entityBlockPos = EntityInfo.getBlockPos((class_1297)entity);
      return BlockInfo.isCombatBlock(entity.method_24515().method_10069(1, 0, 0)) || BlockInfo.isCombatBlock(entityBlockPos.method_10069(-1, 0, 0)) || BlockInfo.isCombatBlock(entityBlockPos.method_10069(0, 0, 1)) || BlockInfo.isCombatBlock(entityBlockPos.method_10069(0, 0, -1));
   }

   public boolean isTrapped(class_1309 t) {
      class_2338 p = t.method_24515().method_10084();
      return BlockInfo.isBlastResist(p.method_10072()) && BlockInfo.isBlastResist(p.method_10095()) && BlockInfo.isBlastResist(p.method_10067()) && BlockInfo.isBlastResist(p.method_10078());
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      if (this.renderMode.get() != RenderUtils.RenderMode.None && this.renderTimer > 0 && (Boolean)this.damageText.get()) {
         if (this.renderMode.get() == RenderUtils.RenderMode.UpperSide && this.renderMode.get() == RenderUtils.RenderMode.LowerSide) {
            this.vec3.set((double)this.renderPos.method_10263() + 0.5, (double)this.renderPos.method_10264() + 1.1, (double)this.renderPos.method_10260() + 0.5);
         } else {
            this.vec3.set((double)this.renderPos.method_10263() + 0.5, (double)this.renderPos.method_10264() + 0.5, (double)this.renderPos.method_10260() + 0.5);
         }

         if (NametagUtils.to2D(this.vec3, (Double)this.damageTextScale.get())) {
            NametagUtils.begin(this.vec3);
            TextRenderer.get().begin(1.0, false, true);
            String text = String.format("%.1f", this.renderDamage);
            double w = TextRenderer.get().getWidth(text) / 2.0;
            TextRenderer.get().render(text, -w, 0.0, (Color)this.lineColor.get(), true);
            TextRenderer.get().end();
            NametagUtils.end();
         }

      }
   }

   public static enum AutoSwitchMode {
      Normal,
      Silent,
      None;

      // $FF: synthetic method
      private static AutoSwitchMode[] $values() {
         return new AutoSwitchMode[]{Normal, Silent, None};
      }
   }

   public static enum YawStepMode {
      Break,
      All;

      // $FF: synthetic method
      private static YawStepMode[] $values() {
         return new YawStepMode[]{Break, All};
      }
   }

   public static enum SupportMode {
      Disabled,
      Accurate,
      Fast;

      // $FF: synthetic method
      private static SupportMode[] $values() {
         return new SupportMode[]{Disabled, Accurate, Fast};
      }
   }

   public static enum CancelCrystal {
      NoDesync,
      OnHit;

      // $FF: synthetic method
      private static CancelCrystal[] $values() {
         return new CancelCrystal[]{NoDesync, OnHit};
      }
   }

   public static enum DamageToPlayer {
      Suicide,
      AntiSuicide,
      Invincibility;

      // $FF: synthetic method
      private static DamageToPlayer[] $values() {
         return new DamageToPlayer[]{Suicide, AntiSuicide, Invincibility};
      }
   }

   public class RenderBlock {
      public class_2338.class_2339 pos = new class_2338.class_2339();
      public int ticks;

      public void tick() {
         --this.ticks;
      }

      public void render(RenderInfo ri, Color sides, Color lines) {
         int preSideA = sides.a;
         int preLineA = lines.a;
         sides.a = (int)((double)sides.a * ((double)this.ticks / (double)(Integer)AutoCrystal.this.fadeAmount.get()));
         lines.a = (int)((double)lines.a * ((double)this.ticks / (double)(Integer)AutoCrystal.this.fadeAmount.get()));
         RenderUtils.render(ri, this.pos, sides, lines, (Double)AutoCrystal.this.height.get());
         sides.a = preSideA;
         lines.a = preLineA;
      }

      public RenderBlock set(class_2338 blockPos) {
         this.pos.method_10101(blockPos);
         this.ticks = (Integer)AutoCrystal.this.fadeTime.get();
         return this;
      }
   }
}
