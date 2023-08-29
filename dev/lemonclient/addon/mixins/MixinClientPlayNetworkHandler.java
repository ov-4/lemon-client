package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.misc.PingSpoof;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_2670;
import net.minecraft.class_634;
import net.minecraft.class_6373;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public class MixinClientPlayNetworkHandler {
   @Inject(
      method = {"onKeepAlive"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void keepAlive(class_2670 packet, CallbackInfo ci) {
      if (Modules.get().isActive(PingSpoof.class) && (Boolean)((PingSpoof)Modules.get().get(PingSpoof.class)).keepAlive.get()) {
         ci.cancel();
         Managers.PING_SPOOF.addKeepAlive(packet.method_11517());
      }
   }

   @Inject(
      method = {"onPing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pong(class_6373 packet, CallbackInfo ci) {
      if (Modules.get().isActive(PingSpoof.class) && (Boolean)((PingSpoof)Modules.get().get(PingSpoof.class)).pong.get()) {
         ci.cancel();
         Managers.PING_SPOOF.addPong(packet.method_36950());
      }
   }
}
