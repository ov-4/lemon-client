package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.hud.ToastNotifications;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.info.Notifications;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1291;

public class WeakNotifier extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgNone;
   private final Setting single;
   private final Setting delay;
   private final Setting weakness;
   private final Setting weaknessEnded;
   private final Setting notifications;
   private int timer;
   private boolean last;

   public WeakNotifier() {
      super(LemonClient.Misc, "Weak Notifier", "Notify you if you get weakness.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgNone = this.settings.createGroup("");
      this.single = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Single")).description("Only sends the message once.")).defaultValue(false)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Delay")).description("Tick delay between sending the message.")).defaultValue(5)).range(0, 60).sliderMax(60).visible(() -> {
         return !(Boolean)this.single.get();
      })).build());
      this.weakness = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("Weakness")).description("get weakness message.")).defaultValue("you have weakness!!!")).build());
      this.weaknessEnded = this.sgGeneral.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("Weakness Ended")).description("weakness ended message.")).defaultValue("weakness has ended")).build());
      this.notifications = this.sgNone.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Notifications")).defaultValue(Notifications.Mode.Chat)).build());
      this.timer = 0;
      this.last = false;
   }

   @EventHandler(
      priority = 100
   )
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.mc.field_1724.method_6059(class_1291.method_5569(18))) {
            if ((Boolean)this.single.get()) {
               if (!this.last) {
                  this.last = true;
                  switch ((Notifications.Mode)this.notifications.get()) {
                     case Chat:
                        this.sendNotificationsInfo((String)this.weakness.get());
                        break;
                     case Notification:
                        Managers.NOTIFICATION.warn(this.title, (String)this.weakness.get());
                        break;
                     case Toast:
                        ToastNotifications.addToast((String)this.weakness.get());
                  }
               }
            } else if (this.timer > 0) {
               --this.timer;
            } else {
               this.timer = (Integer)this.delay.get();
               this.last = true;
               switch ((Notifications.Mode)this.notifications.get()) {
                  case Chat:
                     this.sendNotificationsInfo((String)this.weakness.get());
                     break;
                  case Notification:
                     Managers.NOTIFICATION.warn(this.title, (String)this.weakness.get());
                     break;
                  case Toast:
                     ToastNotifications.addToast((String)this.weakness.get());
               }
            }
         } else if (this.last) {
            this.last = false;
            switch ((Notifications.Mode)this.notifications.get()) {
               case Chat:
                  this.sendNotificationsInfo((String)this.weaknessEnded.get());
                  break;
               case Notification:
                  Managers.NOTIFICATION.warn(this.title, (String)this.weaknessEnded.get());
                  break;
               case Toast:
                  ToastNotifications.addToast((String)this.weaknessEnded.get());
            }
         }
      }

   }
}
