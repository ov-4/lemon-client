package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.events.ClickWindowEvent;
import dev.lemonclient.addon.modules.combat.AutoMine;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2596;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_7204;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_636.class})
public abstract class MixinClientPlayerInteractionManager {
   @Shadow
   @Final
   private class_310 field_3712;
   @Shadow
   private float field_3713;
   @Shadow
   private float field_3715;
   @Shadow
   private class_1799 field_3718;
   @Shadow
   private class_2338 field_3714;
   @Shadow
   private boolean field_3717;
   private class_2338 position = null;

   @Shadow
   public abstract void method_41931(class_638 var1, class_7204 var2);

   @Shadow
   public abstract boolean method_2899(class_2338 var1);

   @Shadow
   public abstract int method_51888();

   @Inject(
      method = {"clickSlot"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void windowClick(int syncId, int slotId, int button, class_1713 actionType, class_1657 player, CallbackInfo callbackInfo) {
      ClickWindowEvent event = ClickWindowEvent.get(syncId, slotId, button, actionType);
      MeteorClient.EVENT_BUS.post(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
      }

   }

   @Inject(
      method = {"attackBlock"},
      at = {@At("HEAD")}
   )
   private void onAttack(class_2338 pos, class_2350 direction, CallbackInfoReturnable cir) {
      this.position = pos;
   }

   @Redirect(
      method = {"attackBlock"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
   ordinal = 1
)
   )
   private void onStart(class_636 instance, class_638 world, class_7204 packetCreator) {
      AutoMine autoMine = (AutoMine)Modules.get().get(AutoMine.class);
      if (!autoMine.isActive()) {
         this.method_41931(world, packetCreator);
      } else {
         class_2680 blockState = world.method_8320(this.position);
         boolean bl = !blockState.method_26215();
         if (bl && this.field_3715 == 0.0F) {
            blockState.method_26179(this.field_3712.field_1687, this.position, this.field_3712.field_1724);
         }

         if (bl && blockState.method_26165(this.field_3712.field_1724, this.field_3712.field_1724.method_37908(), this.position) >= 1.0F) {
            this.method_2899(this.position);
         } else {
            this.field_3717 = true;
            this.field_3714 = this.position;
            this.field_3718 = this.field_3712.field_1724.method_6047();
            this.field_3715 = 0.0F;
            this.field_3713 = 0.0F;
            this.field_3712.field_1687.method_8517(this.field_3712.field_1724.method_5628(), this.field_3714, this.method_51888());
         }

         autoMine.onStart(this.position);
      }
   }

   @Redirect(
      method = {"attackBlock"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
   ordinal = 0
)
   )
   private void onAbort(class_634 instance, class_2596 packet) {
      AutoMine autoMine = (AutoMine)Modules.get().get(AutoMine.class);
      if (!autoMine.isActive()) {
         instance.method_2883(packet);
      } else {
         autoMine.onAbort(this.position);
      }
   }

   @Inject(
      method = {"updateBlockBreakingProgress"},
      at = {@At("HEAD")}
   )
   private void onUpdateProgress(class_2338 pos, class_2350 direction, CallbackInfoReturnable cir) {
      this.position = pos;
   }

   @Redirect(
      method = {"updateBlockBreakingProgress"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
   ordinal = 1
)
   )
   private void onStop(class_636 instance, class_638 world, class_7204 packetCreator) {
      AutoMine autoMine = (AutoMine)Modules.get().get(AutoMine.class);
      if (!autoMine.isActive()) {
         this.method_41931(world, packetCreator);
      } else {
         autoMine.onStop();
      }
   }

   @Redirect(
      method = {"cancelBlockBreaking"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
)
   )
   private void cancel(class_634 instance, class_2596 packet) {
      AutoMine autoMine = (AutoMine)Modules.get().get(AutoMine.class);
      if (!autoMine.isActive()) {
         instance.method_2883(packet);
      } else {
         autoMine.onAbort(this.field_3714);
      }
   }
}
