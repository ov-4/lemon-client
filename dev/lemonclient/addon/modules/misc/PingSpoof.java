package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;

public class PingSpoof extends LemonModule {
   private final SettingGroup sgGeneral;
   public final Setting keepAlive;
   public final Setting pong;
   public final Setting ping;

   public PingSpoof() {
      super(LemonClient.Misc, "Ping Spoof", "Modify your ping.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.keepAlive = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Keep Alive")).description("Delays keep alive packets.")).defaultValue(true)).build());
      this.pong = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pong")).description("Delays pong packets.")).defaultValue(false)).build());
      this.ping = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Ping")).description("Increases your ping by this much.")).defaultValue(69)).min(0).sliderRange(0, 1000).build());
   }
}
