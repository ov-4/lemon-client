package dev.lemonclient.addon.utils.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;

public class RenderInfo {
   public Render3DEvent event;
   public RenderUtils.RenderMode renderMode;
   public ShapeMode shapeMode;

   public RenderInfo(Render3DEvent event, RenderUtils.RenderMode renderMode, ShapeMode shapeMode) {
      this.event = event;
      this.renderMode = renderMode;
      this.shapeMode = shapeMode;
   }

   public RenderInfo(Render3DEvent event, RenderUtils.RenderMode renderMode) {
      this.event = event;
      this.renderMode = renderMode;
   }
}
