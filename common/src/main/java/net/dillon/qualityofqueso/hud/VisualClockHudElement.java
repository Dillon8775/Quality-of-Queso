package net.dillon.qualityofqueso.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import static net.dillon.dillonlib.task.ClientTasks.getGuiHeight;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawVisualTimeClock;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Holds the {@code Visual Clock} to extract.
 */
public class VisualClockHudElement extends ModHudElement {

    /**
     * Extracts the visual clock.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics) {
        if (!client().visualTime().overrideClientTime || !client().visualTime().displayVisualClock) {
            return;
        }

        int clockX = graphics.guiWidth() - 23 + client().visualTime().visualClockPosition[0];
        int clockY = getGuiHeight(graphics) - 3 + client().visualTime().visualClockPosition[1];
        drawVisualTimeClock(
                graphics,
                clockX,
                clockY,
                18,
                false
        );
    }
}