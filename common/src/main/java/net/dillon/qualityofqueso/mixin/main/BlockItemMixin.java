package net.dillon.qualityofqueso.mixin.main;

import net.dillon.qualityofqueso.util.ShulkerStateHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Restores shulker state from shulker item custom data into the placed block entity.
 */
@Mixin(BlockItem.class)
public class BlockItemMixin {

    /**
     * Applies shulker state from the placing stack to the placed shulker block entity.
     */
    @Inject(method = "updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("TAIL"))
    private void restoreShulkerIdentityOnPlace(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        BlockItem self = (BlockItem) (Object) this;
        if (!(self.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity)) {
            return;
        }
        if (!(shulkerBoxBlockEntity instanceof ShulkerStateHolder stateHolder)) {
            return;
        }

        boolean filtered = false;
        boolean tagMode = false;
        List<String> filterItems = new ArrayList<>();
        List<Integer> lockedSlots = new ArrayList<>();
        String sortingMode = "";

        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        if (tag != null) {
            filtered = tag.getBoolean(FILTERED);
            tagMode = tag.getBoolean(TAG_FILTERED);

            String rawItems = tag.getString(FILTER_ITEMS);
            if (!rawItems.isBlank()) {
                filterItems = Arrays.stream(rawItems.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .distinct()
                        .collect(Collectors.toCollection(ArrayList::new));
            }

            int[] rawLocked = tag.getIntArray(LOCKED_SLOTS);
            lockedSlots = Arrays.stream(rawLocked)
                    .boxed()
                    .distinct()
                    .sorted()
                    .collect(Collectors.toCollection(ArrayList::new));
            sortingMode = tag.getString(SORTING_MODE);
        }

        stateHolder.setFiltered(filtered);
        stateHolder.setTagFiltered(tagMode);
        stateHolder.setFilterItems(filterItems);
        stateHolder.setLockedSlots(lockedSlots);
        stateHolder.setSortingMode(sortingMode);
        shulkerBoxBlockEntity.setChanged();
    }
}