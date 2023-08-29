package dev.lemonclient.addon.managers.impl;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2828;

public class OnGroundManager {
   private boolean onGround;

   public OnGroundManager() {
      MeteorClient.EVENT_BUS.subscribe(this);
      this.onGround = false;
   }

   @EventHandler(
      priority = 200
   )
   private void onPacket(PacketEvent.Send event) {
      if (event.packet instanceof class_2828) {
         this.onGround = ((class_2828)event.packet).method_12273();
      }

   }

   public boolean isOnGround() {
      return this.onGround;
   }
}
