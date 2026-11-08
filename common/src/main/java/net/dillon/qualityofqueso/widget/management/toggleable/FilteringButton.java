package net.dillon.qualityofqueso.widget.management.toggleable;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.container.ContainerSyncer;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.management.FilteringMode;
import net.dillon.qualityofqueso.screen.FilterItemsScreen;
import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isDropperDispenserOrHopperScreen;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A versatile management button, which only allows certain item transfers based on if the item is already present. Also works with filtered containers, and opens the {@link FilterItemsScreen}.
 */
public class FilteringButton extends ToggleableButton {

    public FilteringButton(ManagementParams params) {
        super(params, b -> {
            if (!ContainerSyncer.isTrackedFilteringActive()) {
                updateClient(ModClientOptions::cycleFilteringMode);
            }
        });
    }

    @Override
    protected boolean toggle() {
        boolean trackedFilteringEnabled = ContainerSyncer.isTrackedFilteringActive();
        return trackedFilteringEnabled || client().management().filteringMode.matchingOrCurrentStacks();
    }

    @Override
    protected ManagementSprites getManagementSprites() {
        String enabledSprite;

        boolean trackedFilteringEnabled = ContainerSyncer.isTrackedFilteringActive();
        if (!trackedFilteringEnabled) {
            enabledSprite = switch (client().management().filteringMode) {
                case NONE -> "move_anything";
                case MATCHING -> "move_matching_items";
                case CURRENT_STACKS -> "current_stacks";
            };
        } else {
            boolean tagFiltered = ContainerSyncer.CURRENT_FILTER_TYPE.tag();
            if (ContainerSyncer.CURRENT_FILTER_MODE == FilteringMode.CURRENT_STACKS) {
                enabledSprite = tagFiltered ? "current_stacks_tag_filtered" : "current_stacks_alphabetical_filtered";
            } else {
                enabledSprite = tagFiltered ? "matching_tag_filtered" : "matching_alphabetical_filtered";
            }
        }

        return new ManagementSprites(
                managementIdentifier("filtering/" + enabledSprite),
                managementIdentifier("filtering/move_anything")
        );
    }

    @Override
    protected Component getDefaultTooltip() {
        Component original;
        switch (client().management().filteringMode) {
            case MATCHING -> original = Component.translatable("qualityofqueso.gui.move_matching");
            case CURRENT_STACKS -> original = Component.translatable("qualityofqueso.gui.move_current_stacks");
            default -> original = Component.translatable("qualityofqueso.gui.move_anything");
        }
        boolean trackedFilteringEnabled = ContainerSyncer.isTrackedFilteringActive();
        if (isDropperDispenserOrHopperScreen() || ModConstants.INTERACTED_WITH_MINECART_CONTAINER) {
            return original;
        } else if (trackedFilteringEnabled) { // For filteredMode containers
            String filterType = ContainerSyncer.CURRENT_FILTER_TYPE.tag()
                    ? "qualityofqueso.gui.tag_filtered"
                    : "qualityofqueso.gui.item_filtered";
            String filterMode = ContainerSyncer.CURRENT_FILTER_MODE == FilteringMode.CURRENT_STACKS
                    ? "qualityofqueso.gui.filter_mode.current_stacks"
                    : "qualityofqueso.gui.filter_mode.default";
            return Component.translatable(
                    "qualityofqueso.gui.filtered_mode",
                    // filter mode param
                    Component.translatable(filterMode)
                            .copy()
                            .withStyle(ChatFormatting.GOLD),

                    // filter type param
                    Component.translatable(filterType)
                            .copy()
                            .withColor(ContainerSyncer.CURRENT_FILTER_TYPE.tag() ? ModConstants.TAG_COLOR : ModConstants.ITEM_COLOR),

                    // appended param
                    Component.translatable("qualityofqueso.gui.right_click_switch")
            );
        } else {
            Component markContainerText = Component.translatable("qualityofqueso.gui.mark_container");
            return original.copy().append(markContainerText);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        boolean trackedFilteringEnabled = ContainerSyncer.isTrackedFilteringActive();
        if (trackedFilteringEnabled) {
            if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && getScreen() instanceof AbstractContainerScreen<?> parent) {
                ContainerSyncer.OPENING_PLACEHOLDER_SCREEN = true;
                openScreen(new FilterItemsScreen(parent));
            } else {
                ContainerSyncer.cycleCurrentFilteringMode();
                this.playDownSound(getSoundManager());
            }
            return true;
        }
        return super.mouseClicked(event, isDouble);
    }
}