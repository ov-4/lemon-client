package dev.lemonclient.addon.gui.themes.rounded.widgets.input;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorSlider extends WSlider implements LemonClientGuiWidget {
   public WMeteorSlider(double value, double min, double max) {
      super(value, min, max);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      double valueWidth = this.valueWidth();
      this.renderBar(renderer, valueWidth);
      this.renderHandle(renderer, valueWidth);
   }

   private void renderBar(GuiRenderer renderer, double valueWidth) {
      LemonClientGuiTheme theme = this.theme();
      double s = theme.scale(3.0);
      double handleSize = this.handleSize();
      double x = this.x + handleSize / 2.0;
      double y = this.y + this.height / 2.0 - s / 2.0;
      renderer.quad(x, y, valueWidth, s, (Color)theme.sliderLeft.get());
      renderer.quad(x + valueWidth, y, this.width - valueWidth - handleSize, s, (Color)theme.sliderRight.get());
   }

   private void renderHandle(GuiRenderer renderer, double valueWidth) {
      LemonClientGuiTheme theme = this.theme();
      double s = this.handleSize();
      renderer.quad(this.x + valueWidth, this.y, s, s, GuiRenderer.CIRCLE, theme.sliderHandle.get(this.dragging, this.handleMouseOver));
   }
}
