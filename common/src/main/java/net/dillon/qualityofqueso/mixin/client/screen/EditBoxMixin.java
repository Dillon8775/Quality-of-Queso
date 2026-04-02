package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.screen.gui.search.SearchField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.util.ButtonUtil.isInventoryScreen;
import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;
import static net.dillon.qualityofqueso.util.ModUtil.options;

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
    @ModifyArg(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"), index = 1)
    private Identifier applySearchFieldTexture(Identifier original) {
        Identifier newId = NEW_SPRITES.get(this.isActive(), this.isFocused());
        return (EditBox) (Object) this instanceof SearchField && !options().accessibility.useOldSearchBarTexture
                ? options().searching.transparentSearchBar ? ofQoQ("widget/search_field_transparent") : newId
                : original;
    }

    /**
     * Right-aligns the text if the search bar is transparent.
     */
    @Inject(method = "updateTextPosition", at = @At("TAIL"))
    private void rightAlignText(CallbackInfo ci) {
        if (!((EditBox) (Object) this instanceof SearchField) || this.font == null || !options().searching.transparentSearchBar) {
            return;
        }

        String displayed = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        int textWidth = this.font.width(displayed);
        this.textX = this.getX() + this.getWidth() - textWidth - (this.isBordered() ? 4 : 0);
    }

    /**
     * Applies the shadow effect to hint texts.
     */
    @Redirect(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
    private void applyShadowWithHint(GuiGraphicsExtractor graphicsExtractor, Font font, Component text, int x, int y, int color) {
        if (text == this.hint) {
            graphicsExtractor.text(font, text, (((EditBox) (Object) this instanceof SearchField) && options().searching.transparentSearchBar
                            ? x - (isInventoryScreen(Minecraft.getInstance().screen) ? 72 : 65) : x),
                    y, color, this.textShadow);
        }
    }
}