package dev.lemonclient.addon.modules.info;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import java.util.Iterator;
import java.util.Random;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;

public class AnteroTaateli extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting iFriends;
   private final Setting delay;
   private double timer;
   private final Random r;
   private int lastIndex;
   private final String[] messages;

   public AnteroTaateli() {
      super(LemonClient.Info, "Auto Andrew Tate", "What colour is your bugatti?");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.iFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore Friends")).description("Doesn't send messages targeted to friends.")).defaultValue(true)).build());
      this.delay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Delay")).description("Tick delay between messages.")).defaultValue(50.0).min(0.0).sliderRange(0.0, 100.0).build());
      this.timer = 0.0;
      this.r = new Random();
      this.lastIndex = 0;
      this.messages = new String[]{"Hey brokies top G here.", "Top G drinks sparkling water and breathes air.", "I hate dead people all you do is fucking laying down like pussies.", "Get up and do some push-ups.", "Top G is never late time is just running ahead of schedule.", "<NAME>, what color is your Bugatti?", "Hello i am Andrew Tate and you are a brokie.", "Instead of playing a block game how bout you pick up some women.", "We are living inside of The Matrix, and I’m Morpheus.", "The Matrix has attacked me.", "Fucking vape! Vape comes out of the motherfucker. Fucking vape!", "You don't need vape breathe air!", "Are you good enough on your worst day to defeat your opponents on their best day?", "Being poor, weak and broke is your fault. The only person who can make you rich and strong is you. Build yourself.", "The biggest difference between success and failure is getting started.", "There was a guy who looked at me obviously trying to hurt my dignity so i pulled out my RPG and obliterated that fucker", "Being rich is even better than you imagine it to be.", "Your a fucking brokie!"};
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ++this.timer;
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         class_1657 bugatti = this.getClosest();
         if (this.timer >= (Double)this.delay.get() && bugatti != null) {
            this.timer = 0.0;
            ChatUtils.sendPlayerMsg(this.getMessage(bugatti));
         }
      }

   }

   private String getMessage(class_1657 pl) {
      int index = this.r.nextInt(0, this.messages.length);
      String msg = this.messages[index];
      if (index == this.lastIndex) {
         if (index >= this.messages.length - 1) {
            index = 0;
         } else {
            ++index;
         }
      }

      this.lastIndex = index;
      return msg.replace("<NAME>", pl.method_5477().getString());
   }

   private class_1657 getClosest() {
      class_1657 closest = null;
      float distance = -1.0F;
      if (!this.mc.field_1687.method_18456().isEmpty()) {
         Iterator var3 = this.mc.field_1687.method_18456().iterator();

         while(true) {
            class_1657 player;
            do {
               do {
                  do {
                     if (!var3.hasNext()) {
                        return closest;
                     }

                     player = (class_1657)var3.next();
                  } while(player == this.mc.field_1724);
               } while((Boolean)this.iFriends.get() && Friends.get().isFriend(player));
            } while(closest != null && !(this.mc.field_1724.method_19538().method_1022(player.method_19538()) < (double)distance));

            closest = player;

            assert this.mc.field_1724 != null;

            distance = (float)this.mc.field_1724.method_19538().method_1022(player.method_19538());
         }
      } else {
         return closest;
      }
   }
}
