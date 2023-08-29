package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.HoleType;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.player.PlaceData;
import dev.lemonclient.addon.utils.world.hole.HoleUtils;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2358;
import net.minecraft.class_238;
import net.minecraft.class_2459;
import net.minecraft.class_2527;
import net.minecraft.class_2665;
import net.minecraft.class_2671;
import net.minecraft.class_2828;
import net.minecraft.class_2846;
import net.minecraft.class_3341;
import net.minecraft.class_742;
import net.minecraft.class_2350.class_2353;
import net.minecraft.class_2846.class_2847;

public class HolePush extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgDelay;
   private final SettingGroup sgSwing;
   private final SettingGroup sgRender;
   private final Setting pauseEat;
   private final Setting redstone;
   private final Setting onlyHole;
   private final Setting toggleMove;
   private final Setting pistonSwitch;
   private final Setting redstoneSwitch;
   private final Setting prDelay;
   private final Setting rmDelay;
   private final Setting mpDelay;
   private final Setting pistonSwing;
   private final Setting pistonHand;
   private final Setting redstoneSwing;
   private final Setting redstoneHand;
   private final Setting pistonShape;
   private final Setting psColor;
   private final Setting plColor;
   private final Setting redstoneShape;
   private final Setting rsColor;
   private final Setting rlColor;
   private long pistonTime;
   private long redstoneTime;
   private long mineTime;
   private boolean minedThisTick;
   private boolean pistonPlaced;
   private boolean redstonePlaced;
   private boolean mined;
   private class_2338 pistonPos;
   private class_2338 redstonePos;
   private class_2350 pistonDir;
   private PlaceData pistonData;
   private PlaceData redstoneData;
   private class_2338 lastPiston;
   private class_2338 lastRedstone;
   private class_2350 lastDirection;
   private class_2338 startPos;
   private class_2338 currentPos;

   public HolePush() {
      super(LemonClient.Combat, "Hole Push", "Automatically pushes people out of their safe holes.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgDelay = this.settings.createGroup("Delay");
      this.sgSwing = this.settings.createGroup("Swing");
      this.sgRender = this.settings.createGroup("Render");
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause On Eat")).description("Pause while eating.")).defaultValue(true)).build());
      this.redstone = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Redstone")).description("What kind of redstone to use.")).defaultValue(HolePush.Redstone.Torch)).build());
      this.onlyHole = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Only Hole")).description("Toggles when enemy moves.")).defaultValue(true)).build());
      this.toggleMove = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Toggle Move")).description("Toggles when enemy moves.")).defaultValue(true)).build());
      this.pistonSwitch = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Piston Switch")).description("Method of switching. Silent is the most reliable.")).defaultValue(HolePush.SwitchMode.Silent)).build());
      this.redstoneSwitch = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Redstone Switch")).description("Method of switching. Silent is the most reliable.")).defaultValue(HolePush.SwitchMode.Silent)).build());
      this.prDelay = this.sgDelay.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Piston > Redstone")).description("How many seconds to wait between placing piston and redstone.")).defaultValue(0.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.rmDelay = this.sgDelay.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Redstone > Mine")).description("How many seconds to wait between placing redstone and starting to mine it.")).defaultValue(0.2).min(0.0).sliderRange(0.0, 20.0).build());
      this.mpDelay = this.sgDelay.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Mine > Piston")).description("How many seconds to wait after mining the redstone before starting a new cycle.")).defaultValue(0.2).min(0.0).sliderRange(0.0, 20.0).build());
      this.pistonSwing = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Piston Swing")).description("Renders swing animation when placing a piston.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgSwing;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Piston Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.pistonSwing;
      Objects.requireNonNull(var10003);
      this.pistonHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.redstoneSwing = this.sgSwing.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Piston Swing")).description("Renders swing animation when placing redstone.")).defaultValue(true)).build());
      var10001 = this.sgSwing;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Redstone Swing Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.redstoneSwing;
      Objects.requireNonNull(var10003);
      this.redstoneHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.pistonShape = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Piston Shape Mode")).description("Which parts should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.psColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Piston Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 50)).build());
      this.plColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Piston Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 255, 255, 255)).build());
      this.redstoneShape = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Redstone Shape Mode")).description("Which parts should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.rsColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Redstone Side Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 0, 0, 50)).build());
      this.rlColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Redstone Line Color")).description("Color is the visual perception of different wavelengths of light as hue, saturation, and brightness")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
      this.pistonTime = 0L;
      this.redstoneTime = 0L;
      this.mineTime = 0L;
      this.minedThisTick = false;
      this.pistonPlaced = false;
      this.redstonePlaced = false;
      this.mined = false;
      this.pistonPos = null;
      this.redstonePos = null;
      this.pistonDir = null;
      this.pistonData = null;
      this.redstoneData = null;
      this.lastPiston = null;
      this.lastRedstone = null;
      this.lastDirection = null;
      this.startPos = null;
      this.currentPos = null;
   }

   public void onActivate() {
      this.lastPiston = null;
      this.lastRedstone = null;
      this.lastDirection = null;
      this.startPos = null;
      this.redstonePlaced = false;
      this.pistonPlaced = false;
      this.mined = false;
   }

   @EventHandler(
      priority = 200
   )
   private void onTick(TickEvent.Pre event) {
      this.minedThisTick = false;
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.startPos != null && (Boolean)this.toggleMove.get() && !this.startPos.equals(this.currentPos)) {
            this.toggle();
            this.sendToggledMsg("enemy moved");
         } else {
            this.update();
            if (this.pistonPos == null) {
               this.lastPiston = null;
               this.lastRedstone = this.redstonePos;
               this.lastDirection = this.pistonDir;
            } else {
               event.renderer.box(this.getBox(this.pistonPos), (Color)this.psColor.get(), (Color)this.plColor.get(), (ShapeMode)this.pistonShape.get(), 0);
               event.renderer.box(this.getBox(this.redstonePos), (Color)this.rsColor.get(), (Color)this.rlColor.get(), (ShapeMode)this.redstoneShape.get(), 0);
               if ((double)(System.currentTimeMillis() - this.mineTime) > (Double)this.mpDelay.get() * 1000.0 && this.redstonePlaced && this.pistonPlaced && this.mined || !this.pistonPos.equals(this.lastPiston) || !this.redstonePos.equals(this.lastRedstone) || !this.pistonDir.equals(this.lastDirection)) {
                  this.redstonePlaced = false;
                  this.pistonPlaced = false;
                  this.mined = false;
               }

               this.lastPiston = this.pistonPos;
               this.lastRedstone = this.redstonePos;
               this.lastDirection = this.pistonDir;
               if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
                  this.placePiston();
                  this.placeRedstone();
                  this.mineUpdate();
               }
            }
         }
      }
   }

   private void placePiston() {
      if (!this.pistonPlaced) {
         class_1268 hand = this.getHand(class_1802.field_8249);
         boolean available = hand != null;
         if (!available) {
            switch ((SwitchMode)this.pistonSwitch.get()) {
               case Silent:
                  available = InvUtils.findInHotbar(new class_1792[]{class_1802.field_8249}).found();
                  break;
               case PickSilent:
               case InvSwitch:
                  available = InvUtils.find(new class_1792[]{class_1802.field_8249}).found();
            }
         }

         if (available) {
            if (this.mc.field_1724.method_24828()) {
               if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(this.pistonPos)), (entity) -> {
                  return !entity.method_7325() && !(entity instanceof class_1542);
               })) {
                  if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(this.pistonData.pos(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "piston"}))) {
                     this.sendPacket(new class_2828.class_2831(this.pistonDir.method_10144(), Managers.ROTATION.lastDir[1], Managers.ON_GROUND.isOnGround()));
                     boolean switched = false;
                     if (hand == null) {
                        switch ((SwitchMode)this.pistonSwitch.get()) {
                           case Silent:
                              InvUtils.swap(InvUtils.findInHotbar(new class_1792[]{class_1802.field_8249}).slot(), true);
                              switched = true;
                              break;
                           case PickSilent:
                              switched = InventoryUtils.pickSwitch(InvUtils.find(new class_1792[]{class_1802.field_8249}).slot());
                              break;
                           case InvSwitch:
                              switched = InventoryUtils.invSwitch(InvUtils.find(new class_1792[]{class_1802.field_8249}).slot());
                        }
                     }

                     if (hand != null || switched) {
                        hand = hand == null ? class_1268.field_5808 : hand;
                        this.placeBlock(hand, this.pistonData.pos().method_46558(), this.pistonData.dir(), this.pistonData.pos());
                        this.pistonTime = System.currentTimeMillis();
                        this.pistonPlaced = true;
                        if ((Boolean)this.pistonSwing.get()) {
                           this.clientSwing((SwingHand)this.pistonHand.get(), hand);
                        }

                        if (switched) {
                           switch ((SwitchMode)this.pistonSwitch.get()) {
                              case Silent:
                                 InvUtils.swapBack();
                                 break;
                              case PickSilent:
                                 InventoryUtils.pickSwapBack();
                                 break;
                              case InvSwitch:
                                 InventoryUtils.swapBack();
                           }
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private void placeRedstone() {
      if (this.pistonPlaced && !this.redstonePlaced) {
         if (!((double)(System.currentTimeMillis() - this.pistonTime) < (Double)this.prDelay.get() * 1000.0)) {
            class_1268 hand = this.getHand(((Redstone)this.redstone.get()).i);
            boolean available = hand != null;
            if (!available) {
               switch ((SwitchMode)this.redstoneSwitch.get()) {
                  case Silent:
                     available = InvUtils.findInHotbar(new class_1792[]{((Redstone)this.redstone.get()).i}).found();
                     break;
                  case PickSilent:
                  case InvSwitch:
                     available = InvUtils.find(new class_1792[]{((Redstone)this.redstone.get()).i}).found();
               }
            }

            if (available) {
               if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || Managers.ROTATION.start(this.redstoneData.pos(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "restone"}))) {
                  boolean switched = false;
                  if (hand == null) {
                     switch ((SwitchMode)this.redstoneSwitch.get()) {
                        case Silent:
                           InvUtils.swap(InvUtils.findInHotbar(new class_1792[]{((Redstone)this.redstone.get()).i}).slot(), true);
                           switched = true;
                           break;
                        case PickSilent:
                           switched = InventoryUtils.pickSwitch(InvUtils.find(new class_1792[]{((Redstone)this.redstone.get()).i}).slot());
                           break;
                        case InvSwitch:
                           switched = InventoryUtils.invSwitch(InvUtils.find(new class_1792[]{((Redstone)this.redstone.get()).i}).slot());
                     }
                  }

                  if (hand != null || switched) {
                     hand = hand == null ? class_1268.field_5808 : hand;
                     this.placeBlock(hand, this.redstoneData.pos().method_46558(), this.redstoneData.dir(), this.redstoneData.pos());
                     if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                        Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "redstone"}));
                     }

                     this.redstonePlaced = true;
                     this.redstoneTime = System.currentTimeMillis();
                     if ((Boolean)this.redstoneSwing.get()) {
                        this.clientSwing((SwingHand)this.redstoneHand.get(), hand);
                     }

                     if (switched) {
                        switch ((SwitchMode)this.redstoneSwitch.get()) {
                           case Silent:
                              InvUtils.swapBack();
                              break;
                           case PickSilent:
                              InventoryUtils.pickSwapBack();
                              break;
                           case InvSwitch:
                              InventoryUtils.swapBack();
                        }
                     }

                  }
               }
            }
         }
      }
   }

   private class_238 getBox(class_2338 pos) {
      return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1));
   }

   private void mineUpdate() {
      if (this.pistonPlaced && this.redstonePlaced) {
         if (!this.minedThisTick) {
            if (!((double)(System.currentTimeMillis() - this.redstoneTime) < (Double)this.rmDelay.get() * 1000.0)) {
               if (this.redstonePos != null) {
                  if (this.redstone.get() != HolePush.Redstone.Torch || this.mc.field_1687.method_8320(this.redstonePos).method_26204() instanceof class_2459) {
                     if (this.redstone.get() != HolePush.Redstone.Block || this.mc.field_1687.method_8320(this.redstonePos).method_26204() == class_2246.field_10002) {
                        if (!Modules.get().isActive(AutoMine.class) || !this.redstonePos.equals(((AutoMine)Modules.get().get(AutoMine.class)).targetPos())) {
                           AutoMine autoMine = (AutoMine)Modules.get().get(AutoMine.class);
                           if (autoMine.isActive()) {
                              if (this.redstonePos.equals(autoMine.targetPos())) {
                                 return;
                              }

                              autoMine.onStart(this.redstonePos);
                           } else {
                              class_2350 mineDir = SettingUtils.getPlaceOnDirection(this.redstonePos);
                              if (mineDir != null) {
                                 this.sendPacket(new class_2846(class_2847.field_12968, this.redstonePos, mineDir));
                                 this.sendPacket(new class_2846(class_2847.field_12973, this.redstonePos, mineDir));
                              }
                           }

                           if (!this.mined) {
                              this.mineTime = System.currentTimeMillis();
                           }

                           this.mined = true;
                           this.minedThisTick = true;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void update() {
      this.pistonPos = null;
      Iterator var1 = this.mc.field_1687.method_18456().iterator();

      while(var1.hasNext()) {
         class_742 player = (class_742)var1.next();
         if (!Friends.get().isFriend(player) && player != this.mc.field_1724 && !(this.mc.field_1724.method_5739(player) > 10.0F) && !(player.method_6032() <= 0.0F) && !player.method_7325()) {
            if (!LemonUtils.solid2(player.method_24515()) && (Boolean)this.onlyHole.get() && HoleUtils.getHole(player.method_24515(), true, true, false, 1, true).type == HoleType.NotHole) {
               return;
            }

            this.updatePos(player);
            if (this.pistonPos != null) {
               return;
            }
         }
      }

   }

   private void updatePos(class_1657 player) {
      class_2338 eyePos = class_2338.method_49638(player.method_33571());
      if (!LemonUtils.solid2(eyePos.method_10084())) {
         Iterator var3 = class_2353.field_11062.method_29716().sorted(Comparator.comparingDouble((d) -> {
            return eyePos.method_10093(d).method_46558().method_1022(this.mc.field_1724.method_33571());
         })).toList().iterator();

         while(true) {
            class_2350 dir;
            class_2338 pos;
            do {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  dir = (class_2350)var3.next();
                  this.resetPos();
                  pos = eyePos.method_10093(dir);
               } while(!this.upCheck(pos));
            } while(!LemonUtils.replaceable(pos) && !(this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2665) && this.mc.field_1687.method_8320(pos).method_26204() != class_2246.field_10008);

            if (!LemonUtils.solid2(eyePos.method_10093(dir.method_10153())) && !LemonUtils.solid2(eyePos.method_10093(dir.method_10153()).method_10084()) && LemonUtils.solid2(eyePos.method_10093(dir.method_10153()).method_10074())) {
               PlaceData data = SettingUtils.getPlaceData(pos);
               if (data != null && data.valid()) {
                  this.pistonData = data;
                  this.pistonDir = dir;
                  this.updateRedstone(pos);
                  if (this.redstonePos != null) {
                     if (this.startPos == null) {
                        this.startPos = player.method_24515();
                     }

                     this.currentPos = player.method_24515();
                     this.pistonPos = pos;
                     return;
                  }
               }
            }
         }
      }
   }

   private void updateRedstone(class_2338 pos) {
      Iterator var2;
      class_2350 direction;
      class_2338 position;
      if (this.redstone.get() == HolePush.Redstone.Torch) {
         var2 = Arrays.stream(class_2350.values()).sorted(Comparator.comparingDouble((i) -> {
            return pos.method_10093(i).method_46558().method_1022(this.mc.field_1724.method_33571());
         })).toList().iterator();

         do {
            do {
               do {
                  do {
                     do {
                        if (!var2.hasNext()) {
                           this.redstonePos = null;
                           return;
                        }

                        direction = (class_2350)var2.next();
                     } while(direction == this.pistonDir.method_10153());
                  } while(direction == class_2350.field_11033);
               } while(direction == class_2350.field_11036);

               position = pos.method_10093(direction);
            } while(!LemonUtils.replaceable(position) && !(this.mc.field_1687.method_8320(position).method_26204() instanceof class_2459));

            this.redstoneData = SettingUtils.getPlaceDataAND(position, (d) -> {
               if (d == class_2350.field_11036 && !LemonUtils.solid(position.method_10074())) {
                  return false;
               } else {
                  return direction != d.method_10153();
               }
            }, (b) -> {
               if (pos.equals(b)) {
                  return false;
               } else if (this.mc.field_1687.method_8320(b).method_26204() instanceof class_2527) {
                  return false;
               } else {
                  return !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2665) && !(this.mc.field_1687.method_8320(b).method_26204() instanceof class_2671);
               }
            });
         } while(!this.redstoneData.valid() || !SettingUtils.inPlaceRange(this.redstoneData.pos()) || !SettingUtils.inMineRange(position));

         this.redstonePos = position;
      } else {
         var2 = Arrays.stream(class_2350.values()).sorted(Comparator.comparingDouble((i) -> {
            return pos.method_10093(i).method_46558().method_1022(this.mc.field_1724.method_33571());
         })).toList().iterator();

         while(true) {
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        this.redstonePos = null;
                        return;
                     }

                     direction = (class_2350)var2.next();
                  } while(direction == this.pistonDir.method_10153());
               } while(direction == class_2350.field_11033);

               position = pos.method_10093(direction);
            } while(!LemonUtils.replaceable(position) && this.mc.field_1687.method_8320(position).method_26204() != class_2246.field_10002);

            if (!EntityUtils.intersectsWithEntity(class_238.method_19316(new class_3341(position)), (entity) -> {
               return !entity.method_7325() && entity instanceof class_1657;
            })) {
               Objects.requireNonNull(pos);
               this.redstoneData = SettingUtils.getPlaceDataOR(position, pos::equals);
               if (this.redstoneData.valid()) {
                  this.redstonePos = position;
                  return;
               }
            }
         }
      }
   }

   private boolean upCheck(class_2338 pos) {
      double dx = this.mc.field_1724.method_33571().field_1352 - (double)pos.method_10263() - 0.5;
      double dz = this.mc.field_1724.method_33571().field_1350 - (double)pos.method_10260() - 0.5;
      return Math.sqrt(dx * dx + dz * dz) > Math.abs(this.mc.field_1724.method_33571().field_1351 - (double)pos.method_10264() - 0.5);
   }

   private boolean isRedstone(class_2338 pos) {
      return this.mc.field_1687.method_8320(pos).method_26219();
   }

   private boolean blocked(class_2338 pos) {
      class_2248 b = this.mc.field_1687.method_8320(pos).method_26204();
      if (b == class_2246.field_10008) {
         return false;
      } else if (b == class_2246.field_10379) {
         return false;
      } else if (b == class_2246.field_10523) {
         return false;
      } else if (b instanceof class_2358) {
         return false;
      } else {
         return !(this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2189);
      }
   }

   private class_1268 getHand(class_1792 item) {
      return Managers.HOLDING.isHolding(item) ? class_1268.field_5808 : (this.mc.field_1724.method_6079().method_7909() == item ? class_1268.field_5810 : null);
   }

   private void resetPos() {
      this.pistonPos = null;
      this.redstonePos = null;
      this.pistonDir = null;
      this.pistonData = null;
      this.redstoneData = null;
   }

   public static enum Redstone {
      Torch(class_1802.field_8530, class_2246.field_10523),
      Block(class_1802.field_8793, class_2246.field_10002);

      public final class_1792 i;
      public final class_2248 b;

      private Redstone(class_1792 i, class_2248 b) {
         this.i = i;
         this.b = b;
      }

      // $FF: synthetic method
      private static Redstone[] $values() {
         return new Redstone[]{Torch, Block};
      }
   }

   public static enum SwitchMode {
      Disabled,
      Silent,
      PickSilent,
      InvSwitch;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Disabled, Silent, PickSilent, InvSwitch};
      }
   }
}
