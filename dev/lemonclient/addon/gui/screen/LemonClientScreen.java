package dev.lemonclient.addon.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.utils.Version;
import dev.lemonclient.addon.utils.render.BezierCurve;
import dev.lemonclient.addon.utils.render.MSAAFramebuffer;
import dev.lemonclient.addon.utils.render.Renderer2DPlus;
import java.io.IOException;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.prompts.OkPrompt;
import meteordevelopment.meteorclient.utils.render.prompts.YesNoPrompt;
import net.minecraft.class_1074;
import net.minecraft.class_155;
import net.minecraft.class_156;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_426;
import net.minecraft.class_429;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_500;
import net.minecraft.class_526;

public class LemonClientScreen extends GuiRender {
   private BezierCurve aniCur = new BezierCurve(0.35, 0.1, 0.25, 1.0);
   private boolean loaded;
   private final double buttonHeight = 15.0;
   private final double buttonWidth = 92.0;
   private final double buttonOffset = 3.25;
   private final Color[] colors = new Color[]{new Color(0, 0, 0, 255), new Color(27, 52, 53, 170), new Color(255, 255, 240, 170)};
   private final String centerButton = "Alt Manager";
   private final String[] buttons = new String[]{"Singleplayer", "Multiplayer", "Alt Manager", "Options", "Languages", "Click GUI", "Quit Game"};
   private final class_2960 logo = new class_2960("lemon-client", "icons/icon_100x100.png");
   private final class_2960 bg = new class_2960("lemon-client", "background.png");
   private int ticks = 0;
   private double percent = 0.0;
   private double lastPercent;
   private boolean hov;
   double fontScale;

   public LemonClientScreen() {
      this.lastPercent = this.percent;
      this.fontScale = 0.6;
   }

   protected void method_25426() {
      this.ticks = 0;
      super.method_25426();
   }

   public void method_25393() {
      this.lastPercent = this.percent;
      if (this.hov) {
         this.ticks = 0;
      }

      if (this.ticks >= 5000) {
         if (!this.hov && this.loaded) {
            this.loaded = false;
         }
      } else {
         ++this.ticks;
      }

      super.method_25393();
   }

   public boolean method_25402(double mouseX, double mouseY, int button) {
      if (button == 2) {
         this.loaded = !this.loaded;
      }

      double centerA = (double)(this.field_22789 / 2);
      double centerB = (double)(this.field_22790 / 2);
      double centerX = centerA - 46.0;
      double centerY = centerB - 7.5;
      if (button == 0) {
         double y = (-36.5 + centerY) * this.percent;
         String[] var16 = this.buttons;
         int var17 = var16.length;

         for(int var18 = 0; var18 < var17; ++var18) {
            String name = var16[var18];
            boolean hovered = this.isMouseHoveringRect(centerX, y, 92.0, 15.0, mouseX, mouseY);
            if (hovered) {
               switch (name) {
                  case "Singleplayer":
                     MeteorClient.mc.method_1507(new class_526(this));
                     break;
                  case "Multiplayer":
                     if (!MeteorClient.mc.field_1690.field_21840) {
                        MeteorClient.mc.field_1690.field_21840 = true;
                        MeteorClient.mc.field_1690.method_1640();
                     }

                     class_437 screen = new class_500(this);
                     MeteorClient.mc.method_1507(screen);
                     break;
                  case "Alt Manager":
                     MeteorClient.mc.method_1507(GuiThemes.get().accountsScreen());
                     break;
                  case "Options":
                     MeteorClient.mc.method_1507(new class_429(this, MeteorClient.mc.field_1690));
                     break;
                  case "Languages":
                     MeteorClient.mc.method_1507(new class_426(this, MeteorClient.mc.field_1690, MeteorClient.mc.method_1526()));
                     break;
                  case "Click GUI":
                     ((Tab)Tabs.get().get(0)).openScreen(GuiThemes.get());
                     break;
                  case "Quit Game":
                     MeteorClient.mc.method_1490();
                     break;
                  default:
                     System.out.println(name + " Button Clicked");
               }
            }

            y += 18.25;
         }
      }

      return super.method_25402(mouseX, mouseY, button);
   }

   public double textWidth(String s) {
      TextRenderer text = TextRenderer.get();
      text.begin(this.fontScale);
      double width = text.getWidth(s);
      text.end();
      return width;
   }

   public double textHeight() {
      TextRenderer text = TextRenderer.get();
      text.begin(this.fontScale);
      double height = text.getHeight(false);
      text.end();
      return height;
   }

   public void text(String s, double x, double y, Color color) {
      TextRenderer text = TextRenderer.get();
      text.begin(this.fontScale);
      text.render(s, x, y, color);
      text.end();
   }

   public void centerText(String s, double x, double y, Color color) {
      TextRenderer text = TextRenderer.get();
      text.begin(this.fontScale);
      double fX = text.getWidth(s);
      double sX = x - fX / 2.0;
      text.render(s, sX, y, color);
      text.end();
   }

