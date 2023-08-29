package dev.lemonclient.addon.modules.combat;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import dev.lemonclient.addon.enums.RotationType;
import dev.lemonclient.addon.enums.SwingHand;
import dev.lemonclient.addon.managers.Managers;
import dev.lemonclient.addon.utils.LemonUtils;
import dev.lemonclient.addon.utils.SettingUtils;
import dev.lemonclient.addon.utils.player.DamageInfo;
import dev.lemonclient.addon.utils.player.InventoryUtils;
import dev.lemonclient.addon.utils.player.PlaceData;
import dev.lemonclient.addon.utils.player.RotationUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1748;
import net.minecraft.class_2244;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2828;
import net.minecraft.class_2350.class_2353;

public class BedBombV4 extends LemonModule {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlacing;
   private final SettingGroup sgDamage;
   private final SettingGroup sgRender;
   private final Setting fiveB;
   private final Setting pauseEat;
   private final Setting doubleInteract;
   private final Setting logicMode;
   private final Setting switchMode;
   private final Setting rotMode;
   private final Setting speedMode;
   private final Setting speed;
   private final Setting damageSpeed;
   private final Setting maxSpeed;
   private final Setting minDmg;
   private final Setting maxDmg;
   private final Setting maxFriendDmg;
   private final Setting minRatio;
   private final Setting minFriendRatio;
   private final Setting forcePop;
   private final Setting antiPop;
   private final Setting antiFriendPop;
   private final Setting friendSacrifice;
   private final Setting placeSwing;
   private final Setting placeHand;
   private final Setting interactSwing;
   private final Setting interactHand;
   public final Setting shapeMode;
   private final Setting lineColor;
   public final Setting color;
   private final Setting fLineColor;
   public final Setting fColor;
   private int lastIndex;
   private int length;
   private long tickTime;
   private double bestDmg;
   private long lastTime;
   private class_2338 placePos;
   private class_2350 bedDir;
   private PlaceData placeData;
   private class_2338 calcPos;
   private class_2350 calcDir;
   private PlaceData calcData;
   private class_2338 renderPos;
   private class_2350 renderDir;
   private class_2338[] blocks;
   private final List targets;
   private final List friends;
   private final List beds;
   private double timer;
   private double dmg;
   private double enemyHP;
   private double self;
   private double selfHP;
   private double friend;
   private double friendHP;

