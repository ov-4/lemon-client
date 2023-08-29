package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.events.InteractEvent;
import dev.lemonclient.addon.hwid.Hwid;
import dev.lemonclient.addon.modules.misc.MultiTask;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IMinecraftClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_310;
import net.minecraft.class_542;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {class_310.class},
   priority = 1001
)
public abstract class MixinMinecraftClient implements IMinecraftClient {
   @Redirect(
      method = {"handleBlockBreaking"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
)
   )
   public boolean breakBlockCheck(class_746 clientPlayerEntity) {
      return !Modules.get().isActive(MultiTask.class) && ((InteractEvent)MeteorClient.EVENT_BUS.post(InteractEvent.get(clientPlayerEntity.method_6115()))).usingItem;
   }

   @Redirect(
      method = {"doItemUse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"
)
   )
   public boolean useItemBreakCheck(class_636 clientPlayerInteractionManager) {
      return !Modules.get().isActive(MultiTask.class) && ((InteractEvent)MeteorClient.EVENT_BUS.post(InteractEvent.get(clientPlayerInteractionManager.method_2923()))).usingItem;
   }

   @Inject(
      method = {"<init>"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V",
   shift = Shift.BEFORE
)}
   )
   private void init(class_542 args, CallbackInfo ci) {
      Hwid.get();
   }
}
