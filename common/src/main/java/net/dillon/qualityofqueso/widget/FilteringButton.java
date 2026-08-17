package net.dillon.qualityofqueso.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.eum.management.FilteringMode;
import net.dillon.qualityofqueso.screen.FilterItemsScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isDropperDispenserOrHopperScreen;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * A button to only transfer what is present in the opposite container.
 */
public class FilteringButton extends ToggleableButton {
    private final Minecraft minecraft;
    private final AbstractContainerScreen<?> parent;

    public FilteringButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress, Minecraft minecraft, AbstractContainerScreen<?> parent) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
        this.minecraft = minecraft;
        this.parent = parent;
    }

    /**
     * @return a string of "fill what's present".
     */
    private static String of(String s) {
        return "filtering/" + s;
    }

    @Override
    protected String onTextureId() {
        boolean trackedFilteringEnabled = ContainerHelper.isTrackedFilteringActive();
        if (!trackedFilteringEnabled) {
            return switch (client().management().filteringMode) {
                case NONE -> of("move_anything");
                case MATCHING -> of("move_matching_items");
                case CURRENT_STACKS -> of("current_stacks");
            };
        } else {
            boolean tagFiltered = ContainerHelper.CURRENT_FILTER_TYPE.tag();
            if (ContainerHelper.CURRENT_FILTER_MODE == FilteringMode.CURRENT_STACKS) {
                return of(tagFiltered ? "current_stacks_tag_filtered" : "current_stacks_alphabetical_filtered");
            } else {
                return of(tagFiltered ? "matching_tag_filtered" : "matching_alphabetical_filtered");
            }
        }
    }

    @Override
    protected String offTextureId() {
        return of("move_anything");
    }

    @Override
    protected boolean option() {
        boolean trackedFilteringEnabled = ContainerHelper.isTrackedFilteringActive();
        return trackedFilteringEnabled || client().management().filteringMode.matchingOrCurrentStacks();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        boolean trackedFilteringEnabled = ContainerHelper.isTrackedFilteringActive();
        if (trackedFilteringEnabled) {
            if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                ContainerHelper.OPENING_PLACEHOLDER_SCREEN = true;
                openScreen(new FilterItemsScreen(this.parent));
            } else {
                ContainerHelper.cycleCurrentFilteringMode();
                this.playDownSound(Minecraft.getInstance().getSoundManager());
            }
            return true;
        }
        return super.mouseClicked(event, isDouble);
    }

    @Override
    protected Component getTooltipToRender() {
        Component original;
        switch (client().management().filteringMode) {
            case MATCHING -> original = Component.translatable("qualityofqueso.gui.move_matching");
            case CURRENT_STACKS -> original = Component.translatable("qualityofqueso.gui.move_current_stacks");
            default -> original = Component.translatable("qualityofqueso.gui.move_anything");
        }
        boolean trackedFilteringEnabled = ContainerHelper.isTrackedFilteringActive();
        if (isDropperDispenserOrHopperScreen(getScreen())) {
            return original;
        } else if (trackedFilteringEnabled) { // For filtered containers
            String filterType = ContainerHelper.CURRENT_FILTER_TYPE.tag()
                    ? "qualityofqueso.gui.tag_filtered"
                    : "qualityofqueso.gui.item_filtered";
            String filterMode = ContainerHelper.CURRENT_FILTER_MODE == FilteringMode.CURRENT_STACKS
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
                            .withColor(ContainerHelper.CURRENT_FILTER_TYPE.tag() ? ModConstants.TAG_COLOR : ModConstants.ITEM_COLOR),

                    // appended param
                    Component.translatable("qualityofqueso.gui.right_click_switch")
            );
        } else {
            Component markContainerText = Component.translatable("qualityofqueso.gui.mark_container");
            return original.copy().append(markContainerText);
        }
    }
}