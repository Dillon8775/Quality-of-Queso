package net.dillon.qualityofqueso.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.screen.gui.SensitiveButton;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.dillon.qualityofqueso.main.QoQ.modEnabled;
import static net.dillon.qualityofqueso.main.QoQ.options;

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
    public abstract T getScreenHandler();

    @Shadow
    @Nullable
    public Slot focusedSlot;

    @Shadow
    @Nullable
    protected abstract Slot getSlotAt(double mouseX, double mouseY);

    @Shadow
    protected int x;
    @Unique
    private final HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
    @Unique
    private TextFieldWidget containerSearchField;
    @Unique
    private TextFieldWidget inventorySearchField;
    @Unique
    private ClickableWidget transferContainerButton, transferInventoryButton;
    @Unique
    private Inventory inventory;
    @Unique
    private boolean keepInventoryButtonActive = false;
    @Unique
    private final Map<TagKey<Item>, EquipmentSlot> quicklyEquippables = Map.of(
            ItemTags.HEAD_ARMOR, EquipmentSlot.HEAD,
            ItemTags.CHEST_ARMOR, EquipmentSlot.CHEST,
            ItemTags.LEG_ARMOR, EquipmentSlot.LEGS,
            ItemTags.FOOT_ARMOR, EquipmentSlot.FEET
    );

    public HandledScreenMixin(Text title) {
        super(title);
    }

    /**
     * Creates and initializes the search field and container button.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (modEnabled(this.client)) {
            if (this.isValidScreen()) {
                // Determine inventory variable; if instance ShulkerBoxScreen, inventory is the shulker box's inventory
                if (this.screen instanceof ShulkerBoxScreen shulkerBoxScreen) {
                    this.inventory = shulkerBoxScreen.getScreenHandler().inventory;
                }
                // If it's GenericContainerScreen, it's the generic container (or most likely chest/barrel)'s inventory
                else if (this.screen instanceof GenericContainerScreen genericContainerScreen) {
                    this.inventory = genericContainerScreen.getScreenHandler().getInventory();
                }
                // Otherwise, inventory is null
                else {
                    this.inventory = null;
                }

                if (options().chestSearching) {
                    this.containerSearchField = this.initializeSearchField(false);
                    this.addSelectableChild(this.containerSearchField);
                }
                if (options().inventoryManagement) {
                    this.transferContainerButton = this.addSelectableChild(
                            new SensitiveButton(
                                    this.getTransferButtonX(true),
                                    this.getTransferButtonY(),
                                    10,
                                    10,
                                    ModTexts.BLANK,
                                    b -> this.transferItems(this.screen, true, false),
                                    () -> this.transferContainerButton.active
                            )
                    );
                    this.transferContainerButton.active = false;
                }
            } else if (options().inventorySearching && this.screen instanceof InventoryScreen) {
                this.inventorySearchField = this.initializeSearchField(true);
                this.addSelectableChild(this.inventorySearchField);
            }
        }
    }

    /**
     * Initializes a search field widget.
     */
    @Unique
    private TextFieldWidget initializeSearchField(boolean inventory) {
        TextFieldWidget searchField = new TextFieldWidget(this.textRenderer, this.width / 2 + this.getBarWidth() / 2 - (inventory ? 60 : 64), this.y + this.titleY - 2, 90, 12, null);
        if (options().saveSearchText) {
            searchField.setText(QoQ.SAVED_TEXT);
        }
        searchField.setMaxLength(50);
        searchField.setPlaceholder(Text.translatable("qualityofqueso.gui.search.placeholder").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        return searchField;
    }

    /**
     * @return A special int to get the bar width.
     */
    @Unique
    private int getBarWidth() {
        return (int) ((double) this.backgroundWidth * 0.6);
    }

    /**
     * Transfers items from one container to another.
     * <p>boolean variable {@code reverse} should be {@code true} if {@code chest -> inventory,} otherwise {@code inventory -> chest.}</p>
     */
    @Unique
    private void transferItems(HandledScreen<?> screen, boolean reverse, boolean drop) {
        int containerSize = this.inventory.size();
        int totalSlotSize = screen.getScreenHandler().slots.size();

        int fromStart = reverse ? 0 : containerSize;
        int fromEnd = reverse ? containerSize : totalSlotSize;
        int toStart = reverse ? containerSize : 0;
        int toEnd = reverse ? totalSlotSize : containerSize;

        for (int i = fromStart; i < fromEnd; i++) {
            Slot fromSlot = screen.getScreenHandler().getSlot(i);
            ItemStack fromStack = fromSlot.getStack();

            if (!options().includeHotbar && !reverse && this.isValidSlot(screen, fromSlot.id)) {
                continue; // skip slot if exclude hotbar is on and slot is in hotbar
            } else if ((this.containerSearchField != null || this.inventorySearchField != null) && !this.getSearchFieldText().isEmpty() && !this.search(this.getSearchFieldText(), fromSlot)) {
                continue; // skip container slot if query not found via search
            }

            if (!fromStack.isEmpty()) {
                for (int j = toStart; j < toEnd; j++) {
                    Slot toSlot = screen.getScreenHandler().getSlot(j);
                    if (toSlot.getStack().isEmpty()) {
                        // Only transfer items if the query matches whatever the cursor is holding
                        if (!this.getScreenHandler().getCursorStack().isEmpty()) {
                            if (fromStack.isOf(this.getScreenHandler().getCursorStack().getItem())) {
                                this.sendClickSlotPacket(i, drop ? SlotActionType.THROW : SlotActionType.QUICK_MOVE);
                                break;
                            }
                        }
                        // If cursor has nothing in it, move all items over
                        else {
                            this.sendClickSlotPacket(i, drop ? SlotActionType.THROW : SlotActionType.QUICK_MOVE);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Sends a click slot packet.
     */
    @Unique
    private void sendClickSlotPacket(int slotIndex, SlotActionType slotActionType) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (client.player == null || networkHandler == null || client.player.currentScreenHandler == null) {
            return;
        }

        var handler = client.player.currentScreenHandler;
        int syncId = handler.syncId;
        int revision = handler.getRevision();

        ItemStack cursorStack = handler.getCursorStack();
        ItemStack clickedStack = handler.getSlot(slotIndex).getStack();

        if (slotActionType == SlotActionType.THROW && handler.getSlot(slotIndex).getStack().isEmpty()) {
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
                slotActionType == SlotActionType.THROW ? (byte) 1 : (byte) 0,
                slotActionType,
                modifiedStacks,
                cursorHash
        );

        networkHandler.sendPacket(packet);
    }

    /**
     * Grays out any containerSlot which doesn't contain the query name being searched.
     */
    @Inject(method = "renderMain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlightFront(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void grayOutSlot(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if ((this.containerSearchField != null || this.inventorySearchField != null)) {
            for (int i = 0; i < this.getInventorySize(); i++) {
                Slot slot = this.getScreenHandler().getSlot(i);
                // Grayout hotbar slots
                if (!options().includeHotbar && this.isValidSlot(this.screen, i) && this.isValidScreen()) {
                    this.makeSlotUnavailable(context, slot, true);
                    continue; // skip normal search logic for hotbar slots
                }
                // Otherwise, gray out slots that don't match the search
                if (!this.getSearchFieldText().isEmpty() && !this.search(this.getSearchFieldText(), slot)) {
                    this.makeSlotUnavailable(context, slot, false);
                }
            }
        }
    }

    /**
     * Handles rendering, such as the search field and transferring inventory button textures.
     */
    @Inject(method = "renderMain", at = @At("TAIL"))
    private void renderWidgets(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        // Render the search field
        if (this.containerSearchField != null) {
            this.containerSearchField.render(context, mouseX, mouseY, deltaTicks);
            if (options().helpfulTooltips && this.containerSearchField.isHovered() && this.containerSearchField.getText().isEmpty()) {
                ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.chest_search.search_filtering"), context, this.textRenderer, mouseX, mouseY);
            }
        }
        // Render inventory search field
        if (this.inventorySearchField != null) {
            this.inventorySearchField.setX(this.width / 2 + this.getBarWidth() / 2 - (
                    this.screen instanceof RecipeBookScreen<?> recipeBookScreen && recipeBookScreen.recipeBook.isOpen() ? -16 : 60
            ));
            this.inventorySearchField.render(context, mouseX, mouseY, deltaTicks);
        }
        if (modEnabled(this.client) && options().inventoryManagement && this.isValidScreen()) {
            // Determine if transfer container button should be active
            this.shouldButtonBeActive(false, null, this.transferContainerButton);
            PlayerInventory playerInventory = this.client.player.getInventory();
            // Initialize transfer inventory button
            this.transferInventoryButton = this.addSelectableChild(
                    new SensitiveButton(
                            this.getTransferButtonX(false),
                            this.getTransferButtonY(),
                            10,
                            10,
                            ModTexts.BLANK,
                            b -> this.transferItems(this.screen, false, false),
                            () -> (this.altDown() || this.keepInventoryButtonActive) && this.shouldButtonBeActive(true, playerInventory, this.transferInventoryButton)));
            // Transfer inventory button is only active if it is already active and hovered, otherwise only becomes active if ALT is pressed
            boolean isTransferInventoryButtonHovered = this.transferInventoryButton.isMouseOver(mouseX, mouseY);
            if (this.shouldButtonBeActive(true, playerInventory, this.transferInventoryButton)) {
                if (this.altDown()) {
                    // Alt is held, activate and allow "keep active" if mouse is over
                    this.transferInventoryButton.active = true;
                    this.keepInventoryButtonActive = isTransferInventoryButtonHovered;
                } else {
                    // Alt is not held – only keep active if still isTransferInventoryButtonHovered from last Alt-down
                    if (!isTransferInventoryButtonHovered) {
                        this.keepInventoryButtonActive = false;
                    }
                    this.transferInventoryButton.active = this.keepInventoryButtonActive;
                }
            }
            this.shouldButtonBeActive(true, playerInventory, this.transferInventoryButton);
            // Render transfer chest -> inventory button texture
            if (this.transferContainerButton != null) {
                // If button isn't active, render inactive texture
                if (!this.transferContainerButton.active) {
                    this.renderButtonTexture("transfer_container_button_inactive", true, this.transferContainerButton, context);
                }
                // Otherwise render unhovered/texture, depending on if the button is hovered
                else {
                    this.renderButtonTexture(this.transferContainerButton.isMouseOver(mouseX, mouseY) ? "transfer_container_button_hovered" : "transfer_container_button", true, this.transferContainerButton, context);
                }

                // Render tooltip if searching or cursor has item
                if (this.transferContainerButton.isMouseOver(mouseX, mouseY)) {
                    if (!this.getScreenHandler().getCursorStack().isEmpty()) {
                        ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.transfer_container_button.with_cursor_stack", this.getScreenHandler().getCursorStack().getItemName()), context, this.textRenderer, mouseX, mouseY);
                    } else if (!this.getSearchFieldText().isEmpty()) {
                        ButtonUtil.drawTooltip(this.getSearchFieldText().startsWith("#") ?
                                Text.translatable("qualityofqueso.gui.transfer_container_button.with_search_query.tag", this.getSearchFieldText().substring(1)) :
                                this.getSearchFieldText().startsWith("!") ?
                                        Text.translatable("qualityofqueso.gui.transfer_container_button.with_search_query.exclude", this.getSearchFieldText().substring(1)) :
                                        Text.translatable("qualityofqueso.gui.transfer_container_button.with_search_query", this.getSearchFieldText()), context, this.textRenderer, mouseX, mouseY);
                    } else {
                        ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.transfer_container_button"), context, this.textRenderer, mouseX, mouseY);
                    }
                }
            }
            // Render transfer inventory -> chest button texture
            // If the button should not be active (meaning if there is nothing in the inventory), render inactive texture
            if (!this.shouldButtonBeActive(true, playerInventory, this.transferInventoryButton)) {
                this.renderButtonTexture("transfer_inventory_button_inactive", true, this.transferInventoryButton, context);
            }
            // If inventory contains something, then check if alt is down. If it's not, render hold alt texture
            else if (!this.altDown()) {
                this.renderButtonTexture("transfer_inventory_button_hold_alt", true, this.transferInventoryButton, context);
            }
            // Otherwise render unhovered/hovered texture, depending on if the button is hovered
            else {
                this.renderButtonTexture(this.transferInventoryButton.isMouseOver(mouseX, mouseY) ? "transfer_inventory_button_hovered" : "transfer_inventory_button", true, transferInventoryButton, context);
            }

            // Button to include hotbar transfer or not
            ClickableWidget includeHotbarButton = this.addSelectableChild(
                    ButtonWidget.builder(
                            ModTexts.BLANK, button -> {
                                options().includeHotbar = !options().includeHotbar;
                                ModClientOptions.CLIENT_OPTIONS.save();
                            }).dimensions(this.getTransferButtonX(false) - 12, this.getTransferButtonY(), 10, 10).build());

            // Handles rendering textures and tooltips for include hotbar
            if (includeHotbarButton.isMouseOver(mouseX, mouseY)) {
                this.renderButtonTexture(options().includeHotbar ? "include_hotbar_button_hovered" : "exclude_hotbar_button_hovered", false, includeHotbarButton, context);
                ButtonUtil.drawTooltip(options().includeHotbar ?
                        Text.translatable("qualityofqueso.gui.include_hotbar") :
                        Text.translatable("qualityofqueso.gui.exclude_hotbar"), context, this.textRenderer, mouseX, mouseY);
            } else {
                this.renderButtonTexture(options().includeHotbar ? "include_hotbar_button" : "exclude_hotbar_button", false, includeHotbarButton, context);
            }

            // Render tooltip if cursor stack has an item and button is hovered
            if (isTransferInventoryButtonHovered) {
                if (transferInventoryButton.active && this.altDown()) {
                    if (!this.getScreenHandler().getCursorStack().isEmpty()) {
                        ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.transfer_inventory_button.with_cursor_stack", this.getScreenHandler().getCursorStack().getItemName()), context, this.textRenderer, mouseX, mouseY);
                    } else if (!this.getSearchFieldText().isEmpty() && options().searchInventory) {
                        ButtonUtil.drawTooltip(this.getSearchFieldText().startsWith("#") ?
                                Text.translatable("qualityofqueso.gui.transfer_inventory_button.with_search_query.tag", this.getSearchFieldText().substring(1)) :
                                this.getSearchFieldText().startsWith("!") ?
                                        Text.translatable("qualityofqueso.gui.transfer_inventory_button.with_search_query.exclude", this.getSearchFieldText().substring(1)) :
                                        Text.translatable("qualityofqueso.gui.transfer_inventory_button.with_search_query", this.getSearchFieldText()), context, this.textRenderer, mouseX, mouseY);
                    } else {
                        ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.transfer_inventory_button"), context, this.textRenderer, mouseX, mouseY);
                    }
                } else {
                    if (this.shouldButtonBeActive(true, playerInventory, transferInventoryButton)) {
                        ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.transfer_inventory_button.hold_alt"), context, this.textRenderer, mouseX, mouseY);
                    }
                }
            }
        }
    }

    /**
     * @return the {@code searchField namespace} text.
     */
    @Unique
    private String getSearchFieldText() {
        return this.inventorySearchField != null ? this.inventorySearchField.getText() : this.containerSearchField != null ? this.containerSearchField.getText() : "";
    }

    /**
     * @return the inventory (size) that should be searched.
     */
    @Unique
    private int getInventorySize() {
        return options().searchInventory ? this.getScreenHandler().slots.size() : this.inventory.size();
    }

    /**
     * @return {@code true} if the hovered slot has an query (assuming hovered slot isn't {@code null}).
     */
    @Unique
    private boolean hoveredSlotHasItem() {
        return this.focusedSlot != null && this.focusedSlot.getStack() != ItemStack.EMPTY;
    }

    /**
     * @return {@code} if the alt button is held down, return {@code true}; if {@code requireAltToSort} is {@code OFF}, return {@code true} regardless of if the alt button is pressed.
     */
    @Unique
    private boolean altDown() {
        return !options().requireAltToMove || MinecraftClient.getInstance().isAltPressed();
    }

    /**
     * @return the {@code X} value for transferring query buttons.
     */
    @Unique
    private int getTransferButtonX(boolean chestToInventory) {
        int barWidth = (int) ((double) this.backgroundWidth * 0.6);
        return chestToInventory ? this.width / 2 + barWidth / 2 + 16 : this.width / 2 + barWidth / 2 + 4;
    }

    /**
     * @return the {@code Y} value for transferring query buttons.
     */
    @Unique
    private int getTransferButtonY() {
        int y = 120; // 6 rows
        if (this.inventory.size() == 27) { // 3 rows
            y = 66;
        } else if (this.inventory.size() == 36) { // 4 rows3
            y = 84;
        } else if (this.inventory.size() == 45) { // 5 rows
            y = 102;
        }
        return this.y + this.titleY + y;
    }

    /**
     * Grays out a containerSlot.
     */
    @Unique
    private void makeSlotUnavailable(DrawContext context, Slot slot, boolean hotbar) {
        int color = hotbar ? -2139062148 : -1275068416;
        context.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, color, color);
    }

    /**
     * Renders a transfer button texture.
     */
    @Unique
    private void renderButtonTexture(String id, boolean transferable, ClickableWidget buttonReference, DrawContext
            context) {
        String transferableString = !this.getScreenHandler().getCursorStack().isEmpty() ?
                "_with_stack.png" : this.getSearchFieldText().startsWith("!") ?
                "_exclude.png" : this.getSearchFieldText().startsWith("#") ?
                "_with_tag.png" : ".png";
        String appended = transferable ? transferableString : ".png";
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("qualityofqueso:textures/gui/" + id + appended), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
    }

    /**
     * @return {@code true} if the {@code inventory} has a match with the search query.
     */
    @Unique
    private boolean shouldButtonBeActive(boolean isPlayerInventory,
                                         @Nullable PlayerInventory playerInventory, ClickableWidget button) {
        // If button is null, return false because there is no button to be checked
        if (button == null) {
            return false;
        }
        // Determine inventory size to run through
        int size = isPlayerInventory ? playerInventory.size() : this.inventory.size();
        int j = 0;
        for (int i = 0; i < size; i++) {
            // If slot is not empty, the button should be active
            // Increment J and make button active
            ItemStack stack = isPlayerInventory ? playerInventory.getStack(i) : this.getScreenHandler().getSlot(i).getStack();
            // Handle cursor stack
            if (!this.getScreenHandler().getCursorStack().isEmpty()) {
                if (stack.isOf(this.getScreenHandler().getCursorStack().getItem())) {
                    j++;
                    button.active = true;
                }
            } else {
                if (!stack.isEmpty()) {
                    j++;
                    button.active = true;
                }
            }
        }
        // If J == 0 or are slots are unavailable in the player inventory (assuming it's not null), button is not active and return false
        if (j == 0 || this.areAllSlotsUnavailable(isPlayerInventory, isPlayerInventory ? playerInventory : null)) {
            button.active = false;
            return false;
        }
        // Return true otherwise (button should be active)
        return true;
    }

    /**
     * @return {@code true} if all slots are grayed out, or {@code unavailable.}
     */
    @Unique
    private boolean areAllSlotsUnavailable(boolean isPlayerInventory, @Nullable PlayerInventory playerInventory) {
        int j = 0;
        List<Slot> playerSlots = new ArrayList<>();
        // If checking player inventory, loop through all player inventory slots to determine if slot is unavailable
        if (isPlayerInventory) {
            for (Slot s : this.getScreenHandler().slots) {
                if (s.inventory == playerInventory) {
                    playerSlots.add(s);
                }
            }
            for (Slot slot : playerSlots) {
                if (this.search(this.getSearchFieldText(), slot)) {
                    j++; // increment J if query found in slot
                }
            }
        } else {
            // Otherwise, loop through the container inventory and increment J if query found inside
            for (int i = 0; i < this.inventory.size(); i++) {
                Slot slot = this.getScreenHandler().getSlot(i);
                if (this.search(this.getSearchFieldText(), slot)) {
                    j++;
                }
            }
        }
        // If J == 0 then no query was found, returning true for all slots are unavailable
        return j == 0;
    }

    /**
     * @return {@code true} if {@code playerInventory} is empty (excluding armor items).
     */
    @Unique
    private boolean isPlayerInventoryEmpty(PlayerInventory playerInventory) {
        for (ItemStack stack : playerInventory.getMainStacks()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return {@code true} if an query is found from {@code searchQuery}.
     */
    @Unique
    private boolean search(String searchQuery, Slot slot) {
        ItemStack stack = slot.getStack();

        // If slot is empty, return false (slot is unavailable)
        // If include hotbar is off, return false if hotbar slot
        if (stack.isEmpty() ||
                (!options().includeHotbar && options().searchInventory && this.isValidSlot(this.screen, slot.id))) {
            return false;
        }

        String itemName = stack.getItemName().getString().toLowerCase();
        String customName = stack.getCustomName() != null ? stack.getCustomName().getString().toLowerCase() : "";

        String[] terms = searchQuery.split(",");
        // If slot contains a comma, for each query searched (separated by each comma), return true if search query'namespace find an query (make slot available)
        for (String term : terms) {
            if (itemName.contains(term.trim().toLowerCase())) {
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
     * Implements functionality for the {@code Quick Equip right-click feature,} and handles inventory search field.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleMouseClicking(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (modEnabled(this.client)) {
            if (click.button() == InputUtil.GLFW_MOUSE_BUTTON_RIGHT && this.focusedSlot != null && this.isQuicklyEquippable(this.focusedSlot.getStack())) {
                this.quickEquip();
                cir.setReturnValue(true);
            }
            // Refocus inventory search field if clicked.
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
                if (this.transferContainerButton != null && this.transferContainerButton.active && input.key() == ModKeybinds.MOVE_TO_INVENTORY.boundKey.getCode()) {
                    this.transferItems(this.screen, true, false);
                }

                if (this.transferInventoryButton != null && this.transferInventoryButton.active && input.key() == ModKeybinds.MOVE_TO_CONTAINER.boundKey.getCode()) {
                    this.transferItems(this.screen, false, false);
                }
            }

            // Quick equip logic
            if (input.key() == ModKeybinds.QUICK_EQUIP.boundKey.getCode()) {
                this.quickEquip();
            }

            // Prevent E from typing entirely in inventory screens
            if (input.key() == GLFW.GLFW_KEY_E && options().preventEFromTyping && (this.screen instanceof InventoryScreen || this.screen instanceof CreativeInventoryScreen)) {
                this.close();
                cir.setReturnValue(true);
            }

            // Declare typing variables
            // Both of these variables apply to disallowed keys and hotbar switching
            boolean ignoreTyping = this.hoveredSlotHasItem(); // Basic ignore typing variable; applies to recipe book screens only.
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

            // handle switching items from hotbar to another containerSlot in inventory; cancel out typing if an query can be moved
            if (this.getScreenHandler().getCursorStack().isEmpty() && this.focusedSlot != null) {
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
            boolean cannotType = (numberKeyPressed || hotbarKeyPressed || dropKeyPressed || swapKeyPressed) && this.hoveredSlotHasItem();

            // Recipe book search field logic
            if (options().betterSearching && this.screen instanceof RecipeBookScreen<?> recipeScreen && !MinecraftClient.getInstance().isCtrlPressed()) {
                if (!ignoreTyping && !recipeScreen.recipeBook.isOpen() && (this.inventorySearchField == null || !this.inventorySearchField.isFocused())) {
                    recipeScreen.recipeBook.toggleOpen();
                    this.refreshWidgetPositions();
                }
                if (recipeScreen.recipeBook.searchField != null) {
                    System.out.println("what");
                    recipeScreen.recipeBook.searchField.setFocused(!cannotType);

                    if (recipeScreen.recipeBook.searchField.isFocused()) {
                        cir.setReturnValue(recipeScreen.recipeBook.keyPressed(input) || super.keyPressed(input));
                    }
                }
            }
            // Inventory search field logic
            if (options().inventorySearching && this.inventorySearchField != null) {
                if (!MinecraftClient.getInstance().isCtrlPressed()) {
                    if (this.screen instanceof RecipeBookScreen<?> recipeScreen && recipeScreen.recipeBook.isOpen() && !this.inventorySearchField.isFocused()) {
                        recipeScreen.recipeBook.searchField.setFocused(!cannotType);
                        System.out.println("ok");
                    } else if (!secondaryIgnoreTyping) {
                        this.inventorySearchField.setFocused(true);
                    } else if (this.inventorySearchField.isFocused() && cannotType) {
                        this.inventorySearchField.setFocused(false);
                    }
                }

                // Unfocus recipe book search field when inventory search field is focused
                if (this.screen instanceof RecipeBookScreen<?> recipeScreen && recipeScreen.recipeBook.searchField != null) {
                    if (this.inventorySearchField.isFocused()) {
                        recipeScreen.recipeBook.searchField.setFocused(false);
                    }
                    // Unfocus inventory search field when recipe book search field is focused
                    else if (recipeScreen.recipeBook.searchField.isFocused() && this.inventorySearchField != null) {
                        this.inventorySearchField.setFocused(false);
                    }
                }

                if (this.inventorySearchField.isFocused() && this.inventorySearchField.keyPressed(input)) {
                    cir.setReturnValue(true);
                }
            }

            // Chest search field logic
            if (options().chestSearching && isValidScreen()) {
                if (!secondaryIgnoreTyping && !MinecraftClient.getInstance().isCtrlPressed()) {
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
     * @return {@code true} if the slot is valid to move.
     */
    @Unique
    private boolean isValidSlot(HandledScreen<?> screen, int slotIndex) {
        int totalSlots = screen.getScreenHandler().slots.size();
        // If we're in the last 9 slots and hotbar is disabled, return false
        return slotIndex >= totalSlots - 9;
    }

    /**
     * @return {@code true} if the hovered item is a {@code quickly equippable item.}
     */
    @Unique
    private boolean isQuicklyEquippable(ItemStack stack) {
        for (TagKey<Item> quicklyEquippable : this.quicklyEquippables.keySet()) {
            if (stack.isIn(quicklyEquippable) || stack.isOf(Items.ELYTRA)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Quickly swaps two items in the player's inventory.
     */
    @Unique
    private void quickSwap(int sourceSlot, EquipmentSlot slot) {
        // Slot index for armor - see PlayerScreenHandler for proof of these values
        int slotIndex = slot == EquipmentSlot.HEAD ? 5 : slot == EquipmentSlot.CHEST ? 6 : slot == EquipmentSlot.LEGS ? 7 : slot == EquipmentSlot.FEET ? 8 : 6;
        this.sendClickSlotPacket(sourceSlot, SlotActionType.PICKUP);
        this.sendClickSlotPacket(slotIndex, SlotActionType.PICKUP);
        this.sendClickSlotPacket(sourceSlot, SlotActionType.PICKUP);
    }

    /**
     * Quickly equips an item.
     */
    @Unique
    private void quickEquip() {
        if (options().quickEquip && this.focusedSlot != null && (this.focusedSlot.id >= 5) && (this.screen instanceof InventoryScreen || this.screen instanceof CreativeInventoryScreen)) {
            ItemStack stack = this.focusedSlot.getStack();
            EquipmentSlot targetSlot = null;

            for (TagKey<Item> quicklyEquippable : this.quicklyEquippables.keySet()) {
                if (stack.isIn(quicklyEquippable)) {
                    targetSlot = this.quicklyEquippables.get(quicklyEquippable);
                }
            }

            if (stack.isOf(Items.ELYTRA)) {
                targetSlot = EquipmentSlot.CHEST;
            }

            if (targetSlot != null) {
                ItemStack equippedStack = this.client.player.getEquippedStack(targetSlot);
                if (equippedStack.isEmpty()) {
                    this.sendClickSlotPacket(this.focusedSlot.id, SlotActionType.QUICK_MOVE);
                } else {
                    this.quickSwap(this.focusedSlot.id, targetSlot);
                }
            }
        }
    }

    /**
     * Closes the screen when clicking outside of menu.
     */
    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"))
    private void closeButtonOnClickOutOfBounds(Slot slot, int slotId, int button, SlotActionType
            actionType, CallbackInfo ci) {
        if (modEnabled(this.client) && options().betterGuiExit && this.getScreenHandler().getCursorStack().isEmpty() && button == 0 && slot == null) {
            this.close();
        }
    }

    /**
     * Saves the current text in {@code searchField.getText()} to memory so it can be referenced when opening a container screen again.
     */
    @Inject(method = "close", at = @At("TAIL"))
    private void saveSearchText(CallbackInfo ci) {
        if (options().saveSearchText) {
            if (this.screen instanceof InventoryScreen && this.inventorySearchField != null) {
                QoQ.SAVED_TEXT = this.inventorySearchField.getText();
            } else if (this.isValidScreen() && this.containerSearchField != null) {
                QoQ.SAVED_TEXT = this.containerSearchField.getText();
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

    /**
     * @return valid handled screens which can use the container search feature.
     */
    @Unique
    private boolean isValidScreen() {
        return this.screen instanceof GenericContainerScreen || this.screen instanceof ShulkerBoxScreen;
    }
}