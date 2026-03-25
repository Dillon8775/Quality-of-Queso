package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.screen.gui.button.SearchTransportablesButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;

import java.util.List;

import static net.dillon.qualityofqueso.util.AccessorUtil.getImageWidth;
import static net.dillon.qualityofqueso.util.ButtonUtil.*;
import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;
import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A layout for all buttons to be rendered in.
 */
public class ButtonLayout extends AbstractWidget {
    private final List<AbstractWidget> buttons;
    private final AbstractContainerScreen<?> screen;
    private final EditBox searchField;
    private final Container container;
    private final int topPos;
    private final int titleLabelY;

    public ButtonLayout(EditBox searchField, AbstractContainerScreen<?> screen, Container container, int topPos, int titleLabelY, List<AbstractWidget> buttons, int x, int y) {
        super(x, y, 100, 200, Component.empty());
        this.searchField = searchField;
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
        int containerY = this.topPos + this.titleLabelY + 2 * (this.container == null ? 0 : this.container.getContainerSize()) + 12;
        boolean inventoryScreen = isInventoryScreen(this.screen);

        if (RENDERED_BUTTONS > 0 && options().management.verticalLayout) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/button/base/layout/layout_" + this.getLayoutNumber() + ".png"),
                    this.screen.width / 2 + 92 + getRecipeBookModifier(this.screen),
                    containerY - (inventoryScreen ? 37 : 38),
                    0.0F, 0.0F, 52, 68, 52, 68);
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

            if (!options().management.verticalLayout) {
                if (this.searchField != null && widget instanceof SearchTransportablesButton) {
                    widget.setX(this.searchField.getX() - (inventoryScreen ? -2 : 12));
                    widget.setY(this.searchField.getY() + (inventoryScreen ? 14 : 1));
                } else {
                    widget.setX(getManagementButtonX(this.screen, getImageWidth(this.screen), this.screen.width, buttons));
                    widget.setY(getManagementButtonY(this.screen, this.container, this.topPos, this.titleLabelY));
                }
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

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}