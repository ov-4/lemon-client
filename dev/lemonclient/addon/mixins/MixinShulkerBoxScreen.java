package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.modules.misc.ShulkerDupe;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_2561;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_495;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({class_495.class})
public class MixinShulkerBoxScreen extends class_437 {
   public MixinShulkerBoxScreen(class_2561 title) {
      super(title);
   }

   protected void method_25426() {
      super.method_25426();
      if (Modules.get().isActive(ShulkerDupe.class)) {
         this.method_37063((new class_4185.class_7840(class_2561.method_43470("Dupe"), (button) -> {
            this.dupe();
         })).method_46433(240, this.field_22790 / 2 + 35 - 140).method_46437(50, 15).method_46431());
         this.method_37063((new class_4185.class_7840(class_2561.method_43470("Dupe All"), (button) -> {
            this.dupeAll();
         })).method_46433(295, this.field_22790 / 2 + 35 - 140).method_46437(50, 15).method_46431());
      }

   }

   private void dupe() {
      ShulkerDupe.shouldDupe = true;
   }

   private void dupeAll() {
      ShulkerDupe.shouldDupeAll = true;
   }
}
