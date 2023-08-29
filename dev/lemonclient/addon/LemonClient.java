package dev.lemonclient.addon;

import dev.lemonclient.addon.commands.CoordsCommand;
import dev.lemonclient.addon.commands.UUIDCommand;
import dev.lemonclient.addon.gui.themes.rounded.LemonClientGuiTheme;
import dev.lemonclient.addon.hud.ArmorHud;
import dev.lemonclient.addon.hud.CatGirl;
import dev.lemonclient.addon.hud.GearHud;
import dev.lemonclient.addon.hud.Keys;
import dev.lemonclient.addon.hud.Logo;
import dev.lemonclient.addon.hud.NotificationsHud;
import dev.lemonclient.addon.hud.PacketHud;
import dev.lemonclient.addon.hud.Radar;
import dev.lemonclient.addon.hud.TargetHud;
import dev.lemonclient.addon.hud.ToastNotifications;
import dev.lemonclient.addon.modules.combat.AimAssist;
import dev.lemonclient.addon.modules.combat.AnchorAuraPlus;
import dev.lemonclient.addon.modules.combat.AntiAim;
import dev.lemonclient.addon.modules.combat.AntiRegear;
import dev.lemonclient.addon.modules.combat.AutoCrystal;
import dev.lemonclient.addon.modules.combat.AutoCrystalPlus;
import dev.lemonclient.addon.modules.combat.AutoHoleFill;
import dev.lemonclient.addon.modules.combat.AutoHoleFillPlus;
import dev.lemonclient.addon.modules.combat.AutoMine;
import dev.lemonclient.addon.modules.combat.AutoTrapPlus;
import dev.lemonclient.addon.modules.combat.BedBombV2;
import dev.lemonclient.addon.modules.combat.BedBombV4;
import dev.lemonclient.addon.modules.combat.Burrow;
import dev.lemonclient.addon.modules.combat.CevBreaker;
import dev.lemonclient.addon.modules.combat.CityBreaker;
import dev.lemonclient.addon.modules.combat.CityMiner;
import dev.lemonclient.addon.modules.combat.HolePush;
import dev.lemonclient.addon.modules.combat.HoleSnap;
import dev.lemonclient.addon.modules.combat.KillAura;
import dev.lemonclient.addon.modules.combat.NewSurround;
import dev.lemonclient.addon.modules.combat.OffHandPlus;
import dev.lemonclient.addon.modules.combat.PistonCrystal;
import dev.lemonclient.addon.modules.combat.SelfProtect;
import dev.lemonclient.addon.modules.combat.SelfTrapPlus;
import dev.lemonclient.addon.modules.combat.SpeedMine;
import dev.lemonclient.addon.modules.combat.Strafe;
import dev.lemonclient.addon.modules.combat.StrafePlus;
import dev.lemonclient.addon.modules.combat.SurroundPlus;
import dev.lemonclient.addon.modules.combat.TNTAura;
import dev.lemonclient.addon.modules.combat.TickShift;
import dev.lemonclient.addon.modules.combat.XPThrower;
import dev.lemonclient.addon.modules.info.AnteroTaateli;
import dev.lemonclient.addon.modules.info.AutoEz;
import dev.lemonclient.addon.modules.info.AutoLoadKit;
import dev.lemonclient.addon.modules.info.GroupChat;
import dev.lemonclient.addon.modules.info.KillEffects;
import dev.lemonclient.addon.modules.info.Notifications;
import dev.lemonclient.addon.modules.misc.AntiCrawl;
import dev.lemonclient.addon.modules.misc.AutoCraft;
import dev.lemonclient.addon.modules.misc.AutoMoan;
import dev.lemonclient.addon.modules.misc.AutoPearl;
import dev.lemonclient.addon.modules.misc.AutoWither;
import dev.lemonclient.addon.modules.misc.Automation;
import dev.lemonclient.addon.modules.misc.BedCrafter;
import dev.lemonclient.addon.modules.misc.BreakESP;
import dev.lemonclient.addon.modules.misc.CustomFOV;
import dev.lemonclient.addon.modules.misc.EFlyBypass;
import dev.lemonclient.addon.modules.misc.ElytraFlyPlus;
import dev.lemonclient.addon.modules.misc.FlightPlus;
import dev.lemonclient.addon.modules.misc.FogRenderer;
import dev.lemonclient.addon.modules.misc.ForceSneak;
import dev.lemonclient.addon.modules.misc.InvMove;
import dev.lemonclient.addon.modules.misc.LightsOut;
import dev.lemonclient.addon.modules.misc.LogSpots;
import dev.lemonclient.addon.modules.misc.MidClickExtra;
import dev.lemonclient.addon.modules.misc.MultiTask;
import dev.lemonclient.addon.modules.misc.NoHurtCam;
import dev.lemonclient.addon.modules.misc.PacketFly;
import dev.lemonclient.addon.modules.misc.PingSpoof;
import dev.lemonclient.addon.modules.misc.ScaffoldPlus;
import dev.lemonclient.addon.modules.misc.ShieldBypass;
import dev.lemonclient.addon.modules.misc.ShulkerDupe;
import dev.lemonclient.addon.modules.misc.SkinBlinker;
import dev.lemonclient.addon.modules.misc.SoundModifier;
import dev.lemonclient.addon.modules.misc.SprintPlus;
import dev.lemonclient.addon.modules.misc.StepPlus;
import dev.lemonclient.addon.modules.misc.StrictNoSlow;
import dev.lemonclient.addon.modules.misc.Suicide;
import dev.lemonclient.addon.modules.misc.SwingAnimation;
import dev.lemonclient.addon.modules.misc.Twerk;
import dev.lemonclient.addon.modules.misc.WeakNotifier;
import dev.lemonclient.addon.modules.settings.FacingSettings;
import dev.lemonclient.addon.modules.settings.RangeSettings;
import dev.lemonclient.addon.modules.settings.RaytraceSettings;
import dev.lemonclient.addon.modules.settings.RotationSettings;
import dev.lemonclient.addon.modules.settings.ServerSettings;
import dev.lemonclient.addon.modules.settings.SwingSettings;
import dev.lemonclient.addon.utils.Version;
import dev.lemonclient.addon.utils.Wrapper;
import dev.lemonclient.addon.utils.player.DeathUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.class_1802;

