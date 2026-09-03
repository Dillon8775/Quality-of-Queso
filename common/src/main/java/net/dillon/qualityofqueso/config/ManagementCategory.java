package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.management.*;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.DefaultSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.GlobalSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.Sorting;
import net.dillon.qualityofqueso.util.ModOptionUtil;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;

import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.common;
import static net.dillon.qualityofqueso.util.ModOptionUtil.fullKumaKeyMappingAsString;

/**
 * The management options category for the {@link ConfigurationScreen}.
 */
public class ManagementCategory {

    protected static ConfigCategory create() {
        Option<DefaultSortingMode> defaultSortingModeOption = Option.<DefaultSortingMode>createBuilder()
                .name(Component.translatable("qualityofqueso.options.default_sorting_mode"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.default_sorting_mode.description")))
                .binding(DefaultSortingMode.ALPHABETICALLY, () -> client().sorting().defaultSortingMode, v -> client().sorting().defaultSortingMode = v)
                .controller(o -> EnumControllerBuilder.create(o)
                        .enumClass(DefaultSortingMode.class)
                        .formatValue(v -> Component.literal(v.getSerializedName())))
                .available(!client().sorting().useGlobalSortingMode)
                .build();

        Option<GlobalSortingMode> globalSortingModeOption = Option.<GlobalSortingMode>createBuilder()
                .name(Component.translatable("qualityofqueso.options.global_sorting_mode"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.global_sorting_mode.description")))
                .binding(GlobalSortingMode.ALPHABETICALLY, () -> client().sorting().globalSortingMode, v -> {
                    if (v.equals(GlobalSortingMode.ALPHABETICALLY)) {
                        client().sorting().currentSortingMode = CurrentSortingMode.ALPHABETICAL;
                    } else if (v.equals(GlobalSortingMode.BY_TAG)) {
                        client().sorting().currentSortingMode = CurrentSortingMode.TAG;
                    } else if (v.equals(GlobalSortingMode.DESCENDING)) {
                        client().sorting().currentSortingMode = CurrentSortingMode.COUNT_DESCENDING;
                    } else if (v.equals(GlobalSortingMode.ASCENDING)) {
                        client().sorting().currentSortingMode = CurrentSortingMode.COUNT_ASCENDING;
                    } else if (v.equals(GlobalSortingMode.CREATIVE_MENU)) {
                        client().sorting().currentSortingMode = CurrentSortingMode.CREATIVE_MENU;
                    }
                    client().sorting().globalSortingMode = v;
                })
                .controller(o -> EnumControllerBuilder.create(o)
                        .enumClass(GlobalSortingMode.class)
                        .formatValue(v -> Component.literal(v.getSerializedName())))
                .build();

        Option<Boolean> useGlobalSortingModeOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.use_global_sorting_mode"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.use_global_sorting_mode.description")))
                .binding(false, () -> client().sorting().useGlobalSortingMode, v -> client().sorting().useGlobalSortingMode = v)
                .controller(TickBoxControllerBuilder::create)
                .addListener((opt, event) -> {
                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                        boolean bl = opt.pendingValue();
                        globalSortingModeOption.setAvailable(bl);
                        defaultSortingModeOption.setAvailable(!bl);
                    }
                })
                .build();

        Option<ShowLock> showLockOption = Option.<ShowLock>createBuilder()
                .name(Component.translatable("qualityofqueso.options.show_lock"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.show_lock.description")))
                .binding(ShowLock.SCREEN_ONLY, () -> client().lockedSlots().showLock, v -> client().lockedSlots().showLock = v)
                .controller(o -> EnumControllerBuilder.create(o)
                        .enumClass(ShowLock.class)
                        .formatValue(v -> Component.literal(v.getSerializedName())))
                .build();

        Option<Boolean> lockSoundOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.lock_sound"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.lock_sound.description")))
                .binding(true, () -> client().lockedSlots().lockSound, v -> client().lockedSlots().lockSound = v)
                .controller(BooleanControllerBuilder::create)
                .build();

