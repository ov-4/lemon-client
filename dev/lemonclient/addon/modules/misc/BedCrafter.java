package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.utils.entity.EntityInfo;
import dev.lemonclient.addon.utils.misc.Vec3dInfo;
import dev.lemonclient.addon.utils.player.CraftUtils;
import dev.lemonclient.addon.utils.world.BlockInfo;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1714;
import net.minecraft.class_1748;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1860;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3965;
import net.minecraft.class_516;
import net.minecraft.class_5421;

public class BedCrafter extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgAuto;
   private final Setting disableAfter;
   private final Setting disableNoMats;
   private final Setting closeAfter;
   private final Setting placeDelay;
   private final Setting openDelay;
   private final Setting automatic;
   private final Setting antiTotemFail;
   private final Setting antiDesync;
   private final Setting autoOnlyHole;
   private final Setting autoOnlyGround;
   private final Setting autoWhileMoving;
   private final Setting refillAt;
   private final Setting emptySlotsNeeded;
   private final Setting radius;
   private final Setting minHealth;
   private boolean didRefill;
   private boolean startedRefill;
   private boolean alertedNoMats;
   private int placeTimer;
   private int openTimer;

   public BedCrafter() {
      super(LemonClient.Misc, "Bed Crafter", "Automatically craft beds.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgAuto = this.settings.createGroup("Auto (Buggy)");
      this.disableAfter = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-after")).description("Toggle off after filling your inv with beds.")).defaultValue(false)).build());
      this.disableNoMats = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-no-mats")).description("Toggle off if you run out of material.")).defaultValue(false)).build());
      this.closeAfter = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("close-after")).description("Close the crafting GUI after filling.")).defaultValue(true)).build());
      this.placeDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("table-place-delay")).description("Delay between placing crafting tables.")).defaultValue(3)).min(1).sliderMax(10).build());
      this.openDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("table-open-delay")).description("Delay between opening crafting tables.")).defaultValue(3)).min(1).sliderMax(10).build());
      this.automatic = this.sgAuto.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("automatic")).description("Automatically place/search for and open crafting tables when you're out of beds.")).defaultValue(true)).build());
      this.antiTotemFail = this.sgAuto.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-totem-fail")).description("Will not open / close current crafting table if you don't have a totem.")).defaultValue(false)).build());
      this.antiDesync = this.sgAuto.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-desync")).description("Try to prevent inventory desync.")).defaultValue(false)).build());
      this.autoOnlyHole = this.sgAuto.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("in-hole-only")).description("Only auto refill while in a hole.")).defaultValue(false)).build());
      this.autoOnlyGround = this.sgAuto.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("on-ground-only")).description("Only auto refill while on the ground.")).defaultValue(false)).build());
      this.autoWhileMoving = this.sgAuto.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("while-moving")).description("Allow auto refill while in motion")).defaultValue(false)).build());
      this.refillAt = this.sgAuto.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("refill-at")).description("How many beds are left in your inventory to start filling.")).defaultValue(3)).min(1).build());
      this.emptySlotsNeeded = this.sgAuto.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("required-empty-slots")).description("How many empty slots are required for activation.")).defaultValue(5)).min(1).build());
      this.radius = this.sgAuto.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("radius")).description("How far to search for crafting tables near you.")).defaultValue(3)).min(1).build());
      this.minHealth = this.sgAuto.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-health")).description("Min health require to activate.")).defaultValue(10.0).min(1.0).max(36.0).sliderMax(36.0).build());
      this.didRefill = false;
      this.startedRefill = false;
      this.alertedNoMats = false;
   }

   public void onActivate() {
      this.placeTimer = (Integer)this.placeDelay.get();
      this.openTimer = (Integer)this.openDelay.get();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (PlayerUtils.getTotalHealth() <= (Double)this.minHealth.get()) {
         this.closeCraftingTable();
      } else if (this.willTotemFail()) {
         this.closeCraftingTable();
      } else {
         if ((Boolean)this.automatic.get() && this.isOutOfMaterial() && !this.alertedNoMats) {
            this.alertedNoMats = true;
         }

         if ((Boolean)this.automatic.get() && this.needsRefill() && this.canRefill(true) && !this.isOutOfMaterial() && !(this.mc.field_1724.field_7512 instanceof class_1714)) {
            FindItemResult craftTable = CraftUtils.findCraftTable();
            if (!craftTable.found()) {
               this.toggle();
            } else {
               class_2338 tablePos = this.findCraftingTable();
               if (tablePos == null) {
                  if (this.placeTimer <= 0) {
                     this.placeTimer = (Integer)this.placeDelay.get();
                     this.placeCraftingTable(craftTable);
                  } else {
                     --this.placeTimer;
                  }
               } else if (this.openTimer <= 0) {
                  this.openCraftingTable(tablePos);
                  this.openTimer = (Integer)this.openDelay.get();
                  this.didRefill = true;
               } else {
                  --this.openTimer;
               }
            }
         } else {
            if (this.didRefill && !this.needsRefill()) {
               this.didRefill = false;
               this.startedRefill = false;
            }

            class_1703 var3 = this.mc.field_1724.field_7512;
            if (var3 instanceof class_1714) {
               class_1714 currentScreenHandler = (class_1714)var3;
               if (this.mc.field_1724.method_3130() != null && !this.mc.field_1724.method_3130().method_14887(class_5421.field_25763)) {
                  this.mc.field_1724.method_3130().method_14884(class_5421.field_25763, true);
               }

               if (PlayerUtils.getTotalHealth() <= (Double)this.minHealth.get() || this.willTotemFail()) {
                  this.closeCraftingTable();
                  return;
               }

               if (!this.canRefill(false)) {
                  this.closeCraftingTable();
                  if ((Boolean)this.antiDesync.get()) {
                     this.mc.field_1724.method_31548().method_7381();
                  }

                  return;
               }

               if (this.isOutOfMaterial()) {
                  if ((Boolean)this.disableNoMats.get()) {
                     this.toggle();
                  }

                  this.closeCraftingTable();
                  if ((Boolean)this.antiDesync.get()) {
                     this.mc.field_1724.method_31548().method_7381();
                  }

                  return;
               }

               if (CraftUtils.isInventoryFull()) {
                  if ((Boolean)this.disableAfter.get()) {
                     this.toggle();
                  }

                  if ((Boolean)this.closeAfter.get()) {
                     this.closeCraftingTable();
                     if ((Boolean)this.antiDesync.get()) {
                        this.mc.field_1724.method_31548().method_7381();
                     }
                  }

                  return;
               }

               List recipeResultCollectionList = this.mc.field_1724.method_3130().method_1393();
               Iterator var4 = recipeResultCollectionList.iterator();

               label123:
               while(var4.hasNext()) {
                  class_516 recipeResultCollection = (class_516)var4.next();
                  Iterator var6 = recipeResultCollection.method_2648(true).iterator();

                  while(true) {
                     class_1860 recipe;
                     do {
                        if (!var6.hasNext()) {
                           continue label123;
                        }

                        recipe = (class_1860)var6.next();
                     } while(!(recipe.method_8110(this.mc.field_1687.method_30349()).method_7909() instanceof class_1748));

                     for(int i = 0; i < CraftUtils.getEmptySlots(); ++i) {
                        this.mc.field_1761.method_2912(currentScreenHandler.field_7763, recipe, false);
                        this.windowClick(currentScreenHandler, 0, class_1713.field_7794, 1);
                     }
                  }
               }

               if (CraftUtils.isInventoryFull()) {
                  this.closeCraftingTable();
               }
            }

         }
      }
   }

   private void placeCraftingTable(FindItemResult craftTable) {
      List nearbyBlocks = BlockInfo.getSphere(this.mc.field_1724.method_24515(), (Integer)this.radius.get(), (Integer)this.radius.get());
      Iterator var3 = nearbyBlocks.iterator();

      while(var3.hasNext()) {
         class_2338 block = (class_2338)var3.next();
         if (BlockInfo.getBlock(block) == class_2246.field_10124 && BlockUtils.canPlace(block, true)) {
            BlockUtils.place(block, craftTable, 0, true);
            break;
         }
      }

   }

   private class_2338 findCraftingTable() {
      List nearbyBlocks = BlockInfo.getSphere(this.mc.field_1724.method_24515(), (Integer)this.radius.get(), (Integer)this.radius.get());
      Iterator var2 = nearbyBlocks.iterator();

      class_2338 block;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         block = (class_2338)var2.next();
      } while(BlockInfo.getBlock(block) != class_2246.field_9980);

      return block;
   }

   private void openCraftingTable(class_2338 tablePos) {
      class_243 tableVec = Vec3dInfo.closestVec3d(tablePos);
      class_3965 table = new class_3965(tableVec, class_2350.field_11036, tablePos, false);
      this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, table);
   }

   private void closeCraftingTable() {
      if (this.mc.field_1724.field_7512 instanceof class_1714) {
         this.mc.field_1724.method_7346();
      }

   }

   private boolean needsRefill() {
      FindItemResult bed = InvUtils.find((itemStack) -> {
         return itemStack.method_7909() instanceof class_1748;
      });
      if (!bed.found()) {
         return true;
      } else if (bed.count() <= (Integer)this.refillAt.get()) {
         return true;
      } else {
         return !CraftUtils.isInventoryFull();
      }
   }

   private boolean canRefill(boolean checkSlots) {
      if (!(Boolean)this.autoWhileMoving.get() && EntityInfo.isMoving(this.mc.field_1724)) {
         return false;
      } else if ((Boolean)this.autoOnlyHole.get() && !EntityInfo.isInHole(this.mc.field_1724)) {
         return false;
      } else if ((Boolean)this.autoOnlyGround.get() && !this.mc.field_1724.method_24828()) {
         return false;
      } else if (CraftUtils.isInventoryFull()) {
         return false;
      } else if (checkSlots && CraftUtils.getEmptySlots() < (Integer)this.emptySlotsNeeded.get()) {
         return false;
      } else {
         return !(PlayerUtils.getTotalHealth() <= (Double)this.minHealth.get());
      }
   }

   private boolean isOutOfMaterial() {
      FindItemResult wool = InvUtils.find((itemStack) -> {
         return CraftUtils.wools.contains(itemStack.method_7909());
      });
      FindItemResult plank = InvUtils.find((itemStack) -> {
         return CraftUtils.planks.contains(itemStack.method_7909());
      });
      FindItemResult craftTable = CraftUtils.findCraftTable();
      if (!craftTable.found()) {
         return true;
      } else if (wool.found() && plank.found()) {
         return wool.count() < 3 || plank.count() < 3;
      } else {
         return true;
      }
   }

   private boolean willTotemFail() {
      if (!(Boolean)this.antiTotemFail.get()) {
         return false;
      } else {
         class_1792 offhand = this.mc.field_1724.method_6079().method_7909();
         if (offhand == null) {
            return true;
         } else {
            return offhand != class_1802.field_8288;
         }
      }
   }

   private void windowClick(class_1703 container, int slot, class_1713 action, int clickData) {
      assert this.mc.field_1761 != null;

      this.mc.field_1761.method_2906(container.field_7763, slot, clickData, action, this.mc.field_1724);
   }
}
