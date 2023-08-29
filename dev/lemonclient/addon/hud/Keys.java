package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
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
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_304;
import net.minecraft.class_3532;

public class Keys extends HudElement {
   private final SettingGroup sgGeneral;
   private final Setting scale;
   private final Setting textColor;
   private final Setting cTextColor;
   private final Setting textBG;
   private final Setting bgColor;
   private final Setting cbgColor;
   private final Setting mode;
   private final Setting renderTime;
   private final Setting fadeTime;
   private List keys;
   public static final HudElementInfo INFO;

   public Keys() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Scale")).description("Scale to render at.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.textColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Key Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(30, 30, 30, 255)).build());
      this.cTextColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Clicked Key Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.textBG = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Key Background")).description("Should there be a background for keys.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      ColorSetting.Builder var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("BG Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(50, 50, 50, 255));
      Setting var10003 = this.textBG;
      Objects.requireNonNull(var10003);
      this.bgColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Clicked BG Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(50, 50, 50, 255));
      var10003 = this.textBG;
      Objects.requireNonNull(var10003);
      this.cbgColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description("Mode for key locations.")).defaultValue(Keys.Mode.Basic)).build());
      this.renderTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Render Time")).description("Seconds to keep full color before fading.")).defaultValue(0.0).min(0.0).sliderRange(0.0, 1.0).build());
      this.fadeTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Fade Time")).description("How many seconds should fading take.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 5.0).build());
      this.keys = null;
   }

   public void render(HudRenderer renderer) {
      if (this.keys == null) {
         this.keys = new ArrayList();
         class_304[] binds = new class_304[]{MeteorClient.mc.field_1690.field_1894, MeteorClient.mc.field_1690.field_1913, MeteorClient.mc.field_1690.field_1881, MeteorClient.mc.field_1690.field_1849};

         for(int i = 0; i < 4; ++i) {
            class_304 bind = binds[i];
            String key = bind.method_16007().getString().toUpperCase();
            this.keys.add(new Key(key, bind, i));
         }
      }

      double var10001;
      switch ((Mode)this.mode.get()) {
         case Horizontal:
            var10001 = 160.0;
            break;
         case Vertical:
            var10001 = 40.0;
            break;
         case Basic:
            var10001 = 120.0;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      var10001 = var10001 * (Double)this.scale.get() * (Double)this.scale.get();
      double var10002;
      switch ((Mode)this.mode.get()) {
         case Horizontal:
            var10002 = 40.0;
            break;
         case Vertical:
            var10002 = 160.0;
            break;
         case Basic:
            var10002 = 80.0;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      this.setSize(var10001, var10002 * (Double)this.scale.get() * (Double)this.scale.get());
      this.keys.forEach((keyx) -> {
         keyx.updatePos();
         keyx.checkClick();
         if ((Boolean)this.textBG.get()) {
            renderer.quad(keyx.posX + 2.0 * (Double)this.scale.get() * (Double)this.scale.get(), keyx.posY + 2.0 * (Double)this.scale.get() * (Double)this.scale.get(), 36.0 * (Double)this.scale.get() * (Double)this.scale.get(), 36.0 * (Double)this.scale.get() * (Double)this.scale.get(), this.getBGColor(keyx));
         }

         renderer.text(keyx.key, keyx.posX + this.xOffset(keyx.key, renderer), keyx.posY + this.yOffset(renderer), this.getTextColor(keyx), false, (Double)this.scale.get());
      });
   }

   private Color getBGColor(Key k) {
      return this.lerpColor(class_3532.method_15350(((double)k.sinceClick() - (Double)this.renderTime.get() * 1000.0) / (Double)this.fadeTime.get() / 1000.0, 0.0, 1.0), (Color)this.cbgColor.get(), (Color)this.bgColor.get());
   }

   private Color getTextColor(Key k) {
      return this.lerpColor(class_3532.method_15350(((double)k.sinceClick() - (Double)this.renderTime.get() * 1000.0) / (Double)this.fadeTime.get() / 1000.0, 0.0, 1.0), (Color)this.cTextColor.get(), (Color)this.textColor.get());
   }

   private Color lerpColor(double delta, Color s, Color e) {
      return new Color((int)Math.round(class_3532.method_16436(delta, (double)s.r, (double)e.r)), (int)Math.round(class_3532.method_16436(delta, (double)s.g, (double)e.g)), (int)Math.round(class_3532.method_16436(delta, (double)s.b, (double)e.b)), (int)Math.round(class_3532.method_16436(delta, (double)s.a, (double)e.a)));
   }

   private double xOffset(String string, HudRenderer renderer) {
      return (20.0 - renderer.textWidth(string, false) / 2.0) * (Double)this.scale.get() * (Double)this.scale.get();
   }

   private double yOffset(HudRenderer renderer) {
      return (20.0 - renderer.textHeight(false) / 2.0) * (Double)this.scale.get() * (Double)this.scale.get();
   }

   private double getX(int i) {
      double var10000;
      switch ((Mode)this.mode.get()) {
         case Horizontal:
            var10000 = (double)(i * 40);
            break;
         case Vertical:
            var10000 = 0.0;
            break;
         case Basic:
            var10000 = i == 0 ? 40.0 : (double)((i - 1) * 40);
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private double getY(int i) {
      double var10000;
      switch ((Mode)this.mode.get()) {
         case Horizontal:
            var10000 = 0.0;
            break;
         case Vertical:
            var10000 = (double)(i * 40);
            break;
         case Basic:
            var10000 = i == 0 ? 0.0 : 40.0;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Keys", "Draws pressed movement keys.", Keys::new);
   }

   public static enum Mode {
      Horizontal,
      Vertical,
      Basic;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Horizontal, Vertical, Basic};
      }
   }

   private class Key {
      public final String key;
      public final class_304 bind;
      public final int i;
      public double posX = 0.0;
      public double posY = 0.0;
      public long lastClicked = 0L;

      public Key(String key, class_304 bind, int i) {
         this.key = key;
         this.bind = bind;
         this.i = i;
      }

      public void updatePos() {
         this.posX = (double)Keys.this.x + Keys.this.getX(this.i) * (Double)Keys.this.scale.get() * (Double)Keys.this.scale.get();
         this.posY = (double)Keys.this.y + Keys.this.getY(this.i) * (Double)Keys.this.scale.get() * (Double)Keys.this.scale.get();
      }

      public void checkClick() {
         if (this.bind.method_1434()) {
            this.lastClicked = System.currentTimeMillis();
         }

      }

      public long sinceClick() {
         return System.currentTimeMillis() - this.lastClicked;
      }
   }
}
