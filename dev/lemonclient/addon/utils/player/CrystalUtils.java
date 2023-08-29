package dev.lemonclient.addon.utils.player;

import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.world.PredictionUtils;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.mixininterface.IExplosion;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1280;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1890;
import net.minecraft.class_1922;
import net.minecraft.class_1927;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_5134;
import net.minecraft.class_1927.class_4179;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class CrystalUtils {
   private static final class_243 vec3d = new class_243(0.0, 0.0, 0.0);
   private static class_1927 explosion;
   private static class_3959 raycastContext;

   @PreInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(CrystalUtils.class);
   }

   @EventHandler
   private static void onGameJoined(GameJoinedEvent event) {
      explosion = new class_1927(MeteorClient.mc.field_1687, (class_1297)null, 0.0, 0.0, 0.0, 6.0F, false, class_4179.field_18687);
      raycastContext = new class_3959((class_243)null, (class_243)null, class_3960.field_17558, class_242.field_1347, MeteorClient.mc.field_1724);
   }

   public static double crystalDamage(class_1657 player, class_243 crystal, boolean predictMovement, boolean collision, int i, class_2338 obsidianPos, boolean ignoreTerrain) {
      if (!EntityInfo.notNull(player)) {
         return 0.0;
      } else if (EntityInfo.isCreative(player) && !(player instanceof FakePlayerEntity)) {
         return 0.0;
      } else {
         class_243 pVec = PredictionUtils.returnPredictVec(player, collision, i);
         ((IVec3d)vec3d).set(player.method_19538().field_1352, player.method_19538().field_1351, player.method_19538().field_1350);
         if (predictMovement) {
            ((IVec3d)vec3d).set(pVec.method_10216(), pVec.method_10214(), pVec.method_10215());
         }

         double modDistance = Math.sqrt(vec3d.method_1025(crystal));
         if (modDistance > 12.0) {
            return 0.0;
         } else {
            double exposure = getExposure(crystal, player, predictMovement, collision, i, raycastContext, obsidianPos, ignoreTerrain);
            double impact = (1.0 - modDistance / 12.0) * exposure;
            double damage = (impact * impact + impact) / 2.0 * 7.0 * 12.0 + 1.0;
            damage = getDamageForDifficulty(damage);
            damage = (double)class_1280.method_5496((float)damage, (float)player.method_6096(), (float)player.method_5996(class_5134.field_23725).method_6194());
            damage = resistanceReduction(player, damage);
            ((IExplosion)explosion).set(crystal, 6.0F, false);
            damage = blastProtReduction(player, damage, explosion);
            return damage < 0.0 ? 0.0 : damage;
         }
      }
   }

   public static double crystalDamage(class_1657 player, class_243 crystal) {
      return crystalDamage(player, crystal, false, false, 0, (class_2338)null, false);
   }

   private static double getDamageForDifficulty(double damage) {
      double var10000;
      switch (MeteorClient.mc.field_1687.method_8407()) {
         case field_5801:
            var10000 = 0.0;
            break;
         case field_5805:
            var10000 = Math.min(damage / 2.0 + 1.0, damage);
            break;
         case field_5807:
            var10000 = damage * 3.0 / 2.0;
            break;
         default:
            var10000 = damage;
      }

      return var10000;
   }

   private static double blastProtReduction(class_1297 player, double damage, class_1927 explosion) {
      int protLevel = class_1890.method_8219(player.method_5661(), MeteorClient.mc.field_1687.method_48963().method_48807(explosion));
      if (protLevel > 20) {
         protLevel = 20;
      }

      damage *= 1.0 - (double)protLevel / 25.0;
      return damage < 0.0 ? 0.0 : damage;
   }

   private static double resistanceReduction(class_1309 player, double damage) {
      if (player.method_6059(class_1294.field_5907)) {
         int lvl = player.method_6112(class_1294.field_5907).method_5578() + 1;
         damage *= 1.0 - (double)lvl * 0.2;
      }

      return damage < 0.0 ? 0.0 : damage;
   }

   private static double getExposure(class_243 source, class_1297 entity, boolean predictMovement, boolean collision, int ii, class_3959 raycastContext, class_2338 obsidianPos, boolean ignoreTerrain) {
      class_238 box = EntityInfo.getBoundingBox(entity);
      if (predictMovement) {
         box = PredictionUtils.returnPredictBox((class_1657)entity, collision, ii);
      }

      double d = 1.0 / ((box.field_1320 - box.field_1323) * 2.0 + 1.0);
      double e = 1.0 / ((box.field_1325 - box.field_1322) * 2.0 + 1.0);
      double f = 1.0 / ((box.field_1324 - box.field_1321) * 2.0 + 1.0);
      double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
      double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
      if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
         int i = 0;
         int j = 0;

         for(double k = 0.0; k <= 1.0; k += d) {
            for(double l = 0.0; l <= 1.0; l += e) {
               for(double m = 0.0; m <= 1.0; m += f) {
                  double n = class_3532.method_16436(k, box.field_1323, box.field_1320);
                  double o = class_3532.method_16436(l, box.field_1322, box.field_1325);
                  double p = class_3532.method_16436(m, box.field_1321, box.field_1324);
                  ((IVec3d)vec3d).set(n + g, o, p + h);
                  ((IRaycastContext)raycastContext).set(vec3d, source, class_3960.field_17558, class_242.field_1348, entity);
                  if (raycast(raycastContext, obsidianPos, ignoreTerrain).method_17783() == class_240.field_1333) {
                     ++i;
                  }

                  ++j;
               }
            }
         }

         return (double)i / (double)j;
      } else {
         return 0.0;
      }
   }

   private static class_3965 raycast(class_3959 context, class_2338 obsidianPos, boolean ignoreTerrain) {
      return (class_3965)class_1922.method_17744(context.method_17750(), context.method_17747(), context, (raycastContext, blockPos) -> {
         class_2680 blockState;
         if (blockPos.equals(obsidianPos)) {
            blockState = class_2246.field_10540.method_9564();
         } else {
            blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
            if (blockState.method_26204().method_9520() < 600.0F && ignoreTerrain) {
               blockState = class_2246.field_10124.method_9564();
            }
         }

         class_243 vec3d = raycastContext.method_17750();
         class_243 vec3d2 = raycastContext.method_17747();
         class_265 voxelShape = raycastContext.method_17748(blockState, MeteorClient.mc.field_1687, blockPos);
         class_3965 blockHitResult = MeteorClient.mc.field_1687.method_17745(vec3d, vec3d2, blockPos, voxelShape, blockState);
         class_265 voxelShape2 = class_259.method_1073();
         class_3965 blockHitResult2 = voxelShape2.method_1092(vec3d, vec3d2, blockPos);
         double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.method_17750().method_1025(blockHitResult.method_17784());
         double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.method_17750().method_1025(blockHitResult2.method_17784());
         return d <= e ? blockHitResult : blockHitResult2;
      }, (raycastContext) -> {
         class_243 vec3d = raycastContext.method_17750().method_1020(raycastContext.method_17747());
         return class_3965.method_17778(raycastContext.method_17747(), class_2350.method_10142(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350), class_2338.method_49638(raycastContext.method_17747()));
      });
   }
}
