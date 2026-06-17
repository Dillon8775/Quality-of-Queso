package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.universalOptionsInstance;

/**
 * The fog options category for the {@link ConfigurationScreen}.
 */
public class FogCategory {

    protected static ConfigCategory create() {
        Option<Integer> overworldFogIntensity = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.overworld_fog_intensity"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.overworld_fog_intensity.description")))
                .binding(100, () -> clientOptionsInstance().getFogOptions().overworldFogIntensity, v -> clientOptionsInstance().getFogOptions().overworldFogIntensity = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(25, 150)
                        .step(1)
                        .formatValue(v -> Component.literal(v + "%"))
                )
                .available(universalOptionsInstance().getMixins().fogMixins)
                .build();

        Option<Integer> netherFogIntensity = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.nether_fog_intensity"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.nether_fog_intensity.description")))
                .binding(100, () -> clientOptionsInstance().getFogOptions().netherFogIntensity, v -> clientOptionsInstance().getFogOptions().netherFogIntensity = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(5, 100)
                        .step(1)
                        .formatValue(v -> Component.literal(v + "%"))
                )
                .available(universalOptionsInstance().getMixins().fogMixins)
                .build();

        Option<Boolean> overworldFog = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.overworld_fog"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.overworld_fog.description")))
                .binding(true, () -> clientOptionsInstance().getFogOptions().overworldFog, v -> clientOptionsInstance().getFogOptions().overworldFog = v)
                .controller(TickBoxControllerBuilder::create)
                .available(universalOptionsInstance().getMixins().fogMixins)
                .build();

        Option<Boolean> netherFog = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.nether_fog"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.nether_fog.description")))
                .binding(true, () -> clientOptionsInstance().getFogOptions().netherFog, v -> clientOptionsInstance().getFogOptions().netherFog = v)
                .controller(TickBoxControllerBuilder::create)
                .available(universalOptionsInstance().getMixins().fogMixins)
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.fog"))
                .tooltip(Component.translatable("qualityofqueso.options.fog.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.title.fog"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fog.tooltip")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.all_fog"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.all_fog.description")))
                                                .binding(true, () -> clientOptionsInstance().getFogOptions().allFog, v -> clientOptionsInstance().getFogOptions().allFog = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(universalOptionsInstance().getMixins().fogMixins)
                                                .build()
                                )
                                .option(
                                        overworldFog
                                )
                                .option(
                                        netherFog
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.fog.intensity"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fog.intensity.description")))
                                .option(
                                        overworldFogIntensity
                                )
                                .option(
                                        netherFogIntensity
                                )
                                .build()
                )
                .build();
    }
}