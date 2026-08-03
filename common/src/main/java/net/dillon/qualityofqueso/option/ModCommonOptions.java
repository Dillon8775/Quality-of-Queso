package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.util.ModConstants;

/**
 * Common options, shared on server and client.
 */
public class ModCommonOptions {
    public static final CommonOptionsHandler INSTANCE = new CommonOptionsHandler();
    public boolean itemFrameSearching = true;
    public boolean inventoryLocking = true;
    public boolean optimizeItemArgument = true;

    public static class CommonOptionsHandler extends ModBaseOptions<ModCommonOptions> {

        protected CommonOptionsHandler() {
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
}