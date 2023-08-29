package dev.lemonclient.addon.mixins;

import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.utils.render.MSAAFramebuffer;
import dev.lemonclient.addon.utils.render.MeteorSystem;
import dev.lemonclient.addon.utils.timers.MSTimer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiKeyEvents;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiDebugRenderer;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WidgetScreen.class})
public abstract class MixinWidgetScreen {
   @Shadow
   @Final
   private static GuiRenderer RENDERER;
   @Shadow
   public double animProgress;
   @Shadow
   @Final
   private WContainer root;
   @Shadow
   private boolean debug;
   @Shadow
   @Final
   private static GuiDebugRenderer DEBUG_RENDERER;
   @Shadow
   @Final
   protected GuiTheme theme;
   private final MeteorSystem meteorSystem = new MeteorSystem(30);
   private final MSTimer timer = new MSTimer();

   @Shadow
   protected abstract void runAfterRenderTasks();

   @Shadow
   protected abstract void onRenderBefore(class_332 var1, float var2);

   public MixinWidgetScreen() {
      this.timer.reset();
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      WidgetScreen screen = (WidgetScreen)this;
      if (!Utils.canUpdate()) {
         screen.method_25420(context);
      }

      double s = MeteorClient.mc.method_22683().method_4495();
      mouseX = (int)((double)mouseX * s);
      mouseY = (int)((double)mouseY * s);
      this.animProgress += (double)(delta / 20.0F * 14.0F);
      this.animProgress = class_3532.method_15350(this.animProgress, 0.0, 1.0);
      GuiKeyEvents.canUseKeys = true;
      Utils.unscaledProjection();
      this.onRenderBefore(context, delta);
      RENDERER.theme = this.theme;
      this.theme.beforeRender();
      Runnable task = () -> {
         boolean a = false;
         GuiTheme patt2971$temp = this.theme;
         if (patt2971$temp instanceof LemonClientGuiTheme t) {
            if ((Boolean)t.meteorRainbow.get()) {
               a = true;
            }
         }

         this.meteorSystem.setRainbow(a);
         this.meteorSystem.tick();
         this.meteorSystem.render(context);
         RENDERER.begin(context);
         RENDERER.setAlpha(this.animProgress);
         this.root.render(RENDERER, (double)mouseX, (double)mouseY, (double)(delta / 20.0F));
         RENDERER.setAlpha(1.0);
         RENDERER.end();
         boolean tooltip = RENDERER.renderTooltip(context, (double)mouseX, (double)mouseY, (double)(delta / 20.0F));
         if (this.debug) {
            class_4587 matrices = context.method_51448();
            DEBUG_RENDERER.render(this.root, matrices);
            if (tooltip) {
               DEBUG_RENDERER.render(RENDERER.tooltipWidget, matrices);
            }
         }

      };
      if (GuiThemes.get() instanceof LemonClientGuiTheme) {
         MSAAFramebuffer.use(task);
      } else {
         task.run();
      }

      Utils.scaledProjection();
      this.runAfterRenderTasks();
      ci.cancel();
   }
}
