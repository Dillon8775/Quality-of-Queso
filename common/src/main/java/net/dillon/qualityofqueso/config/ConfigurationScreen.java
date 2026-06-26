package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererManager;
import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.dillon.qualityofqueso.helper.ModHelper.saveAndApplyConfigs;

/**
 * The main configuration screen for Quality of queso.
 */
public class ConfigurationScreen {

    public static YetAnotherConfigLib configScreen() {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("qualityofqueso.title"))
                .category(
                        GeneralCategory.create()
                )
                .category(
                        SearchingCategory.create()
                )
                .category(
                        ManagementCategory.create()
                )
                .category(
                        HudCategory.create()
                )
                .category(
                        ItemCounterCategory.create()
                )
                .category(
                        FovEffectsCategory.create()
                )
                .category(
                        FogCategory.create()
                )
                .category(
                        MiscellaneousCategory.create()
                )
                .category(
                        AccessibilityCategory.create()
                )
                .save(() -> {
                    saveAndApplyConfigs(Minecraft.getInstance());
                })
                .build();
    }

    /**
     * @return a custom image renderer for certain options.
     */
    protected static ImageRenderer fixedSizeImage(ResourceLocation texture, int width, int height) {
        return new ImageRenderer() {
            @Override
            public int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta) {
                graphics.blitSprite(texture, x, y, width, height);
                return height;
            }

            @Override
            public void close() {
            }
        };
    }

    /**
     * @return a fixed-size animated WebP renderer for option descriptions.
     */
    protected static CompletableFuture<Optional<ImageRenderer>> fixedSizeWebpImage(ResourceLocation webpTexture, int width) {
        return ImageRendererManager.registerOrGetImage(webpTexture, () -> AnimatedDynamicTextureImage.createWEBPFromTexture(webpTexture))
                .thenApply(renderer -> Optional.of(new ImageRenderer() {
                    @Override
                    public int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta) {
                        return renderer.render(graphics, x, y, width, tickDelta);
                    }

                    @Override
                    public void close() {
                    }
                }));
    }
}