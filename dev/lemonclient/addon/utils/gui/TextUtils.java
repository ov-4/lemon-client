package dev.lemonclient.addon.utils.gui;

import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class TextUtils {
   public static double getWidth(String text, double scale) {
      TextRenderer font = TextRenderer.get();
      font.begin(scale);
      double w = font.getWidth(text);
      font.end();
      return w;
   }

   public static double getHeight(String text, double scale) {
      TextRenderer font = TextRenderer.get();
      font.begin(scale);
      double w = font.getWidth(text);
      font.end();
      return w;
   }

   public static void render(String text, double x, double y, Color color, boolean shadow, double scale) {
      TextRenderer font = TextRenderer.get();
      font.begin(scale);
      font.render(text, x, y, color, shadow);
      font.end();
   }

   public static void render(String text, double x, double y, Color color, double scale) {
      render(text, x, y, color, false, scale);
   }
}
