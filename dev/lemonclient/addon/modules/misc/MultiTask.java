package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.events.InteractEvent;
import meteordevelopment.orbit.EventHandler;

public class MultiTask extends LemonModule {
   public MultiTask() {
      super(LemonClient.Misc, "multi-task", "Allows you to eat while mining a block.");
   }

   @EventHandler
   public void onInteractEvent(InteractEvent event) {
      event.usingItem = false;
   }
}
