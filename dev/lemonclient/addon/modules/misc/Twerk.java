package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;

public class Twerk extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting mode;
   private final Setting speed;
   private boolean hasTwerked;
   private int timer;

   public Twerk() {
      super(LemonClient.Misc, "Twerk", "Automatically sex with other player. =w=");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Which method to sneak.")).defaultValue(Twerk.Mode.Vanilla)).build());
      this.speed = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("speed")).description("The speed of twerking.")).defaultValue(1)).min(1).sliderRange(1, 100).build());
      this.hasTwerked = false;
   }

   public void onActivate() {
      this.timer = 0;
   }

   public void onDeactivate() {
      this.hasTwerked = false;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ++this.timer;
      if (this.timer >= 10 - (Integer)this.speed.get()) {
         this.hasTwerked = !this.hasTwerked;
         this.timer = -1;
      }
   }

   public boolean doPacket() {
      return this.isActive() && this.hasTwerked && !this.mc.field_1724.method_31549().field_7479 && this.mode.get() == Twerk.Mode.Packet;
   }

   public boolean doVanilla() {
      return this.isActive() && this.hasTwerked && !this.mc.field_1724.method_31549().field_7479 && this.mode.get() == Twerk.Mode.Vanilla;
   }

   public static enum Mode {
      Packet("Packet"),
      Vanilla("Vanilla");

      private final String title;

      private Mode(String title) {
         this.title = title;
      }

      public String toString() {
         return this.title;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Packet, Vanilla};
      }
   }
}
