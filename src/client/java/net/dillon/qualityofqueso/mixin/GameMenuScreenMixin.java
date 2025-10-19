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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.main.QoQ.*;

@Environment(EnvType.CLIENT)
@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    @Shadow @Final
    private boolean showMenu;
    @Shadow private @Nullable ButtonWidget exitButton;
    @Unique
    private ButtonWidget settingsButton, blacklistServerButton;

    public GameMenuScreenMixin(Text title) {
        super(title);
    }

    /**
     * Adds all {@code Quality of Queso} configuration buttons.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (this.showMenu) {
            if (options().showQoQButtons) {
                this.settingsButton = this.addDrawableChild(ButtonUtil.initializeButton(this.client, this, this.width / 2 + 106, this.height / 4 + 72 - 16));
            }
            if (this.exitButton != null && options().preventRageQuitting) {
                this.exitButton.active = false;
            }
            if (!(this.client.getCurrentServerEntry() == null)) {
                String address = this.getServerAddress();
                this.blacklistServerButton = this.addDrawableChild(ButtonWidget.builder(ModTexts.BLANK, button -> {
                    if (options().blacklistedServers.contains(address)) {
                        options().blacklistedServers.remove(address);
                    } else {
                        options().blacklistedServers.add(address);
                    }
                    saveAll();
                }).dimensions(this.width / 2 + 106, isFlashbackLoaded() ? this.height / 4 + 120 - 16 : this.height / 4 + 96 - 16, 20, 20).build());
            }
        }
    }

    /**
     * Renders toolips and textures over top of the {@code Quality of Queso buttons.}
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void renderTooltipsAndTextures(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (this.showMenu) {
            if (options().showQoQButtons) {
                ButtonUtil.drawTooltipAndTexture(ModTexts.CONFIGURE_QOQ, ButtonUtil.CHEESE_WHEEL, context, this.textRenderer, this.settingsButton, mouseX, mouseY, null);
            }
            if (options().preventRageQuitting && options().helpfulTooltips && this.exitButton != null && this.exitButton.isHovered()) {
                ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.disconnect"), context, this.textRenderer, mouseX, mouseY);
            }
            if (!(this.client.getCurrentServerEntry() == null)) {
                if (this.blacklistServerButton != null) {
                    this.blacklistServerButton.active = isOnServer(this.client);
                    String address = this.getServerAddress();

                    if (this.blacklistServerButton.isHovered()) {
                        ButtonUtil.drawTexture(context, this.blacklistServerButton.isHovered() ? ButtonUtil.ENABLED_TEXTURE : ButtonUtil.DISABLED_TEXTURE, this.blacklistServerButton);
                        ButtonUtil.drawTooltip(this.tooltipWithPrefix(this.blacklistServerButton.isHovered() ?
                                Text.translatable("qualityofqueso.gui.remove_blacklisted_server") :
                                Text.translatable("qualityofqueso.gui.add_blacklisted_server")),
                                context, this.textRenderer, mouseX, mouseY);
                    } else {
                        ButtonUtil.drawTexture(context, this.isServerBlacklisted(address) ? ButtonUtil.DISABLED_TEXTURE : ButtonUtil.ENABLED_TEXTURE, this.blacklistServerButton);
                    }
                }
            }
        }
    }

    /**
     * Appends the {@code Quality of Queso} prefix to a tooltip.
     */
    @Unique
    private Text tooltipWithPrefix(Text text) {
        return Text.translatable("qualityofqueso.gui.blacklisted_server_button_prefix").append("\n").append(text);
    }

    /**
     * @return the current server address.
     */
    @Unique
    private String getServerAddress() {
        return this.client.getCurrentServerEntry().address;
    }

    /**
     * Checks if a server is blacklisted or not.
     */
    @Unique
    private boolean isServerBlacklisted(String serverAddress) {
        return options().blacklistedServers.contains(serverAddress);
    }
}