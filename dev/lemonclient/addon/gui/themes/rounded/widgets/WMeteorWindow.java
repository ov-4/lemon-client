package dev.lemonclient.addon.gui.themes.rounded.widgets;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import dev.lemonclient.addon.utils.gui.GuiUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorWindow extends WWindow implements LemonClientGuiWidget {
   public WMeteorWindow(WWidget icon, String title) {
      super(icon, title);
   }

   protected WWindow.WHeader header(WWidget icon) {
      return new WMeteorHeader(icon);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (this.expanded || this.animProgress > 0.0) {
         GuiUtils.quadRounded(renderer, this.x, this.y + this.header.height / 2.0, this.width, this.height - this.header.height / 2.0, this.theme().backgroundColor.get(), (double)((LemonClientGuiTheme)this.theme).roundAmount(), false);
      }

   }

   private class WMeteorHeader extends WWindow.WHeader {
      public WMeteorHeader(WWidget icon) {
         super(WMeteorWindow.this, icon);
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         GuiUtils.quadRounded(renderer, this, (Color)WMeteorWindow.this.theme().accentColor.get(), (double)((LemonClientGuiTheme)this.theme).roundAmount());
      }
   }
}
