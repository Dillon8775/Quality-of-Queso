package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.screen.FilterItemsScreen;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.ManagementHelper.isDropperDispenserOrHopperScreen;
import static net.dillon.qualityofqueso.helper.ModHelper.getCurrentScreen;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

/**
 * A button to only transfer what is present in the opposite container.
 */
public class MoveMatchingItemsButton extends ToggleableButton {
    private final Minecraft minecraft;
    private final AbstractContainerScreen<?> parent;

    public MoveMatchingItemsButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress, Minecraft minecraft, AbstractContainerScreen<?> parent) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
        this.minecraft = minecraft;
        this.parent = parent;
    }

    /**
     * @return a string of "fill what's present".
     */
    private static String of(String s) {
        return "move_matching_items/" + s;
    }

    @Override
    protected String onTextureId() {
        if (!ContainerHelper.IS_TRACKED_CONTAINER) {
            return of("move_matching_items");
        } else {
            if (ContainerHelper.CURRENT_FILTER_MODE.tag()) {
                return of("filtered_tag");
            } else {
                return of("filtered");
            }
        }
    }

    @Override
    protected String offTextureId() {
        return of("move_anything");
    }

    @Override
    protected boolean option() {
        return ContainerHelper.IS_TRACKED_CONTAINER || options().management.moveMatchingItems;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int bl) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, bl);
        }

        if (ContainerHelper.IS_TRACKED_CONTAINER) {
            if (bl == 1) {
                ContainerHelper.OPENING_PLACEHOLDER_SCREEN = true;
                this.minecraft.setScreen(new FilterItemsScreen(this.parent));
            } else {
                ContainerHelper.toggleCurrentFilterMode();
                this.playDownSound(Minecraft.getInstance().getSoundManager());
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, bl);
    }

    @Override
    protected Component getTooltipToRender() {
        if (isDropperDispenserOrHopperScreen(getCurrentScreen())) {
            return this.option()
                    ? Component.translatable("qualityofqueso.gui.move_matching_items")
                    : Component.translatable("qualityofqueso.gui.move_anything");
        } else if (ContainerHelper.IS_TRACKED_CONTAINER) {
            String filterModeKey = ContainerHelper.CURRENT_FILTER_MODE.tag()
                    ? "qualityofqueso.gui.tag_filtered"
                    : "qualityofqueso.gui.item_filtered";
            return Component.translatable(
                    "qualityofqueso.gui.filtered_mode",
                    // params
                    Component.translatable(filterModeKey).copy()
                            .withColor(ContainerHelper.CURRENT_FILTER_MODE.tag() ? ModTexts.TAG_COLOR : ModTexts.ITEM_COLOR),
                    Component.translatable("qualityofqueso.gui.right_click_switch")
            );
        } else {
            Component markContainerText = Component.translatable("qualityofqueso.gui.mark_container");
            if (this.option()) {
                return Component.translatable("qualityofqueso.gui.move_matching_items")
                        .copy().append(markContainerText);
            } else {
                return Component.translatable("qualityofqueso.gui.move_anything")
                        .copy().append(markContainerText);
            }
        }
    }
}
