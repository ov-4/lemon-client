package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;

public class CustomFOV extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting FOV;

   public CustomFOV() {
      super(LemonClient.Misc, "Custom FOV", "Allows more customisation to the FOV.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.FOV = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("FOV")).description("What the FOV should be.")).defaultValue(120)).range(0, 358).sliderRange(0, 358).build());
   }

   @EventHandler
   private void onFov(GetFovEvent event) {
      event.fov = (double)(Integer)this.FOV.get();
   }
}
