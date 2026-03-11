package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.util.GuiUtil.PLAYER_FALL_DISTANCE;
import static net.dillon.qualityofqueso.util.GuiUtil.SHOULD_WARN_OF_ELYTRA;
import static net.dillon.qualityofqueso.util.ModUtil.modEnabled;
import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Unique
    private int elytraWarningCooldown;

    /**
     * Tracks items to display total count near hotbar (when thrown {@code in-game}).
     */
    @Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onThrowFromInGame(boolean entireStack, CallbackInfoReturnable<Boolean> cir, ServerboundPlayerActionPacket.Action action, ItemStack itemStack) {
        if (!modEnabled(Minecraft.getInstance()) || !options().hud.displayOnThrow) {
            return;
        }

        ItemHudTracker.setStack(itemStack.copy());
    }

    /**
     * Warns when the player is falling far enough to need an elytra while one exists in inventory but is not equipped.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void playElytraWarningSound(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;

        if (!modEnabled(Minecraft.getInstance()) || !options().misc.elytraAlarm) {
            this.elytraWarningCooldown = 0;
            return;
        }

        boolean hasElytraEquipped = player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
        boolean playerHasElytra = false;
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.is(Items.ELYTRA)) {
                playerHasElytra = true;
                break;
            }
        }

        PLAYER_FALL_DISTANCE = player.fallDistance;

        SHOULD_WARN_OF_ELYTRA = playerHasElytra
                && !hasElytraEquipped
                && !player.onGround()
                && !player.isFallFlying()
                && (player.gameMode() == GameType.SURVIVAL || player.gameMode() == GameType.ADVENTURE)
                && PLAYER_FALL_DISTANCE >= options().misc.minElytraFallDistance;

        if (!SHOULD_WARN_OF_ELYTRA) {
            this.elytraWarningCooldown = 0;
            return;
        }

        if (this.elytraWarningCooldown <= 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARROW_HIT_PLAYER, 0.2F, 1.0F));
            this.elytraWarningCooldown = options().accessibility.elytraAlarmSoundDelay;
            return;
        }

        this.elytraWarningCooldown--;
    }

    /**
     * Tracks arrows to display total count near hotbar after firing a bow.
     */
    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void onBowFired(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (!modEnabled(Minecraft.getInstance()) || player.isCreative() || !player.level().isClientSide() || !options().hud.showArrowCount) {
            return;
        }

        ItemStack useItem = player.getUseItem();
        if (!(useItem.getItem() instanceof ProjectileWeaponItem) || BowItem.getPowerForTime(player.getTicksUsingItem()) < 0.1F) {
            return;
        }

        ItemStack projectile = player.getProjectile(useItem);
        ItemHudTracker.setStack(projectile.isEmpty() ? new ItemStack(Items.ARROW) : projectile.copyWithCount(1));
    }
}