package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.screen.gui.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.dillon.qualityofqueso.main.QoQ.modEnabled;
import static net.dillon.qualityofqueso.main.QoQ.options;
import static net.dillon.qualityofqueso.util.ButtonUtil.*;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Shadow
    protected int backgroundWidth;
    @Shadow
    protected int y;
    @Shadow
    protected int titleY;

    @Shadow
    @Nullable
    public Slot focusedSlot;

    @Shadow
    @Nullable
    protected abstract Slot getSlotAt(double mouseX, double mouseY);

    @Shadow
    @Final
    protected T handler;
    @Unique
    private final HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
    @Unique
    private TextFieldWidget containerSearchField;
    @Unique
    private TextFieldWidget inventorySearchField;
    @Unique
    private TransferButton transferContainerButton;
    @Unique
    private TransferButton transferInventoryButton;
    @Unique
    private TransferButton includeHotbarButton;
    @Unique
    private TransferButton quickDropButton;
    @Unique
    private TransferButton swapButton;
    @Unique
    private int swapCooldown = 0;
    @Unique
    private Inventory inventory;

    public HandledScreenMixin(Text title) {
        super(title);
    }

    /**
     * Creates and initializes the search field and container button.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (modEnabled(this.client)) {
            if (isContainerScreen(this.screen)) {
                // Determine fromInventory variable; if instance ShulkerBoxScreen, fromInventory is the shulker box's fromInventory
                if (this.screen instanceof ShulkerBoxScreen shulkerBoxScreen) {
                    this.inventory = shulkerBoxScreen.getScreenHandler().inventory;
                }
                // If it's GenericContainerScreen, it's the generic container (or most likely chest/barrel)'s fromInventory
                else if (this.screen instanceof GenericContainerScreen genericContainerScreen) {
                    this.inventory = genericContainerScreen.getScreenHandler().getInventory();
                }
                // Otherwise, fromInventory is null
                else {
                    this.inventory = null;
                }

                if (options().chestSearching) {
                    this.containerSearchField = this.initializeSearchField(false);
                    this.addSelectableChild(this.containerSearchField);
                }
            } else if (isInventoryScreen(this.screen)) {
                this.inventory = this.client.player.getInventory();
                if (options().inventorySearching) {
                    this.inventorySearchField = this.initializeSearchField(true);
                    this.addSelectableChild(this.inventorySearchField);
                }
            }
        }
    }

    /**
     * Initializes a search field widget.
     */
    @Unique
    private SearchField initializeSearchField(boolean inventory) {
        return new SearchField(this.textRenderer, this.width / 2 + getBarWidth(this.backgroundWidth) / 2 - (inventory ? 60 : 64), this.y + this.titleY - 2);
    }

    /**
     * @return the {@code searchField namespace} text.
     */
    @Unique
    private String getSearchFieldText() {
        return this.inventorySearchField != null ? this.inventorySearchField.getText() : this.containerSearchField != null ? this.containerSearchField.getText() : "";
    }

    /**
     * Drops all highlighted items.
     */
    @Unique
    private void dropItems(boolean fromInventory) {
        this.moveItems(true, true, fromInventory);
    }

    /**
     * Transfers items the original way.
     */
    @Unique
    private void transferItems(boolean toInventory) {
        this.moveItems(toInventory, false, false);
    }

    /**
     * Transfers items from one container to another.
     */
    @Unique
    private void moveItems(boolean toInventory, boolean drop, boolean fromInventory) {
        int containerSize = getContainerSize(this.inventory);
        int totalSlots = getTotalSlots(this.handler);

        int fromStart = toInventory ? 0 : containerSize;
        int fromEnd = toInventory ? containerSize : totalSlots;
        int toStart = toInventory ? containerSize : 0;
        int toEnd = toInventory ? totalSlots : containerSize;

        if (drop && fromInventory) {
            fromStart = 9;
            fromEnd = 45;
        }

        // Normal logic (dropping and quick move)
        for (int i = fromStart; i < fromEnd; i++) {
            Slot fromSlot = this.handler.getSlot(i);
            ItemStack fromStack = fromSlot.getStack();

            if (!options().includeHotbar) {
                if (drop) {
                    if (isExcludedSlot(this.screen, fromSlot.id) || isInventoryHotbarSlot(isInventoryScreen(this.screen), fromSlot.id)) {
                        continue; // If dropping from InventoryScreen, and it's an excluded slot AND fromInventory hotbar slot, skip slot and continue
                    }
                } else if (!toInventory && isHotbarSlot(fromEnd, fromSlot.id)) {
                    continue; // Otherwise, check if it's a hotbar slot in normal container
                }
            } else if ((this.containerSearchField != null || this.inventorySearchField != null) && !this.getSearchFieldText().isEmpty() && !this.search(this.getSearchFieldText(), fromSlot, false)) {
                continue; // Then skip container slot if query not found via search
            }

            if (!fromStack.isEmpty()) {
                for (int j = toStart; j < toEnd; j++) {
                    Slot toSlot = this.handler.getSlot(j);
                    // Quickly swap items in container
                    if (drop || toSlot.getStack().isEmpty()) {
                        // Only transfer items if the query matches whatever the cursor is holding
                        // IF DROP, CANNOT THROW ITEMS IF CURSOR STACK ISN'T EMPTY. VANILLA FEATURE, CAN'T WORK AROUND IT (for now, anyone know a solution?)
                        SlotActionType slotActionType = drop ? SlotActionType.THROW : SlotActionType.QUICK_MOVE;
                        if (!this.handler.getCursorStack().isEmpty()) {
                            if (fromStack.isOf(this.handler.getCursorStack().getItem())) {
                                sendClickSlotPacket(i, slotActionType);
                                break;
                            }
                        }
                        // If cursor has nothing in it, move all items over
                        else {
                            sendClickSlotPacket(i, slotActionType);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @return {@code true} if the {@code fromInventory} has a match with the search query.
     */
    @Unique
    private boolean shouldButtonBeActive(boolean isPlayerInventory,
                                         @Nullable PlayerInventory playerInventory, ClickableWidget button) {
        // If button is null, return false because there is no button to be checked
        if (button == null) {
            return false;
        }
        // Determine fromInventory size to run through
        int size = isPlayerInventory ? playerInventory.getMainStacks().size() : this.inventory.size();
        int filledSlots = 0;
        for (int i = 0; i < size; i++) {
            // If slot is not empty, the button should be active
            // Increment J and make button active
            ItemStack stack = isPlayerInventory ? playerInventory.getStack(i) : this.handler.getSlot(i).getStack();
            // Handle cursor stack
            boolean isShulkerScreen = this.screen instanceof ShulkerBoxScreen;
            boolean isCursorShulker = isShulkerScreen && this.handler.getCursorStack().isIn(ItemTags.SHULKER_BOXES);
            boolean isStackShulker = isShulkerScreen && stack.isIn(ItemTags.SHULKER_BOXES);
            if (!this.handler.getCursorStack().isEmpty()) {
                if (stack.isOf(this.handler.getCursorStack().getItem()) && !isCursorShulker) {
                    filledSlots++;
                }
            } else {
                if (!stack.isEmpty() && !isStackShulker) {
                    filledSlots++;
                }
            }
        }
        return filledSlots != 0 && !this.areAllSlotsUnavailable(isPlayerInventory, isPlayerInventory ? playerInventory : null);
    }

    /**
     * @return {@code true} if all slots are grayed out, or {@code unavailable.}
     */
    @Unique
    private boolean areAllSlotsUnavailable(boolean isPlayerInventory, @Nullable PlayerInventory playerInventory) {
        int foundQuerys = 0;
        List<Slot> playerSlots = new ArrayList<>();
        // If checking player fromInventory, loop through all player fromInventory slots to determine if slot is unavailable
        if (isPlayerInventory) {
            for (Slot s : this.handler.slots) {
                if (s.inventory == playerInventory) {
                    playerSlots.add(s);
                }
            }
            for (Slot slot : playerSlots) {
                if (this.search(this.getSearchFieldText(), slot, false)) {
                    foundQuerys++; // increment J if query found in slot
                }
            }
        } else {
            // Otherwise, loop through the container fromInventory and increment J if query found inside
            for (int i = 0; i < this.inventory.size(); i++) {
                Slot slot = this.handler.getSlot(i);
                if (this.search(this.getSearchFieldText(), slot, false)) {
                    foundQuerys++;
                }
            }
        }
        // If J == 0 then no query was found (OR if ALL slots are filled in fromInventory/container), returning true for all slots are unavailable
        return foundQuerys == 0;
    }

    /**
     * @return {@code true} if an query is found from {@code searchQuery}.
     */
    @Unique
    private boolean search(String searchQuery, Slot slot, boolean dropping) {
        ItemStack stack = slot.getStack();

        // If slot is empty, return false (slot is unavailable)
        // If include hotbar is off, return false if hotbar slot
        if (stack.isEmpty() ||
                !options().includeHotbar && options().searchInventory && isHotbarSlot(this.handler.slots.size(), dropping ? slot.id + 1 : slot.id) &&
                        (!dropping || !isInventoryScreen(this.screen) || slot.id != 45)) {
            return false;
        }

        String itemName = stack.getItemName().getString().toLowerCase();
        String customName = stack.getCustomName() != null ? stack.getCustomName().getString().toLowerCase() : "";

        String[] terms = searchQuery.split(",");
        // If slot contains a comma, for each query searched (separated by each comma), return true if search query'namespace find an query (make slot available)
        for (String term : terms) {
            if (itemName.contains(term.trim().toLowerCase()) || customName.contains(term.trim().toLowerCase())) {
                return true;
            }
        }

        // As long as slot doesn't contain whatever is searched (beginning after "!"), return true (slot is available)
        if (searchQuery.startsWith("!")) {
            return !itemName.contains(searchQuery.substring(1)) && !customName.contains(searchQuery.substring(1));
        }

        // If tag contains search query and stack is in returned tag, slot is available
        if (searchQuery.startsWith("#")) {
            String tagSearch = searchQuery.substring(1);
            RegistryWrapper.WrapperLookup lookup = MinecraftClient.getInstance().world.getRegistryManager();
            RegistryWrapper<Item> itemRegistry = lookup.getOrThrow(RegistryKeys.ITEM);

            for (TagKey<Item> tagKey : itemRegistry.streamTagKeys().toList()) {
                Identifier id = tagKey.id();
                if (id.getPath().toLowerCase().contains(tagSearch) || id.toString().toLowerCase().contains(tagSearch)) {
                    if (stack.isIn(tagKey)) {
                        return true;
                    }
                }
            }
        }

        // Enchanted book searching logic
        if (stack.hasEnchantments() || stack.isOf(Items.ENCHANTED_BOOK)) {
            ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
            for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                String encName = enchantment.value().description().getString();
                String fullName = encName + " " + enchantments.getLevel(enchantment);

                // If slot contains enchantments searched, return true (slot is available)
                if (fullName.toLowerCase().contains(searchQuery)) {
                    return true;
                }
            }
        }

        // If slot contains whatever is searched, return true (slot is available)
        return itemName.contains(searchQuery) || customName.contains(searchQuery);
    }

    /**
     * Grays out any containerSlot which doesn't contain the query name being searched.
     */
    @Inject(method = "renderMain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlightFront(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void grayOutSlot(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        boolean inventorySearchFieldPresent = this.inventorySearchField != null;
        if ((this.containerSearchField != null || inventorySearchFieldPresent)) {
            for (int i = 0; i < getInventorySize(this.handler, this.inventory); i++) {
                Slot slot = this.handler.getSlot(i);
                // Otherwise, gray out slots that don't match the search
                if (!this.getSearchFieldText().isEmpty() && !this.search(this.getSearchFieldText(), slot, this.inventorySearchField != null)) {
                    makeSlotUnavailable(context, slot, false);
                }
            }
        }
    }

    /**
     * Handles rendering, such as the search field and transferring fromInventory button textures.
     */
    @Inject(method = "renderMain", at = @At("TAIL"))
    private void renderWidgets(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        // Render the search field
        if (this.containerSearchField != null) {
            this.containerSearchField.render(context, mouseX, mouseY, deltaTicks);
        }
        // Render fromInventory search field
        if (this.inventorySearchField != null) {
            this.inventorySearchField.setX(this.width / 2 + getBarWidth(this.backgroundWidth) / 2 - (
                    this.screen instanceof RecipeBookScreen<?> recipeBookScreen && recipeBookScreen.recipeBook.isOpen() ? -16 : 60
            ));
            this.inventorySearchField.render(context, mouseX, mouseY, deltaTicks);
        }
        if (modEnabled(this.client)) {
            if (options().inventoryManagement) {
                PlayerInventory playerInventory = this.client.player.getInventory();
                boolean containerScreen = isContainerScreen(this.screen);
                boolean inventoryScreen = isInventoryScreen(this.screen);
                boolean validScreen = containerScreen || inventoryScreen;
                if (containerScreen) {

                    /* --- */

                    // TRANSFER CONTAINER BUTTON (container -> inventory)
                    this.transferContainerButton = this.addSelectableChild(
                            new TransferButton(
                                    this.handler,
                                    this.textRenderer,
                                    this.getSearchFieldText(),
                                    getManagementButtonX(this.screen, this.backgroundWidth, this.width),
                                    getManagementButtonY(this.screen, this.inventory, this.y, this.titleY),
                                    "transfer_container",
                                    b -> this.transferItems(true),
                                    () -> !isContainerFull(this.handler, this.inventory, true) && this.shouldButtonBeActive(false, null, this.transferContainerButton)));

                    this.transferContainerButton.render(context, mouseX, mouseY, deltaTicks);

                    /* --- */

                    // TRANSFER INVENTORY BUTTON (inventory -> container)
                    this.transferInventoryButton = this.addSelectableChild(
                            new TransferButton(
                                    this.handler,
                                    this.textRenderer,
                                    this.getSearchFieldText(),
                                    getButtonX(this.transferContainerButton) - 12,
                                    getManagementButtonY(this.screen, this.inventory, this.y, titleY),
                                    "transfer_inventory",
                                    b -> this.transferItems(false),
                                    () -> !isContainerFull(this.handler, this.inventory, false) && this.shouldButtonBeActive(true, playerInventory, this.transferInventoryButton)
                            ));

                    this.transferInventoryButton.render(context, mouseX, mouseY, deltaTicks);
                }

                /* --- */

                if (validScreen) {

                    // INCLUDE HOTBAR BUTTON
                    if (options().inventorySearching || options().quickDrop) {
                        this.includeHotbarButton = this.addSelectableChild(
                                new IncludeHotbarButton(
                                        this.handler,
                                        this.textRenderer,
                                        this.getSearchFieldText(),
                                        getManagementButtonX(this.screen, this.backgroundWidth, this.width) - 24,
                                        getManagementButtonY(this.screen, this.inventory, this.y, this.titleY),
                                        "include_hotbar",
                                        b -> {
                                            options().includeHotbar = !options().includeHotbar;
                                            ModClientOptions.CLIENT_OPTIONS.save();
                                        }));

                        this.includeHotbarButton.render(context, mouseX, mouseY, deltaTicks);
                    }

                    /* --- */

                    // SWAP BUTTON
                    if (options().swapping && containerScreen) {
                        this.swapButton = this.addSelectableChild(
                                new SwapButton(
                                        this.handler,
                                        this.textRenderer,
                                        this.getSearchFieldText(),
                                        getButtonX(this.includeHotbarButton) - 12,
                                        getManagementButtonY(this.screen, this.inventory, this.y, this.titleY),
                                        "swap",
                                        b -> swapItems(this.handler, this.inventory),
                                        () -> {
                                            // Decrement swap cooldown
                                            if (swapCooldown > 0) {
                                                swapCooldown--;
                                            }
                                            return (getContainerSize(this.inventory) != 27 || isAnySlotFilled(this.handler, false, 27, 54))
                                                    && this.handler.getCursorStack().isEmpty() && this.getSearchFieldText().isEmpty()
                                                    && this.shouldButtonBeActive(false, null, this.swapButton)
                                                    && this.shouldButtonBeActive(true, playerInventory, this.swapButton);
                                        }
                                ));

                        this.swapButton.render(context, mouseX, mouseY, deltaTicks);
                    }

                    /* --- */

                    // QUICK DROP BUTTON
                    if (options().quickDrop) {
                        this.quickDropButton = this.addSelectableChild(
                                new QuickDropButton(
                                        this.handler,
                                        this.textRenderer,
                                        this.getSearchFieldText(),
                                        getButtonX(this.includeHotbarButton) - (containerScreen && options().swapping ? 24 : 12),
                                        getManagementButtonY(this.screen, this.inventory, this.y, this.titleY),
                                        "quick_drop",
                                        b -> this.dropItems(!containerScreen),
                                        () -> (isInventoryScreen(this.screen) ?
                                                isAnySlotFilled(this.handler, true, 9, 36) :
                                                isAnySlotFilled(this.handler, false, 0, getContainerSize(this.inventory)))
                                                && this.handler.getCursorStack().isEmpty()
                                                && this.shouldButtonBeActive(!containerScreen, containerScreen ? null : playerInventory, this.quickDropButton)
                                ));

                        this.quickDropButton.render(context, mouseX, mouseY, deltaTicks);
                    }
                }
            }
        }
    }

    /**
     * Renders tag tooltips to all slots if searching by tag {@code searchQuery.startsWith(#)}.
     */
    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void addAllItemTagsToTooltip(DrawContext drawContext, int x, int y, CallbackInfo ci) {
        String searchQuery = this.getSearchFieldText();
        if (this.containerSearchField != null) {
            // Exit if search query doesn't start with #
            if (!searchQuery.startsWith("#")) {
                return;
            }

            // Exit if hovered slot doesn't have an query in it
            Slot hoveredSlot = this.getSlotAt(x, y);
            if (hoveredSlot == null || !hoveredSlot.hasStack()) {
                return;
            }

            ItemStack stack = hoveredSlot.getStack();
            RegistryWrapper<Item> itemRegistry = this.client.world.getRegistryManager().getOrThrow(RegistryKeys.ITEM);

            List<Text> originalTooltip = stack.getTooltip(Item.TooltipContext.DEFAULT, this.client.player, MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC);
            boolean foundTags = false;
            // Loop through all tags loaded (vanilla and modded)
            for (TagKey<Item> tagKey : itemRegistry.streamTagKeys().toList()) {
                if (stack.isIn(tagKey)) {
                    // Add each tag to the query hovered
                    String tagString = tagKey.id().getNamespace().equals("c") ? "#fabric:" + tagKey.id().getPath() : "#" + tagKey.id();
                    originalTooltip.add(1, Text.literal(tagString).formatted(Formatting.LIGHT_PURPLE));
                    foundTags = true;
                }
            }

            // If tags were found in the query add it to the tooltip and render
            // cancel out original method to prevent overlapping tooltips
            if (foundTags) {
                drawContext.drawTooltip(this.textRenderer, originalTooltip, Optional.empty(), x, y);
                ci.cancel();
            }
        }
    }

    /**
     * Closes the screen when clicking outside of menu.
     */
    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"))
    private void closeButtonOnClickOutOfBounds(Slot slot, int slotId, int button, SlotActionType
            actionType, CallbackInfo ci) {
        if (modEnabled(this.client) && options().betterGuiExit && this.handler.getCursorStack().isEmpty() && button == 0 && slot == null) {
            this.close();
        }
    }

    /**
     * Implements functionality for the {@code Quick Equip right-click feature,} and handles fromInventory search field.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleMouseClicking(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (modEnabled(this.client)) {
            if (click.button() == InputUtil.GLFW_MOUSE_BUTTON_RIGHT && this.focusedSlot != null && isQuicklyEquippable(this.focusedSlot.getStack())) {
                quickEquip(this.screen, this.focusedSlot);
                cir.setReturnValue(true);
            }
            // Refocus fromInventory search field if clicked.
            if (this.inventorySearchField != null && this.inventorySearchField.mouseClicked(click, doubled)) {
                this.inventorySearchField.setFocused(true);
            }
        }
    }

    /**
     * Handles key pressing correctly and implements functionality for the {@link ModKeybinds#QUICK_EQUIP} keybind.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (modEnabled(this.client)) {
            if (MinecraftClient.getInstance().isCtrlPressed()) {
                if (options().shortcutKeys) {
                    if (this.transferContainerButton != null && this.transferContainerButton.active && input.key() == ModKeybinds.MOVE_CONTAINER.boundKey.getCode()) {
                        this.transferItems(true);
                    }

                    if (this.transferInventoryButton != null && this.transferInventoryButton.active && input.key() == ModKeybinds.MOVE_INVENTORY.boundKey.getCode()) {
                        this.transferItems(false);
                    }

                    if (this.swapCooldown == 0 && this.swapButton != null && this.swapButton.active && input.key() == ModKeybinds.SWAP_ITEMS.boundKey.getCode()) {
                        swapItems(this.handler, this.inventory);
                        this.swapCooldown = 120;
                    }
                    if (this.quickDropButton != null && this.quickDropButton.active && MinecraftClient.getInstance().isAltPressed() && input.key() == GLFW.GLFW_KEY_Q) {
                        this.dropItems(!isContainerScreen(this.screen));
                    }
                }
            }

            // Quick equip logic
            if (input.key() == ModKeybinds.QUICK_EQUIP.boundKey.getCode()) {
                quickEquip(this.screen, this.focusedSlot);
            }

            // Prevent E from typing entirely in fromInventory screens
            if (input.key() == GLFW.GLFW_KEY_E && options().preventEFromTyping && (isInventoryScreen(this.screen) || isCreativeInventoryScreen(this.screen))) {
                this.close();
                cir.setReturnValue(true);
            }

            // Declare typing variables
            // Both of these variables apply to disallowed keys and hotbar switching
            boolean ignoreTyping = hoveredSlotHasItem(this.focusedSlot); // Basic ignore typing variable; applies to recipe book screens only.
            boolean secondaryIgnoreTyping = false; // Secondary ignore typing variable; applies to "T" and "E" keys and chest searching screens only.

            // These variables only apply to the chest search bar
            boolean numberKeyPressed = false; // Determines if a number key was pressed
            boolean hotbarKeyPressed = false; // Determines if a hotbar key was pressed
            boolean dropKeyPressed = false; // Determines if the drop key was pressed
            boolean swapKeyPressed = false; // Determines if the swap item key was pressed

            // If a "disallowed key" is pressed, ignoreTyping and secondaryIgnoreTyping become true.
            for (int key : QoQ.allDisallowedKeys) {
                if (input.key() == key) {
                    ignoreTyping = true;
                    secondaryIgnoreTyping = true;
                    break;
                }
            }

            // handle switching items from hotbar to another containerSlot in fromInventory; cancel out typing if an query can be moved
            if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
                for (int i = 0; i < 9; i++) {
                    if (this.client.options.hotbarKeys[i].matchesKey(input)) {
                        ignoreTyping = true;
                        secondaryIgnoreTyping = true;
                        hotbarKeyPressed = true;
                        break;
                    }
                }
                if (this.screen instanceof RecipeBookScreen<?> && input.key() == ModKeybinds.QUICK_EQUIP.boundKey.getCode()) {
                    ignoreTyping = true;
                }
            }

            // Handle 'T' and 'E' keys
            for (int key : QoQ.popularKeys) {
                if (input.key() == key) {
                    secondaryIgnoreTyping = false;
                    cir.setReturnValue(true);
                    break;
                }
            }

            // Check if number key was pressed
            List<Integer> numbers = List.of(
                    GLFW.GLFW_KEY_1,
                    GLFW.GLFW_KEY_2,
                    GLFW.GLFW_KEY_3,
                    GLFW.GLFW_KEY_4,
                    GLFW.GLFW_KEY_5,
                    GLFW.GLFW_KEY_6,
                    GLFW.GLFW_KEY_7,
                    GLFW.GLFW_KEY_8,
                    GLFW.GLFW_KEY_9
            );
            for (int key : numbers) {
                if (input.key() == key) {
                    numberKeyPressed = true;
                    break;
                }
            }

            // Check if drop key or swap hands key was pressed
            if (input.key() == MinecraftClient.getInstance().options.dropKey.boundKey.getCode()) {
                secondaryIgnoreTyping = true;
                dropKeyPressed = true;
            } else if (input.key() == MinecraftClient.getInstance().options.swapHandsKey.boundKey.getCode()) {
                secondaryIgnoreTyping = true;
                swapKeyPressed = true;
            }

            // If any of these are true, the user cannot type in the search field
            boolean cannotType = (numberKeyPressed || hotbarKeyPressed || dropKeyPressed || swapKeyPressed) && hoveredSlotHasItem(this.focusedSlot);

            // Recipe book search field logic
            if (options().betterSearching && this.screen instanceof RecipeBookScreen<?> recipeScreen && !MinecraftClient.getInstance().isCtrlPressed()) {
                boolean swapKeyValid = swapKeyPressed && (hoveredSlotHasItem(this.focusedSlot) || this.handler.getSlot(45).hasStack());
                if (!ignoreTyping && !swapKeyValid && !recipeScreen.recipeBook.isOpen() && (this.inventorySearchField == null || !this.inventorySearchField.isFocused())) {
                    recipeScreen.recipeBook.toggleOpen();
                    this.refreshWidgetPositions();
                }
                if (recipeScreen.recipeBook.searchField != null) {
                    recipeScreen.recipeBook.searchField.setFocused(!cannotType);

                    if (recipeScreen.recipeBook.searchField.isFocused()) {
                        cir.setReturnValue(recipeScreen.recipeBook.keyPressed(input) || super.keyPressed(input));
                    }
                }
            }
            // Inventory search field logic
            if (options().inventorySearching && this.inventorySearchField != null) {
                if (!MinecraftClient.getInstance().isCtrlPressed() && this.screen instanceof RecipeBookScreen<?> recipeScreen && recipeScreen.recipeBook.isOpen() && !this.inventorySearchField.isFocused()) {
                    recipeScreen.recipeBook.searchField.setFocused(!cannotType);
                } else if (!secondaryIgnoreTyping && (!MinecraftClient.getInstance().isCtrlPressed() || (MinecraftClient.getInstance().isCtrlPressed() && input.key() == GLFW.GLFW_KEY_A))) {
                    this.inventorySearchField.setFocused(true);
                    this.setFocused(this.inventorySearchField);
                } else if (this.inventorySearchField.isFocused() && cannotType) {
                    this.inventorySearchField.setFocused(false);
                }

                // Unfocus recipe book search field when fromInventory search field is focused
                if (this.screen instanceof RecipeBookScreen<?> recipeScreen && recipeScreen.recipeBook.searchField != null) {
                    if (this.inventorySearchField.isFocused()) {
                        recipeScreen.recipeBook.searchField.setFocused(false);
                    }
                    // Unfocus fromInventory search field when recipe book search field is focused
                    else if (recipeScreen.recipeBook.searchField.isFocused() && this.inventorySearchField != null) {
                        this.inventorySearchField.setFocused(false);
                    }
                }

                if (this.inventorySearchField.isFocused() && this.inventorySearchField.keyPressed(input)) {
                    cir.setReturnValue(true);
                }
            }

            // Chest search field logic
            if (options().chestSearching && isContainerScreen(this.screen)) {
                if (!secondaryIgnoreTyping && (!MinecraftClient.getInstance().isCtrlPressed() || (MinecraftClient.getInstance().isCtrlPressed() && input.key() == GLFW.GLFW_KEY_A))) {
                    this.containerSearchField.setFocused(true);
                } else if (this.containerSearchField.isFocused() && cannotType) {
                    this.containerSearchField.setFocused(false);
                }

                if (this.containerSearchField.isFocused() && this.containerSearchField.keyPressed(input)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    /**
     * Allows correct functionality for typing into search field without clicking on it.
     */
    @Override
    public boolean charTyped(CharInput input) {
        if (options().chestSearching && this.containerSearchField != null && this.containerSearchField.isFocused()) {
            return this.containerSearchField.charTyped(input);
        }
        return super.charTyped(input);
    }

    /**
     * Saves the current text in {@code searchField.getText()} to memory so it can be referenced when opening a container screen again.
     */
    @Inject(method = "close", at = @At("TAIL"))
    private void saveSearchText(CallbackInfo ci) {
        if (options().saveSearchText) {
            if (isInventoryScreen(this.screen) && this.inventorySearchField != null) {
                QoQ.SAVED_TEXT = this.inventorySearchField.getText();
            } else if (isContainerScreen(this.screen) && this.containerSearchField != null) {
                QoQ.SAVED_TEXT = this.containerSearchField.getText();
            }
        }
    }

    /**
     * Ensures that the search field text isn't cleared when resizing.
     */
    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if (this.containerSearchField != null) {
            // Get current text and focused status
            String text = this.getSearchFieldText();
            boolean refocus = this.containerSearchField.isFocused();
            // Refresh screen (or resize)
            this.init(client, width, height);
            // Reset text and focused status
            this.containerSearchField.setText(text);
            this.containerSearchField.setFocused(refocus);
        }
    }
}