package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.utils.render.RenderUtils;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1799;
import net.minecraft.class_4587;

public class ArmorHud extends HudElement {
   private final SettingGroup sgGeneral;
   private final Setting scale;
   private final Setting rounding;
   private final Setting bg;
   private final Setting bgColor;
   private final Setting durColor;
   private final Setting durMode;
   public static final HudElementInfo INFO;

   public ArmorHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Scale")).description("Scale to render at.")).defaultValue(1.0).range(0.0, 5.0).sliderRange(0.0, 5.0).build());
      this.rounding = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Rounding")).description("How rounded should the background be.")).defaultValue(50)).range(0, 100).sliderRange(0, 100).build());
      this.bg = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Background")).description("Renders a background behind armor pieces.")).defaultValue(false)).build());
      this.bgColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Background Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(0, 0, 0, 150)).build());
      this.durColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Durability Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.durMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Durability Mode")).description("Where should durability be rendered at.")).defaultValue(ArmorHud.DurMode.Bottom)).build());
   }

   public void render(HudRenderer renderer) {
      if (MeteorClient.mc.field_1724 != null) {
         this.setSize(100.0 * (Double)this.scale.get() * 2.0, 28.0 * (Double)this.scale.get() * 2.0);
         class_4587 stack = new class_4587();
         stack.method_46416((float)this.x, (float)this.y, 0.0F);
         stack.method_22905((float)((Double)this.scale.get() * 2.0), (float)((Double)this.scale.get() * 2.0), 1.0F);
         if ((Boolean)this.bg.get()) {
            RenderUtils.rounded(stack, (float)(Integer)this.rounding.get() * 0.14F, (float)(Integer)this.rounding.get() * 0.14F, 100.0F - (float)(Integer)this.rounding.get() * 0.28F, 28.0F - (float)(Integer)this.rounding.get() * 0.28F, (float)(Integer)this.rounding.get() * 0.14F, 10, ((SettingColor)this.bgColor.get()).getPacked());
         }

         class_4587 drawStack = renderer.drawContext.method_51448();
         drawStack.method_22903();
         drawStack.method_46416((float)this.x, (float)this.y, 0.0F);
         drawStack.method_22905((float)((Double)this.scale.get() * 2.0), (float)((Double)this.scale.get() * 2.0), 1.0F);

         for(int i = 3; i >= 0; --i) {
            class_1799 itemStack = (class_1799)MeteorClient.mc.field_1724.method_31548().field_7548.get(i);
            renderer.drawContext.method_51427(itemStack, i * 20 + 12, this.durMode.get() == ArmorHud.DurMode.Top ? 10 : 0);
            if (!itemStack.method_7960()) {
               this.centeredText(stack, String.valueOf(Math.round(100.0F - (float)itemStack.method_7919() / (float)itemStack.method_7936() * 100.0F)), i * 20 + 20, this.durMode.get() == ArmorHud.DurMode.Top ? 3 : 17, ((SettingColor)this.durColor.get()).getPacked());
            }
         }

         drawStack.method_22909();
      }
   }

   private void centeredText(class_4587 stack, String text, int x, int y, int color) {
      RenderUtils.text(text, stack, (float)x - (float)MeteorClient.mc.field_1772.method_1727(text) / 2.0F, (float)y, color);
   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Armor Hud Plus", "A target hud the fuck you thinkin bruv.", ArmorHud::new);
   }

   public static enum DurMode {
      Top,
      Bottom;

      // $FF: synthetic method
      private static DurMode[] $values() {
         return new DurMode[]{Top, Bottom};
      }
   }
}
