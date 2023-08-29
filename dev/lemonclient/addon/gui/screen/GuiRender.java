package dev.lemonclient.addon.gui.screen;

import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class GuiRender extends class_437 {
   public HudRenderer renderer;

   public GuiRender(class_2561 text) {
      super(text);
      this.renderer = HudRenderer.INSTANCE;
   }

   public GuiRender() {
      super(class_2561.method_30163("LemonClient"));
      this.renderer = HudRenderer.INSTANCE;
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float tickDelta) {
      this.draw(drawContext, mouseX, mouseY, tickDelta);
   }

   public void draw(class_332 matrices, int mouseX, int mouseY, float tickDelta) {
   }

   public boolean isMouseHoveringRect(double x, double y, double w, double h, double mouseX, double mouseY) {
      return mouseX >= x && mouseY >= y && mouseX <= x + w && mouseY <= y + h;
   }
}
