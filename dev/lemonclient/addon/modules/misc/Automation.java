package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.HoleType;
import dev.lemonclient.addon.modules.combat.SurroundPlus;
import dev.lemonclient.addon.utils.world.hole.HoleUtils;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_2338;

public class Automation extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting holeSurround;
   private class_2338 lastPos;
   private SurroundPlus surround;

   public Automation() {
      super(LemonClient.Misc, "Automation", "Automatically enables modules in certain situations.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.holeSurround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Hole Surround")).description("Enables surround when you enter a hole.")).defaultValue(true)).build());
      this.lastPos = null;
      this.surround = null;
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.surround == null) {
            this.surround = (SurroundPlus)Modules.get().get(SurroundPlus.class);
         }

         if (!this.mc.field_1724.method_24515().equals(this.lastPos) && this.inAHole(this.mc.field_1724) && (Boolean)this.holeSurround.get() && !this.surround.isActive()) {
            this.surround.toggle();
            this.surround.sendToggledMsg("enabled by Automation");
         }

         this.lastPos = this.mc.field_1724.method_24515();
      }
   }

   private boolean inAHole(class_1657 player) {
      class_2338 pos = player.method_24515();
      if (HoleUtils.getHole(pos, 1).type == HoleType.Single) {
         return true;
      } else if (HoleUtils.getHole(pos, 1).type != HoleType.DoubleX && HoleUtils.getHole(pos.method_10069(-1, 0, 0), 1).type != HoleType.DoubleX) {
         if (HoleUtils.getHole(pos, 1).type != HoleType.DoubleZ && HoleUtils.getHole(pos.method_10069(0, 0, -1), 1).type != HoleType.DoubleZ) {
            return HoleUtils.getHole(pos, 1).type == HoleType.Quad || HoleUtils.getHole(pos.method_10069(-1, 0, -1), 1).type == HoleType.Quad || HoleUtils.getHole(pos.method_10069(-1, 0, 0), 1).type == HoleType.Quad || HoleUtils.getHole(pos.method_10069(0, 0, -1), 1).type == HoleType.Quad;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }
}
