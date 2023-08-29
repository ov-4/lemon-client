package dev.lemonclient.addon.utils.player;

import dev.lemonclient.addon.modules.combat.BedBombV2;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.network.PacketUtils;
import dev.lemonclient.addon.utils.others.Task;
import dev.lemonclient.addon.utils.world.BlockInfo;
import dev.lemonclient.addon.utils.world.PredictionUtils;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixininterface.IExplosion;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1280;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_1922;
import net.minecraft.class_1927;
import net.minecraft.class_2244;
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
import org.joml.Vector3d;

public class BedUtils {
   private static class_1927 explosion;
   private static final class_243 vec3d = new class_243(0.0, 0.0, 0.0);
   private static class_3959 raycastContext;

   @PreInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(BedUtils.class);
      MeteorClient.EVENT_BUS.subscribe(CrystalUtils.class);
   }

   @EventHandler
   private static void onGameJoined(GameJoinedEvent event) {
      explosion = new class_1927(MeteorClient.mc.field_1687, (class_1297)null, 0.0, 0.0, 0.0, 6.0F, false, class_4179.field_18687);
      raycastContext = new class_3959((class_243)null, (class_243)null, class_3960.field_17558, class_242.field_1347, MeteorClient.mc.field_1724);
   }

   public static boolean canBed(class_2338 canPlace, class_2338 replace) {
      return BlockInfo.getBlock(canPlace) instanceof class_2244 && BlockInfo.getBlock(replace) instanceof class_2244 || BlockUtils.canPlace(canPlace) && MeteorClient.mc.field_1687.method_8320(replace).method_45474();
   }

   public static void packetMine(class_2338 blockpos, boolean autoSwap, Task task) {
      task.run(() -> {
         FindItemResult best = InvUtils.findFastestTool(MeteorClient.mc.field_1687.method_8320(blockpos));
         if (best.found()) {
            if (autoSwap) {
               InvUtils.swap(best.slot(), false);
            }

            PacketUtils.startPacketMine(blockpos, true);
         }
      });
   }

   public static void normalMine(class_2338 blockpos, boolean autoSwap) {
      FindItemResult best = InvUtils.findFastestTool(MeteorClient.mc.field_1687.method_8320(blockpos));
      if (best.found()) {
         if (autoSwap) {
            InvUtils.swap(best.slot(), false);
         }

         BlockUtils.breakBlock(blockpos, false);
      }
   }

   public static ArrayList getTargetSphere(class_1657 target, int xRadius, int yRadius) {
      ArrayList al = new ArrayList();
      class_2338 tPos = EntityInfo.getBlockPos(target);
      class_2338.class_2339 p = new class_2338.class_2339();

      for(int x = -xRadius; x <= xRadius; ++x) {
         for(int y = -yRadius; y <= yRadius; ++y) {
            for(int z = -xRadius; z <= xRadius; ++z) {
               p.method_10101(tPos).method_10100(x, y, z);
               if (class_3532.method_15355((float)((tPos.method_10263() - p.method_10062().method_10263()) * (tPos.method_10263() - p.method_10062().method_10263()) + (tPos.method_10260() - p.method_10062().method_10260()) * (tPos.method_10260() - p.method_10062().method_10260()))) <= (float)xRadius && class_3532.method_15355((float)((tPos.method_10264() - p.method_10062().method_10264()) * (tPos.method_10264() - p.method_10062().method_10264()))) <= (float)yRadius && !al.contains(p.method_10062())) {
                  al.add(p.method_10062());
               }
            }
         }
      }

      return al;
   }

   public static class_2338 getTrapBlock(class_1657 target, double distance) {
      if (target == null) {
         return null;
      } else {
         class_2350[] var3 = class_2350.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            class_2350 direction = var3[var5];
            if (direction != class_2350.field_11036 && direction != class_2350.field_11033) {
               class_2338 pos = EntityInfo.getBlockPos(target).method_10084().method_10093(direction);
               if (BlockInfo.isCombatBlock(pos) && BlockInfo.isWithinRange(pos, distance)) {
                  return pos;
               }
            }
         }

         return null;
      }
   }

   public static boolean shouldBurrowBreak() {
      class_2338 b = EntityInfo.getBlockPos(BedBombV2.target);
      return BlockInfo.isCombatBlock(b) && MeteorClient.mc.field_1724.method_19538().method_1022(BlockInfo.getCenterVec3d(b)) < 4.5;
   }

   public static boolean shouldTrapBreak() {
      return EntityInfo.isSurrounded(BedBombV2.target) && EntityInfo.isTrapped(BedBombV2.target) && getTrapBlock(BedBombV2.target, 4.5) != null;
   }

   public static boolean shouldStringBreak() {
      List strings = new ArrayList();
      CardinalDirection[] var1 = CardinalDirection.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         CardinalDirection d = var1[var3];
         class_2338 cPos = EntityInfo.getBlockPos(BedBombV2.target).method_10084();
         if (BlockInfo.getBlock(cPos).method_8389().equals(class_1802.field_8276) && MeteorClient.mc.field_1724.method_19538().method_1022(BlockInfo.getCenterVec3d(cPos)) < 4.5) {
            strings.add(cPos);
         }

         if (BlockInfo.getBlock(cPos.method_10093(d.toDirection())).method_8389().equals(class_1802.field_8276) && MeteorClient.mc.field_1724.method_19538().method_1022(BlockInfo.getCenterVec3d(cPos.method_10093(d.toDirection()))) < 4.5) {
            strings.add(cPos.method_10093(d.toDirection()));
         }
      }

      return !strings.isEmpty() && !shouldTrapBreak();
   }

   public static double getDamage(class_1657 player, class_243 cVec, boolean predictMovement, boolean collision, int i, boolean ignoreTerrain) {
      if (player != null && EntityInfo.isCreative(player)) {
         return 0.0;
      } else {
         class_243 pVec = PredictionUtils.returnPredictVec(player, collision, i);
         ((IVec3d)vec3d).set(player.method_19538().field_1352, player.method_19538().field_1351, player.method_19538().field_1350);
         if (predictMovement) {
            ((IVec3d)vec3d).set(pVec.method_10216(), pVec.method_10214(), pVec.method_10215());
         }

         double modDistance = Math.sqrt(vec3d.method_1025(cVec));
         if (modDistance > 10.0) {
            return 0.0;
         } else {
            double exposure = getExposure(cVec, player, predictMovement, collision, i, raycastContext, ignoreTerrain);
            double impact = (1.0 - modDistance / 10.0) * exposure;
            double damage = (impact * impact + impact) / 2.0 * 7.0 * 10.0 + 1.0;
            damage = getDamageForDifficulty(damage);
            damage = resistanceReduction(player, damage);
            damage = (double)class_1280.method_5496((float)damage, (float)player.method_6096(), (float)player.method_5996(class_5134.field_23725).method_6194());
            ((IExplosion)explosion).set(cVec, 5.0F, true);
            damage = blastProtReduction(player, damage, explosion);
            if (damage < 0.0) {
               damage = 0.0;
            }

            return damage;
         }
      }
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

   private static double resistanceReduction(class_1309 player, double damage) {
      if (player.method_6059(class_1294.field_5907)) {
         int lvl = player.method_6112(class_1294.field_5907).method_5578() + 1;
         damage *= 1.0 - (double)lvl * 0.2;
      }

      return damage < 0.0 ? 0.0 : damage;
   }

   private static double blastProtReduction(class_1297 player, double damage, class_1927 explosion) {
      int protLevel = class_1890.method_8219(player.method_5661(), MeteorClient.mc.field_1687.method_48963().method_48807(explosion));
      if (protLevel > 20) {
         protLevel = 20;
      }

      damage *= 1.0 - (double)protLevel / 25.0;
      return damage < 0.0 ? 0.0 : damage;
   }

   private static double getExposure(class_243 source, class_1297 entity, boolean predictMovement, boolean collision, int ii, class_3959 raycastContext, boolean ignoreTerrain) {
      class_238 box = EntityInfo.getBoundingBox((class_1657)entity);
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
                  if (raycast(raycastContext, ignoreTerrain).method_17783() == class_240.field_1333) {
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

   private static class_3965 raycast(class_3959 context, boolean ignoreTerrain) {
      return (class_3965)class_1922.method_17744(context.method_17750(), context.method_17747(), context, (raycastContext, blockpos) -> {
         class_2680 blockState = MeteorClient.mc.field_1687.method_8320(blockpos);
         if (!BlockInfo.isBlastResist(blockpos) && ignoreTerrain) {
            blockState = class_2246.field_10124.method_9564();
         }

         class_243 vec3d = raycastContext.method_17750();
         class_243 vec3d2 = raycastContext.method_17747();
         class_265 voxelShape = raycastContext.method_17748(blockState, MeteorClient.mc.field_1687, blockpos);
         class_3965 blockHitResult = MeteorClient.mc.field_1687.method_17745(vec3d, vec3d2, blockpos, voxelShape, blockState);
         class_265 voxelShape2 = class_259.method_1073();
         class_3965 blockHitResult2 = voxelShape2.method_1092(vec3d, vec3d2, blockpos);
         double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.method_17750().method_1025(blockHitResult.method_17784());
         double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.method_17750().method_1025(blockHitResult2.method_17784());
         return d <= e ? blockHitResult : blockHitResult2;
      }, (raycastContext) -> {
         class_243 vec3d = raycastContext.method_17750().method_1020(raycastContext.method_17747());
         return class_3965.method_17778(raycastContext.method_17747(), class_2350.method_10142(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350), class_2338.method_49638(raycastContext.method_17747()));
      });
   }

   public static List split(List list, int count) {
      List part = new ArrayList();

      int i;
      for(i = 0; i < count; ++i) {
         part.add(new ArrayList());
      }

      for(i = 0; i < list.size(); ++i) {
         ((List)part.get(i % count)).add(list.get(i));
      }

      return part;
   }

   public static class RenderText {
      public class_2338.class_2339 pos = new class_2338.class_2339();
      public String text;
      public int ticks;
      private final Vector3d vec3 = new Vector3d();

      public RenderText set(class_2338 pos, String text) {
         this.text = text;
         this.pos.method_10101(pos);
         this.ticks = 30;
         return this;
      }

      public void tick() {
         --this.ticks;
      }

      public void render(Render2DEvent event, Color textColor) {
         if (this.text != null) {
            int preTextA = textColor.a;
            textColor.a = (int)((double)textColor.a * ((double)this.ticks / 5.0));
            this.vec3.set((double)this.pos.method_10263() + 0.5, (double)this.pos.method_10264() + 0.2, (double)this.pos.method_10260() + 0.5);
            if (NametagUtils.to2D(this.vec3, 1.5)) {
               NametagUtils.begin(this.vec3);
               TextRenderer.get().begin(1.0, false, true);
               double w = TextRenderer.get().getWidth(this.text) / 2.0;
               TextRenderer.get().render(this.text, -w, 0.0, textColor, true);
               TextRenderer.get().end();
               NametagUtils.end();
            }

            textColor.a = preTextA;
         }

      }
   }

   public static class RenderBreak {
      public class_2338.class_2339 pos = new class_2338.class_2339();
      public int ticks;

      public RenderBreak set(class_2338 blockPos) {
         this.pos.method_10101(blockPos);
         this.ticks = 50;
         return this;
      }

      public void tick() {
         --this.ticks;
      }

      public void render(Render3DEvent event, Color sides, Color lines, ShapeMode shapeMode) {
         if (this.pos != null) {
            int preSideA = sides.a;
            int preLineA = lines.a;
            sides.a = (int)((double)sides.a + ((double)this.ticks - 1.0));
            lines.a = (int)((double)lines.a + ((double)this.ticks - 1.0));
            event.renderer.box(this.pos, sides, lines, shapeMode, 0);
            sides.a = preSideA;
            lines.a = preLineA;
         }

      }
   }

   public static class RenderBlock {
      public class_2338.class_2339 pos = new class_2338.class_2339();
      public CardinalDirection renderDir;
      public int ticks;

      public RenderBlock set(class_2338 blockPos, CardinalDirection dir) {
         this.renderDir = dir;
         this.pos.method_10101(blockPos);
         this.ticks = 10;
         return this;
      }

      public void tick() {
         --this.ticks;
      }

      public void render(Render3DEvent event, Color sides, Color lines, ShapeMode shapeMode) {
         if (this.renderDir != null) {
            int x = this.pos.method_10263();
            int y = this.pos.method_10264();
            int z = this.pos.method_10260();
            int preSideA = sides.a;
            int preLineA = lines.a;
            sides.a = (int)((double)sides.a * ((double)this.ticks / 5.0));
            lines.a = (int)((double)lines.a * ((double)this.ticks / 5.0));
            switch (this.renderDir) {
               case South:
                  event.renderer.box((double)x, (double)y, (double)z, (double)(x + 1), (double)y + 0.56, (double)(z + 2), sides, lines, shapeMode, 0);
                  break;
               case North:
                  event.renderer.box((double)x, (double)y, (double)(z - 1), (double)(x + 1), (double)y + 0.56, (double)(z + 1), sides, lines, shapeMode, 0);
                  break;
               case West:
                  event.renderer.box((double)(x - 1), (double)y, (double)z, (double)(x + 1), (double)y + 0.56, (double)(z + 1), sides, lines, shapeMode, 0);
                  break;
               case East:
                  event.renderer.box((double)x, (double)y, (double)z, (double)(x + 2), (double)y + 0.56, (double)(z + 1), sides, lines, shapeMode, 0);
            }

            sides.a = preSideA;
            lines.a = preLineA;
         }

      }
   }
}
