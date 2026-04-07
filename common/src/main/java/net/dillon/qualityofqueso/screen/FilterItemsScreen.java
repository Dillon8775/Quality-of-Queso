package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.util.ContainerUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.dillon.qualityofqueso.util.AccessorUtil.key;
import static net.dillon.qualityofqueso.util.ButtonUtil.playButtonSound;

/**
 * Set filtered items in a container (fully 100% client-side).
 */
public class FilterItemsScreen extends Screen {
    private static final int SLOT_SIZE = 18;
    private static final int COLUMNS = 9;
    private static final int PLACEHOLDER_ROWS = 6;
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 222;
    private static final int INVENTORY_START_Y = 139;
    private static final int HOTBAR_Y = 197;
    private static final int TOP_BACKGROUND_HEIGHT = PLACEHOLDER_ROWS * 18 + 17;
    private static final int BOTTOM_BACKGROUND_HEIGHT = 96;
    private static final Identifier CHEST_TEXTURE = Identifier.parse("minecraft:textures/gui/container/generic_54.png");
    private final AbstractContainerScreen<?> parentScreen;
    private final List<ItemStack> placeholders = new ArrayList<>();
    private ItemStack selectedStack = ItemStack.EMPTY;
    private int selectedSourceSlot = -1;
    private int lastMouseX;
    private int lastMouseY;

    public FilterItemsScreen(AbstractContainerScreen<?> parentScreen) {
        super(Component.empty());
        this.parentScreen = parentScreen;
    }

    /**
     * Constructs the screen, and retrieves all filtered items.
     */
    @Override
    protected void init() {
        playButtonSound(this.minecraft);
        this.placeholders.clear();
        int size = PLACEHOLDER_ROWS * COLUMNS;
        for (int i = 0; i < size; i++) {
            this.placeholders.add(ItemStack.EMPTY);
        }

        List<ItemStack> saved = ContainerUtil.getCurrentPlaceholderStacks();
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
     * Closes the filtered item screen properly.
     */
    @Override
    public void onClose() {
        ContainerUtil.setCurrentPlaceholderStacks(this.placeholders);
        ContainerUtil.RETURNING_FROM_PLACEHOLDER_SCREEN = true;
        this.minecraft.setScreen(this.parentScreen);
        playButtonSound(this.minecraft);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int panelX() {
        return (this.width - GUI_WIDTH) / 2;
    }

    private int panelY() {
        return (this.height - GUI_HEIGHT) / 2;
    }

    private int sourceStartY() {
        return this.panelY() + INVENTORY_START_Y;
    }

    private int placeholderStartY() {
        return this.panelY() + 18;
    }

    private int sourceSlotAt(int mouseX, int mouseY) {
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

    private int placeholderSlotAt(int mouseX, int mouseY) {
        int startX = this.panelX() + 8;
        int startY = this.placeholderStartY();
        for (int row = 0; row < PLACEHOLDER_ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int x = startX + col * SLOT_SIZE;
                int y = startY + row * SLOT_SIZE;
                if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                    return row * COLUMNS + col;
                }
            }
        }
        return -1;
    }

    private ItemStack sourceStackAt(int sourceIndex) {
        int slotIndexInMenu = this.parentScreen.getMenu().slots.size() - 36 + sourceIndex;
        Slot slot = this.parentScreen.getMenu().getSlot(slotIndexInMenu);
        return slot.getItem();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (super.mouseClicked(event, isDouble)) {
            return true;
        }

        int mouseX = this.lastMouseX;
        int mouseY = this.lastMouseY;

        int sourceSlot = this.sourceSlotAt(mouseX, mouseY);
        if (sourceSlot >= 0 && event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            ItemStack stack = this.sourceStackAt(sourceSlot);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                copy.setCount(1);
                this.selectedStack = copy;
                this.selectedSourceSlot = sourceSlot;
                playButtonSound(this.minecraft);
            }
            return true;
        }

        int placeholderSlot = this.placeholderSlotAt(mouseX, mouseY);
        if (placeholderSlot >= 0) {
            if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && !this.placeholders.get(placeholderSlot).isEmpty()) {
                this.placeholders.set(placeholderSlot, ItemStack.EMPTY);
                playButtonSound(this.minecraft);
                return true;
            }
            if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
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

        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (!this.selectedStack.isEmpty()) {
                playButtonSound(this.minecraft);
            }
            this.selectedStack = ItemStack.EMPTY;
            this.selectedSourceSlot = -1;
            return true;
        }

        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE || input.key() == key(Minecraft.getInstance().options.keyInventory).getValue()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;

        int panelX = this.panelX();
        int panelY = this.panelY();
        int startX = panelX + 8;
        int placeholderStartY = this.placeholderStartY();
        int sourceStartY = this.sourceStartY();
        int hotbarY = panelY + HOTBAR_Y;

        // Match vanilla GenericContainerScreen rendering to avoid slot/background offset drift.
        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_TEXTURE, panelX, panelY, 0, 0, GUI_WIDTH, TOP_BACKGROUND_HEIGHT, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_TEXTURE, panelX, panelY + TOP_BACKGROUND_HEIGHT, 0, 126, GUI_WIDTH, BOTTOM_BACKGROUND_HEIGHT, 256, 256);

        graphics.text(this.font, Component.translatable("qualityofqueso.gui.placeholder_editor"), panelX + 8, panelY + 6, ModTexts.TEXT_COLOR, false);
        graphics.text(this.font, Component.translatable("container.inventory"), panelX + 8, panelY + (GUI_HEIGHT - 94), ModTexts.TEXT_COLOR, false);

        for (int row = 0; row < PLACEHOLDER_ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int slot = row * COLUMNS + col;
                int x = startX + col * SLOT_SIZE;
                int y = placeholderStartY + row * SLOT_SIZE;

                ItemStack stack = this.placeholders.get(slot);
                if (!stack.isEmpty()) {
                    graphics.fakeItem(stack, x, y);
                    graphics.itemDecorations(this.font, stack, x, y);
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
                graphics.fakeItem(stack, x, y);
                graphics.itemDecorations(this.font, stack, x, y);
            }
        }

        int hoveredSource = this.sourceSlotAt(mouseX, mouseY);
        if (hoveredSource >= 0) {
            ItemStack stack = this.sourceStackAt(hoveredSource);
            if (!stack.isEmpty()) {
                graphics.setTooltipForNextFrame(
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
                    graphics.setTooltipForNextFrame(
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
            graphics.fakeItem(this.selectedStack, mouseX - 8, mouseY - 8);
            graphics.itemDecorations(this.font, this.selectedStack, mouseX - 8, mouseY - 8);
        }

        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
    }
}