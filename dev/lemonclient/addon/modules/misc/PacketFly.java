package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.LemonUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2708;
import net.minecraft.class_2793;
import net.minecraft.class_2828;

public class PacketFly extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgFly;
   private final SettingGroup sgPhase;
   private final Setting onGroundSpoof;
   private final Setting onGround;
   private final Setting xzBound;
   private final Setting yBound;
   private final Setting antiKick;
   private final Setting antiKickDelay;
   private final Setting packets;
   private final Setting speed;
   private final Setting fastVertical;
   private final Setting downSpeed;
   private final Setting upSpeed;
   private final Setting phasePackets;
   private final Setting phaseSpeed;
   private final Setting phaseFastVertical;
   private final Setting phaseDownSpeed;
   private final Setting phaseUpSpeed;
   private int ticks;
   private int id;
   private int sent;
   private int rur;
   private int packetsToSend;
   private final Random random;
   private String info;
   private Map validPos;
   private final List validPackets;
   public boolean moving;

   public PacketFly() {
      super(LemonClient.Misc, "Packet Fly", "Use packet to fly.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgFly = this.settings.createGroup("Fly");
      this.sgPhase = this.settings.createGroup("Phase");
      this.onGroundSpoof = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("On Ground Spoof")).description("Spoofs on ground.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("On Ground")).description("Should we tell the server that you are on ground.")).defaultValue(false);
      Setting var10003 = this.onGroundSpoof;
      Objects.requireNonNull(var10003);
      this.onGround = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.xzBound = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("XZ Bound")).description("Bounds offset horizontally.")).defaultValue(512)).sliderRange(-1337, 1337).build());
      this.yBound = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Y Bound")).description("Bounds offset vertically.")).defaultValue(0)).sliderRange(-1337, 1337).build());
      this.antiKick = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Anti-Kick")).description("Slowly glides down.")).defaultValue(1.0).sliderRange(0.0, 10.0).build());
      this.antiKickDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Anti-Kick Delay")).description("Tick delay between moving anti kick packets.")).defaultValue(10)).min(1).sliderRange(0, 100).build());
      this.packets = this.sgFly.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Fly Packets")).description("How many packets to send every movement tick.")).defaultValue(1)).min(1).sliderRange(0, 10).build());
      this.speed = this.sgFly.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Fly Speed")).description("Distance to travel each packet.")).defaultValue(0.2873).min(0.0).sliderRange(0.0, 10.0).build());
      this.fastVertical = this.sgFly.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Fast Vertical Fly")).description("Sends multiple packets every movement tick while going up.")).defaultValue(false)).build());
      this.downSpeed = this.sgFly.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Fly Down Speed")).description("How fast to fly down.")).defaultValue(0.062).min(0.0).sliderRange(0.0, 10.0).build());
      this.upSpeed = this.sgFly.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Fly Up Speed")).description("How fast to fly up.")).defaultValue(0.062).min(0.0).sliderRange(0.0, 10.0).build());
      this.phasePackets = this.sgPhase.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Phase Packets")).description("How many packets to send every movement tick.")).defaultValue(1)).min(1).sliderRange(0, 10).build());
      this.phaseSpeed = this.sgPhase.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Phase Speed")).description("Distance to travel each packet.")).defaultValue(0.062).min(0.0).sliderRange(0.0, 10.0).build());
      this.phaseFastVertical = this.sgPhase.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Fast Vertical Phase")).description("Sends multiple packets every movement tick while going up.")).defaultValue(false)).build());
      this.phaseDownSpeed = this.sgPhase.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Phase Down Speed")).description("How fast to phase down.")).defaultValue(0.062).min(0.0).sliderRange(0.0, 10.0).build());
      this.phaseUpSpeed = this.sgPhase.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Phase Up Speed")).description("How fast to phase up.")).defaultValue(0.062).min(0.0).sliderRange(0.0, 10.0).build());
      this.ticks = 0;
      this.id = -1;
      this.sent = 0;
      this.rur = 0;
      this.packetsToSend = 0;
      this.random = new Random();
      this.info = null;
      this.validPos = new HashMap();
      this.validPackets = new ArrayList();
      this.moving = false;
   }

   public void onActivate() {
      super.onActivate();
      this.ticks = 0;
      this.validPos = new HashMap();
   }

   @EventHandler
   private void onTick(TickEvent.Post e) {
      ++this.ticks;
      ++this.rur;
      if (this.rur % 20 == 0) {
         this.info = "Packets: " + this.sent;
         this.sent = 0;
      }

   }

   @EventHandler
   private void onMove(PlayerMoveEvent e) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         boolean phasing = this.isPhasing();
         this.mc.field_1724.field_5960 = phasing;
         this.packetsToSend += phasing ? (Integer)this.phasePackets.get() : (Integer)this.packets.get();
         boolean shouldAntiKick = this.ticks % (Integer)this.antiKickDelay.get() == 0;
         double yaw = this.getYaw();
         double motion = phasing ? (Double)this.phaseSpeed.get() : (Double)this.speed.get();
         double x = 0.0;
         double y = 0.0;
         double z = 0.0;
         if (this.jumping()) {
            y = phasing ? (Double)this.phaseUpSpeed.get() : (Double)this.upSpeed.get();
         } else if (this.sneaking()) {
            y = phasing ? -(Double)this.phaseDownSpeed.get() : -(Double)this.downSpeed.get();
         }

         if (y != 0.0) {
            this.moving = false;
         }

         if (this.moving) {
            x = Math.cos(Math.toRadians(yaw + 90.0)) * motion;
            z = Math.sin(Math.toRadians(yaw + 90.0)) * motion;
         } else {
            if (phasing && !(Boolean)this.phaseFastVertical.get()) {
               this.packetsToSend = Math.min(this.packetsToSend, 1);
            }

            if (!phasing && !(Boolean)this.fastVertical.get()) {
               this.packetsToSend = Math.min(this.packetsToSend, 1);
            }
         }

         class_243 newPosition = new class_243(0.0, 0.0, 0.0);

         for(boolean antiKickSent = false; this.packetsToSend >= 1; --this.packetsToSend) {
            newPosition = newPosition.method_1031(x, 0.0, z);
            if (shouldAntiKick && !phasing && y >= 0.0 && !antiKickSent) {
               newPosition = newPosition.method_1031(0.0, (Double)this.antiKick.get() * -0.04, 0.0);
               antiKickSent = true;
            } else {
               newPosition = newPosition.method_1031(0.0, y, 0.0);
            }

            this.send(newPosition.method_1019(this.mc.field_1724.method_19538()), this.getBounds(), this.getOnGround());
            if (x == 0.0 && z == 0.0 && y == 0.0) {
               break;
            }
         }

         ((IVec3d)e.movement).set(newPosition.field_1352, newPosition.field_1351, newPosition.field_1350);
         this.packetsToSend = Math.min(this.packetsToSend, 1);
      }
   }

   @EventHandler
   public void onSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2828) {
         if (!this.validPackets.contains((class_2828)event.packet)) {
            event.cancel();
         } else {
            ++this.sent;
         }
      } else {
         ++this.sent;
      }

   }

   @EventHandler
   private void onReceive(PacketEvent.Receive e) {
      class_2596 var3 = e.packet;
      if (var3 instanceof class_2708 packet) {
         if (this.mc.field_1724 != null) {
            class_243 vec = new class_243(packet.method_11734(), packet.method_11735(), packet.method_11738());
            if (this.validPos.containsKey(packet.method_11737()) && ((class_243)this.validPos.get(packet.method_11737())).equals(vec)) {
               this.mc.field_1724.field_3944.method_2883(new class_2793(packet.method_11737()));
               e.cancel();
               this.validPos.remove(packet.method_11737());
               return;
            }

            this.id = packet.method_11737();
            this.mc.field_1724.field_3944.method_2883(new class_2793(packet.method_11737()));
         }
      }

   }

   public String getInfoString() {
      return this.info;
   }

   private class_243 getBounds() {
      int yaw = this.random.nextInt(0, 360);
      return new class_243(Math.cos(Math.toRadians((double)yaw)) * (double)(Integer)this.xzBound.get(), (double)(Integer)this.yBound.get(), Math.sin(Math.toRadians((double)yaw)) * (double)(Integer)this.xzBound.get());
   }

   private boolean getOnGround() {
      return (Boolean)this.onGroundSpoof.get() ? (Boolean)this.onGround.get() : this.mc.field_1724.method_24828();
   }

   private boolean isPhasing() {
      return LemonUtils.inside(this.mc.field_1724, this.mc.field_1724.method_5829());
   }

   private boolean jumping() {
      return this.mc.field_1690.field_1903.method_1434();
   }

   private boolean sneaking() {
      return this.mc.field_1690.field_1832.method_1434();
   }

   private void send(class_243 pos, class_243 bounds, boolean onGround) {
      class_2828.class_2829 normal = new class_2828.class_2829(pos.field_1352, pos.field_1351, pos.field_1350, onGround);
      class_2828.class_2829 bound = new class_2828.class_2829(pos.field_1352 + bounds.field_1352, pos.field_1351 + bounds.field_1351, pos.field_1350 + bounds.field_1350, onGround);
      this.validPackets.add(normal);
      this.sendPacket(normal);
      this.validPos.put(this.id + 1, pos);
      this.validPackets.add(bound);
      this.sendPacket(bound);
      if (this.id >= 0) {
         ++this.id;
         this.sendPacket(new class_2793(this.id));
      }
   }

   private double getYaw() {
      double f = (double)this.mc.field_1724.field_3913.field_3905;
      double s = (double)this.mc.field_1724.field_3913.field_3907;
      double yaw = (double)this.mc.field_1724.method_36454();
      if (f > 0.0) {
         this.moving = true;
         yaw += s > 0.0 ? -45.0 : (s < 0.0 ? 45.0 : 0.0);
      } else if (f < 0.0) {
         this.moving = true;
         yaw += s > 0.0 ? -135.0 : (s < 0.0 ? 135.0 : 180.0);
      } else {
         this.moving = s != 0.0;
         yaw += s > 0.0 ? -90.0 : (s < 0.0 ? 90.0 : 0.0);
      }

      return yaw;
   }
}
