package dev.lemonclient.addon.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lemonclient.addon.utils.world.BlockInfo;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import net.minecraft.class_327.class_6415;
import net.minecraft.class_5253.class_5254;
import org.joml.Matrix4f;

public class RenderUtils {
   private static final class_4597.class_4598 vertex = class_4597.method_22991(new class_287(2048));

   public static boolean visibleHeight(RenderMode renderMode) {
      return renderMode == RenderUtils.RenderMode.UpperSide || renderMode == RenderUtils.RenderMode.LowerSide;
   }

   public static boolean visibleSide(ShapeMode shapeMode) {
      return shapeMode == ShapeMode.Both || shapeMode == ShapeMode.Sides;
   }

   public static boolean visibleLine(ShapeMode shapeMode) {
      return shapeMode == ShapeMode.Both || shapeMode == ShapeMode.Lines;
   }

   public static void render(RenderInfo ri, class_2338 blockPos, Color sideColor, Color lineColor, double height) {
      if (!BlockInfo.isNull(blockPos)) {
         switch (ri.renderMode) {
            case Box:
               box(ri, blockPos, sideColor, lineColor);
               break;
            case UpperSide:
               side(ri, blockPos, sideColor, lineColor, RenderUtils.Side.Upper, height);
               break;
            case LowerSide:
               side(ri, blockPos, sideColor, lineColor, RenderUtils.Side.Lower, height);
               break;
            case Shape:
               shape(ri, blockPos, sideColor, lineColor);
               break;
            case Romb:
               romb(ri, blockPos, sideColor, lineColor, RenderUtils.Side.Default, height);
               break;
            case UpperRomb:
               romb(ri, blockPos, sideColor, lineColor, RenderUtils.Side.Upper, height);
         }

      }
   }

