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

                ListOptions.autoCloseRecipeBook(),
                ListOptions.preventEFromTyping(),

                ListOptions.perpendicularQuickMoving(),
                ListOptions.searchInventory(),

                ListOptions.menuButton(),
                ListOptions.eChestButton(),

                ListOptions.multiServerConfigs(),
                ListOptions.elytraAlarmSoundDelay(),

                ListOptions.ignoreFabricTags()
        };
    }
}