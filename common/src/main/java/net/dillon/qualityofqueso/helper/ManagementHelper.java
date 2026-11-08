package net.dillon.qualityofqueso.helper;

import net.dillon.qualityofqueso.event.management.ExtractingEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.dillonlib.task.ClientTasks.hasShiftDown;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.canScrollMoveAndHasScrollModifierDown;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasDropOnlyOneItemModifierDown;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Utility and handler class for management features.
 */
public class ManagementHelper {

    /**
     * @return if the screen is a {@link ContainerScreen} or {@link InventoryScreen}.
     */
    public static boolean isValidScreen() {
        return isContainerScreen() || isInventoryScreen();
    }

    /**
     * @return if the screen is a {@link InventoryScreen}.
     */
    public static boolean isInventoryScreen() {
        return getScreen() instanceof InventoryScreen;
    }

    /**
     * @return if the screen is a {@link ContainerScreen} or {@link ShulkerBoxScreen}.
     */
    public static boolean isContainerScreen() {
        return getScreen() instanceof ContainerScreen || isShulkerBoxScreen();
    }

    /**
     * @return if the screen is a {@link ShulkerBoxScreen}.
     */
    public static boolean isShulkerBoxScreen() {
        return getScreen() instanceof ShulkerBoxScreen;
    }

    /**
     * @return if the screen is a {@link HopperScreen} or {@link DispenserScreen}.
     */
    public static boolean isDropperDispenserOrHopperScreen() {
        return isHopperScreen() || isDropperOrDispenserScreen();
    }

    /**
     * @return if the screen is a {@link BrewingStandScreen} or {@link AbstractFurnaceScreen}.
     */
    public static boolean isBrewingOrFurnaceScreen() {
        return isBrewingStandScreen() || isFurnaceScreen();
    }

    /**
     * @return if the screen is either a {@link BrewingStandScreen}, {@link DispenserScreen}, {@link HopperScreen} or {@link AbstractFurnaceScreen}.
     */
    public static boolean isOtherValidScreen() {
        return isBrewingOrFurnaceScreen() || isDropperDispenserOrHopperScreen();
    }

    /**
     * @return if the screen is a {@link BrewingStandScreen}.
     */
    public static boolean isBrewingStandScreen() {
        return getScreen() instanceof BrewingStandScreen;
    }

    /**
     * @return if the screen is a {@link AbstractFurnaceScreen}.
     */
    public static boolean isFurnaceScreen() {
        return getScreen() instanceof AbstractFurnaceScreen<?>;
    }

    /**
     * @return if the screen is a {@link DispenserScreen}.
     */
    public static boolean isDropperOrDispenserScreen() {
        return getScreen() instanceof DispenserScreen;
    }

    /**
     * @return if the screen is a {@link HopperScreen}.
     */
    public static boolean isHopperScreen() {
        return getScreen() instanceof HopperScreen;
    }

    /**
     * @return if the screen is a {@link MerchantScreen}.
     */
    public static boolean isMerchantScreen() {
        return getScreen() instanceof MerchantScreen;
    }

    /**
     * @return if the screen is a {@link CraftingScreen}.
     */
    public static boolean isCraftingScreen() {
        return getScreen() instanceof CraftingScreen;
    }

    /**
     * @return if the screen is a {@link CreativeModeInventoryScreen}.
     */
    public static boolean isCreativeInventoryScreen() {
        return getScreen() instanceof CreativeModeInventoryScreen;
    }

    /**
     * @return valid screens for singular moving, including {@link ContainerScreen}s, {@link DispenserScreen}s, {@link HopperScreen}s, and {@code optional} {@link InventoryScreen}.
     */
    public static boolean isValidScreenForSingularMoving(boolean includeInventory) {
        return isContainerScreen() || isDropperDispenserOrHopperScreen() || (includeInventory && isInventoryScreen());
    }

    /**
     * @return if the screen is a valid screen for rendering slot overlays (locked slot and highlighted items)
     */
    public static boolean isValidScreenForRenderingSlotOverlays() {
        return (
                isValidScreen()
                        || isOtherValidScreen()
                        || isMerchantScreen()
                        || getScreen() instanceof AnvilScreen
                        || getScreen() instanceof BeaconScreen
                        || getScreen() instanceof CartographyTableScreen
                        || getScreen() instanceof CraftingScreen
                        || getScreen() instanceof EnchantmentScreen
                        || getScreen() instanceof GrindstoneScreen
                        || getScreen() instanceof SmithingScreen
                        || getScreen() instanceof StonecutterScreen
        )
                && !isCreativeInventoryScreen()
                && !(getScreen() instanceof CrafterScreen);
    }

