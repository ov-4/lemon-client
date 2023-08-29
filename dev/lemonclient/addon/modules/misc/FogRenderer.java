package dev.lemonclient.addon.modules.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_6854;

public class FogRenderer extends LemonModule {
   private final SettingGroup sgGeneral;
   public final Setting shape;
   public final Setting distance;
   public final Setting fading;
   public final Setting thickness;
   public final Setting color;

   public FogRenderer() {
      super(LemonClient.Misc, "Fog Renderer", "Customizable fog.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.shape = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape")).description("Fog shape.")).defaultValue(class_6854.field_36350)).build());
      this.distance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Distance")).description("How far away should the fog start rendering.")).defaultValue(25.0).min(0.0).sliderRange(0.0, 100.0).build());
      this.fading = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Fading")).description("How smoothly should the fog fade.")).defaultValue(25)).min(0).sliderRange(0, 1000).build());
      this.thickness = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Thickness")).description(".")).defaultValue(10.0).range(1.0, 100.0).sliderRange(1.0, 100.0).build());
      this.color = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Color")).description("Color of the fog.")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
   }

   public void modifyFog() {
      RenderSystem.setShaderFogColor((float)((SettingColor)this.color.get()).r, (float)((SettingColor)this.color.get()).g, (float)((SettingColor)this.color.get()).b, (float)((SettingColor)this.color.get()).a / (float)((100.0 - (Double)this.thickness.get()) * 2.549999952316284));
      RenderSystem.setShaderFogStart((float)((Double)this.distance.get() * 1.0));
      RenderSystem.setShaderFogEnd((float)((Double)this.distance.get() + (double)(Integer)this.fading.get()));
      RenderSystem.setShaderFogShape((class_6854)this.shape.get());
   }
}
