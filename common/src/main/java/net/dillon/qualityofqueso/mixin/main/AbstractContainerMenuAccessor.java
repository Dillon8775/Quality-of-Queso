package net.dillon.qualityofqueso.mixin.main;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Dill(DillType.COMMON)
@Mixin(AbstractContainerMenu.class)
public interface AbstractContainerMenuAccessor {
    @Invoker("moveItemStackTo")
    boolean invokeMoveItemStackTo(ItemStack itemStack, int startSlot, int endSlot, boolean backwards);
}