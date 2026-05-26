package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.dillon.qualityofqueso.helper.ManagementHelper.playButtonSound;
import static net.dillon.qualityofqueso.helper.MethodHelper.key;

/**
 * Set filtered items in a container (fully 100% client-side).
 */
public class FilterItemsScreen extends Screen {
    private static final int SLOT_SIZE = 18;
    private static final int COLUMNS = 9;
    private static final int PLACEHOLDER_ROWS = 6;
    private static final int MAX_PLACEHOLDER_SLOTS = 504;
    private static final int MAX_PLACEHOLDER_ROWS = MAX_PLACEHOLDER_SLOTS / COLUMNS;
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 222;
    private static final int INVENTORY_START_Y = 139;
    private static final int HOTBAR_Y = 197;
    private static final int TOP_BACKGROUND_HEIGHT = PLACEHOLDER_ROWS * 18 + 17;
    private static final int BOTTOM_BACKGROUND_HEIGHT = 96;
    private static final ResourceLocation CHEST_TEXTURE = ResourceLocation.parse("minecraft:textures/gui/container/generic_54.png");
    private final AbstractContainerScreen<?> parentScreen;
    private final List<ItemStack> placeholders = new ArrayList<>();
    private ItemStack selectedStack = ItemStack.EMPTY;
    private int selectedSourceSlot = -1;
    private int scrollRow = 0;
    private int lastMouseX;
    private int lastMouseY;

    public FilterItemsScreen(AbstractContainerScreen<?> parentScreen) {
        super(Component.empty());
        this.parentScreen = parentScreen;
    }

    /**
     * This is not a pause screen.
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * @return the panel X for the screen.
     */
    private int panelX() {
        return (this.width - GUI_WIDTH) / 2;
    }

    /**
     * @return the pane Y for the screen.
     */
    private int panelY() {
        return (this.height - GUI_HEIGHT) / 2;
    }

    /**
     * @return the starting point for the panel Y.
     */
    private int sourceStartY() {
        return this.panelY() + INVENTORY_START_Y;
    }

    /**
     * @return the starting point for the placeholders panel Y.

     */
    private int placeholderStartY() {
        return this.panelY() + 18;
    }

    /**
     * @return a source slot at the mouse's position (from the player's inventory).
     */
    private int sourceSlotAt(double mouseX, double mouseY) {
        int startX = this.panelX() + 8;
        int topInvY = this.sourceStartY();
        int hotbarY = this.panelY() + HOTBAR_Y;
        for (int i = 0; i < 36; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = startX + col * SLOT_SIZE;
            int y = i < 27
                    ? topInvY + row * SLOT_SIZE
                    : hotbarY;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return a placeholder slot from the filtered items list.
     */
    private int placeholderSlotAt(double mouseX, double mouseY) {
        int startX = this.panelX() + 8;
        int startY = this.placeholderStartY();
        for (int row = 0; row < PLACEHOLDER_ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int x = startX + col * SLOT_SIZE;
                int y = startY + row * SLOT_SIZE;
                if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                    int visibleSlot = row * COLUMNS + col;
                    int absoluteSlot = this.scrollRow * COLUMNS + visibleSlot;
                    return absoluteSlot < this.placeholders.size() ? absoluteSlot : -1;
                }
            }
        }
        return -1;
    }

    /**
     * @return the maximum scroll Y for filtered items.
     */
    private int maxScrollRow() {
        return Math.max(0, MAX_PLACEHOLDER_ROWS - PLACEHOLDER_ROWS);
    }

    /**
     * @return the current source stack (from player inventory).
     */
    private ItemStack sourceStackAt(int sourceIndex) {
        int slotIndexInMenu = this.parentScreen.getMenu().slots.size() - 36 + sourceIndex;
        Slot slot = this.parentScreen.getMenu().getSlot(slotIndexInMenu);
        return slot.getItem();
    }

