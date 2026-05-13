package net.dillon.qualityofqueso.mixin.client.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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

    @Accessor("leftPos")
    int getLeftPos();

    @Accessor("titleLabelY")
    int getTitleLabelY();

    @Invoker("slotClicked")
    void performClickSlot(Slot slot, int slotId, int buttonNum, ClickType containerInput);

    @Invoker("renderFloatingItem")
    void invokeRenderFloatingItem(net.minecraft.client.gui.GuiGraphics graphics, ItemStack stack, int x, int y, String amountText);
}