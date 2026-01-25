package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.base.BaseOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * A separate option instance, which is not affected by server-configs (client-side only).
 */
@OnlyIn(Dist.CLIENT)
public class UniversalOptions {
    public boolean applyFogFunction = true;
    public boolean multiServerConfigs = false;
    public List<String> blacklistedServers = new ArrayList<>();

    public static final ModOptionsHandler UNIVERSAL = new ModOptionsHandler();

    public static class ModOptionsHandler extends BaseOptions<UniversalOptions> {

        protected ModOptionsHandler() {
            super("qualityofqueso_client-universal.json");
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