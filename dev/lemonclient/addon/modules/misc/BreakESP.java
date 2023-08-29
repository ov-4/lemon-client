package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.modules.combat.AutoCrystal;
import dev.lemonclient.addon.modules.combat.AutoCrystalPlus;
import dev.lemonclient.addon.utils.LemonUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixin.WorldRendererAccessor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2620;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import org.joml.Vector3d;

public class BreakESP extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting mode;
   private final Setting scale;
   private final Setting maxTime;
   private final Setting shapeMode;
   private final Setting sideColor;
   private final Setting lineColor;
   private final Setting renderProgess;
   private final Setting progressColor;
   private final Setting renderName;
   private final Setting nameColor;
   Map blocks;
   private final List renders;
   Render render;

   public BreakESP() {
      super(LemonClient.Misc, "Break ESP", "Show the destruction progress of the box.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description("Render mode.")).defaultValue(BreakESP.Mode.Both)).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Scale")).defaultValue(1.0).sliderRange(0.1, 2.0).build());
      this.maxTime = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Max Time")).description("Removes rendered box after this time.")).defaultValue(10.0).min(0.0).sliderRange(0.0, 50.0).visible(() -> {
         return this.mode.get() == BreakESP.Mode.Box;
      })).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 100)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 100)).build());
      this.renderProgess = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Render Progess")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      ColorSetting.Builder var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Progress Text Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255));
      Setting var10003 = this.renderProgess;
      Objects.requireNonNull(var10003);
      this.progressColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.renderName = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Render Name")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Name Text Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255));
      var10003 = this.renderName;
      Objects.requireNonNull(var10003);
      this.nameColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.blocks = ((WorldRendererAccessor)this.mc.field_1769).getBlockBreakingInfos();
      this.renders = new ArrayList();
      this.render = null;
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      this.blocks.values().forEach((info) -> {
         class_2338 pos = info.method_13991();
         class_2680 state = this.mc.field_1687.method_8320(pos);
         class_265 shape = state.method_26218(this.mc.field_1687, pos);
         if (!shape.method_1110()) {
            if (!((Mode)this.mode.get()).equals(BreakESP.Mode.Text)) {
               if (this.mc.field_1724 == null || this.mc.field_1687 == null) {
                  return;
               }

               if (this.render != null && this.contains()) {
                  this.render = null;
               }

               this.renders.removeIf((r) -> {
                  return System.currentTimeMillis() > r.time + Math.round((Double)this.maxTime.get() * 1000.0) || this.render != null && r.id == this.render.id || !LemonUtils.solid2(r.pos);
               });
               if (this.render != null) {
                  this.renders.add(this.render);
                  this.render = null;
               }

               this.renders.forEach((r) -> {
                  double delta = Math.min((double)(System.currentTimeMillis() - r.time) / ((Double)this.maxTime.get() * 1000.0), 1.0);
                  event.renderer.box(this.getBox(r.pos, this.getProgress(Math.min(delta * 4.0, 1.0))), this.getColor((Color)this.sideColor.get(), 1.0 - delta), this.getColor((Color)this.lineColor.get(), 1.0 - delta), (ShapeMode)this.shapeMode.get(), 0);
               });
            }

         }
      });
   }

   @EventHandler
   private void onReceive(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2620 packet) {
         this.render = new Render(packet.method_11277(), packet.method_11280(), System.currentTimeMillis());
      }

   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      this.blocks.values().forEach((info) -> {
         class_2338 pos = info.method_13991();
         int stage = info.method_13988();
         boolean compatibility = this.hasRenderPos(pos);
         class_1297 entity = this.mc.field_1687.method_8469(info.method_34868());
         class_1657 player = entity == null ? null : (class_1657)entity;
         double shrinkFactor = (double)(9 - (stage + 1)) / 9.0;
         double progress = 1.0 - shrinkFactor;
         if (!((Mode)this.mode.get()).equals(BreakESP.Mode.Box) && player != null) {
            class_243 rPos = new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.7, (double)pos.method_10260() + 0.5);
            Vector3d p1 = new Vector3d(rPos.field_1352, compatibility ? rPos.field_1351 - 0.3 : rPos.field_1351 - (Double)this.scale.get() / 9.9 - (Double)this.scale.get() / 3.333, rPos.field_1350);
            if (!NametagUtils.to2D(p1, (Double)this.scale.get(), true)) {
               return;
            }

            NametagUtils.begin(p1);
            TextRenderer font = TextRenderer.get();
            font.begin((Double)this.scale.get());
            double var10000 = (double)Math.round(progress * 100.0);
            String text = var10000 / 100.0 + "%";
            String name = player.method_7334().getName();
            if ((Boolean)this.renderProgess.get()) {
               font.render(text, -(font.getWidth(text) / 2.0), -font.getHeight(), (Color)this.progressColor.get(), false);
            }

            if ((Boolean)this.renderName.get()) {
               font.render(name, -(font.getWidth(name) / 2.0), -(font.getHeight() / 2.0) + 2.0 + font.getHeight(), (Color)this.nameColor.get(), false);
            }

            font.end();
            NametagUtils.end();
         }

      });
   }

   private boolean isAutoCrystalEnable() {
      boolean clientCombat = this.isEnable(AutoCrystal.class) || this.isEnable(AutoCrystalPlus.class);
      boolean meteorCombat = this.isEnable(CrystalAura.class);
      return clientCombat || meteorCombat;
   }

   private boolean hasRenderPos(class_2338 pos) {
      if (this.isAutoCrystalEnable() && this.getEnable() != null) {
         Module module = this.getEnable();
         if (module instanceof AutoCrystal) {
            AutoCrystal c = (AutoCrystal)module;
            return c.renderPos.equals(pos);
         }
      }

      return false;
   }

   private Module getEnable() {
      if (this.isEnable(AutoCrystal.class)) {
         return Modules.get().get(AutoCrystal.class);
      } else if (this.isEnable(AutoCrystalPlus.class)) {
         return Modules.get().get(AutoCrystalPlus.class);
      } else {
         return this.isEnable(CrystalAura.class) ? Modules.get().get(CrystalAura.class) : null;
      }
   }

   private boolean isEnable(Class moduleClass) {
      return Modules.get().isActive(moduleClass);
   }

   private boolean contains() {
      Iterator var1 = this.renders.iterator();

      Render r;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         r = (Render)var1.next();
      } while(r.id != this.render.id || !r.pos.equals(this.render.pos));

      return true;
   }

   private Color getColor(Color color, double delta) {
      return new Color(color.r, color.g, color.b, (int)Math.floor((double)color.a * delta));
   }

   private double getProgress(double delta) {
      return 1.0 - Math.pow(1.0 - delta, 5.0);
   }

   private class_238 getBox(class_2338 pos, double progress) {
      return new class_238((double)pos.method_10263() + 0.5 - progress / 2.0, (double)pos.method_10264() + 0.5 - progress / 2.0, (double)pos.method_10260() + 0.5 - progress / 2.0, (double)pos.method_10263() + 0.5 + progress / 2.0, (double)pos.method_10264() + 0.5 + progress / 2.0, (double)pos.method_10260() + 0.5 + progress / 2.0);
   }

   public static enum Mode {
      Text,
      Box,
      Both;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Text, Box, Both};
      }
   }

   private static record Render(class_2338 pos, int id, long time) {
      private Render(class_2338 pos, int id, long time) {
         this.pos = pos;
         this.id = id;
         this.time = time;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public int id() {
         return this.id;
      }

      public long time() {
         return this.time;
      }
   }
}
