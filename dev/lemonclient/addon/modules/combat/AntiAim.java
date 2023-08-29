package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.managers.Managers;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public class AntiAim extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgIgnore;
   private final Setting mode;
   private final Setting enemyRange;
   private final Setting spinSpeed;
   private final Setting rYaw;
   private final Setting rPitch;
   private final Setting csgoPitch;
   private final Setting csDelay;
   private final Setting yaw;
   private final Setting pitch;
   private final Setting bowMode;
   private final Setting encMode;
   private final Setting iYaw;
   private final Setting yItems;
   private final Setting iPitch;
   private final Setting pItems;
   private final Random r;
   private double spinYaw;
   private double csTick;
   private double csYaw;
   private double csPitch;

   public AntiAim() {
      super(LemonClient.Combat, "Anti Aim", "Funi conter stik module.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgIgnore = this.settings.createGroup("Ignore");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description(".")).defaultValue(AntiAim.Modes.Custom)).build());
      this.enemyRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Enemy Range")).description("Looks at players in the range.")).defaultValue(20.0).range(0.0, 1000.0).sliderMin(0.0).visible(() -> {
         return ((Modes)this.mode.get()).equals(AntiAim.Modes.Enemy);
      })).sliderMax(1000.0).build());
      this.spinSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Spin Speed")).description("How many degrees should be turned every tick.")).defaultValue(5.0).min(0.0).sliderMin(0.0).visible(() -> {
         return ((Modes)this.mode.get()).equals(AntiAim.Modes.Spin);
      })).sliderMax(100.0).build());
      this.rYaw = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Random Yaw")).description("Sets yaw to random value.")).defaultValue(true)).visible(() -> {
         return ((Modes)this.mode.get()).equals(AntiAim.Modes.CSGO);
      })).build());
      this.rPitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Random Pitch")).description("Sets pitch to random value.")).defaultValue(false)).visible(() -> {
         return ((Modes)this.mode.get()).equals(AntiAim.Modes.CSGO);
      })).build());
      this.csgoPitch = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("CS Pitch")).description("Sets pitch to this")).defaultValue(90)).range(-90, 90).sliderMin(-90).visible(() -> {
         return ((Modes)this.mode.get()).equals(AntiAim.Modes.CSGO) && !(Boolean)this.rPitch.get();
      })).sliderMax(90).build());
      this.csDelay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("CSGO Delay")).description("Tick delay between csgo rotation update.")).defaultValue(5.0).range(0.0, 100.0).sliderMin(0.0).visible(() -> {
         return ((Modes)this.mode.get()).equals(AntiAim.Modes.CSGO);
      })).sliderMax(100.0).build());
      this.yaw = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Yaw")).description("Sets yaw to this")).defaultValue(0)).range(-180, 180).sliderMin(-180).visible(() -> {
         return ((Modes)this.mode.get()).equals(AntiAim.Modes.Custom);
      })).sliderMax(180).build());
      this.pitch = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Pitch")).description("Sets pitch to this")).defaultValue(90)).range(-90, 90).sliderMin(-90).sliderMax(90).visible(() -> {
         return ((Modes)this.mode.get()).equals(AntiAim.Modes.Custom);
      })).build());
      this.bowMode = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Look Up With Bow")).description("Looks up while holding a bow.")).defaultValue(true)).build());
      this.encMode = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Look Down With Exp")).description("Looks down while holding experience bottles.")).defaultValue(true)).build());
      this.iYaw = this.sgIgnore.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore Yaw")).description("Doesn't change yaw when holding specific items.")).defaultValue(true)).build());
      this.yItems = this.sgIgnore.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("Ignore Yaw Items")).description("Ignores yaw rotations when holding these items.")).defaultValue(new class_1792[]{class_1802.field_8634, class_1802.field_8102, class_1802.field_8287}).build());
      this.iPitch = this.sgIgnore.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore Pitch")).description("Doesn't change pitch when holding specific items.")).defaultValue(true)).build());
      this.pItems = this.sgIgnore.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("Ignore Pitch items")).description("Ignores pitch rotations when holding these items.")).defaultValue(new class_1792[]{class_1802.field_8634, class_1802.field_8102, class_1802.field_8287}).build());
      this.r = new Random();
      this.csTick = 0.0;
   }

   public void onActivate() {
      super.onActivate();
      this.spinYaw = 0.0;
   }

   @EventHandler(
      priority = 200
   )
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.mode.get() == AntiAim.Modes.CSGO) {
            if (this.csTick <= 0.0) {
               this.csTick += (Double)this.csDelay.get();
               this.csYaw = (Boolean)this.rYaw.get() ? (double)this.r.nextInt(-180, 180) : (double)this.mc.field_1724.method_36454();
               this.csPitch = (Boolean)this.rPitch.get() ? (double)this.r.nextInt(-90, 90) : (double)(Integer)this.csgoPitch.get();
            } else {
               --this.csTick;
            }
         }

         class_1792 item = this.mc.field_1724.method_6047().method_7909();
         boolean ignoreYaw = ((List)this.yItems.get()).contains(item) && (Boolean)this.iYaw.get();
         boolean ignorePitch = ((List)this.pItems.get()).contains(item) && (Boolean)this.iPitch.get();
         double var10000;
         if (ignoreYaw) {
            var10000 = (double)this.mc.field_1724.method_36454();
         } else {
            switch ((Modes)this.mode.get()) {
               case Enemy:
                  var10000 = this.closestYaw();
                  break;
               case Spin:
                  var10000 = this.getSpinYaw();
                  break;
               case CSGO:
                  var10000 = this.csYaw;
                  break;
               case Custom:
                  var10000 = (double)(Integer)this.yaw.get();
                  break;
               default:
                  throw new IncompatibleClassChangeError();
            }
         }

         double y = var10000;
         if (item == class_1802.field_8287 && (Boolean)this.encMode.get()) {
            var10000 = 90.0;
         } else if (item == class_1802.field_8102 && (Boolean)this.bowMode.get()) {
            var10000 = -90.0;
         } else if (ignorePitch) {
            var10000 = (double)this.mc.field_1724.method_36455();
         } else {
            switch ((Modes)this.mode.get()) {
               case Enemy:
                  var10000 = this.closestPitch();
                  break;
               case Spin:
                  var10000 = 0.0;
                  break;
               case CSGO:
                  var10000 = this.csPitch;
                  break;
               case Custom:
                  var10000 = (double)(Integer)this.pitch.get();
                  break;
               default:
                  throw new IncompatibleClassChangeError();
            }
         }

         double p = var10000;
         Managers.ROTATION.start(y, p, (double)this.priority, RotationType.Other, (long)Objects.hash(new Object[]{this.name + "look"}));
      }

   }

   public String getInfoString() {
      return ((Modes)this.mode.get()).name();
   }

   private double closestYaw() {
      class_1657 closest = this.getClosest();
      return closest != null ? Rotations.getYaw(closest) : (double)this.mc.field_1724.method_36454();
   }

   private double closestPitch() {
      class_1657 closest = this.getClosest();
      return closest != null ? Rotations.getPitch(closest) : (double)this.mc.field_1724.method_36455();
   }

   private double getSpinYaw() {
      this.spinYaw += (Double)this.spinSpeed.get();
      return this.spinYaw;
   }

   private class_1657 getClosest() {
      class_1657 closest = null;
      Iterator var2 = this.mc.field_1687.method_18456().iterator();

      while(var2.hasNext()) {
         class_1657 pl = (class_1657)var2.next();
         if (pl != this.mc.field_1724 && !Friends.get().isFriend(pl)) {
            if (closest == null) {
               closest = pl;
            }

            double distance = this.mc.field_1724.method_19538().method_1022(pl.method_19538());
            if (!(distance > (Double)this.enemyRange.get()) && distance < closest.method_19538().method_1022(this.mc.field_1724.method_19538())) {
               closest = pl;
            }
         }
      }

      return closest;
   }

   public static enum Modes {
      Enemy,
      Spin,
      CSGO,
      Custom;

      // $FF: synthetic method
      private static Modes[] $values() {
         return new Modes[]{Enemy, Spin, CSGO, Custom};
      }
   }
}
