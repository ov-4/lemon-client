package dev.lemonclient.addon.managers.impl;

import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.events.PreRotationEvent;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.settings.RotationSettings;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.NCPRaytracer;
import dev.lemonclient.addon.utils.player.RotationUtils;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_3341;
import net.minecraft.class_3532;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

public class RotationManager {
   public Target target = null;
   public double timer = 0.0;
   public float[] prevDir = new float[2];
   public float[] currentDir = new float[2];
   public float[] lastDir = new float[2];
   public double priority = 1000.0;
   public RotationSettings settings = null;
   public boolean unsent = false;
   public static List history = new ArrayList();
   public Target lastTarget = null;
   boolean shouldRotate = false;
   private float[] next;
   private boolean rotated = false;
   private long key = 0L;
   private class_243 eyePos = new class_243(0.0, 0.0, 0.0);

   public RotationManager() {
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   @EventHandler(
      priority = 200
   )
   private void onTick(TickEvent.Pre event) {
      this.prevDir[0] = this.currentDir[0];
      this.prevDir[1] = this.currentDir[1];
      this.currentDir[0] = this.lastDir[0];
      this.currentDir[1] = this.lastDir[1];
   }

   @EventHandler(
      priority = 200
   )
   private void onMovePre(SendMovementPacketsEvent.Pre event) {
      this.unsent = true;
   }

   @EventHandler(
      priority = 200
   )
   private void onMovePost(SendMovementPacketsEvent.Post event) {
      if (this.unsent) {
         this.onPreRotate();
         if (this.updateShouldRotate()) {
            this.setEyePos(MeteorClient.mc.field_1724.method_19538());
            this.updateNextRotation();
            if (this.rotated) {
               MeteorClient.mc.method_1562().method_2883(new class_2828.class_2831(this.next[0], this.next[1], Managers.ON_GROUND.isOnGround()));
            }
         }
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      if (MeteorClient.mc.field_1724 != null) {
         if (this.settings == null) {
            this.settings = (RotationSettings)Modules.get().get(RotationSettings.class);
         }

         this.timer -= event.frameTime;
         if (this.timer > 0.0 && this.target != null && this.lastDir != null) {
            if (SettingUtils.shouldVanillaRotate()) {
               MeteorClient.mc.field_1724.method_36456(class_3532.method_17821(MeteorClient.mc.method_1488(), this.prevDir[0], this.currentDir[0]));
               MeteorClient.mc.field_1724.method_36457(class_3532.method_16439(MeteorClient.mc.method_1488(), this.prevDir[1], this.currentDir[1]));
            }
         } else if (this.target != null) {
            this.target = null;
            this.priority = 1000.0;
         } else {
            this.priority = 1000.0;
         }

      }
   }

   public class_2828 onFull(class_2828.class_2830 packet) {
      this.unsent = false;
      this.onPreRotate();
      if (!this.updateShouldRotate()) {
         return packet;
      } else {
         this.setEyePos(new class_243(packet.method_12269(0.0), packet.method_12268(0.0), packet.method_12274(0.0)));
         this.updateNextRotation();
         return (class_2828)(this.rotated ? new class_2828.class_2830(packet.method_12269(0.0), packet.method_12268(0.0), packet.method_12274(0.0), this.next[0], this.next[1], packet.method_12273()) : new class_2828.class_2829(packet.method_12269(0.0), packet.method_12268(0.0), packet.method_12274(0.0), packet.method_12273()));
      }
   }

   public class_2828 onPositionOnGround(class_2828.class_2829 packet) {
      this.unsent = false;
      this.onPreRotate();
      if (!this.updateShouldRotate()) {
         return packet;
      } else {
         this.setEyePos(new class_243(packet.method_12269(0.0), packet.method_12268(0.0), packet.method_12274(0.0)));
         this.updateNextRotation();
         return (class_2828)(this.rotated ? new class_2828.class_2830(packet.method_12269(0.0), packet.method_12268(0.0), packet.method_12274(0.0), this.next[0], this.next[1], packet.method_12273()) : packet);
      }
   }

   public class_2828 onLookAndOnGround(class_2828.class_2831 packet) {
      this.unsent = false;
      this.onPreRotate();
      if (!this.updateShouldRotate()) {
         return packet;
      } else {
         this.setEyePos(MeteorClient.mc.field_1724.method_19538());
         this.updateNextRotation();
         if (this.rotated) {
            return new class_2828.class_2831(this.next[0], this.next[1], packet.method_12273());
         } else {
            return packet.method_12273() != Managers.ON_GROUND.isOnGround() ? new class_2828.class_5911(packet.method_12273()) : null;
         }
      }
   }

   public class_2828 onOnlyOnground(class_2828.class_5911 packet) {
      this.unsent = false;
      this.onPreRotate();
      if (!this.updateShouldRotate()) {
         return packet;
      } else {
         this.setEyePos(MeteorClient.mc.field_1724.method_19538());
         this.updateNextRotation();
         return (class_2828)(this.rotated ? new class_2828.class_2831(this.next[0], this.next[1], packet.method_12273()) : packet);
      }
   }

   private void onPreRotate() {
      MeteorClient.EVENT_BUS.post(PreRotationEvent.INSTANCE);
   }

   private boolean updateShouldRotate() {
      this.shouldRotate = this.target != null && this.timer > 0.0;
      return this.shouldRotate;
   }

   private void updateNextRotation() {
      if (this.shouldRotate) {
         if (this.target instanceof BoxTarget) {
            ((BoxTarget)this.target).vec = this.getTargetPos();
            this.next = new float[]{RotationUtils.nextYaw((double)this.lastDir[0], RotationUtils.getYaw(this.eyePos, ((BoxTarget)this.target).vec), this.settings.yawStep(((BoxTarget)this.target).type)), RotationUtils.nextPitch((double)this.lastDir[1], RotationUtils.getPitch(this.eyePos, ((BoxTarget)this.target).vec), this.settings.pitchStep(((BoxTarget)this.target).type))};
         } else {
            this.next = new float[]{RotationUtils.nextYaw((double)this.lastDir[0], ((AngleTarget)this.target).yaw, this.settings.yawStep(((AngleTarget)this.target).type)), RotationUtils.nextPitch((double)this.lastDir[1], ((AngleTarget)this.target).pitch, this.settings.pitchStep(((AngleTarget)this.target).type))};
         }

         this.rotated = Math.abs(RotationUtils.yawAngle((double)this.next[0], (double)this.lastDir[0])) > 0.0 || Math.abs(this.next[1] - this.lastDir[1]) > 0.0F;
      }

   }

   @EventHandler(
      priority = 300
   )
   private void onSend(PacketEvent.Sent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2828 packet) {
         if (packet.method_36172()) {
            this.lastDir = new float[]{packet.method_12271(0.0F), packet.method_12270(0.0F)};
            this.addHistory((double)this.lastDir[0], (double)this.lastDir[1]);
         }
      }

   }

   public void end(long k) {
      if (k == this.key) {
         this.priority = 1000.0;
      }

   }

   public void endYaw(double yaw, boolean reset) {
      if (this.target instanceof AngleTarget) {
         if (yaw == ((AngleTarget)this.target).yaw) {
            this.priority = 1000.0;
            if (reset) {
               this.target = null;
            }
         }

      }
   }

   public void endPitch(double pitch, boolean reset) {
      if (this.target instanceof AngleTarget) {
         if (pitch == ((AngleTarget)this.target).pitch) {
            this.priority = 1000.0;
            if (reset) {
               this.target = null;
            }
         }

      }
   }

   public boolean startYaw(double yaw, double p, RotationType type, long key) {
      return this.start(yaw, (double)this.lastDir[1], p, type, key);
   }

   public boolean startPitch(double pitch, double p, RotationType type, long key) {
      return this.start((double)this.lastDir[0], pitch, p, type, key);
   }

   public boolean start(double yaw, double pitch, double p, RotationType type, long key) {
      if (this.settings == null) {
         return false;
      } else {
         if (p <= this.priority) {
            this.key = key;
            this.priority = p;
            this.lastTarget = this.target;
            this.target = new AngleTarget(yaw, pitch, type);
            this.timer = this.settings.time(type);
         }

         return (double)this.lastDir[0] == yaw && (double)this.lastDir[1] == pitch;
      }
   }

   public boolean start(class_2338 pos, class_238 box, class_243 vec, double p, RotationType type, long key) {
      if (this.settings == null) {
         return false;
      } else {
         boolean alreadyRotated = SettingUtils.rotationCheck(box, type);
         if (p < this.priority || p == this.priority && (!(this.target instanceof BoxTarget) || SettingUtils.rotationCheck(((BoxTarget)this.target).box, type))) {
            if (!alreadyRotated) {
               this.priority = p;
            }

            this.lastTarget = this.target;
            this.key = key;
            this.target = pos != null ? new BoxTarget(pos, vec != null ? vec : LemonUtils.getMiddle(box), p, type) : new BoxTarget(box, vec != null ? vec : LemonUtils.getMiddle(box), p, type);
            this.timer = this.settings.time(type);
         }

         return alreadyRotated;
      }
   }

   public boolean start(class_238 box, class_243 vec, double p, RotationType type, long key) {
      return this.start((class_2338)null, box, vec, p, type, key);
   }

   public boolean start(class_238 box, double p, RotationType type, long key) {
      return this.start(box, LemonUtils.getMiddle(box), p, type, key);
   }

   public boolean start(class_2338 pos, double p, RotationType type, long key) {
      return this.start(pos, class_238.method_19316(new class_3341(pos)), pos.method_46558(), p, type, key);
   }

   public boolean start(class_2338 pos, class_243 vec, double p, RotationType type, long key) {
      return this.start(pos, new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1)), vec, p, type, key);
   }

   private void setEyePos(class_243 vec3d) {
      this.eyePos = vec3d.method_1031(0.0, (double)MeteorClient.mc.field_1724.method_18381(MeteorClient.mc.field_1724.method_18376()), 0.0);
   }

   public void addHistory(double yaw, double pitch) {
      history.add(0, new Rotation(yaw, pitch, MeteorClient.mc.field_1724.method_33571()));

      for(int i = history.size(); i > 20; --i) {
         if (history.size() > i) {
            history.remove(i);
         }
      }

   }

   public class_243 getTargetPos() {
      BoxTarget t = (BoxTarget)this.target;
      if (this.settings.mode(t.type) == RotationSettings.RotationCheckMode.StrictRaytrace && !NCPRaytracer.raytrace(MeteorClient.mc.field_1724.method_33571(), t.targetVec, t.box)) {
         class_243 eye = MeteorClient.mc.field_1724.method_33571();
         double cd = 1000000.0;
         class_243 closest = null;

         for(double x = 0.0; x <= 1.0; x += 0.1) {
            for(double y = 0.0; y <= 1.0; y += 0.1) {
               for(double z = 0.0; z <= 1.0; z += 0.1) {
                  class_243 vec = new class_243(this.lerp(t.box.field_1323, t.box.field_1320, x), this.lerp(t.box.field_1322, t.box.field_1325, y), this.lerp(t.box.field_1321, t.box.field_1324, z));
                  double d = t.targetVec.method_1022(vec);
                  if (!(d > cd) && NCPRaytracer.raytrace(eye, vec, ((BoxTarget)this.target).box)) {
                     cd = d;
                     closest = vec;
                  }
               }
            }
         }

         return closest == null ? t.targetVec : closest;
      } else {
         return new class_243(class_3532.method_15350(t.targetVec.field_1352 + (Math.random() - 0.5) * 0.05, t.box.field_1323, t.box.field_1320), class_3532.method_15350(t.targetVec.field_1351 + (Math.random() - 0.5) * 0.05, t.box.field_1322, t.box.field_1325), class_3532.method_15350(t.targetVec.field_1350 + (Math.random() - 0.5) * 0.05, t.box.field_1321, t.box.field_1324));
      }
   }

   private double lerp(double from, double to, double delta) {
      return from + (to - from) * delta;
   }

   public void setHeadYaw(Args args) {
      if (this.shouldRotate) {
         args.set(1, this.prevDir[0]);
         args.set(2, this.currentDir[0]);
      }
   }

   public void setBodyYaw(Args args) {
      if (this.shouldRotate) {
         args.set(1, this.prevDir[0]);
         args.set(2, this.currentDir[0]);
      }
   }

   public void setPitch(Args args) {
      if (this.shouldRotate) {
         args.set(1, this.prevDir[1]);
         args.set(2, this.currentDir[1]);
      }
   }

   private static class Target {
   }

   private static class BoxTarget extends Target {
      public final class_2338 pos;
      public final class_238 box;
      public final class_243 targetVec;
      public class_243 vec;
      public final double priority;
      public final RotationType type;

      public BoxTarget(class_2338 pos, class_243 vec, double priority, RotationType type) {
         this.pos = pos;
         this.box = new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1));
         this.vec = vec;
         this.targetVec = vec;
         this.priority = priority;
         this.type = type;
      }

      public BoxTarget(class_238 box, class_243 vec, double priority, RotationType type) {
         this.pos = null;
         this.box = box;
         this.vec = vec;
         this.targetVec = vec;
         this.priority = priority;
         this.type = type;
      }
   }

   private static class AngleTarget extends Target {
      public final double yaw;
      public final double pitch;
      public boolean ended;
      public final RotationType type;

      public AngleTarget(double yaw, double pitch, RotationType type) {
         this.yaw = yaw;
         this.pitch = pitch;
         this.ended = false;
         this.type = type;
      }
   }

   public static record Rotation(double yaw, double pitch, class_243 vec) {
      public Rotation(double yaw, double pitch, class_243 vec) {
         this.yaw = yaw;
         this.pitch = pitch;
         this.vec = vec;
      }

      public double yaw() {
         return this.yaw;
      }

      public double pitch() {
         return this.pitch;
      }

      public class_243 vec() {
         return this.vec;
      }
   }
}
