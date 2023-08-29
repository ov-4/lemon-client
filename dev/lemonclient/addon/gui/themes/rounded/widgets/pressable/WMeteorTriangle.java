package dev.lemonclient.addon.gui.themes.rounded.widgets.pressable;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;

public class WMeteorTriangle extends WTriangle implements LemonClientGuiWidget {
   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      renderer.rotatedQuad(this.x, this.y, this.width, this.height, this.rotation, GuiRenderer.TRIANGLE, this.theme().backgroundColor.get(this.pressed, this.mouseOver));
   }
}
