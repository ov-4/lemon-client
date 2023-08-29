package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.Cancellable;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1743;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2879;
import net.minecraft.class_3966;

public class ShieldBypass extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting ignoreAxe;

   public ShieldBypass() {
      super(LemonClient.Misc, "Shield Bypass", "Attempts to teleport you behind enemies to bypass shields.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.ignoreAxe = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-axe")).description("Ignore if you are holding an axe.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onMouseButton(MouseButtonEvent event) {
      if (!Modules.get().isActive(KillAura.class)) {
         if (this.mc.field_1755 == null && !this.mc.field_1724.method_6115() && event.action == KeyAction.Press && event.button == 0) {
            class_239 var3 = this.mc.field_1765;
            if (var3 instanceof class_3966) {
               class_3966 result = (class_3966)var3;
               this.bypass(result.method_17782(), event);
            }
         }

      }
   }

   private boolean isBlocked(class_243 pos, class_1309 target) {
      class_243 vec3d3 = pos.method_1035(target.method_19538()).method_1029();
      return (new class_243(vec3d3.field_1352, 0.0, vec3d3.field_1350)).method_1026(target.method_5828(1.0F)) >= 0.0;
   }

   public void bypass(class_1297 target, Cancellable event) {
      if (target instanceof class_1309 e) {
         if (e.method_6039()) {
            if ((Boolean)this.ignoreAxe.get() && InvUtils.testInMainHand((ix) -> {
               return ix.method_7909() instanceof class_1743;
            })) {
               return;
            }

            if (this.isBlocked(this.mc.field_1724.method_19538(), e)) {
               return;
            }

            class_243 offset = class_243.method_1030(0.0F, this.mc.field_1724.method_36454()).method_1029().method_1021(2.0);
            class_243 newPos = e.method_19538().method_1019(offset);
            boolean inside = false;

            for(float i = 0.0F; i < 4.0F; i = (float)((double)i + 0.25)) {
               class_243 targetPos = newPos.method_1031(0.0, (double)i, 0.0);
               boolean collides = !this.mc.field_1687.method_8587((class_1297)null, e.method_5829().method_997(offset).method_997(targetPos.method_1020(newPos)));
               if (!inside && collides) {
                  inside = true;
               } else if (inside && !collides) {
                  newPos = targetPos;
                  break;
               }
            }

            if (!this.isBlocked(newPos, e)) {
               return;
            }

            event.cancel();
            this.mc.method_1562().method_2883(new class_2828.class_2829(newPos.method_10216(), newPos.method_10214(), newPos.method_10215(), true));
            this.mc.method_1562().method_2883(class_2824.method_34206(e, this.mc.field_1724.method_5715()));
            this.mc.method_1562().method_2883(new class_2879(this.mc.field_1724.method_6058()));
            this.mc.field_1724.method_7350();
            this.mc.method_1562().method_2883(new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321(), true));
         }
      }

   }
}
