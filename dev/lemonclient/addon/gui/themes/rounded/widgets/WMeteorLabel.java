package dev.lemonclient.addon.gui.themes.rounded.widgets;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorLabel extends WLabel implements LemonClientGuiWidget {
   public WMeteorLabel(String text, boolean title) {
      super(text, title);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (!this.text.isEmpty()) {
         renderer.text(this.text, this.x, this.y, this.color != null ? this.color : (this.title ? (Color)this.theme().titleTextColor.get() : (Color)this.theme().textColor.get()), this.title);
      }

   }
}
