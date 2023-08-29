package dev.lemonclient.addon.managers.impl;

import dev.lemonclient.addon.utils.timers.MSTimer;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;

public class NotificationManager {
   public static NotificationManager INSTANCE;
   public final List notifications = new ArrayList();
   private final MSTimer timer = new MSTimer();

   public NotificationManager() {
      INSTANCE = this;
      this.timer.reset();
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   public void info(String title, String txt) {
      this.notifications.add((new Notification(NotificationManager.Notification.Type.INFO, title, txt)).withShowTime(30));
   }

   public void success(String title, String txt) {
      this.notifications.add((new Notification(NotificationManager.Notification.Type.SUCCESS, title, txt)).withShowTime(30));
   }

   public void warn(String title, String txt) {
      this.notifications.add((new Notification(NotificationManager.Notification.Type.WARING, title, txt)).withShowTime(30));
   }

   public void error(String title, String txt) {
      this.notifications.add((new Notification(NotificationManager.Notification.Type.ERROR, title, txt)).withShowTime(30));
   }

   private void renderUpdate() {
      if (this.timer.hasTimePassed(1L)) {
         if (!this.notifications.isEmpty()) {
            Notification main = (Notification)this.notifications.get(0);
            if (main.startUpdated) {
               main.update();
            }

            if (main.showTime <= 0 && main.willRemove) {
               this.notifications.remove(0);
            }
         }

         this.timer.reset();
      }

   }

   private void tickUpdate() {
      if (!this.notifications.isEmpty()) {
         Notification main = (Notification)this.notifications.get(0);
         if (main.startUpdated) {
            main.update();
         }

         if (main.showTime <= 0 && main.willRemove) {
            this.notifications.remove(0);
         }
      }

   }

   @EventHandler
   private void onRender(Render2DEvent event) {
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      this.tickUpdate();
   }

   public static class Notification {
      public Type type;
      public String title;
      public String text;
      public int showTime = 1500;
      public int maxShowTime = 1500;
      public double x;
      public double y;
      public boolean startUpdated;
      public boolean willRemove;

      public Notification(Type type, String title, String text) {
         this.withType(type).withTitle(title).withText(text);
         this.startUpdated = true;
         this.willRemove = false;
         this.x = (double)Utils.getWindowWidth();
         this.y = (double)Utils.getWindowHeight();
      }

      public Notification withType(Type type) {
         this.type = type;
         return this;
      }

      public Notification withTitle(String s) {
         this.title = s;
         return this;
      }

      public Notification withText(String s) {
         this.text = s;
         return this;
      }

      public Notification withShowTime(int t) {
         this.showTime = t;
         this.maxShowTime = t;
         return this;
      }

      public void update() {
         if (this.showTime > 0) {
            --this.showTime;
         }

      }

      public void destroy() {
         this.type = null;
         this.title = null;
         this.text = null;
         this.showTime = this.maxShowTime = 0;
      }

      public static enum Type {
         INFO,
         SUCCESS,
         WARING,
         ERROR;

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{INFO, SUCCESS, WARING, ERROR};
         }
      }
   }
}
