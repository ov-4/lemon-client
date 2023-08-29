package dev.lemonclient.addon.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.lemonclient.addon.utils.player.PlayerArgumentType;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.class_1657;

public class UUIDCommand extends Command {
   public UUIDCommand() {
      super("uuid", "Returns a players uuid.", new String[0]);
   }

   public void build(LiteralArgumentBuilder builder) {
      builder.executes((context) -> {
         this.info("Your UUID is " + MeteorClient.mc.field_1724.method_5667().toString(), new Object[0]);
         return 1;
      });
      builder.then(argument("player", PlayerArgumentType.player()).executes((context) -> {
         class_1657 player = PlayerArgumentType.getPlayer(context, "player");
         if (player != null) {
            this.info(player.method_5820() + "'s UUID is " + player.method_5667().toString(), new Object[0]);
         }

         return 1;
      }));
   }
}
