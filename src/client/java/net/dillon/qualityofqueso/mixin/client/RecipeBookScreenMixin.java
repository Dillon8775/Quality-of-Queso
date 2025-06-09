package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(RecipeBookScreen.class)
public abstract class RecipeBookScreenMixin<T extends AbstractRecipeScreenHandler> extends HandledScreen<T> {
    @Shadow @Final
    public RecipeBookWidget<?> recipeBook;

    public RecipeBookScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    /**
     * Allows for typing anywhere to begin searching in the recipe GUI.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void searchAnyways(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (QualityOfQuesoClient.options().betterSearching) {
            boolean ignoreTyping = (this.focusedSlot != null && this.focusedSlot.getStack() != ItemStack.EMPTY);
            // handle disallowed keys
            for (int key : QualityOfQuesoClient.disallowedKeys) {
                if (keyCode == key) {
                    ignoreTyping = true;
                    break;
                }
            }
            // handle switching items from hotbar to another slot in inventory; cancel out typing if an item can be moved
            if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
                for (int i = 0; i < 9; i++) {
                    if (this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
                        ignoreTyping = true;
                        break;
                    }
                }
            }
            // if clear, type anywhere
            if (!ignoreTyping) {
                if (!this.recipeBook.isOpen()) {
                    this.recipeBook.toggleOpen();
                    this.refreshWidgetPositions();
                }
                this.recipeBook.searchField.setFocused(true);
                cir.setReturnValue(true);
            }
            if (this.recipeBook.searchField != null && this.recipeBook.searchField.isFocused()) {
                cir.setReturnValue(this.recipeBook.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers));
            }
        }
    }
}