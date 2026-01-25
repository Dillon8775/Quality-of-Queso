package net.dillon.qualityofqueso.mixin.util;

import com.mojang.authlib.GameProfile;
import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.main.QoQ.modEnabled;
import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    /**
     * Tracks items to display total count near hotbar (when thrown {@code in-game}).
     */
    @Inject(method = "dropSelectedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onThrowFromInGame(boolean entireStack, CallbackInfoReturnable<Boolean> cir, PlayerActionC2SPacket.Action action, ItemStack itemStack) {
        if (!modEnabled(MinecraftClient.getInstance()) || !options().displayOnThrow) {
            return;
        }

        PickupHudTracker.setStack(itemStack.copy());
    }
}