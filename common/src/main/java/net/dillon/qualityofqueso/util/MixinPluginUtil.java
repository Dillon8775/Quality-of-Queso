package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.UniversalOptions;
import net.dillon.qualityofqueso.platform.MultiLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for Conditional mixin plugin.
 */
@SuppressWarnings("unchecked")
public class MixinPluginUtil {
    public static final String ABSTRACT_CLIENT_PLAYER_MIXIN = "net.dillon.qualityofqueso.mixin.client.util.AbstractClientPlayerMixin";
    public static final List<String> ACTIVE_FOV_EFFECT_BLACKLISTED_MODS = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger("Quality of Queso/Mixin");
    private static String REASON = "";
    private static final String[] FOV_EFFECT_BLACKLISTED_MODS = new String[]{
            "tweakeroo",
            "ok_zoomer",
            "zoomify"
    };

    /**
     * @return {@code false} if mixin should not apply.
     */
    private static boolean shouldApply(String mixinClassName) {
        if (mixinClassName.equals("net.dillon.qualityofqueso.mixin.render.FogRendererMixin")) {
            if (!UniversalOptions.INSTANCE.getInstance().functions.applyFog) {
                REASON = "\"apply_fog\" function is disabled.";
                return false;
            }
        }

        boolean abstractClientPlayerMixin = mixinClassName.equals(ABSTRACT_CLIENT_PLAYER_MIXIN);
        if (abstractClientPlayerMixin || mixinClassName.equals("net.dillon.qualityofqueso.mixin.render.CameraMixin")) {
            if (abstractClientPlayerMixin) {
                for (String mod : FOV_EFFECT_BLACKLISTED_MODS) {
                    if (MultiLoader.getPlatform().isModLoaded(mod)) {
                        LOGGER.error("Mod \"{}\" is loaded, and due to incompatibility, not applying {}.", mod, mixinClassName);
                        ACTIVE_FOV_EFFECT_BLACKLISTED_MODS.add(mod);
                        return false;
                    }
                }
            } else if (!UniversalOptions.INSTANCE.getInstance().functions.applyFovEffects) {
                REASON = "\"apply_fov_effects\" function is disabled.";
                return false;
            }
        }

        if (mixinClassName.equals("net.dillon.qualityofqueso.mixin.client.render.EquipmentLayerRendererMixin")) {
            if (!UniversalOptions.INSTANCE.getInstance().functions.applyRedArmorTint) {
                REASON = "\"apply_red_armor_tint\" function is disabled.";
                return false;
            }
        }

        // Always apply other mixins
        return true;
    }

    /**
     * @return {@code true} if a mixin should be applied to the game.
     */
    public static boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean bl = shouldApply(mixinClassName);
        if (!bl) {
            LOGGER.warn("Skipping mixin {} for target {} because it should not be applied. Reason: {}",
                    mixinClassName,
                    targetClassName,
                    REASON.isEmpty() ? "null" : REASON);
        }
        return bl;
    }
}