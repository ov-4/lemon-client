package dev.lemonclient.addon.gui.themes.rounded.widgets.input;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorLabel;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorTextBox extends WTextBox implements LemonClientGuiWidget {
   private boolean cursorVisible;
   private double cursorTimer;
   private double animProgress;

   public WMeteorTextBox(String text, String placeholder, CharFilter filter, Class renderer) {
      super(text, placeholder, filter, renderer);
   }

   protected void onCursorChanged() {
      this.cursorVisible = true;
      this.cursorTimer = 0.0;
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (this.cursorTimer >= 1.0) {
         this.cursorVisible = !this.cursorVisible;
         this.cursorTimer = 0.0;
      } else {
         this.cursorTimer += delta * 1.75;
      }

      this.renderBackground(renderer, this, false, false);
      LemonClientGuiTheme theme = this.theme();
      double pad = this.pad();
      double overflowWidth = this.getOverflowWidthForRender();
      renderer.scissorStart(this.x + pad, this.y + pad, this.width - pad * 2.0, this.height - pad * 2.0);
      if (!this.text.isEmpty()) {
         renderer.text(this.text, this.x + pad - overflowWidth, this.y + pad, (Color)theme.textColor.get(), false);
      } else if (this.placeholder != null) {
         this.renderer.render(renderer, this.x + pad - overflowWidth, this.y + pad, this.placeholder, (Color)theme.placeholderColor.get());
      }

      if (this.focused && (this.cursor != this.selectionStart || this.cursor != this.selectionEnd)) {
         double selStart = this.x + pad + this.getTextWidth(this.selectionStart) - overflowWidth;
         double selEnd = this.x + pad + this.getTextWidth(this.selectionEnd) - overflowWidth;
         renderer.quad(selStart, this.y + pad, selEnd - selStart, theme.textHeight(), (Color)theme.textHighlightColor.get());
      }

      this.animProgress += delta * 10.0 * (double)(this.focused && this.cursorVisible ? 1 : -1);
      this.animProgress = Math.max(0.0, Math.min(1.0, this.animProgress));
      if (this.focused && this.cursorVisible || this.animProgress > 0.0) {
         renderer.setAlpha(this.animProgress);
         renderer.quad(this.x + pad + this.getTextWidth(this.cursor) - overflowWidth, this.y + pad, theme.scale(1.0), theme.textHeight(), (Color)theme.textColor.get());
         renderer.setAlpha(1.0);
      }

      renderer.scissorEnd();
   }

   protected WContainer createCompletionsRootWidget() {
      return new WVerticalList() {
         protected void onRender(GuiRenderer renderer1, double mouseX, double mouseY, double delta) {
            LemonClientGuiTheme theme1 = WMeteorTextBox.this.theme();
            double s = theme1.scale(2.0);
            Color c = theme1.outlineColor.get();
            Color col = theme1.backgroundColor.get();
            int preA = col.a;
            col.a += col.a / 2;
            col.validate();
            renderer1.quad(this, col);
            col.a = preA;
            renderer1.quad(this.x, this.y + this.height - s, this.width, s, c);
            renderer1.quad(this.x, this.y, s, this.height - s, c);
            renderer1.quad(this.x + this.width - s, this.y, s, this.height - s, c);
         }
      };
   }

   protected WWidget createCompletionsValueWidth(String completion, boolean selected) {
      return new CompletionItem(completion, false, selected);
   }

   private static class CompletionItem extends WMeteorLabel implements WTextBox.ICompletionItem {
      private static final Color SELECTED_COLOR = new Color(255, 255, 255, 15);
      private boolean selected;

      public CompletionItem(String text, boolean title, boolean selected) {
         super(text, title);
         this.selected = selected;
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         super.onRender(renderer, mouseX, mouseY, delta);
         if (this.selected) {
            renderer.quad(this, SELECTED_COLOR);
         }

      }

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean selected) {
         this.selected = selected;
      }

      public String getCompletion() {
         return this.text;
      }
   }
}
