package net.dillon.qualityofqueso.packet;

import net.dillon.qualityofqueso.container.ShulkerSyncer;
import net.dillon.qualityofqueso.packet.clientbound.SyncShulkerStateS2CPacket;
import net.minecraft.world.entity.player.Player;

import static net.dillon.dillonlib.task.ClientTasks.getMinecraft;

/**
 * Client-side packet handlers.
 */
public class ClientPacketHandlers {

    /**
     * Applies synced shulker state on the client thread.
     */
    public static void handleSyncShulkerState(Player player, SyncShulkerStateS2CPacket packet) {
        getMinecraft().execute(() -> ShulkerSyncer.applySyncedShulkerState(packet));
    }
}