package net.dillon.qualityofqueso.option;

/**
 * Common options.
 */
public class CommonOptions {
    public boolean itemFrameSearching = true;

    public static final ModServerOptionsHandler COMMON_OPTIONS = new ModServerOptionsHandler();

    public static class ModServerOptionsHandler extends BaseOptions<CommonOptions> {

        protected ModServerOptionsHandler() {
            super("qualityofqueso-common_config.json");
            this.load();
        }

        @Override
        protected CommonOptions createDefault() {
            return new CommonOptions();
        }

        @Override
        protected Class<CommonOptions> getConfigClass() {
            return CommonOptions.class;
        }
    }
}