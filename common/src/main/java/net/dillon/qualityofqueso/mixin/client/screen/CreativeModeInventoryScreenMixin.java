package net.dillon.qualityofqueso.mixin.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

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
     * Closes the screen when clicking outside of the menu.
     */
    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void quickGuiClose(Slot slot, int slotId, int mouseButton, ContainerInput type, CallbackInfo ci) {
        if (!modEnabled(this.minecraft)) {
            return;
        }

        if (options().misc.quickGuiExit && this.menu.getCarried().isEmpty() && mouseButton == 0 && slot == null) {
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

        if (!Minecraft.getInstance().hasShiftDown() && selectedTab.getType() == CreativeModeTab.Type.SEARCH && hoveredSlotHasItem(this.hoveredSlot) && this.searchBox != null && this.searchBox.isFocused()) {
            for (int i = 0; i < 9; i++) {
                if (Minecraft.getInstance().options.keyHotbarSlots[i].matches(event)) {
                    this.ignoreTextInput = true;
                    this.searchBox.setFocused(true);
                    cir.setReturnValue(super.keyPressed(event));
                }
            }
        }

        if (options().searching.quickSearch.enabled()) {
            if (hoveredSlotHasItem(this.hoveredSlot) && !this.searchBox.isFocused()) {
                this.ignoreTextInput = true;
                cir.setReturnValue(super.keyPressed(event));
            }

            if (options().accessibility.preventEFromTyping && event.key() == key(Minecraft.getInstance().options.keyInventory).getValue() && !this.searchBox.isFocused()) {
                this.ignoreTextInput = true;
                this.onClose();
                cir.setReturnValue(true);
            }

            for (int key : popularKeys()) {
                if (event.key() == key) {
                    this.ignoreTextInput = false;
                    cir.setReturnValue(true);
                }
            }

            for (int i = 9; i < allDisallowedKeys().size(); i++) {
                if (!Minecraft.getInstance().hasShiftDown() && event.key() == allDisallowedKeys().get(i)) {
                    this.ignoreTextInput = true;
                    cir.setReturnValue(super.keyPressed(event));
                }
            }
        }
    }

    /**
     * Allow typing in creative menu regardless of what menu.
     */
    @Overwrite
    public boolean charTyped(CharacterEvent input) {
        if (this.ignoreTextInput || (!(options().searching.quickSearch.enabled()) && selectedTab.getType() != CreativeModeTab.Type.SEARCH)) {
            return false;
        } else {
            if (modEnabled(this.minecraft) && options().searching.quickSearch.enabled()) {
                if (this.hoveredSlot != null && this.hoveredSlot.hasItem() && this.searchBox.isFocused()) {
                    for (int i = 0; i < 9; i++) {
                        if (Minecraft.getInstance().options.keyHotbarSlots[i].consumeClick()) {
                            this.searchBox.setFocused(false);
                            return true;
                        }
                    }
                } else {
                    this.selectTab(CreativeModeTabs.searchTab());
                }
            }
            String s = this.searchBox.getValue();
            if (this.searchBox.charTyped(input)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.refreshSearchResults();
                }

                return true;
            } else {
                return false;
            }
        }
    }
}