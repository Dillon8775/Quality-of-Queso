package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.helper.EnderChestHelper;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.dillon.qualityofqueso.helper.MethodHelper.key;

/**
 * Read-only preview of the persisted ender chest cache.
 */
public class EnderChestPreviewScreen extends Screen {
    private static final int SLOT_SIZE = 18;
    private static final int COLUMNS = 9;
    private static final int ROWS = 3;
    private static final int GUI_WIDTH = 176;
    private static final int TOP_BACKGROUND_HEIGHT = ROWS * 18 + 17;
    private static final int BOTTOM_CAP_HEIGHT = 7;
    private static final int BOTTOM_CAP_V = 126 + 96 - BOTTOM_CAP_HEIGHT;
    private static final int GUI_HEIGHT = TOP_BACKGROUND_HEIGHT + BOTTOM_CAP_HEIGHT;
    private static final ResourceLocation CHEST_TEXTURE = new ResourceLocation("minecraft:textures/gui/container/generic_54.png");

    private final List<ItemStack> slots = new ArrayList<>();

    public EnderChestPreviewScreen() {
        super(Component.translatable("qualityofqueso.gui.your_ender_chest"));
    }

    @Override
    protected void init() {
        this.slots.clear();
        for (int i = 0; i < COLUMNS * ROWS; i++) {
            this.slots.add(ItemStack.EMPTY);
        }

        List<ContainerData.StoredEnderChestStack> persisted = EnderChestHelper.getPersistedEnderChestItemsForCurrentWorld();
        int writeIndex = 0;
        for (ContainerData.StoredEnderChestStack stored : persisted) {
            if (writeIndex >= this.slots.size()) {
                break;
            }
            ItemStack stack = toItemStack(stored);
            if (stack.isEmpty()) {
                continue;
            }
            this.slots.set(writeIndex, stack);
            writeIndex++;
        }
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        if (keycode == GLFW.GLFW_KEY_ESCAPE || keycode == key(Minecraft.getInstance().options.keyInventory).getValue()) {
            this.onClose();
            return true;
        }
        return true;
    }

    @Override
    public boolean charTyped(char ch, int scancode) {
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int bl) {
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int bl) {
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int bl, double dx, double dy) {
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        int panelX = (this.width - GUI_WIDTH) / 2;
        int panelY = (this.height - GUI_HEIGHT) / 2;
        int slotStartX = panelX + 8;
        int slotStartY = panelY + 18;

        graphics.blit(CHEST_TEXTURE, panelX, panelY, 0, 0, GUI_WIDTH, TOP_BACKGROUND_HEIGHT, 256, 256);
        graphics.blit(CHEST_TEXTURE, panelX, panelY + TOP_BACKGROUND_HEIGHT, 0, BOTTOM_CAP_V, GUI_WIDTH, BOTTOM_CAP_HEIGHT, 256, 256);
        graphics.drawString(this.font, this.title, panelX + 8, panelY + 6, ModConstants.DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR, false);

        int hoveredSlot = hoveredSlotAt(mouseX, mouseY, slotStartX, slotStartY);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int slot = row * COLUMNS + col;
                ItemStack stack = this.slots.get(slot);
                if (stack.isEmpty()) {
                    continue;
                }

                int x = slotStartX + col * SLOT_SIZE;
                int y = slotStartY + row * SLOT_SIZE;
                graphics.renderFakeItem(stack, x, y);
                graphics.renderItemDecorations(this.font, stack, x, y);
            }
        }

        if (hoveredSlot >= 0) {
            ItemStack stack = this.slots.get(hoveredSlot);
            if (!stack.isEmpty()) {
                graphics.renderTooltip(
                        this.font,
                        stack.getTooltipLines(
                                this.minecraft.player,
                                this.minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL
                        ),
                        Optional.empty(),
                        mouseX,
                        mouseY
                );
            }
        }
    }

    private int hoveredSlotAt(int mouseX, int mouseY, int slotStartX, int slotStartY) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int x = slotStartX + col * SLOT_SIZE;
                int y = slotStartY + row * SLOT_SIZE;
                if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                    return row * COLUMNS + col;
                }
            }
        }
        return -1;
    }

    private static ItemStack toItemStack(ContainerData.StoredEnderChestStack stored) {
        if (stored == null || stored.itemId == null || stored.itemId.isBlank() || stored.count <= 0) {
            return ItemStack.EMPTY;
        }

        try {
            ResourceLocation id = new ResourceLocation(stored.itemId);
            Optional<Item> item = BuiltInRegistries.ITEM.getOptional(id);
            if (item.isEmpty()) {
                return ItemStack.EMPTY;
            }
            return new ItemStack(item.get(), stored.count);
        } catch (Exception ignored) {
            return ItemStack.EMPTY;
        }
    }
}
