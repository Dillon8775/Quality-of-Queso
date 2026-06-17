package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.option.eum.general.MenuButton;
import net.dillon.qualityofqueso.util.ModConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A separate option instance, which is not affected by multi-server configs.
 */
public class UniversalOptions {
    public static final ModOptionsHandler INSTANCE = new ModOptionsHandler();
    private final Universal universal = new Universal();
    private final Mixins mixins = new Mixins();

    public Universal getUniversal() {
        return this.universal;
    }

    public Mixins getMixins() {
        return this.mixins;
    }

    public static class Universal {
        public MenuButton menuButton = MenuButton.EVERYWHERE;
        public boolean multiServerConfigs = false;
        public List<String> blacklistedServers = new ArrayList<>();
    }

    public static class Mixins {
        public boolean titleScreenMixin = true;
        public boolean pauseScreenMixin = true;
        public boolean fogMixins = true;
        public boolean fovEffectsMixin = true;
        public boolean redArmorTintMixin = true;
        public boolean clockManagerMixin = true;
    }

    public static class ModOptionsHandler extends BaseOptions<UniversalOptions> {

        protected ModOptionsHandler() {
            super(ModConstants.DEFAULT_UNIVERSAL_CONFIG_FILE_NAME);
            this.load();
        }

        @Override
        protected UniversalOptions createDefault() {
            return new UniversalOptions();
        }

        @Override
        protected Class<UniversalOptions> getConfigClass() {
            return UniversalOptions.class;
        }
    }
}