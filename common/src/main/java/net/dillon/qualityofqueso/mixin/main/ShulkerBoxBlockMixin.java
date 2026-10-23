package net.dillon.qualityofqueso.mixin.main;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.dillon.qualityofqueso.container.ShulkerBlockEntityHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.dillon.qualityofqueso.helper.ModConstants.*;

/**
 * Copies shulker state from block entity data into dropped shulker item custom data.
 */
@Dill(DillType.COMMON)
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

        if (!(shulkerBoxBlockEntity instanceof ShulkerBlockEntityHolder shulkerBlockEntityHolder)) {
            return;
        }

        List<String> filterItems = shulkerBlockEntityHolder.getFilterItems() == null
                ? new ArrayList<>()
                : shulkerBlockEntityHolder.getFilterItems();
        int[] lockedSlots = (shulkerBlockEntityHolder.getLockedSlots() == null ? new ArrayList<Integer>() : shulkerBlockEntityHolder.getLockedSlots())
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
            if (!drop.is(ItemTags.SHULKER_BOXES)) {
                continue;
            }

            CustomData.update(DataComponents.CUSTOM_DATA, drop, tag -> {
                tag.putString(SORTING_MODE, shulkerBlockEntityHolder.getSortingMode());
                tag.putString(FILTERING_MODE, shulkerBlockEntityHolder.getFilteringMode());
                tag.putBoolean(TAG_FILTERED, shulkerBlockEntityHolder.isTagFiltered());
                tag.putBoolean(LOCKED, shulkerBlockEntityHolder.isLocked());
                tag.putString(FILTER_ITEMS, filterItems.stream()
                        .filter(s -> s != null && !s.isBlank())
                        .distinct()
                        .collect(Collectors.joining(",")));
                tag.putIntArray(LOCKED_SLOTS, lockedSlots);
            });
        }
    }
}
