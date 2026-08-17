package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.mixins;

/**
 * The fog options category for the {@link ConfigurationScreen}.
 */
public class FogCategory {

    protected static ConfigCategory create() {
        Option<Integer> overworldFogIntensity = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.overworld_fog_intensity"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.overworld_fog_intensity.description")))
                .binding(100, () -> client().fog().overworldFogIntensity, v -> client().fog().overworldFogIntensity = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(25, 150)
                        .step(1)
                        .formatValue(v -> Component.literal(v + "%"))
                )
                .available(mixins().fogMixins)
                .build();

        Option<Integer> netherFogIntensity = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.nether_fog_intensity"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.nether_fog_intensity.description")))
                .binding(100, () -> client().fog().netherFogIntensity, v -> client().fog().netherFogIntensity = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(5, 100)
                        .step(1)
                        .formatValue(v -> Component.literal(v + "%"))
                )
                .available(mixins().fogMixins)
                .build();

        Option<Boolean> overworldFog = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.overworld_fog"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.overworld_fog.description")))
                .binding(true, () -> client().fog().overworldFog, v -> client().fog().overworldFog = v)
                .controller(TickBoxControllerBuilder::create)
                .addListener((opt, event) -> {
                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                        overworldFogIntensity.setAvailable(opt.pendingValue());
                    }
                })
                .available(mixins().fogMixins)
                .build();

        Option<Boolean> netherFog = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.nether_fog"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.nether_fog.description")))
                .binding(true, () -> client().fog().netherFog, v -> client().fog().netherFog = v)
                .controller(TickBoxControllerBuilder::create)
                .addListener((opt, event) -> {
                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                        netherFogIntensity.setAvailable(opt.pendingValue());
                    }
                })
                .available(mixins().fogMixins)
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
                                                .binding(true, () -> client().fog().allFog, v -> client().fog().allFog = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(mixins().fogMixins)
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        boolean bl = opt.pendingValue();
                                                        overworldFog.setAvailable(bl);
                                                        netherFog.setAvailable(bl);

                                                        overworldFogIntensity.setAvailable(bl);
                                                        netherFogIntensity.setAvailable(bl);
                                                    }
                                                })
                                                .available(mixins().fogMixins)
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