package dev.lemonclient.addon.utils.render;

import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class Renderer2DPlus {
   public static final double circleNone = 0.0;
   public static final double circleQuarter = 1.5707963267948966;
   public static final double circleHalf = Math.PI;
   public static final double circleThreeQuarter = 4.71238898038469;

   public static void quadRoundedOutline(double x, double y, double width, double height, Color color, double r, double s) {
      r = getR(r, width, height);
      if (r <= 0.0) {
         Renderer2D.COLOR.quad(x, y, width, s, color);
         Renderer2D.COLOR.quad(x, y + height - s, width, s, color);
         Renderer2D.COLOR.quad(x, y + s, s, height - s * 2.0, color);
         Renderer2D.COLOR.quad(x + width - s, y + s, s, height - s * 2.0, color);
      } else {
         circlePartOutline(x + r, y + r, r, 4.71238898038469, 1.5707963267948966, color, s);
         Renderer2D.COLOR.quad(x + r, y, width - r * 2.0, s, color);
         circlePartOutline(x + width - r, y + r, r, 0.0, 1.5707963267948966, color, s);
         Renderer2D.COLOR.quad(x, y + r, s, height - r * 2.0, color);
         Renderer2D.COLOR.quad(x + width - s, y + r, s, height - r * 2.0, color);
         circlePartOutline(x + width - r, y + height - r, r, 1.5707963267948966, 1.5707963267948966, color, s);
         Renderer2D.COLOR.quad(x + r, y + height - s, width - r * 2.0, s, color);
         circlePartOutline(x + r, y + height - r, r, Math.PI, 1.5707963267948966, color, s);
      }

   }

   public static void quadRounded(double x, double y, double width, double height, double r, Color color) {
      quadRounded(x, y, width, height, color, r, true);
   }

   public static void quadRounded(double x, double y, double width, double height, Color color, double r, boolean roundTop) {
      r = getR(r, width, height);
      if (r <= 0.0) {
         Renderer2D.COLOR.quad(x, y, width, height, color);
      } else {
         if (roundTop) {
            circlePart(x + r, y + r, r, 4.71238898038469, 1.5707963267948966, color);
            Renderer2D.COLOR.quad(x + r, y, width - 2.0 * r, r, color);
            circlePart(x + width - r, y + r, r, 0.0, 1.5707963267948966, color);
            Renderer2D.COLOR.quad(x, y + r, width, height - 2.0 * r, color);
         } else {
            Renderer2D.COLOR.quad(x, y, width, height - r, color);
         }

         circlePart(x + width - r, y + height - r, r, 1.5707963267948966, 1.5707963267948966, color);
         Renderer2D.COLOR.quad(x + r, y + height - r, width - 2.0 * r, r, color);
         circlePart(x + r, y + height - r, r, Math.PI, 1.5707963267948966, color);
      }

   }

   public static void quadRoundedSide(double x, double y, double width, double height, Color color, double r, boolean right) {
      r = getR(r, width, height);
      if (r <= 0.0) {
         Renderer2D.COLOR.quad(x, y, width, height, color);
      } else if (right) {
         circlePart(x + width - r, y + r, r, 0.0, 1.5707963267948966, color);
         circlePart(x + width - r, y + height - r, r, 1.5707963267948966, 1.5707963267948966, color);
         Renderer2D.COLOR.quad(x, y, width - r, height, color);
         Renderer2D.COLOR.quad(x + width - r, y + r, r, height - r * 2.0, color);
      } else {
         circlePart(x + r, y + r, r, 4.71238898038469, 1.5707963267948966, color);
         circlePart(x + r, y + height - r, r, Math.PI, 1.5707963267948966, color);
         Renderer2D.COLOR.quad(x + r, y, width - r, height, color);
         Renderer2D.COLOR.quad(x, y + r, r, height - r * 2.0, color);
      }

   }

   private static double getR(double r, double w, double h) {
      if (r * 2.0 > h) {
         r = h / 2.0;
      }

      if (r * 2.0 > w) {
         r = w / 2.0;
      }

      return r;
   }

   private static int getCirDepth(double r, double angle) {
      return Math.max(1, (int)(angle * r / 1.5707963267948966));
   }

   public static void circlePart(double x, double y, double r, double startAngle, double angle, Color color) {
      int cirDepth = getCirDepth(r, angle);
      double cirPart = angle / (double)cirDepth;
      int center = Renderer2D.COLOR.triangles.vec2(x, y).color(color).next();
      int prev = vecOnCircle(x, y, r, startAngle, color);

      for(int i = 1; i < cirDepth + 1; ++i) {
         int next = vecOnCircle(x, y, r, startAngle + cirPart * (double)i, color);
         Renderer2D.COLOR.triangles.triangle(prev, center, next);
         prev = next;
      }

   }

   public static void circlePartOutline(double x, double y, double r, double startAngle, double angle, Color color, double outlineWidth) {
      if (outlineWidth >= r) {
         circlePart(x, y, r, startAngle, angle, color);
      } else {
         int cirDepth = getCirDepth(r, angle);
         double cirPart = angle / (double)cirDepth;
         int innerPrev = vecOnCircle(x, y, r - outlineWidth, startAngle, color);
         int outerPrev = vecOnCircle(x, y, r, startAngle, color);

         for(int i = 1; i < cirDepth + 1; ++i) {
            int inner = vecOnCircle(x, y, r - outlineWidth, startAngle + cirPart * (double)i, color);
            int outer = vecOnCircle(x, y, r, startAngle + cirPart * (double)i, color);
            Renderer2D.COLOR.triangles.quad(inner, innerPrev, outerPrev, outer);
            innerPrev = inner;
            outerPrev = outer;
         }

      }
   }

   private static int vecOnCircle(double x, double y, double r, double angle, Color color) {
      return Renderer2D.COLOR.triangles.vec2(x + Math.sin(angle) * r, y - Math.cos(angle) * r).color(color).next();
   }
}
