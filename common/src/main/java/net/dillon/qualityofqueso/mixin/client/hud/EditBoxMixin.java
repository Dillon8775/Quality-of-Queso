package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.widget.gui.SearchField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ModHelper.*;

@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget {
    @Shadow
    @Final
    private Font font;
    @Shadow
    private int textX;

    @Shadow
    public abstract boolean isBordered();

    @Shadow
    private boolean textShadow;
    @Shadow
    private @Nullable Component hint;
    @Shadow
    private String value;
    @Shadow
    private int displayPos;
    @Shadow
    public abstract int getInnerWidth();

    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    /**
     * Applies a vanilla-like search field texture.
     */
    @ModifyArg(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"), index = 1)
    private Identifier applySearchFieldTexture(Identifier original) {
        if (!modEnabled(Minecraft.getInstance())) {
            return original;
        }

        if (!((EditBox)(Object) this instanceof SearchField)) {
            return original;
        }

        Identifier newId = SearchField.getSprites().get(this.isActive(), this.isFocused());
        if (options().searching.searchBarColor.black()) {
            return original;
        } else if (options().accessibility.widgetTheme.searchBarTransparent()) {
            return ofQoQ("widget/search/transparent/search_field_transparent");
        } else {
            return newId;
        }
    }

    /**
     * Right-aligns the text if the search bar is transparent.
     */
    @Inject(method = "updateTextPosition", at = @At("TAIL"))
    private void rightAlignText(CallbackInfo ci) {
        if (!((EditBox) (Object) this instanceof SearchField) || this.font == null
                || options().searching.searchBarColor.black() || !options().accessibility.widgetTheme.searchBarTransparent()) {
            return;
        }

        while (this.displayPos > 0
                && this.font.width(this.value.substring(this.displayPos - 1)) <= this.getInnerWidth()) {
            this.displayPos--;
        }

        String displayed = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        int textWidth = this.font.width(displayed);
        this.textX = this.getX() + this.getWidth() - textWidth - (this.isBordered() ? 4 : 0);
    }
}