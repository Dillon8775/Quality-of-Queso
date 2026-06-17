package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.UniversalOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for Conditional mixin plugin.
 */
@SuppressWarnings("unchecked")
public class MixinPluginUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger("Quality of Queso/Mixin");

    /**
     * @return {@code false} if mixin should not apply.
     */
    private static boolean shouldApply(String mixinClassName) {
        if (mixinClassName.equals(ofQoQMixin("client.screen.TitleScreenMixin"))) {
            if (!UniversalOptions.INSTANCE.getInstance().getMixins().titleScreenMixin) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("client.screen.PauseScreenMixin"))) {
            if (!UniversalOptions.INSTANCE.getInstance().getMixins().pauseScreenMixin) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("render.FogRendererMixin")) || mixinClassName.equals(ofQoQMixin("render.CameraMixin"))) {
            if (!UniversalOptions.INSTANCE.getInstance().getMixins().fogMixins) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("client.util.AbstractClientPlayerMixin"))) {
            if (!UniversalOptions.INSTANCE.getInstance().getMixins().fovEffectsMixin) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("client.render.EquipmentLayerRendererMixin"))) {
            if (!UniversalOptions.INSTANCE.getInstance().getMixins().redArmorTintMixin) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("client.util.ClientClockManagerMixin"))) {
            if (!UniversalOptions.INSTANCE.getInstance().getMixins().clockManagerMixin) {
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
            LOGGER.warn("Skipping mixin {} for target {} because it should not be applied.",
                    mixinClassName,
                    targetClassName);
        }
        return bl;
    }

    /**
     * @return a quality of queso mixin.
     */
    private static String ofQoQMixin(String mixinClassName) {
        return "net.dillon.qualityofqueso.mixin." + mixinClassName;
    }
}