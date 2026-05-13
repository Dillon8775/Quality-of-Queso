package net.dillon.qualityofqueso.mixin.main;

import net.dillon.qualityofqueso.util.ShulkerStateHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.dillon.qualityofqueso.helper.ModHelper.shulkerBoxes;
import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Copies shulker state from block entity data into dropped shulker item custom data.
 */
@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {

    /**
     * Writes shulker state into dropped shulker item stacks.
     */
    @Inject(method = "getDrops", at = @At("RETURN"))
    private void perserveIdentityInDroppedStack(BlockState state, LootParams.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity)) {
            return;
        }
        if (!(shulkerBoxBlockEntity instanceof ShulkerStateHolder stateHolder)) {
            return;
        }

        List<String> filterItems = stateHolder.getFilterItems() == null
                ? new ArrayList<>()
                : stateHolder.getFilterItems();
        int[] lockedSlots = (stateHolder.getLockedSlots() == null ? new ArrayList<Integer>() : stateHolder.getLockedSlots())
                .stream()
                .distinct()
                .sorted()
                .mapToInt(Integer::intValue)
                .toArray();

        List<ItemStack> drops = cir.getReturnValue();
        if (drops == null || drops.isEmpty()) {
            return;
        }

        for (ItemStack drop : drops) {
            if (!isStackShulker(drop)) {
                continue;
            }
            CustomData.update(DataComponents.CUSTOM_DATA, drop, tag -> {
                tag.putBoolean(FILTERED, stateHolder.isFiltered());
                tag.putBoolean(TAG_FILTERED, stateHolder.isTagFiltered());
                tag.putString(FILTER_ITEMS, filterItems.stream()
                        .filter(s -> s != null && !s.isBlank())
                        .distinct()
                        .collect(Collectors.joining(",")));
                tag.putIntArray(LOCKED_SLOTS, lockedSlots);
                tag.putString(SORTING_MODE, stateHolder.getSortingMode());
            });
        }
    }

    /**
     * @return if the itemstack is a shulker.
     */
    @Unique
    @Deprecated
    private boolean isStackShulker(ItemStack stack) {
        for (Item item : shulkerBoxes) {
            if (stack.is(item)) {
                return true;
            }
        }
        return false;
    }
}
