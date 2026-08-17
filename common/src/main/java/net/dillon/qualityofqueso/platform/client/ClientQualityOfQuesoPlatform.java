package net.dillon.qualityofqueso.platform.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.platform.client.ClientModPlatform;
import net.dillon.dillonlib.platform.info.PlatformMenuButton;
import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.client.KeyMapping;

import java.util.List;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.option.OptionInstances.universal;

public abstract class ClientQualityOfQuesoPlatform extends ClientModPlatform {

    @Override
    public String modId() {
        return ModConstants.MOD_ID;
    }

    @Override
    public List<PlatformMenuButton> menuButtons() {
        return List.of(
                new PlatformMenuButton(
                        universal().menuButton.enabled(),
                        false,
                        ButtonHelper.createMainMenuButton(getScreen()),
                        spriteIconButton -> {})
        );
    }

    // Unused
    @Override
    public KeyMapping registerKeyMapping(String name, InputConstants.Type type, KeyMapping.Category category, int value) {
        return null;
    }
}