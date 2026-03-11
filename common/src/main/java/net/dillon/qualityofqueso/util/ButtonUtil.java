package net.dillon.qualityofqueso.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.option.MoveItemsIf;
import net.dillon.qualityofqueso.option.screen.ModOptionsScreen;
import net.dillon.qualityofqueso.screen.gui.button.TransferButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

import static net.dillon.qualityofqueso.util.AccessorUtil.getHoveredSlot;
import static net.dillon.qualityofqueso.util.AccessorUtil.getRecipeBookComponent;
import static net.dillon.qualityofqueso.util.ModUtil.*;

/**
 * Utility class.
 */
public class ButtonUtil {
    public static final String ENABLED_TEXTURE = "sprites/button/qoq_enabled";
    public static final String DISABLED_TEXTURE = "sprites/button/qoq_disabled";

    /**
     * Initializes the settings button.
     */
    public static SpriteIconButton initializeButton(Minecraft client, Screen parent) {
        return SpriteIconButton.builder(ModTexts.BLANK, (onPress) -> client.setScreen(new ModOptionsScreen(parent)), false)
                .width(20)
                .sprite(ofQoQ("button/cheese_wheel"), 16, 16)
                .build();
    }

    /**
     * Draws a tooltip.
     */
    public static void drawTooltip(Component tooltip, GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        graphics.setTooltipForNextFrame(font, font.split(tooltip, 200), mouseX, mouseY);
    }

