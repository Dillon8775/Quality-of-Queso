package net.dillon.qualityofqueso.mixin.fabric;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static net.dillon.qualityofqueso.helper.ModHelper.universalOptionsInstance;

@Mixin(TitleScreen.class)
public class FabricTitleScreenMixin {

    /**
     * Increases the amount of buttons to {@code 4} so the menu button can display.
     */
    @ModifyConstant(method = "init", constant = @Constant(intValue = 3))
    private int makeButtonsFive(int original) {
        if (!universalOptionsInstance().getUniversal().menuButton.enabled()) {
            return original;
        }

        return 4;
    }
}