package dev.lemonclient.addon.mixins;

import java.util.UUID;
import net.minecraft.class_2884;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2884.class})
public interface ISpectatorTeleportC2SPacket {
   @Accessor("targetUuid")
   UUID getID();
}
