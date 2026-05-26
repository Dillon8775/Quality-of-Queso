package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.widget.SearchBar;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget {

    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    /**
     * Applies a vanilla-like search bar texture.
     */
    @ModifyArg(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private ResourceLocation applySearchBarTexture(ResourceLocation original) {
        ResourceLocation newId = SearchBar.getSprites().get(this.isActive(), this.isFocused());
        return (EditBox) (Object) this instanceof SearchBar && !clientOptionsInstance().getSearchingOptions().searchBarColor.black()
                ? newId
                : original;
    }
}