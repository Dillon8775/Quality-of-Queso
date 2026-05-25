package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.management.*;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.DefaultSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.GlobalSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.Sorting;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.commonOptionsInstance;
import static net.dillon.qualityofqueso.util.ModOptionUtil.fullKumaKeyMappingAsString;

/**
 * The management options category for the {@link ConfigurationScreen}.
 */
public class ManagementCategory {

    protected static ConfigCategory create() {
        Option<DefaultSortingMode> defaultSortingModeOption = Option.<DefaultSortingMode>createBuilder()
                .name(Component.translatable("qualityofqueso.options.default_sorting_mode"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.default_sorting_mode.description")))
                .binding(DefaultSortingMode.ALPHABETICALLY, () -> clientOptionsInstance().getSortingOptions().defaultSortingMode, v -> clientOptionsInstance().getSortingOptions().defaultSortingMode = v)
                .controller(o -> EnumControllerBuilder.create(o)
                        .enumClass(DefaultSortingMode.class)
                        .formatValue(v -> Component.literal(v.getSerializedName())))
                .available(!clientOptionsInstance().getSortingOptions().useGlobalSortingMode)
                .build();

        Option<GlobalSortingMode> globalSortingModeOption = Option.<GlobalSortingMode>createBuilder()
                .name(Component.translatable("qualityofqueso.options.global_sorting_mode"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.global_sorting_mode.description")))
                .binding(GlobalSortingMode.ALPHABETICALLY, () -> clientOptionsInstance().getSortingOptions().globalSortingMode, v -> {
                    if (v.equals(GlobalSortingMode.ALPHABETICALLY)) {
                        clientOptionsInstance().getSortingOptions().currentSortingMode = CurrentSortingMode.ALPHABETICAL;
                    } else if (v.equals(GlobalSortingMode.BY_TAG)) {
                        clientOptionsInstance().getSortingOptions().currentSortingMode = CurrentSortingMode.TAG;
                    } else if (v.equals(GlobalSortingMode.DESCENDING)) {
                        clientOptionsInstance().getSortingOptions().currentSortingMode = CurrentSortingMode.COUNT_DESCENDING;
                    } else if (v.equals(GlobalSortingMode.ASCENDING)) {
                        clientOptionsInstance().getSortingOptions().currentSortingMode = CurrentSortingMode.COUNT_ASCENDING;
                    } else if (v.equals(GlobalSortingMode.CREATIVE_MENU)) {
                        clientOptionsInstance().getSortingOptions().currentSortingMode = CurrentSortingMode.CREATIVE_MENU;
                    }
                    clientOptionsInstance().getSortingOptions().globalSortingMode = v;
                })
                .controller(o -> EnumControllerBuilder.create(o)
                        .enumClass(GlobalSortingMode.class)
                        .formatValue(v -> Component.literal(v.getSerializedName())))
                .build();

        Option<Boolean> useGlobalSortingModeOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.use_global_sorting_mode"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.use_global_sorting_mode.description")))
                .binding(false, () -> clientOptionsInstance().getSortingOptions().useGlobalSortingMode, v -> clientOptionsInstance().getSortingOptions().useGlobalSortingMode = v)
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
                .binding(ShowLock.SCREEN_ONLY, () -> clientOptionsInstance().getLockedSlotOptions().showLock, v -> clientOptionsInstance().getLockedSlotOptions().showLock = v)
                .controller(o -> EnumControllerBuilder.create(o)
                        .enumClass(ShowLock.class)
                        .formatValue(v -> Component.literal(v.getSerializedName())))
                .build();

        Option<Boolean> lockSoundOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.lock_sound"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.lock_sound.description")))
                .binding(true, () -> clientOptionsInstance().getLockedSlotOptions().lockSound, v -> clientOptionsInstance().getLockedSlotOptions().lockSound = v)
                .controller(BooleanControllerBuilder::create)
                .build();

        Option<Color> lockedSlotColorOption = Option.<Color>createBuilder()
                .name(Component.translatable("qualityofqueso.options.locked_slots_color"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.locked_slots_color.description")))
                .binding(
                        new Color(ModConstants.DEFAULT_LOCKED_SLOT_COLOR, true),
                        () -> new Color(clientOptionsInstance().getLockedSlotOptions().lockedSlotColor, true),
                        v -> clientOptionsInstance().getLockedSlotOptions().lockedSlotColor = v.getRGB()
                )
                .controller(o -> ColorControllerBuilder.create(o)
                        .allowAlpha(true))
                .build();

        Option<Boolean> preventDroppingOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.prevent_dropping"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.prevent_dropping.description")))
                .binding(true, () -> clientOptionsInstance().getLockedSlotOptions().preventDropping, v -> clientOptionsInstance().getLockedSlotOptions().preventDropping = v)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<Boolean> hardLockSlotsOption = Option.<Boolean>createBuilder()
                .name(Component.translatable("qualityofqueso.options.hard_lock_slots"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.hard_lock_slots.description")))
                .binding(false, () -> clientOptionsInstance().getLockedSlotOptions().hardLockSlots, v -> clientOptionsInstance().getLockedSlotOptions().hardLockSlots = v)
                .controller(BooleanControllerBuilder::create)
                .build();

        ListOption<String> horizontalButtonLayout = ListOption.<String>createBuilder()
                .name(Component.translatable("qualityofqueso.options.management.button_order.horizontal"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.button_order.horiztonal.description")))
                .initial("")
                .controller(StringControllerBuilder::create)
                .binding(
                        ModClientOptions.DEFAULT_HORIZONTAL_BUTTON_LAYOUT,
                        () -> new ArrayList<>(clientOptionsInstance().getManagementOptions().horizontalButtonLayout),
                        v -> {
                            clientOptionsInstance().getManagementOptions().horizontalButtonLayout.clear();
                            clientOptionsInstance().getManagementOptions().horizontalButtonLayout.addAll(v);
                        }
                )
                .minimumNumberOfEntries(clientOptionsInstance().getManagementOptions().horizontalButtonLayout.size())
                .maximumNumberOfEntries(clientOptionsInstance().getManagementOptions().horizontalButtonLayout.size())
                .available(clientOptionsInstance().getManagementOptions().layout.horizontal())
                .collapsed(true)
                .build();

        ListOption<String> verticalButtonLayout = ListOption.<String>createBuilder()
                .name(Component.translatable("qualityofqueso.options.management.button_order.vertical"))
                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.management.button_order.vertical.description")))
                .initial("")
                .controller(StringControllerBuilder::create)
                .binding(
                        ModClientOptions.DEFAULT_VERTICAL_BUTTON_LAYOUT,
                        () -> new ArrayList<>(clientOptionsInstance().getManagementOptions().verticalButtonLayout),
                        v -> {
                            clientOptionsInstance().getManagementOptions().verticalButtonLayout.clear();
                            clientOptionsInstance().getManagementOptions().verticalButtonLayout.addAll(v);
                        }
                )
                .minimumNumberOfEntries(clientOptionsInstance().getManagementOptions().verticalButtonLayout.size())
                .maximumNumberOfEntries(clientOptionsInstance().getManagementOptions().verticalButtonLayout.size())
                .available(!clientOptionsInstance().getManagementOptions().layout.horizontal())
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
                                                .binding(Layout.HORIZONTAL, () -> clientOptionsInstance().getManagementOptions().layout, v -> clientOptionsInstance().getManagementOptions().layout = v)
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
                                                .binding(true, () -> clientOptionsInstance().getManagementOptions().playSounds, v -> clientOptionsInstance().getManagementOptions().playSounds = v)
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
                                                .binding(Transferring.BUTTON_OR_KEY, () -> clientOptionsInstance().getManagementOptions().transferring, v -> clientOptionsInstance().getManagementOptions().transferring = v)
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
                                                .binding(QuickDrop.KEY_ONLY, () -> clientOptionsInstance().getManagementOptions().quickDrop, v -> clientOptionsInstance().getManagementOptions().quickDrop = v)
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
                                                .binding(Sorting.BUTTON_OR_KEY, () -> clientOptionsInstance().getSortingOptions().sorting, v -> clientOptionsInstance().getSortingOptions().sorting = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Sorting.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.container_filtering"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.container_filtering.description")))
                                                .binding(true, () -> clientOptionsInstance().getManagementOptions().containerFiltering, v -> clientOptionsInstance().getManagementOptions().containerFiltering = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.inventory_locking"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.inventory_locking.description")))
                                                .binding(true, () -> commonOptionsInstance().inventoryLocking, v -> commonOptionsInstance().inventoryLocking = v)
                                                .controller(TickBoxControllerBuilder::create)
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
                                                        Component.translatable("qualityofqueso.options.scroll_moving.description",
                                                                fullKumaKeyMappingAsString(ModKeyMappings.SCROLL_MOVE)
                                                        )))
                                                .binding(true, () -> clientOptionsInstance().getManagementOptions().scrollMoving, v -> clientOptionsInstance().getManagementOptions().scrollMoving = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.drag_moving"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.drag_moving.description")))
                                                .binding(true, () -> clientOptionsInstance().getManagementOptions().dragMoving, v -> clientOptionsInstance().getManagementOptions().dragMoving = v)
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
                                                .binding(true, () -> clientOptionsInstance().getLockedSlotOptions().lockedSlots, v -> clientOptionsInstance().getLockedSlotOptions().lockedSlots = v)
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
                                                .binding(true, () -> clientOptionsInstance().getButtonDisplayOptions().displaySearchTransportables, v -> clientOptionsInstance().getButtonDisplayOptions().displaySearchTransportables = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.lock_inventory"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.lock_inventory.description")))
                                                .binding(true, () -> clientOptionsInstance().getButtonDisplayOptions().displayLockInventory, v -> clientOptionsInstance().getButtonDisplayOptions().displayLockInventory = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.bulk_craft"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.bulk_craft.description")))
                                                .binding(true, () -> clientOptionsInstance().getButtonDisplayOptions().displayBulkCraft, v -> clientOptionsInstance().getButtonDisplayOptions().displayBulkCraft = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.bulk_trade"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.bulk_trade.description")))
                                                .binding(true, () -> clientOptionsInstance().getButtonDisplayOptions().displayBulkTrade, v -> clientOptionsInstance().getButtonDisplayOptions().displayBulkTrade = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.safe_bulk"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.safe_bulk.description")))
                                                .binding(true, () -> clientOptionsInstance().getButtonDisplayOptions().safeBulk, v -> clientOptionsInstance().getButtonDisplayOptions().safeBulk = v)
                                                .controller(BooleanControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.always_quick_move"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.always_quick_move.description")))
                                                .binding(false, () -> clientOptionsInstance().getButtonDisplayOptions().displayAlwaysQuickMove, v -> clientOptionsInstance().getButtonDisplayOptions().displayAlwaysQuickMove = v)
                                                .controller(TickBoxControllerBuilder::create)
                                                .build()
                                )
                                .option(
                                        Option.<IncludeHotbar>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.include_hotbar"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.include_hotbar.description")))
                                                .binding(IncludeHotbar.ALWAYS, () -> clientOptionsInstance().getButtonDisplayOptions().displayIncludeHotbar, v -> clientOptionsInstance().getButtonDisplayOptions().displayIncludeHotbar = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(IncludeHotbar.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<FilteringButton>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.filtering"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.filtering.description")))
                                                .binding(FilteringButton.ALWAYS, () -> clientOptionsInstance().getButtonDisplayOptions().displayFiltering, v -> clientOptionsInstance().getButtonDisplayOptions().displayFiltering = v)
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
                                                .binding(Swapping.OFF, () -> clientOptionsInstance().getManagementOptions().swapping, v -> clientOptionsInstance().getManagementOptions().swapping = v)
                                                .controller(o -> EnumControllerBuilder.create(o)
                                                        .enumClass(Swapping.class)
                                                        .formatValue(v -> Component.literal(v.getSerializedName())))
                                                .build()
                                )
                                .option(
                                        Option.<Boolean>createBuilder()
                                                .name(Component.translatable("qualityofqueso.options.drag_sorting"))
                                                .description(OptionDescription.of(Component.translatable("qualityofqueso.options.drag_sorting.description")))
                                                .binding(true, () -> clientOptionsInstance().getManagementOptions().dragSorting, v -> clientOptionsInstance().getManagementOptions().dragSorting = v)
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