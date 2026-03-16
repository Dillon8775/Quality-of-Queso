package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.base.BaseOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A separate option instance, which is not affected by server-configs (client-side only).
 */
public class UniversalOptions {
    public Main main = new Main();
    public Functions functions = new Functions();

    public static class Main {
        public boolean multiServerConfigs = false;
        public List<String> blacklistedServers = new ArrayList<>();
    }

    public static class Functions {
        public boolean applyFog = true;
        public boolean applyFovEffects = true;
    }

    public static final ModOptionsHandler UNIVERSAL = new ModOptionsHandler();
    
    public static class ModOptionsHandler extends BaseOptions<UniversalOptions> {

        protected ModOptionsHandler() {
            super("qualityofqueso-universal_config.json");
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