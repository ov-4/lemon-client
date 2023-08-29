package dev.lemonclient.addon.enums;

public enum RotationType {
   Interact,
   BlockPlace,
   Attacking,
   Mining,
   Use,
   Other;

   // $FF: synthetic method
   private static RotationType[] $values() {
      return new RotationType[]{Interact, BlockPlace, Attacking, Mining, Use, Other};
   }
}
