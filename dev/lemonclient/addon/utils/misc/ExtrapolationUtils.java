package dev.lemonclient.addon.utils.misc;

import dev.lemonclient.addon.utils.LemonUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_742;

public class ExtrapolationUtils {
   private static Map motions = new HashMap();

   @PreInit
   public static void preInit() {
      MeteorClient.EVENT_BUS.subscribe(ExtrapolationUtils.class);
   }

   @EventHandler(
      priority = 1000000
   )
   private static void onTick(TickEvent.Post event) {
      if (MeteorClient.mc.field_1724 != null && MeteorClient.mc.field_1687 != null && !MeteorClient.mc.field_1687.method_18456().isEmpty()) {
         Map newMotions = new HashMap();
         Iterator var2 = MeteorClient.mc.field_1687.method_18456().iterator();

         while(var2.hasNext()) {
            class_742 player = (class_742)var2.next();
            class_243 vec = player.method_19538().method_1023(player.field_6014, player.field_6036, player.field_5969);
            if (!motions.containsKey(player)) {
               List v = new ArrayList();
               v.add(vec);
               newMotions.put(player, v);
            } else {
               List v = (List)motions.get(player);
               v.add(0, vec);
               if (v.size() > 20) {
                  v.subList(20, v.size()).clear();
               }

               newMotions.put(player, v);
            }
         }

         motions = newMotions;
      }
   }

   public static void extrapolateMap(Map old, EpicInterface extrapolation, EpicInterface smoothening) {
      old.clear();
      motions.forEach((player, m) -> {
         if (m != null) {
            old.put(player, extrapolate(player, m, (Integer)extrapolation.get(player), (Integer)smoothening.get(player)));
         }
      });
   }

   public static class_238 extrapolate(class_742 player, int extrapolation, int smoothening) {
      List m = (List)motions.get(player);
      return m == null ? null : extrapolate(player, m, extrapolation, smoothening);
   }

   public static class_238 extrapolate(class_742 player, List m, int extrapolation, int smoothening) {
      class_243 motion = getMotion(m, smoothening);
      double x = motion.field_1352;
      double y = motion.field_1351;
      double z = motion.field_1350;
      double stepHeight = 0.6;
      class_238 box = new class_238(player.method_23317() - 0.3, player.method_23318(), player.method_23321() - 0.3, player.method_23317() + 0.3, player.method_23318() + (player.method_5829().field_1325 - player.method_5829().field_1322), player.method_23321() + 0.3);
      boolean onGround = inside(player, box.method_989(0.0, -0.04, 0.0));

      for(int i = 0; i < extrapolation; ++i) {
         List list = MeteorClient.mc.field_1687.method_20743(player, box.method_1012(x, y, z));
         class_243 movement = new class_243(x, y, z);
         class_243 vec3d = movement.method_1027() == 0.0 ? movement : class_1297.method_20736(player, movement, box, MeteorClient.mc.field_1687, list);
         boolean canStep = (onGround || y < 0.0 && vec3d.field_1351 != y) && (vec3d.field_1352 != x || vec3d.field_1350 != z);
         if (canStep) {
            class_243 vec3d2 = class_1297.method_20736(player, new class_243(x, stepHeight, z), box, MeteorClient.mc.field_1687, list);
            class_243 vec3d3 = class_1297.method_20736(player, new class_243(0.0, stepHeight, 0.0), box.method_1012(x, 0.0, z), MeteorClient.mc.field_1687, list);
            class_243 vec;
            if (vec3d3.field_1351 < stepHeight) {
               vec = class_1297.method_20736(player, new class_243(movement.field_1352, 0.0, movement.field_1350), box.method_997(vec3d3), MeteorClient.mc.field_1687, list).method_1019(vec3d3);
               if (vec.method_37268() > vec3d2.method_37268()) {
                  vec3d2 = vec;
               }
            }

            if (vec3d2.method_37268() > vec3d.method_37268()) {
               vec = vec3d2.method_1019(class_1297.method_20736(player, new class_243(0.0, -vec3d2.field_1351 + movement.field_1351, 0.0), box.method_997(vec3d2), MeteorClient.mc.field_1687, list));
               box = box.method_997(vec);
               onGround = true;
               continue;
            }
         }

         box = box.method_997(vec3d);
         onGround = inside(player, box.method_989(0.0, -0.04, 0.0));
         if (onGround) {
            y = 0.0;
         }

         y = (y - 0.08) * 0.98;
      }

      return box;
   }

   private static boolean inside(class_1657 player, class_238 box) {
      return LemonUtils.inside(player, box);
   }

   private static class_243 getMotion(List vecs, int max) {
      class_243 avg = new class_243(0.0, (((class_243)vecs.get(0)).field_1351 - 0.08) * 0.98, 0.0);
      int s = Math.min(vecs.size(), max);

      for(int i = 0; i < s; ++i) {
         avg = avg.method_1031(((class_243)vecs.get(i)).field_1352, 0.0, ((class_243)vecs.get(i)).field_1350);
      }

      return avg.method_18805((double)(1.0F / (float)s), 1.0, (double)(1.0F / (float)s));
   }
}
