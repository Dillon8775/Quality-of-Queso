package net.dillon.qualityofqueso.screen.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

import java.util.List;

import static net.dillon.qualityofqueso.util.AccessorUtil.getImageWidth;
import static net.dillon.qualityofqueso.util.ButtonUtil.*;
import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;
import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A layout, either horizontally or vertically, for all buttons to be rendered in.
 */
public class WidgetLayout extends AbstractWidget {
    private static final int WIDTH = 52;
    private static final int HEIGHT = 68;
    private final List<AbstractWidget> buttons;
    private final AbstractContainerScreen<?> screen;
    private final Container container;
    private final int topPos;
    private final int titleLabelY;

    public WidgetLayout(AbstractContainerScreen<?> screen, Container container, int topPos, int titleLabelY, List<AbstractWidget> buttons) {
        super(0, 0, 0, 0, Component.empty());
        this.screen = screen;
        this.container = container;
        this.topPos = topPos;
        this.titleLabelY = titleLabelY;
        this.buttons = buttons;
    }

    /**
     * Renders all buttons in an orderly manner.
     */
    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.setX(this.getPanelX());
        this.setY(this.getPanelY());
        this.setWidth(this.getPanelWidth());
        this.setHeight(this.getPanelHeight());

        int containerY = this.topPos + this.titleLabelY + 2 * (this.container == null ? 0 : this.container.getContainerSize()) + 12;
        boolean inventoryScreen = isInventoryScreen(this.screen);

        if (RENDERED_BUTTONS > 0 && !options().management.buttonLayout.horizontal()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/button/base/layout/layout_" + this.getLayoutNumber() + ".png"),
                    this.getX(),
                    this.getPanelY(),
                    0.0F, 0.0F, WIDTH, HEIGHT, WIDTH, HEIGHT);
        }

        int buttons = 0;
        int finalX = 8;

        int x = finalX + getRecipeBookModifier(this.screen);
        int y = inventoryScreen ? -33 : -34;

        int addition = 12;
        for (AbstractWidget widget : this.buttons) {
            if (widget == null) {
                continue;
            }

            if (options().management.buttonLayout.horizontal()) {
                widget.setX(getManagementButtonX(this.screen, getImageWidth(this.screen), this.screen.width, buttons));
                widget.setY(getManagementButtonY(this.screen, this.container, this.topPos, this.titleLabelY));
            } else {
                widget.setX(this.screen.width / 2 + getImageWidth(this.screen) / 2 + x);
                widget.setY(y + containerY);
                if (buttons % 2 == 0) {
                    x += addition;
                } else {
                    x = finalX;
                    y += addition;
                }
            }
            widget.extractRenderState(graphics, mouseX, mouseY, a);
            buttons++;
        }

        RENDERED_BUTTONS = buttons;
    }

    /**
     * Checks if the mouse is over top of the panel.

     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!this.visible || options().management.buttonLayout.horizontal() || RENDERED_BUTTONS <= 0) {
            return false;
        }

        return mouseX >= this.getX() && mouseX < this.getX() + this.width && mouseY >= this.getY() && mouseY < this.getY() + this.height;
    }

    /**
     * @return the layout number to display.
     */
    private int getLayoutNumber() {
        if (RENDERED_BUTTONS > 8) {
            return 10;
        } else if (RENDERED_BUTTONS > 6) {
            return 8;
        } else if (RENDERED_BUTTONS > 4) {
            return 6;
        } else if (RENDERED_BUTTONS > 2) {
            return 4;
        } else if (RENDERED_BUTTONS > 1) {
            return 2;
        }
        return 1;
    }

    /**
     * @return the panel X for the box.
     */
    private int getPanelX() {
        return this.screen.width / 2 + 92 + getRecipeBookModifier(this.screen);
    }

    /**
     * @return the container Y for the box.
     */
    private int getContainerY() {
        return this.topPos + this.titleLabelY + 2 * (this.container == null ? 0 : this.container.getContainerSize()) + 12;
    }

    /**
     * @return the panel Y for the box.
     */
    private int getPanelY() {
        boolean inventoryScreen = isInventoryScreen(this.screen);
        return this.getContainerY() - (inventoryScreen ? 37 : 38);
    }

    /**
     * @return the panel width in which clicking should be allowed.
     */
    private int getPanelWidth() {
        if (this.getLayoutNumber() == 1) {
            return 18;
        }
        return 30;
    }

    /**
     * @return the panel height in which clicking should be allowed.
     */
    private int getPanelHeight() {
        return switch (this.getLayoutNumber()) {
            case 1, 2 -> 19;
            case 4 -> 31;
            case 6 -> 44;
            case 8 -> 55;
            default -> 67;
        };
    }

    /**
     * Cancels out closing the screen if the widget layout was pressed.
     */
    public static void hasClickedOnBox(double mx, double my, WidgetLayout widgetLayout, CallbackInfoReturnable<Boolean> cir) throws CancellationException {
        if (widgetLayout != null && widgetLayout.isMouseOver(mx, my)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Initializes and returns a new WidgetLayout.
     */
    public static WidgetLayout initializeLayout(AbstractContainerScreen<?> screen, EditBox searchField, Container container, int topPos, int titleLabelY, List<AbstractWidget> widgets) {
        return new WidgetLayout(screen, container, topPos, titleLabelY, widgets);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}