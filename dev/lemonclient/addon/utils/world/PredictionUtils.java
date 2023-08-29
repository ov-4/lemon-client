package dev.lemonclient.addon.utils.world;

import dev.lemonclient.addon.utils.entity.EntityInfo;
import java.util.ArrayList;
import java.util.Iterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class PredictionUtils {
   public static ArrayList getPredictCollisionBlocks(class_2338 blockpos) {
      ArrayList array = new ArrayList();
      if (BlockInfo.isBlastResist(blockpos)) {
         array.add(blockpos);
      }

      class_2350[] var2 = class_2350.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         class_2350 d = var2[var4];
         if (d != class_2350.field_11036 && d != class_2350.field_11033) {
            class_2338 pos = blockpos.method_10093(d);
            if (BlockInfo.isBlastResist(pos)) {
               array.add(pos);
            }
         }
      }

      return array;
   }

   public static class_238 returnPredictBox(class_1657 entity, boolean collision, int i) {
      class_238 eBox = EntityInfo.getBoundingBox(entity);
      class_243 eVec = EntityInfo.getVelocity(entity);
      class_238 pBox = eBox.method_989(eVec.method_10216() * (double)i, 0.0, eVec.method_10215() * (double)i);
      if (getEntitySpeed(entity) < 4.0) {
         return eBox;
      } else {
         if (collision) {
            ArrayList l = new ArrayList(getPredictCollisionBlocks(class_2338.method_49638(pBox.method_1005())));
            Iterator var7 = l.iterator();

            while(var7.hasNext()) {
               class_2338 p = (class_2338)var7.next();
               class_238 bBox = new class_238(p);
               if (bBox.method_994(pBox)) {
                  return eBox;
               }
            }
         }

         return pBox;
      }
   }

   public static class_243 returnPredictVec(class_1657 entity, boolean collision, int i) {
      class_238 eBox = EntityInfo.getBoundingBox(entity);
      class_243 eVec = EntityInfo.getVelocity(entity);
      class_243 pVec = new class_243(entity.method_19538().field_1352, entity.method_19538().field_1351, entity.method_19538().field_1350);
      class_238 pBox = eBox.method_989(eVec.method_10216() * (double)i, 0.0, eVec.method_10215() * (double)i);
      if (getEntitySpeed(entity) < 4.0) {
         return pVec;
      } else {
         if (collision) {
            ArrayList l = new ArrayList(getPredictCollisionBlocks(class_2338.method_49638(pBox.method_1005())));
            Iterator var8 = l.iterator();

            while(var8.hasNext()) {
               class_2338 p = (class_2338)var8.next();
               class_238 bBox = new class_238(p);
               if (bBox.method_994(pBox)) {
                  return pVec;
               }
            }
         }

         class_243 spVec = new class_243(pVec.field_1352 + eVec.field_1352 * (double)i, pVec.field_1351, pVec.field_1350 + eVec.field_1350 * (double)i);
         return MeteorClient.mc.field_1724.method_19538().method_1022(spVec) > 7.0 ? pVec : spVec;
      }
   }

   public static double getEntitySpeed(class_1657 entity) {
      if (entity == null) {
         return 0.0;
      } else {
         double tX = Math.abs(entity.method_23317() - entity.field_6014);
         double tZ = Math.abs(entity.method_23321() - entity.field_5969);
         double length = Math.sqrt(tX * tX + tZ * tZ);
         if (entity == MeteorClient.mc.field_1724) {
            Timer timer = (Timer)Modules.get().get(Timer.class);
            if (timer.isActive()) {
               length *= ((Timer)Modules.get().get(Timer.class)).getMultiplier();
            }
         }

         return length * 20.0;
      }
   }
}
