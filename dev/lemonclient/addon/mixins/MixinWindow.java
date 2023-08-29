package dev.lemonclient.addon.mixins;

import com.google.common.collect.ImmutableMap;
import dev.lemonclient.addon.LemonClient;
import net.minecraft.class_1041;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_1041.class})
public abstract class MixinWindow {
   private static final ImmutableMap GLFW_OVERRIDE_VALUES;
   private static final ImmutableMap GLFW_HINT_NAMES;

   @Redirect(
      method = {"<init>"},
      at = @At(
   value = "INVOKE",
   target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V",
   remap = false
)
   )
   private void windowHintOverride(int hint, int value) {
      if (GLFW_OVERRIDE_VALUES.containsKey(hint)) {
         int original = value;
         String hintName = "Unknown";
         if (GLFW_HINT_NAMES.containsKey(hint)) {
            hintName = (String)GLFW_HINT_NAMES.get(hint);
         }

         value = (Integer)GLFW_OVERRIDE_VALUES.get(hint);
         LemonClient.log("Overriding " + hintName + ": " + original + " -> " + (value == -2 ? "None" : value));
         if (value == -2) {
            return;
         }
      }

      GLFW.glfwWindowHint(hint, value);
   }

   static {
      ImmutableMap.Builder overrideBuilder = ImmutableMap.builder();
      overrideBuilder.put(139266, 3);
      overrideBuilder.put(139267, 1);
      overrideBuilder.put(139272, 0);
      overrideBuilder.put(139270, 0);
      GLFW_OVERRIDE_VALUES = overrideBuilder.build();
      ImmutableMap.Builder nameBuilder = ImmutableMap.builder();
      nameBuilder.put(131073, "GLFW_FOCUSED");
      nameBuilder.put(131074, "GLFW_ICONIFIED");
      nameBuilder.put(131075, "GLFW_RESIZABLE");
      nameBuilder.put(131076, "GLFW_VISIBLE");
      nameBuilder.put(131077, "GLFW_DECORATED");
      nameBuilder.put(131078, "GLFW_AUTO_ICONIFY");
      nameBuilder.put(131079, "GLFW_FLOATING");
      nameBuilder.put(131080, "GLFW_MAXIMIZED");
      nameBuilder.put(131081, "GLFW_CENTER_CURSOR");
      nameBuilder.put(131082, "GLFW_TRANSPARENT_FRAMEBUFFER");
      nameBuilder.put(131083, "GLFW_HOVERED");
      nameBuilder.put(131084, "GLFW_FOCUS_ON_SHOW");
      nameBuilder.put(135169, "GLFW_RED_BITS");
      nameBuilder.put(135170, "GLFW_GREEN_BITS");
      nameBuilder.put(135171, "GLFW_BLUE_BITS");
      nameBuilder.put(135172, "GLFW_ALPHA_BITS");
      nameBuilder.put(135173, "GLFW_DEPTH_BITS");
      nameBuilder.put(135174, "GLFW_STENCIL_BITS");
      nameBuilder.put(135175, "GLFW_ACCUM_RED_BITS");
      nameBuilder.put(135176, "GLFW_ACCUM_GREEN_BITS");
      nameBuilder.put(135177, "GLFW_ACCUM_BLUE_BITS");
      nameBuilder.put(135178, "GLFW_ACCUM_ALPHA_BITS");
      nameBuilder.put(135179, "GLFW_AUX_BUFFERS");
      nameBuilder.put(135180, "GLFW_STEREO");
      nameBuilder.put(135181, "GLFW_SAMPLES");
      nameBuilder.put(135182, "GLFW_SRGB_CAPABLE");
      nameBuilder.put(135183, "GLFW_REFRESH_RATE");
      nameBuilder.put(135184, "GLFW_DOUBLEBUFFER");
      nameBuilder.put(139265, "GLFW_CLIENT_API");
      nameBuilder.put(139266, "GLFW_CONTEXT_VERSION_MAJOR");
      nameBuilder.put(139267, "GLFW_CONTEXT_VERSION_MINOR");
      nameBuilder.put(139268, "GLFW_CONTEXT_REVISION");
      nameBuilder.put(139269, "GLFW_CONTEXT_ROBUSTNESS");
      nameBuilder.put(139270, "GLFW_OPENGL_FORWARD_COMPAT");
      nameBuilder.put(139271, "GLFW_OPENGL_DEBUG_CONTEXT");
      nameBuilder.put(139272, "GLFW_OPENGL_PROFILE");
      nameBuilder.put(139273, "GLFW_CONTEXT_RELEASE_BEHAVIOR");
      nameBuilder.put(139274, "GLFW_CONTEXT_NO_ERROR");
      nameBuilder.put(139275, "GLFW_CONTEXT_CREATION_API");
      nameBuilder.put(139276, "GLFW_SCALE_TO_MONITOR");
      nameBuilder.put(143361, "GLFW_COCOA_RETINA_FRAMEBUFFER");
      nameBuilder.put(143362, "GLFW_COCOA_FRAME_NAME");
      nameBuilder.put(143363, "GLFW_COCOA_GRAPHICS_SWITCHING");
      nameBuilder.put(147457, "GLFW_X11_CLASS_NAME");
      nameBuilder.put(147458, "GLFW_X11_INSTANCE_NAME");
      GLFW_HINT_NAMES = nameBuilder.build();
   }
}
