package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.screen.EnderChestPreviewScreen;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ButtonHelper.getConfigButtonX;
import static net.dillon.qualityofqueso.helper.ButtonHelper.getConfigButtonY;
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
     * Adds all {@code Quality of Queso} configuration buttons.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (this.showPauseMenu) {
            if (this.disconnectButton != null && clientOptionsInstance().getMiscOptions().antiRageQuit) {
                this.disconnectButton.active = false;
            }
            if (universalOptionsInstance().getUniversal().menuButton.everywhere()) {
                int index = 0;
                this.addRenderableWidget(ButtonHelper.createMenuButton(
                        getConfigButtonX(this.width, index),
                        getConfigButtonY(this.height, index),
                        (button) -> setScreen(new MainMenuScreen(this)))
                );
                index++;

                if (!(this.minecraft.getCurrentServer() == null)) {
                    String address = this.getServerAddress();

                    this.blacklistServerButton = this.addRenderableWidget(ButtonHelper.createMenuButton(
                            getConfigButtonX(this.width, index),
                            getConfigButtonY(this.height, index),
                            (button) -> {
                                if (universalOptionsInstance().getUniversal().blacklistedServers.contains(address)) {
                                    universalOptionsInstance().getUniversal().blacklistedServers.remove(address);
                                } else {
                                    universalOptionsInstance().getUniversal().blacklistedServers.add(address);
                                }
                                saveAndApplyConfigs(this.minecraft);
                            }));
                    index++;
                }

                if (clientOptionsInstance().getAccessibilityOptions().eChestButton.pauseScreen()) {
                    this.viewEnderChestButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                            ofQoQ(ENDER_CHEST),
                            getConfigButtonX(this.width, index),
                            getConfigButtonY(this.height, index),
                            (button) -> {
                                setScreen(new EnderChestPreviewScreen());
                            }
                    ));
                }
            }
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