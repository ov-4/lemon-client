package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.modules.misc.SoundModifier;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_1927;
import net.minecraft.class_1937;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_1927.class})
public class MixinExplosion {
   @Redirect(
      method = {"affectWorld"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"
)
   )
   private void redirect(class_1937 instance, double x, double y, double z, class_3414 sound, class_3419 category, float volume, float pitch, boolean useDistance) {
      SoundModifier m = (SoundModifier)Modules.get().get(SoundModifier.class);
      if (m.isActive()) {
         if ((Boolean)m.expSound.get()) {
            instance.method_8486(x, y, z, sound, category, (float)((double)volume * (Double)m.explosionVolume.get()), (float)((double)pitch * (Double)m.explosionPitch.get()), useDistance);
         }

      } else {
         instance.method_8486(x, y, z, sound, category, volume, pitch, useDistance);
      }
   }
}
