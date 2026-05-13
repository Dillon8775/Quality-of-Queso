package net.dillon.qualityofqueso.helper;

import net.dillon.qualityofqueso.instance.management.ExtractingInstance;
import net.dillon.qualityofqueso.sound.ModSoundEvents;
import net.dillon.qualityofqueso.widget.TransferButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Utility and handler class for management features.
 */
public class ManagementHelper {

    /**
     * @return if the screen is a {@link ContainerScreen} or {@link InventoryScreen}.
     */
    public static boolean isValidScreen(Screen screen) {
        return isContainerScreen(screen) || isInventoryScreen(screen);
    }

    /**
     * @return if the screen is a {@link InventoryScreen}.
     */
    public static boolean isInventoryScreen(Screen screen) {
        return screen instanceof InventoryScreen;
    }

    /**
     * @return if the screen is a {@link ContainerScreen} or {@link ShulkerBoxScreen}.
     */
    public static boolean isContainerScreen(Screen screen) {
        return screen instanceof ContainerScreen || isShulkerBoxScreen(screen);
    }

    /**
     * @return valid shulkerBoxScreen.
     */
    public static boolean isShulkerBoxScreen(Screen screen) {
        return screen instanceof ShulkerBoxScreen;
    }

    /**
     * @return if the screen is a {@link HopperScreen} or {@link DispenserScreen}.
     */
    public static boolean isDropperDispenserOrHopperScreen(Screen screen) {
        return isHopperScreen(screen) || isDropperOrDispenserScreen(screen);
    }

    /**
     * @return if the screen is a {@link BrewingStandScreen} or {@link AbstractFurnaceScreen}.
     */
    public static boolean isBrewingOrFurnaceScreen(Screen screen) {
        return isBrewingStandScreen(screen) || isFurnaceScreen(screen);
    }

    /**
     * @return if the screen is either a {@link BrewingStandScreen}, {@link DispenserScreen}, {@link HopperScreen} or {@link AbstractFurnaceScreen}.
     */
    public static boolean isOtherValidScreen(Screen screen) {
        return isBrewingOrFurnaceScreen(screen) || isDropperDispenserOrHopperScreen(screen);
    }

    /**
     * @return if the screen is a {@link BrewingStandScreen}.
     */
    public static boolean isBrewingStandScreen(Screen screen) {
        return screen instanceof BrewingStandScreen;
    }

    /**
     * @return if the screen is a {@link AbstractFurnaceScreen}.
     */
    public static boolean isFurnaceScreen(Screen screen) {
        return screen instanceof AbstractFurnaceScreen<?>;
    }

    /**
     * @return if the screen is a {@link DispenserScreen}.
     */
    public static boolean isDropperOrDispenserScreen(Screen screen) {
        return screen instanceof DispenserScreen;
    }

    /**
     * @return if the screen is a {@link HopperScreen}.
     */
    public static boolean isHopperScreen(Screen screen) {
        return screen instanceof HopperScreen;
    }

    /**
     * @return if the screen is a {@link MerchantScreen}.
     */
    public static boolean isMerchantScreen(Screen screen) {
        return screen instanceof MerchantScreen;
    }

    /**
     * @return if the screen is a {@link CraftingScreen}.
     */
    public static boolean isCraftingScreen(Screen screen) {
        return screen instanceof CraftingScreen;
    }

    /**
     * @return if the screen is a {@link CreativeModeInventoryScreen}.
     */
    public static boolean isCreativeInventoryScreen(Screen screen) {
        return screen instanceof CreativeModeInventoryScreen;
    }

    /**
     * @return valid screens for singular moving, including {@link ContainerScreen}s, {@link DispenserScreen}s, {@link HopperScreen}s, and {@code optional} {@link InventoryScreen}.
     */
    public static boolean isValidScreenForSingularMoving(Screen screen, boolean includeInventory) {
        return isContainerScreen(screen) || isDropperDispenserOrHopperScreen(screen) || (includeInventory && isInventoryScreen(screen));
    }

