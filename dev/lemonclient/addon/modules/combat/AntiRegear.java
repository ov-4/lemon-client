package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.network.PacketUtils;
import dev.lemonclient.addon.utils.others.Task;
import dev.lemonclient.addon.utils.player.Interaction;
import dev.lemonclient.addon.utils.world.BlockInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.PlaceBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2336;
import net.minecraft.class_2338;
import net.minecraft.class_2480;
import net.minecraft.class_2680;

public class AntiRegear extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting radius;
   private final Setting own;
   private final Setting render;
   private final Setting shapeMode;
   private final Setting sideColor;
   private final Setting lineColor;
   private final ArrayList ownBlocks;
   private FindItemResult tool;
   private class_2338 currentPos;
   private class_2680 currentState;
   private int timer;
   private final Task mine;
   private final PacketUtils packetMine;

   public AntiRegear() {
      super(LemonClient.Combat, "Anti Regear", "Automatically breaks shulkers and EChests which was placed by enemy.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.radius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("radius")).description("The radius of the sphere around you.")).defaultValue(5)).sliderRange(1, 10).build());
      this.own = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("own")).description("Whether or not to break your own blocks.")).defaultValue(false)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Render")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts should be renderer.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(123, 123, 123, 160)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(123, 123, 123, 160)).build());
      this.ownBlocks = new ArrayList();
      this.mine = new Task();
      this.packetMine = new PacketUtils();
   }

   public void onActivate() {
      this.timer = 0;
      this.currentPos = null;
      this.currentState = null;
      this.ownBlocks.clear();
      this.mine.reset();
   }

   @EventHandler
   public void onPlace(PlaceBlockEvent event) {
      if (event.block instanceof class_2480 || event.block instanceof class_2336) {
         this.ownBlocks.add(event.blockPos);
      }

   }

   @EventHandler
   public void onTick(TickEvent.Pre event) {
      if (!this.getBlocks((Integer)this.radius.get()).isEmpty()) {
         if (this.currentPos != null) {
            this.tool = InvUtils.findFastestTool(this.currentState);
            if (!this.tool.found()) {
               return;
            }

            if (SettingUtils.shouldRotate(RotationType.Mining)) {
               Managers.ROTATION.start(this.currentPos, (double)this.priority, RotationType.Mining, (long)Objects.hash(new Object[]{this.name + "mining"}));
            }

            this.packetMine.mine(this.currentPos, this.mine);
            this.mc.field_1687.method_8517(this.mc.field_1724.method_5628(), this.currentPos, (int)(this.packetMine.getProgress() * 10.0) - 1);
            if (this.packetMine.isReadyOn(0.95)) {
               Interaction.updateSlot(this.tool.slot(), false);
            }

            boolean shouldStop = PlayerUtils.distanceTo(this.currentPos) > 5.0 || this.isBugged();
            if (BlockInfo.isAir(this.currentPos) || shouldStop) {
               if (shouldStop) {
                  this.packetMine.abortMining(this.currentPos);
               }

               this.currentPos = null;
               this.currentState = null;
               this.packetMine.reset();
               this.mine.reset();
            }
         } else {
            this.getBlocks((Integer)this.radius.get()).forEach((blockPos) -> {
               this.currentPos = blockPos;
               this.currentState = this.mc.field_1687.method_8320(this.currentPos);
            });
         }

      }
   }

   private ArrayList getBlocks(int radius) {
      ArrayList sphere = new ArrayList(BlockInfo.getSphere(this.mc.field_1724.method_24515(), radius, radius));
      ArrayList blocks = new ArrayList();
      Iterator var4 = sphere.iterator();

      while(true) {
         class_2338 blockPos;
         do {
            do {
               do {
                  if (!var4.hasNext()) {
                     blocks.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
                     return blocks;
                  }

                  blockPos = (class_2338)var4.next();
               } while(BlockInfo.isAir(blockPos));
            } while(!(Boolean)this.own.get() && this.ownBlocks.contains(blockPos));
         } while((blocks.contains(blockPos) || !(this.mc.field_1687.method_8320(blockPos).method_26204() instanceof class_2480)) && this.mc.field_1687.method_8320(blockPos).method_26204() != class_2246.field_10443);

         blocks.add(blockPos);
      }
   }

   private boolean isBugged() {
      if (!this.packetMine.isReady()) {
         return false;
      } else {
         ++this.timer;
         if (this.timer >= 10) {
            this.timer = 0;
            return true;
         } else {
            return false;
         }
      }
   }

   @EventHandler
   public void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         if (this.getBlocks(5).isEmpty()) {
            return;
         }

         this.getBlocks(5).forEach((blockPos) -> {
            event.renderer.box(blockPos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         });
      }

   }
}
