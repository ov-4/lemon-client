package dev.lemonclient.addon.modules.combat;

import baritone.api.BaritoneAPI;
import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.render.RenderUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1493;
import net.minecraft.class_1501;
import net.minecraft.class_1560;
import net.minecraft.class_1590;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1829;
import net.minecraft.class_1934;
import net.minecraft.class_238;
import net.minecraft.class_2868;
import net.minecraft.class_3532;
import net.minecraft.class_6025;

public class KillAura extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgTargeting;
   private final SettingGroup sgTiming;
   private final SettingGroup sgRender;
   private final Setting weapon;
   private final Setting autoSwitch;
   private final Setting onlyOnClick;
   private final Setting onlyOnLook;
   private final Setting pauseOnCombat;
   private final Setting shieldMode;
   private final Setting entities;
   private final Setting sortPriority;
   public final Setting maxTargets;
   private final Setting range;
   private final Setting wallsRange;
   private final Setting ignoreBabies;
   private final Setting ignoreNamed;
   private final Setting ignorePassive;
   private final Setting ignoreTamed;
   private final Setting pauseOnLag;
   private final Setting pauseOnUse;
   private final Setting pauseOnCA;
   private final Setting tpsSync;
   private final Setting customDelay;
   private final Setting hitDelay;
   private final Setting switchDelay;
   private final Setting swing;
   private final Setting placeHand;
   private final Setting render;
   private final Setting renderMode;
   private final Setting boxLine;
   private final Setting boxSide;
   private final Setting lineColor;
   CrystalAura ca;
   public final List targets;
   private int switchTimer;
   private int hitTimer;
   private boolean wasPathing;

   public KillAura() {
      super(LemonClient.Combat, "Kill Aura Plus", "Attacks specified entities around you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgTargeting = this.settings.createGroup("Targeting");
      this.sgTiming = this.settings.createGroup("Timing");
      this.sgRender = this.settings.createGroup("Render");
      this.weapon = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("weapon")).description("Only attacks an entity when a specified weapon is in your hand.")).defaultValue(KillAura.Weapon.Both)).build());
      this.autoSwitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-switch")).description("Switches to your selected weapon when attacking the target.")).defaultValue(false)).build());
      this.onlyOnClick = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-click")).description("Only attacks when holding left click.")).defaultValue(false)).build());
      this.onlyOnLook = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-look")).description("Only attacks when looking at an entity.")).defaultValue(false)).build());
      this.pauseOnCombat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-baritone")).description("Freezes Baritone temporarily until you are finished attacking the entity.")).defaultValue(true)).build());
      this.shieldMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shield-mode")).description("Will try and use an axe to break target shields.")).defaultValue(KillAura.ShieldMode.Break)).visible(() -> {
         return (Boolean)this.autoSwitch.get() && this.weapon.get() != KillAura.Weapon.Axe;
      })).build());
      this.entities = this.sgTargeting.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to attack.")).onlyAttackable().defaultValue(new class_1299[]{class_1299.field_6097}).build());
      this.sortPriority = this.sgTargeting.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("How to filter targets within range.")).defaultValue(SortPriority.ClosestAngle)).build());
      this.maxTargets = this.sgTargeting.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-targets")).description("How many entities to target at once.")).defaultValue(1)).min(1).sliderRange(1, 5).visible(() -> {
         return !(Boolean)this.onlyOnLook.get();
      })).build());
      this.range = this.sgTargeting.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The maximum range the entity can be to attack it.")).defaultValue(4.5).min(0.0).sliderMax(6.0).build());
      this.wallsRange = this.sgTargeting.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("The maximum range the entity can be attacked through walls.")).defaultValue(3.5).min(0.0).sliderMax(6.0).build());
      this.ignoreBabies = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-babies")).description("Whether or not to attack baby variants of the entity.")).defaultValue(true)).build());
      this.ignoreNamed = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-named")).description("Whether or not to attack mobs with a name.")).defaultValue(false)).build());
      this.ignorePassive = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-passive")).description("Will only attack sometimes passive mobs if they are targeting you.")).defaultValue(true)).build());
      this.ignoreTamed = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-tamed")).description("Will avoid attacking mobs you tamed.")).defaultValue(false)).build());
      this.pauseOnLag = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-lag")).description("Pauses if the server is lagging.")).defaultValue(true)).build());
      this.pauseOnUse = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-use")).description("Does not attack while using an item.")).defaultValue(false)).build());
      this.pauseOnCA = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-CA")).description("Does not attack while CA is placing.")).defaultValue(true)).build());
      this.tpsSync = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("TPS Sync")).description("Tries to sync attack delay with the server's TPS.")).defaultValue(true)).build());
      this.customDelay = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-delay")).description("Use a custom delay instead of the vanilla cooldown.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgTiming;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("hit-delay")).description("How fast you hit the entity in ticks.")).defaultValue(11)).min(0).sliderMax(60);
      Setting var10003 = this.customDelay;
      Objects.requireNonNull(var10003);
      this.hitDelay = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.switchDelay = this.sgTiming.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("switch-delay")).description("How many ticks to wait before hitting an entity after switching hotbar slots.")).defaultValue(0)).min(0).sliderMax(10).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders your swing client-side.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var1 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.swing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Render")).defaultValue(false)).build());
      var10001 = this.sgRender;
      var1 = (EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Render Mode")).defaultValue(KillAura.RenderMode.Box);
      var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.renderMode = var10001.add(((EnumSetting.Builder)var1.visible(var10003::get)).build());
      this.boxLine = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Box Line")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 0, 0, 255)).visible(() -> {
         return ((RenderMode)this.renderMode.get()).equals(KillAura.RenderMode.Box);
      })).build());
      this.boxSide = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Box Side")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 0, 0, 30)).visible(() -> {
         return ((RenderMode)this.renderMode.get()).equals(KillAura.RenderMode.Box);
      })).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).visible(() -> {
         return ((RenderMode)this.renderMode.get()).equals(KillAura.RenderMode.FakeJello);
      })).build());
      this.ca = (CrystalAura)Modules.get().get(CrystalAura.class);
      this.targets = new ArrayList();
      this.wasPathing = false;
   }

   public boolean onAttack() {
      return false;
   }

   public void onDeactivate() {
      this.targets.clear();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1724.method_5805() && PlayerUtils.getGameMode() != class_1934.field_9219) {
         if (!(Boolean)this.pauseOnUse.get() || !this.mc.field_1761.method_2923() && !this.mc.field_1724.method_6115()) {
            if (!(Boolean)this.onlyOnClick.get() || this.mc.field_1690.field_1886.method_1434()) {
               if (!(TickRate.INSTANCE.getTimeSinceLastTick() >= 1.0F) || !(Boolean)this.pauseOnLag.get()) {
                  if (!(Boolean)this.pauseOnCA.get() || (!this.ca.isActive() || this.ca.kaTimer <= 0) && !Modules.get().isActive(AutoCrystalPlus.class) && !Modules.get().isActive(AutoCrystal.class)) {
                     class_1297 primary;
                     if ((Boolean)this.onlyOnLook.get()) {
                        primary = this.mc.field_1692;
                        if (primary == null) {
                           return;
                        }

                        if (!this.entityCheck(primary)) {
                           return;
                        }

                        this.targets.clear();
                        this.targets.add(this.mc.field_1692);
                     } else {
                        this.targets.clear();
                        TargetUtils.getList(this.targets, this::entityCheck, (SortPriority)this.sortPriority.get(), (Integer)this.maxTargets.get());
                     }

                     if (this.targets.isEmpty()) {
                        if (this.wasPathing) {
                           BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("resume");
                           this.wasPathing = false;
                        }

                     } else {
                        primary = (class_1297)this.targets.get(0);
                        if ((Boolean)this.autoSwitch.get()) {
                           Predicate var10000;
                           switch ((Weapon)this.weapon.get()) {
                              case Axe:
                                 var10000 = (stack) -> {
                                    return stack.method_7909() instanceof class_1743;
                                 };
                                 break;
                              case Sword:
                                 var10000 = (stack) -> {
                                    return stack.method_7909() instanceof class_1829;
                                 };
                                 break;
                              case Both:
                                 var10000 = (stack) -> {
                                    return stack.method_7909() instanceof class_1743 || stack.method_7909() instanceof class_1829;
                                 };
                                 break;
                              default:
                                 var10000 = (o) -> {
                                    return true;
                                 };
                           }

                           Predicate predicate = var10000;
                           FindItemResult weaponResult = InvUtils.findInHotbar(predicate);
                           if (this.shouldShieldBreak()) {
                              FindItemResult axeResult = InvUtils.findInHotbar((itemStack) -> {
                                 return itemStack.method_7909() instanceof class_1743;
                              });
                              if (axeResult.found()) {
                                 weaponResult = axeResult;
                              }
                           }

                           InvUtils.swap(weaponResult.slot(), false);
                        }

                        if (this.itemInHand()) {
                           if (SettingUtils.shouldRotate(RotationType.Attacking)) {
                              Managers.ROTATION.start(primary.method_5829(), (double)this.priority, RotationType.Attacking, (long)Objects.hash(new Object[]{this.name + "attacking"}));
                           }

                           if ((Boolean)this.pauseOnCombat.get() && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() && !this.wasPathing) {
                              BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("pause");
                              this.wasPathing = true;
                           }

                           if (this.delayCheck()) {
                              this.targets.forEach(this::attack);
                           }

                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get() && this.itemInHand()) {
         int ii;
         switch ((RenderMode)this.renderMode.get()) {
            case Box:
               for(ii = 0; ii < (this.targets.size() > (Integer)this.maxTargets.get() ? (Integer)this.maxTargets.get() : this.targets.size()); ++ii) {
                  event.renderer.box(((class_1297)this.targets.get(ii)).method_5829(), (Color)this.boxSide.get(), (Color)this.boxLine.get(), ShapeMode.Both, 0);
               }

               return;
            case FakeJello:
               for(ii = 0; ii < (this.targets.size() > (Integer)this.maxTargets.get() ? (Integer)this.maxTargets.get() : this.targets.size()); ++ii) {
                  RenderUtils.drawSigma(event.renderer, event.matrices, (class_1297)this.targets.get(ii), (Color)this.lineColor.get());
               }
         }
      }

   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (event.packet instanceof class_2868) {
         this.switchTimer = (Integer)this.switchDelay.get();
      }

   }

   private boolean shouldShieldBreak() {
      Iterator var1 = this.targets.iterator();

      while(var1.hasNext()) {
         class_1297 target = (class_1297)var1.next();
         if (target instanceof class_1657 player) {
            if (player.method_6061(this.mc.field_1687.method_48963().method_48802(this.mc.field_1724)) && this.shieldMode.get() == KillAura.ShieldMode.Break) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean entityCheck(class_1297 entity) {
      if (!entity.equals(this.mc.field_1724) && !entity.equals(this.mc.field_1719)) {
         if ((!(entity instanceof class_1309) || !((class_1309)entity).method_29504()) && entity.method_5805()) {
            class_238 hitbox = entity.method_5829();
            if (!PlayerUtils.isWithin(class_3532.method_15350(this.mc.field_1724.method_23317(), hitbox.field_1323, hitbox.field_1320), class_3532.method_15350(this.mc.field_1724.method_23318(), hitbox.field_1322, hitbox.field_1325), class_3532.method_15350(this.mc.field_1724.method_23321(), hitbox.field_1321, hitbox.field_1324), (Double)this.range.get())) {
               return false;
            } else if (!((Set)this.entities.get()).contains(entity.method_5864())) {
               return false;
            } else if ((Boolean)this.ignoreNamed.get() && entity.method_16914()) {
               return false;
            } else if (!PlayerUtils.canSeeEntity(entity) && !PlayerUtils.isWithin(entity, (Double)this.wallsRange.get())) {
               return false;
            } else {
               if ((Boolean)this.ignoreTamed.get() && entity instanceof class_6025) {
                  class_6025 tameable = (class_6025)entity;
                  if (tameable.method_6139() != null && tameable.method_6139().equals(this.mc.field_1724.method_5667())) {
                     return false;
                  }
               }

               if ((Boolean)this.ignorePassive.get()) {
                  if (entity instanceof class_1560) {
                     class_1560 enderman = (class_1560)entity;
                     if (!enderman.method_7076(this.mc.field_1724)) {
                        return false;
                     }
                  }

                  if (entity instanceof class_1590) {
                     class_1590 piglin = (class_1590)entity;
                     if (!piglin.method_7076(this.mc.field_1724)) {
                        return false;
                     }
                  }

                  if (entity instanceof class_1493) {
                     class_1493 wolf = (class_1493)entity;
                     if (!wolf.method_6510()) {
                        return false;
                     }
                  }

                  if (entity instanceof class_1501) {
                     class_1501 llama = (class_1501)entity;
                     if (!llama.method_6510()) {
                        return false;
                     }
                  }
               }

               if (entity instanceof class_1657) {
                  class_1657 player = (class_1657)entity;
                  if (player.method_7337()) {
                     return false;
                  }

                  if (!Friends.get().shouldAttack(player)) {
                     return false;
                  }

                  if (this.shieldMode.get() == KillAura.ShieldMode.Ignore && player.method_6061(this.mc.field_1687.method_48963().method_48802(this.mc.field_1724))) {
                     return false;
                  }
               }

               boolean var10000;
               if (entity instanceof class_1429) {
                  class_1429 animal = (class_1429)entity;
                  if ((Boolean)this.ignoreBabies.get() && animal.method_6109()) {
                     var10000 = false;
                     return var10000;
                  }
               }

               var10000 = true;
               return var10000;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean delayCheck() {
      if (this.switchTimer > 0) {
         --this.switchTimer;
         return false;
      } else {
         float delay = (Boolean)this.customDelay.get() ? (float)(Integer)this.hitDelay.get() : 0.5F;
         if ((Boolean)this.tpsSync.get()) {
            delay /= TickRate.INSTANCE.getTickRate() / 20.0F;
         }

         if ((Boolean)this.customDelay.get()) {
            if ((float)this.hitTimer < delay) {
               ++this.hitTimer;
               return false;
            } else {
               return true;
            }
         } else {
            return this.mc.field_1724.method_7261(delay) >= 1.0F;
         }
      }
   }

   private void attack(class_1297 target) {
      if (SettingUtils.shouldRotate(RotationType.Attacking)) {
         Managers.ROTATION.start(target.method_5829(), (double)this.priority, RotationType.Attacking, (long)Objects.hash(new Object[]{this.name + "attacking"}));
      }

      this.mc.field_1761.method_2918(this.mc.field_1724, target);
      if ((Boolean)this.swing.get()) {
         this.clientSwing((SwingHand)this.placeHand.get(), class_1268.field_5808);
      }

      this.hitTimer = 0;
   }

   private boolean itemInHand() {
      if (this.shouldShieldBreak()) {
         return this.mc.field_1724.method_6047().method_7909() instanceof class_1743;
      } else {
         boolean var10000;
         switch ((Weapon)this.weapon.get()) {
            case Axe:
               var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1743;
               break;
            case Sword:
               var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1829;
               break;
            case Both:
               var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1743 || this.mc.field_1724.method_6047().method_7909() instanceof class_1829;
               break;
            default:
               var10000 = true;
         }

         return var10000;
      }
   }

   public class_1297 getTarget() {
      return !this.targets.isEmpty() ? (class_1297)this.targets.get(0) : null;
   }

   public void onActivate() {
      super.onActivate();
   }

   public String getInfoString() {
      return !this.targets.isEmpty() ? EntityUtils.getName(this.getTarget()) : null;
   }

   public static enum Weapon {
      Sword,
      Axe,
      Both,
      Any;

      // $FF: synthetic method
      private static Weapon[] $values() {
         return new Weapon[]{Sword, Axe, Both, Any};
      }
   }

   public static enum ShieldMode {
      Ignore,
      Break,
      None;

      // $FF: synthetic method
      private static ShieldMode[] $values() {
         return new ShieldMode[]{Ignore, Break, None};
      }
   }

   public static enum RenderMode {
      Box,
      FakeJello;

      // $FF: synthetic method
      private static RenderMode[] $values() {
         return new RenderMode[]{Box, FakeJello};
      }
   }
}
