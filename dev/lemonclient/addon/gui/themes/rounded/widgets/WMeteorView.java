package dev.lemonclient.addon.gui.themes.rounded.widgets;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;

public class WMeteorView extends WView implements LemonClientGuiWidget {
   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (this.canScroll && this.hasScrollBar) {
         renderer.quad(this.handleX(), this.handleY(), this.handleWidth(), this.handleHeight(), this.theme().scrollbarColor.get(this.handlePressed, this.handleMouseOver));
      }

   }
}
