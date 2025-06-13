package net.dillon.qualityofqueso.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.dillon.qualityofqueso.client.SensitiveButton;
import net.dillon.qualityofqueso.option.ModOptions;
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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Shadow
    protected int backgroundWidth;
    @Shadow
    protected int x;
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
     * Creates the search field and repositions the mouse in a good position to begin typing.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (this.isValidScreen()) {
            if (screen instanceof ShulkerBoxScreen shulkerBoxScreen) {
                this.inventory = shulkerBoxScreen.getScreenHandler().inventory;
            } else if (screen instanceof GenericContainerScreen genericContainerScreen) {
                this.inventory = genericContainerScreen.getScreenHandler().getInventory();
            } else {
                this.inventory = null;
            }

            if (QualityOfQuesoClient.options().chestSearch) {
                int barWidth = (int)((double)this.backgroundWidth * 0.6);
                this.searchField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, this.width / 2 + barWidth / 2 - 64, this.y + this.titleY - 2, 90, 12, null);
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
     * <p>boolean variable {@code reverse} is true if {@code chest -> inventory,} otherwise {@code inventory -> chest.}</p>
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
                        this.sendClickSlotPacket(i);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Sends transferring item packet from client to server.
     */
    // ChatGPT
    @Unique
    private void sendClickSlotPacket(int slotIndex) {
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
                SlotActionType.QUICK_MOVE,
                modifiedStacks,
                cursorHash
        );

        networkHandler.sendPacket(packet);
    }

    /**
     * Renders the search field.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void renderField(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (QualityOfQuesoClient.options().chestSearch && this.searchField != null) {
            this.searchField.render(context, mouseX, mouseY, deltaTicks); // Render search field
        }
        if (QualityOfQuesoClient.options().inventorySorting && this.isValidScreen()) {
            int j = 0;
            for (int i = 0; i < this.inventory.size(); i++) {
                Slot slot = this.getScreenHandler().getSlot(i);
                if (!slot.getStack().isEmpty()) {
                    j++;
                    this.transferContainerButton.active = true;
                }
            }
            if (j == 0 || this.allSlotsUnavailable()) {
                this.transferContainerButton.active = false;
            }
            // Initialize transfer inventory button
            ClickableWidget transferInventoryButton = this.addSelectableChild(
                    new SensitiveButton(
                            this.getTransferButtonX(false),
                            this.getTransferButtonY(),
                            10,
                            10,
                            ModTexts.BLANK,
                            b -> this.transferItems(this.screen, false),
                            () -> hasAltDown() || this.keepInventoryButtonActive
                    )
            );
            // Transfer inventory button is only active if it is already active and hovered, otherwise only becomes active if ALT is pressed
            boolean hovering = transferInventoryButton.isMouseOver(mouseX, mouseY);
            if (hasAltDown()) {
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
            // Render transfer chest -> inventory button texture
            if (this.transferContainerButton != null) {
                if (!this.transferContainerButton.active) {
                    context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("qualityofqueso:textures/gui/transfer_container_button_inactive.png"), this.transferContainerButton.getX() - 1, this.transferContainerButton.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
                } else {
                    context.drawTexture(RenderLayer::getGuiTextured, Identifier.of(this.transferContainerButton.isMouseOver(mouseX, mouseY) ? "qualityofqueso:textures/gui/transfer_container_button_hovered.png" : "qualityofqueso:textures/gui/transfer_container_button.png"), this.transferContainerButton.getX() - 1, this.transferContainerButton.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
                }
            }
            // Render transfer inventory -> chest button texture
            if (!transferInventoryButton.active) {
                context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("qualityofqueso:textures/gui/transfer_inventory_button_inactive.png"), transferInventoryButton.getX() - 1, transferInventoryButton.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
            } else {
                context.drawTexture(RenderLayer::getGuiTextured, Identifier.of(transferInventoryButton.isMouseOver(mouseX, mouseY) ? "qualityofqueso:textures/gui/transfer_inventory_button_hovered.png" : "qualityofqueso:textures/gui/transfer_inventory_button.png"), transferInventoryButton.getX() - 1, transferInventoryButton.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
            }
        }
    }

    /**
     * Grays out any containerSlot which doesn't contain the item name being searched.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V", shift = At.Shift.AFTER))
    private void grayoutSlot(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
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
     * Returns the {@code search field's} text.
     */
    @Unique
    private String getSearchFieldText() {
        return this.searchField.getText();
    }

    /**
     * Returns the inventory (size) that should be searched.
     */
    @Unique
    private int getInventorySize() {
        return QualityOfQuesoClient.options().searchInventory ? this.getScreenHandler().slots.size() : this.inventory.size();
    }

    /**
     * Returns the {@code X} value for transferring item buttons.
     */
    @Unique
    private int getTransferButtonX(boolean chestToInventory) {
        int barWidth = (int)((double)this.backgroundWidth * 0.6);
        return chestToInventory ? this.width / 2 + barWidth / 2 + 16 : this.width / 2 + barWidth / 2 + 4;
    }

    /**
     * Returns the {@code Y} value for transferring item buttons.
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
     * Returns true if all slots are grayed out, or unavailable.
     */
    @Unique
    private boolean allSlotsUnavailable() {
        int j = 0;
        for (int i = 0; i < this.inventory.size(); i++) {
            Slot slot = this.getScreenHandler().getSlot(i);
            if (isSlotAvailable(this.getSearchFieldText(), slot)) {
                j++;
            }
        }
        return j == 0;
    }

    /**
     * Returns true if an item is found from search result. Returns false otherwise.
     */
    @Unique
    private boolean isSlotAvailable(String searchQuery, Slot slot) {
        ItemStack stack = slot.getStack();

        if (stack.isEmpty()) {
            return false;
        }

        if (searchQuery.startsWith("#")) {
            String tag = searchQuery.substring(1);
            Identifier id = Identifier.tryParse(tag);

            if (id != null) {
                TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, id);
                if (stack.isIn(tagKey)) {
                    return true;
                }
            }
        }

        if (stack.hasEnchantments() || stack.isOf(Items.ENCHANTED_BOOK)) {
            ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
            for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                String encName = enchantment.value().description().getString();
                String fullName = encName + " " + enchantments.getLevel(enchantment);
                if (fullName.toLowerCase().contains(searchQuery)) {
                    return true;
                }
            }
        }

        String itemName = stack.getItemName().getString().toLowerCase();
        String customName = stack.getCustomName() != null ? stack.getName().getString().toLowerCase() : "";

        return itemName.contains(searchQuery) || customName.contains(searchQuery);
    }

    /**
     * Handles key pressing correctly.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        HandledScreen<?> handledScreen = (HandledScreen<?>)(Object)this;
        boolean ignoreTyping = (this.focusedSlot != null && this.focusedSlot.getStack() != ItemStack.EMPTY);
        boolean secondaryIgnoreTyping = false;
        boolean hotbarKeyPressed = false;
        // handle disallowed keys
        for (int key : QualityOfQuesoClient.disallowedKeys) {
            if (keyCode == key) {
                ignoreTyping = true;
                secondaryIgnoreTyping = true;
                break;
            }
        }
        // handle switching items from hotbar to another containerSlot in inventory; cancel out typing if an item can be moved
        if (this.getScreenHandler().getCursorStack().isEmpty() && this.focusedSlot != null) {
            for (int i = 0; i < 9; i++) {
                if (this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
                    ignoreTyping = true;
                    secondaryIgnoreTyping = true;
                    hotbarKeyPressed = true;
                    break;
                }
            }
        }

        // if clear, begin type anywhere (on RECIPE / CREATIVE inventory screens)
        if (QualityOfQuesoClient.options().betterSearching && handledScreen instanceof RecipeBookScreen<?> recipeBookScreen) {
            if (!ignoreTyping) {
                if (!recipeBookScreen.recipeBook.isOpen()) {
                    recipeBookScreen.recipeBook.toggleOpen();
                    this.refreshWidgetPositions();
                }
                recipeBookScreen.recipeBook.searchField.setFocused(true);
                cir.setReturnValue(true);
            }
            if (recipeBookScreen.recipeBook.searchField != null && recipeBookScreen.recipeBook.searchField.isFocused()) {
                cir.setReturnValue(recipeBookScreen.recipeBook.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers));
            }
        }
        // handle chest searching
        else if (QualityOfQuesoClient.options().chestSearch && this.isValidScreen()) {
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

            if (!secondaryIgnoreTyping) {
                this.searchField.setFocused(true);
                this.setInitialFocus(this.searchField);
            } else {
                if (this.searchField.isFocused() && (numberKeyPressed || hotbarKeyPressed) && this.focusedSlot != null && this.focusedSlot.getStack() != ItemStack.EMPTY) {
                    this.searchField.setFocused(false);
                }
            }

            if (this.searchField.isFocused() && this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
                cir.setReturnValue(true);
            }
        }
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
     * Returns valid handled screens which can use the container search feature.
     */
    @Unique
    private boolean isValidScreen() {
        HandledScreen<?> handledScreen = (HandledScreen<?>)(Object)this;
        return handledScreen instanceof GenericContainerScreen || handledScreen instanceof ShulkerBoxScreen;
    }
}