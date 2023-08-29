package dev.lemonclient.addon.gui.themes.rounded.widgets;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WVerticalSeparator;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorVerticalSeparator extends WVerticalSeparator implements LemonClientGuiWidget {
   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      LemonClientGuiTheme theme = this.theme();
      Color colorEdges = (Color)theme.separatorEdges.get();
      Color colorCenter = (Color)theme.separatorCenter.get();
      double s = theme.scale(1.0);
      double offsetX = (double)Math.round(this.width / 2.0);
      renderer.quad(this.x + offsetX, this.y, s, this.height / 2.0, colorEdges, colorEdges, colorCenter, colorCenter);
      renderer.quad(this.x + offsetX, this.y + this.height / 2.0, s, this.height / 2.0, colorCenter, colorCenter, colorEdges, colorEdges);
   }
}
