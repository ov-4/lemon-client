package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.modules.misc.NoHurtCam;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_757.class})
public abstract class MixinGameRenderer {
   @Inject(
      method = {"tiltViewWhenHurt"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onBobViewWhenHurt(class_4587 matrixStack, float f, CallbackInfo info) {
      if (((NoHurtCam)Modules.get().get(NoHurtCam.class)).isActive()) {
         info.cancel();
      }

   }
}
