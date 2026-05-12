package net.dillon.qualityofqueso.plugin;

import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.dillon.qualityofqueso.util.ModUtil;

/**
 * Utility class for Conditional mixin plugin.
 */
public class MixinPluginUtil {

    /**
     * @return {@code true} if a mixin should be applied to the game.
     */
    public static boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean bl = shouldNotApply(mixinClassName);
        if (bl) {
            ModUtil.warn("Skipping mixin " + mixinClassName + " for target " + targetClassName + " because it should not be applied.");
        }
        return !bl;
    }

    /**
     * Return {@code true} if mixin should not apply.
     */
    private static boolean shouldNotApply(String mixinClassName) {
        if (!UniversalOptions.UNIVERSAL.getInstance().functions.applyFog && mixinClassName.equals("net.dillon.qualityofqueso.mixin.render.FogRendererMixin")) {
            return true;
        }
        if (!UniversalOptions.UNIVERSAL.getInstance().functions.applyFovEffects &&
                (mixinClassName.equals("net.dillon.qualityofqueso.mixin.client.util.AbstractClientPlayerMixin") || mixinClassName.equals("net.dillon.qualityofqueso.mixin.render.CameraMixin"))) {
            return true;
        }
        return false;
    }
}