package dev.lemonclient.addon.hud;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.utils.render.RenderUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_640;
import net.minecraft.class_742;
import net.minecraft.class_7532;
import net.minecraft.class_7833;

public class TargetHud extends HudElement {
   private final SettingGroup sgGeneral;
   private final Setting mode;
   private final Setting scale;
   private final Setting bgColor;
   private final Setting textColor;
   private final Setting healthColor;
   private final Setting absorptionColor;
   private final Setting damageTilt;
   public static final HudElementInfo INFO;
   private class_742 target;
   private String renderName;
   private class_2960 renderSkin;
   private float renderHealth;
   private float renderPing;
   private double scaleProgress;
   private long damageTime;
   private UUID lastTarget;
   private float lastHp;
   private boolean popped;
   private final Map tog;

   public TargetHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description("What mode to use for the TargetHud.")).defaultValue(TargetHud.Mode.Normal)).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Scale")).description("Scale to render at")).defaultValue(1.0).range(0.0, 5.0).sliderRange(0.0, 5.0).build());
      this.bgColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Background Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(0, 0, 0, 200)).build());
      this.textColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Text Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.healthColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Health Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
      this.absorptionColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Absorption Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 0, 255)).build());
      this.damageTilt = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Damage Tilt")).description("How many degrees should the box be rotated when enemy takes damage.")).defaultValue(10.0).min(0.0).sliderRange(0.0, 45.0).build());
      this.renderName = null;
      this.renderSkin = null;
      this.scaleProgress = 0.0;
      this.lastTarget = null;
      this.lastHp = 0.0F;
      this.popped = false;
      this.tog = new HashMap();
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   @EventHandler(
      priority = 10000
   )
   private void onTick(TickEvent.Pre event) {
      if (MeteorClient.mc.field_1687 != null && MeteorClient.mc.field_1724 != null) {
         List toRemove = new ArrayList();
         Iterator var3 = this.tog.entrySet().iterator();

         while(true) {
            Map.Entry entry;
            do {
               if (!var3.hasNext()) {
                  Map var10001 = this.tog;
                  Objects.requireNonNull(var10001);
                  toRemove.forEach(var10001::remove);
                  MeteorClient.mc.field_1687.method_18456().forEach((player) -> {
                     if (player.method_24828()) {
                        if (this.tog.containsKey(player)) {
                           this.tog.replace(player, (Integer)this.tog.get(player) + 1);
                        } else {
                           this.tog.put(player, 1);
                        }
                     }

                  });
                  if (this.target != null) {
                     if (this.target.method_5667().equals(this.lastTarget)) {
                        float diff = Math.max(this.lastHp - this.target.method_6032() - this.target.method_6067(), 0.0F);
                        if (diff > 1.0F) {
                           this.damageTime = System.currentTimeMillis();
                        }
                     }

                     this.lastTarget = this.target.method_5667();
                     this.lastHp = this.popped ? 0.0F : this.target.method_6032() + this.target.method_6067();
                     this.popped = false;
                  } else {
                     this.lastTarget = null;
                     this.lastHp = 0.0F;
                     this.damageTime = 0L;
                  }

                  return;
               }

               entry = (Map.Entry)var3.next();
            } while(MeteorClient.mc.field_1687.method_18456().contains(entry.getKey()) && !((class_742)entry.getKey()).method_7325() && ((class_742)entry.getKey()).method_6032() > 0.0F);

            toRemove.add((class_742)entry.getKey());
         }
      }
   }

   @EventHandler(
      priority = 10000
   )
   private void onReceive(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2663 packet) {
         if (packet.method_11470() == 35) {
            class_1297 entity = packet.method_11469(MeteorClient.mc.field_1687);
            if (entity instanceof class_1657) {
               class_1657 player = (class_1657)entity;
               if (player == this.target) {
                  this.popped = true;
               }
            }

         }
      }
   }

   public void render(HudRenderer renderer) {
      byte height;
      short width;
      class_4587 stack;
      String var10000;
      if (this.mode.get() == TargetHud.Mode.Normal) {
         height = 100;
         width = 200;
         this.setSize((double)width * (Double)this.scale.get(), (double)height * (Double)this.scale.get());
         this.updateTarget();
         if (this.renderName == null) {
            return;
         }

         stack = new class_4587();
         this.scaleProgress = class_3532.method_15350(this.scaleProgress + (this.target == null ? -renderer.delta : renderer.delta), 0.0, 1.0);
         float scaleAnimation = (float)(this.scaleProgress * this.scaleProgress * this.scaleProgress);
         stack.method_46416((float)this.x + (1.0F - scaleAnimation) * (float)this.getWidth() / 2.0F, (float)this.y + (1.0F - scaleAnimation) * (float)this.getHeight() / 2.0F, 0.0F);
         stack.method_22905((float)((double)scaleAnimation * (Double)this.scale.get()), (float)((double)scaleAnimation * (Double)this.scale.get()), 1.0F);
         float tilt = (float)((double)((float)Math.max(0L, 500L - System.currentTimeMillis() + this.damageTime) / 500.0F) * (Double)this.damageTilt.get());
         stack.method_22907(class_7833.field_40718.rotationDegrees(tilt));
         RenderUtils.rounded(stack, 15.0F, 15.0F, (float)(width - 30), (float)(height - 30), 15.0F, 10, ((SettingColor)this.bgColor.get()).getPacked());
         this.drawFace(renderer, scaleAnimation * ((Double)this.scale.get()).floatValue(), (double)((float)this.x + (1.0F - scaleAnimation) * (float)this.getWidth() / 2.0F), (double)((float)this.y + (1.0F - scaleAnimation) * (float)this.getHeight() / 2.0F), tilt);
         RenderUtils.text(this.renderName, stack, 60.0F, 20.0F, ((SettingColor)this.textColor.get()).getPacked());
         RenderUtils.text(Math.round(this.renderPing) + "ms", stack, 60.0F, 30.0F, ((SettingColor)this.textColor.get()).getPacked());
         var10000 = String.valueOf((float)Math.round(this.renderHealth * 10.0F) / 10.0F);
         Objects.requireNonNull(MeteorClient.mc.field_1772);
         RenderUtils.text(var10000, stack, 20.0F, 81.0F - 9.0F / 2.0F, ((SettingColor)this.textColor.get()).getPacked());
         float barAnimation = class_3532.method_16439(MeteorClient.mc.method_1488() / 10.0F, this.lastHp, this.renderHealth);
         float barStart = (float)(Math.max(MeteorClient.mc.field_1772.method_1727(String.valueOf((float)Math.round(this.renderHealth * 10.0F) / 10.0F)), MeteorClient.mc.field_1772.method_1727("36.0")) + 28);
         if (barAnimation > 0.0F) {
            RenderUtils.rounded(stack, barStart, 80.0F, class_3532.method_15363(barAnimation / 20.0F, 0.0F, 1.0F) * ((float)(width - 30) - barStart), 2.0F, 3.0F, 10, ((SettingColor)this.healthColor.get()).getPacked());
         }

         if (barAnimation > 20.0F) {
            RenderUtils.rounded(stack, barStart, 80.0F, class_3532.method_15363((barAnimation - 20.0F) / 16.0F, 0.0F, 1.0F) * ((float)(width - 30) - barStart), 2.0F, 3.0F, 10, ((SettingColor)this.absorptionColor.get()).getPacked());
         }
      }

      int progress;
      int i;
      if (this.mode.get() == TargetHud.Mode.ExhibitionOld) {
         height = 60;
         width = 240;
         this.setSize((double)width * (Double)this.scale.get(), (double)height * (Double)this.scale.get());
         this.updateTarget();
         stack = new class_4587();
         if (this.target == null || this.renderName == null) {
            return;
         }

         stack.method_46416((float)this.x, (float)this.y, 0.0F);
         stack.method_22905((float)((Double)this.scale.get() * 1.0), (float)((Double)this.scale.get() * 1.0), 1.0F);
         RenderUtils.quad(stack, 0.0F, 0.0F, (float)width, (float)height, ((SettingColor)this.bgColor.get()).getPacked());
         RenderUtils.quad(stack, 1.0F, 1.0F, 58.0F, 58.0F, (new Color(102, 102, 102, 255)).getPacked());
         this.drawFace(renderer, ((Double)this.scale.get()).floatValue(), (double)this.x, (double)this.y, 0.0F);
         stack.method_22905(2.0F, 2.0F, 1.0F);
         RenderUtils.text(this.renderName, stack, 33.0F, 2.0F, ((SettingColor)this.textColor.get()).getPacked());
         stack.method_22905(0.5F, 0.5F, 1.0F);
         var10000 = (float)Math.round(this.renderHealth * 10.0F) / 10.0F + " Dist: " + (float)Math.round(MeteorClient.mc.field_1724.method_5739(this.target) * 10.0F) / 10.0F;
         Objects.requireNonNull(MeteorClient.mc.field_1772);
         RenderUtils.text(var10000, stack, 66.0F, 35.0F - 9.0F / 2.0F, ((SettingColor)this.textColor.get()).getPacked());
         stack.method_22905(2.0F, 2.0F, 1.0F);
         progress = (int)Math.ceil((double)class_3532.method_15363(this.renderHealth, 0.0F, 20.0F));

         for(i = 0; i < 10; ++i) {
            RenderUtils.quad(stack, (float)(33 + i * 8), 11.0F, (float)(3 * Math.min(progress, 2)), 3.0F, (new Color(204, 204, 0, 255)).getPacked());
            progress -= 2;
            if (progress <= 0) {
               break;
            }
         }

         stack.method_22905(0.5F, 0.5F, 1.0F);
         var10000 = "Yaw: " + (float)Math.round(this.target.method_36454() * 10.0F) / 10.0F + " Pitch: " + (float)Math.round(this.target.method_36455() * 10.0F) / 10.0F + " BodyYaw: " + (float)Math.round(this.target.method_43078() * 10.0F) / 10.0F;
         Objects.requireNonNull(MeteorClient.mc.field_1772);
         RenderUtils.text(var10000, stack, 66.0F, 45.0F - 9.0F / 2.0F, ((SettingColor)this.textColor.get()).getPacked());
         var10000 = "TOG: " + this.tog.getOrDefault(this.target, 0) + " HURT: " + (float)Math.round((float)(this.target.field_6235 * 10)) / 10.0F + " TE: " + Math.round((float)this.target.field_6012);
         Objects.requireNonNull(MeteorClient.mc.field_1772);
         RenderUtils.text(var10000, stack, 66.0F, 55.0F - 9.0F / 2.0F, ((SettingColor)this.textColor.get()).getPacked());
      }

      if (this.mode.get() == TargetHud.Mode.Exhibition) {
         height = 60;
         width = 190;
         this.setSize((double)width * (Double)this.scale.get(), (double)height * (Double)this.scale.get());
         this.updateTarget();
         stack = new class_4587();
         if (this.target == null || this.renderName == null) {
            return;
         }

         stack.method_46416((float)this.x, (float)this.y, 0.0F);
         stack.method_22905((float)((Double)this.scale.get() * 1.0), (float)((Double)this.scale.get() * 1.0), 1.0F);
         RenderUtils.quad(stack, -2.0F, -2.0F, (float)(width + 4), (float)(height + 4), (new Color(52, 52, 52, 255)).getPacked());
         RenderUtils.quad(stack, -1.0F, -1.0F, (float)(width + 2), (float)(height + 2), (new Color(32, 32, 32, 255)).getPacked());
         RenderUtils.quad(stack, 0.0F, 0.0F, (float)width, (float)height, (new Color(52, 52, 52, 255)).getPacked());
         stack.method_22905(1.5F, 1.5F, 1.0F);
         RenderUtils.text(this.renderName, stack, 41.0F, 2.0F, ((SettingColor)this.textColor.get()).getPacked());
         stack.method_22905(0.5F, 0.5F, 1.0F);
         var10000 = (float)Math.round(this.renderHealth * 10.0F) / 10.0F + " Dist: " + (float)Math.round(MeteorClient.mc.field_1724.method_5739(this.target) * 10.0F) / 10.0F;
         Objects.requireNonNull(MeteorClient.mc.field_1772);
         RenderUtils.text(var10000, stack, 83.0F, 40.0F - 9.0F / 2.0F, ((SettingColor)this.textColor.get()).getPacked());
         stack.method_22905(2.0F, 2.0F, 1.0F);
         progress = (int)Math.ceil((double)class_3532.method_15363(this.renderHealth, 0.0F, 20.0F));

         for(i = 0; i < 10; ++i) {
            RenderUtils.quad(stack, (float)(41 + i * 8), 12.0F, (float)(3 * Math.min(progress, 2)), 3.0F, (new Color(204, 204, 0, 255)).getPacked());
            progress -= 2;
            if (progress <= 0) {
               break;
            }
         }

         stack.method_22905(0.9F, 0.9F, 1.0F);
         class_4587 drawStack = renderer.drawContext.method_51448();
         drawStack.method_22903();
         drawStack.method_46416((float)this.x, (float)this.y, 0.0F);
         drawStack.method_22905(((Double)this.scale.get()).floatValue() * 1.35F, ((Double)this.scale.get()).floatValue() * 1.35F, 1.0F);

         for(int i = 0; i < 4; ++i) {
            class_1799 itemStack = (class_1799)this.target.method_31548().field_7548.get(i);
            renderer.drawContext.method_51427(itemStack, (3 - i) * 20 + 42, 25);
         }

         class_1799 itemStack = this.target.method_6047();
         renderer.drawContext.method_51427(itemStack, 122, 25);
         drawStack.method_22909();
      }

   }

   private void drawFace(HudRenderer renderer, float scale, double x, double y, float tilt) {
      class_4587 drawStack = renderer.drawContext.method_51448();
      drawStack.method_22903();
      drawStack.method_22904(x, y, 0.0);
      drawStack.method_22905(scale, scale, 1.0F);
      drawStack.method_22907(class_7833.field_40718.rotationDegrees(tilt));
      class_7532.method_44445(renderer.drawContext, this.renderSkin, 20, 18, 32, false, false);
      drawStack.method_22909();
   }

   private void updateTarget() {
      this.target = null;
      if (MeteorClient.mc.field_1687 != null) {
         class_742 closest = null;
         double distance = Double.MAX_VALUE;
         Iterator var4 = MeteorClient.mc.field_1687.method_18456().iterator();

         while(var4.hasNext()) {
            class_742 player = (class_742)var4.next();
            if (player != MeteorClient.mc.field_1724 && !Friends.get().isFriend(player)) {
               double d = (double)MeteorClient.mc.field_1724.method_5739(player);
               if (d < distance) {
                  closest = player;
                  distance = d;
               }
            }
         }

         this.target = closest;
         if (this.target == null && this.isInEditor()) {
            this.target = MeteorClient.mc.field_1724;
         }

         if (this.target != null) {
            this.renderName = this.target.method_5477().getString();
            this.renderSkin = this.target.method_3117();
            this.renderHealth = this.target.method_6032() + this.target.method_6067();
            class_640 playerListEntry = MeteorClient.mc.method_1562().method_2871(this.target.method_5667());
            this.renderPing = playerListEntry == null ? -1.0F : (float)playerListEntry.method_2959();
         }

      }
   }

   static {
      INFO = new HudElementInfo(LemonClient.HUD_GROUP, "Target Hud", "A target hud the fuck you thinkin bruv.", TargetHud::new);
   }

   public static enum Mode {
      Normal,
      ExhibitionOld,
      Exhibition;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Normal, ExhibitionOld, Exhibition};
      }
   }
}
