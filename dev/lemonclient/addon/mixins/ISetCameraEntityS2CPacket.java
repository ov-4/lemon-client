package dev.lemonclient.addon.mixins;

import net.minecraft.class_2734;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2734.class})
public interface ISetCameraEntityS2CPacket {
   @Accessor("entityId")
   int getId();
}
