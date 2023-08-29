package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Alignment;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import net.minecraft.class_4587;

public class ToastNotifications extends HudElement {
   public static final HudElementInfo INFO;
   private static ToastNotifications instance;
   private final SettingGroup sgGeneral;
   public final Setting toggleMessage;
   public final Setting sound;
   public final Setting left;
   public final Setting toggleList;
   private final Setting removeDelay;
   private final Setting shadow;
   public static ArrayList toasts;
   static int timer1;

   public static ToastNotifications getInstance() {
      return instance;
   }

   public ToastNotifications() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.toggleMessage = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-message")).description("Sends info about toggled modules.")).defaultValue(true)).build());
      this.sound = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("sound")).defaultValue(true)).build());
      this.left = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("left-sided")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      ModuleListSetting.Builder var10002 = (ModuleListSetting.Builder)((ModuleListSetting.Builder)(new ModuleListSetting.Builder()).name("Modules for displaying")).defaultValue(Modules.get().getGroup(Categories.Combat));
      Setting var10003 = this.toggleMessage;
      Objects.requireNonNull(var10003);
      this.toggleList = var10001.add(((ModuleListSetting.Builder)var10002.visible(var10003::get)).build());
      this.removeDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("remove-delay")).description("Delay to clean latest message.")).defaultValue(7)).min(1).sliderMax(10).build());
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Shadow")).description("Renders a shadow behind the chars.")).defaultValue(false)).build());
      instance = this;
   }

   public void tick(HudRenderer renderer) {
      this.updator();
      double width = 0.0;
      double height = 0.0;
      width = Math.max(width, renderer.textWidth("toast-messages"));
      height += renderer.textHeight();
      this.box.setSize(width, height);
   }

   public void render(HudRenderer renderer) {
      renderer.post(() -> {
         try {
            Color back = new Color(50, 50, 50, 255);
            Color textColor = new Color(255, 255, 255, 255);
            this.updator();
            double x = (double)this.x - 0.5;
            double y = (double)this.y - 0.5;
            int w = this.getWidth();
            int h = this.getHeight();
            if (this.isInEditor()) {
               renderer.text("toast-messages", x, y, textColor, false);
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.quad(x, y, (double)w, (double)h, back);
               Renderer2D.COLOR.render((class_4587)null);
               return;
            }

            int i = 0;
            if (toasts.isEmpty()) {
               String t = "";
               TextRenderer.get().render(t, x + this.alignX(renderer.textWidth(t), Alignment.Auto), y, textColor, (Boolean)this.shadow.get());
            } else {
               for(Iterator var21 = toasts.iterator(); var21.hasNext(); ++i) {
                  notifications mes = (notifications)var21.next();
                  double width = TextRenderer.get().getWidth(mes.text) + 5.0;
                  double end = (Boolean)this.left.get() ? (double)this.x + width : (double)(this.x + this.getWidth()) - width;
                  double start = (Boolean)this.left.get() ? (double)this.x - width : end + width;
                  if (mes.pos < width) {
                     mes.pos = moveX(mes.pos, width + 1.0);
                  }

                  if (mes.pos > width) {
                     mes.pos = width;
                  }

                  if (i == 0 && timer1 >= (Integer)this.removeDelay.get() * 140 - 100) {
                     mes.pos = moveX(mes.pos, -(width + 1.0));
                  }

                  start = (Boolean)this.left.get() ? start + mes.pos + 6.0 : start - mes.pos;
                  Renderer2D.COLOR.begin();
                  Renderer2D.COLOR.quad(start - 6.0, y - 4.0, TextRenderer.get().getWidth(mes.text) + 10.0, renderer.textHeight(), mes.color);
                  Renderer2D.COLOR.quad(start - 2.0, y - 4.0, TextRenderer.get().getWidth(mes.text) + 2.0, renderer.textHeight(), back);
                  Renderer2D.COLOR.render((class_4587)null);
                  TextRenderer.get().render(mes.text, start, y - 5.0, textColor, (Boolean)this.shadow.get());
                  y += renderer.textHeight();
                  if (i >= 0) {
                     ++y;
                  }
               }
            }
         } catch (ConcurrentModificationException var20) {
            var20.fillInStackTrace();
         }

      });
   }

   public static void addToast(String text, Color color) {
      if (getInstance() != null) {
         if (toasts.size() == 0) {
            timer1 = 0;
         }

         toasts.add(new notifications(text, color));
         class_310 mc = class_310.method_1551();
         if ((Boolean)getInstance().sound.get()) {
            mc.field_1724.method_5783(class_3417.field_14627, 1.0F, 1.0F);
         }
      }

   }

   public static void addToast(String text) {
      if (getInstance() != null) {
         if (toasts.size() == 0) {
            timer1 = 0;
         }

         toasts.add(new notifications(text, (Color)null));
         class_310 mc = class_310.method_1551();
         if ((Boolean)getInstance().sound.get()) {
            mc.field_1724.method_5783(class_3417.field_14627, 1.0F, 1.0F);
         }
      }

   }

   public static void addToggled(String text, Color color) {
      if (toasts.size() == 0) {
         timer1 = 0;
      }

      toasts.add(new notifications(text, color));
   }

   public static void addToggled(Module module, String mes) {
      String nameToTitle = Utils.nameToTitle(module.name);
      toasts.removeIf((toasts) -> {
         return toasts.text.endsWith("OFF");
      });
      toasts.removeIf((toasts) -> {
         return toasts.text.endsWith("ON");
      });
      if (mes.contains("F")) {
         addToggled(nameToTitle + " OFF", new Color(255, 0, 0, 255));
      } else {
         addToggled(nameToTitle + " ON", new Color(0, 255, 0, 255));
      }

      if (toasts.size() == 0) {
         timer1 = 0;
      }

   }

   private void updator() {
      if (toasts.size() > 7) {
         toasts.remove(0);
      }

      if (!toasts.isEmpty()) {
         if (timer1 >= (Integer)this.removeDelay.get() * 140) {
            toasts.remove(0);
            timer1 = 0;
         } else {
            ++timer1;
         }

      }
   }

   private static double moveX(double start, double end) {
      double speed = (end - start) * 0.1;
      if (speed > 0.0) {
         speed = Math.max(0.1, speed);
         speed = Math.min(end - start, speed);
      } else if (speed < 0.0) {
         speed = Math.min(-0.1, speed);
         speed = Math.max(end - start, speed);
      }

      return start + speed;
   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Toast Notifications", "Displays toast notifications on hud.", ToastNotifications::new);
      toasts = new ArrayList();
   }

   public static class notifications {
      public final String text;
      public final Color color;
      public double pos = -1.0;

      public notifications(String text, Color color) {
         if (color == null) {
            color = new Color(0, 155, 255, 255);
         }

         this.text = text;
         this.color = color;
      }
   }
}
