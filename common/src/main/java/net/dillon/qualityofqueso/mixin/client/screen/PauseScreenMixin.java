package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.screen.EnderChestPreviewScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.*;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {
    @Shadow
    @Final
    private boolean showPauseMenu;
    @Shadow
    private Button disconnectButton;
    @Unique
    private Button blacklistServerButton, viewEnderChestButton;

    public PauseScreenMixin(Component pTitle) {
        super(pTitle);
    }

    /**
     * Appends the {@code Quality of Queso} prefix to a tooltip.
     */
    @Unique
    private Component tooltipWithPrefix(Component text) {
        return Component.translatable("qualityofqueso.gui.blacklisted_server_button_prefix").append("\n").append(text);
    }

    /**
     * @return the current server address.
     */
    @Unique
    private String getServerAddress() {
        return this.minecraft.getCurrentServer().ip;
    }

    /**
     * Checks if a server is blacklisted or not.
     */
    @Unique
    private boolean isServerBlacklisted(String serverAddress) {
        return universalOptionsInstance().getUniversal().blacklistedServers.contains(serverAddress);
    }

    /**
     * Adds all Quality of Queso main menu buttons to the pause screen.
     */
    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 3), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void addQualityOfQuesoButtons(CallbackInfo ci, GridLayout gridLayout, GridLayout.RowHelper helper, LinearLayout iconButtonRow, SpriteIconButton reportBugsButton, SpriteIconButton feedbackButton, PlayerSocialManager playerSocialManager, SpriteIconButton playerReportingButton) {
        if (!universalOptionsInstance().getUniversal().menuButton.everywhere()) {
            return;
        }

        iconButtonRow.addChild(ButtonHelper.createMainMenuButton(this));

        if (!(this.minecraft.getCurrentServer() == null)) {
            String address = this.getServerAddress();
            this.blacklistServerButton = iconButtonRow.addChild(ButtonHelper.createBlacklistServerButton(address));
        }

        if (clientOptionsInstance().getAccessibilityOptions().eChestButton.pauseScreen()) {
            this.viewEnderChestButton = iconButtonRow.addChild(ButtonHelper.createSpriteIconButton(
                    ofQoQ(ENDER_CHEST),
                    (button) -> {
                        setScreen(new EnderChestPreviewScreen());
                    },
                    Component.translatable("qualityofqueso.gui.view_ender_chest.tooltip")
            ));
        }
    }

    /**
     * Renders toolips and textures over top of the {@code Quality of Queso buttons.}
     */
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderTooltipsAndTextures(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!this.showPauseMenu) {
            return;
        }

        if (clientOptionsInstance().getMiscOptions().antiRageQuit && clientOptionsInstance().getGeneralOptions().tooltips.enabled() && this.disconnectButton != null && this.disconnectButton.isHovered()) {
            drawTooltip(Component.translatable("qualityofqueso.gui.disconnect"), graphics, this.font, mouseX, mouseY);
        }

        if (universalOptionsInstance().getUniversal().menuButton.everywhere() && !(this.minecraft.getCurrentServer() == null)) {
            if (this.blacklistServerButton != null) {
                this.blacklistServerButton.active = isOnServer(this.minecraft);
                String address = this.getServerAddress();

                if (universalOptionsInstance().getUniversal().multiServerConfigs) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ofQoQ(MULTI_CONFIG_TEXTURE), this.blacklistServerButton.getX() - 2, this.blacklistServerButton.getY() - 1, 16, 16);
                }
                ButtonHelper.drawTexture(graphics, this.isServerBlacklisted(address) ? DISABLED_TEXTURE : ENABLED_TEXTURE, this.blacklistServerButton);

                Component tooltip = this.isServerBlacklisted(address) ?
                        Component.translatable("qualityofqueso.gui.remove_blacklisted_server") :
                        Component.translatable("qualityofqueso.gui.add_blacklisted_server");
                Component finalTooltip = tooltip;
                if (universalOptionsInstance().getUniversal().multiServerConfigs) {
                    finalTooltip = tooltip.copy().append(Component.translatable("qualityofqueso.gui.multi_server_configs_enabled"));
                }
                if (this.blacklistServerButton.isHovered()) {
                    drawTooltip(this.tooltipWithPrefix(finalTooltip),
                            graphics, this.font, mouseX, mouseY);
                }
            }
        }

        if (this.viewEnderChestButton != null) {
            this.viewEnderChestButton.active = modEnabled(this.minecraft);
            if (clientOptionsInstance().getGeneralOptions().tooltips.enabled() && this.viewEnderChestButton.isHovered()) {
                drawTooltip(Component.translatable("qualityofqueso.gui.view_ender_chest.tooltip"), graphics, this.font, mouseX, mouseY);
            }
        }
    }
}