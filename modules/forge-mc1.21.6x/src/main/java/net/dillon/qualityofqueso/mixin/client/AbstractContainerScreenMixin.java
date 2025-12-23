package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.screen.gui.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
import static net.dillon.qualityofqueso.main.QoQ.saveAll;
import static net.dillon.qualityofqueso.util.ButtonUtil.*;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Shadow
    protected int imageWidth;
    @Shadow
    protected int topPos;
    @Shadow
    protected int titleLabelY;

    @Shadow
    @Final
    protected T menu;

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Shadow
    @Nullable
    public abstract Slot getSlotUnderMouse();

    @Unique
    private final AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
    @Unique
    private EditBox containerSearchField;
    @Unique
    private EditBox inventorySearchField;
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
    private Container container;

    public AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    /**
     * Creates and initializes the search field and container button.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (modEnabled(this.minecraft)) {
            if (isContainerScreen(this.screen)) {
                // Determine fromInventory variable; if instance ShulkerBoxScreen, fromInventory is the shulker box's fromInventory
                if (this.screen instanceof ShulkerBoxScreen shulkerBoxScreen) {
                    this.container = shulkerBoxScreen.getMenu().container;
                }
                // If it's GenericContainerScreen, it's the generic container (or most likely chest/barrel)'s fromInventory
                else if (this.screen instanceof ContainerScreen genericContainerScreen) {
                    this.container = genericContainerScreen.getMenu().getContainer();
                }
                // Otherwise, fromInventory is null
                else {
                    this.container = null;
                }

                if (ModClientOptions.CHEST_SEARCHING.get()) {
                    this.containerSearchField = this.initializeSearchField(false);
                    this.addRenderableWidget(this.containerSearchField);
                }
            } else if (isInventoryScreen(this.screen)) {
                this.container = this.minecraft.player.getInventory();
                if (ModClientOptions.INVENTORY_SEARCHING.get()) {
                    this.inventorySearchField = this.initializeSearchField(true);
                    this.addRenderableWidget(this.inventorySearchField);
                }
            }
        }
    }

    /**
     * Initializes a search field widget.
     */
    @Unique
    private SearchField initializeSearchField(boolean inventory) {
        return new SearchField(this.font, this.width / 2 + getBarWidth(this.imageWidth) / 2 - (inventory ? 60 : 64), this.topPos + this.titleLabelY - 2);
    }

    /**
     * @return the {@code searchField namespace} text.
     */
    @Unique
    private String getSearchFieldText() {
        return this.inventorySearchField != null ? this.inventorySearchField.getValue() : this.containerSearchField != null ? this.containerSearchField.getValue() : "";
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
        int containerSize = getContainerSize(this.container);
        int totalSlots = getTotalSlots(this.menu);

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
            Slot fromSlot = this.menu.getSlot(i);
            ItemStack fromStack = fromSlot.getItem();

            if ((this.containerSearchField != null || this.inventorySearchField != null) && !this.getSearchFieldText().isEmpty() && !this.search(this.getSearchFieldText(), fromSlot, false)) {
                continue; // Skip container slot if query not found via search
            } else if (!ModClientOptions.INCLUDE_HOTBAR.get()) {
                if (drop) {
                    if (isExcludedSlot(this.screen, fromSlot.index) || isInventoryHotbarSlot(isInventoryScreen(this.screen), fromSlot.index)) {
                        continue; // If dropping from InventoryScreen, and it's an excluded slot AND fromInventory hotbar slot, skip slot and continue
                    }
                } else if (!toInventory && isHotbarSlot(fromEnd, fromSlot.index)) {
                    continue; // Otherwise, check if it's a hotbar slot in normal container
                }
            }

            if (!fromStack.isEmpty()) {
                for (int j = toStart; j < toEnd; j++) {
                    Slot toSlot = this.menu.getSlot(j);
                    // Quickly swap items in container
                    if (drop || toSlot.getItem().isEmpty()) {
                        // Only transfer items if the query matches whatever the cursor is holding
                        // IF DROP, CANNOT THROW ITEMS IF CURSOR STACK ISN'T EMPTY. VANILLA FEATURE, CAN'T WORK AROUND IT (for now, anyone know a solution?)
                        ClickType slotActionType = drop ? ClickType.THROW : ClickType.QUICK_MOVE;
                        if (!this.menu.getCarried().isEmpty()) {
                            if (fromStack.is(this.menu.getCarried().getItem())) {
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
                                         @Nullable Inventory playerInventory, AbstractWidget button) {
        // If button is null, return false because there is no button to be checked
        if (button == null) {
            return false;
        }
        // Determine fromInventory size to run through
        int size = isPlayerInventory ? playerInventory.getNonEquipmentItems().size() : this.container.getContainerSize();
        int filledSlots = 0;
        for (int i = 0; i < size; i++) {
            // If slot is not empty, the button should be active
            // Increment J and make button active
            ItemStack stack = isPlayerInventory ? playerInventory.getItem(i) : this.menu.getSlot(i).getItem();
            // Handle cursor stack
            boolean isShulkerScreen = this.screen instanceof ShulkerBoxScreen;
            boolean isCursorShulker = isShulkerScreen && this.menu.getCarried().is(ItemTags.SHULKER_BOXES);
            boolean isStackShulker = isShulkerScreen && stack.is(ItemTags.SHULKER_BOXES);
            if (!this.menu.getCarried().isEmpty()) {
                if (this.getSearchFieldText().isEmpty() && stack.is(this.menu.getCarried().getItem()) && !isCursorShulker) {
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
    private boolean areAllSlotsUnavailable(boolean isPlayerInventory, @Nullable Inventory playerInventory) {
        int foundQuerys = 0;
        List<Slot> playerSlots = new ArrayList<>();
        // If checking player fromInventory, loop through all player fromInventory slots to determine if slot is unavailable
        if (isPlayerInventory) {
            for (Slot s : this.menu.slots) {
                if (s.container == playerInventory) {
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
            for (int i = 0; i < this.container.getContainerSize(); i++) {
                Slot slot = this.menu.getSlot(i);
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
        ItemStack stack = slot.getItem();

        // If slot is empty, return false (slot is unavailable)
        // If include hotbar is off, return false if hotbar slot
        if (stack.isEmpty() ||
                !ModClientOptions.INCLUDE_HOTBAR.get() && ModClientOptions.SEARCH_INVENTORY.get() && isHotbarSlot(this.menu.slots.size(), dropping ? slot.index + 1 : slot.index) &&
                        (!dropping || !isInventoryScreen(this.screen) || slot.index != 45)) {
            return false;
        }

        String itemName = stack.getHoverName().getString().toLowerCase();
        String customName = stack.getCustomName() != null ? stack.getCustomName().getString().toLowerCase() : "";

        String[] terms = searchQuery.split(",");
        // If slot contains a comma, for each query searched (separated by each comma), return true if search query'namespace find an query (make slot available)
        for (int i = 0; i < terms.length; i++) {
            String term = terms[i];
            if (searchQuery.startsWith(":")) {
                String query = term.substring(i == 0 ? 1 : 0);
                if (itemName.matches(query.trim().toLowerCase()) || customName.matches(query.trim().toLowerCase())) {
                    return true;
                }
            } else {
                if (itemName.contains(term.trim().toLowerCase()) || customName.contains(term.trim().toLowerCase())) {
                    return true;
                }
            }
        }

        // As long as slot doesn't contain whatever is searched (beginning after "!"), return true (slot is available)
        if (searchQuery.startsWith("!")) {
            return !itemName.contains(searchQuery.substring(1)) && !customName.contains(searchQuery.substring(1));
        }

        // If tag contains search query and stack is in returned tag, slot is available
        if (searchQuery.startsWith("#")) {
            String tagSearch = searchQuery.substring(1);
            RegistryAccess lookup = Minecraft.getInstance().level.registryAccess();
            Registry<Item> itemRegistry = lookup.lookupOrThrow(Registries.ITEM);

            for (HolderSet.Named<Item> tag : itemRegistry.getTags().toList()) {
                ResourceLocation location = tag.key().location();
                if (location.getPath().toLowerCase().contains(tagSearch) || location.toString().toLowerCase().contains(tagSearch)) {
                    if (stack.is(tag.key())) {
                        return true;
                    }
                }
            }
        }

        // Enchanted book searching logic
        if (stack.isEnchanted() || stack.is(Items.ENCHANTED_BOOK)) {
            ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
            for (Holder<Enchantment> enchantment : enchantments.keySet()) {
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
    @Inject(method = "renderContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlightFront(Lnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
    private void grayOutSlot(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        boolean inventorySearchFieldPresent = this.inventorySearchField != null;
        if ((this.containerSearchField != null || inventorySearchFieldPresent)) {
            for (int i = 0; i < getInventorySize(this.menu, this.container); i++) {
                Slot slot = this.menu.getSlot(i);
                // Otherwise, gray out slots that don't match the search
                if (!this.getSearchFieldText().isEmpty() && !this.search(this.getSearchFieldText(), slot, this.inventorySearchField != null)) {
                    makeSlotUnavailable(graphics, slot, false);
                }
            }
        }
    }

    /**
     * Handles rendering, such as the search field and transferring fromInventory button textures.
     */
    @Inject(method = "renderContents", at = @At("TAIL"))
    private void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        // Render the search field
        if (this.containerSearchField != null) {
            this.containerSearchField.render(graphics, mouseX, mouseY, deltaTicks);
        }
        // Render fromInventory search field
        if (this.inventorySearchField != null) {
            this.inventorySearchField.setX(this.width / 2 + getBarWidth(this.imageWidth) / 2 - (
                    this.screen instanceof AbstractRecipeBookScreen<?> recipeBookScreen && recipeBookScreen.recipeBookComponent.isVisible() ? -16 : 60
            ));
            this.inventorySearchField.render(graphics, mouseX, mouseY, deltaTicks);
        }
        if (modEnabled(this.minecraft)) {
            if (ModClientOptions.INVENTORY_MANAGEMENT.get()) {
                Inventory playerInventory = this.minecraft.player.getInventory();
                boolean containerScreen = isContainerScreen(this.screen);
                boolean inventoryScreen = isInventoryScreen(this.screen);
                boolean validScreen = containerScreen || inventoryScreen;
                if (containerScreen) {

                    /* --- */

                    // TRANSFER CONTAINER BUTTON (container -> inventory)
                    this.transferContainerButton = this.addWidget(
                            new TransferButton(
                                    this.menu,
                                    this.font,
                                    this.getSearchFieldText(),
                                    getManagementButtonX(this.screen, this.imageWidth, this.width),
                                    getManagementButtonY(this.screen, this.container, this.topPos, this.titleLabelY),
                                    "transfer_container",
                                    b -> this.transferItems(true),
                                    () -> !isContainerFull(this.menu, this.container, true) && this.shouldButtonBeActive(false, null, this.transferContainerButton)));

                    this.transferContainerButton.render(graphics, mouseX, mouseY, deltaTicks);

                    /* --- */

                    // TRANSFER INVENTORY BUTTON (inventory -> container)
                    this.transferInventoryButton = this.addWidget(
                            new TransferButton(
                                    this.menu,
                                    this.font,
                                    this.getSearchFieldText(),
                                    getButtonX(this.transferContainerButton) - 12,
                                    getManagementButtonY(this.screen, this.container, this.topPos, titleLabelY),
                                    "transfer_inventory",
                                    b -> this.transferItems(false),
                                    () -> !isContainerFull(this.menu, this.container, false) && this.shouldButtonBeActive(true, playerInventory, this.transferInventoryButton)
                            ));

                    this.transferInventoryButton.render(graphics, mouseX, mouseY, deltaTicks);
                }

                /* --- */

                if (validScreen) {

                    // INCLUDE HOTBAR BUTTON
                    if (ModClientOptions.INVENTORY_SEARCHING.get() || ModClientOptions.QUICK_DROP.get()) {
                        this.includeHotbarButton = this.addWidget(
                                new IncludeHotbarButton(
                                        this.menu,
                                        this.font,
                                        this.getSearchFieldText(),
                                        getManagementButtonX(this.screen, this.imageWidth, this.width) - 24,
                                        getManagementButtonY(this.screen, this.container, this.topPos, this.titleLabelY),
                                        "include_hotbar",
                                        b -> {
                                            ModClientOptions.INCLUDE_HOTBAR.set(!ModClientOptions.INCLUDE_HOTBAR.get());
                                            saveAll();
                                        }));

                        this.includeHotbarButton.render(graphics, mouseX, mouseY, deltaTicks);
                    }

                    /* --- */

                    // SWAP BUTTON
                    if (ModClientOptions.SWAPPING.get() && containerScreen) {
                        this.swapButton = this.addWidget(
                                new SwapButton(
                                        this.menu,
                                        this.font,
                                        this.getSearchFieldText(),
                                        getButtonX(this.includeHotbarButton) - 12,
                                        getManagementButtonY(this.screen, this.container, this.topPos, this.titleLabelY),
                                        "swap",
                                        b -> swapItems(this.menu, this.container),
                                        () -> {
                                            // Decrement swap cooldown
                                            if (swapCooldown > 0) {
                                                swapCooldown--;
                                            }
                                            return (getContainerSize(this.container) != 27 || isAnySlotFilled(this.menu, false, 27, 54))
                                                    && this.menu.getCarried().isEmpty() && this.getSearchFieldText().isEmpty()
                                                    && this.shouldButtonBeActive(false, null, this.swapButton)
                                                    && this.shouldButtonBeActive(true, playerInventory, this.swapButton);
                                        }
                                ));

                        this.swapButton.render(graphics, mouseX, mouseY, deltaTicks);
                    }

                    /* --- */

                    // QUICK DROP BUTTON
                    if (ModClientOptions.QUICK_DROP.get()) {
                        this.quickDropButton = this.addWidget(
                                new QuickDropButton(
                                        this.menu,
                                        this.font,
                                        this.getSearchFieldText(),
                                        getButtonX(this.includeHotbarButton) - (containerScreen && ModClientOptions.SWAPPING.get() ? 24 : 12),
                                        getManagementButtonY(this.screen, this.container, this.topPos, this.titleLabelY),
                                        "quick_drop",
                                        b -> this.dropItems(!containerScreen),
                                        () -> (isInventoryScreen(this.screen) ?
                                                isAnySlotFilled(this.menu, true, 9, 36) :
                                                isAnySlotFilled(this.menu, false, 0, getContainerSize(this.container)))
                                                && this.menu.getCarried().isEmpty()
                                                && this.shouldButtonBeActive(!containerScreen, containerScreen ? null : playerInventory, this.quickDropButton)
                                ));

                        this.quickDropButton.render(graphics, mouseX, mouseY, deltaTicks);
                    }
                }
            }
        }
    }

    /**
     * Renders tag tooltips to all slots if searching by tag {@code searchQuery.startsWith(#)}.
     */
    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void addAllItemTagsToTooltip(GuiGraphics graphics, int x, int y, CallbackInfo ci) {
        String searchQuery = this.getSearchFieldText();
        if (this.containerSearchField != null) {
            // Exit if search query doesn't start with #
            if (!searchQuery.startsWith("#")) {
                return;
            }

            // Exit if hovered slot doesn't have an query in it
            Slot hoveredSlot = this.getSlotUnderMouse();
            if (hoveredSlot == null || !hoveredSlot.hasItem()) {
                return;
            }

            ItemStack stack = hoveredSlot.getItem();
            Registry<Item> itemRegistry = this.minecraft.level.registryAccess().lookupOrThrow(Registries.ITEM);

            List<Component> originalTooltip = stack.getTooltipLines(Item.TooltipContext.EMPTY, this.minecraft.player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
            boolean foundTags = false;
            // Loop through all tags loaded (vanilla and modded)
            for (HolderSet.Named<Item> tag : itemRegistry.getTags().toList()) {
                if (stack.is(tag.key())) {
                    // Add each tag to the query hovered
                    String tagString = "#" + tag.key().location();
                    originalTooltip.add(1, Component.literal(tagString).withStyle(ChatFormatting.LIGHT_PURPLE));
                    foundTags = true;
                }
            }

            // If tags were found in the query add it to the tooltip and render
            // cancel out original method to prevent overlapping tooltips
            if (foundTags) {
                graphics.setTooltipForNextFrame(this.font, originalTooltip, Optional.empty(), x, y);
                ci.cancel();
            }
        }
    }

    /**
     * Closes the screen when clicking outside of menu.
     */
    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void closeButtonOnClickOutOfBounds(Slot slot, int slotId, int button, ClickType
            actionType, CallbackInfo ci) {
        if (modEnabled(this.minecraft) && ModClientOptions.BETTER_GUI_EXIT.get() && this.menu.getCarried().isEmpty() && button == 0 && slot == null) {
            this.onClose();
        }
    }

    /**
     * Implements functionality for the {@code Quick Equip right-click feature,} and handles fromInventory search field.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleMouseClicking(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (modEnabled(this.minecraft)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && this.hoveredSlot != null && isQuicklyEquippable(this.hoveredSlot.getItem())) {
                quickEquip(this.screen, this.hoveredSlot);
                cir.setReturnValue(true);
            }
            // Refocus fromInventory search field if clicked.
            if (this.inventorySearchField != null && this.inventorySearchField.mouseClicked(mouseX, mouseY, button)) {
                this.inventorySearchField.setFocused(true);
            }
        }
    }

    /**
     * Handles key pressing correctly and implements functionality for the {@link ModKeybinds#QUICK_EQUIP} keybind.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (modEnabled(this.minecraft)) {
            if (Screen.hasControlDown()) {
                if (ModClientOptions.SHORTCUT_KEYS.get()) {
                    if (this.transferContainerButton != null && this.transferContainerButton.active && keyCode == ModKeybinds.MOVE_CONTAINER.getKey().getValue()) {
                        this.transferItems(true);
                    }

                    if (this.transferInventoryButton != null && this.transferInventoryButton.active && keyCode == ModKeybinds.MOVE_INVENTORY.getKey().getValue()) {
                        this.transferItems(false);
                    }

                    if (this.swapCooldown == 0 && this.swapButton != null && this.swapButton.active && keyCode == ModKeybinds.SWAP_ITEMS.getKey().getValue()) {
                        swapItems(this.menu, this.container);
                        this.swapCooldown = 120;
                    }
                    if (this.quickDropButton != null && this.quickDropButton.active && Screen.hasAltDown() && keyCode == GLFW.GLFW_KEY_Q) {
                        this.dropItems(!isContainerScreen(this.screen));
                    }
                }
            }

            // Quick equip logic
            if (keyCode == ModKeybinds.QUICK_EQUIP.getKey().getValue()) {
                quickEquip(this.screen, this.hoveredSlot);
            }

            // Prevent E from typing entirely in fromInventory screens
            if (keyCode == GLFW.GLFW_KEY_E && ModClientOptions.PREVENT_E_FROM_TYPING.get() && (isInventoryScreen(this.screen) || isCreativeInventoryScreen(this.screen))) {
                this.onClose();
                cir.setReturnValue(true);
            }

            // Declare typing variables
            // Both of these variables apply to disallowed keys and hotbar switching
            boolean ignoreTyping = hoveredSlotHasItem(this.hoveredSlot); // Basic ignore typing variable; applies to recipe book screens only.
            boolean secondaryIgnoreTyping = false; // Secondary ignore typing variable; applies to "T" and "E" keys and chest searching screens only.

            // These variables only apply to the chest search bar
            boolean numberKeyPressed = false; // Determines if a number key was pressed
            boolean hotbarKeyPressed = false; // Determines if a hotbar key was pressed
            boolean dropKeyPressed = false; // Determines if the drop key was pressed
            boolean swapKeyPressed = false; // Determines if the swap item key was pressed

            // If a "disallowed key" is pressed, ignoreTyping and secondaryIgnoreTyping become true.
            for (int key : QoQ.allDisallowedKeys) {
                if (keyCode == key) {
                    ignoreTyping = true;
                    secondaryIgnoreTyping = true;
                    break;
                }
            }

            // handle switching items from hotbar to another containerSlot in fromInventory; cancel out typing if an query can be moved
            if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
                for (int i = 0; i < 9; i++) {
                    if (this.minecraft.options.keyHotbarSlots[i].matches(keyCode, scanCode)) {
                        ignoreTyping = true;
                        secondaryIgnoreTyping = true;
                        hotbarKeyPressed = true;
                        break;
                    }
                }
                if (this.screen instanceof InventoryScreen && keyCode == ModKeybinds.QUICK_EQUIP.getKey().getValue()) {
                    ignoreTyping = true;
                }
            }

            // Handle 'T' and 'E' keys
            for (int key : QoQ.popularKeys) {
                if (keyCode == key) {
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
                if (keyCode == key) {
                    numberKeyPressed = true;
                    break;
                }
            }

            // Check if drop key or swap hands key was pressed
            if (keyCode == Minecraft.getInstance().options.keyDrop.getKey().getValue()) {
                secondaryIgnoreTyping = true;
                dropKeyPressed = true;
            } else if (keyCode == Minecraft.getInstance().options.keySwapOffhand.getKey().getValue()) {
                secondaryIgnoreTyping = true;
                swapKeyPressed = true;
            }

            // If any of these are true, the user cannot type in the search field
            boolean cannotType = (numberKeyPressed || hotbarKeyPressed || dropKeyPressed || swapKeyPressed) && hoveredSlotHasItem(this.hoveredSlot);

            // Recipe book search field logic
            if (ModClientOptions.BETTER_SEARCHING.get() && this.screen instanceof AbstractRecipeBookScreen<?> recipeScreen && !Screen.hasControlDown()) {
                boolean swapKeyValid = swapKeyPressed && (hoveredSlotHasItem(this.hoveredSlot) || this.menu.getSlot(45).hasItem());
                if (!ignoreTyping && !swapKeyValid && !recipeScreen.recipeBookComponent.isVisible() && (this.inventorySearchField == null || !this.inventorySearchField.isFocused())) {
                    recipeScreen.recipeBookComponent.toggleVisibility();
                    this.repositionElements();
                }
                if (recipeScreen.recipeBookComponent.searchBox != null) {
                    if (Screen.hasShiftDown()) {
                        recipeScreen.recipeBookComponent.searchBox.setFocused(false);
                        return;
                    }
                    recipeScreen.recipeBookComponent.searchBox.setFocused(!cannotType);

                    if (recipeScreen.recipeBookComponent.searchBox.isFocused()) {
                        cir.setReturnValue(recipeScreen.recipeBookComponent.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers));
                    }
                }
            }
            // Inventory search field logic
            if (ModClientOptions.INVENTORY_SEARCHING.get() && this.inventorySearchField != null) {
                if (!Screen.hasControlDown() && this.screen instanceof AbstractRecipeBookScreen<?> recipeScreen && recipeScreen.recipeBookComponent.isVisible() && !this.inventorySearchField.isFocused()) {
                    recipeScreen.recipeBookComponent.searchBox.setFocused(!cannotType);
                } else if (!secondaryIgnoreTyping && (!Screen.hasControlDown() || (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_A))) {
                    this.inventorySearchField.setFocused(true);
                    this.setFocused(this.inventorySearchField);
                } else if (this.inventorySearchField.isFocused() && cannotType) {
                    this.inventorySearchField.setFocused(false);
                }

                // Unfocus recipe book search field when fromInventory search field is focused
                if (this.screen instanceof AbstractRecipeBookScreen<?> recipeScreen && recipeScreen.recipeBookComponent.searchBox != null) {
                    if (this.inventorySearchField.isFocused()) {
                        recipeScreen.recipeBookComponent.searchBox.setFocused(false);
                    }
                    // Unfocus fromInventory search field when recipe book search field is focused
                    else if (recipeScreen.recipeBookComponent.searchBox.isFocused() && this.inventorySearchField != null) {
                        this.inventorySearchField.setFocused(false);
                    }
                }

                if (this.inventorySearchField.isFocused() && this.inventorySearchField.keyPressed(keyCode, scanCode, modifiers)) {
                    cir.setReturnValue(true);
                }
            }

            // Chest search field logic
            if (ModClientOptions.CHEST_SEARCHING.get() && isContainerScreen(this.screen)) {
                if (!secondaryIgnoreTyping && (!Screen.hasControlDown() || (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_A))) {
                    this.containerSearchField.setFocused(true);
                } else if (this.containerSearchField.isFocused() && cannotType) {
                    this.containerSearchField.setFocused(false);
                }

                if (this.containerSearchField.isFocused() && this.containerSearchField.keyPressed(keyCode, scanCode, modifiers)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    /**
     * Allows correct functionality for typing into search field without clicking on it.
     */
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (ModClientOptions.CHEST_SEARCHING.get() && this.containerSearchField != null && this.containerSearchField.isFocused()) {
            return this.containerSearchField.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    /**
     * Saves the current text in {@code searchField.getText()} to memory so it can be referenced when opening a container screen again.
     */
    @Inject(method = "onClose", at = @At("TAIL"))
    private void saveSearchText(CallbackInfo ci) {
        if (ModClientOptions.SAVE_SEARCH_TEXT.get()) {
            if (isInventoryScreen(this.screen) && this.inventorySearchField != null) {
                QoQ.SAVED_TEXT = this.inventorySearchField.getValue();
            } else if (isContainerScreen(this.screen) && this.containerSearchField != null) {
                QoQ.SAVED_TEXT = this.containerSearchField.getValue();
            }
        }
    }

    /**
     * Ensures that the search field text isn't cleared when resizing.
     */
    @Override
    public void resize(Minecraft client, int width, int height) {
        if (this.containerSearchField != null) {
            // Get current text and focused status
            String text = this.getSearchFieldText();
            boolean refocus = this.containerSearchField.isFocused();
            // Refresh screen (or resize)
            this.init(client, width, height);
            // Reset text and focused status
            this.containerSearchField.setValue(text);
            this.containerSearchField.setFocused(refocus);
        }
    }
}