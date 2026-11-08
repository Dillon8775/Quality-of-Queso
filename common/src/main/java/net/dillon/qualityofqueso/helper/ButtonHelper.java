package net.dillon.qualityofqueso.helper;

import com.mojang.blaze3d.Blaze3D;
import net.dillon.dillonlib.client.component.BasicSpriteIconButtonBuilder;
import net.dillon.dillonlib.client.component.SoundBuilder;
import net.dillon.dillonlib.client.component.SpriteIconButtonWithSound;
import net.dillon.dillonlib.client.component.UpdatableSpriteButton;
import net.dillon.dillonlib.core.DillonLibModReferences;
import net.dillon.dillonlib.platform.info.ModReference;
import net.dillon.qualityofqueso.screen.LastKnownEnderChestScreen;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.dillon.qualityofqueso.screen.ManageContainerDataScreen;
import net.dillon.qualityofqueso.widget.ManageLevelButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.nio.file.Path;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.saveAndApplyChanges;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.universal;
import static net.minecraft.client.gui.components.AbstractWidget.playButtonClickSound;

/**
 * Utility and helper class for buttons.
 */
public class ButtonHelper {

    /**
     * Creates the main Quality of Queso menu button.
     */
    public static UpdatableSpriteButton createMainMenuButton() {
        return UpdatableSpriteButton.builder(
                        "Quality of Queso Main Menu Button",
                        Component.translatable("qualityofqueso"),
                        CHEESE_WHEEL_TEXTURE,
                        b -> openScreen(new MainMenuScreen(getScreen()))
                )
                .updateSupplier(MOD_INFO::hasUpdate)
                .tooltip(Component.translatable("qualityofqueso.title"))
                .build();
    }

    /**
     * Creates the manage level button.
     */
    public static SpriteIconButtonWithSound createManageLevelButton() {
        String address = ManageLevelButton.getCurrentServerAddress();

        return new ManageLevelButton(
                b -> {
                    if (isCurrentLevelNull()) {
                        ManageLevelButton.openConfigDirectory();
                    } else if (isOnServer()) {
                        if (universal().blacklistedServers.contains(address)) {
                            universal().blacklistedServers.remove(address);
                        } else {
                            universal().blacklistedServers.add(address);
                        }
                        saveAndApplyChanges();
                    } else {
                        Path worldPath = getMinecraft().getSingleplayerServer().getWorldPath(LevelResource.ROOT);
                        Blaze3D.openPath(worldPath);
                    }
                }
        );
    }

    /**
     * Creates the open screenshots button.
     */
    public static SpriteIconButtonWithSound createScreenshotsButton() {
        return SpriteIconButtonWithSound.builder(
                        OPEN_SCREENSHOTS_DIRECTORY,
                        manager -> {
                            String essential = "essential";

                            if (DillonLibModReferences.isModLoaded(ModReference.of(essential))) {
                                SoundBuilder.create(Identifier.fromNamespaceAndPath(essential, "screenshot")).buildAndPlay();
                            } else {
                                playButtonClickSound(manager);
                            }
                        },
                        b -> {
                            File screenshots = new File(getMinecraft().gameDirectory, "screenshots");
                            if (!screenshots.exists()) {
                                screenshots.mkdirs();
                            }
                            Blaze3D.openPath(screenshots.toPath());
                        }
                )
                .tooltip(Component.translatable("qualityofqueso.open_screenshots_folder"))
                .build();
    }

    /**
     * @return the manage container data button.
     */
    public static SpriteIconButton createManageContainerDataButton() {
        return BasicSpriteIconButtonBuilder.builder(
                        CHEST,
                        b -> openScreen(new ManageContainerDataScreen(getScreen()))
                )
                .tooltip(Component.translatable("qualityofqueso.manage_container_data"))
                .build();
    }

    /**
     * @return the view last known ender chest button.
     */
    public static SpriteIconButtonWithSound createViewLastKnownEnderChestButton() {
        return SpriteIconButtonWithSound.builder(
                        ENDER_CHEST,
                        manager -> SoundHelper.playEnderChestOpenSound(),
                        b -> openScreen(new LastKnownEnderChestScreen(getScreen()))
                )
                .tooltip(Component.translatable("qualityofqueso.view_ender_chest.tooltip"))
                .build();
    }

    /**
     * @return the path for a widget.
     */
    public static String getWidgetPath(boolean forSearchBar) {
        String appended = !forSearchBar && client().accessibility().useLegacyTextures ? "legacy/" : "";
        return switch (client().general().theme) {
            case DARK, TRUE_DARK -> "dark/" + appended;
            case TRANSPARENT -> "transparent/";
            default -> "vanilla/" + appended;
        };
    }
}