        Option<Color> lockedSlotColorOption = Option.<Color>createBuilder()
                .name(Component.translatable("qualityofqueso.options.locked_slots_color"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.locked_slots_color.description")))
                .binding(
                        new Color(ModConstants.DEFAULT_LOCKED_SLOT_COLOR, true),
                        () -> new Color(client().lockedSlots().lockedSlotColor, true),
                        v -> client().lockedSlots().lockedSlotColor = v.getRGB()
                )
                .controller(o -> ColorControllerBuilder.create(o)
                        .allowAlpha(true))
                .build();

        Option<Color> matchingItemsColorOption = Option.<Color>createBuilder()
                .name(Component.translatable("qualityofqueso.options.matching_items_color"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.matching_items_color.description")))
                .binding(
                        new Color(ModConstants.DEFAULT_MATCHING_ITEMS_COLOR, true),
                        () -> new Color(client().management().matchingItemsColor, true),
                        v -> client().management().matchingItemsColor = v.getRGB()
                )
                .controller(o -> ColorControllerBuilder.create(o)
                        .allowAlpha(true))
                .build();

        Option<Boolean> preventDroppingOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.prevent_dropping"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.prevent_dropping.description")))
                .binding(true, () -> client().lockedSlots().preventDropping, v -> client().lockedSlots().preventDropping = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> hardLockSlotsOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.hard_lock_slots"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.hard_lock_slots.description")))
                .binding(false, () -> client().lockedSlots().hardLockSlots, v -> client().lockedSlots().hardLockSlots = v)
                .controller(BooleanControllerBuilder::create)
                .build();

        ListOption<String> horizontalButtonLayout = ListOption.<String>createBuilder()
                .name(Component.translatable("qualityofqueso.options.management.button_order.horizontal"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.button_order.horiztonal.description")))
                .initial("")
                .controller(StringControllerBuilder::create)
                .binding(
                        ModClientOptions.DEFAULT_HORIZONTAL_BUTTON_LAYOUT,
                        () -> new ArrayList<>(client().management().horizontalButtonLayout),
                        v -> {
                            client().management().horizontalButtonLayout.clear();
                            client().management().horizontalButtonLayout.addAll(v);
                        }
                )
                .minimumNumberOfEntries(client().management().horizontalButtonLayout.size())
                .maximumNumberOfEntries(client().management().horizontalButtonLayout.size())
                .available(client().management().layout.horizontal())
                .collapsed(true)
                .build();

        ListOption<String> verticalButtonLayout = ListOption.<String>createBuilder()
                .name(Component.translatable("qualityofqueso.options.management.button_order.vertical"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.button_order.vertical.description")))
                .initial("")
                .controller(StringControllerBuilder::create)
                .binding(
                        ModClientOptions.DEFAULT_VERTICAL_BUTTON_LAYOUT,
                        () -> new ArrayList<>(client().management().verticalButtonLayout),
                        v -> {
                            client().management().verticalButtonLayout.clear();
                            client().management().verticalButtonLayout.addAll(v);
                        }
                )
                .minimumNumberOfEntries(client().management().verticalButtonLayout.size())
                .maximumNumberOfEntries(client().management().verticalButtonLayout.size())
                .available(!client().management().layout.horizontal())
                .collapsed(true)
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable("qualityofqueso.options.title.management"))
                .tooltip(Component.translatable("qualityofqueso.options.management.tooltip"))
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.management.appearance"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.appearance.description")))
                                .option(
                                        Option.<Layout>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.layout"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.layout.description")))
                                                .binding(Layout.HORIZONTAL, () -> client().management().layout, v -> client().management().layout = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Layout.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        boolean bl = opt.pendingValue().horizontal();
                                                        horizontalButtonLayout.setAvailable(bl);
                                                        verticalButtonLayout.setAvailable(!bl);
                                                    }
                                                })
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.play_sounds"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.play_sounds.description")))
                                                .binding(true, () -> client().management().playSounds, v -> client().management().playSounds = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.management.basic"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.basic.description")))
                                .option(
                                        Option.<Transferring>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.transferring"))
                                                .description(OptionDescription.of(
                                                        Component.translatable("qualityofqueso.options.transferring.description",
                                                                fullKumaKeyMappingAsString(ModKeyMappings.MOVE_TO_CONTAINER),
                                                                fullKumaKeyMappingAsString(ModKeyMappings.MOVE_TO_INVENTORY)
                                                        )))
                                                .binding(Transferring.BUTTON_OR_KEY, () -> client().management().transferring, v -> client().management().transferring = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Transferring.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<QuickDrop>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.quick_drop"))
                                                .description(OptionDescription.of(
                                                        Component.translatable("qualityofqueso.options.quick_drop.description",
                                                                fullKumaKeyMappingAsString(ModKeyMappings.QUICK_DROP)
                                                        )))
                                                .binding(QuickDrop.KEY_ONLY, () -> client().management().quickDrop, v -> client().management().quickDrop = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(QuickDrop.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Sorting>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.sorting"))
                                                .description(OptionDescription.of(
                                                        Component.translatable("qualityofqueso.options.sorting.description",
                                                                fullKumaKeyMappingAsString(ModKeyMappings.SORT)
                                                        )))
                                                .binding(Sorting.BUTTON_OR_KEY, () -> client().sorting().sorting, v -> client().sorting().sorting = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Sorting.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.container_filtering"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.container_filtering.description")))
                                                .binding(true, () -> client().management().containerFiltering, v -> client().management().containerFiltering = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.inventory_locking"))
                                                .description(OptionDescription.of(
                                                        ModOptionUtil.serverSideOption(
                                                                Component.translatable("qualityofqueso.options.inventory_locking.description")
                                                        )
                                                ))
                                                .binding(true, () -> common().inventoryLocking, v -> common().inventoryLocking = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.quick_equip"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.quick_equip.description",
                                                        fullKumaKeyMappingAsString(ModKeyMappings.QUICK_EQUIP))))
                                                .binding(true, () -> client().management().quickEquip, v -> client().management().quickEquip = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.management.moving"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.moving.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.scroll_moving"))
                                                .description(OptionDescription.of(
                                                        Component.translatable("qualityofqueso.options.scroll_moving.description"))
                                                )
                                                .binding(true, () -> client().management().scrollMoving, v -> client().management().scrollMoving = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.ctrl_moving"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.ctrl_moving.description")))
                                                .binding(true, () -> client().management().ctrlMoving, v -> client().management().ctrlMoving = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .addListener((option, event) -> {
                                                    boolean bl = option.pendingValue();
                                                    matchingItemsColorOption.setAvailable(bl);
                                                })
                                                .build()
                                )
                                .option(
                                        matchingItemsColorOption
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.drag_moving"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.drag_moving.description")))
                                                .binding(true, () -> client().management().dragMoving, v -> client().management().dragMoving = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.management.locked_slots"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.locked_slots.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.lock_slots"))
                                                .description(OptionDescription.of(
                                                        Component.translatable("qualityofqueso.options.lock_slots.description",
                                                                fullKumaKeyMappingAsString(ModKeyMappings.LOCK_SLOT)
                                                        )))
                                                .binding(true, () -> client().lockedSlots().lockedSlots, v -> client().lockedSlots().lockedSlots = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .addListener((opt, event) -> {
                                                    if (event == OptionEventListener.Event.STATE_CHANGE || event == OptionEventListener.Event.INITIAL) {
                                                        boolean bl = opt.pendingValue();
                                                        showLockOption.setAvailable(bl);
                                                        lockSoundOption.setAvailable(bl);
                                                        lockedSlotColorOption.setAvailable(bl);
                                                        preventDroppingOption.setAvailable(bl);
                                                        hardLockSlotsOption.setAvailable(bl);
                                                    }
                                                })
                                                .build()
                                )
                                .option(
                                        showLockOption
                                )
                                .option(
                                        lockSoundOption
                                )
                                .option(
                                        lockedSlotColorOption
                                )
                                .option(
                                        preventDroppingOption
                                )
                                .option(
                                        hardLockSlotsOption
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.management.button_display_options"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.button_display_options.description")))
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.search_transportables"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.search_transportables.description")))
                                                .binding(true, () -> client().buttonDisplayOptions().displaySearchTransportables, v -> client().buttonDisplayOptions().displaySearchTransportables = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.lock_inventory"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.lock_inventory.description")))
                                                .binding(true, () -> client().buttonDisplayOptions().displayLockInventory, v -> client().buttonDisplayOptions().displayLockInventory = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.bulk_craft"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.bulk_craft.description")))
                                                .binding(true, () -> client().buttonDisplayOptions().displayBulkCraft, v -> client().buttonDisplayOptions().displayBulkCraft = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.bulk_trade"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.bulk_trade.description")))
                                                .binding(true, () -> client().buttonDisplayOptions().displayBulkTrade, v -> client().buttonDisplayOptions().displayBulkTrade = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.safe_bulk"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.safe_bulk.description")))
                                                .binding(true, () -> client().buttonDisplayOptions().safeBulk, v -> client().buttonDisplayOptions().safeBulk = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.always_quick_move"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.always_quick_move.description")))
                                                .binding(false, () -> client().buttonDisplayOptions().displayAlwaysQuickMove, v -> client().buttonDisplayOptions().displayAlwaysQuickMove = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<IncludeHotbar>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.include_hotbar"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.include_hotbar.description")))
                                                .binding(IncludeHotbar.ALWAYS, () -> client().buttonDisplayOptions().displayIncludeHotbar, v -> client().buttonDisplayOptions().displayIncludeHotbar = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(IncludeHotbar.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<FilteringButton>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.filtering"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.filtering.description")))
                                                .binding(FilteringButton.ALWAYS, () -> client().buttonDisplayOptions().displayFiltering, v -> client().buttonDisplayOptions().displayFiltering = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(FilteringButton.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .build()
                )
                .group(
                        OptionGroup.createBuilder()
                                .name(Component.translatable("qualityofqueso.options.management.advanced"))
                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.advanced.description")))
                                .option(
                                        Option.<Swapping>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.swapping"))
                                                .description(OptionDescription.of(
                                                        Component.translatable("qualityofqueso.options.swapping.description",
                                                                fullKumaKeyMappingAsString(ModKeyMappings.SWAP_ITEMS)
                                                        )))
                                                .binding(Swapping.OFF, () -> client().management().swapping, v -> client().management().swapping = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Swapping.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.drag_sorting"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.drag_sorting.description")))
                                                .binding(true, () -> client().management().dragSorting, v -> client().management().dragSorting = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        defaultSortingModeOption
                                )
                                .option(
                                        useGlobalSortingModeOption
                                )
                                .option(
                                        globalSortingModeOption
                                )
                                .build()
                )
                .group(
                        horizontalButtonLayout
                )
                .group(
                        verticalButtonLayout
                )
                .build();
    }
}