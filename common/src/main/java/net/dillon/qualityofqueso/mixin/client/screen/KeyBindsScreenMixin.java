package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.mixin.client.accessor.KeyBindsScreenAccessor;
import net.dillon.qualityofqueso.util.KeybindScrollHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Objects;

@Mixin(KeyBindsScreen.class)
public class KeyBindsScreenMixin {

    /**
     * Scrolls down to the Quality of Queso category, to make changing controls user-friendly.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void qoq$scrollToCategory(CallbackInfo ci) {
        if (!KeybindScrollHelper.consumeRequest()) {
            return;
        }

        KeyBindsList list = ((KeyBindsScreenAccessor) this).getKeyBindsList();

        if (list == null) {
            return;
        }

        int row = this.findQualityOfQuesoCategory(Minecraft.getInstance().options.keyMappings);
        double scrollAmount = row * 20.0;
        list.setScrollAmount(scrollAmount);
    }

    /**
     * Finds the Quality of Queso keybind category.
     */
    @Unique
    private int findQualityOfQuesoCategory(KeyMapping[] mappings) {
        if (mappings == null || mappings.length == 0) {
            return 0;
        }

        KeyMapping[] copy = Arrays.copyOf(mappings, mappings.length);
        Arrays.sort(copy);

        int row = 0;
        String currentCategory = null;

        for (KeyMapping mapping : copy) {
            if (!Objects.equals(currentCategory, mapping.getCategory())) {
                currentCategory = mapping.getCategory();

                if (Objects.equals(ModKeybinds.QOQ_KEY_CATEGORY, currentCategory)) {
                    return row;
                }

                row++;
            }

            row++;
        }

        return 0;
    }
}