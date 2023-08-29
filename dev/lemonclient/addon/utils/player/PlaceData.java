package dev.lemonclient.addon.utils.player;

import net.minecraft.class_2338;
import net.minecraft.class_2350;

public record PlaceData(class_2338 pos, class_2350 dir, boolean valid) {
   public PlaceData(class_2338 pos, class_2350 dir, boolean valid) {
      this.pos = pos;
      this.dir = dir;
      this.valid = valid;
   }

   public class_2338 pos() {
      return this.pos;
   }

   public class_2350 dir() {
      return this.dir;
   }

   public boolean valid() {
      return this.valid;
   }
}
