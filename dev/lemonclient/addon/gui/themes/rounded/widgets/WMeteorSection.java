package dev.lemonclient.addon.gui.themes.rounded.widgets;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorSection extends WSection {
   public WMeteorSection(String title, boolean expanded, WWidget headerWidget) {
      super(title, expanded, headerWidget);
   }

   protected WSection.WHeader createHeader() {
      return new WMeteorHeader(this.title);
   }

   protected class WMeteorHeader extends WSection.WHeader {
      private WTriangle triangle;

      public WMeteorHeader(String title) {
         super(WMeteorSection.this, title);
      }

      public void init() {
         this.add(this.theme.horizontalSeparator(this.title)).expandX();
         if (WMeteorSection.this.headerWidget != null) {
            this.add(WMeteorSection.this.headerWidget);
         }

         this.triangle = new WHeaderTriangle();
         this.triangle.theme = this.theme;
         this.triangle.action = () -> {
            this.onClick();
         };
         this.add(this.triangle);
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         this.triangle.rotation = (1.0 - WMeteorSection.this.animProgress) * -90.0;
      }
   }

   protected static class WHeaderTriangle extends WTriangle implements LemonClientGuiWidget {
      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         renderer.rotatedQuad(this.x, this.y, this.width, this.height, this.rotation, GuiRenderer.TRIANGLE, (Color)this.theme().textColor.get());
      }
   }
}
