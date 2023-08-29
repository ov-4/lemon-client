package dev.lemonclient.addon.gui.themes.rounded.widgets;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WMultiLabel;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorMultiLabel extends WMultiLabel implements LemonClientGuiWidget {
   public WMeteorMultiLabel(String text, boolean title, double maxWidth) {
      super(text, title, maxWidth);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      double h = this.theme.textHeight(this.title);
      Color color = (Color)this.theme().textColor.get();

      for(int i = 0; i < this.lines.size(); ++i) {
         renderer.text((String)this.lines.get(i), this.x, this.y + h * (double)i, color, false);
      }

   }
}
