package dev.lemonclient.addon.gui.themes.rounded.widgets.pressable;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import dev.lemonclient.addon.utils.gui.GuiUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorCheckbox extends WCheckbox implements LemonClientGuiWidget {
   private double animProgress;

   public WMeteorCheckbox(boolean checked) {
      super(checked);
      this.animProgress = checked ? 1.0 : 0.0;
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      LemonClientGuiTheme theme = this.theme();
      this.animProgress += (double)(this.checked ? 1 : -1) * delta * 14.0;
      this.animProgress = Math.max(0.0, Math.min(1.0, this.animProgress));
      this.renderBackground(renderer, this, this.pressed, this.mouseOver);
      if (this.animProgress > 0.0) {
         double cs = (this.width - theme.scale(2.0)) / 1.75 * this.animProgress;
         GuiUtils.quadRounded(renderer, this.x + (this.width - cs) / 2.0, this.y + (this.height - cs) / 2.0, cs, cs, (Color)theme.checkboxColor.get(), (double)theme.roundAmount());
      }

   }
}
