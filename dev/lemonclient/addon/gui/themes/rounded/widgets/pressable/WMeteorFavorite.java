package dev.lemonclient.addon.gui.themes.rounded.widgets.pressable;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorFavorite extends WFavorite implements LemonClientGuiWidget {
   public WMeteorFavorite(boolean checked) {
      super(checked);
   }

   protected Color getColor() {
      return (Color)this.theme().favoriteColor.get();
   }
}
