package dev.lemonclient.addon.mixins;

import net.minecraft.class_2726;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2726.class})
public interface IEntitySetHeadYawS2CPacket {
   @Accessor("entity")
   int getId();
}
