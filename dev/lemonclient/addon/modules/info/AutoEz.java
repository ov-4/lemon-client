package dev.lemonclient.addon.modules.info;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.Wrapper;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.player.DeathUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2596;
import net.minecraft.class_2663;

public class AutoEz extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting killMessages;
   private final Setting resetKillCount;
   private final Setting messageOnPop;
   private final Setting popMessages;
   private final Setting skipMessage;
   private final Object2IntMap totemPopMap;
   private int kills;
   private int pops;
   private int skips;

   public AutoEz() {
      super(LemonClient.Info, "auto-ez", "Sends message in chat if you kill someone");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.killMessages = this.sgGeneral.add(((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("kill-messages")).description("Randomly takes the message from the list and sends on each kill.")).defaultValue(new String[]{"with ease | {kills} ks", "cry more kiddo | {kills} ks", "{target} has been put to sleep by LemonClient | {kills} ks", "nice fireworks | {kills} ks", "packed :smoke: | {kills} ks", "LemonClient owning yet again | {kills} ks", "coping much? | {kills} ks", "ez | {kills} ks", "back to spawn you go! | {kills} ks", "cope cope seethe cope!1!!1!11 | {kills} ks", "smoking fags with LemonClient | {kills} ks", "debil | {kills} ks", "curb stomping kids with LemonClient | {kills} ks"}).build());
      this.resetKillCount = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("reset-killcount")).description("Resets killcount on death.")).defaultValue(false)).build());
      this.messageOnPop = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("message-on-pop")).description("Sends message in chat when target is popping totem.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      StringListSetting.Builder var10002 = ((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("pop-messages")).description("Randomly takes the message from the list and sends on target pop.")).defaultValue(new String[]{"popped by the best meteor addon lemonclient!", "{target} popped by powerful lemonclient", "{target} needs a new totem", "owning {target}", "{target} you should buy lemonclient and stop popping totems.", "{target} popped {pops}, thanks to LemonClient!"});
      Setting var10003 = this.messageOnPop;
      Objects.requireNonNull(var10003);
      this.popMessages = var10001.add(((StringListSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      IntSetting.Builder var1 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("skip-message")).description("Skips messages to prevent being kicked for spamming.")).defaultValue(4)).min(0).sliderMax(20);
      var10003 = this.messageOnPop;
      Objects.requireNonNull(var10003);
      this.skipMessage = var10001.add(((IntSetting.Builder)var1.visible(var10003::get)).build());
      this.totemPopMap = new Object2IntOpenHashMap();
   }

   public void onActivate() {
      this.totemPopMap.clear();
      this.kills = 0;
      this.pops = 0;
      this.skips = (Integer)this.skipMessage.get();
   }

   @EventHandler
   public void onTick(TickEvent.Post event) {
      if (((List)this.killMessages.get()).isEmpty()) {
         ((List)this.killMessages.get()).add("{target} owned by LemonClient");
      }

      if (this.mc.field_1724 != null && (Boolean)this.resetKillCount.get() && EntityInfo.isDead(this.mc.field_1724)) {
         this.kills = 0;
      }

   }

   public void onKill(class_1657 player) {
      if (this.isActive()) {
         ++this.kills;
         String var10000 = (String)Config.get().prefix.get();
         ChatUtils.sendPlayerMsg(var10000 + "say " + this.getMessage(player, AutoEz.MessageType.Kill));
      }

   }

   @EventHandler
   private void onPop(PacketEvent.Receive event) {
      if ((Boolean)this.messageOnPop.get()) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2663) {
            class_2663 p = (class_2663)var3;
            if (p.method_11470() == 35) {
               class_1297 entity = p.method_11469(this.mc.field_1687);
               if (entity instanceof class_1657) {
                  if (!entity.equals(this.mc.field_1724)) {
                     synchronized(this.totemPopMap) {
                        this.pops = this.totemPopMap.getOrDefault(entity.method_5667(), 0);
                        this.totemPopMap.put(entity.method_5667(), ++this.pops);
                        if (this.skips >= (Integer)this.skipMessage.get() && DeathUtils.getTargets().contains(EntityInfo.getName((class_1657)entity))) {
                           String var10000 = (String)Config.get().prefix.get();
                           ChatUtils.sendPlayerMsg(var10000 + "say " + this.getMessage((class_1657)entity, AutoEz.MessageType.Pop));
                           this.skips = 0;
                        } else {
                           ++this.skips;
                        }

                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onDeath(TickEvent.Post event) {
      synchronized(this.totemPopMap) {
         if (this.mc.field_1687 != null) {
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

               this.totemPopMap.removeInt(player.method_5667());
            }
         }
      }
   }

   @EventHandler
   public void onJoin(GameJoinedEvent event) {
      this.totemPopMap.clear();
      this.kills = 0;
      this.pops = 0;
      this.skips = (Integer)this.skipMessage.get();
   }

   public String getMessage(class_1657 player, MessageType messageType) {
      List messageList = null;
      switch (messageType) {
         case Kill:
            messageList = (List)this.killMessages.get();
            break;
         case Pop:
            messageList = (List)this.popMessages.get();
      }

      String text = (String)messageList.get(Wrapper.randomNum(0, messageList.size() - 1));
      text = text.replace("{target}", player.method_7334().getName());
      text = text.replace("{kills}", String.valueOf(this.kills));
      if (this.mc.field_1724 != null) {
         text = text.replace("{me}", this.mc.field_1724.method_7334().getName());
      }

      text = text.replace("{pops}", String.valueOf(this.pops));
      text = this.getGrammar(text);
      return messageList.isEmpty() ? "i ez'ed u with minimal effort" : text;
   }

   private String getGrammar(String text) {
      String finalText;
      if (this.pops == 1) {
         finalText = text.replace("totem's", "totem");
      } else {
         finalText = text.replace("totem", "totem's");
      }

      return finalText;
   }

   public static enum MessageType {
      Kill,
      Pop;

      // $FF: synthetic method
      private static MessageType[] $values() {
         return new MessageType[]{Kill, Pop};
      }
   }
}
