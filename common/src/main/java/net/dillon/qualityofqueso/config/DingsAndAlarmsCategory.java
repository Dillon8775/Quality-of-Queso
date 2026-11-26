package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;

/**
 * The dings and alarms options category for the {@link ConfigurationScreen}.
 */
public class DingsAndAlarmsCategory {

    protected static ConfigCategory create() {
        Option<Integer> elytraAlarmMinFallDistance = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.min_elytra_fall_distance"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.min_elytra_fall_distance.description")))
                .binding(20, () -> client().elytraAlarm().minElytraAlarmFallDistance, v -> client().elytraAlarm().minElytraAlarmFallDistance = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(10, 100)
                        .step(1)
                        .formatValue(v -> Component.literal(v + " blocks"))
                )
                .build();

        Option<Integer> elytraAlarmSoundDelayTicks = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.elytra_alarm_sound_delay"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.elytra_alarm_sound_delay.tooltip")))
                .binding(1, () -> client().elytraAlarm().elytraAlarmSoundDelayTicks, v -> client().elytraAlarm().elytraAlarmSoundDelayTicks = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(1, 120)
                        .step(1)
                        .formatValue(v -> Component.literal(v + " tick(s)"))
                )
                .build();

        Option<Integer> mobHitDingMinDistance = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.min_mob_hit_ding_distance"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.min_mob_hit_ding_distance.description")))
                .binding(15, () -> client().dingsAndAlarms().minMobHitDingDistance, v -> client().dingsAndAlarms().minMobHitDingDistance = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(10, 50)
                        .step(1)
                        .formatValue(v -> Component.literal(v + " blocks"))
                )
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.dings_and_alarms"))
                .tooltip(Component.translatable("qualityofqueso.options.dings_and_alarms.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.elytra_alarm"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.elytra_alarm.description")))
                                .option(
                                        Option.<ElytraAlarm>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.elytra_alarm"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.elytra_alarm.description")))
                                                .binding(ElytraAlarm.ENABLED, () -> client().elytraAlarm().elytraAlarm, v -> client().elytraAlarm().elytraAlarm = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(ElytraAlarm.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        boolean bl = opt.pendingValue().enabled();
                                                        elytraAlarmMinFallDistance.setAvailable(bl);
                                                        elytraAlarmSoundDelayTicks.setAvailable(bl);
                                                    }
                                                })
                                                .build()
                                )
                                .option(
                                        elytraAlarmMinFallDistance
                                )
                                .option(
                                        elytraAlarmSoundDelayTicks
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.dings_and_alarms.dings"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.dings_and_alarms.dings.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.armor_ding"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.armor_ding.description")))
                                                .binding(true, () -> client().dingsAndAlarms().armorDing, v -> client().dingsAndAlarms().armorDing = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.mob_hit_ding"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.mob_hit_ding.description")))
                                                .binding(true, () -> client().dingsAndAlarms().mobHitDing, v -> client().dingsAndAlarms().mobHitDing = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        mobHitDingMinDistance.setAvailable(opt.pendingValue());
                                                    }
                                                })
                                                .build()
                                )
                                .option(
                                        mobHitDingMinDistance
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.alarms_and_timers"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.alarms_and_timers.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.alarm_sounds"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.alarm_sounds.description")))
                                                .binding(true, () -> client().dingsAndAlarms().alarmSounds, v -> client().dingsAndAlarms().alarmSounds = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.show_time_until_alarm_sounds"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.show_time_until_alarm_sounds.description")))
                                                .binding(false, () -> client().dingsAndAlarms().showTimeUntilAlarmSounds, v -> client().dingsAndAlarms().showTimeUntilAlarmSounds = v)
                                                .controller(TickBoxControllerBuilderImpl::new)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.ms_for_stopwatches"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.ms_for_stopwatches.description")))
                                                .binding(true, () -> client().dingsAndAlarms().msForStopwatches, v -> client().dingsAndAlarms().msForStopwatches = v)
                                                .controller(TickBoxControllerBuilderImpl::new)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}