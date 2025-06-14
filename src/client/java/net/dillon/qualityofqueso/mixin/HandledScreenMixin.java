package net.dillon.qualityofqueso.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.option.ModOptions;
import net.dillon.qualityofqueso.screen.gui.SensitiveButton;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        if (this.isValidScreen()) {
            if (this.screen instanceof ShulkerBoxScreen shulkerBoxScreen) {
                this.inventory = shulkerBoxScreen.getScreenHandler().inventory;
            } else if (this.screen instanceof GenericContainerScreen genericContainerScreen) {
                this.inventory = genericContainerScreen.getScreenHandler().getInventory();
            } else {
                this.inventory = null;
            }

            if (QualityOfQuesoClient.options().chestSearch) {
                int barWidth = (int)((double)this.backgroundWidth * 0.6);
                this.searchField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, this.width / 2 + barWidth / 2 - 64, this.y + this.titleY - 2, 90, 12, null);
                if (QualityOfQuesoClient.options().saveSearchText) {
                    this.searchField.setText(QualityOfQuesoClient.SAVED_TEXT);
                }
                this.searchField.setMaxLength(50);
                this.searchField.setDrawsBackground(true);
                this.searchField.setEditableColor(16777215);
                this.addSelectableChild(this.searchField);
            }
            if (QualityOfQuesoClient.options().inventorySorting) {
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

            if (this.searchField != null && !this.getSearchFieldText().isEmpty() && !this.isSlotAvailable(this.getSearchFieldText(), fromSlot)) {
                continue; // skip container slot if item not found via search
            }

            if (!fromStack.isEmpty()) {
                for (int j = toStart; j < toEnd; j++) {
                    Slot toSlot = screen.getScreenHandler().getSlot(j);
                    if (toSlot.getStack().isEmpty()) {
                        if (!this.getScreenHandler().getCursorStack().isEmpty()) {
                            if (fromStack.isOf(this.getScreenHandler().getCursorStack().getItem())) {
                                this.sendClickSlotPacket(i, SlotActionType.QUICK_MOVE);
                                break;
                            }
                        } else {
                            this.sendClickSlotPacket(i, SlotActionType.QUICK_MOVE);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Sends transferring item packet from {@code client to server.}
     */
    // ChatGPT
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

        ComponentChangesHash.ComponentHasher hasher = networkHandler.method_68823();

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
     * Handles rendering, such as the search field and transferring inventory button textures.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void renderField(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (QualityOfQuesoClient.options().chestSearch && this.searchField != null) {
            this.searchField.render(context, mouseX, mouseY, deltaTicks); // Render search field
            this.shouldButtonBeActive(false, null, this.transferContainerButton);
        }
        if (QualityOfQuesoClient.options().inventorySorting && this.isValidScreen()) {
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
            boolean hovering = transferInventoryButton.isMouseOver(mouseX, mouseY);
            if (this.shouldButtonBeActive(true, playerInventory, transferInventoryButton)) {
                if (this.altDown()) {
                    // Alt is held, activate and allow "keep active" if mouse is over
                    transferInventoryButton.active = true;
                    this.keepInventoryButtonActive = hovering;
                } else {
                    // Alt is not held – only keep active if still hovering from last Alt-down
                    if (!hovering) {
                        this.keepInventoryButtonActive = false;
                    }
                    transferInventoryButton.active = this.keepInventoryButtonActive;
                }
            }
            this.shouldButtonBeActive(true, playerInventory, transferInventoryButton);
            // Render transfer chest -> inventory button texture
            if (this.transferContainerButton != null) {
                if (!this.transferContainerButton.active) {
                    this.renderTransferButtonTexture("transfer_container_button_inactive", this.transferContainerButton, context);
                } else {
                    this.renderTransferButtonTexture(this.transferContainerButton.isMouseOver(mouseX, mouseY) ? "transfer_container_button_hovered" : "transfer_container_button", this.transferContainerButton, context);
                }
            }
            // Render transfer inventory -> chest button texture
            if (!this.shouldButtonBeActive(true, playerInventory, transferInventoryButton)) {
                this.renderTransferButtonTexture("transfer_inventory_button_inactive", transferInventoryButton, context);
            } else if (!this.altDown()) {
                this.renderTransferButtonTexture("transfer_inventory_button_hold_alt", transferInventoryButton, context);
            } else {
                this.renderTransferButtonTexture(transferInventoryButton.isMouseOver(mouseX, mouseY) ? "transfer_inventory_button_hovered" : "transfer_inventory_button", transferInventoryButton, context);
            }
        }
    }

    /**
     * Grays out any containerSlot which doesn't contain the item name being searched.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V", shift = At.Shift.AFTER))
    private void grayOutSlot(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (this.searchField != null && !this.getSearchFieldText().isEmpty()) {
            for (int i = 0; i < this.getInventorySize(); i++) {
                Slot slot = this.getScreenHandler().getSlot(i);
                if (!this.isSlotAvailable(this.getSearchFieldText(), slot)) {
                    this.makeSlotUnavailable(context, slot);
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
        return QualityOfQuesoClient.options().searchInventory ? this.getScreenHandler().slots.size() : this.inventory.size();
    }

    /**
     * @return {@code true} if the hovered slot has an item (assuming hovered slot isn't {@code null}).
     */
    @Unique
    private boolean hoveredSlotHasItem() {
        return this.focusedSlot != null && this.focusedSlot.getStack() != ItemStack.EMPTY;
    }

    /**
     * Gets the current ALT button status.
     */
    @Unique
    private boolean altDown() {
        return !QualityOfQuesoClient.options().requireAltToSort || hasAltDown();
    }

    /**
     * @return the {@code X} value for transferring item buttons.
     */
    @Unique
    private int getTransferButtonX(boolean chestToInventory) {
        int barWidth = (int)((double)this.backgroundWidth * 0.6);
        return chestToInventory ? this.width / 2 + barWidth / 2 + 16 : this.width / 2 + barWidth / 2 + 4;
    }

    /**
     * @return the {@code Y} value for transferring item buttons.
     */
    @Unique
    private int getTransferButtonY() {
        return this.inventory.size() > 27 ? this.y + this.titleY + 120 : this.y + this.titleY + 66;
    }

    /**
     * Grays out a containerSlot.
     */
    @Unique
    private void makeSlotUnavailable(DrawContext context, Slot slot) {
        context.fillGradient(RenderLayer.getGuiOverlay(), slot.x, slot.y, slot.x + 16, slot.y + 16, -1275068416, -1275068416, 0);
    }

    /**
     * Renders a transfer button texture.
     */
    @Unique
    private void renderTransferButtonTexture(String id, ClickableWidget buttonReference, DrawContext context) {
        context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("qualityofqueso:textures/gui/"+id+".png"), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
    }

    /**
     * @return {@code true} if the {@code inventory} has something in it.
     */
    @Unique
    private boolean shouldButtonBeActive(boolean isPlayerInventory, @Nullable PlayerInventory playerInventory, ClickableWidget button) {
        if (button == null) {
            return false;
        }
        int size = isPlayerInventory ? playerInventory.size() : this.inventory.size();
        int j = 0;
        for (int i = 0; i < size; i++) {
            ItemStack stack = isPlayerInventory ? playerInventory.getStack(i) : this.getScreenHandler().getSlot(i).getStack();
            if (!stack.isEmpty()) {
                j++;
                button.active = true;
            }
        }
        if (j == 0 || this.areAllSlotsUnavailable(isPlayerInventory, isPlayerInventory ? playerInventory : null)) {
            button.active = false;
            return false;
        }
        return true;
    }

    /**
     * @return {@code true} if all slots are grayed out, or {@code unavailable.}
     */
    @Unique
    private boolean areAllSlotsUnavailable(boolean isPlayerInventory, @Nullable PlayerInventory playerInventory) {
        int j = 0;
        List<Slot> playerSlots = new ArrayList<>();
        if (isPlayerInventory) {
            for (Slot s : this.getScreenHandler().slots) {
                if (s.inventory == playerInventory) {
                    playerSlots.add(s);
                }
            }
            for (Slot slot : playerSlots) {
                if (isSlotAvailable(this.getSearchFieldText(), slot)) {
                    j++;
                }
            }
        } else {
            for (int i = 0; i < this.inventory.size(); i++) {
                Slot slot = this.getScreenHandler().getSlot(i);
                if (isSlotAvailable(this.getSearchFieldText(), slot)) {
                    j++;
                }
            }
        }
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
     * @return {@code true} if an item is found from {@code searchQuery}.
     */
    @Unique
    private boolean isSlotAvailable(String searchQuery, Slot slot) {
        ItemStack stack = slot.getStack();

        // If slot is empty, return false (slot is unavailable)
        if (stack.isEmpty()) {
            return false;
        }

        String itemName = stack.getItemName().getString().toLowerCase();
        String customName = stack.getCustomName() != null ? stack.getName().getString().toLowerCase() : "";

        String[] terms = searchQuery.split(",");
        // If slot contains a comma, for each query searched (separated by each comma), return true if search query'namespace find an item (make slot available)
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
     * Handles key pressing correctly and implements functionality for the {@link ModKeybinds#QUICK_EQUIP} keybind.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == ModKeybinds.QUICK_EQUIP.boundKey.getCode() && this.focusedSlot != null && (this.screen instanceof InventoryScreen || this.screen instanceof CreativeInventoryScreen)) {
            ItemStack stack = this.focusedSlot.getStack();
            if (stack.isIn(ItemTags.HEAD_ARMOR)) {
                if (this.client.player.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
                    this.sendClickSlotPacket(this.focusedSlot.id, SlotActionType.QUICK_MOVE);
                } else {
                    this.quickSwap(this.focusedSlot.id, EquipmentSlot.HEAD);
                }
            } else if (stack.isIn(ItemTags.CHEST_ARMOR) || stack.isOf(Items.ELYTRA)) {
                if (this.client.player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) {
                    this.sendClickSlotPacket(this.focusedSlot.id, SlotActionType.QUICK_MOVE);
                } else {
                    this.quickSwap(this.focusedSlot.id, EquipmentSlot.CHEST);
                }
            } else if (stack.isIn(ItemTags.LEG_ARMOR)) {
                if (this.client.player.getEquippedStack(EquipmentSlot.LEGS).isEmpty()) {
                    this.sendClickSlotPacket(this.focusedSlot.id, SlotActionType.QUICK_MOVE);
                } else {
                    this.quickSwap(this.focusedSlot.id, EquipmentSlot.LEGS);
                }
            } else if (stack.isIn(ItemTags.FOOT_ARMOR)) {
                if (this.client.player.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
                    this.sendClickSlotPacket(this.focusedSlot.id, SlotActionType.QUICK_MOVE);
                } else {
                    this.quickSwap(this.focusedSlot.id, EquipmentSlot.FEET);
                }
            }
        }
        HandledScreen<?> handledScreen = (HandledScreen<?>)(Object)this;
        boolean ignoreTyping = this.hoveredSlotHasItem();
        boolean secondaryIgnoreTyping = false, hotbarKeyPressed = false;
        // handle disallowed keys
        for (int key : QualityOfQuesoClient.disallowedKeys) {
            if (keyCode == key) {
                ignoreTyping = true; secondaryIgnoreTyping = true;
                break;
            }
        }
        // handle switching items from hotbar to another containerSlot in inventory; cancel out typing if an item can be moved
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

        for (int key : QualityOfQuesoClient.keys) {
            if (keyCode == key) {
                secondaryIgnoreTyping = false;
                cir.setReturnValue(true);
                break;
            }
        }
        boolean numberKeyPressed = false;
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

        boolean dropKeyPressed = false;
        if (keyCode == MinecraftClient.getInstance().options.dropKey.boundKey.getCode()) {
            secondaryIgnoreTyping = true; dropKeyPressed = true;
        }

        boolean cannotType = (numberKeyPressed || hotbarKeyPressed || dropKeyPressed) && this.hoveredSlotHasItem();

        // if clear, begin type anywhere (on RECIPE / CREATIVE inventory screens)
        if (QualityOfQuesoClient.options().betterSearching && handledScreen instanceof RecipeBookScreen<?> recipeBookScreen) {
            if (!ignoreTyping) {
                if (!recipeBookScreen.recipeBook.isOpen()) {
                    recipeBookScreen.recipeBook.toggleOpen();
                    this.refreshWidgetPositions();
                }
                recipeBookScreen.recipeBook.searchField.setFocused(true);
            }
            if (recipeBookScreen.recipeBook.searchField != null && recipeBookScreen.recipeBook.searchField.isFocused()) {
                cir.setReturnValue(recipeBookScreen.recipeBook.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers));
            }
        }
        // handle chest searching
        else if (QualityOfQuesoClient.options().chestSearch && this.isValidScreen()) {
            // Prevent drop key from interfering with search field
            if (!secondaryIgnoreTyping) {
                this.searchField.setFocused(true);
            } else {
                if (this.searchField.isFocused() && cannotType) {
                    this.searchField.setFocused(false);
                }
            }

            if (this.searchField.isFocused() && this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
                cir.setReturnValue(true);
            }
        }
    }

    /**
     * Quickly swaps two items in the player's inventory.
     */
    @Unique
    private void quickSwap(int sourceSlot, EquipmentSlot slot) {
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
        if (ModOptions.OPTIONS.betterGuiExit && this.getScreenHandler().getCursorStack().isEmpty() && button == 0 && slot == null) {
            this.close();
        }
    }

    /**
     * Saves the current text in {@code searchField.getText()} to memory so it can be referenced when opening a container screen again.
     */
    @Inject(method = "close", at = @At("TAIL"))
    private void saveSearchText(CallbackInfo ci) {
        if (QualityOfQuesoClient.options().chestSearch && QualityOfQuesoClient.options().saveSearchText && this.searchField != null && this.isValidScreen()) {
            QualityOfQuesoClient.SAVED_TEXT = this.searchField.getText();
        }
    }

    /**
     * Allows correct functionality for typing into search field without clicking on it.
     */
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (QualityOfQuesoClient.options().chestSearch && this.searchField != null && this.searchField.isFocused()) {
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
            String text = this.getSearchFieldText();
            boolean refocus = this.searchField.isFocused();
            this.init(client, width, height);
            this.searchField.setText(text);
            this.searchField.setFocused(refocus);
        }
    }

    /**
     * @return valid handled screens which can use the container search feature.
     */
    @Unique
    private boolean isValidScreen() {
        HandledScreen<?> handledScreen = (HandledScreen<?>)(Object)this;
        return handledScreen instanceof GenericContainerScreen || handledScreen instanceof ShulkerBoxScreen;
    }
}