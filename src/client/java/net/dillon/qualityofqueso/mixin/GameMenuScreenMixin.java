package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.main.QualityOfQueso.isOnServer;
import static net.dillon.qualityofqueso.main.QualityOfQueso.options;

@Environment(EnvType.CLIENT)
@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    @Shadow @Final
    private boolean showMenu;
    @Unique
    private ButtonWidget settingsButton;

    public GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (options().showConfigButton && this.showMenu) {
            this.settingsButton = this.addDrawableChild(ButtonUtil.initializeButton(this.client, this, this.width / 2 + 106, this.height / 4 + 72 - 16));
        }
        ButtonWidget addServerToBlacklistButton = this.addDrawableChild(ButtonWidget.builder(ModTexts.BLANK, button -> {
            options().blacklistedServers.add(this.client.getCurrentServerEntry().address);
        }).dimensions(this.width / 2 + 106, this.height / 4 + 96 - 16, 20, 20).build());
        addServerToBlacklistButton.active = isOnServer(this.client);
        ButtonWidget removeServerFromBlacklistButton = this.addDrawableChild(ButtonWidget.builder(ModTexts.BLANK, button -> {
            options().blacklistedServers.remove(this.client.getCurrentServerEntry().address);
        }).dimensions(this.width / 2 + 106, this.height / 4 + 120 - 16, 20, 20).build());
        removeServerFromBlacklistButton.active = isOnServer(this.client);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTooltips(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (options().showConfigButton && this.showMenu) {
            ButtonUtil.drawTooltipAndTexture(context, this.textRenderer, this.settingsButton, mouseX, mouseY, null);
        }
    }
}