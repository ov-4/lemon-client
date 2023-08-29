package dev.lemonclient.addon.events;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_1713;

public class ClickWindowEvent extends Cancellable {
   private static final ClickWindowEvent INSTANCE = new ClickWindowEvent();
   public int windowId;
   public int slotId;
   public int mouseButtonClicked;
   public class_1713 mode;

   public static ClickWindowEvent get(int windowId, int slotId, int mouseButtonClicked, class_1713 mode) {
      INSTANCE.setCancelled(false);
      INSTANCE.windowId = windowId;
      INSTANCE.mouseButtonClicked = mouseButtonClicked;
      INSTANCE.slotId = slotId;
      INSTANCE.mode = mode;
      return INSTANCE;
   }
}
