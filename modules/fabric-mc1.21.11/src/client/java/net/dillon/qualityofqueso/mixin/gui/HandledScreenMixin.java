package net.dillon.qualityofqueso.mixin.gui;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.screen.gui.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
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

import java.util.*;

import static net.dillon.qualityofqueso.main.QoQ.*;
import static net.dillon.qualityofqueso.util.ButtonUtil.*;

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
    private TransferButton sortButton;
    @Unique
    private int swapCooldown = 0;
    @Unique
    private Inventory inventory;
    @Unique
    private final Set<Integer> excludedSlots = new HashSet<>();
    @Unique
    private boolean excludedAll = false;

    public HandledScreenMixin(Text title) {
        super(title);
    }

    /**
     * Creates and initializes the search field and container button.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (!modEnabled(this.client)) {
            return;
        }

        // If it's BrewingStandScreen, fromInventory is the brewing stand's inventory
        if (this.screen instanceof BrewingStandScreen brewingStandScreen) {
            this.inventory = brewingStandScreen.getScreenHandler().inventory;
        } else if (this.screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen) {
            this.inventory = abstractFurnaceScreen.getScreenHandler().getOutputSlot().inventory;
        }
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

        if (isValidScreen(this.screen) && options().saveExcludedSlots && this.inventory != null) {
            for (int i : this.excludedSlots) {
                this.excludedSlots.remove(i);
            }
            if (SAVED_EXCLUDED_SLOTS.containsKey(getTotalSlots(this.handler))) {
                this.excludedSlots.addAll(SAVED_EXCLUDED_SLOTS.get(getTotalSlots(this.handler)));
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

        if (isBrewingStandScreen(this.screen)) {
            fromStart = 0;
            fromEnd = 3;
        }

        if (isFurnaceScreen(this.screen)) {
            fromStart = 2;
            fromEnd = 3;
        }

        // Normal logic (dropping and quick move)
        for (int i = fromStart; i < fromEnd; i++) {
            Slot fromSlot = this.handler.getSlot(i);
            ItemStack fromStack = fromSlot.getStack();

            // Skip player-chosen excluded slots
            if (shouldSkipSlot(fromSlot.id, this.excludedSlots)) {
                continue;
            }

            if ((this.containerSearchField != null || this.inventorySearchField != null) && !this.getSearchFieldText().isEmpty() && !this.search(this.getSearchFieldText(), fromSlot, false)) {
                continue; // Skip container slot if query not found via search
            } else if (!options().includeHotbar) {
                if (drop) {
                    if (isExcludedSlot(this.screen, fromSlot.id) || isInventoryHotbarSlot(isInventoryScreen(this.screen), fromSlot.id)) {
                        continue; // If dropping from InventoryScreen, and it's an excluded slot AND fromInventory hotbar slot, skip slot and continue
                    }
                } else if (!toInventory && isHotbarSlot(fromEnd, fromSlot.id)) {
                    continue; // Otherwise, check if it's a hotbar slot in normal container
                }
            }

            if (!fromStack.isEmpty()) {
                for (int j = toStart; j < toEnd; j++) {
                    // Only transfer items if the query matches whatever the cursor is holding
                    SlotActionType slotActionType = drop ? SlotActionType.THROW : SlotActionType.QUICK_MOVE;
                    ItemStack cursorStack = getCursorStack(this.screen);

                    if (!cursorStack.isEmpty()) {
                        if (canMoveCursorItem(fromStack, cursorStack)) {
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

    /**
     * @return if a container can swap with inventory.
     */
    @Unique
    private boolean canSwap() {
        PlayerInventory playerInventory = this.client.player.getInventory();
        return (getContainerSize(this.inventory) != 27 || isAnySlotFilled(this.handler, false, 27, 54))
                && getCursorStack(this.screen).isEmpty() && this.getSearchFieldText().isEmpty()
                && this.shouldButtonBeActive(false, null)
                && this.shouldButtonBeActive(true, playerInventory);
    }

    /**
     * @return if a container can be sorted.
     */
    @Unique
    private boolean canSort(boolean checkForButton) {
        boolean excludedContainerSlot = false;
        for (int id : this.excludedSlots) {
            if (id <= getTotalSlots(this.handler) - 37) {
                excludedContainerSlot = true;
                break;
            }
        }
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.handler.getSlot(i).getStack();
            if (stack.isIn(ItemTags.BUNDLES)) {
                return false;
            }
        }
        return isContainerScreen(this.screen)
                && isAnySlotFilled(this.handler, false, 0, getContainerSize(this.inventory))
                && this.getSearchFieldText().isEmpty()
                && !excludedContainerSlot
                && getCursorStack(this.screen).isEmpty()
                && (!checkForButton || this.shouldButtonBeActive(false, null));
    }

    /**
     * @return {@code true} if the {@code fromInventory} has a match with the search query.
     */
    @Unique
    private boolean shouldButtonBeActive(boolean isPlayerInventory, @Nullable PlayerInventory playerInventory) {
        // Determine fromInventory size to run through
        int size = isPlayerInventory ? playerInventory.getMainStacks().size() : this.inventory.size();
        int filledSlots = 0;
        if (this.screen instanceof BrewingStandScreen brewingScreen) {
            size = brewingScreen.getScreenHandler().inventory.size() - 2;
        } else if (this.screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen
                && !abstractFurnaceScreen.getScreenHandler().getOutputSlot().hasStack()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            // If slot is not empty, the button should be active
            // Increment J and make button active
            Slot slot = this.handler.getSlot(i);
            ItemStack stack = isPlayerInventory ? playerInventory.getStack(i) : slot.getStack();
            ItemStack cursorStack = getCursorStack(this.screen);
            // Handle cursor stack
            boolean isShulkerScreen = isShulkerBoxScreen(this.screen);
            boolean isCursorShulker = isShulkerScreen && cursorStack.isIn(ItemTags.SHULKER_BOXES);
            boolean isStackShulker = isShulkerScreen && stack.isIn(ItemTags.SHULKER_BOXES);
            boolean isCursorBundle = !isPlayerInventory && cursorStack.isIn(ItemTags.BUNDLES);
            boolean isStackBundle = !isPlayerInventory && stack.isIn(ItemTags.BUNDLES);
            if (!cursorStack.isEmpty()) {
                if (canMoveCursorItem(stack, cursorStack) && !isCursorShulker) {
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
                // Skip checking player-chosen excluded slots
                if (shouldSkipSlot(slot.id, this.excludedSlots)) {
                    continue;
                }
                if (this.search(this.getSearchFieldText(), slot, false)) {
                    // prevent shulker boxes from counting as a found query when in a shulker box screen
                    if (!(isShulkerBoxScreen(this.screen) && slot.getStack().isIn(ItemTags.SHULKER_BOXES))) {
                        foundQuerys++; // increment J if query found in slot
                    }
                }
            }
        } else {
            // Otherwise, loop through the container fromInventory and increment J if query found inside
            for (int i = 0; i < this.inventory.size(); i++) {
                Slot slot = this.handler.getSlot(i);
                // Skip checking player-chosen excluded slots
                if (shouldSkipSlot(slot.id, this.excludedSlots)) {
                    continue;
                }
                if (this.search(this.getSearchFieldText(), slot, false)) {
                    foundQuerys++;
                }
            }
        }
        // If J == 0 then no query was found (OR if ALL slots are filled in fromInventory/container), returning true for all slots are unavailable
        // Also if player manually excluded all slots for whatever reason, then all slots are unavailable
        return foundQuerys == 0 || this.excludedSlots.size() == this.handler.slots.size();
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
        boolean hasPositiveTerm = false;
        for (String s : terms) {
            // Initialize term variable
            String term = s;
            term = term.trim().toLowerCase();

            // As long as slot doesn't contain whatever is searched (beginning after "!"), return true (slot is available)
            if (!term.startsWith("!")) {
                hasPositiveTerm = true;
            }
            if (term.startsWith("!")) {
                String forbidden = term.substring(1);
                if (itemName.contains(forbidden) || customName.contains(forbidden)) {
                    return false;
                }
                continue; // Continue searching for the rest
            }

            // If tag contains search query and stack is in returned tag, slot is available
            if (term.startsWith("#")) {
                String tagSearch = term.substring(1);
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
                    if (fullName.toLowerCase().contains(term)) {
                        return true;
                    }
                }
            }

            if (term.startsWith(":")) {
                String query = term.substring(1);
                if (itemName.matches(query) || customName.matches(query)) {
                    return true;
                }
            } else {
                if (itemName.contains(term) || customName.contains(term)) {
                    return true;
                }
            }
        }

        // If slot contains whatever is searched, return true (slot is available)
        return !hasPositiveTerm;
    }

    /**
     * Excludes selected slots.
     */
    @Unique
    private void excludeSlot(Click click, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = this.getSlotAt(click.x(), click.y());
        boolean onlyInclude = MinecraftClient.getInstance().isAltPressed() && MinecraftClient.getInstance().isShiftPressed();
        if (slot != null) {
            if (onlyInclude && !this.excludedAll) {
                for (Slot s : this.handler.slots) {
                    this.excludedSlots.add(s.id);
                }
                this.excludedAll = true;
            }
            if (click.button() == 1) {
                if (onlyInclude) {
                    this.excludedSlots.add(slot.id);
                } else {
                    this.excludedSlots.remove(slot.id);
                }
            } else {
                if (onlyInclude) {
                    this.excludedSlots.remove(slot.id);
                } else {
                    this.excludedSlots.add(slot.id);
                }
            }
            cir.setReturnValue(true);
        }
    }

    /**
     * Grays out any containerSlot which doesn't contain the query name being searched.
     */
    @Inject(method = "renderMain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlightFront(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void grayOutSlot(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!isValidScreen(this.screen)) {
            return;
        }

        boolean inventorySearchFieldPresent = this.inventorySearchField != null;
        for (int i = 0; i < getInventorySize(this.handler, this.inventory); i++) {
            Slot slot = this.handler.getSlot(i);
            // Gray out hotbar slots if include hotbar is off and one of the transfer buttons are hovered
            boolean alreadyExcluded = false;
            if ((this.containerSearchField != null || inventorySearchFieldPresent)
                    && !this.getSearchFieldText().isEmpty()
                    && !this.search(this.getSearchFieldText(), slot, inventorySearchFieldPresent)) {
                renderSlotUnavailable(context, slot, false);
                alreadyExcluded = true;
            }
            // Otherwise, gray out slots that don't match the search
            else if (!options().includeHotbar
                    && (isInventoryScreen(this.screen) ? isInventoryHotbarSlot(true, slot.id) : isHotbarSlot(this.handler.slots.size(), slot.id))
                    && options().transferring.orKeyOnly()) {
                if (shouldGrayout(this.screen, this.transferInventoryButton, this.transferContainerButton, this.includeHotbarButton, slot)) {
                    renderSlotUnavailable(context, slot, slot.hasStack());
                    alreadyExcluded = true;
                }
            }
            // Gray out player-chosen excluded slots
            if (!alreadyExcluded && options().dragToSort) {
                for (int id : this.excludedSlots) {
                    if (slot.id == id) {
                        if (isContainerScreen(this.screen)) {
                            // Don't grayout if CTRL is pressed and transfer keys are bounded
                            if (MinecraftClient.getInstance().isCtrlPressed()
                                    && ModKeybinds.MOVE_CONTAINER.boundKey.getCode() != InputUtil.UNKNOWN_KEY.getCode()
                                    && ModKeybinds.MOVE_INVENTORY.boundKey.getCode() != InputUtil.UNKNOWN_KEY.getCode()) {
                                break;
                            } else if (buttonHoveredActiveOrShiftHeld(this.screen, this.transferContainerButton, false)) {
                                if (slot.id <= getTotalSlots(this.handler) - 37) {
                                    renderSlotUnavailable(context, slot, false);
                                }
                            } else if (buttonHoveredActiveOrShiftHeld(this.screen, this.transferInventoryButton, true)) {
                                if (slot.id >= getTotalSlots(this.handler) - 36) {
                                    renderSlotUnavailable(context, slot, false);
                                }
                            } else {
                                renderSlotUnavailable(context, slot, false);
                            }
                        } else {
                            renderSlotUnavailable(context, slot, false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles rendering, such as the search field and transferring fromInventory button textures.
     */
    @Inject(method = "renderMain", at = @At("TAIL"))
    private void renderWidgets(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!modEnabled(this.client)) {
            return;
        }

        // Decrement swap cooldown
        if (this.swapCooldown > 0) {
            this.swapCooldown--;
        }
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

        PlayerInventory playerInventory = this.client.player.getInventory();
        boolean containerScreen = isContainerScreen(this.screen);
        boolean inventoryScreen = isInventoryScreen(this.screen);
        boolean validScreen = containerScreen || inventoryScreen;
        int buttons = 0;
        if (options().transferring.shortcutOrButton()) {

            /* --- */

            // TRANSFER CONTAINER BUTTON (container -> inventory)
            if (containerScreen || isBrewingStandScreen(this.screen) || isFurnaceScreen(this.screen)) {
                this.transferContainerButton = this.addSelectableChild(
                        new TransferButton(
                                this.handler,
                                this.textRenderer,
                                this.getSearchFieldText(),
                                getManagementButtonX(this.screen, this.backgroundWidth, this.width, buttons),
                                getManagementButtonY(this.screen, this.inventory, this.y, this.titleY),
                                "transfer_container",
                                b -> this.transferItems(true),
                                () -> !isContainerFull(this.handler, this.inventory, true) && this.shouldButtonBeActive(false, null)));

                this.transferContainerButton.render(context, mouseX, mouseY, deltaTicks);
                buttons++;
            }

            /* --- */

            if (containerScreen) {
                // TRANSFER INVENTORY BUTTON (inventory -> container)
                this.transferInventoryButton = this.addSelectableChild(
                        new TransferButton(
                                this.handler,
                                this.textRenderer,
                                this.getSearchFieldText(),
                                getManagementButtonX(this.screen, this.backgroundWidth, this.width, buttons),
                                getManagementButtonY(this.screen, this.inventory, this.y, titleY),
                                "transfer_inventory",
                                b -> this.transferItems(false),
                                () -> !isContainerFull(this.handler, this.inventory, false) && this.shouldButtonBeActive(true, playerInventory)
                        ));

                this.transferInventoryButton.render(context, mouseX, mouseY, deltaTicks);
                buttons++;
            }
        }

        /* --- */

        if (validScreen) {

            // INCLUDE HOTBAR BUTTON
            if ((containerScreen && options().transferring.orKeyOnly()) || ((inventoryScreen && options().inventorySearching) || options().quickDrop.orKeyOnly())) {
                this.includeHotbarButton = this.addSelectableChild(
                        new IncludeHotbarButton(
                                this.handler,
                                this.textRenderer,
                                this.getSearchFieldText(),
                                getManagementButtonX(this.screen, this.backgroundWidth, this.width, buttons),
                                getManagementButtonY(this.screen, this.inventory, this.y, this.titleY),
                                "include_hotbar",
                                b -> {
                                    options().includeHotbar = !options().includeHotbar;
                                    ModClientOptions.CLIENT.save();
                                }));

                this.includeHotbarButton.render(context, mouseX, mouseY, deltaTicks);
                buttons++;
            }

            /* --- */

            // SWAP BUTTON
            if (options().swapping.shortcutOrButton() && containerScreen) {
                this.swapButton = this.addSelectableChild(
                        new SwapButton(
                                this.handler,
                                this.textRenderer,
                                this.getSearchFieldText(),
                                getManagementButtonX(this.screen, this.backgroundWidth, this.width, buttons),
                                getManagementButtonY(this.screen, this.inventory, this.y, this.titleY),
                                "swap",
                                b -> {
                                    if (this.swapCooldown == 0) {
                                        swapItems(this.handler, this.inventory, this.excludedSlots);
                                        this.swapCooldown = 200;
                                    }
                                },
                                this::canSwap
                        ));

                this.swapButton.render(context, mouseX, mouseY, deltaTicks);
                buttons++;
            }

            /* --- */

            // SORT BUTTON
            if (options().containerSorting.shortcutOrButton() && containerScreen) {
                this.sortButton = this.addSelectableChild(
                        new SortButton(
                                this.handler,
                                this.textRenderer,
                                this.getSearchFieldText(),
                                getManagementButtonX(this.screen, this.backgroundWidth, this.width, buttons),
                                getManagementButtonY(this.screen, this.inventory, this.y, this.titleY),
                                "sort",
                                b -> sortItems(this.client),
                                () -> this.canSort(true)
                        ));

                this.sortButton.render(context, mouseX, mouseY, deltaTicks);
                buttons++;
            }

            /* --- */

            // QUICK DROP BUTTON
            if (options().quickDrop.shortcutOrButton()) {
                this.quickDropButton = this.addSelectableChild(
                        new QuickDropButton(
                                this.handler,
                                this.textRenderer,
                                this.getSearchFieldText(),
                                getManagementButtonX(this.screen, this.backgroundWidth, this.width, buttons),
                                getManagementButtonY(this.screen, this.inventory, this.y, this.titleY),
                                "quick_drop",
                                b -> this.dropItems(!containerScreen),
                                () -> (isInventoryScreen(this.screen) ?
                                        isAnySlotFilled(this.handler, true, 9, 36) :
                                        isAnySlotFilled(this.handler, false, 0, getContainerSize(this.inventory)))
                                        && getCursorStack(this.screen).isEmpty()
                                        && this.shouldButtonBeActive(!containerScreen, containerScreen ? null : playerInventory)
                        ));

                this.quickDropButton.render(context, mouseX, mouseY, deltaTicks);
            }
        }
    }

    /**
     * Renders tag tooltips to all slots if searching by tag {@code searchQuery.startsWith(#)}.
     */
    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void addAllItemTagsToTooltip(DrawContext drawContext, int x, int y, CallbackInfo ci) {
        String searchQuery = this.getSearchFieldText();
        if (this.containerSearchField != null || this.inventorySearchField != null) {
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
                    break;
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
        if (modEnabled(this.client) && options().betterGuiExit && getCursorStack(this.screen).isEmpty() && button == 0 && slot == null) {
            this.close();
        }
    }

    /**
     * Implements functionality for the {@code Quick Equip right-click feature,} and handles fromInventory search field.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleMouseClicking(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.client)) {
            return;
        }

        if (isExcludingSlots((HandledScreen<?>)(Object)this)) {
            this.excludeSlot(click, cir);
        }
        if (click.button() == InputUtil.GLFW_MOUSE_BUTTON_RIGHT && this.focusedSlot != null && isQuicklyEquippable(this.focusedSlot.getStack())) {
            quickEquip(this.screen, this.focusedSlot);
            cir.setReturnValue(true);
        }
        // Refocus fromInventory search field if clicked.
        if (this.inventorySearchField != null && this.inventorySearchField.mouseClicked(click, doubled)) {
            this.inventorySearchField.setFocused(true);
        }
    }

    /**
     * Drag-sort feature, where you can exclude slots by clicking on them.
     */
    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void dragToExclude(Click click, double offsetX, double offsetY, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.client)) {
            return;
        }

        if (isExcludingSlots((HandledScreen<?>)(Object)this)) {
            this.excludeSlot(click, cir);
        }
    }

    /**
     * Handles key pressing correctly and implements functionality for the {@link ModKeybinds#QUICK_EQUIP} keybind.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.client)) {
            return;
        }

        if (MinecraftClient.getInstance().isCtrlPressed()) {
            if (this.screen instanceof RecipeBookScreen<?> recipeScreen
                    && input.key() == ModKeybinds.HIDE_RECIPE_BOOK.boundKey.getCode()
                    && recipeScreen.recipeBook.isOpen()) {
                String text = this.getSearchFieldText();
                recipeScreen.recipeBook.toggleOpen();
                this.refreshWidgetPositions();
                if (this.inventorySearchField != null) {
                    this.inventorySearchField.setText(text);
                }
                cir.setReturnValue(true);
            }
            if ((isValidScreen(this.screen) || isBrewingStandScreen(this.screen) || isFurnaceScreen(this.screen)) && options().transferring.orKeyOnly()) {
                if (input.key() == ModKeybinds.MOVE_CONTAINER.boundKey.getCode()) {
                    this.transferItems(true);
                }
                if (input.key() == ModKeybinds.MOVE_INVENTORY.boundKey.getCode()) {
                    this.transferItems(false);
                }
            }
            if (isValidScreen(this.screen)) {
                if (this.canSort(false) && options().containerSorting.orKeyOnly() && input.key() == ModKeybinds.SORT_CONTAINER.boundKey.getCode()) {
                    sortItems(this.client);
                }
                if (this.canSwap() && this.swapCooldown == 0 && options().swapping.orKeyOnly() && input.key() == ModKeybinds.SWAP_ITEMS.boundKey.getCode()) {
                    swapItems(this.handler, this.inventory, this.excludedSlots);
                    this.swapCooldown = 200;
                }
                if (options().quickDrop.orKeyOnly() && MinecraftClient.getInstance().isAltPressed() && input.key() == GLFW.GLFW_KEY_Q) {
                    this.dropItems(!isContainerScreen(this.screen));
                }
            }
        }

        // Quick equip logic
        if (input.key() == ModKeybinds.QUICK_EQUIP.boundKey.getCode()) {
            quickEquip(this.screen, this.focusedSlot);
            if (hoveredSlotHasItem(this.focusedSlot) && this.inventorySearchField != null && this.inventorySearchField.isFocused()) {
                this.inventorySearchField.setFocused(false);
            }
            if (isInventoryScreen(this.screen)) {
                return;
            }
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

        // handle switching items from hotbar to another containerSlot in fromInventory; cancel out typing if a query can be moved
        if (getCursorStack(this.screen).isEmpty() && this.focusedSlot != null) {
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
                boolean unfocus = false;
                for (int i = 0; i < 9; i++) {
                    if (this.focusedSlot != null && this.client.options.hotbarKeys[i].matchesKey(input)) {
                        unfocus = true;
                        break;
                    }
                }
                if (MinecraftClient.getInstance().isShiftPressed() || unfocus) {
                    recipeScreen.recipeBook.searchField.setFocused(false);
                    return;
                }
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
                    if (input.key() == GLFW.GLFW_KEY_BACKSPACE && recipeScreen.recipeBook.isOpen()) {
                        String text = this.getSearchFieldText();
                        recipeScreen.recipeBook.toggleOpen();
                        this.refreshWidgetPositions();
                        this.inventorySearchField.setText(text.substring(0, text.length() - 1));
                        this.inventorySearchField.setFocused(true);
                        this.setFocused(this.inventorySearchField);
                    }
                    return;
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

    /**
     * Allows correct functionality for typing into search field without clicking on it.
     */
    @Override
    public boolean charTyped(CharInput input) {
        if (options().chestSearching && this.containerSearchField != null && this.containerSearchField.isFocused()) {
            return this.containerSearchField.charTyped(input);
        }
        if (isInventoryScreen(this.screen)) {
            if (this.screen instanceof RecipeBookScreen<?> recipeScreen
                    && recipeScreen.recipeBook.isOpen()
                    && this.inventorySearchField != null
                    && this.inventorySearchField.isFocused()) {
                String text = this.getSearchFieldText();
                recipeScreen.recipeBook.toggleOpen();
                this.refreshWidgetPositions();
                this.inventorySearchField.setText(text + input.asString());
                this.inventorySearchField.setFocused(true);
            }
        }
        return super.charTyped(input);
    }

    /**
     * Saves the current text in {@code searchField.getText()} to memory so it can be referenced when opening a container screen again. Also auto-sorts a container before the screen is closed, if on.
     */
    @Inject(method = "close", at = @At("HEAD"))
    private void injectClose(CallbackInfo ci) {
        if (!modEnabled(this.client)) {
            return;
        }

        if (options().saveExcludedSlots && this.inventory != null) {
            SAVED_EXCLUDED_SLOTS.put(getTotalSlots(this.handler), this.excludedSlots);
        }

        if (options().saveSearchText) {
            if (isInventoryScreen(this.screen) && this.inventorySearchField != null) {
                QoQ.SAVED_TEXT = this.inventorySearchField.getText();
            } else if (isContainerScreen(this.screen) && this.containerSearchField != null) {
                QoQ.SAVED_TEXT = this.containerSearchField.getText();
            }
        }

        if (options().autoCloseRecipeBook
                && this.screen instanceof RecipeBookScreen<?> recipeBookScreen
                && recipeBookScreen.recipeBook.isOpen()) {
            recipeBookScreen.recipeBook.toggleOpen();
            this.refreshWidgetPositions();
        }
    }

    /**
     * Ensures that the search field text isn't cleared when resizing.
     */
    @Override
    public void resize(int width, int height) {
        if (this.containerSearchField != null) {
            // Get current text and focused status
            String text = this.getSearchFieldText();
            boolean refocus = this.containerSearchField.isFocused();
            // Prevents ConcurrentModificationException
            Set<Integer> temp = new HashSet<>(this.excludedSlots);
            this.excludedSlots.clear();
            // Refresh screen (or resize)
            this.init(width, height);
            this.excludedSlots.addAll(temp);
            // Reset text and focused status
            this.containerSearchField.setText(text);
            this.containerSearchField.setFocused(refocus);
        }
    }
}