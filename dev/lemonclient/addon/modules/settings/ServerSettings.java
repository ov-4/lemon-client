package dev.lemonclient.addon.modules.settings;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;

public class ServerSettings extends LemonModule {
   private final SettingGroup sgGeneral;
   public final Setting cc;
   public final Setting oldVerCrystals;
   public final Setting oldVerDamage;

   public ServerSettings() {
      super(LemonClient.Settings, "Server", "Global server settings for every lemon module.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.cc = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("CC Hitboxes")).description("Newly placed crystals require 1 block tall space without entity hitboxes.")).defaultValue(false)).build());
      this.oldVerCrystals = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("1.12.2 Crystals")).description("Requires 2 block tall space to place crystals.")).defaultValue(false)).build());
      this.oldVerDamage = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("1.12.2 Damage")).description("Calculates damages in old way.")).defaultValue(false)).build());
   }
}
