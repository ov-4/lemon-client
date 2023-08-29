package dev.lemonclient.addon.modules.settings;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.SwingState;
import dev.lemonclient.addon.enums.SwingType;
import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.class_1268;
import net.minecraft.class_2879;

public class SwingSettings extends LemonModule {
   private final SettingGroup sgInteract;
   private final SettingGroup sgBlockPlace;
   private final SettingGroup sgMining;
   private final SettingGroup sgAttack;
   private final SettingGroup sgUse;
   public final Setting interact;
   public final Setting interactState;
   public final Setting blockPlace;
   public final Setting blockPlaceState;
   public final Setting mining;
   public final Setting attack;
   public final Setting attackState;
   public final Setting use;
   public final Setting useState;

   public SwingSettings() {
      super(LemonClient.Settings, "Swing", "Global swing settings for every lemon module.");
      this.sgInteract = this.settings.createGroup("Interact");
      this.sgBlockPlace = this.settings.createGroup("Block Place");
      this.sgMining = this.settings.createGroup("Mining");
      this.sgAttack = this.settings.createGroup("Attack");
      this.sgUse = this.settings.createGroup("Use");
      this.interact = this.sgInteract.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Interact Swing")).description("Swings your hand when you interact with a block.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgInteract;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Interact State")).description("Should we swing our hand before or after the action.")).defaultValue(SwingState.Post);
      Setting var10003 = this.interact;
      Objects.requireNonNull(var10003);
      this.interactState = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.blockPlace = this.sgBlockPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Block Place Swing")).description("Swings your hand when you interact with a block.")).defaultValue(true)).build());
      var10001 = this.sgBlockPlace;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Block Place State")).description("Should we swing our hand before or after the action.")).defaultValue(SwingState.Post);
      var10003 = this.blockPlace;
      Objects.requireNonNull(var10003);
      this.blockPlaceState = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.mining = this.sgMining.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mining Swing")).description("Swings your hand when you place a crystal.")).defaultValue(SwingSettings.MiningSwingState.Double)).build());
      this.attack = this.sgAttack.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Attack Swing")).description("Swings your hand when you attack any entity.")).defaultValue(true)).build());
      var10001 = this.sgAttack;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Attack State")).description("Should we swing our hand before or after the action.")).defaultValue(SwingState.Post);
      var10003 = this.attack;
      Objects.requireNonNull(var10003);
      this.attackState = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.use = this.sgUse.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Use Swing")).description("Swings your hand when using an item. NCP doesn't check this.")).defaultValue(true)).build());
      var10001 = this.sgUse;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Using State")).description("Should we swing our hand before or after the action.")).defaultValue(SwingState.Post);
      var10003 = this.use;
      Objects.requireNonNull(var10003);
      this.useState = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
   }

   public void swing(SwingState state, SwingType type, class_1268 hand) {
      if (this.mc.field_1724 != null) {
         if (state.equals(this.getState(type))) {
            switch (type) {
               case Interact:
                  this.swing((Boolean)this.interact.get(), hand);
                  break;
               case Placing:
                  this.swing((Boolean)this.blockPlace.get(), hand);
                  break;
               case Attacking:
                  this.swing((Boolean)this.attack.get(), hand);
                  break;
               case Using:
                  this.swing((Boolean)this.use.get(), hand);
            }

         }
      }
   }

   public void mineSwing(MiningSwingState state) {
      switch (state) {
         case Start:
            if (this.mining.get() != SwingSettings.MiningSwingState.Start) {
               return;
            }
            break;
         case End:
            if (this.mining.get() != SwingSettings.MiningSwingState.End) {
               return;
            }
            break;
         case Disabled:
            return;
      }

      if (this.mc.field_1724 != null) {
         this.swing(true, class_1268.field_5808);
      }
   }

   private SwingState getState(SwingType type) {
      SwingState var10000;
      switch (type) {
         case Interact:
            var10000 = (SwingState)this.interactState.get();
            break;
         case Placing:
            var10000 = (SwingState)this.blockPlaceState.get();
            break;
         case Attacking:
            var10000 = (SwingState)this.attackState.get();
            break;
         case Using:
            var10000 = (SwingState)this.useState.get();
            break;
         case Mining:
            var10000 = SwingState.Post;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private void swing(boolean shouldSwing, class_1268 hand) {
      if (this.mc.field_1724 != null) {
         if (shouldSwing) {
            this.mc.field_1724.field_3944.method_2883(new class_2879(hand));
         }

      }
   }

   public static enum MiningSwingState {
      Disabled,
      Start,
      End,
      Double;

      // $FF: synthetic method
      private static MiningSwingState[] $values() {
         return new MiningSwingState[]{Disabled, Start, End, Double};
      }
   }
}
