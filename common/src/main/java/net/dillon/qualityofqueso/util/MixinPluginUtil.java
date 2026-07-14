package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.MixinOptions;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.platform.PlatformName;
import net.dillon.qualityofqueso.platform.PlatformMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Utility class for Conditional mixin plugin.
 */
@SuppressWarnings("unchecked")
public class MixinPluginUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger("Quality of Queso/Mixin");
    private static final List<PredicateEntry> ENTRIES = List.of(
            new PredicateEntry(
                    new String[]{"fix.AbstractContainerScreenFix"},
                    MultiLoader.getPlatform().getPlatformName().equals(PlatformName.FABRIC)
                            && MultiLoader.getPlatform().isModLoaded(PlatformMod.VIAFABRICPLUS),
                    "ViaFabricPlus mod is loaded for fabric, disabling this mixin prevents a game crash."
            ),
            new PredicateEntry(
                    new String[]{"client.screen.TitleScreenMixin"},
                    !MixinOptions.INSTANCE.getInstance().titleScreenMixin,
                    "\"title_screen_mixin\" is disabled."
            ),
            new PredicateEntry(
                    new String[]{"client.screen.PauseScreenMixin"},
                    !MixinOptions.INSTANCE.getInstance().pauseScreenMixin,
                    "\"pause_screen_mixin\" is disabled."
            ),
            new PredicateEntry(
                    new String[]{"render.FogRendererMixin", "render.CameraMixin"},
                    !MixinOptions.INSTANCE.getInstance().fogMixins,
                    "\"fog_mixins\" are disabled."
            ),
            new PredicateEntry(
                    new String[]{"client.util.AbstractClientPlayerMixin"},
                    !MixinOptions.INSTANCE.getInstance().fovEffectsMixin,
                    "\"fov_effects_mixin\" is disabled."
            ),
            new PredicateEntry(
                    new String[]{"client.render.EquipmentLayerRendererMixin"},
                    !MixinOptions.INSTANCE.getInstance().redArmorTintMixin,
                    "\"red_armor_tint_mixin\" is disabled."
            ),
            new PredicateEntry(
                    new String[]{"client.util.ClientClockManagerMixin"},
                    !MixinOptions.INSTANCE.getInstance().clockManagerMixin,
                    "\"clock_manager_mixin\" is disabled."
            ),
            new PredicateEntry(
                    new String[]{"main.ItemArgumentMixin"},
                    !MixinOptions.INSTANCE.getInstance().itemArgumentMixin,
                    "\"item_argument_mixin\" is disabled."
            )
    );

    /**
     * @return {@code false} if mixin should not apply.
     */
    public static boolean shouldNotApply(String targetClassName, String mixinClassName) {
        for (PredicateEntry entry : ENTRIES) {
            if (entry.condition()) {
                for (String s : entry.mixins()) {
                    String name = "net.dillon.qualityofqueso.mixin." + s;
                    if (name.equals(mixinClassName)) {
                        LOGGER.warn("Skipping mixin {} for class {}: {}",
                                mixinClassName,
                                targetClassName,
                                entry.reason()
                        );
                        return true;
                    }
                }
            }
        }

        return false;
    }
}