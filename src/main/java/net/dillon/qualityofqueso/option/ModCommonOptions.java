package net.dillon.qualityofqueso.option;

/**
 * Common options.
 */
public class ModCommonOptions {
    public boolean itemFrameSearching = true;

    public static final ModServerOptionsHandler COMMON_OPTIONS = new ModServerOptionsHandler();

    public static class ModServerOptionsHandler extends BaseOptions<ModCommonOptions> {

        protected ModServerOptionsHandler() {
            super("qualityofqueso-common_config.json");
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