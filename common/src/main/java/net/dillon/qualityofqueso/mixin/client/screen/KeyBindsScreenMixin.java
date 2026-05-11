package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.mixin.client.accessor.KeyBindsScreenAccessor;
import net.dillon.qualityofqueso.util.KeybindScrollHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.Objects;

@Mixin(KeyBindsScreen.class)
public abstract class KeyBindsScreenMixin extends OptionsSubScreen {

    public KeyBindsScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    /**
     * Scrolls down to the Quality of Queso category, to make changing controls user-friendly.
     */
    @Override
    protected void init() {
        super.init();
        if (!KeybindScrollHelper.consumeRequest()) {
            return;
        }

        KeyBindsScreen screen = (KeyBindsScreen) (Object) this;
        KeyBindsList list = ((KeyBindsScreenAccessor) screen).getKeyBindsList();

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
        KeyMapping[] copy = mappings.clone();
        Arrays.sort(copy);

        int row = 0;
        KeyMapping.Category currentCategory = null;

        for (KeyMapping mapping : copy) {
            if (!Objects.equals(currentCategory, mapping.getCategory())) {
                currentCategory = mapping.getCategory();

                if (Objects.equals(ModKeyMappings.QOQ_KEY_CATEGORY, currentCategory)) {
                    return row;
                }

                row++;
            }

            row++;
        }

        return 0;
    }
}