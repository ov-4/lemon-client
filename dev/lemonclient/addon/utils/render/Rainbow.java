package dev.lemonclient.addon.utils.render;

import meteordevelopment.meteorclient.utils.render.color.Color;

public class Rainbow {
   public static int getRainbow(float speed, float saturation, float brightness) {
      float hue = (float)(System.currentTimeMillis() % 11520L) / 11520.0F * speed;
      return (new Color(java.awt.Color.HSBtoRGB(hue, saturation, brightness))).getPacked();
   }

   public static Color getRainbowColor(float speed, float saturation, float brightness) {
      return new Color(getRainbow(speed, saturation, brightness));
   }

   public static Color getRainbowColor(float speed, float saturation, float brightness, long add) {
      return new Color(getRainbow(speed, saturation, brightness, add));
   }

   public static int getRainbow(float speed, float saturation, float brightness, long add) {
      float hue = (float)((System.currentTimeMillis() + add) % 11520L) / 11520.0F * speed;
      return (new Color(java.awt.Color.HSBtoRGB(hue, saturation, brightness))).getPacked();
   }
}
