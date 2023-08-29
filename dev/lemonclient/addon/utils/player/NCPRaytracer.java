package dev.lemonclient.addon.utils.player;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2404;
import net.minecraft.class_243;
import net.minecraft.class_2510;
import net.minecraft.class_2680;

public class NCPRaytracer {
   public static boolean raytrace(class_243 from, class_243 to, class_238 box) {
      int lx = 0;
      int ly = 0;
      int lz = 0;

      for(float i = 0.0F; i < 1.0F; i = (float)((double)i + 0.001)) {
         double x = lerp(from.field_1352, to.field_1352, (double)i);
         double y = lerp(from.field_1351, to.field_1351, (double)i);
         double z = lerp(from.field_1350, to.field_1350, (double)i);
         if (box.method_1008(x, y, z)) {
            return true;
         }

         int ix = (int)Math.floor(x);
         int iy = (int)Math.floor(y);
         int iz = (int)Math.floor(z);
         if (lx != ix || ly != iy || lz != iz) {
            class_2338 pos = new class_2338(ix, iy, iz);
            if (validForCheck(pos, MeteorClient.mc.field_1687.method_8320(pos))) {
               return false;
            }
         }

         lx = ix;
         ly = iy;
         lz = iz;
      }

      return false;
   }

   private static double lerp(double from, double to, double delta) {
      return from + (to - from) * delta;
   }

   public static boolean validForCheck(class_2338 pos, class_2680 state) {
      if (state.method_51367()) {
         return true;
      } else if (state.method_26204() instanceof class_2404) {
         return false;
      } else if (state.method_26204() instanceof class_2510) {
         return false;
      } else {
         return state.method_31709() ? false : state.method_26234(MeteorClient.mc.field_1687, pos);
      }
   }
}
