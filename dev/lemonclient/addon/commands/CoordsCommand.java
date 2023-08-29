package dev.lemonclient.addon.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;

public class CoordsCommand extends Command {
   public CoordsCommand() {
      super("coords", "Copies your coordinates to your clipboard.", new String[0]);
   }

   public void build(LiteralArgumentBuilder builder) {
      builder.executes((context) -> {
         if (MeteorClient.mc.field_1724 != null) {
            double var10000 = Math.floor(MeteorClient.mc.field_1724.method_23317());
            String text = "x: " + var10000 + "; y:" + Math.floor(MeteorClient.mc.field_1724.method_23318()) + "; z:" + Math.floor(MeteorClient.mc.field_1724.method_23321()) + ";";
            this.info("Succesfully copied your coordinates: \n" + text, new Object[0]);
            MeteorClient.mc.field_1774.method_1455(text);
         }

         return 1;
      });
   }
}
