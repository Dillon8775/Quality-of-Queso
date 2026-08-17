package net.dillon.qualityofqueso.mixin.main;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.dillon.qualityofqueso.util.ShulkerStateHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.dillon.qualityofqueso.helper.ModConstants.*;

/**
 * Restores shulker state from shulker item custom data into the placed block entity.
 */
@Dill(DillType.COMMON)
@Mixin(BlockItem.class)
public class BlockItemMixin {

    /**
     * Applies shulker state from the placing stack to the placed shulker block entity.
     */
    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.BEFORE))
    private void restoreShulkerIdentityOnPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

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

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            filtered = customData.copyTag().getBooleanOr(FILTERED, false);
            tagMode = customData.copyTag().getBooleanOr(TAG_FILTERED, false);

            String rawItems = customData.copyTag().getStringOr(FILTER_ITEMS, "");
            if (!rawItems.isBlank()) {
                filterItems = Arrays.stream(rawItems.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .distinct()
                        .collect(Collectors.toCollection(ArrayList::new));
            }

            int[] rawLocked = customData.copyTag().getIntArray(LOCKED_SLOTS).orElse(new int[0]);
            lockedSlots = Arrays.stream(rawLocked)
                    .boxed()
                    .distinct()
                    .sorted()
                    .collect(Collectors.toCollection(ArrayList::new));
            sortingMode = customData.copyTag().getStringOr(SORTING_MODE, "");
        }

        stateHolder.setFiltered(filtered);
        stateHolder.setTagFiltered(tagMode);
        stateHolder.setFilterItems(filterItems);
        stateHolder.setLockedSlots(lockedSlots);
        stateHolder.setSortingMode(sortingMode);
        shulkerBoxBlockEntity.setChanged();
    }
}