package dev.lemonclient.addon.utils.entity;

import dev.lemonclient.addon.utils.world.BlockInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1263;
import net.minecraft.class_1297;
import net.minecraft.class_1303;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1656;
import net.minecraft.class_1657;
import net.minecraft.class_1667;
import net.minecraft.class_1683;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2189;
import net.minecraft.class_2199;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2374;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class EntityInfo {
   public static boolean checkEntity(class_2338 pos, boolean breakCrystal, double safeHealth) {
      Iterator var4 = MeteorClient.mc.field_1687.method_8333((class_1297)null, new class_238(pos), (entityx) -> {
         return entityx == MeteorClient.mc.field_1724;
      }).iterator();

      while(true) {
         class_1297 entity;
         do {
            do {
               do {
                  do {
                     do {
                        if (!var4.hasNext()) {
                           return false;
                        }

                        entity = (class_1297)var4.next();
                     } while(isDead(MeteorClient.mc.field_1724));
                  } while(entity instanceof class_1542);
               } while(entity instanceof class_1303);
            } while(entity instanceof class_1683);
         } while(entity instanceof class_1667);

         if (entity instanceof class_1511) {
            if (!breakCrystal || (double)MeteorClient.mc.field_1724.method_6032() < safeHealth) {
               break;
            }
         } else {
            if (entity == MeteorClient.mc.field_1724) {
               continue;
            }
            break;
         }
      }

      return true;
   }

   public static boolean checkEntity(class_2338 pos) {
      Iterator var1 = MeteorClient.mc.field_1687.method_8333((class_1297)null, new class_238(pos), (entityx) -> {
         return entityx == MeteorClient.mc.field_1724;
      }).iterator();

      class_1297 entity;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         entity = (class_1297)var1.next();
      } while(isDead(MeteorClient.mc.field_1724) || entity instanceof class_1542 || entity instanceof class_1303 || entity instanceof class_1683 || entity instanceof class_1667);

      return true;
   }

   public static double distanceTo(class_2338 blockPos1, class_1657 player) {
      if (blockPos1 != null && player != null) {
         double d = (double)blockPos1.method_10263() - player.method_23317();
         double e = (double)blockPos1.method_10264() - player.method_23318();
         double f = (double)blockPos1.method_10260() - player.method_23321();
         return (double)class_3532.method_15355((float)(d * d + e * e + f * f));
      } else {
         return 99.0;
      }
   }

   public static boolean isPlayerNear(class_2338 blockPos) {
      if (blockPos == null) {
         return false;
      } else {
         Iterator var1 = MeteorClient.mc.field_1687.method_18456().iterator();

         class_1657 player;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            player = (class_1657)var1.next();
         } while(player == null || distanceTo(blockPos, player) > 5.0 || player.method_29504() || player == MeteorClient.mc.field_1724 || Friends.get().isFriend(player) || !getBlocksAround(player).contains(blockPos));

         return true;
      }
   }

   public static boolean isAlive(class_1657 entity) {
      return entity.method_5805();
   }

   public static boolean isDead(class_1657 entity) {
      return entity.method_29504();
   }

   public static boolean isWebbed(class_1657 entity) {
      return BlockInfo.doesBoxTouchBlock(entity.method_5829(), class_2246.field_10343);
   }

   public static boolean isOnGround(class_1657 entity) {
      return entity.method_24828();
   }

   public static float getMovementSpeed(class_1657 entity) {
      return entity.method_6029();
   }

   public static boolean isMoving(class_1657 entity) {
      return entity.field_6250 != 0.0F || entity.field_6212 != 0.0F;
   }

   public static boolean canRecieveDmg(class_1657 entity) {
      return entity.field_6235 == 0;
   }

   public static class_2338 getBlockPos(class_1657 entity) {
      return entity.method_24515();
   }

   public static class_2338 getBlockPos(class_1297 entity) {
      return entity.method_24515();
   }

   public static class_2374 getPos(class_1657 entity) {
      return entity.method_19538();
   }

   public static class_243 getVelocity(class_1657 entity) {
      return entity.method_18798();
   }

   public static class_238 getBoundingBox(class_1657 entity) {
      return entity.method_5829();
   }

   public static class_238 getBoundingBox(class_1297 entity) {
      return entity.method_5829();
   }

   public static class_1656 getAbilities(class_1657 entity) {
      return entity.method_31549();
   }

   public static boolean isCreative(class_1657 entity) {
      return entity.method_31549().field_7477;
   }

   public static float getFlySpeed(class_1657 entity) {
      return entity.method_31549().method_7252();
   }

   public static float getWalkSpeed(class_1657 entity) {
      return entity.method_31549().method_7253();
   }

   public static void setWalkSpeed(class_1657 entity, float speed) {
      entity.method_31549().method_7250(speed);
   }

   public static void setFlySpeed(class_1657 entity, float speed) {
      entity.method_31549().method_7248(speed);
   }

   public static class_1937 getWorld(class_1657 entity) {
      return entity.method_37908();
   }

   public static int deathTime(class_1657 entity) {
      return entity.field_6213;
   }

   public static int getFoodLevel(class_1657 entity) {
      return entity.method_7344().method_7586();
   }

   public static float getSaturationLevel(class_1657 entity) {
      return entity.method_7344().method_7589();
   }

   public static float getExhaustionLevel(class_1657 entity) {
      return entity.method_7344().method_35219();
   }

   public static class_1263 getInventory(class_1657 entity) {
      return entity.method_31548();
   }

   public static class_1799 getStack(class_1657 entity, int slot) {
      return entity.method_31548().method_5438(slot);
   }

   public static int getMainSlot(class_1657 entity) {
      return entity.method_31548().field_7545;
   }

   public static int getOffhandSlot(class_1657 entity) {
      return 45;
   }

   public static int getEmptySlot(class_1657 entity) {
      return entity.method_31548().method_7376();
   }

   public static boolean isEmptyInventory(class_1657 entity) {
      return entity.method_31548().method_5442();
   }

   public static boolean isBlastResistant(class_2338 pos, BlastResistantType type) {
      class_2248 block = MeteorClient.mc.field_1687.method_8320(pos).method_26204();
      switch (type) {
         case Any:
         case Mineable:
            return block == class_2246.field_10540 || block == class_2246.field_22423 || block instanceof class_2199 || block == class_2246.field_22108 || block == class_2246.field_10443 || block == class_2246.field_23152 || block == class_2246.field_22109 || block == class_2246.field_10485 || block == class_2246.field_9987 && type == EntityInfo.BlastResistantType.Any || block == class_2246.field_10398 && type == EntityInfo.BlastResistantType.Any;
         case Unbreakable:
            return block == class_2246.field_9987 || block == class_2246.field_10398;
         case NotAir:
            return block != class_2246.field_10124;
         default:
            return false;
      }
   }

   public static boolean notNull(class_1657 entity) {
      return entity != null;
   }

   public static String getName(class_1657 entity) {
      return entity.method_7334().getName();
   }

   public static double X(class_1657 entity) {
      return entity.method_23317();
   }

   public static double Y(class_1657 entity) {
      return entity.method_23318();
   }

   public static double Z(class_1657 entity) {
      return entity.method_23321();
   }

   public static Iterable getEntities() {
      return MeteorClient.mc.field_1687.method_18112();
   }

   public static List getPlayers() {
      return MeteorClient.mc.field_1687.method_18456();
   }

   public static boolean isSurrounded(class_1309 entity) {
      return BlockInfo.isBlastResist(entity.method_24515().method_10072()) && BlockInfo.isBlastResist(entity.method_24515().method_10067()) && BlockInfo.isBlastResist(entity.method_24515().method_10078()) && BlockInfo.isBlastResist(entity.method_24515().method_10095()) && BlockInfo.isBlastResist(entity.method_24515().method_10074());
   }

   public static boolean isTrapped(class_1309 entity) {
      return BlockInfo.isBlastResist(entity.method_24515().method_10072().method_10084()) && BlockInfo.isBlastResist(entity.method_24515().method_10067().method_10084()) && BlockInfo.isBlastResist(entity.method_24515().method_10078().method_10084()) && BlockInfo.isBlastResist(entity.method_24515().method_10095().method_10084()) && BlockInfo.isBlastResist(entity.method_24515().method_10086(2));
   }

   public static boolean isFaceTrapped(class_1309 entity) {
      return BlockInfo.isBlastResist(entity.method_24515().method_10072().method_10084()) && BlockInfo.isBlastResist(entity.method_24515().method_10067().method_10084()) && BlockInfo.isBlastResist(entity.method_24515().method_10078().method_10084()) && BlockInfo.isBlastResist(entity.method_24515().method_10095().method_10084());
   }

   public static boolean isInHole(class_1657 p) {
      class_2338 pos = p.method_24515();
      return !MeteorClient.mc.field_1687.method_8320(pos.method_10069(1, 0, 0)).method_26215() && !MeteorClient.mc.field_1687.method_8320(pos.method_10069(-1, 0, 0)).method_26215() && !MeteorClient.mc.field_1687.method_8320(pos.method_10069(0, 0, 1)).method_26215() && !MeteorClient.mc.field_1687.method_8320(pos.method_10069(0, 0, -1)).method_26215() && !MeteorClient.mc.field_1687.method_8320(pos.method_10069(0, -1, 0)).method_26215();
   }

   public static boolean isInLiquid() {
      if (MeteorClient.mc.field_1724.field_6017 >= 3.0F) {
         return false;
      } else {
         boolean inLiquid = false;
         class_238 bb = MeteorClient.mc.field_1724.method_5829();
         int y = (int)bb.field_1322;

         for(int x = class_3532.method_15357(bb.field_1323); x < class_3532.method_15357(bb.field_1320) + 1; ++x) {
            for(int z = class_3532.method_15357(bb.field_1321); z < class_3532.method_15357(bb.field_1324) + 1; ++z) {
               class_2248 block = MeteorClient.mc.field_1687.method_8320(new class_2338(x, y, z)).method_26204();
               if (!(block instanceof class_2189)) {
                  if (block != class_2246.field_10382) {
                     return false;
                  }

                  inLiquid = true;
               }
            }
         }

         return inLiquid;
      }
   }

   public static class_2338 playerPos(class_1657 targetEntity) {
      return BlockInfo.roundBlockPos(targetEntity.method_19538());
   }

   public static boolean isInHole(class_1657 targetEntity, boolean doubles, BlastResistantType type) {
      if (!Utils.canUpdate()) {
         return false;
      } else {
         class_2338 blockPos = playerPos(targetEntity);
         int air = 0;
         class_2350[] var5 = class_2350.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            class_2350 direction = var5[var7];
            if (direction != class_2350.field_11036 && !isBlastResistant(blockPos.method_10093(direction), type)) {
               if (!doubles || direction == class_2350.field_11033) {
                  return false;
               }

               ++air;
               class_2350[] var9 = class_2350.values();
               int var10 = var9.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  class_2350 dir = var9[var11];
                  if (dir != direction.method_10153() && dir != class_2350.field_11036 && !isBlastResistant(blockPos.method_10093(direction).method_10093(dir), type)) {
                     return false;
                  }
               }
            }
         }

         return air < 2;
      }
   }

   public static List getBlocksAround(class_1657 player) {
      List positions = new ArrayList();
      Iterator var3 = BlockInfo.getSphere(player.method_24515(), 3, 1).iterator();

      while(true) {
         List getEntityBoxes;
         class_2338 blockPos;
         do {
            if (!var3.hasNext()) {
               return positions;
            }

            blockPos = (class_2338)var3.next();
            getEntityBoxes = MeteorClient.mc.field_1687.method_8333((class_1297)null, new class_238(blockPos), (entity) -> {
               return entity == player;
            });
         } while(!getEntityBoxes.isEmpty());

         class_2350[] var5 = class_2350.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            class_2350 direction = var5[var7];
            if (direction != class_2350.field_11036 && direction != class_2350.field_11033) {
               getEntityBoxes = MeteorClient.mc.field_1687.method_8333((class_1297)null, new class_238(blockPos.method_10093(direction)), (entity) -> {
                  return entity == player;
               });
               if (!getEntityBoxes.isEmpty()) {
                  positions.add(new class_2338(blockPos));
               }
            }
         }
      }
   }

   public static class_2350 rayTraceCheck(class_2338 pos, boolean forceReturn) {
      class_243 eyesPos = new class_243(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318() + (double)MeteorClient.mc.field_1724.method_18381(MeteorClient.mc.field_1724.method_18376()), MeteorClient.mc.field_1724.method_23321());
      class_2350[] var3 = class_2350.values();
      class_2350[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         class_2350 direction = var4[var6];
         class_3959 raycastContext = new class_3959(eyesPos, new class_243((double)pos.method_10263() + 0.5 + (double)direction.method_10163().method_10263() * 0.5, (double)pos.method_10264() + 0.5 + (double)direction.method_10163().method_10264() * 0.5, (double)pos.method_10260() + 0.5 + (double)direction.method_10163().method_10260() * 0.5), class_3960.field_17558, class_242.field_1348, MeteorClient.mc.field_1724);
         class_3965 result = MeteorClient.mc.field_1687.method_17742(raycastContext);
         if (result != null && result.method_17783() == class_240.field_1332 && result.method_17777().equals(pos)) {
            return direction;
         }
      }

      if (forceReturn) {
         if ((double)pos.method_10264() > eyesPos.field_1351) {
            return class_2350.field_11033;
         } else {
            return class_2350.field_11036;
         }
      } else {
         return null;
      }
   }

   public static boolean isDoubleSurrounded(class_1309 entity) {
      class_2338 blockPos = entity.method_24515();
      return BlockInfo.isBlastResist(blockPos.method_10069(1, 0, 0)) && BlockInfo.isBlastResist(blockPos.method_10069(-1, 0, 0)) && BlockInfo.isBlastResist(blockPos.method_10069(0, 0, 1)) && BlockInfo.isBlastResist(blockPos.method_10069(0, 0, -1)) && BlockInfo.isBlastResist(blockPos.method_10069(1, 1, 0)) && BlockInfo.isBlastResist(blockPos.method_10069(-1, 1, 0)) && BlockInfo.isBlastResist(blockPos.method_10069(0, 1, 1)) && BlockInfo.isBlastResist(blockPos.method_10069(0, 1, -1));
   }

   public static enum BlastResistantType {
      Any,
      Unbreakable,
      Mineable,
      NotAir;

      // $FF: synthetic method
      private static BlastResistantType[] $values() {
         return new BlastResistantType[]{Any, Unbreakable, Mineable, NotAir};
      }
   }
}
