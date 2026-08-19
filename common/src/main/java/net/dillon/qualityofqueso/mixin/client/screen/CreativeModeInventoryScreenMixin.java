package net.dillon.qualityofqueso.mixin.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.helper.GuiHelper;
import net.dillon.qualityofqueso.widget.SearchBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ManagementHelper.hoveredSlotHasItem;
import static net.dillon.qualityofqueso.helper.MethodHelper.key;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

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
    @Shadow @Final @Mutable
    private boolean displayOperatorCreativeTab;

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    /**
     * Saves current text in the creative menu's search bar.
     */
    @Override
    public void onClose() {
        if (this.searchBox != null) {
            updateClient(client -> client.searching().savedCreativeMenuText = this.searchBox.getValue());
        }
        super.onClose();
    }

    /**
     * Enables the display operator creative tab by default.
     */
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;displayOperatorCreativeTab:Z"))
    private void enableOperatorTabByDefault(CreativeModeInventoryScreen instance, boolean value) {
        this.displayOperatorCreativeTab = client().accessibility().operatorItemsTab || Minecraft.getInstance().options.operatorItemsTab().get();
    }

    /**
     * Sets the last known text in the search bar.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void setText(CallbackInfo ci) {
        if (!modEnabled(this.minecraft) || !client().searching().saveSearchText || this.searchBox == null || selectedTab != CreativeModeTabs.searchTab()) {
            return;
        }

        this.searchBox.setValue(client().searching().savedCreativeMenuText);
        this.refreshSearchResults();
    }

    /**
     * Applies the same functionality that the {@link SearchBar} uses, to the creative menu's search bar.
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleSearchBarClicking(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft) || this.searchBox == null || !this.searchBox.isHovered()) {
            return;
        }

        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
            this.searchBox.setValue("");
            this.refreshSearchResults();
            this.setFocused(false);
            cir.setReturnValue(true);
        } else if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            this.setFocused(true);
            this.searchBox.onClick(event, doubleClick);
            cir.setReturnValue(true);
        }
    }

    /**
     * Closes the screen when clicking outside of the menu.
     */
    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void quickGuiClose(Slot slot, int slotId, int mouseButton, ContainerInput type, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (client().misc().quickGuiExit && this.menu.getCarried().isEmpty() && mouseButton == 0 && slot == null) {
            this.onClose();
        }
    }

    /**
     * Handles key pressing in the creative mode tab screen and fixes a weird bug with the creative tab search.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void allowCharsUnfocusSearchBar(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (!Minecraft.getInstance().hasShiftDown() && selectedTab.getType() == CreativeModeTab.Type.SEARCH && hoveredSlotHasItem(this.hoveredSlot) && this.searchBox != null) {
            for (int i = 0; i < 9; i++) {
                if (Minecraft.getInstance().options.keyHotbarSlots[i].matches(event)) {
                    this.ignoreTextInput = true;
                    boolean handled = super.keyPressed(event);
                    this.searchBox.setFocused(false);
                    cir.setReturnValue(handled);
                    return;
                }
            }
        }

        if (client().searching().quickSearch.creativeMenu()) {
            if (hoveredSlotHasItem(this.hoveredSlot) && !this.searchBox.isFocused()) {
                this.ignoreTextInput = true;
                cir.setReturnValue(super.keyPressed(event));
                return;
            }

            if (client().accessibility().preventEFromTyping && event.key() == key(Minecraft.getInstance().options.keyInventory).getValue() && !this.searchBox.isFocused()) {
                this.ignoreTextInput = true;
                this.onClose();
                cir.setReturnValue(true);
                return;
            }

            for (int key : popularKeys()) {
                if (event.key() == key) {
                    this.ignoreTextInput = false;
                    cir.setReturnValue(true);
                    return;
                }
            }

            for (int i = 9; i < allDisallowedKeys().size(); i++) {
                if (event.key() == allDisallowedKeys().get(i)) {
                    if (event.key() != InputConstants.KEY_LCONTROL || this.searchBox == null || !this.searchBox.isFocused()) {
                        this.ignoreTextInput = true;
                        cir.setReturnValue(super.keyPressed(event));
                        return;
                    }
                }
            }

            if (this.ignoreTextInput || (!(client().searching().quickSearch.creativeMenu()) && selectedTab.getType() != CreativeModeTab.Type.SEARCH)) {
                cir.setReturnValue(false);
                return;
            }

            for (int i = 0; i < 9; i++) {
                if (this.hoveredSlot != null && Minecraft.getInstance().options.keyHotbarSlots[i].matches(event)) {
                    Inventory inventory = this.minecraft.player.getInventory();
                    ItemStack stack = inventory.getItem(i);

                    if (!stack.isEmpty()) {
                        if (this.searchBox != null && this.searchBox.isFocused()) {
                            this.searchBox.setFocused(false);
                        }

                        // Prevents opening search tab
                        this.ignoreTextInput = true;
                        cir.setReturnValue(super.keyPressed(event));
                        return;
                    }
                }
            }

            boolean overrideFocus = selectedTab.getType() != CreativeModeTab.Type.SEARCH;
            this.selectTab(CreativeModeTabs.searchTab());
            GuiHelper.autoFocusElement(this.searchBox, event, true, overrideFocus);
            this.refreshSearchResults();
        }
    }
}