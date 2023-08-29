package dev.lemonclient.addon.managers.impl;

import dev.lemonclient.addon.modules.misc.PingSpoof;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2827;
import net.minecraft.class_6374;

public class PingSpoofManager {
   private final List delayed = new ArrayList();
   private DelayedPacket delayed1 = null;
   private DelayedPacket delayed2 = null;

   public PingSpoofManager() {
      MeteorClient.EVENT_BUS.subscribe(this);
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      List toSend = new ArrayList();
      if (this.delayed1 != null) {
         this.delayed.add(this.delayed1);
         this.delayed1 = null;
      }

      if (this.delayed2 != null) {
         this.delayed.add(this.delayed2);
         this.delayed2 = null;
      }

      Iterator var3 = this.delayed.iterator();

      while(var3.hasNext()) {
         DelayedPacket d = (DelayedPacket)var3.next();
         if (System.currentTimeMillis() > d.time) {
            toSend.add(d);
         }
      }

      toSend.forEach((dx) -> {
         MeteorClient.mc.method_1562().method_2883(dx.packet);
         this.delayed.remove(dx);
      });
      toSend.clear();
   }

   public void addKeepAlive(long id) {
      this.delayed1 = new DelayedPacket(new class_2827(id), System.currentTimeMillis() + (long)(Integer)((PingSpoof)Modules.get().get(PingSpoof.class)).ping.get());
   }

   public void addPong(int id) {
      this.delayed2 = new DelayedPacket(new class_6374(id), System.currentTimeMillis() + (long)(Integer)((PingSpoof)Modules.get().get(PingSpoof.class)).ping.get());
   }

   private static record DelayedPacket(class_2596 packet, long time) {
      private DelayedPacket(class_2596 packet, long time) {
         this.packet = packet;
         this.time = time;
      }

      public class_2596 packet() {
         return this.packet;
      }

      public long time() {
         return this.time;
      }
   }
}
