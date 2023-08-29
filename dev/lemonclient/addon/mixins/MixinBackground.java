package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.modules.misc.FogRenderer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_4184;
import net.minecraft.class_758;
import net.minecraft.class_758.class_4596;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_758.class})
public class MixinBackground {
   @Inject(
      method = {"applyFog"},
      at = {@At("TAIL")}
   )
   private static void applyFog(class_4184 camera, class_758.class_4596 fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
      FogRenderer fog = (FogRenderer)Modules.get().get(FogRenderer.class);
      if (fog != null && fog.isActive() && fogType == class_4596.field_20946) {
         fog.modifyFog();
      }

   }
}
