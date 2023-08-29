package dev.lemonclient.addon.utils.world.hole;

import dev.lemonclient.addon.enums.HoleType;
import dev.lemonclient.addon.utils.LemonUtils;
import net.minecraft.class_1657;
import net.minecraft.class_2338;

public class HoleUtils {
   public static Hole getHole(class_2338 pos) {
      return getHole(pos, true, true, true, 3, true);
   }

   public static Hole getHole(class_2338 pos, int depth) {
      return getHole(pos, depth, true);
   }

   public static Hole getHole(class_2338 pos, int depth, boolean floor) {
      return getHole(pos, true, true, true, depth, floor);
   }

   public static Hole getHole(class_2338 pos, boolean s, boolean d, boolean q, int depth, boolean floor) {
      if (!isHole(pos, depth, floor)) {
         return new Hole(pos, HoleType.NotHole);
      } else if (isBlock(pos.method_10067()) && isBlock(pos.method_10095())) {
         boolean x = isHole(pos.method_10078(), depth, floor) && isBlock(pos.method_10078().method_10095()) && isBlock(pos.method_10089(2));
         boolean z = isHole(pos.method_10072(), depth, floor) && isBlock(pos.method_10072().method_10067()) && isBlock(pos.method_10077(2));
         if (s && !x && !z && isBlock(pos.method_10078()) && isBlock(pos.method_10072())) {
            return new Hole(pos, HoleType.Single);
         } else if (q && x && z && isHole(pos.method_10072().method_10078(), depth, floor) && isBlock(pos.method_10078().method_10078().method_10072()) && isBlock(pos.method_10072().method_10072().method_10078())) {
            return new Hole(pos, HoleType.Quad);
         } else if (!d) {
            return new Hole(pos, HoleType.NotHole);
         } else if (x && !z && isBlock(pos.method_10072()) && isBlock(pos.method_10072().method_10078())) {
            return new Hole(pos, HoleType.DoubleX);
         } else {
            return z && !x && isBlock(pos.method_10078()) && isBlock(pos.method_10072().method_10078()) ? new Hole(pos, HoleType.DoubleZ) : new Hole(pos, HoleType.NotHole);
         }
      } else {
         return new Hole(pos, HoleType.NotHole);
      }
   }

   static boolean isBlock(class_2338 pos) {
      return LemonUtils.collidable(pos);
   }

   static boolean isHole(class_2338 pos, int depth, boolean floor) {
      if (floor && !isBlock(pos.method_10074())) {
         return false;
      } else {
         for(int i = 0; i < depth; ++i) {
            if (isBlock(pos.method_10086(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean inHole(class_1657 player) {
      class_2338 pos = player.method_24515();
      if (getHole(pos, 1).type == HoleType.Single) {
         return true;
      } else if (getHole(pos, 1).type != HoleType.DoubleX && getHole(pos.method_10069(-1, 0, 0), 1).type != HoleType.DoubleX) {
         if (getHole(pos, 1).type != HoleType.DoubleZ && getHole(pos.method_10069(0, 0, -1), 1).type != HoleType.DoubleZ) {
            return getHole(pos, 1).type == HoleType.Quad || getHole(pos.method_10069(-1, 0, -1), 1).type == HoleType.Quad || getHole(pos.method_10069(-1, 0, 0), 1).type == HoleType.Quad || getHole(pos.method_10069(0, 0, -1), 1).type == HoleType.Quad;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }
}
