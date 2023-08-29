package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.LemonUtils;
import java.util.Set;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_3532;
import org.joml.Vector3d;

public class AimAssist extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSpeed;
   private final Setting entities;
   private final Setting range;
   private final Setting fov;
   private final Setting ignoreWalls;
   private final Setting priority;
   private final Setting bodyTarget;
   private final Setting instant;
   private final Setting speed;
   private final Vector3d vec3d1;
   private class_1297 target;

   public AimAssist() {
      super(LemonClient.Combat, "Aim Assist", "Automatically aims at entities.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgSpeed = this.settings.createGroup("Aim Speed");
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to aim at.")).defaultValue(new class_1299[]{class_1299.field_6097}).build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The range at which an entity can be targeted.")).defaultValue(5.0).min(0.0).build());
      this.fov = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fov")).description("Will only aim entities in the fov.")).defaultValue(360.0).min(0.0).max(360.0).build());
      this.ignoreWalls = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-walls")).description("Whether or not to ignore aiming through walls.")).defaultValue(false)).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("How to filter targets within range.")).defaultValue(SortPriority.LowestHealth)).build());
      this.bodyTarget = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("aim-target")).description("Which part of the entities body to aim at.")).defaultValue(Target.Body)).build());
      this.instant = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("instant-look")).description("Instantly looks at the entity.")).defaultValue(false)).build());
      this.speed = this.sgSpeed.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("speed")).description("How fast to aim at the entity.")).defaultValue(5.0).min(0.0).visible(() -> {
         return !(Boolean)this.instant.get();
      })).build());
      this.vec3d1 = new Vector3d();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.target = TargetUtils.get((entity) -> {
         if (!entity.method_5805()) {
            return false;
         } else if (!PlayerUtils.isWithin(entity, (Double)this.range.get())) {
            return false;
         } else if (!(Boolean)this.ignoreWalls.get() && !PlayerUtils.canSeeEntity(entity)) {
            return false;
         } else if (entity != this.mc.field_1724 && ((Set)this.entities.get()).contains(entity.method_5864())) {
            return entity instanceof class_1657 ? Friends.get().shouldAttack((class_1657)entity) : LemonUtils.inFov(entity, (Double)this.fov.get());
         } else {
            return false;
         }
      }, (SortPriority)this.priority.get());
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.target != null) {
         this.aim(this.target, (double)event.tickDelta, (Boolean)this.instant.get());
      }

   }

   private void aim(class_1297 target, double delta, boolean instant) {
      Utils.set(this.vec3d1, target, delta);
      switch ((Target)this.bodyTarget.get()) {
         case Head:
            this.vec3d1.add(0.0, (double)target.method_18381(target.method_18376()), 0.0);
            break;
         case Body:
            this.vec3d1.add(0.0, (double)(target.method_18381(target.method_18376()) / 2.0F), 0.0);
      }

      double deltaX = this.vec3d1.x - this.mc.field_1724.method_23317();
      double deltaZ = this.vec3d1.z - this.mc.field_1724.method_23321();
      double deltaY = this.vec3d1.y - (this.mc.field_1724.method_23318() + (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()));
      double angle = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0;
      double deltaAngle;
      double toRotate;
      if (instant) {
         this.mc.field_1724.method_36456((float)angle);
      } else {
         deltaAngle = class_3532.method_15338(angle - (double)this.mc.field_1724.method_36454());
         toRotate = (Double)this.speed.get() * (double)(deltaAngle >= 0.0 ? 1 : -1) * delta;
         if (toRotate >= 0.0 && toRotate > deltaAngle || toRotate < 0.0 && toRotate < deltaAngle) {
            toRotate = deltaAngle;
         }

         this.mc.field_1724.method_36456(this.mc.field_1724.method_36454() + (float)toRotate);
      }

      double idk = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
      angle = -Math.toDegrees(Math.atan2(deltaY, idk));
      if (instant) {
         this.mc.field_1724.method_36457((float)angle);
      } else {
         deltaAngle = class_3532.method_15338(angle - (double)this.mc.field_1724.method_36455());
         toRotate = (Double)this.speed.get() * (double)(deltaAngle >= 0.0 ? 1 : -1) * delta;
         if (toRotate >= 0.0 && toRotate > deltaAngle || toRotate < 0.0 && toRotate < deltaAngle) {
            toRotate = deltaAngle;
         }

         this.mc.field_1724.method_36457(this.mc.field_1724.method_36455() + (float)toRotate);
      }

   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }
}
