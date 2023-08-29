package dev.lemonclient.addon.gui.themes.rounded.widgets;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiWidget;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.widgets.WAccount;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorAccount extends WAccount implements LemonClientGuiWidget {
   public WMeteorAccount(WidgetScreen screen, Account account) {
      super(screen, account);
   }

   protected Color loggedInColor() {
      return (Color)this.theme().loggedInColor.get();
   }

   protected Color accountTypeColor() {
      return (Color)this.theme().textSecondaryColor.get();
   }
}
