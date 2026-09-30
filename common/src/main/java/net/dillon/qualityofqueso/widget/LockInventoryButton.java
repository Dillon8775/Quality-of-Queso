package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.option.eum.management.LockInventory;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Allows the player to "lock" their inventory, making them have to shift + click on an item to pick it up.
 */
public class LockInventoryButton extends ToggleableButton {

    public LockInventoryButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    protected String onTextureId() {
        return client().management().lockInventory == LockInventory.LOCKED
                ? "lock_inventory/inventory_locked"
                : "lock_inventory/inventory_soft_locked";
    }

    @Override
    protected String offTextureId() {
        return "lock_inventory/inventory_unlocked";
    }

    @Override
    protected boolean option() {
        return client().management().lockInventory.inventoryLocked();
    }

    @Override
    protected Component getTooltipToRender() {
        return switch (client().management().lockInventory) {
            case UNLOCKED -> Component.translatable("qualityofqueso.gui.inventory_unlocked",
                    canUse()
                            ? ""
                            : Component.translatable("qualityofqueso.gui.inventory_unlocked_blocked")
                            .withStyle(ChatFormatting.RED)
            );
            case LOCKED -> Component.translatable("qualityofqueso.gui.inventory_locked");
            case SOFT_LOCKED -> Component.translatable("qualityofqueso.gui.inventory_soft_locked");
        };
    }

    @Override
    protected void activateButton() {
        this.active = canUse();
    }

    /**
     * @return if the button can be used.
     */
    private boolean canUse() {
        return QualityOfQuesoPlatforms.getClientPlatform().canSendPacket(Minecraft.getInstance().player);
    }
}