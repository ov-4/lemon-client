package dev.lemonclient.addon.modules.combat;

import com.google.common.util.concurrent.AtomicDouble;
import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.hud.ToastNotifications;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.info.Notifications;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.misc.Vec3dInfo;
import dev.lemonclient.addon.utils.others.Task;
import dev.lemonclient.addon.utils.player.BedUtils;
import dev.lemonclient.addon.utils.world.BlockInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1541;
import net.minecraft.class_1657;
import net.minecraft.class_1748;
import net.minecraft.class_1802;
import net.minecraft.class_2244;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2620;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3965;

public class BedBombV2 extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPredict;
   private final SettingGroup sgPvP;
   private final SettingGroup sgTrapBreaker;
   private final SettingGroup sgBurrowBreaker;
   private final SettingGroup sgStringBreaker;
   private final SettingGroup sgOther;
   private final SettingGroup sgBedRefill;
   private final SettingGroup sgRender;
   private final SettingGroup sgNone;
   public final Setting placeDelay;
   public final Setting targetRange;
   public final Setting placeRange;
   public final Setting minTargetDamage;
   public final Setting maxSelfDamage;
   public final Setting antiFriendPop;
   public final Setting maxFriendDamage;
   public final Setting ignoreTerrain;
   private final Setting strictDirection;
   public final Setting debug;
   public final Setting predict;
   public final Setting predictIncrease;
   public final Setting predictCollision;
   private final Setting lay;
   private final Setting forceLay;
   public final Setting allowedFails;
   private final Setting zeroTick;
   public final Setting tBreakerMain;
   private final Setting tBreakerMode;
   private final Setting tBreakerSwap;
   private final Setting tBreakerOnlySur;
   private final Setting tBreakerGround;
   public final Setting bBreakerMain;
   private final Setting bBreakerMode;
   private final Setting bBreakerSwap;
   private final Setting bBreakerOnlySur;
   private final Setting bBreakerGround;
   public final Setting sBreakerMain;
   private final Setting sBreakerMode;
   private final Setting sBreakerOnlySur;
   private final Setting sBreakerGround;
   private final Setting pauseOnUse;
   private final Setting pauseOnCA;
   private final Setting hurtTime;
   private final Setting bedRefill;
   private final Setting bedSlot;
   private final Setting render;
   private final Setting shapeMode;
   private final Setting lineColor;
   private final Setting sideColor;
   private final Setting textColor;
   private final Setting notifications;
   public static ExecutorService cached = Executors.newCachedThreadPool();
   AtomicDouble bestDamage;
   private class_2338 finalPos;
   int placeTicks;
   int countTicks;
   int failTimes;
   public static class_1657 target;
   private final ArrayList targets;
   private CardinalDirection placeDirection;
   double offsetTargetDamage;
   boolean smartLay;
   class_2338 prevBreakPos;
   Boolean Boolean;
   private final Task breakTask;
   private final Task infoTask;
   private final Task stageTask;
   private final Task secondStageTask;
   private List strings;
   private final Pool renderTextPool;
   private final List renderTexts;
   private final Pool renderBlockPool;
   private final List renderBlocks;
   private final Pool renderBreakPool;
   private final List renderBreaks;

   public BedBombV2() {
      super(LemonClient.Combat, "Bed Bomb V2", "Automatically places and explodes beds in the Nether and End :massivetroll:.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPredict = this.settings.createGroup("Predict");
      this.sgPvP = this.settings.createGroup("PvP");
      this.sgTrapBreaker = this.settings.createGroup("Trap Breaker");
      this.sgBurrowBreaker = this.settings.createGroup("Burrow Breaker");
      this.sgStringBreaker = this.settings.createGroup("String Breaker");
      this.sgOther = this.settings.createGroup("Other");
      this.sgBedRefill = this.settings.createGroup("Bed Re-fill");
      this.sgRender = this.settings.createGroup("Render");
      this.sgNone = this.settings.createGroup("");
      this.placeDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The delay between placing beds in ticks.")).defaultValue(10)).sliderRange(0, 20).build());
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The range at which players can be targeted.")).defaultValue(15.0).sliderRange(1.0, 25.0).build());
      this.placeRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("The range at which beds can be placed.")).defaultValue(4.5).sliderRange(1.0, 7.0).build());
      this.minTargetDamage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-target-damage")).description("The minimum damage to inflict on your target.")).defaultValue(7.0).range(0.0, 36.0).sliderMax(36.0).build());
      this.maxSelfDamage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-self-damage")).description("The maximum damage to inflict on yourself.")).defaultValue(4.0).range(0.0, 36.0).sliderMax(36.0).build());
      this.antiFriendPop = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-friend-pop")).description("Prevents from popping friends.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-damage")).description("Maximum damage that beds can deal to your friends.")).defaultValue(6.0).range(0.0, 36.0).sliderMax(36.0);
      Setting var10003 = this.antiFriendPop;
      Objects.requireNonNull(var10003);
      this.maxFriendDamage = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.ignoreTerrain = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-terrain")).description("Completely ignores terrain if it can be blown up by beds.")).defaultValue(true)).build());
      this.strictDirection = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("strict-direction")).description("Only places beds in the direction you are facing.")).defaultValue(false)).build());
      this.debug = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("debug")).description("Sends info in chat about calculation.")).defaultValue(false)).build());
      this.predict = this.sgPredict.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-position")).description("Predicts target position.")).defaultValue(true)).build());
      var10001 = this.sgPredict;
      IntSetting.Builder var1 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("predict-increase")).description("Increasing range from predicted position to target.")).defaultValue(2)).sliderRange(1, 4).min(1).max(4);
      var10003 = this.predict;
      Objects.requireNonNull(var10003);
      this.predictIncrease = var10001.add(((IntSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgPredict;
      BoolSetting.Builder var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-collision")).description("Whether to consider collision when predicting.")).defaultValue(true);
      var10003 = this.predict;
      Objects.requireNonNull(var10003);
      this.predictCollision = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      this.lay = this.sgPvP.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-lay")).description("Esu enemy.")).defaultValue(true)).build());
      this.forceLay = this.sgPvP.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-lay")).description("AutoLay starts work if the keybind is pressed. Useful agains player with bed instamine.")).defaultValue(Keybind.none())).build());
      var10001 = this.sgPvP;
      var1 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("fail-times")).description("How much AutoLay fails can be dealed.")).defaultValue(2)).sliderRange(0, 10).min(0).max(10);
      var10003 = this.lay;
      Objects.requireNonNull(var10003);
      this.allowedFails = var10001.add(((IntSetting.Builder)var1.visible(var10003::get)).build());
      this.zeroTick = this.sgPvP.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("zero-tick")).description("Tries to zero tick your target faster.")).defaultValue(true)).build());
      this.tBreakerMain = this.sgTrapBreaker.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("trap-breaker")).description("Breaks targets self trap and prevent re-trapping.")).defaultValue(false)).build());
      var10001 = this.sgTrapBreaker;
      EnumSetting.Builder var3 = (EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mine-method")).defaultValue(BedBombV2.MineMode.Client);
      var10003 = this.tBreakerMain;
      Objects.requireNonNull(var10003);
      this.tBreakerMode = var10001.add(((EnumSetting.Builder)var3.visible(var10003::get)).build());
      var10001 = this.sgTrapBreaker;
      var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-swap")).description("Automatically switches to pickaxe slot.")).defaultValue(true);
      var10003 = this.tBreakerMain;
      Objects.requireNonNull(var10003);
      this.tBreakerSwap = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgTrapBreaker;
      var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("surround-only")).description("Works only while player is surrounded.")).defaultValue(true);
      var10003 = this.tBreakerMain;
      Objects.requireNonNull(var10003);
      this.tBreakerOnlySur = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgTrapBreaker;
      var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Works only while player is standing on ground.")).defaultValue(true);
      var10003 = this.tBreakerMain;
      Objects.requireNonNull(var10003);
      this.tBreakerGround = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      this.bBreakerMain = this.sgBurrowBreaker.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("burrow-breaker")).description("Breaks targets burrow and prevent re-burrowing.")).defaultValue(false)).build());
      var10001 = this.sgBurrowBreaker;
      var3 = (EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mine-method")).defaultValue(BedBombV2.MineMode.Client);
      var10003 = this.bBreakerMain;
      Objects.requireNonNull(var10003);
      this.bBreakerMode = var10001.add(((EnumSetting.Builder)var3.visible(var10003::get)).build());
      var10001 = this.sgBurrowBreaker;
      var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-swap")).description("Automatically switches to pickaxe slot.")).defaultValue(true);
      var10003 = this.bBreakerMain;
      Objects.requireNonNull(var10003);
      this.bBreakerSwap = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgBurrowBreaker;
      var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("surround-only")).description("Works only while player is surrounded.")).defaultValue(true);
      var10003 = this.bBreakerMain;
      Objects.requireNonNull(var10003);
      this.bBreakerOnlySur = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgBurrowBreaker;
      var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Works only while player is standing on ground.")).defaultValue(true);
      var10003 = this.bBreakerMain;
      Objects.requireNonNull(var10003);
      this.bBreakerGround = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      this.sBreakerMain = this.sgStringBreaker.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("string-breaker")).description("Breaks strings around target.")).defaultValue(false)).build());
      var10001 = this.sgStringBreaker;
      var3 = (EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mine-method")).defaultValue(BedBombV2.MineMode.Packet);
      var10003 = this.sBreakerMain;
      Objects.requireNonNull(var10003);
      this.sBreakerMode = var10001.add(((EnumSetting.Builder)var3.visible(var10003::get)).build());
      var10001 = this.sgStringBreaker;
      var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("surround-only")).description("Works only while player is surrounded.")).defaultValue(true);
      var10003 = this.sBreakerMain;
      Objects.requireNonNull(var10003);
      this.sBreakerOnlySur = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgStringBreaker;
      var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Works only while player is standing on ground.")).defaultValue(true);
      var10003 = this.sBreakerMain;
      Objects.requireNonNull(var10003);
      this.sBreakerGround = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      this.pauseOnUse = this.sgOther.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-use")).description("Pauses while using items.")).defaultValue(true)).build());
      this.pauseOnCA = this.sgOther.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-CA")).description("Pauses while Crystal Aura is activated.")).defaultValue(false)).build());
      this.hurtTime = this.sgOther.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hurt-time")).description("Place only while target can recieve damage. Not recommended to use this.")).defaultValue(false)).build());
      this.bedRefill = this.sgBedRefill.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("bed-refill")).description("Moves beds into a selected hotbar slot.")).defaultValue(true)).build());
      var10001 = this.sgBedRefill;
      var1 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("bed-slot")).description("The slot auto move moves beds to.")).defaultValue(7)).min(1).max(9).sliderMin(1).sliderMax(9);
      var10003 = this.bedRefill;
      Objects.requireNonNull(var10003);
      this.bedSlot = var10001.add(((IntSetting.Builder)var1.visible(var10003::get)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders the block where it is placing a bed.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color for positions to be placed.")).defaultValue(new SettingColor(255, 0, 170, 90)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color for positions to be placed.")).defaultValue(new SettingColor(255, 0, 170, 10)).build());
      this.textColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Text Color")).description("The text color for positions to be mined.")).defaultValue(new SettingColor(255, 0, 170, 10)).build());
      this.notifications = this.sgNone.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Notifications")).defaultValue(Notifications.Mode.Chat)).build());
      this.bestDamage = new AtomicDouble(0.0);
      this.finalPos = null;
      this.targets = new ArrayList();
      this.breakTask = new Task();
      this.infoTask = new Task();
      this.stageTask = new Task();
      this.secondStageTask = new Task();
      this.strings = new ArrayList();
      this.renderTextPool = new Pool(BedUtils.RenderText::new);
      this.renderTexts = new ArrayList();
      this.renderBlockPool = new Pool(BedUtils.RenderBlock::new);
      this.renderBlocks = new ArrayList();
      this.renderBreakPool = new Pool(BedUtils.RenderBreak::new);
      this.renderBreaks = new ArrayList();
   }

   public void onActivate() {
      this.infoTask.reset();
      this.breakTask.reset();
      this.stageTask.reset();
      this.secondStageTask.reset();
      this.failTimes = -1;
      this.finalPos = null;
      this.placeDirection = null;
      this.smartLay = true;
      this.countTicks = (Integer)this.placeDelay.get();
      this.placeTicks = 0;
      this.bestDamage.set(0.0);
      Iterator var1 = this.renderBlocks.iterator();

      while(var1.hasNext()) {
         BedUtils.RenderBlock renderBlock = (BedUtils.RenderBlock)var1.next();
         this.renderBlockPool.free(renderBlock);
      }

      this.renderBlocks.clear();
      var1 = this.renderBreaks.iterator();

      while(var1.hasNext()) {
         BedUtils.RenderBreak renderBreak = (BedUtils.RenderBreak)var1.next();
         this.renderBreakPool.free(renderBreak);
      }

      this.renderBreaks.clear();
      var1 = this.renderTexts.iterator();

      while(var1.hasNext()) {
         BedUtils.RenderText renderText = (BedUtils.RenderText)var1.next();
         this.renderTextPool.free(renderText);
      }

      this.renderTexts.clear();
   }

   public void onDeactivate() {
      this.bestDamage.set(0.0);
      Iterator var1 = this.renderBlocks.iterator();

      while(var1.hasNext()) {
         BedUtils.RenderBlock renderBlock = (BedUtils.RenderBlock)var1.next();
         this.renderBlockPool.free(renderBlock);
      }

      this.renderBlocks.clear();
      var1 = this.renderBreaks.iterator();

      while(var1.hasNext()) {
         BedUtils.RenderBreak renderBreak = (BedUtils.RenderBreak)var1.next();
         this.renderBreakPool.free(renderBreak);
      }

      this.renderBreaks.clear();
      var1 = this.renderTexts.iterator();

      while(var1.hasNext()) {
         BedUtils.RenderText renderText = (BedUtils.RenderText)var1.next();
         this.renderTextPool.free(renderText);
      }

      this.renderTexts.clear();
   }

   @EventHandler(
      priority = 1200
   )
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1687.method_8597().comp_648()) {
         this.sendDisableMsg("You can't blow up beds in this dimension.");
         this.toggle();
      } else {
         this.countTicks = (Integer)this.placeDelay.get();
         --this.placeTicks;
         this.renderBlocks.forEach(BedUtils.RenderBlock::tick);
         this.renderBlocks.removeIf((renderBlock) -> {
            return renderBlock.ticks <= 0;
         });
         this.renderBreaks.forEach(BedUtils.RenderBreak::tick);
         this.renderBreaks.removeIf((renderBreak) -> {
            return renderBreak.ticks <= 0;
         });
         this.renderTexts.forEach(BedUtils.RenderText::tick);
         this.renderTexts.removeIf((renderText) -> {
            return renderText.ticks <= 0;
         });
         if (!(Boolean)this.pauseOnCA.get() || !((CrystalAura)Modules.get().get(CrystalAura.class)).isActive() && !((AutoCrystal)Modules.get().get(AutoCrystal.class)).isActive() && !((AutoCrystalPlus)Modules.get().get(AutoCrystalPlus.class)).isActive()) {
            target = TargetUtils.getPlayerTarget((Double)this.targetRange.get(), SortPriority.LowestDistance);
            if (!TargetUtils.isBadTarget(target, (Double)this.targetRange.get()) && (!(Boolean)this.pauseOnUse.get() || !this.mc.field_1724.method_6115())) {
               int i;
               if (((Boolean)this.lay.get() || ((Keybind)this.forceLay.get()).isPressed()) && EntityInfo.isSurrounded(target) && !EntityInfo.isFaceTrapped(target)) {
                  this.calculateHolePos();
                  if (this.placeDirection != null && this.finalPos != null) {
                     i = (Integer)this.placeDelay.get() <= 9 ? 0 : (Integer)this.placeDelay.get() / 2;
                     if (this.failTimes >= (Integer)this.allowedFails.get() || this.smartLay) {
                        i = 0;
                     }

                     this.countTicks = (Integer)this.placeDelay.get() - i;
                     if (this.placeTicks <= 0) {
                        this.bedRefill();
                        this.doHolePlace();
                        this.placeTicks = this.countTicks;
                     }
                  }

               } else {
                  if (!EntityInfo.isFaceTrapped(target)) {
                     this.smartLay = true;
                     this.failTimes = -1;
                  }

                  boolean sHurt;
                  if (target.field_6235 != 0 && (Boolean)this.hurtTime.get()) {
                     sHurt = false;
                  } else {
                     sHurt = true;
                  }

                  if (EntityUtils.getTotalHealth(target) <= 11.0F && (Boolean)this.zeroTick.get() && !EntityInfo.isSurrounded(target)) {
                     i = (Integer)this.placeDelay.get() <= 9 ? 0 : (Integer)this.placeDelay.get() / 2;
                     this.countTicks = (Integer)this.placeDelay.get() - i;
                     sHurt = true;
                  }

                  cached.execute(this::calculatePos);
                  if (this.placeTicks <= 0 && sHurt) {
                     if (this.finalPos == null || this.placeDirection == null) {
                        return;
                     }

                     this.bedRefill();
                     this.doPlace();
                     this.placeTicks = this.countTicks;
                  }

               }
            }
         }
      }
   }

   @EventHandler
   private void onPreSlowTick(TickEvent.Pre event) {
      if (!TargetUtils.isBadTarget(target, (Double)this.targetRange.get()) && (!(Boolean)this.pauseOnUse.get() || !this.mc.field_1724.method_6115())) {
         class_2338 trapBp;
         if ((Boolean)this.bBreakerMain.get() && (!(Boolean)this.bBreakerGround.get() || this.mc.field_1724.method_24828()) && (!(Boolean)this.bBreakerOnlySur.get() || EntityInfo.isSurrounded(this.mc.field_1724)) && BedUtils.shouldBurrowBreak()) {
            trapBp = target.method_24515();
            this.infoTask.run(() -> {
               this.renderBreaks.add(((BedUtils.RenderBreak)this.renderBreakPool.get()).set(trapBp));
               switch ((Notifications.Mode)this.notifications.get()) {
                  case Chat:
                     this.warning("Burrow Breaker triggered!", new Object[0]);
                     break;
                  case Notification:
                     Managers.NOTIFICATION.warn(this.title, "Burrow Breaker triggered!");
                     break;
                  case Toast:
                     ToastNotifications.addToast("Burrow Breaker triggered!");
               }

            });
            switch ((MineMode)this.bBreakerMode.get()) {
               case Packet:
                  BedUtils.packetMine(trapBp, (Boolean)this.bBreakerSwap.get(), this.breakTask);
                  break;
               case Client:
                  BedUtils.normalMine(trapBp, (Boolean)this.bBreakerSwap.get());
            }

         } else {
            this.stageTask.run(() -> {
               this.infoTask.reset();
               this.breakTask.reset();
            });
            if ((Boolean)this.tBreakerMain.get() && (!(Boolean)this.tBreakerGround.get() || this.mc.field_1724.method_24828()) && (!(Boolean)this.tBreakerOnlySur.get() || EntityInfo.isSurrounded(this.mc.field_1724)) && BedUtils.shouldTrapBreak()) {
               trapBp = BedUtils.getTrapBlock(target, 4.5);
               this.infoTask.run(() -> {
                  this.renderBreaks.add(((BedUtils.RenderBreak)this.renderBreakPool.get()).set(trapBp));
                  switch ((Notifications.Mode)this.notifications.get()) {
                     case Chat:
                        this.warning("Trap Breaker triggered!", new Object[0]);
                        break;
                     case Notification:
                        Managers.NOTIFICATION.warn(this.title, "Trap Breaker triggered!");
                        break;
                     case Toast:
                        ToastNotifications.addToast("Trap Breaker triggered!");
                  }

               });
               switch ((MineMode)this.tBreakerMode.get()) {
                  case Packet:
                     BedUtils.packetMine(trapBp, (Boolean)this.tBreakerSwap.get(), this.breakTask);
                     break;
                  case Client:
                     BedUtils.normalMine(trapBp, (Boolean)this.tBreakerSwap.get());
               }

            } else {
               this.secondStageTask.run(() -> {
                  this.infoTask.reset();
                  this.breakTask.reset();
               });
               if ((Boolean)this.sBreakerMain.get() && (!(Boolean)this.sBreakerGround.get() || this.mc.field_1724.method_24828()) && (!(Boolean)this.sBreakerOnlySur.get() || EntityInfo.isSurrounded(this.mc.field_1724)) && BedUtils.shouldStringBreak()) {
                  this.strings.clear();
                  CardinalDirection[] var2 = CardinalDirection.values();
                  int var3 = var2.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                     CardinalDirection dir = var2[var4];
                     if (!(Boolean)this.strictDirection.get() || dir.toDirection() == this.mc.field_1724.method_5735() || dir.toDirection().method_10153() == this.mc.field_1724.method_5735()) {
                        class_2338 cPos = target.method_24515().method_10084();
                        if (this.mc.field_1687.method_8320(cPos).method_26204().method_8389().equals(class_1802.field_8276) && this.mc.field_1724.method_19538().method_1022(BlockInfo.getCenterVec3d(cPos)) < 4.5) {
                           this.strings.add(cPos);
                        }

                        if (this.mc.field_1687.method_8320(cPos.method_10093(dir.toDirection())).method_26204().method_8389().equals(class_1802.field_8276) && this.mc.field_1724.method_19538().method_1022(BlockInfo.getCenterVec3d(cPos.method_10093(dir.toDirection()))) < 4.5) {
                           this.strings.add(cPos.method_10093(dir.toDirection()));
                        }
                     }
                  }

                  if (!this.strings.isEmpty()) {
                     this.infoTask.run(() -> {
                        switch ((Notifications.Mode)this.notifications.get()) {
                           case Chat:
                              this.info("String Breaker triggered!", new Object[0]);
                              break;
                           case Notification:
                              Managers.NOTIFICATION.info(this.title, "String Breaker triggered!");
                              break;
                           case Toast:
                              ToastNotifications.addToast("String Breaker triggered!");
                        }

                     });
                     Iterator var7 = this.strings.iterator();

                     while(var7.hasNext()) {
                        class_2338 p = (class_2338)var7.next();
                        this.renderTexts.add(((BedUtils.RenderText)this.renderTextPool.get()).set(p, "String"));
                        switch ((MineMode)this.sBreakerMode.get()) {
                           case Packet:
                              BedUtils.packetMine(p, false, this.breakTask);
                              break;
                           default:
                              BedUtils.normalMine(p, false);
                        }
                     }
                  }
               }

               this.secondStageTask.reset();
               this.stageTask.reset();
               this.infoTask.reset();
               this.breakTask.reset();
            }
         }
      }
   }

   @EventHandler
   public void onBreakPacket(PacketEvent.Receive event) {
      if ((Boolean)this.lay.get() && this.mc.field_1687 != null && this.mc.field_1724 != null && target != null && this.finalPos != null && this.placeDirection != null) {
         if (event.packet instanceof class_2620) {
            class_2620 packet = (class_2620)event.packet;
            class_2338 packetBp = packet.method_11277();
            if (packetBp.equals(this.prevBreakPos) && packet.method_11278() > 0) {
               return;
            }

            this.Boolean = BlockInfo.getBlock(packetBp) instanceof class_2244;
            if (this.Boolean && packetBp.equals(this.finalPos)) {
               this.smartLay = false;
            } else if (this.Boolean && packetBp.equals(this.finalPos.method_10093(this.placeDirection.toDirection()))) {
               this.smartLay = false;
            }

            this.prevBreakPos = packetBp;
         }

      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         this.renderBlocks.sort(Comparator.comparingInt((o) -> {
            return -o.ticks;
         }));
         this.renderBlocks.forEach((renderBlock) -> {
            renderBlock.render(event, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get());
         });
         this.renderBreaks.sort(Comparator.comparingInt((o) -> {
            return -o.ticks;
         }));
         this.renderBreaks.forEach((renderBreak) -> {
            renderBreak.render(event, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get());
         });
      }
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      if ((Boolean)this.render.get()) {
         this.renderTexts.sort(Comparator.comparingInt((o) -> {
            return -o.ticks;
         }));
         this.renderTexts.forEach((renderText) -> {
            renderText.render(event, (Color)this.textColor.get());
         });
      }
   }

   private void bedRefill() {
      if ((Boolean)this.bedRefill.get()) {
         FindItemResult bedItem = InvUtils.find((itemStack) -> {
            return itemStack.method_7909() instanceof class_1748;
         });
         if (bedItem.found() && bedItem.slot() != (Integer)this.bedSlot.get() - 1) {
            InvUtils.move().from(bedItem.slot()).toHotbar((Integer)this.bedSlot.get() - 1);
            this.mc.method_1562().method_2883(new class_2868((Integer)this.bedSlot.get() - 1));
         }
      }

   }

   private void doPlace() {
      FindItemResult bedItem = InvUtils.findInHotbar((itemStack) -> {
         return itemStack.method_7909() instanceof class_1748;
      });

      assert bedItem.isHotbar();

      class_3965 placeResult = new class_3965(Vec3dInfo.closestVec3d(this.finalPos), class_2350.field_11036, this.finalPos, false);
      class_3965 breakResult = new class_3965(Vec3dInfo.closestVec3d(this.finalPos), class_2350.field_11036, this.finalPos, false);
      double var10000;
      switch (this.placeDirection) {
         case North:
            var10000 = 180.0;
            break;
         case East:
            var10000 = -90.0;
            break;
         case West:
            var10000 = 90.0;
            break;
         case South:
            var10000 = 0.0;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      double y = var10000;
      double p = Rotations.getPitch(Vec3dInfo.closestVec3d(this.finalPos));
      Rotations.rotate(y, p, 1000000, () -> {
         int prevSlot = this.mc.field_1724.method_31548().field_7545;
         InvUtils.swap(bedItem.slot(), false);
         this.mc.method_1562().method_2883(new class_2885(class_1268.field_5808, placeResult, 0));
         this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5810, breakResult);
         this.mc.field_1724.method_31548().field_7545 = prevSlot;
         this.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
         this.mc.method_1562().method_2883(new class_2879(class_1268.field_5810));
         this.renderBlocks.add(((BedUtils.RenderBlock)this.renderBlockPool.get()).set(this.finalPos, this.placeDirection));
         this.bestDamage.set(0.0);
         this.finalPos = null;
         this.placeDirection = null;
      });
   }

   private void doHolePlace() {
      FindItemResult bedItem = InvUtils.findInHotbar((itemStack) -> {
         return itemStack.method_7909() instanceof class_1748;
      });
      if (this.finalPos != null && bedItem.isHotbar()) {
         if (!(this.mc.field_1687.method_8320(this.finalPos).method_26204() instanceof class_2244)) {
            ++this.failTimes;
         }

         class_3965 placeResult = new class_3965(Vec3dInfo.closestVec3d(this.finalPos), class_2350.field_11036, this.finalPos, false);
         class_3965 breakResult = new class_3965(Vec3dInfo.closestVec3d(this.finalPos), class_2350.field_11036, this.finalPos, false);
         double var10000;
         switch (this.placeDirection) {
            case North:
               var10000 = 180.0;
               break;
            case East:
               var10000 = -90.0;
               break;
            case West:
               var10000 = 90.0;
               break;
            case South:
               var10000 = 0.0;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         double y = var10000;
         double p = Rotations.getPitch(Vec3dInfo.closestVec3d(this.finalPos));
         Rotations.rotate(y, p, () -> {
            int prevSlot = this.mc.field_1724.method_31548().field_7545;
            InvUtils.swap(bedItem.slot(), false);
            if (this.failTimes >= (Integer)this.allowedFails.get()) {
               this.mc.method_1562().method_2883(new class_2885(class_1268.field_5808, placeResult, 0));
               this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5810, breakResult);
            } else {
               this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5810, breakResult);
               this.mc.method_1562().method_2883(new class_2885(class_1268.field_5808, placeResult, 0));
            }

            this.mc.field_1724.method_31548().field_7545 = prevSlot;
            this.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
            this.mc.method_1562().method_2883(new class_2879(class_1268.field_5810));
            this.renderBlocks.add(((BedUtils.RenderBlock)this.renderBlockPool.get()).set(this.finalPos, this.placeDirection));
            this.bestDamage.set(0.0);
            this.finalPos = null;
            this.placeDirection = null;
         });
      }
   }

   private void calculateHolePos() {
      long startTime = System.currentTimeMillis();
      if ((Boolean)this.debug.get()) {
         this.debug("thread started");
      }

      class_2338 p = EntityInfo.getBlockPos(target).method_10084();
      double selfDMG = BedUtils.getDamage(this.mc.field_1724, BlockInfo.getCenterVec3d(p), false, false, 0, true);
      double targetDMG = BedUtils.getDamage(target, BlockInfo.getCenterVec3d(p), false, false, 0, true);
      if (BedUtils.canBed(p.method_10095(), p) && BlockInfo.isWithinRange(p.method_10095(), (Double)this.placeRange.get()) && selfDMG < (Double)this.maxSelfDamage.get() && targetDMG > (Double)this.minTargetDamage.get()) {
         this.finalPos = p.method_10095();
         this.placeDirection = CardinalDirection.South;
      } else if (BedUtils.canBed(p.method_10072(), p) && BlockInfo.isWithinRange(p.method_10072(), (Double)this.placeRange.get()) && selfDMG < (Double)this.maxSelfDamage.get() && targetDMG > (Double)this.minTargetDamage.get()) {
         this.finalPos = p.method_10072();
         this.placeDirection = CardinalDirection.North;
      } else if (BedUtils.canBed(p.method_10078(), p) && BlockInfo.isWithinRange(p.method_10078(), (Double)this.placeRange.get()) && selfDMG < (Double)this.maxSelfDamage.get() && targetDMG > (Double)this.minTargetDamage.get()) {
         this.finalPos = p.method_10078();
         this.placeDirection = CardinalDirection.West;
      } else if (BedUtils.canBed(p.method_10067(), p) && BlockInfo.isWithinRange(p.method_10067(), (Double)this.placeRange.get()) && selfDMG < (Double)this.maxSelfDamage.get() && targetDMG > (Double)this.minTargetDamage.get()) {
         this.finalPos = p.method_10067();
         this.placeDirection = CardinalDirection.East;
      }

      if ((Boolean)this.debug.get()) {
         this.debug("thread shutdown in " + (System.currentTimeMillis() - startTime) + "ms");
      }

   }

   private void calculatePos() {
      long startTime = System.currentTimeMillis();
      int radius = (int)this.mc.field_1724.method_5739(target);
      radius -= 2;
      if (radius < 2) {
         radius = 2;
      }

      if (radius > 6) {
         radius = 6;
      }

      ArrayList sphere = new ArrayList(BedUtils.getTargetSphere(target, radius, 3));
      CardinalDirection localDirection = null;
      class_2338 localPos = null;

      try {
         Iterator var7 = sphere.iterator();

         label93:
         while(true) {
            class_2338 p;
            do {
               do {
                  do {
                     if (!var7.hasNext()) {
                        break label93;
                     }

                     p = (class_2338)var7.next();
                     this.offsetTargetDamage = 0.0;
                  } while(this.intersectsWithEntities(p));
               } while(!Vec3dInfo.isWithinRange(BlockInfo.closestVec3d(p), (Double)this.placeRange.get()));
            } while(!BlockInfo.isReplaceable(p));

            CardinalDirection[] var9 = CardinalDirection.values();
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               CardinalDirection d = var9[var11];
               double targetDMG = BedUtils.getDamage(target, BlockInfo.getCenterVec3d(p.method_10093(d.toDirection())), (Boolean)this.predict.get(), (Boolean)this.predictCollision.get(), (Integer)this.predictIncrease.get(), (Boolean)this.ignoreTerrain.get());
               double selfDMG = BedUtils.getDamage(this.mc.field_1724, BlockInfo.getCenterVec3d(p.method_10093(d.toDirection())), (Boolean)this.predict.get(), (Boolean)this.predictCollision.get(), (Integer)this.predictIncrease.get(), (Boolean)this.ignoreTerrain.get());
               double friendDMG = 0.0;
               if ((Boolean)this.antiFriendPop.get()) {
                  Iterator var19 = this.mc.field_1687.method_18112().iterator();

                  while(var19.hasNext()) {
                     class_1297 entity = (class_1297)var19.next();
                     if (entity instanceof class_1657) {
                        class_1657 friend = (class_1657)entity;
                        if (Friends.get().isFriend(friend)) {
                           friendDMG = BedUtils.getDamage(friend, BlockInfo.getCenterVec3d(p.method_10093(d.toDirection())), false, false, 1, false);
                        }
                     }
                  }
               }

               if (BedUtils.canBed(p, p.method_10093(d.toDirection())) && !(selfDMG > (Double)this.maxSelfDamage.get()) && !(targetDMG < (Double)this.minTargetDamage.get()) && (friendDMG == 0.0 || !(friendDMG > (Double)this.maxFriendDamage.get()))) {
                  this.offsetTargetDamage = targetDMG;
                  if (this.offsetTargetDamage > this.bestDamage.get()) {
                     this.bestDamage.set(this.offsetTargetDamage);
                     localDirection = d;
                     localPos = p.method_10062();
                  }
               }
            }
         }
      } catch (Exception var22) {
         var22.fillInStackTrace();
      }

      if (localPos != null && localDirection != null) {
         this.finalPos = localPos;
         this.placeDirection = localDirection;
         if ((Boolean)this.debug.get()) {
            this.debug("thread shutdown in " + (System.currentTimeMillis() - startTime) + "ms");
         }

      }
   }

   private boolean intersectsWithEntities(class_2338 blockPos) {
      class_238 box = new class_238((double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260(), (double)(blockPos.method_10263() + 1), (double)blockPos.method_10264() + 0.6, (double)(blockPos.method_10260() + 1));
      return EntityUtils.intersectsWithEntity(box, (entity) -> {
         return entity instanceof class_1657 || entity instanceof class_1511 || entity instanceof class_1541;
      });
   }

   public String getInfoString() {
      return target != null ? target.method_7334().getName() : null;
   }

   public static enum MineMode {
      Packet,
      Client;

      // $FF: synthetic method
      private static MineMode[] $values() {
         return new MineMode[]{Packet, Client};
      }
   }
}
