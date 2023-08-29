package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.managers.impl.NotificationManager;
import dev.lemonclient.addon.utils.gui.SmoothUtils;
import dev.lemonclient.addon.utils.gui.TextUtils;
import dev.lemonclient.addon.utils.render.MSAAFramebuffer;
import dev.lemonclient.addon.utils.render.Renderer2DPlus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_2960;
import net.minecraft.class_4587;

public class NotificationsHud extends HudElement {
   public static final HudElementInfo INFO;
   private static final class_2960 ERROR_ID;
   private static final class_2960 INFO_ID;
   private static final class_2960 SUCCESS_ID;
   private static final class_2960 WARN_ID;
   private static final Color ERROR_COLOR;
   private static final Color INFO_COLOR;
   private static final Color SUCCESS_COLOR;
   private static final Color WARN_COLOR;
   private final SettingGroup sgGeneral;
   private final Setting maxNotifications;
   private final Setting useCalcWidth;
   private final Setting reverse;

   public NotificationsHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.maxNotifications = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Max Notifications")).description("out of the num will remove")).defaultValue(7)).build());
      this.useCalcWidth = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Use Calc Width")).description("Automatic width calculation.")).defaultValue(false)).build());
      this.reverse = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Reverse Notifications")).description("Reverse the notification render.")).defaultValue(false)).build());
   }

   public void tick(HudRenderer renderer) {
      this.setSize(250.0, 50.0);
      super.tick(renderer);
   }

   public void render(HudRenderer renderer) {
      NotificationManager notificationManager = NotificationManager.INSTANCE;
      renderer.post(() -> {
         MSAAFramebuffer.use(() -> {
            double boxX = (double)this.x;
            double boxY = (double)this.y;
            if (notificationManager != null) {
               GL.enableBlend();
               double offset = 4.0;
               List copied = new ArrayList(notificationManager.notifications);
               if ((Boolean)this.reverse.get()) {
                  Collections.reverse(copied);
               }

               for(Iterator var9 = copied.iterator(); var9.hasNext(); boxY -= 50.0 + offset) {
                  NotificationManager.Notification n = (NotificationManager.Notification)var9.next();
                  if (copied.size() > (Integer)this.maxNotifications.get()) {
                     ((NotificationManager.Notification)notificationManager.notifications.get(0)).showTime = 0;
                  }

                  double width = (Boolean)this.useCalcWidth.get() ? offset + 35.0 + TextUtils.getWidth(n.text, 1.1) + offset : 250.0;
                  if (n.showTime <= 1 && n.startUpdated) {
                     n.x = SmoothUtils.smoothMove(n.x, boxX + width);
                     if (n.x >= boxX + width - 2.0) {
                        n.willRemove = true;
                     }
                  } else if (n.startUpdated) {
                     n.x = SmoothUtils.smoothMove(n.x, boxX + 250.0 - width);
                  }

                  n.y = SmoothUtils.smoothMove(n.y, boxY);
                  Renderer2D.COLOR.begin();
                  Renderer2DPlus.quadRounded(n.x, n.y, width, 50.0, 4.0, new Color(70, 70, 70, 150));
                  Renderer2D.COLOR.end();
                  Color proColor = new Color();
                  switch (n.type) {
                     case INFO:
                        GL.bindTexture(INFO_ID);
                        proColor = INFO_COLOR;
                        break;
                     case ERROR:
                        GL.bindTexture(ERROR_ID);
                        proColor = ERROR_COLOR;
                        break;
                     case WARING:
                        GL.bindTexture(WARN_ID);
                        proColor = WARN_COLOR;
                        break;
                     case SUCCESS:
                        GL.bindTexture(SUCCESS_ID);
                        proColor = SUCCESS_COLOR;
                  }

                  Renderer2D.TEXTURE.begin();
                  Renderer2D.TEXTURE.texQuad(n.x + offset, n.y + 15.0, 23.0, 23.0, Color.WHITE);
                  Renderer2D.TEXTURE.render((class_4587)null);
                  TextUtils.render(n.text, n.x + 35.0, n.y + 15.0, proColor, 1.1);
               }

               GL.disableBlend();
            }

         });
      });
      super.render(renderer);
   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Notifications", "Displays notifications on hud.", NotificationsHud::new);
      ERROR_ID = new class_2960("lemon-client", "notification/error.png");
      INFO_ID = new class_2960("lemon-client", "notification/info.png");
      SUCCESS_ID = new class_2960("lemon-client", "notification/success.png");
      WARN_ID = new class_2960("lemon-client", "notification/warning.png");
      ERROR_COLOR = Color.RED;
      INFO_COLOR = Color.WHITE;
      SUCCESS_COLOR = Color.GREEN;
      WARN_COLOR = Color.YELLOW;
   }
}
