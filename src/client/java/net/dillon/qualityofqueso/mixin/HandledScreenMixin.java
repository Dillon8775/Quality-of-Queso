package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.dillon.qualityofqueso.option.ModOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
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
    @Unique
    private TextFieldWidget searchField;
    @Shadow
    protected int backgroundWidth;
    @Shadow
    protected int titleY;
    @Shadow
    protected int y;
    @Shadow
    public abstract T getScreenHandler();
    @Shadow @Nullable
    public Slot focusedSlot;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    /**
     * Creates the search field and repositions the mouse in a good position to begin typing.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (QualityOfQuesoClient.options().containerSearching && this.isValidScreen()) {
            int barWidth = (int)((double)this.backgroundWidth * 0.6);
            this.searchField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, this.width / 2 + barWidth / 2 - 64, this.y + this.titleY - 2, 90, 12, null);
            this.searchField.setMaxLength(50);
            this.searchField.setDrawsBackground(true);
            this.searchField.setEditableColor(16777215);
            this.addSelectableChild(this.searchField);
        }
    }

    /**
     * Renders the search field.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void renderField(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (QualityOfQuesoClient.options().containerSearching && this.searchField != null) {
            this.searchField.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    /**
     * Grays out any slot which doesn't contain the item name being searched.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V", shift = At.Shift.AFTER))
    private void grayoutSlot(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (QualityOfQuesoClient.options().containerSearching && this.searchField != null) {
            String text = this.searchField.getText();
            if (text != null) {
                for (int i = 0; i < this.getScreenHandler().slots.size(); i++) {
                    Slot slot = this.getScreenHandler().getSlot(i);
                    ItemStack stack = slot.getStack();
                    boolean shouldFill = true;
                    if (stack.hasEnchantments() || stack.isOf(Items.ENCHANTED_BOOK)) {
                        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
                        for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                            String encName = Text.translatable(enchantment.value().description().getString()).getString();
                            String name = encName + " " + enchantments.getLevel(enchantment);
                            if (name.toLowerCase().contains(text)) {
                                shouldFill = false;
                            }
                        }
                        if (shouldFill) {
                            grayoutSlot(context, slot);
                        }
                    } else {
                        if (!stack.getName().getString().toLowerCase().contains(text)) {
                            grayoutSlot(context, slot);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles key pressing correctly.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handleKeyPressing(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        HandledScreen<?> handledScreen = (HandledScreen<?>)(Object)this;
        boolean ignoreTyping = (this.focusedSlot != null && this.focusedSlot.getStack() != ItemStack.EMPTY);
        boolean secondaryIgnoreTyping = false;
        // handle disallowed keys
        for (int key : QualityOfQuesoClient.disallowedKeys) {
            if (keyCode == key) {
                ignoreTyping = true;
                secondaryIgnoreTyping = true;
                break;
            }
        }
        // handle switching items from hotbar to another slot in inventory; cancel out typing if an item can be moved
        if (this.getScreenHandler().getCursorStack().isEmpty() && this.focusedSlot != null) {
            for (int i = 0; i < 9; i++) {
                if (this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
                    ignoreTyping = true;
                    secondaryIgnoreTyping = true;
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
        else if (QualityOfQuesoClient.options().containerSearching && this.isValidScreen()) {
            for (int key : QualityOfQuesoClient.keys) {
                if (keyCode == key) {
                    secondaryIgnoreTyping = false;
                    cir.setReturnValue(true);
                    break;
                }
            }
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
                    ignoreTyping = false;
                    break;
                }
            }

            if (!secondaryIgnoreTyping) {
                this.searchField.setFocused(true);
                this.setInitialFocus(this.searchField);
            } else {
                if (this.searchField.isFocused() && ignoreTyping && this.focusedSlot != null && this.focusedSlot.getStack() != ItemStack.EMPTY) {
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
        if (QualityOfQuesoClient.options().containerSearching && this.searchField != null && this.searchField.isFocused()) {
            return this.searchField.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    /**
     * Grays out a slot.
     */
    @Unique
    private void grayoutSlot(DrawContext context, Slot slot) {
        context.fillGradient(RenderLayer.getGuiOverlay(), slot.x, slot.y, slot.x + 16, slot.y + 16, -1275068416, -1275068416, 0);
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