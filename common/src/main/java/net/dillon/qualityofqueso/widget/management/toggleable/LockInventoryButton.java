package net.dillon.qualityofqueso.widget.management.toggleable;

import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.management.LockInventory;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.task.ClientTasks.getLocalPlayer;
import static net.dillon.qualityofqueso.helper.ModHelper.sendClientPreferencesToServer;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A management button that locks the player's inventory, allowing only certain items to be picked up.
 */
public class LockInventoryButton extends ToggleableButton {

    public LockInventoryButton(ManagementParams params) {
        super(params, b -> {
            updateClient(ModClientOptions::cycleLockedInventoryMode);
            sendClientPreferencesToServer();
        });
    }

    @Override
    protected boolean toggle() {
        return client().management().lockInventory.inventoryLocked();
    }

    @Override
    protected ManagementSprites getManagementSprites() {
        return new ManagementSprites(
                managementIdentifier(
                        client().management().lockInventory == LockInventory.LOCKED
                                ? "lock_inventory/inventory_locked"
                                : "lock_inventory/inventory_soft_locked"
                ),
                managementIdentifier("lock_inventory/inventory_unlocked")
        );
    }

    @Override
    protected Component getDefaultTooltip() {
        return switch (client().management().lockInventory) {
            case UNLOCKED -> Component.translatable("qualityofqueso.gui.inventory_unlocked",
                    this.canBeActive()
                            ? ""
                            : Component.translatable("qualityofqueso.gui.inventory_unlocked_blocked")
                            .withStyle(ChatFormatting.RED)
            );
            case LOCKED -> Component.translatable("qualityofqueso.gui.inventory_locked");
            case SOFT_LOCKED -> Component.translatable("qualityofqueso.gui.inventory_soft_locked");
        };
    }

    @Override
    protected boolean canBeActive() {
        return QualityOfQuesoPlatforms.getClientPlatform().canSendPacket(getLocalPlayer());
    }
}