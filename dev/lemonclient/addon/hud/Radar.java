package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import java.util.Iterator;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_4587;

public class Radar extends HudElement {
   public static final HudElementInfo INFO;
   private final SettingGroup sgGeneral;
   private final Setting backgroundColor;
   private final Setting entities;
   private final Setting letters;
   private final Setting showWaypoints;
   private final Setting scale;
   private final Setting zoom;

   public Radar() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.backgroundColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color of background.")).defaultValue(new SettingColor(0, 0, 0, 64)).build());
      this.entities = this.sgGeneral.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Select specific entities.")).defaultValue(new class_1299[]{class_1299.field_6097}).build());
      this.letters = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("letters")).description("Use entity's type first letter.")).defaultValue(true)).build());
      this.showWaypoints = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("waypoints")).description("Show waypoints.")).defaultValue(false)).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale.")).defaultValue(1.0).min(1.0).sliderRange(0.01, 5.0).onChanged((aDouble) -> {
         this.calculateSize();
      })).build());
      this.zoom = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("zoom")).description("Radar zoom.")).defaultValue(1.0).min(0.01).sliderRange(0.01, 3.0).build());
      this.calculateSize();
   }

   public void calculateSize() {
      this.setSize(200.0 * (Double)this.scale.get(), 200.0 * (Double)this.scale.get());
   }

   public void render(HudRenderer renderer) {
      ESP esp = (ESP)Modules.get().get(ESP.class);
      if (esp != null) {
         renderer.post(() -> {
            if (MeteorClient.mc.field_1724 != null) {
               double width = (double)this.getWidth();
               double height = (double)this.getHeight();
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.quad((double)this.x, (double)this.y, width, height, (Color)this.backgroundColor.get());
               Renderer2D.COLOR.render((class_4587)null);
               Iterator var7;
               double yPos;
               if (MeteorClient.mc.field_1687 != null) {
                  var7 = MeteorClient.mc.field_1687.method_18112().iterator();

                  while(var7.hasNext()) {
                     class_1297 entity = (class_1297)var7.next();
                     if (!((Set)this.entities.get()).contains(entity.method_5864())) {
                        return;
                     }

                     double xPos = (entity.method_23317() - MeteorClient.mc.field_1724.method_23317()) * (Double)this.scale.get() * (Double)this.zoom.get() + width / 2.0;
                     yPos = (entity.method_23321() - MeteorClient.mc.field_1724.method_23321()) * (Double)this.scale.get() * (Double)this.zoom.get() + height / 2.0;
                     if (!(xPos < 0.0) && !(yPos < 0.0) && !(xPos > width - (Double)this.scale.get()) && !(yPos > height - (Double)this.scale.get())) {
                        String icon = "*";
                        if ((Boolean)this.letters.get()) {
                           icon = entity.method_5864().method_35050().substring(0, 1).toUpperCase();
                        }

                        Color c = esp.getColor(entity);
                        if (c == null) {
                           c = Color.WHITE;
                        }

                        renderer.text(icon, xPos + (double)this.x, yPos + (double)this.y, c, false);
                     }
                  }
               }

               if ((Boolean)this.showWaypoints.get()) {
                  var7 = Waypoints.get().iterator();

                  while(var7.hasNext()) {
                     Waypoint waypoint = (Waypoint)var7.next();
                     class_2338 blockPos = waypoint.getPos();
                     class_243 coords = new class_243((double)blockPos.method_10263() + 0.5, (double)blockPos.method_10264(), (double)blockPos.method_10260() + 0.5);
                     yPos = (coords.method_10216() - MeteorClient.mc.field_1724.method_23317()) * (Double)this.scale.get() * (Double)this.zoom.get() + width / 2.0;
                     double yPosx = (coords.method_10215() - MeteorClient.mc.field_1724.method_23321()) * (Double)this.scale.get() * (Double)this.zoom.get() + height / 2.0;
                     if (!(yPos < 0.0) && !(yPosx < 0.0) && !(yPos > width - (Double)this.scale.get()) && !(yPosx > height - (Double)this.scale.get())) {
                        String iconx = "*";
                        if ((Boolean)this.letters.get() && ((String)waypoint.name.get()).length() > 0) {
                           iconx = ((String)waypoint.name.get()).substring(0, 1);
                        }

                        renderer.text(iconx, yPos + (double)this.x, yPosx + (double)this.y, (Color)waypoint.color.get(), false);
                     }
                  }
               }

               Renderer2D.COLOR.render((class_4587)null);
            }
         });
      }
   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Radar", "Draws a Radar on your HUD telling you where entities are.", Radar::new);
   }
}
