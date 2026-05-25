package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.dillon.qualityofqueso.option.eum.hud.ItemCounter;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.config.ConfigurationScreen.fixedSizeImage;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

/**
 * The item counter optons category for the {@link ConfigurationScreen}.
 */
public class ItemCounterCategory {

    protected static ConfigCategory create() {
        Option<Boolean> countContainers = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.count_containers"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.count_containers.description")))
                .binding(true, () -> clientOptionsInstance().getItemCounterOptions().countContainers, v -> clientOptionsInstance().getItemCounterOptions().countContainers = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> countEnderChest = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.count_ender_chest"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.count_ender_chest.description")))
                .binding(false, () -> clientOptionsInstance().getItemCounterOptions().countEnderChest, v -> clientOptionsInstance().getItemCounterOptions().countEnderChest = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> onlyCountMatchingItems = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.only_count_matching_items"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.only_count_matching_items.description")))
                .binding(false, () -> clientOptionsInstance().getItemCounterOptions().onlyCountMatchingItems, v -> clientOptionsInstance().getItemCounterOptions().onlyCountMatchingItems = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> displayOnPickup = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.display_on_pickup"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.display_on_pickup.description")))
                .binding(true, () -> clientOptionsInstance().getItemCounterOptions().displayOnPickup, v -> clientOptionsInstance().getItemCounterOptions().displayOnPickup = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> displayOnThrow = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.display_on_throw"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.display_on_throw.description")))
                .binding(true, () -> clientOptionsInstance().getItemCounterOptions().displayOnThrow, v -> clientOptionsInstance().getItemCounterOptions().displayOnThrow = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> displayTotalWithStacks = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.display_total_with_stacks"))
                .description(
                        OptionDescription.createBuilder()
                                .text(Component.translatable("qualityofqueso.options.display_total_with_stacks.description"))
                                .customImage(fixedSizeImage(ofQoQ("options/item_counter/total_with_stacks"), 75, 32))
                                .build()
                )
                .binding(false, () -> clientOptionsInstance().getItemCounterOptions().displayTotalWithStacks, v -> clientOptionsInstance().getItemCounterOptions().displayTotalWithStacks = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> countAllArrows = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.count_all_arrows"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.count_all_arrows.description")))
                .binding(true, () -> clientOptionsInstance().getItemCounterOptions().countAllArrows, v -> clientOptionsInstance().getItemCounterOptions().countAllArrows = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> onlyShowArrowCounter = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.only_show_arrow_counter"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.only_show_arrow_counter.description")))
                .binding(false, () -> clientOptionsInstance().getItemCounterOptions().onlyShowArrowCounter, v -> clientOptionsInstance().getItemCounterOptions().onlyShowArrowCounter = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> alwaysShowArrowCounter = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.always_show_arrow_counter"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.always_show_arrow_counter.description")))
                .binding(false, () -> clientOptionsInstance().getItemCounterOptions().alwaysShowArrowCounter, v -> clientOptionsInstance().getItemCounterOptions().alwaysShowArrowCounter = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> arrowCounter = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.arrow_counter"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.arrow_counter.description")))
                .binding(true, () -> clientOptionsInstance().getItemCounterOptions().arrowCounter, v -> clientOptionsInstance().getItemCounterOptions().arrowCounter = v)
                .controller(BooleanControllerBuilder::create)
                .addListener((opt, event) -> {
                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                        boolean bl = opt.pendingValue();
                        countAllArrows.setAvailable(bl);
                        onlyShowArrowCounter.setAvailable(bl);
                        alwaysShowArrowCounter.setAvailable(bl);
                    }
                })
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.item_counter"))
                .tooltip(Component.translatable("qualityofqueso.options.item_counter.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.item_counter"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.item_counter.description")))
                                .option(
                                        Option.<ItemCounter>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.item_counter"))
                                                .description(value -> {
                                                    var builder = OptionDescription.createBuilder()
                                                            .text(Component.translatable("qualityofqueso.options.item_counter.description"));

                                                    return switch (value) {
                                                        case TOTAL -> builder.customImage(fixedSizeImage(ofQoQ("options/item_counter/total"), 75, 27)).build();
                                                        case STACKS -> builder.customImage(fixedSizeImage(ofQoQ("options/item_counter/stacks"), 75, 27)).build();
                                                        default -> builder.build();
                                                    };
                                                })
                                                .binding(ItemCounter.STACKS, () -> clientOptionsInstance().getItemCounterOptions().itemCounter, v -> clientOptionsInstance().getItemCounterOptions().itemCounter = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(ItemCounter.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        boolean bl = opt.pendingValue().enabled();
                                                        countContainers.setAvailable(bl);
                                                        countEnderChest.setAvailable(bl);
                                                        onlyCountMatchingItems.setAvailable(bl);

                                                        displayOnPickup.setAvailable(bl);
                                                        displayOnThrow.setAvailable(bl);
                                                        displayTotalWithStacks.setAvailable(bl);

                                                        arrowCounter.setAvailable(bl);
                                                        countAllArrows.setAvailable(bl);
                                                        onlyShowArrowCounter.setAvailable(bl);
                                                        alwaysShowArrowCounter.setAvailable(bl);
                                                    }
                                                })
                                                .build()
                                )
                                .option(
                                        countContainers
                                )
                                .option(
                                        countEnderChest
                                )
                                .option(
                                        onlyCountMatchingItems
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.arrow_counter"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.arrow_counter.description")))
                                .option(
                                        arrowCounter
                                )
                                .option(
                                        countAllArrows
                                )
                                .option(
                                        onlyShowArrowCounter
                                )
                                .option(
                                        alwaysShowArrowCounter
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.item_counter.display"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.item_counter.display.description")))
                                .option(
                                        displayOnPickup
                                )
                                .option(
                                        displayOnThrow
                                )
                                .option(
                                        displayTotalWithStacks
                                )
                                .build()
                )
                .build();
    }
}