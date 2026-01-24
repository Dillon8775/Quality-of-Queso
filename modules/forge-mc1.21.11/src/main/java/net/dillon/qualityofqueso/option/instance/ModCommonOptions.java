package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.base.BaseOptions;

/**
 * Common options, shared on server and client.
 */
public class ModCommonOptions {
    public boolean itemFrameSearching = true;

    public static final ModServerOptionsHandler COMMON = new ModServerOptionsHandler();

    public static class ModServerOptionsHandler extends BaseOptions<ModCommonOptions> {

        protected ModServerOptionsHandler() {
            super(BaseOptions.DEFAULT_COMMON_FILE_NAME);
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