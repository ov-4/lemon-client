package dev.lemonclient.addon.mixins;

import net.minecraft.class_243;
import net.minecraft.class_3959;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_3959.class})
public interface IRaycastContext {
   @Mutable
   @Accessor("start")
   void setStart(class_243 var1);

   @Mutable
   @Accessor("end")
   void setEnd(class_243 var1);
}
