package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.main.QoQ;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.dillon.qualityofqueso.main.QoQ.modEnabled;
import static net.dillon.qualityofqueso.main.QoQ.options;

@OnlyIn(Dist.CLIENT)
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
    private void closeButtonOnClickOutOfBounds(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (modEnabled(this.minecraft) && options().betterGuiExit && this.menu.getCarried().isEmpty() && mouseButton == 0 && slot == null) {
            this.onClose();
        }
    }

    /**
	 * @author Dillon8775
	 * @reason Allow typing in creative menu regardless of what menu.
	 */
    @Overwrite
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.ignoreTextInput || (!(options().betterSearching) && selectedTab.getType() != CreativeModeTab.Type.SEARCH)) {
            return false;
        } else {
            if (modEnabled(this.minecraft) && options().betterSearching) {
                this.selectTab(CreativeModeTabs.searchTab());
            }
            String s = this.searchBox.getValue();
            if (this.searchBox.charTyped(codePoint, modifiers)) {
                if (!Objects.equals(s, this.searchBox.getValue())) {
                    this.refreshSearchResults();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void allowCertainChars(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (modEnabled(this.minecraft) && options().betterSearching) {
            if (this.hoveredSlot != null && this.hoveredSlot.getItem() != ItemStack.EMPTY && !this.searchBox.isFocused()) {
                this.ignoreTextInput = true;
                cir.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
            }

            for (int key : QoQ.popularKeys) {
                if (keyCode == key) {
                    this.ignoreTextInput = false;
                    cir.setReturnValue(true);
                }
            }
            for (int key : QoQ.disallowedKeys) {
                if (keyCode == key) {
                    this.ignoreTextInput = true;
                    cir.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
                }
            }
        }
    }
}