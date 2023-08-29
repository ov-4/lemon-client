package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.misc.Suicide;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.CrystalAura;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1748;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_490;
import net.minecraft.class_2846.class_2847;

public class OffHandPlus extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgHealth;
   private final Setting onlyInInv;
   private final Setting itemMode;
   private final Setting gapMode;
   private final Setting swordMode;
   private final Setting safeSword;
   private final Setting delay;
   private final Setting strict;
   private final Setting hp;
   private final Setting safety;
   private final Setting safetyHealth;
   private double timer;
   private class_1792 item;
   private Suicide suicide;
   private AutoCrystalPlus autoCrystalRewrite;
   private CrystalAura crystalAura;
   private AutoMine autoMine;
   private long lastTime;

   public OffHandPlus() {
      super(LemonClient.Combat, "OffHand+", "Better offhand.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgHealth = this.settings.createGroup("Health");
      this.onlyInInv = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Only in inventory")).description("Will only switch if you are in your inventory.")).defaultValue(false)).build());
      this.itemMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Item Mode")).description("Which item should be held in offhand.")).defaultValue(OffHandPlus.ItemMode.Totem)).build());
      this.gapMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Gapple Mode")).description("When should we hold golden apples.")).defaultValue(OffHandPlus.GapMode.Both)).build());
      SettingGroup var10001 = this.sgGeneral;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Sword Mode")).description("When should we hold sword.")).defaultValue(OffHandPlus.SwordMode.Pressed);
      GapMode var10003 = (GapMode)this.gapMode.get();
      Objects.requireNonNull(var10003);
      this.swordMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::isSword)).build());
      var10001 = this.sgGeneral;
      BoolSetting.Builder var1 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Safe Sword")).description("Only sword gaps if you have enough health.")).defaultValue(false);
      var10003 = (GapMode)this.gapMode.get();
      Objects.requireNonNull(var10003);
      this.safeSword = var10001.add(((BoolSetting.Builder)var1.visible(var10003::isSword)).build());
      this.delay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Delay")).description("Delay between switches.")).defaultValue(0.1).range(0.0, 1.0).sliderRange(0.0, 1.0).build());
      this.strict = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Strict Swap")).description("Uses pick silent and swap with offhand packets to bypass ncp inventory checks.")).defaultValue(false)).build());
      this.hp = this.sgHealth.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Health")).description("Switches to totem when health is under this value.")).defaultValue(14)).range(0, 36).sliderMax(36).build());
      this.safety = this.sgHealth.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Safety")).description("Tries to prevent offhand fails by switching while in danger.")).defaultValue(true)).build());
      var10001 = this.sgHealth;
      IntSetting.Builder var2 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Safety Health")).description("Holds totem if you would have under this health after possible damages.")).defaultValue(0)).range(0, 36).sliderMax(36);
      Setting var3 = this.safety;
      Objects.requireNonNull(var3);
      this.safetyHealth = var10001.add(((IntSetting.Builder)var2.visible(var3::get)).build());
      this.timer = 0.0;
      this.item = null;
      this.suicide = null;
      this.autoCrystalRewrite = null;
      this.crystalAura = null;
      this.autoMine = null;
      this.lastTime = 0L;
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.timer -= (double)(System.currentTimeMillis() - this.lastTime) / 1000.0;
         this.lastTime = System.currentTimeMillis();
         if (this.suicide == null) {
            this.suicide = (Suicide)Modules.get().get(Suicide.class);
         }

         if (this.autoCrystalRewrite == null) {
            this.autoCrystalRewrite = (AutoCrystalPlus)Modules.get().get(AutoCrystalPlus.class);
         }

         if (this.crystalAura == null) {
            this.crystalAura = (CrystalAura)Modules.get().get(CrystalAura.class);
         }

         if (this.autoMine == null) {
            this.autoMine = (AutoMine)Modules.get().get(AutoMine.class);
         }

         this.item = this.getItem();
         if (this.item != null) {
            this.update();
         }

      }
   }

   private void update() {
      if (!(this.timer > 0.0)) {
         if (!this.getPredicate(this.item).test(this.mc.field_1724.method_6079().method_7909())) {
            if (!(Boolean)this.onlyInInv.get() || this.mc.field_1755 instanceof class_490) {
               int slot = this.getSlot(this.getPredicate(this.item));
               this.move(slot);
               this.timer = (Double)this.delay.get();
            }
         }
      }
   }

   private void move(int slot) {
      if ((Boolean)this.strict.get()) {
         InventoryUtils.pickSwitch(slot);
         this.sendPacket(new class_2846(class_2847.field_12969, new class_2338(0, 0, 0), class_2350.field_11033, 0));
         InventoryUtils.pickSwapBack();
         InvUtils.swap(Managers.HOLDING.slot, false);
      } else {
         InvUtils.move().from(slot).toOffhand();
      }
   }

   private Predicate getPredicate(class_1792 item) {
      if (item == class_1802.field_8463) {
         return LemonUtils::isGapple;
      } else if (item == class_1802.field_8789) {
         return (item1) -> {
            return item1 instanceof class_1748;
         };
      } else {
         Objects.requireNonNull(item);
         return item::equals;
      }
   }

   private class_1792 getItem() {
      if (this.mc.field_1724.method_6047().method_7909() instanceof class_1829 && (!(Boolean)this.safeSword.get() || !this.inDanger()) && ((GapMode)this.gapMode.get()).sword) {
         switch ((SwordMode)this.swordMode.get()) {
            case Always:
               return class_1802.field_8463;
            case Pressed:
               if (this.mc.field_1690.field_1904.method_1434()) {
                  return class_1802.field_8463;
               }
         }
      }

      if (this.inDanger() && !this.suicide.isActive() && this.itemAvailable((itemStack) -> {
         return itemStack.method_7909() == class_1802.field_8288;
      })) {
         return class_1802.field_8288;
      } else {
         switch ((ItemMode)this.itemMode.get()) {
            case Totem:
               if (!this.suicide.isActive() && this.itemAvailable((itemStack) -> {
                  return itemStack.method_7909() == class_1802.field_8288;
               })) {
                  return class_1802.field_8288;
               }
               break;
            case Crystal:
               if (this.itemAvailable((itemStack) -> {
                  return itemStack.method_7909() == class_1802.field_8301;
               })) {
                  return class_1802.field_8301;
               }
               break;
            case Gapple:
               if (this.itemAvailable(LemonUtils::isGapple)) {
                  return class_1802.field_8463;
               }
               break;
            case Bed:
               if (this.itemAvailable((itemStack) -> {
                  return itemStack.method_7909() instanceof class_1748;
               })) {
                  return class_1802.field_8789;
               }
         }

         if (this.itemAvailable(LemonUtils::isGapple)) {
            switch ((GapMode)this.gapMode.get()) {
               case Sword:
               case Both:
               case Never:
                  break;
               case LastOption:
                  return class_1802.field_8463;
               default:
                  throw new IncompatibleClassChangeError();
            }
         }

         return null;
      }
   }

   private boolean inDanger() {
      double health = (double)(this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067());
      return health <= (double)(Integer)this.hp.get() || (Boolean)this.safety.get() && health - PlayerUtils.possibleHealthReductions() <= (double)(Integer)this.safetyHealth.get();
   }

   private int getSlot(Predicate predicate) {
      double amount = -1.0;
      int slot = -1;

      class_1799 s;
      int i;
      for(i = 9; i < this.mc.field_1724.method_31548().method_5439() + 1; ++i) {
         s = this.mc.field_1724.method_31548().method_5438(i);
         if (predicate.test(s.method_7909()) && (double)s.method_7947() > amount) {
            slot = i;
            amount = (double)s.method_7947();
         }
      }

      if (slot >= 0) {
         return slot;
      } else {
         for(i = 0; i < 9; ++i) {
            s = this.mc.field_1724.method_31548().method_5438(i);
            if (predicate.test(s.method_7909()) && (double)s.method_7947() > amount) {
               slot = i;
               amount = (double)s.method_7947();
            }
         }

         return slot;
      }
   }

   private boolean itemAvailable(Predicate predicate) {
      return InvUtils.find(predicate).found();
   }

   public static enum ItemMode {
      Totem,
      Crystal,
      Gapple,
      Bed;

      // $FF: synthetic method
      private static ItemMode[] $values() {
         return new ItemMode[]{Totem, Crystal, Gapple, Bed};
      }
   }

   public static enum GapMode {
      Sword(true),
      LastOption(false),
      Both(true),
      Never(false);

      public final boolean sword;

      private GapMode(boolean sword) {
         this.sword = sword;
      }

      public boolean isSword() {
         return this.sword;
      }

      // $FF: synthetic method
      private static GapMode[] $values() {
         return new GapMode[]{Sword, LastOption, Both, Never};
      }
   }

   public static enum SwordMode {
      Pressed,
      Always;

      // $FF: synthetic method
      private static SwordMode[] $values() {
         return new SwordMode[]{Pressed, Always};
      }
   }
}
