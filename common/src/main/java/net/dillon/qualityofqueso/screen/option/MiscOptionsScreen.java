package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MiscOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget minElytraFallDistance, minMobHitDingDistance, redArmorTint;

    public MiscOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.misc_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.minElytraFallDistance = createOption(ListOptions.minElytraFallDistance());
        this.minMobHitDingDistance = createOption(ListOptions.minMobHitDingDistance());
        this.redArmorTint = createOption(ListOptions.redArmorTint());

        return new AbstractWidget[]{
                createOption(ListOptions.elytraAlarm()),
                this.minElytraFallDistance,

                createOption(ListOptions.mobHitDing()),
                this.minMobHitDingDistance,

                createOption(ListOptions.armorDing()),
                createOption(ListOptions.quickEquip()),

                createOption(ListOptions.enchantmentHelper()),
                createOption(ListOptions.quickGuiExit()),

                this.redArmorTint,
                createOption(ListOptions.antiRageQuit()),

                createOption(ListOptions.fortniteBattlePass()),
                createOption(ListOptions.forceAntiRageQuit()),
        };
    }

    @Override
    protected void activateButtons() {
        this.minElytraFallDistance.active = ModHelper.options().elytraAlarm.enableElytraAlarm.enabled();
        this.minMobHitDingDistance.active = ModHelper.options().misc.mobHitDing;
        this.redArmorTint.active = ModHelper.uoptions().functions.applyRedArmorTint;
    }
}