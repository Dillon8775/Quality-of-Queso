package net.dillon.qualityofqueso.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.option.ModOptionsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import static net.dillon.qualityofqueso.main.QoQ.options;
import static net.dillon.qualityofqueso.main.QoQ.quicklyEquippables;

/**
 * Utility class for handling buttons.
 */
@Environment(EnvType.CLIENT)
public class ButtonUtil {
    public static final String ENABLED_TEXTURE = "qoq_enabled";
    public static final String DISABLED_TEXTURE = "qoq_disabled";

    /**
     * Initializes the settings button.
     */
    public static TextIconButtonWidget initializeButton(MinecraftClient client, Screen parent) {
        return TextIconButtonWidget.builder(ModTexts.BLANK, (onPress) -> client.setScreen(new ModOptionsScreen(parent)), false)
                .width(20)
                .texture(Identifier.of("qualityofqueso", "cheese_wheel"), 16, 16)
                .build();
    }

    /**
     * Draws a tooltip.
     */
    public static void drawTooltip(Text tooltip, DrawContext context, TextRenderer renderer, int mouseX, int mouseY) {
        context.drawOrderedTooltip(renderer, renderer.wrapLines(tooltip, 200), mouseX, mouseY);
    }

    /**
     * Draws a texture over a button.
     */
    public static void drawTexture(DrawContext context, String name, ButtonWidget button, float f) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("qualityofqueso:textures/gui/" + name + ".png"), button.getX() + 2, button.getY() + 2, 0.0F, 0.0F, 16, 16, 16, 16, ColorHelper.withAlpha(f, Colors.WHITE));
    }

    /**
     * Draws the texture for a {@code inventory management button.}
     */
    public static void drawButtonTexture(DrawContext context, String name, ButtonWidget button) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("qualityofqueso:textures/gui/" + name + ".png"), button.getX() - 1, button.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
    }

    /**
     * Draws a texture over a button without a custom fade.
     */
    public static void drawTexture(DrawContext context, String name, ButtonWidget button) {
        drawTexture(context, name, button, 1.0F);
    }

    /**
     * @return A special int to get the bar width.
     */
    public static int getBarWidth(int backgroundWidth) {
        return (int) ((double) backgroundWidth * 0.6);
    }

    /**
     * @return the {@code x-value} for the specified button.
     */
    public static int getButtonX(ButtonWidget button) {
        return button.getX();
    }

    /**
     * @return the {@code X} value for transferring query buttons.
     */
    public static int getManagementButtonX(Screen screen, int backgroundWidth, int width) {
        int barWidth = getBarWidth(backgroundWidth);
        int modifier = 18;
        if (screen instanceof RecipeBookScreen<?> recipeBookScreen) {
            modifier = 42;
            if (recipeBookScreen.recipeBook.isOpen()) {
                modifier = 119;
            }
        }
        return width / 2 + barWidth / 2 + modifier;
    }

    /**
     * @return the {@code y-value} for inventory management buttons.
     */
    public static int getManagementButtonY(Screen screen, Inventory inventory, int screenY, int titleY) {
        int y = 2 * inventory.size() + 12;
        return screenY + titleY + (screen instanceof InventoryScreen ? 64 : y);
    }

    /**
     * @return the current {@code container size.}
     */
    public static int getContainerSize(Inventory inventory) {
        return inventory.size();
    }

    /**
     * @return the total size of the {@code screen's slots.}
     */
    public static int getTotalSlots(ScreenHandler handler) {
        return handler.slots.size();
    }

    /**
     * @return the fromInventory (size) that should be searched.
     */
    public static int getInventorySize(ScreenHandler handler, Inventory inventory) {
        return options().searchInventory ? handler.slots.size() : inventory.size();
    }

    /**
     * @return {@code true} if the slot is valid to move.
     */
    public static boolean isHotbarSlot(int totalSlots, int slotIndex) {
        // If we're in the last 9 slots and hotbar is disabled, return false
        return slotIndex >= totalSlots - 9;
    }

    /**
     * @return {@code true} if the slot is a hotbar slot in the fromInventory.
     */
    public static boolean isInventoryHotbarSlot(boolean isInventoryScreen, int slotIndex) {
        // Index 36-44 are hotbar slots in INVENTORY screen.
        return isInventoryScreen && slotIndex <= 44 && slotIndex >= 36;
    }

    /**
     * @return {@code true} if the slot should be excluded entirely.
     */
    public static boolean isExcludedSlot(Screen screen, int slotIndex) {
        // Index 5-8 are armor slots. Index 45 is offhand slot. NEVER drop those items.
        return screen instanceof InventoryScreen && (slotIndex <= 8 && slotIndex >= 5 || slotIndex == 45);
    }

    /**
     * @return {@code true} if any slot (with the range provided) is filled.
     * <p>{@code default start = 9, default end = 36}</p>
     */
    public static boolean isAnySlotFilled(ScreenHandler handler, boolean checkHotbar, int start, int end) {
        for (int i = start; i < (options().includeHotbar && checkHotbar ? end + 9 : end); i++) {
            Slot slot = handler.getSlot(i);
            if (slot.hasStack()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@code true} if the container (or inventory) is full.
     */
    public static boolean isContainerFull(ScreenHandler handler, Inventory inv, boolean inventory) {
        int j = 0;
        int containerSize = getContainerSize(inv);
        int size = options().includeHotbar ? 36 : 27;
        for (int i = inventory ? containerSize : 0; i < (inventory ? containerSize + size : containerSize); i++) {
            Slot slot = handler.getSlot(i);
            if (slot.hasStack()) {
                j++;
            }
        }
        return j == (inventory ? size : containerSize);
    }

    /**
     * @return {@code true} if the hovered slot has an query (assuming hovered slot isn't {@code null}).
     */
    public static boolean hoveredSlotHasItem(Slot focusedSlot) {
        return focusedSlot != null && focusedSlot.getStack() != ItemStack.EMPTY;
    }

    /**
     * Swaps all items in a container.
     */
    public static void swapItems(ScreenHandler handler, Inventory inventory) {
        int totalSlots = getTotalSlots(handler);
        int containerSize = getContainerSize(inventory);
        int offset = totalSlots - 27 - 9;

        int swaps = Math.min(totalSlots - containerSize, containerSize);

        for (int i = 0; i < swaps; i++) {
            int playerSlotIndex = i + offset;

            // Make sure both exist
            if (i >= totalSlots || playerSlotIndex >= totalSlots) {
                continue;
            }

            Slot chestSlot = handler.getSlot(i);
            Slot playerSlot = handler.getSlot(playerSlotIndex);

            if (!options().includeHotbar && isHotbarSlot(handler.slots.size(), playerSlot.id)) {
                continue;
            }

            ItemStack chestStack = chestSlot.getStack();
            ItemStack playerStack = playerSlot.getStack();

            // Ignore if both slots are empty
            if (chestStack.isEmpty() && playerStack.isEmpty()) {
                continue;
            }

            sendSwapSlotPacket(playerSlot.id, chestSlot.id);
        }
    }

    /**
     * Sends a "swap slot" click packet.
     */
    public static void sendSwapSlotPacket(int source, int index) {
        sendClickSlotPacket(source, SlotActionType.PICKUP);
        sendClickSlotPacket(index, SlotActionType.PICKUP);
        sendClickSlotPacket(source, SlotActionType.PICKUP);
    }

    /**
     * Sends a click slot packet.
     */
    public static void sendClickSlotPacket(int slotIndex, SlotActionType slotActionType) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (client.player == null || networkHandler == null || client.player.currentScreenHandler == null) {
            return;
        }

        var handler = client.player.currentScreenHandler;
        int syncId = handler.syncId;
        int revision = handler.getRevision();

        ItemStack cursorStack = handler.getCursorStack() ;
        ItemStack clickedStack = handler.getSlot(slotIndex).getStack();

        if (slotActionType == SlotActionType.THROW) {
            if (!handler.getSlot(slotIndex).getStack().isEmpty()) {
                client.interactionManager.clickSlot(syncId, slotIndex, MinecraftClient.getInstance().isShiftPressed() ? 0 : 1, slotActionType, client.player);
            }
            return;
        }

        ComponentChangesHash.ComponentHasher hasher = networkHandler.getComponentHasher();

        ItemStackHash cursorHash = ItemStackHash.fromItemStack(cursorStack, hasher);
        ItemStackHash clickedHash = ItemStackHash.fromItemStack(clickedStack, hasher);

        Int2ObjectOpenHashMap<ItemStackHash> modifiedStacks = new Int2ObjectOpenHashMap<>();
        modifiedStacks.put(slotIndex, clickedHash);

        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(
                syncId,
                revision,
                (short) slotIndex,
                (byte) 0,
                slotActionType,
                modifiedStacks,
                cursorHash
        );

        networkHandler.sendPacket(packet);
    }

    /**
     * @return {@code true} if the hovered item is a {@code quickly equippable item.}
     */
    public static boolean isQuicklyEquippable(ItemStack stack) {
        for (TagKey<Item> quicklyEquippable : quicklyEquippables.keySet()) {
            if (stack.isIn(quicklyEquippable) || stack.isOf(Items.ELYTRA)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Quickly swaps two items in the player's fromInventory.
     */
    public static void quickSwap(int sourceSlot, EquipmentSlot slot) {
        // Slot index for armor - see PlayerScreenHandler for proof of these values
        int slotIndex = slot == EquipmentSlot.HEAD ? 5 : slot == EquipmentSlot.CHEST ? 6 : slot == EquipmentSlot.LEGS ? 7 : slot == EquipmentSlot.FEET ? 8 : 6;
        sendSwapSlotPacket(sourceSlot, slotIndex);
    }

    /**
     * Quickly equips an item.
     */
    public static void quickEquip(Screen screen, Slot focusedSlot) {
        if (options().quickEquip && focusedSlot != null && (focusedSlot.id >= 5) && (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen)) {
            ItemStack stack = focusedSlot.getStack();
            EquipmentSlot targetSlot = null;

            for (TagKey<Item> quicklyEquippable : quicklyEquippables.keySet()) {
                if (stack.isIn(quicklyEquippable)) {
                    targetSlot = quicklyEquippables.get(quicklyEquippable);
                }
            }

            if (stack.isOf(Items.ELYTRA)) {
                targetSlot = EquipmentSlot.CHEST;
            }

            if (targetSlot != null) {
                MinecraftClient client = MinecraftClient.getInstance();
                ItemStack equippedStack = client.player.getEquippedStack(targetSlot);
                if (equippedStack.isEmpty()) {
                    sendClickSlotPacket(focusedSlot.id, SlotActionType.QUICK_MOVE);
                } else {
                    quickSwap(focusedSlot.id, targetSlot);
                }
            }
        }
    }

    /**
     * Grays out a containerSlot.
     */
    public static void makeSlotUnavailable(DrawContext context, Slot slot, boolean hotbar) {
        int color = hotbar ? -2139062148 : -1275068416;
        context.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, color, color);
    }

    /**
     * @return valid handled screens which can use the container search feature.
     */
    public static boolean isContainerScreen(Screen screen) {
        return screen instanceof GenericContainerScreen || screen instanceof ShulkerBoxScreen;
    }

    /**
     * @return valid fromInventory screen.
     */
    public static boolean isInventoryScreen(Screen screen) {
        return screen instanceof InventoryScreen;
    }

    /**
     * @return valid creative fromInventory screen.
     */
    public static boolean isCreativeInventoryScreen(Screen screen) {
        return screen instanceof CreativeInventoryScreen;
    }
}