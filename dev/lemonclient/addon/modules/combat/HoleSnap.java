package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.HoleType;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.world.hole.Hole;
import dev.lemonclient.addon.utils.world.hole.HoleUtils;
import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2708;

public class HoleSnap extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSpeed;
   private final SettingGroup sgHole;
   private final SettingGroup sgRender;
   private final Setting jump;
   private final Setting jumpCoolDown;
   private final Setting range;
   private final Setting downRange;
   private final Setting coll;
   private final Setting rDisable;
   private final Setting speed;
   private final Setting boost;
   private final Setting boostedSpeed;
   private final Setting boostTicks;
   private final Setting timer;
   private final Setting singleTarget;
   private final Setting depth;
   private final Setting singleHoles;
   private final Setting doubleHoles;
   private final Setting quadHoles;
   private final Setting render;
   private final Setting color;
   private Hole singleHole;
   private int collisions;
   private int rubberbands;
   private int ticks;
   private int boostLeft;
   private Hole targetHole;
   private int alpha;

   public HoleSnap() {
      super(LemonClient.Combat, "Hole Snap", "Move to the hole nearby.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSpeed = this.settings.createGroup("Speed");
      this.sgHole = this.settings.createGroup("Hole");
      this.sgRender = this.settings.createGroup("Render");
      this.jump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Jump")).description("Jumps to the hole.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Jump Cooldown")).description("Ticks between jumps.")).defaultValue(5)).min(0).sliderMax(100);
      Setting var10003 = this.jump;
      Objects.requireNonNull(var10003);
      this.jumpCoolDown = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Range")).description("Horizontal range for finding holes.")).defaultValue(3)).range(0, 5).sliderMax(5).build());
      this.downRange = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Down Range")).description("Vertical range for finding holes.")).defaultValue(3)).range(0, 5).sliderMax(5).build());
      this.coll = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Collisions to disable")).description("0 = doesn't disable.")).defaultValue(15)).min(0).sliderRange(0, 100).build());
      this.rDisable = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Rubberbands to disable")).description("0 = doesn't disable.")).defaultValue(1)).min(0).sliderRange(0, 100).build());
      this.speed = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Speed")).description("Movement Speed.")).defaultValue(0.2873).min(0.0).sliderMax(1.0).build());
      this.boost = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Speed Boost")).description("Jumps to the hole (very useful).")).defaultValue(false)).build());
      var10001 = this.sgSpeed;
      DoubleSetting.Builder var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Boosted Speed")).description("Movement Speed.")).defaultValue(0.5).min(0.0).sliderMax(1.0);
      var10003 = this.boost;
      Objects.requireNonNull(var10003);
      this.boostedSpeed = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgSpeed;
      var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Boost Ticks")).description("How many boosted speed packets should be sent before returning to normal speed.")).defaultValue(3)).min(1).sliderMax(10);
      var10003 = this.boost;
      Objects.requireNonNull(var10003);
      this.boostTicks = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.timer = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Timer")).description("Sends packets faster.")).defaultValue(10.0).min(0.0).sliderMax(100.0).build());
      this.singleTarget = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Single Target")).description("Only chooses target hole once.")).defaultValue(true)).build());
      this.depth = this.sgHole.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Hole Depth")).description("How deep a hole has to be.")).defaultValue(3)).range(1, 5).sliderRange(1, 5).build());
      this.singleHoles = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Single Holes")).description("Targets single block holes.")).defaultValue(true)).build());
      this.doubleHoles = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Double Holes")).description("Targets double holes.")).defaultValue(true)).build());
      this.quadHoles = this.sgHole.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Quad Holes")).description("Targets quad holes.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Render")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(true)).build());
      var10001 = this.sgRender;
      ColorSetting.Builder var2 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255));
      var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.color = var10001.add(((ColorSetting.Builder)var2.visible(var10003::get)).build());
      this.boostLeft = 0;
   }

   public void onActivate() {
      super.onActivate();
      this.singleHole = this.findHole();
      this.rubberbands = 0;
      this.ticks = 0;
      this.boostLeft = (Boolean)this.boost.get() ? (Integer)this.boostTicks.get() : 0;
   }

   public void onDeactivate() {
      super.onDeactivate();
      ((Timer)Modules.get().get(Timer.class)).setOverride(1.0);
   }

   @EventHandler
   private void onPacket(PacketEvent.Receive event) {
      if (event.packet instanceof class_2708 && (Integer)this.rDisable.get() > 0) {
         ++this.rubberbands;
         if (this.rubberbands >= (Integer)this.rDisable.get() && (Integer)this.rDisable.get() > 0) {
            this.toggle();
            this.sendDisableMsg("rubberbanding");
         }
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onMove(PlayerMoveEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         Hole hole = (Boolean)this.singleTarget.get() ? this.singleHole : this.findHole();
         this.targetHole = hole;
         if (hole != null && !this.singleBlocked()) {
            this.alpha = 150;
            ((Timer)Modules.get().get(Timer.class)).setOverride((Double)this.timer.get());
            double yaw = Math.cos(Math.toRadians((double)(this.getAngle(hole.middle) + 90.0F)));
            double pit = Math.sin(Math.toRadians((double)(this.getAngle(hole.middle) + 90.0F)));
            if (this.mc.field_1724.method_23317() == hole.middle.field_1352 && this.mc.field_1724.method_23321() == hole.middle.field_1350) {
               if (this.mc.field_1724.method_23318() == hole.middle.field_1351) {
                  this.toggle();
                  this.sendDisableMsg("in hole");
                  ((IVec3d)event.movement).setXZ(0.0, 0.0);
               } else if (LemonUtils.inside(this.mc.field_1724, this.mc.field_1724.method_5829().method_989(0.0, -0.05, 0.0))) {
                  this.toggle();
                  this.sendDisableMsg("hole unreachable");
               } else {
                  ((IVec3d)event.movement).setXZ(0.0, 0.0);
               }
            } else {
               double x = this.getSpeed() * yaw;
               double dX = hole.middle.field_1352 - this.mc.field_1724.method_23317();
               double z = this.getSpeed() * pit;
               double dZ = hole.middle.field_1350 - this.mc.field_1724.method_23321();
               if (LemonUtils.inside(this.mc.field_1724, this.mc.field_1724.method_5829().method_989(x, 0.0, z))) {
                  ++this.collisions;
                  if (this.collisions >= (Integer)this.coll.get() && (Integer)this.coll.get() > 0) {
                     this.toggle();
                     this.sendDisableMsg("collided");
                  }
               } else {
                  this.collisions = 0;
               }

               if (this.ticks > 0) {
                  --this.ticks;
               } else if (LemonUtils.inside(this.mc.field_1724, this.mc.field_1724.method_5829().method_989(0.0, -0.05, 0.0)) && (Boolean)this.jump.get()) {
                  this.ticks = (Integer)this.jumpCoolDown.get();
                  ((IVec3d)event.movement).setY(0.42);
               }

               --this.boostLeft;
               ((IVec3d)event.movement).setXZ(Math.abs(x) < Math.abs(dX) ? x : dX, Math.abs(z) < Math.abs(dZ) ? z : dZ);
            }
         } else {
            this.toggle();
            this.sendDisableMsg("no hole found");
         }
      }

   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         if (this.alpha > 0) {
            this.alpha -= 2;
         } else {
            this.alpha = 0;
         }

         if (this.targetHole != null) {
            assert this.mc.field_1724 != null;

            class_243 from = this.mc.field_1724.method_19538();
            class_243 to = this.targetHole.middle;
            class_243 linePath = to.method_1031(0.0, 1.5, 0.0);
            int iPath = event.renderer.lines.vec3(linePath.field_1352, linePath.field_1351, linePath.field_1350).color(((SettingColor)this.color.get()).a(this.alpha)).next();
            int iPlayer = event.renderer.lines.vec3(from.field_1352, from.field_1351, from.field_1350).color(((SettingColor)this.color.get()).a(this.alpha)).next();
            int iHole = event.renderer.lines.vec3(to.field_1352, to.field_1351, to.field_1350).color(((SettingColor)this.color.get()).a(this.alpha)).next();
            event.renderer.lines.line(iPlayer, iPath);
            event.renderer.lines.line(iPath, iHole);
         }
      }

   }

   private boolean singleBlocked() {
      if (!(Boolean)this.singleTarget.get()) {
         return false;
      } else {
         class_2338[] var1 = this.singleHole.positions;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            class_2338 pos = var1[var3];
            if (LemonUtils.collidable(pos)) {
               return true;
            }
         }

         return false;
      }
   }

   private Hole findHole() {
      Hole closest = null;

      for(int x = -(Integer)this.range.get(); x <= (Integer)this.range.get(); ++x) {
         for(int y = -(Integer)this.downRange.get(); y < 1; ++y) {
            for(int z = -(Integer)this.range.get(); z < (Integer)this.range.get(); ++z) {
               class_2338 pos = this.mc.field_1724.method_24515().method_10069(x, y, z);
               Hole hole = HoleUtils.getHole(pos, (Boolean)this.singleHoles.get(), (Boolean)this.doubleHoles.get(), (Boolean)this.quadHoles.get(), (Integer)this.depth.get(), true);
               if (hole.type != HoleType.NotHole) {
                  if (y == 0 && this.inHole(hole)) {
                     return hole;
                  }

                  if (closest == null || hole.middle.method_1022(this.mc.field_1724.method_19538()) < closest.middle.method_1022(this.mc.field_1724.method_19538())) {
                     closest = hole;
                  }
               }
            }
         }
      }

      return closest;
   }

   private boolean inHole(Hole hole) {
      class_2338[] var2 = hole.positions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         class_2338 pos = var2[var4];
         if (this.mc.field_1724.method_24515().equals(pos)) {
            return true;
         }
      }

      return false;
   }

   private float getAngle(class_243 pos) {
      return (float)Rotations.getYaw(pos);
   }

   private double getSpeed() {
      return this.boostLeft > 0 ? (Double)this.boostedSpeed.get() : (Double)this.speed.get();
   }
}
