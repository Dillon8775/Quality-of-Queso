package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.option.eum.accessibility.MenuButton;
import net.dillon.qualityofqueso.util.ModConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A separate option instance, which is not affected by multi-server configs.
 */
public class UniversalOptions {
    public static final ModOptionsHandler INSTANCE = new ModOptionsHandler();

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

    public Main main = new Main();
    public Functions functions = new Functions();

    public static class Main {
        public MenuButton menuButton = MenuButton.EVERYWHERE;
        public boolean multiServerConfigs = false;
        public List<String> blacklistedServers = new ArrayList<>();
    }

    public static class Functions {
        public boolean applyFog = true;
        public boolean applyFovEffects = true;
        public boolean applyRedArmorTint = true;
    }
}