    /**
     * @return if the screen is a valid screen for rendering the locked slot color overlay.
     */
    public static boolean isValidScreenForRenderingLockedSlotOverlay(AbstractContainerScreen<?> screen) {
        return !isCreativeInventoryScreen(screen);
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
    public static boolean buttonHovered(Button button, double mouseX, double mouseY) {
        return button != null && button.isMouseOver(mouseX, mouseY);
    }

    /**
     * @return if a button is currently hovered, but not active.
     */
    public static boolean buttonHoveredButInactive(Button button, double mouseX, double mouseY) {
        return buttonInactive(button) && buttonHovered(button, mouseX, mouseY);
    }

    /**
     * @return if a button is hovered and active.
     */
    public static boolean buttonHoveredAndActive(Button button, double mouseX, double mouseY) {
        return buttonActive(button) && buttonHovered(button, mouseX, mouseY);
    }

    /**
     * @return if a button is hovered and active and shift is held.
     */
    public static boolean buttonHoveredActiveOrShiftHeld(ExtractingInstance extractingInstance, Button button, double mouseX, double mouseY, boolean inventory) {
        return buttonHoveredAndActive(button, mouseX, mouseY) || extractingInstance.shiftHeld(inventory);
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
        if (!options().management.playSounds) {
            return;
        }

        manager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /**
     * Plays the bundle sounds without the drop when using buttons.
     */
    public static void playButtonSound(Minecraft client) {
        playButtonSound(client, false);
    }

    /**
     * Plays the bundle sounds when using buttons.
     */
    public static void playButtonSound(Minecraft client, boolean drop) {
        if (!options().management.playSounds) {
            return;
        }

        client.getSoundManager().play(SimpleSoundInstance.forUI(drop ? ModSoundEvents.MANAGEMENT_DROP : ModSoundEvents.MANAGEMENT_SUCCEED, 1.0F, 5.0F));
    }

    /**
     * Plays the bundle sounds when sorting
     */
    public static void playSortSound(Minecraft client) {
        if (!options().management.playSounds) {
            return;
        }

        client.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.MANAGEMENT_SORT, 1.0F, 5.0F));
    }

    /**
     * Plays the inactive bundle sound.
     */
    public static void playButtonInactiveSound(Minecraft client) {
        if (!options().management.playSounds) {
            return;
        }

        client.getSoundManager().play(SimpleSoundInstance.forUI(ModSoundEvents.MANAGEMENT_REJECT, 1.0F, 0.6F));
    }

    /**
     * Plays the lock slot sound.
     */
    public static void playLockSlotSound(Minecraft client, boolean lock) {
        if (!options().lockedSlots.enableLockedSlots || !options().lockedSlots.lockSound || LOCKED_SLOT_SOUND_COOLDOWN > 0) {
            return;
        }

        client.getSoundManager().play(SimpleSoundInstance.forUI(lock ? ModSoundEvents.LOCK_SLOT : ModSoundEvents.UNLOCK_SLOT, 1.0F, 0.15F));
        LOCKED_SLOT_SOUND_COOLDOWN = DEFAULT_LOCKED_SLOT_SOUND_COOLDOWN;
    }

    /**
     * @return the width and height for a {@code transfer button.}
     */
    public static int getTransferButtonXY(TransferButton button) {
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
    public static int getRecipeBookModifier(Screen screen) {
        return screen instanceof InventoryScreen recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible() ? 77 : 0;
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
    public static int getManagementButtonX(Screen screen, int backgroundWidth, int width, int button) {
        int barWidth = getBarWidth(backgroundWidth);
        int modifier = 18;
        if (isBrewingStandScreen(screen)) {
            modifier -= 36;
        } else if (isDropperOrDispenserScreen(screen)) {
            modifier -= 38;
        } else if (isHopperScreen(screen)) {
            modifier -= 20;
        } else if (isFurnaceScreen(screen)) {
            modifier -= 16;
        } else if (screen instanceof InventoryScreen recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible()) {
            modifier += getRecipeBookModifier(screen);
        }
        return (width / 2 + barWidth / 2 + modifier) - (button * 12);
    }

    /**
     * @return the {@code y-value} for container management buttons.
     */
    public static int getManagementButtonY(Screen screen, Container container, int screenY, int titleY) {
        int y = getContainerY(container);
        if (isBrewingStandScreen(screen)) {
            y += 30;
        } else if (isFurnaceScreen(screen)) {
            y += 14;
        } else if (isDropperOrDispenserScreen(screen)) {
            y += 24;
        } else if (isHopperScreen(screen)) {
            y -= 1;
        }
        return screenY + titleY + (screen instanceof InventoryScreen ? 64 : y);
    }
}