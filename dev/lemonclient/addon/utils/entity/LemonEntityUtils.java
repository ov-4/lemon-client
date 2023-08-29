package dev.lemonclient.addon.utils.entity;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.EntityTrackingSectionAccessor;
import meteordevelopment.meteorclient.mixin.SectionedEntityCacheAccessor;
import meteordevelopment.meteorclient.mixin.SimpleEntityLookupAccessor;
import meteordevelopment.meteorclient.mixin.WorldAccessor;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_4076;
import net.minecraft.class_5572;
import net.minecraft.class_5573;
import net.minecraft.class_5577;
import net.minecraft.class_5578;

public class LemonEntityUtils {
   public static boolean intersectsWithEntity(class_238 box, Predicate predicate, Map customBoxes) {
      class_5577 entityLookup = ((WorldAccessor)MeteorClient.mc.field_1687).getEntityLookup();
      if (!(entityLookup instanceof class_5578 simpleEntityLookup)) {
         AtomicBoolean found = new AtomicBoolean(false);
         entityLookup.method_31807(box, (entityx) -> {
            if (!found.get() && predicate.test(entityx)) {
               found.set(true);
            }

         });
         return found.get();
      } else {
         class_5573 cache = ((SimpleEntityLookupAccessor)simpleEntityLookup).getCache();
         LongSortedSet trackedPositions = ((SectionedEntityCacheAccessor)cache).getTrackedPositions();
         Long2ObjectMap trackingSections = ((SectionedEntityCacheAccessor)cache).getTrackingSections();
         int i = class_4076.method_32204(box.field_1323 - 2.0);
         int j = class_4076.method_32204(box.field_1322 - 2.0);
         int k = class_4076.method_32204(box.field_1321 - 2.0);
         int l = class_4076.method_32204(box.field_1320 + 2.0);
         int m = class_4076.method_32204(box.field_1325 + 2.0);
         int n = class_4076.method_32204(box.field_1324 + 2.0);

         label75:
         for(int o = i; o <= l; ++o) {
            long p = class_4076.method_18685(o, 0, 0);
            long q = class_4076.method_18685(o, -1, -1);
            LongBidirectionalIterator longIterator = trackedPositions.subSet(p, q + 1L).iterator();

            while(true) {
               class_5572 entityTrackingSection;
               do {
                  do {
                     long r;
                     int t;
                     do {
                        int s;
                        do {
                           do {
                              do {
                                 if (!longIterator.hasNext()) {
                                    continue label75;
                                 }

                                 r = longIterator.nextLong();
                                 s = class_4076.method_18689(r);
                                 t = class_4076.method_18690(r);
                              } while(s < j);
                           } while(s > m);
                        } while(t < k);
                     } while(t > n);

                     entityTrackingSection = (class_5572)trackingSections.get(r);
                  } while(entityTrackingSection == null);
               } while(!entityTrackingSection.method_31768().method_31885());

               Iterator var25 = ((EntityTrackingSectionAccessor)entityTrackingSection).getCollection().iterator();

               while(var25.hasNext()) {
                  class_1297 entity = (class_1297)var25.next();
                  if ((entity instanceof class_1657 && customBoxes.containsKey(entity) ? (class_238)customBoxes.get(entity) : entity.method_5829()).method_994(box) && predicate.test(entity)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }
}
