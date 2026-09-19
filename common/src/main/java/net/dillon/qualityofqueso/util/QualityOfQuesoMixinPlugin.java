package net.dillon.qualityofqueso.util;

import net.dillon.dillonlib.core.DillonLibModReferences;
import net.dillon.dillonlib.mixinplugin.MixinPluginUtil;
import net.dillon.dillonlib.mixinplugin.PredicateEntry;
import net.dillon.qualityofqueso.option.MixinOptions;
import net.dillon.qualityofqueso.platform.ModReferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class QualityOfQuesoMixinPlugin extends MixinPluginUtil {

    @Override
    public Logger logger() {
        return LoggerFactory.getLogger("Quality of Queso/Mixin");
    }

    @Override
    public String mixinDirectory() {
        return "net.dillon.qualityofqueso.mixin.";
    }

    @Override
    public List<PredicateEntry> entries() {
        return List.of(
                PredicateEntry.ofWarn(
                        PredicateEntry.single("fix.AbstractContainerScreenFix"),
                        DillonLibModReferences.isModLoaded(ModReferences.VIAFABRICPLUS),
                        "ViaFabricPlus mod is loaded for fabric, disabling this mixin prevents a game crash."
                ),
                PredicateEntry.ofDebug(
                        PredicateEntry.single("client.screen.TitleScreenMixin"),
                        !MixinOptions.INSTANCE.getInstance().titleScreenMixin,
                        "\"title_screen_mixin\" is disabled."
                ),
                PredicateEntry.ofDebug(
                        PredicateEntry.single("client.screen.PauseScreenMixin"),
                        !MixinOptions.INSTANCE.getInstance().pauseScreenMixin,
                        "\"pause_screen_mixin\" is disabled."
                ),
                PredicateEntry.ofDebug(
                        PredicateEntry.ddouble(
                                "render.FogRendererMixin",
                                "render.CameraMixin"
                        ),
                        !MixinOptions.INSTANCE.getInstance().fogMixins,
                        "\"fog_mixins\" are disabled."
                ),
                PredicateEntry.ofDebug(
                        PredicateEntry.single("client.util.AbstractClientPlayerMixin"),
                        !MixinOptions.INSTANCE.getInstance().fovEffectsMixin,
                        "\"fov_effects_mixin\" is disabled."
                ),
                PredicateEntry.ofDebug(
                        PredicateEntry.single("client.render.EquipmentLayerRendererMixin"),
                        !MixinOptions.INSTANCE.getInstance().redArmorTintMixin,
                        "\"red_armor_tint_mixin\" is disabled."
                ),
                PredicateEntry.ofDebug(
                        PredicateEntry.single("client.util.ClientClockManagerMixin"),
                        !MixinOptions.INSTANCE.getInstance().clockManagerMixin,
                        "\"clock_manager_mixin\" is disabled."
                ),
                PredicateEntry.ofDebug(
                        PredicateEntry.single("main.ItemArgumentMixin"),
                        !MixinOptions.INSTANCE.getInstance().itemArgumentMixin,
                        "\"item_argument_mixin\" is disabled."
                )
        );
    }
}