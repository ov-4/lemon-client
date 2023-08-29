package dev.lemonclient.addon.modules.misc;

import dev.lemonclient.addon.LemonClient;
import dev.lemonclient.addon.LemonModule;
import java.util.Iterator;
import java.util.Random;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;

public class AutoMoan extends LemonModule {
   private final SettingGroup sgGeneral;
   private final Setting moanmode;
   private final Setting iFriends;
   private final Setting delay;
   private int lastNum;
   private double timer;
   private static final String[] Submissive = new String[]{"fuck me harder daddy", "deeper! daddy deeper!", "Fuck yes your so big!", "I love your cock %s!", "Do not stop fucking my ass before i cum!", "Oh your so hard for me", "Want to widen my ass up %s?", "I love you daddy", "Make my bussy pop", "%s loves my bussy so much", "i made %s cum so hard with my tight bussy", "Your cock is so big and juicy daddy!", "Please fuck me as hard as you can", "im %s's personal femboy cumdupster!", "Please shoot your hot load deep inside me daddy!", "I love how %s's dick feels inside of me!", "%s gets so hard when he sees my ass!", "%s really loves fucking my ass really hard!", "why wont u say the last message"};
   private static final String[] Dominant = new String[]{"Be a good boy for daddy", "I love pounding your ass %s!", "Give your bussy to daddy!", "I love how you drip pre-cum while i fuck your ass %s", "Slurp up and down my cock like a good boy", "Come and jump on daddy's cock %s", "I love how you look at me while you suck me off %s", "%s looks so cute when i fuck him", "%s's bussy is so incredibly tight!", "%s takes dick like the good boy he is", "I love how you shake your ass on my dick", "%s moans so cutely when i fuck his ass", "%s is the best cumdupster there is!", "%s is always horny and ready for his daddy's dick", "My dick gets rock hard every time i see %s", "why wont u say the last message"};
   private final Random r;

   public AutoMoan() {
      super(LemonClient.Misc, "Auto Moan", "Moans sexual things to the closest person.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.moanmode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Message Mode")).description("What kind of messages to send.")).defaultValue(AutoMoan.MoanMode.Submissive)).build());
      this.iFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore Friends")).description("Doesn't send messages targeted to friends.")).defaultValue(true)).build());
      this.delay = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Delay")).description("Tick delay between moans.")).defaultValue(50.0).min(0.0).sliderRange(0.0, 100.0).build());
      this.timer = 0.0;
      this.r = new Random();
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      this.timer = Math.min((Double)this.delay.get(), this.timer + event.frameTime);
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ++this.timer;
      if (this.mc.field_1724 != null && this.mc.field_1687 != null && this.timer >= (Double)this.delay.get()) {
         this.MOAN();
         this.timer = 0.0;
      }

   }

   private void MOAN() {
      class_1657 target = this.getClosest();
      if (target != null) {
         String name = target.method_5477().getString();
         int num;
         switch ((MoanMode)this.moanmode.get()) {
            case Submissive:
               num = this.r.nextInt(0, Submissive.length - 1);
               if (num == this.lastNum) {
                  num = num < Submissive.length - 1 ? num + 1 : 0;
               }

               this.lastNum = num;
               ChatUtils.sendPlayerMsg(Submissive[num].replace("%s", name));
               break;
            case Dominant:
               num = this.r.nextInt(0, Dominant.length - 1);
               if (num == this.lastNum) {
                  num = num < Dominant.length - 1 ? num + 1 : 0;
               }

               this.lastNum = num;
               ChatUtils.sendPlayerMsg(Dominant[num].replace("%s", name));
         }

      }
   }

   private class_1657 getClosest() {
      assert this.mc.field_1724 != null && this.mc.field_1687 != null;

      class_1657 closest = null;
      float distance = -1.0F;
      if (!this.mc.field_1687.method_18456().isEmpty()) {
         Iterator var3 = this.mc.field_1687.method_18456().iterator();

         while(true) {
            class_1657 player;
            do {
               do {
                  do {
                     if (!var3.hasNext()) {
                        return closest;
                     }

                     player = (class_1657)var3.next();
                  } while(player == this.mc.field_1724);
               } while((Boolean)this.iFriends.get() && Friends.get().isFriend(player));
            } while(closest != null && !(this.mc.field_1724.method_19538().method_1022(player.method_19538()) < (double)distance));

            closest = player;
            distance = (float)this.mc.field_1724.method_19538().method_1022(player.method_19538());
         }
      } else {
         return closest;
      }
   }

   public static enum MoanMode {
      Dominant,
      Submissive;

      // $FF: synthetic method
      private static MoanMode[] $values() {
         return new MoanMode[]{Dominant, Submissive};
      }
   }
}
