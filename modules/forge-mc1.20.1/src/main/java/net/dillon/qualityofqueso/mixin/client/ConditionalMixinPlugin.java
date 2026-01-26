package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * A mixin plugin that determines whether {@code certain mixins} should be applied.
 */
@OnlyIn(Dist.CLIENT)
public class ConditionalMixinPlugin implements IMixinConfigPlugin {

    /**
     * Determines whether the {@code BackgroundRendererMixin} should be applied.
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return FMLLoader.getLoadingModList().getModFileById("simplekeybinds") == null && UniversalOptions.UNIVERSAL.getInstance().applyFogFunction;
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