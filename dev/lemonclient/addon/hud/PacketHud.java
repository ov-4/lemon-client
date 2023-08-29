package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class PacketHud extends HudElement {
   private final SettingGroup sgGeneral;
   private final Setting swap;
   public static final HudElementInfo INFO;

   public PacketHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.swap = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swap")).description("Swaps the order of the text.")).defaultValue(false)).build());
   }

   public void tick(HudRenderer renderer) {
      int send = MeteorClient.mc.method_1562() == null ? 23 : (int)MeteorClient.mc.method_1562().method_48296().method_10745();
      int received = MeteorClient.mc.method_1562() == null ? 86 : (int)MeteorClient.mc.method_1562().method_48296().method_10762();
      double width = 0.0;
      double height = 0.0;
      width += renderer.textWidth("Send: " + send + "10");
      height += renderer.textHeight() * 2.0;
      if (renderer.textWidth("Received: " + received + "10") > width) {
         width = renderer.textWidth("Received: " + received + "10");
      }

      this.box.setSize(width, height);
   }

   public void render(HudRenderer renderer) {
      int send = MeteorClient.mc.method_1562() == null ? 23 : (int)MeteorClient.mc.method_1562().method_48296().method_10745();
      int received = MeteorClient.mc.method_1562() == null ? 86 : (int)MeteorClient.mc.method_1562().method_48296().method_10762();
      double x = (double)this.x;
      double y = (double)this.y;
      Color primaryColor = TextHud.getSectionColor(0);
      Color secondaryColor = TextHud.getSectionColor(1);
      if ((Boolean)this.swap.get()) {
         renderer.text("Received: ", x, y, primaryColor, true);
         x += renderer.textWidth("Received: ");
         renderer.text("" + received, x, y, secondaryColor, true);
      } else {
         renderer.text("Send: ", x, y, primaryColor, true);
         x += renderer.textWidth("Send: ");
         renderer.text("" + send, x, y, secondaryColor, true);
      }

      y += renderer.textHeight();
      x = (double)this.x;
      if ((Boolean)this.swap.get()) {
         renderer.text("Send: ", x, y, primaryColor, true);
         x += renderer.textWidth("Send: ");
         renderer.text("" + send, x, y, secondaryColor, true);
      } else {
         renderer.text("Received: ", x, y, primaryColor, true);
         x += renderer.textWidth("Received: ");
         renderer.text("" + received, x, y, secondaryColor, true);
      }

   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Packet Hud", "Displays your average send packets.", PacketHud::new);
   }
}
