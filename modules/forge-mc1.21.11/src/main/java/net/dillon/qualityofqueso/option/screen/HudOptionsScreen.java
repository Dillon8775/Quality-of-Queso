package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudOptionsScreen extends AbstractModOptionsScreen {

    public HudOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.hud_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.armorStatus(),
                ModListOptions.displayOnThrow(),

                ModListOptions.itemCount(),
                ModListOptions.displayOnPickup(),

                ModListOptions.countContainers(),
                ModListOptions.displayTotalWithStacks()
        };
    }
}