    /**
     * Draws a texture over a button.
     */
    public static void drawTexture(GuiGraphics graphics, String name, Button button, float f) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.parse("qualityofqueso:textures/gui/" + name + ".png"), button.getX() + 2, button.getY() + 2, 0.0F, 0.0F, 16, 16, 16, 16, ARGB.color(f, CommonColors.WHITE));
    }

    /**
     * Draws the texture for a {@code inventory management button.}
     */
    public static void drawButtonTexture(GuiGraphics graphics, String name, TransferButton button) {
        int xy = getTransferButtonXY(button);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.parse("qualityofqueso:textures/gui/button/" + name + ".png"), button.getX() - 1, button.getY() - 1, 0.0F, 0.0F, xy, xy, xy, xy);
    }

    /**
     * Draws a texture over a button without a custom fade.
     */
    public static void drawTexture(GuiGraphics graphics, String name, Button button) {
        drawTexture(graphics, name, button, 1.0F);
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
     * @return the {@code X} value for transferring query buttons.
     */
    public static int getManagementButtonX(Screen screen, int backgroundWidth, int width, int amount) {
        int barWidth = getBarWidth(backgroundWidth);
        int modifier = 18;
        if (isBrewingStandScreen(screen)) {
            modifier -= 36;
        } else if (isFurnaceScreen(screen)) {
            modifier -= 16;
        } else if (screen instanceof AbstractRecipeBookScreen<?> recipeBookScreen && getRecipeBookComponent(recipeBookScreen).isVisible()) {
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
        return options().searching.searchInventory ? handler.slots.size() : inventory == null ? 0 : inventory.getContainerSize();
    }

    /**
     * @return if the user is attempting to exclude slots.
     */
    public static boolean isExcludingSlots(AbstractContainerScreen<?> handledScreen) {
        return isValidScreen(handledScreen)
                && options().management.dragSorting
                && (Minecraft.getInstance().hasAltDown() || (Minecraft.getInstance().hasShiftDown() && Minecraft.getInstance().hasAltDown()))
                && getHoveredSlot(handledScreen) != null
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
        return ItemStack.isSameItemSameComponents(a, b);
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
        for (int i = start; i < (options().management.includeHotbar && checkHotbar ? end + 9 : end); i++) {
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

        int inventorySize = options.management.includeHotbar ? 36 : 27;
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
                componentFreeSpace.merge(stack.getComponents(), free, Math::max);
            }
        }

        for (int i = fromStart; i < fromEnd; i++) {
            Slot fromSlot = handler.getSlot(i);
            ItemStack fromItem = fromSlot.getItem();

            if (fromSlot.hasItem()) {
                filledSlots++;
            }

            if (fromItem.isEmpty() || options.accessibility.moveItemsIf.containerIsntFilled()) {
                continue;
            }

            for (int k = toStart; k < toEnd; k++) {
                ItemStack toItem = handler.getSlot(k).getItem();
                if (toItem.isEmpty()) {
                    continue;
                }

                if (options.accessibility.moveItemsIf == MoveItemsIf.CAN_MOVE_AT_ALL) {
                    if (ItemStack.isSameItem(toItem, fromItem)) {
                        Integer free = componentFreeSpace.get(toItem.getComponents());
                        if (free != null && free > 0) {
                            return false;
                        }
                    }
                } else if (options.accessibility.moveItemsIf == MoveItemsIf.LESS_THAN_MAX_STACK_SIZE) {
                    if (ItemStack.isSameItemSameComponents(toItem, fromItem) && !(toItem.getCount() + fromItem.getCount() > toItem.getMaxStackSize())) {
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

        if ((isEnchantedBook || isPotion || isTippedArrow || isFirework) && Minecraft.getInstance().hasShiftDown()) {
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
        Fireworks slotDuration = slotStack.get(DataComponents.FIREWORKS);
        Fireworks cursorDuration = cursorStack.get(DataComponents.FIREWORKS);

        if (slotDuration == null || cursorDuration == null) {
            return false;
        }

        return slotDuration.flightDuration() == cursorDuration.flightDuration();
    }

    /**
     * @return if slot potion effect matches any cursor's potion effects.
     */
    public static boolean statusEffectMatches(ItemStack slotStack, ItemStack cursorStack) {
        PotionContents slotEffects = slotStack.get(DataComponents.POTION_CONTENTS);
        PotionContents cursorEffects = cursorStack.get(DataComponents.POTION_CONTENTS);

        if (slotEffects == null || cursorEffects == null) {
            return false;
        }

        // Ensure no cross-over items
        if (!ItemStack.isSameItem(slotStack, cursorStack)) {
            return false;
        }

        for (MobEffectInstance slotEffect : slotEffects.getAllEffects()) {
            for (MobEffectInstance cursorEffect : cursorEffects.getAllEffects()) {
                if (slotEffect.getEffect() == cursorEffect.getEffect()) {
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
        ItemEnchantments fromStackEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(slotStack);
        ItemEnchantments cursorEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(cursorStack);
        for (Holder<Enchantment> slotEnchantments : fromStackEnchantments.keySet()) {
            for (Holder<Enchantment> heldEnchantments : cursorEnchantments.keySet()) {
                if (slotStack.is(cursorStack.getItem()) && getEnchantmentName(slotEnchantments).contains(getEnchantmentName(heldEnchantments))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return enchantment name as a string.
     */
    public static String getEnchantmentName(Holder<Enchantment> enchantment) {
        return enchantment.value().description().getString().toLowerCase();
    }

    /**
     * @return current screen's cursor stack.
     */
    public static ItemStack getCursorStack(AbstractContainerScreen<?> screen) {
        return screen.getMenu().getCarried();
    }

    /**
     * @return if a stack is in a tag.
     */
    public static boolean areStacksInSameTag(ItemStack fromStack, ItemStack toStack) {
        return fromStack.tags().anyMatch(toStack::is);
    }

    /**
     * @return if two stacks match under the current fill filter mode.
     */
    public static boolean matchesFillFilter(ItemStack fromStack, ItemStack toStack) {
        boolean areMatching = fromStack.getItem() == toStack.getItem();
        if (ContainerTracker.IS_TRACKED_CONTAINER && ContainerTracker.CURRENT_FILTER_MODE.tag()) {
            return areStacksInSameTag(fromStack, toStack) || areMatching;
        }
        return areMatching;
    }

    /**
     * @return true if the item is present in the opposing inventory/container.
     */
    public static boolean isPresentInContainer(Container container, AbstractContainerMenu menu, ItemStack sourceStack) {
        if (ContainerTracker.getCurrentPlaceholderStacks().isEmpty()) {
            for (int i = 0; i < getContainerSize(container); i++) {
                ItemStack opposingStack = menu.getSlot(i).getItem();
                if (!opposingStack.isEmpty() && matchesFillFilter(sourceStack, opposingStack)) {
                    return true;
                }
            }
        }
        return itemMatchesPlaceholder(sourceStack);
    }

    /**
     * @return true if the stack matches any placeholder in the current tracked container.
     */
    public static boolean itemMatchesPlaceholder(ItemStack sourceStack) {
        if (!ContainerTracker.IS_TRACKED_CONTAINER) {
            return false;
        }

        for (ItemStack placeholderStack : ContainerTracker.getCurrentPlaceholderStacks()) {
            if (!placeholderStack.isEmpty() && matchesFillFilter(sourceStack, placeholderStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Plays the default button press sound.
     */
    public static void playDefaultSound(SoundManager manager) {
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
        client.getSoundManager().play(SimpleSoundInstance.forUI(drop ? SoundEvents.BUNDLE_DROP_CONTENTS : SoundEvents.BUNDLE_INSERT, 1.0F, 5.0F));
    }

    /**
     * Plays the inactive bundle sound.
     */
    public static void playButtonInactiveSound(Minecraft client) {
        client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 0.6F));
    }

    /**
     * Swaps all items in a container.
     */
    public static void swapItems(Minecraft minecraft, AbstractContainerMenu handler, Container inventory, Set<Integer> excludedSlots) {
        int totalSlots = getTotalSlots(handler);
        int containerSize = getContainerSize(inventory);
        int offset = totalSlots - 27 - 9;

        int swaps = Math.min(totalSlots - containerSize, containerSize);

        boolean swappedItem = false;
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

            if (!options().management.includeHotbar && isHotbarSlot(handler.slots.size(), playerSlot.index)) {
                continue;
            }

            ItemStack chestStack = chestSlot.getItem();
            ItemStack playerStack = playerSlot.getItem();

            // Ignore if both slots are empty
            if (chestStack.isEmpty() && playerStack.isEmpty()) {
                continue;
            }

            sendSwapSlotPacket(playerSlot.index, chestSlot.index);
            swappedItem = true;
        }

        if (swappedItem) {
            playButtonSound(minecraft, false);
        } else {
            playButtonInactiveSound(minecraft);
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
        client.gameMode.handleContainerInput(
                handler.containerId,
                slot,
                0,
                ContainerInput.PICKUP,
                client.player
        );
    }

    /**
     * Sends a "swap slot" click packet.
     */
    public static void sendSwapSlotPacket(int source, int index) {
        sendClickSlotPacket(source, ContainerInput.PICKUP);
        sendClickSlotPacket(index, ContainerInput.PICKUP);
        sendClickSlotPacket(source, ContainerInput.PICKUP);
    }

    /**
     * Sends a click slot packet.
     */
    public static void sendClickSlotPacket(int slotIndex, ContainerInput containerInput) {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener networkHandler = client.getConnection();

        if (client.player == null || networkHandler == null || client.player.containerMenu == null) {
            return;
        }

        var handler = client.player.containerMenu;
        int syncId = handler.containerId;
        int stateId = handler.getStateId();

        if (containerInput == ContainerInput.THROW) {
            if (!handler.getSlot(slotIndex).getItem().isEmpty()) {
                client.gameMode.handleContainerInput(syncId, slotIndex, Minecraft.getInstance().hasShiftDown() ? 0 : 1, containerInput, client.player);
            }
            return;
        }

        ItemStack carriedStack = handler.getCarried();
        ItemStack clickedStack = handler.getSlot(slotIndex).getItem();

        HashedPatchMap.HashGenerator hasher = networkHandler.decoratedHashOpsGenenerator();

        HashedStack carriedHash = HashedStack.create(carriedStack, hasher);
        HashedStack clickedHash = HashedStack.create(clickedStack, hasher);

        Int2ObjectOpenHashMap<HashedStack> modifiedStacks = new Int2ObjectOpenHashMap<>();
        modifiedStacks.put(slotIndex, clickedHash);

        ServerboundContainerClickPacket packet = new ServerboundContainerClickPacket(
                syncId,
                stateId,
                (short) slotIndex,
                (byte) 0,
                containerInput,
                modifiedStacks,
                carriedHash
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
        if (options().management.dragSorting && Minecraft.getInstance().hasAltDown()) {
            return;
        }

        if (options().misc.quickEquip && focusedSlot != null && (focusedSlot.index >= 5) && (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen)) {
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
                    sendClickSlotPacket(focusedSlot.index, ContainerInput.QUICK_MOVE);
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
        graphics.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, color, color);
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
    public static boolean buttonHoveredActiveOrShiftHeld(AbstractContainerScreen<?> screen, Button button, boolean inventory) {
        return buttonHoveredAndActive(button) || shiftHeld(screen, inventory);
    }

    /**
     * @return whether a slot should be grayed out.
     */
    public static boolean shouldGrayout(AbstractContainerScreen<?> screen, TransferButton inventoryButton, TransferButton containerButton, TransferButton hotbarButton, TransferButton quickDropButton, Slot slot) {
        boolean shortcutKeyReady = isInventoryScreen(screen) ? Minecraft.getInstance().hasControlDown() && Minecraft.getInstance().hasAltDown() : Minecraft.getInstance().hasControlDown();
        return shortcutKeyReady
                || shiftHeld(screen, false)
                || (buttonHoveredAndActive(inventoryButton) && slot.hasItem())
                || buttonHoveredAndActive(containerButton)
                || (buttonHoveredAndActive(hotbarButton) && slot.hasItem())
                || (isInventoryScreen(screen) && buttonHoveredAndActive(quickDropButton) && slot.hasItem());
    }

    /**
     * @return if shift is held and a slot is hovered.
     */
    public static boolean shiftHeld(AbstractContainerScreen<?> screen, boolean inventory) {
        int totalSlots = getTotalSlots(screen.getMenu());
        return Minecraft.getInstance().hasShiftDown()
                && (!options().management.dragSorting || !Minecraft.getInstance().hasAltDown())
                && getHoveredSlot(screen) != null
                && getHoveredSlot(screen).hasItem()
                && (inventory ? getHoveredSlot(screen).index >= totalSlots - 36 : getHoveredSlot(screen).index <= totalSlots - 37);
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
        return screen instanceof ContainerScreen || isShulkerBoxScreen(screen);
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
     * A class that stores button names.
     */
    public static class ButtonNames {
        public static final String SORT = "sort/sort";
    }
}
