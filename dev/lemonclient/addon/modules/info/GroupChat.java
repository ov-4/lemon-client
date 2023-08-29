package dev.lemonclient.addon.modules.info;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_640;

public class GroupChat extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting players;
   private final Setting command;

   public GroupChat() {
      super(LemonClient.Info, "Group Chat", "Talks with people in groups privately using /msg.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.players = this.sgGeneral.add(((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("players")).description("Determines which players to message.")).defaultValue(new String[]{"Fin_LemonKee", "dyzjct"}).build());
      this.command = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("command")).description("How the message command is set up on the server.")).defaultValue("/msg %player% %message%")).build());
   }

   @EventHandler
   private void onMessageSend(SendMessageEvent event) {
      Iterator var2 = ((List)this.players.get()).iterator();

      while(true) {
         while(var2.hasNext()) {
            String playerString = (String)var2.next();
            Iterator var4 = this.mc.method_1562().method_2880().iterator();

            while(var4.hasNext()) {
               class_640 onlinePlayer = (class_640)var4.next();
               if (onlinePlayer.method_2966().getName().equalsIgnoreCase(playerString)) {
                  ChatUtils.sendPlayerMsg(((String)this.command.get()).replace("%player%", onlinePlayer.method_2966().getName()).replace("%message%", event.message));
                  break;
               }
            }
         }

         event.cancel();
         return;
      }
   }
}
