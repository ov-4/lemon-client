package dev.lemonclient.addon.gui.themes.rounded;

import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorAccount;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorHorizontalSeparator;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorLabel;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorModule;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorMultiLabel;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorQuad;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorSection;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorTooltip;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorTopBar;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorVerticalSeparator;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorView;
import dev.lemonclient.addon.gui.themes.rounded.widgets.WMeteorWindow;
import dev.lemonclient.addon.gui.themes.rounded.widgets.input.WMeteorDropdown;
import dev.lemonclient.addon.gui.themes.rounded.widgets.input.WMeteorSlider;
import dev.lemonclient.addon.gui.themes.rounded.widgets.input.WMeteorTextBox;
import dev.lemonclient.addon.gui.themes.rounded.widgets.pressable.WMeteorButton;
import dev.lemonclient.addon.gui.themes.rounded.widgets.pressable.WMeteorCheckbox;
import dev.lemonclient.addon.gui.themes.rounded.widgets.pressable.WMeteorFavorite;
import dev.lemonclient.addon.gui.themes.rounded.widgets.pressable.WMeteorMinus;
import dev.lemonclient.addon.gui.themes.rounded.widgets.pressable.WMeteorPlus;
import dev.lemonclient.addon.gui.themes.rounded.widgets.pressable.WMeteorTriangle;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.DefaultSettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.WAccount;
import meteordevelopment.meteorclient.gui.widgets.WHorizontalSeparator;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WQuad;
import meteordevelopment.meteorclient.gui.widgets.WTooltip;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.gui.widgets.WVerticalSeparator;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class LemonClientGuiTheme extends GuiTheme {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgColors;
   private final SettingGroup sgTextColors;
   private final SettingGroup sgBackgroundColors;
   private final SettingGroup sgOutline;
   private final SettingGroup sgSeparator;
   private final SettingGroup sgScrollbar;
   private final SettingGroup sgSlider;
   private final SettingGroup sgStarscript;
   public final Setting scale;
   public final Setting moduleAlignment;
   public final Setting categoryIcons;
   public final Setting meteorRainbow;
   public final Setting hideHUD;
   public final Setting round;
   public final Setting accentColor;
   public final Setting checkboxColor;
   public final Setting plusColor;
   public final Setting minusColor;
   public final Setting favoriteColor;
   public final Setting textColor;
   public final Setting textSecondaryColor;
   public final Setting textHighlightColor;
   public final Setting titleTextColor;
   public final Setting loggedInColor;
   public final Setting placeholderColor;
   public final ThreeStateColorSetting backgroundColor;
   public final Setting moduleBackground;
   public final ThreeStateColorSetting outlineColor;
   public final Setting separatorText;
   public final Setting separatorCenter;
   public final Setting separatorEdges;
   public final ThreeStateColorSetting scrollbarColor;
   public final ThreeStateColorSetting sliderHandle;
   public final Setting sliderLeft;
   public final Setting sliderRight;
   private final Setting starscriptText;
   private final Setting starscriptBraces;
   private final Setting starscriptParenthesis;
   private final Setting starscriptDots;
   private final Setting starscriptCommas;
   private final Setting starscriptOperators;
   private final Setting starscriptStrings;
   private final Setting starscriptNumbers;
   private final Setting starscriptKeywords;
   private final Setting starscriptAccessedObjects;

   public LemonClientGuiTheme() {
      super("Lemon Client");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgColors = this.settings.createGroup("Colors");
      this.sgTextColors = this.settings.createGroup("Text");
      this.sgBackgroundColors = this.settings.createGroup("Background");
      this.sgOutline = this.settings.createGroup("Outline");
      this.sgSeparator = this.settings.createGroup("Separator");
      this.sgScrollbar = this.settings.createGroup("Scrollbar");
      this.sgSlider = this.settings.createGroup("Slider");
      this.sgStarscript = this.settings.createGroup("Starscript");
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Scale of the GUI.")).defaultValue(1.0).min(0.75).sliderMin(0.75).sliderMax(4.0).onSliderRelease().onChanged((aDouble) -> {
         if (MeteorClient.mc.field_1755 instanceof WidgetScreen) {
            ((WidgetScreen)MeteorClient.mc.field_1755).invalidate();
         }

      })).build());
      this.moduleAlignment = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("module-alignment")).description("How module titles are aligned.")).defaultValue(AlignmentX.Center)).build());
      this.categoryIcons = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("category-icons")).description("Adds item icons to module categories.")).defaultValue(true)).build());
      this.meteorRainbow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Bg Meteor Rainbow")).description("Enable rainbow background meteor color.")).defaultValue(false)).build());
      this.hideHUD = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hide-HUD")).description("Hide HUD when in GUI.")).defaultValue(false)).onChanged((v) -> {
         if (MeteorClient.mc.field_1755 instanceof WidgetScreen) {
            MeteorClient.mc.field_1690.field_1842 = v;
         }

      })).build());
      this.round = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("round")).description("How much windows should be rounded")).defaultValue(5)).min(0).max(20).sliderMin(0).sliderMax(15).build());
      this.accentColor = this.color("accent", "Main color of the GUI.", new SettingColor(130, 110, 255));
      this.checkboxColor = this.color("checkbox", "Color of checkbox.", new SettingColor(130, 110, 255));
      this.plusColor = this.color("plus", "Color of plus button.", new SettingColor(255, 255, 255));
      this.minusColor = this.color("minus", "Color of minus button.", new SettingColor(255, 255, 255));
      this.favoriteColor = this.color("favorite", "Color of checked favorite button.", new SettingColor(255, 255, 0));
      this.textColor = this.color(this.sgTextColors, "text", "Color of text.", new SettingColor(255, 255, 255));
      this.textSecondaryColor = this.color(this.sgTextColors, "text-secondary-text", "Color of secondary text.", new SettingColor(150, 150, 150));
      this.textHighlightColor = this.color(this.sgTextColors, "text-highlight", "Color of text highlighting.", new SettingColor(45, 125, 245, 100));
      this.titleTextColor = this.color(this.sgTextColors, "title-text", "Color of title text.", new SettingColor(255, 255, 255));
      this.loggedInColor = this.color(this.sgTextColors, "logged-in-text", "Color of logged in account name.", new SettingColor(45, 225, 45));
      this.placeholderColor = this.color(this.sgTextColors, "placeholder", "Color of placeholder text.", new SettingColor(255, 255, 255, 20));
      this.backgroundColor = new ThreeStateColorSetting(this.sgBackgroundColors, "background", new SettingColor(20, 20, 20, 200), new SettingColor(30, 30, 30, 200), new SettingColor(40, 40, 40, 200));
      this.moduleBackground = this.color(this.sgBackgroundColors, "module-background", "Color of module background when active.", new SettingColor(50, 50, 50));
      this.outlineColor = new ThreeStateColorSetting(this.sgOutline, "outline", new SettingColor(0, 0, 0), new SettingColor(10, 10, 10), new SettingColor(20, 20, 20));
      this.separatorText = this.color(this.sgSeparator, "separator-text", "Color of separator text", new SettingColor(255, 255, 255));
      this.separatorCenter = this.color(this.sgSeparator, "separator-center", "Center color of separators.", new SettingColor(255, 255, 255));
      this.separatorEdges = this.color(this.sgSeparator, "separator-edges", "Color of separator edges.", new SettingColor(225, 225, 225, 150));
      this.scrollbarColor = new ThreeStateColorSetting(this.sgScrollbar, "Scrollbar", new SettingColor(30, 30, 30, 200), new SettingColor(40, 40, 40, 200), new SettingColor(50, 50, 50, 200));
      this.sliderHandle = new ThreeStateColorSetting(this.sgSlider, "slider-handle", new SettingColor(0, 255, 180), new SettingColor(0, 240, 165), new SettingColor(0, 225, 150));
      this.sliderLeft = this.color(this.sgSlider, "slider-left", "Color of slider left part.", new SettingColor(0, 150, 80));
      this.sliderRight = this.color(this.sgSlider, "slider-right", "Color of slider right part.", new SettingColor(50, 50, 50));
      this.starscriptText = this.color(this.sgStarscript, "starscript-text", "Color of text in Starscript code.", new SettingColor(169, 183, 198));
      this.starscriptBraces = this.color(this.sgStarscript, "starscript-braces", "Color of braces in Starscript code.", new SettingColor(150, 150, 150));
      this.starscriptParenthesis = this.color(this.sgStarscript, "starscript-parenthesis", "Color of parenthesis in Starscript code.", new SettingColor(169, 183, 198));
      this.starscriptDots = this.color(this.sgStarscript, "starscript-dots", "Color of dots in starscript code.", new SettingColor(169, 183, 198));
      this.starscriptCommas = this.color(this.sgStarscript, "starscript-commas", "Color of commas in starscript code.", new SettingColor(169, 183, 198));
      this.starscriptOperators = this.color(this.sgStarscript, "starscript-operators", "Color of operators in Starscript code.", new SettingColor(169, 183, 198));
      this.starscriptStrings = this.color(this.sgStarscript, "starscript-strings", "Color of strings in Starscript code.", new SettingColor(106, 135, 89));
      this.starscriptNumbers = this.color(this.sgStarscript, "starscript-numbers", "Color of numbers in Starscript code.", new SettingColor(104, 141, 187));
      this.starscriptKeywords = this.color(this.sgStarscript, "starscript-keywords", "Color of keywords in Starscript code.", new SettingColor(204, 120, 50));
      this.starscriptAccessedObjects = this.color(this.sgStarscript, "starscript-accessed-objects", "Color of accessed objects (before a dot) in Starscript code.", new SettingColor(152, 118, 170));
      this.settingsFactory = new DefaultSettingsWidgetFactory(this);
   }

   private Setting color(SettingGroup group, String name, String description, SettingColor color) {
      return group.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name(name + "-color")).description(description)).defaultValue(color).build());
   }

   private Setting color(String name, String description, SettingColor color) {
      return this.color(this.sgColors, name, description, color);
   }

   public WWindow window(WWidget icon, String title) {
      return (WWindow)this.w(new WMeteorWindow(icon, title));
   }

   public WLabel label(String text, boolean title, double maxWidth) {
      return maxWidth == 0.0 ? (WLabel)this.w(new WMeteorLabel(text, title)) : (WLabel)this.w(new WMeteorMultiLabel(text, title, maxWidth));
   }

   public WHorizontalSeparator horizontalSeparator(String text) {
      return (WHorizontalSeparator)this.w(new WMeteorHorizontalSeparator(text));
   }

   public WVerticalSeparator verticalSeparator() {
      return (WVerticalSeparator)this.w(new WMeteorVerticalSeparator());
   }

   protected WButton button(String text, GuiTexture texture) {
      return (WButton)this.w(new WMeteorButton(text, texture));
   }

   public WMinus minus() {
      return (WMinus)this.w(new WMeteorMinus());
   }

   public WPlus plus() {
      return (WPlus)this.w(new WMeteorPlus());
   }

   public WCheckbox checkbox(boolean checked) {
      return (WCheckbox)this.w(new WMeteorCheckbox(checked));
   }

   public WSlider slider(double value, double min, double max) {
      return (WSlider)this.w(new WMeteorSlider(value, min, max));
   }

   public WTextBox textBox(String text, String placeholder, CharFilter filter, Class renderer) {
      return (WTextBox)this.w(new WMeteorTextBox(text, placeholder, filter, renderer));
   }

   public WDropdown dropdown(Object[] values, Object value) {
      return (WDropdown)this.w(new WMeteorDropdown(values, value));
   }

   public WTriangle triangle() {
      return (WTriangle)this.w(new WMeteorTriangle());
   }

   public WTooltip tooltip(String text) {
      return (WTooltip)this.w(new WMeteorTooltip(text));
   }

   public WView view() {
      return (WView)this.w(new WMeteorView());
   }

   public WSection section(String title, boolean expanded, WWidget headerWidget) {
      return (WSection)this.w(new WMeteorSection(title, expanded, headerWidget));
   }

   public WAccount account(WidgetScreen screen, Account account) {
      return (WAccount)this.w(new WMeteorAccount(screen, account));
   }

   public WWidget module(Module module) {
      return this.w(new WMeteorModule(module));
   }

   public WQuad quad(Color color) {
      return (WQuad)this.w(new WMeteorQuad(color));
   }

   public WTopBar topBar() {
      return (WTopBar)this.w(new WMeteorTopBar());
   }

   public WFavorite favorite(boolean checked) {
      return (WFavorite)this.w(new WMeteorFavorite(checked));
   }

   public Color textColor() {
      return (Color)this.textColor.get();
   }

   public Color textSecondaryColor() {
      return (Color)this.textSecondaryColor.get();
   }

   public Color starscriptTextColor() {
      return (Color)this.starscriptText.get();
   }

   public Color starscriptBraceColor() {
      return (Color)this.starscriptBraces.get();
   }

   public Color starscriptParenthesisColor() {
      return (Color)this.starscriptParenthesis.get();
   }

   public Color starscriptDotColor() {
      return (Color)this.starscriptDots.get();
   }

   public Color starscriptCommaColor() {
      return (Color)this.starscriptCommas.get();
   }

   public Color starscriptOperatorColor() {
      return (Color)this.starscriptOperators.get();
   }

   public Color starscriptStringColor() {
      return (Color)this.starscriptStrings.get();
   }

   public Color starscriptNumberColor() {
      return (Color)this.starscriptNumbers.get();
   }

   public Color starscriptKeywordColor() {
      return (Color)this.starscriptKeywords.get();
   }

   public Color starscriptAccessedObjectColor() {
      return (Color)this.starscriptAccessedObjects.get();
   }

   public TextRenderer textRenderer() {
      return TextRenderer.get();
   }

   public double scale(double value) {
      return value * (Double)this.scale.get();
   }

   public boolean categoryIcons() {
      return (Boolean)this.categoryIcons.get();
   }

   public boolean hideHUD() {
      return (Boolean)this.hideHUD.get();
   }

   public int roundAmount() {
      return (Integer)this.round.get();
   }

   public class ThreeStateColorSetting {
      private final Setting normal;
      private final Setting hovered;
      private final Setting pressed;

      public ThreeStateColorSetting(SettingGroup group, String name, SettingColor c1, SettingColor c2, SettingColor c3) {
         this.normal = LemonClientGuiTheme.this.color(group, name, "Color of " + name + ".", c1);
         this.hovered = LemonClientGuiTheme.this.color(group, "hovered-" + name, "Color of " + name + " when hovered.", c2);
         this.pressed = LemonClientGuiTheme.this.color(group, "pressed-" + name, "Color of " + name + " when pressed.", c3);
      }

      public SettingColor get() {
         return (SettingColor)this.normal.get();
      }

      public SettingColor get(boolean pressed, boolean hovered, boolean bypassDisableHoverColor) {
         if (pressed) {
            return (SettingColor)this.pressed.get();
         } else {
            return !hovered || !bypassDisableHoverColor && LemonClientGuiTheme.this.disableHoverColor ? (SettingColor)this.normal.get() : (SettingColor)this.hovered.get();
         }
      }

      public SettingColor get(boolean pressed, boolean hovered) {
         return this.get(pressed, hovered, false);
      }
   }
}
