package net.dillon.qualityofqueso.widget.management;

import net.dillon.qualityofqueso.container.ContainerSyncer;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.GuiHelper.blitLockOverlay;
import static net.dillon.qualityofqueso.helper.ManagementHelper.buttonHovered;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isContainerScreen;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * A management button that sorts all items in a container/player's inventory.
 */
public class SortButton extends ManagementButton {

    public SortButton(ManagementParams params) {
        super(
                params,
                "sort",
                NO_APPENDED_TOOLTIP,
                false,
                b -> params.managementEvents().sortingEvents().trySort()
        );
    }

    /**
     * @return if the lock overlay can be rendered over {@code this} button, and it's hovered.
     */
    public static boolean canRenderLockOverlayAndHovered(ManagementButton sort) {
        return canRenderLockOverlay(sort) && buttonHovered(sort);
    }

    /**
     * @return if the lock overlay can be rendered over {@code this} button.
     */
    public static boolean canRenderLockOverlay(ManagementButton sort) {
        if (!(getScreen() instanceof QuesoScreenHolder quesoScreen)) {
            return false;
        }

        ManagementEvents managementEvents = new ManagementEvents(quesoScreen);
        return !sort.active && isContainerScreen() && ContainerSyncer.isActiveContainerLocked() && managementEvents.sortingEvents().shouldSortButtonBeActive();
    }

    @Override
    protected boolean canBeActive() {
        return managementEvents.sortingEvents().canSort();
    }

    @Override
    protected Component getDefaultTooltip() {
        String name = super.getDefaultTooltip().getString();

        if (isContainerScreen() && ContainerSyncer.isActiveContainerLocked()) {
            return Component.translatable(name + ".locked");
        }

        Component sortingTooltip = Component.translatable(name, client().sorting().currentSortingMode.getTooltip());

        if (isContainerScreen()) {
            sortingTooltip = sortingTooltip.copy().append(Component.translatable("qualityofqueso.gui.sort_button.lock"));
        }

        return sortingTooltip;
    }

    @Override
    protected String getDefaultAppendedTexture() {
        return client().sorting().currentSortingMode.getAppended();
    }

    @Override
    protected void renderButton(GuiGraphicsExtractor graphics) {
        this.extractButton(graphics);

        if (canRenderLockOverlay(this)) {
            blitLockOverlay(
                    graphics,
                    this.getX() - 3,
                    this.getY() - 5
            );
        }
    }
}