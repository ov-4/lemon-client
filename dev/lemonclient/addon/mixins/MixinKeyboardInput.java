package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.modules.misc.Twerk;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_743;
import net.minecraft.class_744;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_743.class})
public class MixinKeyboardInput extends class_744 {
   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void isPressed(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
      if (((Twerk)Modules.get().get(Twerk.class)).doVanilla()) {
         this.field_3903 = true;
      }

   }
}
