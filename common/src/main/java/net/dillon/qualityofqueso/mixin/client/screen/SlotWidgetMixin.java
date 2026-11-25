package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.screen.TooltipEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

@Mixin(SlotWidget.class)
public abstract class SlotWidgetMixin extends AbstractWidget implements QuesoScreenHolder {

    public SlotWidgetMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public AbstractContainerScreen<?> screen() {
        if (getScreen() instanceof AbstractContainerScreen<?> abstractContainerScreen) {
            return abstractContainerScreen;
        }

        return null;
    }

    /**
     * Renders mod tooltips with the help of the {@link TooltipEvents} record.
     */
    @Inject(method = "extractTooltipForNextRenderPass", at = @At("HEAD"), cancellable = true)
    private void modifyExistingAndNewTooltips(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!modEnabled() || screen() == null) {
            return;
        }

        events().tooltips().handle(graphics, ci);
    }
}