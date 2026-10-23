package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.container.ContainerSyncer;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.GuiHelper.blitLockOverlay;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ManagementHelper.buttonHovered;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isContainerScreen;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Sorts items in a container or inventory.
 */
public class SortButton extends QuesoButton {

    public SortButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String resourceLocation, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, resourceLocation, buttonName, false, onPress, canBeActive);
    }

    /**
     * @return if the lock overlay can be rendered over {@code this} button, and it's hovered.
     */
    public static boolean canRenderLockOverlayAndHovered(QuesoButton sort) {
        return canRenderLockOverlay(sort) && buttonHovered(sort);
    }

    /**
     * @return if the lock overlay can be rendered over {@code this} button.
     */
    public static boolean canRenderLockOverlay(QuesoButton sort) {
        if (!(getScreen() instanceof QuesoScreenHolder quesoScreen)) {
            return false;
        }

        ManagementEvents managementEvents = new ManagementEvents(quesoScreen);
        return !sort.active && isContainerScreen() && ContainerSyncer.isActiveContainerLocked() && managementEvents.sortingEvents().shouldSortButtonBeActive();
    }

    @Override
    protected Component getTooltipToRender() {
        String name = "qualityofqueso.gui." + this.buttonName + "_button";
        if (isContainerScreen() && ContainerSyncer.isActiveContainerLocked()) {
            return Component.translatable(name + ".locked");
        }

        Component sortTooltip = Component.translatable(name, client().sorting().currentSortingMode.getTooltip());

        if (isContainerScreen()) {
            sortTooltip = sortTooltip.copy().append(Component.translatable("qualityofqueso.gui.sort_button.lock"));
        }

        return sortTooltip;
    }

    @Override
    protected void renderBaseButtonTexture(String id, AbstractWidget buttonReference, GuiGraphicsExtractor graphics) {
        this.renderBaseTexture(graphics);
        ButtonHelper.drawButtonTexture(graphics, id + (!this.canBeActive.get() ? "" : client().sorting().currentSortingMode.getAppended()), this);
        this.renderHoveredTexture(graphics);

        if (canRenderLockOverlay(this)) {
            blitLockOverlay(
                    graphics,
                    this.getX() - 3,
                    this.getY() - 5
            );
        }
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractContents(graphics, mouseX, mouseY, deltaTicks);
        if (canRenderLockOverlayAndHovered(this) && buttonHovered(this)) {
            drawTooltip(this.getTooltipToRender(), graphics, this.font, mouseX, mouseY);
        }
    }
}