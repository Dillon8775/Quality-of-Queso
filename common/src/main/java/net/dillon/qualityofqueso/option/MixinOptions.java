package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.util.ModConstants;

/**
 * An option instance that stores mixin configurations.
 */
public class MixinOptions {
    public static final MixinHandler INSTANCE = new MixinHandler();
    public boolean titleScreenMixin = true;
    public boolean pauseScreenMixin = true;
    public boolean fogMixins = true;
    public boolean fovEffectsMixin = true;
    public boolean redArmorTintMixin = true;
    public boolean clockManagerMixin = true;
    public boolean itemArgumentMixin = true;

    public static class MixinHandler extends BaseOptions<MixinOptions> {

        protected MixinHandler() {
            super(ModConstants.DEFAULT_MIXIN_CONFIG_FILE_NAME);
            this.load();
        }

        @Override
        protected MixinOptions createDefault() {
            return new MixinOptions();
        }

        @Override
        protected Class<MixinOptions> getConfigClass() {
            return MixinOptions.class;
        }
    }
}