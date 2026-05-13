package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.util.ModConstants;

/**
 * Common options, shared on server and client.
 */
public class ModCommonOptions {
    public static final ModServerOptionsHandler INSTANCE = new ModServerOptionsHandler();

    public static class ModServerOptionsHandler extends BaseOptions<ModCommonOptions> {

        protected ModServerOptionsHandler() {
            super(ModConstants.DEFAULT_COMMON_CONFIG_FILE_NAME);
            this.load();
        }

        @Override
        protected ModCommonOptions createDefault() {
            return new ModCommonOptions();
        }

        @Override
        protected Class<ModCommonOptions> getConfigClass() {
            return ModCommonOptions.class;
        }
    }

    public boolean itemFrameSearching = true;
}