   public BedBombV4() {
      super(LemonClient.Combat, "Bed Bomb V4", "Automatically places and breaks beds to cause damage to your opponents in strict anticheat servers.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlacing = this.settings.createGroup("Placing");
      this.sgDamage = this.settings.createGroup("Damage");
      this.sgRender = this.settings.createGroup("Render");
      this.fiveB = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("5B5T")).description("For example requires floor for both bed blocks and allows placing inside entities.")).defaultValue(false)).build());
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause Eat")).description("Pauses when you are eating.")).defaultValue(true)).build());
      this.doubleInteract = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Double Interact")).description("Clicks both bed blocks every time.")).defaultValue(true)).build());
      this.logicMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Logic Mode")).description("Logic for bullying kids.")).defaultValue(BedBombV4.LogicMode.PlaceBreak)).build());
      this.switchMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Switch Mode")).description("Method of switching. Silent is the most reliable.")).defaultValue(BedBombV4.SwitchMode.Silent)).build());
      this.rotMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Rotation Mode")).description("Packet- Sends 1 rotation packet for each bed. Manager- Modifies movement packets to set rotation.")).defaultValue(BedBombV4.RotationMode.Manager)).build());
      this.speedMode = this.sgPlacing.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Speed Mode")).description("Normal mode should be used in everywhere else than 5B.")).defaultValue(BedBombV4.SpeedMode.Normal)).build());
      this.speed = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Speed")).description("How many beds to blow up every second.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 20.0).visible(() -> {
         return this.speedMode.get() == BedBombV4.SpeedMode.Normal;
      })).build());
      this.damageSpeed = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Damage Speed Factor")).description("Sets speed to damage multiplied by factor.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 20.0).visible(() -> {
         return this.speedMode.get() == BedBombV4.SpeedMode.Damage;
      })).build());
      this.maxSpeed = this.sgPlacing.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Damage Speed")).description("Maximum speed for damage mode.")).defaultValue(12.0).min(0.0).sliderRange(0.0, 20.0).visible(() -> {
         return this.speedMode.get() == BedBombV4.SpeedMode.Damage;
      })).build());
      this.minDmg = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Damage")).description("Minimum damage to place.")).defaultValue(8.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.maxDmg = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Max Damage")).description("Maximum self damage to place.")).defaultValue(6.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.maxFriendDmg = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Max Friend Damage")).description("Maximum friend damage to place.")).defaultValue(6.0).min(0.0).sliderRange(0.0, 20.0).build());
      this.minRatio = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Damage Ratio")).description("Minimum damage ratio between self damage and enemy damage.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.minFriendRatio = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Min Friend Damage Ratio")).description("Minimum damage ratio between friend damage and enemy damage.")).defaultValue(2.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.forcePop = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Force Pop")).description("Ignores damage checks if enemy would pop after x explodes.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.antiPop = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Anti Pop")).description("Cancels actions if you would pop after x explodes.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.antiFriendPop = this.sgDamage.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Anti Friend Pop")).description("Cancels actions if any friend would pop after x explodes.")).defaultValue(1.0).min(0.0).sliderRange(0.0, 10.0).build());
      this.friendSacrifice = this.sgDamage.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Friend Sacrifice")).description("Kills your friend if you can also kill any enemy with same bed.")).defaultValue(true)).build());
      this.placeSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Place Swing")).description("Renders swing animation when placing the crafting table.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Place Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      Setting var10003 = this.placeSwing;
      Objects.requireNonNull(var10003);
      this.placeHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.interactSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Interact Swing")).description("Renders swing animation when interacting with a block.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Interact Hand")).description("Which hand should be swung.")).defaultValue(SwingHand.RealHand);
      var10003 = this.interactSwing;
      Objects.requireNonNull(var10003);
      this.interactHand = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Shape Mode")).description("Which parts of the render should be rendered.")).defaultValue(ShapeMode.Both)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Head Line Color")).description("Line color of head block.")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
      this.color = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Head Side Color")).description("Side color of head block.")).defaultValue(new SettingColor(255, 0, 0, 50)).build());
      this.fLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Feet Line Color")).description("Line color of feet block")).defaultValue(new SettingColor(255, 0, 0, 255)).build());
      this.fColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("Feet Side Color")).description("Side color of feet block")).defaultValue(new SettingColor(255, 0, 0, 50)).build());
      this.lastIndex = 0;
      this.length = 0;
      this.tickTime = -1L;
      this.bestDmg = 0.0;
      this.lastTime = 0L;
      this.placePos = null;
      this.bedDir = null;
      this.placeData = null;
      this.calcPos = null;
      this.calcDir = null;
      this.calcData = null;
      this.renderPos = null;
      this.renderDir = null;
      this.blocks = new class_2338[0];
      this.targets = new ArrayList();
      this.friends = new ArrayList();
      this.beds = new ArrayList();
      this.timer = 0.0;
   }

   @EventHandler(
      priority = 200
   )
   private void onTickPre(TickEvent.Post event) {
      this.calculate(this.length - 1);
      this.renderPos = this.calcPos;
      this.placePos = this.calcPos;
      this.renderDir = this.calcDir;
      this.bedDir = this.calcDir;
      this.placeData = this.calcData;
      this.blocks = this.getBlocks(this.mc.field_1724.method_33571(), Math.max(SettingUtils.getPlaceRange(), SettingUtils.getPlaceWallsRange()));
      this.tickTime = System.currentTimeMillis();
      this.length = this.blocks.length;
      this.lastIndex = 0;
      this.bestDmg = 0.0;
      this.calcPos = null;
      this.calcDir = null;
      this.calcData = null;
      this.updateTargets();
   }

   @EventHandler(
      priority = 200
   )
   private void onRender(Render3DEvent event) {
      double delta = (double)((float)(System.currentTimeMillis() - this.lastTime) / 1000.0F);
      this.timer += delta;
      this.lastTime = System.currentTimeMillis();
      List toRemove = new ArrayList();
      this.beds.forEach((bed) -> {
         if (System.currentTimeMillis() - bed.time > 500L) {
            toRemove.add(bed);
         }

      });
      List var10001 = this.beds;
      Objects.requireNonNull(var10001);
      toRemove.forEach(var10001::remove);
      if (this.tickTime >= 0L && this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (this.pauseCheck()) {
            this.update();
         }

         int index = Math.min((int)Math.ceil((double)((float)(System.currentTimeMillis() - this.tickTime) / 50.0F * (float)this.length)), this.length - 1);
         this.calculate(index);
         if (this.renderPos != null && this.pauseCheck()) {
            event.renderer.box(this.bedBox(this.renderPos), (Color)this.color.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
            if (this.renderDir != null) {
               event.renderer.box(this.bedBox(this.renderPos.method_10093(this.renderDir)), (Color)this.fColor.get(), (Color)this.fLineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
            }
         }

      }
   }

   private boolean pauseCheck() {
      return !(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115();
   }

   private void calculate(int index) {
      label53:
      for(int i = this.lastIndex; i < index; ++i) {
         class_2338 pos = this.blocks[i];
         this.damageCalc(pos);
         if (this.dmgCheck()) {
            Iterator var4 = class_2353.field_11062.iterator();

            while(true) {
               class_2350 dir;
               PlaceData data;
               do {
                  do {
                     do {
                        do {
                           if (!var4.hasNext()) {
                              continue label53;
                           }

                           dir = (class_2350)var4.next();
                           data = this.getData(pos, dir);
                        } while(!data.valid());
                     } while(!LemonUtils.replaceable(pos.method_10093(dir)) && !(this.mc.field_1687.method_8320(pos.method_10093(dir)).method_26204() instanceof class_2244));
                  } while(!SettingUtils.inPlaceRange(data.pos()));
               } while(!(Boolean)this.fiveB.get() && EntityUtils.intersectsWithEntity(new class_238(pos.method_10093(dir)), (entity) -> {
                  return !(entity instanceof class_1542);
               }));

               this.calcData = data;
               this.calcPos = pos;
               this.calcDir = dir;
               this.bestDmg = this.dmg;
            }
         }
      }

      this.lastIndex = index;
   }

   private void updateTargets() {
      this.friends.clear();
      this.targets.clear();
      List players = new ArrayList();
      double closestDist = 1000.0;

      label57:
      for(int i = 3; i > 0; --i) {
         class_1657 closest = null;
         Iterator var8 = this.mc.field_1687.method_18456().iterator();

         while(true) {
            double dist;
            class_1657 player;
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var8.hasNext()) {
                              if (closest != null) {
                                 players.add(closest);
                                 if (Friends.get().isFriend(closest)) {
                                    this.friends.add(closest);
                                 } else {
                                    this.targets.add(closest);
                                 }
                              }
                              continue label57;
                           }

                           player = (class_1657)var8.next();
                        } while(players.contains(player));
                     } while(Friends.get().isFriend(player));
                  } while(player == this.mc.field_1724);

                  dist = (double)player.method_5739(this.mc.field_1724);
               } while(dist > 15.0);
            } while(closest != null && !(dist < closestDist));

            closestDist = dist;
            closest = player;
         }
      }

   }

   private class_2338[] getBlocks(class_243 middle, double radius) {
      ArrayList result = new ArrayList();
      int i = (int)Math.ceil(radius);

      for(int x = -i; x <= i; ++x) {
         for(int y = -i; y <= i; ++y) {
            for(int z = -i; z <= i; ++z) {
               class_2338 pos = class_2338.method_49638(middle).method_10069(x, y, z);
               if ((LemonUtils.replaceable(pos) || this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2244) && (!(Boolean)this.fiveB.get() || this.mc.field_1687.method_8320(pos.method_10074()).method_26204() != class_2246.field_10124 && !this.mc.field_1687.method_8320(pos.method_10074()).method_31709()) && this.inRangeToTargets(pos)) {
                  result.add(pos);
               }
            }
         }
      }

      return (class_2338[])result.toArray(new class_2338[0]);
   }

   private boolean inRangeToTargets(class_2338 pos) {
      Iterator var2 = this.targets.iterator();

      class_1657 target;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         target = (class_1657)var2.next();
      } while(!(target.method_19538().method_1031(0.0, 1.0, 0.0).method_1022(pos.method_46558()) < 3.5));

      return true;
   }

   private void update() {
      if (this.placePos != null && this.placeData != null && this.placeData.valid() && this.bedDir != null) {
         List in;
         if (this.logicMode.get() == BedBombV4.LogicMode.PlaceBreak) {
            in = this.interactUpdate();
            if (in != null && !in.isEmpty()) {
               in.forEach(this::removeBed);
            }

            if (this.timer <= 1.0 / this.getSpeed()) {
               return;
            }

            if (LemonUtils.replaceable(this.placePos) && LemonUtils.replaceable(this.placePos.method_10093(this.bedDir)) && this.placeUpdate()) {
               this.removeBed2(this.placePos);
               this.beds.add(new Bed(this.placePos, this.placePos.method_10093(this.bedDir), true, System.currentTimeMillis()));
               this.timer = 0.0;
            }
         } else {
            if (!this.isBed(this.placePos) && !this.isBed(this.placePos.method_10093(this.bedDir)) && this.placeUpdate()) {
               this.removeBed2(this.placePos);
               this.beds.add(new Bed(this.placePos, this.placePos.method_10093(this.bedDir), true, System.currentTimeMillis()));
            }

            if (this.timer <= 1.0 / this.getSpeed()) {
               return;
            }

            in = this.interactUpdate();
            if (in != null && !in.isEmpty()) {
               in.forEach(this::removeBed);
               this.timer = 0.0;
            }
         }

      }
   }

   private void removeBed(class_2338 pos) {
      List toRemove = new ArrayList();
      this.beds.forEach((bed) -> {
         if (bed.feetBlock.equals(pos) || bed.headBlock.equals(pos)) {
            toRemove.add(bed);
         }

      });
      toRemove.forEach((bed) -> {
         this.beds.remove(bed);
         this.beds.add(new Bed(bed.feetBlock, bed.headBlock, false, System.currentTimeMillis()));
      });
   }

   private void removeBed2(class_2338 pos) {
      List toRemove = new ArrayList();
      this.beds.forEach((bed) -> {
         if (bed.feetBlock.equals(pos) || bed.headBlock.equals(pos)) {
            toRemove.add(bed);
         }

      });
      List var10001 = this.beds;
      Objects.requireNonNull(var10001);
      toRemove.forEach(var10001::remove);
   }

   private void place(class_1268 hand) {
      this.placeBlock(hand, this.placeData.pos().method_46558(), this.placeData.dir(), this.placeData.pos());
      if ((Boolean)this.placeSwing.get()) {
         this.clientSwing((SwingHand)this.placeHand.get(), hand);
      }

   }

   private List interactUpdate() {
      if ((Boolean)this.doubleInteract.get()) {
         if (SettingUtils.shouldRotate(RotationType.Interact) && !Managers.ROTATION.start(this.placePos, (double)this.priority, RotationType.Interact, (long)Objects.hash(new Object[]{this.name + "explode"}))) {
            return null;
         } else {
            List list = new ArrayList();
            if (this.isBed(this.placePos) || this.isBed(this.placePos.method_10093(this.bedDir))) {
               if (SettingUtils.inPlaceRange(this.placePos) && this.interact(this.placePos)) {
                  list.add(this.placePos);
               }

               if (SettingUtils.inPlaceRange(this.placePos.method_10093(this.bedDir)) && this.interact(this.placePos.method_10093(this.bedDir))) {
                  list.add(this.placePos.method_10093(this.bedDir));
               }
            }

            if (SettingUtils.shouldRotate(RotationType.Interact)) {
               Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "explode"}));
            }

            return list;
         }
      } else {
         class_2338 interactPos = this.getInteractPos();
         if (interactPos == null) {
            return null;
         } else {
            class_2350 interactDir = SettingUtils.getPlaceOnDirection(interactPos);
            if (interactDir == null) {
               return null;
            } else if (SettingUtils.shouldRotate(RotationType.Interact) && !Managers.ROTATION.start(interactPos, (double)this.priority, RotationType.Interact, (long)Objects.hash(new Object[]{this.name + "explode"}))) {
               return null;
            } else {
               this.interactBlock(class_1268.field_5808, interactPos.method_46558(), interactDir, interactPos);
               if ((Boolean)this.interactSwing.get()) {
                  this.clientSwing((SwingHand)this.interactHand.get(), class_1268.field_5808);
               }

               if (SettingUtils.shouldRotate(RotationType.Interact)) {
                  Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "explode"}));
               }

               List list = new ArrayList();
               list.add(interactPos);
               return list;
            }
         }
      }
   }

   private boolean interact(class_2338 pos) {
      class_2350 dir = SettingUtils.getPlaceOnDirection(pos);
      if (dir == null) {
         return false;
      } else {
         this.interactBlock(class_1268.field_5808, pos.method_46558(), dir, pos);
         if ((Boolean)this.interactSwing.get()) {
            this.clientSwing((SwingHand)this.interactHand.get(), class_1268.field_5808);
         }

         return true;
      }
   }

   private class_2338 getInteractPos() {
      if (this.isBed(this.placePos.method_10093(this.bedDir)) && SettingUtils.inPlaceRange(this.placePos.method_10093(this.bedDir)) && SettingUtils.getPlaceOnDirection(this.placePos.method_10093(this.bedDir)) != null) {
         return this.placePos.method_10093(this.bedDir);
      } else {
         return this.isBed(this.placePos) && SettingUtils.inPlaceRange(this.placePos) && SettingUtils.getPlaceOnDirection(this.placePos) != null ? this.placePos : null;
      }
   }

   private boolean isBed(class_2338 pos) {
      Iterator var2 = this.beds.iterator();

      Bed bed;
      do {
         if (!var2.hasNext()) {
            return this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2244;
         }

         bed = (Bed)var2.next();
      } while(!bed.feetBlock.equals(pos) && !bed.headBlock.equals(pos));

      return bed.isBed;
   }

   private boolean placeUpdate() {
      class_1268 hand = Managers.HOLDING.getStack().method_7909() instanceof class_1748 ? class_1268.field_5808 : (this.mc.field_1724.method_6079().method_7909() instanceof class_1748 ? class_1268.field_5810 : null);
      int beds = hand == class_1268.field_5808 ? Managers.HOLDING.getStack().method_7947() : (hand == class_1268.field_5810 ? this.mc.field_1724.method_6079().method_7947() : 0);
      if (hand == null) {
         FindItemResult result;
         switch ((SwitchMode)this.switchMode.get()) {
            case Silent:
            case Normal:
               result = InvUtils.findInHotbar((item) -> {
                  return item.method_7909() instanceof class_1748;
               });
               beds = result.count();
               break;
            case PickSilent:
            case InvSwitch:
               result = InvUtils.find((item) -> {
                  return item.method_7909() instanceof class_1748;
               });
               beds = result.slot() >= 0 ? result.count() : -1;
         }
      }

      if (beds <= 0) {
         return false;
      } else if (SettingUtils.shouldRotate(RotationType.BlockPlace) && !Managers.ROTATION.start(this.placeData.pos(), (double)this.priority, RotationType.BlockPlace, (long)Objects.hash(new Object[]{this.name + "placing"}))) {
         return false;
      } else {
         boolean switched = hand != null;
         if (this.rotMode.get() == BedBombV4.RotationMode.Packet) {
            this.sendPacket(new class_2828.class_2831(this.bedDir.method_10153().method_10144(), Managers.ROTATION.lastDir[1], Managers.ON_GROUND.isOnGround()));
         } else {
            Managers.ROTATION.startYaw((double)this.bedDir.method_10153().method_10144(), (double)this.priority, RotationType.Other, (long)Objects.hash(new Object[]{this.name + "placing"}));
            if (Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.lastDir[0], (double)this.bedDir.method_10153().method_10144())) > 45.0) {
               return false;
            }
         }

         if (!switched) {
            FindItemResult result;
            switch ((SwitchMode)this.switchMode.get()) {
               case Silent:
               case Normal:
                  result = InvUtils.findInHotbar((item) -> {
                     return item.method_7909() instanceof class_1748;
                  });
                  InvUtils.swap(result.slot(), true);
                  switched = true;
                  break;
               case PickSilent:
                  result = InvUtils.find((item) -> {
                     return item.method_7909() instanceof class_1748;
                  });
                  switched = InventoryUtils.pickSwitch(result.slot());
                  break;
               case InvSwitch:
                  result = InvUtils.find((item) -> {
                     return item.method_7909() instanceof class_1748;
                  });
                  switched = InventoryUtils.invSwitch(result.slot());
            }
         }

         if (!switched) {
            return false;
         } else {
            this.place(hand == null ? class_1268.field_5808 : hand);
            if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
               Managers.ROTATION.end((long)Objects.hash(new Object[]{this.name + "placing"}));
            }

            if (hand == null) {
               switch ((SwitchMode)this.switchMode.get()) {
                  case Silent:
                     InvUtils.swapBack();
                  case Normal:
                  default:
                     break;
                  case PickSilent:
                     InventoryUtils.pickSwapBack();
                     break;
                  case InvSwitch:
                     InventoryUtils.swapBack();
               }
            }

            return true;
         }
      }
   }

   private boolean dmgCheck() {
      if (this.dmg < this.bestDmg) {
         return false;
      } else if (this.self * (Double)this.antiPop.get() >= this.selfHP) {
         return false;
      } else if (!(Boolean)this.friendSacrifice.get() && this.friendHP >= 0.0 && this.friend * (Double)this.antiFriendPop.get() >= this.friendHP) {
         return false;
      } else if (this.enemyHP >= 0.0 && this.dmg * (Double)this.forcePop.get() >= this.enemyHP) {
         return true;
      } else if (this.friendHP >= 0.0 && this.friend * (Double)this.antiFriendPop.get() >= this.friendHP) {
         return false;
      } else if (this.dmg < (Double)this.minDmg.get()) {
         return false;
      } else if (this.self > (Double)this.maxDmg.get()) {
         return false;
      } else if (this.friend > (Double)this.maxFriendDmg.get()) {
         return false;
      } else if (this.dmg / this.self < (Double)this.minRatio.get()) {
         return false;
      } else {
         return !(this.friendHP >= 0.0) || !(this.dmg / this.friend < (Double)this.minFriendRatio.get());
      }
   }

   private double getDmg(class_2338 pos) {
      double highest = -1.0;

      class_1657 target;
      for(Iterator var4 = this.targets.iterator(); var4.hasNext(); highest = Math.max(highest, DamageInfo.bedDamage(target, target.method_5829(), new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5), (class_2338)null))) {
         target = (class_1657)var4.next();
      }

      return highest;
   }

   private void damageCalc(class_2338 pos) {
      double highest = -1.0;
      double highestHP = -1.0;
      Iterator var6 = this.targets.iterator();

      class_1657 friend;
      while(var6.hasNext()) {
         friend = (class_1657)var6.next();
         if (!(friend.method_6032() <= 0.0F)) {
            highest = Math.max(highest, DamageInfo.bedDamage(friend, friend.method_5829(), new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5), (class_2338)null));
            highestHP = (double)(friend.method_6032() + friend.method_6067());
         }
      }

      this.dmg = highest;
      this.enemyHP = highestHP;
      this.self = DamageInfo.bedDamage(this.mc.field_1724, this.mc.field_1724.method_5829(), new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5), (class_2338)null);
      this.selfHP = (double)(this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067());
      highest = -1.0;
      highestHP = -1.0;
      var6 = this.friends.iterator();

      while(var6.hasNext()) {
         friend = (class_1657)var6.next();
         if (!(friend.method_6032() <= 0.0F)) {
            highest = Math.max(highest, DamageInfo.bedDamage(friend, friend.method_5829(), new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5), (class_2338)null));
            highestHP = (double)(friend.method_6032() + friend.method_6067());
         }
      }

      this.friend = highest;
      this.friendHP = highestHP;
   }

   private class_238 bedBox(class_2338 pos) {
      return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.5, (double)(pos.method_10260() + 1));
   }

   private PlaceData getData(class_2338 pos, class_2350 dir) {
      return (Boolean)this.fiveB.get() ? SettingUtils.getPlaceDataAND(pos.method_10093(dir), (direction) -> {
         return direction == class_2350.field_11033;
      }, (pos1) -> {
         return !(this.mc.field_1687.method_8320(pos1).method_26204() instanceof class_2244);
      }) : SettingUtils.getPlaceDataAND(pos.method_10093(dir), (direction) -> {
         return direction != dir;
      }, (pos1) -> {
         return !(this.mc.field_1687.method_8320(pos1).method_26204() instanceof class_2244);
      });
   }

   private double getSpeed() {
      switch ((SpeedMode)this.speedMode.get()) {
         case Normal:
            return (Double)this.speed.get();
         case Damage:
            if (this.placePos == null) {
               return (Double)this.maxSpeed.get();
            }

            double dmg = this.getDmg(this.placePos);
            return Math.min(dmg * (Double)this.damageSpeed.get(), (Double)this.maxSpeed.get());
         default:
            return 2.0;
      }
   }

   public static enum LogicMode {
      PlaceBreak,
      BreakPlace;

      // $FF: synthetic method
      private static LogicMode[] $values() {
         return new LogicMode[]{PlaceBreak, BreakPlace};
      }
   }

   public static enum SwitchMode {
      Silent,
      Normal,
      PickSilent,
      InvSwitch,
      Disabled;

      // $FF: synthetic method
      private static SwitchMode[] $values() {
         return new SwitchMode[]{Silent, Normal, PickSilent, InvSwitch, Disabled};
      }
   }

   public static enum RotationMode {
      Packet,
      Manager;

      // $FF: synthetic method
      private static RotationMode[] $values() {
         return new RotationMode[]{Packet, Manager};
      }
   }

   public static enum SpeedMode {
      Normal,
      Damage;

      // $FF: synthetic method
      private static SpeedMode[] $values() {
         return new SpeedMode[]{Normal, Damage};
      }
   }

   private static record Bed(class_2338 feetBlock, class_2338 headBlock, boolean isBed, long time) {
      private Bed(class_2338 feetBlock, class_2338 headBlock, boolean isBed, long time) {
         this.feetBlock = feetBlock;
         this.headBlock = headBlock;
         this.isBed = isBed;
         this.time = time;
      }

      public class_2338 feetBlock() {
         return this.feetBlock;
      }

      public class_2338 headBlock() {
         return this.headBlock;
      }

      public boolean isBed() {
         return this.isBed;
      }

      public long time() {
         return this.time;
      }
   }
}
