package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.eum.general.MenuButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A separate option instance, which is not affected by multi-server configs.
 */
public class UniversalOptions {
    public static final UniversalOptionsHandler INSTANCE = new UniversalOptionsHandler();
    public MenuButton menuButton = MenuButton.EVERYWHERE;
    public boolean multiServerConfigs = false;
    public List<String> blacklistedServers = new ArrayList<>();

    public static class UniversalOptionsHandler extends ModBaseOptions<UniversalOptions> {

        protected UniversalOptionsHandler() {
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