package dev.lemonclient.addon;

import dev.lemonclient.addon.modules.combat.AnchorAuraPlus;
import dev.lemonclient.addon.modules.combat.AntiAim;
import dev.lemonclient.addon.modules.combat.AutoCrystalPlus;
import dev.lemonclient.addon.modules.combat.AutoHoleFill;
import dev.lemonclient.addon.modules.combat.AutoHoleFillPlus;
import dev.lemonclient.addon.modules.combat.AutoMine;
import dev.lemonclient.addon.modules.combat.AutoTrapPlus;
import dev.lemonclient.addon.modules.combat.BedBombV4;
import dev.lemonclient.addon.modules.combat.KillAura;
import dev.lemonclient.addon.modules.combat.PistonCrystal;
import dev.lemonclient.addon.modules.combat.SelfTrapPlus;
import dev.lemonclient.addon.modules.combat.SpeedMine;
import dev.lemonclient.addon.modules.combat.SurroundPlus;
import dev.lemonclient.addon.modules.misc.AutoPearl;
import dev.lemonclient.addon.modules.misc.ScaffoldPlus;

public class Prioritys {
   public static int get(Object module) {
      if (module instanceof AnchorAuraPlus) {
         return 9;
      } else if (module instanceof AntiAim) {
         return 12;
      } else if (module instanceof AutoCrystalPlus) {
         return 10;
      } else if (module instanceof AutoHoleFill) {
         return 7;
      } else if (module instanceof AutoHoleFillPlus) {
         return 7;
      } else if (module instanceof PistonCrystal) {
         return 10;
      } else if (module instanceof AutoMine) {
         return 9;
      } else if (module instanceof AutoPearl) {
         return 6;
      } else if (module instanceof AutoTrapPlus) {
         return 5;
      } else if (module instanceof BedBombV4) {
         return 8;
      } else if (module instanceof KillAura) {
         return 11;
      } else if (module instanceof ScaffoldPlus) {
         return 2;
      } else if (module instanceof SelfTrapPlus) {
         return 1;
      } else if (module instanceof SpeedMine) {
         return 9;
      } else {
         return module instanceof SurroundPlus ? 0 : 100;
      }
   }
}
