package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.helper.ButtonHelper.drawButtonTexture;
import static net.dillon.qualityofqueso.helper.ButtonHelper.getWidgetPath;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.getHoveredSlot;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.*;
import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * A representation of a transfer button.
 */
public class TransferButton extends Button {
    protected final AbstractContainerMenu menu;
    private final boolean transferrableButton;
    protected final String searchFieldText;
    protected final Font font;
    protected final String resourceLocation;
    protected final String buttonName;
    protected final Supplier<Boolean> canBeActive;

    /**
     * Constructs a default transfer button.
     */
    public TransferButton(AbstractContainerMenu menu, Font font, String searchFieldText, String resourceLocation, String buttonName, boolean transferrableButton, OnPress onPress) {
        super(0, 0, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION);
        this.menu = menu;
        this.font = font;
        this.searchFieldText = searchFieldText;
        this.transferrableButton = transferrableButton;
        this.resourceLocation = resourceLocation;
        this.buttonName = buttonName;
        this.canBeActive = () -> this.active;
    }

    /**
     * Constructs a default transfer button with a boolean supplier, determining if the button can be active or not.
     */
    public TransferButton(AbstractContainerMenu menu, Font font, String searchFieldText, String resourceLocation, String buttonName, boolean transferrableButton, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(0, 0, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION);
        this.menu = menu;
        this.font = font;
        this.searchFieldText = searchFieldText;
        this.resourceLocation = resourceLocation;
        this.buttonName = buttonName;
        this.transferrableButton = transferrableButton;
        this.canBeActive = canBeActive;
    }

    @Override
    public void playDownSound(SoundManager manager) {
    }

    /**
     * Activates a buttons state, if needed.
     */
    protected void activateButton() {
        this.active = this.canBeActive.get();
    }

    /**
     * Fully blocks sound and click if button shouldn't be activated.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int bl) {
        if (this.isMouseOver(mouseX, mouseY) && !this.canBeActive.get()) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, bl);
    }

    /**
     * @return the appended tooltip to use to draw.
     */
    protected Component getTooltipToRender() {
        return Component.translatable("qualityofqueso.gui." + this.buttonName + "_button" + this.getAppendedTooltip());
    }

    /**
     * Renders a button's base texture.
     */
    protected void renderBaseTexture(GuiGraphics graphics) {
        String name = BASE_BUTTON_NAME;
        drawButtonTexture(graphics, "base/" + getWidgetPath() + (!this.canBeActive.get() ? name + "_inactive" : name), this);
    }

    /**
     * Renders the hovered button texture.
     */
    protected void renderHoveredTexture(GuiGraphics graphics, double mouseX, double mouseY) {
        if (!this.canBeActive.get() || !this.isMouseOver(mouseX, mouseY)) {
            return;
        }

        drawButtonTexture(graphics, BASE_BUTTON_HOVERED_PATH, this);
    }

    /**
     * Renders the button texture.
     */
    private void renderButtonTexture(String id, AbstractWidget buttonReference, GuiGraphics graphics, double mouseX, double mouseY) {
        int xy = getTransferButtonXY(this);
        this.renderBaseTexture(graphics);
        graphics.blit(ofQoQ("textures/gui/button/" + id + this.getAppendedTexture()), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, xy, xy, xy, xy);
        this.renderHoveredTexture(graphics, mouseX, mouseY);
    }

