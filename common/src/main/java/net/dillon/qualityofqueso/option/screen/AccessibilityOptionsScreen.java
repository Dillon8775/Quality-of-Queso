package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
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
        return new OptionInstance[]{
                ModListOptions.displayTotalWithStacks(),
                ModListOptions.searchInventory()
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addBig(ModListOptions.useOldSearchBarTexture());
        this.list.addBig(ModListOptions.perpendicularQuickMoving());
        this.list.addBig(ModListOptions.moveItemsIf());
        this.list.addBig(ModListOptions.elytraAlarmSoundDelay());
        this.list.addBig(ModListOptions.onlyCountMatchingItems());
        this.list.addSmall(this.options());
    }

    @Override
    protected boolean addOptionsByDefault() {
        return false;
    }
}