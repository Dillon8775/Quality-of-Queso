package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.util.AccessorUtil.key;
import static net.dillon.qualityofqueso.util.ButtonUtil.playDefaultSound;
import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * Sorts items in a container, by tag -> alphabetically, or just alphabetically.
 */
public class SortButton extends TransferButton {

    public SortButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String resourceLocation, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, x, y, resourceLocation, buttonName, false, onPress, canBeActive);
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable("qualityofqueso.gui." + this.buttonName + "_button",
                options().management.tagSorting
                        ? Component.literal("by ").append(Component.literal("tag").withColor(ModTexts.TAG_COLOR))
                        : Component.literal("alphabetically").withColor(ModTexts.ITEM_COLOR)
        );
    }

    @Override
    protected void renderBaseButtonTexture(String id, AbstractWidget buttonReference, GuiGraphicsExtractor graphics) {
        ButtonUtil.drawButtonTexture(graphics, options().management.tagSorting ? id + "_tag" : id, this);
        this.renderHoveredTexture(graphics);

        if (!Minecraft.getInstance().hasControlDown()) {
            return;
        }

        if (options().accessibility.showButtonShortcuts && this.buttonName.equals("sort") && key(ModKeybinds.SORT_CONTAINER) == ModKeybinds.SORT_CONTAINER.getDefaultKey()) {
            ButtonUtil.drawButtonTexture(graphics, "shortcut/sort_button_shortcut_key", this);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (this.canBeActive.get() && event.button() == 1) {
            options().management.tagSorting = !options().management.tagSorting;
            ModClientOptions.CLIENT.save();
            playDefaultSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return super.mouseClicked(event, isDouble);
    }
}