    /**
     * Renders a transfer button texture.
     */
    protected void renderBaseButtonTexture(String id, AbstractWidget buttonReference, GuiGraphics graphics, double mouseX, double mouseY) {
        this.renderButtonTexture(id, buttonReference, graphics, mouseX, mouseY);

        if (!hasAnyManagementModifierDown() || !this.isValidButtonName(false)) {
            return;
        }

        Screen screen = getCurrentScreen();
        if (options().management.scrollMoving && (isValidScreenForSingularMoving(screen, true))) {
            if (this.isMouseOver(mouseX, mouseY) ||
                    (getCurrentScreen() instanceof AbstractContainerScreen<?> abstractContainerScreen
                            && getHoveredSlot(abstractContainerScreen) != null && getHoveredSlot(abstractContainerScreen).hasItem())) {
                drawButtonTexture(graphics, MOVE_ONE_PATH, this);
            }
        }
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        this.activateButton();

        this.renderBaseButtonTexture(this.resourceLocation + this.buttonName + (!this.canBeActive.get() ? "_inactive" : ""), this, graphics, mouseX, mouseY);

        if (!this.isMouseOver(mouseX, mouseY) || !this.active || !options().accessibility.tooltips.on()) {
            return;
        }

        Screen screen = Minecraft.getInstance().screen;
        if (screen != null) {
            if (isBrewingStandScreen(screen)) {
                drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.brewing_stand"), graphics, this.font, mouseX, mouseY);
                return;
            } else if (screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen) {
                drawTooltip(Component.translatable("qualityofqueso.gui." + this.buttonName + "_button.furnace", abstractFurnaceScreen.getMenu().getSlot(abstractFurnaceScreen.getMenu().getResultSlotIndex()).getItem().getHoverName()), graphics, this.font, mouseX, mouseY);
                return;
            }
        }
        ItemStack cursorStack = this.menu.getCarried();
        boolean singleQuickMoving = this.isValidButtonName(true) && this.movingOrDroppingOne();
        String newButtonName = singleQuickMoving
                ? "qualityofqueso.gui." + this.buttonName + "_button" + ".move_amount"
                : "qualityofqueso.gui." + this.buttonName + "_button";
        if (this.transferrableButton && !cursorStack.isEmpty()) {
            drawTooltip(Component.translatable(newButtonName + ".with_cursor_stack",
                            cursorStack.getHoverName(),
                            Screen.hasShiftDown()
                                    ? Component.translatable("qualityofqueso.gui.transfer_button.matching_cursor_stack")
                                    : Component.translatable("qualityofqueso.gui.transfer_button.match_cursor_stack")),
                    graphics, this.font, mouseX, mouseY);
        } else if (this.transferrableButton && !this.searchFieldText.isEmpty()) {
            drawTooltip(this.searchFieldText.startsWith("#") ?
                    this.queryComponent(newButtonName + ".with_search_query.tag" + this.getAppendedTooltip(), this.searchFieldText.substring(1)) :
                    this.searchFieldText.startsWith("!") ?
                            this.queryComponent(newButtonName + ".with_search_query.exclude" + this.getAppendedTooltip(), this.searchFieldText.substring(1)) :
                    this.searchFieldText.startsWith(":") ?
                            this.queryComponent(newButtonName + ".with_search_query.match" + this.getAppendedTooltip(), this.searchFieldText.substring(1)) :
                            this.queryComponent(newButtonName + ".with_search_query" + this.getAppendedTooltip(), this.searchFieldText), graphics, this.font, mouseX, mouseY);
        } else {
            if (singleQuickMoving) {
                this.extractMoveAmountTooltip(graphics, mouseX, mouseY);
            } else {
                drawTooltip(this.getTooltipToRender(), graphics, this.font, mouseX, mouseY);
            }
        }
    }

    /**
     * @return the appended texture ID to use for the button.
     */
    protected String getAppendedTexture() {
        String transferableString = !this.menu.getCarried().isEmpty() ?
                "_cursor_stack.png" : this.searchFieldText.startsWith("!") ?
                                      "_excluding.png" : this.searchFieldText.startsWith("#") ?
                                                         "_tag.png" : this.searchFieldText.startsWith(":") ?
                                                                      "_matching.png" : ".png";
        String appended = this.transferrableButton && this.canBeActive.get() ? transferableString : ".png";
        Screen screen = Minecraft.getInstance().screen;

        if (screen == null) {
            return appended;
        }

        if (isBrewingOrFurnaceScreen(screen)) {
            appended = ".png";
        }
        return appended;
    }

    /**
     * @return the correct component for a query.
     */
    protected Component queryComponent(String translation, String query) {
        return this.movingOrDroppingOne()
                ? Component.translatable(translation, getActualMoveAmount(), query)
                : Component.translatable(translation, query);
    }

    /**
     * @return if the user is dropping or moving one of an item.
     */
    protected boolean movingOrDroppingOne() {
        return options().management.scrollMoving && (this.buttonName.equals(QUICK_DROP_BUTTON_NAME)
                ? hasDropOnlyOneItemKeyDown()
                : Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> && hasMoveSingleModifierDown());
    }

    /**
     * The tooltip to display for single-move items.
     */
    private void extractMoveAmountTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        Component tooltip = Component.translatable(this.buttonName.equals(QUICK_DROP_BUTTON_NAME)
                        ? "qualityofqueso.gui.quick_drop_button.move_amount"
                        : "qualityofqueso.gui.move_amount_each",
                getActualMoveAmount());
        if (options().accessibility.tooltips.on()) {
            tooltip = appendedSingleTooltip(tooltip);
        }
        drawTooltip(
                tooltip,
                graphics, this.font, mouseX, mouseY
        );
    }

    /**
     * @return valid button names for different functions.
     */
    protected boolean isValidButtonName(boolean includeDrop) {
        boolean inventoryButton = this.buttonName.equals(TRANSFER_INVENTORY_BUTTON_NAME);
        boolean containerButton = this.buttonName.equals(TRANSFER_CONTAINER_BUTTON_NAME);
        boolean quickDropButton = this.buttonName.equals(QUICK_DROP_BUTTON_NAME);
        return inventoryButton || containerButton || (includeDrop && quickDropButton);
    }

    /**
     * @return an appended tooltip for moving single stack items.
     */
    private static Component appendedSingleTooltip(Component original) {
        return original.copy().append("\n").append(Component.translatable("qualityofqueso.gui.scroll_to_change_amount")).copy().append("\n").append(Component.translatable("qualityofqueso.gui.move_amount.reset"));
    }

    /**
     * @return the appended tooltip to use to render.
     */
    protected String getAppendedTooltip() {
        return "";
    }
}