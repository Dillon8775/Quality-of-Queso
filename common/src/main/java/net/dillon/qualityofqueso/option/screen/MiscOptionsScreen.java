package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
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
                ModListOptions.elytraAlarm(),
                ModListOptions.minElytraFallDistance(),

                ModListOptions.mobHitDing(),
                ModListOptions.minMobHitDingDistance(),

                ModListOptions.armorDing(),
                ModListOptions.quickEquip(),

                ModListOptions.enchantingHelper(),
                ModListOptions.quickGuiExit(),

                ModListOptions.preventRageQuitting(),
                ModListOptions.alwaysPreventRageQuitting(),

                ModListOptions.fortniteBattlePass(),
        };
    }
}