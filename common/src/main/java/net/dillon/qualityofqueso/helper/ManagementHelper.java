package net.dillon.qualityofqueso.helper;

import net.dillon.qualityofqueso.event.management.ExtractingEvents;
import net.dillon.qualityofqueso.sound.ModSoundEvents;
import net.dillon.qualityofqueso.widget.QuesoButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;

import static net.dillon.dillonlib.task.ClientTasks.getMinecraft;
import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.sound.ModSoundEvents.getSound;

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
     * @return if a button is active and present.
     */
    public static boolean buttonActive(Button button) {
        return button != null && button.active;
    }

    /**
     * @return if a button is inactive, but present.
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
     * @return if a button is currently hovered, but not active.
     */
    public static boolean buttonHoveredButInactive(Button button) {
        return buttonInactive(button) && buttonHovered(button);
    }

    /**
     * @return if a button is hovered and active.
     */
    public static boolean buttonHoveredAndActive(Button button) {
        return buttonActive(button) && buttonHovered(button);
    }

    /**
     * @return if a button is hovered and active and shift is held.
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
     * Plays the default button press sound.
     */
    public static void playDefaultSound(SoundManager manager) {
        if (!client().management().playSounds) {
            return;
        }

        manager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /**
     * Plays the bundle sounds without the drop when using buttons.
     */
    public static void playButtonSound() {
        playButtonSound(false);
    }

    /**
     * Plays the bundle sounds when using buttons.
     */
    public static void playButtonSound(boolean drop) {
        if (!client().management().playSounds) {
            return;
        }

        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(drop ? getSound(ModSoundEvents.ITEMS_DROPPED) : getSound(ModSoundEvents.ITEMS_MOVED), 1.0F, 5.0F));
    }

    /**
     * Plays the bundle sounds when sorting.
     */
    public static void playSortSound() {
        if (!client().management().playSounds) {
            return;
        }

        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(getSound(ModSoundEvents.ITEMS_SORTED), 1.0F, 5.0F));
    }

    /**
     * Plays the safe sort sound.
     */
    public static void playSafeSortSound() {
        if (SORT_SOUND_COOLDOWN == 0) {
            SORT_SOUND_COOLDOWN = DEFAULT_SORT_SOUND_COOLDOWN;
            playSortSound();
        }
    }

    /**
     * Plays the inactive bundle sound.
     */
    public static void playButtonInactiveSound() {
        if (!client().management().playSounds) {
            return;
        }

        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(getSound(ModSoundEvents.ITEM_MOVES_REJECTED), 1.0F, 0.6F));
    }

    /**
     * Plays the lock slot sound.
     */
    public static void playLockSlotSound(boolean lock) {
        if (!client().lockedSlots().lockedSlots || !client().lockedSlots().lockSound || LOCKED_SLOT_SOUND_COOLDOWN > 0) {
            return;
        }

        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(lock ? getSound(ModSoundEvents.SLOT_LOCKED) : getSound(ModSoundEvents.SLOT_UNLOCKED), 1.0F, 0.10F));
        LOCKED_SLOT_SOUND_COOLDOWN = DEFAULT_LOCKED_SLOT_SOUND_COOLDOWN;
    }

    /**
     * Plays the quick equip sound.
     */
    public static void playQuickEquipSound() {
        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F, 0.45F));
    }

    /**
     * @return the width and height for a {@code transfer button.}
     */
    public static int getTransferButtonXY(QuesoButton button) {
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
     * @return the single-move amount for stacks.
     */
    public static Component getActualMoveAmount() {
        return Component.literal(String.valueOf(MOVE_AMOUNT)).copy().withStyle(ChatFormatting.BOLD);
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