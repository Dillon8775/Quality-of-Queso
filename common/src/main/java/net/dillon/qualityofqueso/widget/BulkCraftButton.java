package net.dillon.qualityofqueso.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * A button which bulk crafts a selected recipe.
 */
public class BulkCraftButton extends ToggleableButton {

    public BulkCraftButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    protected String onTextureId() {
        return "bulk_craft/bulk_craft";
    }

    @Override
    protected String offTextureId() {
        return "bulk_craft/normal_craft";
    }

    @Override
    protected boolean option() {
        return client().management().bulkCraft;
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable(
                this.option()
                        ? "qualityofqueso.gui.bulk_craft"
                        : "qualityofqueso.gui.normal_craft"
        );
    }
}