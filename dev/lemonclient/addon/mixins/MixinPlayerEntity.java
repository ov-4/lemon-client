package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.modules.misc.SoundModifier;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1657.class})
public abstract class MixinPlayerEntity {
   class_1297 attackEntity = null;

   @Inject(
      method = {"attack"},
      at = {@At("HEAD")}
   )
   private void inject(class_1297 target, CallbackInfo ci) {
      this.attackEntity = target;
   }

   @Redirect(
      method = {"attack"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"
)
   )
   private void poseNotCollide(class_1937 instance, class_1657 except, double x, double y, double z, class_3414 sound, class_3419 category, float volume, float pitch) {
      SoundModifier m = (SoundModifier)Modules.get().get(SoundModifier.class);
      if (m.isActive()) {
         if ((Boolean)m.crystalHits.get()) {
            instance.method_43128(except, x, y, z, sound, category, (float)((double)volume * (Double)m.crystalHitVolume.get()), (float)((double)pitch * (Double)m.crystalHitPitch.get()));
         }

      } else {
         instance.method_43128(except, x, y, z, sound, category, volume, pitch);
      }
   }
}