    /**
     * @return valid screens for quick equipping, which include {@link InventoryMenu}s, {@link CreativeModeInventoryScreen.ItemPickerMenu}s, and {@link AbstractMountInventoryMenu}s
     */
    public static boolean isValidMenuForQuickEquipping(AbstractContainerMenu menu) {
        return menu instanceof InventoryMenu || menu instanceof CreativeModeInventoryScreen.ItemPickerMenu || isMountingMenu(menu);
    }

    /**
     * @return if the menu is a {@link AbstractMountInventoryMenu}.
     */
    public static boolean isMountingMenu(AbstractContainerMenu menu) {
        return menu instanceof AbstractMountInventoryMenu;
    }

    /**
     * @return if a button is enabled and present.
     */
    public static boolean buttonActive(Button button) {
        return button != null && button.active;
    }

    /**
     * @return if a button is disabled, but present.
     */
    public static boolean buttonInactive(Button button) {
        return button != null && !button.active;
    }

    /**
     * @return if a button is hovered.
     */
    public static boolean buttonHovered(Button button) {
        return button != null && button.isHovered();
    }

    /**
     * @return if a button is currently hovered, but not enabled.
     */
    public static boolean buttonHoveredButInactive(Button button) {
        return buttonInactive(button) && buttonHovered(button);
    }

    /**
     * @return if a button is hovered and enabled.
     */
    public static boolean buttonHoveredAndActive(Button button) {
        return buttonActive(button) && buttonHovered(button);
    }

    /**
     * @return if a button is hovered and enabled and shift is held.
     */
    public static boolean buttonHoveredActiveOrShiftHeld(ExtractingEvents extractingInstance, Button button, boolean inventory) {
        return buttonHoveredAndActive(button) || extractingInstance.shiftHeld(inventory);
    }

    /**
     * @return {@code true} if the hovered slot has an query (assuming hovered slot isn't {@code null}).
     */
    public static boolean hoveredSlotHasItem(Slot hoveredSlot) {
        return hoveredSlot != null && !hoveredSlot.getItem().isEmpty();
    }

    /**
     * @return the width and height for a {@code transfer button.}
     */
    public static int getManagementButtonSize() {
        return 12;
    }

    /**
     * @return A special int to get the bar width.
     */
    public static int getBarWidth(int backgroundWidth) {
        return (int) ((double) backgroundWidth * 0.6);
    }

    /**
     * @return the modifier to use for recipe books.
     */
    public static int getRecipeBookModifier() {
        return !client().misc().noRecipeBookShift
                && getScreen() instanceof AbstractRecipeBookScreen<?> recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible() ? 77 : 0;
    }

    /**
     * @return the Y-position for a container size.
     */
    public static int getContainerY(Container container) {
        return 2 * (container == null ? 0 : container.getContainerSize()) + 12;
    }

    /**
     * @return if the user is {@code carrying} a stack.
     */
    public static boolean isCarrying(AbstractContainerMenu menu) {
        return !menu.getCarried().isEmpty();
    }

    /**
     * @return if the user is {@code excluding} query.
     */
    public static boolean isExcluding(String searchFieldText) {
        return searchFieldText.startsWith("!");
    }

    /**
     * @return if the user is querying by {@code tag}.
     */
    public static boolean isTagFiltering(String searchFieldText) {
        return searchFieldText.startsWith("#");
    }

    /**
     * @return if the user is querying by {@code match case}.
     */
    public static boolean isMatchCase(String searchFieldText) {
        return searchFieldText.startsWith(":");
    }

    /**
     * @return if the user is dropping or moving one of an item.
     */
    public static boolean movingOrDroppingOne(String buttonName) {
        return client().management().scrollMoving && (buttonName.equals(QUICK_DROP_BUTTON_NAME)
                ? hasDropOnlyOneItemModifierDown()
                : getScreen() instanceof AbstractContainerScreen<?> && canScrollMoveAndHasScrollModifierDown()
        );
    }

