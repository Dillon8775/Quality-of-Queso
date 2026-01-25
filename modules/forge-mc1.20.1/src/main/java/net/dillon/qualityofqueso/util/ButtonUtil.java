package net.dillon.qualityofqueso.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.option.MoveItemsIf;
import net.dillon.qualityofqueso.option.screen.ModOptionsScreen;
import net.dillon.qualityofqueso.screen.gui.TransferButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

import static net.dillon.qualityofqueso.main.QoQ.*;

/**
 * Utility class.
 */
@OnlyIn(Dist.CLIENT)
public class ButtonUtil {
    public static final String ENABLED_TEXTURE = "qoq_enabled";
    public static final String DISABLED_TEXTURE = "qoq_disabled";

    /**
     * Initializes the settings button.
     */
    public static Button initializeButton(Minecraft client, Screen parent, int width, int height) {
        return new ImageButton(width, height, 20, 20, 0, 0, 20, ResourceLocation.parse("qualityofqueso:textures/gui/cheese_wheel.png"), 20, 40, (p_280835_) -> {
            client.setScreen(new ModOptionsScreen(parent));
        }, ModTexts.BLANK);
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

    /**
     * Draws the texture for a {@code inventory management button.}
     */
    public static void drawButtonTexture(GuiGraphics graphics, String name, Button button) {
        graphics.blit(ResourceLocation.parse("qualityofqueso:textures/gui/" + name + ".png"), button.getX() - 1, button.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
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
     * @return the {@code X} value for transferring query buttons.
     */
    public static int getManagementButtonX(Screen screen, int backgroundWidth, int width, int amount) {
        int barWidth = getBarWidth(backgroundWidth);
        int modifier = 18;
        if (isBrewingStandScreen(screen)) {
            modifier -= 36;
        } else if (isFurnaceScreen(screen)) {
            modifier -= 16;
        } else if (screen instanceof InventoryScreen recipeBookScreen && recipeBookScreen.getRecipeBookComponent().isVisible()) {
            modifier += 77;
        }
        return (width / 2 + barWidth / 2 + modifier) - (amount * 12);
    }

    /**
     * @return the {@code y-value} for inventory management buttons.
     */
    public static int getManagementButtonY(Screen screen, Container inventory, int screenY, int titleY) {
        int y = 2 * (inventory == null ? 0 : inventory.getContainerSize()) + 12;
        if (isBrewingStandScreen(screen)) {
            y += 30;
        } else if (isFurnaceScreen(screen)) {
            y += 14;
        }
        return screenY + titleY + (screen instanceof InventoryScreen ? 64 : y);
    }

    /**
     * @return the current {@code container size.}
     */
    public static int getContainerSize(Container inventory) {
        return inventory == null ? 0 : inventory.getContainerSize();
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
        return options().searchInventory ? handler.slots.size() : inventory == null ? 0 : inventory.getContainerSize();
    }

    /**
     * @return if the user is attempting to exclude slots.
     */
    public static boolean isExcludingSlots(AbstractContainerScreen<?> handledScreen) {
        return isValidScreen(handledScreen)
                && options().dragToSort
                && (Screen.hasAltDown() || (Screen.hasShiftDown() && Screen.hasAltDown()))
                && handledScreen.hoveredSlot != null
                && handledScreen.getMenu().getCarried().isEmpty();
    }

    /**
     * @return if a slot should be skipped.
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
     * @return if a stack can be combined/merged.
     */
    private static boolean canCombine(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        if (!a.isStackable()) {
            return false;
        }
        if (!ItemStack.isSameItem(a, b)) {
            return false;
        }
        return ItemStack.isSameItemSameTags(a, b);
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
        for (int i = start; i < (options().includeHotbar && checkHotbar ? end + 9 : end); i++) {
            Slot slot = handler.getSlot(i);
            if (slot.hasItem()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@code true} if the container (or inventory) is full, including/excluding hotbar slots.
     */
    public static boolean isContainerFull(AbstractContainerMenu handler, Container inv, boolean inventory) {
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
            ItemStack stack = handler.getSlot(i).getItem();
            if (stack.isEmpty()) {
                continue;
            }

            int free = stack.getMaxStackSize() - stack.getCount();
            if (free > 0) {
                componentFreeSpace.merge(stack.getTag(), free, Math::max);
            }
        }

        for (int i = fromStart; i < fromEnd; i++) {
            Slot fromSlot = handler.getSlot(i);
            ItemStack fromItem = fromSlot.getItem();

            if (fromSlot.hasItem()) {
                filledSlots++;
            }

            if (fromItem.isEmpty() || options.moveItemsIf.containerIsntFilled()) {
                continue;
            }

            for (int k = toStart; k < toEnd; k++) {
                ItemStack toItem = handler.getSlot(k).getItem();
                if (toItem.isEmpty()) {
                    continue;
                }

                if (options.moveItemsIf == MoveItemsIf.CAN_MOVE_AT_ALL) {
                    if (ItemStack.isSameItem(toItem, fromItem)) {
                        Integer free = componentFreeSpace.get(toItem.getTag());
                        if (free != null && free > 0) {
                            return false;
                        }
                    }
                } else if (options.moveItemsIf == MoveItemsIf.LESS_THAN_MAX_STACK_SIZE) {
                    if (ItemStack.isSameItemSameTags(toItem, fromItem) && !(toItem.getCount() + fromItem.getCount() > toItem.getMaxStackSize())) {
                        return false;
                    }
                }
            }
        }

        return filledSlots == (inventory ? inventorySize : containerSize);
    }

    /**
     * @return {@code true} if the hovered slot has an query (assuming hovered slot isn't {@code null}).
     */
    public static boolean hoveredSlotHasItem(Slot focusedSlot) {
        return focusedSlot != null && focusedSlot.getItem() != ItemStack.EMPTY;
    }

    /**
     * Handles smart-moving actions.
     */
    public static boolean canMoveCursorItem(ItemStack fromStack, ItemStack cursorStack) {
        boolean isEnchantedBook = cursorStack.is(Items.ENCHANTED_BOOK);
        boolean isPotion = cursorStack.is(Items.POTION) || cursorStack.is(Items.SPLASH_POTION) || cursorStack.is(Items.LINGERING_POTION);
        boolean isTippedArrow = cursorStack.is(Items.TIPPED_ARROW);
        boolean isFirework = cursorStack.is(Items.FIREWORK_ROCKET);

        if ((isEnchantedBook || isPotion || isTippedArrow || isFirework) && Screen.hasShiftDown()) {
            return (isEnchantedBook && enchantmentMatches(fromStack, cursorStack))
                    || (isPotion && statusEffectMatches(fromStack, cursorStack))
                    || (isTippedArrow && statusEffectMatches(fromStack, cursorStack))
                    || (isFirework && flightDurationMatches(fromStack, cursorStack));
        }

        return fromStack.is(cursorStack.getItem());
    }

    /**
     * @return if a firework rockets flight duration is the same as cursor stack's.
     */
    public static boolean flightDurationMatches(ItemStack slotStack, ItemStack cursorStack) {
        CompoundTag slotNbt = slotStack.getTag();
        CompoundTag cursorNbt = cursorStack.getTag();

        if (slotNbt == null || cursorNbt == null) {
            return false;
        }

        if (!slotNbt.contains("Fireworks") || !cursorNbt.contains("Fireworks")) {
            return false;
        }

        CompoundTag slotFireworks = slotNbt.getCompound("Fireworks");
        CompoundTag cursorFireworks = cursorNbt.getCompound("Fireworks");

        if (!slotFireworks.contains("Flight") || !cursorFireworks.contains("Flight")) {
            return false;
        }

        return slotFireworks.getByte("Flight") == cursorFireworks.getByte("Flight");
    }

    /**
     * @return if slot potion effect matches any cursor's potion effects.
     */
    public static boolean statusEffectMatches(ItemStack slotStack, ItemStack cursorStack) {
        List<MobEffectInstance> slotEffects = PotionUtils.getMobEffects(slotStack);
        List<MobEffectInstance> cursorEffects = PotionUtils.getMobEffects(cursorStack);

        if (slotEffects.isEmpty() || cursorEffects.isEmpty()) {
            return false;
        }

        // Ensure no cross-over items
        if (!ItemStack.isSameItem(slotStack, cursorStack)) {
            return false;
        }

        for (MobEffectInstance slotEffect : slotEffects) {
            for (MobEffectInstance cursorEffect : cursorEffects) {
                System.out.println(slotEffect.getEffect().getDescriptionId());
                System.out.println(cursorEffect.getEffect().getDescriptionId());
                if (slotEffect.getEffect().getDescriptionId().equals(cursorEffect.getEffect().getDescriptionId())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return if slot enchantment matches cursor's enchantment.
     */
    public static boolean enchantmentMatches(ItemStack slotStack, ItemStack cursorStack) {
        for (Map.Entry<Enchantment, Integer> slotEntry : EnchantmentHelper.getEnchantments(slotStack).entrySet()) {
            for (Map.Entry<Enchantment, Integer> heldEntry : EnchantmentHelper.getEnchantments(cursorStack).entrySet()) {
                if (getEnchantmentName(slotEntry).contains(getEnchantmentName(heldEntry))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return enchantment name as a string.
     */
    public static String getEnchantmentName(Map.Entry<Enchantment, Integer> enchantment) {
        String enchantmentKey = enchantment.getKey().getDescriptionId().toLowerCase();
        return enchantmentKey.substring(enchantmentKey.lastIndexOf('.') + 1).replace('_', ' ');
    }

    /**
     * @return current screen's cursor stack.
     */
    public static ItemStack getCursorStack(AbstractContainerScreen<?> screen) {
        return screen.getMenu().getCarried();
    }

    /**
     * Swaps all items in a container.
     */
    public static void swapItems(AbstractContainerMenu handler, Container inventory, Set<Integer> excludedSlots) {
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
                if (chestSlot.index == id || playerSlot.index == id) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

            if (!options().includeHotbar && isHotbarSlot(handler.slots.size(), playerSlot.index)) {
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
     * Sorts all items in a container.
     */
    @Unique
    public static void sortItems(Minecraft client) {
        if (client.player == null || client.getConnection() == null) {
            return;
        }
        if (!(client.screen instanceof AbstractContainerScreen<?> screen)) {
            return;
        }

        AbstractContainerMenu handler = screen.getMenu();
        int totalSlots = handler.slots.size();
        int containerSize = totalSlots - 36;

        if (containerSize <= 0) {
            return;
        }

        // Merge all stacks
        for (int i = 0; i < containerSize; i++) {
            Slot source = handler.slots.get(i);
            if (!source.hasItem()) {
                continue;
            }

            ItemStack sourceStack = source.getItem();

            // Already full, skip
            if (sourceStack.getCount() >= sourceStack.getMaxStackSize()) {
                continue;
            }

            for (int j = i + 1; j < containerSize; j++) {
                Slot target = handler.slots.get(j);
                if (!target.hasItem()) {
                    continue;
                }

                ItemStack targetStack = target.getItem();

                if (!canCombine(sourceStack, targetStack)) {
                    continue;
                }

                // Pick up target
                clickSlot(client, handler, j);
                // Click source to merge
                clickSlot(client, handler, i);

                // If cursor still has items, put them back
                if (!client.player.containerMenu.getCarried().isEmpty()) {
                    clickSlot(client, handler, j);
                }

                // Stop if source is now full
                if (source.getItem().getCount() >= sourceStack.getMaxStackSize()) {
                    break;
                }
            }
        }

        // Build list from LIVE slots AFTER merge
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < containerSize; i++) {
            ItemStack stack = handler.slots.get(i).getItem();
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
        }

        // Unified alphabetical sort (no stackability logic)
        stacks.sort(Comparator.comparing(
                stack -> stack.getHoverName().getString()
        ));

        // Pad with empties
        while (stacks.size() < containerSize) {
            stacks.add(ItemStack.EMPTY);
        }

        // Perform swap-based sorting
        for (int target = 0; target < containerSize; target++) {
            ItemStack desired = stacks.get(target);
            ItemStack actual = handler.slots.get(target).getItem();

            if (ItemStack.isSameItem(actual, desired)) {
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
     * Finds a slot matching the target merging slot.
     * @return the slot index.
     */
    private static int findMatchingSlot(AbstractContainerMenu handler, ItemStack target, int start, int limit) {
        if (target.isEmpty()) {
            return -1;
        }

        for (int i = start + 1; i < limit; i++) {
            ItemStack stack = handler.slots.get(i).getItem();
            if (ItemStack.isSameItem(stack, target)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Calls {@link ButtonUtil#clickSlot(Minecraft, AbstractContainerMenu, int)} to sort items.
     */
    private static void swapSlots(Minecraft client, AbstractContainerMenu handler, int a, int b) {
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
    private static void clickSlot(Minecraft client, AbstractContainerMenu handler, int slot) {
        client.gameMode.handleInventoryMouseClick(
                handler.containerId,
                slot,
                0,
                ClickType.PICKUP,
                client.player
        );
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
                client.gameMode.handleInventoryMouseClick(syncId, slotIndex, Screen.hasShiftDown() ? 0 : 1, clickType, client.player);
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
        if (options().dragToSort && Screen.hasAltDown()) {
            return;
        }

        if (options().quickEquip && focusedSlot != null && (focusedSlot.index >= 5) && (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen)) {
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
    public static void renderSlotUnavailable(GuiGraphics graphics, Slot slot, boolean hotbar) {
        int color = hotbar ? -2139062148 : -1275068416;
        graphics.fillGradient(RenderType.guiOverlay(), slot.x, slot.y, slot.x + 16, slot.y + 16, color, color, 0);
    }

    /**
     * @return if a button is hovered and active.
     */
    public static boolean buttonHoveredAndActive(Button button, int mouseX, int mouseY) {
        return button != null && button.isMouseOver(mouseX, mouseY) && button.active;
    }

    /**
     * @return if a button is hovered and active and shift is held.
     */
    public static boolean buttonHoveredActiveOrShiftHeld(AbstractContainerScreen<?> screen, Button button, int mouseX, int mouseY, boolean inventory) {
        return buttonHoveredAndActive(button, mouseX, mouseY) || shiftHeld(screen, inventory);
    }

    /**
     * @return whether a slot should be grayed out.
     */
    public static boolean shouldGrayout(AbstractContainerScreen<?> screen, TransferButton inventoryButton, TransferButton containerButton, TransferButton hotbarButton, Slot slot, int mouseX, int mouseY) {
        boolean shortcutKeyReady = isInventoryScreen(screen) ? Screen.hasControlDown() && Screen.hasAltDown() : Screen.hasControlDown();
        return shortcutKeyReady
                || shiftHeld(screen, false)
                || (buttonHoveredAndActive(inventoryButton, mouseX, mouseY) && slot.hasItem())
                || buttonHoveredAndActive(containerButton, mouseX, mouseY)
                || (buttonHoveredAndActive(hotbarButton, mouseX, mouseY) && slot.hasItem());
    }

    /**
     * @return if shift is held and a slot is hovered.
     */
    public static boolean shiftHeld(AbstractContainerScreen<?> screen, boolean inventory) {
        int totalSlots = getTotalSlots(screen.getMenu());
        return Screen.hasShiftDown()
                && (!options().dragToSort || !Screen.hasAltDown())
                && screen.hoveredSlot != null
                && screen.hoveredSlot.hasItem()
                && (inventory ? screen.hoveredSlot.index >= totalSlots - 36 : screen.hoveredSlot.index <= totalSlots - 37);
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
        return screen instanceof ContainerScreen || screen instanceof ShulkerBoxScreen;
    }

    /**
     * @return valid brewingStandScreen.
     */
    public static boolean isBrewingStandScreen(Screen screen) {
        return screen instanceof BrewingStandScreen;
    }

    /**
     * @return valid furanceScreen.
     */
    public static boolean isFurnaceScreen(Screen screen) {
        return screen instanceof AbstractFurnaceScreen<?>;
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
        return screen instanceof CreativeModeInventoryScreen;
    }

    /**
     * @return if the stack is a shulker box.
     */
    public static boolean isStackShulker(ItemStack stack) {
        for (Item shulker : shulkerBoxes) {
            if (stack.is(shulker)) {
                return true;
            }
        }
        return false;
    }
}