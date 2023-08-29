package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.timers.TimerUtils;
import dev.lemonclient.addon.utils.world.BlockInfo;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.LongJump;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1313;
import net.minecraft.class_2246;
import net.minecraft.class_243;
import net.minecraft.class_2708;
import org.joml.Vector2d;

public class StrafePlus extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgVanilla;
   private final SettingGroup sgNCP;
   private final SettingGroup sgPotion;
   private final SettingGroup sgPause;
   private final SettingGroup sgAC;
   private final Setting mode;
   private final Setting groundTimer;
   private final Setting airTimer;
   private final Setting autoSprint;
   private final Setting TPSSync;
   private final Setting vanillaSneakSpeed;
   private final Setting vanillaGroundSpeed;
   private final Setting vanillaAirSpeed;
   private final Setting rubberbandPause;
   private final Setting rubberbandTime;
   private final Setting ncpSpeed;
   private final Setting ncpSpeedLimit;
   private final Setting startingSpeed;
   private final Setting hopMode;
   private final Setting hopHeight;
   private final Setting jumpTime;
   private final Setting jumpedSlowDown;
   private final Setting resetDivisor;
   private final Setting applyJumpBoost;
   private final Setting applySpeed;
   private final Setting applySlowness;
   private final Setting longJumpPause;
   private final Setting flightPause;
   private final Setting eFlyPause;
   private final Setting inWater;
   private final Setting inLava;
   private final Setting whenSneaking;
   private final Setting hungerCheck;
   private final Setting webbedPause;
   private int stage;
   private double distance;
   private double speed;
   private long timer;
   private int rubberbandTicks;
   private boolean rubberbanded;
   private boolean sentMessage;
   private int jumpTicks;
   private boolean jumped;
   Timer timerClass;
   Anchor anchor;
   LongJump longJump;
   Flight flight;
   ElytraFly efly;

   public StrafePlus() {
      super(LemonClient.Combat, "Strafe+", "Increase speed and control.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgVanilla = this.settings.createGroup("Vanilla");
      this.sgNCP = this.settings.createGroup("NCP");
      this.sgPotion = this.settings.createGroup("Potions");
      this.sgPause = this.settings.createGroup("Pause");
      this.sgAC = this.settings.createGroup("Anti Cheat");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Behaviour of your movements.")).defaultValue(StrafePlus.Mode.Smart)).build());
      this.groundTimer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("ground-timer")).description("Ground timer override.")).defaultValue(1.0).sliderRange(0.001, 10.0).build());
      this.airTimer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("air-timer")).description("Air timer override.")).defaultValue(1.088).sliderRange(0.001, 10.0).build());
      this.autoSprint = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-sprint")).description("Makes you sprint if you are moving forward.")).defaultValue(false)).build());
      this.TPSSync = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("TPS-sync")).description("Tries to sync movement with the server's TPS.")).defaultValue(false)).build());
      this.vanillaSneakSpeed = this.sgVanilla.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vanilla-sneak-speed")).description("The speed in blocks per second (on ground and sneaking).")).defaultValue(2.6).min(0.0).sliderMax(20.0).visible(() -> {
         return this.mode.get() == StrafePlus.Mode.Vanilla;
      })).build());
      this.vanillaGroundSpeed = this.sgVanilla.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vanilla-ground-speed")).description("The speed in blocks per second (on ground).")).defaultValue(5.6).min(0.0).sliderMax(20.0).visible(() -> {
         return this.mode.get() == StrafePlus.Mode.Vanilla;
      })).build());
      this.vanillaAirSpeed = this.sgVanilla.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("vanilla-air-speed")).description("The speed in blocks per second (on air).")).defaultValue(6.0).min(0.0).sliderMax(20.0).visible(() -> {
         return this.mode.get() == StrafePlus.Mode.Vanilla;
      })).build());
      this.rubberbandPause = this.sgVanilla.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-rubberband")).description("Will pause Vanilla mode when you rubberband.")).defaultValue(false)).visible(() -> {
         return this.mode.get() == StrafePlus.Mode.Vanilla;
      })).build());
      this.rubberbandTime = this.sgVanilla.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("pause-time")).description("Pauses vanilla mode for x ticks when a rubberband is detected.")).defaultValue(30)).min(0).sliderMax(100).visible(() -> {
         return this.mode.get() == StrafePlus.Mode.Vanilla && (Boolean)this.rubberbandPause.get();
      })).build());
      this.ncpSpeed = this.sgNCP.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("NCP-speed")).description("The speed.")).defaultValue(2.0).min(0.0).sliderMax(3.0).visible(() -> {
         return this.mode.get() != StrafePlus.Mode.Vanilla;
      })).build());
      this.ncpSpeedLimit = this.sgNCP.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("speed-limit")).description("Limits your speed on servers with very strict anticheats.")).defaultValue(false)).visible(() -> {
         return this.mode.get() != StrafePlus.Mode.Vanilla;
      })).build());
      this.startingSpeed = this.sgNCP.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("starting-speed")).description("Initial speed when starting (recommended 1.18 on NCP, 1.080 on Smart).")).defaultValue(1.08).min(0.0).sliderMax(2.0).visible(() -> {
         return this.mode.get() != StrafePlus.Mode.Vanilla;
      })).build());
      this.hopMode = this.sgNCP.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("hop-mode")).description("Mode to use for the hop height.")).defaultValue(StrafePlus.HopMode.Auto)).visible(() -> {
         return this.mode.get() != StrafePlus.Mode.Vanilla;
      })).build());
      this.hopHeight = this.sgNCP.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("hop-height")).description("The hop intensity.")).defaultValue(0.401).min(0.0).sliderMax(1.0).visible(() -> {
         return this.hopMode.get() == StrafePlus.HopMode.Custom && this.mode.get() != StrafePlus.Mode.Vanilla;
      })).build());
      this.jumpTime = this.sgNCP.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("jump-time")).description("How many ticks to recognise that you have jumped for smart mode.")).defaultValue(20)).min(0).sliderMax(30).visible(() -> {
         return this.mode.get() == StrafePlus.Mode.Smart;
      })).build());
      this.jumpedSlowDown = this.sgNCP.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("jumped-slow-down")).description("How much to slow down by after jumping.")).defaultValue(0.76).min(0.0).sliderMax(1.0).visible(() -> {
         return this.mode.get() != StrafePlus.Mode.Vanilla;
      })).build());
      this.resetDivisor = this.sgNCP.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("reset-divisor")).description("Speed value get divided by this amount on rubberband or collision.")).defaultValue(159.0).min(0.0).sliderMax(200.0).visible(() -> {
         return this.mode.get() != StrafePlus.Mode.Vanilla;
      })).build());
      this.applyJumpBoost = this.sgPotion.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("jump-boost")).description("Apply jump boost effect if the player has it.")).defaultValue(true)).visible(() -> {
         return this.mode.get() != StrafePlus.Mode.Vanilla;
      })).build());
      this.applySpeed = this.sgPotion.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("speed-effect")).description("Apply speed effect if the player has it.")).defaultValue(true)).build());
      this.applySlowness = this.sgPotion.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("slowness-effect")).description("Apply slowness effect if the player has it.")).defaultValue(true)).build());
      this.longJumpPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-long-jump")).description("Pauses the module if long jump is active.")).defaultValue(false)).build());
      this.flightPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-flight")).description("Pauses the module if flight is active.")).defaultValue(false)).build());
      this.eFlyPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-elytra-fly")).description("Pauses the module if elytra fly is active.")).defaultValue(false)).build());
      this.inWater = this.sgAC.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("in-water")).description("Uses speed when in water.")).defaultValue(false)).build());
      this.inLava = this.sgAC.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("in-lava")).description("Uses speed when in lava.")).defaultValue(false)).build());
      this.whenSneaking = this.sgAC.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("when-sneaking")).description("Uses speed when sneaking.")).defaultValue(false)).build());
      this.hungerCheck = this.sgAC.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hunger-check")).description("Pauses when hunger reaches 3 or less drumsticks.")).defaultValue(true)).build());
      this.webbedPause = this.sgAC.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("pause-on-webbed")).description("Pauses when you are webbed.")).defaultValue(StrafePlus.WebbedPause.OnAir)).build());
      this.timer = 0L;
      this.timerClass = (Timer)Modules.get().get(Timer.class);
      this.anchor = (Anchor)Modules.get().get(Anchor.class);
      this.longJump = (LongJump)Modules.get().get(LongJump.class);
      this.flight = (Flight)Modules.get().get(Flight.class);
      this.efly = (ElytraFly)Modules.get().get(ElytraFly.class);
   }

   public void onDeactivate() {
      this.timerClass.setOverride(1.0);
   }

   @EventHandler
   private void onPlayerMove(PlayerMoveEvent event) {
      if (event.type == class_1313.field_6308 && !this.mc.field_1724.method_6128() && !this.mc.field_1724.method_6101() && this.mc.field_1724.method_5854() == null) {
         if ((Boolean)this.whenSneaking.get() || !this.mc.field_1724.method_5715()) {
            if ((Boolean)this.inWater.get() || !this.mc.field_1724.method_5799()) {
               if ((Boolean)this.inLava.get() || !this.mc.field_1724.method_5771()) {
                  if (!(Boolean)this.hungerCheck.get() || this.mc.field_1724.method_7344().method_7586() > 6) {
                     if (!(Boolean)this.longJumpPause.get() || !this.longJump.isActive()) {
                        if (!(Boolean)this.flightPause.get() || !this.flight.isActive()) {
                           if (!(Boolean)this.eFlyPause.get() || !this.efly.isActive()) {
                              if (!((HoleSnap)Modules.get().get(HoleSnap.class)).isActive()) {
                                 if (!EntityInfo.isWebbed(this.mc.field_1724) || this.webbedPause.get() != StrafePlus.WebbedPause.Always) {
                                    if (!EntityInfo.isWebbed(this.mc.field_1724) || this.mc.field_1724.method_24828() || this.webbedPause.get() != StrafePlus.WebbedPause.OnAir) {
                                       if (this.mc.field_1724.method_24828()) {
                                          this.timerClass.setOverride(PlayerUtils.isMoving() ? (Double)this.groundTimer.get() * TimerUtils.getTPSMatch((Boolean)this.TPSSync.get()) : 1.0);
                                       } else {
                                          this.timerClass.setOverride(PlayerUtils.isMoving() ? (Double)this.airTimer.get() * TimerUtils.getTPSMatch((Boolean)this.TPSSync.get()) : 1.0);
                                       }

                                       double velX;
                                       double velZ;
                                       if (this.mode.get() == StrafePlus.Mode.Vanilla && !this.rubberbanded) {
                                          class_243 vel;
                                          double value;
                                          if (this.mc.field_1724.method_24828()) {
                                             if (this.mc.field_1724.method_5715()) {
                                                vel = PlayerUtils.getHorizontalVelocity((Double)this.vanillaSneakSpeed.get());
                                                velX = vel.method_10216();
                                                velZ = vel.method_10215();
                                                if (this.mc.field_1724.method_6059(class_1294.field_5904) && (Boolean)this.applySpeed.get()) {
                                                   value = (double)(this.mc.field_1724.method_6112(class_1294.field_5904).method_5578() + 1) * 0.205;
                                                   velX += velX * value;
                                                   velZ += velZ * value;
                                                }

                                                if (this.mc.field_1724.method_6059(class_1294.field_5909) && (Boolean)this.applySlowness.get()) {
                                                   value = (double)(this.mc.field_1724.method_6112(class_1294.field_5909).method_5578() + 1) * 0.205;
                                                   velX -= velX * value;
                                                   velZ -= velZ * value;
                                                }

                                                if (this.anchor.isActive() && this.anchor.controlMovement) {
                                                   velX = this.anchor.deltaX;
                                                   velZ = this.anchor.deltaZ;
                                                }

                                                ((IVec3d)event.movement).set(velX, event.movement.field_1351, velZ);
                                             } else {
                                                vel = PlayerUtils.getHorizontalVelocity((Double)this.vanillaGroundSpeed.get());
                                                velX = vel.method_10216();
                                                velZ = vel.method_10215();
                                                if (this.mc.field_1724.method_6059(class_1294.field_5904) && (Boolean)this.applySpeed.get()) {
                                                   value = (double)(this.mc.field_1724.method_6112(class_1294.field_5904).method_5578() + 1) * 0.205;
                                                   velX += velX * value;
                                                   velZ += velZ * value;
                                                }

                                                if (this.mc.field_1724.method_6059(class_1294.field_5909) && (Boolean)this.applySlowness.get()) {
                                                   value = (double)(this.mc.field_1724.method_6112(class_1294.field_5909).method_5578() + 1) * 0.205;
                                                   velX -= velX * value;
                                                   velZ -= velZ * value;
                                                }

                                                Anchor anchor = (Anchor)Modules.get().get(Anchor.class);
                                                if (anchor.isActive() && anchor.controlMovement) {
                                                   velX = anchor.deltaX;
                                                   velZ = anchor.deltaZ;
                                                }

                                                ((IVec3d)event.movement).set(velX, event.movement.field_1351, velZ);
                                             }
                                          } else {
                                             vel = PlayerUtils.getHorizontalVelocity((Double)this.vanillaAirSpeed.get());
                                             velX = vel.method_10216();
                                             velZ = vel.method_10215();
                                             if (this.mc.field_1724.method_6059(class_1294.field_5904) && (Boolean)this.applySpeed.get()) {
                                                value = (double)(this.mc.field_1724.method_6112(class_1294.field_5904).method_5578() + 1) * 0.205;
                                                velX += velX * value;
                                                velZ += velZ * value;
                                             }

                                             if (this.mc.field_1724.method_6059(class_1294.field_5909) && (Boolean)this.applySlowness.get()) {
                                                value = (double)(this.mc.field_1724.method_6112(class_1294.field_5909).method_5578() + 1) * 0.205;
                                                velX -= velX * value;
                                                velZ -= velZ * value;
                                             }

                                             if (this.anchor.isActive() && this.anchor.controlMovement) {
                                                velX = this.anchor.deltaX;
                                                velZ = this.anchor.deltaZ;
                                             }

                                             ((IVec3d)event.movement).set(velX, event.movement.field_1351, velZ);
                                          }
                                       }

                                       Vector2d change;
                                       if (this.mode.get() == StrafePlus.Mode.NCP) {
                                          switch (this.stage) {
                                             case 0:
                                                if (PlayerUtils.isMoving()) {
                                                   ++this.stage;
                                                   this.speed = (Double)this.startingSpeed.get() * this.getDefaultSpeed() - 0.01;
                                                }
                                             case 1:
                                                if (PlayerUtils.isMoving() && this.mc.field_1724.method_24828()) {
                                                   if (this.hopMode.get() == StrafePlus.HopMode.Auto) {
                                                      ((IVec3d)event.movement).setY(this.getHop(0.40123128));
                                                   } else {
                                                      ((IVec3d)event.movement).setY(this.getHop((Double)this.hopHeight.get()));
                                                   }

                                                   this.speed *= (Double)this.ncpSpeed.get();
                                                   ++this.stage;
                                                }
                                                break;
                                             case 2:
                                                this.speed = this.distance - (Double)this.jumpedSlowDown.get() * (this.distance - this.getDefaultSpeed());
                                                ++this.stage;
                                                break;
                                             case 3:
                                                if (!this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989(0.0, this.mc.field_1724.method_18798().field_1351, 0.0)) || this.mc.field_1724.field_5992 && this.stage > 0) {
                                                   this.stage = 0;
                                                }

                                                this.speed = this.distance - this.distance / (Double)this.resetDivisor.get();
                                          }

                                          this.speed = Math.max(this.speed, this.getDefaultSpeed());
                                          if ((Boolean)this.ncpSpeedLimit.get()) {
                                             if (System.currentTimeMillis() - this.timer > 2500L) {
                                                this.timer = System.currentTimeMillis();
                                             }

                                             this.speed = Math.min(this.speed, System.currentTimeMillis() - this.timer > 1250L ? 0.44 : 0.43);
                                          }

                                          change = this.transformStrafe(this.speed);
                                          velX = change.x;
                                          velZ = change.y;
                                          if (this.anchor.isActive() && this.anchor.controlMovement) {
                                             velX = this.anchor.deltaX;
                                             velZ = this.anchor.deltaZ;
                                          }

                                          ((IVec3d)event.movement).setXZ(velX, velZ);
                                       }

                                       if (this.mode.get() == StrafePlus.Mode.Smart) {
                                          switch (this.stage) {
                                             case 0:
                                                if (PlayerUtils.isMoving()) {
                                                   ++this.stage;
                                                   this.speed = (Double)this.startingSpeed.get() * this.getDefaultSpeed() - 0.01;
                                                }
                                             case 1:
                                                if (PlayerUtils.isMoving() && this.mc.field_1724.method_24828() && this.jumped) {
                                                   if (this.hopMode.get() == StrafePlus.HopMode.Auto) {
                                                      ((IVec3d)event.movement).setY(this.getHop(0.40123128));
                                                   } else {
                                                      ((IVec3d)event.movement).setY(this.getHop((Double)this.hopHeight.get()));
                                                   }

                                                   this.speed *= (Double)this.ncpSpeed.get();
                                                   ++this.stage;
                                                }
                                                break;
                                             case 2:
                                                this.speed = this.distance - (Double)this.jumpedSlowDown.get() * (this.distance - this.getDefaultSpeed());
                                                ++this.stage;
                                                break;
                                             case 3:
                                                if (!this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989(0.0, this.mc.field_1724.method_18798().field_1351, 0.0)) || this.mc.field_1724.field_5992 && this.stage > 0) {
                                                   this.stage = 0;
                                                }

                                                this.speed = this.distance - this.distance / (Double)this.resetDivisor.get();
                                          }

                                          this.speed = Math.max(this.speed, this.getDefaultSpeed());
                                          if ((Boolean)this.ncpSpeedLimit.get()) {
                                             if (System.currentTimeMillis() - this.timer > 2500L) {
                                                this.timer = System.currentTimeMillis();
                                             }

                                             this.speed = Math.min(this.speed, System.currentTimeMillis() - this.timer > 1250L ? 0.44 : 0.43);
                                          }

                                          change = this.transformStrafe(this.speed);
                                          velX = change.x;
                                          velZ = change.y;
                                          if (this.anchor.isActive() && this.anchor.controlMovement) {
                                             velX = this.anchor.deltaX;
                                             velZ = this.anchor.deltaZ;
                                          }

                                          ((IVec3d)event.movement).setXZ(velX, velZ);
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
         }
      }
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      if (!this.mc.field_1724.method_6128() && !this.mc.field_1724.method_6101() && this.mc.field_1724.method_5854() == null) {
         if ((Boolean)this.whenSneaking.get() || !this.mc.field_1724.method_5715()) {
            if ((Boolean)this.inWater.get() || !this.mc.field_1724.method_5799()) {
               if ((Boolean)this.inLava.get() || !this.mc.field_1724.method_5771()) {
                  if (!(Boolean)this.hungerCheck.get() || this.mc.field_1724.method_7344().method_7586() > 6) {
                     if (!(Boolean)this.longJumpPause.get() || !this.longJump.isActive()) {
                        if (!(Boolean)this.flightPause.get() || !this.flight.isActive()) {
                           if (!(Boolean)this.eFlyPause.get() || !this.efly.isActive()) {
                              if (!BlockInfo.doesBoxTouchBlock(this.mc.field_1724.method_5829(), class_2246.field_10343) || this.webbedPause.get() != StrafePlus.WebbedPause.Always) {
                                 if (!BlockInfo.doesBoxTouchBlock(this.mc.field_1724.method_5829(), class_2246.field_10343) || this.mc.field_1724.method_24828() || this.webbedPause.get() != StrafePlus.WebbedPause.OnAir) {
                                    if (this.mc.field_1724.field_6250 > 0.0F && (Boolean)this.autoSprint.get()) {
                                       this.mc.field_1724.method_5728(true);
                                    }

                                    if ((Boolean)this.rubberbandPause.get() && this.mode.get() == StrafePlus.Mode.Vanilla) {
                                       if (this.rubberbandTicks > 0) {
                                          --this.rubberbandTicks;
                                          this.rubberbanded = true;
                                          this.info("Rubberband detected... pausing", new Object[0]);
                                          this.sentMessage = false;
                                       } else {
                                          this.rubberbanded = false;
                                          if (!this.sentMessage) {
                                             this.info("Continued", new Object[0]);
                                          }

                                          this.sentMessage = true;
                                       }
                                    }

                                    if (this.mode.get() == StrafePlus.Mode.Smart) {
                                       if (this.mc.field_1690.field_1903.method_1434() && this.mc.field_1724.method_24828()) {
                                          this.jumpTicks = (Integer)this.jumpTime.get();
                                       }

                                       if (this.jumpTicks > 0) {
                                          this.jumped = true;
                                          --this.jumpTicks;
                                       } else {
                                          this.jumped = false;
                                       }

                                       if (this.mc.field_1724.method_24828()) {
                                          this.jumpTicks = 0;
                                       }
                                    }

                                    if (this.mode.get() != StrafePlus.Mode.Vanilla) {
                                       this.distance = Math.sqrt((this.mc.field_1724.method_23317() - this.mc.field_1724.field_6014) * (this.mc.field_1724.method_23317() - this.mc.field_1724.field_6014) + (this.mc.field_1724.method_23321() - this.mc.field_1724.field_5969) * (this.mc.field_1724.method_23321() - this.mc.field_1724.field_5969));
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
      }
   }

   @EventHandler
   private void onPacketReceive(PacketEvent.Receive event) {
      if (event.packet instanceof class_2708) {
         this.rubberbandTicks = (Integer)this.rubberbandTime.get();
         this.reset();
      }

   }

   private double getDefaultSpeed() {
      double defaultSpeed = 0.2873;
      int amplifier;
      if (this.mc.field_1724.method_6059(class_1294.field_5904) && (Boolean)this.applySpeed.get()) {
         amplifier = this.mc.field_1724.method_6112(class_1294.field_5904).method_5578();
         defaultSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
      }

      if (this.mc.field_1724.method_6059(class_1294.field_5909) && (Boolean)this.applySlowness.get()) {
         amplifier = this.mc.field_1724.method_6112(class_1294.field_5909).method_5578();
         defaultSpeed /= 1.0 + 0.2 * (double)(amplifier + 1);
      }

      return defaultSpeed;
   }

   private void reset() {
      this.stage = 0;
      this.distance = 0.0;
      this.speed = 0.2873;
   }

   private double getHop(double height) {
      class_1293 jumpBoost = this.mc.field_1724.method_6059(class_1294.field_5913) ? this.mc.field_1724.method_6112(class_1294.field_5913) : null;
      if (jumpBoost != null && (Boolean)this.applyJumpBoost.get()) {
         height += (double)((float)(jumpBoost.method_5578() + 1) * 0.1F);
      }

      return height;
   }

   private Vector2d transformStrafe(double speed) {
      float forward = this.mc.field_1724.field_3913.field_3905;
      float side = this.mc.field_1724.field_3913.field_3907;
      float yaw = this.mc.field_1724.field_5982 + (this.mc.field_1724.method_36454() - this.mc.field_1724.field_5982) * this.mc.method_1488();
      if (forward == 0.0F && side == 0.0F) {
         return new Vector2d(0.0, 0.0);
      } else {
         if (forward != 0.0F) {
            if (side >= 1.0F) {
               yaw += (float)(forward > 0.0F ? -45 : 45);
               side = 0.0F;
            } else if (side <= -1.0F) {
               yaw += (float)(forward > 0.0F ? 45 : -45);
               side = 0.0F;
            }

            if (forward > 0.0F) {
               forward = 1.0F;
            } else if (forward < 0.0F) {
               forward = -1.0F;
            }
         }

         double mx = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
         double mz = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
         double velX = (double)forward * speed * mx + (double)side * speed * mz;
         double velZ = (double)forward * speed * mz - (double)side * speed * mx;
         return new Vector2d(velX, velZ);
      }
   }

   public static enum Mode {
      Vanilla,
      NCP,
      Smart;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Vanilla, NCP, Smart};
      }
   }

   public static enum HopMode {
      Auto,
      Custom;

      // $FF: synthetic method
      private static HopMode[] $values() {
         return new HopMode[]{Auto, Custom};
      }
   }

   public static enum WebbedPause {
      Always,
      OnAir,
      None;

      // $FF: synthetic method
      private static WebbedPause[] $values() {
         return new WebbedPause[]{Always, OnAir, None};
      }
   }
}
