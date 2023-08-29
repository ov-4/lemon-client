package dev.lemonclient.addon.modules.info;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.hud.ToastNotifications;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.managers.impl.NotificationManager;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.others.Task;
import dev.lemonclient.addon.utils.world.BlockInfo;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2596;
import net.minecraft.class_2620;
import net.minecraft.class_2663;

public class Notifications extends LemonModule {
   private final SettingGroup sgArmour;
   private final SettingGroup sgPlayers;
   private final SettingGroup sgSurround;
   private final SettingGroup sgNone;
   private final Setting notifications;
   private final Setting armor;
   private final Setting percentage;
   private final Setting message;
   private final Setting totemNotif;
   private final Setting deaths;
   private final Setting surroundBreak;
   private class_2338 prevBreakPos;
   private final Task bootsTask;
   private final Task leggingsTask;
   private final Task chestplateTask;
   private final Task helmetTask;
   private final Object2IntMap totemPopMap;
   private final Object2IntMap chatIdMap;

   public Notifications() {
      super(LemonClient.Info, "Notifications", "Sends messages in hud about different events.");
      this.sgArmour = this.settings.createGroup("Breaks");
      this.sgPlayers = this.settings.createGroup("Players");
      this.sgSurround = this.settings.createGroup("Surround");
      this.sgNone = this.settings.createGroup("");
      this.notifications = this.sgNone.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("notifications")).defaultValue(Notifications.Mode.Toast)).build());
      this.armor = this.sgArmour.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("armor")).description("Sends notifications while armor is low.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgArmour;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("percentage")).description("Precentage of armor to trigger notifier.")).defaultValue(40)).sliderRange(1, 100);
      Setting var10003 = this.armor;
      Objects.requireNonNull(var10003);
      this.percentage = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.message = this.sgArmour.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("message")).description("Messages for armor notify.")).defaultValue("Your {armor}({value}%) is lower than {%}%!")).build());
      this.totemNotif = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("totem-pops")).description("Sends notification for totem pops.")).defaultValue(false)).build());
      this.deaths = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("deaths")).description("Sends notification for deaths.")).defaultValue(false)).build());
      this.surroundBreak = this.sgSurround.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("surround-break")).description("Notifies you while someone breaking your surround.")).defaultValue(false)).build());
      this.bootsTask = new Task();
      this.leggingsTask = new Task();
      this.chestplateTask = new Task();
      this.helmetTask = new Task();
      this.totemPopMap = new Object2IntOpenHashMap();
      this.chatIdMap = new Object2IntOpenHashMap();
   }

   public void onActivate() {
      this.totemPopMap.clear();
      this.chatIdMap.clear();
      this.bootsTask.reset();
      this.leggingsTask.reset();
      this.chestplateTask.reset();
      this.helmetTask.reset();
   }

   @EventHandler
   private void onGameJoin(GameJoinedEvent event) {
      this.totemPopMap.clear();
      this.chatIdMap.clear();
   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2663 p) {
         if (p.method_11470() == 35) {
            class_1297 entity = p.method_11469(this.mc.field_1687);
            if (entity instanceof class_1657) {
               if (!entity.equals(this.mc.field_1724)) {
                  synchronized(this.totemPopMap) {
                     int pops = this.totemPopMap.getOrDefault(entity.method_5667(), 0);
                     Object2IntMap var10000 = this.totemPopMap;
                     UUID var10001 = entity.method_5667();
                     ++pops;
                     var10000.put(var10001, pops);
                     if ((Boolean)this.totemNotif.get()) {
                        this.send(entity.method_5820() + " popped " + pops + " time`s!", this.notifications);
                     }

                     if (((Mode)this.notifications.get()).equals(Notifications.Mode.Toast)) {
                        NotificationManager var9 = Managers.NOTIFICATION;
                        String var10 = this.title;
                        String var10002 = entity.method_5820();
                        var9.info(var10, var10002 + " popped " + pops + " time`s!");
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      synchronized(this.totemPopMap) {
         Iterator var3 = this.mc.field_1687.method_18456().iterator();

         while(true) {
            class_1657 player;
            do {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  player = (class_1657)var3.next();
               } while(!this.totemPopMap.containsKey(player.method_5667()));
            } while(player.field_6213 <= 0 && !(player.method_6032() <= 0.0F));

            int pops = this.totemPopMap.getOrDefault(player.method_5667(), 0);
            String var10000 = player.method_5820();
            String xx = var10000 + " just died after " + pops + " pops!";
            if ((Boolean)this.deaths.get()) {
               this.send(xx, this.notifications);
            }

            if (((Mode)this.notifications.get()).equals(Notifications.Mode.Toast)) {
               Managers.NOTIFICATION.info(this.title, xx);
            }

            this.totemPopMap.removeInt(player.method_5667());
            this.chatIdMap.removeInt(player.method_5667());
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if ((Boolean)this.armor.get()) {
         class_1799 boots = this.mc.field_1724.method_31548().method_7372(0);
         class_1799 leggings = this.mc.field_1724.method_31548().method_7372(1);
         class_1799 chestplate = this.mc.field_1724.method_31548().method_7372(2);
         class_1799 helmet = this.mc.field_1724.method_31548().method_7372(3);
         if (!boots.method_7960() || !leggings.method_7960() || !chestplate.method_7960() || !helmet.method_7960()) {
            if (this.getPercentage(boots) < (Integer)this.percentage.get()) {
               this.bootsTask.run(() -> {
                  this.notifyArmor("Boots", this.getPercentage(boots));
               });
            } else {
               this.bootsTask.reset();
            }

            if (this.getPercentage(leggings) < (Integer)this.percentage.get()) {
               this.leggingsTask.run(() -> {
                  this.notifyArmor("Leggings", this.getPercentage(leggings));
               });
            } else {
               this.leggingsTask.reset();
            }

            if (this.getPercentage(chestplate) < (Integer)this.percentage.get()) {
               this.chestplateTask.run(() -> {
                  this.notifyArmor("Chestplate", this.getPercentage(chestplate));
               });
            } else {
               this.chestplateTask.reset();
            }

            if (this.getPercentage(helmet) < (Integer)this.percentage.get()) {
               this.helmetTask.run(() -> {
                  this.notifyArmor("Helmet", this.getPercentage(helmet));
               });
            } else {
               this.helmetTask.reset();
            }

         }
      }
   }

   @EventHandler
   public void onBreakPacket(PacketEvent.Receive event) {
      if ((Boolean)this.surroundBreak.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2620) {
            class_2620 bbpp = (class_2620)var3;
            class_2338 bbp = bbpp.method_11277();
            if (bbp.equals(this.prevBreakPos) && bbpp.method_11278() > 0) {
               return;
            }

            class_1657 breakingPlayer = (class_1657)this.mc.field_1687.method_8469(bbpp.method_11280());
            class_2338 playerBlockPos = EntityInfo.getBlockPos((class_1657)this.mc.field_1724);
            boolean validBlock = BlockInfo.isCombatBlock(bbp);

            assert breakingPlayer != null;

            if (breakingPlayer.equals(this.mc.field_1724)) {
               return;
            }

            CardinalDirection[] var7 = CardinalDirection.values();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               CardinalDirection direction = var7[var9];
               if (validBlock && bbp.equals(playerBlockPos.method_10093(direction.toDirection()))) {
                  this.notifySurroundBreak(breakingPlayer);
               }
            }

            this.prevBreakPos = bbp;
         }
      }

   }

   public void send(String msg, Setting notify) {
      switch ((Mode)notify.get()) {
         case Chat:
            this.info(msg, new Object[0]);
            break;
         case Toast:
            ToastNotifications.addToast(msg);
      }

   }

   private void notifyArmor(String armor, int percentage) {
      String msg = (String)this.message.get();
      msg = msg.replace("{armor}", armor);
      msg = msg.replace("{value}", String.valueOf(percentage));
      msg = msg.replace("{%}", String.valueOf(this.percentage.get()));
      if (percentage != 0) {
         this.send(msg, this.notifications);
      }

      switch ((Mode)this.notifications.get()) {
         case Chat:
            this.warning(msg, new Object[0]);
            break;
         case Notification:
            Managers.NOTIFICATION.warn(this.title, msg);
      }

   }

   private int getPercentage(class_1799 itemStack) {
      return Math.round((float)(itemStack.method_7936() - itemStack.method_7919()) * 100.0F / (float)itemStack.method_7936());
   }

   private void notifySurroundBreak(class_1657 player) {
      String xx = "Your surround is being broken by " + player.method_5820();
      switch ((Mode)this.notifications.get()) {
         case Chat:
            ChatUtils.warning(xx, new Object[0]);
            break;
         case Toast:
            this.send(xx, this.notifications);
            break;
         case Notification:
            Managers.NOTIFICATION.warn(this.title, xx);
      }

   }

   public static enum Mode {
      Toast,
      Notification,
      Chat;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Toast, Notification, Chat};
      }
   }
}
