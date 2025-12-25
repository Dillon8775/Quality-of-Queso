package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.util.ModUtil.isOnServer;

@OnlyIn(Dist.CLIENT)
@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {
    @Shadow @Final
    private boolean showPauseMenu;
    @Shadow @Nullable
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
            if (this.disconnectButton != null && ModClientOptions.PREVENT_RAGE_QUITTING.get()) {
                this.disconnectButton.active = false;
            }
            if (ModClientOptions.QOQ_BUTTONS.get().everywhere()) {
                SpriteIconButton settingsButton = this.addRenderableWidget(ButtonUtil.initializeButton(this.minecraft, this));
                settingsButton.setPosition(this.width / 2 + 106, this.height / 4 + 72 - 16);
                if (!(this.minecraft.getCurrentServer() == null)) {
                    String address = this.getServerAddress();
                    this.blacklistServerButton = this.addRenderableWidget(Button.builder(ModTexts.BLANK, button -> {
                        List<String> servers = new ArrayList<>(ModClientOptions.BLACKLISTED_SERVERS.get());
                        if (ModClientOptions.BLACKLISTED_SERVERS.get().contains(address)) {
                            servers.remove(address);
                        } else {
                            servers.add(address);
                        }
                        ModClientOptions.BLACKLISTED_SERVERS.set(servers);
                        ModClientOptions.SPEC.save();
                    }).bounds(this.width / 2 + 106, this.height / 4 + 96 - 16, 20, 20).build());
                }
            }
        }
    }

    /**
     * Renders toolips and textures over top of the {@code Quality of Queso buttons.}
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void renderTooltipsAndTextures(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (this.showPauseMenu) {
            if (ModClientOptions.PREVENT_RAGE_QUITTING.get() && ModClientOptions.HELPFUL_TOOLTIPS.get() && this.disconnectButton != null && this.disconnectButton.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.disconnect"), graphics, this.font, mouseX, mouseY);
            }
            if (ModClientOptions.QOQ_BUTTONS.get().everywhere() && !(this.minecraft.getCurrentServer() == null)) {
                if (this.blacklistServerButton != null) {
                    this.blacklistServerButton.active = isOnServer(this.minecraft);
                    String address = this.getServerAddress();

                    if (this.blacklistServerButton.isHovered()) {
                        ButtonUtil.drawTexture(graphics, this.isServerBlacklisted(address) ? ButtonUtil.ENABLED_TEXTURE : ButtonUtil.DISABLED_TEXTURE, this.blacklistServerButton);
                        ButtonUtil.drawTooltip(this.tooltipWithPrefix(this.isServerBlacklisted(address) ?
                                        Component.translatable("qualityofqueso.gui.remove_blacklisted_server") :
                                        Component.translatable("qualityofqueso.gui.add_blacklisted_server")),
                                graphics, this.font, mouseX, mouseY);
                    } else {
                        ButtonUtil.drawTexture(graphics, this.isServerBlacklisted(address) ? ButtonUtil.DISABLED_TEXTURE : ButtonUtil.ENABLED_TEXTURE, this.blacklistServerButton);
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
        return ModClientOptions.BLACKLISTED_SERVERS.get().contains(serverAddress);
    }
}