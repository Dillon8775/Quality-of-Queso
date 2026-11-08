package net.dillon.qualityofqueso.config;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererManager;
import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import net.dillon.dillonlib.client.component.TextureBuilder;
import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;

/**
 * The main configuration screen for Quality of queso.
 */
public class ConfigurationScreen {

    public static YetAnotherConfigLib configScreen() {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("qualityofqueso"))
                .categories(List.of(
                        GeneralCategory.create(),
                        SearchingCategory.create(),
                        ManagementCategory.create(),
                        HudCategory.create(),
                        ItemCounterCategory.create(),
                        GuiCategory.create(),
                        FovEffectsCategory.create(),
                        FogCategory.create(),
                        MiscellaneousCategory.create(),
                        AccessibilityCategory.create()
                ))
                .save(ModHelper::saveAndApplyChanges)
                .build();
    }

    /**
     * @return a custom image renderer for certain options.
     */
    protected static ImageRenderer fixedSizeImage(String texture, int width, int height) {
        return new ImageRenderer() {
            @Override
            public int render(GuiGraphicsExtractor graphics, int x, int y, int renderWidth, float tickDelta) {
                TextureBuilder.create(graphics, qoqIdentifier("config_images/" + texture), x, y, width, height).buildAndBlit(true);
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