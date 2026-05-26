package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.option.eum.misc.ElytraAlarm;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.universalOptionsInstance;
import static net.dillon.qualityofqueso.util.ModOptionUtil.fullKumaKeyMappingAsString;

/**
 * The miscellaneous options category for the {@link ConfigurationScreen}.
 */
public class MiscellaneousCategory {

    protected static ConfigCategory create() {
        Option<Integer> elytraAlarmMinFallDistance = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.min_elytra_fall_distance"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.min_elytra_fall_distance.description")))
                .binding(20, () -> clientOptionsInstance().getElytraAlarmOptions().minElytraAlarmFallDistance, v -> clientOptionsInstance().getElytraAlarmOptions().minElytraAlarmFallDistance = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(10, 100)
                        .step(1)
                        .formatValue(v -> Component.literal(v + " blocks"))
                )
                .build();

        Option<Integer> elytraAlarmSoundDelayTicks = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.elytra_alarm_sound_delay"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.elytra_alarm_sound_delay.tooltip")))
                .binding(1, () -> clientOptionsInstance().getElytraAlarmOptions().elytraAlarmSoundDelayTicks, v -> clientOptionsInstance().getElytraAlarmOptions().elytraAlarmSoundDelayTicks = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(1, 120)
                        .step(1)
                        .formatValue(v -> Component.literal(v + " tick(s)"))
                )
                .build();

        Option<Integer> mobHitDingMinDistance = Option.<Integer>createBuilder()
                .name(Component.translatable("qualityofqueso.options.min_mob_hit_ding_distance"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.min_mob_hit_ding_distance.description")))
                .binding(15, () -> clientOptionsInstance().getMiscOptions().minMobHitDingDistance, v -> clientOptionsInstance().getMiscOptions().minMobHitDingDistance = v)
                .controller(o -> IntegerSliderControllerBuilder.create(o)
                        .range(10, 50)
                        .step(1)
                        .formatValue(v -> Component.literal(v + " blocks"))
                )
                .build();

        Option<Boolean> forceAntiRageQuit = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.force_anti_rage_quit"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.force_anti_rage_quit.description")))
                .binding(false, () -> clientOptionsInstance().getMiscOptions().forceAntiRageQuit, v -> clientOptionsInstance().getMiscOptions().forceAntiRageQuit = v)
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
                                                .binding(ElytraAlarm.ENABLED, () -> clientOptionsInstance().getElytraAlarmOptions().elytraAlarm, v -> clientOptionsInstance().getElytraAlarmOptions().elytraAlarm = v)
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
                                                .binding(true, () -> clientOptionsInstance().getMiscOptions().mobHitDing, v -> clientOptionsInstance().getMiscOptions().mobHitDing = v)
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
                                                .binding(true, () -> clientOptionsInstance().getMiscOptions().armorDing, v -> clientOptionsInstance().getMiscOptions().armorDing = v)
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
                                                .binding(false, () -> clientOptionsInstance().getMiscOptions().noRecipeBookShift, v -> clientOptionsInstance().getMiscOptions().noRecipeBookShift = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.auto_close_recipe_book"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.auto_close_recipe_book.description")))
                                                .binding(true, () -> clientOptionsInstance().getMiscOptions().autoCloseRecipeBook, v -> clientOptionsInstance().getMiscOptions().autoCloseRecipeBook = v)
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
                                                .binding(false, () -> clientOptionsInstance().getMiscOptions().antiRageQuit, v -> clientOptionsInstance().getMiscOptions().antiRageQuit = v)
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
                                                .binding(false, () -> clientOptionsInstance().getMiscOptions().redArmorTint, v -> clientOptionsInstance().getMiscOptions().redArmorTint = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .available(universalOptionsInstance().getFunctions().applyRedArmorTint)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.enchantment_helper"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.enchantment_helper.description")))
                                                .binding(true, () -> clientOptionsInstance().getMiscOptions().enchantmentHelper, v -> clientOptionsInstance().getMiscOptions().enchantmentHelper = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.quick_equip"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.quick_equip.description",
                                                        fullKumaKeyMappingAsString(ModKeyMappings.QUICK_EQUIP))))
                                                .binding(true, () -> clientOptionsInstance().getMiscOptions().quickEquip, v -> clientOptionsInstance().getMiscOptions().quickEquip = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.quick_gui_exit"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.quick_gui_exit.description")))
                                                .binding(true, () -> clientOptionsInstance().getMiscOptions().quickGuiExit, v -> clientOptionsInstance().getMiscOptions().quickGuiExit = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.fortnite_battle_pass"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.fortnite_battle_pass.description")))
                                                .binding(false, () -> clientOptionsInstance().getMiscOptions().fortniteBattlePass, v -> clientOptionsInstance().getMiscOptions().fortniteBattlePass = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}