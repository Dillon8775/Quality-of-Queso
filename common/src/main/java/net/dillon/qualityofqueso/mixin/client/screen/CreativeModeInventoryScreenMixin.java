package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.util.ModConstants;
import net.dillon.qualityofqueso.widget.SearchBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ManagementHelper.hoveredSlotHasItem;
import static net.dillon.qualityofqueso.helper.MethodHelper.key;
import static net.dillon.qualityofqueso.helper.ModHelper.*;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow
    private EditBox searchBox;
    @Shadow
    private static CreativeModeTab selectedTab;
    @Shadow
    private boolean ignoreTextInput;
    @Shadow
    protected abstract void refreshSearchResults();
    @Shadow
    protected abstract void selectTab(CreativeModeTab pTab);

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    /**
     * Saves current text in the creative menu's search bar.
     */
    @Override
    public void onClose() {
        if (this.searchBox != null) {
            ModConstants.SAVED_CREATIVE_MENU_TEXT = this.searchBox.getValue();
        }
        super.onClose();
    }

    /**
     * Sets the last known text in the search bar.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void setText(CallbackInfo ci) {
        if (!modEnabled(this.minecraft) || !clientOptionsInstance().getSearchingOptions().saveSearchText || this.searchBox == null) {
            return;
        }

        this.searchBox.setValue(ModConstants.SAVED_CREATIVE_MENU_TEXT);
        this.refreshSearchResults();
    }

    /**
     * Applies the same functionality that the {@link SearchBar} uses, to the creative menu's search bar.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleSearchBarClicking(double mouseX, double mouseY, int bl, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft) || this.searchBox == null || !this.searchBox.isHovered()) {
            return;
        }

        if (bl == 1) {
            this.searchBox.setValue("");
            this.setFocused(false);
            cir.setReturnValue(true);
        } else if (bl == 0) {
            this.setFocused(true);
            this.searchBox.onClick(mouseX, mouseY);
            cir.setReturnValue(true);
        }
    }

    /**
     * Closes the screen when clicking outside of the menu.
     */
    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void quickGuiClose(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (clientOptionsInstance().getMiscOptions().quickGuiExit && this.menu.getCarried().isEmpty() && mouseButton == 0 && slot == null) {
            this.onClose();
        }
    }

    /**
     * Handles key pressing in the creative mode tab screen and fixes a weird bug with the creative tab search.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void allowCharsUnfocusSearchBar(int keycode, int scancode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (!Screen.hasShiftDown() && selectedTab.getType() == CreativeModeTab.Type.SEARCH && hoveredSlotHasItem(this.hoveredSlot) && this.searchBox != null && this.searchBox.isFocused()) {
            for (int i = 0; i < 9; i++) {
                if (Minecraft.getInstance().options.keyHotbarSlots[i].matches(keycode, scancode)) {
                    this.ignoreTextInput = true;
                    this.searchBox.setFocused(true);
                    cir.setReturnValue(super.keyPressed(keycode, scancode, modifiers));
                }
            }
        }

        if (clientOptionsInstance().getSearchingOptions().quickSearch.creativeMenu()) {
            if (hoveredSlotHasItem(this.hoveredSlot) && !this.searchBox.isFocused()) {
                this.ignoreTextInput = true;
                cir.setReturnValue(super.keyPressed(keycode, scancode, modifiers));
            }

            if (clientOptionsInstance().getAccessibilityOptions().preventEFromTyping && keycode == key(Minecraft.getInstance().options.keyInventory).getValue() && !this.searchBox.isFocused()) {
                this.ignoreTextInput = true;
                this.onClose();
                cir.setReturnValue(true);
            }

            for (int key : popularKeys()) {
                if (keycode == key) {
                    this.ignoreTextInput = false;
                    cir.setReturnValue(true);
                }
            }

            for (int i = 9; i < allDisallowedKeys().size(); i++) {
                if (!Screen.hasShiftDown() && keycode == allDisallowedKeys().get(i)) {
                    this.ignoreTextInput = true;
                    cir.setReturnValue(super.keyPressed(keycode, scancode, modifiers));
                }
            }
        }
    }

    /**
     * Improves typing in the creative menu, for quick searching and moving of slots.
     */
    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void improveCreativeMenuSearching(char chr, int bl, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft) || !clientOptionsInstance().getSearchingOptions().quickSearch.creativeMenu()) {
            return;
        }

        if (this.ignoreTextInput || (!(clientOptionsInstance().getSearchingOptions().quickSearch.creativeMenu()) && selectedTab.getType() != CreativeModeTab.Type.SEARCH)) {
            cir.setReturnValue(false);
        }

        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            for (int i = 0; i < 9; i++) {
                if (Minecraft.getInstance().options.keyHotbarSlots[i].consumeClick()) {
                    if (this.searchBox != null && this.searchBox.isFocused()) {
                        this.searchBox.setFocused(false);
                    }
                    cir.setReturnValue(true);
                }
            }
        } else {
            this.selectTab(CreativeModeTabs.searchTab());
        }
    }
}