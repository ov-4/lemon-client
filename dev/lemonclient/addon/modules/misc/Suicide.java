package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_418;

public class Suicide extends LemonModule {
   private final SettingGroup sgGeneral;
   public final Setting disableDeath;

   public Suicide() {
      super(LemonClient.Misc, "Suicide", "Kills yourself. Recommended.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.disableDeath = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Disable On Death")).description("Disables the module on death.")).defaultValue(true)).build());
   }

   @EventHandler(
      priority = 6969
   )
   private void onDeath(OpenScreenEvent event) {
      if (event.screen instanceof class_418 && (Boolean)this.disableDeath.get()) {
         this.toggle();
         this.sendDisableMsg("died");
      }

   }
}
