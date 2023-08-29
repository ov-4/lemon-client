package dev.lemonclient.addon.utils.player;

import dev.lemonclient.addon.modules.info.AutoEz;
import dev.lemonclient.addon.modules.info.KillEffects;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import java.util.ArrayList;
import java.util.Iterator;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_640;

public class DeathUtils {
   private static final int DeathStatus = 3;

   @PreInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(DeathUtils.class);
   }

   @EventHandler(
      priority = 200
   )
   private static void onPacket(PacketEvent.Receive event) {
      if (!getTargets().isEmpty()) {
         class_2596 var2 = event.packet;
         if (var2 instanceof class_2663) {
            class_2663 packet = (class_2663)var2;
            if (packet.method_11470() == 3) {
               class_1297 entity = packet.method_11469(MeteorClient.mc.field_1687);
               if (entity != null) {
                  if (entity instanceof class_1657) {
                     class_1657 player = (class_1657)entity;
                     if (getTargets().contains(EntityInfo.getName(player))) {
                        ((AutoEz)Modules.get().get(AutoEz.class)).onKill(player);
                        ((KillEffects)Modules.get().get(KillEffects.class)).onKill(player);
                     }
                  }

               }
            }
         }
      }
   }

   public static ArrayList getTargets() {
      ArrayList list = new ArrayList();
      Iterator var1 = Modules.get().getAll().iterator();

      while(var1.hasNext()) {
         Module module = (Module)var1.next();
         String name = module.getInfoString();
         if (module.isActive() && name != null && !list.contains(name)) {
            list.add(name);
         }
      }

      try {
         list.removeIf((namex) -> {
            return !isName(namex);
         });
      } catch (Exception var4) {
         var4.fillInStackTrace();
      }

      return list;
   }

   private static boolean isName(String string) {
      ArrayList playerListEntries = new ArrayList(MeteorClient.mc.method_1562().method_2880());
      Iterator var2 = playerListEntries.iterator();

      class_640 entry;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entry = (class_640)var2.next();
      } while(!string.contains(entry.method_2966().getName()));

      return true;
   }
}
