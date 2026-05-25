package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererManager;
import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.option.UniversalOptions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
                    ModClientOptions.INSTANCE.save();
                    ModCommonOptions.INSTANCE.save();
                    UniversalOptions.INSTANCE.save();
                })
                .build();
    }

    /**
     * @return a custom image renderer for certain options.
     */
    protected static ImageRenderer fixedSizeImage(Identifier texture, int width, int height) {
        return new ImageRenderer() {
            @Override
            public int render(GuiGraphicsExtractor graphics, int x, int y, int renderWidth, float tickDelta) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height);
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
    protected static CompletableFuture<Optional<ImageRenderer>> fixedSizeWebpImage(Identifier webpTexture, int width) {
        return ImageRendererManager.registerOrGetImage(webpTexture, () -> AnimatedDynamicTextureImage.createWEBPFromTexture(webpTexture))
                .thenApply(renderer -> Optional.of(new ImageRenderer() {
                    @Override
                    public int render(GuiGraphicsExtractor graphics, int x, int y, int renderWidth, float tickDelta) {
                        return renderer.render(graphics, x, y, width, tickDelta);
                    }

                    @Override
                    public void close() {
                    }
                }));
    }
}