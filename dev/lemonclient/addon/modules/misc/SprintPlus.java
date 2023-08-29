package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;

public class SprintPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   public final Setting sprintMode;
   public final Setting hungerCheck;

   public SprintPlus() {
      super(LemonClient.Misc, "Sprint+", "Non shit sprint!");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sprintMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description("The method of sprinting.")).defaultValue(SprintPlus.SprintMode.Vanilla)).build());
      this.hungerCheck = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("HungerCheck")).description("Should we check if we have enough hunger to sprint")).defaultValue(true)).build());
   }

   @EventHandler(
      priority = 200
   )
   private void onTick(TickEvent.Pre event) {
      if (!ScaffoldPlus.shouldStopSprinting || !Modules.get().isActive(ScaffoldPlus.class)) {
         if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
            if ((Boolean)this.hungerCheck.get() && this.mc.field_1724.method_7344().method_7586() < 6) {
               this.mc.field_1724.method_5728(false);
               return;
            }

            switch ((SprintMode)this.sprintMode.get()) {
               case Vanilla:
                  if (this.mc.field_1690.field_1894.method_1434()) {
                     this.mc.field_1724.method_5728(true);
                  }
                  break;
               case Omni:
                  if (PlayerUtils.isMoving()) {
                     this.mc.field_1724.method_5728(true);
                  }
                  break;
               case Rage:
                  this.mc.field_1724.method_5728(true);
            }
         }

      }
   }

   public void onDeactivate() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.mc.field_1724.method_5728(false);
      }

   }

   public static enum SprintMode {
      Vanilla,
      Omni,
      Rage;

      // $FF: synthetic method
      private static SprintMode[] $values() {
         return new SprintMode[]{Vanilla, Omni, Rage};
      }
   }
}
