package net.dillon.qualityofqueso.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.ModOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static net.dillon.qualityofqueso.main.QoQ.quicklyEquippables;

/**
 * Utility class.
 */
@OnlyIn(Dist.CLIENT)
public class ButtonUtil {
    public static final String CHEESE_WHEEL = "cheese_wheel";
    public static final String ENABLED_TEXTURE = "qoq_enabled";
    public static final String DISABLED_TEXTURE = "qoq_disabled";

    /**
     * Initializes the settings button.
     */
    public static SpriteIconButton initializeButton(Minecraft client, Screen parent) {
        return SpriteIconButton.builder(ModTexts.BLANK, (onPress) -> client.setScreen(new ModOptionsScreen(parent)), false)
                .width(20)
                .sprite(ResourceLocation.fromNamespaceAndPath("qualityofqueso", "cheese_wheel"), 16, 16)
                .build();
    }

    /**
     * Draws a tooltip.
     */
    public static void drawTooltip(Component tooltip, GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        graphics.renderTooltip(font, font.split(tooltip, 200), mouseX, mouseY);
    }

    /**
     * Draws a texture over a button.
     */
    public static void drawTexture(GuiGraphics graphics, String name, Button button, float f) {
        graphics.blit(ResourceLocation.parse("qualityofqueso:textures/gui/" + name + ".png"), button.getX() + 2, button.getY() + 2, 0.0F, 0.0F, 16, 16, 16, 16);
    }

