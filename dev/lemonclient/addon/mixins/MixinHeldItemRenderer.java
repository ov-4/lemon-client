package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.modules.misc.SwingAnimation;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_1268;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_759.class})
public abstract class MixinHeldItemRenderer {
   @ModifyArgs(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
)
   )
   private void setArgs(Args args) {
      SwingAnimation module = (SwingAnimation)Modules.get().get(SwingAnimation.class);
      if (module.isActive()) {
         args.set(6, module.getY((class_1268)args.get(3)));
         args.set(4, module.getSwing((class_1268)args.get(3)));
      }

   }
}
