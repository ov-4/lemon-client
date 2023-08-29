package dev.lemonclient.addon.gui.themes.rounded;

import dev.lemonclient.addon.utils.gui.GuiUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.BaseWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface LemonClientGuiWidget extends BaseWidget {
   default LemonClientGuiTheme theme() {
      return (LemonClientGuiTheme)this.getTheme();
   }

   default void renderBackground(GuiRenderer renderer, WWidget widget, boolean pressed, boolean mouseOver) {
      LemonClientGuiTheme theme = this.theme();
      int r = theme.roundAmount();
      double s = theme.scale(2.0);
      Color outlineColor = theme.outlineColor.get(pressed, mouseOver);
      GuiUtils.quadRounded(renderer, widget.x + s, widget.y + s, widget.width - s * 2.0, widget.height - s * 2.0, theme.backgroundColor.get(pressed, mouseOver), (double)r - s);
      GuiUtils.quadOutlineRounded(renderer, widget, outlineColor, (double)r, s);
   }
}
