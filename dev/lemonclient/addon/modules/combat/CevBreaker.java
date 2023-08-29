package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.hud.ToastNotifications;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.modules.info.Notifications;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.world.BlockInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.DamageUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1774;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2680;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;

public class CevBreaker extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgBreaking;
   private final SettingGroup sgPause;
   private final SettingGroup sgRender;
   private final SettingGroup sgNone;
   private final Setting toggleModules;
   private final Setting toggleBack;
   private final Setting modules;
   private final Setting mode;
   private final Setting smartDelay;
   private final Setting switchDelay;
   private final Setting pauseAtHealth;
   private final Setting eatPause;
   private final Setting drinkPause;
   private final Setting swing;
   private final Setting placeHand;
   private final Setting notifications;
   private class_1657 target;
   private boolean startedYet;
   private int switchDelayLeft;
   private int timer;
   private int breakDelayLeft;
   private final List blacklisted;
   private final List crystals;
   public ArrayList toActivate;
   boolean pause;

   public CevBreaker() {
      super(LemonClient.Combat, "Cev Breaker", "Break crystals over a ppl's head to deal massive damage!");
      this.sgGeneral = this.settings.createGroup("General");
      this.sgBreaking = this.settings.createGroup("Breaking");
      this.sgPause = this.settings.createGroup("Pause");
      this.sgRender = this.settings.createGroup("Render");
      this.sgNone = this.settings.createGroup("");
      this.toggleModules = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-modules")).description("Turn off other modules when Cev Breaker is activated.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-back-on")).description("Turn the modules back on when Cev Breaker is deactivated.")).defaultValue(false);
      Setting var10003 = this.toggleModules;
      Objects.requireNonNull(var10003);
      this.toggleBack = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      ModuleListSetting.Builder var1 = (ModuleListSetting.Builder)((ModuleListSetting.Builder)(new ModuleListSetting.Builder()).name("modules")).description("Which modules to toggle.");
      var10003 = this.toggleModules;
      Objects.requireNonNull(var10003);
      this.modules = var10001.add(((ModuleListSetting.Builder)var1.visible(var10003::get)).build());
      this.mode = this.sgBreaking.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description("Which mode to use for breaking the obsidian.")).defaultValue(CevBreaker.Mode.Instant)).build());
      this.smartDelay = this.sgBreaking.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Smart Delay")).description("Waits until the target can get damaged again with breaking the block.")).defaultValue(true)).visible(() -> {
         return this.mode.get() == CevBreaker.Mode.Instant;
      })).build());
      this.switchDelay = this.sgBreaking.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("switch-delay")).description("How many ticks to wait before hitting an entity after switching hotbar slots.")).defaultValue(1)).range(0, 20).sliderRange(0, 20).visible(() -> {
         return this.mode.get() == CevBreaker.Mode.Packet;
      })).build());
      this.pauseAtHealth = this.sgPause.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pause-health")).description("Pauses when you go below a certain health.")).defaultValue(5.0).min(0.0).build());
      this.eatPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-eat")).description("Pauses Crystal Aura when eating.")).defaultValue(true)).build());
      this.drinkPause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-drink")).description("Pauses Crystal Aura when drinking.")).defaultValue(true)).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Swing")).description("Renders your swing client-side.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      EnumSetting.Builder var2 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.swing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var2.visible(var10003::get)).build());
      this.notifications = this.sgNone.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Notifications")).defaultValue(Notifications.Mode.Chat)).build());
      this.blacklisted = new ArrayList();
      this.crystals = new ArrayList();
      this.pause = false;
   }

   @EventHandler
   public void onActivate() {
      this.target = null;
      this.startedYet = false;
      this.switchDelayLeft = 0;
      this.timer = 0;
      this.blacklisted.clear();
      this.toActivate = new ArrayList();
      if ((Boolean)this.toggleModules.get() && !((List)this.modules.get()).isEmpty() && this.mc.field_1687 != null && this.mc.field_1724 != null) {
         Iterator var1 = ((List)this.modules.get()).iterator();

         while(var1.hasNext()) {
            Module module = (Module)var1.next();
            if (module.isActive()) {
               module.toggle();
               this.toActivate.add(module);
            }
         }
      }

   }

   public void onDeactivate() {
      if ((Boolean)this.toggleBack.get() && !this.toActivate.isEmpty() && this.mc.field_1687 != null && this.mc.field_1724 != null) {
         Iterator var1 = this.toActivate.iterator();

         while(var1.hasNext()) {
            Module module = (Module)var1.next();
            if (!module.isActive()) {
               module.toggle();
            }
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      --this.switchDelayLeft;
      --this.breakDelayLeft;
      --this.timer;
      int crystalSlot = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8301}).slot();
      int obsidianSlot = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8281}).slot();
      int pickSlot = InvUtils.findInHotbar(new class_1792[]{class_1802.field_22024}).slot();
      pickSlot = pickSlot == -1 ? InvUtils.findInHotbar(new class_1792[]{class_1802.field_8377}).slot() : pickSlot;
      if ((crystalSlot != -1 || this.mc.field_1724.method_6079().method_7909() instanceof class_1774) && obsidianSlot != -1 && pickSlot != -1) {
         this.getEntities();
         if (this.target == null) {
            switch ((Notifications.Mode)this.notifications.get()) {
               case Toast:
                  ToastNotifications.addToast("No target found, disabling...");
                  break;
               case Notification:
                  Managers.NOTIFICATION.error(this.title, "No target found, disabling...");
                  break;
               case Chat:
                  this.error("No target found, disabling...", new Object[0]);
            }

            this.toggle();
         } else if (!PlayerUtils.shouldPause(false, (Boolean)this.eatPause.get(), (Boolean)this.drinkPause.get()) && !(PlayerUtils.getTotalHealth() <= (Double)this.pauseAtHealth.get())) {
            this.pause = false;
            class_2338 blockPos = this.target.method_24515().method_10069(0, 2, 0);
            class_2680 blockState = this.mc.field_1687.method_8320(blockPos);
            boolean crystalThere = false;
            Iterator var8 = this.crystals.iterator();

            while(var8.hasNext()) {
               class_1511 crystal = (class_1511)var8.next();
               if (crystal.method_24515().method_10069(0, -1, 0).equals(blockPos)) {
                  crystalThere = true;
                  break;
               }
            }

            if (!blockState.method_27852(class_2246.field_10540) && !crystalThere && (this.mc.field_1724.method_6047().method_7909().equals(class_1802.field_8281) || this.switchDelayLeft <= 0)) {
               if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                  Managers.ROTATION.start(blockPos, 50.0, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
               }

               if (!BlockUtils.place(blockPos, InvUtils.findInHotbar(new class_1792[]{class_1802.field_8281}), false, 50, (Boolean)this.swing.get(), true, true)) {
                  this.blacklisted.add(this.target);
                  this.getEntities();
                  if (this.target == null) {
                     switch ((Notifications.Mode)this.notifications.get()) {
                        case Toast:
                           ToastNotifications.addToast("Can't place obsidian above the target! Disabling...");
                           break;
                        case Notification:
                           Managers.NOTIFICATION.warn(this.title, "Can't place obsidian above the target! Disabling...");
                           break;
                        case Chat:
                           this.warning("Can't place obsidian above the target! Disabling...", new Object[0]);
                     }

                     this.toggle();
                  }

                  return;
               }
            }

            boolean offhand = this.mc.field_1724.method_6079().method_7909() instanceof class_1774;
            boolean mainhand = this.mc.field_1724.method_6047().method_7909() instanceof class_1774;
            if (!crystalThere && blockState.method_27852(class_2246.field_10540)) {
               if (!offhand && !mainhand && this.switchDelayLeft > 0) {
                  return;
               }

               double x = (double)blockPos.method_10084().method_10263();
               double y = (double)blockPos.method_10084().method_10264();
               double z = (double)blockPos.method_10084().method_10260();
               if (!this.mc.field_1687.method_8335((class_1297)null, new class_238(x, y, z, x + 1.0, y + 2.0, z + 1.0)).isEmpty() || !this.mc.field_1687.method_8320(blockPos.method_10084()).method_26215()) {
                  this.blacklisted.add(this.target);
                  this.getEntities();
                  if (this.target == null) {
                     switch ((Notifications.Mode)this.notifications.get()) {
                        case Toast:
                           ToastNotifications.addToast("Can't place the crystal! Disabling...");
                           break;
                        case Notification:
                           Managers.NOTIFICATION.warn(this.title, "Can't place the crystal! Disabling...");
                           break;
                        case Chat:
                           this.warning("Can't place the crystal! Disabling...", new Object[0]);
                     }

                     this.toggle();
                  }

                  return;
               }

               if (!offhand && !mainhand) {
                  this.mc.field_1724.method_31548().field_7545 = crystalSlot;
               }

               class_1268 hand = offhand ? class_1268.field_5810 : class_1268.field_5808;
               class_3965 result = new class_3965(this.mc.field_1724.method_19538(), (double)blockPos.method_10264() < this.mc.field_1724.method_23318() ? class_2350.field_11036 : class_2350.field_11033, blockPos, false);
               if ((Boolean)this.swing.get()) {
                  this.clientSwing((SwingHand)this.placeHand.get(), class_1268.field_5808);
               } else {
                  this.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
               }

               if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                  Managers.ROTATION.start(blockPos, 25.0, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}));
                  this.mc.field_1724.field_3944.method_2883(new class_2885(hand, result, 0));
               } else {
                  this.mc.field_1724.field_3944.method_2883(new class_2885(hand, result, 0));
               }
            }

            if (blockState.method_26215() && this.mode.get() == CevBreaker.Mode.Packet) {
               this.startedYet = false;
            }

            if ((this.mc.field_1724.method_31548().field_7545 == pickSlot || this.switchDelayLeft <= 0) && crystalThere && blockState.method_27852(class_2246.field_10540)) {
               class_2350 direction = EntityInfo.rayTraceCheck(blockPos, true);
               if (this.mode.get() == CevBreaker.Mode.Instant) {
                  if (!this.startedYet) {
                     this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, blockPos, direction));
                     this.startedYet = true;
                  } else {
                     if ((Boolean)this.smartDelay.get() && this.target.field_6235 > 0) {
                        return;
                     }

                     this.mc.field_1724.method_31548().field_7545 = pickSlot;
                     this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, direction));
                  }
               } else if (this.mode.get() == CevBreaker.Mode.Normal) {
                  this.mc.field_1724.method_31548().field_7545 = pickSlot;
                  this.mc.field_1761.method_2902(blockPos, direction);
               } else if (this.mode.get() == CevBreaker.Mode.Packet) {
                  this.timer = this.startedYet ? this.timer : BlockInfo.getBlockBreakingSpeed(blockState, blockPos, pickSlot);
                  if (!this.startedYet) {
                     this.packetMine(blockPos, (Boolean)this.swing.get());
                     this.startedYet = true;
                  } else if (this.timer <= 0) {
                     this.mc.field_1724.method_31548().field_7545 = pickSlot;
                  }
               }
            }

            AutoCrystal ACrystal = (AutoCrystal)Modules.get().get(AutoCrystal.class);
            if (ACrystal.bestTarget == null || ACrystal.bestTarget != this.target || (Double)ACrystal.minDmg.get() >= 6.0) {
               if (this.mode.get() == CevBreaker.Mode.Packet && this.breakDelayLeft >= 0) {
                  return;
               }

               Iterator var11 = this.crystals.iterator();

               while(var11.hasNext()) {
                  class_1511 crystal = (class_1511)var11.next();
                  if (DamageUtils.crystalDamage(this.target, crystal.method_19538()) >= 6.0) {
                     if ((Boolean)this.swing.get()) {
                        this.clientSwing((SwingHand)this.placeHand.get(), class_1268.field_5808);
                     } else {
                        this.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
                     }

                     if (SettingUtils.shouldRotate(RotationType.Mining)) {
                        Managers.ROTATION.start(crystal.method_5829(), 30.0, RotationType.Mining, (long)Objects.hash(new Object[]{this.name + "mining"}));
                        this.mc.field_1724.field_3944.method_2883(class_2824.method_34206(crystal, false));
                     } else {
                        this.mc.field_1724.field_3944.method_2883(class_2824.method_34206(crystal, false));
                     }
                     break;
                  }
               }
            }

         } else {
            switch ((Notifications.Mode)this.notifications.get()) {
               case Toast:
                  ToastNotifications.addToast("Pausing");
                  break;
               case Notification:
                  Managers.NOTIFICATION.warn(this.title, "Pausing");
                  break;
               case Chat:
                  this.warning("Pausing", new Object[0]);
            }

            this.pause = true;
         }
      } else {
         switch ((Notifications.Mode)this.notifications.get()) {
            case Toast:
               String var10000 = crystalSlot == -1 && !(this.mc.field_1724.method_6079().method_7909() instanceof class_1774) ? "crystals" : (obsidianSlot == -1 ? "obsidian" : "pickaxe");
               ToastNotifications.addToast("No " + var10000 + " found, disabling...");
               break;
            case Notification:
               String var10002 = crystalSlot == -1 && !(this.mc.field_1724.method_6079().method_7909() instanceof class_1774) ? "crystals" : (obsidianSlot == -1 ? "obsidian" : "pickaxe");
               Managers.NOTIFICATION.warn(this.title, "No " + var10002 + " found, disabling...");
               break;
            case Chat:
               this.warning("No " + (crystalSlot == -1 && !(this.mc.field_1724.method_6079().method_7909() instanceof class_1774) ? "crystals" : (obsidianSlot == -1 ? "obsidian" : "pickaxe")) + " found, disabling...", new Object[0]);
         }

         this.toggle();
      }
   }

   private void getEntities() {
      this.target = null;
      this.crystals.clear();
      Iterator var1 = this.mc.field_1687.method_18112().iterator();

      while(true) {
         class_1657 playerEntity;
         do {
            do {
               do {
                  while(true) {
                     class_1297 entity;
                     do {
                        do {
                           if (!var1.hasNext()) {
                              return;
                           }

                           entity = (class_1297)var1.next();
                        } while(!entity.method_24516(this.mc.field_1724, 6.0));
                     } while(!entity.method_5805());

                     if (entity instanceof class_1657 playerEntity) {
                        break;
                     }

                     if (entity instanceof class_1511) {
                        this.crystals.add((class_1511)entity);
                     }
                  }
               } while(playerEntity == this.mc.field_1724);
            } while(!Friends.get().shouldAttack(playerEntity));
         } while(this.target != null && !(this.mc.field_1724.method_5739(playerEntity) < this.mc.field_1724.method_5739(this.target)));

         if (!this.blacklisted.contains(playerEntity)) {
            this.target = playerEntity;
         }
      }
   }

   public void packetMine(class_2338 blockPos, boolean swing) {
      if (SettingUtils.shouldRotate(RotationType.Mining)) {
         Managers.ROTATION.start(blockPos, (double)this.priority, RotationType.Mining, (long)Objects.hash(new Object[]{this.name + "mining"}));
         this.packetMine(blockPos, swing);
      } else {
         this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, blockPos, class_2350.field_11036));
         if (swing) {
            this.clientSwing((SwingHand)this.placeHand.get(), class_1268.field_5808);
         } else {
            this.mc.field_1724.field_3944.method_2883(new class_2879(class_1268.field_5808));
         }

         this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, class_2350.field_11036));
      }

   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2868) {
         this.switchDelayLeft = 1;
         this.breakDelayLeft = (Integer)this.switchDelay.get();
      }

   }

   public String getInfoString() {
      return this.target != null ? this.target.method_5820() : null;
   }

   public static enum Mode {
      Normal,
      Packet,
      Instant;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Normal, Packet, Instant};
      }
   }
}
