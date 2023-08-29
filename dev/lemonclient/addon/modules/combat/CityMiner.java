package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.others.Task;
import dev.lemonclient.addon.utils.world.BlockInfo;
import dev.lemonclient.addon.utils.world.CityUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;

public class CityMiner extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting targetRange;
   private final Setting chatInfo;
   private class_2338 breakPos;
   private class_1657 target;
   private final Task crystalTask;
   private final Task supportTask;

   public CityMiner() {
      super(LemonClient.Combat, "City Miner", "Automatically breaks target's surround with AutoMine.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.targetRange = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Target Range")).description("The range players can be targeted.")).defaultValue(5)).sliderRange(0, 7).build());
      this.chatInfo = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Chat Info")).description("Send chat info to notify you.")).defaultValue(true)).build());
      this.crystalTask = new Task();
      this.supportTask = new Task();
   }

   public void onActivate() {
      this.crystalTask.reset();
      this.supportTask.reset();
      this.breakPos = null;
   }

   public void onDeactivate() {
      if (this.breakPos != null) {
         this.mc.field_1761.method_2910(this.breakPos, class_2350.field_11036);
      }

   }

   @EventHandler
   public void onTick(TickEvent.Post event) {
      this.target = TargetUtils.getPlayerTarget((double)(Integer)this.targetRange.get(), SortPriority.LowestDistance);
      if (TargetUtils.isBadTarget(this.target, (double)(Integer)this.targetRange.get())) {
         if ((Boolean)this.chatInfo.get()) {
            this.sendDisableMsg("Target is null");
         }

         this.toggle();
      } else if (!InvUtils.findInHotbar(new class_1792[]{class_1802.field_8403, class_1802.field_22024, class_1802.field_8377}).found()) {
         if ((Boolean)this.chatInfo.get()) {
            this.sendDisableMsg("There's no pickaxe in your hotbar");
         }

         this.toggle();
      } else {
         if (this.breakPos == null) {
            this.breakPos = CityUtils.getBreakPos(this.target);
         }

         if (this.breakPos != null && !BlockInfo.isAir(this.breakPos)) {
            this.mc.field_1761.method_2902(this.breakPos, class_2350.field_11036);
            this.toggle();
         } else {
            if (this.breakPos == null && (Boolean)this.chatInfo.get()) {
               this.sendDisableMsg("Position is invalid.");
            }

            this.toggle();
         }
      }
   }

   public String getInfoString() {
      return this.target != null ? this.target.method_7334().getName() : null;
   }
}
