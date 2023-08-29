package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.timers.TimerUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_640;
import org.joml.Vector3d;

public class LogSpots extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting nameRender;
   private final Setting healthRender;
   private final Setting coordRender;
   private final Setting timePassed;
   private final Setting armorCheck;
   private final Setting scale;
   private final Setting notification;
   private final Setting shapeMode;
   private final Setting sideColor;
   private final Setting lineColor;
   private final Setting nameColor;
   private final Setting nameBackgroundColor;
   private final List players;
   private final List lastPlayerList;
   private final List lastPlayers;
   private int timer;
   private Dimension lastDimension;
   private static final Vector3d pos = new Vector3d();

   public LogSpots() {
      super(LemonClient.Misc, "Log Spots", "Displays a box where another player has logged out at.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.nameRender = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("name")).defaultValue(true)).build());
      this.healthRender = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("health")).defaultValue(true)).build());
      this.coordRender = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("coordinates")).defaultValue(false)).build());
      this.timePassed = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("time-passed")).defaultValue(true)).build());
      this.armorCheck = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("armor-check")).defaultValue(false)).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).defaultValue(1.0).min(0.2).sliderRange(0.2, 2.0).build());
      this.notification = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat-info")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(79, 90, 112, 10)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(79, 90, 112)).build());
      this.nameColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("name-color")).description("The name color.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.nameBackgroundColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("name-background-color")).description("The name background color.")).defaultValue(new SettingColor(0, 0, 0, 75)).build());
      this.players = new ArrayList();
      this.lastPlayerList = new ArrayList();
      this.lastPlayers = new ArrayList();
      this.lineColor.onChanged();
   }

   public void onActivate() {
      this.lastPlayerList.addAll(this.mc.method_1562().method_2880());
      this.updateLastPlayers();
      this.timer = 10;
      this.lastDimension = PlayerUtils.getDimension();
   }

   public void onDeactivate() {
      this.players.clear();
      this.lastPlayerList.clear();
   }

   private void updateLastPlayers() {
      this.lastPlayers.clear();
      Iterator var1 = this.mc.field_1687.method_18112().iterator();

      while(var1.hasNext()) {
         class_1297 entity = (class_1297)var1.next();
         if (entity instanceof class_1657) {
            this.lastPlayers.add((class_1657)entity);
         }
      }

   }

   @EventHandler
   private void onEntityAdded(EntityAddedEvent event) {
      if (event.entity instanceof class_1657) {
         int toRemove = -1;

         for(int i = 0; i < this.players.size(); ++i) {
            if (((Entry)this.players.get(i)).uuid.equals(event.entity.method_5667())) {
               if ((Boolean)this.notification.get()) {
                  this.info(((Entry)this.players.get(i)).name + " Just logged back!", new Object[]{new Color(100, 100, 255)});
               }

               toRemove = i;
               break;
            }
         }

         if (toRemove != -1) {
            this.players.remove(toRemove);
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.method_1562().method_2880().size() != this.lastPlayerList.size()) {
         Iterator var2 = this.lastPlayerList.iterator();

         label58:
         while(true) {
            class_640 entry;
            do {
               if (!var2.hasNext()) {
                  this.lastPlayerList.clear();
                  this.lastPlayerList.addAll(this.mc.method_1562().method_2880());
                  this.updateLastPlayers();
                  break label58;
               }

               entry = (class_640)var2.next();
            } while(this.mc.method_1562().method_2880().stream().anyMatch((playerListEntry) -> {
               return playerListEntry.method_2966().equals(entry.method_2966());
            }));

            Iterator var4 = this.lastPlayers.iterator();

            while(true) {
               class_1657 player;
               do {
                  if (!var4.hasNext()) {
                     continue label58;
                  }

                  player = (class_1657)var4.next();
               } while(!player.method_5667().equals(entry.method_2966().getId()));

               if ((Boolean)this.armorCheck.get()) {
                  for(int position = 3; position >= 0; --position) {
                     class_1799 itemStack = this.getItem(position, player);
                     if (itemStack.method_7960()) {
                        return;
                     }
                  }
               }

               this.add(new Entry(player));
            }
         }
      }

      if (this.timer <= 0) {
         this.updateLastPlayers();
         this.timer = 10;
      } else {
         --this.timer;
      }

      Dimension dimension = PlayerUtils.getDimension();
      if (dimension != this.lastDimension) {
         this.players.clear();
      }

      this.lastDimension = dimension;
   }

   private void add(Entry entry) {
      this.players.removeIf((player) -> {
         return player.uuid.equals(entry.uuid);
      });
      this.players.add(entry);
   }

   private class_1799 getItem(int i, class_1657 playerEntity) {
      if (playerEntity == null) {
         return class_1799.field_8037;
      } else {
         class_1799 var10000;
         switch (i) {
            case 4:
               var10000 = playerEntity.method_6079();
               break;
            case 5:
               var10000 = playerEntity.method_6047();
               break;
            default:
               var10000 = playerEntity.method_31548().method_7372(i);
         }

         return var10000;
      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      Iterator var2 = this.players.iterator();

      while(var2.hasNext()) {
         Entry player = (Entry)var2.next();
         player.render3D(event);
      }

   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      Iterator var2 = this.players.iterator();

      while(var2.hasNext()) {
         Entry player = (Entry)var2.next();
         player.render2D();
      }

   }

   private class Entry {
      public final double x;
      public final double y;
      public final double z;
      public final double xWidth;
      public final double zWidth;
      public final double halfWidth;
      public final double height;
      public final TimerUtils passed = new TimerUtils();
      public final UUID uuid;
      public final String name;
      public final int health;
      public final int maxHealth;
      public final String healthText;
      class_1657 entity;

      public Entry(class_1657 entity) {
         if ((Boolean)LogSpots.this.notification.get()) {
            LogSpots.this.info(entity.method_5820() + " Just logged out!", new Object[0]);
         }

         this.passed.reset();
         this.halfWidth = (double)(entity.method_17681() / 2.0F);
         this.x = entity.method_23317() - this.halfWidth;
         this.y = entity.method_23318();
         this.z = entity.method_23321() - this.halfWidth;
         this.xWidth = entity.method_5829().method_17939();
         this.zWidth = entity.method_5829().method_17941();
         this.height = entity.method_5829().method_17940();
         this.entity = entity;
         this.uuid = entity.method_5667();
         this.name = entity.method_5820();
         this.health = Math.round(entity.method_6032() + entity.method_6067());
         this.maxHealth = Math.round(entity.method_6063() + entity.method_6067());
         this.healthText = " " + this.health;
      }

      public void render3D(Render3DEvent event) {
         WireframeEntityRenderer.render(event, this.entity, (Double)LogSpots.this.scale.get(), (Color)LogSpots.this.sideColor.get(), (Color)LogSpots.this.lineColor.get(), (ShapeMode)LogSpots.this.shapeMode.get());
      }

      public void render2D() {
         if (!(PlayerUtils.distanceToCamera(this.x, this.y, this.z) > (double)((Integer)LogSpots.this.mc.field_1690.method_42503().method_41753() * 16))) {
            TextRenderer text = TextRenderer.get();
            double s = (Double)LogSpots.this.scale.get();
            LogSpots.pos.set(this.x + this.halfWidth, this.y + this.height + 0.5, this.z + this.halfWidth);
            if (NametagUtils.to2D(LogSpots.pos, s)) {
               NametagUtils.begin(LogSpots.pos);
               String content = "";
               if ((Boolean)LogSpots.this.nameRender.get()) {
                  content = content + this.name;
               }

               if ((Boolean)LogSpots.this.healthRender.get()) {
                  content = content + " " + this.healthText + "HP";
               }

               if ((Boolean)LogSpots.this.coordRender.get()) {
                  content = content + " (" + Math.round(this.entity.method_23317()) + " " + Math.round(this.entity.method_23318()) + " " + Math.round(this.entity.method_23321()) + ")";
               }

               if ((Boolean)LogSpots.this.timePassed.get()) {
                  content = content + " " + this.passed.getPassedTimeMs() / 1000L + "s";
               }

               double i = text.getWidth(content) / 2.0;
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.quad(-i, 0.0, i * 2.0, text.getHeight(), (Color)LogSpots.this.nameBackgroundColor.get());
               Renderer2D.COLOR.render((class_4587)null);
               text.beginBig();
               if ((Boolean)LogSpots.this.nameRender.get()) {
                  text.render(content, -i, 0.0, (Color)LogSpots.this.nameColor.get());
               }

               text.end();
               NametagUtils.end();
            }
         }
      }
   }
}
