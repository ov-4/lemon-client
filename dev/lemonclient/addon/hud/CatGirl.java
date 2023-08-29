package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2960;
import net.minecraft.class_4587;

public class CatGirl extends HudElement {
   public static final HudElementInfo INFO;
   private final SettingGroup sgGeneral;
   private final Setting logo;
   private final Setting scale;
   private final Setting color;
   private class_2960 image;

   public CatGirl() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.logo = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Logo")).defaultValue(CatGirl.LogoEnum.CatGirl)).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Scale")).description("The scale of the logo.")).defaultValue(3.5).min(0.1).sliderRange(0.1, 5.0).build());
      this.color = this.sgGeneral.add(((ColorSetting.Builder)(new ColorSetting.Builder()).name("Color")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.image = new class_2960("lemon-client", "catgirl.png");
   }

   public void tick(HudRenderer renderer) {
      this.box.setSize(72.0 * (Double)this.scale.get(), 15.0 * (Double)this.scale.get());
   }

   public void render(HudRenderer renderer) {
      switch ((LogoEnum)this.logo.get()) {
         case CatGirl:
            this.image = new class_2960("lemon-client", "catgirl.png");
            break;
         case LiLiBai:
            this.image = new class_2960("lemon-client", "lilibai.png");
            break;
         case CNMB:
            this.image = new class_2960("lemon-client", "cnmb.png");
            break;
         case SBGun:
            this.image = new class_2960("lemon-client", "sbgun.png");
            break;
         case FUFU:
            this.image = new class_2960("lemon-client", "fufu.png");
      }

      GL.bindTexture(this.image);
      Renderer2D.TEXTURE.begin();
      Renderer2D.TEXTURE.texQuad((double)this.x, (double)this.y - 29.0 * (Double)this.scale.get(), 70.0 * (Double)this.scale.get(), 70.0 * (Double)this.scale.get(), (Color)this.color.get());
      Renderer2D.TEXTURE.render((class_4587)null);
   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Cat Girl", "You should use fabric api to see it!", CatGirl::new);
   }

   public static enum LogoEnum {
      CatGirl,
      LiLiBai,
      CNMB,
      SBGun,
      FUFU;

      // $FF: synthetic method
      private static LogoEnum[] $values() {
         return new LogoEnum[]{CatGirl, LiLiBai, CNMB, SBGun, FUFU};
      }
   }
}
