package net.dillon.qualityofqueso.option.options;

import net.dillon.qualityofqueso.option.AbstractModOptionsScreen;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MiscOptionsScreen extends AbstractModOptionsScreen {

    public MiscOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.misc_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.ENABLE_MOD,
                ModListOptions.quickEquip(),
                ModListOptions.BETTER_SEARCHING,
                ModListOptions.BETTER_GUI_EXIT,
                ModListOptions.PREVENT_RAGE_QUITTING,
                ModListOptions.PREVENT_E_FROM_TYPING,
                ModListOptions.HELPFUL_TOOLTIPS,
                ModListOptions.QOQ_BUTTONS
        };
    }
}