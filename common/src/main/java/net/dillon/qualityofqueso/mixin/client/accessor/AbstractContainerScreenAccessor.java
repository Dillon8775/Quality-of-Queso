package net.dillon.qualityofqueso.mixin.client.accessor;

import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Invoker("getHoveredSlot")
    SlotWidget invokeGetHoveredSlot();

    @Accessor("imageWidth")
    int getImageWidth();

    @Accessor("leftPos")
    int getLeftPos();

    @Accessor("topPos")
    int getTopPos();

    @Accessor("titleLabelY")
    int getTitleLabelY();

    @Invoker("slotClicked")
    void performClickSlot(SlotWidget slotWidget, int slotId, int buttonNum, ContainerInput containerInput);
}