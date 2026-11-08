package net.dillon.qualityofqueso.widget;

import net.dillon.dillonlib.widespread.DillonsTexts;
import net.dillon.qualityofqueso.screen.VisualTimeScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawVisualTimeClock;

/**
 * A button to open the visual time gui.
 */
public class OpenVisualTimeGuiButton extends AbstractButton {

    public OpenVisualTimeGuiButton() {
        super(0, 0, 20, 20, DillonsTexts.BLANK);
        this.setTooltip(Tooltip.create(Component.translatable("qualityofqueso.visual_time")));
        this.visible = true;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        openScreen(new VisualTimeScreen(getScreen()));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.extractDefaultSprite(graphics);
        drawVisualTimeClock(
                graphics,
                this.getX() + 2,
                this.getY() + 2,
                16,
                true
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}