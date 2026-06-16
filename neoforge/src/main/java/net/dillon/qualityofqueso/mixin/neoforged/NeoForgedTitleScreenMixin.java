package net.dillon.qualityofqueso.mixin.neoforged;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static net.dillon.qualityofqueso.helper.ModHelper.universalOptionsInstance;

@Mixin(TitleScreen.class)
public class NeoForgedTitleScreenMixin {

    /**
     * Increases the amount of buttons to {@code 5} so the menu button can display (different on NeoForged because of NeoForge's additional button).
     */
    @ModifyConstant(method = "init", constant = @Constant(intValue = 4))
    private int makeButtonsFour(int original) {
        if (!universalOptionsInstance().getUniversal().menuButton.enabled()) {
            return original;
        }

        return 5;
    }
}