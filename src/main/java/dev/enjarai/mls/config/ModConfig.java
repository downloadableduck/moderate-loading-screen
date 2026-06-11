//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.enjarai.mls.config;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.ui.core.Color;

import java.util.List;
import java.util.Objects;

public class ModConfig extends ConfigWrapper<ModConfigDef> {
    public final Keys keys = new Keys();
    public final StackingConfig_ stackingConfig;
    private final Option<Color> backgroundColor;
    private final Option<Byte> logoOpacity;
    private final Option<Byte> barOpacity;
    private final Option<Boolean> showTater;
    private final Option<Boolean> modsOnlyOnce;
    private final Option<Boolean> hideLibraries;
    private final Option<List<String>> modIdBlacklist;
    private final Option<Integer> iconSize;
    private final Option<ScreenTypes> screenType;
    private final Option<Orientation> orientation;
    private final Option<Integer> stackingConfig_cycleSeconds;

    private ModConfig() {
        super(ModConfigDef.class);
        this.backgroundColor = this.optionForKey(this.keys.backgroundColor);
        this.logoOpacity = this.optionForKey(this.keys.logoOpacity);
        this.barOpacity = this.optionForKey(this.keys.barOpacity);
        this.showTater = this.optionForKey(this.keys.showTater);
        this.modsOnlyOnce = this.optionForKey(this.keys.modsOnlyOnce);
        this.hideLibraries = this.optionForKey(this.keys.hideLibraries);
        this.modIdBlacklist = this.optionForKey(this.keys.modIdBlacklist);
        this.iconSize = this.optionForKey(this.keys.iconSize);
        this.screenType = this.optionForKey(this.keys.screenType);
        this.orientation = this.optionForKey(this.keys.orientation);
        this.stackingConfig_cycleSeconds = this.optionForKey(this.keys.stackingConfig_cycleSeconds);
        this.stackingConfig = new StackingConfig_();
    }

    private ModConfig(ConfigWrapper.BuilderConsumer consumer) {
        super(ModConfigDef.class, consumer);
        this.backgroundColor = this.optionForKey(this.keys.backgroundColor);
        this.logoOpacity = this.optionForKey(this.keys.logoOpacity);
        this.barOpacity = this.optionForKey(this.keys.barOpacity);
        this.showTater = this.optionForKey(this.keys.showTater);
        this.modsOnlyOnce = this.optionForKey(this.keys.modsOnlyOnce);
        this.hideLibraries = this.optionForKey(this.keys.hideLibraries);
        this.modIdBlacklist = this.optionForKey(this.keys.modIdBlacklist);
        this.iconSize = this.optionForKey(this.keys.iconSize);
        this.screenType = this.optionForKey(this.keys.screenType);
        this.orientation = this.optionForKey(this.keys.orientation);
        this.stackingConfig_cycleSeconds = this.optionForKey(this.keys.stackingConfig_cycleSeconds);
        this.stackingConfig = new StackingConfig_();
    }

    public static ModConfig createAndLoad() {
        ModConfig wrapper = new ModConfig();
        wrapper.load();
        return wrapper;
    }

    public static ModConfig createAndLoad(ConfigWrapper.BuilderConsumer consumer) {
        ModConfig wrapper = new ModConfig(consumer);
        wrapper.load();
        return wrapper;
    }

    public Color backgroundColor() {
        return this.backgroundColor.value();
    }

    public void backgroundColor(Color value) {
        this.backgroundColor.set(value);
    }

    public byte logoOpacity() {
        return this.logoOpacity.value();
    }

    public void logoOpacity(byte value) {
        this.logoOpacity.set(value);
    }

    public byte barOpacity() {
        return this.barOpacity.value();
    }

    public void barOpacity(byte value) {
        this.barOpacity.set(value);
    }

    public boolean showTater() {
        return this.showTater.value();
    }

    public void showTater(boolean value) {
        this.showTater.set(value);
    }

    public boolean modsOnlyOnce() {
        return this.modsOnlyOnce.value();
    }

    public void modsOnlyOnce(boolean value) {
        this.modsOnlyOnce.set(value);
    }

    public boolean hideLibraries() {
        return this.hideLibraries.value();
    }

    public void hideLibraries(boolean value) {
        this.hideLibraries.set(value);
    }

    public List<String> modIdBlacklist() {
        return this.modIdBlacklist.value();
    }

    public void modIdBlacklist(List<String> value) {
        this.modIdBlacklist.set(value);
    }

    public int iconSize() {
        return this.iconSize.value();
    }

    public void iconSize(int value) {
        this.iconSize.set(value);
    }

    public ScreenTypes screenType() {
        return this.screenType.value();
    }

    public void screenType(ScreenTypes value) {
        this.screenType.set(value);
    }

    public Orientation orientation() {
        return this.orientation.value();
    }

    public void orientation(Orientation value) {
        this.orientation.set(value);
    }

    public interface StackingConfig {
        int cycleSeconds();

        void cycleSeconds(int var1);
    }

    public static class Keys {
        public final Option.Key backgroundColor = new Option.Key("backgroundColor");
        public final Option.Key logoOpacity = new Option.Key("logoOpacity");
        public final Option.Key barOpacity = new Option.Key("barOpacity");
        public final Option.Key showTater = new Option.Key("showTater");
        public final Option.Key modsOnlyOnce = new Option.Key("modsOnlyOnce");
        public final Option.Key hideLibraries = new Option.Key("hideLibraries");
        public final Option.Key modIdBlacklist = new Option.Key("modIdBlacklist");
        public final Option.Key iconSize = new Option.Key("iconSize");
        public final Option.Key screenType = new Option.Key("screenType");
        public final Option.Key orientation = new Option.Key("orientation");
        public final Option.Key stackingConfig_cycleSeconds = new Option.Key("stackingConfig.cycleSeconds");
    }

    public class StackingConfig_ implements StackingConfig {
        public StackingConfig_() {
            Objects.requireNonNull(ModConfig.this);
            super();
        }

        public int cycleSeconds() {
            return ModConfig.this.stackingConfig_cycleSeconds.value();
        }

        public void cycleSeconds(int value) {
            ModConfig.this.stackingConfig_cycleSeconds.set(value);
        }
    }
}
