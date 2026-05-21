package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.dillon.qualityofqueso.util.ModTexts;
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
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ContainerHelper.isValidBlockEntity;
import static net.dillon.qualityofqueso.helper.GuiHelper.isHoldingItem;
import static net.dillon.qualityofqueso.helper.ManagementHelper.playButtonSound;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.util.ModConstants.DEFAULT_TRACKED_CONTAINER_COOLDOWN;
import static net.dillon.qualityofqueso.util.ModConstants.TRACKED_CONTAINER_COOLDOWN;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Unique
    private ItemStack stackBeforeGuiThrow = ItemStack.EMPTY;
    @Unique
    private int slotBeforeGuiThrow = -1;

    /**
     * Tracks the item that the player is going to drop {@code from a GUI screen.}
     */
    @Inject(method = "handleContainerInput", at = @At("HEAD"))
    private void cacheStackBeforeThrowFromGUI(int containerId, int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        this.stackBeforeGuiThrow = ItemStack.EMPTY;
        this.slotBeforeGuiThrow = -1;
        if (containerInput != ContainerInput.THROW || player == null || slotIndex < 0) {
            return;
        }

        try {
            this.stackBeforeGuiThrow = player.containerMenu.getSlot(slotIndex).getItem().copy();
            this.slotBeforeGuiThrow = slotIndex;
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    /**
     * Tracks items to display total count near hotbar (when thrown {@code from a GUI screen}).
     */
    @Inject(method = "handleContainerInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void onThrowFromGUI(int containerId, int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        if (!modEnabled(Minecraft.getInstance()) || !options().itemCounter.displayOnThrow || containerInput != ContainerInput.THROW) {
            return;
        }

        try {
            ItemStack stack = player.containerMenu.getSlot(slotIndex).getItem();
            if (stack.isEmpty() && slotIndex == this.slotBeforeGuiThrow) {
                stack = this.stackBeforeGuiThrow;
            }
            if (stack.isEmpty() || !stack.isStackable()) {
                return;
            }

            ItemHudTracker.setStack(stack.copy(), false);
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * Tracks arrows to display total count near hotbar when a charged crossbow is fired.
     */
    @Inject(method = "useItem", at = @At("HEAD"))
    private void onCrossbowUse(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(player instanceof LocalPlayer localPlayer)) {
            return;
        }

        if (!modEnabled(Minecraft.getInstance()) || localPlayer.isCreative() || !localPlayer.level().isClientSide() || !options().itemCounter.arrowCounter) {
            return;
        }

        ItemStack used = localPlayer.getItemInHand(hand);
        if (!(used.getItem() instanceof CrossbowItem) || !CrossbowItem.isCharged(used)) {
            return;
        }

        ChargedProjectiles chargedProjectiles = player.getItemInHand(hand).get(DataComponents.CHARGED_PROJECTILES);
        ItemHudTracker.setStack(chargedProjectiles.isEmpty() ? new ItemStack(Items.ARROW) : chargedProjectiles.itemCopies().get(0), true);
    }

    /**
     * Filters a container when shift + left clicking on it.
     */
    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void filterContainer(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!shouldCancelForContainerFilter(minecraft, pos)) {
            return;
        }
        BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);

        // Never allow breaking while attempting to filter a tracked container
        cir.cancel();
        cir.setReturnValue(false);

        // Avoid retriggering toggle while the mouse is held down.
        if (TRACKED_CONTAINER_COOLDOWN > 0) {
            return;
        }

        boolean tracked = ContainerHelper.toggleTracked(minecraft.level, pos);
        boolean isEnderChest = blockEntity instanceof EnderChestBlockEntity;
        boolean isShulkerBox = blockEntity instanceof ShulkerBoxBlockEntity;
        Component container = blockEntity instanceof ShulkerBoxBlockEntity ? Component.translatable("qualityofqueso.gui.shulker_box").withStyle(ChatFormatting.LIGHT_PURPLE)
                : blockEntity instanceof BarrelBlockEntity ? Component.translatable("qualityofqueso.gui.barrel").withStyle(ChatFormatting.GOLD)
                : isEnderChest ? Component.translatable("qualityofqueso.gui.ender_chest").withStyle(ChatFormatting.AQUA)
                  : Component.translatable("qualityofqueso.gui.chest").withStyle(ChatFormatting.GOLD);
        Component filteredContainer = Component.translatable("qualityofqueso.gui.filtered_container").withColor(ModTexts.ITEM_COLOR);
        Component filtered = Component.translatable("qualityofqueso.gui.filtered").withColor(ModTexts.ITEM_COLOR);
        String message = tracked ? "qualityofqueso.gui.save_filtered_container" : "qualityofqueso.gui.remove_filtered_container";
        if (isEnderChest) {
            if (tracked) {
                message = "qualityofqueso.gui.save_filtered_ender_chest";
            } else {
                message = "qualityofqueso.gui.remove_filtered_ender_chest";
            }
        } else if (isShulkerBox) {
            if (tracked) {
                message = "qualityofqueso.gui.save_filtered_shulker_box";
            } else {
                message = "qualityofqueso.gui.remove_filtered_shulker_box";
            }
        }
        minecraft.player.sendSystemMessage(Component.translatable(
                message,
                container.copy().withStyle(ChatFormatting.BOLD),
                isEnderChest
                        ? filteredContainer
                        : isShulkerBox
                          ? filtered : pos.getX(),
                pos.getY(),
                pos.getZ(),
                filteredContainer
        ));
        playButtonSound(minecraft, false);
        TRACKED_CONTAINER_COOLDOWN = DEFAULT_TRACKED_CONTAINER_COOLDOWN;
    }

    /**
     * Keeps held-click and creative instant-break from destroying filtered containers.
     */
    @Inject(method = "continueDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void preventFilterBreakOnContinue(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (!shouldCancelForContainerFilter(Minecraft.getInstance(), pos)) {
            return;
        }
        cir.cancel();
        cir.setReturnValue(false);
    }

    /**
     * Final creative break guard in case direct destroy calls bypass start/continue flow.
     */
    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void preventFilterBreakOnDestroy(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!shouldCancelForContainerFilter(Minecraft.getInstance(), pos)) {
            return;
        }
        cir.cancel();
        cir.setReturnValue(false);
    }

    /**
     * Remembers container use so the next opening container screen can be identified.
     */
    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void rememberContainer(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(hitResult.getBlockPos());
        if (isValidBlockEntity(blockEntity)) {
            ContainerHelper.rememberOpened(player.level(), hitResult.getBlockPos());
        } else {
            ContainerHelper.clearPendingOpened();
        }
    }

    /**
     * @return if injection point should be canceled out after filtering.
     */
    @Unique
    private static boolean shouldCancelForContainerFilter(Minecraft minecraft, BlockPos pos) {
        if (!modEnabled(minecraft) || !options().management.containerFiltering || minecraft.player == null || minecraft.level == null
                || !minecraft.player.isShiftKeyDown() || isHoldingItem(minecraft.player, DataComponents.TOOL)) {
            return false;
        }
        return isValidBlockEntity(minecraft.level.getBlockEntity(pos));
    }
}