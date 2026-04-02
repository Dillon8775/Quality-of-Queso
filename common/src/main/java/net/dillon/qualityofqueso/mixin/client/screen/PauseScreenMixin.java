package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.util.ButtonUtil.getConfigButtonX;
import static net.dillon.qualityofqueso.util.ButtonUtil.getConfigButtonY;
import static net.dillon.qualityofqueso.util.ModUtil.*;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {
    @Shadow
    @Final
    private boolean showPauseMenu;
    @Shadow
    private Button disconnectButton;
    @Unique
    private Button blacklistServerButton;

    public PauseScreenMixin(Component pTitle) {
        super(pTitle);
    }

    /**
     * Adds all {@code Quality of Queso} configuration buttons.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (this.showPauseMenu) {
            if (this.disconnectButton != null && options().misc.preventRageQuitting) {
                this.disconnectButton.active = false;
            }
            if (options().accessibility.menuButton.everywhere()) {
                int index = 0;
                SpriteIconButton settingsButton = this.addRenderableWidget(ButtonUtil.initializeButton(this.minecraft, this));
                settingsButton.setPosition(getConfigButtonX(this.width, 0), getConfigButtonY(this.height, index));
                index++;

                if (!(this.minecraft.getCurrentServer() == null)) {
                    String address = this.getServerAddress();
                    this.blacklistServerButton = this.addRenderableWidget(Button.builder(ModTexts.BLANK, button -> {
                        if (uoptions().main.blacklistedServers.contains(address)) {
                            uoptions().main.blacklistedServers.remove(address);
                        } else {
                            uoptions().main.blacklistedServers.add(address);
                        }
                        saveAll(this.minecraft);
                    }).bounds(getConfigButtonX(this.width, 1), getConfigButtonY(this.height, index), 20, 20).build());
                }
            }
        }
    }

    /**
     * Renders toolips and textures over top of the {@code Quality of Queso buttons.}
     */
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderTooltipsAndTextures(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (this.showPauseMenu) {
            if (options().misc.preventRageQuitting && options().accessibility.helpfulTooltips && this.disconnectButton != null && this.disconnectButton.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.disconnect"), graphics, this.font, mouseX, mouseY);
            }
            if (options().accessibility.menuButton.everywhere() && !(this.minecraft.getCurrentServer() == null)) {
                if (this.blacklistServerButton != null) {
                    this.blacklistServerButton.active = isOnServer(this.minecraft);
                    String address = this.getServerAddress();

                    if (this.blacklistServerButton.isHovered()) {
                        ButtonUtil.drawTexture(graphics, this.isServerBlacklisted(address) ? ButtonUtil.QOQ_ENABLED_TEXTURE : ButtonUtil.QOQ_DISABLED_TEXTURE, this.blacklistServerButton);
                        ButtonUtil.drawTooltip(this.tooltipWithPrefix(this.isServerBlacklisted(address) ?
                                        Component.translatable("qualityofqueso.gui.remove_blacklisted_server") :
                                        Component.translatable("qualityofqueso.gui.add_blacklisted_server")),
                                graphics, this.font, mouseX, mouseY);
                    } else {
                        ButtonUtil.drawTexture(graphics, this.isServerBlacklisted(address) ? ButtonUtil.QOQ_DISABLED_TEXTURE : ButtonUtil.QOQ_ENABLED_TEXTURE, this.blacklistServerButton);
                    }
                }
            }
        }
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
        return uoptions().main.blacklistedServers.contains(serverAddress);
    }
}