    public static void drawTexture(GuiGraphics graphics, String name, Button button) {
        drawTexture(graphics, name, button, 1.0F);
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
    public static int getButtonX(Button button) {
        return button.getX();
    }

    /**
     * @return the {@code X} value for transferring query buttons.
     */
    public static int getManagementButtonX(Screen screen, int backgroundWidth, int width) {
        int barWidth = getBarWidth(backgroundWidth);
        int modifier = 18;
        if (screen instanceof InventoryScreen recipeBookScreen) {
            modifier = 42;
            if (recipeBookScreen.getRecipeBookComponent().isVisible()) {
                modifier = 119;
            }
        }
        return width / 2 + barWidth / 2 + modifier;
    }

    /**
     * @return the {@code y-value} for inventory management buttons.
     */
    public static int getManagementButtonY(Screen screen, Container inventory, int screenY, int titleY) {
        int y = 2 * inventory.getContainerSize() + 12;
        return screenY + titleY + (screen instanceof InventoryScreen ? 64 : y);
    }

    /**
     * @return the current {@code container size.}
     */
    public static int getContainerSize(Container inventory) {
        return inventory.getContainerSize();
    }

    /**
     * @return the total size of the {@code screen's slots.}
     */
    public static int getTotalSlots(AbstractContainerMenu handler) {
        return handler.slots.size();
    }

    /**
     * @return the fromInventory (size) that should be searched.
     */
    public static int getInventorySize(AbstractContainerMenu handler, Container inventory) {
        return ModClientOptions.SEARCH_INVENTORY.get() ? handler.slots.size() : inventory.getContainerSize();
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
    public static boolean isAnySlotFilled(AbstractContainerMenu handler, boolean checkHotbar, int start, int end) {
        for (int i = start; i < (ModClientOptions.INCLUDE_HOTBAR.get() && checkHotbar ? end + 9 : end); i++) {
            Slot slot = handler.getSlot(i);
            if (slot.hasItem()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@code true} if the container (or inventory) is full.
     */
    public static boolean isContainerFull(AbstractContainerMenu handler, Container inv, boolean inventory) {
        int j = 0;
        int containerSize = getContainerSize(inv);
        for (int i = inventory ? containerSize : 0; i < (inventory ? containerSize + 36 : containerSize); i++) {
            Slot slot = handler.getSlot(i);
            if (slot.hasItem()) {
                j++;
            }
        }
        return j == (inventory ? 36 : containerSize);
    }

    /**
     * @return {@code true} if the hovered slot has an query (assuming hovered slot isn't {@code null}).
     */
    public static boolean hoveredSlotHasItem(Slot focusedSlot) {
        return focusedSlot != null && focusedSlot.getItem() != ItemStack.EMPTY;
    }

    /**
     * Swaps all items in a container.
     */
    public static void swapItems(AbstractContainerMenu handler, Container inventory) {
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

            if (!ModClientOptions.INCLUDE_HOTBAR.get() && isHotbarSlot(handler.slots.size(), playerSlot.index)) {
                continue;
            }

            ItemStack chestStack = chestSlot.getItem();
            ItemStack playerStack = playerSlot.getItem();

            // Ignore if both slots are empty
            if (chestStack.isEmpty() && playerStack.isEmpty()) {
                continue;
            }

            sendSwapSlotPacket(playerSlot.index, chestSlot.index);
        }
    }

    /**
     * Sends a "swap slot" click packet.
     */
    public static void sendSwapSlotPacket(int source, int index) {
        sendClickSlotPacket(source, ClickType.PICKUP);
        sendClickSlotPacket(index, ClickType.PICKUP);
        sendClickSlotPacket(source, ClickType.PICKUP);
    }

    /**
     * Sends a click slot packet.
     */
    public static void sendClickSlotPacket(int slotIndex, ClickType clickType) {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener networkHandler = client.getConnection();

        if (client.player == null || networkHandler == null || client.player.containerMenu == null) {
            return;
        }

        var handler = client.player.containerMenu;
        int syncId = handler.containerId;
        int stateId = handler.getStateId();

        ItemStack carriedStack = handler.getCarried();
        ItemStack clickedStack = handler.getSlot(slotIndex).getItem();

        if (clickType == ClickType.THROW) {
            if (!handler.getSlot(slotIndex).getItem().isEmpty()) {
                client.gameMode.handleInventoryMouseClick(syncId, slotIndex, 1, clickType, client.player);
            }
            return;
        }

        Int2ObjectOpenHashMap<ItemStack> modifiedStacks = new Int2ObjectOpenHashMap<>();
        modifiedStacks.put(slotIndex, clickedStack);

        ServerboundContainerClickPacket packet = new ServerboundContainerClickPacket(
                syncId,
                stateId,
                (short) slotIndex,
                (byte) 0,
                clickType,
                carriedStack,
                modifiedStacks
        );

        networkHandler.send(packet);
    }

    /**
     * @return {@code true} if the hovered item is a {@code quickly equippable item.}
     */
    public static boolean isQuicklyEquippable(ItemStack stack) {
        for (TagKey<Item> quicklyEquippable : quicklyEquippables.keySet()) {
            if (stack.is(quicklyEquippable) || stack.is(Items.ELYTRA)) {
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
        if (ModClientOptions.QUICK_EQUIP.get() && focusedSlot != null && (focusedSlot.index >= 5) && (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen)) {
            ItemStack stack = focusedSlot.getItem();
            EquipmentSlot targetSlot = null;

            for (TagKey<Item> quicklyEquippable : quicklyEquippables.keySet()) {
                if (stack.is(quicklyEquippable)) {
                    targetSlot = quicklyEquippables.get(quicklyEquippable);
                }
            }

            if (stack.is(Items.ELYTRA)) {
                targetSlot = EquipmentSlot.CHEST;
            }

            if (targetSlot != null) {
                Minecraft client = Minecraft.getInstance();
                ItemStack equippedStack = client.player.getItemBySlot(targetSlot);
                if (equippedStack.isEmpty()) {
                    sendClickSlotPacket(focusedSlot.index, ClickType.QUICK_MOVE);
                } else {
                    quickSwap(focusedSlot.index, targetSlot);
                }
            }
        }
    }

    /**
     * Grays out a containerSlot.
     */
    public static void makeSlotUnavailable(GuiGraphics graphics, Slot slot, boolean hotbar) {
        int color = hotbar ? -2139062148 : -1275068416;
        graphics.fillGradient(RenderType.guiOverlay(), slot.x, slot.y, slot.x + 16, slot.y + 16, color, color, 0);
    }

    /**
     * @return valid handled screens which can use the container search feature.
     */
    public static boolean isContainerScreen(Screen screen) {
        return screen instanceof ContainerScreen || screen instanceof ShulkerBoxScreen;
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
        return screen instanceof CreativeModeInventoryScreen;
    }
}