package net.dillon.qualityofqueso.mixin.client.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("hoveredSlot")
    Slot getHoveredSlot();

    @Accessor("imageWidth")
    int getImageWidth();

    @Accessor("topPos")
    int getTopPos();

    @Accessor("titleLabelY")
    int getTitleLabelY();

    @Invoker("slotClicked")
    void performClickSlot(Slot slot, int slotId, int buttonNum, ContainerInput containerInput);
}