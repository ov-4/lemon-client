package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.timers.TimerUtils;
import java.util.Objects;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1775;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_1893;

public class XPThrower extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlayer;
   private final SettingGroup sgPause;
   public final Setting throwBind;
   public final Setting justThrow;
   public final Setting replenish;
   private final Setting hotbarSlot;
   private final Setting maxThreshold;
   public final Setting repairHeld;
   private final Setting autoSwitch;
   private final Setting noGapSwitch;
   private final Setting throwDelay;
   private final Setting tpsSync;
   private final Setting lookDown;
   private final Setting onlyOnGround;
   private final Setting onlyInHole;
   private final Setting allowDoubles;
   private final Setting eatPause;
   private final Setting drinkPause;
   private final Setting minePause;
   private final Setting minHealth;
   private int delay;
   public boolean isRepairing;

   public XPThrower() {
      super(LemonClient.Combat, "XP Thrower", "Throw XP bottles to repair your armor and tools.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlayer = this.settings.createGroup("Player");
      this.sgPause = this.settings.createGroup("Pause");
      this.throwBind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("Key Bind")).description("The keybind to throw XP.")).defaultValue(Keybind.none())).build());
      this.justThrow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("force-throw")).description("Throw XP even if your items are fully repaired.")).defaultValue(false)).build());
      this.replenish = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("replenish")).description("Automatically move XP into your hotbar.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("hotbar-slot")).description("Which hotbar slot to move the XP to.")).defaultValue(5)).range(1, 9).sliderRange(1, 9);
      Setting var10003 = this.replenish;
      Objects.requireNonNull(var10003);
      this.hotbarSlot = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.maxThreshold = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-durability")).description("The maximum durability to repair items to.")).defaultValue(80)).range(1, 100).sliderRange(1, 100).visible(() -> {
         return !(Boolean)this.justThrow.get();
      })).build());
      this.repairHeld = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("repair-held")).description("Repairs the item you are holding in your main hand.")).defaultValue(false)).build());
      this.autoSwitch = this.sgPlayer.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("auto-switch")).description("How to switch to XP.")).defaultValue(XPThrower.SwitchMode.Silent)).build());
      this.noGapSwitch = this.sgPlayer.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-gap-switch")).description("Whether to switch to XP if you're holding a gap.")).defaultValue(true)).visible(() -> {
         return this.autoSwitch.get() == XPThrower.SwitchMode.Normal;
      })).build());
      this.throwDelay = this.sgPlayer.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("throw-delay")).description("How fast to throw XP.")).defaultValue(1)).range(0, 20).sliderRange(0, 20).build());
      this.tpsSync = this.sgPlayer.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("TPS-sync")).description("Syncs the throw delay with the server's TPS.")).defaultValue(true)).build());
      this.lookDown = this.sgPlayer.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("look-down")).description("Forces you to rotate downwards when throwing XP.")).defaultValue(true)).build());
      this.onlyOnGround = this.sgPlayer.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Only activate when you are on the ground.")).defaultValue(true)).build());
      this.onlyInHole = this.sgPlayer.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-in-hole")).description("Only activate when you are in a hole.")).defaultValue(false)).build());
      var10001 = this.sgPlayer;
      BoolSetting.Builder var1 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("allow-doubles")).description("Allows double holes to count as holes.")).defaultValue(false);
      var10003 = this.onlyInHole;
      Objects.requireNonNull(var10003);
      this.allowDoubles = var10001.add(((BoolSetting.Builder)var1.visible(var10003::get)).build());
      this.eatPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-eat")).description("Whether to pause while eating.")).defaultValue(true)).build());
      this.drinkPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-drink")).description("Whether to pause while eating.")).defaultValue(true)).build());
      this.minePause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-mine")).description("Whether to pause while eating.")).defaultValue(false)).build());
      this.minHealth = this.sgPause.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-health")).description("How much health you must have to throw XP.")).defaultValue(10.0).range(1.0, 36.0).sliderRange(1.0, 36.0).build());
      this.delay = 0;
   }

   public void onActivate() {
      this.delay = 0;
      this.isRepairing = false;
   }

   public void onDeactivate() {
      this.isRepairing = false;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.delay > 0) {
         --this.delay;
      }

      if (!this.shouldWait()) {
         if (!((Keybind)this.throwBind.get()).isPressed() || this.mc.field_1755 != null || this.isRepaired() && !(Boolean)this.justThrow.get()) {
            this.isRepairing = false;
         } else {
            this.isRepairing = true;
            FindItemResult XP = InvUtils.find(new class_1792[]{class_1802.field_8287});
            FindItemResult hotbarXP = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8287});
            if (XP.found()) {
               if (!hotbarXP.found()) {
                  if (!(Boolean)this.replenish.get()) {
                     return;
                  }

                  InvUtils.move().from(XP.slot()).toHotbar((Integer)this.hotbarSlot.get() - 1);
               }

               if (this.delay <= 0) {
                  if ((Boolean)this.lookDown.get()) {
                     Rotations.rotate((double)this.mc.field_1724.method_36454(), 90.0, () -> {
                        this.throwXP(hotbarXP);
                     });
                  } else {
                     this.throwXP(hotbarXP);
                  }

                  this.delay = (int)((double)(Integer)this.throwDelay.get() / TimerUtils.getTPSMatch((Boolean)this.tpsSync.get()));
               }
            }
         }

      }
   }

   public boolean isRepaired() {
      class_1799 helmet = this.mc.field_1724.method_31548().method_7372(3);
      class_1799 chestplate = this.mc.field_1724.method_31548().method_7372(2);
      class_1799 leggings = this.mc.field_1724.method_31548().method_7372(1);
      class_1799 boots = this.mc.field_1724.method_31548().method_7372(0);
      class_1799 tool = this.mc.field_1724.method_6047();
      boolean helmetRepaired;
      if (class_1890.method_8225(class_1893.field_9101, helmet) > 0) {
         helmetRepaired = (float)(helmet.method_7936() - helmet.method_7919()) / (float)helmet.method_7936() * 100.0F >= (float)(Integer)this.maxThreshold.get();
      } else {
         helmetRepaired = true;
      }

      boolean chestplateRepaired;
      if (class_1890.method_8225(class_1893.field_9101, chestplate) > 0) {
         chestplateRepaired = (float)(chestplate.method_7936() - chestplate.method_7919()) / (float)chestplate.method_7936() * 100.0F >= (float)(Integer)this.maxThreshold.get();
      } else {
         chestplateRepaired = true;
      }

      boolean leggingsRepaired;
      if (class_1890.method_8225(class_1893.field_9101, leggings) > 0) {
         leggingsRepaired = (float)(leggings.method_7936() - leggings.method_7919()) / (float)leggings.method_7936() * 100.0F >= (float)(Integer)this.maxThreshold.get();
      } else {
         leggingsRepaired = true;
      }

      boolean bootsRepaired;
      if (class_1890.method_8225(class_1893.field_9101, boots) > 0) {
         bootsRepaired = (float)(boots.method_7936() - boots.method_7919()) / (float)boots.method_7936() * 100.0F >= (float)(Integer)this.maxThreshold.get();
      } else {
         bootsRepaired = true;
      }

      boolean toolsRepaired;
      if ((Boolean)this.repairHeld.get()) {
         if (class_1890.method_8225(class_1893.field_9101, tool) > 0) {
            toolsRepaired = (float)(tool.method_7936() - tool.method_7919()) / (float)tool.method_7936() * 100.0F >= (float)(Integer)this.maxThreshold.get();
         } else {
            toolsRepaired = true;
         }
      } else {
         toolsRepaired = true;
      }

      return helmetRepaired && chestplateRepaired && leggingsRepaired && bootsRepaired && toolsRepaired;
   }

   private boolean shouldWait() {
      if (PlayerUtils.shouldPause((Boolean)this.minePause.get(), (Boolean)this.drinkPause.get(), (Boolean)this.eatPause.get())) {
         return true;
      } else if ((Boolean)this.onlyOnGround.get() && !this.mc.field_1724.method_24828()) {
         return true;
      } else if ((Boolean)this.allowDoubles.get() && !EntityInfo.isInHole(this.mc.field_1724, (Boolean)this.allowDoubles.get(), EntityInfo.BlastResistantType.Any)) {
         return true;
      } else {
         return PlayerUtils.getTotalHealth() <= (Double)this.minHealth.get();
      }
   }

   private void throwXP(FindItemResult hotbarExp) {
      int prevSlot = this.mc.field_1724.method_31548().field_7545;
      if (hotbarExp.isOffhand()) {
         this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5810);
      } else if (this.autoSwitch.get() == XPThrower.SwitchMode.Silent || !(Boolean)this.noGapSwitch.get() || !(this.mc.field_1724.method_6047().method_7909() instanceof class_1775)) {
         InvUtils.swap(hotbarExp.slot(), false);
         this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
         if (this.autoSwitch.get() == XPThrower.SwitchMode.Silent) {
            InvUtils.swap(prevSlot, false);
         }
      }

   }

   public static enum SwitchMode {
      Normal,
      Silent;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Normal, Silent};
      }
   }
}
