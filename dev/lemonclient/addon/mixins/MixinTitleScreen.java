package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.gui.screen.LemonClientScreen;
import meteordevelopment.meteorclient.gui.GuiThemes;
import net.minecraft.class_2561;
import net.minecraft.class_437;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_442.class})
public class MixinTitleScreen extends class_437 {
   public MixinTitleScreen(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void tick(CallbackInfo ci) {
      if (GuiThemes.get() != null) {
         this.field_22787.method_1507(new LemonClientScreen());
      }

   }

   @Inject(
      method = {"init"},
      at = {@At("HEAD")}
   )
   private void init(CallbackInfo ci) {
      if (GuiThemes.get() != null) {
         this.field_22787.method_1507(new LemonClientScreen());
      }

   }
}
