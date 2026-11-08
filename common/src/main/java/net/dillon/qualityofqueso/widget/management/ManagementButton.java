package net.dillon.qualityofqueso.widget.management;

import net.dillon.dillonlib.client.component.TextureBuilder;
import net.dillon.dillonlib.widespread.DillonsTexts;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.ButtonHelper.getWidgetPath;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.MOVE_ONE_PATH;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.canScrollMoveAndHasScrollModifierDown;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasAnyManagementModifierDown;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A management button, with vast versatility and settings for creating and implementing different management features.
 */
public abstract class ManagementButton extends Button {
    protected static final String NO_APPENDED_TOOLTIP = "";
    protected final AbstractContainerMenu menu;
    protected final ManagementEvents managementEvents;
    protected final String searchFieldText;
    protected final String buttonName;
    private final String location;
    private final String appendedTooltip;
    private final boolean transferable;

    /**
     * Constructs a management button, with the same location as the button name.
     */
    public ManagementButton(
            ManagementParams params,
            String buttonNameAndLocation,
            String appendedTooltip,
            boolean transferable,
            Button.OnPress onPress
    ) {
        this(
                params,
                buttonNameAndLocation,
                buttonNameAndLocation,
                appendedTooltip,
                transferable,
                onPress
        );
    }

    /**
     * Constructs a management button.
     */
    public ManagementButton(
            ManagementParams params,
            String buttonName,
            String location,
            String appendedTooltip,
            boolean transferable,
            Button.OnPress onPress
    ) {
        super(0, 0, 10, 10, DillonsTexts.BLANK, onPress, DEFAULT_NARRATION);
        this.menu = params.menu();
        this.managementEvents = params.managementEvents();
        this.searchFieldText = params.managementEvents().searchEvents().getSearchFieldText();
        this.buttonName = buttonName;
        this.location = location;
        this.appendedTooltip = appendedTooltip;
        this.transferable = transferable;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (!this.canBeActive()) {
            return false;
        }

        return super.mouseClicked(event, isDouble);
    }

    @Override
    public void playDownSound(SoundManager manager) {
    }

    /**
     * @return if the button can be active. Must be checked. Use {@link ManagementEvents} to determine can be active.
     */
    protected abstract boolean canBeActive();

    /**
     * Activates a buttons state.
     */
    protected final void activateButton() {
        active = this.canBeActive();
    }

    /**
     * @return the appended tooltips to use to draw.
     */
    protected Component getDefaultTooltip() {
        return Component.translatable("qualityofqueso.gui." + buttonName + "_button" + appendedTooltip);
    }

    /**
     * @return the appended texture ID to use for the button.
     */
    protected String getDefaultAppendedTexture() {
        String transferableString = "";

        if (isCarrying(menu)) {
            transferableString = "_cursor_stack";
        } else if (isExcluding(searchFieldText)) {
            transferableString = "_excluding";
        } else if (isTagFiltering(searchFieldText)) {
            transferableString = "_tag";
        } else if (isMatchCase(searchFieldText)) {
            transferableString = "_matching";
        }

        String appended = transferable && this.canBeActive()
                ? transferableString
                : "";

        if (getScreen() == null) {
            return appended;
        } else if (isBrewingOrFurnaceScreen()) {
            return "";
        } else {
            return appended;
        }
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        activateButton();

        renderButton(graphics);

        if (isHoveredOrFocused() && active && client().general().tooltips.enabled()) {
            Screen screen = getScreen();

            if (screen != null) {
                if (isBrewingStandScreen()) {
                    drawTooltip(Component.translatable("qualityofqueso.gui." + buttonName + "_button.brewing_stand"), graphics, mouseX, mouseY);
                    return;
                } else if (screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen) {
                    drawTooltip(Component.translatable("qualityofqueso.gui." + buttonName + "_button.furnace", abstractFurnaceScreen.getMenu().getResultSlot().getItem().getHoverName()), graphics, mouseX, mouseY);
                    return;
                }
            }

            ItemStack cursorStack = menu.getCarried();
            boolean singleQuickMoving = isValidButtonName(buttonName, true) && movingOrDroppingOne(buttonName);
            String newButtonName = singleQuickMoving
                    ? "qualityofqueso.gui." + buttonName + "_button" + ".move_amount"
                    : "qualityofqueso.gui." + buttonName + "_button";

            if (transferable && isCarrying(menu)) {
                drawTooltip(
                        Component.translatable(newButtonName + ".with_cursor_stack", cursorStack.getItemName(), getCursorTooltip()),
                        graphics, mouseX, mouseY
                );
            } else if (transferable && !searchFieldText.isEmpty()) {
                drawTooltip(getQueryTooltip(newButtonName, searchFieldText, appendedTooltip), graphics, mouseX, mouseY);
            } else {
                if (singleQuickMoving) {
                    extractMoveAmountTooltip(buttonName, graphics, mouseX, mouseY);
                } else {
                    drawTooltip(getDefaultTooltip(), graphics, mouseX, mouseY);
                }
            }
        }
    }

    /**
     * Extracts the button.
     */
    protected void renderButton(GuiGraphicsExtractor graphics) {
        this.extractButton(graphics);

        if (!hasAnyManagementModifierDown() || !isValidButtonName(buttonName, false)) {
            return;
        }

        if (canScrollMoveAndHasScrollModifierDown() && client().management().scrollMoving && (isValidScreenForSingularMoving(true))) {
            if (isHovered()) {
                this.drawButtonTexture(graphics, managementIdentifier(MOVE_ONE_PATH));
            }
        }
    }

    /**
     * @return the sprite to render for this button.
     */
    protected ManagementSprites getManagementSprites() {
        String button = location + "/" + buttonName;
        return new ManagementSprites(
                managementIdentifier(button + getDefaultAppendedTexture()),
                managementIdentifier(button + "_inactive")
        );
    }

    /**
     * Extracts the management sprite.
     */
    protected void extractManagementSprite(GuiGraphicsExtractor graphics) {
        this.drawButtonTexture(graphics, this.getManagementSprites().get(active));
    }

    /**
     * Extracts the base button texture.
     */
    protected final void extractButton(GuiGraphicsExtractor graphics) {
        this.drawButtonTexture(graphics, this.getBaseSprites().get(active, isHoveredOrFocused()));
        this.extractManagementSprite(graphics);
    }

    /**
     * @return the base sprite to render for this button.
     */
    protected final WidgetSprites getBaseSprites() {
        String path = "base/" + getWidgetPath(false);
        return new WidgetSprites(
                managementIdentifier(path + "button"),
                managementIdentifier(path + "button_inactive"),
                managementIdentifier(path + "button_focused")
        );
    }

    /**
     * Draws the texture for a {@code management button} with a sprite.
     */
    protected final void drawButtonTexture(GuiGraphicsExtractor graphics, Identifier sprite) {
        TextureBuilder.create(
                graphics,
                sprite,
                getX() - 1,
                getY() - 1,
                getManagementButtonSize(),
                getManagementButtonSize()
        ).buildAndBlit(false); // Cannot blit as sprite because buttons look weird
    }
}