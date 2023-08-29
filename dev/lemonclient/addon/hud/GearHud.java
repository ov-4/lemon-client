package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_4587;

public class GearHud extends HudElement {
   private final SettingGroup sgGeneral;
   private final Setting items;
   private final Setting scale;
   private final Setting color;
   private final Setting shadow;
   private final Setting experienceInfo;
   public static final HudElementInfo INFO;

   public GearHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.items = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("Items")).description("Items to show.")).defaultValue(new class_1792[]{class_1802.field_8301, class_1802.field_8287, class_1802.field_8281, class_1802.field_8288}).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Scale")).description("The scale.")).defaultValue(1.5).min(0.0).sliderRange(0.0, 10.0).build());
      this.color = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Shadow")).description("Renders a shadow behind the chars.")).defaultValue(true)).build());
      this.experienceInfo = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Experience Info")).description("Displays mend percentage for armor next to experience bottles.")).defaultValue(true)).build());
   }

   public void render(HudRenderer renderer) {
      this.setSize(55.0 * (Double)this.scale.get() * (Double)this.scale.get(), 20.0 * (Double)this.scale.get() * (Double)this.scale.get() * (double)((List)this.items.get()).size());

      for(int i = 0; i < ((List)this.items.get()).size(); ++i) {
         int posY = (int)Math.round((double)this.y + (double)(i * 20) * (Double)this.scale.get() * (Double)this.scale.get());
         class_4587 drawStack = renderer.drawContext.method_51448();
         drawStack.method_22903();
         drawStack.method_46416((float)this.x, (float)this.y, 0.0F);
         drawStack.method_22905((float)((Double)this.scale.get() * (Double)this.scale.get()), (float)((Double)this.scale.get() * (Double)this.scale.get()), 1.0F);
         renderer.drawContext.method_51427(((class_1792)((List)this.items.get()).get(i)).method_7854(), this.x, posY);
         drawStack.method_22909();
         renderer.text(this.getText(((class_1792)((List)this.items.get()).get(i)).method_8389()), (double)this.x + 25.0 * (Double)this.scale.get() * (Double)this.scale.get(), (double)posY, (Color)this.color.get(), (Boolean)this.shadow.get(), (Double)this.scale.get());
      }

   }

   private int amountOf(class_1792 item) {
      return InvUtils.find((itemStack) -> {
         return itemStack.method_7909().equals(item);
      }).count();
   }

   private String getText(class_1792 item) {
      if (item == class_1802.field_8287 && this.armorDur() > 0.0 && (Boolean)this.experienceInfo.get()) {
         int var10000 = this.amountOf(item);
         return "" + var10000 + "  " + Math.round((double)(this.amountOf(item) * 14) / this.armorDur() * 100.0) + "%";
      } else {
         return String.valueOf(this.amountOf(item));
      }
   }

   private double armorDur() {
      double rur = 0.0;
      if (MeteorClient.mc.field_1724 != null) {
         for(int i = 0; i < 4; ++i) {
            rur += (double)((class_1799)MeteorClient.mc.field_1724.method_31548().field_7548.get(i)).method_7936();
         }
      }

      return rur;
   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Gear Hud", "Item Hud lol.", GearHud::new);
   }
}
