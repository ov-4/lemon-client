package dev.lemonclient.addon.gui.themes.rounded.widgets.pressable;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorButton extends WButton implements LemonClientGuiWidget {
   public WMeteorButton(String text, GuiTexture texture) {
      super(text, texture);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      LemonClientGuiTheme theme = this.theme();
      double pad = this.pad();
      this.renderBackground(renderer, this, this.pressed, this.mouseOver);
      if (this.text != null) {
         renderer.text(this.text, this.x + this.width / 2.0 - this.textWidth / 2.0, this.y + pad, (Color)theme.textColor.get(), false);
      } else {
         double ts = theme.textHeight();
         renderer.quad(this.x + this.width / 2.0 - ts / 2.0, this.y + pad, ts, ts, this.texture, (Color)theme.textColor.get());
      }

   }
}
