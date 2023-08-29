package dev.lemonclient.addon;

import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.enums.SwingState;
import dev.lemonclient.addon.enums.SwingType;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.misc.SwingAnimation;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.timers.TimerUtils;
import dev.lemonclient.addon.utils.world.BlockInfo;
import java.util.Iterator;
import java.util.Objects;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_2885;
import net.minecraft.class_2886;
import net.minecraft.class_3965;
import net.minecraft.class_7202;
import net.minecraft.class_7204;

public class LemonModule extends Module {
   private final String prefix;
   public int priority;

   public LemonModule(Category category, String name, String description) {
      super(category, name, description);
      this.prefix = class_124.field_1080 + "[" + class_124.field_1061 + "LemonClient" + class_124.field_1080 + "]";
      this.priority = Prioritys.get(this);
   }

   public void sendToggledMsg() {
      if ((Boolean)Config.get().chatFeedback.get() && this.chatFeedback && this.mc.field_1687 != null) {
         ChatUtils.forceNextPrefixClass(this.getClass());
         String var10000 = this.prefix;
         String msg = var10000 + " " + class_124.field_1080 + "[" + class_124.field_1076 + this.name + class_124.field_1080 + "]" + class_124.field_1068 + " toggled" + (this.isActive() ? class_124.field_1060 + " ON" : class_124.field_1061 + " OFF");
         this.sendMessage(class_2561.method_30163(msg), this.hashCode());
      }

   }

   public void sendToggledMsg(String message) {
      if ((Boolean)Config.get().chatFeedback.get() && this.chatFeedback && this.mc.field_1687 != null) {
         ChatUtils.forceNextPrefixClass(this.getClass());
         String var10000 = this.prefix;
         String msg = var10000 + " " + class_124.field_1080 + "[" + class_124.field_1076 + this.name + class_124.field_1080 + "]" + class_124.field_1068 + " toggled" + (this.isActive() ? class_124.field_1060 + " ON " : class_124.field_1061 + " OFF ") + class_124.field_1080 + message;
         this.sendMessage(class_2561.method_30163(msg), this.hashCode());
      }

   }

   public void sendDisableMsg(String text) {
      if (this.mc.field_1687 != null) {
         ChatUtils.forceNextPrefixClass(this.getClass());
         String msg = this.prefix + " " + class_124.field_1080 + "[" + class_124.field_1076 + this.name + class_124.field_1080 + "]" + class_124.field_1068 + " toggled" + class_124.field_1061 + " OFF " + class_124.field_1080 + text;
         this.sendMessage(class_2561.method_30163(msg), this.hashCode());
      }

   }

   public void sendNotificationsInfo(String text) {
      if (this.mc.field_1687 != null) {
         ChatUtils.forceNextPrefixClass(this.getClass());
         String msg = this.prefix + " " + class_124.field_1080 + "[" + class_124.field_1076 + this.name + class_124.field_1080 + "] " + text;
         this.sendMessage(class_2561.method_30163(msg), Objects.hash(new Object[]{this.name + "-info"}));
      }

   }

   public void debug(String text) {
      if (this.mc.field_1687 != null) {
         ChatUtils.forceNextPrefixClass(this.getClass());
         String msg = this.prefix + " " + class_124.field_1080 + "[" + class_124.field_1076 + this.name + class_124.field_1080 + "] " + class_124.field_1075 + text;
         this.sendMessage(class_2561.method_30163(msg), 0);
      }

   }

   public void sendMessage(class_2561 text, int id) {
      ((IChatHud)this.mc.field_1705.method_1743()).meteor$add(text, id);
   }

   public void sendPacket(class_2596 packet) {
      if (this.mc.method_1562() != null) {
         this.mc.method_1562().method_2883(packet);
      }
   }

   public void sendSequenced(class_7204 packetCreator) {
      if (this.mc.field_1761 != null && this.mc.field_1687 != null && this.mc.method_1562() != null) {
         class_7202 sequence = this.mc.field_1687.method_41925().method_41937();
         class_2596 packet = packetCreator.predict(sequence.method_41942());
         this.mc.method_1562().method_2883(packet);
         sequence.close();
      }
   }

