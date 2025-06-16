package net.dillon.qualityofqueso.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class ModServerOptions {
    public boolean itemFrameSearchingOnServer = true;

    public static final ModServerOptionsHandler SERVER_OPTIONS = new ModServerOptionsHandler();

    public static class ModServerOptionsHandler extends BaseOptions<ModServerOptions> {

        protected ModServerOptionsHandler() {
            super("qualityofqueso-server_config.json");
            this.load();
        }

        @Override
        protected ModServerOptions createDefault() {
            return new ModServerOptions();
        }

        @Override
        protected Class<ModServerOptions> getConfigClass() {
            return ModServerOptions.class;
        }
    }
}