public class LemonClient extends MeteorAddon {
   public static final Category Combat;
   public static final Category Misc;
   public static final Category Info;
   public static final Category Settings;
   public static final HudGroup HUD_GROUP;
   public static final String ADDON;
   public static final String MOD_ID = "lemon-client";
   public static final Version VERSION;
   public static final ModMetadata MOD_META;
   public static final String COLOR = "Color is the visual perception of different wavelengths of light as hue, saturation, and brightness";

   public void onInitialize() {
      log("Initializing LemonClient");
      this.initializeModules(Modules.get());
      this.initializeCommands();
      this.initializeHud(Hud.get());
      GuiThemes.add(new LemonClientGuiTheme());
      DeathUtils.init();
      Wrapper.init();
      log("Finish initializing LemonClient");
   }

   private void initializeModules(Modules modules) {
      modules.add(new AimAssist());
      modules.add(new AnchorAuraPlus());
      modules.add(new AntiAim());
      modules.add(new AntiRegear());
      modules.add(new AutoCrystal());
      modules.add(new AutoCrystalPlus());
      modules.add(new AutoHoleFill());
      modules.add(new AutoHoleFillPlus());
      modules.add(new AutoMine());
      modules.add(new AutoTrapPlus());
      modules.add(new BedBombV2());
      modules.add(new BedBombV4());
      modules.add(new Burrow());
      modules.add(new CevBreaker());
      modules.add(new CityBreaker());
      modules.add(new CityMiner());
      modules.add(new HolePush());
      modules.add(new HoleSnap());
      modules.add(new KillAura());
      modules.add(new NewSurround());
      modules.add(new OffHandPlus());
      modules.add(new PistonCrystal());
      modules.add(new SelfProtect());
      modules.add(new SelfTrapPlus());
      modules.add(new SpeedMine());
      modules.add(new Strafe());
      modules.add(new StrafePlus());
      modules.add(new SurroundPlus());
      modules.add(new TickShift());
      modules.add(new TNTAura());
      modules.add(new AnteroTaateli());
      modules.add(new AutoEz());
      modules.add(new AutoLoadKit());
      modules.add(new GroupChat());
      modules.add(new KillEffects());
      modules.add(new Notifications());
      modules.add(new AntiCrawl());
      modules.add(new AutoCraft());
      modules.add(new Automation());
      modules.add(new AutoMoan());
      modules.add(new AutoPearl());
      modules.add(new AutoWither());
      modules.add(new BedCrafter());
      modules.add(new BreakESP());
      modules.add(new CustomFOV());
      modules.add(new EFlyBypass());
      modules.add(new ElytraFlyPlus());
      modules.add(new FlightPlus());
      modules.add(new FogRenderer());
      modules.add(new ForceSneak());
      modules.add(new InvMove());
      modules.add(new LightsOut());
      modules.add(new LogSpots());
      modules.add(new MidClickExtra());
      modules.add(new MultiTask());
      modules.add(new NoHurtCam());
      modules.add(new PacketFly());
      modules.add(new PingSpoof());
      modules.add(new ScaffoldPlus());
      modules.add(new ShieldBypass());
      modules.add(new ShulkerDupe());
      modules.add(new SkinBlinker());
      modules.add(new SoundModifier());
      modules.add(new SprintPlus());
      modules.add(new StepPlus());
      modules.add(new StrictNoSlow());
      modules.add(new Suicide());
      modules.add(new SwingAnimation());
      modules.add(new Twerk());
      modules.add(new WeakNotifier());
      modules.add(new XPThrower());
      modules.add(new FacingSettings());
      modules.add(new RangeSettings());
      modules.add(new RaytraceSettings());
      modules.add(new RotationSettings());
      modules.add(new ServerSettings());
      modules.add(new SwingSettings());
   }

