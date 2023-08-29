package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.modules.misc.AntiCrawl;
import dev.lemonclient.addon.modules.misc.ForceSneak;
import dev.lemonclient.addon.modules.misc.StepPlus;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_4050;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1297.class})
public abstract class MixinEntity {
   @Shadow
   public abstract boolean method_41328(class_4050 var1);

   @Shadow
   public abstract class_2561 method_5477();

   @Shadow
   public abstract class_1937 method_37908();

   @Shadow
   public abstract class_1269 method_5688(class_1657 var1, class_1268 var2);

   @Shadow
   protected abstract void method_5623(double var1, boolean var3, class_2680 var4, class_2338 var5);

   @Shadow
   protected abstract boolean method_51701(class_2338 var1, class_2680 var2, boolean var3, boolean var4, class_243 var5);

   @Shadow
   public abstract float method_49476();

   @Shadow
   public abstract boolean method_24828();

   @Shadow
   public abstract class_238 method_5829();

   @Inject(
      method = {"adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void inject(class_243 movement, CallbackInfoReturnable cir) {
      StepPlus step = (StepPlus)Modules.get().get(StepPlus.class);
      class_1297 entity = (class_1297)this;
      boolean active = step.isActive() && entity == MeteorClient.mc.field_1724;
      if (active && (Boolean)step.slow.get()) {
         step.slowStep(entity, movement, cir);
      } else {
         active = active && (double)(System.currentTimeMillis() - step.lastStep) > (Double)step.cooldown.get() * 1000.0;
         class_238 box = this.method_5829();
         List list = this.method_37908().method_20743(entity, box.method_18804(movement));
         class_243 vec3d = movement.method_1027() == 0.0 ? movement : class_1297.method_20736(entity, movement, box, this.method_37908(), list);
         boolean bl = movement.field_1352 != vec3d.field_1352;
         boolean bl2 = movement.field_1351 != vec3d.field_1351;
         boolean bl3 = movement.field_1350 != vec3d.field_1350;
         boolean bl4 = this.method_24828() || !active && bl2 && movement.field_1351 < 0.0;
         if ((active ? (Double)step.height.get() : (double)this.method_49476()) > 0.0 && bl4 && (bl || bl3)) {
            class_243 vec3d2 = class_1297.method_20736(entity, new class_243(movement.field_1352, active ? (Double)step.height.get() : (double)this.method_49476(), movement.field_1350), box, this.method_37908(), list);
            class_243 vec3d3 = class_1297.method_20736(entity, new class_243(0.0, active ? (Double)step.height.get() : (double)this.method_49476(), 0.0), box.method_1012(movement.field_1352, 0.0, movement.field_1350), this.method_37908(), list);
            class_243 v;
            if (vec3d3.field_1351 < (active ? (Double)step.height.get() : (double)this.method_49476())) {
               v = class_1297.method_20736(entity, new class_243(movement.field_1352, 0.0, movement.field_1350), box.method_997(vec3d3), this.method_37908(), list).method_1019(vec3d3);
               if (v.method_37268() > vec3d2.method_37268()) {
                  vec3d2 = v;
               }
            }

            if (vec3d2.method_37268() > vec3d.method_37268()) {
               v = vec3d2.method_1019(class_1297.method_20736(entity, new class_243(0.0, -vec3d2.field_1351 + movement.field_1351, 0.0), box.method_997(vec3d2), entity.method_37908(), list));
               if (active) {
                  step.step(step.getOffsets(v.field_1351));
               }

               cir.setReturnValue(v);
               return;
            }
         }

         cir.setReturnValue(vec3d);
      }
   }

   @Inject(
      method = {"isInSneakingPose"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void isSneaking(CallbackInfoReturnable cir) {
      if (MeteorClient.mc.field_1724 == null || this.method_5477() != MeteorClient.mc.field_1724.method_5477()) {
         cir.setReturnValue(((ForceSneak)Modules.get().get(ForceSneak.class)).isActive() || this.method_41328(class_4050.field_18081));
      }

   }

   @Inject(
      method = {"wouldPoseNotCollide"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void poseNotCollide(class_4050 pose, CallbackInfoReturnable cir) {
      if (Modules.get().isActive(AntiCrawl.class)) {
         cir.setReturnValue(true);
      }

   }
}
