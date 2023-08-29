package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.managers.Managers;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1309;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_922.class})
public class MixinLivingEntityRenderer {
   private boolean itsami = false;

   @Inject(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = {@At("HEAD")}
   )
   private void inject(class_1309 livingEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, CallbackInfo ci) {
      this.itsami = livingEntity == MeteorClient.mc.field_1724;
   }

   @ModifyArgs(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F",
   ordinal = 0
)
   )
   public void changeBodyYaw(Args args) {
      if (this.itsami) {
         Managers.ROTATION.setBodyYaw(args);
      }

   }

   @ModifyArgs(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F",
   ordinal = 1
)
   )
   public void changeHeadYaw(Args args) {
      if (this.itsami) {
         Managers.ROTATION.setHeadYaw(args);
      }

   }

   @ModifyArgs(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"
)
   )
   public void changePitch(Args args) {
      if (this.itsami) {
         Managers.ROTATION.setPitch(args);
      }

   }
}
