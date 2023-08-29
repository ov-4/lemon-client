package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;

public class AntiCrawl extends LemonModule {
   public AntiCrawl() {
      super(LemonClient.Misc, "Anti Crawl", "Doesn't crawl or sneak when in low space (should be used on 1.12.2).");
   }
}