   public void placeBlock(class_1268 hand, class_243 blockHitVec, class_2350 blockDirection, class_2338 pos) {
      class_243 eyes = this.mc.field_1724.method_33571();
      boolean inside = eyes.field_1352 > (double)pos.method_10263() && eyes.field_1352 < (double)(pos.method_10263() + 1) && eyes.field_1351 > (double)pos.method_10264() && eyes.field_1351 < (double)(pos.method_10264() + 1) && eyes.field_1350 > (double)pos.method_10260() && eyes.field_1350 < (double)(pos.method_10260() + 1);
      SettingUtils.swing(SwingState.Pre, SwingType.Placing, hand);
      this.sendSequenced((s) -> {
         return new class_2885(hand, new class_3965(blockHitVec, blockDirection, pos, inside), s);
      });
      SettingUtils.swing(SwingState.Post, SwingType.Placing, hand);
   }

   public void placeBlockAndAttackCrystal(class_2338 pos, class_2350 dir, boolean pauseEat, boolean attackSwing, SwingHand hand, boolean breakCrystal, long breakDelay, double safeHealth) {
      if (BlockInfo.canPlace(pos, breakCrystal, safeHealth)) {
         if (breakCrystal) {
            this.attackNearbyCrystal(pos, pauseEat, attackSwing, hand, breakDelay);
         }

         if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
            Managers.ROTATION.start(pos, (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
         }

         this.placeBlock(class_1268.field_5808, pos.method_46558(), dir, pos);
      }

   }

   public void interactBlock(class_1268 hand, class_243 blockHitVec, class_2350 blockDirection, class_2338 pos) {
      class_243 eyes = this.mc.field_1724.method_33571();
      boolean inside = eyes.field_1352 > (double)pos.method_10263() && eyes.field_1352 < (double)(pos.method_10263() + 1) && eyes.field_1351 > (double)pos.method_10264() && eyes.field_1351 < (double)(pos.method_10264() + 1) && eyes.field_1350 > (double)pos.method_10260() && eyes.field_1350 < (double)(pos.method_10260() + 1);
      SettingUtils.swing(SwingState.Pre, SwingType.Interact, hand);
      this.sendSequenced((s) -> {
         return new class_2885(hand, new class_3965(blockHitVec, blockDirection, pos, inside), s);
      });
      SettingUtils.swing(SwingState.Post, SwingType.Interact, hand);
   }

   public void attackNearbyCrystal(class_2338 pos, boolean eatingPause, boolean swing, SwingHand hand, long breakDelay) {
      TimerUtils timer = new TimerUtils();
      if (timer.passedMs(breakDelay)) {
         if (!eatingPause || !this.mc.field_1724.method_6115()) {
            Iterator var8 = this.mc.field_1687.method_8333((class_1297)null, new class_238(pos), (entityx) -> {
               return entityx == this.mc.field_1724;
            }).iterator();

            while(var8.hasNext()) {
               class_1297 entity = (class_1297)var8.next();
               if (entity instanceof class_1511) {
                  timer.reset();
                  this.sendPacket(class_2824.method_34206(entity, this.mc.field_1724.method_5715()));
                  if (swing) {
                     this.clientSwing(hand, class_1268.field_5808);
                  }

                  if (SettingUtils.shouldRotate(RotationType.Attacking)) {
                     Managers.ROTATION.start(entity.method_5829(), (double)this.priority, RotationType.Attacking, (long)Objects.hash(new Object[]{this.name + "attacking"}));
                  }
                  break;
               }
            }

         }
      }
   }

   public void useItem(class_1268 hand) {
      SettingUtils.swing(SwingState.Pre, SwingType.Using, hand);
      this.sendSequenced((s) -> {
         return new class_2886(hand, s);
      });
      SettingUtils.swing(SwingState.Post, SwingType.Using, hand);
   }

   public void clientSwing(SwingHand swingHand, class_1268 realHand) {
      class_1268 var10000;
      switch (swingHand) {
         case MainHand:
            var10000 = class_1268.field_5808;
            break;
         case OffHand:
            var10000 = class_1268.field_5810;
            break;
         case RealHand:
            var10000 = realHand;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      class_1268 hand = var10000;
      this.mc.field_1724.method_23667(hand, true);
      ((SwingAnimation)Modules.get().get(SwingAnimation.class)).startSwing(hand);
   }
}