   private void initializeCommands() {
      Commands.add(new CoordsCommand());
      Commands.add(new UUIDCommand());
   }

   private void initializeHud(Hud hud) {
      hud.register(ArmorHud.INFO);
      hud.register(CatGirl.INFO);
      hud.register(NotificationsHud.INFO);
      hud.register(GearHud.INFO);
      hud.register(Keys.INFO);
      hud.register(Logo.INFO);
      hud.register(PacketHud.INFO);
      hud.register(Radar.INFO);
      hud.register(TargetHud.INFO);
      hud.register(ToastNotifications.INFO);
   }

   public void onRegisterCategories() {
      Modules.registerCategory(Combat);
      Modules.registerCategory(Misc);
      Modules.registerCategory(Info);
      Modules.registerCategory(Settings);
   }

   public static void log(String message) {
      System.out.println("[" + ADDON + "] " + message);
   }

   public String getWebsite() {
      return "http://lemonclient.cn/";
   }

   public String getPackage() {
      return "dev.lemonclient.addon";
   }

   static {
      Combat = new Category("Combat+", class_1802.field_8288.method_7854());
      Misc = new Category("Misc+", class_1802.field_8270.method_7854());
      Info = new Category("Info", class_1802.field_8575.method_7854());
      Settings = new Category("Settings", class_1802.field_8281.method_7854());
      HUD_GROUP = new HudGroup("LemonClient");
      MOD_META = ((ModContainer)FabricLoader.getInstance().getModContainer("lemon-client").orElseThrow()).getMetadata();
      ADDON = MOD_META.getName();
      String versionString = MOD_META.getVersion().getFriendlyString();
      if (versionString.contains("-")) {
         versionString = versionString.split("-")[0];
      }

      if (versionString.equals("${version}")) {
         versionString = "0.0.0";
      }

      VERSION = new Version(versionString);
   }
}
