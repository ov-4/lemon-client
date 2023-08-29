package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import java.util.Objects;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.RainbowColor;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2960;
import net.minecraft.class_4587;

public class Logo extends HudElement {
   public static final HudElementInfo INFO;
   private final SettingGroup sgGeneral;
   private final Setting logo;
   private final Setting scale;
   public final Setting chroma;
   private final Setting chromaSpeed;
   private final Setting color;
   private class_2960 image;
   private static final RainbowColor RAINBOW;

   public Logo() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.logo = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Logo")).defaultValue(Logo.LogoEnum.Text)).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Scale")).description("The scale of the logo.")).defaultValue(3.5).min(0.1).sliderRange(0.1, 5.0).build());
      this.chroma = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Chroma")).description("Chroma logo animation.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      DoubleSetting.Builder var10002 = ((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Factor")).defaultValue(0.1).min(0.01).sliderMax(5.0).decimalPlaces(4);
      Setting var10003 = this.chroma;
      Objects.requireNonNull(var10003);
      this.chromaSpeed = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).build());
      this.color = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Color")).defaultValue(new SettingColor(255, 255, 255)).visible(() -> {
         return !(Boolean)this.chroma.get();
      })).build());
      this.image = new class_2960("lemon-client", "text.png");
   }

   public void tick(HudRenderer renderer) {
      this.box.setSize(72.0 * (Double)this.scale.get(), 15.0 * (Double)this.scale.get());
   }

   public void render(HudRenderer renderer) {
      switch ((LogoEnum)this.logo.get()) {
         case Text:
            this.image = new class_2960("lemon-client", "text.png");
            break;
         case NewText:
            this.image = new class_2960("lemon-client", "newtext.png");
            break;
         case Jello:
            this.image = new class_2960("lemon-client", "jellotext.png");
            break;
         case Logo:
            this.image = new class_2960("lemon-client", "icons/icon.png");
      }

      GL.bindTexture(this.image);
      Renderer2D.TEXTURE.begin();
      if ((Boolean)this.chroma.get()) {
         RAINBOW.setSpeed((Double)this.chromaSpeed.get() / 100.0);
         Renderer2D.TEXTURE.texQuad((double)this.x, (double)this.y - 29.0 * (Double)this.scale.get(), 70.0 * (Double)this.scale.get(), 70.0 * (Double)this.scale.get(), RAINBOW.getNext(renderer.delta));
      } else {
         Renderer2D.TEXTURE.texQuad((double)this.x, (double)this.y - 29.0 * (Double)this.scale.get(), 70.0 * (Double)this.scale.get(), 70.0 * (Double)this.scale.get(), (Color)this.color.get());
      }

      Renderer2D.TEXTURE.render((class_4587)null);
   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Logo", "You should use fabric api to see it!", Logo::new);
      RAINBOW = new RainbowColor();
   }

   public static enum LogoEnum {
      Text,
      NewText,
      Jello,
      Logo;

      // $FF: synthetic method
      private static LogoEnum[] $values() {
         return new LogoEnum[]{Text, NewText, Jello, Logo};
      }
   }
}
