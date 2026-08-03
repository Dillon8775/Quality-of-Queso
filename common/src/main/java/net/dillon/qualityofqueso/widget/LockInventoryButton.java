package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.option.eum.management.LockInventory;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

/**
 * Allows the player to "lock" their inventory, making them have to shift + click on an item to pick it up.
 */
public class LockInventoryButton extends ToggleableButton {

    public LockInventoryButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    protected String onTextureId() {
        return clientOptionsInstance().getManagementOptions().lockInventory == LockInventory.LOCKED
                ? "lock_inventory/inventory_locked"
                : "lock_inventory/inventory_soft_locked";
    }

    @Override
    protected String offTextureId() {
        return "lock_inventory/inventory_unlocked";
    }

    @Override
    protected boolean option() {
        return clientOptionsInstance().getManagementOptions().lockInventory.inventoryLocked();
    }

    @Override
    protected Component getTooltipToRender() {
        return switch (clientOptionsInstance().getManagementOptions().lockInventory) {
            case UNLOCKED -> Component.translatable("qualityofqueso.gui.inventory_unlocked");
            case LOCKED -> Component.translatable("qualityofqueso.gui.inventory_locked");
            case SOFT_LOCKED -> Component.translatable("qualityofqueso.gui.inventory_soft_locked");
        };
    }

    protected void activateButton() {
        this.active = QualityOfQuesoPlatforms.getClientPlatform().canSendPacket(Minecraft.getInstance().player);
    }
}