package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * A mixin plugin that determines whether {@code certain mixins} should be applied.
 */
@Environment(EnvType.CLIENT)
public class ConditionalMixinPlugin implements IMixinConfigPlugin {

    /**
     * Determines whether the {@code BackgroundRendererMixin} should be applied.
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (FabricLoader.getInstance().isModLoaded("simplekeybinds") || !UniversalOptions.UNIVERSAL.getInstance().applyFogFunction) {
            return !mixinClassName.equals("net.dillon.qualityofqueso.mixin.FogRendererMixin");
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
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}