package net.dillon.qualityofqueso.mixin.client.util;

import com.mojang.authlib.GameProfile;
import net.dillon.qualityofqueso.main.ClientEvents;
import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.helper.GuiHelper.*;
import static net.dillon.qualityofqueso.helper.ManagementHelper.playButtonInactiveSound;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.PLAYER_FALL_DISTANCE;
import static net.dillon.qualityofqueso.util.ModConstants.SHOULD_WARN_OF_ELYTRA;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow
    @Final
    protected Minecraft minecraft;
    @Unique
    private int elytraWarningCooldown;
    @Unique
    private final boolean[] playedDing = new boolean[4];
    @Unique
    private final int[] timesToPlay = new int[4];
    @Unique
    private final int[] dingCooldown = new int[4];

    public LocalPlayerMixin(ClientLevel level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    /**
     * Clears the armor status hud when respawning.
     */
    @Inject(method = "respawn", at = @At("TAIL"))
    private void resetArmorStateWhenRespawning(CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        ClientEvents.afterLevelChangeOrRespawn();
    }

    /**
     * Prevents dropping locked hotbar slots while in-game.
     */
    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void preventDropFromLockedSlot(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft) || !options().lockedSlots.enableLockedSlots) {
            return;
        }

        if (isLockedHotbarSlot(this.minecraft, true)) {
            playButtonInactiveSound(this.minecraft);
            cir.setReturnValue(false);
        }
    }

    /**
     * Tracks items to display total count near hotbar (when thrown {@code in-game}).
     */
    @Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onThrowFromInGame(boolean entireStack, CallbackInfoReturnable<Boolean> cir, ServerboundPlayerActionPacket.Action action, ItemStack itemStack) {
        if (!modEnabled(Minecraft.getInstance()) || !options().itemCounter.displayOnThrow || !itemStack.isStackable()) {
            return;
        }

        ItemHudTracker.setStack(itemStack.copy(), false);
    }

    /**
     * Tracks arrows to display total count near hotbar after firing a bow.
     */
    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void onBowUse(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (!modEnabled(Minecraft.getInstance()) || player.isCreative() || !player.level().isClientSide() || !options().itemCounter.arrowCounter) {
            return;
        }

        ItemStack useItem = player.getUseItem();
        if (!(useItem.getItem() instanceof ProjectileWeaponItem) || BowItem.getPowerForTime(player.getTicksUsingItem()) < 0.1F) {
            return;
        }

        ItemStack projectile = player.getProjectile(useItem);

        if (!hasInfinity(useItem) || !projectile.is(Items.ARROW)) {
            ItemHudTracker.setStack(projectile.isEmpty() ? new ItemStack(Items.ARROW) : projectile.copyWithCount(1), true);
        }
    }

    /**
     * Warns when the player is falling far enough to need an elytra while one exists in inventory but is not equipped. Also implements armor ding effects.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void playDingSounds(CallbackInfo ci) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        LocalPlayer player = (LocalPlayer) (Object) this;

        if (options().misc.armorDing) {
            for (int i = 0; i < this.playedDing.length; i++) {
                EquipmentSlot slot = equipmentSlots()[i];
                ItemStack stack = this.getItemBySlot(slot);

                boolean lowDurability = getItemHealthPercentage(stack) < 0.11F;

                // First tick entering low durability
                if (lowDurability && !this.playedDing[i]) {
                    this.timesToPlay[i] = 3;
                    this.playedDing[i] = true;
                }

                if (this.dingCooldown[i] > 0) {
                    this.dingCooldown[i]--;
                }

                // Play one ding per tick
                if (this.timesToPlay[i] > 0 && this.dingCooldown[i] == 0) {
                    playDingSound(this.minecraft);
                    this.timesToPlay[i]--;
                    this.dingCooldown[i] = 3;
                }

                // Reset when durability is no longer low
                if (!lowDurability) {
                    this.playedDing[i] = false;
                    this.timesToPlay[i] = 0;
                    this.dingCooldown[i] = 0;
                }
            }
        }

        if (!options().elytraAlarm.enableElytraAlarm.enabled()) {
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
                && !player.getAbilities().mayfly
                && !player.getAbilities().flying
                && (player.gameMode() == GameType.SURVIVAL || player.gameMode() == GameType.ADVENTURE)
                && PLAYER_FALL_DISTANCE >= options().elytraAlarm.minFallDistance;

        for (String itemName : options().elytraAlarm.blacklistedItems) {
            if (isHoldingItem(player, itemName)) {
                SHOULD_WARN_OF_ELYTRA = false;
                break;
            }
        }

        if (!SHOULD_WARN_OF_ELYTRA || options().elytraAlarm.enableElytraAlarm.indicatorOnly()) {
            this.elytraWarningCooldown = 0;
            return;
        }

        if (this.elytraWarningCooldown <= 0) {
            playDingSound(this.minecraft);
            this.elytraWarningCooldown = options().elytraAlarm.soundDelayTicks;
            return;
        }

        this.elytraWarningCooldown--;
    }
}