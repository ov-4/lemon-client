package dev.lemonclient.addon.mixins;

import net.minecraft.class_2663;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2663.class})
public interface IEntityStatusS2CPacket {
   @Accessor("id")
   int getId();
}
