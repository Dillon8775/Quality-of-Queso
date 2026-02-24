package net.dillon.qualityofqueso.mixin.screen;

import net.dillon.qualityofqueso.main.QoQ;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
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

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Shadow
    private static ItemGroup selectedTab;
    @Shadow
    private boolean ignoreTypedCharacter;
    @Shadow
    private TextFieldWidget searchBox;

    @Shadow
    protected abstract void search();

    @Shadow
    protected abstract void setSelectedTab(ItemGroup group);

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    /**
     * Closes the screen when clicking outside of the menu.
     */
    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"))
    private void closeButtonOnClickOutOfBounds(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (modEnabled(this.client) && options().quickGuiExit && this.handler.getCursorStack().isEmpty() && button == 0 && slot == null) {
            this.close();
        }
    }

    /**
     * @author Dillon8775
     * @reason Allow typing in creative menu regardless of what menu.
     */
    @Overwrite
    public boolean charTyped(CharInput input) {
        if (this.ignoreTypedCharacter || (!(options().quickSearch) && selectedTab.getType() != ItemGroup.Type.SEARCH)) {
            return false;
        } else {
            if (modEnabled(this.client) && options().quickSearch) {
                this.setSelectedTab(ItemGroups.getSearchGroup());
            }
            String string = this.searchBox.getText();
            if (this.searchBox.charTyped(input)) {
                if (!Objects.equals(string, this.searchBox.getText())) {
                    this.search();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Fixes certain characters not being inputted when typing.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void allowCertainChars(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (modEnabled(this.client) && options().quickSearch) {
            if (this.focusedSlot != null && this.focusedSlot.getStack() != ItemStack.EMPTY && !this.searchBox.isFocused()) {
                this.ignoreTypedCharacter = true;
                cir.setReturnValue(super.keyPressed(input));
            }

            for (int key : QoQ.popularKeys) {
                if (input.key() == key) {
                    this.ignoreTypedCharacter = false;
                    cir.setReturnValue(true);
                }
            }

            for (int key : QoQ.disallowedKeys) {
                if (input.key() == key) {
                    this.ignoreTypedCharacter = true;
                    cir.setReturnValue(super.keyPressed(input));
                }
            }
        }
    }
}