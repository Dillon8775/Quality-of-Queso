package net.dillon.qualityofqueso.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.main.QualityOfQueso;
import net.dillon.qualityofqueso.option.ModOptions;
import net.dillon.qualityofqueso.screen.gui.SensitiveButton;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
import java.util.Optional;

import static net.dillon.qualityofqueso.main.QualityOfQueso.options;

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
    @Shadow @Nullable
    public Slot focusedSlot;
    @Shadow @Nullable
    protected abstract Slot getSlotAt(double mouseX, double mouseY);
    @Unique
    private final HandledScreen<?> screen = (HandledScreen<?>)(Object)this;
    @Unique
    private TextFieldWidget searchField;
    @Unique
    private ClickableWidget transferContainerButton;
    @Unique
    private Inventory inventory;
    @Unique
    private boolean keepInventoryButtonActive = false;

    public HandledScreenMixin(Text title) {
        super(title);
    }

    /**
     * Creates and initializes the search field and container button.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (options().enableMod && this.isValidScreen()) {
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

            if (options().chestSearch) {
                int barWidth = (int)((double)this.backgroundWidth * 0.6);
                this.searchField = new TextFieldWidget(this.textRenderer, this.width / 2 + barWidth / 2 - 64, this.y + this.titleY - 2, 90, 12, null);
                if (options().saveSearchText) {
                    this.searchField.setText(QualityOfQueso.SAVED_TEXT);
                }
                this.searchField.setMaxLength(50);
                this.searchField.setPlaceholder(Text.translatable("qualityofqueso.gui.search.placeholder").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                this.addSelectableChild(this.searchField);
            }
            if (options().inventorySorting) {
                this.transferContainerButton = this.addSelectableChild(
                        new SensitiveButton(
                                this.getTransferButtonX(true),
                                this.getTransferButtonY(),
                                10,
                                10,
                                ModTexts.BLANK,
                                b -> this.transferItems(this.screen, true),
                                () -> this.transferContainerButton.active
                        )
                );
                this.transferContainerButton.active = false;
            }
        }
    }

    /**
     * Transfers items from one container to another.
     * <p>boolean variable {@code reverse} should be {@code true} if {@code chest -> inventory,} otherwise {@code inventory -> chest.}</p>
     */
    @Unique
    private void transferItems(HandledScreen<?> screen, boolean reverse) {
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
            } else if (this.searchField != null && !this.getSearchFieldText().isEmpty() && !this.search(this.getSearchFieldText(), fromSlot)) {
                continue; // skip container slot if query not found via search
            }

            if (!fromStack.isEmpty()) {
                for (int j = toStart; j < toEnd; j++) {
                    Slot toSlot = screen.getScreenHandler().getSlot(j);
                    if (toSlot.getStack().isEmpty()) {
                        // Only transfer items if the query matches whatever the cursor is holding
                        if (!this.getScreenHandler().getCursorStack().isEmpty()) {
                            if (fromStack.isOf(this.getScreenHandler().getCursorStack().getItem())) {
                                this.sendClickSlotPacket(i, SlotActionType.QUICK_MOVE);
                                break;
                            }
                        }
                        // If cursor has nothing in it, move all items over
                        else {
                            this.sendClickSlotPacket(i, SlotActionType.QUICK_MOVE);
                            break;
                        }
                    }
                }
            }
        }
    }

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
     * Grays out any containerSlot which doesn't contain the query name being searched.
     */
    @Inject(method = "renderMain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlightFront(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void grayOutSlot(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (this.searchField != null && !this.getSearchFieldText().isEmpty()) {
            for (int i = 0; i < this.getInventorySize(); i++) {
                Slot slot = this.getScreenHandler().getSlot(i);
                // If query not found from search result, the slot becomes unavailable
                if (!this.search(this.getSearchFieldText(), slot)) {
                    this.makeSlotUnavailable(context, slot);
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
        if (this.searchField != null) {
            this.searchField.render(context, mouseX, mouseY, deltaTicks);
            if (this.searchField.isHovered()) {
                context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.chest_search.search_filtering"), 200), mouseX, mouseY);
            }
        }
        if (options().enableMod && options().inventorySorting && this.isValidScreen()) {
            // Determine if transfer container button should be active
            this.shouldButtonBeActive(false, null, this.transferContainerButton);
            PlayerInventory playerInventory = this.client.player.getInventory();
            // Initialize transfer inventory button
            ClickableWidget transferInventoryButton = this.addSelectableChild(
                    new SensitiveButton(
                            this.getTransferButtonX(false),
                            this.getTransferButtonY(),
                            10,
                            10,
                            ModTexts.BLANK,
                            b -> this.transferItems(this.screen, false),
                            () -> (this.altDown() || this.keepInventoryButtonActive) && !this.isPlayerInventoryEmpty(playerInventory)
                    )
            );
            // Transfer inventory button is only active if it is already active and hovered, otherwise only becomes active if ALT is pressed
            boolean isTransferInventoryButtonHovered = transferInventoryButton.isMouseOver(mouseX, mouseY);
            if (this.shouldButtonBeActive(true, playerInventory, transferInventoryButton)) {
                if (this.altDown()) {
                    // Alt is held, activate and allow "keep active" if mouse is over
                    transferInventoryButton.active = true;
                    this.keepInventoryButtonActive = isTransferInventoryButtonHovered;
                } else {
                    // Alt is not held – only keep active if still isTransferInventoryButtonHovered from last Alt-down
                    if (!isTransferInventoryButtonHovered) {
                        this.keepInventoryButtonActive = false;
                    }
                    transferInventoryButton.active = this.keepInventoryButtonActive;
                }
            }
            this.shouldButtonBeActive(true, playerInventory, transferInventoryButton);
            // Render transfer chest -> inventory button texture
            if (this.transferContainerButton != null) {
                // If button isn't active, render inactive texture
                if (!this.transferContainerButton.active) {
                    this.renderTransferButtonTexture("transfer_container_button_inactive", this.transferContainerButton, context);
                }
                // Otherwise render unhovered/texture, depending on if the button is hovered
                else {
                    this.renderTransferButtonTexture(this.transferContainerButton.isMouseOver(mouseX, mouseY) ? "transfer_container_button_hovered" : "transfer_container_button", this.transferContainerButton, context);
                }

                // Render tooltip if searching or cursor has item
                if (this.transferContainerButton.isMouseOver(mouseX, mouseY)) {
                    if (!this.getScreenHandler().getCursorStack().isEmpty()) {
                        context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.transfer_container_button.with_cursor_stack", this.getScreenHandler().getCursorStack().getItemName()), 200), mouseX, mouseY);
                    } else if (!this.getSearchFieldText().isEmpty()) {
                        context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.transfer_container_button.with_search_query", this.getSearchFieldText()), 200), mouseX, mouseY);
                    } else {
                        context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.transfer_container_button"), 200), mouseX, mouseY);
                    }
                }
            }
            // Render transfer inventory -> chest button texture
            // If the button should not be active (meaning if there is nothing in the inventory), render inactive texture
            if (!this.shouldButtonBeActive(true, playerInventory, transferInventoryButton)) {
                this.renderTransferButtonTexture("transfer_inventory_button_inactive", transferInventoryButton, context);
            }
            // If inventory contains something, then check if alt is down. If it's not, render hold alt texture
            else if (!this.altDown()) {
                this.renderTransferButtonTexture("transfer_inventory_button_hold_alt", transferInventoryButton, context);
            }
            // Otherwise render unhovered/hovered texture, depending on if the button is hovered
            else {
                this.renderTransferButtonTexture(transferInventoryButton.isMouseOver(mouseX, mouseY) ? "transfer_inventory_button_hovered" : "transfer_inventory_button", transferInventoryButton, context);
            }

            // Button to include hotbar transfer or not
            ClickableWidget includeHotbarButton = this.addSelectableChild(
                    ButtonWidget.builder(
                            ModTexts.BLANK, button -> {
                                options().includeHotbar = !options().includeHotbar;
                                ModOptions.OPTIONS.save();
                            }).dimensions(this.getTransferButtonX(false) - 12, this.getTransferButtonY(), 10, 10).build());

            // Handles rendering textures and tooltips for include hotbar
            if (includeHotbarButton.isMouseOver(mouseX, mouseY)) {
                if (options().includeHotbar) {
                    this.renderTransferButtonTexture("include_hotbar_button_hovered", includeHotbarButton, context);
                    context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.include_hotbar"), 200), mouseX, mouseY);
                } else {
                    this.renderTransferButtonTexture("exclude_hotbar_button_hovered", includeHotbarButton, context);
                    context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.exclude_hotbar"), 200), mouseX, mouseY);
                }
            } else {
                if (options().includeHotbar) {
                    this.renderTransferButtonTexture("include_hotbar_button", includeHotbarButton, context);
                } else {
                    this.renderTransferButtonTexture("exclude_hotbar_button", includeHotbarButton, context);
                }
            }

            // Render tooltip if cursor stack has an item and button is hovered
            if (isTransferInventoryButtonHovered) {
                if (transferInventoryButton.active && this.altDown()) {
                    if (!this.getScreenHandler().getCursorStack().isEmpty()) {
                        context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.transfer_inventory_button.with_cursor_stack", this.getScreenHandler().getCursorStack().getItemName()), 200), mouseX, mouseY);
                    } else if (!this.getSearchFieldText().isEmpty() && options().searchInventory) {
                        context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.transfer_inventory_button.with_search_query", this.getSearchFieldText()), 200), mouseX, mouseY);
                    } else {
                        context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.transfer_inventory_button"), 200), mouseX, mouseY);
                    }
                } else {
                    if (this.shouldButtonBeActive(true, playerInventory, transferInventoryButton)) {
                        context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.transfer_inventory_button.hold_alt"), 200), mouseX, mouseY);
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
        return this.searchField != null ? this.searchField.getText() : "";
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
        return !options().requireAltToMove || hasAltDown();
    }

    /**
     * @return the {@code X} value for transferring query buttons.
     */
    @Unique
    private int getTransferButtonX(boolean chestToInventory) {
        int barWidth = (int)((double)this.backgroundWidth * 0.6);
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
        } else if (this.inventory.size() == 36) { // 4 rows
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
    private void makeSlotUnavailable(DrawContext context, Slot slot) {
        context.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, -1275068416, -1275068416);
    }

    /**
     * Renders a transfer button texture.
     */
    @Unique
    private void renderTransferButtonTexture(String id, ClickableWidget buttonReference, DrawContext context) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("qualityofqueso:textures/gui/"+id+".png"), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
    }

    /**
     * @return {@code true} if the {@code inventory} has something in it.
     */
    @Unique
    private boolean shouldButtonBeActive(boolean isPlayerInventory, @Nullable PlayerInventory playerInventory, ClickableWidget button) {
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
            if (!stack.isEmpty()) {
                j++;
                button.active = true;
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
                if (search(this.getSearchFieldText(), slot)) {
                    j++; // increment J if query found in slot
                }
            }
        } else {
            // Otherwise, loop through the container inventory and increment J if query found inside
            for (int i = 0; i < this.inventory.size(); i++) {
                Slot slot = this.getScreenHandler().getSlot(i);
                if (search(this.getSearchFieldText(), slot)) {
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
        String customName = stack.getCustomName() != null ? stack.getName().getString().toLowerCase() : "";

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
        if (this.searchField != null) {
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
     * Handles key pressing correctly and implements functionality for the {@link ModKeybinds#QUICK_EQUIP} keybind.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (options().enableMod) {
            // Quick equip key logic
            if (options().quickEquip && keyCode == ModKeybinds.QUICK_EQUIP.boundKey.getCode() && this.focusedSlot != null && (this.screen instanceof InventoryScreen || this.screen instanceof CreativeInventoryScreen)) {
                ItemStack stack = this.focusedSlot.getStack();
                EquipmentSlot targetSlot = null;

                if (stack.isIn(ItemTags.HEAD_ARMOR)) {
                    targetSlot = EquipmentSlot.HEAD;
                } else if (stack.isIn(ItemTags.CHEST_ARMOR) || stack.isOf(Items.ELYTRA)) {
                    targetSlot = EquipmentSlot.CHEST;
                } else if (stack.isIn(ItemTags.LEG_ARMOR)) {
                    targetSlot = EquipmentSlot.LEGS;
                } else if (stack.isIn(ItemTags.FOOT_ARMOR)) {
                    targetSlot = EquipmentSlot.FEET;
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

            // Declare typing variables
            boolean ignoreTyping = this.hoveredSlotHasItem();
            boolean secondaryIgnoreTyping = false;
            boolean numberKeyPressed = false;
            boolean hotbarKeyPressed = false;
            boolean dropKeyPressed = false;
            boolean swapKeyPressed = false;

            // If a "disallowed key" is pressed, ignoreTyping and secondaryIgnoreTyping become true.
            for (int key : QualityOfQueso.disallowedKeys) {
                if (keyCode == key) {
                    ignoreTyping = true; secondaryIgnoreTyping = true;
                    break;
                }
            }

            // handle switching items from hotbar to another containerSlot in inventory; cancel out typing if an query can be moved
            if (this.getScreenHandler().getCursorStack().isEmpty() && this.focusedSlot != null) {
                for (int i = 0; i < 9; i++) {
                    if (this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
                        ignoreTyping = true; secondaryIgnoreTyping = true; hotbarKeyPressed = true;
                        break;
                    }
                }
                if (this.screen instanceof RecipeBookScreen<?> && keyCode == ModKeybinds.QUICK_EQUIP.boundKey.getCode()) {
                    ignoreTyping = true;
                }
            }

            // Handle 'T' and 'E' keys
            for (int key : QualityOfQueso.keys) {
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
            if (keyCode == MinecraftClient.getInstance().options.dropKey.boundKey.getCode()) {
                secondaryIgnoreTyping = true;
                dropKeyPressed = true;
            } else if (keyCode == MinecraftClient.getInstance().options.swapHandsKey.boundKey.getCode()) {
                secondaryIgnoreTyping = true;
                swapKeyPressed = true;
            }

            // If any of these are true, the user cannot type in the search field
            boolean cannotType = (numberKeyPressed || hotbarKeyPressed || dropKeyPressed || swapKeyPressed) && this.hoveredSlotHasItem();

            // Recipe book search field logic
            if (options().betterSearching && this.screen instanceof RecipeBookScreen<?> recipeScreen) {
                if (!ignoreTyping && !recipeScreen.recipeBook.isOpen()) {
                    recipeScreen.recipeBook.toggleOpen();
                    refreshWidgetPositions();
                }
                if (recipeScreen.recipeBook.searchField != null) {
                    recipeScreen.recipeBook.searchField.setFocused(!ignoreTyping);
                    if (recipeScreen.recipeBook.searchField.isFocused()) {
                        cir.setReturnValue(recipeScreen.recipeBook.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers));
                    }
                }
            }
            // Chest search field logic
            else if (options().chestSearch && isValidScreen()) {
                if (!secondaryIgnoreTyping) {
                    this.searchField.setFocused(true);
                } else if (this.searchField.isFocused() && cannotType) {
                    this.searchField.setFocused(false);
                }

                if (this.searchField.isFocused() && this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
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
     * Closes the screen when clicking outside of menu.
     */
    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"))
    private void closeButtonOnClickOutOfBounds(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (options().betterGuiExit && this.getScreenHandler().getCursorStack().isEmpty() && button == 0 && slot == null) {
            this.close();
        }
    }

    /**
     * Saves the current text in {@code searchField.getText()} to memory so it can be referenced when opening a container screen again.
     */
    @Inject(method = "close", at = @At("TAIL"))
    private void saveSearchText(CallbackInfo ci) {
        if (options().chestSearch && options().saveSearchText && this.searchField != null && this.isValidScreen()) {
            QualityOfQueso.SAVED_TEXT = this.searchField.getText();
        }
    }

    /**
     * Allows correct functionality for typing into search field without clicking on it.
     */
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (options().chestSearch && this.searchField != null && this.searchField.isFocused()) {
            return this.searchField.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    /**
     * Ensures that the search field text isn't cleared when resizing.
     */
    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if (this.searchField != null) {
            // Get current text and focused status
            String text = this.getSearchFieldText();
            boolean refocus = this.searchField.isFocused();
            // Refresh screen (or resize)
            this.init(client, width, height);
            // Reset text and focused status
            this.searchField.setText(text);
            this.searchField.setFocused(refocus);
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