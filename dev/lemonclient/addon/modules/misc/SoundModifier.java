package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;

public class SoundModifier extends LemonModule {
   private final SettingGroup sgCrystal;
   public final Setting crystalHits;
   public final Setting crystalHitVolume;
   public final Setting crystalHitPitch;
   public final Setting expSound;
   public final Setting explosionVolume;
   public final Setting explosionPitch;

   public SoundModifier() {
      super(LemonClient.Misc, "Sound Modifier", "Modifies sounds to make crystal pvp less horrible for ears.");
      this.sgCrystal = this.settings.createGroup("Crystal");
      this.crystalHits = this.sgCrystal.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Crystal Hit Sound")).description("Allows hit sounds when attacking end crystal.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgCrystal;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Crystal Hit Volume")).description("Multiplies crystal hit volumes.")).defaultValue(1.0).sliderRange(0.0, 10.0);
      Setting var10003 = this.crystalHits;
      Objects.requireNonNull(var10003);
      this.crystalHitVolume = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgCrystal;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Crystal Hit Pitch")).description("Multiplies pitch of crystal hit sounds.")).defaultValue(1.0).sliderRange(0.0, 10.0);
      var10003 = this.crystalHits;
      Objects.requireNonNull(var10003);
      this.crystalHitPitch = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.expSound = this.sgCrystal.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Explosion Sound")).description("Allows explosion sounds")).defaultValue(true)).build());
      var10001 = this.sgCrystal;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Explosion Volume")).description("Multiplies explosion volumes.")).defaultValue(1.0).sliderRange(0.0, 10.0);
      var10003 = this.expSound;
      Objects.requireNonNull(var10003);
      this.explosionVolume = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgCrystal;
      var10002 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Explosion Pitch")).description("Multiplies pitch of explosions sounds.")).defaultValue(1.0).sliderRange(0.0, 10.0);
      var10003 = this.expSound;
      Objects.requireNonNull(var10003);
      this.explosionPitch = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
   }
}
