package net.dillon.qualityofqueso.packet;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * Client-side packet handlers.
 */
public class ClientPacketHandlers {

    /**
     * Applies synced shulker state on the client thread.
     */
    public static void handleSyncShulkerState(Player player, SyncShulkerStateS2CPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }

        minecraft.execute(() -> ContainerHelper.applySyncedShulkerState(packet));
    }
}