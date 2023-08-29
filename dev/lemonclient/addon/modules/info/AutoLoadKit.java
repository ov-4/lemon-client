package dev.lemonclient.addon.modules.info;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.hud.ToastNotifications;
import dev.lemonclient.addon.managers.Managers;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_418;

public class AutoLoadKit extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting kName;
   private final Setting kCommand;
   private final Setting notifications;
   private boolean lock;
   private int i;

   public AutoLoadKit() {
      super(LemonClient.Info, "Auto Load Kit", "Automatically takes specified kit after joining server/respawn.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.kName = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name-of-kit")).description("Name of kit that should be taken.")).defaultValue("")).build());
      this.kCommand = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("kit-command")).description("Command to activate kit commands.")).defaultValue("/kit")).build());
      this.notifications = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("notifications")).defaultValue(Notifications.Mode.Toast)).build());
      this.lock = false;
      this.i = 40;
   }

   @EventHandler
   private void onOpenScreenEvent(OpenScreenEvent event) {
      if (event.screen instanceof class_418) {
         this.lock = true;
         this.i = 40;
      }
   }

   @EventHandler
   private void onGameJoin(GameJoinedEvent event) {
      this.lock = true;
      this.i = 40;
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (!(this.mc.field_1755 instanceof class_418)) {
         if (this.lock) {
            --this.i;
         }

         if (this.lock && this.i <= 0) {
            switch ((Notifications.Mode)this.notifications.get()) {
               case Toast:
                  ToastNotifications.addToast("Selected kit: " + (String)this.kName.get());
                  break;
               case Notification:
                  Managers.NOTIFICATION.info(this.title, "Selected kit: " + (String)this.kName.get());
                  break;
               case Chat:
                  this.info("Selected kit: " + (String)this.kName.get(), new Object[0]);
            }

            String var10000 = (String)this.kCommand.get();
            ChatUtils.sendPlayerMsg(var10000 + " " + (String)this.kName.get());
            this.lock = false;
            this.i = 40;
         }

      }
   }
}
