package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Advanced and technical options.
 */
public class AccessibilityOptionsScreen extends AbstractModOptionsScreen {

    public AccessibilityOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.accessibility_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance<?>[]{
                ListOptions.tooltips(),
                ListOptions.widgetTheme(),

                ListOptions.perpendicularQuickMoving(),
                ListOptions.preventEFromTyping(),

                ListOptions.menuButton(),
                ListOptions.searchInventory(),

                ListOptions.eChestButton(),
                ListOptions.elytraAlarmSoundDelay(),

                ListOptions.multiServerConfigs(),
                ListOptions.ignoreFabricTags()
        };
    }
}