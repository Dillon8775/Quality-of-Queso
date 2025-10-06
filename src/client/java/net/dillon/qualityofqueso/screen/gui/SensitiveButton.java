package net.dillon.qualityofqueso.screen.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Supplier;

/**
 * Used for transferring inventory buttons.
 */
public class SensitiveButton extends ButtonWidget {
    private final Supplier<Boolean> canActivate;

    public SensitiveButton(int x, int y, int width, int height, Text message, PressAction onPress, Supplier<Boolean> canActivate) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.canActivate = canActivate;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        // Fully blocks sound AND onClick if not allowed
        if (!this.canActivate.get()) {
            return false;
        }
        return super.mouseClicked(click, doubled);
    }
}