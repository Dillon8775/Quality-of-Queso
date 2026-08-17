package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.mixins;

/**
 * The miscellaneous options category for the {@link ConfigurationScreen}.
 */
public class MiscellaneousCategory {

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
                .binding(15, () -> client().misc().minMobHitDingDistance, v -> client().misc().minMobHitDingDistance = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(10, 50)
                        .step(1)
                        .formatValue(v -> Component.literal(v + " blocks"))
                )
                .build();

        Option<Boolean> forceAntiRageQuit = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.force_anti_rage_quit"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.force_anti_rage_quit.description")))
                .binding(false, () -> client().misc().forceAntiRageQuit, v -> client().misc().forceAntiRageQuit = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.miscellaneous"))
                .tooltip(Component.translatable("qualityofqueso.options.miscellaneous.tooltip"))
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
                                .name(Component.translatable("qualityofqueso.options.misc.dings"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.misc.dings.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.mob_hit_ding"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.mob_hit_ding.description")))
                                                .binding(true, () -> client().misc().mobHitDing, v -> client().misc().mobHitDing = v)
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
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.armor_ding"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.armor_ding.description")))
                                                .binding(true, () -> client().misc().armorDing, v -> client().misc().armorDing = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.misc.recipe_book"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.misc.recipe_book.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.no_recipe_book_shift"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.no_recipe_book_shift.description")))
                                                .binding(false, () -> client().misc().noRecipeBookShift, v -> client().misc().noRecipeBookShift = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.auto_close_recipe_book"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.auto_close_recipe_book.description")))
                                                .binding(true, () -> client().misc().autoCloseRecipeBook, v -> client().misc().autoCloseRecipeBook = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.misc.anti_rage"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.misc.anti_rage.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.anti_rage_quit"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.anti_rage_quit.description")))
                                                .binding(false, () -> client().misc().antiRageQuit, v -> client().misc().antiRageQuit = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        forceAntiRageQuit.setAvailable(opt.pendingValue());
                                                    }
                                                })
                                                .build()
                                )
                                .option(
                                        forceAntiRageQuit
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.misc.other"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.misc.other.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.red_armor_tint"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.red_armor_tint.description")))
                                                .binding(false, () -> client().misc().redArmorTint, v -> client().misc().redArmorTint = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(mixins().redArmorTintMixin)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.enchantment_helper"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.enchantment_helper.description")))
                                                .binding(true, () -> client().misc().enchantmentHelper, v -> client().misc().enchantmentHelper = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.enhanced_cursor"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.enhanced_cursor.description")))
                                                .binding(true, () -> client().misc().enhancedCursor, v -> client().misc().enhancedCursor = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.quick_gui_exit"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.quick_gui_exit.description")))
                                                .binding(true, () -> client().misc().quickGuiExit, v -> client().misc().quickGuiExit = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fortnite_battle_pass"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fortnite_battle_pass.description")))
                                                .binding(false, () -> client().misc().fortniteBattlePass, v -> client().misc().fortniteBattlePass = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}