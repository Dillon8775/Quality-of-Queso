package net.dillon.qualityofqueso.screen.option;

import net.blay09.mods.balm.Balm;
import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.option.eum.accessibility.WidgetTheme;
import net.dillon.qualityofqueso.widget.gui.BlacklistedServersField;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModHelper.uoptions;

/**
 * Advanced and technical options.
 */
public class AccessibilityOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget searchInventory, darkOverlay, elytraAlarmSoundDelay, ignoreFabricTags;
    private BlacklistedServersField blacklistedServersField;

    public AccessibilityOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.accessibility_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.searchInventory = createOption(ListOptions.searchInventory());
        this.darkOverlay = createOption(ListOptions.darkerOverlay());
        this.elytraAlarmSoundDelay = createOption(ListOptions.elytraAlarmSoundDelay());
        this.ignoreFabricTags = createOption(ListOptions.ignoreFabricTags());
        this.blacklistedServersField = new BlacklistedServersField(this.font);

        return new AbstractWidget[]{
                createOption(ListOptions.tooltips()),
                createOption(ListOptions.widgetTheme()),

                createOption(ListOptions.autoCloseRecipeBook()),
                createOption(ListOptions.preventEFromTyping()),

                createOption(ListOptions.perpendicularQuickMoving()),
                this.darkOverlay,

                this.searchInventory,
                createOption(ListOptions.darkDisc()),

                createOption(ListOptions.menuButton()),
                createOption(ListOptions.eChestButton()),

                createOption(ListOptions.multiServerConfigs()),
                this.elytraAlarmSoundDelay,

                this.blacklistedServersField,
                this.ignoreFabricTags
        };
    }

    @Override
    protected void activateButtons() {
        this.searchInventory.active = ModHelper.options().searching.containerSearching;
        this.elytraAlarmSoundDelay.active = ModHelper.options().elytraAlarm.enableElytraAlarm.enabled();
        this.darkOverlay.active = ModHelper.options().accessibility.widgetTheme == WidgetTheme.VANILLA;
        this.ignoreFabricTags.active = Balm.platform().name().equals("fabric");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);
        if (this.blacklistedServersField.isHovered()) {
            drawTooltip(Component.translatable("qualityofqueso.options.blacklisted_servers.tooltip"), graphics, this.font, mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        uoptions().main.blacklistedServers.clear();
        uoptions().main.blacklistedServers.addAll(this.blacklistedServersField.getBlacklistedServers());
        super.onClose();
    }
}