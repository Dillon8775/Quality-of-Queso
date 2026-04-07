package net.dillon.qualityofqueso.button;

import net.dillon.qualityofqueso.screen.FilterItemsScreen;
import net.dillon.qualityofqueso.util.ContainerUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A button to only transfer what is present in the opposite container.
 */
public class FillWhatsPresentButton extends ToggleableButton {
    private final Minecraft minecraft;
    private final AbstractContainerScreen<?> parent;

    public FillWhatsPresentButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress, Minecraft minecraft, AbstractContainerScreen<?> parent) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
        this.minecraft = minecraft;
        this.parent = parent;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (ContainerUtil.IS_TRACKED_CONTAINER) {
            if (event.button() == 1) {
                ContainerUtil.OPENING_PLACEHOLDER_SCREEN = true;
                this.minecraft.setScreen(new FilterItemsScreen(this.parent));
            } else {
                ContainerUtil.toggleCurrentFilterMode();
                this.playDownSound(Minecraft.getInstance().getSoundManager());
            }
            return true;
        }
        return super.mouseClicked(event, isDouble);
    }

    /**
     * @return a string of "fill what's present".
     */
    private static String of(String s) {
        return "fill_whats_present/" + s;
    }

    @Override
    protected String onTextureId() {
        if (!ContainerUtil.IS_TRACKED_CONTAINER) {
            return of("fill_whats_present");
        } else {
            if (ContainerUtil.CURRENT_FILTER_MODE.tag()) {
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
        return options().management.fillWhatsPreset;
    }

    @Override
    protected Component getTooltipToRender() {
        if (ContainerUtil.IS_TRACKED_CONTAINER) {
            String filterModeKey = ContainerUtil.CURRENT_FILTER_MODE.tag()
                    ? "qualityofqueso.gui.tag_filtered"
                    : "qualityofqueso.gui.item_filtered";
            return Component.translatable(
                    "qualityofqueso.gui.filtered_mode",
                    // params
                    Component.translatable(filterModeKey).copy()
                            .withColor(ContainerUtil.CURRENT_FILTER_MODE.tag() ? ModTexts.TAG_COLOR : ModTexts.ITEM_COLOR),
                    Component.translatable("qualityofqueso.gui.right_click_switch")
            );
        } else {
            Component markContainerText = Component.translatable("qualityofqueso.gui.mark_container");
            if (this.option()) {
                return Component.translatable("qualityofqueso.gui.fill_whats_present")
                        .copy().append(Component.translatable("qualityofqueso.gui.disable_fill_whats_present"))
                        .copy().append(markContainerText);
            } else {
                return Component.translatable("qualityofqueso.gui.move_anything")
                        .copy().append(Component.translatable("qualityofqueso.gui.disable_move_anything"))
                        .copy().append(markContainerText);
            }
        }
    }
}