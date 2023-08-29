package dev.lemonclient.addon.utils.player;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1657;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class PlayerArgumentType implements ArgumentType {
   private static Collection EXAMPLES;
   private static final DynamicCommandExceptionType NO_SUCH_PLAYER;

   public static PlayerArgumentType player() {
      return new PlayerArgumentType();
   }

   public static class_1657 getPlayer(CommandContext context, String name) {
      return (class_1657)context.getArgument(name, class_1657.class);
   }

   public class_1657 parse(StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();

      while(reader.canRead() && this.isValidChar(reader.peek())) {
         reader.skip();
      }

      String argument = reader.getString().substring(start, reader.getCursor());
      class_1657 player = null;
      Iterator var5 = MeteorClient.mc.field_1687.method_18456().iterator();

      while(var5.hasNext()) {
         class_1657 entity = (class_1657)var5.next();
         if (entity.method_5820().equalsIgnoreCase(argument)) {
            player = entity;
            break;
         }
      }

      if (player == null) {
         throw NO_SUCH_PLAYER.create(argument);
      } else {
         return player;
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return class_2172.method_9264(MeteorClient.mc.field_1687.method_18456().stream().map(class_1657::method_5820), builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   private boolean isValidChar(char character) {
      return character != '\\' && (character >= '0' && character <= '9' || character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z' || character >= '!' && character <= '/' || character >= ':' && character <= '@' || character >= '[' && character <= '`' || character >= '{' && character <= '~');
   }

   static {
      if (MeteorClient.mc.field_1687 != null) {
         EXAMPLES = (Collection)MeteorClient.mc.field_1687.method_18456().stream().map(class_1657::method_5820).collect(Collectors.toList());
      }

      NO_SUCH_PLAYER = new DynamicCommandExceptionType((object) -> {
         return class_2561.method_43470("Player with name " + object + " doesn't exist.");
      });
   }
}
