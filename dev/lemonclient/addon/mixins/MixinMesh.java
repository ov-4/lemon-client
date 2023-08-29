package dev.lemonclient.addon.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Mesh;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Mesh.class})
public class MixinMesh {
   @Shadow
   public boolean depthTest;
   @Shadow
   private boolean rendering3D;
   @Shadow
   private boolean beganRendering;

   @Overwrite
   public void beginRender(class_4587 matrices) {
      GL.saveState();
      if (this.depthTest) {
         GL.enableDepth();
      } else {
         GL.disableDepth();
      }

      GL.enableBlend();
      GL.disableCull();
      GL.enableLineSmooth();
      if (this.rendering3D) {
         class_4587 matrixStack = RenderSystem.getModelViewStack();
         matrixStack.method_22903();
         if (matrices != null) {
            matrixStack.method_34425(matrices.method_23760().method_23761());
         }

         if (MeteorClient.mc.field_1687 != null) {
            class_243 cameraPos = MeteorClient.mc.field_1773.method_19418().method_19326();
            matrixStack.method_22904(0.0, -cameraPos.field_1351, 0.0);
         }
      }

      this.beganRendering = true;
   }
}
