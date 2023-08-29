package dev.lemonclient.addon.gui.themes.rounded.widgets;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import dev.lemonclient.addon.utils.gui.GuiUtils;
import java.util.Iterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

public class WMeteorTopBar extends WTopBar implements LemonClientGuiWidget {
   protected Color getButtonColor(boolean pressed, boolean hovered) {
      return this.theme().backgroundColor.get(pressed, hovered);
   }

   protected Color getNameColor() {
      return (Color)this.theme().textColor.get();
   }

   public void init() {
      Iterator var1 = Tabs.get().iterator();

      while(var1.hasNext()) {
         Tab tab = (Tab)var1.next();
         this.add(new WTopBarButton(tab));
      }

   }

   protected int getState(WTopBarButton btn) {
      int a = 0;
      if (btn.equals(((Cell)this.cells.get(0)).widget())) {
         a |= 1;
      }

      if (btn.equals(((Cell)this.cells.get(this.cells.size() - 1)).widget())) {
         a |= 2;
      }

      return a;
   }

   protected class WTopBarButton extends WPressable {
      private final Tab tab;

      public WTopBarButton(Tab tab) {
         this.tab = tab;
      }

      protected void onCalculateSize() {
         double pad = this.pad();
         this.width = pad + this.theme.textWidth(this.tab.name) + pad;
         this.height = pad + this.theme.textHeight() + pad;
      }

      protected void onPressed(int button) {
         class_437 screen = MeteorClient.mc.field_1755;
         if (!(screen instanceof TabScreen) || ((TabScreen)screen).tab != this.tab) {
            double mouseX = MeteorClient.mc.field_1729.method_1603();
            double mouseY = MeteorClient.mc.field_1729.method_1604();
            this.tab.openScreen(this.theme);
            GLFW.glfwSetCursorPos(MeteorClient.mc.method_22683().method_4490(), mouseX, mouseY);
         }

      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         double pad = this.pad();
         Color color = WMeteorTopBar.this.getButtonColor(this.pressed || MeteorClient.mc.field_1755 instanceof TabScreen && ((TabScreen)MeteorClient.mc.field_1755).tab == this.tab, this.mouseOver);
         switch (WMeteorTopBar.this.getState(this)) {
            case 1:
               GuiUtils.quadRoundedSide(renderer, this, color, (double)((LemonClientGuiTheme)this.theme).roundAmount(), false);
               break;
            case 2:
               GuiUtils.quadRoundedSide(renderer, this, color, (double)((LemonClientGuiTheme)this.theme).roundAmount(), true);
               break;
            case 3:
               GuiUtils.quadRounded(renderer, this, color, (double)((LemonClientGuiTheme)this.theme).roundAmount());
               break;
            default:
               renderer.quad(this, color);
         }

         renderer.text(this.tab.name, this.x + pad, this.y + pad, WMeteorTopBar.this.getNameColor(), false);
      }
   }
}
