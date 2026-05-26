package net.dillon.qualityofqueso.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

import java.util.List;

import static net.dillon.qualityofqueso.helper.ButtonHelper.getWidgetPath;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.getImageWidth;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;
import static net.dillon.qualityofqueso.util.ModConstants.RENDERED_BUTTONS;

/**
 * A layout, either horizontally or vertically, for all buttons to be rendered in.
 */
public class WidgetLayout extends AbstractWidget {
    private static final int LAYOUT_TEXTURE_WIDTH = 64;
    private static final int LAYOUT_TEXTURE_HEIGHT = 84;
    private static final int LAYOUT_BASE_WIDTH = 37;
    private static final int LAYOUT_BASE_HEIGHT = 82;
    private static final int LAYOUT_CAP_HEIGHT = 8;
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
     * Initializes and returns a new WidgetLayout.
     */
    public static WidgetLayout initializeLayout(AbstractContainerScreen<?> screen, Container container, int topPos, int titleLabelY, List<AbstractWidget> widgets) {
        return new WidgetLayout(screen, container, topPos, titleLabelY, widgets);
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
     * @return if the player has more than 2 effects, determining if the buttons should be rendered in a different place.
     */
    public boolean hasTooManyEffects(LocalPlayer player) {
        return isInventoryScreen(this.screen) && player != null && player.getActiveEffects().size() > 2;
    }

    /**
     * @return the layout number to display.
     */
    private int getLayoutNumber() {
        if (RENDERED_BUTTONS > 10) {
            return 12;
        } else if (RENDERED_BUTTONS > 8) {
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
     * @return the layout rows.
     */
    private int getLayoutRows() {
        int layoutNumber = this.getLayoutNumber();
        if (layoutNumber <= 2) {
            return 1;
        }
        return layoutNumber / 2;
    }

    /**
     * @return the panel X for the box.
     */
    private int getPanelX(LocalPlayer player) {
        int x = this.screen.width / 2 + (this.hasTooManyEffects(player) ? 44 : 92) + getRecipeBookModifier(this.screen);
        if (isMerchantScreen(this.screen)) {
            x += 52;
        }
        return x;
    }

    /**
     * @return the panel Y for the box.
     */
    private int getPanelY(LocalPlayer player) {
        boolean inventoryScreen = isInventoryScreen(this.screen);
        int y = this.getContainerY() - (inventoryScreen ? (this.hasTooManyEffects(player) ? -68 : 37) : (isOtherValidScreen(this.screen) ? -8 : 38));
        if (isMerchantScreen(this.screen)) {
            y += 56;
        } else if (isCraftingScreen(this.screen)) {
            y += 54;
        }
        return y;
    }

    /**
     * @return the container Y for the box.
     */
    private int getContainerY() {
        return this.topPos + this.titleLabelY + 2 * (this.container == null ? 0 : this.container.getContainerSize()) + (
                this.screen instanceof InventoryScreen || isOtherValidScreen(this.screen) ? 12
                        : 34);
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
        int height;
        switch (this.getLayoutNumber()) {
            case 1, 2 -> height = 16;
            case 4 -> height = 25;
            case 6 -> height = 34;
            case 8 -> height = 43;
            case 10 -> height = 52;
            default -> height = 61;
        };
        return height + this.getLayoutRows() * 3;
    }

    /**
     * Checks if the mouse is over top of the panel.
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!this.visible || (!isDropperDispenserOrHopperScreen(this.screen) && clientOptionsInstance().getManagementOptions().layout.horizontal()) || RENDERED_BUTTONS <= 0) {
            return false;
        }

        return mouseX >= this.getX() && mouseX < this.getX() + this.width && mouseY >= this.getY() && mouseY < this.getY() + this.height;
    }

    /**
     * Renders all buttons in an orderly manner.
     */
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        LocalPlayer player = Minecraft.getInstance().player;

        this.setX(this.getPanelX(player));
        this.setY(this.getPanelY(player));
        this.setWidth(this.getPanelWidth());
        this.setHeight(this.getPanelHeight());

        boolean inventoryScreen = isInventoryScreen(this.screen);
        boolean craftingScreen = isCraftingScreen(this.screen);
        boolean merchantScreen = isMerchantScreen(this.screen);

        if (RENDERED_BUTTONS > 0 && (!clientOptionsInstance().getManagementOptions().layout.horizontal() || isDropperDispenserOrHopperScreen(this.screen))) {
            this.extractPanel(graphics, player);
        }

        int buttons = 0;
        int finalX = 8;

        int recipeBookModifier = getRecipeBookModifier(this.screen);
        int x = finalX + recipeBookModifier;
        int inventoryBaseY = -33;
        int y = inventoryScreen ? inventoryBaseY : -34;

        int newX = finalX;
        boolean alreadyAddedModifier = false;
        if (this.hasTooManyEffects(player)) {
            newX = -40 + recipeBookModifier;
            alreadyAddedModifier = true;
            x = newX;
            y = 72;
        } else if (isOtherValidScreen(this.screen)) {
            y = 12;
        }

        int addition = 12;
        int originalX = 0;
        int originalY = 0;
        for (AbstractWidget widget : this.buttons) {
            if (widget == null) {
                continue;
            }

            boolean bulkCraft = clientOptionsInstance().getManagementOptions().layout.horizontal() && widget instanceof BulkCraftButton;
            if (clientOptionsInstance().getManagementOptions().layout.horizontal() && !isDropperDispenserOrHopperScreen(this.screen)) {
                widget.setX(getManagementButtonX(this.screen, getImageWidth(this.screen), this.screen.width, buttons));
                widget.setY(getManagementButtonY(this.screen, this.container, this.topPos, this.titleLabelY));
                if (inventoryScreen) {
                    if (buttons == 0) {
                        originalX = widget.getX();
                        originalY = widget.getY();
                    }

                    int column = buttons % 3;
                    int row = buttons / 3;

                    if (bulkCraft) {
                        widget.setX(this.screen.width / 2 + getImageWidth(this.screen) / 2 + recipeBookModifier - 41);
                        widget.setY(inventoryBaseY + this.getContainerY() - 24);
                    } else {
                        widget.setX(originalX - (column * addition));
                        widget.setY(originalY - (row * addition));
                    }
                } else if (merchantScreen) {
                    widget.setX(widget.getX() + 12);
                    widget.setY(widget.getY() + 22);
                } else if (craftingScreen) {
                    widget.setX(widget.getX() - 4);
                    widget.setY(widget.getY() + 20);
                }
            } else {
                widget.setX(this.screen.width / 2 + getImageWidth(this.screen) / 2 + x);
                widget.setY(y + this.getContainerY());

                if (merchantScreen) {
                    widget.setX(widget.getX() + 2);
                    widget.setY(widget.getY() + 56);
                } else if (craftingScreen) {
                    widget.setY(widget.getY() + 54);
                }

                if (buttons % 2 == 0) {
                    x += addition;
                } else {
                    x = newX + (!alreadyAddedModifier ? recipeBookModifier : 0);
                    y += addition;
                }
            }
            widget.render(graphics, mouseX, mouseY, a);
            if (!bulkCraft) {
                buttons++;
            }
        }

        RENDERED_BUTTONS = buttons;
    }

    /**
     * Extracts the widget panel for the vertical button layout.
     */
    private void extractPanel(GuiGraphics graphics, LocalPlayer player) {
        int panelX = this.getX();
        int panelY = this.getPanelY(player);
        float xScale = this.getPanelWidth() / (float) LAYOUT_BASE_WIDTH;
        int panelHeight = this.getPanelHeight();
        var layoutTexture = ofQoQ("textures/gui/sprites/button/base/" + getWidgetPath(true) + "layout.png");

        if (this.getLayoutNumber() == 1) {
            int panelWidth = this.getPanelWidth();
            int borderWidth = 3;
            int borderHeight = 3;
            int centerSourceWidth = Math.max(1, LAYOUT_BASE_WIDTH - borderWidth * 2);
            int centerTargetWidth = Math.max(0, panelWidth - borderWidth * 2);
            int centerSourceHeight = Math.max(1, LAYOUT_BASE_HEIGHT - borderHeight * 2);
            int centerTargetHeight = Math.max(0, panelHeight - borderHeight * 2);

            graphics.pose().pushPose();
            graphics.pose().translate(panelX, panelY, 0.0F);
            graphics.blit(layoutTexture, 0, 0, 0.0F, 0.0F,
                    borderWidth, borderHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
            graphics.pose().popPose();

            if (centerTargetWidth > 0) {
                float centerXScale = centerTargetWidth / (float) centerSourceWidth;
                graphics.pose().pushPose();
                graphics.pose().translate(panelX + borderWidth, panelY, 0.0F);
                graphics.pose().scale(centerXScale, 1.0F, 1.0F);
                graphics.blit(layoutTexture, 0, 0, borderWidth, 0.0F,
                        centerSourceWidth, borderHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
                graphics.pose().popPose();
            }

            graphics.pose().pushPose();
            graphics.pose().translate(panelX + panelWidth - borderWidth, panelY, 0.0F);
            graphics.blit(layoutTexture, 0, 0, LAYOUT_BASE_WIDTH - borderWidth, 0.0F,
                    borderWidth, borderHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
            graphics.pose().popPose();

            if (centerTargetHeight > 0) {
                float centerYScale = centerTargetHeight / (float) centerSourceHeight;

                graphics.pose().pushPose();
                graphics.pose().translate(panelX, panelY + borderHeight, 0.0F);
                graphics.pose().scale(1.0F, centerYScale, 1.0F);
                graphics.blit(layoutTexture, 0, 0, 0.0F, borderHeight,
                        borderWidth, centerSourceHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
                graphics.pose().popPose();

                if (centerTargetWidth > 0) {
                    float centerXScale = centerTargetWidth / (float) centerSourceWidth;
                    graphics.pose().pushPose();
                    graphics.pose().translate(panelX + borderWidth, panelY + borderHeight, 0.0F);
                    graphics.pose().scale(centerXScale, centerYScale, 1.0F);
                    graphics.blit(layoutTexture, 0, 0, borderWidth, borderHeight,
                            centerSourceWidth, centerSourceHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
                    graphics.pose().popPose();
                }

                graphics.pose().pushPose();
                graphics.pose().translate(panelX + panelWidth - borderWidth, panelY + borderHeight, 0.0F);
                graphics.pose().scale(1.0F, centerYScale, 1.0F);
                graphics.blit(layoutTexture, 0, 0, LAYOUT_BASE_WIDTH - borderWidth, borderHeight,
                        borderWidth, centerSourceHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
                graphics.pose().popPose();
            }

            graphics.pose().pushPose();
            graphics.pose().translate(panelX, panelY + panelHeight - borderHeight, 0.0F);
            graphics.blit(layoutTexture, 0, 0, 0.0F, LAYOUT_BASE_HEIGHT - borderHeight,
                    borderWidth, borderHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
            graphics.pose().popPose();

            if (centerTargetWidth > 0) {
                float centerXScale = centerTargetWidth / (float) centerSourceWidth;
                graphics.pose().pushPose();
                graphics.pose().translate(panelX + borderWidth, panelY + panelHeight - borderHeight, 0.0F);
                graphics.pose().scale(centerXScale, 1.0F, 1.0F);
                graphics.blit(layoutTexture, 0, 0, borderWidth, LAYOUT_BASE_HEIGHT - borderHeight,
                        centerSourceWidth, borderHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
                graphics.pose().popPose();
            }

            graphics.pose().pushPose();
            graphics.pose().translate(panelX + panelWidth - borderWidth, panelY + panelHeight - borderHeight, 0.0F);
            graphics.blit(layoutTexture, 0, 0, LAYOUT_BASE_WIDTH - borderWidth, LAYOUT_BASE_HEIGHT - borderHeight,
                    borderWidth, borderHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
            graphics.pose().popPose();
        } else {
            int topCapHeight = Math.min(LAYOUT_CAP_HEIGHT, panelHeight / 2);
            int bottomCapHeight = Math.min(LAYOUT_CAP_HEIGHT, panelHeight - topCapHeight);
            int centerSourceHeight = Math.max(1, LAYOUT_BASE_HEIGHT - LAYOUT_CAP_HEIGHT * 2);
            int centerTargetHeight = Math.max(0, panelHeight - topCapHeight - bottomCapHeight);

            graphics.pose().pushPose();
            graphics.pose().translate(panelX, panelY, 0.0F);
            graphics.pose().scale(xScale, 1.0F, 1.0F);
            graphics.blit(layoutTexture, 0, 0, 0.0F, 0.0F,
                    LAYOUT_BASE_WIDTH, topCapHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
            graphics.pose().popPose();

            if (centerTargetHeight > 0) {
                float centerYScale = centerTargetHeight / (float) centerSourceHeight;
                graphics.pose().pushPose();
                graphics.pose().translate(panelX, panelY + topCapHeight, 0.0F);
                graphics.pose().scale(xScale, centerYScale, 1.0F);
                graphics.blit(layoutTexture, 0, 0, 0.0F, LAYOUT_CAP_HEIGHT,
                        LAYOUT_BASE_WIDTH, centerSourceHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
                graphics.pose().popPose();
            }

            graphics.pose().pushPose();
            graphics.pose().translate(panelX, panelY + panelHeight - bottomCapHeight, 0.0F);
            graphics.pose().scale(xScale, 1.0F, 1.0F);
            graphics.blit(layoutTexture, 0, 0, 0.0F, LAYOUT_BASE_HEIGHT - LAYOUT_CAP_HEIGHT,
                    LAYOUT_BASE_WIDTH, bottomCapHeight, LAYOUT_TEXTURE_WIDTH, LAYOUT_TEXTURE_HEIGHT);
            graphics.pose().popPose();
        }
    }

    /**
     * Required method. No support for this right now.
     */
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}