   private static void romb(RenderInfo ri, class_2338 blockPos, Color sideColor, Color lineColor, Side side, double height) {
      switch (side) {
         case Default:
            render(ri, blockPos, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 0.0, 0.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 0.0, 1.0, 0.0, 0.5, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 0.0, 1.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 0.5, 1.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 0.5, 1.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 1.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 1.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 1.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 1.0, -0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 1.0, 0.5, 0.0, 0.5, 0.5, 0.0, 0.5, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, -0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 1.0, 0.5, 0.0, 0.5, 0.5, 0.0, 0.5, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 1.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 1.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 1.0, 0.5, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 1.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 0.0, 0.5, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 0.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            break;
         case Upper:
            render(ri, blockPos, 0.0, 1.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 1.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.5, 1.0, 0.5, 0.5, 0.0, 0.0, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
            render(ri, blockPos, 0.0, 1.0, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, sideColor, lineColor, ri.shapeMode);
      }

   }

   private static void render(RenderInfo ri, class_2338 blockPos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color sideColor, Color lineColor, ShapeMode shapeMode) {
      class_243 vec3d = new class_243((double)blockPos.method_10263() + x, (double)blockPos.method_10264() + y, (double)blockPos.method_10260() + z);
      ri.event.renderer.side(vec3d.field_1352 + x1, vec3d.field_1351 + y1, vec3d.field_1350 + z1, vec3d.field_1352 + x2, vec3d.field_1351 + y2, vec3d.field_1350 + z2, vec3d.field_1352 + x3, vec3d.field_1351 + y3, vec3d.field_1350 + z3, vec3d.field_1352 + x4, vec3d.field_1351 + y4, vec3d.field_1350 + z4, sideColor, lineColor, shapeMode);
   }

   private static void line(RenderInfo ri, class_2338 blockPos, double x, double y, double z, double x1, double y1, double z1, Color lineColor) {
      class_243 vec3d = new class_243((double)blockPos.method_10263() + x, (double)blockPos.method_10264() + y, (double)blockPos.method_10260() + z);
      ri.event.renderer.line(vec3d.field_1352 + x, vec3d.field_1351 + y, vec3d.field_1350 + z, x1, y1, z1, lineColor);
   }

   private static void shape(RenderInfo ri, class_2338 blockPos, Color sideColor, Color lineColor) {
      if (!BlockInfo.getShape(blockPos).method_1110()) {
         render(ri, blockPos, BlockInfo.getBox(blockPos), sideColor, lineColor);
      }
   }

   private static void box(RenderInfo ri, class_2338 blockPos, Color sideColor, Color lineColor) {
      ri.event.renderer.box(blockPos, sideColor, lineColor, ri.shapeMode, 0);
   }

   private static void side(RenderInfo ri, class_2338 blockPos, Color sideColor, Color lineColor, Side side, double height) {
      double y = side == RenderUtils.Side.Upper ? (double)(blockPos.method_10264() + 1) : (double)blockPos.method_10264();
      ri.event.renderer.box((double)blockPos.method_10263(), (double)blockPos.method_10264() + height, (double)blockPos.method_10260(), (double)(blockPos.method_10263() + 1), y, (double)(blockPos.method_10260() + 1), sideColor, lineColor, ri.shapeMode, 0);
   }

   private static void render(RenderInfo ri, class_2338 blockPos, class_238 box, Color sideColor, Color lineColor) {
      ri.event.renderer.box((double)blockPos.method_10263() + box.field_1323, (double)blockPos.method_10264() + box.field_1322, (double)blockPos.method_10260() + box.field_1321, (double)blockPos.method_10263() + box.field_1320, (double)blockPos.method_10264() + box.field_1325, (double)blockPos.method_10260() + box.field_1324, sideColor, lineColor, ri.shapeMode, 0);
   }

   public static void thickRender(Render3DEvent event, class_2338 pos, ShapeMode mode, Color sideColor, Color sideColor2, Color lineColor, Color lineColor2, double lineSize) {
      double high = 1.0 - lineSize;
      if (mode == ShapeMode.Lines || mode == ShapeMode.Both) {
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260() + lineSize, lineColor, lineColor2);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + lineSize, (double)(pos.method_10264() + 1), (double)pos.method_10260(), lineColor, lineColor2);
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260() + lineSize, lineColor, lineColor2);
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + high, (double)(pos.method_10264() + 1), (double)pos.method_10260(), lineColor, lineColor2);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260() + high, lineColor, lineColor2);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263() + lineSize, (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), lineColor, lineColor2);
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260() + high, lineColor, lineColor2);
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263() + high, (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), lineColor, lineColor2);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + high, (double)pos.method_10260(), lineColor, lineColor);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10260() + lineSize, lineColor);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263(), (double)pos.method_10264() + high, (double)(pos.method_10260() + 1), lineColor, lineColor);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + lineSize, (double)(pos.method_10260() + 1), lineColor);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10264() + high, (double)(pos.method_10260() + 1), lineColor, lineColor);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10260() + high, lineColor);
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + high, (double)(pos.method_10260() + 1), lineColor, lineColor);
         event.renderer.quadHorizontal((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + high, (double)(pos.method_10260() + 1), lineColor);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + lineSize, (double)pos.method_10260(), lineColor2, lineColor2);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10260() + lineSize, lineColor2);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)pos.method_10264() + lineSize, (double)(pos.method_10260() + 1), lineColor2, lineColor2);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + lineSize, (double)(pos.method_10260() + 1), lineColor2);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10264() + lineSize, (double)(pos.method_10260() + 1), lineColor2, lineColor2);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10260() + high, lineColor2);
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + lineSize, (double)(pos.method_10260() + 1), lineColor2, lineColor2);
         event.renderer.quadHorizontal((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + high, (double)(pos.method_10260() + 1), lineColor2);
      }

      if (mode == ShapeMode.Sides || mode == ShapeMode.Both) {
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), sideColor, sideColor2);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), sideColor, sideColor2);
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), sideColor, sideColor2);
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), sideColor, sideColor2);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10260() + 1), sideColor);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10260() + 1), sideColor2);
      }

   }

   public static void thickUpperSide(Render3DEvent event, class_2338 pos, ShapeMode mode, Color sideColor, Color lineColor, double lineSize) {
      double high = 1.0 - lineSize;
      if (mode == ShapeMode.Lines || mode == ShapeMode.Both) {
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + high, (double)pos.method_10260(), lineColor, lineColor);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10260() + lineSize, lineColor);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263(), (double)pos.method_10264() + high, (double)(pos.method_10260() + 1), lineColor, lineColor);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + lineSize, (double)(pos.method_10260() + 1), lineColor);
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10264() + high, (double)(pos.method_10260() + 1), lineColor, lineColor);
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10260() + high, lineColor);
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + high, (double)(pos.method_10260() + 1), lineColor, lineColor);
         event.renderer.quadHorizontal((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + high, (double)(pos.method_10260() + 1), lineColor);
      }

      if (mode == ShapeMode.Sides || mode == ShapeMode.Both) {
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10260() + 1), sideColor);
      }

   }

   public static void rounded(class_4587 stack, float x, float y, float w, float h, float radius, int p, int color) {
      Matrix4f matrix4f = stack.method_23760().method_23761();
      float a = (float)class_5254.method_27762(color) / 255.0F;
      float r = (float)class_5254.method_27765(color) / 255.0F;
      float g = (float)class_5254.method_27766(color) / 255.0F;
      float b = (float)class_5254.method_27767(color) / 255.0F;
      RenderSystem.enableBlend();
      RenderSystem.setShader(class_757::method_34540);
      class_287 bufferBuilder = class_289.method_1348().method_1349();
      bufferBuilder.method_1328(class_5596.field_27381, class_290.field_1576);
      corner(x + w, y, radius, 360, (float)p, r, g, b, a, bufferBuilder, matrix4f);
      corner(x, y, radius, 270, (float)p, r, g, b, a, bufferBuilder, matrix4f);
      corner(x, y + h, radius, 180, (float)p, r, g, b, a, bufferBuilder, matrix4f);
      corner(x + w, y + h, radius, 90, (float)p, r, g, b, a, bufferBuilder, matrix4f);
      class_286.method_43433(bufferBuilder.method_1326());
      RenderSystem.disableBlend();
   }

   public static void corner(float x, float y, float radius, int angle, float p, float r, float g, float b, float a, class_287 bufferBuilder, Matrix4f matrix4f) {
      for(float i = (float)angle; i > (float)(angle - 90); i -= 90.0F / p) {
         bufferBuilder.method_22918(matrix4f, (float)((double)x + Math.cos(Math.toRadians((double)i)) * (double)radius), (float)((double)y + Math.sin(Math.toRadians((double)i)) * (double)radius), 0.0F).method_22915(r, g, b, a).method_1344();
      }

   }

   public static void text(String text, class_4587 stack, float x, float y, int color) {
      MeteorClient.mc.field_1772.method_27521(text, x, y, color, false, stack.method_23760().method_23761(), vertex, class_6415.field_33993, 0, 15728880);
      vertex.method_22993();
   }

   public static void quad(class_4587 stack, float x, float y, float w, float h, int color) {
      Matrix4f matrix4f = stack.method_23760().method_23761();
      float a = (float)class_5254.method_27762(color) / 255.0F;
      float r = (float)class_5254.method_27765(color) / 255.0F;
      float g = (float)class_5254.method_27766(color) / 255.0F;
      float b = (float)class_5254.method_27767(color) / 255.0F;
      RenderSystem.enableBlend();
      RenderSystem.setShader(class_757::method_34540);
      class_287 bufferBuilder = class_289.method_1348().method_1349();
      bufferBuilder.method_1328(class_5596.field_27381, class_290.field_1576);
      bufferBuilder.method_22918(matrix4f, x + w, y, 0.0F).method_22915(r, g, b, a).method_1344();
      bufferBuilder.method_22918(matrix4f, x, y, 0.0F).method_22915(r, g, b, a).method_1344();
      bufferBuilder.method_22918(matrix4f, x, y + h, 0.0F).method_22915(r, g, b, a).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w, y + h, 0.0F).method_22915(r, g, b, a).method_1344();
      class_286.method_43433(bufferBuilder.method_1326());
      RenderSystem.disableBlend();
   }

   public static double easeInOutQuad(double x) {
      double percent;
      if (x < 0.5) {
         percent = 2.0 * x * x;
      } else {
         percent = 1.0;
         double ii = -2.0 * x + 2.0;
         byte i = 2;
         percent -= Math.pow(ii, (double)i) / 2.0;
      }

      return percent;
   }

   public static void drawSigma(Renderer3D renderer, class_4587 matrices, class_1297 entity, Color lineColor) {
      int everyTime = 3000;
      int drawTime = (int)(System.currentTimeMillis() % (long)everyTime);
      boolean drawMode = drawTime > everyTime / 2;
      double drawPercent = (double)drawTime / ((double)everyTime / 2.0);
      if (drawMode) {
         --drawPercent;
      } else {
         drawPercent = 1.0 - drawPercent;
      }

      drawPercent = easeInOutQuad(drawPercent);
      matrices.method_22903();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      class_238 entitybb = entity.method_5829();
      double radius = (entitybb.field_1320 - entitybb.field_1323 + (entitybb.field_1324 - entitybb.field_1321)) * 0.5;
      double height = entitybb.field_1325 - entitybb.field_1322;
      double x = entity.field_6038 + (entity.method_23317() - entity.field_6038);
      double z = entity.field_5989 + (entity.method_23321() - entity.field_5989);
      double y = entity.field_5971 + (entity.method_23318() - entity.field_5971) + height * drawPercent;
      drawCircle(renderer, matrices, x, z, x, z, y, radius, lineColor);
      RenderSystem.disableBlend();
      matrices.method_22909();
   }

   public static void drawCircle(Renderer3D renderer, class_4587 matrices, double x1, double z1, double x2, double z2, double y, double radius, Color color) {
      matrices.method_22903();

      for(int i = 5; i <= 360; ++i) {
         double MPI = Math.PI;
         double x = x1 - Math.sin((double)i * MPI / 180.0) * radius;
         double z = z1 + Math.cos((double)i * MPI / 180.0) * radius;
         double xx = x2 - Math.sin((double)(i - 5) * MPI / 180.0) * radius;
         double zz = z2 + Math.cos((double)(i - 5) * MPI / 180.0) * radius;
         renderer.line(x, y, z, xx, y, zz, color);
      }

      matrices.method_22909();
   }

   public static enum RenderMode {
      Box,
      Smooth,
      UpperSide,
      LowerSide,
      Shape,
      Romb,
      UpperRomb,
      None;

      // $FF: synthetic method
      private static RenderMode[] $values() {
         return new RenderMode[]{Box, Smooth, UpperSide, LowerSide, Shape, Romb, UpperRomb, None};
      }
   }

   public static enum Side {
      Default,
      Upper,
      Lower;

      // $FF: synthetic method
      private static Side[] $values() {
         return new Side[]{Default, Upper, Lower};
      }
   }

   public static enum Render {
      Meteor,
      LemonClient,
      None;

      // $FF: synthetic method
      private static Render[] $values() {
         return new Render[]{Meteor, LemonClient, None};
      }
   }
}
