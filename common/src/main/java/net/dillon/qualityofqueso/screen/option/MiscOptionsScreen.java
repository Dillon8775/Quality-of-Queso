package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MiscOptionsScreen extends AbstractModOptionsScreen {

    public MiscOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.misc_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ListOptions.elytraAlarm(),
                ListOptions.minElytraFallDistance(),

                ListOptions.mobHitDing(),
                ListOptions.minMobHitDingDistance(),

                ListOptions.armorDing(),
                ListOptions.quickEquip(),

                ListOptions.enchantmentHelper(),
                ListOptions.quickGuiExit(),

                ListOptions.fortniteBattlePass(),
                ListOptions.antiRageQuit(),

                ListOptions.forceAntiRageQuit(),
        };
    }
}