package dev.lemonclient.addon.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_310;
import net.minecraft.class_332;

public class MeteorSystem {
   public List meteorList;
   private boolean rainbow;

   public MeteorSystem(int initAmount, boolean rainbow) {
      this.meteorList = new ArrayList();
      this.addParticles(initAmount);
      this.rainbow = rainbow;
   }

   public MeteorSystem(int initAmount) {
      this(initAmount, false);
   }

   public void addParticles(int amount) {
      for(int i = 0; i < amount; ++i) {
         this.meteorList.add(LineMeteorRenderer.generateMeteor());
      }

   }

   public void tick() {
      Iterator var1 = this.meteorList.iterator();

      while(var1.hasNext()) {
         LineMeteorRenderer meteor = (LineMeteorRenderer)var1.next();
         meteor.tick();
      }

   }

   public void setRainbow(boolean rainbow) {
      this.rainbow = rainbow;
   }

   public void render(class_332 context) {
      if (class_310.method_1551().field_1755 != null) {
         this.meteorList.forEach((meteor) -> {
            Color color = this.rainbow ? meteor.randomColor : Color.WHITE;
            Renderer2D.COLOR.begin();
            RenderSystem.lineWidth(meteor.getLineWidth());
            Renderer2D.COLOR.line(meteor.getX(), meteor.getY(), meteor.getX2(), meteor.getY2(), new Color(color.r, color.g, color.b, (int)meteor.getAlpha()));
            Renderer2D.COLOR.render(context.method_51448());
         });
      }
   }
}
