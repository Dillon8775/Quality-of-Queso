package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.neoforged.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * A mixin plugin that determines whether {@code certain mixins} should be applied.
 */
public class ConditionalMixinPlugin implements IMixinConfigPlugin {

    /**
     * Determines whether the {@code BackgroundRendererMixin} should be applied.
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (FMLLoader.getCurrent().getLoadingModList().getModFileById("simplekeybinds") != null || !UniversalOptions.UNIVERSAL.getInstance().applyFogFunction) {
            return !mixinClassName.equals("net.dillon.qualityofqueso.mixin.client.render.FogRendererMixin");
        }
        return true;
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