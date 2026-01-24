package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class HudOptionsScreen extends AbstractModOptionsScreen {

    public HudOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.title.hud_options"));
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[]{
                ModListOptions.armorStatus(),
                ModListOptions.displayOnThrow(),

                ModListOptions.itemCount(),
                ModListOptions.displayOnPickup(),

                ModListOptions.countContainers(),
                ModListOptions.displayTotalWithStacks()
        };
    }
}