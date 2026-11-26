package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.util.QualityOfQuesoMixinPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ConditionalMixinPlugin implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        QualityOfQuesoMixinPlugin qoqMixinPlugin = new QualityOfQuesoMixinPlugin();
        return !qoqMixinPlugin.shouldNotApply(targetClassName, mixinClassName);
    }
}