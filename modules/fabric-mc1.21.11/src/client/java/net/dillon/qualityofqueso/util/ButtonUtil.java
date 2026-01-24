package net.dillon.qualityofqueso.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.option.MoveItemsIf;
import net.dillon.qualityofqueso.option.screen.ModOptionsScreen;
import net.dillon.qualityofqueso.screen.gui.TransferButton;
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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
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
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

import static net.dillon.qualityofqueso.main.QoQ.options;
import static net.dillon.qualityofqueso.main.QoQ.quicklyEquippables;

/**
 * Utility class for handling, rendering and functioning buttons.
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
     * @return the {@code X} value for transferring query buttons.
     */
    public static int getManagementButtonX(Screen screen, int backgroundWidth, int width, int amount) {
        int barWidth = getBarWidth(backgroundWidth);
        int modifier = 18;
        if (isBrewingStandScreen(screen)) {
            modifier -= 36;
        } else if (screen instanceof RecipeBookScreen<?> recipeBookScreen && recipeBookScreen.recipeBook.isOpen()) {
            modifier += 77;
        }
        return (width / 2 + barWidth / 2 + modifier) - (amount * 12);
    }

    /**
     * @return the {@code y-value} for inventory management buttons.
     */
    public static int getManagementButtonY(Screen screen, Inventory inventory, int screenY, int titleY) {
        int y = 2 * inventory.size() + 12;
        if (isBrewingStandScreen(screen)) {
            y += 30;
        }
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
     * @return if the user is attempting to exclude slots.
     */
    public static boolean isExcludingSlots(HandledScreen<?> handledScreen) {
        return isValidScreen(handledScreen)
                && options().dragToSort
                && (MinecraftClient.getInstance().isAltPressed() || (MinecraftClient.getInstance().isShiftPressed() && MinecraftClient.getInstance().isAltPressed()))
                && handledScreen.focusedSlot != null
                && handledScreen.getScreenHandler().getCursorStack().isEmpty();
    }

    /**
     * @return if a fromSlot should be skipped.
     */
    public static boolean shouldSkipSlot(int slotId, Set<Integer> excludedSlots) {
        for (int id : excludedSlots) {
            if (slotId == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return if a checkedItem can be combined/merged.
     */
    private static boolean canCombine(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        if (!a.isStackable()) {
            return false;
        }
        if (!ItemStack.areItemsEqual(a, b)) {
            return false;
        }
        return ItemStack.areItemsAndComponentsEqual(a, b);
    }

    /**
     * @return {@code true} if the fromSlot is valid to move.
     */
    public static boolean isHotbarSlot(int totalSlots, int slotIndex) {
        // If we're in the last 9 slots and hotbar is disabled, return false
        return slotIndex >= totalSlots - 9;
    }

    /**
     * @return {@code true} if the fromSlot is a hotbar fromSlot in the fromInventory.
     */
    public static boolean isInventoryHotbarSlot(boolean isInventoryScreen, int slotIndex) {
        // Index 36-44 are hotbar slots in INVENTORY screen.
        return isInventoryScreen && slotIndex <= 44 && slotIndex >= 36;
    }

    /**
     * @return {@code true} if the fromSlot should be excluded entirely.
     */
    public static boolean isExcludedSlot(Screen screen, int slotIndex) {
        // Index 5-8 are armor slots. Index 45 is offhand fromSlot. NEVER drop those items.
        return screen instanceof InventoryScreen && (slotIndex <= 8 && slotIndex >= 5 || slotIndex == 45);
    }

    /**
     * @return {@code true} if any fromSlot (with the range provided) is filled.
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
     * @return {@code true} if the container (or inventory) is full, including/excluding hotbar slots.
     */
    public static boolean isContainerFull(ScreenHandler handler, Inventory inv, boolean inventory) {
        var options = options();

        int containerSize = getContainerSize(inv);

        int inventorySize = options.includeHotbar ? 36 : 27;
        int inventoryEnd = containerSize + inventorySize;

        int fromStart = inventory ? containerSize : 0;
        int fromEnd = inventory ? inventoryEnd : containerSize;

        int toStart = inventory ? 0 : containerSize;
        int toEnd = inventory ? containerSize : inventoryEnd;

        int checkStart = inventory ? containerSize : 0;
        int checkEnd = inventory ? inventoryEnd : containerSize;

        int filledSlots = 0;
        Map<Object, Integer> componentFreeSpace = new HashMap<>();
        for (int i = checkStart; i < checkEnd; i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isEmpty()) {
                continue;
            }

            int free = stack.getMaxCount() - stack.getCount();
            if (free > 0) {
                componentFreeSpace.merge(stack.getComponents(), free, Math::max);
            }
        }

        for (int i = fromStart; i < fromEnd; i++) {
            Slot fromSlot = handler.getSlot(i);
            ItemStack fromItem = fromSlot.getStack();

            if (fromSlot.hasStack()) {
                filledSlots++;
            }

            if (fromItem.isEmpty() || options.moveItemsIf.containerIsntFilled()) {
                continue;
            }

            for (int k = toStart; k < toEnd; k++) {
                ItemStack toItem = handler.getSlot(k).getStack();
                if (toItem.isEmpty()) {
                    continue;
                }

                if (options.moveItemsIf == MoveItemsIf.CAN_MOVE_AT_ALL) {
                    if (ItemStack.areItemsEqual(toItem, fromItem)) {
                        Integer free = componentFreeSpace.get(toItem.getComponents());
                        if (free != null && free > 0) {
                            return false;
                        }
                    }
                } else if (options.moveItemsIf == MoveItemsIf.LESS_THAN_MAX_STACK_SIZE) {
                    if (ItemStack.areItemsAndComponentsEqual(toItem, fromItem) && !(toItem.getCount() + fromItem.getCount() > toItem.getMaxCount())) {
                        return false;
                    }
                }
            }
        }

        return filledSlots == (inventory ? inventorySize : containerSize);
    }

//    Causes massive performance issues:
//    public static boolean isContainerFull(ScreenHandler handler, Inventory inv, boolean inventory) {
//        int j = 0;
//        int containerSize = getContainerSize(inv);
//        int size = options().includeHotbar ? 36 : 27;
//        for (int i = inventory ? containerSize : 0; i < (inventory ? containerSize + size : containerSize); i++) {
//            Slot fromSlot = handler.getSlot(i);
//            ItemStack fromItem = fromSlot.getStack();
//            if (!options().moveItemsIf.containerIsntFilled()) {
//                for (int k = inventory ? 0 : containerSize; k < (inventory ? containerSize : handler.slots.size()); k++) {
//                    Slot toSlot = handler.getSlot(k);
//                    ItemStack toItem = toSlot.getStack();
//
//                    for (int l = inventory ? containerSize : 0; l < (inventory ? getTotalSlots(handler) : containerSize); l++) {
//                        Slot checkedSlot = handler.getSlot(l);
//                        ItemStack checkedItem = checkedSlot.getStack();
//
//                        // Ensure components are equal
//                        if (!Objects.equals(checkedItem.getComponents(), toItem.getComponents())) {
//                            continue;
//                        }
//
//                        // Otherwise, go through fromSlot checks
//                        if (options().moveItemsIf == MoveItemsIf.CAN_MOVE_AT_ALL
//                                && ItemStack.areItemsEqual(toItem, fromItem)
//                                && !checkedItem.isEmpty()
//                                && checkedItem.getCount() < checkedItem.getMaxCount()) {
//                            return false;
//                        } else if (options().moveItemsIf == MoveItemsIf.LESS_THAN_MAX_STACK_SIZE
//                                && ItemStack.areItemsAndComponentsEqual(toItem, fromItem)
//                                && !(toItem.getCount() + fromItem.getCount() > toItem.getMaxCount())) {
//                            return false;
//                        }
//                    }
//                }
//            }
//            // If slot has stack, increment j
//            if (fromSlot.hasStack()) {
//                j++;
//            }
//        }
//        // If j == inventory/container size, then container is full, return true
//        return j == (inventory ? size : containerSize);
//    }

    /**
     * @return {@code true} if the hovered fromSlot has an query (assuming hovered fromSlot isn't {@code null}).
     */
    public static boolean hoveredSlotHasItem(Slot focusedSlot) {
        return focusedSlot != null && focusedSlot.getStack() != ItemStack.EMPTY;
    }

    /**
     * Handles smart-moving actions.
     */
    public static boolean canMoveCursorItem(ItemStack fromStack, ItemStack cursorStack) {
        boolean isEnchantedBook = cursorStack.isOf(Items.ENCHANTED_BOOK);
        boolean isPotion = cursorStack.isOf(Items.POTION) || cursorStack.isOf(Items.SPLASH_POTION) || cursorStack.isOf(Items.LINGERING_POTION);
        boolean isTippedArrow = cursorStack.isOf(Items.TIPPED_ARROW);
        boolean isFirework = cursorStack.isOf(Items.FIREWORK_ROCKET);

        if ((isEnchantedBook || isPotion || isTippedArrow || isFirework) && MinecraftClient.getInstance().isShiftPressed()) {
            return (isEnchantedBook && enchantmentMatches(fromStack, cursorStack))
                    || (isPotion && statusEffectMatches(fromStack, cursorStack))
                    || (isTippedArrow && statusEffectMatches(fromStack, cursorStack))
                    || (isFirework && flightDurationMatches(fromStack, cursorStack));
        }

        return fromStack.isOf(cursorStack.getItem());
    }

    /**
     * @return if a firework rockets flight duration is the same as cursor checkedItem's.
     */
    public static boolean flightDurationMatches(ItemStack slotStack, ItemStack cursorStack) {
        FireworksComponent slotDuration = slotStack.get(DataComponentTypes.FIREWORKS);
        FireworksComponent cursorDuration = cursorStack.get(DataComponentTypes.FIREWORKS);

        if (slotDuration == null || cursorDuration == null) {
            return false;
        }

        return slotDuration.flightDuration() == cursorDuration.flightDuration();
    }

    /**
     * @return if fromSlot potion effect matches any cursor's potion effects.
     */
    public static boolean statusEffectMatches(ItemStack slotStack, ItemStack cursorStack) {
        PotionContentsComponent slotEffects = slotStack.get(DataComponentTypes.POTION_CONTENTS);
        PotionContentsComponent cursorEffects = cursorStack.get(DataComponentTypes.POTION_CONTENTS);

        if (slotEffects == null || cursorEffects == null) {
            return false;
        }

        // Ensure no cross-over items
        if (!ItemStack.areItemsEqual(slotStack, cursorStack)) {
            return false;
        }

        for (StatusEffectInstance slotEffect : slotEffects.getEffects()) {
            for (StatusEffectInstance cursorEffect : cursorEffects.getEffects()) {
                if (slotEffect.getEffectType() == cursorEffect.getEffectType()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return if fromSlot enchantment matches cursor's enchantment.
     */
    public static boolean enchantmentMatches(ItemStack slotStack, ItemStack cursorStack) {
        ItemEnchantmentsComponent fromStackEnchantments = EnchantmentHelper.getEnchantments(slotStack);
        ItemEnchantmentsComponent cursorEnchantments = EnchantmentHelper.getEnchantments(cursorStack);
        for (RegistryEntry<Enchantment> slotEnchantments : fromStackEnchantments.getEnchantments()) {
            for (RegistryEntry<Enchantment> heldEnchantments : cursorEnchantments.getEnchantments()) {
                if (getEnchantmentName(slotEnchantments).contains(getEnchantmentName(heldEnchantments))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return enchantment name as a string.
     */
    public static String getEnchantmentName(RegistryEntry<Enchantment> enchantment) {
        return enchantment.value().description().getString().toLowerCase();
    }

    /**
     * @return current screen's cursor checkedItem.
     */
    public static ItemStack getCursorStack(HandledScreen<?> screen) {
        return screen.getScreenHandler().getCursorStack();
    }

    /**
     * Swaps all items in a container.
     */
    public static void swapItems(ScreenHandler handler, Inventory inventory, Set<Integer> excludedSlots) {
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

            // Skip player-chosen excluded slots
            boolean skip = false;
            for (int id : excludedSlots) {
                if (chestSlot.id == id || playerSlot.id == id) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

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
     * Sorts all items in a container.
     */
    @Unique
    public static void sortItems(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) {
            return;
        }
        if (!(client.currentScreen instanceof HandledScreen<?> screen)) {
            return;
        }

        ScreenHandler handler = screen.getScreenHandler();
        int totalSlots = handler.slots.size();
        int containerSize = totalSlots - 36;

        if (containerSize <= 0) {
            return;
        }

        // Merge all stacks
        for (int i = 0; i < containerSize; i++) {
            Slot source = handler.slots.get(i);
            if (!source.hasStack()) {
                continue;
            }

            ItemStack sourceStack = source.getStack();

            // Already full, skip
            if (sourceStack.getCount() >= sourceStack.getMaxCount()) {
                continue;
            }

            for (int j = i + 1; j < containerSize; j++) {
                Slot target = handler.slots.get(j);
                if (!target.hasStack()) {
                    continue;
                }

                ItemStack targetStack = target.getStack();

                if (!canCombine(sourceStack, targetStack)) {
                    continue;
                }

                // Pick up target
                clickSlot(client, handler, j);
                // Click source to merge
                clickSlot(client, handler, i);

                // If cursor still has items, put them back
                if (!client.player.currentScreenHandler.getCursorStack().isEmpty()) {
                    clickSlot(client, handler, j);
                }

                // Stop if source is now full
                if (source.getStack().getCount() >= sourceStack.getMaxCount()) {
                    break;
                }
            }
        }

        // Build list from LIVE slots AFTER merge
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < containerSize; i++) {
            ItemStack stack = handler.slots.get(i).getStack();
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
        }

        // Unified alphabetical sort (no stackability logic)
        stacks.sort(Comparator.comparing(
                stack -> {
                    return stack.getCustomName() != null ? stack.getCustomName().getString() : stack.getItemName().getString();
                }
        ));

        // Pad with empties
        while (stacks.size() < containerSize) {
            stacks.add(ItemStack.EMPTY);
        }

        // Perform swap-based sorting
        for (int target = 0; target < containerSize; target++) {
            ItemStack desired = stacks.get(target);
            ItemStack actual = handler.slots.get(target).getStack();

            if (ItemStack.areEqual(actual, desired)) {
                continue;
            }

            int source = findMatchingSlot(handler, desired, target, containerSize);
            if (source == -1) {
                continue;
            }

            swapSlots(client, handler, source, target);
        }
    }

    /**
     * Finds a fromSlot matching the target merging fromSlot.
     * @return the fromSlot index.
     */
    private static int findMatchingSlot(ScreenHandler handler, ItemStack target, int start, int limit) {
        if (target.isEmpty()) {
            return -1;
        }

        for (int i = start + 1; i < limit; i++) {
            ItemStack stack = handler.slots.get(i).getStack();
            if (ItemStack.areEqual(stack, target)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Calls {@link ButtonUtil#clickSlot(MinecraftClient, ScreenHandler, int)} to sort items.
     */
    private static void swapSlots(MinecraftClient client, ScreenHandler handler, int a, int b) {
        // Pick up A
        clickSlot(client, handler, a);
        // Pick up B (places A, picks up B)
        clickSlot(client, handler, b);
        // Place B into A
        clickSlot(client, handler, a);
    }

    /**
     * Performs the {@code click action} to sort items.
     */
    private static void clickSlot(MinecraftClient client, ScreenHandler handler, int slot) {
        client.interactionManager.clickSlot(
                handler.syncId,
                slot,
                0,
                SlotActionType.PICKUP,
                client.player
        );
    }

    /**
     * Sends a "swap fromSlot" click packet.
     */
    public static void sendSwapSlotPacket(int source, int index) {
        sendClickSlotPacket(source, SlotActionType.PICKUP);
        sendClickSlotPacket(index, SlotActionType.PICKUP);
        sendClickSlotPacket(source, SlotActionType.PICKUP);
    }

    /**
     * Sends a click fromSlot packet.
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
        if (options().dragToSort && MinecraftClient.getInstance().isAltPressed()) {
            return;
        }

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
    public static void renderSlotUnavailable(DrawContext context, Slot slot, boolean hotbar) {
        int color = hotbar ? -2139062148 : -1275068416;
        context.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, color, color);
    }

    /**
     * @return if a button is hovered and active.
     */
    public static boolean buttonHoveredAndActive(ButtonWidget button) {
        return button != null && button.isHovered() && button.active;
    }

    /**
     * @return if a button is hovered and active and shift is held.
     */
    public static boolean buttonHoveredActiveOrShiftHeld(HandledScreen<?> screen, ButtonWidget button, boolean inventory) {
        return buttonHoveredAndActive(button) || shiftHeld(screen, inventory);
    }

    /**
     * @return whether a fromSlot should be grayed out.
     */
    public static boolean shouldGrayout(HandledScreen<?> screen, TransferButton inventoryButton, TransferButton containerButton, TransferButton hotbarButton, Slot slot) {
        boolean shortcutKeyReady = isInventoryScreen(screen) ? MinecraftClient.getInstance().isCtrlPressed() && MinecraftClient.getInstance().isAltPressed() : MinecraftClient.getInstance().isCtrlPressed();
        return shortcutKeyReady
                || shiftHeld(screen, false)
                || (buttonHoveredAndActive(inventoryButton) && slot.hasStack())
                || buttonHoveredAndActive(containerButton)
                || (buttonHoveredAndActive(hotbarButton) && slot.hasStack());
    }

    /**
     * @return if shift is held and a fromSlot is hovered.
     */
    public static boolean shiftHeld(HandledScreen<?> screen, boolean inventory) {
        int totalSlots = getTotalSlots(screen.getScreenHandler());
        return (!options().dragToSort || !MinecraftClient.getInstance().isAltPressed())
                && MinecraftClient.getInstance().isShiftPressed()
                && screen.focusedSlot != null
                && screen.focusedSlot.hasStack()
                && (inventory ? screen.focusedSlot.id >= totalSlots - 36 : screen.focusedSlot.id <= totalSlots - 37);
    }

    /**
     * @return valid shulkerBoxScreen.
     */
    public static boolean isShulkerBoxScreen(Screen screen) {
        return screen instanceof ShulkerBoxScreen;
    }

    /**
     * @return valid handled screens which can use the container search feature.
     */
    public static boolean isContainerScreen(Screen screen) {
        return screen instanceof GenericContainerScreen || isShulkerBoxScreen(screen);
    }

    /**
     * @return valid brewingStandScreen.
     */
    public static boolean isBrewingStandScreen(Screen screen) {
        return screen instanceof BrewingStandScreen;
    }

    /**
     * @return valid fromInventory screen.
     */
    public static boolean isInventoryScreen(Screen screen) {
        return screen instanceof InventoryScreen;
    }

    /**
     * @return valid screen for mod to work.
     */
    public static boolean isValidScreen(Screen screen) {
        return isContainerScreen(screen) || isInventoryScreen(screen);
    }

    /**
     * @return valid creative fromInventory screen.
     */
    public static boolean isCreativeInventoryScreen(Screen screen) {
        return screen instanceof CreativeInventoryScreen;
    }
}