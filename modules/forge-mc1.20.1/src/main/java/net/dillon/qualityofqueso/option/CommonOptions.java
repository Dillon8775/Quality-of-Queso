package net.dillon.qualityofqueso.option;

/**
 * Common options.
 */
public class CommonOptions {
    public boolean itemFrameSearching = true;

    public static final ModServerOptionsHandler COMMON_OPTIONS = new ModServerOptionsHandler();

    public static class ModServerOptionsHandler extends BaseOptions<CommonOptions> {

        protected ModServerOptionsHandler() {
            super(BaseOptions.DEFAULT_COMMON_FILE_NAME);
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