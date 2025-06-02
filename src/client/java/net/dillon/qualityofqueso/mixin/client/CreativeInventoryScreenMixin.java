package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.option.ModOptions;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

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
		if (ModOptions.OPTIONS.close_gui_menu_by_clicking_off && this.handler.getCursorStack().isEmpty() && button == 0 && slot == null) {
			this.close();
		}
	}

	/**
	 * @author Dillon8775
	 * @reason Allow typing in creative menu regardless of what menu.
	 */
	@Overwrite
	public boolean charTyped(char chr, int modifiers) {
		if (this.ignoreTypedCharacter) {
			return false;
		} else if (!ModOptions.OPTIONS.type_anywhere_to_search && selectedTab.getType() != ItemGroup.Type.SEARCH) {
			return false;
		} else {
			if (ModOptions.OPTIONS.type_anywhere_to_search) {
				this.setSelectedTab(ItemGroups.getSearchGroup());
			}
			String string = this.searchBox.getText();
			if (this.searchBox.charTyped(chr, modifiers)) {
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
	private void allowCertainChars(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (ModOptions.OPTIONS.type_anywhere_to_search) {
			List<Integer> keys = List.of(GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_E);
			List<Integer> disallowedKeys = List.of(
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
			for (int key : keys) {
				if (keyCode == key) {
					this.ignoreTypedCharacter = false;
					cir.setReturnValue(true);
				}
			}
			for (int key : disallowedKeys) {
				if (keyCode == key) {
					this.ignoreTypedCharacter = true;
					cir.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
				}
			}
		}
	}
}