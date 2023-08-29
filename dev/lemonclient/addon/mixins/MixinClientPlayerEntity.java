package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.combat.TickShift;
import dev.lemonclient.addon.modules.misc.SwingAnimation;
import dev.lemonclient.addon.modules.misc.Twerk;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_1268;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_634;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_746.class})
public abstract class MixinClientPlayerEntity {
   @Shadow
   @Final
   public class_634 field_3944;
   private static boolean sent = false;

   @Inject(
      method = {"swingHand(Lnet/minecraft/util/Hand;)V"},
      at = {@At("HEAD")}
   )
   private void swingHand(class_1268 hand, CallbackInfo ci) {
      ((SwingAnimation)Modules.get().get(SwingAnimation.class)).startSwing(hand);
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("HEAD")}
   )
   private void sendPacketsHead(CallbackInfo ci) {
      sent = false;
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
)},
      require = 0
   )
   private void onSendPacket(CallbackInfo ci) {
      sent = true;
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("TAIL")}
   )
   private void sendPacketsTail(CallbackInfo ci) {
      if (!sent) {
         TickShift tickShift = (TickShift)Modules.get().get(TickShift.class);
         if (tickShift.isActive()) {
            tickShift.unSent = Math.min((Integer)tickShift.packets.get(), tickShift.unSent + 1);
         }
      }

   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
   ordinal = 2
),
      require = 0
   )
   private void sendPacketFull(class_634 instance, class_2596 packet) {
      this.field_3944.method_2883(Managers.ROTATION.onFull((class_2828.class_2830)packet));
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
   ordinal = 3
),
      require = 0
   )
   private void sendPacketPosGround(class_634 instance, class_2596 packet) {
      this.field_3944.method_2883(Managers.ROTATION.onPositionOnGround((class_2828.class_2829)packet));
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
   ordinal = 4
),
      require = 0
   )
   private void sendPacketLookGround(class_634 instance, class_2596 packet) {
      class_2828 toSend = Managers.ROTATION.onLookAndOnGround((class_2828.class_2831)packet);
      if (toSend != null) {
         this.field_3944.method_2883(toSend);
      }

   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
   ordinal = 5
),
      require = 0
   )
   private void sendPacketGround(class_634 instance, class_2596 packet) {
      this.field_3944.method_2883(Managers.ROTATION.onOnlyOnground((class_2828.class_5911)packet));
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSneaking()Z"
)
   )
   private boolean isSneaking(class_746 clientPlayerEntity) {
      return clientPlayerEntity.method_5715() || ((Twerk)Modules.get().get(Twerk.class)).doPacket();
   }
}
