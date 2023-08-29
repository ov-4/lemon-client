package dev.lemonclient.addon.mixins;

import net.minecraft.class_2684;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2684.class})
public interface IEntityS2CPacket {
   @Accessor("id")
   int getId();
}
