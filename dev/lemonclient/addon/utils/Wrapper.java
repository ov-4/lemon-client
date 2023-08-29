package dev.lemonclient.addon.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.hwid.Hwid;
import dev.lemonclient.addon.utils.misc.Vec3dInfo;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_1011;
import net.minecraft.class_1157;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.class_641;
import net.minecraft.class_642;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Wrapper {
   public static void init() {
      setTitle(LemonClient.ADDON + " " + LemonClient.VERSION);
      setIcon();
      skipTutorial();
      Vec3dInfo.init();
      addServers();
      ChatUtils.registerCustomPrefix("dev.lemonclient.addon", Wrapper::getPrefix);
   }

   public static class_2561 getPrefix() {
      class_5250 logo = class_2561.method_43470("LemonClient");
      class_5250 prefix = class_2561.method_43470("");
      logo.method_10862(logo.method_10866().method_27706(class_124.field_1061));
      prefix.method_10862(prefix.method_10866().method_27706(class_124.field_1061));
      prefix.method_27693("[");
      prefix.method_10852(logo);
      prefix.method_27693("] ");
      return prefix;
   }

   private static void setIcon() {
      RenderSystem.assertInInitPhase();
      List list = List.of((InputStream)Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/lemon-client/icons/icon_16x16.png")), (InputStream)Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/lemon-client/icons/icon_32x32.png")), (InputStream)Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/lemon-client/icons/icon_48x48.png")), (InputStream)Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/lemon-client/icons/icon_128x128.png")), (InputStream)Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/lemon-client/icons/icon_256x256.png")));
      List list2 = new ArrayList(list.size());

      try {
         MemoryStack memoryStack = MemoryStack.stackPush();

         try {
            GLFWImage.Buffer buffer = GLFWImage.malloc(list.size(), memoryStack);

            for(int i = 0; i < list.size(); ++i) {
               class_1011 nativeImage = class_1011.method_4309((InputStream)list.get(i));

               try {
                  ByteBuffer byteBuffer = MemoryUtil.memAlloc(nativeImage.method_4307() * nativeImage.method_4323() * 4);
                  list2.add(byteBuffer);
                  byteBuffer.asIntBuffer().put(nativeImage.method_48463());
                  buffer.position(i);
                  buffer.width(nativeImage.method_4307());
                  buffer.height(nativeImage.method_4323());
                  buffer.pixels(byteBuffer);
               } catch (Throwable var18) {
                  try {
                     nativeImage.close();
                  } catch (Throwable var17) {
                     var18.addSuppressed(var17);
                  }

                  throw var18;
               }

               nativeImage.close();
            }

            GLFW.glfwSetWindowIcon(MeteorClient.mc.method_22683().method_4490(), (GLFWImage.Buffer)buffer.position(0));
         } catch (Throwable var19) {
            Throwable var20 = var19;

            try {
               memoryStack.close();
            } catch (Throwable var16) {
               var19.addSuppressed(var16);
            }

            try {
               throw var20;
            } catch (IOException var15) {
               throw new RuntimeException(var15);
            }
         }

         memoryStack.close();
      } finally {
         list2.forEach(MemoryUtil::memFree);
      }

   }

   public static void setTitle(String titleText) {
      Config.get().customWindowTitle.set(true);
      Config.get().customWindowTitleText.set(titleText);
      MeteorClient.mc.method_22683().method_24286(titleText);
      Hwid.get();
   }

   public static void skipTutorial() {
      MeteorClient.mc.method_1577().method_4910(class_1157.field_5653);
   }

   public static int randomNum(int min, int max) {
      return min + (int)(Math.random() * (double)(max - min + 1));
   }

   public static void addServers() {
      if (!Hwid.getBoolean()) {
         String var10000 = new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBmYWlsZWQhIEhXSUQ6IA=="));
         LemonClient.log(var10000 + Hwid.getValue());
         System.exit(1);
      } else {
         LemonClient.log(new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBzdWNjZXNzZnVsISBXZWxjb21lIHRvIHVzZSBMZW1vbkNsaWVudCE=")));
      }

      class_641 servers = new class_641(MeteorClient.mc);
      servers.method_2981();
      boolean b = false;

      for(int i = 0; i < servers.method_2984(); ++i) {
         class_642 server = servers.method_2982(i);
         if (server.field_3761.contains("pvp.obsserver.cn")) {
            b = true;
            break;
         }
      }

      if (!b) {
         servers.method_2988(new class_642("Test2B2TPvP", "pvp.obsserver.cn", false), false);
         servers.method_2987();
      }

   }
}
