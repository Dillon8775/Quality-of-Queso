package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.helper.EnderChestHelper;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    private static final Identifier CHEST_TEXTURE = Identifier.parse("minecraft:textures/gui/container/generic_54.png");

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
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE || input.key() == key(Minecraft.getInstance().options.keyInventory).getValue()) {
            this.onClose();
            return true;
        }
        return true;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        int panelX = (this.width - GUI_WIDTH) / 2;
        int panelY = (this.height - GUI_HEIGHT) / 2;
        int slotStartX = panelX + 8;
        int slotStartY = panelY + 18;

        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_TEXTURE, panelX, panelY, 0, 0, GUI_WIDTH, TOP_BACKGROUND_HEIGHT, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_TEXTURE, panelX, panelY + TOP_BACKGROUND_HEIGHT, 0, BOTTOM_CAP_V, GUI_WIDTH, BOTTOM_CAP_HEIGHT, 256, 256);
        graphics.text(this.font, this.title, panelX + 8, panelY + 6, ModConstants.DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR, false);

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
                graphics.fakeItem(stack, x, y);
                graphics.itemDecorations(this.font, stack, x, y);
            }
        }

        if (hoveredSlot >= 0) {
            ItemStack stack = this.slots.get(hoveredSlot);
            if (!stack.isEmpty()) {
                graphics.setTooltipForNextFrame(
                        this.font,
                        stack.getTooltipLines(
                                Item.TooltipContext.EMPTY,
                                this.minecraft.player,
                                this.minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL
                        ),
                        Optional.empty(),
                        mouseX,
                        mouseY
                );
            }
        }

        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
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
            Identifier id = Identifier.parse(stored.itemId);
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