    /**
     * @return the first empty placeholder slot, or available slot.
     */
    private int firstEmptyPlaceholderSlot() {
        for (int i = 0; i < this.placeholders.size(); i++) {
            if (this.placeholders.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return {@code true} if the container already has stack in filtered list.
     */
    private boolean alreadyHasPlaceholderItem(ItemStack stack) {
        for (ItemStack placeholder : this.placeholders) {
            if (!placeholder.isEmpty() && ItemStack.isSameItemSameComponents(placeholder, stack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves placeholder/filter items, ensures that we are returning from a placeholder screen, and closes the screen with the sound.
     */
    @Override
    public void onClose() {
        ContainerHelper.setCurrentPlaceholderStacks(this.placeholders);
        ContainerHelper.RETURNING_FROM_PLACEHOLDER_SCREEN = true;
        this.minecraft.setScreen(this.parentScreen);
        playButtonSound(this.minecraft);
    }

    /**
     * Handles closing the screen correctly.

     */
    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        if (keycode == GLFW.GLFW_KEY_ESCAPE || keycode == key(Minecraft.getInstance().options.keyInventory).getValue()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keycode, scancode, modifiers);
    }

    /**
     * Constructs the screen, and adds all placeholder items from data.
     */
    @Override
    protected void init() {
        playButtonSound(this.minecraft);
        this.placeholders.clear();
        this.scrollRow = 0;
        int size = MAX_PLACEHOLDER_SLOTS;
        for (int i = 0; i < size; i++) {
            this.placeholders.add(ItemStack.EMPTY);
        }

        List<ItemStack> saved = ContainerHelper.getCurrentPlaceholderStacks();
        for (int i = 0; i < Math.min(saved.size(), size); i++) {
            ItemStack stack = saved.get(i);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                this.placeholders.set(i, copy);
            }
        }
    }

    /**
     * Handles all mouse clicking events, from adding/removing placeholder slots.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int bl) {
        if (super.mouseClicked(mouseX, mouseY, bl)) {
            return true;
        }

        int lastMouseX = this.lastMouseX;
        int lastMouseY = this.lastMouseY;

        int sourceSlot = this.sourceSlotAt(lastMouseX, lastMouseY);
        if (sourceSlot >= 0 && bl == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            ItemStack stack = this.sourceStackAt(sourceSlot);
            if (!stack.isEmpty()) {
                if (Screen.hasShiftDown()) {
                    if (this.alreadyHasPlaceholderItem(stack)) {
                        return false;
                    }
                    int placeholderSlot = this.firstEmptyPlaceholderSlot();
                    if (placeholderSlot >= 0) {
                        ItemStack copy = stack.copy();
                        copy.setCount(1);
                        this.placeholders.set(placeholderSlot, copy);
                        this.selectedStack = ItemStack.EMPTY;
                        this.selectedSourceSlot = -1;
                        playButtonSound(this.minecraft);
                    }
                    return true;
                }

                ItemStack copy = stack.copy();
                copy.setCount(1);
                this.selectedStack = copy;
                this.selectedSourceSlot = sourceSlot;
                playButtonSound(this.minecraft);
            }
            return true;
        }

        int placeholderSlot = this.placeholderSlotAt(lastMouseX, lastMouseY);
        if (placeholderSlot >= 0) {
            if (bl == GLFW.GLFW_MOUSE_BUTTON_RIGHT && !this.placeholders.get(placeholderSlot).isEmpty()) {
                this.placeholders.set(placeholderSlot, ItemStack.EMPTY);
                playButtonSound(this.minecraft);
                return true;
            }
            if (bl == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (this.alreadyHasPlaceholderItem(this.selectedStack)) {
                    return false;
                }
                if (!this.selectedStack.isEmpty()) {
                    ItemStack copy = this.selectedStack.copy();
                    copy.setCount(1);
                    this.placeholders.set(placeholderSlot, copy);
                    this.selectedStack = ItemStack.EMPTY;
                    this.selectedSourceSlot = -1;
                    playButtonSound(this.minecraft);
                }
                return true;
            }
        }

        if (bl == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (!this.selectedStack.isEmpty()) {
                playButtonSound(this.minecraft);
            }
            this.selectedStack = ItemStack.EMPTY;
            this.selectedSourceSlot = -1;
            return true;
        }

        return true;
    }

    /**
     * Handles scrolling through the filtered items list.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0) {
            this.scrollRow = Math.max(0, this.scrollRow - 1);
        } else if (verticalAmount < 0) {
            this.scrollRow = Math.min(this.maxScrollRow(), this.scrollRow + 1);
        }

        return true;
    }

    /**
     * Extracts background textures, items and tooltips.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;

        int panelX = this.panelX();
        int panelY = this.panelY();
        int startX = panelX + 8;
        int placeholderStartY = this.placeholderStartY();
        int sourceStartY = this.sourceStartY();
        int hotbarY = panelY + HOTBAR_Y;

        // Match vanilla GenericContainerScreen rendering to avoid slot/background offset drift.
        graphics.blit(CHEST_TEXTURE, panelX, panelY, 0, 0, GUI_WIDTH, TOP_BACKGROUND_HEIGHT, 256, 256);
        graphics.blit(CHEST_TEXTURE, panelX, panelY + TOP_BACKGROUND_HEIGHT, 0, 126, GUI_WIDTH, BOTTOM_BACKGROUND_HEIGHT, 256, 256);

        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.placeholder_editor"), panelX + 8, panelY + 6, ModConstants.DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR, false);
        graphics.drawString(this.font, Component.translatable("container.inventory"), panelX + 8, panelY + (GUI_HEIGHT - 94), ModConstants.DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR, false);

        for (int row = 0; row < PLACEHOLDER_ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int visibleSlot = row * COLUMNS + col;
                int slot = this.scrollRow * COLUMNS + visibleSlot;
                int x = startX + col * SLOT_SIZE;
                int y = placeholderStartY + row * SLOT_SIZE;

                ItemStack stack = this.placeholders.get(slot);
                if (!stack.isEmpty()) {
                    graphics.renderFakeItem(stack, x, y);
                    graphics.renderItemDecorations(this.font, stack, x, y);

                }
            }
        }

        for (int i = 0; i < 36; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = startX + col * SLOT_SIZE;
            int y = i < 27
                    ? sourceStartY + row * SLOT_SIZE
                    : hotbarY;
            if (this.selectedSourceSlot == i) {
                graphics.fill(x - 1, y - 1, x + 17, y + 17, 0x66A0A0FF);
            }

            ItemStack stack = this.sourceStackAt(i);
            if (!stack.isEmpty()) {
                graphics.renderFakeItem(stack, x, y);
                graphics.renderItemDecorations(this.font, stack, x, y);
            }
        }

        int hoveredSource = this.sourceSlotAt(mouseX, mouseY);
        if (hoveredSource >= 0) {
            ItemStack stack = this.sourceStackAt(hoveredSource);
            if (!stack.isEmpty()) {
                graphics.renderTooltip(
                        this.font,
                        stack.getTooltipLines(Item.TooltipContext.EMPTY, this.minecraft.player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL),
                        Optional.empty(),
                        mouseX,
                        mouseY
                );
            }
        } else {
            int hoveredPlaceholder = this.placeholderSlotAt(mouseX, mouseY);
            if (hoveredPlaceholder >= 0) {
                ItemStack stack = this.placeholders.get(hoveredPlaceholder);
                if (!stack.isEmpty()) {
                    graphics.renderTooltip(
                            this.font,
                            stack.getTooltipLines(Item.TooltipContext.EMPTY, this.minecraft.player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL),
                            Optional.empty(),
                            mouseX,
                            mouseY
                    );
                }
            }
        }

        if (!this.selectedStack.isEmpty()) {
            graphics.renderFakeItem(this.selectedStack, mouseX - 8, mouseY - 8);
            graphics.renderItemDecorations(this.font, this.selectedStack, mouseX - 8, mouseY - 8);
        }
    }
}