    /**
     * @return valid button names for different functions.
     */
    public static boolean isValidButtonName(String buttonName, boolean includeDrop) {
        boolean inventoryButton = buttonName.equals(TRANSFER_INVENTORY_BUTTON_NAME);
        boolean containerButton = buttonName.equals(TRANSFER_CONTAINER_BUTTON_NAME);
        boolean quickDropButton = buttonName.equals(QUICK_DROP_BUTTON_NAME);
        return inventoryButton || containerButton || (includeDrop && quickDropButton);
    }

    /**
     * The tooltips to display for single-move items.
     */
    public static void extractMoveAmountTooltip(String buttonName, GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Component tooltip = Component.translatable(buttonName.equals(QUICK_DROP_BUTTON_NAME)
                        ? "qualityofqueso.gui.quick_drop_button.move_amount"
                        : "qualityofqueso.gui.move_amount_each",
                getActualMoveAmount());
        if (client().general().tooltips.enabled()) {
            tooltip = appendedSingleTooltip(tooltip);
        }

        drawTooltip(
                tooltip,
                graphics, mouseX, mouseY
        );
    }

    /**
     * @return the single-move amount for stacks.
     */
    public static Component getActualMoveAmount() {
        return Component.literal(String.valueOf(MOVE_AMOUNT)).copy().withStyle(ChatFormatting.BOLD);
    }

    /**
     * @return the cursor tooltip for a management button.
     */
    public static Component getCursorTooltip() {
        return hasShiftDown()
                ? Component.translatable("qualityofqueso.gui.transfer_button.matching_cursor_stack")
                : Component.translatable("qualityofqueso.gui.transfer_button.match_cursor_stack");
    }

    /**
     * @return the correct component for a query.
     */
    public static Component queryComponent(String translation, String query) {
        return movingOrDroppingOne(translation)
                ? Component.translatable(translation, getActualMoveAmount(), query)
                : Component.translatable(translation, query);
    }

    /**
     * @return the query tooltip to display.
     */
    public static Component getQueryTooltip(String newButtonName, String searchFieldText, String appendedTooltip) {
        String query = newButtonName + ".with_search_query";
        String querySubbed = searchFieldText.substring(1);

        if (isTagFiltering(searchFieldText)) {
            return queryComponent(query + ".tag" + appendedTooltip, querySubbed);
        } else if (isExcluding(searchFieldText)) {
            return queryComponent(query + ".excluding" + appendedTooltip, querySubbed);
        } else if (isMatchCase(searchFieldText)) {
            return queryComponent(query + ".matching" + appendedTooltip, querySubbed);
        }

        return queryComponent(
                query + appendedTooltip,
                searchFieldText
        );
    }

    /**
     * @return an appended tooltips for moving single stack items.
     */
    public static Component appendedSingleTooltip(Component original) {
        return original.copy().append("\n").append(Component.translatable("qualityofqueso.gui.scroll_to_change_amount")).copy().append("\n").append(Component.translatable("qualityofqueso.gui.move_amount.reset"));
    }

    /**
     * @return the {@code X} value for transferring query buttons.
     */
    public static int getManagementButtonX(int backgroundWidth, int width, int button) {
        int barWidth = getBarWidth(backgroundWidth);
        int modifier = 18;
        if (isBrewingStandScreen()) {
            modifier -= 36;
        } else if (isDropperOrDispenserScreen()) {
            modifier -= 38;
        } else if (isHopperScreen()) {
            modifier -= 20;
        } else if (isFurnaceScreen()) {
            modifier -= 16;
        }

        if (getScreen() instanceof AbstractRecipeBookScreen<?> recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible()) {
            modifier += getRecipeBookModifier();
        }
        return (width / 2 + barWidth / 2 + modifier) - (button * 12);
    }

    /**
     * @return the {@code y-value} for container management buttons.
     */
    public static int getManagementButtonY(Container container, int screenY, int titleY) {
        int y = getContainerY(container);
        if (isBrewingStandScreen()) {
            y += 30;
        } else if (isFurnaceScreen()) {
            y += 14;
        } else if (isDropperOrDispenserScreen()) {
            y += 24;
        } else if (isHopperScreen()) {
            y -= 1;
        }
        return screenY + titleY + (getScreen() instanceof InventoryScreen ? 64 : y);
    }
}