package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.screen.gui.SearchField;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;
import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget {
    @Unique
    private static final WidgetSprites NEW_SPRITES = new WidgetSprites(
            ofQoQ("widget/search_field"), ofQoQ("widget/search_field_highlighted")
    );

    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    /**
     * Applies a vanilla-like search field texture.
     */
    @ModifyArg(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"), index = 0)
    private ResourceLocation applySearchFieldTexture(ResourceLocation original) {
        ResourceLocation newId = NEW_SPRITES.get(this.isActive(), this.isFocused());
        return (EditBox) (Object) this instanceof SearchField && !options().accessibility.useOldSearchBarTexture
                ? newId
                : original;
    }
}