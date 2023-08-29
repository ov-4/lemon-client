package dev.lemonclient.addon.gui.themes.rounded.widgets;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.utils.gui.GuiUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WQuad;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorQuad extends WQuad {
   public WMeteorQuad(Color color) {
      super(color);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      GuiUtils.quadRounded(renderer, this.x, this.y, this.width, this.height, this.color, (double)((LemonClientGuiTheme)this.theme).roundAmount());
   }
}
