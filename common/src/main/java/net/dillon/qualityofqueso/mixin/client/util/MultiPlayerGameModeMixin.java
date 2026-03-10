package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.util.ContainerTracker;
import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.util.ButtonUtil.playButtonSound;
import static net.dillon.qualityofqueso.util.ContainerTracker.isValidBlockEntity;
import static net.dillon.qualityofqueso.util.ModUtil.modEnabled;
import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    /**
     * Toggles chest tracking with shift + left click.
     */
    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void onStartDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!modEnabled(minecraft) || !options().containerFiltering || minecraft.player == null || minecraft.level == null || !minecraft.player.isShiftKeyDown() || ContainerTracker.COOLDOWN > 0) {
            return;
        }

        BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        if (!isValidBlockEntity(blockEntity)) {
            return;
        }

        boolean tracked = ContainerTracker.toggleTracked(minecraft.level, pos);
        Component container = blockEntity instanceof ShulkerBoxBlockEntity ? Component.literal("shulker box").withStyle(ChatFormatting.LIGHT_PURPLE)
                : blockEntity instanceof BarrelBlockEntity ? Component.literal("barrel").withStyle(ChatFormatting.GOLD)
                : Component.literal("chest").withStyle(ChatFormatting.GOLD);
        minecraft.player.sendSystemMessage(Component.translatable(
                tracked ? "qualityofqueso.gui.save_filtered_container"
                        : "qualityofqueso.gui.remove_filtered_container",
                container.copy().withStyle(ChatFormatting.BOLD),
                pos.getX(),
                pos.getY(),
                pos.getZ()
        ));
        playButtonSound(minecraft, false);
        ContainerTracker.COOLDOWN = ContainerTracker.DEFAULT_COOLDOWN;
        cir.setReturnValue(false);
        cir.cancel();
    }

    /**
     * Prevents creative instant-break in creative mode when shift + left clicking to toggle container tracking.
     */
    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!modEnabled(minecraft) || !options().containerFiltering || minecraft.player == null || minecraft.level == null || !minecraft.player.isCreative() || !minecraft.player.isShiftKeyDown()) {
            return;
        }

        BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        if (!isValidBlockEntity(blockEntity)) {
            return;
        }

        cir.setReturnValue(false);
        cir.cancel();
    }

    /**
     * Remembers chest use so the next opening chest screen can be identified.
     */
    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void onUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!modEnabled(Minecraft.getInstance()) || !options().containerFiltering) {
            return;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(hitResult.getBlockPos());
        if (isValidBlockEntity(blockEntity)) {
            ContainerTracker.rememberOpened(player.level(), hitResult.getBlockPos());
        } else {
            ContainerTracker.clearPendingOpened();
        }
    }

    /**
     * Tracks arrows to display total count near hotbar when a charged crossbow is fired.
     */
    @Inject(method = "useItem", at = @At("HEAD"))
    private void onCrossbowFireOrUse(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(player instanceof LocalPlayer localPlayer)) {
            return;
        }

        if (!modEnabled(Minecraft.getInstance()) || localPlayer.isCreative() || !localPlayer.level().isClientSide() || !options().showArrowCount) {
            return;
        }

        ItemStack used = localPlayer.getItemInHand(hand);
        if (!(used.getItem() instanceof CrossbowItem) || !CrossbowItem.isCharged(used)) {
            return;
        }

        ChargedProjectiles chargedProjectiles = player.getItemInHand(hand).get(DataComponents.CHARGED_PROJECTILES);
        ItemHudTracker.setStack(chargedProjectiles.isEmpty() ? new ItemStack(Items.ARROW) : chargedProjectiles.itemCopies().get(0));
    }
}
