package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ContainerTracker;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.util.ButtonUtil.playDefaultSound;
import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A class for the include hotbar button and transportables button.
 */
public abstract class ToggleableButton extends TransferButton {
    private final String on;
    private final String off;
    private final String[] tooltip;

    public ToggleableButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress, String on, String off, String[] tooltip) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress);
        this.on = on;
        this.off = off;
        this.tooltip = tooltip;
    }

    /**
     * @return The option to go off of.
     */
    abstract boolean option();

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        this.renderButtonTexture(this.option() ? this.on : this.off, false, this, context);
        if (this.isHovered()) {
            String name;
            switch (this.getHoverSize()) {
                case BIG -> name = "big_hovered";
                default -> name = "basic_hovered";
            }
            ButtonUtil.drawButtonTexture(context, "hovered/" + name, this);

            if (options().helpfulTooltips) {
                boolean fillWhatsPresentButton = this.tooltip.length > 2;
                if (fillWhatsPresentButton && ContainerTracker.IS_TRACKED_CONTAINER) {
                    String filterModeKey = ContainerTracker.CURRENT_FILTER_MODE.tag()
                            ? "qualityofqueso.gui.fill_whats_present/tag_filtered"
                            : "qualityofqueso.gui.fill_whats_present/item_filtered";
                    ButtonUtil.drawTooltip(
                            Component.translatable(
                                    "qualityofqueso.gui.fill_whats_present/filtered_mode",
                                    this.option() ? Component.translatable(this.tooltip[1]) : Component.translatable(this.tooltip[2]),
                                    Component.translatable(filterModeKey).copy()
                                            .withColor(ContainerTracker.CURRENT_FILTER_MODE.tag() ? 0x7FFFFF : 0x96FFB7),
                                    Component.translatable("qualityofqueso.gui.fill_whats_present/right_click_switch")
                            ),
                            context, this.font, mouseX, mouseY
                    );
                } else {
                    ButtonUtil.drawTooltip(this.option() ?
                            Component.translatable(this.tooltip[fillWhatsPresentButton ? 1 : 0])
                                    .copy().append(fillWhatsPresentButton
                                            ? Component.translatable("qualityofqueso.gui.fill_whats_present/disable_fill_whats_present")
                                            : ModTexts.BLANK)
                                    .copy().append(fillWhatsPresentButton
                                            ? Component.translatable("qualityofqueso.gui.fill_whats_present/mark_container")
                                            : ModTexts.BLANK):
                            Component.translatable(this.tooltip[fillWhatsPresentButton ? 2 : 1])
                                    .copy().append(fillWhatsPresentButton
                                            ? Component.translatable("qualityofqueso.gui.fill_whats_present/disable_move_anything")
                                            : ModTexts.BLANK)
                                    .copy().append(fillWhatsPresentButton
                                            ? Component.translatable("qualityofqueso.gui.fill_whats_present/mark_container")
                                            : ModTexts.BLANK), context, this.font, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void playDownSound(SoundManager manager) {
        playDefaultSound(manager);
    }
}
