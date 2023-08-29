package dev.lemonclient.addon.mixins;

import net.minecraft.class_2824;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2824.class})
public interface IInteractEntityC2SPacket {
   @Accessor("entityId")
   @Final
   @Mutable
   void setId(int var1);

   @Accessor("entityId")
   int getId();
}
