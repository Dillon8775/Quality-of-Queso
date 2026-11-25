package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.helper.ModConstants;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.dillonlib.util.BaseOptions.guardedInstance;

/**
 * A separate option instance, which is not affected by multi-server configs.
 */
public class UniversalOptions {
    private static final UniversalOptionsHandler INSTANCE = new UniversalOptionsHandler();
    public List<String> blacklistedServers = new ArrayList<>();

    public static UniversalOptionsHandler getInstance() {
        return guardedInstance(INSTANCE);
    }

    public static class UniversalOptionsHandler extends ModBaseOptionsHandler<UniversalOptions> {

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