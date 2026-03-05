package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.util.ModUtil.modEnabled;
import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    /**
     * Tracks items to display total count near hotbar (when thrown {@code in-game}).
     */
    @Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onThrowFromInGame(boolean entireStack, CallbackInfoReturnable<Boolean> cir, ServerboundPlayerActionPacket.Action action, ItemStack itemStack) {
        if (!modEnabled(Minecraft.getInstance()) || !options().displayOnThrow) {
            return;
        }

        PickupHudTracker.setStack(itemStack.copy());
    }
}