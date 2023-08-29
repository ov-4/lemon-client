package dev.lemonclient.addon.mixins;

import net.minecraft.class_4970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_4970.class_2251.class})
public interface IBlockSettings {
   @Accessor("replaceable")
   boolean replaceable();
}
