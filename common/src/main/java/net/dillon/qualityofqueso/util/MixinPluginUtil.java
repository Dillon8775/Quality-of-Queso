package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.MixinOptions;
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
            if (!MixinOptions.INSTANCE.getInstance().titleScreenMixin) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("client.screen.PauseScreenMixin"))) {
            if (!MixinOptions.INSTANCE.getInstance().pauseScreenMixin) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("render.FogRendererMixin")) || mixinClassName.equals(ofQoQMixin("render.CameraMixin"))) {
            if (!MixinOptions.INSTANCE.getInstance().fogMixins) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("client.util.AbstractClientPlayerMixin"))) {
            if (!MixinOptions.INSTANCE.getInstance().fovEffectsMixin) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("client.render.EquipmentLayerRendererMixin"))) {
            if (!MixinOptions.INSTANCE.getInstance().redArmorTintMixin) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("client.util.ClientClockManagerMixin"))) {
            if (!MixinOptions.INSTANCE.getInstance().clockManagerMixin) {
                return false;
            }
        }

        if (mixinClassName.equals(ofQoQMixin("main.ItemArgumentMixin"))) {
            if (!MixinOptions.INSTANCE.getInstance().itemArgumentMixin) {
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