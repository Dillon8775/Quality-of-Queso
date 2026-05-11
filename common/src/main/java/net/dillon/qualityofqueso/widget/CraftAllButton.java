package net.dillon.qualityofqueso.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.ModHelper.options;

/**
 * A button which bulk crafts a selected recipe.
 */
public class CraftAllButton extends ToggleableButton {

    public CraftAllButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    protected String onTextureId() {
        return "craft_all/craft_all";
    }

    @Override
    protected String offTextureId() {
        return "craft_all/craft_normal";
    }

    @Override
    protected boolean option() {
        return options().management.craftAll;
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable(
                this.option()
                        ? "qualityofqueso.gui.craft_all"
                        : "qualityofqueso.gui.craft_normal"
        );
    }
}