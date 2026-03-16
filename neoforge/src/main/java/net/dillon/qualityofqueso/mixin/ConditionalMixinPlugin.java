package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.dillon.qualityofqueso.util.ModUtil;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * A mixin plugin that determines whether {@code certain mixins} should be applied.
 */
public class ConditionalMixinPlugin implements IMixinConfigPlugin {

    /**
     * Determines whether certain mixins should be applied.
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean bl = this.shouldNotApply(mixinClassName);
        if (bl) {
            ModUtil.warn("Skipping mixin " + mixinClassName + " for target " + targetClassName + " because it should not be applied.");
        }
        return !bl;
    }

    /**
     * Returns client-side mixins that should not apply based on certain conditions.
     */
    private boolean shouldNotApply(String mixinClassName) {
        if (!UniversalOptions.UNIVERSAL.getInstance().functions.applyFog && mixinClassName.equals("net.dillon.qualityofqueso.mixin.render.FogRendererMixin")) {
            return true;
        }
        if (!UniversalOptions.UNIVERSAL.getInstance().functions.applyFovEffects &&
                (mixinClassName.equals("net.dillon.qualityofqueso.mixin.client.util.AbstractClientPlayerMixin") || mixinClassName.equals("net.dillon.qualityofqueso.mixin.render.CameraMixin"))) {
            return true;
        }
        return false;
    }

    // Other methods...
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, org.objectweb.asm.tree.ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, org.objectweb.asm.tree.ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}