   public void draw(class_332 drawContext, int mouseX, int mouseY, float tickDelta) {
      if (Utils.firstTimeTitleScreen) {
         Utils.firstTimeTitleScreen = false;
         if (!LemonClient.VERSION.isZero()) {
            LemonClient.log("Checking latest version of Meteor Client");
            MeteorExecutor.execute(() -> {
               Version latest;
               try {
                  latest = Version.getLatest();
               } catch (IOException var3) {
                  throw new RuntimeException(var3);
               }

               if (latest.isHigherThan(LemonClient.VERSION)) {
                  YesNoPrompt.create().title("New Update").message("A new version of LemonClient has been released.").message("Your version: %s", new Object[]{LemonClient.VERSION}).message("Latest version: %s", new Object[]{latest}).message("Do you want to update?").onYes(() -> {
                     class_156.method_668().method_670("https://lemonclient.cn/");
                  }).onNo(() -> {
                     OkPrompt.create().title("Are you sure?").message("Using old versions of Meteor is not recommended").message("and could report in issues.").id("new-update-no").onOk(this::method_25419).show();
                  }).id("new-update").show();
               }

            });
         }
      }

      this.loaded = true;
      double centerA = (double)(this.field_22789 / 2);
      double centerB = (double)(this.field_22790 / 2);
      double centerX = centerA - 46.0;
      double centerY = centerB - 7.5;
      double Y = -36.5 + centerY;
      String[] var15 = this.buttons;
      int var16 = var15.length;

      for(int var17 = 0; var17 < var16; ++var17) {
         String var10000 = var15[var17];
         boolean hovered = this.isMouseHoveringRect(centerX, Y, 92.0, 18.25, (double)mouseX, (double)mouseY);
         this.hov = hovered;
         if (hovered && !this.loaded) {
            this.loaded = true;
         }

         Y += 18.25;
      }

      RenderSystem.texParameter(3553, 10241, 9729);
      RenderSystem.texParameter(3553, 10240, 9729);
      drawContext.method_25290(this.bg, 0, 0, 0.0F, 0.0F, this.field_22789, this.field_22790, this.field_22789, this.field_22790);
      if (this.loaded) {
         this.percent = this.aniCur.get(false, 12);
      } else {
         this.percent = this.aniCur.get(true, 12);
      }

      double fontHeight;
      if (this.percent != 0.0) {
         double logoScale = 40.0;
         double logoX = centerA - logoScale / 2.0;
         fontHeight = (-36.5 + centerY - 80.0) * (double)smoothTrans(this.lastPercent, this.percent);
         this.renderer.texture(this.logo, logoX, fontHeight + 30.0, logoScale, logoScale, Color.WHITE);
         boolean meshRender = true;
         if (!meshRender) {
            RenderSystem.setShaderTexture(0, this.logo);
            RenderSystem.texParameter(3553, 10241, 9729);
            RenderSystem.texParameter(3553, 10240, 9729);
            drawContext.method_25290(this.logo, (int)logoX, (int)fontHeight, 0.0F, 0.0F, (int)logoScale, (int)logoScale, (int)logoScale, (int)logoScale);
         }

         MSAAFramebuffer.use(() -> {
            double y = (-36.5 + centerY) * (double)smoothTrans(this.lastPercent, this.percent);
            String[] var11 = this.buttons;
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               String name = var11[var13];
               double buttonRoundRadius = 3.0;
               Renderer2D.COLOR.begin();
               boolean hovered = this.isMouseHoveringRect(centerX, y, 92.0, 15.0, (double)mouseX, (double)mouseY);
               Renderer2DPlus.quadRoundedOutline(centerX, y, 92.0, 15.0, hovered ? this.colors[2] : this.colors[1], buttonRoundRadius - 0.1, 0.5);
               Renderer2DPlus.quadRounded(centerX + 0.5, y + 0.5, 91.0, 14.0, buttonRoundRadius, this.colors[0]);
               Renderer2D.COLOR.render((class_4587)null);
               Color fontColor = Color.WHITE;
               double fY = y + 7.5 - this.textHeight() / 2.0;
               this.centerText(name, centerA, fY, fontColor);
               y += 18.25;
            }

         });
      }

      String copyright = "Copyright (c) LemonClient Development. Do not distribute!";
      String versionInfo = "Minecraft " + class_155.method_16673().method_48019();
      if (MeteorClient.mc.method_1530()) {
         versionInfo = versionInfo + " Demo";
      } else {
         versionInfo = versionInfo + ("release".equalsIgnoreCase(MeteorClient.mc.method_1547()) ? "" : "/" + MeteorClient.mc.method_1547());
      }

      if (class_310.method_24289().method_39029()) {
         versionInfo = versionInfo + class_1074.method_4662("menu.modded", new Object[0]);
      }

      String updateInfo = "(Latest)";
      String clientInfo = LemonClient.ADDON + " " + LemonClient.VERSION;
      fontHeight = this.textHeight();
      double textX = 0.2;
      double textOffset = fontHeight + 0.5;
      double textY = (double)this.field_22790 - textOffset + 1.0;
      this.text(copyright, (double)((float)textX), (double)((float)textY), Color.WHITE);
      textY -= textOffset;
      this.text(versionInfo, (double)((float)textX), (double)((float)textY), Color.WHITE);
      textY -= textOffset;
      this.text(clientInfo, (double)((float)textX), (double)((float)textY), Color.WHITE);
      double tempWidth = this.textWidth(clientInfo) + 0.5;
      this.text(updateInfo, tempWidth, textY, Color.GREEN);
      String clientDevInfo = LemonClient.ADDON + " is developed by Fin & WuMie";
      String userInfo = "Welcome, " + MeteorClient.mc.method_1548().method_1676();
      textX = (double)this.field_22789 - this.textWidth(clientDevInfo);
      textY = (double)this.field_22790 - textOffset + 1.0;
      this.text(clientDevInfo, (double)((float)textX), (double)((float)textY), Color.WHITE);
      textX = (double)this.field_22789 - this.textWidth(userInfo);
      textY -= textOffset;
      this.text(userInfo, (double)((float)textX), (double)((float)textY), Color.WHITE);
   }

   public static float smoothTrans(double current, double last) {
      return (float)(current * (double)MeteorClient.mc.method_1488() + last * (double)(1.0F - MeteorClient.mc.method_1488()));
   }
}
