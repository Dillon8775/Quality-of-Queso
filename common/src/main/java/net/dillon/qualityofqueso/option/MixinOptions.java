package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.helper.ModConstants;

import static net.dillon.dillonlib.util.BaseOptions.guardedInstance;

/**
 * An option instance that stores mixin configurations.
 */
public class MixinOptions {
    private static final MixinHandler INSTANCE = new MixinHandler();
    public boolean fogMixins = true;
    public boolean fovEffectsMixin = true;
    public boolean redArmorTintMixin = true;
    public boolean clockManagerMixin = true;
    public boolean itemArgumentMixin = true;
    public boolean itemStackMixin = true;

    public static MixinHandler getInstance() {
        return guardedInstance(INSTANCE);
    }

    public static class MixinHandler extends ModBaseOptionsHandler<MixinOptions> {

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